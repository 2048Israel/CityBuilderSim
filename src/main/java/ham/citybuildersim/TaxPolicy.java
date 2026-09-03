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
 * plausible number on screen, so there is exactly one place it can be got
 * wrong.
 *
 * Income tax is not converted: it is a share of a month's income, so the rate
 * applies to the month directly.
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

    private double incomeTaxRate = DEFAULT_INCOME_TAX;
    private double propertyTaxRate = DEFAULT_PROPERTY_TAX;

    //getters
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

    //setters
    public void setIncomeTaxRate(double rate) {
        this.incomeTaxRate = clamp(rate, MAX_INCOME_TAX);
    }

    /** Takes the ANNUAL rate. */
    public void setPropertyTaxRate(double annualRate) {
        this.propertyTaxRate = clamp(annualRate, MAX_PROPERTY_TAX);
    }

    private double clamp(double rate, double max) {
        if (rate < 0) {
            return 0;
        }
        return Math.min(rate, max);
    }

    /** What one month's property tax comes to on a given assessed value. */
    public double propertyTaxOn(double assessedValue) {
        if (assessedValue <= 0) {
            return 0;   // a business that owns nothing owes nothing
        }
        return assessedValue * getMonthlyPropertyTaxRate();
    }

    public void reset() {
        incomeTaxRate = DEFAULT_INCOME_TAX;
        propertyTaxRate = DEFAULT_PROPERTY_TAX;
    }
}
