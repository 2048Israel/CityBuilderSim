package ham.citybuildersim;

/**
 * The city's tax rates - the revenue half of what the player actually decides.
 *
 * Both rates used to be one hardcoded field on EconomyManager. They live here
 * together because they are the same kind of thing, they are both set by the
 * player, they both have to survive a save, and because the two of them are
 * only interesting relative to each other: an income tax takes a share of what
 * a business earned, a property tax takes a share of what it owns whether it
 * earned anything or not. Which one the city leans on decides who actually pays
 * for it.
 *
 * ANNUAL IN, MONTHLY OUT
 *
 * Property tax is quoted annually, because that is how anyone who has ever paid
 * one thinks about it, and charged monthly, because that is the game's tick.
 * The conversion lives here and nowhere else - getting a 1.5%/year rate charged
 * as 1.5%/month would be an eighteen-fold error that still looks like a
 * plausible number on screen, so there is exactly one place it can be got wrong.
 *
 * Income tax is not converted: it is a share of a month's income, so the rate
 * applies to the month directly.
 *
 * ======================================================================
 * CITY RATES, AND OFFSETS FROM THEM
 * ======================================================================
 *
 * There are two city-wide rates, and then every band and every sector carries an
 * OFFSET in rate points from one of them. Industry at -0.03 pays three points
 * under whatever the city rate is.
 *
 * Jerus's call, and it is the right one for a game where the city rate is a
 * lever the player pulls often: raise the city rate and every sector you have
 * customised keeps its relative treatment instead of being silently left behind.
 * The cost is that a rate on screen is arithmetic rather than a number, so every
 * screen shows the offset AND what it resolves to.
 *
 * RESOLUTION HAPPENS HERE, ONCE. effective*() clamps to [0, max]. No caller
 * adds an offset itself: an offset that escapes clamping is a negative tax rate,
 * which is the city paying businesses to trade, and it would show up as revenue
 * appearing from nowhere three layers away from the line that caused it.
 *
 * DEFAULTS ARE ALL ZERO, which makes every effective rate the city rate and
 * reproduces the single-rate behaviour this replaced, exactly.
 */
public class TaxPolicy {

    /** Where income tax started before it was a dial. */
    public static final double DEFAULT_INCOME_TAX = .15;

    /**
     * 1.5% a year, near the real-world average.
     *
     * Worth being conservative with. Property tax is charged on capital whether
     * or not it earned anything, so it is the one rate that can push a business
     * under while it is doing nothing wrong - and the city's biggest owner of
     * idle capital is its construction sector.
     */
    public static final double DEFAULT_PROPERTY_TAX = .015;

    /** Nobody has ever paid 100% property tax and the game should not model it. */
    public static final double MAX_PROPERTY_TAX = .10;

    /** Above this, income tax stops being a policy and starts being confiscation. */
    public static final double MAX_INCOME_TAX = .60;

    /**
     * How far a band or sector may be moved from the city rate, either way.
     *
     * Bounded so a single offset cannot express a policy the city rate could not
     * express on its own - an offset is a discount or a surcharge, not a
     * separate tax system.
     */
    public static final double MAX_OFFSET = .30;

    private double incomeTaxRate = DEFAULT_INCOME_TAX;
    private double propertyTaxRate = DEFAULT_PROPERTY_TAX;

    private final double[] wageOffset     = new double[WageBand.values().length];
    private final double[] profitOffset   = new double[PolicySector.values().length];
    private final double[] salesOffset    = new double[PolicySector.values().length];
    private final double[] propertyOffset = new double[PolicySector.values().length];

    /* ==================================================================
       THE CITY RATES
       ================================================================== */

    public double getIncomeTaxRate() {
        return incomeTaxRate;
    }

    /** The annual rate - what the player sets and what the screens show. */
    public double getPropertyTaxRate() {
        return propertyTaxRate;
    }

    /** The annual rate divided by twelve. What is actually charged each month. */
    public double getMonthlyPropertyTaxRate() {
        return propertyTaxRate / 12;
    }

    public void setIncomeTaxRate(double rate) {
        this.incomeTaxRate = clamp(rate, MAX_INCOME_TAX);
    }

    /** Takes the ANNUAL rate. */
    public void setPropertyTaxRate(double annualRate) {
        this.propertyTaxRate = clamp(annualRate, MAX_PROPERTY_TAX);
    }

    /* ==================================================================
       OFFSETS
       ================================================================== */

    public double getWageOffset(WageBand band)          { return wageOffset[band.ordinal()]; }
    public double getProfitOffset(PolicySector s)       { return profitOffset[s.ordinal()]; }
    public double getSalesOffset(PolicySector s)        { return salesOffset[s.ordinal()]; }
    public double getPropertyOffset(PolicySector s)     { return propertyOffset[s.ordinal()]; }

    public void setWageOffset(WageBand band, double points) {
        wageOffset[band.ordinal()] = clampOffset(points);
    }

    public void setProfitOffset(PolicySector s, double points) {
        profitOffset[s.ordinal()] = clampOffset(points);
    }

    public void setSalesOffset(PolicySector s, double points) {
        salesOffset[s.ordinal()] = clampOffset(points);
    }

    /** In ANNUAL points, matching the rate it offsets. */
    public void setPropertyOffset(PolicySector s, double points) {
        propertyOffset[s.ordinal()] = clampOffset(points);
    }

    /* ==================================================================
       EFFECTIVE RATES - the only numbers anything is ever charged at
       ================================================================== */

    /** What wages in this band are taxed at. */
    public double effectiveWageRate(WageBand band) {
        return clamp(incomeTaxRate + wageOffset[band.ordinal()], MAX_INCOME_TAX);
    }

    /** What this sector's profit is taxed at. */
    public double effectiveProfitRate(PolicySector sector) {
        return clamp(incomeTaxRate + profitOffset[sector.ordinal()], MAX_INCOME_TAX);
    }

    /** What this sector charges on the value it adds. See SalesTaxLedger. */
    public double effectiveSalesRate(PolicySector sector) {
        return clamp(incomeTaxRate + salesOffset[sector.ordinal()], MAX_INCOME_TAX);
    }

    /** ANNUAL property tax rate for this sector. */
    public double effectivePropertyRate(PolicySector sector) {
        return clamp(propertyTaxRate + propertyOffset[sector.ordinal()], MAX_PROPERTY_TAX);
    }

    /** ...and the monthly one, which is what is actually billed. */
    public double effectiveMonthlyPropertyRate(PolicySector sector) {
        return effectivePropertyRate(sector) / 12;
    }

    /**
     * What one month's property tax comes to on a given assessed value.
     *
     * The sector-blind version, kept for the one caller that has a value but no
     * sector - and for old tests. Prefer propertyTaxOn(value, sector).
     */
    public double propertyTaxOn(double assessedValue) {
        if (assessedValue <= 0) {
            return 0;   // a business that owns nothing owes nothing
        }
        return assessedValue * getMonthlyPropertyTaxRate();
    }

    /** One month's property tax at this sector's own rate. */
    public double propertyTaxOn(double assessedValue, PolicySector sector) {
        if (assessedValue <= 0 || sector == null) {
            return propertyTaxOn(assessedValue);
        }
        return assessedValue * effectiveMonthlyPropertyRate(sector);
    }

    /* ==================================================================
       WAGES
       ================================================================== */

    /**
     * The month's wage tax, summed job type by job type at its band's rate.
     *
     * NOT the total wage bill times an average rate. The bands exist so the
     * player can tax a doctor differently from a labourer, and averaging would
     * throw away exactly the distinction they just set - while still LOOKING
     * right, because the total is in the same neighbourhood either way.
     *
     * @param wagePerType the monthly wage bill per JobType, before fill
     * @param fillRate    what share of each type's posts are actually staffed
     */
    public double wageTaxOn(double[] wagePerType, double[] fillRate) {

        if (wagePerType == null) return 0;

        double tax = 0;
        for (int i = 0; i < wagePerType.length && i < JobType.values().length; i++) {

            double paid = wagePerType[i];
            if (fillRate != null && i < fillRate.length) {
                paid *= fillRate[i];
            }
            if (paid <= 0) continue;

            tax += paid * effectiveWageRate(WageBand.of(JobType.values()[i]));
        }
        return Math.max(0, tax);
    }

    /* ==================================================================
       SAVE AND RESTORE
       ================================================================== */

    /**
     * Every offset as one array, city rates first.
     *
     * ORDER IS THE FORMAT and new fields go on the END - the same rule the
     * report state carries. Restored whole or not at all.
     */
    public double[] getPolicyState() {

        int bands = WageBand.values().length;
        int sectors = PolicySector.values().length;

        double[] state = new double[2 + bands + sectors * 3];
        int i = 0;
        state[i++] = incomeTaxRate;
        state[i++] = propertyTaxRate;

        for (int b = 0; b < bands; b++)   state[i++] = wageOffset[b];
        for (int s = 0; s < sectors; s++) state[i++] = profitOffset[s];
        for (int s = 0; s < sectors; s++) state[i++] = salesOffset[s];
        for (int s = 0; s < sectors; s++) state[i++] = propertyOffset[s];

        return state;
    }

    /** @return false if the array is not this build's shape; nothing is changed */
    public boolean restorePolicyState(double[] state) {

        int bands = WageBand.values().length;
        int sectors = PolicySector.values().length;

        if (state == null || state.length != 2 + bands + sectors * 3) {
            return false;
        }

        int i = 0;
        setIncomeTaxRate(state[i++]);
        setPropertyTaxRate(state[i++]);

        for (WageBand b : WageBand.values())     setWageOffset(b, state[i++]);
        for (PolicySector s : PolicySector.values()) setProfitOffset(s, state[i++]);
        for (PolicySector s : PolicySector.values()) setSalesOffset(s, state[i++]);
        for (PolicySector s : PolicySector.values()) setPropertyOffset(s, state[i++]);

        return true;
    }

    public void reset() {
        incomeTaxRate = DEFAULT_INCOME_TAX;
        propertyTaxRate = DEFAULT_PROPERTY_TAX;
        java.util.Arrays.fill(wageOffset, 0);
        java.util.Arrays.fill(profitOffset, 0);
        java.util.Arrays.fill(salesOffset, 0);
        java.util.Arrays.fill(propertyOffset, 0);
    }

    private double clamp(double rate, double max) {
        if (rate < 0) {
            return 0;
        }
        return Math.min(rate, max);
    }

    private double clampOffset(double points) {
        if (Double.isNaN(points)) return 0;
        return Math.max(-MAX_OFFSET, Math.min(points, MAX_OFFSET));
    }
}
