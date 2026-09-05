package ham.citybuildersim;

/**
 * The city's age pyramid, and since the switch, the city's POPULATION.
 *
 * ==================== THIS IS NOW LOAD-BEARING ====================
 *
 * It was a placeholder for two batches, asserted inert by a harness that played
 * two identical cities and required every figure to match. That assertion is
 * gone, on purpose: `total()` IS the population now, and everything downstream -
 * the workforce, the job fill rate, every sector's output, the wage bill, the
 * wage tax, GDP - keys off it.
 *
 * WHAT CHANGED, AND WHY IT MATTERS MORE THAN IT LOOKS
 *
 * The population used to be `min(housing, jobs * 2.25)`: derived fresh every
 * month from nothing, with no memory. A finished tower filled instantly, a
 * demolished one erased its residents, and a city could double in a month.
 *
 * Now it is a STOCK. Births and deaths move it, `Migration` moves it far more,
 * and housing and jobs set a TARGET that migration chases rather than a ceiling
 * the city snaps to. That is where the city's inertia comes from, and it is the
 * whole point: a tower fills over months, a closed plant does not evaporate its
 * workers, and a bust takes a year to begin.
 *
 * =================================================================
 *
 * THE MECHANIC
 *
 * Every month each band gives up `1/spanMonths` of itself to the next one, and
 * seniors give theirs up to nothing. Births arrive at the bottom. That is a
 * compartment model, and its known cost is that residence time is exponential
 * rather than fixed - see AgeBand.monthlyOutflowRate(), where the numbers are.
 *
 * Held as doubles, not ints. Rounding 1/444 of an adult population to a whole
 * person every month would round almost every transfer to zero in a small city
 * and leak people steadily in a large one; the pyramid is a distribution and it
 * is allowed to hold fractions. Only the DISPLAY rounds.
 */
public class PopulationCohorts {

    /**
     * Births per thousand residents per year.
     *
     * PLACEHOLDER, at 1.5% a year on Jerus's call, standing in for what will
     * eventually depend on healthcare, housing and prosperity.
     *
     * Slightly above the rate that would hold the population exactly steady
     * against the mortality in AgeBand - which works out near 1.4% - so a city
     * left alone grows very slowly rather than shrinking.
     *
     * THIS IS THE FLOOR NOW, not the rate. Childcare multiplies it, up to
     * double at full coverage, so fifteen per thousand is what a city with
     * nowhere to leave a child manages and thirty is what one that has solved
     * the problem does. See Healthcare.birthFactor(). (The note that used to sit
     * here said scaleTo() overrode the total anyway; that stopped being true the
     * day the pyramid became the population.)
     */
    public static final double BIRTHS_PER_1000_PER_YEAR = 15.0;

    private final double[] band = new double[AgeBand.values().length];

    /** Everything that happened last month, for the screen. */
    private double lastBirths;
    private double lastDeaths;
    private double lastMigration;
    private final double[] lastPromoted = new double[AgeBand.values().length];
    private final double[] lastDeathsByBand = new double[AgeBand.values().length];

    public PopulationCohorts() { }

    /* ----------------------------- reading ----------------------------- */

    public double get(AgeBand b)      { return band[b.ordinal()]; }
    public double getLastBirths()     { return lastBirths; }
    public double getLastDeaths()     { return lastDeaths; }
    public double getLastMigration()  { return lastMigration; }
    public double getPromoted(AgeBand b) { return lastPromoted[b.ordinal()]; }
    public double getDeaths(AgeBand b)   { return lastDeathsByBand[b.ordinal()]; }

    public double total() {
        double sum = 0;
        for (double v : band) sum += v;
        return sum;
    }

    /** Share of the city in this band, 0-1. */
    public double share(AgeBand b) {
        double t = total();
        return t > 0 ? band[b.ordinal()] / t : 0;
    }

    /** Everyone of working age. Not the workforce - see the note below. */
    public double workingAge() {
        double sum = 0;
        for (AgeBand b : AgeBand.values()) {
            if (b.isWorkingAge()) sum += band[b.ordinal()];
        }
        return sum;
    }

    /**
     * Children and seniors per hundred working-age adults.
     *
     * The number that will eventually matter most: it is what decides how many
     * earners a household has against how many mouths, and therefore what the
     * city can actually tax. Reported now purely so the shape can be judged.
     */
    public double dependencyRatio() {
        double working = workingAge();
        if (working <= 0) return 0;
        return (total() - working) / working * 100;
    }

    /* ----------------------------- ageing ----------------------------- */

    /**
     * One month of ageing. Called from the monthly tick; reads nothing else.
     *
     * WORKED FROM THE TOP DOWN, which is not cosmetic. Going upward would let a
     * baby promoted this month be promoted again out of the child band in the
     * same tick, and again out of teens - a newborn could reach the workforce in
     * three months. Draining seniors first and babies last means every transfer
     * lands in a band that has already been processed.
     *
     * An empty city stays empty until somebody moves in - see migrate(), which
     * is where a city with no residents gets its first ones. There is nothing to
     * age here and nobody to give birth.
     */
    public void advanceMonth() {
        advanceMonth(null);
    }

    /**
     * The same month, with healthcare's hand on the death rate.
     *
     * @param mortalityFactor one multiplier per band, in band order, or null for
     *                        a city where nothing modifies mortality. A wrong-
     *                        length array is refused whole and treated as null,
     *                        per the standing rule - a pyramid that applied a
     *                        childcare multiplier to seniors because the array
     *                        was read at an offset would be far worse than one
     *                        that applied nothing.
     *
     * The multiplier goes through AgeBand.monthlyFromAnnual() rather than
     * multiplying monthlyMortality() directly, which is not the same thing and
     * is the whole reason that method is public. Scaling the ANNUAL rate and
     * re-compounding is the honest conversion, and it is what keeps the figure
     * checkable against a life table; scaling the monthly rate would drift, and
     * would skip the MAX_MONTHLY_MORTALITY cap that stops an unreviewed factor
     * emptying a band in a month.
     */
    public void advanceMonth(double[] mortalityFactor) {
        advanceMonth(mortalityFactor, 1);
    }

    /**
     * The same month, with healthcare's hand on BOTH ends of a life.
     *
     * @param birthFactor what the birth rate is multiplied by - somewhere to
     *                    leave a child is the difference between one and two,
     *                    and until this existed births were a flat fifteen per
     *                    thousand a year whatever the player did. See
     *                    Healthcare.birthFactor().
     */
    public void advanceMonth(double[] mortalityFactor, double birthFactor) {

        double[] factor = (mortalityFactor != null
                && mortalityFactor.length == AgeBand.values().length)
                ? mortalityFactor : null;

        java.util.Arrays.fill(lastPromoted, 0);
        java.util.Arrays.fill(lastDeathsByBand, 0);
        lastBirths = 0;
        lastDeaths = 0;
        lastMigration = 0;

        if (total() <= 0) {
            return;
        }

        /*
         * AGEING AND DYING ARE COMPETING RISKS, both taken as a share of the
         * balance the band STARTED the month with rather than one applied after
         * the other. Sequential application would make the second risk depend on
         * the first - deaths-then-ageing and ageing-then-deaths give different
         * answers, and neither is more defensible than the other. Computing both
         * off the same opening balance keeps them independent, which is what
         * they are.
         */
        for (int i = AgeBand.values().length - 1; i >= 0; i--) {

            AgeBand b = AgeBand.values()[i];
            double opening = band[i];
            if (opening <= 0) continue;

            double rate = factor == null
                    ? b.monthlyMortality()
                    : AgeBand.monthlyFromAnnual(b.getAnnualMortality() * Math.max(0, factor[i]));

            double dying  = opening * rate;
            double ageing = opening * b.monthlyOutflowRate();

            // Belt and braces. The rates are far below this and the cap in
            // AgeBand keeps a modifier from getting near it, but a band losing
            // more people than it has would be a negative population, and this
            // pyramid is about to become the population.
            double total = dying + ageing;
            if (total > opening) {
                double scale = opening / total;
                dying *= scale;
                ageing *= scale;
            }

            band[i] -= (dying + ageing);
            lastPromoted[i] = ageing;
            lastDeathsByBand[i] = dying;
            lastDeaths += dying;

            AgeBand next = b.next();
            if (next != null) {
                band[next.ordinal()] += ageing;
            } else {
                // Out of the top of the pyramid at 120, which is also a death.
                lastDeathsByBand[i] += ageing;
                lastDeaths += ageing;
            }
        }

        lastBirths = total() * (BIRTHS_PER_1000_PER_YEAR / 1000.0) / 12.0
                * Math.max(0, birthFactor);
        band[AgeBand.BABY.ordinal()] += lastBirths;
    }

    /**
     * People moving in, or out. This is now the city's main source of residents.
     *
     * Migrants arrive in the city's own proportions, per Jerus - if 30% of the
     * city is adults then 30% of the arrivals are adults. Crude, and known to be:
     * real migration skews heavily toward working age, which is why a city here
     * imports its own dependency ratio rather than improving it. Worth fixing
     * once there is a reason to; it is on the screen's own list of omissions.
     *
     * THE FIRST ARRIVALS ARE THE EXCEPTION. An empty city has no proportions to
     * copy, so the first people through the door get the shape a settled
     * population of that size would have - see seedFrom(). Without this a new
     * game can never start: an empty pyramid has nothing to scale, so nobody
     * would ever arrive and the city would sit at zero residents forever with
     * every job going begging.
     */
    public void migrate(double netArrivals) {

        double t = total();

        if (t <= 0) {
            if (netArrivals > 0) {
                seedFrom((int) Math.round(netArrivals));
                lastMigration = netArrivals;
            }
            return;
        }

        lastMigration = netArrivals;
        if (netArrivals == 0) return;

        for (int i = 0; i < band.length; i++) {
            band[i] = Math.max(0, band[i] + netArrivals * (band[i] / t));
        }
    }

    /**
     * Gives an empty pyramid the shape a settled population of this size has.
     *
     * WHY NOT JUST SPANS. The first version shared the bands out in proportion
     * to how long people stay in each - a real property of a compartment model,
     * and true only if nobody ever dies. They do now, and it broke the shape
     * badly: seniors span fifty of the hundred and twenty years, so a brand-new
     * frontier town was founded **41.7% pensioners**, with barely two fifths of
     * it of working age. Harmless while the pyramid was a display; not harmless
     * at all now that it is the population and the workforce is drawn from it.
     *
     * The honest version solves the steady state of the chain the model actually
     * runs. A band is in balance when what arrives equals what leaves, and what
     * leaves is ageing AND dying:
     *
     *     n(i) * (outflow(i) + mortality(i))  =  n(i-1) * outflow(i-1)
     *
     * Walk that down from the babies and the weights fall out. Nothing is typed
     * in, so the seed follows the rates automatically - change a mortality
     * figure, or add healthcare that scales one, and the shape a new city starts
     * with moves with it rather than quietly disagreeing with the shape it
     * converges to.
     *
     * Gives roughly 8% babies, 10% children, 7% teens, 58% adults, 17% seniors,
     * against the 8.9/10.2/7.2/57.3/16.4 a played city actually settles at. The
     * small gap is growth: a city whose births exceed its deaths runs slightly
     * younger than a perfectly stationary one.
     */
    private void seedFrom(int livePopulation) {
        if (livePopulation <= 0) return;

        for (AgeBand b : AgeBand.values()) {
            band[b.ordinal()] = livePopulation * equilibriumShare(b);
        }
    }

    /**
     * What share of a settled city sits in this band, solved from the rates.
     *
     * Public and static because two other things need it and neither should
     * work it out again. `seedFrom()` uses it to found a city, and `Migration`
     * uses the ADULT share as the fallback for how many residents a job needs
     * when there is nobody here yet to measure. A second copy of this
     * derivation is exactly the bug this codebase keeps finding: the copy is
     * right the day it is written and wrong the first time a mortality figure
     * moves.
     */
    public static double equilibriumShare(AgeBand of) {
        double[] weight = equilibriumWeights();
        double sum = 0;
        for (double w : weight) sum += w;
        return sum > 0 ? weight[of.ordinal()] / sum : 0;
    }

    private static double[] equilibriumWeights() {
        AgeBand[] bands = AgeBand.values();
        double[] weight = new double[bands.length];

        // One unit of births enters the bottom; everything else is what survives
        // the band below it.
        double arriving = 1;
        for (int i = 0; i < bands.length; i++) {
            double leaving = bands[i].monthlyOutflowRate() + bands[i].monthlyMortality();
            weight[i] = leaving > 0 ? arriving / leaving : 0;
            arriving = weight[i] * bands[i].monthlyOutflowRate();
        }
        return weight;
    }

    /* ----------------------------- saving ----------------------------- */

    public double[] toSaveArray() {
        double[] out = new double[band.length + 3];
        System.arraycopy(band, 0, out, 0, band.length);
        out[band.length]     = lastBirths;
        out[band.length + 1] = lastDeaths;
        out[band.length + 2] = lastMigration;
        return out;
    }

    /**
     * Puts a saved pyramid back.
     *
     * Refused whole on a length mismatch rather than padded, which is this
     * codebase's standing rule for state arrays: a pyramid read at the wrong
     * offsets is worse than no pyramid, because it looks like data.
     */
    public void restore(double[] saved) {
        if (saved == null) return;

        /*
         * TWO LENGTHS ACCEPTED, and only these two.
         *
         * lastMigration was appended after the fact - the array carried births
         * and deaths but not the third flow beside them, so a reloaded People
         * screen showed the saved month's births and deaths next to a migration
         * of zero.
         *
         * Refusing an older save outright, which the strict length check would
         * have done, is far worse here than the missing figure: this array IS
         * the population since the switch, so a refusal would hand back a city
         * with nobody in it. Downward compatibility is not a nicety in this one.
         */
        boolean withMigration = saved.length == band.length + 3;
        if (!withMigration && saved.length != band.length + 2) {
            return;   // any other shape is refused whole, per the standing rule
        }
        System.arraycopy(saved, 0, band, 0, band.length);
        lastBirths = saved[band.length];
        lastDeaths = saved[band.length + 1];
        lastMigration = withMigration ? saved[band.length + 2] : 0;
    }

    public void reset() {
        java.util.Arrays.fill(band, 0);
        java.util.Arrays.fill(lastPromoted, 0);
        java.util.Arrays.fill(lastDeathsByBand, 0);
        lastBirths = 0;
        lastDeaths = 0;
    }
}
