package ham.citybuildersim;

/**
 * A simple balance sheet for one business in the city.
 *
 * Deliberately built as its own class rather than a handful of fields on
 * IndustrialHandler: the plan is for every sector to get one - real estate,
 * retail, utilities - and the whole point of doing this before adding more
 * industries is that the second, third and fourth set of books should be a
 * few lines of wiring rather than a copy of this logic.
 *
 * Structure follows the accounting identity:
 *
 *     ASSETS = LIABILITIES + EQUITY
 *
 * Equity is not stored. It is derived as assets minus liabilities, which means
 * the sheet balances by construction and can never be shown out of balance.
 * That is the "make the formula happy" version. When retained earnings become
 * a real quantity - a business that can be undercapitalised, issue shares, or
 * go bankrupt - equity becomes a stored figure and this derivation becomes a
 * CHECK against it instead.
 *
 * Placeholders as of now: land and bonds payable are always zero, because the
 * game does not model land ownership or per-business debt yet. They are here so
 * the layout does not have to change when it does.
 */
public class BalanceSheet {

    private final String owner;

    /* ------------------------------ ASSETS ------------------------------ */

    /** Cash reserve held by this business. */
    private double cash;

    /**
     * Stock on hand, valued at the current market price. For a commodity
     * producer that is the honest number - the warehouse really is worth what
     * the market will pay today - and it means a glut visibly shrinks the
     * balance sheet while a shortage inflates it, so the market mechanic shows
     * up in the accounts instead of being hidden behind a fixed cost figure.
     */
    private double inventory;
    private int inventoryUnits;
    private double inventoryUnitPrice;

    /** Not modelled yet - the game has no concept of land ownership. */
    private double land;

    /**
     * Buildings at construction cost: cash paid plus materials at market. It
     * excludes construction labour, which is paid by the construction sector
     * rather than capitalised here, so this understates true cost somewhat.
     * There is no depreciation, so this is gross book value, not net.
     */
    private double buildings;

    /* --------------------------- LIABILITIES ---------------------------- */

    /** Not modelled yet - city debt is not attributed to individual businesses. */
    private double bondsPayable;

    public BalanceSheet(String owner) {
        this.owner = owner;
    }

    //setters (chained)
    public BalanceSheet setCash(double cash) {
        this.cash = cash;
        return this;
    }

    /** Stock valued at market: units on hand times the current price per unit. */
    public BalanceSheet setInventory(int units, double unitPrice) {
        this.inventoryUnits = units;
        this.inventoryUnitPrice = unitPrice;
        this.inventory = units * unitPrice;
        return this;
    }

    public BalanceSheet setLand(double land) {
        this.land = land;
        return this;
    }

    public BalanceSheet setBuildings(double buildings) {
        this.buildings = buildings;
        return this;
    }

    public BalanceSheet setBondsPayable(double bondsPayable) {
        this.bondsPayable = bondsPayable;
        return this;
    }

    //getters
    public String getOwner()              { return owner; }
    public double getCash()               { return cash; }
    public double getInventory()          { return inventory; }
    public int    getInventoryUnits()     { return inventoryUnits; }
    public double getInventoryUnitPrice() { return inventoryUnitPrice; }
    public double getLand()               { return land; }
    public double getBuildings()          { return buildings; }
    public double getBondsPayable()       { return bondsPayable; }

    //derived
    public double getCurrentAssets() {
        return cash + inventory;
    }

    public double getNonCurrentAssets() {
        return land + buildings;
    }

    public double getTotalAssets() {
        return getCurrentAssets() + getNonCurrentAssets();
    }

    /** Nothing is due within the year yet - no payables, no short-term debt. */
    public double getCurrentLiabilities() {
        return 0;
    }

    public double getTotalLiabilities() {
        return bondsPayable;
    }

    /** The balancing figure. See the class note. */
    public double getEquity() {
        return getTotalAssets() - getTotalLiabilities();
    }

    public double getTotalLiabilitiesAndEquity() {
        return getTotalLiabilities() + getEquity();
    }

    /* ------------------------------ RATIOS ------------------------------ */

    /**
     * Current ratio. Undefined while there are no current liabilities, which is
     * always, for now - returns 0 rather than infinity so nothing downstream has
     * to handle a non-finite number.
     */
    public double getCurrentRatio() {
        double cl = getCurrentLiabilities();
        return (cl > 0) ? getCurrentAssets() / cl : 0;
    }

    public double getDebtToAssets() {
        double ta = getTotalAssets();
        return (ta > 0) ? getTotalLiabilities() / ta : 0;
    }

    public double getInventoryShareOfAssets() {
        double ta = getTotalAssets();
        return (ta > 0) ? inventory / ta : 0;
    }

    /** Monthly return on assets. Pass the month's net income. */
    public double getReturnOnAssets(double netIncome) {
        double ta = getTotalAssets();
        return (ta > 0) ? netIncome / ta : 0;
    }
}
