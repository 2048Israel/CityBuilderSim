package ham.citybuildersim;

/**
 * The six ages of a resident.
 *
 * Each band knows the span it covers, and therefore what share of it moves up
 * every month. Ages are inclusive of the first year and exclusive of the next
 * band's, so a resident is a BABY from 0 up to their sixth birthday.
 */
public enum AgeBand {

    BABY   ("Babies",   0,   6, .0010),
    CHILD  ("Children", 6,  13, .000075),
    TEEN   ("Teens",   13,  18, .0002),
    ADULT  ("Adults",  18,  70, .00225),
    SENIOR ("Seniors", 70,  85, .0290),
    ELDER  ("Elders",  85, 120, .1277);

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

    /**
     * True for the bands past working age.
     *
     * Asked rather than listed, because FamilyModel places these by hand out of
     * bands nothing else draws from, and the day a third one is added the two
     * loops that skip them must skip it too. `this == SENIOR` in four places was
     * how the elder band would have indexed past the end of a four-element array
     * on its first month.
     */
    public boolean isRetirementAge() {
        return this == SENIOR || this == ELDER;
    }

    /* ===================================================================
       MORTALITY

       Chance of dying in a year, by band, against real life-table figures:

         Babies   0.10%  - almost all of it in the first year; a five-year-old
                           is one of the safest people alive
         Children 0.0075% - the safest band there is
         Teens    0.02%  - accidents, and it ticks up from the child rate
         Adults   0.225% - averaged over a FIFTY-TWO year span
         Seniors  2.90%  - 70 to 85, population-weighted
         Elders   12.77% - 85 to 120, population-weighted

       THE SENIORS WERE ONE BAND OF FIFTY YEARS until 2026-09-15, at a flat
       4.5%. Jerus: senior care asked for a place for every person over 70,
       and in the real world about one in nine of them is in one - 2.0% of
       74-year-olds against 29.6% of the over-85s. One band cannot hold a
       fifteenfold gradient, and the same band could not hold a mortality
       curve that runs from 1.4% a year at seventy to over 30% at ninety-five.

       THE TWO FIGURES ARE POPULATION-WEIGHTED FROM A LIFE TABLE, not picked:
       Canadian death rates for 2019 (OSFI Actuarial Study 22), interpolated
       log-linearly between the published ages and extrapolated past ninety on
       the Gompertz slope they imply - a doubling every 6.1 years - then
       averaged over a stationary population from age seventy. The table that
       produces gives life expectancy at 85 of 7.8 years against the 7.5 OSFI
       publishes, which is the check that the interpolation is not inventing
       anything.

       WHAT IT DOES TO A LIFE, through this model's own two buckets: a senior
       leaves the band at 2.90% dead plus 6.67% turned eighty-five, an elder at
       12.77% dead plus 2.86% aged out at 120, and the two together give about
       14.9 years left at seventy against the 15.4 the single band gave. Total
       deaths among the over-seventies move from 6.5% a year to about 6.7%.

       So the split is very nearly aggregate-preserving, which is luck worth
       naming: the old flat 4.5% had been calibrated to land life expectancy in
       the right place, and the real gradient happens to land it there too. It
       means a sixteen-seed comparison across this change is reading the care
       denominator and the household shapes rather than a step in mortality.

       HALVED FOR CHILDREN, TEENS AND ADULTS on 2026-09-11, Jerus's call - every
       band but babies and seniors - from 0.015%, 0.04% and 0.45%, the
       life-table figures.
       What these three bands used to lose to illness they lose now through
       Sickness, which kills whoever stays sick past two months: a city that
       cures its sick gets back below the life table, a city that does not goes
       well above it. So the numbers here are the deaths that are nobody's
       fault, not the whole of a band's mortality.

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
