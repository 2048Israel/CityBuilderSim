package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The private economy: every sector, every market, the credit desk, the
 * tax policy and the national accounts, and the month they run in.
 *
 * SINCE THE SECTOR TEMPLATE (2026-09-11) this class holds a REGISTRY and
 * loops it. It used to hold five handlers by name and loop them by hand in
 * eleven places - pricing credit, charging property tax, pushing balance
 * sheets, settling loans, striking VAT, banking the month, restoring a save
 * - and each of those was a place a seventh sector would have been
 * forgotten. See Sectors, Sector and claude/the-sector-template.md.
 *
 * WHAT IS STILL HERE BY NAME: the two housing figures the landlords need
 * from the family model and the land office (see updateHousing()), and
 * the shops' budget constraint, which Game hands in from the households.
 * Both are inputs to a sector that is not a factory, and they arrive here
 * because this class already talks to everything that produces them.
 *
 * @author Jerus
 */
public class EconomyManager {

    /* ===================================================================
       THE CITY'S PARTS
       =================================================================== */

    private final BuildingManager buildingManager;
    private final Markets markets = new Markets();
    private final Sectors sectors;
    private final BusinessDebtManager businessDebtManager;
    private final TaxPolicy taxPolicy = new TaxPolicy();
    private final SalesTaxLedger salesTaxLedger = new SalesTaxLedger();
    private final NationalAccounts nationalAccounts = new NationalAccounts();

    public EconomyManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
        this.sectors = new Sectors(buildingManager, markets);
        this.businessDebtManager = new BusinessDebtManager();
        businessDebtManager.setSectors(sectors.keys());
    }

    public Sectors getSectors()                          { return sectors; }
    public Markets getMarkets()                          { return markets; }
    public GoodsMarket getMarket(Good g)                 { return markets.get(g); }
    public BusinessDebtManager getBusinessDebtManager()  { return businessDebtManager; }
    public TaxPolicy getTaxPolicy()                      { return taxPolicy; }
    public SalesTaxLedger getSalesTaxLedger()            { return salesTaxLedger; }
    public NationalAccounts getNationalAccounts()        { return nationalAccounts; }
    public BuildingManager getBuildingManager()          { return buildingManager; }

    /** A sector by its saved name, or null. */
    public Sector sector(String key) { return sectors.byKey(key); }

    /* ===================================================================
       WHAT THE CITY TELLS THE ECONOMY EACH MONTH
       =================================================================== */

    private double cash;
    private int totalJobs;
    private int population;
    private int households;
    private double totalWage;
    private final double[] fillRate = new double[11];

    /** What ONE job of each tier costs a month, as PopulationManager last set it. */
    private final double[] wageRates = new double[11];

    /** The wage bill per job type, staffed, for the banded wage tax. */
    private double[] staffedWagePerType = new double[JobType.values().length];

    /** The bank's posts, so its tellers can be charged to the bank and not to the shops. */
    private int[] bankJobs;

    public void setTotalJobs(int jobs)        { totalJobs = jobs; }
    public void setPopulation(int pop)        { population = pop; }
    public void setHouseholds(int houseCap)   { households = houseCap; }
    public void setCash(int money)            { cash = money; }
    public void setTotalWage(double wage)     { totalWage = wage; }
    public void setWageDetail(double[] staffedPerType) {
        if (staffedPerType != null) this.staffedWagePerType = staffedPerType.clone();
    }
    public double[] getStaffedWagePerType()   { return staffedWagePerType.clone(); }
    public int getPopulation()                { return population; }

    public void updateJobFillRate(double[] fill) {
        if (fill == null) return;
        System.arraycopy(fill, 0, fillRate, 0, Math.min(fill.length, fillRate.length));
        for (Sector s : sectors.all()) s.updateJobFillRate(fillRate);
    }

    /**
     * The wage schedule, handed to every sector with its own posts.
     *
     * @param wagePerType what one post of each tier costs a month
     * @param bankPosts   the bank's posts, which belong to no sector
     */
    public void updateWages(double[] wagePerType, int[] bankPosts) {
        if (wagePerType == null) return;
        System.arraycopy(wagePerType, 0, wageRates, 0, Math.min(wagePerType.length, wageRates.length));
        this.bankJobs = bankPosts;
        for (Sector s : sectors.all()) {
            s.updateJobFillRate(fillRate);
            s.updateWages(wageRates, s.postsPerTier());
        }
    }

    /** A copy, so nothing downstream can quietly rewrite the schedule. */
    public double[] getWageRates() { return wageRates.clone(); }

    /**
     * What the bank's own staff cost this month. A Commercial Bank is a
     * COMMERCIAL building with no sector, so its posts are in nobody's
     * payroll but the bank's - charged by Game through Bank.payRunning().
     */
    public double getBankPayroll() {
        if (bankJobs == null) return 0;
        double total = 0;
        int n = Math.min(bankJobs.length, wageRates.length);
        for (int i = 0; i < n; i++) total += bankJobs[i] * wageRates[i] * fillRate[i];
        return total;
    }

    /* ----------------------------- utilities ----------------------------- */

    private double pricePerWatt;
    private double pricePerWaterUnit;

    public double getPricePerWatt()      { return pricePerWatt; }
    public double getPricePerWaterUnit() { return pricePerWaterUnit; }

    public void setPricePerWatt(double price) {
        pricePerWatt = price;
        for (Sector s : sectors.all()) s.setPricePerWatt(price);
    }

    public void setPricePerWaterUnit(double price) {
        pricePerWaterUnit = price;
        for (Sector s : sectors.all()) s.setPricePerWaterUnit(price);
    }

    public void setEnergyRatio(double ratio) { for (Sector s : sectors.all()) s.setEnergyRatio(ratio); }
    public void setWaterRatio(double ratio)  { for (Sector s : sectors.all()) s.setWaterRatio(ratio); }
    /** The road network's throughput, handed to everyone whose goods move on it. */
    public void setRoadRatio(double ratio)   { for (Sector s : sectors.all()) s.setRoadRatio(ratio); }

    /**
     * ...and the same thing done properly, once the streams exist: each sector
     * feels the congestion IT generates.
     *
     * A contact centre is three hundred people and nothing shipped. A steel
     * mill is 83% ore. They stand in the same city on the same roads and they
     * do not have the same problem - the office's people can be on a tram
     * while the mill's lorries sit in the same jam they always did - and a
     * single city-wide ratio could not say so. The mix is the sector's own
     * buildings, which is the only honest measure of what it puts on the road.
     *
     * A SECTOR WITH NOTHING STANDING READS ONE, not zero: it is not being held
     * up by traffic, it simply is not there.
     */
    public void setRoadRatio(InfrastructureManager roads, BuildingManager buildings) {
        if (roads == null || buildings == null) return;
        for (Sector s : sectors.all()) {
            double[] mix = new double[Traffic.values().length];
            for (Traffic stream : Traffic.values()) {
                final Traffic t = stream;
                mix[t.ordinal()] = buildings.totalBySector(s.key(), b -> b.loadOf(t));
            }
            s.setRoadRatio(roads.throughputFor(mix));
        }
    }
    /** What is left of the workforce once this month's illness is taken off. Never touches payroll. See Health. */
    public void setHealthRatio(double ratio) { for (Sector s : sectors.all()) s.setHealthRatio(ratio); }

    /** What the sectors are currently running at, for the harnesses. */
    public double getHealthRatio() { return sectors.retail().getHealthRatio(); }

    /**
     * Every sector's draw, off its own buildings. Billed to the sectors that
     * draw it, water exactly as power is.
     *
     * EXCEPT THE HOMES. A house draws power and water and nobody is billed
     * for it - the tenants are not charged for utilities in this model, and
     * the landlords never were either (the old billing list was four
     * categories, and RESIDENTIAL was not one of them). Charging the
     * landlords for 40,000 houses' worth of water would be a rent rise
     * dressed as a utility bill. A household utility bill is a change to the
     * household model, not to the sectors, and it is on the list.
     */
    public void setElectricityConsumption() {
        for (Sector s : sectors.all()) {
            s.setElectricityConsumption(buildingManager.totalBySector(s.key(),
                    t -> BuildingsTemplate.isBilledForUtilities(t) ? t.getElectricityConsumption() : 0));
            s.setWaterConsumption(buildingManager.totalBySector(s.key(),
                    t -> BuildingsTemplate.isBilledForUtilities(t) ? t.getWaterConsumption() : 0));
        }
    }

    /** The month's power bills, as the sectors' statements booked them. */
    public double getSectorElectricityCharges() {
        double total = 0;
        for (Sector s : sectors.all()) total += s.statement().electricity;
        return total;
    }

    /** The month's water bills, likewise. */
    public double getSectorWaterCharges() {
        double total = 0;
        for (Sector s : sectors.all()) total += s.statement().water;
        return total;
    }

    /* ----------------------------- the world ----------------------------- */

    /**
     * Every world price in the game is quoted by the world in ITS money and
     * converted at this rate - times the world's own price level, which is
     * how world inflation rides in. Pushed in by Game.startOfMonthUpdate()
     * before anything is priced.
     */
    private double exchangeRate = 1.0;

    public void setExchangeRate(double rate) {
        this.exchangeRate = rate > 0 ? rate : 1.0;
        markets.setExchangeRate(this.exchangeRate);
        buildingManager.setExchangeRate(this.exchangeRate);
    }

    public double getExchangeRate() { return exchangeRate; }

    /* ------------------------------ the land ------------------------------ */

    /**
     * What the city currently charges for a square foot, so assessed values
     * track the price the player sets. A real reassessment with teeth both
     * ways: raising the land price raises what every landowner owes.
     */
    private double landPricePerSqFt;

    public void setLandPricePerSqFt(double price) { this.landPricePerSqFt = price; }
    public double getLandPricePerSqFt()           { return landPricePerSqFt; }

    /** What a sector's land is worth at the city's current price. */
    public double landValueOf(Sector s) {
        return buildingManager.getLandSqFtBySector(s.key()) * landPricePerSqFt;
    }

    /** What a city-owned category's land is worth. */
    public double landValueOf(BuildingType category) {
        return buildingManager.getLandSqFtByCategory(category) * landPricePerSqFt;
    }

    /* ===================================================================
       HOUSING - what the landlords need from the family model and the land office
       =================================================================== */

    private double householdCount;
    private double marginalHousingCost;
    private double occupiedHomes;
    private double studioSeekers, familySeekers, studioSeekerHeads, familySeekerHeads;

    public void setHouseholdCount(double count)       { this.householdCount = count; }
    public void setMarginalHousingCost(double perCap) { this.marginalHousingCost = perCap; }
    public double getMarginalHousingCost()            { return marginalHousingCost; }
    public void setOccupiedHomes(double occupied)     { this.occupiedHomes = occupied; }
    public void setHousingSeekers(double studio, double family, double studioHeads, double familyHeads) {
        this.studioSeekers = Math.max(0, studio);
        this.familySeekers = Math.max(0, family);
        this.studioSeekerHeads = Math.max(0, studioHeads);
        this.familySeekerHeads = Math.max(0, familyHeads);
    }
    public double getStudioSeekers() { return studioSeekers; }
    public double getFamilySeekers() { return familySeekers; }

    /**
     * Hands the landlords and the shops what the month looks like: how many
     * people there are, how many doors, who is chasing them and what a new
     * home costs. SET here; the rent WALKS at the bottom of the month, in
     * the sector's own endOfMonth() - setting state is not advancing it.
     */
    public void updateHousing() {
        var re = sectors.realEstate();
        re.setPopulation(population);
        re.setHousehold(households);
        re.setHomes(buildingManager.getTotalHomes());
        re.setOccupiedHomes(occupiedHomes);
        re.setHouseholdCount(householdCount);
        re.setMarginalHousingCost(marginalHousingCost);
        repriceHousingCosts();

        int[] bySize = buildingManager.homesBySize();
        int studioDoors = 0, familyDoors = 0;
        for (int size = 1; size < bySize.length; size++) {
            if (size <= FamilyModel.STUDIO_MAX_SIZE) studioDoors += bySize[size];
            else                                     familyDoors += bySize[size];
        }
        re.setSegments(studioDoors, familyDoors, studioSeekers, familySeekers,
                studioSeekerHeads, familySeekerHeads);

        sectors.retail().setPopulation(population);
    }

    /**
     * What one of these has to earn a month before it is worth putting up.
     *
     * ZERO PROFIT, PLUS A MARGIN. Jerus: "theyll rent at zero profit, but
     * they wont build more if new rent is zero profit." Maintenance on the
     * structure, property tax on the whole asset with its plot, and then a
     * margin. NO INTEREST IN THIS NUMBER: whether the building pays is this
     * question; whether the company can carry the loan is
     * servicesItsOwnDebt()'s, and asking it twice answered no both times.
     */
    public double housingBuildHurdle(BuildingsTemplate t) {
        if (t == null || buildingManager == null) return 0;
        double structure = t.getCashCost()
                + t.getConstructionMaterials() * Math.max(0, buildingManager.getConstructionMaterialPrice());
        double land = t.getLandSqFt() * Math.max(0, landPricePerSqFt);
        double full = structure + land;
        if (full <= 0) return 0;
        double monthlyPropertyRate = taxPolicy.effectiveMonthlyPropertyRate(sectors.realEstate());
        double carry = structure * ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12
                + full * monthlyPropertyRate;
        return carry * (1 + ham.citybuildersim.sectors.RealEstate.BUILD_MARGIN);
    }

    /** The cheapest way to house one more person, land included, per segment. */
    public void repriceHousingCosts() {
        var re = sectors.realEstate();
        double materialPrice = Math.max(0, buildingManager.getConstructionMaterialPrice());
        double bestFull = 0, bestStructure = 0, bestLand = 0, bestStudio = 0, bestFamily = 0;
        for (BuildingsTemplate t : buildingManager.getTemplates()) {
            if (t == null || !re.key().equals(t.getSector()) || t.getCapacity() <= 0) continue;
            double structure = (t.getCashCost() + t.getConstructionMaterials() * materialPrice) / t.getCapacity();
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
        re.setHousingCosts(bestStructure, bestLand);
        re.setSegmentHousingCosts(bestStudio, bestFamily);
        re.setOwnedHousingCapacity(buildingManager.getCapacityInPortfolioBySector(re.key()));
    }

    /* ===================================================================
       CASH BY NAME - the seam almost everything routes through
       =================================================================== */

    public double getSectorCash(String key) {
        Sector s = sectors.byKey(key);
        return s == null ? 0 : s.getCash();
    }

    public void setSectorCash(String key, double cash) {
        Sector s = sectors.byKey(key);
        if (s != null) s.setCash(cash);
    }

    /* ===================================================================
       THE MONTH, AT THE TOP
       =================================================================== */

    /**
     * Prices this month's business credit and hands every sector its
     * interest bill BEFORE the statements run, so the figures they bank are
     * net of it.
     */
    public void updateBusinessCredit(double governmentRate) {
        businessDebtManager.setRiskFreeRate(governmentRate);
        refreshCreditAssets();
        businessDebtManager.updateRates();
        for (Sector s : sectors.all()) {
            s.setInterestExpense(businessDebtManager.getMonthlyInterest(s.key()));
        }
        pushBalanceSheetInputs();
    }

    /**
     * Tells the credit manager what each sector is worth right now - its
     * own sheet plus what it holds abroad, which a lender that ignored
     * would call a rich sector broke.
     */
    public void refreshCreditAssets() {
        for (Sector s : sectors.all()) {
            businessDebtManager.setAssets(s.key(),
                    s.getBalanceSheet().getTotalAssets() + getForeignAssets(s.key()));
            businessDebtManager.setCash(s.key(), s.getCash());
        }
    }

    /** Book values and outstanding debt, refreshed onto each set of books. Buildings finished AND on site. */
    public void pushBalanceSheetInputs() {
        for (Sector s : sectors.all()) {
            s.setBalanceSheetInputs(landValueOf(s),
                    buildingManager.getBuildingsValueBySector(s.key()),
                    businessDebtManager.getPrincipal(s.key()));
        }
    }

    /* ------------------------------ property tax ------------------------------ */

    private double totalPropertyTax;
    private final Map<String, Double> propertyTaxBySector = new LinkedHashMap<>();

    /**
     * Land at today's price plus FINISHED buildings at replacement cost. Sites
     * are not assessed.
     *
     * THE LAND HALF IS SCALED BY WHAT THE ROLL ACTUALLY CARRIES (2026-09-13),
     * which is one for every sector but the fields. A farm's ground is worth
     * what a developer would pay for it and grows what a farmer can grow on it,
     * and taxing the first drives out the second long before the city reaches
     * the fence - so the player has a dial. See TaxPolicy.assessedLandShare.
     */
    public double getAssessedValue(Sector s) {
        return landValueOf(s) * taxPolicy.assessedLandShare(s.key())
                + buildingManager.getBookValueBySector(s.key());
    }

    /** The same for a city-owned category, which the city does not tax but a screen may want to show. */
    public double getAssessedValue(BuildingType category) {
        return landValueOf(category) + buildingManager.getBookValueByCategory(category);
    }

    /** One month's property tax on a sector, at its own rate. */
    public double getPropertyTaxFor(Sector s) {
        return taxPolicy.propertyTaxOn(getAssessedValue(s), s.key());
    }

    /**
     * Hands each sector its property tax bill for the month. Runs with the
     * interest bill, before the statements, for the same reason: what a
     * sector banks has to already be net of what it owes.
     */
    public void chargePropertyTax() {
        totalPropertyTax = 0;
        propertyTaxBySector.clear();
        for (Sector s : sectors.all()) {
            double bill = getPropertyTaxFor(s);
            s.setPropertyTaxExpense(bill);
            propertyTaxBySector.put(s.key(), bill);
            totalPropertyTax += bill;
        }
    }

    public double getTotalPropertyTax()            { return totalPropertyTax; }
    public void setTotalPropertyTax(double value)  { this.totalPropertyTax = value; }
    public double getPropertyTaxCharged(String key){ return propertyTaxBySector.getOrDefault(key, 0.0); }

    /* ------------------------------ maintenance ------------------------------ */

    /**
     * EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. Jerus: "all
     * buildings need maintenance, and make sure they get billed." A real
     * order with the builders - money, materials and points - priced in the
     * building's own inputs at today's material price, one percent a year of
     * what it cost to put up. The sectors bear their own; the city bears
     * the buildings nobody else owns; the bank bears its branches.
     */
    private static final BuildingType[] CITY_MAINTAINED = {
        BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE,
        BuildingType.HEALTHCARE,  BuildingType.EDUCATION, BuildingType.SAFETY
    };

    private double maintenanceBillTotal;
    private double maintenanceMaterialsTotal;
    private double maintenancePointsTotal;
    private double cityMaintenanceBill;
    private double bankMaintenanceBill;
    private final Map<String, Double> maintenanceBySector = new LinkedHashMap<>();

    private static final double MAINTENANCE_RATE = ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12;

    /** The month's repair bill for one sector, in money - materials priced in. */
    public double maintenanceBillFor(Sector s, double materialPrice) {
        double cash = buildingManager.totalBySector(s.key(), BuildingsTemplate::getCashCost) * MAINTENANCE_RATE;
        double mats = buildingManager.totalBySector(s.key(), BuildingsTemplate::getConstructionMaterials) * MAINTENANCE_RATE;
        double bill = cash + mats * Math.max(0, materialPrice);
        return Double.isFinite(bill) && bill > 0 ? bill : 0;
    }

    /** The same for a city-owned category. */
    public double maintenanceBillFor(BuildingType category, double materialPrice) {
        double cash = buildingManager.getTotalByCategoryDouble(category, t -> t.isOwnedBySector() ? 0 : t.getCashCost()) * MAINTENANCE_RATE;
        double mats = buildingManager.getTotalByCategoryDouble(category, t -> t.isOwnedBySector() ? 0 : t.getConstructionMaterials()) * MAINTENANCE_RATE;
        double bill = cash + mats * Math.max(0, materialPrice);
        return Double.isFinite(bill) && bill > 0 ? bill : 0;
    }

    /** The material UNITS the whole city's repairs consume this month. */
    public double maintenanceMaterialsTotal() {
        double mats = buildingManager.getTotalDouble(BuildingsTemplate::getConstructionMaterials) * MAINTENANCE_RATE;
        return Double.isFinite(mats) && mats > 0 ? mats : 0;
    }

    /** The builders' time the whole city's repairs consume this month. */
    public double maintenancePointsTotal() {
        double pts = buildingManager.getTotalDouble(BuildingsTemplate::getConstructionPoints) * MAINTENANCE_RATE;
        return Double.isFinite(pts) && pts > 0 ? pts : 0;
    }

    /**
     * Hands every owner its repair bill for the month. No money moves here:
     * the figure is assigned, each income statement subtracts it, and what
     * a sector banks is already net of it. Game.chargeBuildingMaintenance()
     * owns the other half - the materials really consumed, the builders
     * really paid.
     *
     * @param materialPrice what a unit of construction material costs today
     */
    public void chargeMaintenance(double materialPrice) {
        maintenanceBySector.clear();
        maintenanceBillTotal = 0;
        cityMaintenanceBill = 0;
        bankMaintenanceBill = 0;

        for (Sector s : sectors.all()) {
            double bill = maintenanceBillFor(s, materialPrice);
            s.setMaintenanceExpense(bill);
            maintenanceBySector.put(s.key(), bill);
            maintenanceBillTotal += bill;
        }
        for (BuildingType category : CITY_MAINTAINED) {
            double bill = maintenanceBillFor(category, materialPrice);
            cityMaintenanceBill += bill;
            maintenanceBillTotal += bill;
        }
        // ...and the bank's branches, which are commercial buildings nobody in the private sector owns.
        BuildingsTemplate branch = buildingManager.getTemplateByName("Commercial Bank");
        if (branch != null) {
            int n = buildingManager.getQuantity(branch.getId());
            double bill = n * (branch.getCashCost() + branch.getConstructionMaterials() * Math.max(0, materialPrice)) * MAINTENANCE_RATE;
            if (Double.isFinite(bill) && bill > 0) {
                bankMaintenanceBill = bill;
                maintenanceBillTotal += bill;
            }
        }
        maintenanceMaterialsTotal = maintenanceMaterialsTotal();
        maintenancePointsTotal = maintenancePointsTotal();
    }

    /** Every owner's repair bill added up - what the builders are owed. */
    public double getMaintenanceBillTotal()      { return maintenanceBillTotal; }
    /** The share of it the treasury pays, because the city owns those buildings. */
    public double getCityMaintenanceBill()       { return cityMaintenanceBill; }
    /** The share the bank pays, for its branches. */
    public double getBankMaintenanceBill()       { return bankMaintenanceBill; }
    /** Material UNITS, not money - the yard has to find these. */
    public double getMaintenanceMaterialsTotal() { return maintenanceMaterialsTotal; }
    /** Builders' time, which comes straight off what the city can put up. */
    public double getMaintenancePointsTotal()    { return maintenancePointsTotal; }
    /** One sector's bill, for the screens. */
    public double getMaintenanceCharge(String key){ return maintenanceBySector.getOrDefault(key, 0.0); }

    /* ------------------------------ the strike ------------------------------ */

    /**
     * Strikes every sector's statement, settles the VAT from the same
     * figures, and banks the month. Once, at the top of the month, after
     * the bills are set. See Sector.strike() and bank().
     */
    public void strikeSectors() {
        for (Sector s : sectors.all()) {
            s.setTaxRate(taxPolicy.effectiveProfitRate(s));
            s.strike();
        }
        settleSalesTax();
    }

    /**
     * The month's sales tax, STRUCK FROM THE TRADES. A sale to a local
     * buyer at the seller's rate; an export zero-rated; a purchase from a
     * local supplier credited at THE SUPPLIER's rate, which is what it
     * actually remitted; an import charged at the buyer's own. Housing is
     * an exempt supply, so the landlords' sales are neither charged nor
     * credited (Good.taxExempt()). This was eleven hand-written lines with
     * three known asymmetries; it is one loop now.
     */
    public double settleSalesTax() {

        salesTaxLedger.startMonth();

        for (Sector s : sectors.all()) {
            Sector.Statement st = s.statement();
            double taxable = st.localSales - exemptSales(s);
            salesTaxLedger.recordSales(s.key(), taxable + st.otherRevenue);
            salesTaxLedger.recordExport(s.key(), st.exports);
            for (Map.Entry<String, Double> e : st.purchasesBySupplier.entrySet()) {
                Sector supplier = sectors.byKey(e.getKey());
                double rate = supplier == null ? 0 : taxPolicy.effectiveSalesRate(supplier);
                salesTaxLedger.recordInputTax(s.key(), e.getValue() * rate);
            }
            salesTaxLedger.chargeImport(s.key(), st.imports, taxPolicy);
        }

        salesTax = salesTaxLedger.settle(taxPolicy);

        // AND THE SECTORS PAY IT, ON THEIR OWN INCOME STATEMENTS, and bank
        // the month net of both taxes - one movement, one place, one set of
        // numbers.
        for (Sector s : sectors.all()) s.bank(salesTaxLedger.getNet(s.key()));

        return salesTax;
    }

    /** The exempt part of a sector's local sales: what it sold of goods the tax never touches. */
    private double exemptSales(Sector s) {
        boolean anyExempt = false;
        for (Good g : s.goodsMade()) if (g.taxExempt()) anyExempt = true;
        if (!anyExempt) return 0;
        // A sector making only exempt goods sold nothing taxable; one making
        // both would need the split by good, which no sector does today.
        for (Good g : s.goodsMade()) if (!g.taxExempt()) return 0;
        return s.statement().localSales;
    }

    /** Whoever the city owes this month, or null. See SalesTaxLedger. */
    public String getDeepestRefundSector()        { return salesTaxLedger.deepestRefund(); }
    public double getSectorSalesTax(String key)   { return salesTaxLedger.getNet(key); }
    public double getSectorSalesTax(Sector s)     { return salesTaxLedger.getNet(s.key()); }

    /* ===================================================================
       INSOLVENCY, at the bottom of the investment step
       =================================================================== */

    private double overdraftForgivenThisMonth;
    private final Map<String, Double> overdraftForgivenBySector = new LinkedHashMap<>();
    private final Map<String, Double> overdraftForgivenThisMonthBySector = new LinkedHashMap<>();

    /**
     * End of the month: work out who is beyond saving and write their debt
     * down to what their assets support. THE OVERDRAFT A RESTRUCTURE
     * FORGAVE is put back into the sector's balance here - it arrives from
     * the creditors who ate it, and MoneyAudit declares it as such.
     *
     * @return total written off this month
     */
    public double settleInsolvency() {
        pushBalanceSheetInputs();
        refreshCreditAssets();
        businessDebtManager.advanceBlocks();
        double writtenOff = businessDebtManager.restructureInsolventSectors();

        overdraftForgivenThisMonth = 0;
        overdraftForgivenThisMonthBySector.clear();
        for (Sector s : sectors.all()) {
            double forgiven = businessDebtManager.takeOverdraftForgiven(s.key());
            overdraftForgivenThisMonthBySector.put(s.key(), forgiven);
            if (forgiven > 0) {
                s.addCash(forgiven);
                overdraftForgivenThisMonth += forgiven;
                overdraftForgivenBySector.merge(s.key(), forgiven, Double::sum);
                GameLog.note(String.format(
                        "%s went bankrupt: $%,.0fk of bills it could not pay were written off.",
                        s.key(), forgiven));
            }
        }
        return writtenOff;
    }

    public double getOverdraftForgiven() { return overdraftForgivenThisMonth; }
    public double getOverdraftForgivenThisMonth(String key) { return overdraftForgivenThisMonthBySector.getOrDefault(key, 0.0); }
    public double getOverdraftForgivenTotal(String key)     { return overdraftForgivenBySector.getOrDefault(key, 0.0); }

    /** Repay what matured, then borrow if that left the sector short. A maturing loan is usually rolled. */
    public void settleBusinessCredit(int month) {
        businessDebtManager.processMonth();
        for (Sector s : sectors.all()) {
            double cash = s.getCash() - businessDebtManager.takeMaturedPrincipal(s.key());
            double loss = Math.max(-s.getNetIncome(), 0);
            cash += businessDebtManager.coverShortfall(s.key(), cash, loss, month);
            s.setCash(cash);
        }
        pushBalanceSheetInputs();
    }

    /* ===================================================================
       WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS
       =================================================================== */

    private final Map<String, Double> depositInterestPaid = new LinkedHashMap<>();

    public void clearDepositInterest() { depositInterestPaid.clear(); }

    public void recordDepositInterest(String sector, double amount) {
        if (sector == null || !(amount > 0)) return;
        depositInterestPaid.merge(sector, amount, Double::sum);
    }

    public double getDepositInterestPaid(String sector) {
        return depositInterestPaid.getOrDefault(sector, 0.0);
    }

    /* ===================================================================
       THE OWNERS AND THE MONEY ABROAD
       =================================================================== */

    private OutwardInvestment outward;
    public void setOutwardInvestment(OutwardInvestment outward) { this.outward = outward; }
    public OutwardInvestment getOutwardInvestment() { return outward; }

    /** What one sector holds abroad, in the city's money at the rate it was last valued at. */
    public double getForeignAssets(String sector) {
        return outward == null ? 0 : outward.localValue(sector);
    }

    private Equity equity;
    public void setEquity(Equity equity) { this.equity = equity; }
    public Equity getEquity() { return equity; }

    private final Map<String, Double> equityRaised = new LinkedHashMap<>();
    private final Map<String, Double> dividendsPaid = new LinkedHashMap<>();
    private final Map<String, Double> sharesBoughtBack = new LinkedHashMap<>();

    public void clearEquityFlows() { equityRaised.clear(); dividendsPaid.clear(); sharesBoughtBack.clear(); stolen.clear(); }

    /** Taken from each sector's till by thieves this month. See Crime. Cleared with the equity flows, at the top of the month. */
    private final Map<String, Double> stolen = new LinkedHashMap<>();
    public void recordStolen(String sector, double amount) {
        if (sector == null || !(amount > 0)) return;
        stolen.merge(sector, amount, Double::sum);
    }
    public double getStolen(String sector) { return stolen.getOrDefault(sector, 0.0); }
    public void recordSharesBoughtBack(String sector, double amount) {
        if (sector == null || !(amount > 0)) return;
        sharesBoughtBack.merge(sector, amount, Double::sum);
    }
    public double getSharesBoughtBack(String sector) { return sharesBoughtBack.getOrDefault(sector, 0.0); }
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

    /* ===================================================================
       THE MONTH, IN THE MIDDLE AND AT THE BOTTOM
       =================================================================== */

    /** The economy's inputs for the month, set from the simulation. Capacities are read live off the buildings. */
    public void updateEcon() {
        updateHousing();
    }

    /**
     * The bottom of the month: every market clears, every maker produces.
     * See Markets.clearMonth(). NOT on the load path - a market clearing IS
     * a month passing, which is the trap the old refreshEconPrices() existed
     * to avoid.
     */
    public void finalEconUpdate(Game game) {
        markets.clearMonth(sectors, game);
        this.interest = 0;
        setElectricityConsumption();
    }

    /** What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. */
    public void refreshEconPrices() {
        setElectricityConsumption();
    }

    /* ===================================================================
       THE CITY'S BOOKS
       =================================================================== */

    private double salesTax;
    private double totalWageTax;
    private double totalBankTax;
    private double totalContributions;
    private double interest;
    private double utilityIncome = 0;
    private double debt = 0;
    private double GDP;
    private double yearGDP;

    public void setBankTax(double amount) { this.totalBankTax = Math.max(0, amount); }
    public double getBankTax()            { return totalBankTax; }

    /** The profit tax one sector paid this month, as its statement carries it. */
    public double getProfitTax(Sector s) { return s.getProfitTax(); }

    /** Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. */
    public double getBusinessTax() {
        double total = totalBankTax;
        for (Sector s : sectors.all()) if (s != sectors.industry()) total += s.getProfitTax();
        return total;
    }

    /** The food industry's, on its own line, as the screens have always shown it. */
    public double getIndustrialTax() { return sectors.industry().getProfitTax(); }
    public double getHeavyIndustryTax() { return sectors.heavyIndustry().getProfitTax() + sectors.mining().getProfitTax(); }
    public double getConstructionTax()  { return sectors.construction().getProfitTax(); }
    public double getSalesTax()   { return salesTax; }
    public double getWageTax()    { return totalWageTax; }
    public double getUtilityIncome() { return utilityIncome; }

    /** The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. */
    public double wageTaxOnPayroll(double[] payrollPerType) {
        return taxPolicy.wageTaxOn(payrollPerType, null);
    }

    public double getTaxIncome() {
        double profit = 0;
        for (Sector s : sectors.all()) profit += s.getProfitTax();
        // Banded, and summed job type by job type rather than as one total
        // times an average - averaging throws away the distinction the
        // bands exist to express.
        totalWageTax = taxPolicy.wageTaxOn(staffedWagePerType, null);
        // Pension contributions, taken off the same staffed wage bill. Revenue, and NOT a tax.
        totalContributions = SocialSecurity.contributionsOn(totalWage, taxPolicy.getContributionRate());
        // ...and the EI premium, off the same bill, the same way (2026-09-11).
        totalEiPremiums = Math.max(0, totalWage) * taxPolicy.getEiPremiumRate();
        /*
         * ...AND THE HEALTH PREMIUM (2026-09-19), off the same bill again:
         * every wage, employee side, no cap - the EI premium's own base (the
         * insurable cap is on EI's BENEFIT, not its premium). Revenue against
         * the health service's cost, with no automatic balancing: a surplus
         * or a shortfall is the treasury's, per Jerus. Zero at the default,
         * and x + 0.0 is x, so a city that charges none is the city it was.
         */
        totalHealthPremiums = Math.max(0, totalWage) * taxPolicy.getHealthPremiumRate();
        // Property tax and sales tax are NOT recomputed here: the sectors
        // were billed them and bore them, and the city collects the figure
        // the businesses paid.
        return profit + totalWageTax + salesTax + totalPropertyTax
                + totalContributions + totalEiPremiums + healthcareFees + educationFees
                + transitFares + totalBankTax + totalHealthPremiums;
    }

    /* ------------------- EI and the student grant (2026-09-11) ------------------- */

    private double totalEiPremiums;
    private double eiBenefits;
    private double studentGrants;

    /** The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). */
    private double totalHealthPremiums;

    /** What the health premium raised this month, employee side, off the whole wage bill. */
    public double getHealthPremiums() { return totalHealthPremiums; }

    /**
     * The interest the graduates paid the treasury on their student loans
     * this month (2026-09-21): a revenue line beside the premiums, set by
     * Game off the household ledger the month it is struck. NOT in
     * getTaxIncome(), because Game moves the cash for it where it moves the
     * principal - see syncHouseholdAccounts - and a line in both would arrive
     * twice; the budget carries it through NationalAccounts.
     */
    private double studentLoanInterest;

    /** Sets the month's student-loan interest, the treasury's. See Game.getStudentLoanInterest(). */
    public void setStudentLoanInterest(double interest) { this.studentLoanInterest = Math.max(0, interest); }

    /** What the graduates paid in interest on their student loans this month. */
    public double getStudentLoanInterest() { return studentLoanInterest; }

    /** The month's EI bill and grant bill, set by Game off Unemployment and the students before the cash moves. */
    public void setOutsidePayments(double eiBenefits, double studentGrants) {
        this.eiBenefits = Math.max(0, eiBenefits);
        this.studentGrants = Math.max(0, studentGrants);
    }

    public double getEiPremiums()    { return totalEiPremiums; }
    public double getEiBenefits()    { return eiBenefits; }
    public double getStudentGrants() { return studentGrants; }

    /** What the premiums cover of the EI bill. 1 when nobody is drawing it. */
    public double getEiCoverage() {
        return eiBenefits > 0 ? totalEiPremiums / eiBenefits : 1;
    }

    /* ----------------------------- the services ----------------------------- */

    private double healthcareBill, healthcareFees;
    public void setHealthcare(double grossCost, double fees) {
        this.healthcareBill = Math.max(0, grossCost);
        this.healthcareFees = fees;
    }
    public double getHealthcareBill() { return healthcareBill; }
    public double getHealthcareFees() { return healthcareFees; }
    public double getHealthcareNet()  { return healthcareBill - healthcareFees; }

    private double educationBill, educationFees;
    public void setEducation(double grossCost, double fees) {
        this.educationBill = Math.max(0, grossCost);
        this.educationFees = fees;
    }
    public double getEducationBill() { return educationBill; }
    public double getEducationFees() { return educationFees; }
    public double getEducationNet()  { return educationBill - educationFees; }

    /**
     * What the police and the prisons cost this month: payroll and upkeep, no
     * fees - nobody pays to be policed or jailed. See Crime.
     */
    private double safetyBill;
    public void setSafety(double grossCost) { this.safetyBill = Math.max(0, grossCost); }
    public double getSafetyBill() { return safetyBill; }

    /* =======================================================================
       THE TRANSIT BOOKS (2026-09-16)

       The fourth thing the city builds for itself, and the FIRST ONE THAT CAN
       PAY FOR ITSELF. Jerus: "yes riders pay, yes its adjustable, to the point
       you can even make it profitable by alot."

       That is deliberately unlike the other three. Nobody pays to be policed;
       a patient pays something and never enough; a school charges a fee that
       is a rounding error against its wage bill. A tram is different because
       the person on it chose to be, so a fare is a price rather than a charge,
       and a city that sets it high enough is running a business.

       AND THE PRICE HAS A COST, which is what stops it being free money: a
       fare high enough to turn a profit is a fare people will not pay, and
       everybody who will not pay it is back in a car on the road the transit
       was built to relieve. The revenue line and the congestion line move
       against each other, and that is the whole decision. See
       InfrastructureManager.ridershipAt() and TaxPolicy.getTransitFare().
       ======================================================================= */

    private double transitBill, transitFares;
    public void setTransit(double grossCost, double fares) {
        this.transitBill = Math.max(0, grossCost);
        this.transitFares = Math.max(0, fares);
    }
    public double getTransitBill()  { return transitBill; }
    public double getTransitFares() { return transitFares; }

    /** Negative when the fare more than covers the wages, which a player can arrange. */
    public double getTransitNet()   { return transitBill - transitFares; }

    /** What the city paid this month to hold protected sectors at break-even. Reporting only; the cash already left. */
    private double subsidiesPaid;
    public void setSubsidiesPaid(double v) { this.subsidiesPaid = Math.max(0, v); }
    public double getSubsidiesPaid()       { return subsidiesPaid; }

    /* ------------------------------ pensions ------------------------------ */

    private double seniors;
    public void setSeniors(double seniors)   { this.seniors = Math.max(0, seniors); }
    public double getSeniors()               { return seniors; }
    public double getContributions()         { return totalContributions; }
    public double getPensionsPaid()          { return Math.max(0, seniors) * taxPolicy.pensionPerSenior(); }
    public double getPensionShortfall()      { return Math.max(0, getPensionsPaid() - totalContributions); }
    public double getPensionCoverage() {
        double owed = getPensionsPaid();
        if (owed <= 0) return 1;
        return totalContributions / owed;
    }

    /** What the city pays out this month. */
    public double getExpenses() {
        return interest + getPensionsPaid() + eiBenefits + studentGrants + healthcareBill + educationBill
                + safetyBill;
    }

    /** The interest alone, which is the only part of getExpenses() that is CARRIED. */
    public double getInterestAccrued() { return interest; }
    public void setInterest(double value) { this.interest = value; }
    public void updateInterestExpense(double interest) { this.interest += interest; }

    public double getTotalIncome() { return getTaxIncome() - getExpenses(); }

    public void setUtilityIncome(double income) { this.utilityIncome = income; }
    public void setDebt(double debt)            { this.debt = debt; }

    /* ===================================================================
       THE NATIONAL ACCOUNTS
       =================================================================== */

    /**
     * Measures the month's output and the government's books, from the
     * statements the sectors have just struck.
     *
     * Only FINAL sales count: what the shops and the landlords sold to
     * households is C; a mill's sale to a shop is intermediate. Exports are
     * every sector's sales to the world; imports are every sector's
     * purchases from it, split into the three lines the screen has always
     * shown - food (the shops'), materials (the builders'), raw material
     * (everyone else's).
     */
    public void updateNationalAccounts(double constructionWorkDone,
                                       double governmentServices,
                                       double interest,
                                       double capitalSpending,
                                       double landSales,
                                       double landPurchases,
                                       double propertyTax) {

        var retail = sectors.retail();
        var construction = sectors.construction();
        /*
         * CONSUMPTION IS WHAT HOUSEHOLDS BOUGHT, AND THAT IS MORE THAN ONE
         * SECTOR NOW (2026-09-17).
         *
         * This read Retail alone, which was true for as long as Retail was the
         * only thing a household could buy from. The luxury shops broke it in
         * the worst possible way: their IMPORTS land in net exports, which is
         * in GDP, and their SALES landed nowhere - so a city that took up
         * shopping reported NEGATIVE GDP sixty times over a run, worst at
         * -4,193 with raw imports of 8,636 against exports of 1. The goods
         * arrived and nobody had bought them.
         *
         * Named rather than summed over every sector, because two of the
         * others sell to households and are not goods consumption: Real Estate
         * is the housing line right below, and Automotive's cars go through
         * the market rather than a shop. Anything new that sells over a
         * counter belongs here the day it is written.
         */
        double retailSales = retail.statement().salesToHouseholds
                + sectors.luxuryRetail().statement().salesToHouseholds
                // ...and a dinner, which is consumption bought over a counter
                // like the other two. See the note above.
                + sectors.restaurants().statement().salesToHouseholds;
        double rentPaid = sectors.realEstate().statement().salesToHouseholds;

        /*
         * THIRTEEN GOODS, AND STILL A VOLUME - WHICH IS THE WHOLE POINT.
         *
         * Kilograms of grain do not add to kilograms of fish, so the obvious
         * move is to value each good's stock at its own price and hand the
         * accounts the money. THAT IS THE BUG NationalAccounts.update() WARNS
         * AGAINST IN THE LINES THAT RECEIVE THIS: "a change in PRICE is neither
         * production nor consumption, and measuring the change in value rather
         * than in volume booked every price move as production". Done that way,
         * every wobble in the food market becomes output.
         *
         * So the thirteen are aggregated at FIXED WEIGHTS - each good's world
         * import price, which is a constant of the model and cannot move - to
         * make one volume index that behaves exactly as a count of loaves did.
         * The deflator beside it is what that volume costs in this city today,
         * so the accounts still multiply a change in volume by a price, which
         * is the shape the comment asks for.
         *
         * MATERIALS IS STILL ONE GOOD and is still counted in bricks.
         */
        double foodUnits = 0, foodWrittenOff = 0, foodAtLocal = 0;
        double materialUnits = 0;
        for (Sector s : sectors.all()) {
            for (Good fg : ham.citybuildersim.sectors.Retail.SHELF) {
                double held = s.getStock(fg) + s.getPantry(fg);
                foodUnits      += held * fg.worldImportPrice();
                foodWrittenOff += s.output(fg).writtenOff * fg.worldImportPrice();
                foodAtLocal    += held * Math.max(0, markets.get(fg).getLocalPrice());
            }
            materialUnits += s.getStock(Good.MATERIALS);
        }
        double foodPrice = foodUnits > 0 ? foodAtLocal / foodUnits : 0;
        double materialPrice = markets.get(Good.MATERIALS).getLocalPrice();

        /*
         * AND THE LUXURY SHELF, which is one good counted in pieces and needs
         * no weighting. Summed over every sector rather than off the boutiques
         * for the same reason the two above are: the accounts want the city's
         * stock, not one sector's, and a loop cannot forget a sector that
         * starts holding the good later. See the note in NationalAccounts for
         * why a month of imports read as negative output without this.
         */
        double luxuryUnits = 0;
        for (Sector s : sectors.all()) {
            luxuryUnits += s.getStock(Good.LUXURIES) + s.getPantry(Good.LUXURIES);
        }
        double luxuryPrice = Math.max(0, markets.get(Good.LUXURIES).getLocalPrice());

        double exports = 0, rawImports = 0;
        for (Sector s : sectors.all()) {
            exports += s.statement().exports;
            if (s != retail && s != construction) rawImports += s.statement().imports;
        }
        double foodImports = retail.statement().imports;
        double materialImports = construction.statement().imports;

        nationalAccounts.update(retailSales, rentPaid,
                constructionWorkDone,
                foodUnits, foodWrittenOff, foodPrice,
                materialUnits, materialPrice,
                luxuryUnits, luxuryPrice,
                governmentServices,
                foodImports, materialImports,
                rawImports, exports);

        nationalAccounts.updateGovernment(
                getBusinessTax(), getIndustrialTax(), salesTax, totalWageTax,
                utilityIncome, landSales, propertyTax,
                interest, capitalSpending, landPurchases,
                totalContributions, getPensionsPaid(),
                healthcareFees, healthcareBill,
                educationFees, educationBill,
                subsidiesPaid);
        nationalAccounts.setOutsideLines(totalEiPremiums, eiBenefits, studentGrants,
                totalHealthPremiums, studentLoanInterest);
        nationalAccounts.setSafetySpending(safetyBill);
        nationalAccounts.setTransitLines(transitBill, transitFares);

        GDP = nationalAccounts.getGdp();
    }

    /**
     * Repopulates the government's revenue and expenditure block after a
     * load, and nothing else - running the whole measure would be running
     * a month of the economy with the calendar standing still.
     */
    public void refreshGovernmentAccounts(double landSales, double capitalSpending,
                                          double landPurchases, double interestPaid) {
        getTaxIncome();
        nationalAccounts.updateGovernment(
                getBusinessTax(), getIndustrialTax(), salesTax, totalWageTax,
                utilityIncome, landSales, getTotalPropertyTax(),
                interestPaid, capitalSpending, landPurchases,
                totalContributions, getPensionsPaid(),
                healthcareFees, healthcareBill,
                educationFees, educationBill,
                subsidiesPaid);
        nationalAccounts.setOutsideLines(totalEiPremiums, eiBenefits, studentGrants,
                totalHealthPremiums, studentLoanInterest);
        nationalAccounts.setSafetySpending(safetyBill);
        nationalAccounts.setTransitLines(transitBill, transitFares);
    }

    public double getLastFoodVolume() { return nationalAccounts.getLastFoodVolume(); }

    /**
     * A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE.
     *
     * It counted units of FOOD until 2026-09-15 and holds a thirteen-good
     * volume index after it - still a volume, but weighted, so an old file's
     * loaf count read into it would put one month's inventory investment out
     * by the weighting. The index goes in a new slot at the tail and a file
     * without it reads zero, which is exactly the fallback a pre-13-slot file
     * already had.
     */
    public void restoreNationalAccounts(double[] a) {
        if (a == null || a.length < 11) return;
        boolean hasUnits = a.length >= 13;
        boolean hasFoodValue = a.length >= 14;
        /*
         * SLOT 14 IS THE LUXURY SHELF, appended the day the good was written
         * (2026-09-17) and read as zero by every file without it - which is
         * every file older than the sector, and those cities held none. Same
         * tail-append the food index got in slot 13, and for the same reason:
         * a reloaded city that thinks last month's stock was zero counts its
         * whole stockroom as this month's production.
         */
        boolean hasLuxury = a.length >= 15;
        nationalAccounts.restore(a[0], hasFoodValue ? a[13] : 0,
                a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
                hasUnits ? a[12] : 0, hasLuxury ? a[14] : 0, hasUnits);
    }

    public double[] getNationalAccountsState() {
        return new double[] {
            nationalAccounts.getGdp(),
            nationalAccounts.getLastFoodVolume(),
            nationalAccounts.getConsumptionGoods(),
            nationalAccounts.getConsumptionHousing(),
            nationalAccounts.getInvestmentConstruction(),
            nationalAccounts.getInvestmentInventories(),
            nationalAccounts.getGovernment(),
            nationalAccounts.getImportsFood(),
            nationalAccounts.getImportsMaterials(),
            nationalAccounts.getImportsRawMaterial(),
            nationalAccounts.getExports(),
            nationalAccounts.getLastFoodVolume(),
            nationalAccounts.getLastMaterialUnits(),
            nationalAccounts.getLastFoodVolume(),    // slot 13 - see restoreNationalAccounts()
            nationalAccounts.getLastLuxuryUnits()     // slot 14 - and the same note
        };
    }

    double[] governmentMonthToSave()          { return nationalAccounts.governmentToSave(); }
    void restoreGovernmentMonth(double[] m)   { nationalAccounts.restoreGovernment(m); }

    public double getMonthGdp() { return nationalAccounts.getGdp(); }
    public double getYearGdp()  { return yearGDP; }
    public double getGDP()      { return GDP; }
    public double getTaxRate()  { return taxPolicy.getIncomeTaxRate(); }

    public void setPreviousGdp(HistorySave historySave) {
        List<Double> gdp = new ArrayList<>(historySave.getGdp());
        int start = Math.max(0, gdp.size() - 11);
        List<Double> last11 = gdp.subList(start, gdp.size());
        yearGDP = nationalAccounts.getAnnualGdp();
        if (yearGDP == 0) {
            yearGDP = GDP;
            for (Double d : last11) yearGDP += d;
        }
    }

    /* ===================================================================
       CONVENIENCES - the prices the screens and the harnesses ask for by name
       =================================================================== */

    /** What one person-month of food costs the shops at today's market prices. */
    public double getFoodLocalPrice() { return sectors.retail().getFoodPrice(); }
    public double getIronLocalPrice() { return markets.get(Good.IRON).getLocalPrice(); }

    /** Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. */
    public int getFoodUnitsHeld() {
        double kg = 0;
        for (Sector s : sectors.all()) {
            for (Good fg : ham.citybuildersim.sectors.Retail.SHELF) kg += s.getStock(fg) + s.getPantry(fg);
        }
        return (int) Math.floor(kg);
    }

    /* ===================================================================
       SAVE AND RESTORE
       =================================================================== */

    public List<SectorState> getSectorStates()          { return sectors.toState(); }
    public void restoreSectorStates(List<SectorState> s) { sectors.restore(s); pushBalanceSheetInputs(); }

    /** The month's bills back over the rebuild's re-derivation, and the total they add to. */
    public void restoreSectorBills(List<SectorState> saved) {
        if (saved == null) return;
        totalPropertyTax = 0;
        for (SectorState st : saved) {
            if (st == null) continue;
            Sector s = sectors.byKey(st.key);
            if (s == null) continue;
            s.restoreBills(st);
            propertyTaxBySector.put(s.key(), s.getPropertyTaxExpense());
            maintenanceBySector.put(s.key(), s.getMaintenanceExpense());
            totalPropertyTax += s.getPropertyTaxExpense();
        }
    }
    public List<Markets.State> getMarketStates()        { return markets.toState(); }
    public void restoreMarketStates(List<Markets.State> s) { markets.restore(s); }

    public SalesTaxLedger.State getSalesTaxState()      { return salesTaxLedger.toState(); }

    /** The month's VAT back, AND the total that came out of it - one fact. */
    public boolean restoreSalesTaxState(SalesTaxLedger.State state) {
        if (!salesTaxLedger.restore(state)) return false;
        salesTax = salesTaxLedger.getTotalRemitted();
        return true;
    }

    /* ===================================================================
       PRINTERS, RESET, THE REFORM
       =================================================================== */

    public void printWageTaxInfo() {
        System.out.printf("\nTOTAL WAGE TAX INCOME: $%s%n", formatter.format(totalWageTax));
    }

    public void printCityStats() {
        System.out.println("\n====================== CITY FINANCIAL REPORT ======================");
        double totalRev = getBusinessTax() + getIndustrialTax() + salesTax + totalWageTax;
        System.out.println("\n--------------------------- REVENUES ---------------------------");
        System.out.printf("Business Tax Revenue:        $%s Thousand%n", formatter.format(getBusinessTax()));
        System.out.printf("Industrial Tax Revenue:      $%s Thousand%n", formatter.format(getIndustrialTax()));
        System.out.printf("Sales Tax Revenue:           $%s Thousand%n", formatter.format(salesTax));
        System.out.printf("Wage Tax Revenue:            $%s Thousand%n", formatter.format(totalWageTax));
        if (utilityIncome >= 0) {
            System.out.printf("Municipal Services Profit:    $%s Thousand%n", formatter.format(utilityIncome));
            totalRev += utilityIncome;
        }
        System.out.println("---------------------------------------------------------------");
        System.out.printf("TOTAL GOVERNMENT REVENUE:    $%s Thousand%n", formatter.format(totalRev));
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
        System.out.println("\n----------------------- BUDGET BALANCE ------------------------");
        System.out.printf("Monthly Budget Balance:      $%s Thousand%n", formatter.format(totalRev - totalExp));
        System.out.println("\n--------------------- ECONOMIC INDICATORS ---------------------");
        System.out.printf("Monthly GDP:                 $%s Thousand%n", formatter.format(GDP / 12));
        System.out.printf("Annualized GDP:              $%s Thousand%n", formatter.format(yearGDP));
        if (population > 0) {
            System.out.printf("GDP per Capita:              $%s%n", formatter.format(((yearGDP) / population) * 1000));
        }
        System.out.printf("Debt-to-GDP Ratio:           %.2f%%%n", (debt / (yearGDP)) * 100);
        System.out.println("================================================================\n");
    }

    public void resetEconomyManager() {
        cash = 0;
        totalJobs = 0;
        population = 0;
        households = 0;
        totalWageTax = 0;
        interest = 0;
        totalWage = 0;
        totalPropertyTax = 0;
        salesTax = 0;
        sectors.reset();
        markets.reset();
        salesTaxLedger.reset();
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    /**
     * Every figure the economy manager holds, and every sector and market
     * under it, in the new unit. The markets first: the price a month was
     * traded at and their copy of the exchange rate both move, or the
     * national accounts value the change in the food warehouse at a hundred
     * times the right price for one month and book it as production.
     */
    public void redenominate(double scale) {
        cash *= scale;
        landPricePerSqFt *= scale;
        totalPropertyTax *= scale;
        totalBankTax *= scale;
        totalWageTax *= scale;
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
        safetyBill     *= scale;
        subsidiesPaid  *= scale;
        totalContributions *= scale;
        totalEiPremiums *= scale;  eiBenefits *= scale;  studentGrants *= scale;
        totalHealthPremiums *= scale;  studentLoanInterest *= scale;
        exchangeRate *= scale;
        pricePerWatt *= scale;
        pricePerWaterUnit *= scale;
        scaleArray(wageRates, scale);
        scaleArray(staffedWagePerType, scale);
        propertyTaxBySector.replaceAll((k, v) -> v * scale);
        maintenanceBySector.replaceAll((k, v) -> v * scale);
        maintenanceBillTotal *= scale;
        cityMaintenanceBill *= scale;
        bankMaintenanceBill *= scale;
        equityRaised.replaceAll((k, v) -> v * scale);
        dividendsPaid.replaceAll((k, v) -> v * scale);
        sharesBoughtBack.replaceAll((k, v) -> v * scale);
        depositInterestPaid.replaceAll((k, v) -> v * scale);
        overdraftForgivenBySector.replaceAll((k, v) -> v * scale);
        overdraftForgivenThisMonthBySector.replaceAll((k, v) -> v * scale);
        overdraftForgivenThisMonth *= scale;

        taxPolicy.redenominate(scale);
        markets.redenominate(scale);
        sectors.redenominate(scale);
        businessDebtManager.redenominate(scale);
        salesTaxLedger.redenominate(scale);
        nationalAccounts.redenominate(scale);
    }

    private static void scaleArray(double[] values, double scale) {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) values[i] *= scale;
    }
}
