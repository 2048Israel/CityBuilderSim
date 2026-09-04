package ham.citybuildersim;

/**
 * The five ages of a resident.
 *
 * Each band knows the span it covers, and therefore what share of it moves up
 * every month. Ages are inclusive of the first year and exclusive of the next
 * band's, so a resident is a BABY from 0 up to their sixth birthday.
 */
public enum AgeBand {

    BABY   ("Babies",   0,   6, .0010),
    CHILD  ("Children", 6,  13, .00015),
    TEEN   ("Teens",   13,  18, .0004),
    ADULT  ("Adults",  18,  70, .0045),
    SENIOR ("Seniors", 70, 120, .0450);

    private final String label;
    private final int fromAge;
    private final int toAge;
    private final double annualMortality;

    AgeBand(String label, int fromAge, int toAge, double annualMortality) {
        this.label = label;
        this.fromAge = fromAge;
        this.toAge = toAge;
        this.annualMortality = annualMortality;
    }

    public String getLabel() { return label; }
    public int getFromAge()  { return fromAge; }
    public int getToAge()    { return toAge; }

    /** How many months a resident spends here, on average. */
    public int spanMonths() {
        return (toAge - fromAge) * 12;
    }

    /**
     * The fraction that moves up each month.
     *
     * WHAT THIS IS AND IS NOT, because the difference will come up the first
     * time somebody looks at the pyramid and asks why it is smooth.
     *
     * Draining 1/72 of the baby bucket every month gives an average stay of six
     * years - correct - but the stay is EXPONENTIALLY distributed, not fixed.
     * About 37% of a given month's babies are still in the baby bucket six years
     * later, and about 5% are still there at eighteen. Nobody is tracked, so
     * nobody has an age; the bucket only has a mean.
     *
     * That is a deliberate simplification, chosen over one-bucket-per-year. The
     * cost is that a baby boom smears into a smooth bulge instead of travelling
     * through the city as a wave you can watch arrive at the schools and then at
     * the job market. If that wave turns out to matter, the fix is to store an
     * array of yearly buckets and let these five bands become views over it -
     * which is why nothing outside this package should ever assume the five
     * buckets ARE the storage.
     */
    public double monthlyOutflowRate() {
        return 1.0 / spanMonths();
    }

    /** The band after this one, or null for the last. */
    public AgeBand next() {
        int i = ordinal() + 1;
        return i < values().length ? values()[i] : null;
    }

    /** True for the bands that can hold a job. */
    public boolean isWorkingAge() {
        return this == ADULT;
    }

    /* ===================================================================
       MORTALITY

       Chance of dying in a year, by band, against real life-table figures:

         Babies   0.10%  - almost all of it in the first year; a five-year-old
                           is one of the safest people alive
         Children 0.015% - the safest band there is
         Teens    0.04%  - accidents, and it ticks up from the child rate
         Adults   0.45%  - averaged over a FIFTY-TWO year span, from about
                           0.08% at twenty to about 1.5% at sixty-nine
         Seniors  4.5%   - on top of the 2%/yr that ages out at 120, giving a
                           total outflow of 6.5% and therefore an average of
                           about fifteen more years at seventy, which is what
                           life expectancy at seventy actually is

       WHY THE ADULT NUMBER LOOKS LOW AND IS NOT. It is a single figure for
       everyone from eighteen to sixty-nine, and the real curve across that span
       is nearly twenty-fold. A band average is the price of five compartments;
       the alternative is per-year buckets, which was considered and declined.
       =================================================================== */

    public double getAnnualMortality() {
        return annualMortality;
    }

    /**
     * Monthly chance of dying, COMPOUNDED rather than divided.
     *
     * Jerus's warning, and it is the right one: "since it's %, it's quite easy
     * to do some math that turns out way too many people die."
     *
     * Dividing by twelve is the obvious conversion and it is wrong in exactly
     * that direction - applying 4.5%/12 twelve times kills 4.41%, not 4.5%, and
     * the error grows with the rate, so it is worst precisely where it matters
     * most. Solving `1 - (1-annual)^(1/12)` instead means the number written in
     * this file is the number that actually happens over a year, which is what
     * makes it checkable against a life table at all. PopulationCheck asserts
     * the round trip.
     *
     * CAPPED, because healthcare will eventually multiply this and a factor
     * nobody sanity-checked should not be able to empty a band in a month. Five
     * percent monthly is already a catastrophe - about 46% a year - and well
     * beyond anything a plague should reach in a city builder.
     */
    public double monthlyMortality() {
        return monthlyFromAnnual(annualMortality);
    }

    /** The same conversion, exposed so a modifier can be applied honestly. */
    public static double monthlyFromAnnual(double annualRate) {
        if (annualRate <= 0) return 0;
        if (annualRate >= 1) return MAX_MONTHLY_MORTALITY;
        return Math.min(MAX_MONTHLY_MORTALITY,
                1 - Math.pow(1 - annualRate, 1.0 / 12.0));
    }

    /** No band may lose more than this in a single month, whatever modifies it. */
    public static final double MAX_MONTHLY_MORTALITY = .05;
}
