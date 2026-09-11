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
    /** See setNotices: null on every save written before the inbox existed. */
    private java.util.List<Notice> notices;

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
    /** Material the sites still have to draw, by id. Absent on a save from before the crews drew as they built. */
    private double[] materialsOwedById;
    /** The builders' contract still on each template's sites, by id. Absent on a save from before the book was kept per stack. */
    private double[] contractValueById;

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
     * EVERY SECTOR, WHOLE, BY NAME - and every market's price (2026-09-11,
     * the sector template). Its cash, its stocks, the month in progress, the
     * month last struck, the three bills of the month, and whatever state is
     * its own. This replaced five differently-shaped report arrays, two
     * arrays indexed by BuildingType.ordinal(), and a dozen loose fields
     * (commercialCash, industryFoodInventory, retailFillBasis...) carried
     * one by one. See SectorState and Markets.State.
     */
    private java.util.List<SectorState> sectors;
    private java.util.List<Markets.State> markets;

    public void setSectors(java.util.List<SectorState> s)   { this.sectors = s; }
    public java.util.List<SectorState> getSectors()         { return sectors; }
    public void setMarkets(java.util.List<Markets.State> m) { this.markets = m; }
    public java.util.List<Markets.State> getMarkets()       { return markets; }

    /**
     * The workforce the month was worked by - see
     * PopulationManager.restoreWorkforce(). -1 means a save from before this
     * was carried, where the load recomputes as it always did.
     */
    private int workforce = -1;

    /* ------------------------- land, ore and the retainer -------------------
     *
     * The listing is written out in full rather than regenerated from a seed.
     * Regenerating would be smaller and would tie every existing save to the
     * exact contents of the parcel generator forever - change one weighting and
     * every player's window silently reshuffles, including the expensive plot
     * they were saving up for.
     * ---------------------------------------------------------------------- */
    private double[] landListing;
    /** The office's struck prices and minimum lot - see LandMarket.getPriceState(). */
    private double[] landMarketPrices;
    private int ironDeposits;
    private double ironReserveTonnes;

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
     * The rest of the borrower's record: how many times each sector has been
     * written down, and how many months of borrowing ban it has left. Null on
     * a save from before 2026-09-10, which restores as a clean record - what
     * those cities were already reading, wrongly, on every load.
     */
    private java.util.Map<String, Integer> restructureCounts;
    private java.util.Map<String, Integer> blockedMonths;

    /** The city's own yard, in units. */
    private int constructionMaterials;
    private int population;
    
    /*
     * THE SECTOR STATEMENTS, this month and last.
     *
     * Additive, and deliberately NOT a format bump. Gson matches by field name:
     * an older save has no such key and lands here as null, which restoreFrom
     * treats as "no books yet" and the screens report honestly; an older BUILD
     * reading a newer save ignores a field it does not know. Same reasoning as
     * the notices - see GameVersion's note on why SAVE_FORMAT stayed at 19.
     */
    private java.util.List<SectorBooks.SectorMonth> sectorBooks;
    private java.util.List<SectorBooks.SectorMonth> sectorBooksBefore;

    public void setSectorBooks(java.util.List<SectorBooks.SectorMonth> books) {
        this.sectorBooks = books;
    }

    public void setSectorBooksBefore(java.util.List<SectorBooks.SectorMonth> books) {
        this.sectorBooksBefore = books;
    }

    public java.util.List<SectorBooks.SectorMonth> getSectorBooks() {
        return sectorBooks;
    }

    public java.util.List<SectorBooks.SectorMonth> getSectorBooksBefore() {
        return sectorBooksBefore;
    }

    //settings
    private boolean reports = true;
    private boolean graphs = true;
    
    
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

    /**
     * The last month the treasury closed: opening, closing, raised, repaid,
     * surplus, and whether it happened at all. See Game.takeTreasuryMonth().
     *
     * An array rather than six fields because it is one fact - a month - and
     * six loose doubles in this class is six chances to save five of them.
     */
    private double[] treasuryMonth;

    public void setTreasuryMonth(double[] state) { this.treasuryMonth = state; }
    public double[] getTreasuryMonth()           { return treasuryMonth; }

    /**
     * The government's own month: seventeen revenue and spending lines, saved
     * and restored as one. See NationalAccounts.governmentToSave() for why
     * every one of them has to be carried rather than rebuilt.
     *
     * Absent from older saves, which keeps whatever the rebuild produced for
     * them - the same partial block they always came back with - so SAVE_FORMAT
     * does not move. See GameVersion, "NOT 20".
     */
    private double[] governmentMonth;

    public void setGovernmentMonth(double[] state) { this.governmentMonth = state; }
    public double[] getGovernmentMonth()           { return governmentMonth; }
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
    
    public void setPopulation(int population){
        this.population = population;
    }
    public void setReports(boolean reports){
        this.reports = reports;
    }

    /**
     * The city's inbox.
     *
     * A typed list rather than a JsonArray, because unlike the debts these are
     * one shape and Gson can round-trip them without help. A save written
     * before the inbox existed has no key for this, so Gson leaves the field
     * null - which restoreFrom reads as "nothing had been said to this city
     * yet", and next month whatever is still wrong says it again.
     */
    public void setNotices(java.util.List<Notice> notices){
        this.notices = notices;
    }
    public void setGraphs(boolean graphs){
        this.graphs = graphs;
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

    public void setConstructionById(int[] underConstruction, double[] progress, double[] materialsOwed, double[] contractValue) {
        this.underConstructionById = underConstruction;
        this.constructionProgressById = progress;
        this.materialsOwedById = materialsOwed;
        this.contractValueById = contractValue;
    }

    /** False on a save that kept one order book for the whole city. */
    public boolean hasContractsById() { return contractValueById != null; }

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

    public double getContractValueById(int templateId) {
        return (contractValueById == null
                || templateId < 0
                || templateId >= contractValueById.length)
                ? 0 : contractValueById[templateId];
    }

    /** Zero on a save that has no record: its orders drew their material the day they were placed. */
    public double getMaterialsOwedById(int templateId) {
        return (materialsOwedById == null
                || templateId < 0
                || templateId >= materialsOwedById.length)
                ? 0 : materialsOwedById[templateId];
    }

    /* ------------------------ charged, not derived ------------------------ */

    public void setPropertyTaxCharged(double value) { this.propertyTaxCharged = value; }
    public double getPropertyTaxCharged()           { return propertyTaxCharged; }

    public void setCityInterestAccrued(double value) { this.cityInterestAccrued = value; }
    public double getCityInterestAccrued()           { return cityInterestAccrued; }

    public void setWorkforce(int workforce) { this.workforce = workforce; }

    /** -1 when the save predates this field. */
    public int getWorkforce()               { return workforce; }

    public void setLandState(double[] listing, int deposits, double reserveTonnes) {
        this.landListing = listing;
        this.ironDeposits = deposits;
        this.ironReserveTonnes = reserveTonnes;
    }

    public double[] getLandListing()        { return landListing; }
    public void setLandMarketPrices(double[] state) { this.landMarketPrices = state; }
    public double[] getLandMarketPrices()   { return landMarketPrices; }
    public int getIronDeposits()            { return ironDeposits; }
    public double getIronReserveTonnes()    { return ironReserveTonnes; }

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

    /**
     * What the households have saved and what they owe, per pay tier.
     *
     * A STOCK. Everything else on the household screen is this month's flow and
     * is rebuilt from the month; savings and debt are the accumulation of every
     * month before it and cannot be. A save without them reloads a city whose
     * families are all suddenly solvent.
     */
    private double[] householdBalance;

    /**
     * The same stock, per CELL of the family matrix - one shape at one tier -
     * since 2026-09-10, when the households became objects (Household,
     * HouseholdBalance). Named cell by cell, so a shape added to the enum
     * cannot read one cell's money into another's; a key this build has no
     * cell for is skipped. The row array above is still written, as the sum
     * of the cells, for a build from before them - which is why neither array
     * moved SAVE_FORMAT. A save without this seeds every cell of a row with
     * the row's position.
     */
    private String[] householdCellKeys;
    private double[] householdCells;

    /**
     * The bank's cash.
     *
     * A STOCK, and one of MoneyAudit's pools since 2026-09-07, so a save that
     * left it out reloaded a city whose total money supply had changed by
     * however much its bank happened to be holding. It is a single number
     * rather than the whole position because everything else the bank knows -
     * its branches, its deposits, its loan book - is re-read off the city at
     * the top of every month and would be overwritten a tick later anyway.
     */
    private double bankCash;


    /**
     * The housing match the month's rent was struck on.
     *
     * Derived, and carried anyway, because it is derived from a stock that has
     * since changed: homes finish construction after the match runs, so
     * recomputing it on load bills a different month's housing.
     */
    private double rentWeight;

    /** The protected sectors, by name, and the month's VAT ledger by name. Since the sector template. */
    private java.util.List<String> subsidisedSectors;
    private SalesTaxLedger.State salesTax;
    /** Every sector's three tax offsets, by name. See TaxPolicy.getSectorOffsets(). */
    private java.util.List<TaxPolicy.SectorOffsets> sectorOffsets;

    public void setSubsidisedSectors(java.util.List<String> keys) { this.subsidisedSectors = keys; }
    public java.util.List<String> getSubsidisedSectors()          { return subsidisedSectors; }
    public void setSalesTax(SalesTaxLedger.State state)           { this.salesTax = state; }
    public SalesTaxLedger.State getSalesTax()                     { return salesTax; }
    public void setSectorOffsets(java.util.List<TaxPolicy.SectorOffsets> o) { this.sectorOffsets = o; }
    public java.util.List<TaxPolicy.SectorOffsets> getSectorOffsets()       { return sectorOffsets; }

    public void setTaxPolicyState(double[] state)  { this.taxPolicyState = state; }
    public double[] getTaxPolicyState()            { return taxPolicyState; }

    public void setHouseholdBalance(double[] state) { this.householdBalance = state; }
    public double[] getHouseholdBalance()           { return householdBalance; }

    public void setHouseholdCells(String[] keys, double[] state) {
        this.householdCellKeys = keys;
        this.householdCells = state;
    }
    public String[] getHouseholdCellKeys() { return householdCellKeys; }
    public double[] getHouseholdCells()    { return householdCells; }

    public void setBankCash(double cash) { this.bankCash = cash; }
    public double getBankCash()          { return bankCash; }

    /**
     * Branches the shareholders have already paid capital for.
     *
     * A COUNTER, and it has to be carried or every reload re-capitalises every
     * branch in the city - which is $32,000 of fresh equity per branch per load,
     * declared to the audit as money arriving from outside, for nothing. Free
     * money for anybody who noticed that saving and loading made their bank
     * stronger.
     */
    private double bankBranchesCapitalised;

    public void setBankBranchesCapitalised(double n) { this.bankBranchesCapitalised = n; }
    public double getBankBranchesCapitalised()       { return bankBranchesCapitalised; }

    /**
     * The bank's profit for the month this save was taken in.
     *
     * A FLOW, and carried for the reason every flow in this file is carried:
     * next month charges tax on it, and nothing about the balance sheet the
     * month ended with can reproduce it.
     */
    private double bankProfitLastMonth;

    public void setBankProfitLastMonth(double v) { this.bankProfitLastMonth = v; }
    public double getBankProfitLastMonth()       { return bankProfitLastMonth; }

    /** What savers were paid. A flow - see Bank.setDepositRate(). */
    private double bankDepositRate;
    public void setBankDepositRate(double v) { this.bankDepositRate = v; }
    public double getBankDepositRate()       { return bankDepositRate; }

    /**
     * The city's foreign position: reserves, the trailing import bill the cover
     * is measured against, how many months have been counted into it, and the
     * exchange rate.
     *
     * All STOCKS. The reserve is the accumulation of every month that has ever
     * crossed the city's edge and cannot be recovered from the month the save
     * was taken in; the trailing import figure is an average of months that have
     * gone. See ForeignAccounts.
     */
    private double[] foreignAccounts;

    public void setForeignAccounts(double[] state) { this.foreignAccounts = state; }
    public double[] getForeignAccounts()           { return foreignAccounts; }

    /**
     * The city's standing with foreign lenders: the default scar and how long
     * ago it was earned.
     *
     * Carried because a scar that heals on reload is not a scar - a player who
     * defaulted abroad could otherwise save, load, and find the window open
     * again the same afternoon.
     */
    private double[] foreignStanding;

    public void setForeignStanding(double[] state) { this.foreignStanding = state; }
    public double[] getForeignStanding()           { return foreignStanding; }

    /**
     * Hot money: how much is here, and how long the city has left to sweat.
     *
     * The stock is the obvious half. The panic clock is the half that would be
     * missed: a city loaded mid-stop with the clock reset would find the money
     * flooding straight back in on a currency it had just run from.
     */
    private double[] capitalFlows;

    public void setCapitalFlows(double[] state) { this.capitalFlows = state; }
    public double[] getCapitalFlows()           { return capitalFlows; }

    /**
     * The city's own savings abroad, per sector. See OutwardInvestment.
     * Absent from a save written before 2026-09-10: Gson leaves the field
     * null, restore() refuses it, and the city starts with nothing abroad -
     * which is what every such city had.
     */
    private double[] outwardInvestment;

    public void setOutwardInvestment(double[] state) { this.outwardInvestment = state; }
    public double[] getOutwardInvestment()           { return outwardInvestment; }

    /**
     * The share register, company by company, named. See Equity. Absent from
     * a save written before 2026-09-10 (evening): nobody owned anything, and
     * the companies list on the first month back exactly as a founding city
     * would.
     */
    private String[] equityKeys;
    private double[] equity;

    public void setEquity(String[] keys, double[] state) { this.equityKeys = keys; this.equity = state; }
    public String[] getEquityKeys() { return equityKeys; }
    public double[] getEquity()     { return equity; }

    /** The exchange's quotes, company by company, named. See Exchange. Absent before 2026-09-10 (night). */
    private double[] exchange;
    public void setExchange(double[] state) { this.exchange = state; }
    public double[] getExchange()           { return exchange; }

    /**
     * How often the bank has failed, what its creditors ate, and whether it is
     * frozen right now.
     *
     * Not derivable from the sheet - see Bank.solvencyToSave(). Null on a save
     * written before 2026-09-10, which restores as a bank with no record, which
     * is the state those saves already loaded in.
     *
     * NOT a SAVE_FORMAT change: Gson leaves an absent field alone.
     */
    private double[] bankSolvency;

    public void setBankSolvency(double[] state) { this.bankSolvency = state; }
    public double[] getBankSolvency()           { return bankSolvency; }

    /**
     * Doors that were lived in when the month's housing pass ran.
     *
     * A MID-MONTH FACT, and therefore not derivable from the stock the month
     * ended with. CommercialHandler.setOccupiedHomes() clamps what the family
     * model needs against the homes that existed AT THAT MOMENT, deliberately -
     * a landlord cannot let a flat that has not been built yet. Buildings then
     * finish later in the same month, so the load path, re-deriving it against
     * the final stock, clamps against a bigger number and gets a bigger answer.
     *
     * Measured by HousingCheck on a tight city: 1.614 doors let on the reloaded
     * city against the 1.000 the saved one had. Null on a save from before
     * 2026-09-10, which keeps the re-derived figure those saves already loaded.
     */
    private double[] housingOccupancy;

    public void setHousingOccupancy(double[] state) { this.housingOccupancy = state; }
    public double[] getHousingOccupancy()           { return housingOccupancy; }

    /**
     * The bank's last CLOSED month: payroll, upkeep, interest earned, book.
     *
     * Read by the investment advisor when it asks whether another branch would
     * pay for itself, which it asks in the gap between a month opening and
     * anything moving through it - so the live fields are all zero there. See
     * Bank.closeMonth(). Null on an older save, which leaves the advisor with
     * nothing to judge on for one month, exactly as before this existed.
     */
    private double[] bankLastMonth;

    public void setBankLastMonth(double[] state) { this.bankLastMonth = state; }
    public double[] getBankLastMonth()           { return bankLastMonth; }

    /**
     * The price basket, its weights, and a year of readings.
     *
     * The index itself could be restruck from today's prices. The YEAR OF
     * HISTORY could not, and without it a reloaded city reports zero inflation
     * for twelve months however fast prices are moving.
     */
    private double[] priceIndex;

    public void setPriceIndex(double[] state) { this.priceIndex = state; }
    public double[] getPriceIndex()           { return priceIndex; }

    /**
     * The world's price level and what it is rising at.
     *
     * The level is a compounding stock and cannot be rebuilt from anything the
     * city holds; the rate is a walk whose position is the whole of its state.
     * A reloaded city without these starts the world's prices over at founding,
     * which would make three centuries of imported inflation vanish.
     */
    private double[] worldEconomy;

    public void setWorldEconomy(double[] state) { this.worldEconomy = state; }
    public double[] getWorldEconomy()           { return worldEconomy; }

    /*
     * THE RATE THE MONTH WAS TRADED AT, as EconomyManager holds it: the foreign
     * rate times the world's price level, struck at the top of the month and
     * fanned out to the food market and the building manager. Until 2026-09-10
     * the load path restored the rate into DebtManager - "load-path parity,
     * for the third time" - and never into EconomyManager, so a loaded city
     * held the founding 1.0 until the next tick: every import price at its
     * founding value, the balance sheet repricing every building on it, and
     * the next month's GDP 6.8% low. Zero on an older save; Game falls back to
     * recomputing the product, which is within a third of a percent.
     */
    private double tradedExchangeRate;

    public void setTradedExchangeRate(double rate) { this.tradedExchangeRate = rate; }
    public double getTradedExchangeRate()          { return tradedExchangeRate; }

    /**
     * The currency's unit and how many reforms it has been through.
     *
     * CARRIED, NOT DERIVED, and it has to be: buildings.json is read fresh in
     * founding dollars every time a game starts, so a reloaded city that did
     * not know it had lopped two zeros would find a House costing a hundred
     * times its real price. Absent (null) means a save from before currency
     * reform existed, which is a city in founding money - exactly what a fresh
     * Denomination already says.
     */
    private double[] denomination;

    public void setDenomination(double[] state) { this.denomination = state; }
    public double[] getDenomination()           { return denomination; }

    /** The policy rate. A decision, not a derivation - so it has to be carried. */
    private double policyRate;

    public void setPolicyRate(double rate) { this.policyRate = rate; }
    public double getPolicyRate()          { return policyRate; }

    /**
     * How far wages have chased the cost of living.
     *
     * A STOCK - it is an accumulation of every month's drift towards a target,
     * and the target alone cannot reproduce it. A save that forgot it reloaded a
     * city whose workers had never noticed the last devaluation.
     */
    private double costOfLiving = 1;

    public void setCostOfLiving(double v) { this.costOfLiving = v; }
    public double getCostOfLiving()       { return costOfLiving <= 0 ? 1 : costOfLiving; }

    /**
     * The tax the city actually took off the bank this month.
     *
     * The same rule as propertyTaxCharged above: the month's tax figures are
     * CARRIED, not recomputed, because the city has to report the figure the
     * business really paid. Left out, a reloaded city previewed next month's
     * income $19 light - the bank's tax line was simply missing from a total
     * everything downstream reads.
     */
    private double bankTaxCharged;

    public void setBankTaxCharged(double v) { this.bankTaxCharged = v; }
    public double getBankTaxCharged()       { return bankTaxCharged; }


    /** What the landlords billed this month - see FamilyModel.setRentWeight(). */
    public void setRentWeight(double weight) { this.rentWeight = weight; }
    public double getRentWeight()            { return rentWeight; }

    /**
     * The studio half of that weight, added 2026-09-09 with the segment split.
     *
     * Absent from every older save, where it deserialises to zero - and zero is
     * the right answer for those saves rather than a missing one: they had a
     * single rent price, that price is what rentPrice still means, and the
     * family half is where it belongs. So the load path takes the family weight
     * as `rentWeight - rentWeightStudio` on every save, old or new, and needs
     * no version test at all.
     *
     * Saved rather than re-derived because re-deriving it drifts. The split was
     * briefly reconstructed by re-running the housing match on load and
     * apportioning the saved total by it - which lands within a household of
     * where it was, and SaveFileCheck measured that as 1.3 cents on $690,709
     * one month later. Two prices make a rounding error into a wrong bill.
     */
    private double rentWeightStudio;

    public void setRentWeightStudio(double weight) { this.rentWeightStudio = weight; }
    public double getRentWeightStudio()            { return rentWeightStudio; }

    public void setConstructionShedding(int month, double points) {
        this.constructionShedMonth = month;
        this.constructionShedPoints = points;
    }
    public int getConstructionShedMonth()     { return constructionShedMonth; }
    public double getConstructionShedPoints() { return constructionShedPoints; }

    public void setDemolitions(java.util.List<DemolitionLog.Entry> entries) {
        this.demolitions = entries;
    }
    public java.util.List<DemolitionLog.Entry> getDemolitions() { return demolitions; }

    /*
     * The demographics. `cohorts` is the population itself since the switch, so
     * this is no longer a nicety - a city that lost its pyramid would lose its
     * residents. `families` is derived and saved anyway, so a reloaded city
     * looks identical on the first frame rather than after the next tick.
     *
     * `migration` is the twelve months of per-tier wage history behind the
     * decline test. It has to be carried for the reason every other flow in this
     * codebase has to be: a streak cannot be reconstructed from the month it
     * ended in. Without it a reloaded city forgets that its steel industry has
     * been dying since spring and cannot shed a single resident for a year.
     */
    private double[] cohorts;
    private double[] families;
    private double[] migration;

    /**
     * The people out of work: the EI claims by the month they began, who is
     * past EI, who has been evicted, and the month the flows were struck
     * against (2026-09-11). STOCKS - a claim eleven months old is one month
     * from ending, and a city that forgot it would hand everybody a fresh
     * year. Null in a save from before, which Unemployment takes as its first
     * month.
     */
    private double[] unemployment;

    /**
     * Who has been sick how long: five rings of thirteen monthly shares, the
     * last month's deaths from sickness by band, the recovery and whether the
     * ring has been seeded (2026-09-11). A STOCK - somebody two months sick is
     * a month from being able to die of it. Null in a save from before, which
     * Sickness seeds at its steady state on the first month back.
     */
    private double[] sickness;

    /**
     * The month's sickness: the outbreak still decaying, and the rate the
     * sectors were throttled by.
     *
     * A decaying outbreak is a streak by another name, and the same rule
     * applies - it cannot be rebuilt from the month it ended in. Worse, the
     * outbreak roll is a pure function of the month number, so a city that
     * forgot its severity on load would re-roll nothing and simply walk out of
     * the epidemic. Null in a save from before healthcare, which Health.restore
     * takes as "start well".
     */
    private double[] health;

    /**
     * The health service: plots used, the unburied backlog, and the month's
     * bill.
     *
     * The first two are STOCKS and are the reason this exists. A reloaded city
     * that forgot its plots would resurrect a century of graves and hand the
     * player a cemetery that never fills; one that forgot its backlog would
     * walk out of an epidemic of its own making. Null in a save from before
     * healthcare had books, which Healthcare.restore() refuses whole - leaving
     * a city with empty graveyards, which is exactly what those saves were.
     */
    private double[] healthcare;

    /**
     * The minimum wage, then the eleven wages the city is actually paying.
     *
     * A DAMPED PRICE CANNOT BE RECOMPUTED. LabourMarket walks each wage a
     * fraction of the way toward its target every month, so today's wage is the
     * result of every month of scarcity the city has been through. Rebuild it
     * from today's posts and workers and you get the TARGET - the number the
     * live city was still walking toward - and a reloaded city jumps to it
     * while the live one has not arrived. Measured as a $52 discrepancy in next
     * month's income before this was carried.
     *
     * Same rule that put the health array here: a flow cannot be reconstructed
     * from the state a month ended in, and neither can a lagged one.
     */
    private double[] labour;

    /**
     * How many skilled workers the city has, by band. Index 0 unused.
     *
     * A STOCK, and the purest kind. Until schools exist a skill arrives in
     * somebody's head and leaves the same way, so this number is the entire
     * history of who has moved to this city - nothing in the state a month
     * ended in can reproduce it. A save that forgot it would reload a city
     * whose hospitals had no doctors and whose mills had no engineers, all of
     * whom were there a moment ago.
     */
    private double[] skilledWorkforce;

    /**
     * Who is licensed to practise what, and the schools' running total.
     *
     * BOTH ARE STOCKS. A medical licence is a fact about a person that took
     * seven years to acquire and does not expire, so nothing in the state a
     * month ended in reproduces it - a save that forgot it would reload a city
     * whose hospitals had no doctors and whose medical school had apparently
     * never graduated anybody, all of whom were there a moment ago.
     *
     * The education array is the tuition subsidy plus everyone the city has
     * ever put through school by band. The subsidy is a policy the player set;
     * the totals are the only honest answer to "is this working", because forty
     * graduates a month is impressive or pitiful depending entirely on how long
     * it has been going.
     */
    private double[] licences;
    private double[] education;

    public void setLicences(double[] a)  { this.licences = a; }
    public double[] getLicences()        { return licences; }
    public void setEducation(double[] a) { this.education = a; }
    public double[] getEducation()       { return education; }

    public void setLabour(double[] a)           { this.labour = a; }
    public double[] getLabour()                 { return labour; }
    public void setSkilledWorkforce(double[] a) { this.skilledWorkforce = a; }
    public double[] getSkilledWorkforce()       { return skilledWorkforce; }

    public void setCohorts(double[] a)  { this.cohorts = a; }
    public double[] getCohorts()        { return cohorts; }
    public void setFamilies(double[] a) { this.families = a; }
    public double[] getFamilies()       { return families; }
    public void setMigration(double[] a){ this.migration = a; }
    public double[] getMigration()      { return migration; }
    public void setUnemployment(double[] a) { this.unemployment = a; }
    public double[] getUnemployment()       { return unemployment; }
    public void setSickness(double[] a)     { this.sickness = a; }
    public double[] getSickness()           { return sickness; }
    public void setHealth(double[] a)   { this.health = a; }
    public double[] getHealth()         { return health; }
    public void setHealthcare(double[] a){ this.healthcare = a; }
    public double[] getHealthcare()      { return healthcare; }

    /*
     * The private sector's memory, and the player's own turn.
     *
     * `sectorLossMonths` is how long each sector has been losing money -
     * retirement needs six in a row, so forgetting it across a load reset the
     * clock and made scrapping capacity save-scummable. `populationTrend` is the
     * twelve-month window the retail and industry planners forecast from.
     *
     * The last three accumulate DURING the player's turn and are read by next
     * month's national accounts. Save mid-turn without them and a building the
     * player just paid for vanishes from GDP - which also moves the city's
     * borrowing rate, since the debt market prices off GDP.
     */
    private java.util.Map<String, Integer> sectorLossMonths;
    private java.util.List<Integer> populationTrend;
    private double cityCapitalSpending;
    private double monthlyMaterialImports;
    /**
     * The same imports in money, at the price each was charged at - the
     * builders' materials expense and the accounts' import line. Absent from
     * a save written before 2026-09-10; Game rebuilds it from the count at the
     * old unit for that one month. See Game.monthlyMaterialImportBill.
     */
    private double monthlyMaterialImportBill;
    private int materialsConsumed;

    public void setSectorLossMonths(java.util.Map<String, Integer> m){ this.sectorLossMonths = m; }
    public java.util.Map<String, Integer> getSectorLossMonths(){ return sectorLossMonths; }
    public void setPopulationTrend(java.util.List<Integer> l){ this.populationTrend = l; }
    public java.util.List<Integer> getPopulationTrend(){ return populationTrend; }
    public void setCityCapitalSpending(double v){ this.cityCapitalSpending = v; }
    public double getCityCapitalSpending(){ return cityCapitalSpending; }

    /**
     * What the treasury paid the builders to keep the city's own buildings up.
     *
     * A FLOW, struck inside the tick, so it cannot be rebuilt from the stock a
     * month ended with - the standing buildings say what the bill WOULD be, not
     * what was settled, and those differ for a month after anything is built or
     * pulled down. Without it a reloaded city's Government screen reads $0
     * repairs beside a treasury that had just paid millions, which is the
     * fourteen-readings-at-zero bug the load path was audited for once already.
     *
     * NOT a SAVE_FORMAT change: Gson leaves an absent field alone, so a save
     * written before 2026-09-09 loads with zero here - and zero is exactly what
     * those cities paid, because nothing but housing was billed then.
     */
    private double cityMaintenancePaid;

    public void setCityMaintenancePaid(double v){ this.cityMaintenancePaid = v; }
    public double getCityMaintenancePaid(){ return cityMaintenancePaid; }
    public void setMonthlyMaterialImports(double v){ this.monthlyMaterialImports = v; }
    public double getMonthlyMaterialImports(){ return monthlyMaterialImports; }
    public void setMonthlyMaterialImportBill(double v){ this.monthlyMaterialImportBill = v; }
    public double getMonthlyMaterialImportBill(){ return monthlyMaterialImportBill; }
    public void setMaterialsConsumed(int v){ this.materialsConsumed = v; }
    public int getMaterialsConsumed(){ return materialsConsumed; }

    /**
     * What each protected sector was paid last month.
     *
     * A flow the load path cannot re-derive - see Game.paySubsidyIfOwed(). Null
     * on an older save, which restores as the zeros those cities showed anyway.
     */
    private java.util.Map<String, Double> subsidyPaid;
    public void setSubsidyPaid(java.util.Map<String, Double> v){ this.subsidyPaid = v; }
    public java.util.Map<String, Double> getSubsidyPaid(){ return subsidyPaid; }

    /**
     * The residents' month: twelve scalars and eleven per-tier arrays.
     *
     * Carried rather than rebuilt - see HouseholdAccounts.getStatementState().
     * Null on an older save, which keeps whatever the rebuild produced, which
     * is what those cities always showed.
     */
    private double[] householdStatement;
    public void setHouseholdStatement(double[] v){ this.householdStatement = v; }
    public double[] getHouseholdStatement(){ return householdStatement; }

    public void setBuilds(java.util.List<BuildLog.Entry> entries) {
        this.builds = entries;
    }
    public java.util.List<BuildLog.Entry> getBuilds() { return builds; }

    public void setWriteOffTotals(java.util.Map<String, Double> totals) {
        this.writeOffTotals = totals;
    }
    public java.util.Map<String, Double> getWriteOffTotals() { return writeOffTotals; }

    public void setRestructureCounts(java.util.Map<String, Integer> counts) {
        this.restructureCounts = counts;
    }
    public java.util.Map<String, Integer> getRestructureCounts() { return restructureCounts; }

    public void setBlockedMonths(java.util.Map<String, Integer> months) {
        this.blockedMonths = months;
    }
    public java.util.Map<String, Integer> getBlockedMonths() { return blockedMonths; }

    public void setNationalAccounts(double[] state) { this.nationalAccounts = state; }
    public double[] getNationalAccounts()           { return nationalAccounts; }

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
    
    public int getPopulation(){
        return population;
    }
    public boolean getReports(){
        return reports;
    }
    public java.util.List<Notice> getNotices(){
        return notices;
    }
    public boolean getGraphs(){
        return graphs;
    }
}
