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

    /* =====================================================================
       THE PENSION PROMISE, AS TWO DIALS

       They were constants on SocialSecurity, and constants are the wrong shape
       for the only two numbers in the game that are a POLICY CHOICE about the
       age structure. A city whose pyramid is greying has exactly two levers -
       charge the workers more, or pay the pensioners less - and until now it
       had neither.

       They live here rather than on SocialSecurity because this is the class
       that is saved, has a screen, and already holds every other rate the
       player sets. SocialSecurity keeps the arithmetic and now takes the rate
       as an argument, which also makes it testable at rates nobody has set.
       ===================================================================== */

    private double contributionRate  = SocialSecurity.DEFAULT_CONTRIBUTION_RATE;
    private double pensionReplacement = SocialSecurity.DEFAULT_PENSION_REPLACEMENT;

    /** Nobody hands over more than a fifth of a wage, whatever the deficit. */
    public static final double MAX_CONTRIBUTION = .20;

    /** A pension of more than one unskilled wage is a wage, not a pension. */
    public static final double MAX_REPLACEMENT = 1.00;

    public double getContributionRate()   { return contributionRate; }
    public double getPensionReplacement() { return pensionReplacement; }

    public void setContributionRate(double rate) {
        contributionRate = Math.max(0, Math.min(MAX_CONTRIBUTION, rate));
    }

    public void setPensionReplacement(double share) {
        pensionReplacement = Math.max(0, Math.min(MAX_REPLACEMENT, share));
    }

    /** What one pensioner receives a month, at the rate currently set. */
    /**
     * A pension, in TODAY's money.
     *
     * SocialSecurity computes this as `replacement x PayTier.UNSKILLED`, which
     * is a compile-time money constant read every month - so a reformed city
     * went on paying its pensioners the FOUNDING cheque out of the new,
     * hundred-times-smaller money. Measured at a factor of two: seniors
     * received 110.65 where 55.32 was due, and they were the last row in the
     * household matrix that would not come into line.
     *
     * (The wage this is struck against does not move with the labour market
     * either, so a pension is frozen in real terms for three centuries. That is
     * a separate design question and not this one's to answer - filed, not
     * fixed.)
     */
    public double pensionPerSenior() {
        return pensionReplacement * pensionWageBase;
    }

    /** The wage a pension is a share of, carried in today's money. */
    private double pensionWageBase = PayTier.UNSKILLED.getMonthlyWage();

    public void redenominate(double scale) { pensionWageBase *= scale; }

    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        pensionWageBase = PayTier.UNSKILLED.getMonthlyWage() / unit;
    }

    private final double[] wageOffset     = new double[WageBand.values().length];

    /*
     * THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector
     * template). They were three arrays indexed by PolicySector.ordinal(),
     * which was the cleanest registry-conversion candidate in the codebase
     * and the one that would have re-keyed every player's policy the day a
     * seventh sector was inserted anywhere but the end. A name is a name.
     * An unset sector reads zero, which is the city rate - the same default
     * the arrays had.
     */
    private final java.util.Map<String, Double> profitOffset   = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> salesOffset    = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> propertyOffset = new java.util.LinkedHashMap<>();

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

    public double getWageOffset(WageBand band)     { return wageOffset[band.ordinal()]; }
    public double getProfitOffset(String sector)   { return profitOffset.getOrDefault(sector, 0.0); }
    public double getSalesOffset(String sector)    { return salesOffset.getOrDefault(sector, 0.0); }
    public double getPropertyOffset(String sector) { return propertyOffset.getOrDefault(sector, 0.0); }

    public double getProfitOffset(Sector s)   { return getProfitOffset(s.key()); }
    public double getSalesOffset(Sector s)    { return getSalesOffset(s.key()); }
    public double getPropertyOffset(Sector s) { return getPropertyOffset(s.key()); }

    public void setWageOffset(WageBand band, double points) {
        wageOffset[band.ordinal()] = clampOffset(points);
    }

    public void setProfitOffset(String sector, double points) {
        if (sector != null) profitOffset.put(sector, clampOffset(points));
    }

    public void setSalesOffset(String sector, double points) {
        if (sector != null) salesOffset.put(sector, clampOffset(points));
    }

    /** In ANNUAL points, matching the rate it offsets. */
    public void setPropertyOffset(String sector, double points) {
        if (sector != null) propertyOffset.put(sector, clampOffset(points));
    }

    public void setProfitOffset(Sector s, double points)   { setProfitOffset(s.key(), points); }
    public void setSalesOffset(Sector s, double points)    { setSalesOffset(s.key(), points); }
    public void setPropertyOffset(Sector s, double points) { setPropertyOffset(s.key(), points); }

    /* ==================================================================
       EFFECTIVE RATES - the only numbers anything is ever charged at
       ================================================================== */

    /** What wages in this band are taxed at. */
    public double effectiveWageRate(WageBand band) {
        return clamp(incomeTaxRate + wageOffset[band.ordinal()], MAX_INCOME_TAX);
    }

    /** What this sector's profit is taxed at. */
    public double effectiveProfitRate(String sector) {
        return clamp(incomeTaxRate + getProfitOffset(sector), MAX_INCOME_TAX);
    }

    /** What this sector charges on the value it adds. See SalesTaxLedger. */
    public double effectiveSalesRate(String sector) {
        return clamp(incomeTaxRate + getSalesOffset(sector), MAX_INCOME_TAX);
    }

    /** ANNUAL property tax rate for this sector. */
    public double effectivePropertyRate(String sector) {
        return clamp(propertyTaxRate + getPropertyOffset(sector), MAX_PROPERTY_TAX);
    }

    /** ...and the monthly one, which is what is actually billed. */
    public double effectiveMonthlyPropertyRate(String sector) {
        return effectivePropertyRate(sector) / 12;
    }

    public double effectiveProfitRate(Sector s)          { return effectiveProfitRate(s.key()); }
    public double effectiveSalesRate(Sector s)           { return effectiveSalesRate(s.key()); }
    public double effectivePropertyRate(Sector s)        { return effectivePropertyRate(s.key()); }
    public double effectiveMonthlyPropertyRate(Sector s) { return effectiveMonthlyPropertyRate(s.key()); }

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
    public double propertyTaxOn(double assessedValue, String sector) {
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
        double tax = 0;
        for (double t : wageTaxPerTier(wagePerType, fillRate)) tax += t;
        return Math.max(0, tax);
    }

    /**
     * The same wage tax, split across the six pay tiers.
     *
     * The residents' books needed the split so each tier could have its own
     * cash-flow statement, and this is the ONLY place either figure is worked
     * out - wageTaxOn() is now a sum over this array rather than a second loop
     * that does the same thing. That is deliberate and it is not tidiness.
     *
     * The last time this codebase kept a second copy of the wage tax it cost
     * 23%. Game.updateHouseholdAccounts() computed the residents' tax as
     * `wages * incomeTaxRate` under a comment claiming it was "the identical
     * formula EconomyManager uses". It was, the day it was written - and then
     * the Policy tab added per-band offsets in this file only, so the moment a
     * player used the wage bands at all, the households were shown paying a
     * different tax from the one the city collected. With every offset at zero
     * the two agreed exactly, which is precisely why nothing noticed.
     *
     * Banded, and summed job type by job type rather than as one total times an
     * average - averaging would throw away exactly the distinction the bands
     * exist to express while still looking about right.
     */
    public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate) {

        double[] byTier = new double[PayTier.values().length];
        if (wagePerType == null) return byTier;

        for (int i = 0; i < wagePerType.length && i < JobType.values().length; i++) {

            double paid = wagePerType[i];
            if (fillRate != null && i < fillRate.length) {
                paid *= fillRate[i];
            }
            if (paid <= 0) continue;

            JobType type = JobType.values()[i];
            byTier[PayTier.of(type).ordinal()] +=
                    paid * effectiveWageRate(WageBand.of(type));
        }
        return byTier;
    }

    /* ==================================================================
       SAVE AND RESTORE
       ================================================================== */

    /**
     * The city rates and the wage-band offsets as one array, city rates
     * first. ORDER IS THE FORMAT and new fields go on the END. The sector
     * offsets are NOT in here any more: they are keyed by name and saved as
     * their own list - see getSectorOffsets().
     */
    public double[] getPolicyState() {

        int bands = WageBand.values().length;

        double[] state = new double[4 + bands];
        int i = 0;
        state[i++] = incomeTaxRate;
        state[i++] = propertyTaxRate;
        state[i++] = contributionRate;
        state[i++] = pensionReplacement;
        for (int b = 0; b < bands; b++) state[i++] = wageOffset[b];
        return state;
    }

    /** @return false if the array is not this build's shape; nothing is changed */
    public boolean restorePolicyState(double[] state) {

        int bands = WageBand.values().length;
        if (state == null || state.length != 4 + bands) return false;

        int i = 0;
        setIncomeTaxRate(state[i++]);
        setPropertyTaxRate(state[i++]);
        setContributionRate(state[i++]);
        setPensionReplacement(state[i++]);
        for (WageBand b : WageBand.values()) setWageOffset(b, state[i++]);
        return true;
    }

    /** One sector's three offsets, as the save carries them. */
    public static final class SectorOffsets {
        public String sector;
        public double profit, sales, property;
    }

    public java.util.List<SectorOffsets> getSectorOffsets() {
        java.util.Set<String> keys = new java.util.LinkedHashSet<>();
        keys.addAll(profitOffset.keySet());
        keys.addAll(salesOffset.keySet());
        keys.addAll(propertyOffset.keySet());
        java.util.List<SectorOffsets> out = new java.util.ArrayList<>();
        for (String k : keys) {
            SectorOffsets o = new SectorOffsets();
            o.sector = k;
            o.profit = getProfitOffset(k);
            o.sales = getSalesOffset(k);
            o.property = getPropertyOffset(k);
            out.add(o);
        }
        return out;
    }

    /** A sector the build does not have keeps its row - harmless, and it comes back if the sector does. */
    public void restoreSectorOffsets(java.util.List<SectorOffsets> saved) {
        profitOffset.clear();
        salesOffset.clear();
        propertyOffset.clear();
        if (saved == null) return;
        for (SectorOffsets o : saved) {
            if (o == null || o.sector == null) continue;
            setProfitOffset(o.sector, o.profit);
            setSalesOffset(o.sector, o.sales);
            setPropertyOffset(o.sector, o.property);
        }
    }

    public void reset() {
        incomeTaxRate = DEFAULT_INCOME_TAX;
        propertyTaxRate = DEFAULT_PROPERTY_TAX;
        contributionRate = SocialSecurity.DEFAULT_CONTRIBUTION_RATE;
        pensionReplacement = SocialSecurity.DEFAULT_PENSION_REPLACEMENT;
        java.util.Arrays.fill(wageOffset, 0);
        profitOffset.clear();
        salesOffset.clear();
        propertyOffset.clear();
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
