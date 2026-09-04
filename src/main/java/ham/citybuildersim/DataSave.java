package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.util.List;

/**
 *
 * @author Jerus
 */
public class DataSave {

    /*
     * The save path used to live here as a literal, and in three other places
     * besides. It is GameFiles' job now - see the note at the top of that class
     * for why one copy of the fact beats four.
     */

    /*
     * The slot header. These field names are duplicated in SaveHeader, on
     * purpose - the same JSON deserialises into either class, so the label on
     * the save menu can never describe a different city from the file it came
     * from. See SaveHeader.
     */
    private String slotName;
    private String gameVersion;
    private int saveFormat;
    private long savedAt;

    //save variables
    private double cash;
    private int[] buildings;
    private int month = 1;
    private JsonArray debts;

    /**
     * Private-sector loans. Kept in its own array rather than mixed into debts,
     * because the two hierarchies are separate and the load switch would
     * otherwise have to disambiguate government bonds from business loans by
     * type string alone.
     */
    private JsonArray businessDebts;

    /*
     * LEGACY construction state: one entry per stack, in build order. Read on
     * load so old saves are not worse off than they were, never written any
     * more. See constructionProgressById below for why.
     */
    private double[] progress;
    private int[] underConstruction;

    /*
     * Construction, keyed by template id - the same key buildings[] uses.
     *
     * The positional arrays above could not survive a load: stacks come back in
     * id order and only for templates with a completed quantity, so a building
     * that was purely under construction left no stack, everything after it
     * shifted, and the whole array was refused. A player who saved with four
     * depots part-built reloaded to find the work gone.
     */
    private double[] constructionProgressById;
    private int[] underConstructionById;

    /*
     * The property tax the city CHARGED this month, rather than a figure
     * derived from its state.
     *
     * getTaxIncome() reads this back instead of recomputing it, deliberately:
     * the sectors have already borne this exact number in their income
     * statements, and recomputing risks the city collecting a different figure
     * from the one the businesses paid. That makes it state, and state has to
     * be saved - without it a freshly loaded city showed a next-month income
     * missing its whole property-tax line, which then silently corrected itself
     * the first time a month was simulated.
     *
     * Debt interest is NOT saved alongside it, even though it looks like the
     * same kind of figure. finalEconUpdate() zeroes the interest field at the
     * end of every month, so by the time any save can be taken it is already 0
     * and restoring it would only ever write a zero back. (That zeroing is its
     * own bug - the displayed income never subtracts city debt interest - but
     * it is identical before and after a load, so it is not this one.)
     */
    private double propertyTaxCharged;

    /**
     * Interest the city's own bonds accrued this month, not yet charged.
     *
     * The exact twin of propertyTaxCharged above, and it was the half of that
     * fix that never got wired up: EconomyManager.setInterest() was written for
     * this, with a comment saying so, and nothing ever called it.
     *
     * DebtManager.processAllDebts() accrues this AFTER the month's income has
     * been banked, so it is always charged one month later. A save taken in
     * between came back with it at zero - the city skipped a month's interest,
     * and its next-month figure jumped by the whole bill. On $219,700 of bonds
     * that was a $110 swing from a $103 deficit to a $7 surplus.
     */
    private double cityInterestAccrued;

    /*
     * And the same figure split by sector, indexed by BuildingType ordinal.
     *
     * Saved rather than recomputed on load, which is not obvious: property tax
     * is charged early in the month and buildings finish construction after
     * that, so by the time a save is taken the assessed value has moved on.
     * Recomputing from the saved building stock billed retail 4.35 against the
     * 2.95 it actually paid. The charge is a fact about a month, not a function
     * of the state that month ended in.
     */
    private double[] propertyTaxCharges;

    /*
     * The month's trading. Flows, not balances - and nothing can rederive a flow
     * from the balance a month ended on, which is the whole reason these exist.
     *
     * retailCostOfGoods is set by buyInventory() and never recomputed, so
     * without it a loaded city priced its shops with no cost of goods at all.
     * The two industry counts reconstruct the stock the mills traded FROM:
     * updateFinalIndustrialHandler() subtracts both from foodInventory after the
     * statement is written, so the saved inventory is the closing balance and
     * the statement was against the opening one.
     */
    /*
     * What each sector's borrowing cost it, by BuildingType ordinal. Priced off
     * the balance sheet as it stood when the month ran; re-pricing it from the
     * sheet the month ended on gives a different number.
     */
    private double[] interestCharges;

    private double retailCostOfGoods;
    private int retailLocalImports;
    private int retailGlobalImports;
    private double retailFillBasis;
    private double retailImportTax;
    private double industryDemand;
    private int industryUnitsSold;
    private int industryUnitsImported;
    private boolean hasMonthFlows;

    /*
     * The utilisation the month's income statements were written against.
     *
     * A flow, not a balance, exactly like the figures above: the statements run
     * at the start of a month off last month's ratios, and the month then moves
     * them. Recomputing from the state the save was taken in prices the month at
     * ratios it was never traded at - which is invisible while every ratio is 1
     * and obvious the moment roads make one of them routinely less.
     *
     * Absent from saves written before roads existed; hasRatioBasis says so, and
     * the load falls back to recomputing, which is what those saves did anyway.
     */
    private double energyRatioBasis = 1;
    private double waterRatioBasis = 1;
    private double roadRatioBasis = 1;
    private boolean hasRatioBasis;

    /*
     * The month's income statements, as the sectors actually wrote them.
     *
     * The end of the road the four fields above are on. Every one of them is an
     * INPUT to a statement, carried so the statement could be rebuilt - and
     * each one carried revealed another input underneath it. These three arrays
     * are the statements themselves, so there is nothing left to rebuild.
     *
     * Positional, and refused whole rather than padded if the shape does not
     * match this build. See CommercialHandler.getReportState().
     */
    /**
     * The workforce the month was worked by - see
     * PopulationManager.restoreWorkforce(). -1 means a save from before this
     * was carried, where the load recomputes as it always did.
     */
    private int workforce = -1;

    private double[] commercialReport;
    private double[] industrialReport;
    private double[] heavyIndustryReport;
    private double[] miningReport;

    /* ------------------------- land, ore and the retainer -------------------
     *
     * The listing is written out in full rather than regenerated from a seed.
     * Regenerating would be smaller and would tie every existing save to the
     * exact contents of the parcel generator forever - change one weighting and
     * every player's window silently reshuffles, including the expensive plot
     * they were saving up for.
     * ---------------------------------------------------------------------- */
    private double[] landListing;
    private int ironDeposits;
    private double ironReserveTonnes;

    /** The ore price the month traded at. A flow, like every other price here. */
    private double ironLocalPrice;

    private double miningCash;
    private double constructionSubsidy;

    /* ------------------------- the shedding warning -------------------------
     *
     * When construction last sold capacity, and how much it has sold since the
     * player last acknowledged it.
     *
     * A warning is state, not decoration. Without these two fields a reload
     * silently cleared the banner: the city was still dismantling its
     * construction industry, the player had never answered the question, and the
     * game had quietly stopped asking. That is worse than never having warned at
     * all - the one save-and-reload a player does mid-crisis is exactly when
     * they need it most.
     *
     * -1 is "not shedding", which is what a save from before this existed
     * decodes to, and it is the right answer for one: an old save has no record
     * of a warning, so it has no warning to restore.
     * ---------------------------------------------------------------------- */
    private int constructionShedMonth = -1;
    private double constructionShedPoints;

    /*
     * The month's GDP, and the inventory level it was measured against.
     * Investment in inventories is a change, so a loaded city that believes
     * last month's stock was zero books its entire warehouse as new production.
     */
    private double[] nationalAccounts;

    /*
     * Two records of things that HAPPENED, rather than things the city has.
     *
     * Neither was saved, so every load emptied the demolition log and reset the
     * write-off history to zero - a city came back looking like it had never
     * lost a building or defaulted on anything. Both are history, and history
     * is the one kind of state that cannot be recomputed from the present.
     */
    private java.util.List<DemolitionLog.Entry> demolitions;

    /*
     * The other half of that history: what the city GAINED. Reads back null on
     * every save written before this build, which BuildLog.restore() takes as an
     * empty log - the ordinary downward-compatible case, not an error.
     */
    private java.util.List<BuildLog.Entry> builds;
    private java.util.Map<String, Double> writeOffTotals;

    /*
     * Construction's books: cash, and the order book that percentage-of-
     * completion revenue is recognised against. Without the backlog a loaded
     * city books zero construction output until the queue would have emptied.
     */
    private double constructionCash;
    private double constructionUnearnedRevenue;
    private double constructionBacklogPoints;
    private int constructionMaterials;
    private int storeInventory;
    private int industryFoodInventory;
    private int population;
    
    //settings
    private boolean reports = true;
    private boolean graphs = true;
    
    
    //business stuff
    //cash
    private double industrialCash;
    private double commercialCash;
    private double realEstateCash;
    private double heavyIndustryCash;

    /**
     * What the residents have not spent, since the city was founded.
     *
     * Saved because it is history rather than a monthly figure: losing it on
     * load would silently reset a record of whether the city's wages have kept
     * up with its prices, which is the one thing the household account exists
     * to show over time.
     */
    private double householdSavings;

    /*
     * Land.
     *
     * What the city OWNS is saved; what is built on is not, because the
     * buildings already say that and two records of the same fact can disagree.
     * A save written before land existed reads landOwned as 0, which the load
     * path treats as "no land data" and falls back to the starting allocation.
     */
    private double landOwned;
    private int landBlocksPurchased;
    private double landPricePerSqFt;

    /*
     * Tax rates. Zero means "written before these were saved" rather than "a
     * city that charges nothing" - a genuine zero rate is indistinguishable
     * from a missing field in JSON, and defaulting an old save to no taxes at
     * all would be a far stranger surprise than defaulting it to the standard
     * ones. A player who really wants zero can set it again in two clicks.
     */
    private double incomeTaxRate;
    private double propertyTaxRate;
            

 

    /* ------------------------------- the header ------------------------------- */

    public void setSlotName(String name)      { this.slotName = name; }
    public String getSlotName()               { return slotName; }

    /** Stamped at save time so a save always says which build wrote it. */
    public void stamp(String gameVersion, int saveFormat, long savedAt) {
        this.gameVersion = gameVersion;
        this.saveFormat = saveFormat;
        this.savedAt = savedAt;
    }

    public String getGameVersion() { return gameVersion; }
    public int getSaveFormat()     { return saveFormat; }
    public long getSavedAt()       { return savedAt; }

    public void setHeavyIndustryCash(double cash) { this.heavyIndustryCash = cash; }
    public double getHeavyIndustryCash()          { return heavyIndustryCash; }

    public void setHouseholdSavings(double value)  { this.householdSavings = value; }
    public double getHouseholdSavings()            { return householdSavings; }

    public void setLandOwned(double sqFt)          { this.landOwned = sqFt; }
    public void setLandBlocksPurchased(int blocks) { this.landBlocksPurchased = blocks; }
    public void setLandPricePerSqFt(double price)  { this.landPricePerSqFt = price; }

    public double getLandOwned()          { return landOwned; }
    public int    getLandBlocksPurchased(){ return landBlocksPurchased; }
    public double getLandPricePerSqFt()   { return landPricePerSqFt; }

    public void setIncomeTaxRate(double rate)   { this.incomeTaxRate = rate; }
    public void setPropertyTaxRate(double rate) { this.propertyTaxRate = rate; }

    public double getIncomeTaxRate()   { return incomeTaxRate; }
    public double getPropertyTaxRate() { return propertyTaxRate; }

    public void setUnderConstruction(int[] underConstruction) {
        this.underConstruction = underConstruction;
    }

    //setters
    public void setCash(double money) {
        cash = money;
    }
    
    public void setMonth(int month){
        this.month = month;
    }

    public void setBuildingNum(int i) {
        buildings = new int[i];
    }

    public void setBuildingQuantity(int index, int quantity) {
        if (index < 0 || index >= buildings.length) {
            throw new IllegalArgumentException("Invalid building index: " + index);
        }
        buildings[index] = quantity;
    }
    
    public void setDebt(List<Debt> debts) {
        Gson gson = new Gson();
        this.debts = gson.toJsonTree(debts).getAsJsonArray();
    }
    
    public void setProgress(double[] progress){
        this.progress = progress;
    }
    
    public void setConstructionMaterials(int constructionMaterials){
        this.constructionMaterials = constructionMaterials;
    }
    
    public void setStoreInventory(int storeInventory){
        this.storeInventory = storeInventory;
    }
    public void setIndustryFoodInventory(int foodInventory){
        this.industryFoodInventory = foodInventory;
    }
    public void setPopulation(int population){
        this.population = population;
    }
    public void setReports(boolean reports){
        this.reports = reports;
    }
    public void setGraphs(boolean graphs){
        this.graphs = graphs;
    }

    public void setIndustrialCash(double industrialCash) {
        this.industrialCash = industrialCash;
    }

    public void setCommercialCash(double commercialCash) {
        this.commercialCash = commercialCash;
    }

    public void setRealEstateCash(double realEstateCash) {
        this.realEstateCash = realEstateCash;
    }
    

    /**
     * Writes the city out.
     *
     * Returns the outcome instead of swallowing it. The previous version caught
     * IOException, printed "Error saving." to a console no player will ever see,
     * and returned normally - so Game.saveGame() went on to announce "Game
     * successfuly saved." on top of a save that had not happened. Telling
     * someone their city is safe when it is not is worse than not saving at all,
     * because it is the point at which they stop worrying about it.
     */
    /**
     * Writes the save, and refuses to take the game down with it if it cannot.
     *
     * Gson will not serialise NaN or Infinity - it throws
     * IllegalArgumentException rather than writing them - and that throw used
     * to escape all the way out of Game.save(). Which meant one overflowed
     * number anywhere in the city killed the process, from the AUTOSAVE, in the
     * middle of a skip, where the player is not even looking. The city was
     * still perfectly playable; it just could not be written down.
     *
     * Found by the long playtest: an insolvent city rolls its emergency T-Bills
     * at 25% every four months, which is exponential, so given enough centuries
     * the numbers leave what a double can hold. That underlying spiral is a
     * design question and is written up separately. THIS is not: whatever state
     * the city gets into, failing to save is a message, not a crash.
     */
    public GameFiles.Result saveGame(GameFiles files, int slot) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json;

        try {
            json = gson.toJson(this);
        } catch (RuntimeException e) {
            return GameFiles.Result.failed(files.saveFile(slot),
                    "the city's numbers cannot be written down"
                    + describeUnwritable()
                    + " (" + e.getClass().getSimpleName() + ")");
        }

        return files.write(files.saveFile(slot), json);
    }

    /**
     * Names the field that broke, if it can find it.
     *
     * "Could not save" is a dead end for whoever reads the log; "cash is
     * Infinity" is a bug report. Reflection rather than an enumerated list on
     * purpose - this runs once, on a path that has already failed, and a list
     * would go stale the next time a field is added.
     */
    private String describeUnwritable() {

        StringBuilder found = new StringBuilder();

        for (java.lang.reflect.Field field : DataSave.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);

                if (value instanceof Double d && !Double.isFinite(d)) {
                    append(found, field.getName(), d);
                } else if (value instanceof double[] array) {
                    for (int i = 0; i < array.length; i++) {
                        if (!Double.isFinite(array[i])) {
                            append(found, field.getName() + "[" + i + "]", array[i]);
                        }
                    }
                }
            } catch (Exception | LinkageError ignored) {
                // A field that will not answer is not worth failing over twice.
            }
        }

        return (found.length() == 0) ? "" : ": " + found;
    }

    private void append(StringBuilder sb, String name, double value) {
        if (sb.length() > 0) sb.append(", ");
        if (sb.length() < 200) sb.append(name).append(" is ").append(value);
    }


  
    /* ------------------------- construction, by id ------------------------- */

    public void setConstructionById(int[] underConstruction, double[] progress) {
        this.underConstructionById = underConstruction;
        this.constructionProgressById = progress;
    }

    /** False for a save written before the format changed. */
    public boolean hasConstructionById() {
        return underConstructionById != null && constructionProgressById != null;
    }

    public int getConstructionByIdLength() {
        return (underConstructionById == null) ? 0 : underConstructionById.length;
    }

    public int getUnderConstructionById(int templateId) {
        return (underConstructionById == null
                || templateId < 0
                || templateId >= underConstructionById.length)
                ? 0 : underConstructionById[templateId];
    }

    public double getConstructionProgressById(int templateId) {
        return (constructionProgressById == null
                || templateId < 0
                || templateId >= constructionProgressById.length)
                ? 0 : constructionProgressById[templateId];
    }

    /* ------------------------ charged, not derived ------------------------ */

    public void setPropertyTaxCharged(double value) { this.propertyTaxCharged = value; }
    public double getPropertyTaxCharged()           { return propertyTaxCharged; }

    public void setCityInterestAccrued(double value) { this.cityInterestAccrued = value; }
    public double getCityInterestAccrued()           { return cityInterestAccrued; }

    public void setPropertyTaxCharges(double[] charges) { this.propertyTaxCharges = charges; }
    public double[] getPropertyTaxCharges()             { return propertyTaxCharges; }

    public void setInterestCharges(double[] charges) { this.interestCharges = charges; }
    public double[] getInterestCharges()             { return interestCharges; }

    public void setMonthFlows(double retailCostOfGoods, int retailLocal, int retailGlobal,
                              double retailFillBasis, double retailImportTax,
                              double demand, int sold, int imported) {
        this.retailCostOfGoods = retailCostOfGoods;
        this.retailLocalImports = retailLocal;
        this.retailGlobalImports = retailGlobal;
        this.retailFillBasis = retailFillBasis;
        this.retailImportTax = retailImportTax;
        this.industryDemand = demand;
        this.industryUnitsSold = sold;
        this.industryUnitsImported = imported;
        this.hasMonthFlows = true;
    }

    public void setRatioBasis(double energy, double water, double road) {
        this.energyRatioBasis = energy;
        this.waterRatioBasis = water;
        this.roadRatioBasis = road;
        this.hasRatioBasis = true;
    }

    public void setWorkforce(int workforce) { this.workforce = workforce; }

    /** -1 when the save predates this field. */
    public int getWorkforce()               { return workforce; }

    public void setReportState(double[] commercial, double[] industrial,
                               double[] heavy, double[] mining) {
        this.commercialReport = commercial;
        this.industrialReport = industrial;
        this.heavyIndustryReport = heavy;
        this.miningReport = mining;
    }

    public void setLandState(double[] listing, int deposits, double reserveTonnes) {
        this.landListing = listing;
        this.ironDeposits = deposits;
        this.ironReserveTonnes = reserveTonnes;
    }

    public double[] getLandListing()        { return landListing; }
    public int getIronDeposits()            { return ironDeposits; }
    public double getIronReserveTonnes()    { return ironReserveTonnes; }

    public void setIronLocalPrice(double price) { this.ironLocalPrice = price; }
    public double getIronLocalPrice()           { return ironLocalPrice; }

    public void setMiningCash(double cash)      { this.miningCash = cash; }
    public double getMiningCash()               { return miningCash; }

    public void setConstructionSubsidy(double amount) { this.constructionSubsidy = amount; }
    public double getConstructionSubsidy()            { return constructionSubsidy; }

    /* -------------------------- policy --------------------------
     *
     * The city's two rates and every band and sector offset, plus which sectors
     * the city has undertaken to protect, plus the month's VAT ledger.
     *
     * The ledger is here because it is a FLOW - what each sector sold and what
     * tax it had already paid on its inputs during the month. Nothing about the
     * state the month ended in can reconstruct it, which is the same reason the
     * income statements are carried rather than recomputed.
     */
    private double[] taxPolicyState;
    private boolean[] autoSubsidy;
    private double[] salesTaxLedger;

    public void setTaxPolicyState(double[] state)  { this.taxPolicyState = state; }
    public double[] getTaxPolicyState()            { return taxPolicyState; }

    public void setAutoSubsidy(boolean[] on)       { this.autoSubsidy = on; }
    public boolean[] getAutoSubsidy()              { return autoSubsidy; }

    public void setSalesTaxLedger(double[] state)  { this.salesTaxLedger = state; }
    public double[] getSalesTaxLedger()            { return salesTaxLedger; }

    public void setConstructionShedding(int month, double points) {
        this.constructionShedMonth = month;
        this.constructionShedPoints = points;
    }
    public int getConstructionShedMonth()     { return constructionShedMonth; }
    public double getConstructionShedPoints() { return constructionShedPoints; }

    public double[] getMiningReport()        { return miningReport; }
    public double[] getCommercialReport()    { return commercialReport; }
    public double[] getIndustrialReport()    { return industrialReport; }
    public double[] getHeavyIndustryReport() { return heavyIndustryReport; }

    /** False for a save written before roads, whose ratios were all 1 anyway. */
    public boolean hasRatioBasis()          { return hasRatioBasis; }
    public double getEnergyRatioBasis()     { return energyRatioBasis; }
    public double getWaterRatioBasis()      { return waterRatioBasis; }
    public double getRoadRatioBasis()       { return roadRatioBasis; }

    /** False for a save written before flows were carried. */
    public boolean hasMonthFlows()          { return hasMonthFlows; }
    public double getRetailCostOfGoods()    { return retailCostOfGoods; }
    public int getRetailLocalImports()      { return retailLocalImports; }
    public int getRetailGlobalImports()     { return retailGlobalImports; }
    public double getRetailFillBasis()      { return retailFillBasis; }
    public double getRetailImportTax()      { return retailImportTax; }
    public double getIndustryDemand()       { return industryDemand; }
    public int getIndustryUnitsSold()       { return industryUnitsSold; }
    public int getIndustryUnitsImported()   { return industryUnitsImported; }

    public void setDemolitions(java.util.List<DemolitionLog.Entry> entries) {
        this.demolitions = entries;
    }
    public java.util.List<DemolitionLog.Entry> getDemolitions() { return demolitions; }

    public void setBuilds(java.util.List<BuildLog.Entry> entries) {
        this.builds = entries;
    }
    public java.util.List<BuildLog.Entry> getBuilds() { return builds; }

    public void setWriteOffTotals(java.util.Map<String, Double> totals) {
        this.writeOffTotals = totals;
    }
    public java.util.Map<String, Double> getWriteOffTotals() { return writeOffTotals; }

    public void setNationalAccounts(double[] state) { this.nationalAccounts = state; }
    public double[] getNationalAccounts()           { return nationalAccounts; }

    public void setConstructionBooks(double cash, double unearned, double backlog) {
        this.constructionCash = cash;
        this.constructionUnearnedRevenue = unearned;
        this.constructionBacklogPoints = backlog;
    }
    public double getConstructionCash()            { return constructionCash; }
    public double getConstructionUnearnedRevenue() { return constructionUnearnedRevenue; }
    public double getConstructionBacklogPoints()   { return constructionBacklogPoints; }

    //getters
    /*
     * Null-safe, because new saves no longer write the legacy arrays at all.
     * Before this these threw NullPointerException on a save that omitted them,
     * and the NPE would have escaped the IOException catch in loadGame() exactly
     * the way the old IllegalArgumentException did - abandoning the rest of the
     * load without saying so.
     */
    public int getUnderConstructionLength(){
        return (underConstruction == null) ? 0 : underConstruction.length;
    }
       public int getUnderConstruction(int index) {
        return underConstruction[index];
    }
    public double getCash() {
        return cash;
    }
    
    public int getMonth(){
        return month;
    }
    
    public int getBuildingQuantity(int index){
        return buildings[index];
    }
    
    public int getBuildingsLength(){
        return buildings.length;
    }
    
    public JsonArray getDebt(){
        return debts;
    }

    public void setBusinessDebt(List<BusinessDebt> loans) {
        Gson gson = new Gson();
        this.businessDebts = gson.toJsonTree(loans).getAsJsonArray();
    }

    public JsonArray getBusinessDebt(){
        return businessDebts;
    }

    public int getProgressLength(){
        return (progress == null) ? 0 : progress.length;
    }
    
    public double getProgress(int index){
        return progress[index];
    }
    
    public int getConstructionMaterials(){
        return constructionMaterials;
    }
    
    public int getStoreInventory(){
        return storeInventory;
    }
    
    public int getIndustryFoodInventory(){
        return industryFoodInventory;
    }
    public int getPopulation(){
        return population;
    }
    public boolean getReports(){
        return reports;
    }
    public boolean getGraphs(){
        return graphs;
    }
    
    public double getIndustrialCash() {
        return industrialCash;
    }

    public double getCommercialCash() {
        return commercialCash;
    }

    
    public double getRealEstateCash() {
        return realEstateCash;
    }
}
