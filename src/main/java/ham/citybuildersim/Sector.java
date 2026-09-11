package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One business in the city, and the template every sector extends.
 *
 * WHY (2026-09-11). Jerus: "we should make a single template, and every
 * single sector just extends it, and adds or overrides, right? cause in the
 * future there will be over a thousand or near a thousand sectors." Until
 * this class the six sectors were five handlers in five shapes, named by hand
 * in a hundred places; a seventh would have been a week of finding them. See
 * claude/the-sector-template.md for the design and the decisions.
 *
 * WHAT EVERY SECTOR HAS, written once here:
 *
 *   identity      a key (its saved name), a label, the build-menu group its
 *                 buildings sit under
 *   buildings     every template in buildings.json names its owner; the
 *                 sector reads capacity, jobs, land, book value, power, water
 *                 and road load off its own stacks
 *   production    the goods it makes and the goods it uses, in units per
 *                 building per month from the templates; the operating rate
 *                 (staffing x power x water x roads x sickness) that scales
 *                 output, and payroll that scales with staffing alone
 *   stock         a warehouse per stockable output, a pantry per input it
 *                 buys ahead of using
 *   books         one income statement in one order for everyone; the cash
 *                 it keeps; the balance-sheet inputs the city pushes in
 *   the ledger    every sale and every purchase of the month, by counterparty,
 *                 which is what the statement, the VAT and the accounts read
 *   credit, listing, savings abroad - all keyed by the sector's name in
 *                 BusinessDebtManager, Equity, Exchange and OutwardInvestment,
 *                 none of which know or care what the sector makes
 *   planning      the generic expansion decision, and the shrinking rules
 *
 * A SECTOR CLASS IS A DECLARATION. Twenty lines, in ham.citybuildersim.sectors,
 * one file each: what it makes, what it uses, what it stocks, and any hook it
 * overrides. The quantities come from the buildings and the prices from the
 * goods, so a sector that is nothing but a factory needs no code of its own
 * at all - see sectors.Mining for the smallest one and sectors.RealEstate for
 * the largest.
 *
 * THE MONTH, in the order Sectors runs it (the same order the handlers ran
 * in, so nothing about when money moves has changed):
 *
 *   top of the month   bill(): interest, property tax and maintenance are
 *                      set for the month; strike(): the statement is struck
 *                      from LAST month's trades and THIS month's bills, the
 *                      VAT is settled from the same figures, and the month
 *                      is banked to cash. That is what the handlers did -
 *                      they struck at the top, off the month before.
 *   middle             the simulation refreshes wages, ratios, capacities.
 *   bottom             Markets.clearMonth(): the seller-priced goods are sold
 *                      (the shops to the households, the landlords' doors),
 *                      then every traded good is priced, offered, bid for,
 *                      allocated, imported and exported, and the makers
 *                      produce for next month. Every fill lands in a ledger.
 *
 * MONEY MOVES AT THE STRIKE AND NOWHERE ELSE in this class. A trade is a
 * fact about units and a price; the cash follows when the statement is
 * banked, buyer and seller in the same pass, so a local sale is always a
 * pool-to-pool move inside one MoneyAudit window. The "cheque in the post"
 * pool the food trade needed is gone with the lag that needed it.
 */
public abstract class Sector {

    /* ===================================================================
       IDENTITY AND DECLARATION
       =================================================================== */

    private final String key;
    private final String label;
    private final BuildingType group;
    private String blurb = "";

    private final Set<Good> makes = new LinkedHashSet<>();
    private final Set<Good> uses = new LinkedHashSet<>();

    /** Inputs it buys ahead of using, and how many months of use it keeps. */
    private final Map<Good, Double> pantryMonths = new EnumMap<>(Good.class);

    /**
     * @param key   the saved name. "Retail", "Real Estate", "Industry",
     *              "Construction", "Heavy Industry", "Mining" are what every
     *              save already calls the six; a new sector picks a name and
     *              keeps it for ever.
     * @param label what the screens call it
     * @param group the build-menu group its buildings sit under, and the band
     *              the city assesses its property tax on
     */
    protected Sector(String key, String label, BuildingType group) {
        this.key = key;
        this.label = label;
        this.group = group;
    }

    protected final void makes(Good good)  { makes.add(good); }
    protected final void uses(Good good)   { uses.add(good); }

    /** It keeps this many months of use of an input on hand, buying the difference each month. */
    protected final void pantry(Good good, double coverMonths) {
        uses.add(good);
        pantryMonths.put(good, Math.max(0, coverMonths));
    }

    protected final void blurb(String text) { this.blurb = text == null ? "" : text; }

    public final String key()         { return key; }
    public final String label()       { return label; }
    public final BuildingType group() { return group; }
    public final String blurb()       { return blurb; }
    public final Set<Good> goodsMade() { return Collections.unmodifiableSet(makes); }
    public final Set<Good> goodsUsed() { return Collections.unmodifiableSet(uses); }
    public final boolean isMaker(Good g) { return makes.contains(g); }
    public final boolean isUser(Good g)  { return uses.contains(g); }
    public final boolean hasPantry(Good g) { return pantryMonths.containsKey(g); }

    /* ===================================================================
       WIRING - set once by Sectors.attach()
       =================================================================== */

    protected BuildingManager buildings;
    protected Markets markets;

    /** The city, for the few hooks that need more than the buildings and the markets. Null in a bare fixture. */
    protected Game game;

    void attach(BuildingManager buildings, Markets markets) {
        this.buildings = buildings;
        this.markets = markets;
    }

    void attachGame(Game game) { this.game = game; }

    public BuildingManager buildings() { return buildings; }
    public Markets markets()           { return markets; }

    /* ===================================================================
       LABOUR
       =================================================================== */

    /** Payroll per tier at full staffing: wage x posts. */
    protected final double[] wages = new double[11];
    protected final int[] jobs = new int[11];
    protected final double[] fill = new double[11];

    /** Share of its posts that are filled. 1 with no posts, so an empty sector idles rather than dies. */
    protected double averageFill = 1;

    /** The fill rate per tier, as PopulationManager last set it. */
    public void updateJobFillRate(double[] fillRate) {
        if (fillRate == null) return;
        System.arraycopy(fillRate, 0, fill, 0, Math.min(fillRate.length, fill.length));
    }

    /**
     * The wage bill, from the schedule and this sector's own posts.
     *
     * @param wagePerType what one post of each tier costs a month
     * @param posts       this sector's posts per tier - its buildings' jobs
     */
    public void updateWages(double[] wagePerType, int[] posts) {
        if (wagePerType == null || posts == null) return;
        int n = Math.min(Math.min(wagePerType.length, posts.length), wages.length);
        double filled = 0;
        int total = 0;
        for (int i = 0; i < n; i++) {
            jobs[i] = posts[i];
            wages[i] = wagePerType[i] * posts[i];
            filled += fill[i] * posts[i];
            total += posts[i];
        }
        averageFill = total == 0 ? 1 : filled / total;
    }

    /** Posts per tier, off its own buildings. */
    public int[] postsPerTier() {
        return buildings == null ? new int[11] : buildings.getJobArrayBySector(key);
    }

    /** What the staffed posts cost this month. Staffing, not sickness - the sick are paid. */
    public double getPayroll() {
        double total = 0;
        for (double tier : wages) total += tier;
        return total * averageFill * payrollScale();
    }

    /**
     * A sector that pays less than its full staffed payroll in a slack month
     * says so here. Construction keeps a core crew when there is nothing to
     * build; everyone else pays the people it employs.
     */
    protected double payrollScale() { return 1; }

    /** The staffed payroll by tier, for the banded wage tax. */
    public double[] getStaffedPayrollPerType() {
        double[] out = new double[wages.length];
        double scale = averageFill * payrollScale();
        for (int i = 0; i < wages.length; i++) out[i] = wages[i] * scale;
        return out;
    }

    public double getAverageFill() { return averageFill; }

    /* ===================================================================
       UTILISATION

       Four throttles that multiply: a gridlocked city in a brownout is worse
       off than either alone. Sickness cuts output only and never payroll -
       the staff are on the books whether they came in or not (see Health).
       =================================================================== */

    protected double energyRatio = 1, waterRatio = 1, roadRatio = 1, healthRatio = 1;
    protected double electricity, water;
    protected double pricePerWatt, pricePerWaterUnit;

    public void setEnergyRatio(double r)  { energyRatio = r; }
    public void setWaterRatio(double r)   { waterRatio = r; }
    public void setRoadRatio(double r)    { roadRatio = r; }
    public void setHealthRatio(double r)  { healthRatio = r; }
    public void setPricePerWatt(double p)       { pricePerWatt = p; }
    public void setPricePerWaterUnit(double p)  { pricePerWaterUnit = p; }
    /** Draw at nameplate, off its own buildings. Set by EconomyManager each month. */
    public void setElectricityConsumption(double kw)  { electricity = kw; }
    public void setWaterConsumption(double units)     { water = units; }

    public double getEnergyRatio()  { return energyRatio; }
    public double getWaterRatio()   { return waterRatio; }
    public double getRoadRatio()    { return roadRatio; }
    public double getHealthRatio()  { return healthRatio; }

    /** How much of nameplate actually runs: staffing times the four ratios. */
    public double getOperatingRate() {
        return averageFill * energyRatio * waterRatio * roadRatio * healthRatio;
    }

    /** Charged for what was DELIVERED, not asked for - the utility books the same slice. */
    public double getElectricityCost() { return electricity * energyRatio * pricePerWatt; }
    public double getWaterCost()       { return water * waterRatio * pricePerWaterUnit; }

    /* ===================================================================
       MONEY AND THE BILLS
       =================================================================== */

    private double cash;

    /** Set for the month at the top of it, before the statement runs. No money moves on setting. */
    private double interestExpense;
    private double propertyTaxExpense;
    private double maintenanceExpense;

    /** The profit rate in force this month. Set before the statement runs. */
    private double taxRate;

    private double landValue, buildingsValue, bondsPayable;

    public final double getCash()            { return cash; }
    public final void setCash(double cash)   { this.cash = cash; }
    public final void addCash(double amount) { this.cash += amount; }

    public void setInterestExpense(double v)    { interestExpense = Math.max(0, v); }
    public void setPropertyTaxExpense(double v) { propertyTaxExpense = Math.max(0, v); }
    public void setMaintenanceExpense(double v) { maintenanceExpense = Math.max(0, v); }
    public void setTaxRate(double rate)         { taxRate = Math.max(0, rate); }

    public double getInterestExpense()    { return interestExpense; }
    public double getPropertyTaxExpense() { return propertyTaxExpense; }
    public double getMaintenanceExpense() { return maintenanceExpense; }
    public double getTaxRate()            { return taxRate; }

    public void setBalanceSheetInputs(double land, double buildingsWorth, double bonds) {
        this.landValue = land;
        this.buildingsValue = buildingsWorth;
        this.bondsPayable = bonds;
    }

    public double getLandValue()      { return landValue; }
    public double getBuildingsValue() { return buildingsValue; }
    public double getBondsPayable()   { return bondsPayable; }

    /** Stock on hand at the price it would fetch today, every good together. */
    public double getInventoryValue() {
        double total = 0;
        for (Map.Entry<Good, Double> e : stock.entrySet()) {
            total += e.getValue() * priceOf(e.getKey());
        }
        for (Map.Entry<Good, Double> e : pantry.entrySet()) {
            total += e.getValue() * priceOf(e.getKey());
        }
        return total;
    }

    /** Today's price of a good, for valuing stock: the market's, or nothing for a seller-priced good. */
    protected double priceOf(Good g) {
        if (markets == null) return 0;
        GoodsMarket m = markets.get(g);
        return m == null ? 0 : m.getLocalPrice();
    }

    /**
     * Position as of right now - a balance sheet is an instant, an income
     * statement a period. Cash after the strike, stock at today's price.
     */
    public BalanceSheet getBalanceSheet() {
        BalanceSheet sheet = new BalanceSheet(label)
                .setCash(cash)
                .setLand(landValue)
                .setBuildings(buildingsValue)
                .setBondsPayable(bondsPayable);
        sheet.setInventoryValue(getInventoryValue());
        return sheet;
    }

    /* ===================================================================
       STOCK
       =================================================================== */

    /** Output on hand, per stockable good it makes. */
    protected final Map<Good, Double> stock = new EnumMap<>(Good.class);

    /** Input on hand, per good it keeps a pantry of. */
    protected final Map<Good, Double> pantry = new EnumMap<>(Good.class);

    public double getStock(Good g)   { return stock.getOrDefault(g, 0.0); }
    public double getPantry(Good g)  { return pantry.getOrDefault(g, 0.0); }

    public void setStock(Good g, double units)  { stock.put(g, Math.max(0, units)); }
    public void setPantry(Good g, double units) { pantry.put(g, Math.max(0, units)); }

    /** Warehouse room for a good, off its buildings' `stock` field. */
    public double getStockCapacity(Good g) {
        return buildings == null ? 0 : buildings.totalBySector(key, t -> t.stocks(g));
    }

    /* ===================================================================
       CAPACITY, off the buildings
       =================================================================== */

    /** Nameplate output of a good a month, finished buildings only. */
    public double getCapacity(Good g) {
        return buildings == null ? 0 : buildings.totalBySector(key, t -> t.makes(g));
    }

    /** ...and what is on site, which counts as supply for the planner. */
    public double getPipeline(Good g) {
        return buildings == null ? 0 : buildings.underConstructionBySector(key, t -> t.makes(g));
    }

    /** Input a good needs a month at nameplate, across its buildings. */
    public double getInputAtCapacity(Good g) {
        return buildings == null ? 0 : buildings.totalBySector(key, t -> t.uses(g));
    }

    /* ===================================================================
       THE MONTH'S PRODUCTION FIGURES, per output good
       =================================================================== */

    /** What the month did with each good it makes. For the screens and the checks. */
    public static final class Output {
        public double capacity;      // nameplate a month
        public double planned;       // what it decided to make for home
        public double produced;      // what went into stock or to market
        public double idled;         // nameplate neither the city nor the world would take
        public double exportBound;   // made straight for the ship
        public double offered;       // released to the market at the price
        public double withheld;      // kept back below cost
        public double soldLocal;     // units taken by local buyers
        public double exported;      // units shipped, from the line or the shed
        public double writtenOff;    // stock lost to a demolished warehouse
        public double costPerUnit;   // the month's break-even, for the screen
    }

    /** What the month did with each good it uses. */
    public static final class Input {
        public double needed;        // at this month's operating rate
        public double bid;           // what it asked the market for
        public double boughtLocal;   // filled at home
        public double imported;      // filled from the world
    }

    protected final Map<Good, Output> outputs = new EnumMap<>(Good.class);
    protected final Map<Good, Input> inputs = new EnumMap<>(Good.class);

    public Output output(Good g) { return outputs.computeIfAbsent(g, k -> new Output()); }
    public Input input(Good g)   { return inputs.computeIfAbsent(g, k -> new Input()); }

    /* ===================================================================
       THE LEDGER

       Every sale and every purchase of the month in progress, by
       counterparty. Struck into the statement at the top of the next month
       and then cleared. SAVED, because a save is taken at the bottom of the
       month with a full ledger nobody has struck yet - the same reason the
       old handlers carried "month flows" in the save.
       =================================================================== */

    public static final class Ledger {
        /** Sold to local buyers - sectors, households, the city - in money. */
        public double localSales;
        /** Sold to the world. Zero-rated for the VAT. */
        public double exports;
        /** Revenue that is not a sale of a good: recognised building work, repairs billed. */
        public double otherRevenue;
        /** Bought from each local supplier, by the supplier's key: the input tax credit is at THEIR rate. */
        public final Map<String, Double> purchasesBySupplier = new LinkedHashMap<>();
        /** Bought from the world. Charged at the buyer's own rate. */
        public double imports;
        /** Units sold and bought, per good, for the accounts and the screens. */
        public final Map<Good, Double> unitsSold = new EnumMap<>(Good.class);
        public final Map<Good, Double> unitsBought = new EnumMap<>(Good.class);
        /** Sold to households in particular - consumption, in the national accounts. */
        public double salesToHouseholds;

        public double purchases() {
            double total = imports;
            for (double v : purchasesBySupplier.values()) total += v;
            return total;
        }

        public double revenue() { return localSales + exports + otherRevenue; }

        void clear() {
            localSales = exports = otherRevenue = imports = salesToHouseholds = 0;
            purchasesBySupplier.clear();
            unitsSold.clear();
            unitsBought.clear();
        }

        void scale(double s) {
            localSales *= s; exports *= s; otherRevenue *= s; imports *= s; salesToHouseholds *= s;
            purchasesBySupplier.replaceAll((k, v) -> v * s);
        }
    }

    private Ledger pending = new Ledger();

    public Ledger pending() { return pending; }

    /** A sale of this sector's, booked. Called by Markets for every fill. */
    protected void bookSale(Trade t) {
        if (t == null) return;
        if (t.isExport()) pending.exports += t.value();
        else {
            pending.localSales += t.value();
            if (Trade.HOUSEHOLDS.equals(t.buyer())) pending.salesToHouseholds += t.value();
        }
        pending.unitsSold.merge(t.good(), t.units(), Double::sum);
        output(t.good());
        if (t.isExport()) outputs.get(t.good()).exported += t.units();
        else              outputs.get(t.good()).soldLocal += t.units();
    }

    /** A purchase of this sector's, booked. */
    protected void bookPurchase(Trade t) {
        if (t == null) return;
        if (t.isImport()) pending.imports += t.value();
        else pending.purchasesBySupplier.merge(t.seller(), t.value(), Double::sum);
        pending.unitsBought.merge(t.good(), t.units(), Double::sum);
        Input in = input(t.good());
        if (t.isImport()) in.imported += t.units();
        else              in.boughtLocal += t.units();
    }

    /** Revenue that is not a sale of a good, booked into the month. */
    protected final void bookOtherRevenue(double amount) {
        if (amount > 0 && Double.isFinite(amount)) pending.otherRevenue += amount;
    }

    /* ===================================================================
       THE STATEMENT
       =================================================================== */

    /**
     * The month's figures, as the statement was struck. Read by SectorBooks,
     * the screens and the checks; written by strike() and bank() only.
     */
    public static final class Statement {
        public double revenue, inputs, payroll, electricity, water, maintenance;
        public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome;
        /** The two halves of revenue, for the accounts. */
        public double localSales, exports, otherRevenue, salesToHouseholds;
        /** The two halves of inputs, for the accounts and the audit. */
        public double localPurchases, imports;
        public Map<String, Double> purchasesBySupplier = new LinkedHashMap<>();

        void scale(double s) {
            revenue *= s; inputs *= s; payroll *= s; electricity *= s; water *= s; maintenance *= s;
            operatingIncome *= s; interest *= s; propertyTax *= s; salesTax *= s;
            preTaxIncome *= s; profitTax *= s; netIncome *= s;
            localSales *= s; exports *= s; otherRevenue *= s; salesToHouseholds *= s;
            localPurchases *= s; imports *= s;
            purchasesBySupplier.replaceAll((k, v) -> v * s);
        }
    }

    private final Statement statement = new Statement();

    public Statement statement() { return statement; }

    /** Pre-tax, before the profit tax. What the loss counter and the subsidy judge. */
    public double getNetIncome() { return statement.preTaxIncome; }

    /** What the city collects from it this month. Floored at zero: a loss earns no refund. */
    public double getProfitTax() { return statement.profitTax; }

    /**
     * Strikes the month WITHOUT the sales tax and without banking: the ledger
     * is read into the statement, and the VAT is struck from these figures
     * next, by SalesTaxLedger, and cannot be known while they are written.
     */
    public void strike() {
        Statement s = statement;
        s.localSales = pending.localSales;
        s.exports = pending.exports;
        s.otherRevenue = pending.otherRevenue;
        s.salesToHouseholds = pending.salesToHouseholds;
        s.revenue = pending.revenue();
        s.imports = pending.imports;
        s.localPurchases = pending.purchases() - pending.imports;
        s.purchasesBySupplier = new LinkedHashMap<>(pending.purchasesBySupplier);
        s.inputs = pending.purchases();
        s.payroll = getPayroll();
        s.electricity = getElectricityCost();
        s.water = getWaterCost();
        s.maintenance = maintenanceExpense;
        s.operatingIncome = s.revenue - s.inputs - s.payroll - s.electricity - s.water - s.maintenance;
        s.interest = interestExpense;
        s.propertyTax = propertyTaxExpense;
        s.salesTax = 0;
        s.preTaxIncome = s.operatingIncome - s.interest - s.propertyTax;
        s.profitTax = Math.max(s.preTaxIncome * taxRate, 0);
        s.netIncome = s.preTaxIncome - s.profitTax;
    }

    /**
     * ...and banks it, once the sales tax is known.
     *
     * The profit tax is struck AFTER the VAT, which is what real accounting
     * does: revenue is booked net of VAT, so the tax authority never taxes
     * a remittance as profit. What lands in cash is net of both. The ledger
     * is then cleared for the month that is starting.
     */
    public void bank(double salesTaxRemitted) {
        Statement s = statement;
        s.salesTax = salesTaxRemitted;
        s.preTaxIncome = s.operatingIncome - s.interest - s.propertyTax - s.salesTax;
        s.profitTax = Math.max(s.preTaxIncome * taxRate, 0);
        s.netIncome = s.preTaxIncome - s.profitTax;
        cash += s.netIncome;
        pending.clear();
        for (Output o : outputs.values()) {
            o.soldLocal = 0; o.exported = 0; o.writtenOff = 0; o.offered = 0; o.withheld = 0;
        }
        for (Input in : inputs.values()) {
            in.bid = 0; in.boughtLocal = 0; in.imported = 0;
        }
        afterBank();
    }

    /** A sector with something to clear when its month is banked says so here. */
    protected void afterBank() { }

    /**
     * The struck month, put back on load, so the first month back reads the
     * same as the one before it and the loss counter sees what it saw.
     */
    public void restoreStatement(Statement saved) {
        if (saved == null) return;
        Statement s = statement;
        s.revenue = saved.revenue; s.inputs = saved.inputs; s.payroll = saved.payroll;
        s.electricity = saved.electricity; s.water = saved.water; s.maintenance = saved.maintenance;
        s.operatingIncome = saved.operatingIncome; s.interest = saved.interest;
        s.propertyTax = saved.propertyTax; s.salesTax = saved.salesTax;
        s.preTaxIncome = saved.preTaxIncome; s.profitTax = saved.profitTax; s.netIncome = saved.netIncome;
        s.localSales = saved.localSales; s.exports = saved.exports; s.otherRevenue = saved.otherRevenue;
        s.salesToHouseholds = saved.salesToHouseholds;
        s.localPurchases = saved.localPurchases; s.imports = saved.imports;
        s.purchasesBySupplier = saved.purchasesBySupplier == null
                ? new LinkedHashMap<>() : new LinkedHashMap<>(saved.purchasesBySupplier);
    }

    /* ===================================================================
       THE MONTH AT THE BOTTOM - HOOKS Markets CALLS

       Defaults make a factory: it makes what it can sell, stocks what keeps,
       exports what the city will not take, and buys its inputs at the
       operating rate. A sector that is not a factory overrides.
       =================================================================== */

    /**
     * How many months of local demand a maker holds in stock before it
     * idles. Two: enough to ride a bad month without a shortage, not enough
     * to set the price by flooding. IndustrialHandler's figure.
     */
    public static final double STOCK_MONTHS = 2;

    /** Above this share of warehouse room a maker clears stock even at a loss. */
    public static final double DUMP_THRESHOLD = .8;

    /**
     * What the city will want of a good this month, for planning output.
     *
     * The market's last strike: what buyers intended to take. A maker plans
     * against demand that does not depend on what it did last month, or a
     * throttle driven off its own sales deadlocks - zero output makes zero
     * purchases makes zero output. Nobody has told a new market anything, so
     * a maker with no signal fills its shed to the dump line and stops.
     */
    protected double plannedDemand(Good g) {
        GoodsMarket m = markets == null ? null : markets.get(g);
        return m == null ? 0 : m.getDemand();
    }

    /**
     * What it will make of a stockable good this month for the home market:
     * nameplate at today's rate, or what brings the stock to STOCK_MONTHS of
     * demand, whichever is less. THE FIGURE THE MARKET IS PRICED ON - a
     * market is priced on what comes to it, not on what could.
     */
    public double getPlannedOutput(Good g) {
        double nameplate = getCapacity(g) * getOperatingRate();
        if (!g.stockable()) return nameplate;
        double demand = plannedDemand(g);
        double target = demand > 0
                ? demand * STOCK_MONTHS
                : getStockCapacity(g) * DUMP_THRESHOLD;
        return Math.max(0, Math.min(nameplate, target - getStock(g)));
    }

    /**
     * Nameplate the city cannot eat, made for export instead - if the export
     * price clears the marginal cost of running the line, which is the
     * energy and water and nothing else: the staff are paid either way.
     */
    public double getExportBoundOutput(Good g) {
        if (!g.exportable() || markets == null) return 0;
        double spare = Math.max(0, getCapacity(g) * getOperatingRate() - getPlannedOutput(g));
        if (spare <= 0) return 0;
        return markets.get(g).exportPrice() >= getMarginalCostPerUnit(g) ? spare : 0;
    }

    /**
     * Break-even per unit this month: everything the line costs over what it
     * makes. The decision to BUILD is judged against this.
     */
    public double getCostPerUnit(Good g) {
        double output = getCapacity(g) * getOperatingRate();
        if (output <= 0) return Double.MAX_VALUE;
        return (getPayroll() + getElectricityCost() + getWaterCost() + maintenanceExpense
                + inputCostAtRate()) / output;
    }

    /**
     * What it costs to SELL a unit already made: the energy and water, and
     * the inputs, and not the payroll, which is paid whether or not a unit
     * leaves the shed. Judging a sale against average cost refused revenue
     * to avoid a cost already incurred - a hog cycle with a fixed cost
     * mistaken for a variable one at the centre of it (IndustrialHandler).
     */
    public double getMarginalCostPerUnit(Good g) {
        double output = getCapacity(g) * getOperatingRate();
        if (output <= 0) return Double.MAX_VALUE;
        return (getElectricityCost() + getWaterCost() + inputCostAtRate()) / output;
    }

    /** What the month's inputs cost at the operating rate, at today's prices. */
    protected double inputCostAtRate() {
        double total = 0;
        for (Good g : uses) {
            if (!g.traded()) continue;
            total += getInputAtCapacity(g) * getOperatingRate() * priceOf(g);
        }
        return total;
    }

    /**
     * What it wants of an input this month.
     *
     * At the operating rate, because a mill at half rate does not buy a full
     * month of raw material and throw half away. A pantry good is bid for to
     * bring the shelf to its cover, off what left it last month.
     */
    public double bid(Good g) {
        if (pantryMonths.containsKey(g)) {
            double target = pantryTarget(g);
            return Math.max(0, target - getPantry(g));
        }
        return getInputAtCapacity(g) * getOperatingRate();
    }

    /** Units of a pantry good the sector aims to hold: months of recent use. */
    protected double pantryTarget(Good g) {
        double cover = pantryMonths.getOrDefault(g, 0.0);
        double recent = recentUse(g);
        return Math.min(Math.ceil(recent * cover), getStockCapacity(g) > 0 ? getStockCapacity(g) : Double.MAX_VALUE);
    }

    /** What it used of a pantry good last month. The default is the sector's own record. */
    protected double recentUse(Good g) { return pantryUsedLastMonth.getOrDefault(g, 0.0); }

    protected final Map<Good, Double> pantryUsedLastMonth = new EnumMap<>(Good.class);

    /**
     * Units it will release to the market at the price.
     *
     * At or above marginal cost it offers everything it holds. Below, it
     * withholds and lets stock build - except above DUMP_THRESHOLD of its
     * room, where stock would otherwise be lost to the cap. A flow good is
     * offered whole: a mine ships what it lifts.
     */
    public double offer(Good g, double price) {
        Output o = output(g);
        o.costPerUnit = getCostPerUnit(g);
        if (!g.stockable()) {
            o.offered = o.produced;
            o.withheld = 0;
            return o.offered;
        }
        double held = getStock(g);
        if (price >= getMarginalCostPerUnit(g)) {
            o.offered = held;
            o.withheld = 0;
        } else {
            double threshold = getStockCapacity(g) * DUMP_THRESHOLD;
            o.offered = Math.max(held - threshold, 0);
            o.withheld = held - o.offered;
        }
        return o.offered;
    }

    /**
     * Lifts, brews or smelts a flow good for the month - what the makers
     * bring to a market that has no stock behind it. Before the market
     * clears. Limited by the ground where there is ground to limit it.
     */
    public void produceFlow(Good g) {
        if (g.stockable()) return;
        Output o = output(g);
        o.capacity = getCapacity(g);
        double asked = o.capacity * getOperatingRate();
        o.planned = asked;
        o.produced = groundLimit(g, asked);
        o.idled = 0;
        o.exportBound = 0;
    }

    /** A sector whose output is limited by what is in the ground says so here. */
    protected double groundLimit(Good g, double asked) { return asked; }

    /**
     * A flow good's unsold units, once the market has taken what it wants:
     * shipped abroad at the export price, or lost.
     */
    public void shipUnsoldFlow(Good g, GoodsMarket market) {
        if (g.stockable()) return;
        Output o = output(g);
        double unsold = Math.max(0, o.produced - o.soldLocal);
        if (unsold <= 0) return;
        if (g.exportable()) {
            Trade t = market.record(key, Trade.WORLD, unsold, market.exportPrice());
            bookSale(t);
        } else {
            o.idled += unsold;
        }
    }

    /**
     * Runs the month's production of a stockable good into the warehouse,
     * after the market has taken what it wanted from last month's stock.
     *
     * THE PLANT IDLES RATHER THAN FLOOD ITS OWN WAREHOUSE. A firm does not
     * make what it cannot sell: output stops at what brings the stock to
     * STOCK_MONTHS of demand; the staff are still paid, so idling costs it
     * nothing it was not already paying. The export-bound share leaves from
     * the line, never through the shed, so it moves neither the stock nor
     * the price at home.
     *
     * Stock above what the sector can now hold is destroyed, and REPORTED:
     * a demolished warehouse is a loss of assets, not negative output.
     */
    public void produceStock(Good g, GoodsMarket market) {
        if (!g.stockable()) return;
        Output o = output(g);
        o.capacity = getCapacity(g);
        double nameplate = o.capacity * getOperatingRate();
        double planned = getPlannedOutput(g);
        double direct = getExportBoundOutput(g);
        o.planned = planned;
        o.exportBound = direct;
        o.idled = Math.max(0, nameplate - planned - direct);
        o.produced = planned;

        double held = getStock(g) + planned;
        double room = getStockCapacity(g);
        double capped = Math.min(held, room);
        o.writtenOff = Math.max(0, held - capped);
        stock.put(g, capped);

        if (direct > 0 && g.exportable() && market != null) {
            Trade t = market.record(key, Trade.WORLD, direct, market.exportPrice());
            bookSale(t);
        }
    }

    /** Units of a good taken out of stock by a local sale or an export from the shed. */
    void takeFromStock(Good g, double units) {
        if (!g.stockable()) return;
        stock.put(g, Math.max(0, getStock(g) - units));
    }

    /** Units of an input received into the pantry, or consumed on the spot. */
    void receiveInput(Good g, double units) {
        if (pantryMonths.containsKey(g)) {
            pantry.put(g, getPantry(g) + units);
        }
    }

    /** A pantry good used up this month, recorded for next month's cover. */
    protected final void usePantry(Good g, double units) {
        double have = getPantry(g);
        double used = Math.max(0, Math.min(have, units));
        pantry.put(g, have - used);
        pantryUsedLastMonth.put(g, used);
    }

    /**
     * The seller-priced goods, sold: the shops to the households, the
     * landlords' doors to the families. Before the traded goods clear, so a
     * shelf that emptied this month is restocked this month. Nothing by
     * default - a factory sells on the market.
     */
    public void sellOwnPriced(Markets markets, Game game) { }

    /** After every market has cleared and every good is made: anything the month still needs. */
    public void endOfMonth(Game game) { }

    /* ===================================================================
       PLANNING - the decision to grow, and to shrink

       The generic rule is IndustrialHandler's, which was the most-argued
       one: demand for the good against capacity and pipeline; the price
       against cost; the best template by income over cost; the order sized
       to the gap. A sector that plans off something other than its market
       overrides plan(); the landlords plan off jobs, the builders off their
       order book, the mines off the ground.
       =================================================================== */

    /**
     * What this sector would like to build this month, or why not.
     *
     * @param plans the engine, for the shared arithmetic - lead times, order
     *              sizing, land, the trend
     */
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {
        return plans.planMaker(this, game);
    }

    /**
     * The good the generic planner sizes the sector by: its first stockable
     * output, else its first output. A sector making two things overrides.
     */
    public Good planningGood() {
        for (Good g : makes) if (g.stockable()) return g;
        for (Good g : makes) return g;
        return null;
    }

    /**
     * The share of a plant's nameplate the city has to be taking, a month,
     * before this sector sinks its FIRST plant. Nothing by default - the
     * food industry has always built its first mill against any demand and
     * grown into it. A sector whose plant is large against a young city's
     * needs says otherwise; see BusinessInvestment.planMaker().
     */
    public double firstPlantUtilisation() { return 0; }

    /**
     * What the sector can SEE it will sell a month over the next `months`,
     * when its customers' orders are on a book - a ceiling on the demand
     * the maker's rule reads off the trend, since a trend read off a boom
     * runs on after the boom's orders are delivered. Unbounded by default:
     * a shop's customers keep no order book. See BusinessInvestment.forecast().
     */
    public double visibleDemandOver(double months, EconomyManager economy) { return Double.MAX_VALUE; }

    /** What one of its templates would clear a month, for the interest test. See BusinessInvestment. */
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        return plans.estimatedMakerProfit(this, t);
    }

    /**
     * The demand the spare-capacity rule measures the sector against, and
     * the capacity it measures. Null means the rule does not apply - a
     * price-taking exporter always sells what it makes and shrinks only on
     * distress. The default is the planning good's market against nameplate.
     *
     * THE LARGER OF THIS MONTH AND THE TREND. The planner builds on the
     * smaller of the two (BusinessInvestment.planMaker: high AND been
     * high); a sector sells plant on the same evidence read the other way,
     * when demand is low AND has been low. Measured on the month alone,
     * the first materials plant ever built read "160 capacity against 0
     * used" in a month between orders and was scrapped six months after it
     * opened, against a year's average that would have kept it.
     */
    public double[] retirementDemandAndCapacity(Game game) {
        Good g = planningGood();
        if (g == null || !g.stockable()) return null;
        GoodsMarket m = markets == null ? null : markets.get(g);
        double trend = m == null ? 0 : m.getDemandTrend();
        return new double[] { Math.max(plannedDemand(g), trend), getCapacity(g) };
    }

    /** What one of these contributes to the measure the sector is judged on. Nameplate of the planning good. */
    public double unitsOf(BuildingsTemplate t) {
        Good g = planningGood();
        return g == null || t == null ? 0 : t.makes(g);
    }

    /** Whether a holding of this template may be sold back this month. The landlords say no to a door somebody wants. */
    public boolean mayRetire(BuildingsTemplate t) { return true; }

    /** Why nothing could be sold, when mayRetire() refused everything. */
    public String noRetirementReason(boolean distress) {
        return distress ? "overdrawn, and nothing left to sell" : "nothing left to sell";
    }

    /* ===================================================================
       THE SCREENS

       What the sector did, in lines the operations page draws. Data, not
       JavaFX: a sector class must not import the toolkit. The default page
       is the factory's - what it made, sold, held and bought.
       =================================================================== */

    /** One line of the operations page. */
    public record Line(Kind kind, String label, String value, Tone tone) {
        public enum Kind { HEAD, LINE, NOTE }
        public enum Tone { NONE, GOOD, WARN, BAD, MUTED, HEAD }

        public static Line head(String text)  { return new Line(Kind.HEAD, text, "", Tone.NONE); }
        public static Line note(String text)  { return new Line(Kind.NOTE, text, "", Tone.NONE); }
        public static Line of(String label, String value) { return new Line(Kind.LINE, label, value, Tone.NONE); }
        public static Line of(String label, String value, Tone tone) { return new Line(Kind.LINE, label, value, tone); }
    }

    /** What the sector's direct-cost line is called on its income statement. */
    public String inputLabel() {
        if (uses.isEmpty()) return "Inputs";
        StringBuilder sb = new StringBuilder();
        for (Good g : uses) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(g.label().toLowerCase());
        }
        return sb.substring(0, 1).toUpperCase() + sb.substring(1) + " bought";
    }

    public List<Line> operations(Game game) {
        List<Line> lines = new ArrayList<>();
        Formats f = Formats.INSTANCE;
        lines.add(Line.head("The plant"));
        lines.add(Line.of("Staffed", f.pct(averageFill), averageFill < .9 ? Line.Tone.WARN : Line.Tone.NONE));
        double rate = getOperatingRate();
        lines.add(Line.of("Running at", f.pct(rate),
                rate < .5 ? Line.Tone.BAD : rate < .9 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.note(String.format(
                "Staffed %s, power %s, water %s, roads %s, well %s — output is cut by whichever is thinnest.",
                f.pct(averageFill), f.pct(energyRatio), f.pct(waterRatio), f.pct(roadRatio), f.pct(healthRatio))));
        for (Good g : makes) {
            if (!g.traded()) continue;
            Output o = output(g);
            GoodsMarket m = markets == null ? null : markets.get(g);
            lines.add(Line.head(g.label()));
            lines.add(Line.of("Could make", f.units(o.capacity, g)));
            lines.add(Line.of("Made", f.units(o.produced + o.exportBound, g)));
            if (o.idled > 0) lines.add(Line.of("Idled", f.units(o.idled, g), Line.Tone.MUTED));
            lines.add(Line.of("Sold at home", f.units(o.soldLocal, g)));
            if (o.exported > 0) lines.add(Line.of("Exported", f.units(o.exported, g), Line.Tone.GOOD));
            if (m != null) {
                lines.add(Line.of("Price", f.cash(m.getLocalPrice())));
                lines.add(Line.of("Cost to make one", f.cash(o.costPerUnit),
                        o.costPerUnit > m.getLocalPrice() ? Line.Tone.BAD : Line.Tone.NONE));
            }
            if (g.stockable()) {
                lines.add(Line.of("In the warehouse", f.units(getStock(g), g)));
                if (o.withheld > 0) {
                    lines.add(Line.of("Held back", f.units(o.withheld, g), Line.Tone.WARN));
                    lines.add(Line.note("It will not sell below what a unit costs to ship. Stock it refuses to move at today's price sits in the warehouse instead."));
                }
                if (o.writtenOff > 0) lines.add(Line.of("Spoiled", f.units(o.writtenOff, g), Line.Tone.BAD));
            }
        }
        for (Good g : uses) {
            if (!g.traded()) continue;
            Input in = input(g);
            GoodsMarket m = markets == null ? null : markets.get(g);
            lines.add(Line.head(g.label() + ", bought"));
            lines.add(Line.of("Wanted", f.units(in.bid, g)));
            lines.add(Line.of("From the city", f.units(in.boughtLocal, g),
                    in.boughtLocal > 0 ? Line.Tone.GOOD : Line.Tone.WARN));
            if (in.imported > 0) lines.add(Line.of("Imported instead", f.units(in.imported, g), Line.Tone.WARN));
            if (m != null && m.good().importable()) {
                lines.add(Line.note(String.format("Local %s is %s a %s and imported %s. Both work; one keeps the margin here.",
                        g.label().toLowerCase(), f.cash(m.getLocalPrice()), g.unit(), f.cash(m.importPrice()))));
            }
            if (hasPantry(g)) lines.add(Line.of("On hand", f.units(getPantry(g), g)));
        }
        return lines;
    }

    /* ===================================================================
       SAVE AND RESTORE
       =================================================================== */

    /** Everything about this sector that a save has to carry. See SectorState. */
    public SectorState toState() {
        SectorState s = new SectorState();
        s.key = key;
        s.cash = cash;
        s.interest = interestExpense;
        s.propertyTax = propertyTaxExpense;
        s.maintenance = maintenanceExpense;
        s.taxRate = taxRate;
        for (Map.Entry<Good, Double> e : stock.entrySet())  s.stock.put(e.getKey().name(), e.getValue());
        for (Map.Entry<Good, Double> e : pantry.entrySet()) s.pantry.put(e.getKey().name(), e.getValue());
        for (Map.Entry<Good, Double> e : pantryUsedLastMonth.entrySet()) s.pantryUsed.put(e.getKey().name(), e.getValue());
        s.ledger = SectorState.LedgerState.of(pending);
        s.statement = SectorState.StatementState.of(statement);
        s.extras = new LinkedHashMap<>();
        saveExtras(s.extras);
        return s;
    }

    public void restore(SectorState s) {
        if (s == null) return;
        cash = s.cash;
        restoreBills(s);
        stock.clear();
        pantry.clear();
        pantryUsedLastMonth.clear();
        if (s.stock != null) for (Map.Entry<String, Double> e : s.stock.entrySet()) {
            Good g = Good.byName(e.getKey());
            if (g != null && e.getValue() != null) stock.put(g, Math.max(0, e.getValue()));
        }
        if (s.pantry != null) for (Map.Entry<String, Double> e : s.pantry.entrySet()) {
            Good g = Good.byName(e.getKey());
            if (g != null && e.getValue() != null) pantry.put(g, Math.max(0, e.getValue()));
        }
        if (s.pantryUsed != null) for (Map.Entry<String, Double> e : s.pantryUsed.entrySet()) {
            Good g = Good.byName(e.getKey());
            if (g != null && e.getValue() != null) pantryUsedLastMonth.put(g, Math.max(0, e.getValue()));
        }
        pending = s.ledger == null ? new Ledger() : s.ledger.toLedger();
        if (s.statement != null) restoreStatement(s.statement.toStatement());
        restoreExtras(s.extras == null ? new LinkedHashMap<>() : s.extras);
    }

    /** The bills of the month back, over whatever a rebuild re-derived. See Game.loadGame(). */
    public void restoreBills(SectorState s) {
        if (s == null) return;
        interestExpense = Math.max(0, s.interest);
        propertyTaxExpense = Math.max(0, s.propertyTax);
        maintenanceExpense = Math.max(0, s.maintenance);
        taxRate = Math.max(0, s.taxRate);
    }

    /** A sector with state of its own - a price it walks, an order book - writes it here by name. */
    protected void saveExtras(Map<String, Double> extras) { }

    /** ...and reads it back. A missing name is a save from before the field; leave the default. */
    protected void restoreExtras(Map<String, Double> extras) { }

    /** Everything back to a founding sector. */
    public void reset() {
        cash = 0;
        stock.clear();
        pantry.clear();
        pantryUsedLastMonth.clear();
        pending = new Ledger();
        outputs.clear();
        inputs.clear();
        restoreStatement(new Statement());
        interestExpense = propertyTaxExpense = maintenanceExpense = 0;
        landValue = buildingsValue = bondsPayable = 0;
        averageFill = 1;
        java.util.Arrays.fill(wages, 0);
        java.util.Arrays.fill(jobs, 0);
        energyRatio = waterRatio = roadRatio = healthRatio = 1;
        resetExtras();
    }

    protected void resetExtras() { }

    /**
     * Its money in the new unit. Units of stock, posts and ratios are not
     * money and do not move; every price and every dollar does.
     */
    public void redenominate(double scale) {
        cash *= scale;
        interestExpense *= scale;
        propertyTaxExpense *= scale;
        maintenanceExpense *= scale;
        landValue *= scale;
        buildingsValue *= scale;
        bondsPayable *= scale;
        pricePerWatt *= scale;
        pricePerWaterUnit *= scale;
        for (int i = 0; i < wages.length; i++) wages[i] *= scale;
        pending.scale(scale);
        statement.scale(scale);
        for (Output o : outputs.values()) o.costPerUnit *= scale;
        redenominateExtras(scale);
    }

    protected void redenominateExtras(double scale) { }

    @Override
    public String toString() { return key; }
}
