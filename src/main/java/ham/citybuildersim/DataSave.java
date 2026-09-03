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
     * The month's GDP, and the inventory level it was measured against.
     * Investment in inventories is a change, so a loaded city that believes
     * last month's stock was zero books its entire warehouse as new production.
     */
    private double[] nationalAccounts;

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
    public GameFiles.Result saveGame(GameFiles files, int slot) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return files.write(files.saveFile(slot), gson.toJson(this));
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
