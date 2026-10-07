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
    private long population;   
    private long[] jobs = new long[JobType.values().length];
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
     * What the player decided, and when (0.7.23; DecisionLog): every change of
     * a policy and every spend at scale, at the month it was made. Beside the
     * inbox and the books because it is the same kind of thing - a record of
     * months that have happened - and saved, because a decision is a flow
     * the state a month ended in cannot give back. Built fresh by buildWorld()
     * and held there until the door into the city is through: the
     * constructor, newGame() and loadGameSave() each release it at their end,
     * and loadGame() holds it for itself.
     */
    private DecisionLog decisions = new DecisionLog(() -> month);

    /** The player's decisions, oldest first (0.7.23): what the chart's flags are drawn from. */
    public DecisionLog getDecisions() { return decisions; }

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
    /** The last order's material, in units - drawn by the crews as they build. A receipt field. */
    double receiptMaterials = 0;
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
        this(gameFiles, Founding.defaults());
    }

    /**
     * ...founded as the player chose (0.7.10): a name, its money, a treasury,
     * a vault and a world. The same door as the one above with the defaults
     * in it; see buildWorld() and Founding.
     */
    public Game(GameFiles gameFiles, Founding founding) {
        this.gameFiles = gameFiles;
        buildWorld(foundable(founding));
        decisions.release();
    }

    /** The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. */
    private static Founding foundable(Founding founding) {
        String problem = founding == null ? "no founding" : founding.problem();
        if (problem != null) throw new IllegalArgumentException("Cannot found this city: " + problem);
        return founding;
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
     *
     * AND IT TAKES THE FOUNDING (0.7.10): the name, the money, the treasury,
     * the vault and the world, which were constants. Every door into a city
     * comes through here, so every door founds from a record rather than from
     * a constant - the load path with the defaults, which the save's own
     * record then replaces (loadGame()).
     */
    private void buildWorld(Founding founding) {

        // A new city's decisions start empty, and nothing is one until the
        // door into the city is through (0.7.23; see the field).
        decisions = new DecisionLog(() -> month);
        decisions.hold();

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
        /*
         * ...AND THE HEALTH SERVICE AND THE SICK RATE (2026-09-19). Neither was
         * here, and neither has a reset: a new game after a load kept the old
         * city's graves, its unburied and last month's bills, and nothing
         * noticed until the clinic's full-service bill joined the fingerprint
         * NewGameCheck takes and leaked across the new city on the first run.
         */
        health = new Health();
        healthcare = new Healthcare();
        historyGrapher = new HistoryGrapher();
        debtManager = new DebtManager();
        businessInvestment = new BusinessInvestment(buildingManager, economyManager);
        businessInvestment.watchOrders(orderWatch);   // a harness's watch outlives a load (0.7.54)

        // Every sector gets a handle on the city, for the few hooks that need
        // more than the buildings and the markets - the mines and the ground.
        economyManager.getSectors().attachGame(this);
        // ...and the builders offer only the posts their work needs (0.7.17):
        // the city's job count reads their share through the buildings, so
        // the posts they lay off are nobody's (sectors.Construction, THE
        // CREWS THE WORK NEEDS; BuildingManager, THE POSTS A SECTOR OFFERS).
        ham.citybuildersim.sectors.Construction builders = economyManager.getSectors().construction();
        buildingManager.setOfferedShare(key -> key.equals(builders.key()) ? builders.getPostsOfferedShare() : 1);
        // ...and what their crews cost a point, today and at founding, which
        // the labour in every price follows (0.7.19; BuildingManager, THE
        // LABOUR IN A PRICE KEEPS UP WITH WAGES).
        buildingManager.setBuildersWages(buildersWages);
        // ...and the overtime on the city's rushed sites is wages the
        // households are paid (0.7.22; PopulationManager, THE OVERTIME ON A
        // RUSHED SITE IS WAGES), read off the builders who pay it.
        populationManager.setOvertimeWages(builders::getOvertimeWages);
        // ...and what a founding dollar is today, which the rebate on a new
        // rental home is struck in (0.7.19, revised; EconomyManager, THE
        // REBATES ON A NEW HOME, AND THE CITY'S).
        economyManager.setFoundingToToday(() -> {
            double unit = denomination.getUnit();
            // ...at the expected price level since 0.7.42, like every money
            // constant (restrikeMoneyConstants()); the price index until then.
            return expectations.getExpectedLevel() / (unit > 0 ? unit : 1);
        });
        economyManager.setOutwardInvestment(outward);
        economyManager.setEquity(equity);
        householdBalance.setMarket(exchange, equity, bank);
        // ...and the exchange settles against the same city from the start,
        // before its first step (0.7.12 round 2): a cell short of money sells
        // into the book at the first month's waterfall.
        exchange.attach(equity, householdBalance, bank, exchangeCompanies, 0);
        // ...and the bank's desk for their city paper (0.7.1).
        householdBalance.setPaperDesk(this::desksBuysHouseholdPaper);
        // ...and the businesses' bonds (0.7.12): the market reads the city
        // through bondReadings, and the lender, the households and the
        // economy each read the market. See BondMarket.
        bondMarket.reset();
        bondMarket.attach(bondReadings, householdBalance, bank, economyManager, outward);
        // ...and the city's fund, a holder on both books (0.7.14).
        fund.reset();
        exchange.attachFund(fund);
        bondMarket.attachFund(fund);
        ownersWipedAbroadThisMonth = 0;
        bankedClearedAtLoad = 0;
        economyManager.setBondMarket(bondMarket);
        economyManager.getBusinessDebtManager().setBondMarket(bondMarket, bondMarket);
        householdBalance.setBondMarket(bondMarket);
        
        simulationEngine = new SimulationEngine(
                economyManager,
                populationManager,
                servicesManager,
                buildingManager,
                debtManager);
        
        // Reading the rate live, so what the office charges in local money is
        // always today's (0.7.6); a lambda, so foreign is read when it is asked.
        // ...and the world's price level, which the dollar price follows (0.7.55).
        // ...and on the world the city was founded on, in the month (0.7.57):
        // read when the land is founded and each offer listed.
        landManager = new LandManager(() -> foreign.getRate(), () -> world.getPriceLevel(),
                () -> this.founding.getWorldSeed(), () -> month);
        // ...and the fresh water limit is the land's and the rights' (0.7.59,
        // batch J2): read each time the services update, on every path. A new
        // city has no rights; a load sets its own (readTheSave()).
        freshRights = 0;
        servicesManager.setFreshCapSource(this::getFreshCap);
        // ...and the city map waits to be asked for (0.7.60): THE CITY MAP.
        cityMap = null;
        mapFailures = 0;
        mapGeneration++;
        demolitionLog = new DemolitionLog();
        buildLog = new BuildLog();
        cohorts = new PopulationCohorts();
        families = new FamilyModel();
        families.rememberHouseholds(true);   // see FamilyModel: THE HOUSEHOLDS REMEMBER
        migration = new Migration();
        labourMarket = new LabourMarket();
        education = new Education();
        skipReport = new TimeSkipReport();
        households = new HouseholdAccounts();
        householdBalance.reset();
        unemployment.reset();
        sickness.reset();
        crime.reset();
        lastOrphanDeaths = 0; lastUnhousedDeaths = 0;
        studentLoansLent = 0; studentLoansRepaid = 0; studentLoansWrittenOff = 0;
        studentLoanInterest = 0;
        treasuryJournal.reset(); treasuryRaisedSoFar = 0;
        // ...and paper the last city sold and its bank has not paid for, which
        // the next settle would otherwise charge to THIS city's bank. Harmless
        // while the settle read zero; not since it pays (2026-09-21).
        cityDebtRaisedThisMonth = cityDiscountThisMonth = 0;
        cityDebtRaisedForBank = cityDiscountForBank = cityPaperSettled = 0;
        // The holders' carried figures (0.7.1): a new city owes nobody anything.
        legacyDiscountDue = 0;
        buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;
        buybackToHouseholds = buybackAbroad = 0;
        couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;
        bankPrincipalRepaidThisMonth = 0;
        bank.reset();
        foreign.reset();
        // The land office converts cash until the player says otherwise (0.7.6).
        landPaidFromVault = false;
        lastLandReceipt = "";
        // ...and the treasury rolls nothing and has netted nothing (0.7.13):
        // newGame() turns it on, the load path puts the save's back.
        rollover.reset();
        // ...and the central bank, founded fresh rather than reset, reading
        // the vault where ForeignAccounts keeps it. See getCentralBank().
        centralBank = new CentralBank(foreign::getReserves);
        // ...and every dial the player sets writes its change to the city's
        // decision log where it is applied (0.7.23): the taxes and promises,
        // the wage floor, the share of tuition, the rate, the rule and the
        // target, the holdings and the advances. Game's own methods write the
        // rest - the standing subsidies, the bank, the fund, the paper, the
        // money and the queue.
        economyManager.getTaxPolicy().recordTo(decisions);
        labourMarket.recordTo(decisions);
        education.recordTo(decisions);
        debtManager.recordTo(decisions);
        centralBank.recordTo(decisions);
        arrears.clear();
        arrearsPaidTo.clear();
        arrearsRefusedThisMonth = arrearsPaidThisMonth = 0;
        arrearsRefusedLifetime = arrearsPaidLifetime = 0;
        hotMoney.reset();
        outward.reset();
        equity.reset();
        exchange.reset();
        bondMarket.reset();
        priceIndex.reset();
        // ...and the anchor, seeded at the city's target by its first month (0.7.42).
        expectations.reset();
        /*
         * THE WORLD IS CHOSEN AT FOUNDING (0.7.10), and set BEFORE the reset
         * rather than after it: reset() back-casts a year of the world's
         * price level at the mean it holds, so a mean set afterwards - as the
         * main menu did until 0.7.10, straight after newGame() - left the
         * first year's realised inflation reading the previous city's world.
         * WorldEconomy.setMeanInflation() says why it is a founding choice.
         */
        world.setMeanInflation(founding.getMeanInflation());
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
         * premium a city with no bank paid until 0.7.7.
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
         *
         * ...AND SPLIT ON 2026-09-21: the same $3.5B, D$2.5B of it here and
         * US$1B in the vault. See THE FOUNDING RESERVE below buildWorld(). The
         * power plant was then 57% of what the treasury opened with rather
         * than 41%, and still the first thing the endowment bought.
         *
         * ...AND CUT TO D$100M ON 2026-09-24 (0.7.10), BECAUSE ITS JOB HAS
         * CHANGED. Jerus: "lets reduce the cash the city starts with, both
         * the foreign usd and the starting cash ... cause the city should
         * borrow right". The endowment no longer buys the opening, big public
         * works and all. It founds the village and pays for one of the first
         * big works, and the city borrows for the rest.
         *
         * What a trace of the deployed 0.7.9 tree found (seed 0, the default
         * playtest) is why. The treasury never touched its D$2.5B: it never
         * fell below D$2.467B in thirty years. The playtest's first fourteen
         * months of building - about sixty houses, five shops, two farms and a
         * depot by hand, then its advisor - cost about $46M in all; after that
         * it spent $0.1-2M a month against $0.4-5M a month of revenue, carried
         * no city debt for about ninety years, and ended with $264B in the
         * treasury. An endowment nobody ever draws on is not a start.
         *
         * THE REAL WORLD BUILDS A TOWN ON DEBT, NOT ON AN ENDOWMENT. A
         * long-lived public work is financed with bonds and repaid over the
         * asset's life - "pay-as-you-use" - so the people who use the plant
         * are the ones who pay for it. Britain's New Towns (New Towns Act
         * 1946) were built by development corporations on sixty-year loans
         * from the Exchequer. And a young city here can afford to borrow:
         * DebtManager.priceAt() adds MAX_SPREAD_PER_MEASURE x debt / (annual
         * measure) / FULL_STRESS_MULTIPLE per measure, so a $66M water plant
         * on a city with about $12M of yearly revenue adds roughly 0.2 points
         * (0.3 at the $110M a new city is actually invoiced for it - Founding,
         * WHAT IT BUYS), and the playtest's advisor keeps its interest under
         * DEBT_SERVICE_LIMIT (25%) of the month's tax income (canService()).
         *
         * THE ENDOWMENT ITSELF IS STILL A GAME NUMBER. No real town is handed
         * cash; the figure has a job, the one above, and D$100M is Jerus's
         * pick over D$50M, D$25M and keeping D$2.5B. Since the same batch it
         * is the Standard preset of four the player founds on (Founding), and
         * this constant stays the default the harnesses and the playtest read.
         * What each preset buys at the catalogue's invoices is Founding's
         * whatItBuys(); NewGameCheck holds the job to the catalogue.
         */
        this.cash = founding.getCash();
        /*
         * THE FOUNDERS' DOLLARS, bought on day one at the opening rate and
         * booked as the purchase they are - buyReserves() carries them in
         * lifetimeIntervention, so the vault stays "what the treasury chose to
         * buy and has not yet sold". HERE, beside the cash and after
         * foreign.reset(), so every door into a city founds both halves of the
         * endowment together: the constructor, newGame(), and newGame() after
         * a load. The load path comes through here too, and restore() starts
         * from an empty vault rather than from this one - a save carries its
         * own. The first month's startMonth() clears the day's purchase from
         * the month's bookkeeping long before the audit is struck, so no
         * month's audit books a flow that happened before month one began:
         * the treasury simply opens with the founding's cash in it. Whatever
         * vault was chosen is bought the same way, an empty one not at all.
         */
        foreign.buyReserves(foreign.toLocal(founding.getReserveUsd()));
        // ...and the record of it, which nothing changes after this line.
        this.founding = founding;
        // ...and an Insane city's ground, owed abroad (0.7.14).
        if (founding.getPreset() == Founding.Preset.INSANE) foundTheLandBond();
        this.population = 0;
        this.jobs = new long[JobType.values().length];

        this.materialsConsumed = 0;
        this.receiptMaterials = 0;
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
         * The warning about construction shedding.
         *
         * buildWorld() rebuilds every MANAGER, which is what made the
         * twenty-three-field reset bug go away - but fields that live on Game
         * itself are nothing's to clear. A new game once inherited the
         * previous city's construction retainer and its shedding warning; the
         * warning is cleared below with the rest. THE RETAINER IS GONE
         * (0.7.1): it had not been paid since the standing policy replaced it
         * - set, saved, reset and reformed, and read by no line of the month -
         * so the field and its save key were removed rather than cleared, and
         * an old save's key is simply not read. See THE CONSTRUCTION SUBSIDY.
         *
         * NewGameCheck did not catch it because its snapshot did not reach
         * these fields. It does now, which is the actual fix - the list being
         * short is the bug that keeps recurring here, not any one field on it.
         */

        // Policy is the player's, so a new city starts with none of it: no
        // sector protected, no offsets, both rates at their defaults. Leaving
        // any of these behind is the leak New Game has produced twice already.
        autoSubsidy.clear();
        subsidyPaid.clear();
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

        // Forty offers have to be standing before the player's first turn,
        // not after their first month - on a new city. A load puts its own
        // land and offers back (readTheSave()), so it founds none here: the
        // default world would be built for nothing (0.7.57).
        if (!loadingSave) landManager.updateMarket(populationManager.getPopulation());

        initialized = true;
        }
 
    }
    
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
    

    //buttons
    /** A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. See newGame(Founding). */
    public void newGame(){
        newGame(Founding.defaults());
    }

    /**
     * A new city, founded as the player chose on the founding screen (0.7.10).
     * Refused - an IllegalArgumentException - when the founding has a problem
     * (Founding.problem()), which the screen never lets through.
     */
    public void newGame(Founding choices){

        // Rebuild rather than reset. See buildWorld() for the twenty-three
        // fields the old reset was missing and why the list itself was the bug.
        buildWorld(foundable(choices));

        // buildWorld() made a new BuildingManager, so the templates have to be
        // loaded into it. initialize() is guarded, so the flag has to drop first
        // or it would quietly do nothing and leave a city with no buildings in
        // its catalogue.
        initialized = false;
        initialize();
        foundingBank();

        /*
         * A NEW GAME FOUNDS ON THE AUTOPILOT, AND ROLLS WHAT FALLS DUE (0.7.13).
         * Jerus: "the dial should default when you start a game on the
         * automatic, aka not your hand", and the treasury's rollover "default
         * toggles on" - in the same structure (Rollover). Here, not in
         * buildWorld(), because this is the door a player founds through -
         * the founding screen reaches it (UserInterface.foundCity()), as
         * "Found with defaults" did until 0.7.21 - while buildWorld() is also
         * the load path, where the save's own setting is put back, and the constructor,
         * which builds the bare city the harnesses and the playtest found on
         * and state their own settings over. An older save keeps whatever it
         * saved: the dial's hand (DataSave.getPolicyAutopilot(), off when the
         * key is missing), and the rollover as MANUAL - see the load path.
         */
        debtManager.setAutopilot(true);
        rollover.setMode(Rollover.Mode.SAME_STRUCTURE);
        /*
         * ...AND RESOLVES A FAILED BANK THE MONTH IT FAILS (0.7.14). Jerus:
         * "Treasury setting, auto". The bare constructor city the harnesses
         * and the playtest found keeps the button, and an older save loads on
         * it, how it was played (TreasuryFund.reset(), the load path).
         */
        fund.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
        // The founding's settings were the founding's, not the player's: the log opens now.
        decisions.release();
    }

    /**
     * The city opens with a bank already standing.
     *
     * WHY. `Bank.ratePremium()` charged the full eighteen points to any city
     * with a loan book and no branches, until 0.7.7 priced such a city by
     * cost like any other (Bank, WHAT A LOAN COSTS) - "borrowing without a
     * banking system is borrowing from strangers who do not know you". It
     * lasted about forty years: a Commercial Bank costs $9.0M to build
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
         * beside Continue (Resume until 0.7.21) and Save, so this is a thing
         * players do.
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
         *
         * On the defaults (0.7.10), because the save carries its own founding
         * and loadGame() puts it back - an old save's as Founding.legacy().
         */
        buildWorld(Founding.defaults());

        // buildWorld() made a new BuildingManager, so the templates have to be
        // reloaded into it - and initialize() is guarded, so the flag has to
        // drop first or loadGame()'s per-index template lookups hit an empty
        // list. Same two lines, same reason, as newGame().
        initialized = false;
        loadingSave = true;
        try {
            initialize();
        } finally {
            loadingSave = false;
        }

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
        decisions.release();
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
     * city - newGame() and loadGameSave() both go through it, and so does the
     * constructor, so the world the window builds at start-up is running too.
     * The main menu's Resume asked this until 0.7.20, which is why a cold
     * start's Resume opened that empty world; the window keeps its own
     * cityOpen since then (UserInterface), and nothing reads this now.
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

    /** The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. */
    SimulationEngine getSimulationEngineForTest() { return simulationEngine; }
    
    public int getMonth(){
        return month;
    }
    public double getCash(){
        return cash;
    }
    /* =====================================================================
       THE FOUNDING RESERVE (2026-09-21)

       Jerus, reading his own city's year book - prices 399x founding in
       twenty-five years, the currency at its 100x guard, inflation averaging
       29% a year: "i think we should make it so that of the 3.5B you start
       with, 1B is in usd in the reserve, so you only see 2.5B start with...
       i think that greatly helps, since 99% players wont add to reserves
       most probably cause they have no clue."

       THE SAME ENDOWMENT, SPLIT. Nothing was given that had not been given
       before: the founders' $3.5B became D$2.5B in the treasury and US$1B
       bought on day one at the opening rate of 1.00 - booked by
       buyReserves() exactly as a purchase the treasury made, so
       lifetimeIntervention carries it and the vault is still "what the
       treasury chose to buy and has not yet sold". It is in the vault
       rather than the treasury for the reason a reserve exists at all:
       import cover damps the pressure to fall from the first month the rate
       is allowed to move (ForeignAccounts.SETTLING_MONTHS; absorption() - on
       the way down only, see A RESERVE DEFENDS A CURRENCY, without which this
       founding reserve reproduced the year book in three seeds of eight), it
       backs the hot money a young city attracts, and - since the vault is
       kept in dollars - it is the one thing the city owns that gains when
       its currency falls.

       ...AND BOTH HALVES CUT ON 2026-09-24 (0.7.10): D$100M in the treasury
       and US$25M in the vault, Jerus's pick ("lets reduce the cash the city
       starts with, both the foreign usd and the starting cash ... cause the
       city should borrow right") over US$50M, US$10M and keeping US$1B. The
       treasury's half is argued where it is spent, at buildWorld()'s
       endowment; the vault's is here.

       A VAULT IS SIZED BY WHAT IT COVERS, and the IMF's traditional rule of
       thumb is three months of imports. The trace of the deployed tree (seed
       0, the default playtest) found a young city importing $0.4-3.7M a
       month over its first thirty years, its population under 3,000, so
       US$25M covers such a city for years and a town of about 8-10k people
       for three months. US$1B was far bigger than anything it did: the
       central bank's defence had spent about US$20M by month 133 and
       US$135M by month 349, the vault was effectively empty from about month
       1,140 (US$7.9K) with no harm done, and the currency STRENGTHENED in
       that seed, from 1.00 to 0.5-0.6 local per dollar. Like the treasury's,
       the figure is a game number - no town is handed a vault - with a job,
       and the job is the young city's cover. It is the Standard preset's;
       Founding has the others.
       ===================================================================== */

    /** What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. The Standard preset's, and the default. */
    public static final double FOUNDING_CASH = 100_000;

    /** What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. The Standard preset's, and the default. */
    public static final double FOUNDING_RESERVE_USD = 25_000;

    /** For this many months the screens say where the vault's first dollars came from; after that they are the city's own. */
    public static final int FOUNDERS_NOTE_MONTHS = 120;

    /* ---------------------------------------------------------------------
       THE FOUNDING RECORD (0.7.10)

       What this city was founded with - its name, its money's name, the
       treasury and the vault - set by buildWorld() from the founding choices
       and put back by loadGame() from the save (Founding.legacy() on a save
       from before it). Nothing changes it in between. The founders' note, the
       window's title and every figure written in the city's money read it
       here, and the slot list reads the name off the save, where DataSave
       writes it; see Founding for why it is a record and not constants.
       Since 0.7.56 it carries the seed of the ground the city stands on,
       getWorld(), which the city's land is a piece of since 0.7.57
       (CityLand; the project's spec-land.md).
       --------------------------------------------------------------------- */

    private Founding founding = Founding.defaults();

    /** How this city was founded. Its mean inflation is the world's (WorldEconomy keeps it; see Founding). */
    public Founding getFounding() {
        return new Founding(founding.getCityName(), founding.getCurrency(), founding.getCash(),
                founding.getReserveUsd(), world.getMeanInflation(), founding.getWorldSeed());
    }

    /** The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. */
    public long getWorldSeed() { return founding.getWorldSeed(); }

    /**
     * The world this city stands on (0.7.56): its terrain, its founding site
     * and river, its fields of ore and oil. Shared through World.of(), and
     * built the first time it is asked anything - about half a second, paid
     * once, the first time the city's land, its office or its map asks.
     */
    public World getWorld() { return World.of(founding.getWorldSeed()); }

    /** The city's land on the world (0.7.57): its centre, its forty lanes and every purchase along them (CityLand). */
    public CityLand getCityLand() { return landManager.getCityLand(); }

    /* =====================================================================
       THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5)

       The city's buildings counted by type in each district of its land
       (CityMap), what the map view paints the city from (batch J4).
       NOTHING IN THE MODEL READS IT. It is drawn the first time it is asked
       for - canonically, each type in proportion in every district, inner
       first - and from then on kept up at the end of every month
       (reconcileMap(), after the month's construction and demolitions), so
       nothing placed moves. A save carries it beside itself as a sidecar
       (GameFiles.mapFile()) and its stamp in the save (DataSave.mapStamp); a
       load reads it back when the stamp matches, draws it again canonically
       when the save had a map and its sidecar is missing or stale, and
       otherwise leaves it to be asked for. A city never asked for its map
       pays nothing for it - most harness cities, and ScaleCheck's copies of
       a city K times over; the playtest asks at its founding (LongPlaytest,
       THE CITY MAP, WATCHED).
       ===================================================================== */

    /** The city map, or null until it is asked for. */
    private CityMap cityMap;

    /** How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. */
    private int mapFailures;

    /** The city map: drawn canonically the first time it is asked for, then kept up month by month. */
    public CityMap getCityMap() {
        if (cityMap == null) cityMap = drawMap();
        return cityMap;
    }

    /** Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. */
    public boolean hasCityMap() { return cityMap != null; }

    /* ----- THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) -----
     *
     * The land office asks for the map the first time it opens. For Jerus's
     * city that is a few milliseconds; for a loaded city of billions with no
     * sidecar it is seconds of measuring districts, which on the screen's
     * thread would freeze the window. CityMap is not thread-safe - the month
     * keeps it up (reconcileMap()), and the land it reads moves with every
     * purchase - so it is not drawn on the live city from another thread:
     * the screen's thread takes a DRAFT (mapDraft()), a copy of the land
     * field for field (CityLand.copy(), restore() of its own records), what remains
     * of each resource by holding and the counts, all as they stand; any
     * thread draws the map on the copy (MapDraft.draw(), CityMap.canonical(),
     * which touches nothing of the city's); and the screen's thread ADOPTS it
     * (adoptMap()): bound to the city's own land and kept up to the month
     * with one reconcile() - the counts and any purchase since the draft, as
     * a month would place them. A draft drawn for another city (a load or a
     * new city since: mapGeneration), on land drawn again since, or after the
     * map was drawn some other way, is not kept.
     * ------------------------------------------------------------------- */

    /** Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. */
    private int mapGeneration;

    /** A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. */
    public static final class MapDraft {
        private final Game game;
        private final int generation;
        private final CityLand land;
        private final java.util.Map<Resource, double[]> remaining = new java.util.EnumMap<>(Resource.class);
        private final BuildingVisual.Type[] types;
        private final long[] counts;
        private volatile CityMap map;

        private MapDraft(Game game) {
            this.game = game;
            this.generation = game.mapGeneration;
            this.land = game.landManager.getCityLand().copy();
            for (Resource r : Resource.values()) remaining.put(r, game.landManager.remainingByHolding(r).clone());
            this.types = game.getMapTypes();
            this.counts = game.getMapCounts();
        }

        /** Draws the map on the copy: any thread, once. */
        public void draw() {
            if (map == null) map = CityMap.canonical(land, r -> remaining.get(r), types, counts);
        }

        /** Whether it has been drawn. */
        public boolean drawn() { return map != null; }

        /** The map drawn, or null: a harness's (MapCheck 7). */
        CityMap map() { return map; }

        /** The game it was taken from. */
        public Game game() { return game; }
    }

    /** A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. The screen's thread only. */
    public MapDraft mapDraft() {
        return cityMap != null ? null : new MapDraft(this);
    }

    /**
     * Keeps a drawn draft as the city's map, bound to its own land and kept
     * up to the month (reconcile()), and says whether it is the city's map
     * now. False - and nothing kept - for another city's draft, an undrawn
     * one, land drawn again since, or a reconcile that fails; true, keeping
     * nothing, when the map was drawn meanwhile. The screen's thread only.
     */
    public boolean adoptMap(MapDraft draft) {
        if (draft == null || draft.game != this || draft.generation != mapGeneration || draft.map == null) return false;
        if (cityMap != null) return true;
        CityLand live = landManager.getCityLand();
        if (live.seed() != draft.land.seed() || live.siteX() != draft.land.siteX() || live.siteY() != draft.land.siteY()
                || live.centreStamp() != draft.land.centreStamp() || live.purchases().size() < draft.land.purchases().size()) {
            return false;
        }
        CityMap map = draft.map;
        map.rebind(live, landManager::remainingByHolding);
        try {
            if (!map.reconcile(getMapCounts())) return false;
        } catch (RuntimeException e) {
            mapFailures++;
            System.out.println("The city map drawn aside could not be kept up, and will be drawn again: " + e);
            return false;
        }
        cityMap = map;
        return true;
    }

    /** How many months the map failed to keep up and was dropped. */
    public int getMapFailures() { return mapFailures; }

    /** The building types as the map draws them, by id. */
    public BuildingVisual.Type[] getMapTypes() { return BuildingVisual.table(buildingManager.getTemplates()); }

    /** Every type's standing count, by id: what the map's districts sum to. */
    public long[] getMapCounts() {
        long[] counts = new long[buildingManager.getMaxTemplateId() + 1];
        for (BuildingsTemplate t : buildingManager.getTemplates()) {
            if (t.getId() >= 0 && t.getId() < counts.length) counts[t.getId()] = buildingManager.getQuantity(t.getId());
        }
        return counts;
    }

    /** The map drawn canonically from the city as it stands. */
    private CityMap drawMap() {
        return CityMap.canonical(landManager.getCityLand(), landManager::remainingByHolding, getMapTypes(), getMapCounts());
    }

    /**
     * The month's change placed on the map (CityMap.reconcile()), after the
     * month's construction and demolitions; the land drawn again (a
     * restatement) draws the map again. A failure is logged and drops the map
     * - it is the picture, and the month goes on - to be drawn again when
     * next asked for.
     */
    private void reconcileMap() {
        if (cityMap == null) return;
        try {
            if (!cityMap.reconcile(getMapCounts())) cityMap = drawMap();
        } catch (RuntimeException e) {
            mapFailures++;
            cityMap = null;
            System.out.println("The city map could not keep up in month " + month + " and will be drawn again: " + e);
        }
    }

    /** The city's name. */
    public String getCityName() { return founding.getCityName(); }

    /** The city's money: its name, code and symbols. See Currency. */
    public Currency getCurrency() { return founding.getCurrency(); }

    /** The treasury this city was founded with, in thousands. */
    public double getFoundingCash() { return founding.getCash(); }

    /** The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. */
    public double getFoundingReserveUsd() { return founding.getReserveUsd(); }

    /** The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. */
    private java.util.List<BuildingsTemplate> catalogue() {
        if (!buildingManager.getTemplates().isEmpty()) return buildingManager.getTemplates();
        if (catalogueBeforeFounding == null) {
            BuildingManager reader = new BuildingManager();
            reader.initializeTemplates();
            catalogueBeforeFounding = reader.getTemplates();
        }
        return catalogueBeforeFounding;
    }
    private java.util.List<BuildingsTemplate> catalogueBeforeFounding;

    /**
     * What a founding of this treasury and vault buys, at a new city's
     * invoices over the catalogue - the founding screen's line under each
     * preset until 0.7.20, when the screen stopped saying what the money
     * buys; NewGameCheck and ReadPathCheck read it since. See Founding,
     * WHAT IT BUYS.
     */
    public Founding.Buys whatItBuys(double cash, double reserveUsd) {
        return Founding.whatItBuys(catalogue(), cash, reserveUsd);
    }

    /* ============= THE CONSTRUCTION SUBSIDY - removed in 0.7.1 =============
     *
     * A monthly retainer that kept builders on the books between projects.
     *
     * The 4,000-month playtest found that idle construction is loss-making, so
     * it sheds capacity in any lull - including the capacity a player just paid
     * for. Buy four depots, watch the population double, watch the sector scrap
     * all four sixty months later, and the city falls back to where it started
     * for the next eight centuries.
     *
     * That lever was a retainer, deliberately not free and not absolute: real
     * money, paid every month whether anything was being built or not,
     * protecting exactly as much capacity as it covered the payroll of.
     *
     * REMOVED IN 0.7.1. Nothing had paid it since the standing policy below
     * replaced it (found 2026-09-21, the-central-bank-opens.md section 4): the
     * field was set, saved, reset and reformed, and no line of the month read
     * it. The field, its save key and its four lines are gone; an old save's
     * key is left unread, which Gson does without being asked. The standing
     * policy is the lever now - see TreasuryLine.CONSTRUCTION_SUBSIDY, which
     * is what it pays the builders through.
     * ==================================================================== */

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

       UNTIL THE CEILING, since 0.7.0. The overdraft is the central bank's
       advance now, and once the advances reach the ceiling - CentralBank's
       dial, DEFAULT_ADVANCES_MONTHS of revenue until the player moves it
       (0.7.2) - a subsidy is a discretionary line: paid from cash at or above
       zero, the rest owed to the sector as arrears and paid when cash returns -
       Jerus's rule for a treasury past its ceiling, "pay promises first, cut
       the rest". See paySubsidyIfOwed() and treasuryPays().

       WHY IT IS A CAPITAL CONTRIBUTION, NOT REVENUE. It moves cash from the
       city to the sector and touches neither income statement. The sector's
       books still say it lost money, because it did - what changed is that
       somebody else absorbed it. Booking it as sector revenue would have been
       easier and would have made every subsidised sector report a permanent
       break-even, hiding the very thing the player needs to see to decide
       whether to keep paying.
       ==================================================================== */

    /** Keyed by the sector's name since the sector template - a seventh sector is a seventh key. */
    private final java.util.Map<String, Boolean> autoSubsidy = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> subsidyPaid = new java.util.LinkedHashMap<>();

    public boolean isAutoSubsidised(Sector sector){ return isAutoSubsidised(sector.key()); }
    public boolean isAutoSubsidised(String key)   { return autoSubsidy.getOrDefault(key, false); }

    public void setAutoSubsidised(Sector sector, boolean on){ setAutoSubsidised(sector.key(), on); }
    public void setAutoSubsidised(String key, boolean on) {
        if (key == null) return;
        boolean was = isAutoSubsidised(key);
        autoSubsidy.put(key, on);
        if (was != on) {
            decisions.record(DecisionLog.PROMISE, on ? key + " subsidised as standing policy"
                                                    : key + "'s standing subsidy stopped");
        }
    }

    /** What this sector was paid this month. Zero when it did not need it. */
    public double getSubsidyPaid(Sector sector){ return getSubsidyPaid(sector.key()); }
    public double getSubsidyPaid(String key)   { return subsidyPaid.getOrDefault(key, 0.0); }

    public double getTotalSubsidyPaid(){
        double total = 0;
        for (double d : subsidyPaid.values()) total += d;
        return total;
    }

    /** The protected sectors, by name, for the save. */
    public java.util.List<String> getSubsidisedSectors() {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, Boolean> e : autoSubsidy.entrySet()) if (e.getValue()) out.add(e.getKey());
        return out;
    }

    /**
     * One subsidy payment against a stated loss, for PolicyCheck.
     *
     * Package-private and named for what it is. It calls the real method rather
     * than reproducing it, because a test helper that does its own arithmetic
     * agrees with itself and proves nothing about the code that ships.
     */
    double subsidiseForTest(Sector sector, double netIncome){
        return paySubsidyIfOwed(sector, netIncome);
    }
    double subsidiseForTest(String key, double netIncome){
        return paySubsidyIfOwed(getSectors().byKey(key), netIncome);
    }

    /**
     * Puts the treasury at a stated figure, for a fixture that needs to CAUSE a
     * condition rather than wait for one.
     *
     * Package-private. It was written for RestructureCheck, which has to put a
     * city in front of a bond it cannot afford, and has to give another one
     * enough cash to attempt eight round trips. Playing a city into either
     * state would make the test about the trajectory instead of the rule.
     *
     * Since 0.7.10 it is also how forty-two fixtures in twenty-five harnesses
     * are handed the treasury they were written against, now that a city
     * founds on D$100M: the old D$2.5B (Founding.WEALTHY_CASH), or the whole
     * D$3.5B for ForeignCheck's devaluation city - each with a comment saying
     * so.
     */
    void setCashForTest(double amount){
        this.cash = amount;
    }

    /**
     * Tops a protected sector up to break-even.
     *
     * @return what was paid, so the caller can hand the loss counter the figure
     *         AFTER support rather than before it
     */
    private double paySubsidyIfOwed(Sector sector, double netIncome){

        subsidyPaid.put(sector.key(), 0.0);

        if (!isAutoSubsidised(sector) || netIncome >= 0) {
            return 0;
        }

        double owed = -netIncome;

        /*
         * Overdrawn if it must be - UNTIL THE CEILING (0.7.0). A subsidy is a
         * discretionary line: once the central bank's advances reach the
         * ceiling (CentralBank.ceiling(), the dial's months of revenue) it is
         * paid only from cash at or above zero, and the rest is owed to this
         * sector as arrears. See treasuryPays(). The construction sector's is
         * the construction subsidy; any other's is a business subsidy.
         */
        TreasuryLine line = sector == getSectors().construction()
                ? TreasuryLine.CONSTRUCTION_SUBSIDY : TreasuryLine.BUSINESS_SUBSIDIES;
        double paid = treasuryPays(line, owed, sector.key());
        // A plain add: nothing here may compute an amount, or the money the
        // city spent and the money the sector received could differ.
        sector.addCash(paid);
        subsidyPaid.put(sector.key(), paid);
        return paid;
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
    public double protectedConstructionCapacity(){
        return isAutoSubsidised(getSectors().construction())
                ? buildingManager.getTotalConstructionCapacity()
                : 0;
    }

    /**
     * EARNED (0.7.31's name for it): the tax take less the running programmes,
     * plus the utilities' net, at today's dials - the header's "+$X earned a
     * month". getEarnedToBudget() walks it to the budget's balance.
     *
     * READS, AND WRITES NOTHING (0.7.31, the Government spec's B8): it summed
     * through getTotalIncome(), whose getTaxIncome() strikes four of the
     * month's lines as it goes, so every draw of the header rewrote them.
     * getTaxIncomeNow() is the same sum, struck nowhere.
     */
    public double getIncome(){
        double income = economyManager.getTaxIncomeNow() - economyManager.getExpenses()
                + servicesManager.getServiceNetIncome();
        return income;
    }
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

    /** Every sector, in the registry's order. */
    public Sectors getSectors(){
        return economyManager.getSectors();
    }

    /** Every goods market. */
    public Markets getMarkets(){
        return economyManager.getMarkets();
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
        LandParcel cheapest = landManager.getMarket().cheapest();
        if (cheapest == null) {
            landManager.updateMarket(populationManager.getPopulation());
            cheapest = landManager.getMarket().cheapest();
        }
        return cheapest != null && buyLandParcel(cheapest.getId());
    }

    /* ===================================================================
       LAND IS BOUGHT IN DOLLARS (0.7.6)

       Jerus: "when you buy land, make it so that it costs USD not domestic
       currency, and basically how it would work is a little toggle at the
       top to choose, when you buy land, to use up your USD reserves or to
       convert cash into usd exactly to buy the land, and the default is that
       you convert."

       A parcel is priced in US dollars (LandMarket), and the world is paid
       one of two ways, the toggle's (landPaidFromVault, saved):

       CONVERTING, the default. The treasury pays usd x rate in local money
       through TreasuryLine.LAND, the line land always went out on - so the
       budget's "Land bought" and the refusal rule are what they were - and
       ForeignAccounts.buyAndSpendDollarsForLand() buys exactly those dollars
       and hands them to the seller in one movement: the vault ends where it
       began. The local cash left the city; that is the whole of it.

       FROM THE VAULT. ForeignAccounts.spendReservesOnLand() pays the seller
       out of reservesUsd and no local money moves - a capital transaction
       like the defence's. The budget still carries the land at usd x rate
       (its value on the city's books is what it cost in local money on the
       day), so the journal carries the same amount the other way, "Bought
       land with US$X of reserves": the bridge from the budget to the cash
       then closes with nothing left over. A vault too short for the parcel
       pays what it holds and the rest is converted, and the log says so - a
       purchase never fails for the toggle's sake, only for want of money.

       NEITHER IS IN AN AUDIT WINDOW, and neither ever was: land is bought
       between two presses, before the next month's opening pools are read,
       so MoneyAudit has never carried a land line and needs none. What it
       would see is what LandCheck asserts on the pools around a purchase -
       converting, the treasury's pool down by exactly usd x rate and no
       other pool up (money across the edge); from the vault, no pool moved.
       =================================================================== */

    /** Whether the land office pays out of the vault rather than converting cash (0.7.6). */
    private boolean landPaidFromVault;

    /** True when land is paid for out of the vault; false - the default - converts cash. */
    public boolean isLandPaidFromVault() { return landPaidFromVault; }

    /** The land office's toggle, applied at once to the next purchase. */
    public void setLandPaidFromVault(boolean fromVault) {
        if (fromVault != landPaidFromVault) {
            decisions.record(DecisionLog.CURRENCY, fromVault ? "Land paid from the vault" : "Land paid in local money");
        }
        this.landPaidFromVault = fromVault;
    }

    /**
     * Buys one specific listed plot - the land office screen's action - in US
     * dollars at today's rate, paid the way the toggle says. See LAND IS
     * BOUGHT IN DOLLARS.
     *
     * @return true if it was bought. False means it was not listed or not
     *         affordable, and nothing changed.
     */
    public boolean buyLandParcel(int parcelId){
        LandParcel parcel = landManager.getMarket().find(parcelId);
        if (parcel == null) {
            return false;
        }
        double rate = foreign.getRate();
        double usd = parcel.getPriceUsd();
        double fromVault = landPaidFromVault ? Math.min(usd, foreign.getReservesUsd()) : 0;
        double cost = landManager.buyParcel(parcelId, landPayable(parcel),
                populationManager.getPopulation());
        if (cost <= 0) {
            return false;
        }
        double paidFromVault = landPaidFromVault ? foreign.spendReservesOnLand(fromVault) : 0;
        double converted = usd - paidFromVault;
        double convertedLocal = 0;
        if (converted > 0) {
            convertedLocal = foreign.buyAndSpendDollarsForLand(converted);
            treasuryPays(TreasuryLine.LAND, convertedLocal);
        }
        if (paidFromVault > 0) {
            treasuryJournal.record(String.format("Bought land with %s of reserves",
                    usdWords(paidFromVault)), paidFromVault * rate);
        }
        // The plot's size in square kilometres since 0.7.13, as the office shows it (under a hundredth of one in square metres since 0.7.68).
        String area = LandManager.areaWords(parcel.getSizeSqFt());
        String here = getCurrency().qualifiedSymbol();
        /*
         * IN THE SCREENS' MONEY SINCE 0.7.26 (Formats.amount(), rate()): it
         * printed the model's thousands with a "k" stuck on and every digit
         * kept - "for US$101,800k, converting D$72,000k" - beside an office
         * whose cards said "US$101.8M" and "D$72.0M" for the same plot.
         */
        if (paidFromVault <= 0) {
            lastLandReceipt = String.format("Bought %s for %s, converting %s of cash at %s%s"
                    + " to the dollar.", area, usdWords(usd), localWords(here, convertedLocal),
                    here, Formats.INSTANCE.rate(rate));
        } else if (converted <= 0) {
            lastLandReceipt = String.format("Bought %s for %s out of the vault, which holds"
                    + " %s now. No cash moved.", area, usdWords(usd), usdWords(foreign.getReservesUsd()));
        } else {
            lastLandReceipt = String.format("Bought %s for %s. The vault held only %s,"
                    + " so that went and the other %s was converted from %s of cash.",
                    area, usdWords(usd), usdWords(paidFromVault), usdWords(converted),
                    localWords(here, convertedLocal));
        }
        lastLandReceiptMonth = month;
        GameLog.note(lastLandReceipt);
        return true;
    }

    /** Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). */
    private static String usdWords(double thousands) {
        return Formats.INSTANCE.amount(thousands).replace("$", Currency.FOREIGN_SYMBOL);
    }

    /** ...and thousands of local money, with its own mark: "D$72.0M". */
    private static String localWords(String here, double thousands) {
        return Formats.INSTANCE.amount(thousands).replace("$", here);
    }

    /**
     * The most the city can pay for this parcel today, in local money: its
     * cash, and - paying from the vault - the vault's part of the parcel at
     * today's rate. Affordable when the treasury can pay the converted part
     * out of its cash, as a purchase always had to be; the vault's part needs
     * no cash at all.
     */
    private double landPayable(LandParcel parcel) {
        double fromVault = landPaidFromVault
                ? Math.min(parcel.getPriceUsd(), foreign.getReservesUsd()) : 0;
        return Math.max(0, cash) + fromVault * foreign.getRate();
    }

    /** Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks landNeedsFunding() instead. */
    public boolean canAffordParcel(LandParcel parcel) {
        return parcel != null && parcel.localPrice(foreign.getRate()) <= landPayable(parcel);
    }

    /* -------------------------------------------------------------------
       WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13)

       Jerus: "on the land section, if you are on buy by converting and you
       dont have enough, you can stilll click buy, just the popup to issue
       debt appears, but if you are in buy with reserves, and click buy,
       then pop up to issue foreign debt should appear (aka the short or the
       20y, like with buildings)" - and "add a button to buy multiple, so for
       example buy the next 5 land options, and then also debt popup appears
       if not enough".

       THE GAP IS IN THE MONEY THE TOGGLE PAYS IN: local money converting
       (landCashGap()), US dollars from the vault (landVaultGapUsd()). The
       land office's funding page sizes the build screen's two offers to it -
       converting, quoteLongBondForCash() at BUILD_BOND_YEARS and quoteTBill()
       at BUILD_NOTE_MONTHS; from the vault, the same two terms in dollars
       (quoteForeignForCash(), booked by handleForeignForCash() with the
       dollars held in reserve) - and, from the vault, today's third way:
       buyLandParcel() takes what the vault has and converts the rest, which
       the page offers as a choice when the cash covers it
       (landTopUpCovers()), never by itself. Then buyLandParcels().

       SEVERAL AT ONCE ARE BOUGHT ONE AT A TIME, each through buyLandParcel(),
       because the market's own rule makes that the same thing: a listed
       plot's price is frozen at listing (LandMarket, WHAT IS LISTED STAYS
       LISTED), and a purchase moves only the plots listed AFTER it - each
       one refills the window priced against the bigger city
       (LandManager.buyParcel()). So the N taken off the shelf together cost
       what they cost one by one, and leave the same trail. landShelf() is
       the office's own order, the order the screen lays the cards in.
       ------------------------------------------------------------------- */

    /** The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. */
    public java.util.List<LandParcel> landShelf() {
        java.util.List<LandParcel> shelf = landManager.getMarket().getListing();
        shelf.sort(java.util.Comparator.comparingDouble(LandParcel::getUsdPerSqFt));
        return shelf;
    }

    /** The first n plots of landShelf(), by id: what "Buy the next N plots" buys. */
    public java.util.List<Integer> nextLandParcels(int n) {
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (LandParcel parcel : landShelf()) {
            if (ids.size() >= n) break;
            ids.add(parcel.getId());
        }
        return ids;
    }

    /** What these plots are listed at together, in US dollars; an id not on offer counts nothing. */
    public double landPriceUsd(java.util.List<Integer> ids) {
        double usd = 0;
        for (int id : ids) {
            LandParcel parcel = landManager.getMarket().find(id);
            if (parcel != null) usd += parcel.getPriceUsd();
        }
        return usd;
    }

    /** ...and what that is in local money at today's rate - what converting pays. */
    public double landPriceLocal(java.util.List<Integer> ids) {
        double local = 0;
        for (int id : ids) {
            LandParcel parcel = landManager.getMarket().find(id);
            if (parcel != null) local += parcel.localPrice(foreign.getRate());
        }
        return local;
    }

    /**
     * Converting: what the treasury's cash is short of these plots' local
     * price, never below nothing - an overdraft included, as the build
     * screen's buildFundingGap() counts it, so a loan of this much leaves
     * the cash buyLandParcel() needs.
     */
    public double landCashGap(java.util.List<Integer> ids) {
        return Math.max(0, landPriceLocal(ids) - cash);
    }

    /** From the vault: what the vault is short of these plots' dollar price, never below nothing. */
    public double landVaultGapUsd(java.util.List<Integer> ids) {
        return Math.max(0, landPriceUsd(ids) - foreign.getReservesUsd());
    }

    /** True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. */
    public boolean landNeedsFunding(java.util.List<Integer> ids) {
        return landPaidFromVault ? landVaultGapUsd(ids) > 0 : landCashGap(ids) > 0;
    }

    /**
     * True when buyLandParcels() would buy every one of these today: paying
     * from the vault, the cash covers at today's rate the dollars the vault
     * lacks, so what the vault has goes and the rest is converted - each
     * purchase passing buyLandParcel()'s own test in turn, because the cash
     * each one converts only falls. Converting, the cash covers them. One
     * plot's answer is canAffordParcel()'s.
     */
    public boolean canAffordLandParcels(java.util.List<Integer> ids) {
        double outOfCash = landPaidFromVault ? landVaultGapUsd(ids) * foreign.getRate() : landPriceLocal(ids);
        return outOfCash <= Math.max(0, cash);
    }

    /** From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. */
    public boolean landTopUpCovers(java.util.List<Integer> ids) {
        return landPaidFromVault && canAffordLandParcels(ids);
    }

    /** ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. */
    public double landTopUpLocal(java.util.List<Integer> ids) {
        return landVaultGapUsd(ids) * foreign.getRate();
    }

    /**
     * Buys these plots in the order given, each through buyLandParcel() -
     * paid the way the toggle says, one at a time, as the market's rule
     * has it - and stops at the first it cannot. How many were bought. The
     * land office's receipt names them all when there was more than one.
     */
    public int buyLandParcels(java.util.List<Integer> ids) {
        int bought = 0;
        double sqFt = 0, usd = 0;
        for (int id : ids) {
            LandParcel parcel = landManager.getMarket().find(id);
            if (parcel == null || !buyLandParcel(id)) break;
            bought++;
            sqFt += parcel.getSizeSqFt();
            usd += parcel.getPriceUsd();
        }
        if (bought > 1) {
            lastLandReceipt = String.format("Bought %d plots, %s in all, for %s. The last: %s",
                    bought, LandManager.areaWords(sqFt), usdWords(usd), lastLandReceipt);
            GameLog.note(String.format("Bought %d plots, %s in all, for %s.",
                    bought, LandManager.areaWords(sqFt), usdWords(usd)));
        }
        return bought;
    }

    /**
     * What the last land purchase cost and how it was paid, in the player's
     * words - the land office shows it under ON OFFER (under the toggle until
     * 0.7.26), and a short vault says here that the rest was converted. Not
     * saved: it is the answer to the button just pressed, and a reloaded
     * city has pressed nothing - and it is the month's, so once the month
     * turns it is gone.
     */
    private String lastLandReceipt = "";
    private int lastLandReceiptMonth;

    /** The last land purchase's receipt, or "" once the month it was made in has turned. */
    public String getLastLandReceipt() {
        return lastLandReceiptMonth == month ? lastLandReceipt : "";
    }

    /** The offers standing. */
    public java.util.List<LandParcel> getLandListing(){
        return landManager.getListing();
    }

    /* -------------------------------------------------------------------
       THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14)

       What the Build tab's shortcut (LAND FREE's "Buy the best land") and its
       refusal pages buy (0.7.61, batch J4): in general the most dry ground a
       dollar among the offers the city can afford that are not mostly sea,
       the nearer on a tie - or, affording none, among them all, so the
       funding page can be sized to it; for a shortfall, the cheapest offer
       whose dry ground covers it and that is bare ground, if one does
       (below); for a deposit, the cheapest offer holding the resource
       (LandMarket.cheapestWith(), since 0.7.64 - what the test player buys
       its iron with; until then the most of its sites a dollar,
       LandMarket.richest()); for a coast, the cheapest offer with sea in it.

       A SHORTFALL IS MET WITH GROUND, NOT ORE (0.7.58, batch J1c). An offer
       holding ore is priced by its tonnes - since 0.7.64 a whole field's, the
       default world's founding field 449 Mt at about US$180M, and even a
       one-site field 12.8 Mt at about US$5.1M - against a few hundred
       thousand for the ground under it. "The cheapest offer that covers it"
       bought, for the playtest's founding village at 0.7.58 (when fields
       were shared site by site), the one offer big enough - West 6, two
       sites of iron, US$11.4M - where the best-value offers covered the same
       ground for a tenth of that; the treasury it left could not afford the
       next plant, and the city stood still for two hundred months. A
       building short of ground has not asked for ore, so the shortfall rule
       passes over an offer holding a priced resource
       (LandMarket.bareGround()); with no bare offer big enough it buys the
       best value, as for room, and what is left of the shortfall is asked
       again.
       ------------------------------------------------------------------- */

    /** What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. */
    public record LandNeed(Kind kind, double drySqFt, Resource resource) {

        /** The four needs. */
        public enum Kind { ROOM, SHORTFALL, DEPOSIT, COAST }

        /** Room to grow. */
        public static LandNeed room()                        { return new LandNeed(Kind.ROOM, 0, null); }

        /** This much more dry ground than the city has free. */
        public static LandNeed shortfall(double drySqFt)     { return new LandNeed(Kind.SHORTFALL, drySqFt, null); }

        /** A site of this resource to stand a mine or a well on. */
        public static LandNeed deposit(Resource resource)    { return new LandNeed(Kind.DEPOSIT, 0, resource); }

        /** Sea, for a desalination plant (batch J2). */
        public static LandNeed coast()                       { return new LandNeed(Kind.COAST, 0, null); }
    }

    /** The offer that best meets a need, by THE BEST OFFER's rules; null when none does. */
    public LandParcel bestOffer(LandNeed need) {
        LandMarket market = landManager.getMarket();
        switch (need.kind()) {
            case DEPOSIT:
                return market.cheapestWith(need.resource() == null ? Resource.IRON : need.resource());
            case COAST:
                return market.cheapestWithSea();
            case SHORTFALL: {
                LandParcel cheapest = null;
                for (LandParcel p : market.getListing()) {
                    if (p.getSizeSqFt() < need.drySqFt() || !LandMarket.bareGround(p)) continue;
                    if (cheapest == null || p.getPriceUsd() < cheapest.getPriceUsd()
                            || (p.getPriceUsd() == cheapest.getPriceUsd() && market.nearer(p, cheapest))) cheapest = p;
                }
                if (cheapest != null) return cheapest;
                return bestOffer(LandNeed.room());
            }
            default: {
                LandParcel best = null;
                for (LandParcel p : market.getListing()) {
                    if (p.isMostlySea() || !canAffordParcel(p)) continue;
                    if (best == null || p.getDryKm2PerUsd() > best.getDryKm2PerUsd()
                            || (p.getDryKm2PerUsd() == best.getDryKm2PerUsd() && market.nearer(p, best))) best = p;
                }
                return best != null ? best : market.bestValue();
            }
        }
    }

    public BusinessInvestment getBusinessInvestment(){
        return businessInvestment;
    }

    /** Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. */
    private BusinessInvestment.OrderWatch orderWatch;

    /** Harnesses only: tell this watch every order the three searches decide; null to stop. Nothing in the game sets one. */
    public void watchOrders(BusinessInvestment.OrderWatch watch) {
        this.orderWatch = watch;
        businessInvestment.watchOrders(watch);
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
        refusedOnPrice.clear();
        refusedByLender.clear();
        heldForDownPayment.clear();

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
         *
         * BY SECTOR since 0.7.8, because each sector's book holds its own
         * allowance and a write-off is drawn against its own first (Bank,
         * THE ALLOWANCE). The same total as lastWriteOff, which since 0.7.8
         * is each sector's slice of defaulted firms (BusinessDebtManager, A
         * SECTOR DEFAULTS A SLICE AT A TIME) and the whole-sector restructure
         * only as the backstop, for a sector with nothing left.
         */
        /*
         * ...EXCEPT WHAT THE CITY INSURED (0.7.11). The part of a write-down
         * that came off a landlord's insured mortgages is the insurer's: the
         * treasury pays the bank that balance - a claim, and a promise
         * (TreasuryLine.MORTGAGE_INSURANCE_CLAIMS), so a treasury that is
         * short draws its advances for it as it would for a coupon - and the
         * bank books only the rest as its loss. Its capital, its allowance
         * and its provision take none of the insured part. The borrower's
         * record, surcharge and ban are unchanged: the default is its own.
         */
        BusinessDebtManager restructured = economyManager.getBusinessDebtManager();
        for (String s : restructured.sectors()) {
            double insured = restructured.getInsuredWrittenOffThisMonth(s);
            // ...and what the defaults took off the sector's bonds it holds,
            // at what they cost it (0.7.12): its share of the bondholders'
            // loss, at a bond's own recovery since round 2 (BusinessDebtManager,
            // RECOVERIES BY INSTRUMENT).
            bank.writeOffSector(s, restructured.getWrittenOffThisMonth(s) - insured + bondMarket.takeBankLoss(s));
            if (insured > 0) {
                bank.receiveInsuranceClaim(treasuryPays(TreasuryLine.MORTGAGE_INSURANCE_CLAIMS, insured));
            }
        }

        /*
         * EVERY SECTOR IN THE REGISTRY'S ORDER, each asked its own question.
         *
         * Land is refreshed before each, not once for all: consider() builds
         * immediately, so real estate taking the last of the land has to be
         * visible to retail when retail plans a moment later.
         *
         * The bank first and separately - see BusinessInvestment.planBank():
         * retail's money, retail's investor, but not retail's one decision,
         * so that wanting a branch cannot stop the city building shops.
         *
         * The order of the six is the order the handlers were asked in, and
         * it carried two dependencies worth keeping: mines before mills,
         * because a mill's business case is the ore; and construction last,
         * because it reads the queue everyone else just added to. Both hold
         * in Sectors.KEYS.
         */
        refreshLand();
        consider(businessInvestment.planBank(),
                sectorInvestor(getSectors().retail().key()), "Bank");

        for (Sector sector : getSectors().all()) {
            // Held only by a harness measuring something else; see
            // BusinessInvestment.holdSector.
            if (businessInvestment.isHeld(sector.key())) continue;
            refreshLand();
            consider(sector.plan(businessInvestment, this), sectorInvestor(sector.key()));
        }
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

    /**
     * Every school kind's tuition scale, from the policy to the schools
     * (0.7.6) - at the month's education step and on the load path, where
     * one scale was told until the nine parted. Telling the schools only
     * getTuitionScale() would put a city's nine prices back to one on every
     * load, which is what the one income rate would have done to the three
     * bases in 0.7.4.
     */
    private void tellTheSchoolsTheirPrices(TaxPolicy tax) {
        for (EducationType kind : EducationType.values()) {
            if (kind != EducationType.NONE) education.setTuitionScaleOf(kind, tax.tuitionScaleOf(kind));
        }
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
        int rowCount = Household.ROWS;
        double[] rowPeople = new double[rowCount];
        double[] rowHomes  = new double[rowCount];
        for (PayTier t : PayTier.values()) {
            rowPeople[t.ordinal()] = families.peopleIn(t);
            rowHomes[t.ordinal()]  = families.workingHouseholdsIn(t);
        }
        rowPeople[HouseholdAccounts.RETIRED] = families.retiredPeople();
        rowHomes[HouseholdAccounts.RETIRED]  = families.retiredHouseholds();
        // ...and the people outside the families, one to a household.
        rowPeople[HouseholdAccounts.UNEMPLOYED] = unemployment.getPool();
        rowHomes[HouseholdAccounts.UNEMPLOYED]  = unemployment.getPool();
        rowPeople[HouseholdAccounts.STUDENTS]   = families.getSeekers(FamilyModel.Seeker.STUDENT);
        rowHomes[HouseholdAccounts.STUDENTS]    = families.getSeekers(FamilyModel.Seeker.STUDENT);
        rowPeople[HouseholdAccounts.ORPHANS]    = families.getOrphansTotal();
        rowHomes[HouseholdAccounts.ORPHANS]     = families.getOrphansTotal();
        rowPeople[HouseholdAccounts.PRISONERS]  = crime.prisoners();
        rowHomes[HouseholdAccounts.PRISONERS]   = crime.prisoners();
        households.setOutsideMoney(economyManager.getEiPremiums(),
                economyManager.getEiBenefits(), economyManager.getStudentGrants(),
                // ...and the health premium, off the same payslips (2026-09-19).
                economyManager.getHealthPremiums());
        /*
         * ...AND THE FOOD VOUCHERS (0.7.43), by the cells that were paid them
         * at the sale these books settle - a position each cell carries, so a
         * reloaded city's books read the same. The treasury paid exactly
         * this (a promise); it is in the savings already, not the take-home.
         */
        double[] foodAid = householdBalance.foodAssistanceByRow();
        double foodAidTotal = 0;
        for (double a : foodAid) foodAidTotal += a;
        households.setFoodAssistance(foodAidTotal, foodAid);
        /*
         * WHO PAID FOR CARE, AND WHAT THE BILL WOULD HAVE BEEN (2026-09-19).
         * The clinic's fees are split over the heads who paid - a household
         * the price turned away last month is not billed for care it did not
         * get - and the same rows are told the bill at full service, which is
         * what each household measures its means against below. The scale
         * is the policy's, told to the service here as well as at the
         * month's health step, so a reloaded city reads its own scale.
         */
        healthcare.setFeeScale(tax.getHealthFeeScale());
        /*
         * ...AND THE TWO EDUCATION DIALS THE LEDGER READS (2026-09-21), for
         * the same reason and at the same place: the loan rate before the
         * cells settle (or re-strike, on the load path) below, and the
         * tuition scale before anything reads a fee, so a reloaded city
         * charges its own price and its own interest from the first read.
         * The scale is told again at the month's education step; the rate
         * is told here only, which is every month and the load path both.
         */
        householdBalance.setStudentLoanRate(tax.getStudentLoanRate());
        tellTheSchoolsTheirPrices(tax);
        households.setCarePaid(householdBalance.carePaidShares());
        households.setCareBills(healthcare.getTreatmentFees(), healthcare.fullTreatmentFees());
        /*
         * What the city took at the barrier, read from the figure it is shown
         * COLLECTING rather than recomputed here - the same one-source rule the
         * health fees and the tuition above are read by, and for the same
         * reason: two copies of one number is how this codebase has produced
         * money from nowhere before.
         */
        households.setTransitFares(economyManager.getTransitFares());
        /*
         * ...AND WHO RODE AND WHO DROVE, BY ROW (0.7.49), so the fares fall
         * on the rows that ride and the month's fuel on the rows that drive
         * (HouseholdAccounts, THE COMMUTE, BY ROW). The road's own split read
         * back onto the rows: of each row's commuters with no car of their
         * own, the share of the car-less who rode; of its owners, the share
         * of owners who chose the bus, and the rest drove. The retired, the
         * out of work and the students commute nowhere and pay neither.
         */
        {
            InfrastructureManager im = getInfrastructureManager();
            double[] workers = householdBalance.commuteWorkersByRow(), cars = householdBalance.commuteCarsByRow();
            double carless = im.getCaptiveCommuters(), owners = im.getOwnerCommuters();
            double carlessRode = carless > 0 ? im.getCaptiveRiders() / carless : 0;
            double ownersRode = owners > 0 ? im.getChoiceRiders() / owners : 0;
            double[] riders = new double[workers.length], drivers = new double[workers.length];
            for (int r = 0; r < workers.length; r++) {
                riders[r] = Math.max(0, workers[r] - cars[r]) * carlessRode + cars[r] * ownersRode;
                drivers[r] = cars[r] * (1 - ownersRode);
            }
            households.setCommute(motoring.getFuelBill(), motoring.getFuelImports(), riders, drivers);
        }
        /*
         * ...AND THE BANK'S ACCOUNT FEE (0.7.7), struck here on both paths:
         * the fee at the month's price index, told to the cells that pay it
         * with their other fixed bills, and what each row will be charged,
         * told to the books before they are struck - from the balance's own
         * census, so the line and the money agree. See Bank, FEES.
         */
        // Struck at the expected price level since 0.7.42, like every money
        // constant (restrikeMoneyConstants() seeds the base); at the index
        // until then.
        householdBalance.setAccountFee(bank.accountFee(1));
        households.setAccountFees(householdBalance.accountFeesByRow(families::get));

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
        double rentPaid = getSectors().realEstate().statement().salesToHouseholds;
        double retailSales = getSectors().retail().statement().salesToHouseholds;

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

        double[] doors = rowDoors();
        households.updateByTier(wagePerTier, taxPerTier, rowPeople, rowHomes,
                householdBalance.plannedShare(), interestPerRow, doors);

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
            /*
             * ...AND THE FARE, SINCE 2026-09-16. The third fee, and the one
             * the city had been collecting from nobody - see
             * HouseholdAccounts.fares for how long and why nothing noticed.
             * Here it becomes real money: what this row's people paid to ride
             * comes out of the same waterfall the clinic's fees do.
             * ...AND THE FUEL, SINCE 0.7.49: what this row's drivers burned
             * getting to work, the same way (Motoring, THE FUEL).
             */
            fees[r] = households.getRowHealthcare(r) + households.getRowTuition(r)
                    + households.getRowFares(r) + households.getRowFuel(r);
            actualShopping[r] = households.getRowShopping(r);
        }
        System.arraycopy(fees, 0, rowFeesSettled, 0, Math.min(fees.length, rowFeesSettled.length));
        ham.citybuildersim.sectors.Retail shops = getSectors().retail();

        /*
         * PER CELL, since 2026-09-10. The balance is handed the census - who
         * lives in each shape at each tier, straight off the family model -
         * and the ROW money above, and splits the money across the cells
         * itself by the one rule that makes the cells sum to the rows. The
         * row arrays are still what the books show; the cells are what the
         * households are.
         */
        /*
         * ...AND WHAT SHARE OF THEIR SPENDING ABOVE SUBSISTENCE THEY PLAN
         * (0.7.3), struck once a month on the real deposit rate, before the
         * plan on both paths: the month's own and the load path's re-strike,
         * which derives it again from the deposit rate and the price index
         * the save restored - so nothing is saved for it. See
         * HouseholdBalance's banner AND WHAT IT SPENDS ANSWERS THE REAL RATE.
         */
        householdBalance.setSpendFactor(spendFactor());
        /*
         * ...AND WHAT THEY TAKE TO THE GROCER (0.7.43), on both paths: the
         * price a full basket is still wanted at, and the food assistance
         * dial each household's voucher is struck on as it plans. See
         * HouseholdBalance, GROCERIES AT A PRICE.
         */
        householdBalance.setSatiationPrice(getSectors().retail().getSatiationPrice());
        householdBalance.setFoodAssistance(tax.getFoodAssistance());
        if (accrue) {
            // At the month's rate, for a household that sells its paper
            // abroad to eat - see Household.settle().
            householdBalance.setExchangeRate(foreign.getRate());
            // ...and the month's one ratio for the city's paper at home
            // (0.7.1): the households' book at the curve over its face, what
            // it counts for and what the desk pays for it. Saved, because the
            // plan reads it and the load path re-strikes the plan.
            householdBalance.setPaperRatio(debtManager.householdBookRatio());
            /*
             * ...AND THE TREATMENT BILLS, BY ROW (2026-09-19): what the row
             * was billed and what it would have been billed had all of its
             * people paid, for the test of who can afford the clinic next
             * month. See HouseholdBalance's banner THE PRICE AT THE CLINIC
             * DOOR.
             */
            double[] careBilled = new double[rowCount];
            double[] careFull = new double[rowCount];
            for (int r = 0; r < rowCount; r++) {
                careBilled[r] = households.getRowCareBilled(r);
                careFull[r] = households.getRowCareFull(r);
            }
            householdBalance.setCareBills(careBilled, careFull);
            householdBalance.advanceMonth(families::get, disposable,
                    households.rentPerHousehold(), fees, actualShopping,
                    // The bank's household rate, which each household's own
                    // months owed sit on (0.7.7; the city's rate until then).
                    shops.getStoreSellPrice(), bank.householdRate(debtManager.getPolicyRate()),
                    /*
                     * THE HONEST SHARE, NOT THE SHOPS' OWN (2026-09-17). This
                     * used to be getSupplyRatio(), whose denominator is demand
                     * AFTER the shops capped it at what they could serve - so
                     * the hunger measure downstream divided by a number the
                     * shops had chosen. See Retail.getHouseholdShare().
                     */
                    shops.getHouseholdShare());
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
                    shops.getStoreSellPrice(), bank.householdRate(debtManager.getPolicyRate()));

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
                    householdBalance.plannedShare(), interestPerRow, doors);
        }

        /*
         * THE SHELF IS RESTOCKED FOR WHAT THIS CITY EATS, struck from the
         * household ledger that was just settled rather than from a constant.
         * Before the shops can price or restock anything they have to know
         * what a person-month is made of - see Retail.setBasket().
         */
        shops.setBasket(cityBasketPerHead());
        /*
         * ...AND SO DO THE KITCHENS, from the SAME call and not a copy of it.
         * A meal out replaces groceries, which it can only do honestly if it
         * is made of the food groceries are made of - so the two sectors read
         * one basket, and the day Consumption changes what a person-month is,
         * both of them change with it. See Restaurants.setBasket().
         */
        getSectors().restaurants().setBasket(cityBasketPerHead());
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
            bank.writeOffHouseholds(householdBalance.getWrittenOff());
            // ...and its fees (0.7.7): the account fees they paid, cash from
            // outside the pools and declared; and the fee on what they drew,
            // added to what they owe, which moves no cash at all.
            bank.takeAccountFees(householdBalance.totalAccountFees());
            // ...and who paid them: the bank's customers, whom its branches
            // share (0.7.19, Bank's THE BRANCHES, BY THEIR CUSTOMERS) - the
            // fees over the month's fee, so customers x fee is the fees taken.
            double feeEach = householdBalance.getAccountFee();
            bank.setCustomers(feeEach > 0 ? householdBalance.totalAccountFees() / feeEach : 0);
            bank.bookLoanFees(householdBalance.totalLoanFees());

            /*
             * THE PEOPLE OUTSIDE THE FAMILIES' MONTH, on the city's side.
             * Who could not pay their rent is moved at the demographics; the
             * students' loans leave the treasury and the graduates' repayments
             * come back, the month they are struck, as the bank's do. What a
             * graduate who left the city still owed is the treasury's loss - its
             * book, not its cash.
             */
            unemployment.noteEvicted(householdBalance.getEvicted());
            studentLoansLent = householdBalance.totalStudentBorrowed();
            studentLoansRepaid = householdBalance.totalStudentRepaid();
            studentLoansWrittenOff = householdBalance.getStudentDebtTakenAway();
            /*
             * ...AND THE INTEREST THE GRADUATES PAID ON THEM (2026-09-21),
             * Jerus's "you get the interest if there is any". Into the cash
             * here with the principal, and into the budget as its own
             * revenue line through EconomyManager, which is why it is NOT in
             * the journal's line below: a budget line is already in the
             * bridge's first row, and naming it twice would count it twice.
             * Nothing at the default rate, and x + 0.0 is x.
             */
            studentLoanInterest = householdBalance.totalStudentInterest();
            cash += studentLoansRepaid + studentLoanInterest;
            // The loans themselves: lent at enrolment, a promise (0.7.0).
            treasuryPays(TreasuryLine.STUDENT_LOANS, studentLoansLent);
            economyManager.setStudentLoanInterest(studentLoanInterest);
            // Principal lent and principal back: neither is a budget line (the
            // grant is, and so is the interest), so the bridge names it. See
            // TreasuryJournal.
            treasuryJournal.record("Lent to students, net of repayments",
                    studentLoansRepaid - studentLoansLent);
        }
    }


    /* =====================================================================
       THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java.
       The ownership rate carried across a load stays here because the load
       path reads it.
       ===================================================================== */

    /**
     * The ownership rate the save was taken at, applied inside the rebuild.
     *
     * CARRIED BECAUSE OF WHEN THE CELLS COME BACK. The cars live in the
     * household cells and the cells are restored AFTER
     * rebuildSimulationState() - by design, and for reasons the note at that
     * restore gives - while the road ratio is struck INSIDE it. So the network
     * would read an unmotorised city for the whole of the first month back,
     * which is exactly the bug the railway's share note one line above
     * setRoadRatio() describes. -1 means a save from before cars existed, and
     * the cells' own figure (zero) is then right.
     */
    private double carriedCarOwnership = -1;

    /**
     * ...and the month's transit bill as 6d struck it (0.7.49), applied
     * inside the rebuild for the same reason: the rebuild would strike it
     * at the fill the month ended on, not the one it was paid at. -1 means
     * a save from before 0.7.49, and the rebuild derives it as it always
     * did. See DataSave.transitBill.
     */
    private double carriedTransitBill = -1;

    /**
     * ...and the commute as 6d struck it (0.7.49): the share of the working
     * cells' workers with no car of their own, because the cells are not
     * back when the road ratio is first struck (carriedCarOwnership's
     * reason), and a journey's fuel, because it was struck at 6d's exchange
     * rate and the rate moves after it. -1 in a save from before 0.7.49:
     * derived as the month would. See InfrastructureManager, WHO RIDES, BY
     * WHAT THEY PAY.
     */
    private double carriedCaptiveShare = -1, carriedFuel = -1;

    /** ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. */
    private double[] carriedFuelMonth;

    /** The households' car market; runs once a month, after the ledger. See Motoring. */
    private final Motoring motoring = new Motoring();

    /** The car market, for the screens and the harnesses that read past the getters below. */
    public Motoring getMotoring() { return motoring; }

    /** Cars the households bought this month. */
    public double getHouseholdCarsBought() { return motoring.getHouseholdCarsBought(); }

    /** ...what they paid for them, and what of that left the country. */
    public double getHouseholdCarSpend()   { return motoring.getHouseholdCarSpend(); }
    public double getHouseholdCarImports() { return motoring.getHouseholdCarImports(); }

    /** ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). */
    public double getHouseholdFuel()       { return households.getFuel(); }

    /** ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. */
    public double getHouseholdFuelImports() { return households.getFuelImports(); }

    /** Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). Zero until a month is settled; never saved. */
    private final double[] rowFeesSettled = new double[Household.ROWS];

    /** ...one row's. */
    public double getRowFeesSettled(int row) { return row >= 0 && row < rowFeesSettled.length ? rowFeesSettled[row] : 0; }

    /** ...and what of it the bank advanced rather than the household finding. */
    public double getHouseholdCarCredit()  { return motoring.getHouseholdCarCredit(); }

    /** Cars that changed hands second-hand this month. */
    public double getUsedCarsTraded()      { return motoring.getUsedCarsTraded(); }

    /** ...against what was put up for sale, which in a crash is far more. */
    public double getUsedCarsOffered()     { return motoring.getUsedCarsOffered(); }

    /** What one went for. Zero in a month when nobody offered one. */
    public double getUsedCarPrice()        { return motoring.getUsedCarPrice(); }

    /** What the buyers paid for them, all in. */
    public double getUsedCarSpend()        { return motoring.getUsedCarSpend(); }

    /** ...and what of that a lender advanced. */
    public double getUsedCarCredit()       { return motoring.getUsedCarCredit(); }

    /* =====================================================================
       THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java.
       ===================================================================== */

    /** The boutiques and the kitchens; each runs once a month, after the cars. See LuxuryCounter. */
    private final LuxuryCounter luxuryCounter = new LuxuryCounter();

    /** The luxury counter, for the screens and the harnesses that read past the getters below. */
    public LuxuryCounter getLuxuryCounter() { return luxuryCounter; }

    /** Meals the kitchens served the households this month. */
    public double getMealsServed()  { return luxuryCounter.getMealsServed(); }

    /** ...and what the households paid for them. */
    public double getMealSpend()    { return luxuryCounter.getMealSpend(); }

    /** ...at this price a meal, struck against the queue at the door. */
    public double getMealPrice()    { return luxuryCounter.getMealPrice(); }

    /** ...against this many meals they came for. */
    public double getMealsWanted()  { return luxuryCounter.getMealsWanted(); }

    /** Pieces the households bought over a counter this month. */
    public double getLuxuriesSold()  { return luxuryCounter.getLuxuriesSold(); }

    /** ...what they paid for them... */
    public double getLuxurySpend()   { return luxuryCounter.getLuxurySpend(); }

    /** ...what one went for... */
    public double getLuxuryPrice()   { return luxuryCounter.getLuxuryPrice(); }

    /** ...and how many they came for, which in a city short of shops is more. */
    public double getLuxuryWanted()  { return luxuryCounter.getLuxuryWanted(); }

    /* =====================================================================
       THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE
       The bank's capital sold as shares and put back by the treasury, the
       dividends, the exchange's month, the bank re-read off the city, the
       treasury's foreign-currency dealing, and the land re-read for the
       investor. These sat under the luxury counter's banner until
       2026-09-18, when the counter moved out and they were left there.
       ===================================================================== */

    /**
     * Sells the bank's paid-in capital as shares.
     *
     * The households first, out of their savings, the world for the rest -
     * "specially the bank, they need equity to avoid rough start". A founding
     * branch is bought on prospects; a later one on the bank's record, and a
     * bank the world will not fund opens its branch under-capitalised, which
     * is what a bank nobody will fund is.
     *
     * ...AND SINCE 0.7.14 WHAT THE BANK SELLS TO REPAY THE CITY: the
     * preferred at its third anniversary, and the warrants after it
     * (Bank.redeemDuePreferred(), repurchaseWarrants()), whatever its spare
     * capital does not cover. The city is not among the subscribers, so the
     * shares it holds are diluted by it.
     *
     * @return what was raised, home and abroad: the capital put in
     */
    private double capitaliseBank(double wanted) {
        if (wanted <= 0) return 0;
        double before = equity.getRaisedHomeThisMonth(Equity.BANK);
        double beforeAbroad = equity.getRaisedAbroadThisMonth(Equity.BANK);
        equity.offer(Equity.BANK, wanted, Math.max(0, bank.equity()),
                householdBalance, DebtManager.WORLD_BASE_RATE,
                exchange.hasTraded(Equity.BANK) ? exchange.price(Equity.BANK) : 0);
        double home = equity.getRaisedHomeThisMonth(Equity.BANK) - before;
        double abroad = equity.getRaisedAbroadThisMonth(Equity.BANK) - beforeAbroad;
        bank.injectCapital(home, abroad);
        return home + abroad;
    }

    /**
     * What the bank's profit is taxed at: a Commercial Bank is a commercial
     * building, so retail's rate. One place, because the tax charged at the
     * top of the month, the dividend paid on what it leaves and the income
     * the register records have to be read at the same rate.
     */
    private double bankProfitTaxRate() {
        return economyManager.getTaxPolicy().effectiveProfitRate(getSectors().retail());
    }

    /**
     * The bank's profit tax under another policy (0.7.36, the Policy spec's
     * M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s
     * rule - so NEXT month's bill, the tax being in arrears. What the Policy
     * tab's Profit page previews the bank's line with; it has no dial of its
     * own (D12). Pure.
     */
    public double bankTaxUnder(TaxPolicy p) {
        return bank.taxAt(p.effectiveProfitRate(getSectors().retail()));
    }

    /**
     * Each household cell's own bonds, by the cell's name (0.7.12 round 2).
     * A round-1 save held the households' bonds as one pool with a claim per
     * cell (the cells' Household.bonds, in the cell arrays): the pool is
     * handed to the cells by those claims. Then the market's households' face
     * is checked against the cells' (BondMarket.recountHouseholds()). Twice
     * on the load path: before the rebuild prices anything off them, and
     * after it has re-counted the cells.
     */
    private void restoreCellBonds(DataSave loaded) {
        if (loaded.getHouseholdBondsByCell() != null) {
            householdBalance.restoreBondsByCell(loaded.getHouseholdBondsByCell());
        } else {
            householdBalance.claimPooledBonds(bondMarket.householdsFaceById());
        }
        bondMarket.recountHouseholds();
    }

    /**
     * What a company's owners are due this month off its books, before its
     * till is asked: payDividends()'s figure, which the clearing reads ahead
     * of it (0.7.12 round 6) - the owners are paid out of the till after the
     * markets clear and before the month's purchases are billed, so what a
     * sector can pay for leaves it aside (EconomyManager.purchaseBudget()).
     * Zero for the bank and for a name that is not a company.
     */
    public double dividendDueFor(String sector) {
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c != Equity.BANK && Equity.COMPANIES[c].equals(sector)) return dividendDue(c);
        }
        return 0;
    }

    /** One company's (not the bank's): see payDividends(). */
    private double dividendDue(int c) {
        String sector = Equity.COMPANIES[c];
        SectorBooks.SectorMonth m = sectorBooks.get(sector);
        if (m == null) return 0;
        // After the principal that fell due that month (0.7.11,
        // round 2; Equity.dividendDue()).
        // ...its bonds' principal among it (0.7.12).
        /*
         * ...NET OF WHAT REFINANCED IT (0.7.12, round 2): free cash
         * flow to equity is net income less NET repayment - the
         * principal repaid less the new borrowing that rolled it.
         * 0.7.11 took the gross principal, so a bullet the desk simply
         * rolled cut that month's dividend by the whole of it, $33B a
         * run across the sectors (the brief's error, Jerus's fix). The
         * borrowing that rolled it is the shortfall desk's that month,
         * loans and bonds, never more than what fell due; a project's
         * loan buys a building and refinances nothing.
         */
        double repaid = Math.max(0, m.repaid()) + Math.max(0, m.bondsRepaid());
        double rolled = Math.min(repaid, economyManager.getBusinessDebtManager().getShortfallLentThisMonth(sector));
        return equity.dividendDue(c, m.netIncome(), repaid - rolled);
    }

    /**
     * What a sector can pay for as the markets clear this month (0.7.12
     * round 6): EconomyManager.purchaseBudget(), with the dividend the month
     * will pay its owners first. See Sector, BUY ONLY WHAT IT CAN PAY FOR.
     */
    public double purchaseBudget(Sector s) {
        return economyManager.purchaseBudget(s, dividendDueFor(s.key()), month);
    }

    /**
     * Pays every company's owners on its last closed month.
     *
     * A fixed share of a positive net income - Jerus: "net income, not cash,
     * and only if it's positive" - from the till, and from what the company
     * holds abroad when the till is short. Nothing is borrowed for it. The
     * households' part lands in their savings this month; the world's leaves
     * on the income account.
     *
     * AFTER THE PRINCIPAL SINCE ROUND 2 OF 0.7.11 (Jerus: "Pay out after
     * principal"): the share is of the net income less the principal that
     * fell due in that month, the mortgages' principal parts and any bullet
     * that matured, and nothing when the principal is the larger
     * (Equity.dividendDue()). Round 1 found a landlord paying 40% of a
     * profit smaller than its mortgages' principal, emptying its till and
     * borrowing its amortization from the shortfall desk. NET of what the
     * shortfall desk lent that month to roll it since 0.7.12 round 2 - free
     * cash flow to equity (see the note in the loop).
     *
     * THE BANK PAYS BY ITS OWN RULE since 0.7.8 (Bank.dividendDue(), WHAT IT
     * DOES WITH ITS PROFIT): nothing under its capital target, a share of
     * its profit after tax inside its band, and the excess over the top of
     * it a twelfth a month - and NOT capped at the cash it holds. A bank's
     * dividend is bounded by its capital; one whose book is funded by
     * deposits pays from them like any bank, and until 0.7.8 the cap meant a
     * bank that had lent its reserves out paid nothing at all. It was
     * Equity.PAYOUT of its profit after tax (before tax until 0.7.7).
     */
    private void payDividends() {
        // A company with a book and no owners is listed: the founders' shares.
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double book = c == Equity.BANK ? bank.equity()
                    : sectorBooks.get(Equity.COMPANIES[c]).equity();
            equity.listIfUnlisted(c, book, householdBalance);
        }
        // Whoever left this month took their shares with them.
        equity.followEmigrants(householdBalance);
        /*
         * THE CITY'S PREFERRED IS PAID FIRST (0.7.14): its dividend ranks
         * ahead of the common's, accrues when the bank cannot pay it, and
         * while any is unpaid the common gets nothing (Bank.payPreferredDividends(),
         * Bank.dividendDue()). What the bank pays goes to the city's fund.
         */
        double preferredPaid = bank.payPreferredDividends();
        fund.receivePreferredDividend(preferredPaid);
        // ...a row on the fund's record (0.7.39, FundLedger).
        if (preferredPaid > 0) fund.getLedger().preferred("dividend", preferredPaid, 0, month);
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double paid;
            if (c == Equity.BANK) {
                // On the profit AFTER the tax it will be charged, as every
                // sector's owners are paid on theirs (0.7.7; it was the
                // profit before tax) - and by the bank's own payout rule,
                // which pays nothing failed or under its minimum (0.7.8) -
                // and, while the city's consent binds, no more a share than
                // before it bought its preferred (0.7.14).
                paid = equity.getShares(c) > 0
                        ? bank.payOwners(bank.getProfitAfterTaxLastMonth(bankProfitTaxRate()), equity.getShares(c)) : 0;
            } else {
                String sector = Equity.COMPANIES[c];
                double due = dividendDue(c);
                if (due <= 0) continue;
                double till = economyManager.getSectorCash(sector);
                if (till < due) till += outward.recall(sector, due - till, economyManager);
                paid = Math.min(due, Math.max(0, till));
                if (paid > 0) {
                    economyManager.setSectorCash(sector, till - paid);
                    economyManager.recordDividendPaid(sector, paid);
                }
            }
            if (paid > 0) {
                double deskBefore = equity.getDividendDeskThisMonth(c);
                double cityBefore = equity.getDividendCityThisMonth(c);
                double cityHeld = equity.getCityShares(c);
                double rescueShare = cityHeld > 0 ? equity.getCityRescueShares(c) / cityHeld : 0;
                equity.payDividend(c, paid, householdBalance);
                // The desk's inventory is paid like any holder: into the bank.
                bank.receiveDividend(equity.getDividendDeskThisMonth(c) - deskBefore);
                // ...and the city's fund on what it holds (0.7.14), its books apart.
                fund.receiveDividend(equity.getDividendCityThisMonth(c) - cityBefore, rescueShare);
                // ...each book's lot's income (0.7.39).
                fund.getLedger().dividend(Equity.COMPANIES[c], equity.getDividendCityThisMonth(c) - cityBefore,
                        rescueShare, month);
                // ...and the register keeps what was paid: the yield every
                // participant on the exchange values the share on (0.7.12
                // round 2; Equity.dividendPerShareAnnual()).
                equity.noteDividendPaid(c, paid);
            }
        }
        equity.closeDividendMonth();
    }

    /**
     * The exchange's month: the desk quotes, the leavers, the world, the
     * households and the companies trade, and the bank carries what is left
     * at the closing mark. After the dividends, so the yields are this
     * month's, and before the sectors move their money abroad, so a buyback
     * is paid from the till before the till is emptied.
     */
    private void tradeShares() {
        double[] book = new double[Equity.COMPANIES.length];
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            book[c] = c == Equity.BANK ? bank.equity()
                    : sectorBooks.get(Equity.COMPANIES[c]).equity();
        }
        exchange.takeMonth(equity, householdBalance, bank, exchangeCompanies, book,
                DebtManager.WORLD_BASE_RATE, bank.depositRate(), month, fundBondsValue());
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double k = exchange.getSplit(c);
            if (k > 1) GameLog.note(String.format("%s split its shares %,.0f for one.", Equity.COMPANIES[c], k));
            else if (k > 0) GameLog.note(String.format("%s consolidated its shares one for %,.0f.", Equity.COMPANIES[c], 1 / k));
            // ...and the city's preferred's warrants and dividend cap follow the bank's shares (0.7.14).
            if (c == Equity.BANK && k > 0) bank.splitShares(k);
        }
    }

    /**
     * What the exchange reads of a company that is not on the register: its
     * till, its sheet and its costs. One object, wired at the founding and on
     * the load path (Exchange.attach()), because a company's buyback bid rests
     * on the book past the step it was posted at.
     */
    private final Exchange.Companies exchangeCompanies = new Exchange.Companies() {
            @Override public double cashAvailable(int company, double wanted) {
                String sector = Equity.COMPANIES[company];
                double till = economyManager.getSectorCash(sector);
                if (till < wanted) till += outward.recall(sector, wanted - till, economyManager);
                return till;
            }
            @Override public double till(int company) {
                return Math.max(0, economyManager.getSectorCash(Equity.COMPANIES[company]));
            }
            @Override public void payBuyback(int company, double cash) {
                String sector = Equity.COMPANIES[company];
                economyManager.setSectorCash(sector, economyManager.getSectorCash(sector) - cash);
                economyManager.recordSharesBoughtBack(sector, cash);
            }
            @Override public double assets(int company) {
                return sectorBooks.get(Equity.COMPANIES[company]).totalAssets();
            }
            @Override public double equity(int company) {
                return sectorBooks.get(Equity.COMPANIES[company]).equity();
            }
            @Override public double monthlyOperatingCost(int company) {
                return sectorBooks.get(Equity.COMPANIES[company]).operatingCost();
            }
            // ...and its debt service: operating cost stops above the
            // interest line, so the interest and the principal repaid, both
            // off the same closed month (0.7.11, round 2).
            @Override public double monthlyDebtService(int company) {
                SectorBooks.SectorMonth m = sectorBooks.get(Equity.COMPANIES[company]);
                return Math.max(0, m.interest()) + Math.max(0, m.repaid()) + Math.max(0, m.bondsRepaid());
            }
            // Whether the bank buys its own shares back or issues them is its
            // capital policy since 0.7.8, which the exchange reads off the bank
            // (Bank.buysBackOwnShares(), issuesOwnShares()); it was "flush" here - twice
            // the minimum, nothing owed a regulator, a record neither bad nor new.
        };

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
        for (String s : Sectors.KEYS) {
            sectorCash += Math.max(0, economyManager.getSectorCash(s));
        }
        bank.refresh(
                buildingManager.countByName("Commercial Bank"),
                householdBalance.totalSavings(),
                sectorCash,
                economyManager.getBusinessDebtManager().getAllPrincipal(),
                // The city's DOMESTIC paper, since 0.7.0: the bank holds what it
                // bought. This was getAllPrincipal(), dollar bonds included -
                // paper sold abroad that the bank never paid for, carried on
                // its book as an asset and weighed against its capital.
                // And since 0.7.1 only ITS OWN SHARE of it, of the paper it has
                // paid for: the households and the central bank hold the rest
                // (Debt, WHO HOLDS IT), and paper sold between the presses is
                // on nobody's book until the settle (DebtManager.bankBook()).
                debtManager.bankBook(),
                householdBalance.bookOwed());
        // ...less the discount on it the bank has not yet earned (0.7.1),
        // re-derived with the book from the paper's own remainders.
        bank.setUnearnedDiscount(debtManager.bankUnearnedDiscount());

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
        double businessWeighted = 0, mortgageFace = 0, mortgageWeighted = 0;
        // ...and, off the same loans, what each sector owes it outside its
        // insured mortgages, and its interim financing apart (0.7.13): the
        // Balance sheet page's detail, in Sectors.KEYS order.
        double[] loansBySector = new double[Sectors.KEYS.length];
        double[] interimBySector = new double[Sectors.KEYS.length];
        java.util.List<String> keys = java.util.Arrays.asList(Sectors.KEYS);
        for (BusinessDebt loan : economyManager.getBusinessDebtManager().getLoans()) {
            // An insured mortgage weighs RISK_INSURED_MORTGAGE (0.7.11), and
            // the weight table shows it as its own row.
            if (loan instanceof Mortgage m && m.isInsured()) {
                double w = m.getOutstandingPrincipal()
                        * Bank.RISK_INSURED_MORTGAGE * Bank.maturityWeight(m.getRemainingMonths());
                businessWeighted += w;
                mortgageFace += m.getOutstandingPrincipal();
                mortgageWeighted += w;
                continue;
            }
            businessWeighted += loan.getOutstandingPrincipal()
                    * Bank.RISK_BUSINESS * Bank.maturityWeight(loan.getRemainingMonths());
            int at = keys.indexOf(loan.getSector());
            if (at >= 0) {
                if (loan instanceof InterimLoan) interimBySector[at] += loan.getOutstandingPrincipal();
                else loansBySector[at] += loan.getOutstandingPrincipal();
            }
        }
        double cityWeighted = 0;
        for (Debt paper : debtManager.getDebt()) {
            if (paper.isForeign()) continue;   // held abroad - see the refresh above
            if (paper.getSettleDue() > 0) continue;   // not paid for yet - see bankBook()
            cityWeighted += paper.bankPrincipal()   // its own share (0.7.1)
                    * Bank.RISK_CITY * Bank.maturityWeight(paper.getRemainingMonths());
        }
        bank.setWeightedBook(businessWeighted, cityWeighted,
                householdBalance.bookOwed() * Bank.RISK_HOUSEHOLD);
        bank.setMortgageBook(mortgageFace, mortgageWeighted);
        bank.setSectorLoans(loansBySector, interimBySector);
        // ...and the businesses' bonds it holds, and the book's concentration (0.7.12).
        strikeBankBonds();

        // ...and what foreigners have borrowed to take abroad, which is the one
        // book with no debt object behind it. The stock in CapitalFlows is the
        // truth and this mirrors it, so a reload rebuilds it like the rest.
        bank.setCarryBook(hotMoney.getCarryStock());
    }

    /**
     * THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the
     * market and the lender: the bonds at what they cost it, weighed as loans
     * to their issuers for the months left (RISK_BUSINESS,
     * Bank.maturityWeight()); and every sector's exposure - what it owes the
     * bank that nobody insures and its bonds the bank holds - at its default
     * rate and the loss given default of what the bank holds of it (Bank,
     * THE BANK PRICES CONCENTRATION). At every refresh, after the market's
     * step - the last thing in the month to move the bank's bonds, so a saved
     * city reloads onto the book it closed on - and at the top of the month,
     * before the capital rule and the prices read it.
     */
    private void strikeBankBonds() {
        double cost = 0, weighted = 0, face = 0;
        for (CorporateBond b : bondMarket.getBonds()) {
            if (!(b.bank() > 0)) continue;
            cost += b.bankCost();
            face += b.bank();
            weighted += b.bankCost() * Bank.RISK_BUSINESS * Bank.maturityWeight(b.remainingMonths(month));
        }
        bank.setBondBook(cost, weighted, face);
        bank.setConcentration(bankExposures());
    }

    /** Every sector's exposure as the bank's concentration reads it. */
    private java.util.Map<String, Bank.Exposure> bankExposures() {
        BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        java.util.Map<String, Bank.Exposure> out = new java.util.LinkedHashMap<>();
        for (String s : credit.sectors()) {
            double loans = Math.max(0, credit.getUninsuredPrincipal(s));
            double bonds = bondMarket.bankCost(s);
            // Each class at its own loss given default (0.7.12, round 2:
            // BusinessDebtManager, RECOVERIES BY INSTRUMENT).
            double lgdLoans = BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT;
            double lgdBonds = BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT;
            double amount = loans + bonds;
            double lgd = amount > 0 ? (loans * lgdLoans + bonds * lgdBonds) / amount : lgdLoans;
            // Owing nothing, it has no leverage to read - the curve would read
            // an empty balance sheet as a certain default and its first loan
            // as needing no capital at all - so its first dollar is at the
            // floor, the rate a sound loan is priced for; the loan's own
            // spread carries the deal's risk.
            double pd = credit.quarterPrincipal(s) > 0
                    ? Math.max(BondMarket.DEFAULT_RATE_FLOOR, BusinessDebtManager.defaultProbability(
                            BusinessDebtManager.pricingLeverage(credit.quarterPrincipal(s), credit.quarterAssets(s))))
                    : BondMarket.DEFAULT_RATE_FLOOR;
            out.put(s, new Bank.Exposure(amount, pd, lgd));
        }
        return out;
    }

    /** What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). */
    private java.util.Map<String, Double> concentrationCharges() {
        java.util.Map<String, Double> out = new java.util.LinkedHashMap<>();
        double policy = debtManager.getPolicyRate();
        for (String s : economyManager.getBusinessDebtManager().sectors()) {
            out.put(s, bank.concentrationCharge(policy, Bank.PRIME_TERM_MONTHS, s));
        }
        return out;
    }

    /**
     * THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's
     * allowance struck from its borrowers as they stand at the month's end,
     * and the month's write-offs drawn against what each book held - see
     * Bank, THE ALLOWANCE. Once a month, after the refresh that has every
     * loan and write-off of the month on the books; NOT on the load path,
     * which restores the allowance the save struck (Bank.restoreAllowance()).
     */
    private void provideForLosses() {
        /*
         * THE MONTH'S READING, filed first (0.7.8, round 3): what each sector
         * owes and owns as the bank reads it at the month's end, the latest of
         * the quarter its allowance and its next loan's price are struck on -
         * see BusinessDebtManager, THE BANK READS A BORROWER FROM ITS LAST
         * QUARTER. Every month, bank or no bank, so a first branch opens with
         * a quarter to read.
         */
        BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        for (java.util.Map.Entry<String, double[]> e : sectorPositions().entrySet()) {
            credit.recordStatement(e.getKey(), e.getValue()[0], e.getValue()[1]);
        }
        bank.provide(bankReadings(), householdBalance.lossAllowance(),
                householdBalance.debtInTrouble());
    }

    /**
     * What the bank provides on, per sector (0.7.8): {what the sector owed
     * over its last quarter, what it owned over it, what it owes now} - the
     * quarter's leverage, the curve its allowance and stage are read at, and
     * the debt the loss is struck on (Bank.sectorAllowance(owed, principal,
     * assets)). WHAT IT OWES THAT NOBODY INSURES, since 0.7.11: an insured
     * mortgage is the city's loss if it goes, not the bank's, so it carries
     * no allowance in either stage - while the leverage the curve reads is
     * still the borrower's whole debt, because its firms default on all of it.
     */
    private java.util.Map<String, double[]> bankReadings() {
        BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        java.util.Map<String, double[]> out = new java.util.LinkedHashMap<>();
        for (String s : credit.sectors()) {
            /*
             * ...AND THE SECTOR'S BONDS IT HOLDS (0.7.12), at what they cost
             * it, beside what it has lent - the loans at a loan's loss given
             * default and the bonds at a bond's, the same a bondholder values
             * the bond on (BusinessDebtManager, RECOVERIES BY INSTRUMENT,
             * since round 2).
             */
            out.put(s, new double[]{ credit.quarterPrincipal(s), credit.quarterAssets(s), credit.getUninsuredPrincipal(s),
                    BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT, bondMarket.bankCost(s),
                    BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT });
        }
        return out;
    }

    /**
     * Each sector's name to {what it owes, its assets} - the month-end
     * reading the bank files (provideForLosses()): the figures
     * the restructure rule judged it on this month
     * (BusinessDebtManager.getAssets(), struck at the insolvency check), with
     * what it has borrowed since to build on BOTH sides, the way the
     * investment desk counts a project (canFundProject(): the building is the
     * collateral). Read against the check's assets alone, a sector that had
     * just borrowed for a plant read as leveraged by the whole loan and was
     * put in stage 2 by its own new building.
     */
    private java.util.Map<String, double[]> sectorPositions() {
        BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        java.util.Map<String, double[]> out = new java.util.LinkedHashMap<>();
        for (String s : credit.sectors()) {
            double owed = credit.getPrincipal(s);
            double since = Math.max(0, owed - credit.getPrincipalJudged(s));
            out.put(s, new double[]{ owed, credit.getAssets(s) + since });
        }
        return out;
    }

    /* =====================================================================
       THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14)

       Jerus's answers, 2026-09-27 (TreasuryFund has them whole): "City takes
       the shares", "Treasury setting, auto", "Preferred shares", "Rule plus
       your hand", "New issues only", "Central bank advances it", the dial
       "default 0, max 300%" of "the year's surplus", "Home only".

       A FAILED BANK IS RESOLVED FOR ITS SHARES (resolveBank()): the month it
       fails when the treasury's setting is automatic, at the Bank tab's
       button otherwise. The city's preferred and warrants go with the hole;
       the old owners are wiped out; the city pays the hole and the capital to
       reopen (Bank.recapitalisationNeeded() - the shortfall plus
       resolutionExitEquity(), the exit level it always had) as a promise,
       the central bank advancing what the treasury lacks at the next settle;
       and every share is the city's rescue book. The bank reopens the same
       month. Its own new shares go to others, so the city's stake falls as
       it rebuilds.

       A STANDING BANK UNDER ITS MINIMUM ASKS FOR PREFERRED
       (considerPreferredOffer()): an offer the player answers from the
       inbox, TARP's terms (Bank, THE CITY'S CAPITAL). Accepted, the treasury
       buys it, a purchase; a treasury short of it is offered the funding
       page's local paper first. After either answer the bank asks again
       only after a quarter (TreasuryFund.OFFER_AGAIN_MONTHS), while it is
       still under. Each block is repaid whole at its third anniversary, from
       the bank's capital over its target and then new shares sold to the
       public, which dilute the city's (settleThePreferred(); Jerus: "Sell
       new shares to repay"). During a time skip the offer waits:
       the skip has never stopped for a notice (only the running clock pauses
       on one, UserInterface.stopIfSomethingHappened()), so the skip's months
       pass without the city's capital, as declined months would.

       THE FUND'S DIAL (fundYearEnd()), ITS TRANSFER (settleTreasury()), ITS
       RULE (Exchange.postFund(), BondMarket.postFund()) AND THE HAND
       (fundBuyShares() and the rest, fundPayIn(), fundDrawOut()).
       ===================================================================== */

    /** The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. */
    private double ownersWipedAbroadThisMonth;

    /** What the audit declares as the world's shares wiped out since its last strike (0.7.14). */
    public double getOwnersWipedAbroadThisMonth() { return ownersWipedAbroadThisMonth; }

    /** What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. */
    public double bankRecapitalisationNeeded() {
        return bank.recapitalisationNeeded();
    }

    /** What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. */
    public double bankResolutionAdvance() {
        return Math.max(0, bank.recapitalisationNeeded() - Math.max(0, cash));
    }

    /** True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. */
    public boolean canResolveBank() {
        return bank.getBranches() > 0 && bank.isInsolvent();
    }

    /** When the treasury's setting is automatic and the bank has failed, the city resolves it now. */
    private void resolveIfAutomatic() {
        if (fund.getRescueMode() == TreasuryFund.RescueMode.AUTOMATIC && canResolveBank()) resolveBank();
    }

    /**
     * THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button,
     * and the automatic setting's month. The whole rescue - nothing to choose.
     *
     *   1. The city's preferred and its warrants, if any, are cancelled: the
     *      hole took them (Bank.cancelPreferred()).
     *   2. The old owners are wiped out: every cell's shares, the world's and
     *      the fund's own market book pass to the city for nothing
     *      (HouseholdBalance.surrenderShares(), Equity.takeAllForCity()), and
     *      their paid-in capital is written off against the losses
     *      (Bank.wipeOwners()). The world's, at its last price, is a
     *      valuation across the border (getOwnersWipedAbroadThisMonth()); the
     *      households', inside the country and outside the pools, has no
     *      line.
     *   3. The city pays the hole and the capital to reopen, as a promise
     *      (TreasuryLine.BANK_RESOLUTION): from its cash first, and what it
     *      lacks the central bank advances at the next settle, past its
     *      ceiling if it must.
     *   4. The bank reopens (Bank.takeResolutionCapital()), and the shares
     *      are the fund's rescue book, at what the city paid.
     *
     * @return what the city paid; nothing when there was nothing to resolve
     */
    public double resolveBank() {
        if (!canResolveBank()) return 0;
        double price = exchange.price(Equity.BANK);
        double[] cancelled = bank.cancelPreferred();
        bank.wipeOwners();
        double households = householdBalance.surrenderShares(Equity.BANK);
        double shortfall = Math.max(0, -bank.equity());
        double exit = bank.resolutionExitEquity();
        double amount = shortfall + exit;
        // The fund's market-book shares pass for nothing: their cost, a realized loss (0.7.39, FundLedger).
        double marketCostLost = fund.getLedger().rescueTakes(Equity.COMPANIES[Equity.BANK], month);
        double[] before = equity.takeAllForCity(Equity.BANK, amount / equity.foundingPrice());
        double cashBefore = cash;
        treasuryPays(TreasuryLine.BANK_RESOLUTION, amount);
        double fromCash = Math.min(amount, Math.max(0, cashBefore));
        treasuryJournal.record("Resolved the bank for its shares", -amount);
        bank.takeResolutionCapital(amount);
        ownersWipedAbroadThisMonth += before[0] * price;

        TreasuryFund.Resolution r = TreasuryFund.newResolution(month);
        r.paid = amount;
        r.fromCash = fromCash;
        r.advanced = amount - fromCash;
        r.shortfall = shortfall;
        r.exitCapital = exit;
        r.shares = equity.getShares(Equity.BANK);
        r.householdsShares = households;
        r.householdsValue = households * price;
        r.worldShares = before[0];
        r.worldValue = before[0] * price;
        r.fundShares = before[1];
        r.fundValue = before[1] * price;
        r.preferredCancelled = cancelled[0];
        r.warrantsCancelled = cancelled[1];
        fund.noteResolution(r);
        fund.getLedger().rescued(Equity.COMPANIES[Equity.BANK], r, marketCostLost, month);
        if (cancelled[0] > 0) fund.getLedger().preferred("cancelled", 0, -cancelled[0], month);
        if (cancelled[1] > 0) fund.getLedger().warrants("cancelled", 0, cancelled[1], month);

        // A press of the Bank tab's button, or the automatic setting's month - the player's setting either way.
        decisions.record(DecisionLog.BANK, "Bank rescued for its shares: " + DecisionLog.money(amount)
                + (fund.getRescueMode() == TreasuryFund.RescueMode.AUTOMATIC ? " (automatic)" : ""));

        String here = getCurrency().qualifiedSymbol();
        GameLog.note(String.format("The bank failed. The city took all its shares; the old owners lost everything"
                        + " (%s%,.0fk at the last price). The city put in %s%,.0fk: %s%,.0fk from its cash and"
                        + " %s%,.0fk advanced by the central bank.",
                here, r.ownersLost(), here, amount, here, fromCash, here, r.advanced));
        return amount;
    }

    /** The city's last resolution, or null if it has never resolved the bank. */
    public TreasuryFund.Resolution getLastResolution() {
        java.util.List<TreasuryFund.Resolution> all = fund.getResolutions();
        return all.isEmpty() ? null : all.get(all.size() - 1);
    }

    /** The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. */
    public double cityStakeInBank() { return equity.cityShare(Equity.BANK); }

    /* ------------------------------ the preferred offer ------------------------------ */

    /**
     * A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A
     * QUARTER: at the bottom of the month, once the bank's month is final.
     * An offer still waiting lapses if the bank is back over its minimum on
     * its own. Moves no money.
     */
    private void considerPreferredOffer() {
        if (fund.isOfferPending()) {
            if (!bank.wantsPreferred()) fund.noteLapsed();
            return;
        }
        if (bank.wantsPreferred() && fund.mayOffer(month) && bank.preferredOfferSize() > 0) {
            fund.noteOffered(month);
            String here = getCurrency().qualifiedSymbol();
            GameLog.note(String.format("The bank is under its minimum capital and asks the city to buy %s%,.0fk"
                    + " of preferred shares.", here, bank.preferredOfferSize()));
        }
    }

    /** True while the bank's offer waits for the player's answer. */
    public boolean isPreferredOfferPending() { return fund.isOfferPending(); }

    /** What the bank asks for now: Bank.preferredOfferSize(). */
    public double preferredOfferSize() { return bank.preferredOfferSize(); }

    /** The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". */
    public double preferredOfferWarrantValue() { return Bank.WARRANT_SHARE * bank.preferredOfferSize(); }

    /** ...and their strike: a share at last month's price, the exchange's. */
    public double preferredOfferStrike() { return exchange.price(Equity.BANK); }

    /** What the treasury is short of the offer: what the funding page raises first. */
    public double preferredOfferShortBy() { return Math.max(0, bank.preferredOfferSize() - cash); }

    /**
     * THE CITY ACCEPTS: the treasury buys the preferred, a purchase
     * (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. Not when the
     * treasury is short of it - the page raises that on the funding page's
     * paper first - and not when there is nothing pending.
     *
     * @return true if it was bought
     */
    public boolean acceptPreferredOffer() {
        if (!fund.isOfferPending()) return false;
        double size = bank.preferredOfferSize();
        if (!(size > 0)) { fund.noteLapsed(); return false; }
        if (cash < size) return false;
        double paid = treasuryPays(TreasuryLine.BANK_CAPITAL, size);
        if (paid < size) { cash += paid; return false; }
        treasuryJournal.record("Bought the bank's preferred shares", -size);
        double price = exchange.price(Equity.BANK);
        double shares = equity.getShares(Equity.BANK);
        double capPerShare = shares > 0 ? equity.getDividendsPaidOverYear(Equity.BANK) / shares / 12 : 0;
        double warrants = bank.issuePreferred(size, price, capPerShare);
        fund.noteAccepted(month, size);
        fund.getLedger().preferred("bought", size, 0, month);
        decisions.record(DecisionLog.BANK, "Bought " + DecisionLog.money(size) + " of the bank's preferred shares");
        String here = getCurrency().qualifiedSymbol();
        GameLog.note(String.format("The city bought %s%,.0fk of the bank's preferred shares, %.0f%% a year for five"
                        + " years then %.0f%%, with warrants on %,.0f of its shares at %s%,.3fk.",
                here, size, Bank.PREFERRED_RATE * 100, Bank.PREFERRED_STEP_RATE * 100, warrants, here, price));
        return true;
    }

    /** THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. */
    public void declinePreferredOffer() {
        if (!fund.isOfferPending()) return;
        fund.noteDeclined(month);
        decisions.record(DecisionLog.BANK, "Declined the bank's preferred shares");
        GameLog.note("The city declined the bank's preferred shares; it will ask again in "
                + TreasuryFund.OFFER_AGAIN_MONTHS + " months if it is still under its minimum.");
    }

    /**
     * THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus:
     * "Sell new shares to repay"): every block at its third anniversary
     * redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from
     * the bank's capital over its target and, for the rest, an offering of
     * new common to the public (capitaliseBank()); once no preferred is left,
     * the city's warrants bought back at their fair value the same way
     * (Bank.repurchaseWarrants()); and warrants at their expiry, still out,
     * exercised into new shares for the rescue book. Everything the bank pays
     * goes to the fund. Package-private for FundCheck, which calls it between
     * presses to size a repayment exactly.
     */
    void settleThePreferred() {
        if (bank.getPreferred().isEmpty()) return;
        double price = exchange.price(Equity.BANK);
        java.util.function.DoubleUnaryOperator offering = this::capitaliseBank;
        double[] redeemed = bank.redeemDuePreferred(offering);
        fund.receivePreferredDividend(redeemed[0]);
        fund.receiveRedemption(redeemed[1], 0);
        double warrants = bank.repurchaseWarrants(exchange.price(Equity.BANK), bankVolatility(),
                debtManager.getPolicyRate(), offering);
        fund.receiveRedemption(0, warrants);
        // ...rows on the fund's record (0.7.39): the warrants cost nothing, so what they bring is realized whole.
        if (redeemed[0] > 0) fund.getLedger().preferred("dividend", redeemed[0], 0, month);
        if (redeemed[1] > 0) fund.getLedger().preferred("redeemed", redeemed[1], 0, month);
        if (warrants > 0) fund.getLedger().warrants("bought back", warrants, 0, month);
        if (redeemed[1] > 0 || warrants > 0) {
            String here = getCurrency().qualifiedSymbol();
            GameLog.note(String.format("The bank repaid the city: %s%,.0fk of preferred at par, %s%,.0fk of its unpaid"
                            + " dividends and %s%,.0fk for the warrants; %s%,.0fk of it raised by selling new shares.",
                    here, redeemed[1], here, redeemed[0], here, warrants, here, bank.getRepaymentRaisedThisMonth()));
        }
        double shares = bank.exerciseExpiredWarrants(price);
        if (shares > 0) {
            equity.issueToCityRescue(Equity.BANK, shares);
            fund.noteWarrantShares(shares);
            fund.getLedger().warrants("exercised", 0, shares, month);
            GameLog.note(String.format("The city's warrants on the bank expired in the money: %,.0f new shares"
                    + " for the city.", shares));
        }
    }

    /** The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. */
    public double bankVolatility() {
        return TreasuryFund.annualVolatility(historySave.aligned(HistorySave.priceKey(Equity.COMPANIES[Equity.BANK])));
    }

    /* ------------------------------ what the fund is worth ------------------------------ */

    /** Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). */
    public double fundSharesValue()       { return exchange.cityValue(equity); }
    /** ...its market book's alone. */
    public double fundMarketSharesValue() { return exchange.cityMarketValue(equity); }
    /** ...its rescue book's shares. */
    public double fundRescueSharesValue() {
        double t = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) t += equity.getCityRescueShares(c) * exchange.cityMark(c);
        return t;
    }
    /** Its bonds, at the market's valuation. */
    public double fundBondsValue()        { return bondMarket.valueHeld(CorporateBond::city, month); }
    /** Its preferred, at par. */
    public double fundPreferredValue()    { return bank.preferredOutstanding(); }
    /** Its warrants, at Black-Scholes, on the bank's share at the city's mark. */
    public double fundWarrantsValue() {
        return bank.warrantValue(exchange.cityMark(Equity.BANK), bankVolatility(), debtManager.getPolicyRate());
    }
    /** Its rescue book: the rescue shares, the preferred and the warrants. */
    public double fundRescueValue()       { return fundRescueSharesValue() + fundPreferredValue() + fundWarrantsValue(); }
    /** Everything it holds, both books, and its cash: what its transfer is struck on. */
    public double fundValue() {
        return fund.getCash() + fundSharesValue() + fundBondsValue() + fundPreferredValue() + fundWarrantsValue();
    }
    /** Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. */
    public double fundEquityShare() {
        double v = fundMarketSharesValue() + fundBondsValue() + Math.max(0, fund.getCash());
        return v > 0 ? fundMarketSharesValue() / v : 0;
    }
    /** What this month's transfer is on the fund as it stands, at the withdrawal in force: TreasuryFund.withdrawalOn(fundValue()) - transferOn() at the default. */
    public double fundTransferDue()       { return fund.withdrawalOn(fundValue()); }

    /** One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). */
    public double fundCompanyValue(int company) {
        return equity.getCityShares(company) * exchange.cityMark(company);
    }
    /** ...its rescue book's part. */
    public double fundCompanyRescueValue(int company) {
        return equity.getCityRescueShares(company) * exchange.cityMark(company);
    }
    /** The share of a company the city owns, 0-1, both books over the shares in issue. */
    public double fundCompanyShare(int company)  { return equity.cityShare(company); }
    /** ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. */
    public double fundCompanyMarketShare(int company) {
        double shares = equity.getShares(company);
        return shares > 0 ? equity.getCityMarketShares(company) / shares : 0;
    }
    /** One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). */
    public double fundBondValue(CorporateBond b) {
        return b == null || !(b.city() > 0) ? 0 : b.city() * bondMarket.modelPrice(b, month);
    }
    /** ...and one issuer's bonds in the fund, all of them. */
    public double fundBondsValueOf(String issuer) {
        double t = 0;
        for (CorporateBond b : bondMarket.getBonds(issuer)) t += fundBondValue(b);
        return t;
    }

    /* ------------------------------ the dial ------------------------------ */

    /** The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. */
    public double getFundDial()             { return fund.getDial(); }
    public void setFundDial(double dial) {
        double was = fund.getDial();
        fund.setDial(dial);
        if (DecisionLog.moved(was, fund.getDial())) {
            decisions.record(DecisionLog.FUND, "Fund dial to " + DecisionLog.pct(fund.getDial()) + " of the surplus");
        }
    }

    /** The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. */
    public double getFundWithdrawal()       { return fund.getWithdrawal(); }
    /** ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. */
    public void setFundWithdrawal(double share) {
        double was = fund.getWithdrawal();
        fund.setWithdrawal(share);
        if (DecisionLog.moved(was, fund.getWithdrawal())) {
            decisions.record(DecisionLog.FUND, "Fund's withdrawal to " + DecisionLog.pct2(fund.getWithdrawal())
                    + " a month (" + DecisionLog.pct(fund.getWithdrawal() * TreasuryFund.YEAR_MONTHS) + " a year)");
        }
    }

    /** The treasury's setting for a failed bank. */
    public TreasuryFund.RescueMode getRescueMode()          { return fund.getRescueMode(); }
    public void setRescueMode(TreasuryFund.RescueMode mode) {
        TreasuryFund.RescueMode was = fund.getRescueMode();
        fund.setRescueMode(mode);
        if (was != fund.getRescueMode()) {
            decisions.record(DecisionLog.BANK, fund.getRescueMode() == TreasuryFund.RescueMode.AUTOMATIC
                    ? "A failed bank to be resolved at once" : "A failed bank to wait for the button");
        }
    }

    /**
     * ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the
     * last Rollover.NETTING_MONTHS, as the history keeps them (revenue less
     * the surplus, month by month), a month's worth. The floor the fund's
     * pay-in never takes the treasury under - Canada's Department of Finance
     * holds liquidity for at least one month of net projected cash flows
     * (Debt Management Report 2017-18, Part 1), the rollover's own source -
     * so the fund is never what the central bank then advances.
     */
    public double monthOfSpending() {
        double[] revenue = historySave.aligned("revenue");
        double[] surplus = historySave.aligned("surplus");
        int n = Math.min(revenue.length, surplus.length);
        double sum = 0;
        int months = 0;
        for (int i = Math.max(0, n - Rollover.NETTING_MONTHS); i < n; i++) {
            double r = revenue[revenue.length - n + i], s = surplus[surplus.length - n + i];
            if (Double.isNaN(r) || Double.isNaN(s)) continue;
            sum += r - s;
            months++;
        }
        return months > 0 ? Math.max(0, sum / months) : 0;
    }

    /** The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. */
    public double surplusThisYearSoFar() {
        double[] surplus = historySave.aligned("surplus");
        int k = CityCalendar.monthOfYear(month);
        double sum = 0;
        for (int i = Math.max(0, surplus.length - k); i < surplus.length; i++) {
            if (!Double.isNaN(surplus[i])) sum += surplus[i];
        }
        return sum;
    }

    /**
     * WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this
     * calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing
     * once this year's pay-in is made, and nothing at a dial of 0.
     */
    public double fundReservation() {
        if (fund.getLastPayInMonth() == month) return 0;
        return TreasuryFund.reservedFor(fund.getDial(), surplusThisYearSoFar());
    }

    /**
     * THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first
     * press after December has closed, before the rollover runs.
     *
     * WHY A YEAR, AND ITS END. Jerus's unit is "the year's surplus", and "a
     * deficit year saves nothing": a monthly pay-in on each month's surplus
     * would save in the surplus months of a year in deficit. Appropriating a
     * year's surplus at its end is a real practice - Canada applies a
     * budgetary surplus to its debt when the fiscal year closes, and
     * Singapore's reserves take a government's surpluses at the end of its
     * term. (Norway's inflows are continuous because they follow the oil
     * revenue as it arrives, which is not what this dial is a share of.) The
     * year's surplus is the last Rollover.NETTING_MONTHS of the national
     * accounts' balance (surplusOverLastYear(), the rollover's), which at the
     * year end is the calendar year.
     *
     * THE FOUR RULES (TreasuryFund.payIn()): the dial's share first - before
     * the rollover nets, which through the year has left the dial's share of
     * the year so far alone (fundReservation()); the surplus used once, off
     * the rollover's ledger, where this pay-in is entered too
     * (Rollover.noteFundTook()); a deficit year saves nothing; past 100% the
     * extra from the treasury's cash, and never below monthOfSpending().
     */
    private void fundYearEnd() {
        if (!(fund.getDial() > 0)) return;
        if (CityCalendar.monthOfYear(month) != 12 || fund.getLastPayInMonth() == month) return;
        double year = surplusOverLastYear();
        double unused = year - rollover.usedInYear(month);
        double[] in = TreasuryFund.payIn(fund.getDial(), year, unused, cash, monthOfSpending());
        double total = in[0] + in[1];
        cash -= total;
        fund.notePayIn(month, year, in[0], in[1]);
        rollover.noteFundTook(month, in[0]);
        if (total > 0) treasuryJournal.record("Paid into the fund (the dial)", -total);
        fund.getLedger().flow(FundLedger.PAY_IN, total, false, month);
        String here = getCurrency().qualifiedSymbol();
        GameLog.note(year > 0
                ? String.format("THE FUND, year end: %.0f%% of the year's surplus of %s%,.0fk - %s%,.0fk from the surplus"
                        + " and %s%,.0fk from the treasury's cash - paid in.", fund.getDial() * 100, here, year,
                        here, in[0], here, in[1])
                : String.format("THE FUND, year end: the year ran a deficit of %s%,.0fk, and saves nothing.", here, -year));
    }

    /* ------------------------------ the hand ------------------------------ */

    /** The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. @return what moved */
    public double fundPayIn(double amount) {
        double moved = Math.max(0, Math.min(amount, cash));
        if (!(moved > 0)) return 0;
        cash -= moved;
        fund.notePaidInByHand(moved);
        fund.getLedger().flow(FundLedger.PAY_IN, moved, true, month);
        treasuryJournal.record("Paid into the fund", -moved);
        decisions.record(DecisionLog.FUND, "Paid " + DecisionLog.money(moved) + " into the fund");
        GameLog.note(String.format("The city paid %s%,.0fk into its fund.", getCurrency().qualifiedSymbol(), moved));
        return moved;
    }

    /** ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. @return what moved */
    public double fundDrawOut(double amount) {
        double moved = Math.max(0, Math.min(amount, fund.getCash()));
        if (!(moved > 0)) return 0;
        cash += moved;
        fund.noteDrawnOutByHand(moved);
        fund.getLedger().flow(FundLedger.DRAW_OUT, moved, true, month);
        treasuryJournal.record("Drawn from the fund", moved);
        decisions.record(DecisionLog.FUND, "Drew " + DecisionLog.money(moved) + " from the fund");
        GameLog.note(String.format("The city drew %s%,.0fk from its fund.", getCurrency().qualifiedSymbol(), moved));
        return moved;
    }

    /**
     * Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month.
     * (0.7.39: never more than the cash the hand's other buys leave - TreasuryFund.handReserve() - and a decision
     * that says it is an order, which the step may fill in part or not at all - the spec's B5.)
     */
    public void fundBuyShares(int company, double money) { fundBuyShares(company, money, 0); }

    /** ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). */
    public void fundBuyShares(int company, double money, double limit) {
        double spend = Math.min(money, fundCashFree());
        fund.queue(new TreasuryFund.HandOrder(false, company, -1, true, spend, month, limit));
        if (spend > 0) {
            decisions.record(DecisionLog.FUND, "Order: buy " + DecisionLog.money(spend) + " of "
                    + Equity.COMPANIES[company] + " shares" + atLimit(limit, false));
        }
    }

    /** Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. */
    public void fundSellShares(int company, double shares) { fundSellShares(company, shares, 0); }

    /** ...at a price a share the player names (0.7.39): 0 is fair value. */
    public void fundSellShares(int company, double shares, double limit) {
        double selling = Math.min(shares, equity.getCityShares(company));
        fund.queue(new TreasuryFund.HandOrder(false, company, -1, false, selling, month, limit));
        if (selling > 0) {
            decisions.record(DecisionLog.FUND, "Order: sell " + shareCount(selling) + " "
                    + Equity.COMPANIES[company] + " shares" + atLimit(limit, false));
        }
    }

    /** Buys a bond with this much of the fund's cash, at its value, at the next step. */
    public void fundBuyBond(int bondId, double money) { fundBuyBond(bondId, money, 0); }

    /** ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. */
    public void fundBuyBond(int bondId, double money, double limit) {
        double spend = Math.min(money, fundCashFree());
        fund.queue(new TreasuryFund.HandOrder(true, -1, bondId, true, spend, month, limit));
        if (spend > 0) decisions.record(DecisionLog.FUND, "Order: buy " + DecisionLog.money(spend) + " of bond #" + bondId
                + atLimit(limit, true));
    }

    /** Sells this much face of a bond the fund holds, at its value, at the next step. */
    public void fundSellBond(int bondId, double face) { fundSellBond(bondId, face, 0); }

    /** ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. */
    public void fundSellBond(int bondId, double face, double limit) {
        CorporateBond b = bondMarket.bond(bondId);
        double selling = b == null ? 0 : Math.min(face, b.city());
        fund.queue(new TreasuryFund.HandOrder(true, -1, bondId, false, selling, month, limit));
        if (selling > 0) decisions.record(DecisionLog.FUND, "Order: sell " + DecisionLog.money(selling) + " of bond #" + bondId
                + atLimit(limit, true));
    }

    /** The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). */
    public double fundCashFree() {
        return Math.max(0, fund.getCash() - fund.handReserve());
    }

    /** A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). */
    private static String shareCount(double n) {
        if (n >= 100) return Formats.INSTANCE.count(n);
        return String.format(java.util.Locale.ROOT, n >= 1 ? "%,.2f" : "%.4f", n);
    }

    /** A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. */
    private String atLimit(double limit, boolean bond) {
        if (!(limit > 0)) return "";
        return bond ? String.format(java.util.Locale.ROOT, " at %.2f per 100", limit * 100)
                : " at " + DecisionLog.money(limit);
    }

    /**
     * CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the
     * spec's D3): the order at this place in TreasuryFund.getHandOrders(). Its
     * cash is the rule's again. An order already on the book rests until the
     * step after, as every order does.
     *
     * @return true if there was one to cancel
     */
    public boolean fundCancelOrder(int i) {
        TreasuryFund.HandOrder o = fund.cancel(i);
        if (o == null) return false;
        String what = o.bond() ? "bond #" + o.bondId()
                : (o.company() >= 0 && o.company() < Equity.COMPANIES.length ? Equity.COMPANIES[o.company()] : "?") + " shares";
        String size = o.buy() || o.bond() ? DecisionLog.money(o.amount()) : shareCount(o.amount());
        decisions.record(DecisionLog.FUND, "Cancelled: " + (o.buy() ? "buy " : "sell ") + size + " of " + what);
        return true;
    }

    /* ------------------------------ an Insane founding ------------------------------ */

    /**
     * AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own
     * twenty-year dollar term loan, LongTermBond abroad, its coupon fixed at
     * Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.landBondUsd() -
     * STARTING_SQ_FT at the land market's opening dollar price. The proceeds
     * paid the land's sellers, so no cash arrives: booked on the debt and
     * the foreign accounts' stock of it at the founding rate (the rate
     * books no revaluation), and on no audited pool - the founding is before
     * month one's window opens, as the founders' vault is. Its market value
     * is whatever the world's curve makes of a 3% coupon
     * (DebtManager.marketValue()), not par. Saved as ordinary debt; serviced
     * as any dollar debt is - out of the vault if it holds dollars,
     * converted from cash otherwise.
     */
    private void foundTheLandBond() {
        double usd = Founding.landBondUsd();
        if (!(usd > 0)) return;
        debtManager.addLongTermBond(usd, Founding.INSANE_LAND_YEARS * 12, month, Founding.INSANE_LAND_COUPON, true);
        foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());
    }

    /*
     * AN INSANE CITY RUNS FROM DAY ONE (0.7.15). Jerus: "Play works from day
     * one. The central bank covers what the treasury must pay, which starts
     * with just the land bond's coupon; optional spending is refused." 0.7.14
     * stopped the play clock on an empty treasury with no revenue behind it,
     * and the time skip at any empty treasury; both stops are gone, the
     * skip's by his "Skip runs too" (simulateMonths()). What an Insane city
     * must pay - its promises (TreasuryLine): on day 0 the land bond's
     * dollar coupon, converted from cash (payForeignInterest()), and the
     * founding residents' pensions - takes its cash under nothing, and the
     * next settle advances the shortfall (settleTreasury()), past a ceiling
     * of no revenue if it must. Anything discretionary is refused: a
     * ceiling of nothing and no cash leave discretionaryRoom() nothing.
     * Borrowing is how it builds - the build screen's, the land office's and
     * the Finances tab's funding pages quote it at D$0 (dayZeroQuotes()).
     */

    /**
     * WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's
     * two offers - the Game.BUILD_BOND_YEARS bond and the
     * Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city
     * founded as given and not yet played, by the model's own quote
     * functions: no revenue and no output, so DebtManager.spreadFor()
     * charges the full spread on both measures. The Found a city screen's
     * "what it buys" for Insane read it until 0.7.20; nothing calls it since
     * (the playtest and FundCheck quote their day-0 offers themselves). A
     * scratch city, founded in a temporary folder and thrown away; nothing is
     * written.
     *
     * @return {the bond's quote, the note's}
     */
    public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded) {
        Game scratch = new Game(GameFiles.scratch("day-zero-quote"), founding);
        java.io.PrintStream out = System.out;
        try {
            System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
            scratch.run();
            return new DebtQuote[] {
                    scratch.quoteLongBondForCash(cashNeeded, BUILD_BOND_YEARS, BUILD_BOND_GRANULE),
                    scratch.quoteTBill(cashNeeded, BUILD_NOTE_MONTHS, BUILD_NOTE_GRANULE) };
        } finally {
            System.setOut(out);
        }
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
        treasuryPays(TreasuryLine.RESERVE_PURCHASES, spend);
        treasuryJournal.record("Bought reserves", -spend);
        foreign.buyReserves(spend);
        decisions.record(DecisionLog.CURRENCY, "Bought reserves for " + DecisionLog.money(spend));
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
        treasuryJournal.record("Sold reserves", sold);
        decisions.record(DecisionLog.CURRENCY, "Sold reserves for " + DecisionLog.money(sold));
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

        /*
         * Standing policy first: a sector the city has undertaken to protect is
         * brought up to break-even BEFORE the loss counter sees the month, which
         * is the whole point - six consecutive losses is what makes a sector
         * start selling its capacity, so a subsidy that arrives after the count
         * protects nothing.
         */
        for (Sector sector : getSectors().all()) {
            double net = sector.getNetIncome();
            double covered = paySubsidyIfOwed(sector, net);
            businessInvestment.recordSectorResult(sector.key(), net + covered);
        }

        /*
         * The spare-capacity rule, for every sector that has a measure for it
         * - the landlords against people housed, the shops against people
         * served, the mills against what the shops can serve, the builders
         * against their queue - and the distress rule for everyone, which is
         * the only rule a price-taking exporter has. Both pass NOMINAL
         * capacity, not this month's output: staffing decides how much a
         * firm PRODUCES, not how much plant it owns.
         */
        for (Sector sector : getSectors().all()) {
            /*
             * HELD MEANS HELD, IN BOTH DIRECTIONS (2026-09-17).
             *
             * holdSector() was honoured by the investment loop above and not by
             * this one, so a harness that took a sector out of the planner's
             * hands still had the planner DEMOLISHING its fixture. RailCheck
             * found it: it lays six extra spurs to ask what an over-built
             * railway can charge, and the distress rule below quietly scrapped
             * them - 618,701k of track in the build before this batch, 422,566k
             * after, and the assertion failed for the one reason it is not
             * allowed to, which is that a planner decided its subject.
             *
             * That class's own header says it: "An assertion whose subject is
             * decided by a planner is an assertion about the planner." It was
             * written about the investment loop and is just as true here.
             */
            if (businessInvestment.isHeld(sector.key())) continue;
            int orders = buildingManager.getUnderConstructionBySector(sector.key());
            int shed = 0;
            double[] measure = sector.retirementDemandAndCapacity(this);
            if (measure != null) {
                shed = retire(businessInvestment.planRetirement(
                        sector, measure[0], measure[1], orders),
                        sectorInvestor(sector.key()), false);
            }
            if (shed == 0) {
                retire(businessInvestment.planDistressRetirement(
                        sector, sector.getCash(), orders),
                        sectorInvestor(sector.key()), true);
            }
        }

        /*
         * ...AND THE BANK'S BRANCHES (0.7.19; 0.7.11's book test until then):
         * every branch past the first whose customers' fees this month do not
         * cover what a branch cost last month closes now, as many at once as
         * it takes, never the first (Bank.branchesToClose(), THE BRANCHES, BY
         * THEIR CUSTOMERS). Asked after the month's fees are taken
         * (updateHouseholdAccounts(), above this in the month) and before any
         * branch's staff are hired for the month, so the branches the month
         * pays are the ones its fees covered at last month's cost - a month
         * whose wages rise can find them short, and the next month's rule
         * closes the one that no longer pays (Jerus, "Accept the lag"). The
         * branch is retail's building -
         * retail paid for it (BusinessInvestment.planBank()) - so it leaves by
         * retail's path above: the plot back to the city, the material to the
         * builders. What the bank was founded with stays in the bank.
         */
        int branchesStanding = buildingManager.countByName("Commercial Bank");
        if (bank.closesBranch(branchesStanding)) closeBranches(bank.branchesToClose(branchesStanding));
    }

    /**
     * Closes branches (0.7.19: as many as the rule says, at once; one at a
     * time from 0.7.11 round 2 until then): the buildings retired by the
     * path any retired building takes (retire()), sold by their owner,
     * retail. Never the last. The count is for the run, like the lender's
     * renewals.
     */
    private void closeBranches(int wanted) {
        BuildingsTemplate branch = buildingManager.getTemplateByName("Commercial Bank");
        int standing = buildingManager.countByName("Commercial Bank");
        int n = Math.min(wanted, standing - 1);
        if (branch == null || n <= 0) return;
        String owner = getSectors().retail().key();
        String why = String.format("its customers' fees of $%,.1fk a branch do not cover the $%,.1fk a branch past the first costs",
                bank.feesPerBranch(), bank.laterBranchCost());
        int closed = retire(new BusinessInvestment.Decision(owner, branch, n, why, true),
                sectorInvestor(owner), false);
        if (closed > 0) {
            branchesClosed += closed;
            GameLog.note(String.format("The bank closed %d branch%s: %s.", closed, closed == 1 ? "" : "es", why));
        }
    }

    /** Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. */
    private int branchesClosed;
    public int getBranchesClosed() { return branchesClosed; }

    /**
     * Scraps what the decision named, sells the plot back to the city, and
     * sells the building's material to the builders (0.7.8 - see THE PLANT'S
     * MATERIAL, TO THE BUILDERS).
     *
     * @param distress whether the distress rule decided it, rather than the
     *                 spare-capacity rule - both retire through here, and the
     *                 sale is counted by which
     * @return how many buildings went, so a caller can tell whether to try
     *         the distress rule next
     */
    private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress){

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
            treasuryPays(TreasuryLine.LAND, proceeds);
            seller.receive(proceeds);
            landManager.recordBuyback(landFreed);
        } else {
            proceeds = 0;   // abandoned - the city takes it back for nothing
        }

        double material = sellMaterialToTheBuilders(decision, scrapped, seller, distress);

        lastInvestment.put(decision.sector,
                String.format("Sold %,d %s - %s%s%s",
                        scrapped, decision.template.getName(), decision.reason,
                        proceeds > 0
                                ? String.format(" (plot back to the city for $%s)",
                                        formatter.format(proceeds))
                                : " (plot abandoned - the city could not pay)",
                        material > 0
                                ? String.format(" (its material to the builders for $%s)",
                                        formatter.format(material))
                                : ""));

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
        if (getSectors().construction().key().equals(decision.sector)) {
            constructionShedMonth = month;
            constructionShedPoints += decision.template.makes(Good.BUILDING_WORK) * scrapped;
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

    /**
     * Whether the city should be told construction is dismantling itself.
     *
     * Only while it is RECENT and the standing policy is not already covering
     * the capacity that is left. A player who has set a subsidy has answered the
     * question and should not keep being asked it.
     */
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
     * The sectors whose plan this month was declined on its price (0.7.8):
     * not one of it would carry its interest at the rate the loan would be
     * written at, where it would have at prime and its record alone - so what
     * refused it was its own expected loss (BusinessDebtManager, PRICING FROM
     * THE CURVE). Cleared with the land's; the month's, read in the month.
     */
    private final java.util.Set<String> refusedOnPrice = new java.util.LinkedHashSet<>();

    public java.util.Set<String> getRefusedOnPrice(){
        return java.util.Collections.unmodifiableSet(refusedOnPrice);
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
     * test is very nearly monotone in quantity but not provably so. Since
     * 0.7.54 the scan is the countdown's first COUNTDOWN_SLICES slices and
     * then a search below them - see THE LARGEST SLICE, WITHOUT COUNTING TO
     * IT, below consider().
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
            SectorBooks.SectorMonth books = sectorBooks.get(decision.sector);
            double planCost = businessInvestment.getCostOf(decision.template, decision.quantity);
            double ask = equity.raiseFor(company, books.totalAssets(), books.equity(), planCost);
            /*
             * ...AND THE DOWN PAYMENT A MORTGAGE ASKS FOR (0.7.11). Jerus: "the
             * landlord's own cash or new shares pay the rest." What its till
             * and what it holds abroad cannot put down on the order, the
             * owners are asked for - on top of what the register would raise
             * anyway, and never more than the down payment itself: new shares
             * buy a building, not an overdraft.
             *
             * WHY THE OWNERS ARE ASKED, and why it stays (round 2; Jerus:
             * "Keep asking the owners."). This is how the equity in a rental
             * building is found. A developer raises it from its partners or
             * investors before the lender will close. A listed landlord (a
             * REIT) sells new units to fund the equity part of an
             * acquisition or a build, because it pays most of its income out
             * and keeps little in the till. A landlord here is the second
             * kind: a company over its equity target buys its surplus back
             * down to a cushion of its costs (Exchange, the companies buy
             * back), and in its normal regime the register raises nothing
             * and it borrows (Equity.raiseFor()). Without this ask it never
             * held a down payment at all. Measured on the first run of this
             * batch, seed 0 was held for the down payment in 3,877 of 4,001
             * months and ended at 1,987 people.
             */
            if (buysOnMortgage(decision)) {
                double down = Mortgage.ownFundsFor(planCost);
                double lacking = Math.min(down, down - payer.getCash()
                        - economyManager.getForeignAssets(decision.sector));
                if (lacking > ask) ask = lacking;
            }
            // ...and not at a quote under what the shares are worth: then it borrows.
            if (ask > 0 && !exchange.quoteSupportsIssue(company)) ask = 0;
            if (ask > 0) {
                // Built at the book's price: its last trade, or fair value
                // before one (0.7.12 round 2 - the dealer's mid until then).
                double raised = equity.offer(company, ask, books.equity(),
                        householdBalance, DebtManager.WORLD_BASE_RATE,
                        exchange.hasTraded(company) ? exchange.price(company) : 0);
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

        // A landlord's home is bought on an insured mortgage (0.7.11): its own
        // down payment, and the lender's test - see THE LANDLORDS' MORTGAGES.
        if (buysOnMortgage(decision)) {
            considerOnMortgage(decision, slot, cash, perUnitProfit);
            return;
        }

        // The largest slice that carries its interest: the countdown's own
        // first slices, then a search (0.7.54) - see THE LARGEST SLICE,
        // WITHOUT COUNTING TO IT, below.
        Afford afford = largestSliceThatCarries(decision, cash, perUnitProfit);
        if (orderWatch != null) orderWatch.invested(decision, cash, perUnitProfit, afford);
        int affordable = afford.quantity();
        boolean atPrime = afford.atPrime();
        double firstRate = afford.firstRate();

        if (affordable <= 0) {
            if (atPrime) refusedOnPrice.add(decision.sector);
            lastInvestment.put(slot, atPrime
                    ? String.format("Declined %s - not even one would cover its interest at %.2f%%,"
                            + " the rate its own risk costs at the debt it would take on",
                            decision.template.getName(), firstRate * 100)
                    : String.format("Declined %s - not even one would cover its interest",
                            decision.template.getName()));
            return;
        }

        int quantity = affordable;
        String trimmed = quantity < decision.quantity
                ? String.format(" (trimmed from %,d - could not carry the interest)",
                        decision.quantity)
                : "";

        projectFinancing.remove(decision.sector);
        if (buildFor(payer, decision.template, quantity)) {
            lastInvestment.put(slot,
                    String.format("Built %,d %s%s%s - %s",
                            quantity, decision.template.getName(), trimmed,
                            financingWords(projectFinancing.get(decision.sector)), decision.reason));
        } else if (credit.wasRefusedAtDefaultPoint(decision.sector)) {
            /*
             * PAST THE DEFAULT POINT (0.7.12, round 2): the sector owes more
             * than INSOLVENCY_TRIGGER times what it owns, and the bank lends
             * it nothing until it is back under - see BusinessDebtManager,
             * NOTHING PAST THE DEFAULT POINT. Read over its last quarter since
             * round 3, as the gate reads it.
             */
            double lev = credit.defaultPointLeverage(decision.sector);
            lastInvestment.put(slot, String.format("Holding: it owes %s, past the default point of %.2f"
                            + " times - the bank lends it nothing, %s included, until it is under",
                    Double.isFinite(lev) ? String.format("%.2f times what it owns, over its last quarter", lev)
                            : "more than everything it owns",
                    BusinessDebtManager.INSOLVENCY_TRIGGER, decision.template.getName()));
        } else if (credit.wasRefusedForCapital(decision.sector)) {
            /*
             * THE BANK'S CAPITAL SAID NO (0.7.8), and the investor says so
             * rather than blaming the land: the loan it needed was more than
             * the bank's capital rule lets a borrower's debt grow this month
             * (BusinessDebtManager.canFundProject()). Not a land shortage, so
             * not filed as one.
             */
            lastInvestment.put(slot, credit.isKeepGoingOnly()
                    ? String.format("Holding: the bank is under its capital minimum and lends only"
                            + " to keep its borrowers going - %s would need credit",
                            decision.template.getName())
                    : String.format("Holding: the bank is rebuilding its capital and lets a borrower's"
                            + " debt grow %.2f%% this month - %s would need more credit than that",
                            credit.getCapitalGrowth() * 100, decision.template.getName()));
        } else {
            // The plan cleared every test and the build still did not happen,
            // which now has exactly one cause: the land went between planning
            // and buying. Saying so beats the silence this used to leave.
            landBlockedSectors.add(decision.sector);
            lastInvestment.put(slot,
                    String.format("Could not build %s - needs %s, %s free",
                            decision.template.getName(),
                            LandManager.areaWords(decision.template.getLandSqFt() * (double) quantity),
                            LandManager.areaWords(landManager.getAvailableSqFt())));
        }
    }

    /* =======================================================================
       THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54)

       consider() trims an order to the largest slice that carries its
       interest. Until 0.7.54 it counted down from the whole order one
       building at a time and asked the bond desk about every one
       (financeProject()), so the month grew with the order and the order
       with the city: the scale study (the project's spec-scale.md, section
       4) measured months of a 1.1 billion city averaging 4.1 s and reaching
       14.8 s, 98.5% of it in this loop.

       NOW THE COUNTDOWN'S FIRST SLICES, THEN A SEARCH:
         1. the countdown itself, from the whole order down, for at most
            COUNTDOWN_SLICES slices - the same slices, asked the same way
            (Slices.passes()), so an order trimmed by fewer than that is
            decided exactly as it always was, whatever shape the test has;
         2. below them, doubling down from the last slice that failed - one
            less, then two, four, eight - until one passes or one is left;
         3. then halving between that one and the last that failed.
       The bond desk is asked at most COUNTDOWN_SLICES + 2 x 31 + 1 times an
       order, whatever its size (deskCallsMost()).

       WHY NOT HALVING FROM THE START. Halving assumes that if a slice fails,
       every bigger one fails, and here that is not proved. The rate a slice
       is judged at can fall as it borrows more: the leverage after the deal,
       (owed + a) / (owns + a), falls towards one for a sector that owes more
       than it owns (BusinessDebtManager.quote()), and a bond's fixed costs
       can make a bigger bond cheaper a dollar than a smaller one
       (BondMarket.plan()). The study's halving matched the countdown to
       x1000 and drifted 0.12% in people at x10,000. Here the search can
       differ from the countdown only if, below the slices counted, the test
       passes at some slice, fails at a smaller one and passes again at a
       smaller one still. In the runs measured at today's sizes - the long
       run of OrderSearchCheck, and Jerus's city and city2400 as they are -
       no order was trimmed into the search at all. OrderSearchCheck holds
       the two to the same answer over that run, in a copy a thousand times
       over whose trims do reach the search, and on tests of its own;
       ScaleCheck holds the bond desk to deskCallsMost() at 5 and 10
       billion. Measured beside the countdown on the research cities' copies
       to 10 billion, every order was the countdown's, the deepest trimmed
       by 9.1 million.

       THE REFUSAL SAYS WHAT THE COUNTDOWN SAID. The rate at the whole order
       (firstRate) is the first slice asked. Whether any slice would have
       carried its interest at prime and its record (atPrime) matters only
       when none passes, and then the countdown had asked every slice that.
       Up to PRIME_SCAN_SLICES slices, so do we (Slices.askPrimeOfTheRest()),
       which costs no bond desk. Past that we ask four, because at a fixed
       rate that test is drawn in straight lines: none passing means even one
       building borrows, so every slice borrows its cost less the till, and
       the test is the profit against a fixed share of that. The cost is the
       builders' price a building and the materials past the yard - two
       straight pieces, the second steeper - so what the interest is short
       by is least at one, at the whole order, or either side of where the
       yard runs out (BusinessInvestment.yardCovers()), and if none of those
       four carries it, none does: exactly so in real arithmetic, and to the
       rounding of the test's own figures in a double, which the scan to
       PRIME_SCAN_SLICES keeps out of every order today's cities place.
       Asking every slice of an order of 280 million, as 0.7.53's count did
       in a copy of the playtest's city at 5 billion, kept its median month
       at 8.8 s.
       ======================================================================= */

    /** The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. */
    public static final int COUNTDOWN_SLICES = 16;

    /** The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. */
    public static final int PRIME_SCAN_SLICES = 4096;

    /** The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down and halving, each at most once a bit of the order. */
    public static int deskCallsMost(int quantity) {
        int bits = 32 - Integer.numberOfLeadingZeros(Math.max(1, quantity));
        return COUNTDOWN_SLICES + 2 * bits + 1;
    }

    /**
     * What consider() decided of an order (0.7.54): the largest slice that
     * carries its interest, 0 for none; whether any slice would have carried
     * it at prime and its record, for the refusal; the rate at the whole
     * order; and how many times the bond desk was asked (financeProject()).
     */
    public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls) { }

    /** The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. */
    private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit) {
        Slices test = new Slices(decision, cash, perUnitProfit);
        int pass = largestSlice(decision.quantity, test::passes);
        if (pass == 0) test.askPrimeOfTheRest(decision.quantity);
        return test.result(pass);
    }

    /**
     * The largest n from 1 to quantity that passes, 0 for none, found as THE
     * LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first
     * COUNTDOWN_SLICES, then doubling down, then halving. Asks `passes` at
     * most deskCallsMost(quantity) times. OrderSearchCheck holds it to the
     * countdown on every boundary of a run from one.
     */
    static int largestSlice(int quantity, java.util.function.IntPredicate passes) {
        int fail = Math.max(1, quantity);   // the smallest slice known to fail, once one has
        // 1. The countdown itself, for its first COUNTDOWN_SLICES slices.
        for (int n = quantity; n >= 1 && n > quantity - COUNTDOWN_SLICES; n--) {
            if (passes.test(n)) return n;
            fail = n;
        }
        // 2. Doubling down from the last that failed...
        int pass = 0;              // the largest slice known to pass; 0 for none
        for (long step = 1; fail > 1; step *= 2) {
            int n = (int) Math.max(1, fail - step);
            if (passes.test(n)) { pass = n; break; }
            fail = n;
        }
        // 3. ...then halving between the two.
        while (fail - pass > 1) {
            int mid = (int) (((long) pass + fail) >>> 1);
            if (passes.test(mid)) pass = mid; else fail = mid;
        }
        return pass;
    }

    /** One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). */
    private final class Slices {
        final BusinessInvestment.Decision decision;
        final double cash, perUnitProfit;
        final BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        final boolean banned;
        boolean atPrime;
        double firstRate = Double.NaN;
        int deskCalls;

        Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit) {
            this.decision = decision;
            this.cash = cash;
            this.perUnitProfit = perUnitProfit;
            this.banned = credit.isBorrowingBlocked(decision.sector);
        }

        /** Whether n of them carry their interest; what failed at prime and the rate at the whole order are kept for the refusal. */
        boolean passes(int n) {
            double cost = businessInvestment.getCostOf(decision.template, n);
            double borrowed = Math.max(cost - cash, 0);
            // Under a ban the largest slice is the one its own cash pays for.
            if (banned && borrowed > 0) return false;
            /*
             * AT THE RATE THE LOAN WOULD BE WRITTEN AT (0.7.8), not today's
             * quote: the curve at the leverage this slice leaves the sector
             * at, its building counted (BusinessDebtManager.projectRate()).
             * Judged at the quote, a plan that takes a sector from 0.8 to 1.2
             * times its assets was asked to earn prime and a quarter point,
             * and then written at prime and eleven. This is the brake.
             */
            double rate = credit.projectRate(decision.sector, borrowed);
            if (n == decision.quantity) firstRate = rate;
            /*
             * ...OR AT A BOND'S, WHERE ONE IS CHEAPER (0.7.12). Jerus:
             * "Cheapest, within the bank's limit." The same test - the
             * building must earn BusinessInvestment's margin over the
             * interest - at the instrument's own rate: the bond's coupon on
             * what the bond raises and the loan's rate on the rest, as
             * financeProject() splits it. A plan that cannot raise the whole
             * is judged at the loan's rate, as before, and the bank's own
             * rules say no at the door.
             */
            BusinessDebtManager.Plan plan = null;
            if (borrowed > 0) {
                plan = financeProject(decision.sector, borrowed, rate);
                deskCalls++;
            }
            double tested = plan != null && plan.hasBond() && plan.covers() ? plan.blendedRate() : rate;
            if (businessInvestment.servicesItsOwnDebt(perUnitProfit * n, borrowed, tested)) return true;
            if (carriesAtPrime(n, borrowed)) atPrime = true;
            return false;
        }

        /** Whether n of them would carry their interest at prime and the sector's record. */
        boolean carriesAtPrime(int n, double borrowed) {
            return businessInvestment.servicesItsOwnDebt(perUnitProfit * n, borrowed,
                    credit.getPrimeRate() + credit.getRecordSurcharge(decision.sector));
        }

        /** When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that. */
        void askPrimeOfTheRest(int q) {
            if (q <= PRIME_SCAN_SLICES) {
                for (int n = 1; n <= q && !atPrime; n++) askPrime(n);
                return;
            }
            long yard = businessInvestment.yardCovers(decision.template);
            for (long n : new long[] { 1, q, yard, yard + 1 }) {
                if (n >= 1 && n <= q && !atPrime) askPrime((int) n);
            }
        }

        /** Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. */
        void askPrime(int n) {
            double borrowed = Math.max(businessInvestment.getCostOf(decision.template, n) - cash, 0);
            if (banned && borrowed > 0) return;
            if (carriesAtPrime(n, borrowed)) atPrime = true;
        }

        Afford result(int quantity) { return new Afford(quantity, atPrime, firstRate, deskCalls); }
    }

    /* =======================================================================
       THE LANDLORDS' MORTGAGES (0.7.11)

       Jerus, 2026-09-24: "Mortgages", on "CMHC (Canada)" terms, "Insured by
       the city". A building the landlords order is bought as a landlord
       buys one: at least 15% down from its own funds - what the equity
       raise and the recall from abroad leave in its till, above, the owners
       asked for whatever of the down payment the till and the money abroad
       cannot put down - and the rest on an insured Mortgage, at most
       Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost, if the lender's test
       passes.

       THE LENDER'S TEST, which replaces servicesItsOwnDebt() for these orders
       only: the building's net operating income must cover the mortgage's
       monthly payment Mortgage.MORTGAGE_DEBT_COVERAGE times. The income is
       the rent it would let at today's segment price
       (RealEstate.estimatedMonthlyProfit()) less what it costs to hold -
       its maintenance and its property tax, the carry the owner's own test
       puts its margin on (EconomyManager.housingCarry()); the payment is the
       level annuity on the principal, premium included, over
       Mortgage.MORTGAGE_AMORTIZATION_MONTHS, at the rate the test is asked
       at - the real one since 0.7.44, below. The owner's own test,
       whether the building is worth putting up at all
       (EconomyManager.housingBuildHurdle(), in RealEstate.plan()), stays; so
       does what and how much the planner orders. This decides how much of
       it is bought.

       THE DOWN PAYMENT. A till that cannot put 15% down on the whole order
       orders fewer; one that cannot put it down on one holds, and says how
       much it needs. A till in overdraft covers nothing. A till that covers
       the whole order buys it outright, as any business does.

       WHY. A 36-month bullet at prime and its own risk, lent against 100%
       of the cost and asked to earn 1.25 times its interest gross, stopped
       the cranes twice in the 0.7.10 trace: for two hundred months on
       autopilot, when the landlord's hole was added to every building's
       loan, and for 333 years at a 10% dial - see Mortgage.

       THE LENDER'S TEST IS REAL (0.7.44; star 7 of spec-inflation.md,
       extended to the landlords). The payment is read at the insured rate
       less the inflation the city expects, never under
       BusinessInvestment.REAL_HURDLE_FLOOR of it
       (BusinessInvestment.realTestRate()) - the test every other investor's
       order has been asked since 0.7.42 (servicesItsOwnDebt(), THE HURDLE IS
       REAL), which never reached these orders because this test replaces
       that one. The reason is the same and stronger here: the mortgage is
       nominal, and since 0.7.43 the rent it is paid out of drifts at expected
       inflation (RealEstate.repriceRent()). The mortgage itself is still
       written at the insured rate; only the test reads the real one, and the
       refusal says the rate it read. Measured (runs/diag-0743.md, section 1):
       at the nominal rate the lender said no for 26-31 months of the founding
       step while the rule rate stood at 8-27%, no door was added from month
       62 to 91, and 10 of 16 ensemble cities had households with no home at
       months 89-94; at the real rate, none.
       ======================================================================= */

    /** True for an order bought on an insured mortgage: a residential building the landlords order. Every template the landlords own is residential, and nothing else is. */
    private boolean buysOnMortgage(BusinessInvestment.Decision decision) {
        return decision != null && decision.template != null
                && decision.template.getCategory() == BuildingType.RESIDENTIAL
                && getSectors().realEstate().key().equals(decision.sector);
    }

    /**
     * The landlords' order, on a mortgage: the largest slice of it the
     * landlord's own funds can put down on and the lender's test passes, of
     * what the planner asked for (Mortgage.decide(), which halves since
     * 0.7.54) - and the advisor's line, in the lender's words when it says no.
     *
     * @param perUnitRent what one of these would let for a month (estimatedMonthlyProfit())
     */
    private void considerOnMortgage(BusinessInvestment.Decision decision, String slot,
                                    double cash, double perUnitRent) {
        BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        BuildingsTemplate t = decision.template;
        // ...tested at the real rate, written at the insured one: see THE LENDER'S TEST IS REAL.
        double insured = credit.getInsuredMortgageRate();
        double tested = businessInvestment.realTestRate(insured);
        java.util.function.IntToDoubleFunction costOf = n -> businessInvestment.getCostOf(t, n);
        double noi = perUnitRent - economyManager.housingCarry(t);
        Mortgage.Decision d = Mortgage.decide(decision.quantity, costOf, cash, noi, tested);
        if (orderWatch != null) orderWatch.mortgaged(decision.quantity, costOf, cash, noi, tested, d);

        if (d.quantity() <= 0) {
            (d.shortOfDown() ? heldForDownPayment : refusedByLender).add(decision.sector);
            lastInvestment.put(slot, d.refusal(t.getName(), tested, insured));
            return;
        }

        int quantity = d.quantity();
        String trimmed = d.trimmed(decision.quantity);
        double downShare = 1 - Mortgage.MORTGAGE_MAX_LOAN_TO_COST;

        String[] refusal = { null };
        int written = credit.getMortgagesWrittenThisMonth();
        if (buildFor(mortgageInvestor(decision.sector, refusal), t, quantity)) {
            boolean mortgaged = credit.getMortgagesWrittenThisMonth() > written;
            lastInvestment.put(slot, String.format("Built %,d %s%s%s - %s", quantity, t.getName(),
                    mortgaged ? " on an insured mortgage" : "", trimmed, decision.reason));
        } else if (refusal[0] != null) {
            /*
             * THE LENDER SAID NO AT THE DOOR, after the plan passed: the price
             * moved between the plan and the purchase, the bank is shut, the
             * deal would leave the landlord past the default point, or - while
             * the bank's leverage ratio binds (round 2) - its capital rule has
             * no room for the mortgage (BusinessDebtManager.canFundMortgage()).
             */
            if (BusinessDebtManager.MORTGAGE_DOWN_PAYMENT.equals(refusal[0])) heldForDownPayment.add(decision.sector);
            else refusedByLender.add(decision.sector);
            lastInvestment.put(slot, switch (refusal[0]) {
                case BusinessDebtManager.MORTGAGE_BANK_SHUT -> String.format(
                        "Holding: the bank has failed and writes no mortgage - %s would need one", t.getName());
                case BusinessDebtManager.MORTGAGE_DOWN_PAYMENT -> String.format(
                        "Holding: needs more of its own for the %.0f%% down payment on %,d %s",
                        downShare * 100, quantity, t.getName());
                case BusinessDebtManager.MORTGAGE_PAST_DEFAULT_POINT -> String.format(
                        "Declined %s - the mortgage would leave it owing past %.2f times what it owns",
                        t.getName(), BusinessDebtManager.INSOLVENCY_TRIGGER);
                case BusinessDebtManager.MORTGAGE_CAPITAL -> String.format(
                        "Holding: the bank is short of capital against everything it has lent"
                                + " - %s would need a mortgage", t.getName());
                default -> String.format("Holding: the lender will not write the mortgage on %s (%s)",
                        t.getName(), refusal[0]);
            });
        } else {
            landBlockedSectors.add(decision.sector);
            lastInvestment.put(slot,
                    String.format("Could not build %s - needs %s, %s free",
                            t.getName(), LandManager.areaWords(t.getLandSqFt() * (double) quantity),
                            LandManager.areaWords(landManager.getAvailableSqFt())));
        }
    }

    /**
     * The sectors whose plan this month the mortgage lender declined, and
     * those that held for the down payment (0.7.11) - the month's, cleared
     * with the land's, for the playtest's count by reason.
     */
    private final java.util.Set<String> refusedByLender = new java.util.LinkedHashSet<>();
    private final java.util.Set<String> heldForDownPayment = new java.util.LinkedHashSet<>();

    public java.util.Set<String> getRefusedByLender() {
        return java.util.Collections.unmodifiableSet(refusedByLender);
    }

    public java.util.Set<String> getHeldForDownPayment() {
        return java.util.Collections.unmodifiableSet(heldForDownPayment);
    }

    /**
     * One sector's cash and its insured-mortgage lender, as a payer: what
     * sectorInvestor() does with its till, and a mortgage where that would
     * borrow a loan. The lender's answer at the door is left in refusal[0]
     * when it says no. The landlord is handed the loan less its fee - the
     * shortfall buildFor() asked for - and the premium added to the loan
     * goes to the treasury as the mortgage is written: revenue, on the
     * budget's mortgage insurance line (NationalAccounts).
     */
    private Investor mortgageInvestor(final String sector, final String[] refusal) {
        final Investor own = sectorInvestor(sector);
        final BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        return new Investor() {
            @Override public String getName() { return own.getName(); }
            @Override public double getCash() { return own.getCash(); }
            @Override public void spend(double amount) { own.spend(amount); }
            @Override public boolean canBorrow(double amount) {
                boolean yes = credit.canFundMortgage(sector, amount, amount + own.getCash());
                if (!yes) refusal[0] = credit.getMortgageRefusal();
                return yes;
            }
            @Override public void borrow(double amount, int month) {
                Mortgage m = credit.issueMortgage(sector, amount, month);
                economyManager.setSectorCash(sector, economyManager.getSectorCash(sector)
                        + m.getLoan() - BusinessDebtManager.feeOn(m.getOutstandingPrincipal()));
                cash += m.getPremium();
            }
        };
    }

    /* =======================================================================
       THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8)

       Jerus, 2026-09-23: "Sell a failing sector's plant ... have it be sold
       to construction company just in materials, and land to treasury". The
       plot already went to the treasury (retire(), above); until now the
       building itself was torn down for nothing, and a sector that retired
       its plant lost the whole of its book value from its assets in the
       month - Manufacturing's went $146M -> $5.2M in three on the first
       traced seed, and with its leverage past 5 the month's defaults took
       60% of its debt at once.

       Now the builders buy the MATERIAL the building was built with - its
       template's constructionMaterials, the units an order draws - at the
       materials market's price the day it is sold, and take it into their
       own stock (Construction.addSalvage()), which their next draws take
       before they buy from the plant or the world (drawMaterials()). The
       labour and everything else in the build cost fetch nothing.

       WHAT THE BUILDERS' BOOKS ALLOW: they pay out of the cash they hold,
       and buy what that covers; what they cannot pay for is scrapped for
       nothing, as it always was. Room does not bind - their depots declare no
       warehouse and the model gives a stock no ceiling - and they do not
       borrow for it. The money moves between two sectors inside the city,
       so the audit closes as it did: the seller's till and the builders'
       change by the same sum, recorded on both cash-flow statements beside
       what each spent on premises (SectorBooks salvage). A builder retiring
       its own depot keeps its material and pays itself nothing.
       ======================================================================= */

    /**
     * One building type's material, sold this month: whose, how many
     * buildings, the units, the price a unit, what the builders paid for
     * what they could afford, the units they could not pay for, and which
     * rule retired it. A month's flow, read in the month.
     */
    public record Salvage(String seller, String building, int buildings, double units,
                          double price, double paid, double unitsBought, boolean distress) { }

    private final java.util.List<Salvage> salvageThisMonth = new java.util.ArrayList<>();
    private final java.util.Map<String, Double> salvageBySector = new java.util.LinkedHashMap<>();
    private double salvageUsedThisMonth;

    /** Every sale of scrapped plant's material this month. */
    public java.util.List<Salvage> getSalvageThisMonth() {
        return java.util.Collections.unmodifiableList(salvageThisMonth);
    }

    /** What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. */
    public double getSalvageThisMonth(String sector) {
        return salvageBySector.getOrDefault(sector, 0.0);
    }

    /** Units of material the builders drew from their salvage this month instead of buying. */
    public double getSalvageUsedThisMonth() { return salvageUsedThisMonth; }

    /** @return what the builders paid */
    private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped,
                                             Investor seller, boolean distress) {
        double units = decision.template.getConstructionMaterials() * (double) scrapped;
        double price = getMarkets().get(Good.MATERIALS).getLocalPrice();
        if (!(units > 0) || !(price > 0)) return 0;
        ham.citybuildersim.sectors.Construction builders = getSectors().construction();
        boolean own = builders.key().equals(decision.sector);
        double bought = own ? units : Math.min(units, Math.max(0, builders.getCash()) / price);
        double paid = own ? 0 : bought * price;
        if (paid > 0) {
            sectorInvestor(builders.key()).spend(paid);
            seller.receive(paid);
            salvageBySector.merge(builders.key(), paid, Double::sum);
            salvageBySector.merge(decision.sector, -paid, Double::sum);
        }
        if (bought > 0) builders.addSalvage(bought, paid);
        salvageThisMonth.add(new Salvage(decision.sector, decision.template.getName(), scrapped,
                units, price, paid, bought, distress));
        return paid;
    }

    /* =======================================================================
       A BOND OR THE BANK, FOR A BUILDING (0.7.12)

       The investment desk's half of BondMarket, WHO ISSUES, AND WHEN: what
       a building's borrowing would raise in a bond - the largest one whose
       coupon, with its costs spread over its ten years, is cheaper than the
       bank's loan - and what the bank lends of the rest; never more,
       together, than the bank would have lent (projectLoanRoom()), since the
       brief is "cheapest, within the bank's limit". consider() judges the
       building at the plan's rate; the sector's investor issues it.
       ======================================================================= */

    /** How a building's borrowing would be financed now: a bond, the bank, or both. Books nothing. */
    private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate) {
        BusinessDebtManager credit = economyManager.getBusinessDebtManager();
        return bondMarket.plan(sector, amount, loanRate, credit.projectLoanRoom(sector, amount),
                credit.projectBondRoom(sector, amount), amount, month);
    }

    /** The plan each sector's last building was financed on this month, for the advisor's line. */
    private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing = new java.util.LinkedHashMap<>();

    /**
     * THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond
     * and its coupon against the bank's rate when a bond was no dearer, and
     * the bank's part beside it; the bank, and what the book would have
     * cleared at, when the bank was the cheaper - or when the book would
     * clear lower but an issue's fixed costs outweigh it on a small sum.
     * Nothing for a building paid from the till.
     */
    public static String financingWords(BusinessDebtManager.Plan plan) {
        if (plan == null) return "";
        if (plan.hasBond()) {
            String bond = String.format(" - issued $%,.0fk of %d-year bonds at %.2f%%", plan.bondFace(),
                    CorporateBond.TERM_MONTHS / 12, plan.coupon() * 100);
            if (plan.loan() > 0) {
                return bond + String.format(" and borrowed $%,.0fk from the bank at %.2f%%",
                        plan.loan(), plan.loanRate() * 100);
            }
            return plan.allIn() < plan.loanAllIn() - 1e-9
                    ? bond + String.format(" - cheaper than the bank's %.2f%%", plan.loanRate() * 100)
                    : bond + String.format(" - no dearer than the bank's %.2f%%, its costs counted", plan.loanRate() * 100);
        }
        if (Double.isNaN(plan.clearing())) return " - borrowed from the bank - the bond book would not fill it";
        return plan.clearing() >= plan.loanRate()
                ? String.format(" - borrowed from the bank - the bond book would clear only at %.2f%%",
                        plan.clearing() * 100)
                : String.format(" - borrowed from the bank - the bond book would clear at %.2f%%, but an issue's"
                        + " costs on so small a sum would make it the dearer", plan.clearing() * 100);
    }

    /** The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. */
    public static double grossedForFee(double purpose) {
        return purpose > 0 ? purpose / (1 - Bank.LOAN_FEE) : 0;
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

            // Businesses borrow freely here - the rate is the curve's at the
            // leverage the loan leaves them at (0.7.8; a leverage line capped
            // at seven points before). What stops a spiral is the project
            // having to service its own debt at that rate, checked in
            // consider() above.
            //
            // EXCEPT during a borrowing ban. A sector that was just written
            // down is barred from shortfall loans for twelve months
            // (BusinessDebtManager.coverShortfall), and until 2026-09-06 that
            // ban stopped at the shortfall desk: this method said yes to any
            // positive amount, so the same sector could not borrow to keep the
            // lights on but could borrow to expand. The ban is one ban.
            @Override public boolean canBorrow(double amount) {
                // ...GROSSED UP FOR ITS FEE (0.7.12, round 5): what the bank
                // is asked for is what hands the builder `amount` - see borrow().
                amount = grossedForFee(amount);
                // ...AND THE LINE. The ban stopped here from 2026-09-06; the
                // insolvency line did not until 2026-09-10, so a sector could
                // be lent the money to expand straight past the point the
                // lender would write it down. canFundProject() tests the
                // balance sheet after the deal, with the building counted, and
                // is false while barred and while the bank itself is shut.
                // ...OR A BOND AND THE BANK TOGETHER RAISE IT ALL (0.7.12),
                // which they can only where the bank would have lent it all
                // (financeProject()) - so this is the same answer, asked of
                // the plan the investor will borrow on.
                BusinessDebtManager.Plan plan = financeProject(sector, amount, credit.projectRate(sector, amount));
                if (plan != null && plan.hasBond() && plan.covers()) return true;
                return credit.canFundProject(sector, amount);
            }

            @Override public void borrow(double amount, int month) {
                /*
                 * THE BOND FIRST, WHERE THE PLAN HAS ONE (0.7.12): sold at its
                 * coupon, the proceeds less its costs to the sector; the bank
                 * lends the rest.
                 *
                 * ...AND EVERY LOAN HANDED ITS FULL PURPOSE (0.7.12, round 5;
                 * Jerus: "gross a project loan up for its fee, as the
                 * shortfall desk's loan already is (0.7.7). Every loan is then
                 * handed its full purpose."). The plan is struck on the
                 * grossed amount, and the bank lends what the bond's
                 * proceeds left of `amount` grossed for the loan's fee, so a
                 * bond's costs ride in the loan's principal too: the builder is
                 * handed exactly `amount`, and the till the build is paid
                 * from does not end the month short by a fee.
                 */
                double gross = grossedForFee(amount);
                BusinessDebtManager.Plan plan = financeProject(sector, gross, credit.projectRate(sector, gross));
                double handed = 0;
                if (plan != null && plan.hasBond() && plan.covers()) {
                    handed += bondMarket.issue(plan, month);
                    GameLog.note(String.format("%s%s.", sector, financingWords(plan).replaceFirst(" - ", " ")));
                }
                projectFinancing.put(sector, plan);
                double loan = grossedForFee(Math.max(0, amount - handed));
                if (loan > 0) {
                    // A project's loan, priced with its building counted (0.7.8).
                    credit.issueProjectLoan(sector, loan, month);
                    // ...handed the principal less the loan's fee, which the
                    // bank keeps back at the settle (0.7.7).
                    handed += loan - BusinessDebtManager.feeOn(loan);
                }
                economyManager.setSectorCash(sector, economyManager.getSectorCash(sector) + handed);
            }
        };
    }

    /**
     * Construction points the last load cleared from stacks that carried
     * them past what they owed (0.7.17; see the load path and
     * BuildingManager.clearBankedProgress()). Zero for a new city and for any
     * save written since. Reported, not saved.
     */
    private double bankedClearedAtLoad;

    public double getBankedClearedAtLoad() { return bankedClearedAtLoad; }

    /** Read-only access for the utilities and construction screens. */
    public ServicesManager getServicesManager(){
        return servicesManager;
    }

    /**
     * This month's effective construction output: the sector's capacity scaled
     * by how well it is staffed and by what the roads will carry.
     *
     * THE ONE DEFINITION. SimulationEngine.simulateMonth() calls this rather
     * than working it out again (through getBuildingOutput(), less the
     * repairs), and hands the same figure to Construction.recogniseWork() for
     * the month's work (recogniseSiteWork(); startOfMonthUpdate() did until
     * 2026-09-11). Those three
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
        ham.citybuildersim.sectors.Construction builders = getSectors().construction();
        double constructionFillRate = builders.getAverageFill();
        double roadRatio = servicesManager.getRoadRatio();

        /*
         * THE CREWS KEPT ON (0.7.17): the depots' capacity at the share of
         * their posts the builders offered this month, the city's own works
         * department whole - it has no posts to lay off. See
         * sectors.Construction, THE CREWS THE WORK NEEDS. With every post
         * offered this is the figure it always was
         * (getConstructionOutputAtEveryPost()).
         */
        double capacity = buildingManager.getTotalConstructionCapacity();
        double depots = Math.max(0, capacity - BuildingManager.BASE_CONSTRUCTION);
        double working = capacity - depots * (1 - builders.getPostsOfferedShare());

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
        return (int) Math.round(working * constructionFillRate * roadRatio * health.getWorkRatio());
    }

    /**
     * What the builders would do this month with every post offered, at the
     * city's fill (0.7.17): the figure getConstructionOutput() was before the
     * builders laid idle crews off. For whoever is weighing an ORDER - the
     * builders' own planner and retirement, the landlords' months of work,
     * every sector's lead time and the build quote - because an order is
     * work, and work hires the crews back the month after it is placed.
     */
    public int getConstructionOutputAtEveryPost() {
        return (int) Math.round(buildingManager.getTotalConstructionCapacity()
                * getSectors().construction().getAverageFill()
                * servicesManager.getRoadRatio() * health.getWorkRatio());
    }

    /** ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. */
    public int getBuildingOutputAtEveryPost() {
        return (int) Math.max(0,
                Math.round(getConstructionOutputAtEveryPost() - getMaintenancePoints()));
    }

    /**
     * The builders strike the month's crews (0.7.17): the work ahead - the
     * repairs of the city standing now and every point still owed on site
     * after this month's advance - against what their depots and the city's
     * works department would do with every post filled. Called once a month
     * from SimulationEngine.updatePopulation(), before the jobs are counted
     * and the labour market allocates them. See sectors.Construction, THE
     * CREWS THE WORK NEEDS, for why there and what a reload does.
     */
    void strikeBuildersCrews() {
        double atFullStaffing = servicesManager.getRoadRatio() * health.getWorkRatio();
        double capacity = buildingManager.getTotalConstructionCapacity();
        double depots = Math.max(0, capacity - BuildingManager.BASE_CONSTRUCTION);
        getSectors().construction().strikeCrews(
                getMaintenancePoints() + buildingManager.getRemainingConstructionPoints(),
                (capacity - depots) * atFullStaffing,
                depots * atFullStaffing);
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
        return buildingManager.totalBySector(getSectors().realEstate().key(),
                BuildingsTemplate::getConstructionPoints)
                * ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12;
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
        return economyManager.maintenancePointsTotal();
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
     * THE RAILWAY'S MONTH, and the band it leaves behind.
     *
     * Beside chargeBuildingMaintenance() because it is the same kind of thing:
     * one sector billing every other, before any statement is struck. See
     * sectors.Rail.haul(), which does the arithmetic and explains why the
     * invoice is raised at LAST month's quote.
     *
     * AND THE ROAD IS TOLD, in the same breath, because what the railway is
     * carrying is freight that is not on the street. The relief is applied the
     * way the highways' and the trams' already are - see
     * InfrastructureManager.RAIL_ROAD_RELIEF - so a city with no track computes
     * exactly what it computed before any of this existed.
     */
    private void chargeFreight() {
        ham.citybuildersim.sectors.Rail rail = getSectors().rail();
        rail.haul(getSectors());
        getInfrastructureManager().setRailShare(rail.getCarried());
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

        ham.citybuildersim.sectors.Construction builders = getSectors().construction();

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
        drawMaterials(economyManager.getMaintenanceMaterialsTotal(), true);

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
        /*
         * ...UNLESS IT CANNOT (0.7.0). The city's repairs are a discretionary
         * line: once the central bank's advances reach their ceiling they are
         * paid only from cash at or above zero, and what is not paid is owed to
         * the builders as arrears - the work was done. The builders are paid
         * what the treasury paid now and the rest when the arrears are paid
         * down, so the money they receive is the money that left.
         */
        double repairsOwed = 0;
        if (cityShare > 0) {
            double paidNow = treasuryPays(TreasuryLine.CITY_REPAIRS, cityShare);
            repairsOwed = cityShare - paidNow;
            cityShare = paidNow;
            cityMaintenancePaid = cityShare;
            /*
             * JOURNALLED, BECAUSE THE BUDGET BALANCE DOES NOT CARRY IT. The
             * Government screen listed this bill under spending as "Repairs"
             * (since 0.7.31 it names it under the total, outside the
             * budget's), but NationalAccounts.getTotalExpenses() has no line
             * for it, so the surplus the bridge starts from is struck without
             * it and the bridge's last row held exactly -cityShare every
             * month, in every city with a road. Named here until the accounts
             * carry it; the day they do, this line comes out. See
             * TreasuryJournal.
             */
            treasuryJournal.record("Repaired the city's own buildings", -cityShare);
        } else {
            cityMaintenancePaid = 0;
        }

        // ...AND THE BANK ITS BRANCHES, out of its own cash, with its payroll -
        // and since 0.7.19 their templates' operating cost beside the repairs,
        // the charter's excepted, struck at the settle
        // (EconomyManager.bankOperatingCost()).
        bankMaintenanceDue = economyManager.getBankMaintenanceBill();

        builders.receiveMaintenance(bill - repairsOwed);
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

    /** The bank's repair bill for its branches this month, charged with its running costs. */
    private double bankMaintenanceDue;

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
     * Runs up to {@code months} monthly cycles, stopping early only if a month
     * throws (below). Returns how many months actually ran.
     *
     * AN EMPTY TREASURY NO LONGER STOPS IT (0.7.15, Jerus: "Skip runs too").
     * It broke at cash <= 0 from its first version until 0.7.14, which also
     * said why on the skip's report; the play clock never did, and the skip now
     * runs on the central bank's advances as play does - the settle at the
     * top of each month advances the shortfall (settleTreasury()), and the
     * report counts the months on advances and what was advanced
     * (TimeSkipReport.sampleTreasury()).
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
                // ...and the treasury's month with its central bank (0.7.1):
                // since 0.7.15 what it advanced, and what the treasury owes it.
                skipReport.sampleTreasury(centralBank.getAdvancesToTreasury() > 0,
                        centralBank.ceilingBound(), centralBank.getAdvancedToTreasury(),
                        centralBank.getAdvancesToTreasury());
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
        /** What the city's own yard covers, free. */
        public final double materialsInStock;
        /** What the materials plant sells the order, at the market price. Since the sector template. */
        public final double materialsFromPlant;
        public final double plantPrice;
        public final double plantCost;
        public final double materialsImported;
        public final double materialsPrice;
        public final double importCost;
        /**
         * The sales tax in the price (0.7.19): what the builders remit on it,
         * less what they claim back on the plant's material - passed on, as a
         * shop's shelf price passes it on. See quoteBuild().
         */
        public final double salesTax;
        /**
         * What the price allows for the material beyond the yard (0.7.19): the
         * plant's and the world's at today's prices, net of the credit on the
         * plant's and with the builders' tax on it - the part of the total an
         * escalation clause trues up as the crews draw it.
         */
        public final double allowance;
        public final double total;
        public final double landNeeded;
        public final double landFree;
        /**
         * Months to finish at this month's shares of the site output, the
         * planners' own reading (quoteMonths()), or NaN when the builders
         * have no site output.
         */
        public final double months;

        BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock,
                   Markets.Draw boughtIn, double plantPrice, double materialsPrice,
                   double landNeeded, double landFree, double months,
                   double buildersRate, double plantRate) {
            this.quantity = quantity;
            this.sticker = sticker;
            this.materialsNeeded = materialsNeeded;
            this.materialsInStock = Math.min(materialsNeeded, materialsInStock);
            this.materialsFromPlant = boughtIn.local();
            this.plantPrice = plantPrice;
            this.plantCost = boughtIn.localCost();
            this.materialsImported = boughtIn.imported();
            this.materialsPrice = materialsPrice;
            this.importCost = boughtIn.importCost();
            double gross = 1 / (1 - Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, buildersRate)));
            double plantNet = plantCost * (1 - Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, plantRate)));
            this.allowance = (importCost + plantNet) * gross;
            // Summed in the order the price always was - the work, the plant's,
            // the world's - so an untaxed quote is the old one to the last bit.
            this.total = sticker * gross + plantNet * gross + importCost * gross;
            this.salesTax = total - sticker - plantCost - importCost;
            this.landNeeded = landNeeded;
            this.landFree = landFree;
            this.months = months;
        }

        /** What had to be bought beyond the yard - the plant's and the world's together. */
        public double boughtInCost() { return plantCost + importCost; }

        /** The material units the crews will draw beyond the yard, which the allowance was priced on. */
        public double unitsBeyondYard() { return materialsFromPlant + materialsImported; }
    }

    /* =====================================================================
       THE BUILDERS' PRICE (0.7.19)

       Jerus chose "Builders' prices keep up": "The labour part of building
       and repair prices rises with wages, materials are paid at the price
       when they're used, and sales tax is in the builder's quote."

       THE LABOUR. The non-material part of the price is the template's cash
       cost with its labour at today's builders' wages
       (BuildingManager.nonMaterialCost()); the wage bill a point is read
       here, off a Construction Depot's posts - buildersWages.

       THE SALES TAX IS IN THE QUOTE, as a shop's shelf price has it. The
       model's VAT rules (SalesTaxLedger): a seller remits its rate on its
       gross sales; a buyer is credited what its local supplier charged, at
       the supplier's rate; an import is charged and credited to the
       importer, and nets to nothing. So a builder whose price is G remits
       rB x G and claims rM x the plant's material back, and what it keeps
       for the work is G(1 - rB) - imports - plant(1 - rM). For it to keep
       the non-material price N:

           G = (N + imports + plant x (1 - rM)) / (1 - rB)

       and the tax falls on the owner - on the value the builders added and
       on the material alike, once. Until 0.7.19 the price was N + imports +
       plant and the builders remitted 16% of all of it out of the work: in
       Jerus's city 108.6 of 150.2 of their tax fell on material passed
       through at cost (the trace's Q5). An owner that makes taxable supplies
       claims the tax back as the work is billed (EconomyManager,
       settleSalesTax(), THE TAX ON A BUILDING IS CLAIMED BACK). The
       landlords, whose rent is exempt, claim no credit, but since the
       revision ("Both rebates") get the tax on a new rental home back by a
       rebate - all of it on an apartment building, little or none on a House
       - and the city's own rebate is the tax coming home to its treasury as
       the builders remit it (EconomyManager, THE REBATES ON A NEW HOME, AND
       THE CITY'S). The bank's branch bears it.

       THE MATERIAL, AT THE PRICE WHEN IT IS USED: an escalation clause. The
       quote still prices the material at today's rates - the ALLOWANCE - and
       the owner still pays the whole quote up front, so what it borrows at
       the order is the quote, as before. As the crews draw it, the owner
       pays what the month's draw cost the builders, grossed up the same way,
       less the allowance for those units - or is refunded the difference
       (settleSiteContracts()). An economic price adjustment on material is
       a standard contract clause - the US Federal Acquisition Regulation's
       is FAR 52.216-4, "Economic Price Adjustment - Labor and Material"
       (acquisition.gov/far/52.216-4), which adjusts the price by the change
       in the material's cost - and this is its material half; the labour
       half is the index above, struck when the order is priced.
       ===================================================================== */

    /**
     * Who ordered a building of this kind, for a save that did not say
     * (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays
     * for them (BusinessInvestment.planBank()); the city for the rest.
     */
    private String ownerOfOrder(BuildingsTemplate t) {
        if (t == null) return "City";
        if ("Commercial Bank".equals(t.getName())) return getSectors().retail().key();
        return t.isOwnedBySector() && getSectors().byKey(t.getSector()) != null ? t.getSector() : "City";
    }

    /** Set by the load path when a save carries no payers, and read once the land price is back. */
    private boolean contractsToInfer;

    /**
     * ...and the share of the tax on it that owner gets back, for the same
     * save (revised 0.7.19): on the tax a building of it carries at today's
     * price (EconomyManager.taxRecoveredShare()).
     */
    private double recoveredOnOldContract(String payer, BuildingsTemplate t) {
        if (t == null) return 0;
        double before = buildingManager.nonMaterialCost(t)
                + t.getConstructionMaterials() * Math.max(0, buildingManager.getConstructionMaterialPrice());
        return economyManager.taxRecoveredShare(payer, t, economyManager.withBuildersTax(before) - before);
    }

    /** What the builders' crews cost a point, today and at founding. See THE BUILDERS' PRICE. */
    private final BuildingManager.BuildersWages buildersWages = new BuildingManager.BuildersWages() {
        @Override public double perPointToday()      { return depotWageBillPerPoint(true); }
        @Override public double perPointAtFounding() { return depotWageBillPerPoint(false); }
    };

    /**
     * A Construction Depot's posts - the builders' own job mix - at today's
     * wages or at the founding ladder (PayTier, in today's unit), over the
     * points it makes. Zero when there is no depot to read, or no wage yet
     * for one of its posts today.
     */
    private double depotWageBillPerPoint(boolean today) {
        BuildingsTemplate depot = buildingManager == null ? null
                : buildingManager.getTemplateByName("Construction Depot");
        double points = depot == null ? 0 : depot.makes(Good.BUILDING_WORK);
        if (!(points > 0)) return 0;
        double[] wages = today ? economyManager.getWageRates() : null;
        double unit = denomination.getUnit() > 0 ? denomination.getUnit() : 1;
        double bill = 0;
        for (JobType job : JobType.values()) {
            double posts = depot.getJobs(job);
            if (posts <= 0) continue;
            double wage = !today ? PayTier.wageOf(job) / unit
                    : job.ordinal() < wages.length ? wages[job.ordinal()] : 0;
            if (!(wage > 0)) return 0;
            bill += posts * wage;
        }
        return bill / points;
    }

    /** The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. */
    private double buildersSalesRate() {
        return economyManager.getTaxPolicy().effectiveSalesRate(getSectors().construction());
    }
    private double plantSalesRate() {
        return economyManager.getTaxPolicy().effectiveSalesRate(getSectors().materials());
    }

    /**
     * What a draw of material cost the builders, as they bill it on (0.7.19):
     * the salvage at what they paid for it, the plant's net of the credit
     * they claim on it, the world's at its landed cost (its tax is charged
     * and credited), all grossed up by the tax they remit on the bill. The
     * quote's allowance is the same arithmetic at the order's prices.
     */
    private double billableMaterial(double salvageCost, double plantCost, double importCost) {
        double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, buildersSalesRate()));
        double rM = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, plantSalesRate()));
        return (salvageCost + plantCost * (1 - rM) + importCost) / (1 - rB);
    }

    /**
     * THE INVOICE. The material is priced as if drawn today - the yard's
     * share free, the plant's at the market, the rest imported - and that is
     * what the order is charged up front. The crews then draw it month by
     * month as they build (see drawSiteMaterials()), and since 0.7.19 the
     * owner pays what it cost then, less what this allowed for it
     * (settleSiteContracts()). The work itself is its labour at today's
     * builders' wages and the rest of its cash cost, and the price carries
     * the sales tax the builders pass on. See THE BUILDERS' PRICE.
     */
    public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity) {
        return quoteBuild(selected, quantity, buildingManager.getConstructionMaterials());
    }

    /** ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). */
    private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds) {
        double needed = selected.constructionMaterials * (double) quantity;
        double yard = yardHolds;
        double beyondYard = Math.max(0, needed - yard);
        Markets.Draw boughtIn = getMarkets().quote(Good.MATERIALS, beyondYard, getSectors());
        return new BuildQuote(
                quantity,
                buildingManager.nonMaterialCost(selected) * quantity,
                needed,
                yard,
                boughtIn,
                getMarkets().get(Good.MATERIALS).getLocalPrice(),
                buildingManager.getConstructionMaterialPrice(),
                selected.getLandSqFt() * (double) quantity,
                landManager.getAvailableSqFt(),
                // The city's own order's wait (0.7.22, after the docs pass):
                // quoteMonths() exactly with no order set and no rush, and with
                // one, where the order would land in it (quoteCityMonths()).
                quoteCityMonths(selected, quantity),
                buildersSalesRate(),
                plantSalesRate());
    }

    /**
     * Months an order would take to finish (0.7.17): the wait every planner
     * reads for its lead time and order size - BuildingManager.waitFor(), the
     * order's points over the share of the site output it would get beside
     * everything on site, at the builders' site output after the repairs
     * with every post offered (getBuildingOutputAtEveryPost()). It was the
     * order's points over the builders' whole output, repairs included, as
     * though the order had them to itself. NaN when there is no site output.
     */
    public double quoteMonths(BuildingsTemplate template, int quantity) {
        double months = buildingManager.waitFor(template, quantity, getBuildingOutputAtEveryPost());
        return months == Double.MAX_VALUE ? Double.NaN : months;
    }

    /**
     * Months what is on site of one building would take to finish (0.7.20):
     * the same wait at the same output as quoteMonths(), with no order added
     * - BuildingManager.waitOnSite(). For the build card's "N on site", the
     * Needs-you line and the construction panel, so all three read the
     * definition the quote and the planners read. NaN when there is no site
     * output; 0 with nothing of it on site. Reads; changes nothing.
     */
    public double onSiteMonths(BuildingsTemplate template) {
        // ...through the one wait every screen reads since 0.7.22 (siteMonths()):
        // waitOnSite()'s, to the bit, while the player's hand is off the site.
        return template == null ? 0 : siteMonths(ConstructionControl.keyOf(template));
    }

    /**
     * ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on
     * site would take at today's queue - a stack's by its key, or a
     * demolition's - at the output the quote reads. The construction page,
     * the right panel, a build card's "N on site" and the Needs-you line all
     * read it (through onSiteMonths() for a building), so a city site the
     * player's order starves reads the same everywhere. With no order set
     * and no rush on it, waitOnSite()'s wait, to the bit. NaN when there is
     * no site output; 0 with nothing of it on site. Reads. See
     * BuildingManager, ONE WAIT FOR A SITE.
     */
    public double siteMonths(String key) {
        double months = buildingManager.siteMonths(key, getBuildingOutputAtEveryPost());
        return months == Double.MAX_VALUE ? Double.NaN : months;
    }

    /**
     * ...and what a city order of n of these would wait if placed now (0.7.22,
     * after the docs pass): quoteMonths() exactly with no order set and no
     * rush on its site; otherwise where the order would land in the
     * player's order - on its building's site where that stands in it, or,
     * with nothing of it on site, a new city site at the bottom. The build
     * quote's months. NaN when there is no site output. Reads.
     */
    public double quoteCityMonths(BuildingsTemplate template, int quantity) {
        double months = buildingManager.cityOrderWait(template, quantity, getBuildingOutputAtEveryPost());
        return months == Double.MAX_VALUE ? Double.NaN : months;
    }

    /**
     * Takes material for the month's building work or a repair: the city's
     * yard first, for free; then the materials plant, at the market price;
     * then the world. THE BUILDERS BUY IT - every draw is the construction
     * sector's purchase, booked in its ledger, credited at the plant's rate
     * or charged at its own, and billed on to whoever ordered the building
     * inside the order price - and since 0.7.19, for the sites' draw, what it
     * cost past what that price allowed for it, or refunded what it cost
     * less (settleSiteContracts()). See Markets.draw().
     *
     * AS THE WORK IS DONE, since 2026-09-11 - not the day the order is
     * placed. See BuildingsStacks.materialsOwed for what the order-day draw
     * did to the materials market, and drawSiteMaterials() below.
     *
     * @param yardFirst whether the city's yard is drawn before the market. A
     *                  repair's is; the sites' monthly draw is NOT - the yard
     *                  was delivered to the sites the day the order was placed
     *                  (see deliverYardToSites), because the quote priced it
     *                  as free and a yard promised to every order in turn was
     *                  promised several times over: measured, an order quoted
     *                  against a full yard, the yard drawn down by the orders
     *                  ahead of it, and the builders importing $4M of material
     *                  a month against $266k of work.
     * @return what the part beyond the yard came to
     */
    private Markets.Draw drawMaterials(double units, boolean yardFirst) {
        int wanted = (int) Math.round(units);
        if (wanted <= 0) return new Markets.Draw(0, 0, 0, 0, 0);
        int fromYard = yardFirst ? buildingManager.takeFromYard(wanted) : 0;
        double beyondYard = wanted - fromYard;
        // ...then the builders' own stock of scrapped plant's material, which
        // they have paid for already (0.7.8), before the plant and the world.
        double fromSalvage = getSectors().construction().takeSalvage(beyondYard);
        salvageUsedThisMonth += fromSalvage;
        beyondYard -= fromSalvage;
        Markets.Draw d = getMarkets().draw(Good.MATERIALS, getSectors().construction(), null,
                beyondYard, getSectors());
        materialsConsumed += wanted;
        monthlyMaterialImports += d.imported();
        monthlyMaterialImportBill += d.importCost();
        return d;
    }

    /**
     * The month's draw for the sites: what the work the crews just delivered
     * was owed in material, summed by BuildingManager.advanceConstruction()
     * over every stack in proportion to the points it advanced. Called by
     * SimulationEngine right after the sites advance, so the plant's sale
     * and the builders' purchase land in the month the work did, and the
     * market's next strike counts the draw as that month's demand - a
     * builder's demand for material is now the rate it builds at, which is
     * the figure a materials plant can be sized to.
     */
    public void drawSiteMaterials(double units) {
        siteDrawBillable = 0;
        if (!(units > 0)) return;
        ham.citybuildersim.sectors.Construction builders = getSectors().construction();
        double salvageBefore = builders.getSalvageCost();
        Markets.Draw d = drawMaterials(units, false);
        double salvage = Math.max(0, salvageBefore - builders.getSalvageCost());
        siteDrawBillable = billableMaterial(salvage, d.localCost(), d.importCost());
    }

    /**
     * What the month's site draw cost the builders, as they bill it on
     * (billableMaterial()): what the owners' escalation is struck against.
     * Set by drawSiteMaterials(), read by settleSiteContracts() a line later
     * in the same tick. Not saved.
     */
    private double siteDrawBillable;

    /* =====================================================================
       MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19)

       The escalation clause (see THE BUILDERS' PRICE). Each order's share of
       the month's work was taken off its contract on its stack
       (BuildingsStacks.Contract): the price it earned, the material units it
       drew and the allowance its quote had in it for them. Its owner pays
       its share of what the draw cost the builders, by units, less that
       allowance - or is refunded it - and the builders earn it, beside the
       work (Construction.recogniseEscalation()): revenue, taxed like the
       rest, and investment, as the work it pays for is.

       The CITY pays out of the treasury, on its own line
       (TreasuryLine.BUILDING_ESCALATION): discretionary, and owed to the
       builders as arrears when the ceiling has bound, because the material
       was used. A refund comes back into the treasury. Both are the city's
       capital spending, as the order was. A SECTOR pays out of its till as
       it paid for the building (sectorInvestor(): spentOnBuildings), and a
       sector that gets the tax on its buildings back - a business's credit,
       or (revised) a landlord's rebate on a new rental home - is recorded as
       having bought the month's work and its escalation from the builders,
       at the share of it that comes back, which is what the credit is
       struck on (Sector.recordCapitalPurchase()).
       ===================================================================== */

    /**
     * Settles the month's site work with the owners who ordered it. Called by
     * SimulationEngine right after the work is recognised, so the escalation
     * lands in the builders' ledger in the same month as the draw it pays for.
     */
    public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues) {
        if (dues == null || dues.isEmpty()) return;
        ham.citybuildersim.sectors.Construction builders = getSectors().construction();
        double units = 0;
        for (BuildingsStacks.Due d : dues) units += Math.max(0, d.units);
        double billable = Math.max(0, siteDrawBillable);
        for (BuildingsStacks.Due d : dues) {
            double material = units > 0 ? billable * Math.max(0, d.units) / units : 0;
            double escalation = material - d.allowance;
            if (!Double.isFinite(escalation)) escalation = 0;
            Sector owner = "City".equals(d.payer) ? null : getSectors().byKey(d.payer);
            double settled;
            if (owner == null) {
                if (escalation > 0) {
                    settled = treasuryPays(TreasuryLine.BUILDING_ESCALATION, escalation);
                } else {
                    settled = escalation;
                    cash -= escalation;
                }
                cityCapitalSpending += settled;
            } else {
                settled = escalation;
                sectorInvestor(owner.key()).spend(escalation);
                // ...and the share of the tax on it the owner gets back - a
                // business's credit, a landlord's rebate on a new rental home -
                // struck at the next strike, as the tax is paid (revised).
                if (d.recovered > 0) owner.recordCapitalPurchase(builders.key(), (d.revenue + escalation) * d.recovered);
            }
            builders.recogniseEscalation(settled);
        }
    }

    /**
     * The yard's share of a new order, delivered to the sites the day it is
     * placed - the units the quote priced as free (see quoteBuild), taken
     * off what the sites still owe so the monthly draws buy only the rest.
     */
    private double deliverYardToSites(BuildingsTemplate template, double needed) {
        int fromYard = buildingManager.takeFromYard((int) Math.round(needed));
        if (fromYard <= 0) return 0;
        buildingManager.deliverToSites(template, fromYard);
        materialsConsumed += fromYard;
        return fromYard;
    }

    /**
     * ...and the work those sites delivered is recognised in the same breath,
     * into the same ledger. The BUILDING output, not the gross: the crews
     * that spent part of the month on repairs did not spend it on sites - see
     * getBuildingOutput(). The statement struck at the top of next month
     * then carries the month's work and the month's material together. (The
     * payroll was scaled by the month's utilisation until 0.7.17; now the
     * builders lay off the crews the work does not need - sectors
     * .Construction, THE CREWS THE WORK NEEDS - and utilisation is only
     * the operations page's "Busy".)
     *
     * @param pointsBuilt     what the sites took of it (0.7.17): no site takes
     *                        more than it owes, and the rest was idle
     * @param pointsAvailable the month's site output
     */
    public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable) {
        getSectors().construction().recogniseWork(earned, pointsBuilt, pointsAvailable);
    }

    /* =====================================================================
       THE PLAYER'S HAND ON THE QUEUE (0.7.22)

       Jerus asked for a construction panel that is more than "a blue loading
       screen", then for "not only repirotize and cancel but also destroy
       buildings, like you yourself destroy buildings"; on 2026-09-30 he chose
       the mechanics: priority "Both", cancel "Keep the half-built shell",
       demolish "City's, plus buy-outs (Recommended)". The rules and their
       sources are ConstructionControl's (A to E); the crews are
       BuildingManager's (THE PLAYER'S HAND ON THE QUEUE there). This is the
       money, in two halves:

       BETWEEN THE PRESSES, the player's hand, as a build order is placed:
       a reorder and a rush (nothing moves), a cancel (nothing moves until the
       month's end), a restart (the city pays today's quote for the remaining
       work, as an order: TreasuryLine.BUILDINGS and the builders' book), a
       demolition (the same, for the demolition's quote; its buildings close
       as the next month starts, closeDemolished()), and a buy-out (the owner
       is paid the compensation - the ground on the land line and recorded as
       a buyback, the building and the business loss on the building line -
       and then the demolition). Each
       moves money from the treasury to a pool the audit reads - the builders'
       book or the owner's till - in the gap the game is played in.

       IN THE MONTH, settleConstructionControl(), off what the advance left:
       the overtime's premium paid to the builders and paid out as wages; a
       cancelled order's refund back out of their book; and a finished
       demolition's material sold to them by the 0.7.8 rule, with its ground
       released. All of it moves between pools the audit reads, inside its
       window, so the money audit closes as it did.
       ===================================================================== */

    /** What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). */
    public boolean isCitysToDemolish(BuildingsTemplate t) {
        return t != null && !t.isOwnedBySector() && !"Commercial Bank".equals(t.getName());
    }

    /** What the city may buy out and demolish: a building a sector owns. */
    public boolean isBuyOutable(BuildingsTemplate t) {
        return t != null && t.isOwnedBySector() && getSectors().byKey(t.getSector()) != null;
    }

    /**
     * A demolition, priced and laid out BEFORE the player commits: its work,
     * its price, what it closes, what comes back and when. Every figure the
     * staging card prints; books nothing.
     */
    public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price,
                                  double salvageUnits, double salvagePrice, double salvageAffordable,
                                  double landSqFt, double months, long homes, double households,
                                  double places, long posts, double runningCost) {
        /** What the builders would pay for the material today, for as much as their cash covers. */
        public double salvageProceeds() { return salvageAffordable * salvagePrice; }
    }

    /** A buy-out, priced: the compensation, part by part, and the demolition after it. */
    public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground,
                              double businessLoss, double profitShare, double operatingIncome,
                              double replacementMonths) {
        /** What the owner is paid. */
        public double compensation() { return buildingValue + ground + businessLoss; }
        /** ...and what it all costs the city, the demolition included. */
        public double total() { return compensation() + demolition.price().total; }
    }

    /**
     * D. The demolition's price: DEMOLITION_SHARE of the work in it at the
     * builders' rate for that building's work today
     * (BuildingManager.nonMaterialCost(), its labour at today's wages), no
     * material, and the builders' sales tax - THE BUILDERS' PRICE, through
     * the one BuildQuote every order is priced by.
     *
     * @param workPoints the points of work the buildings hold: the whole
     *                   template's for a standing building, a shell's progress
     */
    private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months) {
        double perPoint = t.getConstructionPoints() > 0
                ? buildingManager.nonMaterialCost(t) / t.getConstructionPoints() : 0;
        double sticker = ConstructionControl.DEMOLITION_SHARE * workPoints * perPoint;
        return new BuildQuote(1, sticker, 0, 0, new Markets.Draw(0, 0, 0, 0, 0), 0,
                buildingManager.getConstructionMaterialPrice(), 0, landManager.getAvailableSqFt(), months,
                buildersSalesRate(), plantSalesRate());
    }

    /** What demolishing n standing buildings of this kind would do and cost. Reads. */
    public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n) {
        if (t == null || n < 1) return null;
        double points = ConstructionControl.DEMOLITION_SHARE * t.getConstructionPoints() * (double) n;
        double months = demolitionMonths(t, n, points);
        BuildQuote price = demolitionPrice(t, t.getConstructionPoints() * (double) n, points, months);
        double units = t.getConstructionMaterials() * (double) n;
        double unitPrice = getMarkets().get(Good.MATERIALS).getLocalPrice();
        double affordable = unitPrice > 0
                ? Math.min(units, Math.max(0, getSectors().construction().getCash()) / unitPrice) : 0;
        long homes = (long) t.getDwellings() * n;
        double totalHomes = buildingManager.getTotalHomes();
        double occupancy = totalHomes > 0 ? Math.min(1, families.homesNeeded() / totalHomes) : 0;
        long posts = 0;
        double wages = 0;
        double[] rates = economyManager.getWageRates();
        for (JobType job : JobType.values()) {
            posts += (long) t.getJobs(job) * n;
            if (job.ordinal() < rates.length) wages += t.getJobs(job) * (double) n * rates[job.ordinal()];
        }
        double places = (t.getTeaches() != null && t.getTeaches() != EducationType.NONE)
                || (t.getCare() != null && t.getCare() != CareType.NONE)
                || (t.getSafety() != null && t.getSafety() != SafetyType.NONE)
                ? t.getCapacity() * (double) n : 0;
        return new DemolitionQuote(t, n, points, price, units, unitPrice, affordable,
                t.getLandSqFt() * n, months, homes, homes * occupancy, places, posts,
                t.isOwnedBySector() ? 0 : wages + t.getUpkeep() * n);
    }

    /** A shell's demolition: DEMOLITION_SHARE of the work it holds, the material it drew to salvage, its ground. Reads. */
    public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell) {
        BuildingsTemplate t = shell == null ? null : buildingManager.getTemplate(shell.templateId);
        if (t == null) return null;
        double points = ConstructionControl.DEMOLITION_SHARE * shell.progress;
        double months = demolitionMonths(t, shell.buildings, points);
        BuildQuote price = demolitionPrice(t, shell.progress, points, months);
        double units = Math.max(0, t.getConstructionMaterials() * (double) shell.buildings - shell.materialsOwed);
        double unitPrice = getMarkets().get(Good.MATERIALS).getLocalPrice();
        double affordable = unitPrice > 0
                ? Math.min(units, Math.max(0, getSectors().construction().getCash()) / unitPrice) : 0;
        return new DemolitionQuote(t, shell.buildings, points, price, units, unitPrice, affordable,
                t.getLandSqFt() * shell.buildings, months, 0, 0, 0, 0, 0);
    }

    /** Months a demolition of these points would take at today's queue, at the output the quote reads. NaN with no output. */
    private double demolitionMonths(BuildingsTemplate t, int n, double points) {
        double months = buildingManager.waitForDemolition(n, points, getBuildingOutputAtEveryPost());
        return months == Double.MAX_VALUE ? Double.NaN : months;
    }

    /**
     * E. A buy-out, priced: the building at its owner's own value of it, its
     * ground at the land market's price, and the business loss - its share
     * of the owner's buildings, at that value, of last month's operating
     * income, for the months a replacement would take at today's queue, and
     * nothing if that is a loss. Then the demolition. Reads; null if the
     * builders have no output to time a replacement by.
     */
    public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n) {
        if (!isBuyOutable(t) || n < 1) return null;
        Sector owner = getSectors().byKey(t.getSector());
        double each = t.getCashCost() + t.getConstructionMaterials() * buildingManager.getConstructionMaterialPrice();
        double buildingValue = each * n;
        double ground = landManager.priceFor(t.getLandSqFt() * n);
        double book = buildingManager.getBookValueBySector(owner.key());
        double share = book > 0 ? Math.min(1, buildingValue / book) : 0;
        double operating = owner.statement().operatingIncome;
        double months = quoteMonths(t, n);
        if (Double.isNaN(months)) return null;
        double loss = Math.max(0, share * operating) * months;
        return new BuyOutQuote(quoteDemolition(t, n), owner.key(), buildingValue, ground, loss, share,
                operating, months);
    }

    /** Why a demolition or a buy-out was not made, or null when it was. */
    private String lastHandRefusal;
    public String getLastHandRefusal() { return lastHandRefusal; }

    /** D. Demolishes n of the city's own standing buildings of this kind: pays the quote and puts the demolition on site; they close as the next month starts (closeDemolished()). */
    public boolean demolish(BuildingsTemplate t, int n) {
        lastHandRefusal = null;
        if (!isCitysToDemolish(t)) { lastHandRefusal = "not the city's to demolish"; return false; }
        if (n < 1 || demolishable(t) < n) { lastHandRefusal = "not that many standing"; return false; }
        DemolitionQuote q = quoteDemolition(t, n);
        if (q.price().total > cash) { lastHandRefusal = "the treasury is short of the quote"; return false; }
        placeDemolition(q, "City", 0);
        decisions.record(DecisionLog.CONSTRUCTION, "Demolished " + n + " " + t.getName()
                + " for " + DecisionLog.money(q.price().total));
        GameLog.note(String.format("The city ordered %d %s demolished, for $%,.0fk.", n, t.getName(),
                q.price().total));
        return true;
    }

    /** E. Buys n of a sector's buildings by compulsory purchase and demolishes them. */
    public boolean buyOutAndDemolish(BuildingsTemplate t, int n) {
        lastHandRefusal = null;
        if (!isBuyOutable(t)) { lastHandRefusal = "not a business's or a landlord's"; return false; }
        if (n < 1 || demolishable(t) < n) { lastHandRefusal = "not that many standing"; return false; }
        BuyOutQuote q = quoteBuyOut(t, n);
        if (q == null) { lastHandRefusal = "the builders have no output to time a replacement by"; return false; }
        if (q.total() > cash) { lastHandRefusal = "the treasury is short of it"; return false; }
        // The owner is paid: the ground as the land line pays for a plot a
        // business gives back (retire()), the building and the business loss
        // as a purchase of the city's. Its debts stay its own.
        Investor owner = sectorInvestor(q.sector());
        double ground = treasuryPays(TreasuryLine.LAND, q.ground());
        landManager.recordBuyback(t.getLandSqFt() * n);
        double rest = treasuryPays(TreasuryLine.BUILDINGS, q.buildingValue() + q.businessLoss());
        cityCapitalSpending += rest;
        owner.receive(ground + rest);
        ConstructionControl.Expropriation e = new ConstructionControl.Expropriation();
        e.month = month;
        e.templateId = t.getId();
        e.building = t.getName();
        e.buildings = n;
        e.sector = q.sector();
        e.buildingValue = q.buildingValue();
        e.ground = q.ground();
        e.businessLoss = q.businessLoss();
        e.months = q.replacementMonths();
        buildingManager.getControl().recordExpropriation(e);
        lastInvestment.put(q.sector(), String.format("Sold %,d %s to the city by compulsory purchase, for $%s",
                n, t.getName(), formatter.format(q.compensation())));
        placeDemolition(q.demolition(), q.sector(), q.ground());
        decisions.record(DecisionLog.CONSTRUCTION, "Bought out " + n + " " + t.getName() + " from " + q.sector()
                + " for " + DecisionLog.money(q.compensation()) + ", to demolish");
        GameLog.note(String.format("The city bought %d %s from %s for $%,.0fk and ordered them demolished.",
                n, t.getName(), q.sector(), q.compensation()));
        return true;
    }

    /** How many of this kind stand that no demolition has been ordered for. */
    public int demolishable(BuildingsTemplate t) {
        if (t == null) return 0;
        return Math.max(0, buildingManager.getQuantity(t.getId()) - buildingManager.getControl().closingOf(t.getId()));
    }

    /** The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). */
    private void placeDemolition(DemolitionQuote q, String from, double groundPaid) {
        BuildingsTemplate t = q.template();
        double paid = treasuryPays(TreasuryLine.BUILDINGS, q.price().total);
        cityCapitalSpending += paid;
        getSectors().construction().bill(paid, q.points());
        ConstructionControl.Demolition d = buildingManager.getControl().addDemolition(t, q.buildings(), q.points(),
                q.salvageUnits(), q.landSqFt(), paid, from, month, true);
        d.groundPaid = groundPaid;
    }

    /**
     * D. THE BUILDINGS CLOSE AS THE NEXT MONTH STARTS - the first the order
     * is in force for, since an order placed between two presses is the
     * coming month's - before anything in it reads the city: off the stack
     * they stood on, so their posts, their homes and their capacity are gone
     * from the month's first count. The families are arranged into the homes that stand at
     * the month's demographics; whoever does not fit is unhoused. No money
     * moves: the order was paid, and a bought building's owner paid, when it
     * was placed.
     */
    private void closeDemolished() {
        for (ConstructionControl.Demolition d : buildingManager.getControl().demolitions()) {
            if (!d.closing) continue;
            BuildingsTemplate t = buildingManager.getTemplate(d.templateId);
            int closed = t == null ? 0 : buildingManager.retire(t, d.buildings);
            d.closing = false;
            // In the log as the city's own order, and whose it was if it was bought.
            if (t != null) demolitionLog.record(t.getName(), closed,
                    "City".equals(d.from) ? "City" : "City, bought from " + d.from, month, d.groundPaid);
        }
    }

    /** C. A shell demolished: its quote paid, the shell gone, the demolition on site. */
    public boolean demolishShell(int templateId) {
        lastHandRefusal = null;
        ConstructionControl.Shell shell = buildingManager.getControl().shellOf(templateId);
        if (shell == null) { lastHandRefusal = "no shell of it"; return false; }
        DemolitionQuote q = quoteShellDemolition(shell);
        if (q == null) { lastHandRefusal = "no such building"; return false; }
        if (q.price().total > cash) { lastHandRefusal = "the treasury is short of the quote"; return false; }
        double paid = treasuryPays(TreasuryLine.BUILDINGS, q.price().total);
        cityCapitalSpending += paid;
        getSectors().construction().bill(paid, q.points());
        buildingManager.getControl().removeShell(shell);
        buildingManager.getControl().addDemolition(q.template(), shell.buildings, q.points(), q.salvageUnits(),
                q.landSqFt(), paid, "Shell", month, false);
        decisions.record(DecisionLog.CONSTRUCTION, "Demolished the " + q.template().getName() + " shell for "
                + DecisionLog.money(paid));
        return true;
    }

    /**
     * C. A shell's restart, priced: its remaining work at today's quote - the
     * non-material cost of the points it still owes, its labour at today's
     * builders' wages; the material it has still to draw, the yard's share
     * free and the rest at today's market; and the builders' tax - THE
     * BUILDERS' PRICE, as any order. Its ground is held already. Reads.
     */
    public BuildQuote quoteRestart(ConstructionControl.Shell shell) {
        BuildingsTemplate t = shell == null ? null : buildingManager.getTemplate(shell.templateId);
        if (t == null || t.getConstructionPoints() <= 0) return null;
        double pointsLeft = Math.max(0, shell.buildings * (double) t.getConstructionPoints() - shell.progress);
        double sticker = buildingManager.nonMaterialCost(t) * pointsLeft / t.getConstructionPoints();
        double needed = Math.max(0, shell.materialsOwed);
        double yard = buildingManager.getConstructionMaterials();
        Markets.Draw boughtIn = getMarkets().quote(Good.MATERIALS, Math.max(0, needed - yard), getSectors());
        return new BuildQuote(shell.buildings, sticker, needed, yard, boughtIn,
                getMarkets().get(Good.MATERIALS).getLocalPrice(), buildingManager.getConstructionMaterialPrice(),
                0, landManager.getAvailableSqFt(),
                restartMonths(t, shell.buildings, pointsLeft),
                buildersSalesRate(), plantSalesRate());
    }

    /** A restart's wait, by the one wait (BuildingManager.restartWait()). NaN with no output. */
    private double restartMonths(BuildingsTemplate t, int n, double pointsLeft) {
        double months = buildingManager.restartWait(t, n, pointsLeft, getBuildingOutputAtEveryPost());
        return months == Double.MAX_VALUE ? Double.NaN : months;
    }

    /** C. A shell put back on site for its remaining work, at today's quote, as an order of the city's. */
    public boolean restartShell(int templateId) {
        lastHandRefusal = null;
        ConstructionControl.Shell shell = buildingManager.getControl().shellOf(templateId);
        BuildQuote q = quoteRestart(shell);
        if (q == null) { lastHandRefusal = "no shell of it"; return false; }
        if (q.total > cash) { lastHandRefusal = "the treasury is short of the quote"; return false; }
        BuildingsTemplate t = buildingManager.getTemplate(templateId);
        double pointsLeft = Math.max(0, shell.buildings * (double) t.getConstructionPoints() - shell.progress);
        buildingManager.resumeShell(shell);
        // The yard's share to the sites now, free, as the quote priced it; the
        // rest drawn as the crews build - see processBuildOrder().
        double fromYard = deliverYardToSites(t, q.materialsNeeded);
        buildingManager.bookContract(t, "City", 0, q.total, Math.max(0, q.materialsNeeded - fromYard), q.allowance);
        treasuryPays(TreasuryLine.BUILDINGS, q.total);
        cityCapitalSpending += q.total;
        getSectors().construction().bill(q.total, pointsLeft);
        decisions.record(DecisionLog.CONSTRUCTION, "Restarted " + t.getName() + " for " + DecisionLog.money(q.total));
        GameLog.note(String.format("The city restarted %d %s, for $%,.0fk.", shell.buildings, t.getName(),
                q.total));
        return true;
    }

    /** C. A cancel of one of the city's own sites, at the month's end - or taken back before it. */
    public boolean cancelSite(String key, boolean cancel) {
        if (cancel && !buildingManager.isCitySite(key)) return false;
        if (cancel && buildingManager.getControl().demolitionOf(key) != null) return false;
        boolean was = buildingManager.getControl().isCancelling(key);
        buildingManager.getControl().setCancelling(key, cancel);
        if (was != buildingManager.getControl().isCancelling(key)) {
            decisions.record(DecisionLog.CONSTRUCTION, (cancel ? "Cancelled " : "Kept ")
                    + buildingManager.nameOfSite(key));
        }
        return true;
    }

    /**
     * C. What a cancel would hand back if the month ended now with nothing
     * more built: the city's contract left on the site - the work not done
     * and the material not drawn, with the sales tax on both - and its
     * material allowance. The month's work comes off it first; the panel
     * says so. {value, allowance}; zeros for a site that is not the city's.
     */
    public double[] cancelRefundNow(String key) {
        BuildingsStacks s = buildingManager.stackOfKey(key);
        if (s == null || !s.isCitysOwn()) return new double[] { 0, 0 };
        for (BuildingsStacks.Contract c : s.getContracts()) {
            if ("City".equals(c.payer)) return new double[] { c.getValue(), c.getAllowance() };
        }
        return new double[] { 0, 0 };
    }

    /** B. A rush on one of the city's own sites, on or off. */
    public boolean rushSite(String key, boolean on) {
        if (on && !buildingManager.isCitySite(key)) return false;
        boolean was = buildingManager.getControl().isRushed(key);
        buildingManager.getControl().setRush(key, on);
        if (was != buildingManager.getControl().isRushed(key)) {
            decisions.record(DecisionLog.CONSTRUCTION, (on ? "Rushed " : "Stopped rushing ")
                    + buildingManager.nameOfSite(key));
        }
        return true;
    }

    /** A. Moves one of the city's sites up (-1) or down (+1) its order, setting the order if none was. */
    public boolean moveSite(String key, int by) {
        java.util.List<String> order = buildingManager.cityOrder();
        int at = order.indexOf(key), to = at + by;
        if (at < 0 || to < 0 || to >= order.size()) return false;
        order.remove(at);
        order.add(to, key);
        buildingManager.getControl().setOrder(order);
        decisions.record(DecisionLog.CONSTRUCTION, buildingManager.nameOfSite(key)
                + (by < 0 ? " up" : " down") + " the city's order, to " + (to + 1));
        return true;
    }

    /** A. Back to the crews' rule. */
    public void resetSiteOrder() {
        boolean was = buildingManager.getControl().isPrioritySet();
        buildingManager.getControl().clearOrder();
        if (was) decisions.record(DecisionLog.CONSTRUCTION, "The city's sites back to the crews' rule");
    }

    /**
     * B. What a month on overtime would do on this site next month, at this
     * month's crews - the numbers the construction page shows before and
     * while the player rushes: {its month on overtime, its work over a
     * normal month's, the premium with the builders' tax in it}. Zeros for a
     * site that is not the city's. Reads.
     */
    public double[] rushPreview(String key) {
        if (!buildingManager.isCitySite(key)) return new double[] { 0, 0, 0 };
        ConstructionControl.Rush r = buildingManager.getControl().rushOf(key);
        int next = r == null ? 1 : r.months + 1;
        double share = buildingManager.plan(getBuildingOutput()).shareOf(key);
        double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, buildersSalesRate()));
        double premium = ConstructionControl.premiumShare() * share * buildingManager.buildersWagePerPoint() / (1 - rB);
        return new double[] { next, ConstructionControl.overtimeOutput(next), premium };
    }

    /* ----- in the month ----- */

    /** This month's events, kept for the screens and the inbox: what the advance left, settled. */
    private ConstructionControl.Events controlThisMonth = new ConstructionControl.Events();
    /** What the treasury paid the builders this month for overtime, with their tax in it. */
    private double overtimePaidThisMonth;
    /** What a finished demolition's material fetched this month, from the builders. */
    private double demolitionSalvageThisMonth;

    public ConstructionControl.Events getControlThisMonth() { return controlThisMonth; }
    public double getOvertimePaidThisMonth() { return overtimePaidThisMonth; }
    public double getDemolitionSalvageThisMonth() { return demolitionSalvageThisMonth; }

    /**
     * Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. Called by
     * SimulationEngine after the escalation, every month; a month with
     * nothing to settle only tells the builders they paid no overtime.
     */
    public void settleConstructionControl(ConstructionControl.Events ev) {
        ham.citybuildersim.sectors.Construction builders = getSectors().construction();
        controlThisMonth = ev == null ? new ConstructionControl.Events() : ev;
        overtimePaidThisMonth = 0;
        demolitionSalvageThisMonth = 0;

        // B. The overtime: the premium on the rushed sites' crews, priced with
        // the builders' tax in it as every price of theirs is, paid from the
        // treasury like any construction bill - and owed to them past the
        // ceiling - and paid out by them as wages, the whole premium, in the
        // mix of a depot's posts at today's wages.
        double premium = 0;
        for (ConstructionControl.Overtime o : controlThisMonth.overtime) premium += Math.max(0, o.premium());
        if (premium > 0) {
            double rB = Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, buildersSalesRate()));
            double paid = treasuryPays(TreasuryLine.BUILDING_OVERTIME, premium / (1 - rB));
            cityCapitalSpending += paid;
            builders.recogniseOvertime(paid);
            overtimePaidThisMonth = paid;
        }
        builders.setOvertimeWages(premium > 0 ? overtimeWagesByType(premium) : null);

        // C. The cancelled orders, stopped: the city's contract left comes
        // back out of the builders' book, the work it was for off their order
        // book's points.
        for (ConstructionControl.Refund r : controlThisMonth.refunds) {
            double back = Math.max(0, r.value());
            if (back > 0) {
                cash += back;
                cityCapitalSpending -= back;
            }
            builders.cancelOrder(back, r.points());
            r.shell().month = month;
            r.shell().refunded += back;
            GameLog.note(String.format("The city's %d %s stopped: $%,.0fk back, the shell kept.", r.buildings(),
                    r.building(), back));
        }

        // D. The demolitions finished: the material sold to the builders by
        // the 0.7.8 rule, the proceeds to the treasury, the ground freed.
        for (ConstructionControl.Completed c : controlThisMonth.completed) {
            ConstructionControl.Demolition d = c.site();
            double units = d.salvageUnits;
            double price = getMarkets().get(Good.MATERIALS).getLocalPrice();
            double bought = units > 0 && price > 0 ? Math.min(units, Math.max(0, builders.getCash()) / price) : 0;
            double paid = bought * price;
            if (paid > 0) {
                sectorInvestor(builders.key()).spend(paid);
                salvageBySector.merge(builders.key(), paid, Double::sum);
                cash += paid;
                treasuryJournal.record("Sold a demolition's material to the builders", paid);
                demolitionSalvageThisMonth += paid;
            }
            if (bought > 0) builders.addSalvage(bought, paid);
            salvageThisMonth.add(new Salvage("City", d.building, d.buildings, units, price, paid, bought, false));
            // ...and on the site's own event, which the inbox reads (0.7.22, after
            // the docs pass): two demolitions of one building and count finishing
            // in one month are two sales, not one read twice.
            c.sold(bought, paid);
            landManager.release(d.landSqFt);
            GameLog.note(String.format("Demolished %d %s: %,.0f units of material to the builders for $%,.0fk, %s freed.",
                    d.buildings, d.building, bought, paid, LandManager.areaWords(d.landSqFt)));
        }
    }

    /** The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). */
    private double[] overtimeWagesByType(double premium) {
        BuildingsTemplate depot = buildingManager.getTemplateByName("Construction Depot");
        double[] wages = economyManager.getWageRates();
        double[] out = new double[JobType.values().length];
        double bill = 0;
        for (JobType job : JobType.values()) {
            if (depot == null || job.ordinal() >= wages.length) continue;
            out[job.ordinal()] = depot.getJobs(job) * wages[job.ordinal()];
            bill += out[job.ordinal()];
        }
        if (!(bill > 0)) return null;
        for (int i = 0; i < out.length; i++) out[i] = premium * out[i] / bill;
        return out;
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
        BuildQuote quote = quoteBuild(selected, quantity);
        double totalMaterialsRequired = noConstruction ? 0 : quote.materialsNeeded;

        double totalCost = noConstruction
                ? selected.getCashCost() * quantity
                : quote.total;

        // 2. Set stats for the UI Receipt
        this.receiptMaterials = totalMaterialsRequired;
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
                    + LandManager.areaWords(landNeeded) + ", have "
                    + LandManager.areaWords(landManager.getAvailableSqFt()) + ")");
            return;
        }

        if (totalCost <= cash) {
            landManager.allocate(landNeeded);
            this.hasNewReceipt = true;
            this.receiptSerial++;
            lastBuildingName = buildingManager.getName(selected);
            lastBuildQuantity = quantity;

            // The order. The yard's share goes to the sites now, free, as the
            // quote priced it; the rest is drawn by the crews as they build -
            // see drawSiteMaterials() - at whatever it costs then, and since
            // 0.7.19 the city pays the difference from what the quote allowed,
            // or is refunded it (MATERIAL AT THE PRICE WHEN IT IS USED); the
            // builders carried it until then, as a fixed-price contractor does.
            buildingManager.addStack(selected, quantity, noConstruction);
            if (!noConstruction) {
                double fromYard = deliverYardToSites(selected, totalMaterialsRequired);
                // ...booked to the city, which claims no tax back - its rebate
                // is the tax coming home to the treasury as the builders remit
                // it (EconomyManager, THE REBATES ON A NEW HOME, AND THE
                // CITY'S) - with the material the crews will draw for it and
                // what the price allowed for that (0.7.19; see MATERIAL AT THE
                // PRICE WHEN IT IS USED).
                buildingManager.bookContract(selected, "City", 0, totalCost,
                        Math.max(0, totalMaterialsRequired - fromYard), quote.allowance);
            }

            // 4. Subtract everything at once
            treasuryPays(TreasuryLine.BUILDINGS, totalCost);
            cityCapitalSpending += totalCost;

            // ...and it lands somewhere now. The construction sector did the
            // work; this is what it gets paid for doing it - recognised as the
            // work is actually delivered, not all on the order month.
            //
            // Unless there was no work: an instant build skips the queue, so
            // paying construction for it would be revenue against nothing
            // delivered, and recogniseWork() would never have the points to
            // earn it back.
            if (!noConstruction) {
                // Earned as the work is delivered, material included - the
                // crews buy it as they go. See sectors.Construction.bill().
                getSectors().construction().bill(totalCost,
                        selected.getConstructionPoints() * (double) quantity);
            }

            System.out.println(quantity + " " + selected.getName()
                    + " construction started. $" + totalCost + " cash used");
        } else {
            // buildStack() only calls this once the price is covered, so this
            // branch is unreachable. It used to fall into quickIssueDebt(), a
            // console-era T-Bill issuer with its own stale pricing and no land
            // allocation; the JavaFX path offers the note BEFORE building, on
            // Build's funding page (BuildScreen.showBuildFunding()). Loud
            // rather than silent if that ever changes.
            throw new IllegalStateException("processBuildOrder() called with $"
                    + formatter.format(totalCost) + " due and $"
                    + formatter.format(cash) + " on hand");
        }
    }

    
    
    public enum BuildResult {SUCCESS, NEEDS_FUNDING, NO_LAND, NO_DEPOSIT, NO_LICENCE, FAILED, NO_COAST}

    /* =======================================================================
       HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12)

       An Engineering Services Office is seventy-eight licensed engineers. A
       firm opens when it can staff the core of its practice and hires or
       imports the rest, so the gate is half - and half rather than all because
       the Institute of Technology graduates over eighty-four months and a gate
       set at all seventy-eight would never open.

       Measured in SPARE licences, not licences: an engineer already working at
       the hospital is not available to this office.
       ======================================================================= */
    public static final double LICENCE_COVER_TO_OPEN = .5;

    /** Spare licences the city holds against what this order would need staffed. */
    public double licencesNeededFor(BuildingsTemplate template, int quantity) {
        if (template == null || template.getRequiresLicence() == null) return 0;
        return template.getLicensedPosts() * Math.max(1, quantity) * LICENCE_COVER_TO_OPEN;
    }

    /**
     * Can the city staff the core of this building's practice?
     *
     * True for every building that needs no licence, which is forty-six of the
     * forty-nine. See BuildingsTemplate.requiresLicence for why this is a hard
     * refusal and not left to the licence premium.
     */
    public boolean hasLicencesFor(BuildingsTemplate template, int quantity) {
        JobType licence = template == null ? null : template.getRequiresLicence();
        if (licence == null) return true;
        return populationManager.spareLicences(licence) >= licencesNeededFor(template, quantity);
    }

    /**
     * Mines standing, being built, or already ordered.
     *
     * A deposit supports one mine, and the count has to include work in progress
     * or the player could queue five mines against one deposit and have four of
     * them open onto nothing.
     *
     * IRON MINES ONLY since 0.7.62 (spec-land 2.4): an Oil Well is MINING too,
     * and stands on an oil site, not an iron one - see committedOn().
     */
    public int minesCommitted() { return committedOn(Resource.IRON); }

    /** ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site. */
    public int wellsCommitted() { return committedOn(Resource.OIL); }

    /**
     * The MINING buildings that stand on a resource's sites - standing, on
     * site or ordered (0.7.62): every MINING template whose good is the
     * resource's (siteOf()).
     */
    public int committedOn(Resource r) {
        int committed = 0;
        int[] underConstruction = buildingManager.getUnderConstructionById();
        for (BuildingsTemplate t : buildingManager.getTemplates()) {
            if (siteOf(t) != r) continue;
            committed += buildingManager.getQuantity(t.getId());
            if (t.getId() < underConstruction.length) {
                committed += underConstruction[t.getId()];
            }
        }
        return committed;
    }

    /**
     * The resource whose sites a building stands on (0.7.62): a MINING
     * template's, the resource whose good it makes - iron for an Iron Mine,
     * oil for an Oil Well; null for anything else. The map's rule
     * (BuildingVisual.of()).
     */
    public static Resource siteOf(BuildingsTemplate t) {
        if (t == null || t.getCategory() != BuildingType.MINING) return null;
        for (Resource r : Resource.values()) {
            if (r.good() != null && t.makes(r.good()) > 0) return r;
        }
        return null;
    }

    /**
     * Whether this order can go ahead on the ore the city owns.
     *
     * A mine needs ground with iron under it. Buying a land parcel with a
     * deposit is what unlocks one, which is the whole reason the listing has
     * deposits in it - and why the parcels that carry them cost more.
     *
     * BY ITS GOOD since 0.7.62: an Oil Well needs an unworked oil site and
     * oil left in the ground, as a mine needs iron (siteOf()).
     */
    public boolean hasDepositFor(BuildingsTemplate template, int quantity) {
        return hasDepositFor(template, quantity, 0);
    }

    /** ...with `before` more on the same resource's sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()). */
    private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before) {
        Resource r = siteOf(template);
        if (r == null) return true;
        return landManager.getSites(r) >= committedOn(r) + before + quantity
                && landManager.getRemaining(r) > 0;
    }

    /* -------------------------------------------------------------------
       THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3)

       A Water Treatment Plant treats the fresh water the city owns, and the
       plants together treat no more than it yields: the fresh cap,
       UtilitiesHandler.FRESH_UNITS_PER_KM2 a square kilometre of lake or
       river, plus the city's rights (below). Past it the plants' nameplate
       idles and the water is rationed through waterRatio, as any shortage
       is. A Desalination Plant draws the sea instead - the cap does not
       reach it - and it has to stand on the coast: an order for one with no
       owned sea is refused NO_COAST, before land and money, for the reason
       ore is: a plant with no sea to draw is not a funding problem.

       THE RIGHTS are what an older city already pumped past its lakes. A
       save from before 0.7.59 carries none, and is given the fresh plants'
       nameplate standing less what its lakes and river yield, never below
       nothing (spec-land 2.9), so loading idles nothing it had; a new city
       has none, and its fresh water is its lakes'. Saved, and never
       recomputed after: buying lakes adds to the cap on top of them.
       ------------------------------------------------------------------- */

    /** Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). */
    private double freshRights;

    /** The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). */
    public double getFreshRights() {
        return freshRights;
    }

    /** Harnesses and the load only: sets the rights, never below nothing. */
    void setFreshRights(double units) {
        this.freshRights = Math.max(0, units);
    }

    /** The fresh water limit, units a month: what the owned lakes and river yield plus the rights. */
    public double getFreshCap() {
        return UtilitiesHandler.FRESH_UNITS_PER_KM2 * landManager.getFreshKm2() + freshRights;
    }

    /**
     * The rights an older save is given (spec-land 2.9): the fresh plants'
     * nameplate standing less what the city's lakes and river yield, never
     * below nothing - raised by the last unit in the last place when the
     * limit they make (getFreshCap(), the yield plus them) would round under
     * that nameplate, so a loaded city's plants are not held back by a
     * rounding.
     */
    double derivedFreshRights() {
        double nameplate = buildingManager.getTotalByCategoryDouble(BuildingType.WATER,
                t -> t.isFreshWater() ? t.getProduction1() : 0);
        double yields = UtilitiesHandler.FRESH_UNITS_PER_KM2 * landManager.getFreshKm2();
        double rights = Math.max(0, nameplate - yields);
        while (rights > 0 && yields + rights < nameplate) rights = Math.nextUp(rights);
        return rights;
    }

    /** Whether this order can stand on the city's coast: true for everything but a desalination plant, which needs owned sea. */
    public boolean hasCoastFor(BuildingsTemplate template, int quantity) {
        return template == null || !template.isSeaWater() || landManager.getSeaKm2() > 0;
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

        BuildQuote quote = quoteBuild(template, quantity);

        double landNeeded = template.getLandSqFt() * quantity;

        if (!hasDepositFor(template, quantity)) {
            return false;   // no ground with ore in it to sell them
        }

        if (!hasCoastFor(template, quantity)) {
            return false;   // no sea to draw
        }

        if (!hasLicencesFor(template, quantity)) {
            return false;   // nobody licensed to practise in it
        }

        if (!landManager.canAllocate(landNeeded)) {
            return false;   // the city has no plot to sell them
        }

        // A business buys its plot from the city. This is the one part of a
        // private build the player actually receives.
        double landPrice = landManager.priceFor(landNeeded);

        double totalCost = quote.total + landPrice;

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
        double fromYard = deliverYardToSites(template, quote.materialsNeeded);
        // ...booked to the business that ordered it, with the share of the
        // tax on it that it gets back - a credit, or a landlord's rebate on a
        // new rental home, on the tax a building of it was charged (0.7.19;
        // see MATERIAL AT THE PRICE WHEN IT IS USED and
        // EconomyManager.taxRecoveredShare()).
        double taxPerBuilding = (totalCost - landPrice) * economyManager.buildersSalesRate() / Math.max(1, quantity);
        buildingManager.bookContract(template, payer.getName(),
                economyManager.taxRecoveredShare(payer.getName(), template, taxPerBuilding),
                totalCost - landPrice, Math.max(0, quote.materialsNeeded - fromYard), quote.allowance);

        // Construction is paid for the building work only - the land was the
        // city's, not theirs to be paid for. The material beyond the yard is
        // drawn as the crews build, see drawSiteMaterials().
        getSectors().construction().bill(totalCost - landPrice,
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

    /** One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. */
    public Investor getSectorInvestor(String sector) { return sectorInvestor(sector); }
    
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

        // ...and the sea before land and money, for the same reason (0.7.59):
        // a desalination plant with no coast is not a funding problem.
        if (!hasCoastFor(template, quantity)) {
            this.hasNewReceipt = false;
            return BuildResult.NO_COAST;
        }

        // Licences before land and before money, for the same reason as ore: a
        // firm with nobody to practise is not a funding problem, and offering a
        // bond to fix it would be a lie about what is wrong.
        if (!hasLicencesFor(template, quantity)) {
            this.hasNewReceipt = false;
            return BuildResult.NO_LICENCE;
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

    /**
     * What the treasury is short of for this order: its invoice less the cash
     * on hand, never below nothing. The figure the build screen's two offers
     * were sized to from 0.7.10 - the page used to work it out itself - until
     * 0.7.40 sized them to the whole run (buildFundingGap(Map), A RUN OF
     * ORDERS), which for a run of one is this figure.
     */
    public double buildFundingGap(BuildingsTemplate template, int quantity){
        return Math.max(0, calculateTotalCost(template, quantity) - cash);
    }

    /* -------------------------------------------------------------------
       A RUN OF ORDERS (0.7.40)

       Jerus: "when you click build all and you dont have the credit it
       just builds one not all". Build's "Build all three", its order bar
       and Enter place several orders one after another, and the screen
       offered a loan for the first one the cash could not cover, sized to
       that order alone; after it the rest of the run never happened. The
       funding page is sized to the run now, and these are what it reads.
       Pure reads: nothing is placed, booked or allocated.

       AN ORDER IN A RUN IS PRICED ON THE YARD THE ORDERS BEFORE IT LEAVE.
       Placing an order sends the yard's share of its material to its sites
       the same day (deliverYardToSites()), so the next order's quote has
       less of the yard to count free and buys more in: when the yard holds
       some of what the run needs but not all of it, the run costs more than
       its orders quoted one by one, BuildAdvice.quoteTotal(). The
       Overview's "all three" total added the quotes until 0.7.51 (its
       tooltip said "an earlier one's material from the yard can make a
       later one dearer"); it is this invoice now.
       Nothing else an order changes reaches the next one's price: the
       plant's stock is drawn by the crews month by month, not at the order,
       and the wages and the tax rates do not move. So buildRunInvoice() is
       what placing the run charges, order by order, to the last bit
       (BuildCardCheck, section 9, places one and sums what it was charged).

       ...AND CHECKED AS IT WILL BE PLACED. buildStack() refuses an order
       short of ore, of licences or of ground before it asks about money, so
       the city is never sold a loan for a building it has nowhere to put.
       buildRunAhead() walks the run in that order with what each order
       takes - its mines' deposits and its ground - counted against the next;
       licences are not taken by an order (a site employs nobody), so each is
       held against the same spare. The first that would be refused is where
       the run stops, and what it is refused for is buildRunStop().
       ------------------------------------------------------------------- */

    /** What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. */
    public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run) {
        int yard = buildingManager.getConstructionMaterials();
        double total = 0;
        for (java.util.Map.Entry<BuildingsTemplate, Integer> e : run.entrySet()) {
            if (e.getKey() == null || e.getValue() == null || e.getValue() <= 0) continue;
            BuildQuote q = quoteBuild(e.getKey(), e.getValue(), yard);
            total += q.total;
            // What deliverYardToSites() will take: BuildingManager.takeFromYard()'s own rule.
            yard -= Math.max(0, Math.min((int) Math.round(q.materialsNeeded), yard));
        }
        return total;
    }

    /** What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. */
    public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run) {
        return Math.max(0, buildRunInvoice(run) - cash);
    }

    /** How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, licences, ground - each placed after the ones before it. */
    public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run) {
        return runAhead(run, null);
    }

    /** What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_LICENCE or NO_LAND - or SUCCESS when every order passes. */
    public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run) {
        BuildResult[] stop = { BuildResult.SUCCESS };
        runAhead(run, stop);
        return stop[0];
    }

    private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop) {
        int ahead = 0;
        // ...the run's own orders on each resource's sites so far (0.7.62: iron and oil apart).
        int[] onSites = new int[Resource.values().length];
        double ground = 0;
        for (java.util.Map.Entry<BuildingsTemplate, Integer> e : run.entrySet()) {
            BuildingsTemplate t = e.getKey();
            int n = e.getValue() == null ? 0 : e.getValue();
            Resource site = siteOf(t);
            if (t != null && n > 0) {
                BuildResult refused = !hasDepositFor(t, n, site == null ? 0 : onSites[site.ordinal()]) ? BuildResult.NO_DEPOSIT
                        : !hasCoastFor(t, n) ? BuildResult.NO_COAST
                        : !hasLicencesFor(t, n) ? BuildResult.NO_LICENCE
                        : !landManager.canAllocate(ground + t.getLandSqFt() * (double) n) ? BuildResult.NO_LAND
                        : null;
                if (refused != null) {
                    if (stop != null) stop[0] = refused;
                    return ahead;
                }
                if (site != null) onSites[site.ordinal()] += n;
                ground += t.getLandSqFt() * (double) n;
            }
            ahead++;
        }
        return ahead;
    }

    /** What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. */
    public double cashShortfall() {
        return Math.max(0, -cash);
    }

    /** Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). */
    public double getMaterialsUsed(){
        return receiptMaterials;
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
         * rate fixed point and the build screen's note (the emergency path in
         * nextMonth(), until 0.7.0) cannot each have their own version of it -
         * which is exactly what they had.
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
         * the build screen's note borrows the exact shortfall, receives a few
         * hundred less than that, and buildStack() re-tests the price against
         * cash and refuses. Debt issued, nothing built.
         *
         * THE BONDS ARE NOT THE SAME BUG and are deliberately left alone. For a
         * serial bond the face value IS the request - the player asks for an
         * amount of paper and receives par less fees, which is what issuing at
         * par means. For a term bond the request is what the face is WORTH at
         * issue (faceValueOfLongBond() solves the face from it), and the city
         * receives that less fees. Either way the fees come out of what was
         * asked for, and that is what those quotes say. It is only the note
         * whose whole job is "how much paper do I need to RAISE this cash", and
         * only the note that was answering it wrong.
         *
         * ...until the build screen offered a term bond beside the note
         * (0.7.10), which asks the note's question of the long instrument. That
         * has its own solver, quoteLongBondForCash(), and quoteLongBond() is
         * still what the finance screen's term loan is.
         */
        // On the curve at its own months (0.7.1) - which, for a note of a
        // year or less, is the short end: no term premium, the T-bill rate.
        double rate = debtManager.quoteRate(requested,
                r -> Math.ceil(faceForNetProceeds(requested, r, months) / rounding) * rounding,
                months);

        double faceValue = Math.ceil(
                faceForNetProceeds(requested, rate, months) / rounding) * rounding;

        double received = netProceeds(faceValue, rate, months);

        /*
         * ...and the proceeds are rounded to the cent, which can still land a
         * fraction under the ask when the face lands exactly on a granule. A
         * quote that does not cover what was asked for is not a quote for it,
         * so it buys more granules until it does.
         *
         * IT COULD SPIN, AND IT DID. The old body added ONE granule a turn on
         * the argument that "each one adds at least rounding x 4.25% to the
         * proceeds, so this cannot spin". Both halves of that are wrong once
         * the city has real prices in it:
         *
         *   - netProceeds() rounds to the cent. If a granule is worth less
         *     than about 0.24 then 4.25% of it is under a cent, the rounding
         *     swallows the whole increment, and `received` never moves. That
         *     is not slow convergence, it is a loop with no exit.
         *   - and when the ask is large against the granule - which is what
         *     inflation does to every nominal figure in the game - it is
         *     ask/(granule x 4.25%) turns. Found when an experimental price
         *     index first reached 37x founding and a 4,002-month playtest
         *     stopped finishing: the JVM would not even answer a thread dump,
         *     because a counted loop like this never reaches a safepoint.
         *
         * So it jumps by however many granules the shortfall needs at the
         * worst-case yield, which lands in one or two turns, and it is hard
         * bounded regardless. MIN_PROCEEDS_PER_FACE is the 4.25% floor the
         * comment on faceForNetProceeds() derives: the discount is capped at
         * 95% and the spread is 0.75%.
         */
        for (int guard = 0; received < requested && guard < 64; guard++) {
            double shortfall = requested - received;
            double steps = Math.max(1,
                    Math.ceil(shortfall / (rounding * MIN_PROCEEDS_PER_FACE)));
            faceValue += rounding * steps;
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
        return issueNote(quote);
    }

    /**
     * The booking every note shares - handleTBillLogic()'s, and since 0.7.13
     * the rollover's, which books the quote it sized (issueForRollover()).
     * Every figure comes off the quote, its term in months.
     */
    private String issueNote(DebtQuote quote) {
        debtManager.addShortTermTBill(quote.faceValue(), quote.duration(), month)
                .markIssued(quote.cashReceived(), quote.marketRate());
        this.cash += quote.cashReceived();
       cityDebtRaisedThisMonth += quote.cashReceived();
       cityDiscountThisMonth += quote.faceValue() - quote.cashReceived();
        treasuryRaisedSoFar += quote.cashReceived();

        debtManager.updateInterest();
        decisions.record(DecisionLog.BORROWING, "Borrowed " + DecisionLog.money(quote.faceValue())
                + ": " + quote.duration() + "-month note");
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
        // Priced at its FINAL maturity on the curve (0.7.1): a serial bond's
        // last slice is the longest money in it.
        double annualRate = debtManager.quoteRate(faceValue, duration * 12);
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

        debtManager.addMediumTermBond(quote.faceValue(), duration * 12, month, quote.marketRate())
                .markIssued(quote.cashReceived(), quote.marketRate());
        this.cash += quote.cashReceived();
       cityDebtRaisedThisMonth += quote.cashReceived();
       cityDiscountThisMonth += quote.faceValue() - quote.cashReceived();
        treasuryRaisedSoFar += quote.cashReceived();

        debtManager.updateInterest();
        decisions.record(DecisionLog.BORROWING, "Borrowed " + DecisionLog.money(quote.faceValue())
                + ": " + duration + "-year serial bond");
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

        // Only the five (0.7.1): any other term is quoted nothing, and
        // handleLongBondLogic() says why. See LongTermBond.MATURITIES.
        if (amount <= 0 || !LongTermBond.isIssuable(duration)) {
            return new DebtQuote("Term", duration, 0, before, before, 0, 0, 0, 0);
        }

        /*
         * ON THE CURVE AT ITS DURATION (0.7.1). The rate the face is solved at
         * - and so the one longBondPvPerFace() and faceValueOfLongBond() are
         * handed, here and in the fixed point - is curveRate(duration * 12)
         * with the loan priced in: the dial, the credit spread, and the term
         * premium for those years less what the central bank's holdings
         * compress of it. The buyback prices the same paper at the same
         * curve for the months it has left (DebtManager.marketValue()), which
         * is what keeps RestructureCheck's round trip neutral by construction.
         */
        final double requested = amount;
        double marketRate = debtManager.quoteRate(requested,
                r -> faceValueOfLongBond(requested, duration, rounding, r), duration * 12);

        double faceValue = faceValueOfLongBond(requested, duration, rounding, marketRate);

        // What that face is actually worth today, less the underwriter and
        // bond counsel. The worth is the request before rounding; a little
        // above it after, because the face rounds UP to the instrument's
        // granularity and the proceeds follow the face.
        double received = longBondNetProceeds(faceValue, marketRate, duration);

        return longBondQuote(requested, duration, marketRate, before, faceValue, received);
    }

    /**
     * What the city actually banks for a long bond of this face: its worth at
     * the rate struck, less the fees, to the cent. The long bond's
     * netProceeds(), shared by both quotes so they cannot drift.
     */
    private double longBondNetProceeds(double faceValue, double marketRate, int duration) {
        return Math.round(
                (faceValue * longBondPvPerFace(marketRate, duration)
                        - costOfIssuance(faceValue)) * 100) / 100.0;
    }

    /** A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. */
    private DebtQuote longBondQuote(double requested, int duration, double marketRate,
                                    double before, double faceValue, double received) {

        // Yield curve: long money carries a lower coupon than the medium-term
        // market rate. Small monthly payments are the point of the instrument.
        double couponYield = longBondCouponYield(marketRate, duration);

        double monthlyInterest = (faceValue * couponYield) / 12;
        double totalCost = (faceValue - received) + (monthlyInterest * duration * 12);

        return new DebtQuote("Term", duration, requested, marketRate, before,
                faceValue, received, monthlyInterest, totalCost);
    }

    /** Books a long bond on exactly the terms quoted. */
    public String handleLongBondLogic(double amount, int duration, double rounding) {

        if (!LongTermBond.isIssuable(duration)) return LongTermBond.REFUSAL;
        DebtQuote quote = quoteLongBond(amount, duration, rounding);
        if (quote.isEmpty()) return "Nothing issued.";
        return issueLongBond(quote);
    }

    /**
     * The booking both long-bond issues share. Every figure comes off the
     * quote, as handleTBillLogic()'s do.
     */
    private String issueLongBond(DebtQuote quote) {

        // The coupon, not the market rate - a long bond's whole shape is a low
        // monthly payment bought with a redemption premium.
        debtManager.addLongTermBond(quote.faceValue(), quote.duration() * 12, month, quote.couponRate())
                .markIssued(quote.cashReceived(), quote.marketRate());
        this.cash += quote.cashReceived();
       cityDebtRaisedThisMonth += quote.cashReceived();
       cityDiscountThisMonth += quote.faceValue() - quote.cashReceived();
        treasuryRaisedSoFar += quote.cashReceived();

        debtManager.updateInterest();
        decisions.record(DecisionLog.BORROWING, "Borrowed " + DecisionLog.money(quote.faceValue())
                + ": " + quote.duration() + "-year term bond");
        return "Term bond issued.\n" + quote.summary();
    }

    /* -----------------------------------------------------------------------
       A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10)

       The build screen's second offer, beside its note. quoteLongBond()
       answers "what does this much paper cost": its amount is what the face
       is WORTH at issue, and the city banks that less the underwriter and
       bond counsel - right for the finance screen, where the player picks
       the size. The build screen asks the question the note answers
       (faceForNetProceeds()): how much paper must be sold for the cash that
       ARRIVES to pay for the order. Guessed - NewGameCheck's water plant
       asked for the gap and five per cent - it either falls short, and the
       order is refused with the debt already issued, or it overshoots and
       borrows money nobody needed.

       So the face is solved, the note's shape at the long bond's price:

           face x pvPerFace - FIXED - face x SPREAD  >=  cashNeeded

       pvPerFace is longBondPvPerFace(), the one the quote and the rate's
       fixed point price with. It is never below a third, because the coupon
       is never below a third of the market rate (longBondCouponYield()), so
       the denominator cannot reach zero in play; the guards are for
       fixtures. The coupon, the monthly bill, the cost of the credit and the
       booking are the finance screen's term loan's, through the same two
       helpers.
       ----------------------------------------------------------------------- */

    /** Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. */
    private double longBondFaceForProceeds(double cashNeeded, int duration,
                                           double rounding, double marketRate) {
        double perFace = longBondPvPerFace(marketRate, duration) - UNDERWRITING_SPREAD;
        if (perFace <= 1e-9) {
            return faceValueOfLongBond(cashNeeded + issuanceFee(), duration, rounding, marketRate);
        }
        return Math.ceil(((cashNeeded + issuanceFee()) / perFace) / rounding) * rounding;
    }

    /**
     * What a term bond whose CASH covers cashNeeded would cost. Books nothing.
     *
     * The quote's `requested` is the cash asked for, which is what the record
     * says that field is; its face is what the city will owe. Priced with
     * itself included, on the curve at its duration, exactly as
     * quoteLongBond() is - only the face each rate implies differs.
     */
    public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding) {

        priceTheDebtMarket();
        double before = debtManager.getRate();

        if (cashNeeded <= 0 || !LongTermBond.isIssuable(duration)) {
            return new DebtQuote("Term", duration, 0, before, before, 0, 0, 0, 0);
        }

        final double needed = cashNeeded;
        double marketRate = debtManager.quoteRate(needed,
                r -> longBondFaceForProceeds(needed, duration, rounding, r), duration * 12);

        double faceValue = longBondFaceForProceeds(needed, duration, rounding, marketRate);
        double received = longBondNetProceeds(faceValue, marketRate, duration);

        // The proceeds are rounded to the cent, so a face that lands exactly on
        // its solution can bank a fraction under the ask - the note's case,
        // met the note's way (quoteTBill()): as many granules as the shortfall
        // needs at what a granule of this face banks, and hard bounded.
        double perFace = longBondPvPerFace(marketRate, duration) - UNDERWRITING_SPREAD;
        for (int guard = 0; received < needed && perFace > 1e-9 && guard < 64; guard++) {
            faceValue += rounding * Math.max(1,
                    Math.ceil((needed - received) / (rounding * perFace)));
            received = longBondNetProceeds(faceValue, marketRate, duration);
        }

        return longBondQuote(needed, duration, marketRate, before, faceValue, received);
    }

    /** Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. */
    public String handleLongBondForCash(double cashNeeded, int duration, double rounding) {

        if (!LongTermBond.isIssuable(duration)) return LongTermBond.REFUSAL;
        DebtQuote quote = quoteLongBondForCash(cashNeeded, duration, rounding);
        if (quote.isEmpty()) return "Nothing issued.";
        return issueLongBond(quote);
    }

    /* =======================================================================
       BORROWING IN SOMEBODY ELSE'S MONEY
       =======================================================================

       Three instruments, the same three shapes, written in USD. What differs
       from the domestic pair above is small in code and enormous in play:

         - the rate comes off DebtManager.foreignRate(), which is the world's
           base plus what the world charges THIS city - and starts below the
           domestic floor - and since 0.7.2 off the world's CURVE at the
           paper's maturity (DebtManager.foreignCurveRate(): the same term
           premium table the city's curve carries), where it was flat at every
           maturity before
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
        // The player's choice; the forced default holds the log (checkForeignSolvency()).
        decisions.record(DecisionLog.BORROWING, "Defaulted on " + "US" + DecisionLog.money(written) + " of foreign debt");
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
     *
     * THE CASH IT READS IS ONE MONTH DEEP. It runs at the bottom of the month,
     * and since 0.7.0 the central bank advances the whole shortfall at the top
     * of every month, without refusal (settleTreasury()) - so this trips only
     * on a month whose own bills, a balloon falling due say, come to more than
     * a year of revenue. A long insolvency accumulates in the advances, which
     * this does not read. TODO(docs): whether it should - the advances, or the
     * advances past the ceiling - is a design question the code does not
     * answer; the rule is as it was.
     */
    private void checkForeignSolvency() {
        if (!debtManager.hasForeignDebt()) return;
        double annualRevenue = debtManager.getMonthlyTaxRevenue() * 12;
        if (annualRevenue <= 0) return;
        if (cash < -annualRevenue * DEFAULT_OVERDRAFT_YEARS) {
            // The city's fate, not the player's decision: not in the log (0.7.23).
            decisions.hold();
            try { defaultOnForeignDebt("the treasury could not find the dollars"); }
            finally { decisions.release(); }
        }
    }

    /**
     * The city's real rate against the world's, on today's figures (0.7.2):
     * the dial less the city's inflation - expected, since 0.7.42 (below) -
     * less the world's base rate less the
     * world's realised inflation - the one definition the month hands the
     * currency (nextMonth(), before the reprice) and the monetary page reads.
     *
     * EX ANTE SINCE 0.7.42: the city's side is the dial less EXPECTED
     * inflation (Expectations), the real rate a lender actually prices; the
     * year's inflation until then. An import spike under a credible bank no
     * longer cuts the real rate and pushes the currency down with it. The
     * world's side is unchanged.
     */
    public double realRateDifferential() {
        return (debtManager.getPolicyRate() - expectations.getExpectedInflation())
                - (DebtManager.WORLD_BASE_RATE - world.realisedInflation());
    }

    /**
     * What savers earn after inflation (0.7.3): the bank's deposit rate less
     * the inflation savers expect (Expectations, since 0.7.42 - the same
     * expected inflation the real rate differential reads; the year's, as
     * the parity reads it, until then) - the one definition the month strikes
     * the households' spend factor on and the monetary page prints. See
     * HouseholdBalance's banner AND WHAT IT SPENDS ANSWERS THE REAL RATE.
     */
    public double realDepositRate() {
        // Ex ante since 0.7.42: the deposit rate less what savers expect
        // inflation to be (Expectations), not last year's.
        return bank.depositRate() - expectations.getExpectedInflation();
    }

    /** The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. Pure. */
    public double realPolicyRate() {
        return debtManager.getPolicyRate() - expectations.getExpectedInflation();
    }

    /**
     * The share of their spending above subsistence the households plan at
     * today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on
     * realDepositRate(). Struck once a month, where the households plan
     * (syncHouseholdAccounts()); read between presses it is the factor the
     * next month's plan will be struck at, because neither the deposit rate
     * nor the price index moves until the month does.
     */
    public double spendFactor() {
        return HouseholdBalance.spendFactor(realDepositRate());
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

        // A dollar term loan is held to the same five maturities (0.7.1).
        if (requestedUsd <= 0 || !debtManager.foreignWindowOpen()
                || ("Term".equals(type) && !LongTermBond.isIssuable(duration))) {
            return new DebtQuote(type, duration, 0, before, before, 0, 0, 0, 0);
        }

        return switch (type) {
            case "Note" -> {
                double face = Math.ceil(requestedUsd / rounding) * rounding;
                double rate = selfPricedForeignRate(face, duration, r ->
                        new ShortTermTBill(face, duration, month, true));
                double gross = face * (1 - ShortTermTBill.discountFraction(rate, duration));
                double received = Math.round((gross - costOfIssuance(face)) * 100) / 100.0;
                yield new DebtQuote("Note", duration, requestedUsd, rate, before,
                        face, received, 0, face - received);
            }
            case "Serial" -> {
                double face = Math.ceil(requestedUsd / rounding) * rounding;
                // On the world's curve at its final maturity (0.7.2), as the
                // domestic serial is on the city's.
                double rate = selfPricedForeignRate(face, duration * 12, r ->
                        new MediumTermBond(face, duration * 12, month, r, true));
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
                // On the world's curve at its duration (0.7.2) - the rate the
                // buyback reads back for the months it has left
                // (DebtManager.marketValue()), so a round trip is neutral by
                // construction. It was the flat foreign rate at every term.
                double rate = selfPricedForeignRate(face, duration * 12, r ->
                        new LongTermBond(face, duration * 12, month, longBondCouponYield(r, duration), true));
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
     * THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's
     * curve, with this paper booked at that rate's coupon, reads that rate
     * back - DebtManager.quoteForeignRate(Debt, months), walked from the
     * principal-only quote until it stops moving. The service term reads the
     * coupon and the coupon is struck on the rate, so it is a fixed point, and
     * a contraction: a point of rate moves the premium by a small share of a
     * point.
     *
     * @param shape the paper as it would be booked at a given rate
     */
    private double selfPricedForeignRate(double face, int months,
                                         java.util.function.DoubleFunction<Debt> shape) {
        double rate = debtManager.quoteForeignRate(face, months);
        for (int i = 0; i < FOREIGN_QUOTE_ITERATIONS; i++) {
            double next = debtManager.quoteForeignRate(shape.apply(rate), months);
            if (Math.abs(next - rate) < 1e-13) return next;
            rate = next;
        }
        return rate;
    }

    /** The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. */
    private static final int FOREIGN_QUOTE_ITERATIONS = 50;

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
        if ("Term".equals(type) && !LongTermBond.isIssuable(duration)) return LongTermBond.REFUSAL;

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
        treasuryRaisedSoFar += local;

        /*
         * "Hold as reserves" is not a different kind of accounting. It is the
         * treasury taking the local money it just raised and buying foreign
         * currency back with it - which is precisely what parking the proceeds
         * abroad IS, and which the intervention screen has been able to do
         * since phase 2. One path, already audited, already harnessed.
         */
        // Held as reserves is part of this one decision, and says so on its line (0.7.23).
        if (holdAsReserves) {
            decisions.hold();
            try { buyForeignCurrency(local); } finally { decisions.release(); }
        }

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
        decisions.record(DecisionLog.BORROWING, "Borrowed US" + DecisionLog.money(quote.faceValue()) + " abroad: "
                + ("Note".equals(type) ? duration + "-month note"
                   : duration + "-year " + ("Serial".equals(type) ? "serial" : "term") + " bond")
                + (holdAsReserves ? ", held as reserves" : ""));
        return (holdAsReserves
                ? "Issued abroad; the dollars are in reserve.\n"
                : "Issued abroad and converted.\n") + quote.summary();
    }

    /**
     * The quote for whichever instrument, by the name the menus use.
     *
     * Mirrors the dispatch in the issuance screens so a screen can ask "what
     * would this cost" without knowing which instrument it is looking at.
     */
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

    /* -----------------------------------------------------------------------
       THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13)

       The build screen's offers are sized so the cash they bring covers a
       gap: the note by quoteTBill(), whose ask IS the cash, and the bond by
       quoteLongBondForCash(). The land office's dollar offers and the
       treasury's rollover ask the same question of the serial bond and of
       the three dollar instruments, whose quotes take a FACE
       (quoteMediumBond(), quoteForeign()). So the face is searched: estimated
       from what a unit of face banks at today's rate for its maturity, less
       the fixed fee, then as many granules more as the existing quote says
       it is still short - quoteLongBondForCash()'s shape, hard bounded. What
       comes back IS the existing quote at that face, so handleMediumBondLogic()
       and handleForeignLogic() on its requested() book exactly what it says:
       the price and the costs are the existing ones.
       ----------------------------------------------------------------------- */

    /** The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). */
    public static final double BUILD_NOTE_GRANULE = 1000;

    /** A serial bond whose CASH covers cashNeeded: quoteMediumBond() at the face that brings it. Books nothing. */
    public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding) {
        if (!(cashNeeded > 0) || !(rounding > 0)) return quoteMediumBond(0, duration, rounding);
        double perFace = 1 - UNDERWRITING_SPREAD;
        double face = Math.ceil(((cashNeeded + issuanceFee()) / perFace) / rounding) * rounding;
        DebtQuote quote = quoteMediumBond(face, duration, rounding);
        for (int guard = 0; quote.cashReceived() < cashNeeded && guard < 64; guard++) {
            face += rounding * Math.max(1,
                    Math.ceil((cashNeeded - quote.cashReceived()) / (rounding * perFace)));
            quote = quoteMediumBond(face, duration, rounding);
        }
        return quote;
    }

    /**
     * A dollar note, serial or term loan whose CASH covers cashNeededUsd:
     * quoteForeign() at the face that brings it, on the world's curve at its
     * maturity. Books nothing, and is empty with the window shut.
     */
    public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding) {
        if (!(cashNeededUsd > 0) || !(rounding > 0)) return quoteForeign(type, 0, duration, rounding);
        int months = "Note".equals(type) ? duration : duration * 12;
        double perFace = foreignProceedsPerFace(type, debtManager.foreignCurveRate(months), duration);
        double face = Math.ceil(((cashNeededUsd + issuanceFee()) / perFace) / rounding) * rounding;
        DebtQuote quote = quoteForeign(type, face, duration, rounding);
        for (int guard = 0; !quote.isEmpty() && quote.cashReceived() < cashNeededUsd && guard < 64; guard++) {
            double atQuote = foreignProceedsPerFace(type, quote.marketRate(), duration);
            face += rounding * Math.max(1,
                    Math.ceil((cashNeededUsd - quote.cashReceived()) / (rounding * atQuote)));
            quote = quoteForeign(type, face, duration, rounding);
        }
        return quote;
    }

    /** What a unit of a dollar instrument's face banks at this rate, net of the spread - never under MIN_PROCEEDS_PER_FACE, so the search above always moves. */
    private double foreignProceedsPerFace(String type, double rate, int duration) {
        double perFace = switch (type) {
            case "Note" -> 1 - ShortTermTBill.discountFraction(rate, duration);
            case "Term" -> longBondPvPerFace(rate, duration);
            default     -> 1;
        };
        return Math.max(MIN_PROCEEDS_PER_FACE, perFace - UNDERWRITING_SPREAD);
    }

    /** Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. */
    public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding,
                                       boolean holdAsReserves) {
        DebtQuote quote = quoteForeignForCash(type, cashNeededUsd, duration, rounding);
        if (quote.isEmpty()) {
            return debtManager.foreignWindowOpen() ? "Nothing issued."
                    : "No lender abroad will take this paper: " + debtManager.foreignWindowReason();
        }
        return handleForeignLogic(type, quote.requested(), duration, rounding, holdAsReserves);
    }

    /* =======================================================================
       ROLLING WHAT FALLS DUE (0.7.13)

       Jerus: "the game checks whats going to mature next month, and issues
       what the treasury is lacking ... either manual, aka you do it yourself,
       or that it defualts to same structure, or that it defualts to 12 month
       tbill". The setting and the ledger are Rollover's; what it reads and
       what it books are here, because both are the city's.

       WHEN: between two presses - the first thing nextMonth() does, before
       the calendar turns. That is where a player's own issue lands, so the
       paper settles to its buyers at this month's settle and its cash is in
       the opening pools of the audit window: one path, already audited. What
       it rolls is what this month's processAllDebts() will pay - the player
       is looking at month m, the maturity falls due in m + 1, and the
       proceeds sit in the treasury until it does.

       WHAT: rolloverPlan(), which the Finances hub prints (both borrow
       pages until 0.7.32) before it happens and rollMaturities() books - one
       function on one state, so the sentence on the screen is the issue at
       the press.
         - what falls due: Debt.principalDueNextMonth(), piece by piece, at
           home and abroad;
         - the netting: surplusOverLastYear(), the national accounts' own
           balance over the last Rollover.NETTING_MONTHS, less what the ledger
           says earlier rollovers netted in those months - and, since 0.7.14,
           what the city's fund took of it at a year end (one ledger,
           Rollover.noteFundTook()) and the dial's share of this year so far,
           which the fund's pay-in will take first (fundReservation(), nothing
           at a dial of 0); S is Rollover.netting() - never more than the
           cash, nor than falls due;
         - each piece's share of what falls due less S, pro rata, sized so
           its CASH covers it - Jerus's "issues what the treasury is
           lacking", which he chose over face for face knowing what it does
           (0.7.13, round 2) - through the existing quotes: quoteTBill(),
           quoteMediumBondForCash(), quoteLongBondForCash(),
           quoteForeignForCash(), at the build screen's granules,
           BUILD_NOTE_GRANULE for a note and BUILD_BOND_GRANULE for a bond.
           Paper sells under its face - a note by its discount, a term loan
           by its redemption premium, any of them by the underwriting spread
           and the fixed fee - so the face that raises the cash is more than
           the face falling due: rolling for cash capitalises the interest of
           the paper it replaces into the new principal, at every roll.
           Rollover's javadoc has what that did on a Lean city. The holders
           buy it as they buy any issue;
         - SAME_STRUCTURE: its own instrument, term and currency. A dollar
           piece rolls abroad in dollars, and with the window shut into local
           paper of the same term, which the log says. A serial's instalment
           rolls into a new serial of its ORIGINAL term: Jerus's "same
           structure" is the same instrument and term, and a term bond of the
           remaining life would be neither - term loans are issued only at
           LongTermBond.MATURITIES besides. A term loan whose own term is no
           longer one of those (an older save's) rolls into the nearest one;
         - TWELVE_MONTH_BILL: a local note of Rollover.BILL_MONTHS;
         - pieces rolling into the same paper are one issue, and an issue
           under minimumIssueSize() - the smallest deal worth arranging, which
           the Finances tab already holds a player to - is not arranged: its
           share is paid out of cash with the rest of the maturity;
         - since 0.7.15, what the central bank holds of what falls due is
           its own to roll: it takes that par of the new paper at issue, on
           top of what the market is sold, while its dial is at or above what
           it holds (THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE, below). So the
           issues are sized for what falls due less the par it would roll -
           the market's part - less S. S is struck on all of what falls due,
           as before, and (Jerus: "Surplus pays everyone") pays the market's
           part first and then the central bank's par, which rolls only what
           S left of it. With none of its in what falls due, all of it is the
           market's, to the byte.

       WHAT IT IS NOT: a treasury short of cash for its spending is the
       central bank's (settleTreasury(), unchanged); the surplus nets only up
       to the cash the treasury holds. Rollover's javadoc has the rest, and
       the source for the month the proceeds wait.
       ======================================================================= */

    private final Rollover rollover = new Rollover();

    /** The treasury's rollover: its setting, the ledger of what it netted, and its record. */
    public Rollover getRollover() { return rollover; }

    /** The setting, as the Finances tab's chips read it. */
    public Rollover.Mode getRolloverMode() { return rollover.getMode(); }

    /** ...and as they set it, applied at the next press. */
    public void setRolloverMode(Rollover.Mode mode) {
        Rollover.Mode was = rollover.getMode();
        rollover.setMode(mode);
        if (was != rollover.getMode()) {
            decisions.record(DecisionLog.BORROWING, switch (rollover.getMode()) {
                case MANUAL            -> "Rollover off: what falls due is paid from cash";
                case TWELVE_MONTH_BILL -> "Rollover on: everything into " + Rollover.BILL_MONTHS + "-month notes";
                default                -> "Rollover on: each piece into its own kind";
            });
        }
    }

    /**
     * The budget surplus the city ran over the last Rollover.NETTING_MONTHS,
     * in local money: the national accounts' balance month by month, as the
     * history keeps it (HistorySave's "surplus"), over as many months as the
     * city has lived if fewer. Negative is a deficit.
     */
    public double surplusOverLastYear() {
        double[] surplus = historySave.aligned("surplus");
        double sum = 0;
        for (int i = Math.max(0, surplus.length - Rollover.NETTING_MONTHS); i < surplus.length; i++) {
            if (!Double.isNaN(surplus[i])) sum += surplus[i];
        }
        return sum;
    }

    /** What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. */
    private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars) { }

    private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen) {
        if (mode == Rollover.Mode.TWELVE_MONTH_BILL) {
            return new RollsInto("Note", Rollover.BILL_MONTHS, false, false);
        }
        boolean abroad = paper.isForeign() && windowOpen;
        boolean atHome = paper.isForeign() && !windowOpen;
        if (paper instanceof ShortTermTBill) {
            return new RollsInto("Note", Math.max(1, paper.getDuration()), abroad, atHome);
        }
        int years = Math.max(1, (int) Math.round(paper.getDuration() / 12.0));
        if (paper instanceof MediumTermBond) return new RollsInto("Serial", years, abroad, atHome);
        return new RollsInto("Term", nearestTermMaturity(years), abroad, atHome);
    }

    /** The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. */
    static int nearestTermMaturity(int years) {
        int best = LongTermBond.MATURITIES[0];
        for (int m : LongTermBond.MATURITIES) {
            if (Math.abs(m - years) < Math.abs(best - years)) best = m;
        }
        return best;
    }

    /**
     * What the rollover will do at the next press, on the city as it stands:
     * what falls due, the year's surplus and what earlier rollovers netted
     * of it, S, and the issues. Books nothing. MANUAL nets and issues
     * nothing, but still says what falls due.
     */
    public Rollover.Plan rolloverPlan() {
        Rollover.Mode mode = rollover.getMode();
        double due = 0, abroad = 0;
        java.util.List<Debt> falling = new java.util.ArrayList<>();
        java.util.List<Double> owed = new java.util.ArrayList<>();
        // ...and of each piece, the central bank's par (0.7.15, round 2): what
        // it holds of it, which it rolls itself at issue unless it runs off.
        // See THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE.
        java.util.List<Double> its = new java.util.ArrayList<>();
        double itsDue = 0;
        for (Debt paper : debtManager.getDebt()) {
            double principal = paper.principalDueNextMonth();
            if (!(principal > 0)) continue;
            falling.add(paper);
            owed.add(principal);
            double c = centralBankShareOf(paper, principal);
            its.add(c);
            itsDue += c;
            due += principal;
            if (paper.isForeign()) abroad += principal;
        }
        // What it would roll - its par less what it holds past its dial - and
        // so the market's part: the rest, all of what falls due when it holds
        // none.
        double itsRolls = centralBankRolls(itsDue);
        double market = due - itsRolls;
        double surplus = surplusOverLastYear();
        // What earlier rollovers netted and the city's fund took of it (one
        // ledger since 0.7.14), and the dial's share of this year so far,
        // which the fund's year-end pay-in will take first (fundReservation():
        // nothing at a dial of 0, which is 0.7.13's netting to the byte).
        double used = rollover.usedInYear(month) + fundReservation();
        // S on all of what falls due. It pays the market's part first, and
        // what is left of it pays down the central bank's par (round 3, Jerus:
        // "Surplus pays everyone"), which then rolls only the rest.
        double netted = mode == Rollover.Mode.MANUAL ? 0 : Rollover.netting(surplus, used, cash, due);
        // All of it when S covers all of what falls due, so nothing is left
        // to roll to the last bit.
        double itsNetted = netted >= due ? itsRolls : Math.max(0, netted - market);
        double itsPar = itsRolls - itsNetted;

        java.util.List<Rollover.Issue> issues = new java.util.ArrayList<>();
        if (mode != Rollover.Mode.MANUAL && market > netted) {
            double toRoll = market - netted;
            boolean windowOpen = debtManager.foreignWindowOpen();
            java.util.Map<RollsInto, double[]> byPaper = new java.util.LinkedHashMap<>();
            for (int i = 0; i < falling.size(); i++) {
                RollsInto into = rollsInto(falling.get(i), mode, windowOpen);
                RollsInto key = new RollsInto(into.type(), into.term(), into.foreign(), false);
                double[] sum = byPaper.computeIfAbsent(key, k -> new double[3]);
                // The piece's market part: what falls due less what the central bank would roll of it.
                double part = itsRolls > 0 ? owed.get(i) - its.get(i) * (itsRolls / itsDue) : owed.get(i);
                sum[0] += part / market * toRoll;            // its share of the cash, pro rata
                sum[1] += 1;                                 // pieces
                sum[2] += into.atHomeForDollars() ? 1 : 0;   // dollar pieces rolled at home
            }
            double smallest = minimumIssueSize();
            for (java.util.Map.Entry<RollsInto, double[]> e : byPaper.entrySet()) {
                double[] sum = e.getValue();
                if (sum[0] < smallest) continue;
                RollsInto into = e.getKey();
                double face = localFace(into.foreign(),
                        rolloverQuote(into.type(), into.term(), into.foreign(), sum[0]));
                issues.add(new Rollover.Issue(into.type(), into.term(), into.foreign(),
                        sum[0], face, (int) sum[1], (int) sum[2]));
            }
        }
        return new Rollover.Plan(mode, due, abroad, surplus, used, cash, netted, issues, itsDue, itsPar, itsNetted);
    }

    /** The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. */
    private void rollMaturities() {
        // What the central bank rolls of its own at issue this month (0.7.15,
        // round 2), struck on the book the plan reads, before the rollover
        // books anything: the plan's figure, which S has already paid down;
        // by hand, where nothing is netted, its par less what it
        // holds past its dial. See THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE.
        centralBankAlone.clear();
        centralBankTender = 0;
        if (rollover.getMode() == Rollover.Mode.MANUAL) {
            centralBankTender = centralBankRolls(centralBankParFallingDue());
            return;
        }
        Rollover.Plan plan = rolloverPlan();
        centralBankTender = plan.centralBankPar();
        if (!(plan.due() > 0)) return;

        String here = getCurrency().qualifiedSymbol();
        GameLog.note(String.format("THE ROLLOVER, %s: %s%,.0fk falls due next month; last year's surplus"
                        + " nets %s%,.0fk of it; %s%,.0fk to roll.%s", Rollover.words(plan.mode()),
                here, plan.due(), here, plan.netted(), here, plan.toRoll(),
                plan.centralBankDue() > 0 ? String.format(" The central bank holds %s%,.0fk of it: the surplus"
                        + " pays %s%,.0fk of that, it rolls %s%,.0fk at issue, and %s%,.0fk runs off past its dial.",
                        here, plan.centralBankDue(), here, plan.centralBankNetted(), here, plan.centralBankPar(),
                        here, plan.centralBankRunsOff()) : ""));
        double raised = 0, faced = 0;
        int issued = 0, atHome = 0;
        for (Rollover.Issue issue : plan.issues()) {
            // Quoted again at the press, each issue after the one before it is
            // on the books: the plan quoted every issue on the books as they
            // stood, and it is this quote that is booked.
            DebtQuote quote = rolloverQuote(issue.type(), issue.term(), issue.foreign(), issue.cash());
            double before = cash;
            String booked = issueForRollover(issue, quote);
            double brought = cash - before;
            double face = brought > 0 ? localFace(issue.foreign(), quote) : 0;
            if (brought > 0) {
                raised += brought;
                faced += face;
                issued++;
                atHome += issue.atHomeForDollars();
            }
            GameLog.note(String.format("  rolled %s%,.0fk into %s, %s: %s%,.0fk of face raised %s%,.0fk. %s",
                    here, issue.cash(), issue.paper(), issue.why(plan.mode()), here, face, here, brought,
                    booked.replace('\n', ' ')));
        }
        if (plan.issues().isEmpty() && plan.toRoll() > 0) {
            GameLog.note(String.format("  nothing issued: %s%,.0fk is under the smallest deal worth arranging"
                    + " (%s%,.0fk), and is paid out of cash.", here, plan.toRoll(), here, minimumIssueSize()));
        }
        rollover.record(month, plan.due(), plan.netted(), faced, raised, issued, atHome);
        // None of the city's term paper sold between the presses for the
        // central bank's par to be added on to: its par alone (0.7.15, round 2).
        if (centralBankTender > 0 && !termPaperSoldBetweenPresses()) strikeParAlone();
    }

    /**
     * The paper whose CASH covers a rollover issue's share, on the existing
     * quotes, at the build screen's granules: a note by quoteTBill(), whose
     * ask is the cash; a serial by quoteMediumBondForCash(); a term loan by
     * quoteLongBondForCash(); dollar paper by quoteForeignForCash(), its
     * share in dollars at the day's rate - the rate the dollar paper falling
     * due is valued at. Books nothing.
     */
    private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash) {
        double granule = "Note".equals(type) ? BUILD_NOTE_GRANULE : BUILD_BOND_GRANULE;
        if (abroad) return quoteForeignForCash(type, cash / foreign.getRate(), term, granule);
        return switch (type) {
            case "Note"   -> quoteTBill(cash, term, granule);
            case "Serial" -> quoteMediumBondForCash(cash, term, granule);
            default       -> quoteLongBondForCash(cash, term, granule);
        };
    }

    /** A rollover quote's face in local money: a dollar quote's at the day's rate. */
    private double localFace(boolean abroad, DebtQuote quote) {
        if (quote == null || quote.isEmpty()) return 0;
        return abroad ? quote.faceValue() * foreign.getRate() : quote.faceValue();
    }

    /**
     * One of the rollover's issues, booked on exactly the terms of its quote
     * (rolloverQuote()). Dollar paper is converted, not held: what falls due
     * is paid out of the treasury's cash at the day's rate
     * (repayForeignPrincipal()).
     */
    private String issueForRollover(Rollover.Issue issue, DebtQuote quote) {
        if (quote == null || quote.isEmpty()) return "Nothing issued.";
        double granule = "Note".equals(issue.type()) ? BUILD_NOTE_GRANULE : BUILD_BOND_GRANULE;
        // The player's setting at work, not a decision: the issue is not in the log (0.7.23).
        decisions.hold();
        try {
            if (issue.foreign()) {
                return handleForeignLogic(issue.type(), quote.requested(), issue.term(), granule, false);
            }
            return switch (issue.type()) {
                case "Note"   -> issueNote(quote);
                case "Serial" -> handleMediumBondLogic(quote.requested(), issue.term(), granule);
                default       -> issueLongBond(quote);
            };
        } finally {
            decisions.release();
        }
    }

    
    //printers
    private void printPopulationInfo(){
        populationManager.printPopulationInfo();
    }
    
    /** Every sector's operations page, as text - the same lines the screen draws. */
    private void printSectorInfo(){
        for (Sector s : getSectors().all()) {
            System.out.println("=== " + s.key() + " ===");
            for (Sector.Line line : s.operations(this)) {
                switch (line.kind()) {
                    case HEAD -> System.out.println("-- " + line.label());
                    case NOTE -> System.out.println("   " + line.label());
                    default   -> System.out.printf("   %-40s %s%n", line.label(), line.value());
                }
            }
        }
    }
    
    private void printUtilityInfo(){
        servicesManager.printUtilityInfo();
    }
    
    private void printCityStats(){
        economyManager.printCityStats();
    }

    
    
    
    private void nextMonth() {
        /*
         * THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced
         * (0.7.42, THE ANCHOR): at the expected price level the last month
         * ended on, so the month lives at one level from here to the next
         * press - and whatever is read between the presses, EARNED and a
         * build card and a quote, is at the month's level like every other
         * price. See Expectations, THE MONTH STRIKES ITS CONSTANTS AT ITS TOP.
         */
        expectations.strikeLevel();
        restrikeMoneyConstants();
        /*
         * WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns -
         * in the gap between two presses, where a player's own issue lands,
         * so it settles to its buyers and crosses the audit window exactly as
         * one does. See ROLLING WHAT FALLS DUE.
         *
         * ...AFTER THE CITY'S FUND HAS TAKEN THE DIAL'S SHARE OF A YEAR'S
         * SURPLUS (0.7.14): at the year's end, before the rollover nets - the
         * player's explicit choice before the automatic netting. Nothing at a
         * dial of 0. See fundYearEnd().
         */
        fundYearEnd();
        rollMaturities();

        updateConstructionCost();

        /*
         * A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an
         * emergency note for the gap, six months, sold to the bank. It is the
         * central bank's advance now, drawn further down, once the month's
         * audit window has opened - see settleTreasury() and THE CENTRAL BANK
         * AND THE TREASURY, which keeps the note's history.
         */
        month++;
        monthsSinceAutosave++;
        // The buildings the player ordered demolished between the presses
        // close now, as the month after the order starts - the first it is
        // in force for - before anything in it reads the city (0.7.22; THE
        // PLAYER'S HAND ON THE QUEUE). No money moves. None in a city that
        // ordered none.
        closeDemolished();
        // The bank's clock, for its preferred's anniversaries, and the fund's
        // month (0.7.14).
        bank.setMonth(month);
        fund.startMonth();

        // The register's month: nothing offered, nothing paid, until it is.
        equity.startMonth();
        exchange.startMonth(month);
        bondMarket.startMonth();
        economyManager.clearEquityFlows();

        // The autosave is written at the bottom of the month (0.7.52), once the
        // month is recorded - see THE AUTOSAVE HOLDS A WHOLE MONTH, below.

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
        /* ===================================================================
           THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)

           Jerus, on the central bank that is coming: "the central bank would
           buy gbonds or sell gbonds from thin air". It cannot trade paper
           with a commercial bank that never paid for the paper it holds -
           and this one never had. Every bond and bill the city sells lands
           between two presses (the finance screen, the build screen's
           funding, and until 0.7.0 the emergency note), so it is on these two counters
           when the month begins. The settlement snapshot was taken further
           down, after the bank's tax and AFTER the clear, so the bank was told
           about zero every month: its book rose by the face at the next
           refresh, it collected the coupons and the principal, and it never
           handed over a dollar. Its equity rose by the face of every issue,
           from nothing, and the treasury's cash came from nowhere.

           SNAPSHOTTED FIRST, THEN CLEARED - here, rather than clearing later,
           so the two lines that decide what the bank pays sit beside the
           clear that would otherwise eat them, and the order is the
           explanation. The bank hands over the cash at the settle below,
           exactly as it does for a business loan - since 0.7.1 for what the
           households did not take, and earning its discount as it accretes
           rather than booking it there (THE DISCOUNT ACCRETES, below). Until
           it has, what it owes for the paper is carried against its pool in
           MoneyAudit (getCityPaperUnsettled()), because the treasury already
           has the money.
           =================================================================== */
        cityDebtRaisedForBank = cityDebtRaisedThisMonth;
        cityDiscountForBank = cityDiscountThisMonth;
        cityDebtRaisedThisMonth = 0;
        cityDiscountThisMonth = 0;
        cityPrincipalRepaidThisMonth = 0;
        bankPrincipalRepaidThisMonth = 0;
        // The holders' month (0.7.1): what the households were paid on their
        // paper and paid for it. See THE HOLDERS ARE PAID.
        couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;
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
        salvageThisMonth.clear();
        salvageBySector.clear();
        salvageUsedThisMonth = 0;
        double[] poolsBefore = MoneyAudit.pools(this);
        double pooledBefore = 0;
        for (double p : poolsBefore) pooledBefore += p;
        double interestDue = economyManager.getInterestAccrued();

        bank.startMonth();
        foreign.startMonth();
        centralBank.startMonth();

        /*
         * A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1). The treasury
         * paid its holders outside the pools when the player pressed the
         * button, and the pools have carried what it paid them since
         * (MoneyAudit.pools()); this month's window is where that money is
         * seen to leave - to the households, abroad, and to the central bank,
         * which destroys its share now and takes its gain against face into
         * this month's profit. See Game.repurchaseDebt().
         */
        buybackToHouseholds = buybackToHouseholdsUnsettled;
        buybackAbroad = buybackAbroadUnsettled;
        buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;
        centralBank.settleRedemptions();

        /*
         * THE AUTOPILOT (0.7.0), before anything is priced: with the rule's
         * hand on the dial, the dial goes where the rule says - DebtManager
         * .advisedPolicyRate() on the year's inflation - and holds where it
         * is until there is a year of prices to read. Jerus's toggle; the
         * player's hand on the dial turns it off (DebtManager.takeTheDial()).
         */
        if (debtManager.isAutopilot() && priceIndex.hasRate()) {
            debtManager.setPolicyRate(debtManager.advisedPolicyRate(priceIndex.inflation()));
        }

        // The central bank settles with the treasury: last month's profit
        // remitted, the advances' interest charged, repaid from cash above
        // zero or advanced the shortfall. Inside the audit's window, so every
        // dollar it makes or destroys is one the month declares. See
        // settleTreasury().
        settleTreasury();

        // The central bank's own maturing paper, replaced at issue by its
        // add-on to what the city sold between the presses, par for par
        // (0.7.15, round 2): after the settle, so its money waits for the
        // maturity as the market's does; before the holdings step, which then
        // reads a book carrying the replacement. See THE CENTRAL BANK ROLLS
        // ITS OWN, AT ISSUE.
        rollCentralBankAtIssue();

        // The holdings dial (0.7.1): the central bank buys or sells the city's
        // term paper toward its target, after the settle above and before the
        // market is priced. See THE HOLDINGS DIAL, AT THE TOP OF THE MONTH.
        openMarketOperation();

        /*
         * THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1). The
         * households' ledger is told the grant at the top of the month
         * (updateHouseholdAccounts(), through HouseholdAccounts
         * .setOutsideMoney()), and the treasury used to pay it at the bottom -
         * a bill struck in the middle of the month on the students the month
         * then enrolled. So the students were credited last month's payment
         * while the treasury paid this month's: a month apart, and invisible
         * to the audit because households are outside its pools (the
         * bill-and-credit lag on the list since the-central-bank-opens.md
         * section 4). Struck and paid here now, on the students the month
         * opens with - the ones the ledger credits a few lines on - so the
         * figure the treasury pays, the one the students are credited and the
         * audit's "- e StudentGrants" are one month's one figure. The arrears
         * rule is unchanged: back pay first, then the bill, both as far as
         * discretionaryRoom() allows.
         */
        payStudentGrants(studentGrantBill());
        /*
         * ...AND EI, THE SAME WAY (0.7.3). The out of work are credited at the
         * top of the month (HouseholdAccounts.setOutsideMoney(), a few lines
         * on) and the treasury used to pay at the bottom, off a bill struck in
         * the middle on the pool the month then produced - so the households
         * were credited last month's bill while the treasury paid this
         * month's, the lag batch C fixed for the grant and batch D's list
         * found here. Struck again and paid here now, on the pool the month
         * opens with, at the dial as the player left it: the figure the
         * treasury pays, the one the out of work are credited, the budget's
         * EI line and the audit's "- e EiBenefits" are one month's one figure.
         * A promise, paid whatever it takes, as it was at the bottom.
         */
        payEiBenefits();

        /*
         * THE BANK PAYS ITS PROFIT TAX, on the month that has just finished.
         *
         * Here, at the top, and not with the rest of the bank's settlement at
         * the bottom - because the city's tax take is struck inside
         * finalUpdateEconomy(), which runs before the bank knows what it made.
         * See Bank.chargeTax() for why arrears is the only ordering that keeps
         * the money in one channel.
         *
         * At the RETAIL rate: a Commercial Bank is a commercial building and
         * its counters are retail's money. Since the sector template the
         * policy's offsets are a map by name, so a dial of its own would be
         * one more key rather than a resized array - but the bank is not a
         * Sector (it has no goods and its own books), and giving it a policy
         * row without a Sector behind it is a decision for another day.
         */
        economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));

        startOfMonthUpdate();
        simulationEngine.simulateMonth(this);
        finalUpdateEconomy();
        economyManager.setPreviousGdp(historySave);
        priceTheDebtMarket();
        // The paper sold between the presses settles to its holders: the
        // households first, before the month's coupon (0.7.1). The bank pays
        // for the rest below. See THE HOUSEHOLDS TAKE THEIR SHARE.
        if (settleProbeForTest != null) settleProbeForTest.accept(false);
        householdsTakeTheirShare();
        if (settleProbeForTest != null) settleProbeForTest.accept(true);
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
        // The residual buyer (0.7.1): what the households did not pay for.
        cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper;
        bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper);
        // ...and the fee on what it lent the businesses, which they were
        // handed their loans net of (0.7.7): pool to pool, so it cancels.
        bank.takeLoanFees(lender.getFeesThisMonth());
        bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);
        /*
         * THE DISCOUNT ACCRETES (0.7.1). Face less what the bank paid is its
         * interest, and it belongs to the paper's whole life: this is the
         * month's accretion on the bank's share of every piece, struck in
         * DebtManager.processAllDebts() - non-cash, and internal, because it
         * is the city that is paying it. The bank carries what it has not yet
         * earned against its book (Bank.setUnearnedDiscount(), at the
         * refresh below), so its equity does not jump the month the paper
         * settles. Until 0.7.1 the whole discount landed here in the settle
         * month: $60M of "interest" in one month on seed 0 borrowing at home,
         * taxed, 45% of it payable to savers (the-bank-that-never-paid.md
         * section 5). legacyDiscountDue is a 0.7.0 save's paper, saved between
         * its issue and its settle and carrying no discount of its own, which
         * that save's bank books whole here as it would have.
         */
        bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);
        legacyDiscountDue = 0;
        // Settled: the bank has paid for the paper, so it owes nothing for it
        // and MoneyAudit's closing pool is its cash. See THE BANK PAYS FOR THE
        // CITY'S PAPER at the top of the month.
        cityDebtRaisedForBank = 0;
        cityDiscountForBank = 0;

        double sectorInterestPaid = 0;
        for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;
        // The city's coupons and the businesses' interest, kept by who paid
        // them since 0.7.9 - the same cash and income as their sum, to the bit.
        // Less the coupons on the businesses' bonds (0.7.12), which are their
        // holders': the bank takes its own beside the loans' interest, and the
        // market pays the rest at its step.
        bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());
        bank.takeBondCoupons(bondMarket.getCouponsDueToBank());

        refreshBank();
        // ...and what it sets aside against it, now every loan and write-off
        // of the month is on the books (0.7.8). See provideForLosses().
        provideForLosses();

        /*
         * AND THE BANK PAYS FOR ITS OWN MONEY.
         *
         * Last, after every loan has moved and the position has been re-read,
         * because what it needs to fund is whatever it is short by once the
         * month is done. Before the audit looks, because the money it raises
         * is made by the central bank at the window (it came from outside the
         * city until 0.7.0) and the audit has to see it made.
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
        // ...and what its branches cost to run, the ones standing now (0.7.19),
        // the charter exempt from the operating cost (revised), with what a
        // later branch carries of it for the branch rule to read.
        bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,
                economyManager.bankOperatingCost(buildingManager.countByName("Commercial Bank")),
                economyManager.bankOperatingCostPerLaterBranch());
        bankMaintenanceDue = 0;

        /*
         * Capital for whatever branches opened this month. Shareholders' money,
         * from outside the city - a bank with no capital has no capacity, and a
         * bank with no capacity can never earn any, so without this a first
         * branch could never begin lending.
         */
        capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));

        /* =================================================================
           AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD.

           The other half of CapitalFlows, and Jerus's diagnosis of why the
           currency runs away: "the issue is trade surplus... aka supply v
           demand... we later just have to add carry trade, where foreign
           borrow from the bank and convert to usd to do stuff with it, aka
           effectively having outflow of currency... its basically the opposite
           of hot money."

           The city ends every run as a funding currency - half a point against
           a world base of two - with a bank holding a hundred billion of
           deposits and a book of nothing. So a foreigner borrows local here,
           sells it for dollars, and earns the world's rate on them. Selling the
           local currency to do it is the outflow the surplus has never had.

           WHY HERE, and not beside hot money at the bottom of the month:

             - the loan must be on the bank's book BEFORE fundToCover() prices
               what the bank's own money costs, or the funding cost is struck
               against a balance sheet that does not exist yet
             - the border crossing must be INSIDE the audit's window, which is
               exactly what hot money spent its whole life outside of
             - and refreshBank() has already run, so headroom() is net of every
               domestic borrower this month. That is Jerus's "domestic first":
               the carry trade gets what is left, never what somebody here
               wanted.

           The rate they pay is the bank's price for them - since 0.7.7 its
           costs at RISK_CARRY with no expected loss (Bank.carryRate(), and
           WHAT A LOAN COSTS); the risk-free plus the bank's strain premium,
           through lendingRate(), before it.

           THE RISK-FREE IS THE POLICY RATE since 0.7.0, which is what the
           bank's reserves earn at the central bank - nothing lends below it.
           It was the city's own paper rate, debtManager.getRate(), which had
           the bank's strain premium already inside it (DebtManager.priceAt()
           added it), so lendingRate() added the premium a second time on top:
           a strained bank quoted the carry trade, and priced its own deposit
           bid against, a rate carrying the premium twice. The city's paper is
           priced off the policy rate now too (DebtManager.floorRate()), so
           the two agree on what money costs before anybody's credit is priced.
           ================================================================= */
        double carryRate = bank.carryRate(debtManager.getPolicyRate());
        double carryMoved = hotMoney.carryTakeMonth(
                carryRate,
                DebtManager.WORLD_BASE_RATE,
                debtManager.countryPremium(),
                bank.headroom());

        if (carryMoved > 0)      bank.lendCarry(carryMoved);
        else if (carryMoved < 0) bank.repayCarry(-carryMoved);
        bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));

        // The book just moved, so the capacity every line below reads has to be
        // the one that includes it - strain, and what funding costs.
        refreshBank();

        /*
         * WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as
         * a function until 0.7.7, for the deposit rate's bid for hot money
         * (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with no
         * caller left). The bank chooses its rate
         * by how it is funded now, and the bid went with the rule - see Bank,
         * WHAT TO PAY SAVERS.
         */
        bank.fundToCover(debtManager.getPolicyRate());

        /*
         * ...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two
         * figures so the two cannot disagree: the policy rate on its reserves,
         * paid in money made; the window's interest, paid back and destroyed;
         * and the window itself brought to what the bank now owes past its
         * deposits - advanced if it reached further, repaid if it came back.
         * A city with no branch has no bank at the window (its lending is
         * strangers', priced by fundsTransferPrice() as though it were the
         * window's money since 0.7.7 - see fundToCover()), so the window is
         * closed to it.
         */
        centralBank.payInterestOnReserves(bank.getPlacementIncome());
        centralBank.chargeWindow(bank.getFundingCost());
        centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);

        // ...and if that left it owing more than it owns, it has failed - and
        // waits for the city, which resolves it now if its setting is
        // automatic (0.7.14; resolveIfAutomatic()).
        bank.resolveIfFailed();
        resolveIfAutomatic();

        // The month is final, so the figure next month's tax is charged on is
        // final too. Carried in the save - see Bank.getProfitLastMonth().
        bank.closeMonth();

        // ...and the central bank's: its profit struck, to be remitted at the
        // top of next month, or its loss carried. See CentralBank.closeMonth().
        centralBank.closeMonth();

        /*
         * ...AND THE PRICE OF MONEY IS FINAL WITH IT.
         *
         * closeMonth() strikes what the month's funding actually cost, and
         * every rate in the city is floored on that - so it has to reach the
         * debt market HERE, at the close, and not wait for the top of next
         * month. A save is taken after this line and a load re-derives from
         * the state it wrote, so a live city that was still quoting off last
         * month's close would be quoting something its own reload does not.
         *
         * Measured before this existed: the live city quoted 1.30% for a loan
         * and the same city, reloaded from its own save a line later, quoted
         * 1.29%. See Bank.closeMonth() and BankCheck's "...and so does what
         * the bank is charging for money".
         */
        pushCostOfFundsToTheDebtMarket();
        debtManager.updateInterest();

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
            for (String s : Sectors.KEYS) {
                totalSectorCash += Math.max(0, economyManager.getSectorCash(s));
            }
            if (totalSectorCash > 0) {
                for (String s : Sectors.KEYS) {
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
        tradeShares();
        // ...and the city's preferred (0.7.14): every block at its third
        // anniversary repaid whole at par with its unpaid dividends, from the
        // bank's capital over its target and the rest by an offering of new
        // common to the public; the warrants bought back once none is left;
        // any still out at their expiry exercised. See settleThePreferred().
        // It sits after payDividends(), so the capital over its target that it
        // repays from is what the month's distributions left, and after
        // tradeShares(), so the warrants' value and an expiring warrant's
        // exercise read the price the month's trading struck.
        settleThePreferred();
        /*
         * ...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their
         * holders, last month's orders withdrawn, every bond valued and every
         * participant's orders posted again - after the shares, and before
         * the sectors move their money abroad, so a company's bid is paid from
         * its till first. The last thing in the month to move the bank's
         * bonds, so its book is re-read straight after. See BondMarket, THE
         * ORDERS ARE GOOD FOR A MONTH.
         */
        bondMarket.takeMonth(month, exchange.cityMarketValue(equity));
        strikeBankBonds();

        outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,
                foreign.getRate(), economyManager);
        // ...and the households, by the same rule, with what the owners were
        // just paid. See HouseholdBalance.investAbroad().
        householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());
        // ...and the city's paper, when the spread that brought them in has
        // gone (0.7.1): home to the bank's desk, a little a month.
        householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());

        /*
         * ...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS
         * MONTH (0.7.8). The owners' dividend, the desk's trades and re-mark
         * and the paper bought from the households all move the bank's equity
         * after resolveIfFailed() above, so a failure they caused showed a
         * month late - resolved at the next month's close, after a month of
         * lending as a standing bank. Asked again here, inside the audit's
         * window - where a resolution's valuation is declared ("+ bank
         * OwnersWipedAbroad" since 0.7.14; until then the hole itself, "+ bank
         * ResolutionLoss") - and before anything reads the bank for next
         * month. The hot money below moves its cash and what it owes
         * together, so it cannot break it.
         * ...AND RESOLVED THE SAME MONTH WHEN THE TREASURY'S SETTING IS
         * AUTOMATIC (0.7.14), and a standing bank under its minimum asks the
         * city for preferred (considerPreferredOffer()).
         */
        bank.resolveIfFailed();
        resolveIfAutomatic();
        considerPreferredOffer();

        /* =================================================================
           AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD.

           MOVED HERE 2026-09-12, and the reason is the whole point. This block
           used to sit at the very bottom of the month, after the currency had
           repriced, on the argument that "a carry trader reads the month that
           closed, not the one in progress". The argument was good and the
           position was wrong, because down there it is OUTSIDE THE AUDIT:

             - bank.startMonth() zeroes hotMoneyIn and hotMoneyOut
             - MoneyAudit.strike() reads them, and got zero every time
             - receiveHotMoney/returnHotMoney set them, after the strike

           So both directions read zero for the whole life of the mechanic, and
           the old comment three lines below the call - "it crosses the city's
           edge to get there, so MoneyAudit sees both directions" - was false.
           Hot money has never reached the balance of payments, pressure(),
           financialTrailing or financialGrossTrailing.

           AND THE CASH WAS OUTSIDE EVERY WINDOW TOO. poolsBefore is re-read
           fresh at the top of the next month, so the bank-cash move landed in
           the gap between strike(N) and poolsBefore(N+1). The two errors
           cancelled exactly, the residual stayed at $0.00, and 4,002 months of
           a cent-level audit never flagged it. See MoneyAudit's note on the
           cross-month guard that now closes that gap.

           WHAT IT COSTS TO MOVE IT: the currency has not repriced yet, so the
           panic terms - the year's depreciation and the reserves behind the
           stock - are read as the month OPENED rather than as it closed. That
           is still "the month that closed" in the sense the old comment meant;
           it is just the previous one. A trader acts on the last published
           rate, not on one that has not been struck.
           ================================================================= */
        double hotBefore = hotMoney.getStock();
        hotMoney.setMonth(month);
        hotMoney.takeMonth(
                bank.depositRate(),
                // The city's rate: there is no strain premium in it to take
                // back off since 0.7.7 (it was getRateBeforeStrain(), 0.7.2).
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
         * THE MONEY MOVES FOR REAL, and now it moves where the audit can see
         * it. It lands in the bank's cash, because that is what funding IS, and
         * it crosses the city's edge to get there. A stop is the bank's cash
         * leaving. Deposits in and cash in together, so the bank's equity does
         * not move on a flow that is somebody else's money either way.
         */
        double hotMoved = hotMoney.getStock() - hotBefore;
        if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);
        else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);
        bank.setForeignDeposits(hotMoney.getStock());

        lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);
        // Declared (0.7.14): the valuation a resolution wiped off the world's
        // shares is a month's, whether the month's or a press's before it.
        ownersWipedAbroadThisMonth = 0;

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
         * LEVELS, so the inflations beside it - the drift's until 0.7.2, the
         * real rate differential's since - have to be struck from what the two
         * levels are DOING - and the world's headline rate is not that; the
         * level is pulled back to trend and the headline is not. See
         * WorldEconomy.realisedInflation() for what handing it the headline
         * did to the exchange rate, and through it to every exporter.
         */
        foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),
                priceIndex.inflation(), world.realisedInflation());
        /*
         * ...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN
         * REAL TERMS (0.7.2).
         *
         * The city's real rate against the world's: the policy rate less the
         * inflation the city expects (Expectations, since 0.7.42; the year's
         * inflation setParity() was just handed until then), against the
         * world's base rate less the world's realised inflation. This is the
         * channel that makes the dial
         * a defence: raise it past inflation and the currency is supported
         * because money comes to be lent here, at the cost of every borrower
         * in the city paying more. Asia 1997, as a lever. It was the NOMINAL
         * differential until 0.7.2, and a city inflating at 40% with its dial
         * at 25% read as paying 23 points over the world when it was paying
         * 15 under it (ForeignAccounts, THE REAL RATE, NOT THE NOMINAL).
         */
        foreign.setRealRateDifferential(realRateDifferential());
        // ...and the anchored drift, the credible part of expected inflation
        // against the world's (0.7.42; ForeignAccounts, THE ANCHORED DRIFT).
        foreign.setExpectedDrift(anchoredDrift());
        foreign.repriceCurrency();
        /*
         * ...and what the vault's dollars fetched, if the reprice defended the
         * currency: the central bank's equity line, booked here, after the
         * audit, because no pool moves on it - a capital transaction against
         * the world, the vault down and M0 where it was (CentralBank, THE
         * DEFENCE).
         */
        centralBank.dollarsSold(foreign.getDefenceLocal());

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
        /*
         * ...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same
         * place (2026-09-21): the vault is kept in dollars, so the reprice just
         * moved its local value, and this books the move as a revaluation - not
         * cash, not an audit flow. See ForeignAccounts.revalueVault().
         */
        foreign.revalueVault();
        debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());
        debtManager.ageForeignStanding();
        checkForeignSolvency();

        /*
         * ...AND WHAT THE MONTH COST A FAMILY.
         *
         * Priced on the month that has closed, from the prices it actually
         * charged and what households actually paid for each - groceries and
         * rent until 0.7.43, and since then a meal out, a piece over a luxury
         * counter and the fees they are billed as well, on a basket chained
         * every ten years (PriceIndex, WHAT IS IN THE BASKET). Read at the top
         * of NEXT month by the wage drift, which is what makes the wage-price
         * loop a loop with a lag in it rather than a simultaneous equation.
         */
        priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);
        /*
         * ...AND WHAT IT EXPECTS PRICES TO DO (0.7.42, THE ANCHOR), straight
         * after the index it reads: credibility against the target, the lean
         * the rate showed against the miss, expected inflation and the level -
         * and the investors' and the currency's reads of it handed on. Read
         * by the whole of next month, whose first statement strikes the money
         * constants at the level (restrikeMoneyConstants()). See Expectations.
         */
        // The lean is measured against what holding the target takes - the
        // Standard rule's advice and the neutral rate - at any strictness
        // (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's own.
        double target = debtManager.getInflationTarget();
        expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),
                debtManager.neutralRate(),
                debtManager.holdingRate(priceIndex.hasRate() ? priceIndex.inflation() : target));
        handOnExpectations();

        // A year of the rate, so next year can tell a drift from a run.
        rateHistory[month % 12] = foreign.getRate();
        if (rateHistoryFilled < 12) rateHistoryFilled++;

        takeTreasuryMonth();

        dataSave.setCash(cash);

        /* =================================================================
           NOTHING AFTER THE AUDIT MAY MOVE A POOL.

           Added 2026-09-12. The audit reconciles WITHIN a month: pooled after
           minus pooled before, against inflows minus outflows. So money that
           moves after the strike is invisible to it twice over - missing from
           the flows, and already in the pools by the time the next month reads
           them. The two errors cancel, the residual stays at $0.00, and 4,002
           months of a cent-level audit never notices.

           That is precisely where hot money lived for the whole life of the
           mechanic. It has been moved above the strike; this is what stops the
           next thing from landing in the same place.

           IT CANNOT BE A CROSS-MONTH CHECK, and that was the first attempt.
           Comparing one month's close with the next month's open flags the
           PLAYER: an advisor or a person placing a build order between turns
           legitimately moves the treasury's cash into the builders' order book,
           which showed up as fifty-four findings in one run. The gap between
           months is where the game is played. The gap between the strike and
           the bottom of this method is where nothing should happen.

           Everything below the strike is bookkeeping - the balance of payments,
           the currency, the revaluation, the price index, the treasury's own
           record - and none of it is allowed to touch a pool.

           AND IT IS THE LAST THING IN THE METHOD, which the first version was
           not. Placed mid-tail it measured only as far as itself, and a
           deliberate re-break of the hot-money ordering walked straight past it
           reading $0.00 - the guard was evaded by the very bug it was written
           for. A guard that is not last does not guard the end.
           ================================================================= */
        postAuditDrift = 0;
        if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {
            double[] nowPools = MoneyAudit.pools(this);
            double[] atClose = lastMoneyAudit.poolsAtClose;
            if (nowPools.length == atClose.length) {
                double worst = 0;
                String which = "";
                for (int i = 0; i < nowPools.length; i++) {
                    double moved = nowPools[i] - atClose[i];
                    if (Math.abs(moved) > Math.abs(worst)) {
                        worst = moved;
                        which = MoneyAudit.POOL_NAMES[i];
                    }
                    postAuditDrift += moved;
                }
                postAuditDriftPool = which;
                postAuditDriftWorst = worst;
            }
        }

        printEndOfTurn();
        recordMonth();
        // The city map takes the month's buildings (0.7.60): THE CITY MAP. Moves no pool.
        reconcileMap();

        /*
         * THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52). It was written near the
         * top of this method until 0.7.51, after the calendar had turned and
         * before anything in the month had run, so the file carried month N
         * on a city that had only finished N - 1, and a history that ended at
         * N - 1. A city loaded from it pressed on to N + 1: month N never ran,
         * and its history skipped it (Jerus's autosave, saved by 0.7.49:
         * history to 1850 at month 1851, then 1852). Here, after
         * recordMonth(), the counter and every month-end record agree, as
         * they do for a save made between the presses - which is where the
         * two other autosaves, "before skipping" (simulateMonths()) and "on
         * quit" (toggleQuit()), were always written. The save moves no pool.
         * SaveSlotCheck holds it. An autosave written before 0.7.52 is left as
         * it is: its month's top had run (the fund's year end, the rollover,
         * the startMonth()s above), and would run again if the counter were
         * put back, so it loads at its month with that month gone from its
         * history.
         */
        if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {
            autosave("month " + month);
        }
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
         * ...and wages start chasing what the basket now costs.
         *
         * The price index is the cost of living - a real basket since phase 5
         * (PriceIndex; the exchange rate stood in for it before), chained
         * since 0.7.43. See LabourMarket.updateCostOfLiving() for why all of
         * it, and why slowly.
         */
        /*
         * ...half of it from what people expect, once the basket is based
         * (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE.
         */
        if (priceIndex.isBased()) {
            labourMarket.updateCostOfLiving(priceIndex.getIndex(), expectations.monthlyExpected());
        } else {
            labourMarket.updateCostOfLiving(priceIndex.getIndex());
        }
        /*
         * ...AND THE FLOOR HOLDS ITS WORTH. The minimum wage is a standard of
         * living now, so the cash figure is restruck from the index every month
         * rather than sitting where the player last typed it while prices moved
         * out from under it. See LabourMarket.cashMinimumWage().
         */
        /*
         * THE FLOOR NEEDS NO SEPARATE INDEXATION. baseWage() already multiplies
         * it by costOfLiving, which updateCostOfLiving() has just walked toward
         * this month's prices - a twenty-fourth of the way before the basket is
         * based, and since 0.7.42 half on what people expect and half a
         * forty-eighth of the gap after it - so the floor
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
         * What the money costs the bank at all goes to the city's paper first:
         * the city's rate may not go under it (DebtManager.floorRate(); see
         * Bank.marginalCostOfFunds()). Until 0.7.7 the bank's strain premium
         * was set on the debt market here too, before anything re-priced.
         */
        pushCostOfFundsToTheDebtMarket();
        // A failed bank lends nothing. With no branches there is no bank and
        // the lending comes from outside the city, priced by the same four
        // costs with the window's money as its funding - that path stays
        // open; it is the frozen INSTITUTION that is shut.
        economyManager.getBusinessDebtManager().setLendingOpen(
                !(bank.getBranches() > 0 && bank.isInsolvent()));
        /*
         * ...AND HOW FAR A STANDING ONE WILL LEND ON ITS CAPITAL (0.7.8): the
         * bank's one rule, read once and handed to every desk that lends
         * this month - the businesses' and the families' - before any of
         * them writes a loan. Free at or over its target; a borrower's debt
         * growing at most lendingGrowthLimit() under it; only what keeps
         * existing borrowers going under the minimum. See Bank, WHAT IT
         * LENDS. The carry trade reads the same rule through headroom().
         */
        // ...its bonds and its book's concentration re-read first (0.7.12),
        // so the rule and the prices below read the book as it stands.
        strikeBankBonds();
        double growth = bank.lendingGrowthLimit();
        boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();
        // ...the insured mortgages with the rest while the leverage ratio
        // binds (0.7.11, round 2): then a mortgage uses the capital the bank
        // is short of like any loan (BusinessDebtManager, THE LANDLORDS'
        // MORTGAGES).
        economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());
        householdBalance.setCapitalRule(growth, keepGoingOnly);
        // ...and every business's spread sits on the bank's prime (0.7.7; the
        // city's rate until then), on the dial as it stands this month - and
        // the landlords' insured mortgages are written and renewed at the
        // insured rate beside it (0.7.11). And each sector's loans pay the
        // capital the book's concentration asks of them (0.7.12; Bank, THE
        // BANK PRICES CONCENTRATION), pushed before anything is priced.
        economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());
        economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),
                bank.insuredMortgageRate(debtManager.getPolicyRate()));
        // ...and the bonds' coupons struck on the same face the interest
        // bills just were, by who holds each now (0.7.12).
        bondMarket.strikeCoupons();

        // Property tax with the interest bill, and for the same reason: both are
        // owed before the month's statements run, so what each sector banks is
        // already net of them.
        economyManager.chargePropertyTax();

        // The repair bill on the housing stock, before EITHER statement runs -
        // it is an expense on one of them and revenue on the other.
        chargeBuildingMaintenance();

        // ...and the freight bill on the month's trade, for exactly the same
        // reason: an expense on eleven sets of books and revenue on a twelfth.
        chargeFreight();

        /*
         * THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT. The work
         * is recognised where the sites advance and the crews draw their
         * material - SimulationEngine, in the middle of the tick, see
         * drawSiteMaterials() - so the figure here is last month's, booked
         * into last month's ledger beside last month's material purchase,
         * and struck a few lines below into the same statement. Until
         * 2026-09-11 it was recognised HERE, at the top, one strike ahead of
         * the material that went into it: the accounts read this month's work
         * against last month's import and a city building steadily read
         * negative in the month a job finished. Same tick for both now.
         */
        ham.citybuildersim.sectors.Construction construction = getSectors().construction();
        double constructionWorkDone = construction.getRecognisedThisMonth();

        /*
         * EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT
         * settled from the same figures between the two halves. One loop,
         * one order for everyone - see EconomyManager.strikeSectors() and
         * Sector.strike(). This was five hand-listed report calls and a
         * hand-written ledger.
         */
        economyManager.strikeSectors();
        // ...and the sales tax a business claimed back on the buildings it
        // bought reached its till at the bank (Sector.bank()) as cash back on
        // them, so the month's building spending is net of it (0.7.19).
        for (Sector s : getSectors().all()) {
            double credit = s.statement().capitalTaxCredit;
            if (credit != 0 && Double.isFinite(credit)) sectorInvested.merge(s.key(), -credit, Double::sum);
        }

        economyManager.updateNationalAccounts(
                constructionWorkDone,
                /*
                 * GOVERNMENT CONSUMPTION, and healthcare is most of it now.
                 * Every real national accounts adds government healthcare to
                 * GDP at exactly what it costs to provide, because there is
                 * no market price to value it at. The gross cost is the right
                 * figure: the fees are a transfer from households, not a
                 * second lot of output.
                 */
                servicesManager.getUtilitiesHandler().getUtilityPayroll()
                        + healthcare.getGrossCost()
                        // ...and the police and the prisons, the same way: a
                        // service with no market price, valued at what it costs.
                        + crime.getGrossCost()
                        // ...and the schools and transit (0.7.49, B9): the schools
                        // were left out from the start, and transit's bill was
                        // paid by nobody until this batch.
                        + education.getGrossCost()
                        + economyManager.getTransitBill(),
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

        // The households' month - and with it the bank's account fees and
        // (0.7.19) its customers, the fees over the fee, which the branch rule
        // in runPrivateInvestment() below reads.
        updateHouseholdAccounts();

        /*
         * ...AND THEY SPEND SOME OF IT ON A CAR. After the ledger, because the
         * money it draws on is the money the ledger just settled; before the
         * investor below, because a month of car sales is demand the investor
         * should be able to see. See Motoring.month().
         */
        motoring.month(this);

        /*
         * ...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED.
         * Straight after the cars and for the same reasons: the savings are
         * fresh, and a month of luxury sales is demand the investor below
         * should be able to see. See LuxuryCounter.shop().
         */
        luxuryCounter.shop(this);

        /*
         * ...AND THE KITCHENS, right after, because they are the same kind of
         * purchase out of the same surplus. After the boutiques rather than
         * before them for no better reason than that the boutiques were
         * written first; the two budgets are struck separately in
         * Household.plan() and neither can spend the other's.
         */
        luxuryCounter.dine(this);

        // Then advance the loans, take back matured principal, and lend to
        // whichever sector the month left short.
        economyManager.settleBusinessCredit(month);

        // Businesses get their turn: shrink first - where the bank's branches
        // past what this month's fees cover at last month's cost close (0.7.19,
        // runRetirement()) - then look at demand, forecast it forward, and
        // expand if the new capacity would carry its own debt.
        runPrivateInvestment();

        printStartOfMonth();
    }
    private void printStartOfMonth() {
        System.out.println("\n================================================================================================================================================================");
        System.out.println("\n================================================================================================================================================================");
        System.out.println("\n================================================================================================================================================================");
        
        if(reports){
            printPopulationInfo();
            economyManager.printWageTaxInfo();
            printSectorInfo();
            printUtilityInfo();
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

    /** The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. */
    public static final int BUILD_NOTE_MONTHS = 6;

    /**
     * The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it.
     *
     * Jerus chose it when the founding treasury went to D$100M and the page
     * became how a player pays for a first big work: a water plant bought on
     * the note left the treasury D$16.2M overdrawn when it matured, ten
     * months on the central bank's advances; on a twenty-year bond sized to
     * the gap it never fell below D$0.5M and never touched them. Must be one
     * of LongTermBond.MATURITIES.
     */
    public static final int BUILD_BOND_YEARS = 20;

    /** The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. */
    public static final double BUILD_BOND_GRANULE = 100;

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
    public static final double FIXED_ISSUE_COST = 12;

    /** The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). See FIXED_ISSUE_COST. */
    private double issuanceFee() {
        double unit = denomination.getUnit();
        return FIXED_ISSUE_COST * expectations.getExpectedLevel() / (unit > 0 ? unit : 1);
    }

    /** Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). */
    public static final double UNDERWRITING_SPREAD = .0075;

    /**
     * The least a dollar of face can ever bank, net of the discount and the
     * spread. ShortTermTBill caps the discount at 95%, so this is
     * 1 - 0.95 - 0.0075. Used to size the top-up in quoteTBill() rather than
     * discovering it a granule at a time; see the note there.
     */
    private static final double MIN_PROCEEDS_PER_FACE = 1 - .95 - UNDERWRITING_SPREAD;

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

    /** ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. */
    public double minimumIssueSizeUsd() {
        return foreign.toUsd(minimumIssueSize());
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

    /**
     * Hands the debt market what money costs the bank, from the one struck
     * figure - the floor under the city's paper. (The business lender was
     * handed it too, as a floor, until 0.7.7 priced business loans off the
     * bank's prime.)
     *
     * A helper rather than a line, because there are four call sites - the
     * close, the top of the month, and twice on the load path - and a city that
     * set it in three of them would quote a different rate in the fourth. That
     * is the shape of every bug this file's comments describe.
     */
    private void pushCostOfFundsToTheDebtMarket() {
        debtManager.setCostOfFunds(bank.marginalCostOfFunds());
    }

    /**
     * Hands the debt market everything it prices against.
     *
     * Four inputs since 0.7.0, when what the treasury owes its central bank
     * joined them, and the overdraft is the one that used to be missing: the
     * market could not see that the city was in the red, so a city $1.1M
     * overdrawn with no bonds left outstanding was quoted the floor rate. All
     * four have to be current before updateInterest() or any quote, which is
     * why this is one call rather than four scattered ones.
     */
    private void priceTheDebtMarket(){
        debtManager.setGDP(economyManager.getMonthGdp());
        debtManager.setTaxRevenue(economyManager.getTaxIncome());
        debtManager.setCashPosition(cash);
        // ...and what it owes its central bank (0.7.0) - see DebtManager.setAdvances().
        debtManager.setAdvances(centralBank.getAdvancesToTreasury());
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

        // The month's revenue, filed for the ceiling on the central bank's
        // advances: six months of it, averaged over the last twelve.
        centralBank.noteRevenue(economyManager.getNationalAccounts().getTotalRevenue());

        // Read and cleared in one place, a line apart, so nothing in between
        // can see a half-cleared month and nothing needs a second copy of them.
        // What a SAVE needs is a different question and has a different answer:
        // the struck block itself is carried - see
        // NationalAccounts.governmentToSave().
        cityCapitalSpending = 0;
        // ...the ground's month too: the forest's regrowth, then its flows (0.7.57).
        landManager.endMonth();
        // ...and what the same land cost in dollars, struck in the same breath
        // so the Exchange page and the land line read the budget's month (0.7.6).
        foreign.strikeLandMonth();
    }

    private void finalUpdateEconomy(){
        economyManager.setDebt(debtManager.getAllPrincipal());
        /*
         * THE MONTH'S BUDGET, LINE BY LINE (0.7.0). This was one figure -
         * getTotalIncome(), the tax take less everything the city pays out -
         * added to the cash in one go. Struck at the same point now, before
         * the markets clear, and paid after them exactly as before, but a line
         * at a time through treasuryPays(). Every one here is a promise, paid
         * whatever it takes; the student grant, the one the arrears rule may
         * cut, is paid at the top of the month since 0.7.1, where the students
         * are credited it, and EI since 0.7.3 for the same reason - see
         * nextMonth().
         */
        double taxIn      = economyManager.getTaxIncome();
        double interestOut = economyManager.getInterestAccrued();
        double pensionsOut = economyManager.getPensionsPaid();
        // Neither the students' grant nor EI is here any more: both are paid
        // at the top of the month, where they are credited (0.7.1, 0.7.3) -
        // see nextMonth().
        double careOut     = economyManager.getHealthcareBill();
        double schoolsOut  = economyManager.getEducationBill();
        double safetyOut   = economyManager.getSafetyBill();
        // ...and transit's wages and upkeep (0.7.49, B9), struck at 6d and paid by nobody until now.
        double transitOut  = economyManager.getTransitBill();
        double tempCash = cash + taxIn - (interestOut + pensionsOut
                + careOut + schoolsOut + safetyOut + transitOut);
        // The utility books what its customers were charged - see
        // UtilitiesHandler.setBilledRevenue().
        servicesManager.getUtilitiesHandler().setBilledRevenue(
                economyManager.getSectorElectricityCharges(),
                economyManager.getSectorWaterCharges());
        economyManager.setUtilityIncome(servicesManager.getServiceNetIncome());
        double servicesNet = servicesManager.getServiceNetIncome();
        tempCash += servicesNet;
        // Every market clears and every maker produces - see Markets.clearMonth().
        // The mines ask the ground through their own hook; see sectors.Mining.
        economyManager.finalEconUpdate(this);
        /*
         * ...AND THE FOOD VOUCHERS THE SALE TOOK ARE PAID (0.7.43): what the
         * households spent at the till in vouchers, which went into their
         * savings as the baskets were handed over (HouseholdBalance
         * .allocateGroceries()). A promise, in the month of the sale, inside
         * the audit's window - paid whatever the guard below decides, because
         * the groceries have been eaten. See TaxPolicy.getFoodAssistance().
         */
        economyManager.setFoodAssistance(treasuryPays(TreasuryLine.FOOD_ASSISTANCE,
                householdBalance.getFoodAssistancePaid()));

        servicesManager.updateServices();
        economyManager.setPricePerWatt(servicesManager.getPricePerWatt());
        economyManager.setPricePerWaterUnit(servicesManager.getPricePerWaterUnit());
        
        if (Double.isFinite(tempCash)) {
            // The tax take in, and every line out - the promises whole.
            // tempCash is the guard it always was: a month whose books do not
            // come to a finite figure moves no cash at all.
            cash += taxIn + Math.max(0, servicesNet);
            treasuryPays(TreasuryLine.INTEREST, interestOut);
            treasuryPays(TreasuryLine.PENSIONS, pensionsOut);
            treasuryPays(TreasuryLine.HEALTHCARE, careOut);
            treasuryPays(TreasuryLine.SCHOOLS, schoolsOut);
            treasuryPays(TreasuryLine.SAFETY, safetyOut);
            treasuryPays(TreasuryLine.TRANSIT, transitOut);
            treasuryPays(TreasuryLine.CITY_SERVICES, Math.max(0, -servicesNet));
            /*
             * THE FARES ARE A BUDGET LINE SINCE 0.7.49 (B9). They arrive in the
             * cash a line above, inside getTaxIncome(), and were journalled
             * here as "Took in transit fares" because
             * NationalAccounts.getTotalRevenue() had no line for them. It has
             * one now, and the bill they are set against is paid above, so the
             * line came out, as this note said it would. See TreasuryJournal.
             */
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
    
    public long getHouseholdCapacity() {
        return buildingManager.getTotalHouseCapacity();

    }
    public long getStoreCapacity() {
        return buildingManager.getTotalStoreCoverage();

    }
    public long[] getJobs(){
        return buildingManager.getTotalJobs();
    }
   
    
    public BuildingManager getBuildingManager() {
        return buildingManager;
    }
    // Needed by the JavaFX sector screens. Safe to expose now that the report
    // getters on CommercialHandler are all pure reads - the UI cannot mutate
    // economy state through this.
    private BusinessInvestment businessInvestment;
    private LandManager landManager = new LandManager(() -> this.foreign.getRate(), () -> this.world.getPriceLevel(),
            () -> this.founding.getWorldSeed(), () -> this.month);

    /** True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). */
    private boolean loadingSave;

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
    private FamilyModel families = rememberingFamilies();

    /** The game's families keep what still fits from month to month; a bare model does not. */
    private static FamilyModel rememberingFamilies() {
        FamilyModel f = new FamilyModel();
        f.rememberHouseholds(true);
        return f;
    }
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

        /*
         * ...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19). Jerus:
         * a household that cannot pay the clinic's fee goes without care,
         * not without food. Of the people each kind of care would serve, the
         * share whose household can pay - struck at this month's household
         * strike, see careAffordability() - multiplies the coverage, so the
         * unserved-because-poor are unserved the way the unserved-because-
         * no-clinic are: in the sick rate, in the swings, in the births.
         * Multiplying by exactly 1.0 is bit-exact, so a city where everybody
         * can pay - every city at the founding fee that is not going hungry -
         * is the city it was.
         */
        double[] affordable = new double[CareType.values().length];
        java.util.Arrays.fill(affordable, 1);
        for (CareType care : CareType.values()) {
            if (care.servesTheLiving()) affordable[care.ordinal()] = careAffordability(care);
        }
        childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];
        seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];
        generalCoverage   *= affordable[CareType.GENERAL.ordinal()];
        // ...and the service keeps the three, so a screen shows the coverage
        // the month read rather than one it worked out from the beds.
        healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);

        // What the beds could do; the service takes the priced-out off it.
        double[] served = new double[CareType.values().length];
        for (CareType care : CareType.values()) {
            if (!care.servesTheLiving()) continue;
            served[care.ordinal()] = Math.min(
                    buildingManager.getStaffedCareCapacity(care, fill),
                    care.populationServed(cohorts));
        }
        /*
         * ...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds
         * built: the people the beds could take, less the ones the fee turned
         * away, over the people. An empty bed treats nobody who cannot pay
         * for it, so an overbuilt city with a dear fee is still an unserved
         * one. With everybody paying this is min(beds, people) over people,
         * which is the clamped ratio Health struck before, to the bit.
         */
        generalCapacity = served[CareType.GENERAL.ordinal()]
                * affordable[CareType.GENERAL.ordinal()];

        /*
         * BOTH ENDS OF A LIFE, and now the beginning of one too.
         *
         * Childcare swings infant mortality forty-fold and doubles the birth
         * rate; senior care moves the seniors' gently. Today's rates are what a
         * HALF-served city gets, so building care does better than the game has
         * ever done and building none does very much worse. The multipliers and
         * the reasoning live in Healthcare. General care keeps teenagers and
         * adults alive through Sickness instead, below.
         */
        double[] mortalityFactors = Healthcare.mortalityFactors(
                childcareCoverage, generalCoverage, seniorCoverage);

        /*
         * THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11). The pyramid
         * has no sub-groups, so each enters its band's factor as its share of
         * the band, from last month's count: the unhoused at
         * Unemployment.UNHOUSED_MORTALITY times the band's rate, the orphans at
         * the rate their band has with no care at all - nobody is caring for
         * them. Jerus: "yes they get sick and die for now."
         */
        double[] unhousedByBand = families.unhousedPeopleByBand();
        unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();
        double[] orphansByBand = new double[AgeBand.values().length];
        double[] inBand = new double[AgeBand.values().length];
        for (AgeBand b : AgeBand.values()) {
            orphansByBand[b.ordinal()] = families.getOrphans(b);
            inBand[b.ordinal()] = cohorts.get(b);
        }
        double[] careFactors = mortalityFactors;
        mortalityFactors = Unemployment.blendMortality(mortalityFactors,
                Healthcare.mortalityFactors(0, 0, 0), inBand, unhousedByBand, orphansByBand);

        /*
         * ...AND THE PEOPLE WHO STAYED SICK (2026-09-11). Whoever has been sick
         * for more than two months dies at their age's monthly chance, from the
         * ring as last month left it. A city with no ring yet - a new one, or a
         * save from before - gets one at the steady state for the rate it has,
         * or nobody could die of sickness for two months. See Sickness.
         */
        if (!sickness.isSeeded()) {
            sickness.seed(health.getSickRate(), generalCoverage, childcareCoverage, seniorCoverage);
        }
        double[] illness = sickness.deathRates();
        /*
         * ...AND THE PEOPLE VIOLENCE KILLED (2026-09-11). Last month's killings
         * over the adults, as this month's chance - the victims of homicide are
         * overwhelmingly adults, and the pyramid has no other way to say who.
         * See Crime.
         */
        double[] violence = new double[AgeBand.values().length];
        double adultsBefore = cohorts.get(AgeBand.ADULT);
        violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0
                ? Math.min(1, crime.getKilled() / adultsBefore) : 0;
        cohorts.advanceMonth(mortalityFactors, illness, violence,
                Healthcare.birthFactor(childcareCoverage));
        sickness.setLastDeaths(cohorts.getIllnessDeaths());
        // Who among them were orphans, and who had no home - for the running
        // totals on the graphs. See Unemployment.attributeDeaths(). The killed
        // are spread over the band as the sick are.
        double[] dyingByBand = new double[AgeBand.values().length];
        for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);
        double[] spreadDead = cohorts.getIllnessDeaths();
        double[] killedDead = cohorts.getKilled();
        for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];
        double[] outsideDead = Unemployment.attributeDeaths(careFactors,
                Healthcare.mortalityFactors(0, 0, 0), inBand, unhousedByBand, orphansByBand,
                dyingByBand, spreadDead);
        lastOrphanDeaths = outsideDead[0];
        lastUnhousedDeaths = outsideDead[1];

        /*
         * ...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE. Before the
         * workforce is read, so they are not counted as looking for work in a
         * city they have left. Adults only - see PopulationCohorts.leave().
         */
        cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());
        // Kept for the skills step below: the graduates die at the rate the
        // adults do, and that rate depends on the clinics.
        lastAdultMortality = AgeBand.monthlyFromAnnual(
                AgeBand.ADULT.getAnnualMortality()
                        * Math.max(0, mortalityFactors[AgeBand.ADULT.ordinal()]))
                + illness[AgeBand.ADULT.ordinal()]
                + violence[AgeBand.ADULT.ordinal()];

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

        // ...and what last month's crime says about living here. See
        // Migration.crimePull(): "like dear rent", Jerus.
        migration.setCrimeVsCanada(crime.getRateVsCanada());

        // ...and the doors standing today, which the room the placement has
        // left is asked against (0.7.17): arrivals are bounded by it. See
        // Migration.monthlyNet() and FamilyModel.roomLeft().
        migration.setDoors(buildingManager.homesBySize());

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
                Math.round(cohorts.total()), adultsAlreadyHere);

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
        long[] posts = populationManager.getJobs();

        for (JobType type : JobType.values()) {
            int i = type.ordinal();
            jobsByTier[PayTier.of(type).ordinal()] += posts[i] * fillRate[i];
        }

        /*
         * WHO IS OUT OF WORK, AND WHO THEY WERE. The pool is the labour
         * market's; Unemployment works out who is in it - on EI, past it,
         * evicted - from the month's flows. Then the families are built from
         * the adults who WORK, and the out of work and the students are told
         * to the housing match as households of their own.
         */
        double[] postsByTier = new double[PayTier.values().length];
        double[] wageByTier = new double[PayTier.values().length];
        double[] staffedWage = populationManager.getStaffedWagePerTier();
        for (JobType type : JobType.values()) {
            postsByTier[PayTier.of(type).ordinal()] += posts[type.ordinal()];
        }
        for (int t = 0; t < wageByTier.length; t++) {
            wageByTier[t] = jobsByTier[t] > 0 && staffedWage != null && t < staffedWage.length
                    ? staffedWage[t] / jobsByTier[t] : 0;
        }
        /*
         * CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11).
         *
         * Before the pool is read, because a prisoner is out of the labour
         * force the month they go in, and after migration, because coverage is
         * the officers against the people who live here now. The REASONS are
         * last month's - who was out of work, who had no door, who went short
         * - which is the only honest source: this month's households have not
         * been built yet. The thefts and the injuries land this month; the
         * killings and the pull on migration land next month, carried in
         * Crime's state. See Crime, and claude/crime-has-reasons.md.
         */
        double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);
        double[] offenderWeight = new double[householdBalance.cellCount()];
        Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);
        crime.advanceMonth(causes, cohorts.total(), officers,
                buildingManager.getStaffedSafetyCapacity(SafetyType.PRISON, fill),
                lastAdultMortality + AgeBand.ADULT.monthlyOutflowRate(),
                unskilledWage());
        // Who went in: out of the pool by the pool's share of the pressure,
        // group by group; the rest leave the families, by the labour market's
        // own identity, when the supply shrinks by them below.
        unemployment.imprison(crime.getPressure() > 0
                ? crime.getAdmitted() * Math.min(1, causes.poolWeighted() / crime.getPressure()) : 0);
        populationManager.setImprisoned(crime.prisoners());
        offending.steal(this, offenderWeight);

        double outOfWork = populationManager.getUnemployed();
        double studying = populationManager.getStudyingTotal();
        unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,
                unskilledWage(), lastAdultMortality,
                economyManager.getTaxPolicy().getEiBenefitRate());
        // The month's adult arrivals look for work next month.
        unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));

        /*
         * THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE
         * HOUSE. Both are outside the families - none of them is built into
         * one - but only a prisoner stops living with their children. See the
         * note on FamilyModel.rebuild(): the out of work and the students take
         * their dependants with them, and before 2026-09-15 they did not, which
         * is why a city that built universities filled its orphan section.
         */
        families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);
        families.setSeekers(unemployment.getHoused(), studying);
        // The student body above is last month's education step, and so are
        // the ones who finished: they leave it with their loans at this
        // month's census. See HouseholdBalance.setGraduates().
        householdBalance.setGraduates(education.getFinished());

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
        long[] stock = buildingManager.homesBySize();
        double unplaced = families.house(stock);
        families.squeezeUnplaced(unplaced);
        families.noteUnplaced(families.house(stock));

        // What the landlords can bill, off the match rather than off an
        // average, and split by which segment the door was in. See
        // CommercialHandler.getRentIncome() and FamilyModel's TWO SEGMENTS.
        getSectors().realEstate().setRentWeight(
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
        // ...and the people outside the families, with their own kind.
        families.shareSeekersByAffordability(households.seekerPressure(new double[] {
                unemployment.getHoused(), families.getSeekers(FamilyModel.Seeker.STUDENT) }));

        // EI and the grant are both paid at the top of the month now, where
        // they are credited (0.7.3 and 0.7.1), and what was paid is kept for
        // the month's budget and its audit: the bill the pool's step struck
        // just above is next month's, paid and credited at the top of it -
        // see payEiBenefits() and payStudentGrants().

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
        // At the player's fee scale (2026-09-19), and with the share of each
        // kind of care's people who could pay - see Healthcare.advanceMonth().
        healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());
        healthcare.advanceMonth(
                buildingManager.getCategoryPayroll(BuildingType.HEALTHCARE,
                        populationManager.getWagesPerType(), fill),
                buildingManager.getUpkeepByCategory(BuildingType.HEALTHCARE),
                served, affordable,
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
        // At the player's tuition scale (2026-09-21), told here as the clinic's
        // scale is above, so the fees this step charges are this month's -
        // kind by kind since 0.7.6, each school at its own price.
        tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());
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

        /*
         * ...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools
         * page's row per kind: the same payroll and upkeep the total above
         * was struck from, narrowed to the buildings teaching each course,
         * at the same wages and fill. Read-only - the treasury pays the total.
         */
        for (EducationType kind : EducationType.values()) {
            if (kind == EducationType.NONE) continue;
            education.setCostOf(kind,
                    buildingManager.getSchoolPayroll(kind, populationManager.getWagesPerType(), fill),
                    buildingManager.getSchoolUpkeep(kind));
        }

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
         * 6c. AND THE POLICE AND THE PRISONS: what they cost, charged to the
         *     city like the hospitals and the schools. No fees.
         */
        crime.setCosts(
                buildingManager.getCategoryPayroll(BuildingType.SAFETY,
                        populationManager.getWagesPerType(), fill),
                buildingManager.getUpkeepByCategory(BuildingType.SAFETY));
        economyManager.setSafety(crime.getGrossCost());

        /*
         * 6d. AND THE BUSES AND THE TRAMS, which are the first thing the city
         *     builds for itself that can turn a profit.
         *
         *     THE WAGES WERE FREE UNTIL TODAY, and only because nothing in
         *     INFRASTRUCTURE had ever had a job: a road does not employ
         *     anybody, so no line was ever written to pay one. A Metro Line is
         *     eight hundred and twenty-eight posts. Adding the category here
         *     costs a city with only roads exactly nothing, which is every
         *     city that exists, and stops the transit stock being staffed by
         *     volunteers.
         *
         *     ...AND UNPAID UNTIL 0.7.49 (B9): struck here and read by the
         *     national accounts alone. finalUpdateEconomy() pays it now, as a
         *     promise (TreasuryLine.TRANSIT).
         *
         *     ...AND WHO RIDES, BY WHAT THEY PAY (0.7.49): the commuters with
         *     no car of their own, read off the cells, and a journey's fuel
         *     at today's exchange rate, told to the network before the fare
         *     so the riders below are the month's; then the drivers' fuel.
         *
         *     ...THE FUEL A MARKET SINCE 0.7.62 (batch K): a journey's fuel is
         *     its litres at what a litre costs to bring in (Motoring.journeyFuel()
         *     - the refiners' price while the city has fuel on offer, the
         *     import price at the world's price level while it has none), and
         *     the drivers' litres are drawn off the refiners' shelf and the
         *     rest imported (Motoring, THE FUEL IS DRAWN).
         */
        getInfrastructureManager().setCommute(householdBalance.captiveShare(),
                Motoring.journeyFuel(getMarkets()));
        servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());
        motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);
        economyManager.setTransit(
                buildingManager.getCategoryPayroll(BuildingType.INFRASTRUCTURE,
                        populationManager.getWagesPerType(), fill)
                        + buildingManager.getUpkeepByCategory(BuildingType.INFRASTRUCTURE),
                getInfrastructureManager().getTransitRiders()
                        * economyManager.getTaxPolicy().monthlyFare());

        /*
         * 7. And who is too ill to work. Last, because the dead nobody buried
         *    are one of the three things that decide it.
         */
        health.advanceMonth(generalCapacity, servedThisMonth, month,
                healthcare.getUnburied(),
                // The last step of the household waterfall, arriving as a health
                // problem: savings gone, credit gone, so they eat less.
                householdBalance.getHungerRate(),
                // ...and the people with no home, who get sick faster.
                unhousedShareOfCity(),
                // ...and the people violent crime put off work.
                crime.getInjuredShare());

        /*
         * 8. And how long they have been sick. The ring turns on this month's
         *    rate: the dead come out, general care cures its share, the rest are
         *    a month longer, and whoever fell ill this month joins. Next month's
         *    deaths are struck from what this leaves.
         */
        sickness.advanceMonth(health.getSickRate(), generalCoverage,
                childcareCoverage, seniorCoverage);
    }

    /* =====================================================================
       WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java.
       ===================================================================== */

    /** Who is at risk of offending, and the thefts; runs in the crime step of the month. See Offending. */
    private final Offending offending = new Offending();

    /** The offenders' sort, for the harnesses that read past the getter below. */
    public Offending getOffending() { return offending; }

    /** The share of the city with no home: the unhoused, and the orphans. */
    public double unhousedShareOfCity() { return offending.unhousedShareOfCity(this); }

    /* =====================================================================
       THE READINGS THE MONTH TAKES OF THE CITY
       Care coverage, the burial share, the unskilled wage, what a cell pays
       of a door's rent: small figures the month strikes and hands on, with
       the build log and the getters for the people-side subsystems among
       them, and at the end the fields for the money-side ones - the foreign
       accounts, the hot money, the savings abroad, the equity, the exchange
       and the price index. Sat under the offending banner until 2026-09-18.
       ===================================================================== */

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
     * Of the people a kind of care would serve, the share who live in a
     * household that can pay its fee (2026-09-19).
     *
     * Headcount-weighted over the cells, by the places the care type counts
     * - a senior needs 0.19 of a place and an elder a whole one, the same
     * weights CareType.populationServed() divides coverage by - and each
     * cell's share is what it struck for itself at this month's household
     * strike, see Household.affordCare(). A city with nobody to serve reads
     * 1, like the coverage it multiplies: no children is not a childcare
     * shortage and not a childcare price either. The orphans and the
     * prisoners pay no fee and are counted as paying.
     */
    public double careAffordability(CareType care) {
        if (care == null || !care.servesTheLiving()) return 1;
        double heads = 0, paying = 0;
        for (Household c : householdBalance.cells()) {
            double n = careHeads(c, care) * c.households();
            if (n <= 0) continue;
            heads += n;
            paying += n * c.carePaid();
        }
        return heads > 0 ? paying / heads : 1;
    }

    /**
     * The places one household of a cell needs of a kind of care: its
     * shape's members in the bands the care serves, at the care's places per
     * head. A cell outside the families is its adult, and the children who
     * came with an out-of-work parent or a student are apportioned across
     * the three child bands the way the family model counted them; an
     * orphan household is one child of its band, and pays nothing.
     */
    private double careHeads(Household c, CareType care) {
        if (c.shape() != null) {
            double places = 0;
            for (AgeBand band : AgeBand.values()) {
                places += c.shape().membersOf(band) * care.placesPerHead(band);
            }
            return places;
        }
        if (c instanceof OrphanHousehold o) return care.placesPerHead(o.band());
        double places = care.placesPerHead(AgeBand.ADULT);
        double kin = c.getDependants();
        if (kin > 0 && families != null) {
            double followed = 0, weighted = 0;
            for (AgeBand band : new AgeBand[] { AgeBand.BABY, AgeBand.CHILD, AgeBand.TEEN }) {
                double n = families.getOutsideDependants(band);
                followed += n;
                weighted += n * care.placesPerHead(band);
            }
            places += followed > 0 ? kin * weighted / followed : kin * care.placesPerHead(AgeBand.CHILD);
        }
        return places;
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
            // ...and the owner's loss clock starts again - see BusinessInvestment.noteOpened().
            BuildingsTemplate t = buildingManager.getTemplateByName(done.building);
            if (t != null && t.isOwnedBySector()) businessInvestment.noteOpened(t.getSector());
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

    /*
     * THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). Jerus: "a new household
     * structure called unemployed... or unhoused... and they will have their
     * own cashflow and stuff." The out of work, the students and the orphans
     * have books of their own in the balance; this is what tells the balance
     * how many are in each, and what share of a door's rent each pays. See
     * claude/the-people-the-books-left-out.md.
     */
    private final Unemployment unemployment = new Unemployment();
    public Unemployment getUnemployment() { return unemployment; }

    /** Who has been sick how long, and who it kills. See Sickness. */
    private final Sickness sickness = new Sickness();
    public Sickness getSickness() { return sickness; }

    /**
     * Crime, the police and the prisons (2026-09-11). Jerus: "crime is a
     * function of unemployment, and tight or under households, we need police,
     * and also prison." See Crime, and claude/crime-has-reasons.md.
     */
    private final Crime crime = new Crime();
    public Crime getCrime() { return crime; }

    /** Last month's dead who were orphans, and who had no home. See Unemployment.attributeDeaths(). */
    private double lastOrphanDeaths, lastUnhousedDeaths;
    public double getLastOrphanDeaths()   { return lastOrphanDeaths; }
    public double getLastUnhousedDeaths() { return lastUnhousedDeaths; }

    {
        householdBalance.setOutsideCensus(this::outsideHouseholds);
        householdBalance.setRentShares(this::rentShareOf);
        householdBalance.setHousedShares(this::housedShareOf);
        householdBalance.setOutsideDependants(this::outsideDependantsOf);
    }

    /** How many households are in a cell the family matrix does not hold. */
    private double outsideHouseholds(Household c) {
        if (c instanceof UnemployedHousehold u) {
            switch (u.status()) {
                case ON_EI:    return unemployment.onEi();
                case OFF_EI:   return unemployment.getOffEi();
                case UNHOUSED: return unemployment.getUnhoused();
                default:       return 0;
            }
        }
        if (c instanceof PrisonerHousehold) return crime.prisoners();
        if (families == null) return 0;
        if (c instanceof StudentHousehold) return families.getSeekers(FamilyModel.Seeker.STUDENT);
        if (c instanceof OrphanHousehold o) return families.getOrphans(o.band());
        return 0;
    }

    /**
     * Dependants in one household of a cell the family matrix does not hold.
     *
     * The out of work and the students took their children with them, so they
     * carry them here; a prisoner did not, so theirs are in the orphan section
     * and this is zero. An orphan household holds nobody else by definition -
     * it IS the children, one cell a band, and giving it dependants as well
     * would count the same child twice.
     */
    private double outsideDependantsOf(Household c) {
        if (families == null) return 0;
        if (c instanceof UnemployedHousehold || c instanceof StudentHousehold) {
            return families.dependantsPerOutsideHousehold();
        }
        return 0;
    }

    /** The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). */
    private double rentShareOf(Household c) {
        if (families == null) return 1;
        if (c instanceof UnemployedHousehold u) {
            return u.status() == UnemployedHousehold.Status.UNHOUSED ? 0
                    : families.seekerDoorShare(FamilyModel.Seeker.UNEMPLOYED);
        }
        if (c instanceof StudentHousehold) return families.seekerDoorShare(FamilyModel.Seeker.STUDENT);
        if (c instanceof OrphanHousehold) return 0;
        // The prison houses them.
        if (c instanceof PrisonerHousehold) return 0;
        if (c.shape() != null) return 1 - families.unhousedShareOf(c.shape());
        return 1;
    }

    /**
     * How much of a cell has a home, for the bank's account fee (0.7.7): all
     * of a household that has one - alone or sharing - and none of the
     * orphans, the prisoners or the unhoused. rentShareOf() answers a
     * different question (what share of a door they pay), which a household
     * sharing a flat pays a fifth of and still has a bank account.
     */
    private double housedShareOf(Household c) {
        if (c instanceof UnemployedHousehold u) {
            return u.status() == UnemployedHousehold.Status.UNHOUSED ? 0 : 1;
        }
        if (c instanceof OrphanHousehold || c instanceof PrisonerHousehold) return 0;
        if (c instanceof StudentHousehold) return 1;
        if (families != null && c.shape() != null) return 1 - families.unhousedShareOf(c.shape());
        return 1;
    }

    /** The doors each row of the household books pays rent on. See HouseholdAccounts.rowDoors. */
    private double[] rowDoors() {
        double[] doors = new double[Household.ROWS];
        PayTier retiredSlot = PayTier.values()[0];
        for (Household c : householdBalance.cells()) {
            double n = c.shape() == null ? outsideHouseholds(c)
                    : families.get(c.shape(), c.isRetired() ? retiredSlot : c.tier());
            doors[c.row()] += n * rentShareOf(c);
        }
        return doors;
    }

    /** Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. */
    private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff;
    public double getStudentLoansLent()       { return studentLoansLent; }
    public double getStudentLoansRepaid()     { return studentLoansRepaid; }
    public double getStudentLoansWrittenOff() { return studentLoansWrittenOff; }

    /** Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. */
    private double studentLoanInterest;
    public double getStudentLoanInterest()    { return studentLoanInterest; }

    /** What an unskilled post pays a month, today: EI's cap and the grant are struck against it. */
    private double unskilledWage() {
        double[] wages = populationManager.getWagesPerType();
        return wages != null && wages.length > JobType.NO_DIPLOMA.ordinal()
                ? wages[JobType.NO_DIPLOMA.ordinal()] : PayTier.UNSKILLED.getMonthlyWage();
    }

    /** The same wage, for the Schools page to name what a share of it is. */
    public double getUnskilledWage() { return unskilledWage(); }

    /* -------------------- the price of a place (2026-09-21) --------------------
     * The grant bill, struck ONCE, here, from the four figures TaxPolicy's
     * rule needs - the students, the unskilled wage, last month's surplus
     * and the student body's tuition at today's price - and read by the
     * treasury's bill in advanceDemographics(), the save's re-strike, and
     * the Schools page's preview of a basis the player has not chosen yet.
     *
     * LAST MONTH'S SURPLUS IS THE BRIDGE'S. It is read from treasurySurplus,
     * the budget balance takeTreasuryMonth() struck at the bottom of the
     * last tick - the figure the Government tab calls the balance - and
     * never recomputed here: NationalAccounts.getBalance() mid-tick is a
     * month mixed from two, which is exactly why the bridge carries its own.
     */

    /** The month's grant bill under the city's basis and amount. */
    public double studentGrantBill() {
        TaxPolicy tax = economyManager.getTaxPolicy();
        return studentGrantBillUnder(tax.getGrantBasis(), tax.getGrantAmount());
    }

    /** ...and under any basis and amount, for a preview: the same rule, the same four figures. */
    public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount) {
        // A FIXED grant at the expected price level since 0.7.42, like every
        // money constant; at the price index from 0.7.19.
        return TaxPolicy.grantBill(basis, amount,
                families.getSeekers(FamilyModel.Seeker.STUDENT), unskilledWage(),
                expectations.getExpectedLevel(), treasurySurplus, education.studentBodyTuition());
    }

    /** What that comes to per student - the bill over this month's students, or nothing with none. */
    public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount) {
        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);
        return students > 0 ? studentGrantBillUnder(basis, amount) / students : 0;
    }

    /**
     * Today's grant per student, re-expressed as an amount under another
     * basis: the number the Schools page starts the amount dial at when a
     * basis is picked, so picking one changes nothing until the amount is
     * moved. Nothing to re-express - no students, no wage, no surplus, no
     * tuition - reads as the basis's own default.
     */
    public double grantAmountAs(TaxPolicy.GrantBasis basis) {
        TaxPolicy tax = economyManager.getTaxPolicy();
        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);
        double each = grantPerStudentUnder(tax.getGrantBasis(), tax.getGrantAmount());
        if (basis == null) basis = TaxPolicy.DEFAULT_GRANT_BASIS;
        double base;
        switch (basis) {
            case FIXED:         base = expectations.getExpectedLevel(); break;   // a real amount (0.7.19), at the expected level (0.7.42)
            case SURPLUS_SHARE: base = students > 0 ? Math.max(0, treasurySurplus) / students : 0; break;
            case TUITION_SHARE: base = students > 0 ? education.studentBodyTuition() / students : 0; break;
            default:            base = unskilledWage(); break;
        }
        if (base > 0 && each > 0) return Math.min(tax.maxGrantAmount(basis), each / base);
        return basis == TaxPolicy.GrantBasis.WAGE_SHARE ? TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE : 0;
    }

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

    /** Where the shares change hands, with the bank as the dealer. See Exchange. */
    private final Exchange exchange = new Exchange();
    public Exchange getExchange() { return exchange; }

    /**
     * The businesses' bonds and the order book each trades on (0.7.12): who
     * issued what, who holds it, and the rule each participant trades by.
     * See BondMarket, and CorporateBond for the instrument.
     */
    private final BondMarket bondMarket = new BondMarket();
    public BondMarket getBondMarket() { return bondMarket; }

    /**
     * The city's fund (0.7.14): its cash, its dial and its rescue setting,
     * the bank's preferred offer, its rescues and the player's orders. Its
     * shares are on the register (Equity's city holding), its bonds on each
     * CorporateBond, its preferred and warrants on the Bank. See TreasuryFund,
     * and THE CITY'S FUND AND THE BANK'S RESCUE below.
     */
    private final TreasuryFund fund = new TreasuryFund();
    public TreasuryFund getFund() { return fund; }

    /** What the bond market reads of the city, read live. */
    private final BondMarket.Readings bondReadings = new BondMarket.Readings() {
        @Override public int month()                { return month; }
        @Override public double curve(int months)   { return debtManager.curveRate(months); }
        @Override public double policyRate()        { return debtManager.getPolicyRate(); }
        @Override public double depositRate()       { return bank.depositRate(); }
        @Override public double worldRate()         { return DebtManager.WORLD_BASE_RATE; }
        @Override public double countryPremium()    { return debtManager.countryPremium(); }
        @Override public double monthlyGdp()        { return economyManager.getMonthGdp(); }
        @Override public double localPerUsd()       { return foreign.getRate(); }
        @Override public boolean worldRunning()     { return hotMoney.isStopped(); }
        @Override public double unit()              { return denomination.getUnit(); }
    };

    public OutwardInvestment getOutwardInvestment() { return outward; }

    /**
     * What a month costs a household, against founding. See PriceIndex.
     *
     * Priced at the END of the month, from the prices the month actually
     * charged, and READ at the top of the next one - which is why wages chase
     * it with a lag rather than reacting to a number struck the same instant.
     */
    private final PriceIndex priceIndex = new PriceIndex();

    /* =====================================================================
       THE ANCHOR (0.7.42): what the city expects prices to do, and the
       money constants struck at what it expects them to be.

       Expected inflation and the bank's credibility (Expectations) are
       taken straight after the price index, at the bottom of the month, and
       read by everything the following month: the wages' indexing
       (LabourMarket.updateCostOfLiving()), the real rates
       (realRateDifferential(), realDepositRate()), the investors' hurdle and,
       since 0.7.44, the landlords' lender's test (BusinessInvestment
       .servicesItsOwnDebt() and realTestRate()), the currency's drift
       (anchoredDrift()), the drift every sticky price carries since 0.7.43
       (handOnExpectations()) and every money constant that prices something
       (restrikeMoneyConstants()). The project's spec-inflation.md, sections
       2.1-2.5 and 3, is the design and its measurements.
       ===================================================================== */
    private final Expectations expectations = new Expectations();

    /** Expected inflation, credibility and the expected price level. See Expectations. */
    public Expectations getExpectations() { return expectations; }

    /** What the city expects inflation to be, a fraction a year. See Expectations.getExpectedInflation(). */
    public double getExpectedInflation() { return expectations.getExpectedInflation(); }

    /** How far the city believes the central bank, Expectations.KMIN to KMAX. See Expectations.getCredibility(). */
    public double getCredibility() { return expectations.getCredibility(); }

    /**
     * The currency's anchored drift, a fraction a year (ForeignAccounts, THE
     * ANCHORED DRIFT): the credible part of expected inflation - credibility
     * times the target - against the world's realised inflation, and nothing
     * before the basket is based.
     */
    public double anchoredDrift() {
        if (!priceIndex.isBased()) return 0;
        return (1 + expectations.getCredibility() * debtManager.getInflationTarget())
                / (1 + world.realisedInflation()) - 1;
    }

    /* ----- what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) ----- */

    /** Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. */
    double[] indexPrices() {
        double[] p = new double[PriceIndex.COMPONENTS];
        p[PriceIndex.GROCERIES] = getSectors().retail().getStoreSellPrice();
        /*
         * The AVERAGE actually paid, not the family price. Rent is two prices
         * now and the cost of living is what households handed over across
         * both. Continuous with what the index used before the split: the two
         * prices open equal, so the average opens on the same number the base
         * was struck at.
         */
        p[PriceIndex.RENT]   = getSectors().realEstate().getAverageRentPaid();
        /*
         * A meal and a piece only at a price somebody paid this month: a
         * counter with no shop behind it strikes its ceiling margin on buyers
         * nobody serves, and that is not a price anybody lives at. Measured
         * in the default playtest's founding: its one Boutique closed at
         * month 33, the margin climbed from 1.3 to 4 on nobody served, and
         * at a twelfth of the basket it put 28 points on the index in two
         * years. Nothing sold is no price, and the index holds the last one.
         */
        ham.citybuildersim.sectors.Restaurants kitchens = getSectors().restaurants();
        ham.citybuildersim.sectors.LuxuryRetail counters = getSectors().luxuryRetail();
        p[PriceIndex.MEALS]  = kitchens.getServed() > 0 ? kitchens.getSellPrice() : 0;
        p[PriceIndex.LUXURY] = counters.getServed() > 0 ? counters.getSellPrice() : 0;
        return p;
    }

    /** ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. */
    double[] indexSpends() {
        double[] s = new double[PriceIndex.COMPONENTS];
        s[PriceIndex.GROCERIES] = getSectors().retail().statement().salesToHouseholds;
        s[PriceIndex.RENT]      = getSectors().realEstate().statement().salesToHouseholds;
        s[PriceIndex.MEALS]     = householdBalance.getMealSpend();
        s[PriceIndex.LUXURY]    = householdBalance.getLuxurySpend();
        return s;
    }

    /**
     * ...each fee line's price: the clinic's fee for a general visit at the
     * player's scale, a seat's tuition (the adult kinds' fees averaged - a
     * price, so a shift in who studies what is not inflation), the fare a
     * ride is charged and the bank's account fee.
     */
    double[] indexFees() {
        double[] f = new double[PriceIndex.FEE_LINES];
        f[PriceIndex.HEALTH_FEE] = healthcare.feeNow(CareType.GENERAL);
        double seats = 0, kinds = 0;
        for (EducationType type : EducationType.values()) {
            if (!type.isAdult()) continue;
            seats += education.feeFor(type);
            kinds++;
        }
        f[PriceIndex.TUITION]     = kinds > 0 ? seats / kinds : 0;
        f[PriceIndex.FARE]        = economyManager.getTaxPolicy().chargedFare();
        f[PriceIndex.ACCOUNT_FEE] = householdBalance.getAccountFee();
        return f;
    }

    /** ...and what the households paid on each fee line, off their books. */
    double[] indexFeeSpends() {
        double[] s = new double[PriceIndex.FEE_LINES];
        s[PriceIndex.HEALTH_FEE]  = households.getHealthcare();
        s[PriceIndex.TUITION]     = households.getTuition();
        s[PriceIndex.FARE]        = households.getFares();
        s[PriceIndex.ACCOUNT_FEE] = households.getAccountFees();
        return s;
    }

    /**
     * Every money constant that prices something, struck at the expected
     * price level: founding / unit x Expectations.getStruckLevel(), the level
     * the last month ended on. Called as the first statement of a month
     * (nextMonth()), and on a load once the unit is restored, at the level
     * the saved month had struck. Idempotent - each class keeps its founding figure - so a
     * reform needs nothing new: the unit and the level are both in the
     * product.
     *
     * WHAT FOLLOWS THE LEVEL (spec-inflation.md, section 2.3): the shelf's
     * opening price, the price of ground, every template's cash cost and
     * upkeep (with the labour in them split off at the level -
     * BuildingManager, THE CASH IS STRUCK AT THE EXPECTED LEVEL), the care and
     * burial fees, the tuition table, the pension's wage base, a founding
     * share's par, the bank's paid-in capital, its domestic-capital scale and
     * its account fee, and the fare (TaxPolicy, THE FARE IS REAL). WHAT GAME
     * STRIKES ITSELF - the FIXED grant, the new-home rebates and the issue
     * fee - reads Expectations.getExpectedLevel() where it is struck: inside
     * a month that is the level struck here (the anchor moves it only at the
     * month's end), and between the presses it is the level the next press
     * will strike at, so a bill read before a month runs is the bill the
     * month pays (EducationCheck). WHAT DOES NOT: the numerical guards and grains, which follow
     * the unit only (the seeds in the load path), and everything priced in
     * world money.
     *
     * NEVER THE INDEX: a constant that prices something follows what people
     * expect prices to be, so no constant reads a price it sets.
     */
    void restrikeMoneyConstants() {
        double unit = denomination.getUnit();
        double level = expectations.getStruckLevel();
        if (!(level > 0)) level = 1;
        double struck = (unit > 0 ? unit : 1) / level;
        getSectors().retail().seedConstants(struck);
        landManager.seedConstants(struck);
        buildingManager.seedConstants(struck);
        buildingManager.setExpectedLevel(level);
        healthcare.seedConstants(struck);
        education.seedConstants(struck);
        economyManager.getTaxPolicy().seedConstants(struck);
        economyManager.getTaxPolicy().setExpectedLevel(level);
        // ...and the fare dial's cap in today's unit (B6, 0.7.47).
        economyManager.getTaxPolicy().setMoneyUnit(unit > 0 ? unit : 1);
        // ...and the network's fare preview, which prices a ride at it (0.7.45) - and the
        // owners, who weigh a ride at it against a journey's fuel since 0.7.49.
        getInfrastructureManager().setFareLevel(level);
        // ...and the unit its ridership curve reads the dial in founding money at (B6, 0.7.47).
        getInfrastructureManager().setFareUnit(unit > 0 ? unit : 1);
        equity.seedConstants(struck);
        bank.seedConstants(struck);
    }

    /** ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based), the currency's drift, and - since 0.7.43 - the drift every sticky seller's price carries (Retail.stickyPrice(): the shelf and the two rents) at expected inflation a month. Each month after the anchor, and on a load. */
    private void handOnExpectations() {
        businessInvestment.setExpectedInflation(priceIndex.isBased() ? expectations.getExpectedInflation() : 0);
        foreign.setExpectedDrift(anchoredDrift());
        getSectors().retail().setExpected(expectations.getExpectedLevel(), expectations.monthlyExpected());
        getSectors().realEstate().setExpectedMonthly(expectations.monthlyExpected());
    }

    /* =====================================================================
       WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java.
       The Consumption model and its lazy loader stay here because the field
       is Game's: loaded once, on first ask, and a missing consumption.json
       gets an empty model rather than stopping the game.
       ===================================================================== */
    private final Consumption consumption = new Consumption();
    private boolean consumptionLoaded;

    /** Loaded on first ask, so a caller never gets an empty model by arriving early. */
    public Consumption getConsumption() {
        if (!consumptionLoaded) { consumptionLoaded = true; consumption.load(); }
        return consumption;
    }

    /** The basket per head, struck from the household statements each time the shops ask. See CityBasket. */
    private final CityBasket cityBasket = new CityBasket();

    /** The basket's maker, for the harnesses that read past the getter below. */
    public CityBasket getCityBasket() { return cityBasket; }

    /** Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. See CityBasket.perHead(). */
    public java.util.Map<Good, Double> cityBasketPerHead() { return cityBasket.perHead(this); }

    /* =====================================================================
       THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS
       The rest of the world and the rate a year ago, the bank, the figures a
       save carries across a load, what the city and the sectors spent on
       buildings, the managers' getters, the labour repricing, the migrants'
       skills and the month's record in the graph history. Sat under the
       basket's banner until 2026-09-18.
       ===================================================================== */

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

    /**
     * The month across the city's edge, good by good (0.7.35): the
     * businesses' struck statements split by good, an import with no good
     * behind it by the sector that bought it, the households' cars among
     * the cars bought and, since 0.7.62, their imported fuel among FUEL's
     * (with the railway's; both were imports with no good until then) - so
     * it foots to the balance of payments (Sectors.tradeByGood()).
     * The Trade tab's mirrored bars. Pure.
     */
    public Sectors.TradeByGood getTradeByGood() {
        return economyManager.getSectors().tradeByGood(getHouseholdCarImports(), getHouseholdFuelImports());
    }

    /**
     * What is left of the vault once everything owed against it is taken
     * off: the dollar paper outstanding and the foreign money parked in the
     * bank, both claims on the one pot (0.7.35; the Trade tab's own
     * subtraction until then). Hold a hundred, owe eighty, and twenty is the
     * city's whichever dollar came from where. The parked money is the one
     * addition to netForeignPosition(), and the right one: a claim that can
     * be exercised at no notice is heavier than a bond with a date on it.
     * Negative when more is owed than is held.
     */
    public double getOwnReserves() {
        return foreign.getReserves() - foreign.getForeignDebt() - hotMoney.getStock();
    }

    /** The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). */
    public double getSharesHeldAbroad() {
        double total = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) total += equity.getForeignShares(c) * exchange.price(c);
        return total;
    }

    /*
     * WHAT THE CITY HOLDS ABROAD AND WHAT THE WORLD HOLDS HERE (0.7.35):
     * the rough shape of an international investment position, as the Trade
     * tab's holdings card draws it - each side the sum of its parts at
     * today's rate, so the card prints a total the model added up.
     */

    /** The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. */
    public double getHeldAbroadPrivately() {
        return outward.totalLocalValue() + householdBalance.totalAbroadValue();
    }

    /** ...and the vault with it: everything the city holds abroad. */
    public double getHeldAbroad() {
        return getHeldAbroadPrivately() + foreign.getReserves();
    }

    /** What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank and the city's dollar paper at today's rate. */
    public double getHeldHereByTheWorld() {
        return getSharesHeldAbroad() + bondMarket.faceHeldByWorld() + hotMoney.getStock() + foreign.getForeignDebt();
    }

    /**
     * The city's commercial bank - every loan in it, and every default.
     *
     * Owned here rather than by EconomyManager because it lends to all three of
     * them: the sectors, the treasury and the households. See Bank.
     */
    private final Bank bank = new Bank();
    public Bank getBank() { return bank; }

    /**
     * The central bank - the balance sheet money is made on (0.7.0).
     *
     * Beside the commercial bank and the foreign accounts because it deals
     * with both: it takes the bank's spare cash as reserves, lends it at the
     * window, advances the treasury its overdraft, and lists the vault as its
     * own asset. NOT final: buildWorld() founds a fresh one, so a new game
     * after a load cannot inherit the last city's advances - the rule the
     * health service's rebuild taught on 2026-09-19. See CentralBank.
     */
    private CentralBank centralBank = new CentralBank(() -> 0);
    public CentralBank getCentralBank() { return centralBank; }

    /**
     * M2: what the public holds - the bank's deposits, the households', the
     * sectors' and the world's, plus currency, which is none. The Money page's
     * figure, and one definition of it (0.7.0).
     *
     * READ OFF THE STOCKS THEMSELVES, the same three refreshBank() hands the
     * bank as its deposits, rather than off the bank's copy of them: that copy
     * is struck at the settle, before the savers are paid and the owners are
     * paid, so a city saved at the end of the month and reloaded read a
     * different M2 from the one it was saved with - 3% apart in
     * CentralBankCheck's city.
     */
    public double getM2() {
        return getHouseholdDeposits() + getSectorDeposits() + bank.getForeignDeposits()
                + centralBank.getCurrency();
    }

    /** What the households have banked - their savings, which are their deposits. */
    public double getHouseholdDeposits() { return householdBalance.totalSavings(); }

    /** What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). */
    public double getSectorDeposits() {
        double held = 0;
        for (String s : Sectors.KEYS) held += Math.max(0, economyManager.getSectorCash(s));
        return held;
    }

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
     * NOT the receipt field for the build screen (receiptMaterials, once the
     * imports of the last order): that one is assigned on a build order and
     * never cleared, so it holds the last order's figure forever. Subtracting
     * that as imports charged the city
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

    /** This month's adult death rate with the clinics applied. Set by advanceDemographics(). */
    private double lastAdultMortality = AgeBand.ADULT.monthlyMortality();

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
                // After tax, as every sector's is (0.7.7; before tax until then).
                equity.recordMonth(c, bank.getProfitAfterTaxLastMonth(bankProfitTaxRate()), 0);
            } else {
                SectorBooks.SectorMonth m = sectorBooks.get(Equity.COMPANIES[c]);
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
                buildingManager.getConstructionProgressById(),
                buildingManager.getMaterialsOwedById(),
                buildingManager.getContractValueById());
        // ...and who placed each part of it (0.7.19; see OLD CONTRACTS).
        dataSave.setContractRecords(buildingManager.getContractRecords());
        // ...and the order the stacks stand in (0.7.43; DataSave.getStackOrder()).
        dataSave.setStackOrder(buildingManager.stackOrder());
        // ...and the player's hand on the queue (0.7.22): the order, the
        // rushes, the shells, the demolitions, the buy-outs and each run.
        dataSave.setConstructionControl(buildingManager.getControl().toState());
        // ...and the player's decisions (0.7.23; DecisionLog), which nothing
        // else in the save could give back.
        dataSave.setDecisionLog(decisions.toState());

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

        /*
         * EVERY SECTOR, WHOLE, BY NAME (2026-09-11, the sector template): its
         * cash, its stocks, the month in progress, the month last struck, and
         * whatever state is its own - the shops' shelf price, the landlords'
         * rents, the builders' order book. And every market's price. This
         * replaced five differently-shaped report arrays, two arrays indexed
         * by BuildingType.ordinal(), and a dozen loose fields. See SectorState.
         */
        dataSave.setSectors(economyManager.getSectorStates());
        dataSave.setMarkets(economyManager.getMarketStates());

        // Policy: the rates, every offset, the protected sectors, and the
        // month's VAT ledger.
        dataSave.setTaxPolicyState(economyManager.getTaxPolicy().getPolicyState());
        dataSave.setSectorOffsets(economyManager.getTaxPolicy().getSectorOffsets());
        dataSave.setHouseholdBalance(householdBalance.toSaveArray());
        dataSave.setHouseholdCells(householdBalance.cellKeys(),
                householdBalance.toCellSaveArray());
        // ...and the graduates the next census carries the loans of (0.7.63).
        dataSave.setHouseholdGraduates(householdBalance.graduatesToSave());
        dataSave.setBankCash(bank.getCash());
        dataSave.setBankBranchesCapitalised(bank.getBranchesCapitalised());
        dataSave.setBankProfitLastMonth(bank.getProfitLastMonth());
        dataSave.setBankDepositRate(bank.depositRate());
        dataSave.setBankSolvency(bank.solvencyToSave());
        dataSave.setBankLastMonth(bank.lastMonthToSave());
        // ...and what the bank's prices are struck from, and what it earned
        // after its close (0.7.7).
        dataSave.setBankPricingHistory(bank.pricingHistoryToSave());
        dataSave.setBankLateProfit(bank.lateProfit());
        // ...and what it has set aside, the record its capital target is
        // struck from, and the month's statement lines (0.7.8).
        dataSave.setBankAllowance(bank.allowanceToSave());
        dataSave.setBankCapitalRecord(bank.capitalRecordToSave());
        dataSave.setBankMonthLines(bank.monthLinesToSave());
        // ...and its year of statements (0.7.9), which last month's column
        // and the last twelve months are read from.
        dataSave.setBankStatementYear(bank.statementYearToSave());
        // ...and its balance sheet at the top of each of the last twelve
        // months (0.7.13), which the Balance sheet page's year-ago column reads.
        dataSave.setBankSheetYear(bank.sheetYearToSave());
        // ...and its equity in two parts at the top of the month (round 2),
        // by name; nothing on a bank loaded from a save that kept neither.
        dataSave.setBankPaidInOpening(bank.knowsEquitySplit() ? bank.paidInOpening() : null);
        dataSave.setBankRetainedOpening(bank.knowsEquitySplit() ? bank.retainedOpening() : null);
        // ...and the city's preferred and its warrants (0.7.14), block by
        // block, with the arrears and their record.
        dataSave.setBankPreferred(new java.util.ArrayList<>(bank.getPreferred()), bank.preferredRecordToSave());
        // ...and the city's fund: its cash, its dial, its rescue setting, the
        // pending offer and its clocks, its rescues and the player's orders.
        // Its shares ride the register, its bonds the bonds.
        dataSave.setFund(fund.toState());
        dataSave.setDebtMarket(debtManager.marketToSave());
        dataSave.setHousingOccupancy(new double[]{
                getSectors().realEstate().getOccupiedHomes() });
        dataSave.setForeignAccounts(foreign.toSaveArray());
        dataSave.setCentralBank(centralBank.toSaveArray());
        dataSave.setTreasuryArrears(arrears.keySet().toArray(new String[0]),
                arrears.values().stream().mapToDouble(Double::doubleValue).toArray());
        dataSave.setForeignStanding(debtManager.foreignStandingToSave());
        dataSave.setCapitalFlows(hotMoney.toSaveArray());
        dataSave.setOutwardInvestment(outward.toSaveArray());
        dataSave.setEquity(equity.keys(), equity.toSaveArray());
        dataSave.setExchangeState(exchange.toState());
        dataSave.setPriceIndex(priceIndex.toSaveArray());
        // ...and the anchor (0.7.42), under its own key.
        dataSave.setExpectations(expectations.toSaveArray());
        dataSave.setWorldEconomy(world.toSaveArray());
        dataSave.setTradedExchangeRate(economyManager.getExchangeRate());
        dataSave.setPolicyRate(debtManager.getPolicyRate());
        dataSave.setPolicyAutopilot(debtManager.isAutopilot());
        // ...and the treasury's rollover (0.7.13): its setting by name, the
        // ledger of what it netted, and its record.
        dataSave.setRolloverMode(rollover.getMode().name());
        dataSave.setRolloverLedger(rollover.ledgerToSave());
        dataSave.setRolloverRecord(rollover.recordToSave());
        // ...and how the land office pays (0.7.6), the player's toggle.
        dataSave.setLandPaidFromVault(landPaidFromVault);
        // ...and how the city was founded (0.7.10): its name, its money, the
        // treasury and the vault. The world's mean rides in its own array.
        dataSave.setFounding(founding);
        // ...and the target the rule aims at (0.7.4), under its own key, and
        // how strictly it holds it (0.7.52), by name.
        dataSave.setInflationTarget(debtManager.getInflationTarget());
        dataSave.setPolicyStrictness(debtManager.getStrictness().name());
        // The holdings dial, the households' paper ratio, and what a buyback
        // between the presses still has to declare (0.7.1), each under its own key.
        dataSave.setQeTargetShare(centralBank.getTargetShare());
        // ...and the advances ceiling dial beside it (0.7.2), under its own key.
        dataSave.setAdvancesCeilingMonths(centralBank.getAdvancesCeilingMonths());
        dataSave.setHouseholdPaperRatio(householdBalance.getPaperRatio());
        // ...and their bonds' ratio (0.7.12), which the plan reads too.
        dataSave.setHouseholdBondRatio(householdBalance.getBondRatio());
        dataSave.setBuybackUnsettled(buybackToHouseholdsUnsettled, buybackAbroadUnsettled);
        dataSave.setCostOfLiving(labourMarket.getCostOfLiving());
        /*
         * THE MOTORING, in the two figures the road cannot rebuild. The cars
         * themselves ride in the household cells; these are the network's
         * reading of them - the ownership rate, because the cells are not back
         * yet when the road ratio is first struck on the load path, and the
         * remembered commute, because it is a lagged average of months that
         * are gone. See DataSave.rememberedCommute.
         */
        dataSave.setCarsPerHousehold(householdBalance.carsPerHousehold());
        // ...and the month's transit bill as 6d struck it (0.7.49). See carriedTransitBill.
        dataSave.setTransitBill(economyManager.getTransitBill());
        // ...and the commute as 6d struck it, not re-read from the cells (0.7.49). See carriedCaptiveShare.
        dataSave.setCaptiveShare(getInfrastructureManager().getCaptiveShare());
        dataSave.setFuelPerJourney(getInfrastructureManager().getFuelPerJourney());
        // ...and the drivers' fuel as 6d drew it (0.7.62). See carriedFuelMonth.
        dataSave.setHouseholdFuel(new double[] { motoring.getFuelBill(), motoring.getFuelImports(), motoring.getFuelLitres() });
        dataSave.setRememberedCommute(
                getInfrastructureManager().getRememberedThroughput());
        dataSave.setDenomination(denomination.toSaveArray());
        dataSave.setBankTaxCharged(economyManager.getBankTax());
        dataSave.setRentWeight(families.rentWeight());
        dataSave.setRentWeightStudio(families.studioRentWeight());
        dataSave.setSubsidisedSectors(getSubsidisedSectors());
        dataSave.setSalesTax(economyManager.getSalesTaxState());

        // The city's land on the world, the offers standing and what has been
        // taken out of its ground, with the world's totals (0.7.57); its
        // blocks and holdings since 0.7.67 (SAVE_FORMAT 32).
        CityLand land = landManager.getCityLand();
        dataSave.setCityLand(land.centreState(), land.centreRectsState(), land.holdingsState(), land.partFieldsState(),
                land.convertedState(), landManager.getMarket().getOffersState(), landManager.getMarket().getNextOfferId(),
                landManager.getDepletionState(), landManager.getWorldTotalsState(),
                landManager.getWorldSeaTheta());
        dataSave.setLandMarketPrices(landManager.getMarket().getPriceState());
        // ...and the city's water rights (0.7.59): THE FRESH WATER LIMIT AND THE COAST.
        dataSave.setFreshRights(freshRights);
        dataSave.setConstructionShedding(constructionShedMonth, constructionShedPoints);

        dataSave.setNationalAccounts(economyManager.getNationalAccountsState());

        // History, not state: what the city lost and what its lenders wrote off.
        dataSave.setDemolitions(demolitionLog.all());
        dataSave.setBuilds(buildLog.all());
        dataSave.setBandNames(PopulationCohorts.saveBands());
        dataSave.setShapeNames(FamilyModel.saveShapes());
        dataSave.setCohorts(cohorts.toSaveArray());
        dataSave.setFamilies(families.toSaveArray());
        dataSave.setMigration(migration.toSaveArray());
        dataSave.setUnemployment(unemployment.toSaveArray());
        dataSave.setSickness(sickness.getState());
        dataSave.setCrime(crime.getState());
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
        dataSave.setSubsidyPaid(new java.util.LinkedHashMap<>(subsidyPaid));
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
        dataSave.setCreditStatements(
                economyManager.getBusinessDebtManager().getStatementsToSave());
        // ...and the mortgages' month and the insurance book (0.7.11).
        dataSave.setMortgageRepaid(
                economyManager.getBusinessDebtManager().getMortgageRepaidToSave());
        dataSave.setInsurancePremiums(
                economyManager.getBusinessDebtManager().getPremiumsTotalToSave());
        dataSave.setInsuranceClaims(
                economyManager.getBusinessDebtManager().getInsuredWrittenOffTotals());
        // ...and the businesses' bonds (0.7.12): every bond and who holds it,
        // every book's resting orders, the market's month and its record; and
        // what defaults have taken off the bonds over the city's life.
        dataSave.setBondMarket(bondMarket.toState());
        // ...and each cell's own bonds, by the cell's name (round 2).
        dataSave.setHouseholdBondsByCell(householdBalance.bondsByCellToSave());
        dataSave.setBondWrittenOff(economyManager.getBusinessDebtManager().getBondWrittenOffTotals());

        dataSave.setConstructionMaterials(buildingManager.getConstructionMaterials());
        dataSave.setPopulation(populationManager.getPopulation());

        // Not derivable from the population beside it: the month was worked by
        // the people who lived here when it started. See
        // PopulationManager.restoreWorkforce().
        dataSave.setWorkforce(populationManager.getWorkforceForSave());
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
        dataSave.setTreasuryJournal(treasuryJournal.closedLabels(), treasuryJournal.closedAmounts(),
                treasuryJournal.pendingLabels(), treasuryJournal.pendingAmounts());
        dataSave.setTreasuryRaisedPending(treasuryRaisedSoFar);
        dataSave.setCityPaperUnsettled(getCityPaperUnsettled(), cityDiscountThisMonth + cityDiscountForBank);
        dataSave.setGovernmentMonth(economyManager.governmentMonthToSave());
        dataSave.setReports(reports);
        dataSave.setGraphs(graphs);
        dataSave.setSlotName(slotName);
        // The city map's sidecar (0.7.60), written after the save; its stamp in the save, none when there is no map.
        byte[] mapBytes = null;
        if (cityMap != null) {
            try {
                mapBytes = cityMap.writeSidecar(month);
            } catch (RuntimeException e) {
                System.out.println("The city map could not be written: " + e);
            }
        }
        dataSave.setMapStamp(mapBytes != null ? cityMap.lastStamp() : null);
        dataSave.stamp(GameVersion.VERSION, GameVersion.SAVE_FORMAT,
                System.currentTimeMillis());

        lastSaveResult = dataSave.saveGame(gameFiles, slot);
        if (mapBytes != null && lastSaveResult.ok) {
            GameFiles.Result map = gameFiles.write(gameFiles.mapFile(slot), mapBytes);
            if (!map.ok) System.out.println(map.message());
        }

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

    /**
     * Writes the run out as plain text, one row a year and one row a decade,
     * and each book's two tables again as CSV beside it (0.7.16).
     *
     * Two books rather than one because they answer different questions: a
     * 333-year run is a quarter of a megabyte a year at a time and a tenth of
     * that by decade, and the reader who is short of room should not have to
     * throw away the detail to get the shape.
     *
     * Each book is built once (YearBook.Book) and its text and its CSV files
     * are written from that one object, so the files of one export cannot
     * disagree with each other.
     *
     * Returned rather than thrown - one array a book, in the order {year,
     * decade}, and each {text, table, within} - because the screen has to say
     * something either way - see GameFiles.Result.
     */
    public GameFiles.Result[][] writeBooks() {
        HistorySave history = getHistorySave();
        YearBook.Book year   = YearBook.yearBook(history, getCurrency());
        YearBook.Book decade = YearBook.decadeBook(history, getCurrency());
        return new GameFiles.Result[][] {
            {
                gameFiles.write(gameFiles.yearBookFile(),      year.text()),
                gameFiles.write(gameFiles.yearBookCsv(),       year.table().csv()),
                gameFiles.write(gameFiles.yearBookWithinCsv(), year.within().csv())
            },
            {
                gameFiles.write(gameFiles.decadeBookFile(),      decade.text()),
                gameFiles.write(gameFiles.decadeBookCsv(),       decade.table().csv()),
                gameFiles.write(gameFiles.decadeBookWithinCsv(), decade.within().csv())
            }
        };
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
        // Principal falling due is a promise (0.7.0): paid whatever the
        // treasury holds, advances past the ceiling included. See treasuryPays().
        treasuryPays(TreasuryLine.PRINCIPAL, amount);
        cityPrincipalRepaidThisMonth += amount;
        // ...to the bank, which is who this reaches at the settle; the other
        // holders' shares are paid by payDomesticPrincipal() (0.7.1).
        bankPrincipalRepaidThisMonth += amount;
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
    public double getForeignPrincipalRepaidThisMonth() { return foreignPrincipalRepaidThisMonth; }
    public double getForeignInterestPaidThisMonth()    { return foreignInterestPaidThisMonth; }

    /**
     * A slice of USD principal, repaid.
     *
     * @param usd what the contract says, in the currency it says it in
     */
    public void repayForeignPrincipal(double usd) {
        double local = usd * foreign.getRate();
        treasuryPays(TreasuryLine.FOREIGN_PRINCIPAL, local);
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
        treasuryPays(TreasuryLine.FOREIGN_INTEREST, local);
        foreignInterestPaidThisMonth += local;
    }

    /*
     * WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR.
     *
     * Every issue lands between two presses - the finance screen, the build
     * screen's funding (and the emergency note, until 0.7.0) - and
     * adds what the treasury received here. The top of the next month hands
     * it to the settlement (cityDebtRaisedForBank) and clears it; see THE
     * BANK PAYS FOR THE CITY'S PAPER. SAVED since 2026-09-21, with the
     * discount beside it: a city saved between the issue and the settle has
     * already been paid for paper its bank has not paid for, and a reload
     * that dropped the figure would hand the bank the bond for nothing - the
     * very bug the settlement exists to end, by the load path.
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
     *
     * SINCE 0.7.1 THE PAPER CARRIES ITS OWN DISCOUNT (Debt.getIssueDiscount())
     * and the bank earns it as it accretes, so the settle no longer books this
     * figure. It is still kept and saved, under the key batch A gave it, for
     * what it tells a load: a save from 0.7.0 taken between an issue and its
     * settle carries paper with no discount of its own, and this is what its
     * bank is owed at the settle (legacyDiscountDue).
     */
    private double cityDiscountThisMonth;
    private double cityPrincipalRepaidThisMonth;

    /**
     * What the treasury raised, as it stood when the month began.
     *
     * cityDebtRaisedThisMonth is cleared at the top of nextMonth(), so by the
     * time the bank settles at the bottom it holds this month's issuance and
     * not the one the bank is being told about. Snapshotted rather than read -
     * and snapshotted BEFORE the clear since 2026-09-21, which is the whole of
     * the fix: from 0.4.3 at least to 0.6.10 the snapshot sat after the
     * clear and read zero, and the bank never paid for a bond.
     * Zeroed again once the settle has paid, so what the bank still owes for
     * paper is exactly getCityPaperUnsettled().
     */
    private double cityDebtRaisedForBank;
    private double cityDiscountForBank;

    /** A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. Set only by the load path; see cityDiscountThisMonth. */
    private double legacyDiscountDue;

    /**
     * What the bank owes the treasury for paper it has taken and not yet
     * settled: sold between the presses and not yet paid for at the bottom of
     * a month. MoneyAudit carries it against the bank's pool, because the
     * treasury already holds the cash - so an issue between two months moves
     * no money in or out of the city's pools, and neither does the settle.
     */
    public double getCityPaperUnsettled() { return cityDebtRaisedThisMonth + cityDebtRaisedForBank; }

    /**
     * What the bank handed the treasury for its paper at this month's settle.
     * A flow of the month, read by LongPlaytest as the month ends and by
     * nothing in the game; zero on a freshly loaded city until the next
     * settle, which is what a month nobody has struck reads.
     */
    private double cityPaperSettled;
    public double getCityPaperSettled()      { return cityPaperSettled; }
    double getCityDebtRaisedThisMonth()      { return cityDebtRaisedThisMonth; }
    public double getCityPrincipalRepaidThisMonth() { return cityPrincipalRepaidThisMonth; }
    /** ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). */
    public double getBankPrincipalRepaidThisMonth() { return bankPrincipalRepaidThisMonth; }
    private double bankPrincipalRepaidThisMonth;

    /* =======================================================================
       THE HOLDERS ARE PAID (0.7.1)

       Jerus, on the holders: "yes households should be able to hold." Until
       0.7.1 every coupon and every dollar of principal on the city's own paper
       went to the bank, because the bank held all of it. Now a piece of paper
       says who holds it (Debt, WHO HOLDS IT), and what it pays is split by
       the shares struck BEFORE the payment:

         - the BANK's share goes the way it always went: the coupon into the
           month's interest accrual, paid with next month's budget and taken
           at that settle; the principal to the bank at this one;
         - the HOUSEHOLDS' share is paid now, from the treasury into their
           savings - the coupon as investment income, untaxed as the foreign
           coupon is, the principal with their paper down by the same - and
           declared to the audit, because households are outside its pools;
         - the CENTRAL BANK's share is paid now too, and destroyed: its income,
           remitted back to the treasury the month after - "debt to itself".

       THE TREASURY'S BILL IS THE WHOLE BILL. The government's books are
       struck on cityInterestPaid, which carries every holder's coupon, the
       way the foreign coupon is put back on them (payForeignInterest()); and
       the bridge's repaid row carries every holder's principal.
       ======================================================================= */

    /** Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. */
    private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper;

    public double getCouponsToHouseholds()   { return couponsToHouseholds; }
    public double getPrincipalToHouseholds() { return principalToHouseholds; }
    public double getHouseholdsBoughtPaper() { return householdsBoughtPaper; }

    /**
     * Of a payment on this paper, the households' and the central bank's
     * shares, struck before it: each holder's principal over what is
     * outstanding, times the payment. One line since 0.7.3 - there were two
     * branches, for a payment at or past the principal and one under it, and
     * they were the same product (a floating-point multiply commutes, so
     * hh x owed and owed x hh are the same double) under a test that chose
     * between them.
     */
    private double[] holderShares(Debt paper, double owed) {
        double out = paper.getOustandingPrincipal();
        double hh = paper.householdPrincipal(), cb = paper.centralBankPrincipal();
        if (!(out > 0) || !(owed > 0) || hh + cb <= 0) return new double[] { 0, 0 };
        return new double[] { owed * hh / out, owed * cb / out };
    }

    /** A coupon on the city's own paper, split by holder. See THE HOLDERS ARE PAID. */
    public void payDomesticCoupon(Debt paper, double owed) {
        double[] s = holderShares(paper, owed);
        InterestExpense(owed - s[0] - s[1]);
        double outside = s[0] + s[1];
        if (!(outside > 0)) return;
        double paid = treasuryPays(TreasuryLine.INTEREST, outside);
        cityInterestPaid += paid;
        householdBalance.creditPaperCoupon(s[0]);
        couponsToHouseholds += s[0];
        centralBank.takeCoupon(s[1]);
    }

    /** Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. */
    public void payDomesticPrincipal(Debt paper, double owed) {
        double[] s = holderShares(paper, owed);
        subtractCash(owed - s[0] - s[1]);
        double outside = s[0] + s[1];
        if (!(outside > 0)) return;
        treasuryPays(TreasuryLine.PRINCIPAL, outside);
        cityPrincipalRepaidThisMonth += outside;
        paper.moveToHouseholds(-s[0]);
        paper.moveToCentralBank(-s[1]);
        householdBalance.creditPaperPrincipal(s[0]);
        principalToHouseholds += s[0];
        centralBank.takePrincipal(s[1]);
    }

    /* ----------------------- the desk, for the households ----------------------- */

    /**
     * THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the
     * spread gone, or a household on its way out of the city: this much face
     * for this much cash, off every piece's household share pro rata, onto
     * the bank's book with the unearned discount riding on it. The cash
     * leaves the bank's pool for a household - declared by MoneyAudit as
     * "- desk PaperBoughtFromHouseholds".
     */
    private void desksBuysHouseholdPaper(double face, double cash) {
        double held = debtManager.householdPrincipal();
        if (!(face > 0) || held <= 0) return;
        double take = Math.min(face, held);
        double unearned = 0;
        for (Debt d : debtManager.getDebt()) {
            double mine = d.householdPrincipal();
            if (mine <= 0) continue;
            double off = mine >= held ? take : take * mine / held;
            off = Math.min(off, mine);
            unearned += d.unaccretedOn(off);
            d.moveToHouseholds(-off);
        }
        bank.buyPaperFromHouseholds(cash, take, unearned);
    }

    /* -------------------- THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) --------------------

       At the settle of an issue - the month after the treasury sold it -
       before the bank pays and before the month's coupon, so the first coupon
       and the first month of the discount are split by the holders the paper
       settled to. Each piece of paper still owed for (Debt.getSettleDue())
       is offered at its issue yield; the households want the share of it
       HouseholdBalance.paperShareAt() gives against the deposit rate, and
       take what their savings past the cushion can pay, pro rata across the
       pieces and the cells. Their cash is what the bank does not have to pay:
       the treasury was paid at issue, so it is the bank's settle that shrinks.
       Their paper is the face they bought at the issue's price - face times
       cash over received, so the discount is theirs to earn at maturity.
       --------------------------------------------------------------------------- */
    /** A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. Null in play. */
    java.util.function.Consumer<Boolean> settleProbeForTest;

    private double householdsTakeTheirShare() {
        java.util.List<Debt> settling = new java.util.ArrayList<>();
        double wanted = 0;
        double depositRate = bank.depositRate();
        for (Debt d : debtManager.getDebt()) {
            if (d.isForeign() || d.getSettleDue() <= 0) continue;
            settling.add(d);
            wanted += HouseholdBalance.paperShareAt(d.getIssueYield(), depositRate) * d.getSettleDue();
        }
        double paid = 0;
        if (wanted > 0) {
            double spare = householdBalance.spareForPaper();
            double take = Math.min(wanted, spare);
            if (take > 0) {
                // The face per dollar is the mix they buy: every settling
                // piece in proportion to what they want of it - of the face
                // the market bought, the central bank's add-on at issue being
                // its own and paid for (0.7.15, round 2; the whole face on a
                // piece it took none of).
                double face = 0;
                for (Debt d : settling) {
                    double cash = take * HouseholdBalance.paperShareAt(d.getIssueYield(), depositRate)
                            * d.getSettleDue() / wanted;
                    face += (d.getOustandingPrincipal() - d.centralBankPrincipal()) * cash / d.getSettleDue();
                }
                paid = householdBalance.buyAtIssue(take, face / take);
                double scale = take > 0 ? paid / take : 0;
                for (Debt d : settling) {
                    double cash = take * HouseholdBalance.paperShareAt(d.getIssueYield(), depositRate)
                            * d.getSettleDue() / wanted;
                    d.moveToHouseholds((d.getOustandingPrincipal() - d.centralBankPrincipal()) * cash
                            / d.getSettleDue() * scale);
                }
            }
        }
        for (Debt d : settling) d.settled();
        householdsBoughtPaper += paid;
        return paid;
    }

    /* ======================================================================
       THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1)

       Jerus: "the central bank would buy gbonds or sell gbonds from thin air
       basically ... like QE and QT, just like USA does." Once a month, after
       the treasury has settled with its central bank (settleTreasury()) and
       before anything is priced (startOfMonthUpdate()), the central bank
       moves its holding of the city's term paper toward its dial: the target
       is CentralBank.getTargetShare() of the term paper outstanding, and the
       step is at most CentralBank.QE_SPEED of the larger of the dial and the
       setting before it (CentralBank.stepFor()) - so a move from one setting
       to another takes four months at most, buying or selling, and exactly
       four from nothing or to nothing. After last month's settle, so the
       commercial bank owns everything it sells - paper still owed for
       (getSettleDue()) is not offered; and before the market is priced, so
       this month's quotes carry the compression it buys. Pro rata across the
       bank's term paper when buying and across its own when selling, at the
       curve's market value - settled paper either way, since what it took at
       issue this month (0.7.15, round 2, just before this step) is still owed
       for by the market until the settle.

       ...READ AS THE BOOK STANDS ONCE THE MONTH'S MATURITY IS PAID (0.7.15,
       round 2). In a month a piece falls due the book carries both the
       maturing principal, paid at the bottom of the month
       (processAllDebts()), and its replacement, sold at the press and, for
       the central bank's par, added on at issue just before this step (THE
       CENTRAL BANK ROLLS ITS OWN, AT ISSUE) - on the issue date the Fed's
       book holds one or the other, never both. Counted twice, a central
       bank holding more or less of a maturing piece than its dial would
       sell the difference to the bank that month and buy it back the next.
       So the target and the holding are read net of the principal the month
       pays, the central bank's share of it off its holding; the pace is
       struck on the paper outstanding, as before. And a piece paying
       principal this month is not traded - neither from the bank nor from
       the households, nor sold: its payment is split by the holders it
       opened the month with (holderShares()), so face bought of it would
       come back as principal before the month was out and leave the book
       under the dial it was bought toward. A month with nothing falling due
       reads and trades exactly as it did.

       ...AND FROM THE HOUSEHOLDS, ONCE THE BANK HAS NONE TO SELL (0.7.15).
       Jerus lifted the dial to the whole of the paper (CentralBank
       .MAX_QE_SHARE), and the households hold up to half of every issue
       (HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE), so a dial past what the
       bank holds could not be reached from the bank alone. What the step
       still wants once the bank's settled term paper is gone it buys from the
       households' term paper, pro rata across the pieces, at the same curve's
       market value, and the cells give up their paper and take the price
       into their savings pro rata, by the path a buyback's households' share
       takes (HouseholdBalance.creditPaperBuyback()). The money is made for
       it, as for the bank's: MoneyAudit sees it made (+ centralbank
       BoughtPaper) and paid out to the households (- centralbank
       BoughtFromHouseholds). SELLING stays as it was: to the bank only.
       ====================================================================== */
    private void openMarketOperation() {
        // Nothing to aim at and nothing held: no move, as it always read.
        if (!(centralBank.getTargetShare() > 0) && !(centralBank.getPaperHeld() > 0)) return;
        double term = debtManager.termPrincipal();
        // What this month's processAllDebts() repays of the term paper, and
        // the central bank's share of it (0.7.15, round 2): nothing in a month
        // with nothing falling due.
        double dueTerm = 0, dueHeld = 0;
        for (Debt d : debtManager.getDebt()) {
            if (!DebtManager.isTermPaper(d)) continue;
            double principal = d.principalDueNextMonth();
            if (!(principal > 0)) continue;
            dueTerm += principal;
            dueHeld += centralBankShareOf(d, principal);
        }
        double held = centralBank.getPaperHeld() - dueHeld;
        double target = centralBank.getTargetShare() * (term - dueTerm);
        double step = centralBank.stepFor(term);
        double move = Math.max(-step, Math.min(step, target - held));
        if (Math.abs(move) <= 1e-9 * Math.max(1, term)) return;
        java.util.List<Debt> pieces = new java.util.ArrayList<>();
        double pool = 0;
        for (Debt d : debtManager.getDebt()) {
            if (!DebtManager.isTermPaper(d)) continue;
            double available = d.getSettleDue() > 0 || d.principalDueNextMonth() > 0 ? 0
                    : move > 0 ? d.bankPrincipal() : d.centralBankPrincipal();
            if (available <= 0) continue;
            pieces.add(d);
            pool += available;
        }
        double face = Math.min(Math.abs(move), pool);
        if (face > 0) {
            double price = 0, unearned = 0;
            double[] faceOf = new double[pieces.size()];
            for (int i = 0; i < pieces.size(); i++) {
                Debt d = pieces.get(i);
                double available = move > 0 ? d.bankPrincipal() : d.centralBankPrincipal();
                double f = face >= pool ? available : face * available / pool;
                faceOf[i] = f;
                double out = d.getOustandingPrincipal();
                price += debtManager.marketValue(d) * f / out;
                unearned += d.unaccretedOn(f);
            }
            for (int i = 0; i < pieces.size(); i++) {
                pieces.get(i).moveToCentralBank(move > 0 ? faceOf[i] : -faceOf[i]);
            }
            if (move > 0) {
                centralBank.buyPaper(price, face);
                bank.sellPaperToCentralBank(price, face, unearned);
            } else {
                centralBank.sellPaper(price, face);
                bank.buyPaperFromCentralBank(price, face, unearned);
            }
        }
        if (move > 0 && face < move) buyPaperFromHouseholds(move - Math.max(0, face));
    }

    /**
     * The rest of a purchase the bank could not fill, from the households'
     * term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH):
     * pro rata across the pieces they hold that are settled and pay no
     * principal this month (round 2), at the curve's market value, off their
     * cells pro rata into their savings.
     */
    private void buyPaperFromHouseholds(double wanted) {
        java.util.List<Debt> pieces = new java.util.ArrayList<>();
        double pool = 0;
        for (Debt d : debtManager.getDebt()) {
            if (!DebtManager.isTermPaper(d) || d.getSettleDue() > 0 || d.principalDueNextMonth() > 0) continue;
            double available = d.householdPrincipal();
            if (available <= 0) continue;
            pieces.add(d);
            pool += available;
        }
        double face = Math.min(wanted, pool);
        if (!(face > 0)) return;
        double price = 0;
        double[] faceOf = new double[pieces.size()];
        for (int i = 0; i < pieces.size(); i++) {
            Debt d = pieces.get(i);
            double available = d.householdPrincipal();
            double f = face >= pool ? available : face * available / pool;
            faceOf[i] = f;
            price += debtManager.marketValue(d) * f / d.getOustandingPrincipal();
        }
        for (int i = 0; i < pieces.size(); i++) {
            pieces.get(i).moveToHouseholds(-faceOf[i]);
            pieces.get(i).moveToCentralBank(faceOf[i]);
        }
        centralBank.buyPaperFromHouseholds(price, face);
        householdBalance.creditPaperBuyback(face, price);
    }

    /* ------------- THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) -------------

       Jerus chose "Build the rollover fix" for round 1's Needs Jerus 2, "the
       way the Fed does". Until then a central bank holding the city's paper
       was repaid its share of a maturing piece, the rollover sold the
       replacement to the commercial bank - the residual buyer - and the
       holdings dial bought it back from the bank over the months after. A
       bank whose paper the central bank had bought held little else, so it
       funded the whole issue at the window, charged before the first coupon
       against a month's straight-line accretion, and failed on it (held 25%,
       dial 100%, seed 5, m2668: $44.1B of face for $14.7B on $69.8M of
       equity). In 0.7.15's first ensembles 51 of the 76 failures the dial
       added at 100% fell in or just after a month the city issued paper at
       home, and all 134 it added at 50% - though 71 of those 134 were next
       to paper the playtest sold to fund the bank's preferred, not a roll.

       THE RULE, the Federal Reserve Bank of New York's "FAQs: Treasury
       Rollovers": "the Desk rolls over the SOMA's maturing Treasury security
       holdings by replacing maturing holdings with securities issued at
       Treasury auctions ... by placing non-competitive bids at Treasury
       auctions equal in par amount to the value of holdings maturing on the
       issue date of the securities being auctioned, allocated proportionally
       across those securities by announced offering amount"; "Bids at
       Treasury auctions are placed as non-competitive tenders and are treated
       as add-ons to announced auction sizes"; "Noncompetitive bidders receive
       the stop-out rate, yield or discount margin determined by the
       competitive auction process." And what they roll into: "SOMA holdings
       of Treasury notes, bonds, ... are exchanged at auction across all
       Treasury notes, bonds, TIPS, and FRNs issued on that day" - coupon
       paper into coupon paper, which here is term paper into term paper, the
       only paper the dial holds (DebtManager.isTermPaper()).

         - WHAT: its par in what falls due - each maturing piece's principal
           times the share of it the central bank holds, the split the payment
           itself makes (holderShares()) - less what runs off past its dial
           and what last year's surplus pays off (both below).
         - WHEN: struck at the press (rollMaturities(), before the rollover
           books anything, on the book rolloverPlan() reads) and taken in the
           month the maturity is paid - the issue date - inside the audit's
           window (rollCentralBankAtIssue()): after the treasury's settle with
           its central bank, so the money waits for the maturity as the
           market's proceeds do rather than repaying advances the maturity
           then draws again; before the holdings step, which reads the book as
           it stands once the month's maturity is paid - the replacement in,
           the maturing slice out (openMarketOperation()); and before
           processAllDebts() pays it the maturing par as it always has
           (payDomesticPrincipal(), money destroyed).
         - ON TOP: the rollover sizes its issues for the rest (rolloverPlan():
           what falls due less its par, less S), and its par is
           added on to the city's own term paper sold between the two presses
           - the rollover's issues, and any the player sold by hand, which is
           the same situation - pro rata to their face, the Fed's "by
           announced offering amount" (Debt.addOnForCentralBank()), each at
           its issue's price on each unit of face (what the treasury was paid
           over the face the market bought), in money made for it
           (CentralBank.buyAtIssue()). MoneyAudit's "+ centralbank
           BoughtPaper" sees it arrive in the treasury's pool, and nothing
           else is declared. What it paid under par is its gain against face,
           in the month's profit and back to the treasury as the remittance.
         - NOTHING TO ADD IT ON TO: with the rollover on and none of the
           city's term paper sold between the presses - its dial at 100%,
           where the market's part is nothing, or the rest too small to
           arrange, or rolled into notes - its par is the issue's whole size:
           the paper each maturing piece rolls into in the same structure,
           priced at the rollover's own quote for it at the press, held by it
           from issue (strikeParAlone()). The source has no auction without a
           public part; this is the add-on on top of a market sized at
           nothing. A par whose quote brings nothing is not arranged. With the
           rollover by hand and nothing sold there is no issue at all, and its
           holding runs off: the treasury repays it out of cash, as it pays
           what falls due by hand.
         - ONLY WHILE ITS DIAL IS AT OR ABOVE WHAT IT HOLDS. Under its QT - the
           dial below what it holds - the maturing holding runs off, as the
           Fed's did: the FOMC's "Plans for Reducing the Size of the Federal
           Reserve's Balance Sheet" (May 4, 2022), "principal payments from
           securities held in the SOMA will be reinvested to the extent that
           they exceed monthly caps". Read at the margin with the dial for
           the cap: what it holds past the dial's share of the term paper
           (past the holdings step's own tolerance) runs off, up to its whole
           par, and the rest is rolled (centralBankRolls()) - so a holding a
           dollar over its dial lets a dollar run off, not its whole par, and
           is not left under its dial to buy the rest back from the bank.
         - THE SURPLUS PAYS EVERYONE (round 3). Jerus: "Surplus pays
           everyone". Last year's surplus, S, is struck on all of what falls
           due (Rollover.netting(), one ledger, so it is used once) and pays
           the market's part first - what falls due less what the central
           bank would roll - and then the central bank's par. What it pays of
           the par is repaid it at the maturity out of the treasury's cash,
           and the add-on is what is left (rolloverPlan(), Rollover.Plan
           .centralBankNetted()). A surplus the size of what falls due pays it
           all and no add-on is issued, so a city in surplus clears its paper
           whoever holds it. A surplus that pays only part of the central
           bank's par shrinks the paper under what it rolls, so it can end the
           month over its dial, and the holdings step sells the excess to the
           bank as it sells any holding over the dial - left as built, Jerus:
           "Leave it (as built)".
       ------------------------------------------------------------------------------ */

    /** What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). Not saved - both happen inside one nextMonth(), and a press strikes it afresh. */
    private double centralBankTender;

    /** ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. */
    private final java.util.List<ParAlone> centralBankAlone = new java.util.ArrayList<>();

    /** Its par alone in one paper, on the terms of the rollover's quote for it. */
    private record ParAlone(RollsInto into, double par, DebtQuote quote) { }

    /** Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). */
    private static double centralBankShareOf(Debt paper, double principal) {
        double held = paper.centralBankPrincipal(), out = paper.getOustandingPrincipal();
        if (paper.isForeign() || !(held > 0) || !(out > 0) || !(principal > 0)) return 0;
        return principal * held / out;
    }

    /** The central bank's par in what falls due next month, whether it rolls it or not. */
    public double centralBankParFallingDue() {
        if (!(centralBank.getPaperHeld() > 0)) return 0;
        double par = 0;
        for (Debt paper : debtManager.getDebt()) par += centralBankShareOf(paper, paper.principalDueNextMonth());
        return par;
    }

    /**
     * How far the central bank's holding is over its dial: what it holds past
     * the dial's share of the term paper, or nothing within the holdings
     * step's own tolerance (openMarketOperation()).
     */
    public double centralBankOverItsDial() {
        double term = debtManager.termPrincipal();
        double over = centralBank.getPaperHeld() - centralBank.getTargetShare() * term;
        return over > 1e-9 * Math.max(1, term) ? over : 0;
    }

    /** Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. */
    private double centralBankRolls(double par) {
        if (!(par > 0)) return 0;
        return par - Math.min(par, centralBankOverItsDial());
    }

    /** True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. */
    private boolean termPaperSoldBetweenPresses() {
        for (Debt d : debtManager.getDebt()) if (DebtManager.isTermPaper(d) && d.getSettleDue() > 0) return true;
        return false;
    }

    /**
     * At the press, with nothing to add its par on to: the paper each maturing
     * piece it holds part of rolls into in the same structure (rollsInto(),
     * at home), its par in each, priced at the rollover's quote for that
     * paper at that size - struck for the par's worth of cash, then again for
     * what a unit of face brings, so the quote is for a face of about its
     * par. Books nothing; rollCentralBankAtIssue() books it in the window.
     */
    private void strikeParAlone() {
        double itsDue = 0;
        java.util.Map<RollsInto, Double> byPaper = new java.util.LinkedHashMap<>();
        for (Debt paper : debtManager.getDebt()) {
            if (!DebtManager.isTermPaper(paper)) continue;
            double c = centralBankShareOf(paper, paper.principalDueNextMonth());
            if (!(c > 0)) continue;
            RollsInto into = rollsInto(paper, Rollover.Mode.SAME_STRUCTURE, false);
            byPaper.merge(new RollsInto(into.type(), into.term(), false, false), c, Double::sum);
            itsDue += c;
        }
        if (!(itsDue > 0)) return;
        for (java.util.Map.Entry<RollsInto, Double> e : byPaper.entrySet()) {
            RollsInto into = e.getKey();
            double par = centralBankTender == itsDue ? e.getValue() : e.getValue() * centralBankTender / itsDue;
            DebtQuote first = rolloverQuote(into.type(), into.term(), false, par);
            if (first == null || first.isEmpty() || !(first.faceValue() > 0) || !(first.cashReceived() > 0)) continue;
            DebtQuote quote = rolloverQuote(into.type(), into.term(), false,
                    par * first.cashReceived() / first.faceValue());
            if (quote == null || quote.isEmpty() || !(quote.faceValue() > 0) || !(quote.cashReceived() > 0)) continue;
            centralBankAlone.add(new ParAlone(into, par, quote));
        }
    }

    /**
     * THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at
     * the press, added on to the city's term paper sold between the presses,
     * pro rata to its face, each at its issue's price on each unit of face;
     * or its par alone, as struck at the press; or, with neither, nothing -
     * its holding runs off. The treasury is paid in money made for it.
     */
    private void rollCentralBankAtIssue() {
        double par = centralBankTender;
        centralBankTender = 0;
        java.util.List<ParAlone> alone = new java.util.ArrayList<>(centralBankAlone);
        centralBankAlone.clear();
        if (!(par > 0)) return;
        double paid = 0, took = 0;
        if (!alone.isEmpty()) {
            for (ParAlone a : alone) {
                DebtQuote q = a.quote();
                double price = a.par() * q.cashReceived() / q.faceValue();
                Debt paper = "Serial".equals(a.into().type())
                        ? debtManager.addMediumTermBond(a.par(), a.into().term() * 12, month - 1, q.marketRate())
                        : debtManager.addLongTermBond(a.par(), q.duration() * 12, month - 1, q.couponRate());
                paper.markIssued(price, q.marketRate());
                paper.settled();
                paper.moveToCentralBank(a.par());
                paid += centralBank.buyAtIssue(price, a.par());
                took += a.par();
            }
        } else {
            java.util.List<Debt> sold = new java.util.ArrayList<>();
            double faces = 0;
            for (Debt d : debtManager.getDebt()) {
                if (!DebtManager.isTermPaper(d) || !(d.getSettleDue() > 0)) continue;
                double face = d.getOustandingPrincipal() - d.centralBankPrincipal();
                if (!(face > 0)) continue;
                sold.add(d);
                faces += face;
            }
            for (Debt d : sold) {
                double face = d.getOustandingPrincipal() - d.centralBankPrincipal();
                double addOn = sold.size() == 1 ? par : par * face / faces;
                double price = addOn * d.getSettleDue() / face;
                d.addOnForCentralBank(addOn);
                paid += centralBank.buyAtIssue(price, addOn);
                took += addOn;
            }
        }
        String here = getCurrency().qualifiedSymbol();
        if (took < par * (1 - 1e-12)) {
            GameLog.note(String.format("The central bank's %s%,.0fk falling due runs off: nothing was issued"
                    + " to add it on to.", here, par - took));
        }
        if (!(paid > 0)) return;
        cash += paid;
        treasuryRaisedSoFar += paid;
        debtManager.updateInterest();
        GameLog.note(String.format("The central bank rolled %s%,.0fk of its own falling due into new paper"
                + " at issue, paying the treasury %s%,.0fk.", here, took, here, paid));
    }

    /* ----------------------- a buyback's holders outside the pools ----------------------- */

    /**
     * What a buyback between two presses paid the households and the holders
     * of a dollar bond, carried in the treasury's pool until the next month
     * declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled
     * paper has on the other side. The central bank's share is carried as its
     * own redemption (CentralBank.getRedemptionDue()). Saved under their own
     * keys; an old save owes nothing.
     */
    private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled;
    /** ...and declared this month. */
    private double buybackToHouseholds, buybackAbroad;

    /** What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. */
    public double getBuybackUnsettled() {
        return buybackToHouseholdsUnsettled + buybackAbroadUnsettled + centralBank.getRedemptionDue();
    }

    public double getBuybackToHouseholds() { return buybackToHouseholds; }
    public double getBuybackAbroad()       { return buybackAbroad; }

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

       AND THE RESIDUAL IS NOT SWALLOWED. getTreasuryUnexplained() is whatever
       the three named flows do not account for, and the screen prints it as
       its own line. A breakdown that quietly absorbs its own gap is worse than no
       breakdown - the same rule the sector cash flow and the sick rate follow.

       AND SINCE 2026-09-18 THE RESIDUAL OPENS. Jerus: "it just says 'everything
       else' - that should be expandable, cause a lot of times that's where a
       bunch of important things happen." Every non-budget movement of the cash
       is written into a TreasuryJournal by name as it happens - capital into
       the bank, reserves, a buyback, the students' loans, and the two lines
       the budget balance omits - and the screen opens the row into those
       lines. What the lines do not explain is getTreasuryResidual(), printed
       under them as "Not accounted for". See TreasuryJournal for which sites
       are journalled and why the rest are not.
       ======================================================================= */

    private double treasuryOpening;
    private double treasuryClosing;
    private double treasuryRaised;
    private double treasuryRepaid;
    private double treasurySurplus;
    private boolean treasuryRecorded;

    /**
     * What the treasury has raised by issuing paper since the last strike, in
     * local money - the bridge's own counter, press to press.
     *
     * NOT cityDebtRaisedThisMonth + foreignDebtRaisedThisMonth, which is what
     * the strike read until 2026-09-18 and which is always zero there: every
     * issue - the player's, from the finance screen, and (until 0.7.0) the
     * emergency note at the top of nextMonth() - lands before the top-of-tick clear at
     * `cityDebtRaisedThisMonth = 0`, and nothing issues inside the tick. So the
     * "Raised by issuing paper" row read $0 on a month the city borrowed
     * $20M, and the $20M sat in "Everything else". Measured with a probe on
     * the day the journal was written. Those two counters keep their own job
     * (the audit's window, and the bank's settlement); this one is read and
     * cleared in takeTreasuryMonth(), and carried in the save because a city
     * saved between two presses has already raised what the next strike
     * counts.
     */
    private double treasuryRaisedSoFar;

    /** The named non-budget movements, this month and last. See TreasuryJournal. */
    private final TreasuryJournal treasuryJournal = new TreasuryJournal();

    private void takeTreasuryMonth() {
        treasuryClosing = cash;
        treasuryRaised  = treasuryRaisedSoFar;
        treasuryRaisedSoFar = 0;
        treasuryRepaid  = cityPrincipalRepaidThisMonth + foreignPrincipalRepaidThisMonth;
        treasurySurplus = economyManager.getNationalAccounts().getBalance();
        treasuryRecorded = true;
        // Struck with the rest: last month's lines become the ones the bridge
        // shows, and the month in progress opens empty - here, at the bottom
        // of the tick, so the player's decisions between two presses land in
        // the window that measures them.
        treasuryJournal.close();
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
     * The city's net position (0.7.32, the Finances tab's THE BALANCE): its
     * cash - below nothing when it is overdrawn - less the paper it owes and
     * what its central bank has advanced it. The tab worked it out itself
     * until 0.7.32, as the cash less the paper less the overdraft, which on
     * an overdrawn city took the overdraft off twice (the cash below
     * nothing was it already) and left the advances out.
     */
    public double getNetPosition() {
        return cash - debtManager.getAllPrincipal() - centralBank.getAdvancesToTreasury();
    }

    /**
     * Everything the three named flows do not explain - the whole of the
     * bridge's last row, "Everything else the treasury did".
     *
     * NOT A RESIDUAL TO BE HIDDEN, and not zero in this model. It is the rest
     * of what the treasury did: reserves bought or sold, capital put into the
     * bank, bonds bought back, the students' loans - none of which is a budget
     * line - PLUS the line the budget balance omits (the city's repair bill,
     * see TreasuryJournal; the transit fares were a second until 0.7.49,
     * when the budget took them), PLUS whatever the
     * government's books date to a different month from the money. Land and
     * buildings are NOT in it: land bought and sold and buildings paid for are
     * budget lines, struck over the same window, and the bridge's first row
     * already carries them. The screen prints this as its own row with its own
     * name, and since 2026-09-18 opens it into getTreasuryJournal()'s lines
     * with getTreasuryResidual() as the last of them.
     */
    public double getTreasuryUnexplained() {
        return getTreasuryChange()
                - (treasurySurplus + treasuryRaised - treasuryRepaid);
    }

    /**
     * Last month's journal: the non-budget movements by name, in the order
     * they happened, signed as the treasury sees them. Empty until a month has
     * closed, and empty on a month nothing was journalled - in which case the
     * screen keeps the row shut, because a caret that opens onto the figure
     * above it is worse than no caret.
     */
    public java.util.List<TreasuryJournal.Entry> getTreasuryJournal() {
        return treasuryJournal.lastMonth();
    }

    /** The journal itself, for the harnesses that read past the getter above. */
    public TreasuryJournal getTreasuryJournalBook() { return treasuryJournal; }

    /**
     * What the journal does not explain: the residual after the three named
     * rows AND the journal's lines. Timing between the books and the money,
     * and anything that moves the cash and is not yet journalled - printed as
     * "Not accounted for" rather than folded in, so that a gap stays a gap.
     */
    public double getTreasuryResidual() {
        return getTreasuryUnexplained() - treasuryJournal.lastMonthTotal();
    }

    /* -----------------------------------------------------------------------
       FROM EARNED TO THE BUDGET (0.7.31)

       Jerus, on the header: "the money one has is barely visible to see as
       well as ones income" - and in his city the line under the cash said
       "+$1.5B a month" while the cash grew $2.3B. Three figures, all right:
       EARNED (getIncome(), the header's), the budget's SURPLUS
       (NationalAccounts.getBalance()) and what the cash BANKED
       (getTreasuryChange()). The bridge above walks the budget to the cash;
       this walks EARNED to the budget, so the Government tab can draw the
       whole road EARNED -> SURPLUS -> BANKED with every step named and the
       screen adding nothing up itself (the project's spec-government-0731.md,
       D3).

       EARNED is the tax take (getTaxIncomeNow(): the fares in it) less the
       running programmes (getExpenses()) plus the utilities' net. The budget
       has the same lines - measured equal to the cent in two played cities -
       and eleven more: land sold and bought, buildings, the fund's transfer,
       the mortgage insurance's premiums and claims, subsidies, the food
       vouchers (0.7.45), the central bank's remittance and its interest, the
       students' loan interest. Those eleven are the steps, each an existing
       getter. The transit fares were a twelfth until 0.7.49: in EARNED and in
       the cash, and not in the budget. The budget carries them since (B9),
       with the bill EARNED carries too.

       What they leave is getEarnedResidual(), and it is not a gap to hide.
       getIncome() reads today's dials (getTaxIncomeNow()); the budget was
       struck at the month's. Move the wage tax between two presses and EARNED
       moves while the budget does not - the residual is that move, named
       "today's dials, not the month's" on the screen, and nothing in a month
       nobody touched a dial (TreasuryCheck holds both).
       ----------------------------------------------------------------------- */

    /** The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. */
    public java.util.List<TreasuryJournal.Entry> getEarnedToBudget() {
        NationalAccounts na = economyManager.getNationalAccounts();
        return java.util.List.of(
                new TreasuryJournal.Entry("Land sold", na.getLandSales()),
                new TreasuryJournal.Entry("Land bought", -na.getLandPurchases()),
                new TreasuryJournal.Entry("Buildings", -na.getCapitalSpending()),
                new TreasuryJournal.Entry("Transfer from the fund", na.getFundTransfer()),
                new TreasuryJournal.Entry("Mortgage insurance premiums", na.getMortgagePremiums()),
                new TreasuryJournal.Entry("Mortgage insurance claims", -na.getMortgageClaims()),
                new TreasuryJournal.Entry("Subsidies", -na.getSubsidies()),
                // ...and the food vouchers (0.7.45, the UI spec's B1): paid at the sale, as the
                // subsidies are, and outside EARNED, which left them in the residual until now.
                new TreasuryJournal.Entry("Food assistance", -na.getFoodAssistance()),
                new TreasuryJournal.Entry("Central bank remittance", na.getCentralBankRemittance()),
                new TreasuryJournal.Entry("Interest to the central bank", -na.getCentralBankInterest()),
                new TreasuryJournal.Entry("Student loan interest", na.getStudentLoanInterest()));
    }

    /** What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. */
    public double getEarnedResidual() {
        double steps = 0;
        for (TreasuryJournal.Entry e : getEarnedToBudget()) steps += e.amount();
        return economyManager.getNationalAccounts().getBalance() - (getIncome() + steps);
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

    /**
     * How much moved after the audit struck, which must be nothing.
     *
     * Never saved: it is a property of the month just run, meaningless in a
     * loaded city. See the note at the bottom of nextMonth().
     */
    private double postAuditDrift;
    private String postAuditDriftPool = "";
    private double postAuditDriftWorst;

    /** Money that moved after the audit struck. Zero, or something is in the gap. */
    public double getPostAuditDrift() { return postAuditDrift; }

    /** Which pool moved most after the strike, for naming the culprit. */
    public String getPostAuditDriftPool() { return postAuditDriftPool; }

    public double getPostAuditDriftWorst() { return postAuditDriftWorst; }
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
    
    
    /* =====================================================================
       THE CENTRAL BANK AND THE TREASURY (0.7.0)

       Jerus: "the feds sheet would show how much debt it holds, like debt to
       itself aka money printing."

       THE OVERDRAFT WAS AN EMERGENCY NOTE until 0.7.0. A treasury below zero
       at the top of a month issued a six-month note for the gap
       (issueEmergencyDebt, on EMERGENCY_NOTE_MONTHS - "Jerus's call ... long
       enough that a city has a real chance to fix the underlying problem or
       refinance into something longer, short enough that it still hurts"),
       priced like any other bill and sold to the bank. It had no limit. Until
       0.6.11 the bank never paid for the notes, and a broke city lived on free
       money; once it did, the notes priced at 27-36% and a city held at a 10%
       policy rate compounded to $193 quadrillion - the free number had been
       the floor (the-bank-that-never-paid.md section 3).

       NOW THE CENTRAL BANK ADVANCES IT. The shortfall at the top of a month is
       advanced at the policy rate (settleTreasury()), the interest is paid
       monthly and comes back as the remittance less what reserves cost, and
       cash above zero repays it first thing, before anything else. The
       advances stop at the ceiling - the player's dial since 0.7.2, in months
       of the treasury's trailing revenue (CentralBank.ceiling()) - and past
       that, Jerus's rule, chosen the night it was written: "pay promises
       first, cut the rest." Every payment the treasury makes goes through
       treasuryPays(), which reads TreasuryLine's two flags: a promise is paid
       whatever it takes, a discretionary line only from cash at or above
       zero once the ceiling has bound, and what is refused is owed -
       arrears, interest-free, paid down before anything discretionary once
       cash returns. A refused purchase is simply not made.
       A save still carrying an old emergency note reads it and runs it off
       like any other note.
       ===================================================================== */

    /**
     * What the treasury owes and has not paid, by line and by whom it is owed
     * - "LINE" or "LINE:sector" - in thousands. Saved; an old save has none.
     */
    private final java.util.Map<String, Double> arrears = new java.util.LinkedHashMap<>();

    /** This month's arrears: refused and booked, and paid down. For the screens and the playtest. */
    private double arrearsRefusedThisMonth, arrearsPaidThisMonth;

    /** Refused and paid down since founding, for the playtest's record. Not saved: a count for the run. */
    private double arrearsRefusedLifetime, arrearsPaidLifetime;

    /**
     * This month's arrears paid down, by the sector whose till they reached
     * (0.7.55): what its statement's arrears line reads (SectorBooks,
     * arrearsPaid), struck at the settle and read when the month is recorded,
     * both inside the month - so not saved, like the month's total beside it.
     */
    private final java.util.Map<String, Double> arrearsPaidTo = new java.util.LinkedHashMap<>();

    /**
     * The treasury's month with its central bank, first thing - inside the
     * audit's window, so every dollar made or destroyed here is one the month
     * declares.
     *
     * In this order: the interest on last month's advances, a promise; last
     * month's profit back as the remittance, which is mostly that interest
     * less what reserves cost; cash above zero repays the advances before
     * anything else; what is left pays down arrears; and a shortfall is
     * advanced whole - a promise already paid for it, past the ceiling if it
     * had to be, because treasuryPays() never let a discretionary line spend
     * past it. The two budget lines are set here so the government's books
     * carry them when they are struck at the bottom.
     */
    private void settleTreasury() {
        arrearsRefusedThisMonth = 0;
        arrearsPaidThisMonth = 0;
        arrearsPaidTo.clear();

        double interest = treasuryPays(TreasuryLine.CENTRAL_BANK_INTEREST,
                centralBank.advancesInterestDue(debtManager.getPolicyRate()));
        centralBank.chargeAdvances(interest);

        double remitted = centralBank.remit();
        cash += remitted;
        economyManager.setCentralBankLines(remitted, interest);

        /*
         * ...AND THE CITY'S FUND PAYS ITS TRANSFER (0.7.14): the withdrawal
         * dial's share of all it is worth (0.7.48; by default a twelfth of
         * TreasuryFund.TRANSFER_RATE, Norway's fiscal rule), from its cash -
         * and over the default, last month's sale first - a revenue line
         * beside the remittance. Before the advances are settled below, so a
         * treasury it lifts over nothing repays them with it. Nothing from an
         * empty fund.
         */
        double transfer = fund.payTransfer(fundValue(), CityCalendar.yearOf(month));
        cash += transfer;
        economyManager.setFundTransfer(transfer);
        fund.getLedger().flow(FundLedger.TRANSFER, transfer, false, month);

        if (cash > 0 && centralBank.getAdvancesToTreasury() > 0) {
            double repaid = centralBank.repayFromTreasury(cash);
            cash -= repaid;
            treasuryJournal.record("Repaid the central bank", -repaid);
        }

        if (cash > 0) payDownArrears();

        if (cash < 0) {
            double advanced = centralBank.advanceToTreasury(-cash);
            cash += advanced;
            treasuryJournal.record("Advanced by the central bank (printed)", advanced);
        }
    }

    /**
     * EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0).
     *
     * A promise is paid in full whatever the treasury holds. A discretionary
     * line is paid what discretionaryRoom() allows; the rest is refused, and
     * if the line owes its refusals (TreasuryLine.accruesArrears()) it is
     * booked as arrears. A purchase's caller checks it can pay before it
     * buys, so a purchase that reaches here is always paid whole.
     *
     * @return what was paid, which the caller hands to whoever was owed it
     */
    public double treasuryPays(TreasuryLine line, double amount) {
        return treasuryPays(line, amount, null);
    }

    /** ...and with whom a refusal is owed to - a sector's key, or null. */
    double treasuryPays(TreasuryLine line, double amount, String payee) {
        if (line == null || !(amount > 0)) return 0;
        double paid = line.promise ? amount : Math.min(amount, discretionaryRoom());
        if (!(paid > 0)) paid = 0;
        cash -= paid;
        double refused = amount - paid;
        if (refused > 0 && line.accruesArrears()) {
            arrears.merge(arrearsKey(line, payee), refused, Double::sum);
            arrearsRefusedThisMonth += refused;
            arrearsRefusedLifetime += refused;
        }
        return paid;
    }

    /**
     * What the treasury may spend on something that is not a promise, now.
     *
     * Until the ceiling binds, its cash and whatever the central bank will
     * still advance it. Once it has - the advances at the ceiling, or arrears
     * still owed, which is the ceiling having bound and not yet been paid
     * off - only cash at or above zero.
     */
    public double discretionaryRoom() {
        if (centralBank.ceilingBound() || hasArrears()) return Math.max(0, cash);
        return Math.max(0, cash + centralBank.headroom());
    }

    /**
     * Pays down what is owed, oldest first, out of cash above zero. The
     * sectors' arrears go to their tills - a transfer, like the subsidy or
     * the repair bill it was - and the students' are paid with the next grant
     * (payStudentGrants()), because the grant reaches the households through
     * their ledger and nowhere else.
     */
    private void payDownArrears() {
        java.util.Iterator<java.util.Map.Entry<String, Double>> it = arrears.entrySet().iterator();
        while (it.hasNext() && cash > 0) {
            java.util.Map.Entry<String, Double> owed = it.next();
            TreasuryLine line = arrearsLine(owed.getKey());
            if (line == null || line == TreasuryLine.STUDENT_GRANTS) continue;
            Sector payee = arrearsPayee(owed.getKey());
            if (payee == null) continue;
            double paid = treasuryPays(line, Math.min(cash, owed.getValue()));
            payee.addCash(paid);
            // ...and onto its statement (0.7.55), the figure that reached its till.
            arrearsPaidTo.merge(payee.key(), paid, Double::sum);
            arrearsPaidThisMonth += paid;
            arrearsPaidLifetime += paid;
            treasuryJournal.record("Paid down arrears", -paid);
            if (owed.getValue() - paid <= 1e-9) it.remove();
            else owed.setValue(owed.getValue() - paid);
        }
    }

    /**
     * The month's student grants, arrears first. The bill as the students'
     * ledger will be told it (EconomyManager.setOutsidePayments()) is what
     * was actually paid, so the households, the budget's grant line and the
     * audit's "- e StudentGrants" all read one figure - and since 0.7.1 in
     * the same month: paid at the top of it, a few lines before the ledger is
     * told.
     */
    private double payStudentGrants(double bill) {
        String key = arrearsKey(TreasuryLine.STUDENT_GRANTS, null);
        double owed = arrears.getOrDefault(key, 0.0);
        double backPay = 0;
        if (owed > 0) {
            backPay = treasuryPays(TreasuryLine.STUDENT_GRANTS, Math.min(owed, Math.max(0, cash)));
            if (owed - backPay <= 1e-9) arrears.remove(key);
            else arrears.put(key, owed - backPay);
            arrearsPaidThisMonth += backPay;
            arrearsPaidLifetime += backPay;
        }
        double paid = treasuryPays(TreasuryLine.STUDENT_GRANTS, bill);
        economyManager.setOutsidePayments(economyManager.getEiBenefits(), backPay + paid);
        return backPay + paid;
    }

    /**
     * The month's EI, struck again on the pool the month opens with at the
     * dial as the player left it (Unemployment.restrikeBenefits()) and paid
     * in full - a promise - at the top of the month, where the out of work
     * are credited it (0.7.3). The bill the ledger is told
     * (EconomyManager.setOutsidePayments()) is what was paid, so the
     * households, the budget's EI line and the audit's "- e EiBenefits" read
     * one figure in one month.
     */
    private double payEiBenefits() {
        double bill = unemployment.restrikeBenefits(economyManager.getTaxPolicy().getEiBenefitRate());
        double paid = treasuryPays(TreasuryLine.EI_BENEFITS, bill);
        economyManager.setOutsidePayments(paid, economyManager.getStudentGrants());
        return paid;
    }

    private static String arrearsKey(TreasuryLine line, String payee) {
        return payee == null ? line.name() : line.name() + ":" + payee;
    }

    private static TreasuryLine arrearsLine(String key) {
        int colon = key.indexOf(':');
        try {
            return TreasuryLine.valueOf(colon < 0 ? key : key.substring(0, colon));
        } catch (IllegalArgumentException unknown) {
            return null;
        }
    }

    /** Whose till an arrear is owed to: the named sector, or the builders for the construction lines. */
    private Sector arrearsPayee(String key) {
        int colon = key.indexOf(':');
        if (colon >= 0) return getSectors().byKey(key.substring(colon + 1));
        TreasuryLine line = arrearsLine(key);
        return line == TreasuryLine.CONSTRUCTION_SUBSIDY || line == TreasuryLine.CITY_REPAIRS
                || line == TreasuryLine.BUILDING_ESCALATION || line == TreasuryLine.BUILDING_OVERTIME
                ? getSectors().construction() : null;
    }

    /** True while anything is owed and unpaid. */
    public boolean hasArrears() { return !arrears.isEmpty(); }

    /** Everything owed and unpaid. */
    public double getArrearsTotal() {
        double total = 0;
        for (double v : arrears.values()) total += v;
        return total;
    }

    /** Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. */
    public java.util.Map<TreasuryLine, Double> getArrearsByLine() {
        java.util.Map<TreasuryLine, Double> out = new java.util.EnumMap<>(TreasuryLine.class);
        for (java.util.Map.Entry<String, Double> e : arrears.entrySet()) {
            TreasuryLine line = arrearsLine(e.getKey());
            if (line != null) out.merge(line, e.getValue(), Double::sum);
        }
        return out;
    }

    /** Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. */
    public double getArrearsOwedTo(String sectorKey) {
        double total = 0;
        for (java.util.Map.Entry<String, Double> e : arrears.entrySet()) {
            TreasuryLine line = arrearsLine(e.getKey());
            if (line == null || line == TreasuryLine.STUDENT_GRANTS) continue;
            Sector payee = arrearsPayee(e.getKey());
            if (payee != null && payee.key().equals(sectorKey)) total += e.getValue();
        }
        return total;
    }

    /** What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. */
    public double getArrearsPaidTo(String sectorKey) { return arrearsPaidTo.getOrDefault(sectorKey, 0.0); }

    public double getArrearsRefusedThisMonth() { return arrearsRefusedThisMonth; }
    public double getArrearsPaidThisMonth()    { return arrearsPaidThisMonth; }
    public double getArrearsRefusedLifetime()  { return arrearsRefusedLifetime; }
    public double getArrearsPaidLifetime()     { return arrearsPaidLifetime; }

    /* =====================================================================
       BUYING YOUR OWN DEBT BACK

       The other half of the finance menu. Issuing turns future payments into
       cash now; this turns cash now into no future payments, and the price is
       whatever the paper is actually worth rather than what it says on it.
       ===================================================================== */

    /**
     * What one bond would cost to clear right now: at the curve's rate for the
     * months it has left (0.7.1) - DebtManager.marketValue(), the same curve
     * it was issued on, which is what keeps a round trip neutral.
     */
    public double quoteRepurchase(Debt debt) {
        if (debt == null) return 0;
        priceTheDebtMarket();
        debtManager.updateInterest();
        return debtManager.marketValue(debt);
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
     * as principal by the same market (getPricedDebt) - and since 0.7.0 it is
     * the central bank's advance, which a buyback is a discretionary line
     * against (TreasuryLine.BUYBACKS) - so borrowing in order to retire paper
     * is doing the same thing twice with a cost in between. Letting the button
     * do it would be handing the player a trap with a green tick on it.
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

        double principal = debt.getOustandingPrincipal();
        // Who holds it, struck before it comes off the list (0.7.1).
        double hhFace = debt.householdPrincipal(), cbFace = debt.centralBankPrincipal();
        double bankFace = debt.bankPrincipal();
        double bankUnearned = debt.unaccretedOn(bankFace);
        if (!debtManager.retire(debt)) {
            return 0;   // not on the books; do not charge for it
        }

        treasuryPays(TreasuryLine.BUYBACKS, price);
        decisions.record(DecisionLog.BORROWING, "Bought back " + DecisionLog.money(principal) + " of "
                + debt.getType() + " paper for " + DecisionLog.money(price));
        // Not a repayment - retire() takes it off the books without touching
        // cityPrincipalRepaidThisMonth - so the bridge names it here.
        treasuryJournal.record("Bought back a bond", -price);
        /*
         * ...AND EVERY HOLDER IS PAID ITS SHARE (0.7.0, and by holder since
         * 0.7.1). A buyback used to pay nobody: the cash left the treasury,
         * the bond came off the list, and the bank's book fell by the
         * principal at the next refresh with nothing arriving. Now the price
         * is split by who held the paper, pro rata to principal:
         *
         *   - the BANK's share goes to its cash and its book drops by its face
         *     now, with the discount it had not yet earned on it; the
         *     difference is its gain or loss (Bank.sellPaperBack());
         *   - the HOUSEHOLDS' share goes to their savings with their paper
         *     down by their face;
         *   - the CENTRAL BANK's share comes off its book at face, and the
         *     price - money destroyed, the treasury having paid the boundary -
         *     settles at the top of next month with its gain against face;
         *   - and a DOLLAR bond's whole price leaves the country.
         *
         * The last three are paid out of the pools between two presses, so the
         * treasury's pool carries what it paid them (getBuybackUnsettled())
         * until the next month declares it leaving - households, abroad, the
         * central bank's money destroyed - which is where the audit sees it,
         * rather than cash leaving the treasury for nowhere (the dollar case
         * until 0.7.1: the-central-bank-opens.md section 4).
         */
        if (debt.isForeign()) {
            buybackAbroadUnsettled += price;
            // The stock the world is owed moves now, as it does when one is
            // issued (handleForeignLogic()); same rate, so no revaluation.
            foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());
        } else {
            double hhPrice = principal > 0 ? price * hhFace / principal : 0;
            double cbPrice = principal > 0 ? price * cbFace / principal : 0;
            bank.sellPaperBack(price - hhPrice - cbPrice, bankFace, bankUnearned);
            if (hhFace > 0) {
                householdBalance.creditPaperBuyback(hhFace, hhPrice);
                buybackToHouseholdsUnsettled += hhPrice;
            }
            if (cbFace > 0) centralBank.paperBoughtBack(cbPrice, cbFace);
        }

        // Both sides moved - one bond fewer, and less cash - so the market has
        // to be told before anything reads the rate again.
        priceTheDebtMarket();
        debtManager.updateInterest();

        return price;
    }

   /**
    * Set by loadGame() from the save, consumed by the next
    * rebuildSimulationState(). -1 means "recompute", which is what a save from
    * before the workforce was carried gets, and what newGame() and every other
    * caller of the rebuild gets too.
    */
   private long pendingWorkforce = -1;

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
   public double marginalHousingCost() {

       double materialPrice = buildingManager.getConstructionMaterialPrice();
       double cheapest = 0;

       for (BuildingsTemplate t : buildingManager.getTemplates()) {
           if (t == null || t.getCategory() != BuildingType.RESIDENTIAL) continue;
           if (t.getCapacity() <= 0) continue;

           // ...at what the landlord pays the builders for it (0.7.19): its
           // labour at today's wages and the sales tax it cannot claim back.
           double cost = economyManager.ownersBuildCost(t, t.getSector(),
                   buildingManager.nonMaterialCost(t)
                           + t.getConstructionMaterials() * Math.max(0, materialPrice));
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
    families.adoptCarriedDoubling();
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
    getSectors().realEstate().setRentWeight(
            families.studioRentWeight(), families.familyRentWeight());
    businessInvestment.setFamilies(families);
    // ...and the bank, as advanceDemographics() hands it over every month
    // (0.7.25). Without it the first month after every load planned the
    // branch with no bank - BusinessInvestment.planBank()'s "no bank" was
    // the bank's word on the Investors page and the build card - because
    // the month's investment runs before advanceDemographics() sets it.
    businessInvestment.setBank(bank);
    // BOTH retired bands, or the over-85s stop drawing a pension the day the
    // band lands and the city's pension bill silently falls.
    economyManager.setSeniors(cohorts.get(AgeBand.SENIOR) + cohorts.get(AgeBand.ELDER));
    /*
     * The month's EI and grant bills, as the save struck them. EI here is the
     * pool's own bill, which since 0.7.3 is NEXT month's - the month paid the
     * bill struck on the pool it opened with (payEiBenefits()) - so on a save
     * with the government's month the figure the month paid comes back over
     * this, below. The grant is re-struck from the same rule the
     * month used, which reproduces the bill exactly on the founding basis
     * and the fixed one; on a basis that reads last month's surplus or the
     * student body's tuition it cannot - the bottom of the tick overwrote
     * the one and the education step moved the other on - so the bill the
     * save actually struck comes back over this with the government's
     * month, below the rebuild (see loadedGovernmentMonth). A flow cannot be
     * reconstructed from the state a month ended in; this is the figure the
     * screens have until the carried one lands.
     */
    economyManager.setOutsidePayments(unemployment.getBenefitsPaid(), studentGrantBill());
    economyManager.setTotalJobs(populationManager.getTotalJobs());
    economyManager.setTotalWage(populationManager.getTotalWage());

    // The split behind that total, because the wage tax is banded now and a
    // single figure cannot be charged at four different rates.
    economyManager.setWageDetail(populationManager.getStaffedWagePerType());
    economyManager.setEnergyRatio(servicesManager.getEnergyRatio());
    economyManager.setWaterRatio(servicesManager.getWaterRatio());
    /*
     * The railway's share, before the road ratio reads it: a reloaded city has
     * the track it was saved with, so the relief has to be back on the network
     * before anything is measured against it. See chargeFreight().
     */
    getInfrastructureManager().setRailShare(getSectors().rail().getCarried());
    // ...and the band the saved month was quoting, which lives on the markets
    // and is not saved there. See sectors.Rail.reapplyBand().
    getSectors().rail().reapplyBand();
    /*
     * ...AND THE CARS, for the same reason and one line later: a reloaded city
     * has the fleet it was saved with, and a fleet is a load on the road. See
     * carriedCarOwnership for why this is a carried figure rather than a read
     * of the cells.
     */
    getInfrastructureManager().setCarOwnership(carriedCarOwnership >= 0
            ? carriedCarOwnership : householdBalance.carsPerHousehold());
    /*
     * ...AND THE FARE, which is the third thing the road ratio reads and is
     * not saved: ridershipAt() is struck from the dial every month, so a
     * reloaded city put every rider back on a free tram - more riders, less
     * road load, a different ratio - until the next tick corrected it. The
     * dial itself is saved; what is derived from it has to be re-derived
     * HERE, above the line that reads it. See setTransit() below for the
     * money half and for how long this whole family of gap has existed.
     *
     * ...AND THE COMMUTE BEFORE IT (0.7.49): who has a car of their own and
     * a journey's fuel, as the month struck them (carriedCaptiveShare,
     * carriedFuel; derived for a save from before them), then the drivers'
     * fuel, as advanceDemographics() 6d does.
     */
    getInfrastructureManager().setCommute(carriedCaptiveShare >= 0 ? carriedCaptiveShare : householdBalance.captiveShare(),
            carriedFuel >= 0 ? carriedFuel : Motoring.CAR_FUEL_PER_JOURNEY * foreign.getRate());
    servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());
    /*
     * ...and the drivers' fuel as 6d drew it (0.7.62): the bill, its imported
     * part and the litres, carried (carriedFuelMonth) - a draw is a month
     * passing and cannot be run again here. A save from before 0.7.62 struck
     * it as drivers x journeys x a journey's fuel, every litre imported, and
     * is given exactly that.
     */
    if (carriedFuelMonth != null && carriedFuelMonth.length >= 3) {
        motoring.restoreFuel(carriedFuelMonth[0], carriedFuelMonth[1], carriedFuelMonth[2]);
    } else {
        motoring.setFuelBill(getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH
                * getInfrastructureManager().getFuelPerJourney());
    }
    economyManager.setRoadRatio(getInfrastructureManager(), buildingManager);

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
    /*
     * ...AND THE SCHOOLS' (2026-09-21), the SIXTH sighting of the shape the
     * note above describes, and the one that had been standing next to the
     * fix the whole time: the health service's books were restored here on
     * the day the gap was found, the police's beside them, and the schools'
     * never were. A reloaded city with schools read an education bill of
     * zero and school fees of zero until its first tick - a month of free
     * schools on the treasury's books, and next-month income out by the
     * whole of it. Invisible for as long as the playtest built no school
     * (it never does on its own); the first ensemble run with schools in it
     * flagged "income across a save" nine times in 3,650 months. Education
     * carries the month's payroll, upkeep and fees in its own state, so
     * this is the same one-line restore the others are.
     */
    economyManager.setEducation(education.getGrossCost(), education.getFees());
    // ...and the police and the prisons', from the month Crime carried.
    economyManager.setSafety(crime.getGrossCost());

    /*
     * ...AND THE BUSES, ON THE LOAD PATH (2026-09-16), which is the FIFTH
     * sighting of the shape the two lines above already carry notes about:
     * set in advanceDemographics() on the monthly path and nowhere here.
     * Mining's wages went that way, then the property-tax charge, then the
     * health ratio, then the health service's books, and now this.
     *
     * IT ONLY BECAME VISIBLE THE DAY THE FARE BECAME REAL MONEY. The transit
     * bill is read by nothing but the national accounts, so a reloaded city
     * quietly reporting a transit bill of zero cost nothing anybody could
     * measure. The fare is a charge on the households now, so a reloaded city
     * charged them nothing for a month - and LongPlaytest caught it at
     * $12.85 on $10,388 of next-month income, eight months out of 3,650.
     *
     * The fare SHARE goes with it and had to go FURTHER UP, above the line
     * that strikes the road ratio - see the note there.
     *
     * THE BILL ITSELF IS CARRIED SINCE 0.7.49 (carriedTransitBill): struck
     * here, it read the fill the month ended on, not the one 6d paid at.
     * A save from before then derives it as before.
     */
    economyManager.setTransit(carriedTransitBill >= 0 ? carriedTransitBill :
            buildingManager.getCategoryPayroll(BuildingType.INFRASTRUCTURE,
                    populationManager.getWagesPerType(), populationManager.getJobFillRate())
                    + buildingManager.getUpkeepByCategory(BuildingType.INFRASTRUCTURE),
            getInfrastructureManager().getTransitRiders()
                    * economyManager.getTaxPolicy().monthlyFare());

    /*
     * EVERY SECTOR'S WAGES, in one call - the same call the monthly path
     * makes. Mining's were once forgotten here and the mine booked a month
     * of revenue against a payroll of zero; a registry loop cannot forget
     * one.
     */
    economyManager.updateJobFillRate(populationManager.getJobFillRate());
    economyManager.updateWages(populationManager.getWagesPerType(),
            buildingManager.getJobArrayByName("Commercial Bank"));

    economyManager.updateEcon();

    servicesManager.updateServices();

    economyManager.setPricePerWatt(servicesManager.getPricePerWatt());
    economyManager.setPricePerWaterUnit(servicesManager.getPricePerWaterUnit());

    // refreshEconPrices(), NOT finalEconUpdate() - the latter clears every
    // market and has every maker produce. Calling it here ran a month of
    // the economy with the calendar standing still. See its note in EconomyManager.
    economyManager.refreshEconPrices();

    // Price business credit off the restored balance sheets so a freshly loaded
    // save shows real rates and interest rather than zeroes. THE STATEMENTS
    // ARE NOT RECOMPUTED: every sector's struck month came out of the save
    // whole (see SectorState), and a statement is a fact about a month, not
    // a function of the state the month ended in.
    economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),
            bank.insuredMortgageRate(debtManager.getPolicyRate()));

    priceTheDebtMarket();
    debtManager.updateInterest();

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
        // Every dial the load puts back goes through its setter, and none of
        // it is a decision (0.7.23): the log is held for the whole load.
        decisions.hold();
        try {
            readTheSave(slot);
        } finally {
            decisions.release();
        }
    }

    /*
     * THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the
     * save's stamp is its stamp and it was drawn on this land in this month
     * (CityMap.readSidecar()); a save that had a map whose sidecar is missing
     * or stale has it drawn again canonically, once, and the picture shifts;
     * a save with no stamp had no map, and the city waits to be asked for one.
     */
    private void readTheMap(int slot, Long stamp) {
        cityMap = null;
        mapGeneration++;
        if (stamp == null) return;
        try {
            Path file = gameFiles.mapFile(slot);
            if (Files.isRegularFile(file)) {
                cityMap = CityMap.readSidecar(Files.readAllBytes(file), landManager.getCityLand(),
                        landManager::remainingByHolding, getMapTypes(), month, stamp);
            }
            if (cityMap == null) {
                long t0 = System.nanoTime();
                cityMap = drawMap();
                System.out.printf("The city map's sidecar was missing or not this save's: drawn again in %.0f ms"
                        + " (the picture shifts once).%n", (System.nanoTime() - t0) / 1e6);
            }
        } catch (IOException | RuntimeException e) {
            cityMap = null;
            System.out.println("The city map could not be read, and will be drawn when asked for: " + e);
        }
    }

    /*
     * THE LAND ON THE WORLD, PUT BACK OR CONVERTED (0.7.57, SAVE_FORMAT 31;
     * on the block grid since 0.7.67, SAVE_FORMAT 32).
     *
     * A format-32 save carries the city's land whole - its centre and its
     * blocks, every purchase, the fields it holds in part, the twenty-four
     * offers standing, what has been taken out of its ground and the world's
     * totals - and it comes back as it was, the grid replayed from the
     * rectangles. Its square feet (landOwned) are the land's dry ground, and
     * are checked against it: a save whose figure is more than a plot from it
     * has its land drawn again to hold the figure (LandConversion.restate()),
     * and says so.
     *
     * A save of format 31 carries a centre and forty lanes, and is snapped to
     * the grid, once (LandConversion.convertLanes(), spec-grid 2.6): one
     * converted holding, its books the plots drawn, no field changing hands,
     * no money moving. An older save carries one figure, a pool of iron and
     * nine parcels with no place, and is converted, once (LandConversion.
     * convert()): on its own world, a centre of blocks holding its ground to
     * within a plot, its iron as it had it. Either way its figure becomes the
     * converted ground's dry plots, and its old listing goes: twenty-four
     * offers are listed afresh once the buildings are back (landConverted,
     * readTheSave()), priced on the converted city's crowding.
     *
     * Returns whether the land was converted.
     */
    private boolean restoreLandOnTheWorld(DataSave loaded, double owned) {
        long seed = founding.getWorldSeed();
        double sea = loaded.getWorldSeaTheta() != null ? loaded.getWorldSeaTheta() : World.of(seed).seaTheta();
        int format = loaded.getSaveFormat();
        CityLand land = format > LandConversion.LAST_LANES_FORMAT
                ? CityLand.restore(seed, loaded.getLandCentre(), loaded.getLandCentreRects(), loaded.getLandHoldings(),
                        loaded.getLandPartFields(), loaded.getLandConverted())
                : null;
        if (land == null) {
            long t0 = System.nanoTime();
            boolean lanes = format == LandConversion.LAST_LANES_FORMAT && LandConversion.convertLanes(landManager, seed,
                    loaded.getLandCentre(), loaded.getLandLanes(), loaded.getLandPurchases(), loaded.getGameVersion(),
                    loaded.getDepletion(), loaded.getWorldTotals(), sea);
            if (!lanes) {
                LandConversion.convert(landManager, seed, owned, loaded.getIronDeposits(), loaded.getIronReserveTonnes(),
                        minesCommitted());
            }
            CityLand on = landManager.getCityLand();
            System.out.printf("Put the city's land on the block grid (save format %d, %s): %s of dry ground for the save's %s,"
                            + " %s in all, in blocks of %,.0f m at (%d, %d) on world %d, %d fields held in part; %d iron sites and"
                            + " %,.0f t; %.0f ms.%n",
                    format, lanes ? "its lanes snapped" : "a centre drawn to the plot",
                    LandManager.areaWords(landManager.getOwnedSqFt()), LandManager.areaWords(owned),
                    LandManager.areaWords(LandManager.sqFt(on.totalKm2(CityLand.TOTAL))),
                    (1L << on.level()) * World.PLOT_M, on.siteX(), on.siteY(), seed,
                    on.partFields().size(), landManager.getIronDeposits(), landManager.getIronReserveTonnes(),
                    (System.nanoTime() - t0) / 1e6);
            return true;
        }
        landManager.install(land, loaded.getDepletion(), loaded.getWorldTotals(), sea);
        landManager.getMarket().restoreOffers(loaded.getLandOffers(), loaded.getNextOfferId());
        landManager.restoreOwnedSqFt(owned);
        double ground = landManager.getLandDrySqFt();
        if (!LandConversion.sameGround(owned, ground)) {
            System.out.printf("The save's ground (%s) is not its land's (%s): the land is drawn again to hold it.%n",
                    LandManager.areaWords(owned), LandManager.areaWords(ground));
            LandConversion.restate(landManager, owned);
        } else {
            // A place the save listed nothing for gets its next, at the prices restored.
            landManager.getMarket().listMissing();
        }
        return false;
    }

    /** The load itself; see loadGame(). */
    private void readTheSave(int slot) {

        /*
         * Applied after rebuildSimulationState() rather than inside the try,
         * because that rebuild runs finalEconUpdate() and the month's charged
         * figures are exactly the kind of thing it resets. Restoring it here
         * and then rebuilding would put the bug straight back.
         */
        double restoredPropertyTax = 0;
        double restoredCityInterest = 0;
        DataSave restoredFlows = null;

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
            /*
             * ...AND FROM BEFORE THE SECTOR TEMPLATE. Jerus: "clean break".
             * A save older than format 21 carries five handlers' arrays and
             * nothing this build can read a sector out of, so it is refused
             * with a message that says why rather than loaded as a city with
             * seven empty businesses.
             */
            if (GameVersion.isFromBeforeSectors(loaded.getSaveFormat())) {
                System.out.println(GameFiles.slotLabel(slot)
                        + " was written before the sector template (save format "
                        + loaded.getSaveFormat() + "; this build reads from "
                        + GameVersion.FIRST_SECTOR_FORMAT + "). Not loaded.");
                loadFailure = "This save is from before the sector redesign and cannot be loaded.";
                return;
            }
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
            // The yard, in units. One unit has been $18,000 since format 20,
            // and every save this build reads is 21 or later.
            buildingManager.setConstructionMaterials(loaded.getConstructionMaterials());
            populationManager.setPopulation(loaded.getPopulation());
            this.population = loaded.getPopulation();

            /*
             * The land office's prices, before rebuildSimulationState() and
             * before anything re-prices the market. The land itself and its
             * offers come back with the land below, after the buildings
             * (restoreLandOnTheWorld(), 0.7.57).
             */
            landManager.getMarket().restorePriceState(loaded.getLandMarketPrices());

            /*
             * Policy. Each piece is restored only if the save carries it in this
             * build's shape - a save from before the Policy tab existed simply
             * keeps the defaults, which are the single city rate applied to
             * everything, which is exactly what that save meant.
             */
            // Whether the array was read decides whether the one income rate
            // further down is: it carries the three bases apart since 0.7.4,
            // and the old single key would put them back together.
            boolean taxArrayRead =
                    economyManager.getTaxPolicy().restorePolicyState(loaded.getTaxPolicyState());
            economyManager.getTaxPolicy().restoreSectorOffsets(loaded.getSectorOffsets());

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
                    loaded.getHouseholdCells(), loaded.getEquityKeys());
            bank.setCash(loaded.getBankCash());
            bank.setBranchesCapitalised(loaded.getBankBranchesCapitalised());
            bank.setProfitLastMonth(loaded.getBankProfitLastMonth());
            bank.setDepositRate(loaded.getBankDepositRate());
            bank.restoreSolvency(loaded.getBankSolvency());
            bank.restoreLastMonth(loaded.getBankLastMonth());
            bank.restorePricingHistory(loaded.getBankPricingHistory());
            bank.restoreLateProfit(loaded.getBankLateProfit());
            /*
             * ...AND 0.7.8'S: the record its capital target is struck from,
             * before anything prices off it (the rebuild below prices credit,
             * and prime's capital charge reads the target); the allowance,
             * book by book - an older save has none, and one is set up at the
             * end of the load, once the borrowers are back (openBankAllowance);
             * and the month's statement lines, after the allowance, whose
             * opening figure they carry exactly.
             */
            bank.restoreCapitalRecord(loaded.getBankCapitalRecord());
            bankAllowanceToOpen = !bank.restoreAllowance(loaded.getBankAllowance());
            bank.restoreMonthLines(loaded.getBankMonthLines());
            // ...and the months before it (0.7.9); an older save has none,
            // and the year starts with the month it was saved in.
            bank.restoreStatementYear(loaded.getBankStatementYear());
            // ...and its balance sheet a year back (0.7.13); an older save has
            // none, and the year-ago column reads "—" until it has lived one.
            bank.restoreSheetYear(loaded.getBankSheetYear());
            // ...and its equity in two parts (round 2), after the month's
            // lines, which carry the month's own causes. An older save kept
            // neither, and its bank shows its equity whole.
            bank.restoreEquitySplit(loaded.getBankPaidInOpening(), loaded.getBankRetainedOpening());
            // ...and the city's preferred (0.7.14), on the bank's clock. An
            // older save holds none, and its fund has not begun: empty, the
            // dial at 0, the rescue on the button - as it was played.
            bank.setMonth(month);
            bank.restorePreferred(loaded.getBankPreferred(), loaded.getBankPreferredRecord());
            fund.restore(loaded.getFund());
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
             * AN OLDER QUOTE WAS PRICED IN LOCAL MONEY (0.7.6), and it is
             * read as dollars at the rate of the day it is loaded - which is
             * only known now, with the foreign accounts back - so the local
             * cost the player saw is what it costs today. A dollar quote is
             * left alone. See LandMarket.settleLocalPrices(). (An older
             * listing was read the same way until 0.7.57; the conversion
             * lists forty offers in its place now.)
             */
            landManager.getMarket().settleLocalPrices(foreign.getRate());
            // The central bank's books, under their own key. A save from
            // before 0.7.0 has none, and restore() leaves the bank buildWorld()
            // founded - empty - which is that city's central bank: it had not
            // made a dollar yet.
            centralBank.restore(loaded.getCentralBank());
            // ...and what the treasury owed and had not paid - nothing, on a
            // save from before the arrears rule.
            arrears.clear();
            String[] owedKeys = loaded.getTreasuryArrearsKeys();
            double[] owedAmounts = loaded.getTreasuryArrearsAmounts();
            if (owedKeys != null && owedAmounts != null) {
                for (int i = 0; i < Math.min(owedKeys.length, owedAmounts.length); i++) {
                    if (owedKeys[i] != null && owedAmounts[i] > 0
                            && arrearsLine(owedKeys[i]) != null) {
                        arrears.put(owedKeys[i], owedAmounts[i]);
                    }
                }
            }
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
             * ...AND HOW IT WAS FOUNDED (0.7.10), after the world, whose mean
             * the record reads. A save from before 0.7.10 carries none of it
             * and reads as Founding.legacy(): Danzik, the Danzik dollar,
             * D$2.5B and US$1B - see DataSave.getFounding().
             */
            founding = loaded.getFounding(world.getMeanInflation());
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
            //
            // WHATEVER WAS SAVED, zero included (0.7.0). This restored only a
            // positive rate, which is how a save without the key was told
            // apart from one with it - and so a city whose player had set the
            // dial to 0% reloaded at 3%. The field is boxed now and null is
            // the save without the key. See DataSave.getPolicyRate().
            if (loaded.getPolicyRate() != null) debtManager.setPolicyRate(loaded.getPolicyRate());
            // ...and whose hand was on it: an older save reads the player's.
            debtManager.setAutopilot(loaded.getPolicyAutopilot());
            // ...and how the land office pays (0.7.6): an older save converts.
            landPaidFromVault = loaded.getLandPaidFromVault();
            // ...and the treasury's rollover (0.7.13). An older save rolls
            // nothing: it was played paying every maturity out of cash, and it
            // loads as it was played - the Finances tab turns it on.
            rollover.setMode(loaded.getRolloverMode());
            rollover.restoreLedger(loaded.getRolloverLedger());
            rollover.restoreRecord(loaded.getRolloverRecord());
            // ...and what the rule aims at (0.7.4): an older save has no key
            // and reads the default - 2%, the constant it was.
            debtManager.setInflationTarget(loaded.getInflationTarget() != null
                    ? loaded.getInflationTarget() : DebtManager.DEFAULT_INFLATION_TARGET);
            // ...and how strictly (0.7.52): an older save has no key and reads
            // Standard - the rule it was played under.
            debtManager.setStrictness(loaded.getPolicyStrictness());
            // ...and the anchor (0.7.42), after the index and the target it
            // reads. An older save has no key and is seeded: credibility at
            // Expectations.KSEED, the year's inflation smoothed, and the level
            // at 1.0 - its constants are still at founding money, so nothing
            // jumps on the load (spec-inflation.md, section 2.1).
            if (!expectations.restore(loaded.getExpectations())) {
                expectations.seed(priceIndex, debtManager.getInflationTarget());
            }
            // An older save's FIXED grant was nominal; read it as the real
            // amount that pays the same at this level (0.7.19, at the index
            // until 0.7.42) - and an older save's fare the same way (0.7.42).
            // See TaxPolicy.realiseFixedGrant() and realiseFare().
            economyManager.getTaxPolicy().realiseFixedGrant(expectations.getExpectedLevel());
            economyManager.getTaxPolicy().realiseFare(expectations.getStruckLevel());
            // ...and the holdings dial beside it (0.7.1), after the balance
            // sheet above, whose restore() founds an empty bank first and
            // carries the setting before the dial. An older save reads 0: a
            // central bank that holds nothing. Not setTargetShare(), which
            // would record the empty bank's 0 as the setting before.
            centralBank.restoreTargetShare(loaded.getQeTargetShare());
            // ...and the advances ceiling (0.7.2), after the same restore(),
            // which founds the default. An older save has no key and reads
            // the default - six months, the constant it was.
            centralBank.setAdvancesCeilingMonths(loaded.getAdvancesCeilingMonths() != null
                    ? loaded.getAdvancesCeilingMonths() : CentralBank.DEFAULT_ADVANCES_MONTHS);
            hotMoney.restore(loaded.getCapitalFlows());
            hotMoney.setMonth(month);
            bank.setForeignDeposits(hotMoney.getStock());
            // Absent from an older save: a city that never invested abroad.
            outward.restore(loaded.getOutwardInvestment());
            // ...or whose companies had no owners yet.
            equity.restore(loaded.getEquityKeys(), loaded.getEquity());
            // ...or no market. The desk's mark is put back from the quote and
            // the inventory, without calling the difference income.
            // ...on its books since 0.7.12 round 2; a save from before them
            // reads the dealer's array.
            if (!exchange.restore(loaded.getExchangeState())) {
                exchange.restore(loaded.getEquityKeys(), loaded.getExchange());
            }
            exchange.attach(equity, householdBalance, bank, exchangeCompanies, month);
            bank.restoreSecurities(exchange.markToMarket(equity));
            exchange.reopen(bank);
            labourMarket.setCostOfLiving(loaded.getCostOfLiving());
            carriedCarOwnership = loaded.getCarsPerHousehold();
            carriedTransitBill = loaded.getTransitBill();
            carriedCaptiveShare = loaded.getCaptiveShare();
            carriedFuel = loaded.getFuelPerJourney();
            carriedFuelMonth = loaded.getHouseholdFuel();
            getInfrastructureManager().setRememberedThroughput(loaded.getRememberedCommute());

            /*
             * EVERY SECTOR, WHOLE, and every market's price. Before the
             * rebuild, which prices credit off the restored balance sheets;
             * the struck statements come back with them and are NOT
             * recomputed. See SectorState.
             */
            economyManager.restoreSectorStates(loaded.getSectors());
            economyManager.restoreMarketStates(loaded.getMarkets());

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
                getSectors().retail().seedConstants(unit);
                labourMarket.seedConstants(unit);
                bank.seedConstants(unit);
                landManager.seedConstants(unit);
                healthcare.seedConstants(unit);
                foreign.seedConstants(unit);
                economyManager.getTaxPolicy().seedConstants(unit);
                education.seedConstants(unit);
                hotMoney.seedConstants(unit);
                equity.seedConstants(unit);
                exchange.seedConstants(unit);
                outward.seedConstants(unit);
                householdBalance.seedConstants(unit);
                bondMarket.seedConstants(unit);
            }
            // ...and the ones that price something, at the level the saved
            // month struck them at as well as the unit (0.7.42) - every month
            // does the same at its top - and what the anchor hands on.
            restrikeMoneyConstants();
            handOnExpectations();
            carriedRentWeight = loaded.getRentWeight();
            carriedStudioWeight = loaded.getRentWeightStudio();

            // The shops' budget constraint came back inside the Retail sector's
            // own state; it is re-applied after the rebuild below all the same,
            // because the rebuild re-strikes the households' plan.
            carriedRetailCapacity = getSectors().retail().getSpendingCapacity();
            carriedRetailWant = getSectors().retail().getWantedSpend();

            autoSubsidy.clear();
            if (loaded.getSubsidisedSectors() != null) {
                for (String key : loaded.getSubsidisedSectors()) setAutoSubsidised(key, true);
            }

            economyManager.restoreSalesTaxState(loaded.getSalesTax());

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
            households.setCumulativeSaving(loaded.getHouseholdSavings());
            this.reports = loaded.getReports();
            this.graphs = loaded.getGraphs();
            inbox.restoreFrom(loaded.getNotices());
            sectorBooks.restoreFrom(loaded.getSectorBooks(),
                    loaded.getSectorBooksBefore());
            restoreTreasuryMonth(loaded.getTreasuryMonth());
            // The journal and the raised counter go with it: nothing in the
            // rebuild re-strikes them, so they are put back here and stay.
            treasuryJournal.restore(loaded.getTreasuryJournalLabels(), loaded.getTreasuryJournalAmounts(),
                    loaded.getTreasuryJournalPendingLabels(), loaded.getTreasuryJournalPendingAmounts());
            treasuryRaisedSoFar = loaded.getTreasuryRaisedPending();
            // ...and the paper its bank has not yet paid for, which the next
            // settle pays (THE BANK PAYS FOR THE CITY'S PAPER). Zero from an
            // older save, which is what its bank would have paid.
            cityDebtRaisedThisMonth = loaded.getCityPaperUnsettled();
            cityDiscountThisMonth = loaded.getCityDiscountUnsettled();
            cityDebtRaisedForBank = cityDiscountForBank = 0;
            // What a buyback between the presses still has to declare, and the
            // households' paper ratio their plan is re-struck on (0.7.1).
            buybackToHouseholdsUnsettled = loaded.getBuybackToHouseholdsUnsettled();
            buybackAbroadUnsettled = loaded.getBuybackAbroadUnsettled();
            householdBalance.setPaperRatio(loaded.getHouseholdPaperRatio());
            // ...and their bonds' (0.7.12); 1 on an older save, which holds none.
            householdBalance.setBondRatio(loaded.getHouseholdBondRatio() > 0 ? loaded.getHouseholdBondRatio() : 1);
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
                            loaded.getConstructionProgressById(id),
                            loaded.getMaterialsOwedById(id),
                            loaded.getContractValueById(id));
                }
            }

            // ...stood in the order the city stood them in (0.7.43): the
            // restore above is by id. See DataSave.getStackOrder().
            buildingManager.orderStacks(loaded.getStackOrder());

            // A save that kept one order book for the whole city: spread it
            // over the sites by the work still owed, which is the rate the
            // old book earned it at. See BuildingsStacks.contractValue.
            if (!loaded.hasContractsById()) {
                buildingManager.spreadContracts(getSectors().construction().getUnearnedRevenue());
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
             * OUTPUT PARKED ON A STACK IS NOT KEPT (0.7.17). A save from before
             * every building got a crew can carry progress past what a stack
             * owes - on a stack with nothing on site, all of it: 7.82M points
             * in Jerus's year-149 city. Under the rule it could never have
             * built up, so it goes, and no money with it - see
             * BuildingManager.clearBankedProgress(). What went is kept for
             * the tools that report it; a save written since carries none.
             */
            bankedClearedAtLoad = buildingManager.clearBankedProgress();
            if (bankedClearedAtLoad > 0) {
                System.out.printf("Cleared %,.0f construction points parked on sites past what they owe.%n",
                        bankedClearedAtLoad);
            }

            /*
             * WHO ORDERED THE WORK ON SITE (0.7.19), for the escalation clause
             * and the tax credit (MATERIAL AT THE PRICE WHEN IT IS USED). A
             * save since carries every payer's contract on every stack.
             *
             * OLD CONTRACTS. An older save carries each stack's contract and
             * the material its sites still owe, but not who ordered them, nor
             * what the price allowed for the material. So each stack's whole
             * contract is read as its owner's - the template's sector, retail
             * for the bank's branch as it pays for them, the city for
             * everything else - and its allowance as the contract left less
             * the work still owed at the cash cost it was priced at, the rest
             * being what it priced the material at. From the load on, the
             * owner pays the material at the price when it is drawn, less
             * that allowance: the escalation starts where the save is.
             */
            if (loaded.getContractRecords() != null) {
                buildingManager.restoreContractRecords(loaded.getContractRecords());
            } else {
                // ...once the land price is back (below), which the share of
                // the tax a landlord's home gets back reads (revised 0.7.19).
                contractsToInfer = true;
            }

            /*
             * THE PLAYER'S HAND ON THE QUEUE (0.7.22): the order, the rushes,
             * the cancels, the shells, the demolitions, the buy-outs, and
             * each stack's run - before the land below, because a shell and
             * a demolition on site hold ground the allocation is struck from
             * (BuildingManager.getTotalLandFootprint()). A format-27 save has
             * none of it, and loads with none.
             */
            buildingManager.getControl().restore(loaded.getConstructionControl());
            // The player's decisions (0.7.23): a format-28 save has none, and
            // loads with an empty log.
            decisions.restore(loaded.getDecisionLog());

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

            boolean landConverted = restoreLandOnTheWorld(loaded, owned);
            landManager.setAllocatedSqFt(built);
            // A converted city's offers (0.7.67, spec-grid 2.6): twenty-four
            // places listed afresh, once, with the buildings back, at the
            // prices the save last struck (settled above for an older save's
            // local quote) - a load strikes no prices; the first month strikes
            // them on the converted ground.
            if (landConverted) landManager.getMarket().listMissing();
            landManager.clearMonth();

            // The water rights (0.7.59), after the buildings and the land they
            // are reckoned from: a save from before them carries none, and is
            // given what its fresh plants already pump past its lakes and river.
            if (loaded.getFreshRights() != null) {
                freshRights = Math.max(0, loaded.getFreshRights());
            } else {
                freshRights = derivedFreshRights();
                if (freshRights > 0) {
                    System.out.printf("Water rights for what the city already pumps: %,.0f units a month past the"
                            + " %s of lake and river it owns.%n", freshRights, LandManager.areaWords(
                            LandManager.sqFt(landManager.getFreshKm2())));
                }
            }

            // The city map (0.7.60), after the buildings and the land it is
            // drawn from: THE CITY MAP.
            readTheMap(slot, loaded.getMapStamp());

            TaxPolicy policy = economyManager.getTaxPolicy();
            // The one income rate, for a save whose policy array was not read
            // - only there (0.7.4): the array sets the three bases one by one,
            // and this key, which is all three at the profit rate, would undo
            // a split. Where the array was read the two agreed until 0.7.4.
            if (!taxArrayRead && loaded.getIncomeTaxRate() > 0) {
                policy.setIncomeTaxRate(loaded.getIncomeTaxRate());
            }
            if (loaded.getPropertyTaxRate() > 0) {
                policy.setPropertyTaxRate(loaded.getPropertyTaxRate());
            }

            restoredPropertyTax = loaded.getPropertyTaxCharged();
            restoredCityInterest = loaded.getCityInterestAccrued();
            restoredFlows = loaded;
            

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

            /*
             * ...AND WHETHER THE PAPER STILL OWED FOR CARRIES ITS OWN DISCOUNT
             * (0.7.1). Paper issued by this build knows what it is owed for
             * (Debt.getSettleDue()) and accretes its own discount; a 0.7.0
             * save taken between an issue and its settle carries a discount
             * (cityDiscountThisMonth, read above) and paper that knows
             * neither, and its bank books that discount whole at the settle,
             * as it would have.
             */
            legacyDiscountDue = 0;
            if (cityDiscountThisMonth > 0) {
                boolean carriesItsOwn = false;
                for (Debt d : loadedDebts) if (d.getSettleDue() > 0) carriesItsOwn = true;
                if (!carriesItsOwn) legacyDiscountDue = cityDiscountThisMonth;
            }

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

                        // Interim financing after a default (0.7.12, round
                        // 5): a business loan that ranks ahead of the
                        // sector's other debt, which its type carries.
                        case InterimLoan.TYPE:
                            loadedLoans.add(gson.fromJson(obj, InterimLoan.class));
                            break;

                        // A landlord's insured mortgage (0.7.11): its balance,
                        // rate, term and amortization, as it stood.
                        case "MORTGAGE":
                            loadedLoans.add(gson.fromJson(obj, Mortgage.class));
                            break;
                    }

                    // future business instrument types go here
                }
            }

            economyManager.getBusinessDebtManager().setLoans(loadedLoans);
            // ...and the quarter the bank reads each sector on (0.7.8, round 3),
            // before the rebuild prices anything off it. None in an older
            // save: each sector reads its month until it has its own.
            economyManager.getBusinessDebtManager().restoreStatements(loaded.getCreditStatements());
            // ...and the businesses' bonds (0.7.12), before the rebuild prices
            // any sector's interest bill off them. An older save has none: a
            // city whose businesses owe only the bank, which is what it was.
            bondMarket.restore(loaded.getBondMarket());
            // ...and each cell's own face of them (round 2) - put back again
            // below, after the rebuild's census, with the cells' other stocks.
            restoreCellBonds(loaded);
            economyManager.getBusinessDebtManager().restoreBondWrittenOff(loaded.getBondWrittenOff());

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

        // An older save's contracts, read as their owners' (OLD CONTRACTS),
        // now the land price the rebate on a new rental home reads is back.
        if (contractsToInfer) {
            contractsToInfer = false;
            buildingManager.inferContracts(this::ownerOfOrder, this::recoveredOnOldContract);
        }

        /*
         * The property tax each sector was charged came back inside its own
         * state (SectorState carries the three bills of the month); the
         * city's total is carried on its own line, because the two are the
         * same fact struck in two places and the treasury reads this one.
         */
        economyManager.setTotalPropertyTax(restoredPropertyTax);

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
            String[] savedBands = restoredFlows.getBandNames();
            cohorts.restore(savedBands, restoredFlows.getCohorts());
            families.restore(savedBands, restoredFlows.getShapeNames(), restoredFlows.getFamilies());
            migration.restore(restoredFlows.getMigration());
            unemployment.restore(restoredFlows.getUnemployment());
            sickness.restore(savedBands, restoredFlows.getSickness());
            // The prisoners are out of the supply on the load path as on the
            // monthly one, like the students below - or a reloaded city has
            // more workers than the live one until its first month.
            if (!crime.restore(restoredFlows.getCrime())) crime.reset();
            populationManager.setImprisoned(crime.prisoners());
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
            subsidyPaid.clear();
            if (restoredFlows.getSubsidyPaid() != null) subsidyPaid.putAll(restoredFlows.getSubsidyPaid());
            monthlyMaterialImports = restoredFlows.getMonthlyMaterialImports();
            monthlyMaterialImportBill = restoredFlows.getMonthlyMaterialImportBill();
            materialsConsumed = restoredFlows.getMaterialsConsumed();
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
            getSectors().retail().setSpendingCapacity(carriedRetailCapacity);
            getSectors().retail().setWantedSpend(carriedRetailWant);
        }
        if (carriedRentWeight > 0) {
            // Both halves, and the family one by subtraction so the two always
            // sum to the figure the landlords were paid. See the note at the
            // other restore site.
            double studio = Math.max(0, Math.min(carriedStudioWeight, carriedRentWeight));
            families.setRentWeight(studio, carriedRentWeight - studio);
            getSectors().realEstate().setRentWeight(studio, carriedRentWeight - studio);
        }

        /*
         * THE PRICE OF MONEY, BEFORE ANYTHING IS PRICED OFF IT.
         *
         * BusinessDebtManager's base rate is set once a month, at the top of
         * nextMonth() - the bank's prime since 0.7.7, the city's own curve
         * before it - and nothing on the load path set it at all, so a reloaded city sat on the 1% default until
         * its next tick. Everything a household or a business borrowed in the
         * month after a load was priced off that: measured, two cities that
         * were otherwise identical came out 0.13% apart on the household book
         * and $88.90 apart on the bank's interest, and the bank's profit and
         * tax followed.
         *
         * NOT CARRIED, because it does not need to be: it is a pure function
         * of state the save already restores - the dial, and the parts of
         * the bank's price closeMonth() struck and the save carried (Bank,
         * WHAT A LOAN COSTS). Re-derived here, from the same calls nextMonth()
         * makes and in the same order, and AFTER the debt is back, which is
         * why it is here rather than inside the rebuild. (The city's debt,
         * its rating and the bank's strain premium, until 0.7.7.)
         */
        // The floor is restored the same way - a reload that put back one
        // price without the other would quote a different rate than the city
        // was looking at when it saved.
        pushCostOfFundsToTheDebtMarket();
        economyManager.getBusinessDebtManager().setPrimeRate(bank.prime(debtManager.getPolicyRate()));
        economyManager.getBusinessDebtManager().setInsuredMortgageRate(
                bank.insuredMortgageRate(debtManager.getPolicyRate()));

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
            /*
             * ...AND THE GRANT BILL WITH IT (2026-09-21). The rebuild
             * re-struck it from the rule, which is exact on the founding
             * basis and not on one that reads last month's surplus or the
             * student body's tuition (see the re-strike's note). The block
             * that just came back carries the bill the month actually
             * struck, on the slot EI and the grants have had since 2026-09-11,
             * so that is what the screens read until the next press - the
             * same double, on the founding basis, and the right one on the
             * others. A block from before that slot keeps the re-strike.
             * (Until 0.7.1 it was also what the students were handed at the
             * next strike; they are paid at the top of the month now, struck
             * afresh on the students it opens with - see payStudentGrants().)
             * ...AND THE EI THE MONTH PAID (0.7.3), from the same slots: it
             * is paid at the top of the month on the pool the month opened
             * with (payEiBenefits()), so the pool the save carries has
             * already struck next month's, and the block is the only place
             * the month's own figure survives.
             */
            if (loadedGovernmentMonth.length >= NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS) {
                economyManager.setOutsidePayments(economyManager.getNationalAccounts().getEiBenefits(),
                        economyManager.getNationalAccounts().getStudentGrants());
            }
            // ...and the interest the graduates paid, which only the block
            // carries: the Schools page reads it as "received last month".
            economyManager.setStudentLoanInterest(
                    economyManager.getNationalAccounts().getStudentLoanInterest());
            // ...and the food vouchers the month paid (0.7.43), which only the
            // block carries: paid after the sale, struck into it at the bottom.
            economyManager.setFoodAssistance(economyManager.getNationalAccounts().getFoodAssistance());
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

        if (restoredFlows != null) {
            economyManager.restoreNationalAccounts(restoredFlows.getNationalAccounts());
            // ...and its rolling year from the graph history, read above
            // (0.7.31; NationalAccounts.seedHistory()): every "of GDP" on the
            // Government tab and the header's GDP tile read one month after a
            // load, scaled up, until a year had been played again.
            economyManager.getNationalAccounts().seedHistory(historySave.getGdp());

            demolitionLog.restore(restoredFlows.getDemolitions());
            buildLog.restore(restoredFlows.getBuilds());
            // cohorts, families and migration are restored ABOVE, before
            // rebuildSimulationState() re-runs the economy off them.
            economyManager.getBusinessDebtManager()
                    .restoreWriteOffs(restoredFlows.getWriteOffTotals());
            economyManager.getBusinessDebtManager().restoreCreditRecord(
                    restoredFlows.getRestructureCounts(),
                    restoredFlows.getBlockedMonths());
            // ...the mortgages' month and the insurance book (0.7.11); an
            // older save insured nothing.
            economyManager.getBusinessDebtManager().restoreMortgageRepaid(
                    restoredFlows.getMortgageRepaid());
            economyManager.getBusinessDebtManager().restoreInsuranceRecord(
                    restoredFlows.getInsurancePremiums(), restoredFlows.getInsuranceClaims());

            /*
             * THE BILLS OF THE MONTH, BACK OVER THE REBUILD. Every sector's
             * interest, property tax and maintenance came out of the save
             * inside its own state, and rebuildSimulationState() has just
             * re-priced credit off the restored sheets, which is a different
             * answer from the one the month was struck at - $7.30 against
             * $4.87 on a city that had just ordered two depots. Re-derivation
             * is not restoration when the thing being derived is a flow.
             */
            economyManager.restoreSectorBills(restoredFlows.getSectors());

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
                getSectors().retail().setSpendingCapacity(carriedRetailCapacity);
                getSectors().retail().setWantedSpend(carriedRetailWant);
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
                    restoredFlows.getHouseholdCells(), restoredFlows.getEquityKeys());
            // ...and each cell's own bonds with them, for the same reason: the
            // rebuild's census folds a cell it reads under half a household
            // into the rest, bonds and all, and the cell arrays above put
            // every other stock back but not these (0.7.12 round 2 -
            // SaveFileCheck found three cells of forty-five moved).
            restoreCellBonds(restoredFlows);
            // ...and the students waiting to carry their loans at the next
            // census, which only the month sets (0.7.63; an older save: none).
            householdBalance.restoreGraduates(restoredFlows.getHouseholdGraduates());
            householdBalance.setExchangeRate(foreign.getRate());

            /*
             * ...and the households' placement, for the third time and the same
             * reason. FamilyModel.restore() runs with the rest of the save, and
             * then rebuildSimulationState() re-houses everybody against a stock
             * it is still in the middle of putting back - so doubledUp came out
             * different from the figure the save was taken with, and the two
             * counters that explain WHY people are doubled up were still at
             * zero. Put back last, like the statement above it.
             *
             * WITH THE SAVE'S OWN BANDS AND SHAPES (0.7.8). The one-argument
             * restore() reads the array as the legacy layout, and a save from
             * after the shapes split is longer than any legacy length it
             * accepts - so this call was refused whole on every modern save,
             * and the counters kept the load path's one matching pass instead
             * of the two the saved month ran. It passed while the two passes
             * happened to agree; SaveFileCheck's fixture city under 0.7.8's
             * pricing saved a month where they did not ("...and the ones a
             * studio turned away", 129.85 against 129.12).
             */
            families.restore(restoredFlows.getBandNames(), restoredFlows.getShapeNames(),
                    restoredFlows.getFamilies());
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
         * had quoted a moment before it was saved. Deriving it needs these
         * three lines - and since 0.7.14 the market's last strike, carried in
         * the save, is put back over them at the end (see below).
         *
         * The live path does all of this at the end of nextMonth(); this is
         * that same sequence, in that same order, at the end of the load.
         */
        /*
         * THE FLEET, READ OFF THE CELLS THAT ARE FINALLY BACK. The rebuild
         * used the carried figure because the cells were not; this is the same
         * number from its own source, and the two agreeing is the point rather
         * than a coincidence - the save wrote the carried figure out of these
         * same cells. Cheap, and it stops a carried scalar and the cells that
         * own it from ever drifting apart unnoticed.
         */
        getInfrastructureManager().setCarOwnership(householdBalance.carsPerHousehold());
        carriedCarOwnership = -1;
        carriedTransitBill = -1;
        carriedCaptiveShare = -1;
        carriedFuel = -1;
        carriedFuelMonth = null;

        priceTheDebtMarket();
        refreshBank();
        /*
         * A BANK FROM BEFORE 0.7.8 HAS SET NOTHING ASIDE, and is given the
         * allowance its borrowers call for now that they are all back - with
         * no provision: its equity falls by it between two presses, and no
         * month's income statement carries it (Bank.openAllowance()). A save
         * that carried one restored it, struck when the month was, and is
         * not re-struck: a reload re-reads the borrowers as they stand after
         * the month, not as the month that struck it read them.
         */
        if (bankAllowanceToOpen) {
            // Every sector's balance sheet as it stands, and the credit
            // desk's reading of it - the pair the insolvency check strikes
            // (EconomyManager.settleInsolvency()); the rebuild above read the
            // assets before the sheets had their buildings back, and a sector
            // read against a sheet with nothing on it is in stage 2 by the
            // whole of what it owes. The next month re-strikes both at its top.
            economyManager.pushBalanceSheetInputs();
            economyManager.refreshCreditAssets();
            bank.openAllowance(bankReadings(), householdBalance.lossAllowance(),
                    householdBalance.debtInTrouble());
            bankAllowanceToOpen = false;
        }
        pushCostOfFundsToTheDebtMarket();
        debtManager.updateInterest();
        /*
         * ...AND EVERY SECTOR'S RATE WITH ITS RECORD AND THE BOOK'S
         * CONCENTRATION IN IT (0.7.33; the project's spec-bank-0733.md, B5).
         * The rebuild priced business credit before restoreCreditRecord() put
         * the write-downs back, and with no concentration charge pushed - the
         * month pushes them at its top (startOfMonthUpdate()) - so a city just
         * loaded quoted every sector at prime and its own risk alone: Mining
         * read 3.07% after Continue and 6.38% a month on with nothing changed.
         * The month re-prices before it writes a loan, so it was the screens'
         * error, not the simulation's. Re-priced here from the same two calls
         * the month's top makes, after the bank's book and its concentration
         * are back (refreshBank() above); that top pushes its own over them.
         */
        economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());
        economyManager.getBusinessDebtManager().updateRates();
        /*
         * ...AND THE CITY'S OWN RATE AS THE MONTH LAST STRUCK IT (0.7.14). The
         * lines above re-strike it from the cash the save holds, and the
         * treasury's cash moves after the month's last strike: a reloaded
         * city in deficit priced its own paper a basis point off the live
         * city's (11.4945% against 11.4846%, a Lean city's month 1475), and
         * every curve rate, quote and bond mark read off it with it. The
         * market's inputs and its rate are carried whole
         * (DebtManager.marketToSave()) and put back last, over the re-strike:
         * the live city's, exactly. An older save keeps the re-strike.
         */
        if (restoredFlows != null) debtManager.restoreMarket(restoredFlows.getDebtMarket());
        // ...the month's trade in units for a save from before them (0.7.63): every price is back now.
        // See Sector.deriveCarriedTrade().
        for (Sector s : getSectors().all()) s.deriveCarriedTrade();
        // ...and, last, the fund's cost basis for a save from before it (0.7.39): every price is back now.
        if (fund.needsLedgerSeed()) seedFundLedger();
    }

    /**
     * THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's
     * spec-fund-0739.md, 3.5): each market lot and bond at its market value
     * this month, flagged as such, the rescue lot exact from the counters,
     * one TRACKING row. Reads prices and writes the ledger only.
     */
    void seedFundLedger() {
        fund.getLedger().seed(this, month);
        fund.ledgerSeeded();
    }

    /** True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. */
    private boolean bankAllowanceToOpen;


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
        constructionShedPoints *= scale;
        lastWriteOff *= scale;
        // ...and the month's sales of scrapped plant's material (0.7.8).
        salvageBySector.replaceAll((k, v) -> v * scale);
        salvageThisMonth.replaceAll(s -> new Salvage(s.seller(), s.building(), s.buildings(), s.units(),
                s.price() * scale, s.paid() * scale, s.unitsBought(), s.distress()));
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
        cityPaperSettled *= scale;
        // The holders' figures (0.7.1), money like the rest of this block.
        legacyDiscountDue *= scale;
        buybackToHouseholdsUnsettled *= scale;
        buybackAbroadUnsettled *= scale;
        buybackToHouseholds *= scale;
        buybackAbroad *= scale;
        couponsToHouseholds *= scale;
        principalToHouseholds *= scale;
        householdsBoughtPaper *= scale;
        bankPrincipalRepaidThisMonth *= scale;
        treasuryRaisedSoFar *= scale;
        treasuryJournal.redenominate(scale);

        economyManager.redenominate(scale);
        bank.redenominate(scale);
        // The central bank's books and the treasury's arrears (0.7.0), which
        // are money like everything else here; the ceiling's months and the
        // window's penalty are not.
        centralBank.redenominate(scale);
        arrears.replaceAll((key, owed) -> owed * scale);
        arrearsRefusedThisMonth *= scale;  arrearsPaidThisMonth *= scale;
        arrearsRefusedLifetime *= scale;   arrearsPaidLifetime *= scale;
        arrearsPaidTo.replaceAll((key, paid) -> paid * scale);
        foreign.redenominate(scale);
        hotMoney.redenominate(scale);
        outward.redenominate(scale);
        equity.redenominate(scale);
        exchange.redenominate(scale);
        bondMarket.redenominate(scale);
        // ...and the city's fund (0.7.14): its cash, its record and its orders.
        fund.redenominate(scale);
        ownersWipedAbroadThisMonth *= scale;
        // The last closed month is what the next dividend is paid on.
        sectorBooks.redenominate(scale);
        priceIndex.redenominate(scale);
        householdBalance.redenominate(scale);
        households.redenominate(scale);
        unemployment.redenominate(scale);
        studentLoansLent *= scale;  studentLoansRepaid *= scale;  studentLoansWrittenOff *= scale;
        studentLoanInterest *= scale;
        labourMarket.redenominate(scale);
        populationManager.redenominate(scale);
        migration.redenominate(scale);
        landManager.redenominate(scale);
        debtManager.redenominate(scale);
        // ...and what the treasury's rollover netted and raised (0.7.13).
        rollover.redenominate(scale);
        buildingManager.redenominate(scale);
        healthcare.redenominate(scale);
        education.redenominate(scale);
        crime.redenominate(scale);
        if (servicesManager != null && servicesManager.getUtilitiesHandler() != null) {
            servicesManager.getUtilitiesHandler().redenominate(scale);
        }
        /*
         * ...AND THE YEAR OF THE RATE BEHIND US.
         *
         * rateHistory holds twelve past exchange rates and yearlyDepreciation()
         * compares today's against the one a year old. Unscaled, a reform makes
         * the city read a ninety-nine percent currency move that never happened,
         * for the next twelve months - and that number is what tells hot money
         * whether to panic and the foreign desk whether the currency is running.
         * Found 2026-09-12 while chasing a scale divergence in the carry trade.
         */
        for (int i = 0; i < rateHistory.length; i++) rateHistory[i] *= scale;

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

        /*
         * ...EXCEPT THE UNIT THE FARE DIAL IS READ AT (B6, 0.7.47), which is a
         * unit and not a figure: the dial was divided with the money above
         * (TaxPolicy.redenominate()), and the ridership curve and the dial's
         * cap read it in founding money at the new unit, so the same real
         * fare draws the same riders. Until then the next month read a
         * thousandth of the fare against a cap in founding money.
         */
        economyManager.getTaxPolicy().setMoneyUnit(denomination.getUnit());
        getInfrastructureManager().setFareUnit(denomination.getUnit());
        /*
         * ...AND THE FUEL (0.7.49), which IS a figure: the journey's price the
         * month struck and the month's bill are money, divided where they sit,
         * as the transit bill and the fares are (EconomyManager.redenominate()).
         */
        getInfrastructureManager().redenominateFuel(scale);
        motoring.restoreFuel(motoring.getFuelBill() * scale, motoring.getFuelImports() * scale, motoring.getFuelLitres());

        decisions.record(DecisionLog.CURRENCY, String.format(java.util.Locale.ROOT,
                "Currency reformed: one new %s for %s old", getCurrency().name(), Formats.INSTANCE.count(factor)));
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
 


 
   