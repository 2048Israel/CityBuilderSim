package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class EconomyManager {

    private double cash;
    private double upkeep;
    private int totalJobs;
    private int population;
    /**
     * Both tax rates, and the annual-to-monthly conversion for the property
     * one. Was a bare `taxRate = .15` field; see TaxPolicy for why they moved.
     */
    private final TaxPolicy taxPolicy = new TaxPolicy();

    /**
     * What the city currently charges for a square foot, so assessed values
     * track the price the player sets rather than what anyone historically
     * paid. Pushed in from Game each month, because LandManager lives there.
     *
     * That is a real reassessment, and it has teeth in both directions: raising
     * the land price raises what every landowner in the city owes, not just
     * what the next buyer pays.
     */
    private double landPricePerSqFt;

    /** This month's property tax, by sector. Charged to them, banked by the city. */
    private double totalPropertyTax;

    /** This month's property tax by category ordinal. See getPropertyTaxCharges(). */
    private double[] propertyTaxCharges;

    /** This month's business-debt interest by category ordinal. See getInterestCharges(). */
    private double[] interestCharges;

    /** Income tax on the mills. Folded into the business-tax line on the reports. */
    private double totalHeavyIndustryTax;
    /** Construction's profit tax, collected since 2026-09-10 - see ConstructionHandler.getTaxIncome(). */
    private double totalConstructionTax;
    private double totalBusinessTax;

    /**
     * Last month's profit tax from the bank, already collected.
     *
     * Assigned by Game when the bank is charged, and READ BACK here rather than
     * recomputed - the same rule property tax and sales tax follow, and for the
     * same reason: the city must collect exactly the figure the business paid.
     * Recomputing it from a rate and a profit that have both moved since is how
     * the two ends stop agreeing.
     */
    private double totalBankTax;

    public void setBankTax(double amount) { this.totalBankTax = Math.max(0, amount); }
    public double getBankTax()            { return totalBankTax; }
    private double totalWageTax;
    private int households;
    private double totalIncome = 0;
    private double totalTaxIncome = 0;
    private double interest;
    private double[] fillRate = new double[11];
    private double averageStoreFill;
    private double totalWage;

    //tax stuff
    private double totalIndustrialTax = 0;
    //industrial variables

    //ECONOMY
    private double GDP;
    private double yearGDP;
    private double salesTax;
    private double utilityIncome = 0;
    private double debt = 0;


    //Classes
    private BuildingManager buildingManager;
    private IndustrialHandler industrialHandler;
    private CommercialHandler commercialHandler;
    private FoodMarket foodMarket;
    private BusinessDebtManager businessDebtManager;
    private final NationalAccounts nationalAccounts = new NationalAccounts();

    /**
     * Construction lives under ServicesManager but banks like a business, so
     * the economy needs a handle on it. Injected rather than owned, because
     * ServicesManager is still what drives its production and labour.
     */
    private ConstructionHandler constructionHandler;

    //Send variables to other classes
        //all
    private int pTotalIncome;

        //stores
    private double[] storeWages = new double[11];
    private int[] storeJobs = new int [11];
        //industrial
    private double[] industrialWages = new double[11];
    private int[] industrialJobs = new int [11];

    public EconomyManager(BuildingManager buildingManager){
        this.buildingManager = buildingManager;
        industrialHandler = new IndustrialHandler();
        commercialHandler = new CommercialHandler();
        foodMarket = new FoodMarket();
        businessDebtManager = new BusinessDebtManager();
    }


    //reset for next month
    public void startOfMontEconUpdate(){

        // NOTE: industry's demand used to be the stores' *sales* figure. It is now
        // what the stores actually bought from industry last month, so the two
        // sides trade with each other rather than each guessing at the other.
        industrialHandler.setFoodDemand(commercialHandler.getReportLocalImports());
        industrialHandler.setLocalSalesValue(commercialHandler.getReportLocalPurchaseValue());
    }
    public void updateEcon(){
        getMonthGdp();
        updateCommercial();
        updateIndustrial();
        updateHeavyIndustry();
        updateMining();
    }
    public void updateCommercial(){
        commercialHandler.updateJobFillRate(fillRate);
        commercialHandler.updateStoreWages(storeWages, storeJobs, bankJobs);
        commercialHandler.setPopulation(population);
        commercialHandler.setStoreCoverage(buildingManager.getTotalStoreCoverage());
        commercialHandler.setStoreCapacity(buildingManager.getTotalStoreCapacity());
        commercialHandler.setHousehold(households);

        // Rent is charged per home now, not per head, so the handler needs to
        // know how many front doors there are and how many are lived in.
        commercialHandler.setHomes(buildingManager.getTotalHomes());
        commercialHandler.setOccupiedHomes(occupiedHomes);

        /*
         * ...and the two further inputs the rent price needs. SET here, where
         * the rest of the housing state is set; WALKED in finalEconUpdate(),
         * which is a different thing and belongs in a different place - see
         * repriceRent()'s note there.
         */
        commercialHandler.setHouseholdCount(householdCount);
        commercialHandler.setMarginalHousingCost(marginalHousingCost);
        repriceHousingCosts();

        /*
         * ...and the two segments, so each can be priced on its own scarcity.
         * The doors come from the building stock, the households from the
         * family model - which is where the studio rule lives, so it is where
         * "who can live in a studio" gets answered.
         */
        int[] bySize = buildingManager.homesBySize();
        int studioDoors = 0, familyDoors = 0;
        for (int size = 1; size < bySize.length; size++) {
            if (size <= FamilyModel.STUDIO_MAX_SIZE) studioDoors += bySize[size];
            else                                     familyDoors += bySize[size];
        }
        commercialHandler.setSegments(studioDoors, familyDoors,
                studioSeekers, familySeekers, studioSeekerHeads, familySeekerHeads);
    }

    /* =====================================================================
       WHAT IT COSTS TO HOLD A HOME, per person of capacity, per month.

       The four inputs CommercialHandler's BUILD HURDLE needs and cannot get
       for itself. Worked out here rather than there because every one of them
       belongs to something else - the templates, the land office, the tax
       policy and the bank - and this class already talks to all four.

       The FLOOR needs none of this - it is measured off the company's own
       three cost lines. This is the hurdle only. See CommercialHandler's TWO
       RENTS note.

       PRICED OFF THE CHEAPEST WAY TO HOUSE ONE MORE PERSON, land included.
       marginalHousingCost() in Game answers nearly this question and is left
       alone because half the game reads it, but it leaves LAND out - which
       was defensible when land was 1% of a house and is not now that it can
       be most of one. A rent floor that ignores the ground is a floor that
       says a tower and a field cost the same to hold.

       The cheapest template by FULL cost per head is the right one to price
       off: it is what a developer would actually build, so it is what sets
       the price at which building stops being worth it.
       ===================================================================== */
    /**
     * What one of these has to earn a month before it is worth putting up.
     *
     * THE SAME FORMULA THE RENT HURDLE USES, asked about ONE template rather
     * than about the cheapest. CommercialHandler.rentToBuild() answers "what
     * must a person of capacity pay"; this answers "what must this building
     * take", which is what an investment decision actually needs - a studio
     * block and a row of houses have different land, different materials and
     * different numbers of doors, and a per-head figure hides all three.
     *
     * ZERO PROFIT, PLUS A MARGIN. Jerus: "theyll rent at zero profit, but they
     * wont build more if new rent is zero profit." The break-even floor is the
     * first half; this is the second. Maintenance on the structure, property
     * tax on the whole asset with its plot, and then a margin - because a
     * building that exactly breaks even pays for nothing but its own upkeep
     * for ever and nobody puts one up.
     */
    public double housingBuildHurdle(BuildingsTemplate t) {

        if (t == null || buildingManager == null) return 0;

        double structure = t.getCashCost()
                + t.getConstructionMaterials()
                        * Math.max(0, buildingManager.getConstructionMaterialPrice());
        double land = t.getLandSqFt() * Math.max(0, landPricePerSqFt);
        double full = structure + land;
        if (full <= 0) return 0;

        double monthlyPropertyRate = taxPolicy != null
                ? taxPolicy.effectiveMonthlyPropertyRate(PolicySector.REAL_ESTATE)
                : 0;

        /*
         * NO INTEREST IN THIS NUMBER, and it took three tries to see why.
         *
         * The first version financed the whole asset at the sector's own rate.
         * That rate is risk-priced, so a young levered landlord is quoted 23%,
         * and the hurdle came out at twice any rent the city could bear: no
         * template ever cleared it and the city sat at five residents and no
         * homes for 240 months.
         *
         * The second used the RISK-FREE rate instead, on the reasoning that a
         * project is costed at the price of money rather than at the
         * borrower's distress. In this game the risk-free rate is the city's
         * own T-bill and it runs at 19% in the early decades - a Low-Rise's
         * hurdle came out at $266,000 a month against an income of $32,000, and
         * the city stalled at six homes.
         *
         * The third is this one, and it is the right question rather than a
         * kinder version of the wrong one. THIS ASKS WHETHER THE BUILDING
         * PAYS. Whether the COMPANY can carry the loan is a different question
         * about a different thing, and it is already asked, by
         * servicesItsOwnDebt() - which prices the sector's actual borrowing
         * against what the building would earn and refuses the loan when it
         * does not cover it. Putting interest in both places asked it twice
         * and answered no both times.
         *
         * What is left is what the building costs its owner whatever they do
         * with it and however they paid for it: the painting and the rates.
         */
        double carry = structure * CommercialHandler.MAINTENANCE_PER_YEAR / 12
                + full * monthlyPropertyRate;

        return carry * (1 + CommercialHandler.BUILD_MARGIN);
    }

    /** What the city charges per square foot today. For the advisor's costing. */
    public double getLandPricePerSqFt() { return landPricePerSqFt; }

    public void repriceHousingCosts() {

        if (commercialHandler == null || buildingManager == null) return;

        double materialPrice = Math.max(0, buildingManager.getConstructionMaterialPrice());

        double bestFull = 0, bestStructure = 0, bestLand = 0;

        /*
         * ...AND THE SAME SCAN, KEPT APART BY SEGMENT.
         *
         * The blended figure above is the cheapest home in the city and it is
         * still what a caller with no segment in mind should get. It is not
         * what either MARKET should be priced off: a studio costs more per
         * person than a family flat does, here and in the world, because it
         * puts a kitchen and a bathroom behind every door. Pricing the studio
         * market off the low-rise's cost is why no city ever built a studio -
         * see CommercialHandler.targetFor().
         *
         * The segment a template belongs to is its homeSize against the studio
         * rule, exactly as FamilyModel and priceForSegment() read it, so all
         * three places agree on which market a building is in.
         */
        double bestStudio = 0, bestFamily = 0;

        for (BuildingsTemplate t : buildingManager.getTemplates()) {
            if (t == null || t.getCategory() != BuildingType.RESIDENTIAL) continue;
            if (t.getCapacity() <= 0) continue;

            double structure = (t.getCashCost()
                    + t.getConstructionMaterials() * materialPrice) / t.getCapacity();
            double land = t.getLandSqFt() * Math.max(0, landPricePerSqFt) / t.getCapacity();
            double full = structure + land;

            if (full <= 0) continue;
            if (bestFull <= 0 || full < bestFull) {
                bestFull = full; bestStructure = structure; bestLand = land;
            }
            if (t.homeSize() <= FamilyModel.STUDIO_MAX_SIZE) {
                if (bestStudio <= 0 || full < bestStudio) bestStudio = full;
            } else {
                if (bestFamily <= 0 || full < bestFamily) bestFamily = full;
            }
        }

        commercialHandler.setHousingCosts(bestStructure, bestLand);
        commercialHandler.setSegmentHousingCosts(bestStudio, bestFamily);

        /*
         * ...and what the landlords hold, which is the denominator the
         * break-even is measured over.
         *
         * NOT getTotalHouseCapacity(), which adds the hundred heads the city
         * houses before any landlord exists - a small company's costs divided
         * by that answers "rent is nearly free".
         *
         * SITES INCLUDED, because the tax bill in the numerator includes their
         * ground. See BuildingManager.getCapacityInPortfolio().
         */
        commercialHandler.setOwnedHousingCapacity(
                buildingManager.getCapacityInPortfolio(BuildingType.RESIDENTIAL));
    }

    /**
     * Households wanting somewhere to live, and what one more person of
     * capacity would cost to supply. Both are set by Game, from FamilyModel and
     * from the building templates priced at today's materials and land.
     */
    private double householdCount;
    private double marginalHousingCost;

    public void setHouseholdCount(double count)        { this.householdCount = count; }

    /**
     * The two segments' demand, from FamilyModel. Set by Game beside the
     * household count, because it comes from the same place and describes the
     * same month.
     */
    private double studioSeekers, familySeekers;
    private double studioSeekerHeads, familySeekerHeads;

    public void setHousingSeekers(double studio, double family,
                                  double studioHeads, double familyHeads) {
        this.studioSeekers = Math.max(0, studio);
        this.familySeekers = Math.max(0, family);
        this.studioSeekerHeads = Math.max(0, studioHeads);
        this.familySeekerHeads = Math.max(0, familyHeads);
    }

    public double getStudioSeekers() { return studioSeekers; }
    public double getFamilySeekers() { return familySeekers; }
    public void setMarginalHousingCost(double perCap)  { this.marginalHousingCost = perCap; }
    public double getMarginalHousingCost()             { return marginalHousingCost; }

    /**
     * Households actually living somewhere, from FamilyModel.
     *
     * Set beside setHouseholds() (which is CAPACITY, confusingly) because the
     * two are different questions - one is how many people the buildings hold,
     * the other is how many cheques the landlords receive.
     */
    private double occupiedHomes;

    public void setOccupiedHomes(double occupied){ this.occupiedHomes = occupied; }

    /**
     * Runs the commercial sector's monthly income statement.
     *
     * NOTE: this used to happen as a side effect of printCommercialInfo(), which
     * Game.printStartOfMonth() only calls `if(reports)`. That meant turning
     * reports off (which handleMultipleMonths() does automatically) zeroed out
     * commercial sales tax and commercial GDP, and opening the report screen
     * more than once re-banked the month's income. The calculation now lives in
     * CommercialHandler.calculateCommercialResults() and is driven from here,
     * unconditionally, once per month.
     *
     * Must run before calculateSalesTax() or getMonthGdp() read the result.
     */
    public void updateCommercialReport(){
        // Before the statement runs, for the same reason industry's rate is set
        // before its own: the tax line on this report IS what the city collects
        // now (see CommercialHandler.getBusinessTaxIncome()), so it has to be
        // struck at the rate in force during the month rather than at whatever
        // the last read of getTaxIncome() happened to leave behind.
        commercialHandler.setTaxRates(
                taxPolicy.effectiveProfitRate(PolicySector.RETAIL),
                taxPolicy.effectiveProfitRate(PolicySector.REAL_ESTATE));

        // What the shop is charged on what it BUYS, which is a different tax
        // from the one above. The mill charges its own rate on local food; an
        // import has no local supplier, so it is charged at the buyer's.
        commercialHandler.setPurchaseTaxRates(
                taxPolicy.effectiveSalesRate(PolicySector.INDUSTRY),
                taxPolicy.effectiveSalesRate(PolicySector.RETAIL));
        commercialHandler.calculateCommercialResults();
    }

    /**
     * Recomputes the commercial report figures without accumulating cash. Used
     * by Game.rebuildSimulationState() so a freshly loaded save has real numbers
     * on the sector screen (and correct sales tax / GDP inputs) before the first
     * month is simulated.
     */
    public void refreshCommercialReport(){
        commercialHandler.computeMonthlyReport();
    }

    /* -----------------------------------------------------------------------
       HEAVY INDUSTRY

       Its own handler rather than a second producer inside IndustrialHandler,
       because that class divides its whole cost base by its food output to find
       food's break-even price. See HeavyIndustryHandler.
       ----------------------------------------------------------------------- */
    private final HeavyIndustryHandler heavyIndustryHandler = new HeavyIndustryHandler();

    /* --------------------------------- ORE ---------------------------------
       Its own sector, and its own market between the mines and the mills. See
       MiningHandler for why this is not just a cheaper input on the mills'
       books, and IronMarket for the band the ore clears in.
       ---------------------------------------------------------------------- */
    private final MiningHandler miningHandler = new MiningHandler();
    private final IronMarket ironMarket = new IronMarket();

    public MiningHandler getMiningHandler() { return miningHandler; }
    public IronMarket getIronMarket()       { return ironMarket; }

    /** Once-per-month mining income statement. */
    public void updateMiningReport(){
        miningHandler.setTaxRate(taxPolicy.effectiveProfitRate(PolicySector.MINING));
        miningHandler.calculateResults();
    }

    /** Pure recompute for the load path - does not bank cash. */
    public void refreshMiningReport(){ miningHandler.computeMonthlyReport(); }

    /** Reads the mines' capacity off whatever is built. */
    public void updateMining(){
        miningHandler.setCapacityTonnes(buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getProduction1));

        // Every mine ships at the same world price today, so an average is
        // exact; written as a weighted average anyway so a second mine type
        // with a different export price does not silently break it.
        double tonnes = buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getProduction1);
        double value = buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING,
                b -> b.getProduction1() * b.getProductionModifier1());

        if (tonnes > 0) {
            // What the world pays for ore, in the city's money.
            ironMarket.setExportPrice(value / tonnes * exchangeRate);
        }
        miningHandler.setExportPrice(ironMarket.getExportPrice());
    }

    public void updateMiningWages(double[] wages){
        rememberWageRates(wages);
        miningHandler.updateJobFillRate(fillRate);
        miningHandler.updateWages(wages,
                buildingManager.getJobArrayPerCategory(BuildingType.MINING));
    }

    /* ------------------------- the wage schedule -------------------------
     *
     * What ONE job of each tier costs a month, as PopulationManager last set it.
     *
     * Every handler is handed this array and immediately multiplies it out by
     * its own job counts, so nothing kept the rates themselves. The investment
     * engine needs them un-multiplied: it is pricing a building that does not
     * exist yet, and cannot read a payroll off a sector that is not running one.
     */
    private final double[] wageRates = new double[11];

    private void rememberWageRates(double[] wages) {
        if (wages == null) return;
        System.arraycopy(wages, 0, wageRates, 0,
                Math.min(wages.length, wageRates.length));
    }

    /** A copy, so nothing downstream can quietly rewrite the schedule. */
    public double[] getWageRates() { return wageRates.clone(); }

    /**
     * Prices the ore market and tells both sides what it settled at.
     *
     * Split out from the production step the way priceFoodMarket() is, so the
     * load path can have the price without lifting any ore.
     */
    public void priceIronMarket(){

        // The mills' fallback is imported scrap, and that is the market's
        // ceiling - read off the mills themselves so the two can never drift.
        double scrap = heavyIndustryHandler.getScrapPricePerTonne();
        if (scrap > 0) {
            // Already converted - the mills' input cost is set at the rate below.
            ironMarket.setScrapPrice(scrap);
        }

        ironMarket.updatePrice(miningHandler.getPotentialOutput(),
                heavyIndustryHandler.getOreDemand());

        miningHandler.setLocalPrice(ironMarket.getLocalPrice());
        miningHandler.setLocalDemand(heavyIndustryHandler.getOreDemand());
        heavyIndustryHandler.setOrePrice(ironMarket.getLocalPrice());
    }

    /**
     * Lifts the month's ore and settles who got it.
     *
     * The ground is the limit, not the mine: extractIron() hands back what was
     * actually there, which is less than asked for once a deposit runs low and
     * nothing once it is out.
     */
    public void mineIron(LandManager land){
        double lifted = land.extractIron(miningHandler.getPotentialOutput());
        miningHandler.settle(lifted, heavyIndustryHandler.getOreDemand());
        heavyIndustryHandler.setLocalOreAvailable(miningHandler.getOreSoldLocally());
    }

    public HeavyIndustryHandler getHeavyIndustryHandler(){
        return heavyIndustryHandler;
    }

    /** Once-per-month heavy industry income statement. */
    public void updateHeavyIndustryReport(){
        heavyIndustryHandler.setTaxRate(taxPolicy.effectiveProfitRate(PolicySector.HEAVY_INDUSTRY));
        heavyIndustryHandler.calculateResults();
    }

    /** Pure recompute for the load path - does not bank cash. */
    public void refreshHeavyIndustryReport(){
        heavyIndustryHandler.computeMonthlyReport();
    }

    /**
     * Reads the mills' capacity off whatever is built.
     *
     * Revenue and input cost are summed as VALUE across the buildings rather
     * than as tonnes times an average price, so two mills selling at different
     * prices need no reconciling.
     */
    public void updateHeavyIndustry(){

        heavyIndustryHandler.setOutputCapacity(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getProduction1));

        heavyIndustryHandler.setInputTonnes(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getProduction2));

        // Steel out and scrap in are both world prices, both converted.
        heavyIndustryHandler.setRevenueAtCapacity(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY,
                b -> b.getProduction1() * b.getProductionModifier1()) * exchangeRate);

        heavyIndustryHandler.setInputCostAtCapacity(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY,
                b -> b.getProduction2() * b.getProductionModifier2()) * exchangeRate);
    }

    public void updateHeavyIndustryWages(double[] wages){
        heavyIndustryHandler.updateJobFillRate(fillRate);
        heavyIndustryHandler.updateWages(wages,
                buildingManager.getJobArrayPerCategory(BuildingType.HEAVY_INDUSTRY));
    }

    /** Once-per-month industrial income statement. Same contract as the commercial one. */
    public void updateIndustrialReport(){
        industrialHandler.calculateIndustrialResults();
    }

    /** Pure recompute for the load path - does not bank cash. */
    public void refreshIndustrialReport(){
        industrialHandler.computeMonthlyReport();
    }

    public IndustrialHandler getIndustrialHandler(){
        return industrialHandler;
    }

    public FoodMarket getFoodMarket(){
        return foodMarket;
    }

    public BusinessDebtManager getBusinessDebtManager(){
        return businessDebtManager;
    }

    public void setConstructionHandler(ConstructionHandler handler){
        this.constructionHandler = handler;
    }

    /**
     * The sectors' savings abroad. Game moves them; this reads them, because
     * a sector's balance sheet is worth what it holds abroad as well as what
     * it holds at the counter, and the lender prices it on the whole.
     */
    private OutwardInvestment outward;

    public void setOutwardInvestment(OutwardInvestment outward) { this.outward = outward; }
    public OutwardInvestment getOutwardInvestment() { return outward; }

    /**
     * The share register. Game runs the offerings and pays the dividends;
     * this holds the two cash-flow lines each sector's books read them off,
     * cleared at the top of the month by clearEquityFlows().
     */
    private Equity equity;
    public void setEquity(Equity equity) { this.equity = equity; }
    public Equity getEquity() { return equity; }

    private final java.util.Map<String, Double> equityRaised = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> dividendsPaid = new java.util.LinkedHashMap<>();

    public void clearEquityFlows() { equityRaised.clear(); dividendsPaid.clear(); }
    public void recordEquityRaised(String sector, double amount) {
        if (sector == null || !(amount > 0)) return;
        equityRaised.merge(sector, amount, Double::sum);
    }
    public void recordDividendPaid(String sector, double amount) {
        if (sector == null || !(amount > 0)) return;
        dividendsPaid.merge(sector, amount, Double::sum);
    }
    public double getEquityRaised(String sector)  { return equityRaised.getOrDefault(sector, 0.0); }
    public double getDividendsPaid(String sector) { return dividendsPaid.getOrDefault(sector, 0.0); }

    /** What one sector holds abroad, in the city's money at the rate it was last valued at. */
    public double getForeignAssets(String sector) {
        return outward == null ? 0 : outward.localValue(sector);
    }

    /** Construction's monthly income statement, banked to its own cash. */
    public void updateConstructionReport(){
        if (constructionHandler != null) {
            // Rate in force during the month, before the statement - as for
            // every other sector.
            constructionHandler.setTaxRate(taxPolicy.effectiveProfitRate(PolicySector.CONSTRUCTION));
            constructionHandler.calculateConstructionResults();
        }
    }

    /* -----------------------------------------------------------------------
       Sector cash by name, so an Investor can be written once against a sector
       string rather than three times against three different handlers.
       ----------------------------------------------------------------------- */

    public double getSectorCash(String sector){
        if (BusinessDebtManager.RETAIL.equals(sector))      return commercialHandler.getCommercialCash();
        if (BusinessDebtManager.REAL_ESTATE.equals(sector)) return commercialHandler.getRealEstateCash();
        if (BusinessDebtManager.INDUSTRY.equals(sector))    return industrialHandler.getIndustrialCash();
        if (BusinessDebtManager.CONSTRUCTION.equals(sector) && constructionHandler != null) {
            return constructionHandler.getCash();
        }
        if (BusinessDebtManager.MINING.equals(sector)) {
            return miningHandler.getCash();
        }
        if (BusinessDebtManager.HEAVY_INDUSTRY.equals(sector)) {
            return heavyIndustryHandler.getCash();
        }
        return 0;
    }

    public ConstructionHandler getConstructionHandler(){
        return constructionHandler;
    }

    public void setSectorCash(String sector, double cash){
        if (BusinessDebtManager.RETAIL.equals(sector))      commercialHandler.setCommercialCash(cash);
        else if (BusinessDebtManager.REAL_ESTATE.equals(sector)) commercialHandler.setRealEstateCash(cash);
        else if (BusinessDebtManager.INDUSTRY.equals(sector))    industrialHandler.setIndustrialCash(cash);
        else if (BusinessDebtManager.CONSTRUCTION.equals(sector) && constructionHandler != null) {
            constructionHandler.setCash(cash);
        }
        else if (BusinessDebtManager.MINING.equals(sector)) {
            miningHandler.setCash(cash);
        }
        else if (BusinessDebtManager.HEAVY_INDUSTRY.equals(sector)) {
            heavyIndustryHandler.setCash(cash);
        }
    }

    /* =======================================================================
       PRIVATE SECTOR CREDIT

       Two halves, either side of the monthly income statements.

       updateBusinessCredit() runs FIRST: it reprices each sector off its current
       leverage and hands the handlers this month's interest bill, so the
       statements include it and the cash they bank is already net of it.

       settleBusinessCredit() runs AFTER: it advances the loans, takes principal
       back on the ones that matured, and writes a new loan for any sector left
       short. Running it before the statements would have charged interest on
       money the sector had not borrowed yet.
       ======================================================================= */

    /* =====================================================================
       WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS

       Recorded by Game when it hands the money out, because Game is what knows
       the split - the bank has no idea who its depositors are. Read by
       SectorBooks a few lines later, in the same month, so nothing is carried
       and nothing can go stale.
       ===================================================================== */

    private final java.util.Map<String, Double> depositInterestPaid =
            new java.util.LinkedHashMap<>();

    /** Wipes last month's figures. Called before the month's are handed out. */
    public void clearDepositInterest() { depositInterestPaid.clear(); }

    public void recordDepositInterest(String sector, double amount) {
        if (sector == null || !(amount > 0)) return;
        depositInterestPaid.merge(sector, amount, Double::sum);
    }

    public double getDepositInterestPaid(String sector) {
        return depositInterestPaid.getOrDefault(sector, 0.0);
    }

    public void updateBusinessCredit(double governmentRate){

        businessDebtManager.setRiskFreeRate(governmentRate);

        // Leverage is measured against the balance sheets, so they have to be
        // told what the sector owes before they are used to price what it owes.
        refreshCreditAssets();

        businessDebtManager.updateRates();

        commercialHandler.setRetailInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.RETAIL));
        commercialHandler.setRealEstateInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.REAL_ESTATE));
        industrialHandler.setInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.INDUSTRY));

        if (constructionHandler != null) {
            constructionHandler.setInterestExpense(
                    businessDebtManager.getMonthlyInterest(BusinessDebtManager.CONSTRUCTION));
        }

        heavyIndustryHandler.setInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.HEAVY_INDUSTRY));

        miningHandler.setInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.MINING));

        interestCharges = new double[BuildingType.values().length];
        interestCharges[BuildingType.COMMERCIAL.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.RETAIL);
        interestCharges[BuildingType.RESIDENTIAL.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.REAL_ESTATE);
        interestCharges[BuildingType.INDUSTRIAL.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.INDUSTRY);
        interestCharges[BuildingType.CONSTRUCTION.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.CONSTRUCTION);
        interestCharges[BuildingType.HEAVY_INDUSTRY.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.HEAVY_INDUSTRY);
        interestCharges[BuildingType.MINING.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.MINING);

        pushBalanceSheetInputs();
    }

    /**
     * What each sector's borrowing actually cost it this month.
     *
     * Saved for the same reason the property-tax charges are: it is priced off
     * the balance sheet as it stood WHEN the month ran, and re-pricing it later
     * from the sheet the month ended on gives a different answer - $7.30 against
     * $4.87 on a city that had just ordered two depots. Re-derivation is not
     * restoration when the thing being derived is a flow.
     */
    public double[] getInterestCharges() {
        return (interestCharges == null)
                ? new double[BuildingType.values().length]
                : interestCharges.clone();
    }

    /** Assigns expense figures only. No money moves; see restorePropertyTaxCharges. */
    public void restoreInterestCharges(double[] charges) {

        double[] c = widen(charges);
        if (c == null) return;

        commercialHandler.setRetailInterestExpense(c[BuildingType.COMMERCIAL.ordinal()]);
        commercialHandler.setRealEstateInterestExpense(c[BuildingType.RESIDENTIAL.ordinal()]);
        industrialHandler.setInterestExpense(c[BuildingType.INDUSTRIAL.ordinal()]);
        heavyIndustryHandler.setInterestExpense(c[BuildingType.HEAVY_INDUSTRY.ordinal()]);
        miningHandler.setInterestExpense(c[BuildingType.MINING.ordinal()]);

        if (constructionHandler != null) {
            constructionHandler.setInterestExpense(c[BuildingType.CONSTRUCTION.ordinal()]);
        }

        interestCharges = c;
    }

    /**
     * Pads a per-category array saved by an older build up to today's length.
     *
     * These arrays are indexed by BuildingType.ordinal(), so every category
     * added to that enum makes every existing save's copy one slot short. The
     * old code refused anything shorter than the current length and silently
     * restored nothing - which meant adding INFRASTRUCTURE would have wiped the
     * property tax and interest off the first month of every save ever taken,
     * a bug that would have shown up as exactly the kind of one-month income
     * drift this whole area was fixed for.
     *
     * Padding with zero is right rather than merely convenient: the missing
     * slots are categories that did not exist when the month ran, so nothing
     * was charged to them. A LONGER array - a save from a newer build - is
     * refused; guessing at a layout we do not know is worse than restoring
     * nothing, and GameFiles already turns those away by save format.
     *
     * @return an array of exactly the current length, or null if unusable
     */
    private double[] widen(double[] charges) {

        int wanted = BuildingType.values().length;

        if (charges == null || charges.length > wanted) return null;
        if (charges.length == wanted) return charges.clone();

        double[] padded = new double[wanted];
        System.arraycopy(charges, 0, padded, 0, charges.length);
        return padded;
    }

    /**
     * Tells the credit manager what each sector is worth right now.
     *
     * Its own method because two things need it at different points in the
     * month: pricing credit at the start, and judging solvency at the end.
     * Restructuring a sector on assets measured before its results were known
     * would be writing off the wrong month's debt.
     */
    public void refreshCreditAssets(){

        // ...plus what each holds abroad, which the handlers' own sheets do
        // not carry: a dollar in a foreign bond is as much the sector's as a
        // dollar at the counter, and a lender that ignored it would call a
        // rich sector broke. See OutwardInvestment.
        businessDebtManager.setAssets(BusinessDebtManager.RETAIL,
                commercialHandler.getRetailBalanceSheet().getTotalAssets()
                        + getForeignAssets(BusinessDebtManager.RETAIL));
        businessDebtManager.setAssets(BusinessDebtManager.REAL_ESTATE,
                commercialHandler.getRealEstateBalanceSheet().getTotalAssets()
                        + getForeignAssets(BusinessDebtManager.REAL_ESTATE));
        businessDebtManager.setAssets(BusinessDebtManager.INDUSTRY,
                industrialHandler.getBalanceSheet().getTotalAssets()
                        + getForeignAssets(BusinessDebtManager.INDUSTRY));

        if (constructionHandler != null) {
            businessDebtManager.setAssets(BusinessDebtManager.CONSTRUCTION,
                    constructionHandler.getBalanceSheet().getTotalAssets()
                            + getForeignAssets(BusinessDebtManager.CONSTRUCTION));
        }

        businessDebtManager.setAssets(BusinessDebtManager.HEAVY_INDUSTRY,
                heavyIndustryHandler.getBalanceSheet().getTotalAssets()
                        + getForeignAssets(BusinessDebtManager.HEAVY_INDUSTRY));
        businessDebtManager.setAssets(BusinessDebtManager.MINING,
                miningHandler.getBalanceSheet().getTotalAssets()
                        + getForeignAssets(BusinessDebtManager.MINING));

        // ...and the cash inside those assets, which is what an overdraft is.
        for (String sector : BusinessDebtManager.SECTORS) {
            businessDebtManager.setCash(sector, getSectorCash(sector));
        }
    }

    /**
     * End of the month: work out who is beyond saving and write their debt down
     * to what their assets support.
     *
     * @return total written off this month
     */
    public double settleInsolvency(){
        pushBalanceSheetInputs();
        refreshCreditAssets();
        businessDebtManager.advanceBlocks();
        double writtenOff = businessDebtManager.restructureInsolventSectors();

        /*
         * THE OVERDRAFT A RESTRUCTURE FORGAVE. The money is put back into the
         * sector's balance here - it arrives from outside the city's pools,
         * from the creditors who ate it, and MoneyAudit declares it as such
         * (getOverdraftForgiven(), Scope.VALUATION), exactly as the bank's
         * resolution loss is declared. See BusinessDebtManager.restructure().
         */
        overdraftForgivenThisMonth = 0;
        overdraftForgivenThisMonthBySector.clear();
        for (String sector : BusinessDebtManager.SECTORS) {
            double forgiven = businessDebtManager.takeOverdraftForgiven(sector);
            overdraftForgivenThisMonthBySector.put(sector, forgiven);
            if (forgiven > 0) {
                setSectorCash(sector, getSectorCash(sector) + forgiven);
                overdraftForgivenThisMonth += forgiven;
                overdraftForgivenBySector.merge(sector, forgiven, Double::sum);
                GameLog.note(String.format(
                        "%s went bankrupt: $%,.0fk of bills it could not pay were written off.",
                        sector, forgiven));
            }
        }
        return writtenOff;
    }

    /** This month's forgiven overdrafts, for the audit. Zeroed each month above. */
    private double overdraftForgivenThisMonth;
    private final java.util.Map<String, Double> overdraftForgivenBySector = new java.util.LinkedHashMap<>();

    public double getOverdraftForgiven() { return overdraftForgivenThisMonth; }

    private final java.util.Map<String, Double> overdraftForgivenThisMonthBySector = new java.util.LinkedHashMap<>();

    /** This month's forgiven overdraft for one sector, for its cash-flow statement. */
    public double getOverdraftForgivenThisMonth(String sector) {
        return overdraftForgivenThisMonthBySector.getOrDefault(sector, 0.0);
    }

    public double getOverdraftForgivenTotal(String sector) {
        return overdraftForgivenBySector.getOrDefault(sector, 0.0);
    }

    /** Book values and outstanding debt, refreshed onto each set of books. */
    public void pushBalanceSheetInputs(){

        // Buildings are finished AND on site - see
        // BuildingManager.getWorkInProgressByCategory() for what leaving the
        // unfinished ones off did to every loan that ever built one.
        // Land is a real figure now rather than the placeholder zero it was
        // when these books went in: square feet held, at the city's current
        // price. The same number the property tax is assessed on, deliberately -
        // a business should be taxed on the value its own balance sheet claims.
        commercialHandler.setRetailBalanceSheetInputs(
                landValueOf(BuildingType.COMMERCIAL),
                buildingManager.getBuildingsValueByCategory(BuildingType.COMMERCIAL),
                businessDebtManager.getPrincipal(BusinessDebtManager.RETAIL));

        // Real estate owns the housing stock - it is what collects the rent.
        commercialHandler.setRealEstateBalanceSheetInputs(
                landValueOf(BuildingType.RESIDENTIAL),
                buildingManager.getBuildingsValueByCategory(BuildingType.RESIDENTIAL),
                businessDebtManager.getPrincipal(BusinessDebtManager.REAL_ESTATE));

        industrialHandler.setLandValue(landValueOf(BuildingType.INDUSTRIAL));
        industrialHandler.setBuildingsValue(
                buildingManager.getBuildingsValueByCategory(BuildingType.INDUSTRIAL));
        industrialHandler.setBondsPayable(
                businessDebtManager.getPrincipal(BusinessDebtManager.INDUSTRY));

        if (constructionHandler != null) {
            constructionHandler.setLandValue(landValueOf(BuildingType.CONSTRUCTION));
            constructionHandler.setBuildingsValue(
                    buildingManager.getBuildingsValueByCategory(BuildingType.CONSTRUCTION));
            constructionHandler.setBondsPayable(
                    businessDebtManager.getPrincipal(BusinessDebtManager.CONSTRUCTION));
        }

        heavyIndustryHandler.setLandValue(landValueOf(BuildingType.HEAVY_INDUSTRY));
        heavyIndustryHandler.setBuildingsValue(
                buildingManager.getBuildingsValueByCategory(BuildingType.HEAVY_INDUSTRY));
        heavyIndustryHandler.setBondsPayable(
                businessDebtManager.getPrincipal(BusinessDebtManager.HEAVY_INDUSTRY));

        miningHandler.setLandValue(landValueOf(BuildingType.MINING));
        miningHandler.setBuildingsValue(
                buildingManager.getBuildingsValueByCategory(BuildingType.MINING));
        miningHandler.setBondsPayable(
                businessDebtManager.getPrincipal(BusinessDebtManager.MINING));
    }

    /** What a category's land is worth at the city's current price. */
    public double landValueOf(BuildingType category){
        return buildingManager.getLandSqFtByCategory(category) * landPricePerSqFt;
    }

    public void settleBusinessCredit(int month){

        businessDebtManager.processMonth();

        settleSector(BusinessDebtManager.RETAIL, month,
                commercialHandler.getCommercialCash(),
                commercialHandler.getReportRetailNetIncome(),
                commercialHandler::setCommercialCash);

        settleSector(BusinessDebtManager.REAL_ESTATE, month,
                commercialHandler.getRealEstateCash(),
                commercialHandler.getReportRealEstateNetIncome(),
                commercialHandler::setRealEstateCash);

        settleSector(BusinessDebtManager.INDUSTRY, month,
                industrialHandler.getIndustrialCash(),
                industrialHandler.getNetIncome(),
                industrialHandler::setIndustrialCash);

        if (constructionHandler != null) {
            settleSector(BusinessDebtManager.CONSTRUCTION, month,
                    constructionHandler.getCash(),
                    constructionHandler.getNetIncome(),
                    constructionHandler::setCash);
        }

        settleSector(BusinessDebtManager.HEAVY_INDUSTRY, month,
                heavyIndustryHandler.getCash(),
                heavyIndustryHandler.getNetIncome(),
                heavyIndustryHandler::setCash);

        settleSector(BusinessDebtManager.MINING, month,
                miningHandler.getCash(),
                miningHandler.getNetIncome(),
                miningHandler::setCash);

        pushBalanceSheetInputs();
    }

    /**
     * Repay what matured, then borrow if that left the sector short.
     *
     * A maturing loan is usually rolled: the balloon takes cash negative and the
     * shortfall check immediately writes a replacement. That is deliberate - it
     * is what a business with no spare cash actually does - and it reprices the
     * debt at whatever the sector's credit is worth by then.
     */
    private void settleSector(String sector, int month, double cash, double netIncome,
                              java.util.function.DoubleConsumer setCash){

        cash -= businessDebtManager.takeMaturedPrincipal(sector);

        double loss = Math.max(-netIncome, 0);
        cash += businessDebtManager.coverShortfall(sector, cash, loss, month);

        setCash.accept(cash);
    }

    public void updateIndustrial(){
        // Before the statement runs, so the month is taxed at the rate in force
        // during it rather than at whatever the last read of getTaxIncome()
        // happened to leave behind.
        industrialHandler.setTaxRate(taxPolicy.effectiveProfitRate(PolicySector.INDUSTRY));
        updateFoodProduction();
        industrialHandler.setFoodCapacity(buildingManager.getFoodCapacity());
        // What the shops will want, off the headcount - the same figure
        // runRetirement() judges the sector's demand by. See
        // IndustrialHandler.setPlannedDemand().
        industrialHandler.setPlannedDemand(
                Math.min(commercialHandler.getStoreCoverage(), population));

        industrialHandler.updateJobFillRate(fillRate);
        industrialHandler.updateIndustrialWages(industrialWages, industrialJobs);
        industrialHandler.updateIndustrialHandler();

    }
    /**
     * Clears the food market for the month, then lets industry produce.
     *
     * Order matters and is unchanged from before: the stores see the inventory
     * industry was holding at the start of the month, not this month's output.
     */
    public void procedureUpdate(){
        priceFoodMarket();
        priceIronMarket();
        industrialHandler.produceFood();
    }

    /**
     * Clears the food market and tells both sides the price. Produces nothing.
     *
     * Split out from procedureUpdate() so the load path can have the prices
     * without the production - see refreshEconPrices().
     */
    private void priceFoodMarket(){

        // 1. price the month from production flow, the stockpile and the stores'
        //    intended purchase
        // Priced on what the mills will bring, not on their nameplate - see
        // IndustrialHandler.getPlannedOutput().
        foodMarket.updatePrice(industrialHandler.getPlannedOutput(),
                industrialHandler.getFoodInventory(),
                commercialHandler.getExpectedPurchase());

        // 2. industry decides how much it will release at that price - below its
        //    own cost per unit it withholds and lets inventory build
        double offered = industrialHandler.offerToMarket(foodMarket);

        // 3. both sides trade on the same price
        industrialHandler.setFoodPrice(foodMarket.getLocalPrice());
        industrialHandler.setImportPrice(foodMarket.getImportPrice());
        commercialHandler.setFoodPrice(foodMarket.getLocalPrice());
        // Already in the city's money - FoodMarket converts at the rate.
        commercialHandler.setImportPrice(foodMarket.getImportPrice());
        commercialHandler.setFoodAvailableForSale((int) offered);
    }

    /**
     * What the load path needs from finalEconUpdate(), and nothing else.
     *
     * finalEconUpdate() is not a refresh. It runs a month: procedureUpdate()
     * ends by PRODUCING food, updateFinalIndustrialHandler() then subtracts what
     * was sold and imported, and updateCommercialHandler() has the shops sell
     * their stock and buy more. The load path was calling it, so opening a save
     * ran a month of production and trading with the calendar standing still -
     * the mills made 2,185 units of food out of nothing every time.
     *
     * That was also why a reloaded city slowly drifted away from the one it was
     * saved from rather than converging back to it: the extra production moved
     * the food market, so the shops started importing globally at import prices
     * instead of buying from local industry, and every month after compounded
     * the difference.
     *
     * This does the pricing and the electricity draw the expense lines need, and
     * changes nothing anyone owns.
     */
    public void refreshEconPrices(){
        priceFoodMarket();
        priceIronMarket();
        setElectricityConsumption();
    }
    public void finalEconUpdate(){

        procedureUpdate();
        industrialHandler.updateFinalIndustrialHandler();
        commercialHandler.updateCommercialHandler();

        /*
         * RENT WALKS HERE, AND ONLY HERE, for the same reason this whole method
         * is not on the load path: moving a lagged price one step toward its
         * target IS a month passing. It started life in updateCommercial(),
         * beside the setters that feed it, and that was wrong in the way this
         * codebase keeps being wrong - updateEcon() is called by
         * rebuildSimulationState(), so every load walked rent an extra twelfth
         * and a reloaded city charged 0.174309 where the live one charged
         * 0.174210. SaveFileCheck caught it inside a minute, which is the
         * ninth time that assertion has earned its keep.
         *
         * The inputs are still set in updateCommercial(). Setting state is not
         * advancing it.
         */
        commercialHandler.repriceRent();

        this.interest = 0;
        setElectricityConsumption();
    }

    /**
     * The month's sales tax, as payable less input tax credits.
     *
     * NOTE: this used to be three lines - food-plant revenue, store revenue and
     * the retail import tax, each times the one city rate. It taxed the same
     * food TWICE, once at the plant and again at the store, and it never touched
     * Heavy Industry or Mining at all, so steel and ore moved untaxed while
     * bread was charged at every step.
     *
     * Every sector now charges on what it sells and claims back the tax embedded
     * in what it bought, so the city collects tax on VALUE ADDED and the total
     * across the chain is the tax on final consumption. SalesTaxLedger carries
     * the reasoning and the two departures from real HST.
     *
     * RESIDENTIAL RENT IS AN EXEMPT SUPPLY - no tax charged, no credits claimed -
     * exactly as long-term residential rent is exempt under real HST. Taxing it
     * would put a fifteen-percent charge on every tenant in the city, which is a
     * balance change nobody asked for hiding inside a plumbing change.
     */
    /**
     * SETTLED ONCE A MONTH, not recomputed on every read.
     *
     * This is the same rule property tax already follows two methods down, and
     * for the same reason: what the city collects has to be the figure the
     * businesses were actually charged, not a re-derivation from whatever state
     * happens to be loaded when somebody reads the total.
     *
     * It matters more here than it did for the old three-line sales tax, because
     * the VAT reads figures that a reload does NOT restore - construction
     * carries no report state at all, so its revenue and materials expense are
     * live fields that come back as whatever the fresh handler was built with.
     * Recomputing on the load path produced a sales tax of 267 against the 438
     * the same month had actually collected, in nine months of a 4,000-month
     * playtest. The ledger is a flow; flows are carried, never rebuilt.
     */
    public double settleSalesTax(){

        salesTaxLedger.startMonth();

        // --- Mining: ore sold to local mills is taxable, ore leaving the city
        //     is zero-rated. No goods inputs are tracked, so no credit.
        // REPORT figures, not live ones: oreSoldLocally is set by settle() during
        // the month and is not restored by a load, while rOreSoldLocally is.
        salesTaxLedger.recordSales(PolicySector.MINING,
                miningHandler.getReportOreSoldLocally() * miningHandler.getReportLocalPrice());
        salesTaxLedger.recordExport(PolicySector.MINING,
                miningHandler.getReportOreExported() * miningHandler.getReportExportPrice());

        // --- Heavy industry: sells steel, credits the ore and scrap it bought.
        //     Local ore was charged at MINING's rate - the credit has to be what
        //     the supplier actually remitted, or the city refunds tax it never
        //     collected. Imported scrap is charged at the buyer's own rate.
        /*
         * ZERO-RATED, because every tonne of it leaves the city.
         *
         * This was recordSales(), which charged the mills full VAT on an export
         * while the mines beside them shipped ore out free - $8.2M a month on
         * Jerus's slot 7, more than half the whole VAT take, and the single
         * biggest reason heavy industry looked unprofitable: it reported losing
         * $1.2M a month while actually losing $9.4M. The ledger's own header
         * has said "EXPORTS ARE ZERO-RATED" since the day it was written; the
         * mills were simply never split into a local half and an export half,
         * because they have no local half.
         *
         * The credits behind the export stay claimable, so a month of heavy
         * milling can end with the city owing the mills money. That is what
         * zero-rating means - see the header - and it is why it is a real
         * incentive to export rather than a bookkeeping nicety. Jerus's call.
         */
        salesTaxLedger.recordExport(PolicySector.HEAVY_INDUSTRY,
                heavyIndustryHandler.getReportRevenue());
        salesTaxLedger.recordInputTax(PolicySector.HEAVY_INDUSTRY,
                heavyIndustryHandler.getReportLocalOreUsed() * ironMarket.getLocalPrice()
                        * taxPolicy.effectiveSalesRate(PolicySector.MINING));
        /*
         * The mill's own scrap price, not the market's copy of it.
         *
         * These are two sources for one number, held equal by priceIronMarket()
         * copying one into the other each month - and that copy is guarded by
         * `if (scrap > 0)`, so with no mills standing getScrapPricePerTonne()
         * returns 0, the copy is skipped, and ironMarket keeps its .40 default.
         * The two were already unequal in that state and the VAT line read a
         * stale price. Charging the buyer at the price the buyer actually paid
         * removes the question.
         */
        salesTaxLedger.chargeImport(PolicySector.HEAVY_INDUSTRY,
                heavyIndustryHandler.getReportScrapImported()
                        * heavyIndustryHandler.getScrapPricePerTonne(),
                taxPolicy);

        // --- Food processing: sells to the stores.
        salesTaxLedger.recordSales(PolicySector.INDUSTRY,
                industrialHandler.getGrossRevenue());

        // --- Retail: sells to residents, which is where the chain ends and the
        //     tax finally sticks. Credits the tax inside its inventory.
        salesTaxLedger.recordSales(PolicySector.RETAIL,
                commercialHandler.getGrossRevenue());
        /*
         * THE NET PURCHASE, AT THE SUPPLIER'S RATE - the same shape as heavy
         * industry three blocks up, which had it right all along.
         *
         * This read `getReportInventoryCost() * effectiveSalesRate(INDUSTRY)`,
         * and was wrong three ways at once. The inventory cost is TAX-INCLUSIVE,
         * so the credit was struck on a figure that already contained the tax
         * and over-claimed by exactly the rate - measured at 15%, enough on its
         * own to make the city's whole sales tax NEGATIVE. The imported half was
         * already inside that cost, so chargeImport() credited it a second time.
         * And chargeImport() takes a landed VALUE and was handed getImportTax(),
         * which is already a tax - so that second credit came out in units of
         * tax squared.
         */
        salesTaxLedger.recordInputTax(PolicySector.RETAIL,
                commercialHandler.getReportLocalPurchaseValue()
                        * taxPolicy.effectiveSalesRate(PolicySector.INDUSTRY));
        salesTaxLedger.chargeImport(PolicySector.RETAIL,
                commercialHandler.getReportImportPurchaseValue(), taxPolicy);

        /*
         * --- Construction: sells the work it puts in place, credits materials.
         *
         * THE REPORT FIELDS, NOT THE LIVE ONES. This read getRevenue(), and
         * calculateConstructionResults() - which runs four lines earlier in the
         * same month - sets `revenue = 0` as its last act. So the ledger has
         * been handed a zero every month since the VAT went in, and the
         * construction sector has never remitted a cent. rRevenue exists for
         * exactly this and its own comment says so: "anything that wants to know
         * what was actually charged reads these".
         *
         * Same for the materials credit, which read the live materialsExp - a
         * field updateServices() rewrites later in the month with NEXT month's
         * inputs. It happened to still hold this month's figure at this point,
         * which is the worst kind of correct.
         */
        if (constructionHandler != null) {
            salesTaxLedger.recordSales(PolicySector.CONSTRUCTION,
                    constructionHandler.getReportRevenue());
            salesTaxLedger.recordInputTax(PolicySector.CONSTRUCTION,
                    constructionHandler.getReportMaterialsExpense()
                            * taxPolicy.effectiveSalesRate(PolicySector.CONSTRUCTION));
        }

        salesTax = salesTaxLedger.settle(taxPolicy);

        /*
         * AND THE SECTORS PAY IT, ON THEIR OWN INCOME STATEMENTS.
         *
         * Until 2026-09-06 the city collected the ledger's total and nobody was
         * debited: retail paid a markup on its purchases to nobody at all, and
         * the other five sectors paid nothing. The rate follows the producer, so
         * the producer remits - its payable on what it sold, less the credit on
         * what it bought, out of its own cash. A sector in a refund position is
         * credited. Every dollar the treasury books here comes out of a pool
         * MoneyAudit can see.
         *
         * That fix moved the CASH and stopped there. This one puts the same
         * figure on the statement the cash belongs to. It used to be
         *
         *     for each sector: sectorCash -= ledger.getNet(sector)
         *
         * with no handler ever told, so every sector reported a profit it had
         * already paid part of away, and the profit tax was charged on that
         * overstated figure. The bank movement is one line now, made of the
         * numbers the statement shows: cash += net income (which has the VAT in
         * it) less the profit tax (which is struck after it). One movement, one
         * place, one set of numbers.
         */
        bankSectorMonths();
        return salesTax;
    }

    /**
     * Hands every sector the VAT it owes and lets it bank the month.
     *
     * The five statements were computed BEFORE this - the ledger above is struck
     * from their revenue - so each one is recomputed here with its tax line
     * filled in. computeMonthlyReport() is pure and the inputs have not moved
     * between the two calls, so the second pass changes exactly two lines: net
     * income, and the profit tax struck off it.
     */
    private void bankSectorMonths() {
        commercialHandler.bankMonth(salesTaxLedger.getNet(PolicySector.RETAIL),
                                    salesTaxLedger.getNet(PolicySector.REAL_ESTATE));
        industrialHandler.bankMonth(salesTaxLedger.getNet(PolicySector.INDUSTRY));
        heavyIndustryHandler.bankMonth(salesTaxLedger.getNet(PolicySector.HEAVY_INDUSTRY));
        miningHandler.bankMonth(salesTaxLedger.getNet(PolicySector.MINING));
        if (constructionHandler != null) {
            constructionHandler.bankMonth(salesTaxLedger.getNet(PolicySector.CONSTRUCTION));
        }
    }

    /**
     * The same figures, put back on a reloaded city WITHOUT banking anything.
     *
     * The statements are recomputed by Game.rebuildSimulationState(), and a
     * recomputed statement with no tax line is the overstated profit all over
     * again on the first screen a returning player opens. Read off the restored
     * ledger rather than saved a second time in each handler, so there is one
     * record of the month's VAT and it cannot disagree with itself.
     */
    private void handOutSalesTax() {
        commercialHandler.setSalesTaxRemitted(salesTaxLedger.getNet(PolicySector.RETAIL),
                                              salesTaxLedger.getNet(PolicySector.REAL_ESTATE));
        industrialHandler.setSalesTaxRemitted(salesTaxLedger.getNet(PolicySector.INDUSTRY));
        heavyIndustryHandler.setSalesTaxRemitted(
                salesTaxLedger.getNet(PolicySector.HEAVY_INDUSTRY));
        miningHandler.setSalesTaxRemitted(salesTaxLedger.getNet(PolicySector.MINING));
        if (constructionHandler != null) {
            constructionHandler.setSalesTaxRemitted(
                    salesTaxLedger.getNet(PolicySector.CONSTRUCTION));
        }
    }

    /** Whoever the city owes this month, or null. See SalesTaxLedger. */
    public PolicySector getDeepestRefundSector() { return salesTaxLedger.deepestRefund(); }
    public double getSectorSalesTax(PolicySector s) { return salesTaxLedger.getNet(s); }

    public SalesTaxLedger getSalesTaxLedger() { return salesTaxLedger; }

    /**
     * Puts a saved month's VAT back, AND the total that came out of it.
     *
     * Both, through one call, because they are one fact. Restoring only the
     * ledger left salesTax at the zero a fresh EconomyManager starts with, so a
     * reloaded city collected nothing where the live one had collected 438 -
     * which is the same class of mistake as recomputing the ledger, arrived at
     * from the opposite direction.
     */
    public boolean restoreSalesTaxLedger(double[] state) {
        if (!salesTaxLedger.restoreLedgerState(state)) return false;
        salesTax = salesTaxLedger.getTotalRemitted();
        // ...and the sectors get their own share of it back, so the statements
        // rebuildSimulationState() is about to recompute have their tax line.
        handOutSalesTax();
        return true;
    }

    private final SalesTaxLedger salesTaxLedger = new SalesTaxLedger();

    public double getTaxIncome(){
        double tax = 0;
        totalBusinessTax = commercialHandler.getBusinessTaxIncome(
                taxPolicy.effectiveProfitRate(PolicySector.RETAIL),
                taxPolicy.effectiveProfitRate(PolicySector.REAL_ESTATE));
        totalIndustrialTax = industrialHandler.getIndustrialTaxIncome(
                taxPolicy.effectiveProfitRate(PolicySector.INDUSTRY));

        // Banded, and summed job type by job type rather than as one total times
        // an average - averaging would throw away exactly the distinction the
        // bands exist to express while still looking about right.
        totalWageTax = taxPolicy.wageTaxOn(staffedWagePerType, null);

        totalHeavyIndustryTax =
                heavyIndustryHandler.getTaxIncome(
                        taxPolicy.effectiveProfitRate(PolicySector.HEAVY_INDUSTRY))
                + miningHandler.getTaxIncome(
                        taxPolicy.effectiveProfitRate(PolicySector.MINING));
        // Property tax is assigned by chargePropertyTax() earlier in the month,
        // not recomputed here: the sectors have already been billed it and have
        // already borne it in their income statements. Recomputing would risk
        // the city collecting a different figure from the one the businesses
        // paid, which is exactly the kind of money-from-nowhere this codebase
        // keeps producing.
        // salesTax is NOT recomputed here - see settleSalesTax(). Same rule as
        // property tax below, and the same reason.
        /*
         * Pension contributions, taken off the same staffed wage bill the wage
         * tax is charged on. Revenue to the city, and NOT a tax - the money is
         * earmarked in the sense that the screen shows it against the pension
         * bill, though nothing ring-fences it, which is what pay-as-you-go
         * means.
         */
        totalContributions = SocialSecurity.contributionsOn(totalWage,
                taxPolicy.getContributionRate());

        totalConstructionTax = constructionHandler == null ? 0
                : constructionHandler.getTaxIncome(
                        taxPolicy.effectiveProfitRate(PolicySector.CONSTRUCTION));

        tax = totalBusinessTax + totalIndustrialTax + totalWageTax + salesTax
                + totalHeavyIndustryTax + totalConstructionTax + totalPropertyTax
                + totalContributions + healthcareFees + educationFees + totalBankTax;
        return tax;
    }

    /* =====================================================================
       THE HEALTH SERVICE'S BOOKS

       Held here rather than computed here, for the same reason the senior
       count is: the buildings, the job fill and the age pyramid all belong to
       Game, and this class has no business reaching into any of them.
       Healthcare works the figures out; these two fields are where they land
       so that getTaxIncome() and getExpenses() can see them.

       BOTH SIDES, SEPARATELY. The bill is the gross cost - what actually
       leaves the treasury every month - and the fees are revenue like the
       pension contributions beside them. Netting them into one number would
       make a large service that nearly pays for itself look identical to a
       small one that does not.
       ===================================================================== */

    private double healthcareBill;
    private double healthcareFees;

    public void setHealthcare(double grossCost, double fees) {
        this.healthcareBill = Math.max(0, grossCost);
        this.healthcareFees = fees;
    }

    public double getHealthcareBill() { return healthcareBill; }
    public double getHealthcareFees() { return healthcareFees; }
    public double getHealthcareNet()  { return healthcareBill - healthcareFees; }

    /* =====================================================================
       EDUCATION, on the same terms as healthcare.

       The city builds the schools, pays the teachers and covers most of the
       tuition, and charges households the rest. It never pays for itself and is
       not meant to - what it buys is a workforce twenty years from now, which
       is the longest thing on this balance sheet by an order of magnitude.

       Gross and fees separately, for the reason the healthcare comment gives:
       a net figure makes a large service that nearly pays for itself look
       identical to a small one that does not.
       ===================================================================== */

    private double educationBill;
    private double educationFees;

    public void setEducation(double grossCost, double fees) {
        this.educationBill = Math.max(0, grossCost);
        this.educationFees = fees;
    }

    public double getEducationBill() { return educationBill; }
    public double getEducationFees() { return educationFees; }
    public double getEducationNet()  { return educationBill - educationFees; }

    /*
     * What the city paid this month to hold a protected sector at break-even.
     *
     * Held here for the same reason the health and school bills are: Game owns
     * the dial and does the paying, and both strike sites need the figure. It
     * is not in getExpenses() - that figure drives the treasury's cash and the
     * subsidy has ALREADY left it, on the spot, inside paySubsidyIfOwed. This
     * is the reporting side only.
     */
    private double subsidiesPaid;
    public void setSubsidiesPaid(double v) { this.subsidiesPaid = Math.max(0, v); }
    public double getSubsidiesPaid()       { return subsidiesPaid; }

    /* =====================================================================
       PENSIONS

       The city pays every senior, per Jerus. Contributions cover part of it and
       general revenue covers the rest - see SocialSecurity, where the split and
       the reason for it live.

       Fed the senior count rather than deriving it, because the age pyramid
       belongs to Game and this class has no business reaching into it.
       ===================================================================== */

    private double seniors;
    private double totalContributions;

    public void setSeniors(double seniors)   { this.seniors = Math.max(0, seniors); }
    public double getSeniors()               { return seniors; }
    public double getContributions()         { return totalContributions; }
    public double getPensionsPaid() {
        // Struck off TaxPolicy's cheque, not off SocialSecurity's compile-time
        // one: the constant is in FOUNDING dollars and TaxPolicy is the only
        // thing that carries it in today's. See TaxPolicy.pensionPerSenior().
        return Math.max(0, seniors) * taxPolicy.pensionPerSenior();
    }
    /*
     * OFF THE TWO FIGURES THE SCREEN ALREADY PRINTS, and not off
     * SocialSecurity's default overloads. Those hardcode the compile-time
     * contribution rate, replacement rate and unskilled wage - the neighbour
     * above got this right and carries the comment saying why; these two did
     * not. So the pension card's "Covered, X%" contradicted the Collected and
     * Paid lines printed beside it: set contributions to 12% and the shortfall
     * read 34x too high; raise the replacement rate and the shortfall FELL
     * while the real bill nearly doubled; after a 100:1 reform it read 195x
     * high, on the pension card, the advisor's warning and the Policy screen's
     * PROMISES headline. Seventeenth member of the money-constant family.
     */
    public double getPensionShortfall()      {
        return Math.max(0, getPensionsPaid() - totalContributions);
    }
    public double getPensionCoverage()       {
        double owed = getPensionsPaid();
        if (owed <= 0) return 1;
        return totalContributions / owed;
    }

    //getters
    /**
     * What the city pays out this month.
     *
     * Healthcare joined interest and pensions here, and that one line is the
     * whole of the funding fix: getTotalIncome() is getTaxIncome() minus this,
     * and finalUpdateEconomy() moves the cash by it. Before, the health
     * service's 2,128 jobs were paid by nobody at all - counted in the wage
     * bill, taxed, spent by the households, and debited from no account
     * anywhere.
     */
    public double getExpenses(){
        return interest + getPensionsPaid() + healthcareBill + educationBill;
    }

    /**
     * The interest alone, which is the only part of getExpenses() that is CARRIED.
     *
     * The save used to store getExpenses() under the name "city interest
     * accrued" and hand it straight back to setInterest() on load. That was
     * correct while the two were the same number, and it broke the moment
     * pensions joined the expense side: the saved figure came back as interest,
     * getExpenses() added the pension bill to it a second time, and a reloaded
     * city's outgoings doubled. InfrastructureCheck caught it as an income
     * mismatch across a save, which is exactly what that assertion is for.
     *
     * Pensions are re-derived from the restored senior count instead - they are
     * a function of who is alive, not a fact about the month that has to be
     * carried.
     */
    public double getInterestAccrued(){
        return interest;
    }
    public double getTotalIncome(){
        double tempNetIncome = getTaxIncome()-getExpenses();
        return tempNetIncome;
    }

    public int getStoreInventory(){
        return commercialHandler.getStoreInventory();
    }

    public int getIndustryFoodInventory(){
        return industrialHandler.getFoodInventory();
    }
    public NationalAccounts getNationalAccounts(){
        return nationalAccounts;
    }

    /** The government's month, carried whole. See NationalAccounts. */
    double[] governmentMonthToSave()          { return nationalAccounts.governmentToSave(); }
    void restoreGovernmentMonth(double[] m)   { nationalAccounts.restoreGovernment(m); }

    /**
     * Measures the month's output and the government's books.
     *
     * Called once a month after the sector income statements have run, because
     * every figure it needs is one of their results.
     *
     * @param constructionWorkDone value of construction put in place this month
     * @param governmentServices   cost of running the services the city owns
     * @param materialImports      construction materials bought in from outside
     * @param interest             interest paid on city debt
     * @param capitalSpending      what the city itself spent on buildings
     */
    public void updateNationalAccounts(double constructionWorkDone,
                                       double governmentServices,
                                       double materialImports,
                                       double materialsInStock,
                                       double materialPrice,
                                       double workInProgress,
                                       double interest,
                                       double capitalSpending,
                                       double landSales,
                                       double landPurchases,
                                       double propertyTax){

        // Only FINAL sales count. The food a store buys from a mill is
        // intermediate and stays out; the food it sells to households is C.
        double retailSales = commercialHandler.getGrossRevenue();
        double rentPaid = commercialHandler.getReportRentIncome();

        // Stock on both sides of the food chain, in UNITS. The price goes in
        // separately: NationalAccounts measures the change in volume, because
        // measuring the change in value books every price move as production.
        double foodPrice = foodMarket.getLocalPrice();
        double foodUnits =
                industrialHandler.getFoodInventory() + commercialHandler.getStoreInventory();

        double foodImports = commercialHandler.getReportGlobalImports()
                * commercialHandler.getImportPrice();

        // The city's first exports. Heavy industry ships everything it makes
        // abroad and buys its raw material from abroad, so it shows up on both
        // sides of net exports - and only the difference is output the city
        // actually produced.
        /*
         * Only what actually crosses the border.
         *
         * Steel is all exported, so its revenue is an export. Its raw material
         * is NOT all imported any more - ore bought from a local mine is a
         * domestic purchase, and counting it as an import would understate GDP
         * by the whole of the mining sector's local sales. Only the scrap the
         * mills still bring in from abroad counts.
         *
         * Ore the mines could not sell locally goes abroad, so that half of
         * mining's revenue is an export too. Its local half is not: it is an
         * intermediate good, already inside the steel that gets exported, and
         * counting it again would be double-counting the same tonne.
         */
        double exports = heavyIndustryHandler.getReportRevenue()
                + miningHandler.getReportOreExported() * ironMarket.getExportPrice()
                // Surplus food dumped abroad at a discount. It leaves the city
                // and it earns money, which makes it an export - and leaving it
                // out meant the stock it drained was never added back anywhere.
                + industrialHandler.getFoodExportRevenue();

        double rawImports = heavyIndustryHandler.getReportScrapImported()
                * heavyIndustryHandler.getScrapPricePerTonne();

        nationalAccounts.update(retailSales, rentPaid,
                constructionWorkDone,
                foodUnits, industrialHandler.getInventoryWrittenOff(), foodPrice,
                materialsInStock, materialPrice,
                workInProgress,
                governmentServices,
                foodImports, materialImports,
                rawImports, exports);

        nationalAccounts.updateGovernment(
                // The bank's profit tax rides on the business line. It is
                // charged at the RETAIL rate off a commercial building whose
                // property tax and payroll already sit in that sector, so this
                // is where a reader would look for it - and leaving it out was
                // the whole of the treasury bridge's remaining residual: every
                // one of 110 months in a 120-month run was adrift by exactly
                // the bank's tax and by nothing else. See TreasuryCheck.
                totalBusinessTax + totalHeavyIndustryTax + totalConstructionTax + totalBankTax,
                totalIndustrialTax, salesTax, totalWageTax,
                utilityIncome, landSales, propertyTax,
                interest, capitalSpending, landPurchases,
                totalContributions, getPensionsPaid(),
                healthcareFees, healthcareBill,
                educationFees, educationBill,
                subsidiesPaid);

        GDP = nationalAccounts.getGdp();
    }

    /**
     * NOTE: this used to compute one thing and return another - it assigned
     * `GDP = totalWage + two net incomes` and then returned `yearGDP / 12`, a
     * different figure from a different month. It also measured GDP as wages
     * plus profit, so once businesses started paying interest their losses
     * swamped the wage bill and the city's output read as negative while its
     * shops were full. Both are fixed; see NationalAccounts.
     */
    public double getLastFoodUnits(){ return nationalAccounts.getLastFoodUnits(); }

    /**
     * Puts the month's accounts back.
     *
     * Accepts both shapes. A save from before materials were counted as
     * inventory carries eleven entries and no unit baseline, so it restores with
     * the baseline marked UNKNOWN - the first month afterwards sets it and books
     * no inventory change, which costs one month's accuracy instead of booking
     * an entire warehouse as that month's production.
     */
    public void restoreNationalAccounts(double[] a){
        if (a == null || a.length < 11) return;

        boolean hasUnits = a.length >= 13;
        boolean hasWip = a.length >= 14;
        nationalAccounts.restore(a[0], hasUnits ? a[11] : 0,
                a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                hasUnits ? a[12] : 0, hasWip ? a[13] : 0, hasUnits);
    }

    /** The month's accounts, flattened for the save. Order matches restore(). */
    public double[] getNationalAccountsState(){
        return new double[] {
            nationalAccounts.getGdp(),
            // Slot 1 was the inventory VALUE. It is kept only so an older build
            // reading this save finds something plausible there; this build
            // reads the unit counts on the end instead.
            nationalAccounts.getLastFoodUnits(),
            nationalAccounts.getConsumptionGoods(),
            nationalAccounts.getConsumptionHousing(),
            nationalAccounts.getInvestmentConstruction(),
            nationalAccounts.getInvestmentInventories(),
            nationalAccounts.getGovernment(),
            nationalAccounts.getImportsFood(),
            nationalAccounts.getImportsMaterials(),
            nationalAccounts.getImportsRawMaterial(),
            nationalAccounts.getExports(),
            // Appended, per the rule that order is the format: the inventory
            // baseline in units, which is what the volume measure needs.
            nationalAccounts.getLastFoodUnits(),
            nationalAccounts.getLastMaterialUnits(),
            nationalAccounts.getLastWorkInProgress()
        };
    }

    public double getMonthGdp(){
        return nationalAccounts.getGdp();
    }

    public double getYearGdp(){
        return yearGDP;
    }
    public double getCommercialCash(){
        return commercialHandler.getCommercialCash();
    }
    public double getRealEstateCash(){
        return commercialHandler.getRealEstateCash();
    }
    public double getIndustrialCash(){
        return industrialHandler.getIndustrialCash();
    }

    /**
     * Read-only access for the JavaFX sector screens. The handler's report
     * getters are all pure - nothing on the UI path mutates economy state.
     */
    public CommercialHandler getCommercialHandler(){
        return commercialHandler;
    }

    /* -----------------------------------------------------------------------
       READ-ONLY ACCESSORS for the city overview panel.

       All of these return already-computed fields and mutate nothing. Note in
       particular that the panel must NOT call getMonthGdp() - that method
       recalculates and reassigns the GDP field as a side effect, which is
       exactly the pattern that made printCommercialInfo() unsafe to call from a
       screen. getGDP() below is the pure read.
       ----------------------------------------------------------------------- */
    public double getGDP(){ return GDP; }
    public double getTaxRate(){ return taxPolicy.getIncomeTaxRate(); }

    public TaxPolicy getTaxPolicy(){ return taxPolicy; }

    /* ===================== THE PRICE OF FOREIGN THINGS =====================
     *
     * Every price below is quoted by the world in ITS money, not the city's.
     * Steel sells abroad for so many dollars a tonne whatever the city's
     * currency is doing; scrap and shop stock are bought abroad the same way.
     * So each of them enters the game as a USD price converted at the rate, and
     * a weaker currency makes imports dearer and exports better paid in local
     * money - which is the entire mechanism by which an exchange rate does
     * anything at all.
     *
     * Held here rather than reached for through Game, because these conversions
     * happen deep inside the monthly update where the world is not visible.
     * Pushed in by Game.startOfMonthUpdate() before anything is priced.
     */
    private double exchangeRate = 1.0;

    public void setExchangeRate(double rate) {
        this.exchangeRate = rate > 0 ? rate : 1.0;
        foodMarket.setExchangeRate(this.exchangeRate);
        buildingManager.setExchangeRate(this.exchangeRate);
    }

    public double getExchangeRate() { return exchangeRate; }

    public void setLandPricePerSqFt(double price){ this.landPricePerSqFt = price; }

    public double getTotalPropertyTax(){ return totalPropertyTax; }

    /*
     * Both of these are CHARGED during a month rather than derived from the
     * city's state, which is why getTaxIncome() and getExpenses() read them back
     * instead of recomputing them - the sectors have already borne these exact
     * figures in their income statements, and recomputing risks the city
     * collecting a different number from the one the businesses paid.
     *
     * That makes them state, and state has to survive a save. Without these
     * setters a freshly loaded city showed its next-month income short by the
     * whole property-tax line and long by the whole interest bill, then silently
     * corrected itself the first time a month was simulated.
     */
    public void setTotalPropertyTax(double value){ this.totalPropertyTax = value; }
    public void setInterest(double value){ this.interest = value; }

    /* =======================================================================
       PROPERTY TAX

       Charged on what a sector OWNS, not on what it earned - which is the whole
       point of having it alongside an income tax, and the reason it is the one
       levy that can push a business under while it is doing nothing wrong.

       Assessed value is land at today's sale price plus FINISHED buildings at
       replacement cost. Both are current-value rather than historical, matching
       getBookValueByCategory(), which already marks buildings to the current
       materials price. Nothing stores what a plot originally cost, and a real
       assessor would not care if it did. Construction in progress is on the
       balance sheet (since 2026-09-10) and is deliberately NOT assessed: an
       assessor taxes a building when it is a building, and so does this one.

       Municipal buildings - the power station, the water plant - are exempt.
       They are the city's own, and taxing them would move money from one pocket
       to the other while making the utility's books look worse for no reason.
       ======================================================================= */

    /** Land plus buildings, at current value, for one building category. */
    public double getAssessedValue(BuildingType category) {
        /*
         * Asked of landValueOf(), not re-typed from it. The comment above states
         * the invariant in prose - "the same number the property tax is assessed
         * on, deliberately, because a business should be taxed on the value its
         * own balance sheet claims" - and an invariant asserted in prose and
         * enforced by two identical expressions is one zoning multiplier away
         * from being false.
         */
        return landValueOf(category)
                + buildingManager.getBookValueByCategory(category);
    }

    /** One month's property tax on a category. */
    /**
     * One month's property tax on a category, at that sector's own rate.
     *
     * A city-owned category (roads, utilities) maps to no PolicySector, and
     * propertyTaxOn falls back to the city rate for it - which charges nothing
     * in practice, because the city does not assess itself.
     */
    public double getPropertyTaxFor(BuildingType category){
        return taxPolicy.propertyTaxOn(getAssessedValue(category),
                PolicySector.byCategory(category));
    }

    /* =====================================================================
       EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE (2026-09-09).

       Jerus: "all buildings need maintenance, and make sure they get billed."

       WHAT WAS ACTUALLY WRONG. `upkeep` has been a field on every template
       since the beginning and was charged on exactly two categories -
       healthcare and education - which BuildingManager's own note describes
       accurately: "getUpkeep() had two callers in the entire codebase before
       this, a debug println and BuildingDataCheck, so every building's upkeep
       in this file was a wish." Residential got a real repair flow on
       2026-09-09. That left EIGHT of eleven categories paying nothing at all
       to stand, which is the same bug as an empty home costing its owner
       nothing - the one the housing pass had just finished fixing.

       SHAPED LIKE THE PROPERTY TAX, deliberately. A per-category charge handed
       to whoever owns the category is a path this codebase already trusts and
       already audits; inventing a second shape for the same idea is how two
       breakdowns end up disagreeing. Same rule as the tax, too: NO MONEY MOVES
       HERE. The figure is assigned, each income statement subtracts it, and
       what a sector banks is already net of it.

       AND IT IS A REAL ORDER, not a fee. A repair costs money, MATERIALS and
       CONSTRUCTION POINTS, and all three are placed with the construction
       sector - which is what makes a city's own building stock compete with its
       new building for the same builders. Game.chargeBuildingMaintenance() owns
       that half; this owns who pays.

       WHY THE CITY BEARS FIVE OF THEM. Roads, schools, hospitals, the power
       plant and the water plant have no company behind them in this model -
       the utilities net straight to the municipal books and the rest are
       services - so the treasury is their owner and the treasury is billed.
       That is not a simplification of the accounting; it IS the accounting.

       UPKEEP IS NOT REPLACED. Healthcare and education keep paying it, and
       they now pay this as well, because they are different things: upkeep is
       running the service - supplies, cleaning, the things a hospital consumes
       - and this is keeping the building standing. A real hospital pays both.
       ===================================================================== */

    /** The categories whose repairs the treasury pays, because nobody else owns them. */
    private static final BuildingType[] CITY_MAINTAINED = {
        BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE,
        BuildingType.HEALTHCARE,  BuildingType.EDUCATION
    };

    private double maintenanceBillTotal;
    private double maintenanceMaterialsTotal;
    private double maintenancePointsTotal;
    private double cityMaintenanceBill;
    private double[] maintenanceCharges = new double[BuildingType.values().length];

    /** The month's repair bill for one category, in money - materials priced in. */
    public double maintenanceBillFor(BuildingType category, double materialPrice) {
        double rate = CommercialHandler.MAINTENANCE_PER_YEAR / 12;
        double cash = buildingManager.getTotalByCategoryDouble(
                category, BuildingsTemplate::getCashCost) * rate;
        double mats = buildingManager.getTotalByCategoryDouble(
                category, BuildingsTemplate::getConstructionMaterials) * rate;
        double bill = cash + mats * Math.max(0, materialPrice);
        return Double.isFinite(bill) && bill > 0 ? bill : 0;
    }

    /** The material UNITS one category's repairs consume this month. */
    public double maintenanceMaterialsFor(BuildingType category) {
        double mats = buildingManager.getTotalByCategoryDouble(
                category, BuildingsTemplate::getConstructionMaterials)
                * CommercialHandler.MAINTENANCE_PER_YEAR / 12;
        return Double.isFinite(mats) && mats > 0 ? mats : 0;
    }

    /** The builders' time one category's repairs consume this month. */
    public double maintenancePointsFor(BuildingType category) {
        double pts = buildingManager.getTotalByCategoryDouble(
                category, BuildingsTemplate::getConstructionPoints)
                * CommercialHandler.MAINTENANCE_PER_YEAR / 12;
        return Double.isFinite(pts) && pts > 0 ? pts : 0;
    }

    /**
     * Hands every sector its repair bill for the month.
     *
     * @param materialPrice what a unit of construction material costs today
     */
    public void chargeMaintenance(double materialPrice) {

        java.util.Arrays.fill(maintenanceCharges, 0);
        maintenanceBillTotal = 0;
        maintenanceMaterialsTotal = 0;
        maintenancePointsTotal = 0;
        cityMaintenanceBill = 0;

        for (BuildingType category : BuildingType.values()) {
            double bill = maintenanceBillFor(category, materialPrice);
            maintenanceCharges[category.ordinal()] = bill;
            maintenanceBillTotal += bill;
            maintenanceMaterialsTotal += maintenanceMaterialsFor(category);
            maintenancePointsTotal += maintenancePointsFor(category);
        }

        commercialHandler.setPropertyMaintenance(charge(BuildingType.RESIDENTIAL));
        commercialHandler.setRetailMaintenance(charge(BuildingType.COMMERCIAL));
        industrialHandler.setMaintenanceExpense(charge(BuildingType.INDUSTRIAL));
        heavyIndustryHandler.setMaintenanceExpense(charge(BuildingType.HEAVY_INDUSTRY));
        miningHandler.setMaintenanceExpense(charge(BuildingType.MINING));
        if (constructionHandler != null) {
            constructionHandler.setMaintenanceExpense(charge(BuildingType.CONSTRUCTION));
        }

        for (BuildingType category : CITY_MAINTAINED) {
            cityMaintenanceBill += charge(category);
        }
    }

    private double charge(BuildingType category) {
        return maintenanceCharges[category.ordinal()];
    }

    /** Every category's repair bill added up - what the builders are owed. */
    public double getMaintenanceBillTotal()      { return maintenanceBillTotal; }

    /** The share of it the treasury pays, because the city owns those buildings. */
    public double getCityMaintenanceBill()       { return cityMaintenanceBill; }

    /** Material UNITS, not money - the yard has to find these. */
    public double getMaintenanceMaterialsTotal() { return maintenanceMaterialsTotal; }

    /** Builders' time, which comes straight off what the city can put up. */
    public double getMaintenancePointsTotal()    { return maintenancePointsTotal; }

    /** One category's bill, for the screens. */
    public double getMaintenanceCharge(BuildingType category) {
        return category == null ? 0 : maintenanceCharges[category.ordinal()];
    }

    /**
     * Hands each sector its property tax bill for the month.
     *
     * Runs with the interest bill, before the income statements, for the same
     * reason: what a sector banks has to already be net of what it owes.
     */
    public void chargePropertyTax(){

        double commercial   = getPropertyTaxFor(BuildingType.COMMERCIAL);
        double residential  = getPropertyTaxFor(BuildingType.RESIDENTIAL);
        double industrial   = getPropertyTaxFor(BuildingType.INDUSTRIAL);
        double construction = getPropertyTaxFor(BuildingType.CONSTRUCTION);
        double heavy        = getPropertyTaxFor(BuildingType.HEAVY_INDUSTRY);
        double mining       = getPropertyTaxFor(BuildingType.MINING);

        commercialHandler.setRetailPropertyTax(commercial);
        commercialHandler.setRealEstatePropertyTax(residential);
        industrialHandler.setPropertyTaxExpense(industrial);

        if (constructionHandler != null) {
            constructionHandler.setPropertyTaxExpense(construction);
        }

        heavyIndustryHandler.setPropertyTaxExpense(heavy);
        miningHandler.setPropertyTaxExpense(mining);

        totalPropertyTax = commercial + residential + industrial + heavy + mining
                + (constructionHandler != null ? construction : 0);

        propertyTaxCharges = new double[BuildingType.values().length];
        propertyTaxCharges[BuildingType.COMMERCIAL.ordinal()]     = commercial;
        propertyTaxCharges[BuildingType.RESIDENTIAL.ordinal()]    = residential;
        propertyTaxCharges[BuildingType.INDUSTRIAL.ordinal()]     = industrial;
        propertyTaxCharges[BuildingType.HEAVY_INDUSTRY.ordinal()] = heavy;
        propertyTaxCharges[BuildingType.MINING.ordinal()] = mining;
        propertyTaxCharges[BuildingType.CONSTRUCTION.ordinal()]   =
                (constructionHandler != null) ? construction : 0;
    }

    /**
     * What each sector was actually charged this month, by category ordinal.
     *
     * Kept because it CANNOT be recomputed later, which is not obvious and cost
     * a wrong fix before a test caught it: property tax is charged early in the
     * month, and buildings finish construction after that. By the time a save is
     * taken, the assessed value has moved on - recomputing from the saved
     * building stock produced a retail charge of 4.35 against the 2.95 that was
     * actually billed. The charge is a historical fact about a month, not a
     * function of the state the month ended in.
     */
    /* ------------- the month's trading, for the save -------------
     *
     * These are flows, not balances. Nothing can rederive them from the state a
     * month ended in, which is exactly why they have to be carried.
     */
    public double getRetailCostOfGoods()  { return commercialHandler.getStoreInventoryCost(); }
    public double getRetailLocalPurchase()  { return commercialHandler.getLocalPurchaseValue(); }
    public double getRetailImportPurchase() { return commercialHandler.getImportPurchaseValue(); }
    public void restoreRetailPurchases(double local, double imported) {
        commercialHandler.setPurchaseValues(local, imported);
    }
    public double getRetailFillBasis()    { return commercialHandler.getReportAverageStoreFill(); }
    public double getRetailImportTax()    { return commercialHandler.getImportTax(); }
    public int getRetailLocalImports()    { return commercialHandler.getReportLocalImports(); }
    public int getRetailGlobalImports()   { return commercialHandler.getReportGlobalImports(); }
    public double getIndustryDemand()     { return industrialHandler.getFoodDemand(); }
    public double getIndustryLocalSalesValue() { return industrialHandler.getLocalSalesValue(); }
    public int getIndustryUnitsSold()     { return industrialHandler.getProductsSoldCopy(); }
    public int getIndustryUnitsImported() { return industrialHandler.getProductsImportedCopy(); }

    public void restoreMonthFlows(double retailCostOfGoods, int retailLocal, int retailGlobal,
                                  double retailFillBasis, double retailImportTax,
                                  double industryDemand, double industryLocalSalesValue,
                                  int industrySold, int industryImported,
                                  double energyBasis, double waterBasis, double roadBasis,
                                  double healthBasis) {
        industrialHandler.setLocalSalesValue(industryLocalSalesValue);
        commercialHandler.setStoreInventoryCost(retailCostOfGoods);
        commercialHandler.setReportImports(retailLocal, retailGlobal);
        commercialHandler.restoreMonthReport(retailFillBasis, retailImportTax,
                energyBasis, waterBasis, roadBasis, healthBasis);
        industrialHandler.restoreMonthReport(industryDemand, industrySold, industryImported,
                energyBasis, waterBasis, roadBasis, healthBasis);
        heavyIndustryHandler.computeMonthlyReport(energyBasis, waterBasis, roadBasis, healthBasis);
        miningHandler.computeMonthlyReport(energyBasis, waterBasis, roadBasis, healthBasis);
    }

    /* ---------------------- the month's ratio basis ----------------------
     *
     * The utilisation the statements were actually written against. All three
     * sectors are handed the same figures at the same moment, so one triple
     * describes the whole month and the commercial handler's snapshot is as
     * good a place to read it from as any.
     */
    /* ------------------- the month's statements, carried -------------------
     *
     * The three sectors' income statements, saved whole. See
     * CommercialHandler.getReportState() for why this is carried rather than
     * rebuilt, and note that the ratio basis above is now a belt-and-braces
     * measure: it keeps a RECOMPUTED statement honest for a save whose report
     * arrays are the wrong shape, which is the only path left that recomputes.
     */
    public double[] getCommercialReportState()    { return commercialHandler.getReportState(); }
    public double[] getIndustrialReportState()    { return industrialHandler.getReportState(); }
    public double[] getHeavyIndustryReportState() { return heavyIndustryHandler.getReportState(); }
    public double[] getMiningReportState()        { return miningHandler.getReportState(); }

    /**
     * Puts the month's statements back.
     *
     * All three or none: a city showing a saved retail statement beside a
     * recomputed industrial one would be a mix of two different months, and
     * harder to reason about than either alone.
     *
     * @return false if any array is the wrong shape, in which case NOTHING was
     *         applied and the statements the load already recomputed stand
     */
    public boolean restoreReportState(double[] commercial, double[] industrial,
                                      double[] heavy, double[] mining) {

        if (commercial == null || industrial == null || heavy == null) return false;

        // Mining arrived after the other three, so a save from before it simply
        // has none - the sector is empty in those cities anyway, and refusing
        // the whole restore over it would throw away three good statements.
        if (mining != null
                && mining.length == miningHandler.getReportState().length) {
            miningHandler.restoreReportState(mining);
        }

        // Checked before anything is written, so a mismatched save cannot leave
        // two sectors restored and one rebuilt.
        if (commercial.length != commercialHandler.getReportState().length
                || industrial.length != industrialHandler.getReportState().length
                || heavy.length != heavyIndustryHandler.getReportState().length) {
            return false;
        }

        return commercialHandler.restoreReportState(commercial)
                & industrialHandler.restoreReportState(industrial)
                & heavyIndustryHandler.restoreReportState(heavy);
    }

    /* ------------------- the ore market's month ------------------- */

    public double getIronLocalPrice() { return ironMarket.getLocalPrice(); }
    public double getMiningCash()     { return miningHandler.getCash(); }

    public void restoreIronMarket(double localPrice) {
        if (localPrice > 0) ironMarket.setLocalPrice(localPrice);
    }

    public double getFoodLocalPrice() { return foodMarket.getLocalPrice(); }

    /** The food price back, and told to both sides that trade on it. See FoodMarket.setLocalPrice(). */
    public void restoreFoodMarket(double localPrice) {
        if (localPrice <= 0) return;
        foodMarket.setLocalPrice(localPrice);
        industrialHandler.setFoodPrice(localPrice);
        industrialHandler.setImportPrice(foodMarket.getImportPrice());
        commercialHandler.setFoodPrice(localPrice);
    }

    public void setMiningCash(double cash) { miningHandler.setCash(cash); }

    public double getEnergyRatioBasis() { return commercialHandler.getReportEnergyRatio(); }
    public double getWaterRatioBasis()  { return commercialHandler.getReportWaterRatio(); }
    public double getRoadRatioBasis()   { return commercialHandler.getReportRoadRatio(); }
    public double getHealthRatioBasis() { return commercialHandler.getReportHealthRatio(); }

    /** What the sectors are currently running at, for the harnesses. */
    public double getHealthRatio()      { return commercialHandler.getHealthRatio(); }

    public double[] getPropertyTaxCharges() {
        return (propertyTaxCharges == null)
                ? new double[BuildingType.values().length]
                : propertyTaxCharges.clone();
    }

    /**
     * Puts the month's charges back on load. Assigns expense figures only - no
     * money moves, because the sectors bore this when their statements ran and
     * their restored cash balances are already net of it.
     */
    public void restorePropertyTaxCharges(double[] charges) {

        double[] c = widen(charges);
        if (c == null) return;

        double commercial   = c[BuildingType.COMMERCIAL.ordinal()];
        double residential  = c[BuildingType.RESIDENTIAL.ordinal()];
        double industrial   = c[BuildingType.INDUSTRIAL.ordinal()];
        double heavy        = c[BuildingType.HEAVY_INDUSTRY.ordinal()];
        double mining       = c[BuildingType.MINING.ordinal()];
        double construction = c[BuildingType.CONSTRUCTION.ordinal()];

        commercialHandler.setRetailPropertyTax(commercial);
        commercialHandler.setRealEstatePropertyTax(residential);
        industrialHandler.setPropertyTaxExpense(industrial);
        heavyIndustryHandler.setPropertyTaxExpense(heavy);
        miningHandler.setPropertyTaxExpense(mining);

        if (constructionHandler != null) {
            constructionHandler.setPropertyTaxExpense(construction);
        }

        propertyTaxCharges = c;
        totalPropertyTax = commercial + residential + industrial + heavy + mining
                + (constructionHandler != null ? construction : 0);
    }
    public double getBusinessTax(){ return totalBusinessTax; }
    public double getIndustrialTax(){ return totalIndustrialTax; }
    public double getSalesTax(){ return salesTax; }
    public double getHeavyIndustryTax(){ return totalHeavyIndustryTax; }
    public double getConstructionTax()  { return totalConstructionTax; }

    /**
     * The banded wage tax on one sector's payroll, for a screen that wants to
     * say what a building's wages are worth to the treasury. Asked of the
     * policy rather than multiplied out in the UI, because the wage tax is
     * banded and "payroll x the income rate" was wrong for every band but one.
     */
    public double wageTaxOnPayroll(double[] payrollPerType) {
        return taxPolicy.wageTaxOn(payrollPerType, null);
    }

    /** The month's power bills, as the four charged sectors' statements booked them. */
    public double getSectorElectricityCharges() {
        return commercialHandler.getReportElectricityCost()
                + industrialHandler.getReportElectricityCost()
                + heavyIndustryHandler.getReportElectricityCost()
                + miningHandler.getReportElectricityCost();
    }

    /** The month's water bills, likewise. */
    public double getSectorWaterCharges() {
        return commercialHandler.getReportWaterCost()
                + industrialHandler.getReportWaterCost()
                + heavyIndustryHandler.getReportWaterCost()
                + miningHandler.getReportWaterCost();
    }
    public double getWageTax(){ return totalWageTax; }
    public double getUtilityIncome(){ return utilityIncome; }



    //setters
    public void setTotalJobs(int jobs) {
        totalJobs = jobs;
    }
    public void setPopulation(int pop){
        population = pop;
    }
    public void setHouseholds(int houseCap){
        households = houseCap;
    }
    public void setCash(int money){
        cash = money;
    }
    public void updateInterestExpense(double interest){
        this.interest += interest;
    }

    /**
     * The wage bill per job type, and how much of each is actually staffed.
     *
     * Kept because the wage tax is banded now: a single total cannot be taxed at
     * four different rates, and rebuilding the split from the population on
     * demand would make the tax depend on the state the month ENDED in rather
     * than on the wages actually paid during it.
     */
    private double[] staffedWagePerType = new double[JobType.values().length];

    /** Already staffed - it sums to totalWage by construction. */
    public void setWageDetail(double[] staffedPerType){
        if (staffedPerType != null) this.staffedWagePerType = staffedPerType.clone();
    }

    public double[] getStaffedWagePerType() { return staffedWagePerType.clone(); }

    public void setTotalWage(double totalWage){
        this.totalWage = totalWage;
    }

    public void setStoreInventory(int storeInventory){
        commercialHandler.setStoreInventory(storeInventory);
    }
    public void setIndustryFoodInventory(int foodInventory){
        industrialHandler.setFoodInventory(foodInventory);
    }
    public void setWaterRatio(double ratio){
        commercialHandler.setWaterRatio(ratio);
        industrialHandler.setWaterRatio(ratio);
        miningHandler.setWaterRatio(ratio);
    }

    public void setEnergyRatio(double ratio){
        commercialHandler.setEnergyRatio(ratio);
        industrialHandler.setEnergyRatio(ratio);
        heavyIndustryHandler.setEnergyRatio(ratio);
        miningHandler.setEnergyRatio(ratio);
    }

    /** The road network's throughput, handed to everyone whose goods move on it. */
    public void setRoadRatio(double ratio){
        commercialHandler.setRoadRatio(ratio);
        industrialHandler.setRoadRatio(ratio);
        heavyIndustryHandler.setRoadRatio(ratio);
        miningHandler.setRoadRatio(ratio);
    }

    /**
     * What is left of the workforce once this month's illness is taken off.
     *
     * Deliberately shaped exactly like setEnergyRatio - same fan-out, same four
     * handlers, same multiplier on the far end - because sickness IS a
     * utilisation ratio and pretending otherwise would have meant a second
     * mechanism doing the same job. The one difference lives inside the
     * handlers: this one never touches payroll. See Health.
     */
    public void setHealthRatio(double ratio){
        commercialHandler.setHealthRatio(ratio);
        industrialHandler.setHealthRatio(ratio);
        heavyIndustryHandler.setHealthRatio(ratio);
        miningHandler.setHealthRatio(ratio);
    }
    /* The utility prices, kept so the investment engine can cost a building it
     * has not built yet. Every handler is told them; nothing remembered them. */
    private double pricePerWatt;
    private double pricePerWaterUnit;

    public double getPricePerWatt()      { return pricePerWatt; }
    public double getPricePerWaterUnit() { return pricePerWaterUnit; }

    public void setPricePerWaterUnit(double price){
        this.pricePerWaterUnit = price;
        commercialHandler.setPricePerWaterUnit(price);
        industrialHandler.setPricePerWaterUnit(price);
        heavyIndustryHandler.setPricePerWaterUnit(price);
        miningHandler.setPricePerWaterUnit(price);
    }

    public void setPricePerWatt(double price){
        this.pricePerWatt = price;
        commercialHandler.setPricePerWatt(price);
        industrialHandler.setPricePerWatt(price);
        heavyIndustryHandler.setPricePerWatt(price);
        miningHandler.setPricePerWatt(price);
    }
    public void setElectricityConsumption(){
        commercialHandler.setElectricityConsumption(buildingManager.getTotalByCategoryInteger(BuildingType.COMMERCIAL, BuildingsTemplate::getElectricityConsumption));
        industrialHandler.setElectricityConsumption(buildingManager.getTotalByCategoryInteger(BuildingType.INDUSTRIAL, BuildingsTemplate::getElectricityConsumption));

        // Water is billed to the sectors that draw it, exactly as power is.
        // Without this the utility would collect water revenue that nobody paid,
        // which is money created out of nothing.
        commercialHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(BuildingType.COMMERCIAL, BuildingsTemplate::getWaterConsumption));
        industrialHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(BuildingType.INDUSTRIAL, BuildingsTemplate::getWaterConsumption));

        heavyIndustryHandler.setElectricityConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getElectricityConsumption));
        heavyIndustryHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getWaterConsumption));

        miningHandler.setElectricityConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getElectricityConsumption));
        miningHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getWaterConsumption));
    }
    /**
     * Repopulates the government's revenue and expenditure block after a load.
     *
     * updateGovernment() is called from exactly one place - inside
     * updateNationalAccounts(), on the monthly path - so a reloaded city showed
     * the entire City Finances screen as zeros: every tax line, the pension
     * lines, both pie charts, and a SURPLUS/DEFICIT of nothing.
     *
     * Deliberately NOT the whole of updateNationalAccounts(). That measures GDP,
     * moves the inventory baseline and books a month of output; running it on
     * the load path would be running a month of the economy with the calendar
     * standing still, which is the exact trap refreshEconPrices() exists to
     * avoid. This touches the government block and nothing else.
     */
    /**
     * @param interestPaid this period's city interest, PASSED IN rather than
     *        read off the field.
     *
     *        FOUND BY JERUS, 2026-09-07: "the pie chart works but it doesn't
     *        show the interest portion." It never could. On the live path this
     *        method runs a few lines after finalEconUpdate(), which zeroes the
     *        field - so the government block was re-struck every month with an
     *        interest expense of zero, and budgetPie() drops a zero slice.
     *
     *        Note which way round the failure went: the LOAD path was the
     *        correct one, because rebuildSimulationState() restores the accrual
     *        and never calls finalEconUpdate(). A reloaded city showed its
     *        interest and a played one did not. The usual load-path-parity bug
     *        wearing its coat inside out, and the fix is the same either way -
     *        a flow gets passed to whoever needs it, not read back off a field
     *        somebody else is entitled to clear.
     */
    public void refreshGovernmentAccounts(double landSales, double capitalSpending,
                                          double landPurchases, double interestPaid) {
        getTaxIncome();   // assigns the tax fields and the contributions
        nationalAccounts.updateGovernment(
                // ...and the bank's, on the same line - see updateNationalAccounts().
                totalBusinessTax + totalHeavyIndustryTax + totalConstructionTax + totalBankTax,
                totalIndustrialTax, salesTax, totalWageTax,
                utilityIncome, landSales, getTotalPropertyTax(),
                interestPaid, capitalSpending, landPurchases,
                totalContributions, getPensionsPaid(),
                healthcareFees, healthcareBill,
                educationFees, educationBill,
                subsidiesPaid);
    }

    public void setUtilityIncome(double income){
        this.utilityIncome = income;
    }
    public void setDebt(double debt){
        this.debt = debt;
    }

    public void setPreviousGdp(HistorySave historySave) {
        // Get the full GDP history
        List<Double> gdp = new ArrayList<>(historySave.getGdp());

        // Keep only the last 11 entries
        int start = Math.max(0, gdp.size() - 11);
        List<Double> last11 = gdp.subList(start, gdp.size());

        yearGDP = nationalAccounts.getAnnualGdp();

        // NOTE: the old version summed the last 11 history entries plus the
        // current GDP field. NationalAccounts keeps its own history and sums the
        // last twelve of it, so the annual figure can no longer drift from the
        // monthly one that feeds it.
        if (yearGDP == 0) {
            yearGDP = GDP;
            for (int i = 0; i < last11.size(); i++) {
                yearGDP += last11.get(i);
            }
        }

    }

    public void setCommercialCash(double cash) {
        commercialHandler.setCommercialCash(cash);
    }

    public void setRealEstateCash(double cash) {
        commercialHandler.setRealEstateCash(cash);
    }

    public void setIndustrialCash(double cash) {
        industrialHandler.setIndustrialCash(cash);
    }




    public void updateFoodProduction(){
        industrialHandler.setBaseFoodProduction(buildingManager.getFoodProduction());
    }
    public void updateStoreWages(double[] wages, int[] jobs) {
        updateStoreWages(wages, jobs, null);
    }

    /** The wage RATE per job type, as the commercial sector last saw it. */
    public double[] getStoreWageRates() { return storeWages; }

    /** @param bankJobs the commercial jobs that belong to the bank - see CommercialHandler. */
    public void updateStoreWages(double[] wages, int[] jobs, int[] bankJobs) {

        System.arraycopy(wages, 0, this.storeWages, 0, wages.length);
        System.arraycopy(jobs, 0, this.storeJobs, 0, wages.length);
        this.bankJobs = bankJobs;
    }

    private int[] bankJobs;
    public void updateIndustrialWages(double[] wages) {

        for (int i = 0; i < wages.length; i++) {
            this.industrialWages[i] = wages[i];

        }
        System.arraycopy(buildingManager.getJobArrayPerCategory(BuildingType.INDUSTRIAL),
                0, this.industrialJobs, 0, buildingManager.getJobArrayPerCategory(BuildingType.INDUSTRIAL).length);
    }
    public void updateJobFillRate(double[]fillRate){

        for(int i = 0; i < fillRate.length; i++){
            this.fillRate[i] = fillRate[i];

        }
    }



    public void printCommercialInfo(){
        commercialHandler.printCommercialInfo();
    }
    public void printIndustrialInfo(){
        industrialHandler.printIndustrialInfo();
    }
    public void printWageTaxInfo(){
        System.out.printf("\nTOTAL WAGE TAX INCOME: $%s%n", formatter.format(totalWageTax));
    }

    public void printCityStats() {

        System.out.println("\n====================== CITY FINANCIAL REPORT ======================");

        /* -------------------------------------------------------------------
       GOVERNMENT BUDGET SUMMARY
       ------------------------------------------------------------------- */
        double totalRev = 0;

        System.out.println("\n--------------------------- REVENUES ---------------------------");

        System.out.printf("Commercial Tax Revenue:      $%s Thousand%n", formatter.format(totalBusinessTax));
        System.out.printf("Industrial Tax Revenue:      $%s Thousand%n", formatter.format(totalIndustrialTax));
        System.out.printf("Sales Tax Revenue:           $%s Thousand%n", formatter.format(salesTax));
        System.out.printf("Wage Tax Revenue:            $%s Thousand%n", formatter.format(totalWageTax));

        totalRev += totalBusinessTax + totalIndustrialTax + salesTax + totalWageTax;

        if (utilityIncome >= 0) {
            System.out.printf("Municipal Services Profit:    $%s Thousand%n", formatter.format(utilityIncome));
            totalRev += utilityIncome;
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf("TOTAL GOVERNMENT REVENUE:    $%s Thousand%n", formatter.format(totalRev));

        /* -------------------------------------------------------------------
       EXPENSES
       ------------------------------------------------------------------- */
        double totalExp = 0;

        System.out.println("\n--------------------------- EXPENSES ---------------------------");

        if (utilityIncome < 0) {
            System.out.printf("Municipal Services Loss:      $%s Thousand%n", formatter.format(utilityIncome));
            totalExp -= utilityIncome;
        }

        System.out.printf("Debt Interest Payments:      $%s Thousand%n", formatter.format(interest));
        totalExp += interest;

        System.out.println("---------------------------------------------------------------");
        System.out.printf("TOTAL GOVERNMENT EXPENSES:   $%s Thousand%n", formatter.format(totalExp));

        /* -------------------------------------------------------------------
       NET BUDGET POSITION
       ------------------------------------------------------------------- */
        double netIncome = totalRev - totalExp;

        System.out.println("\n----------------------- BUDGET BALANCE ------------------------");
        System.out.printf("Monthly Budget Balance:      $%s Thousand%n", formatter.format(netIncome));

        /* -------------------------------------------------------------------
       ECONOMIC INDICATORS
       ------------------------------------------------------------------- */
        System.out.println("\n--------------------- ECONOMIC INDICATORS ---------------------");

        System.out.printf("Monthly GDP:                 $%s Thousand%n", formatter.format(GDP/12));
        System.out.printf("Annualized GDP:              $%s Thousand%n", formatter.format(yearGDP));

        if (population > 0) {
            System.out.printf("GDP per Capita:              $%s%n",
                    formatter.format(((yearGDP) / population) * 1000));
        }

        System.out.printf("Debt-to-GDP Ratio:           %.2f%%%n",
                (debt / (yearGDP)) * 100);

        System.out.println("================================================================\n");
    }

    public void resetEconomyManager() {
        cash = 0;

        totalJobs = 0;
        population = 0;

        totalBusinessTax = 0;
        totalWageTax = 0;
        households = 0;
        totalIncome = 0;
        totalTaxIncome = 0;
        interest = 0;
        averageStoreFill = 0;
        totalWage = 0;
        //tax reset
        totalIndustrialTax = 0;

        totalHeavyIndustryTax = 0;
        totalConstructionTax = 0;
        totalPropertyTax = 0;

        commercialHandler.resetCommercialHandler();
        industrialHandler.resetIndustrialHandler();
        heavyIndustryHandler.reset();
        miningHandler.reset();
        ironMarket.reset();
        foodMarket.resetFoodMarket();
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }







    /**
     * Every figure the economy manager holds, and every handler under it, in
     * the new unit.
     *
     * The fill rates, the utility ratios and the wage RATES per job are the
     * handlers' own business; what is here is the tax take, the charges, the
     * wage bills and the land price, plus the call down into each sector.
     */
    public void redenominate(double scale) {
        cash *= scale;
        upkeep *= scale;
        landPricePerSqFt *= scale;
        totalPropertyTax *= scale;
        totalHeavyIndustryTax *= scale;
        totalConstructionTax *= scale;
        totalBusinessTax *= scale;
        totalBankTax *= scale;
        totalWageTax *= scale;
        totalIndustrialTax *= scale;
        totalIncome *= scale;
        totalTaxIncome *= scale;
        interest *= scale;
        totalWage *= scale;
        GDP *= scale;
        yearGDP *= scale;
        salesTax *= scale;
        utilityIncome *= scale;
        debt *= scale;
        marginalHousingCost *= scale;
        healthcareBill *= scale;   healthcareFees *= scale;
        educationBill  *= scale;   educationFees  *= scale;
        subsidiesPaid  *= scale;
        totalContributions *= scale;
        exchangeRate *= scale;
        pricePerWatt *= scale;
        pricePerWaterUnit *= scale;

        scaleArray(propertyTaxCharges, scale);
        scaleArray(interestCharges, scale);
        scaleArray(storeWages, scale);
        scaleArray(industrialWages, scale);
        scaleArray(wageRates, scale);
        scaleArray(staffedWagePerType, scale);

        /*
         * THE TWO MARKETS FIRST, and forgetting them was the bug that took
         * longest to find. FoodMarket carries the price the month was traded at
         * AND its own copy of the exchange rate; leaving both in the old unit
         * meant the national accounts valued the change in the food warehouse
         * at a hundred times the right price for exactly one month, and booked
         * the difference as production. GDP came out eleven times too high
         * while every stock in the city was correct to the last cent - which is
         * why the money audit was clean throughout and said nothing.
         */
        taxPolicy.redenominate(scale);
        if (foodMarket != null) foodMarket.redenominate(scale);
        ironMarket.redenominate(scale);

        commercialHandler.redenominate(scale);
        industrialHandler.redenominate(scale);
        heavyIndustryHandler.redenominate(scale);
        miningHandler.redenominate(scale);
        if (constructionHandler != null) constructionHandler.redenominate(scale);
        businessDebtManager.redenominate(scale);
        salesTaxLedger.redenominate(scale);
        nationalAccounts.redenominate(scale);
    }

    private static void scaleArray(double[] values, double scale) {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) values[i] *= scale;
    }

}
