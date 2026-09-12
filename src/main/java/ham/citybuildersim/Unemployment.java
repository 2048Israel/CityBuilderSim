package ham.citybuildersim;

/**
 * The people out of work: how many, who they were, what Employment Insurance
 * pays them, and who has lost their home.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * Jerus, 2026-09-11: "we are to add a new household structure called
 * unemployed, these are different, these will just sum up by age the
 * unemployed, or unhoused, cause i think we are missing those categories...
 * and they will have their own cashflow and stuff." Until now unemployment
 * was one number, workforce less the filled posts, and the people behind it
 * lived in family households at the tier mix of the filled jobs, sharing a
 * wage they did not earn.
 *
 * The families are built from the adults who work now (FamilyModel), and
 * the rest have books of their own (UnemployedHousehold). This class is what
 * those books need that nothing else knows: who is on EI and at what wage,
 * who has run out of it, and who has been evicted.
 *
 * ==================== THE POOL, AND THE FLOWS THROUGH IT ====================
 *
 * The SIZE of the pool is not decided here. It is the labour market's:
 * everyone in the labour force less the posts filled (PopulationManager
 * .getUnemployed()). What this class decides is who is IN it, from the flows
 * between one month and the next - Jerus's answers, each one:
 *
 *   jobs lost      only posts that DISAPPEARED - "net losses only", no churn.
 *                  The fall in filled posts, but no more than the fall in
 *                  posts: somebody who left the city left a post empty, and
 *                  nobody lost a job.
 *   openings       the rise in filled posts, filled PRO RATA between the pool
 *                  and last month's arrivals, who joined the workforce this
 *                  month. Jerus: "pro rata".
 *   arrivals       an arrival who does not get a post goes on EI "same as
 *   not hired      locals", at the unskilled rate.
 *   exits          hires, deaths, turning seventy, and those who left the city
 *                  broke - drawn from every group in proportion.
 *   the rest       whatever the stock says the pool is that the flows above
 *                  do not explain - a graduate with no post, a teenager
 *                  turning eighteen - joins it WITHOUT EI: they never lost a
 *                  job. Or, the other way, leaves it.
 *
 * ==================== EI, ON THE INFLOW ====================
 *
 * Jerus: "for ei, use inflow of unemployed to get the rolling avg EI." A ring
 * of twelve monthly cohorts, each the month's inflow and the insured wage it
 * came from. Each month every cohort shrinks by the pool's exits, the oldest
 * drops off EI, and the month's inflow starts a new one. What EI pays is the
 * ring: every claimant, 55% of their insured wage, capped. The individual is
 * never tracked; the cohort is.
 *
 * The numbers are the real 2026 ones (ESDC; Canada.ca): a 1.63% premium off
 * every wage, 55% of insurable earnings, insurable earnings capped at $68,900
 * a year - 1.66 times the unskilled wage the game's ladder is anchored on,
 * held as that multiple so it moves with wages and with a currency reform.
 * Twelve months, where the real thing runs 14-45 weeks: Jerus's call.
 *
 * ==================== EVICTION ====================
 *
 * Jerus: when EI is over and savings and credit are gone, "a share leaves"
 * and the rest are unhoused. HouseholdBalance strikes who could not pay; this
 * class moves them, a month later: LEAVE_WHEN_BROKE of them leave the city,
 * the rest stay without a home. The unhoused leave the pool the way anybody
 * does - hired, dead, seventy - and the city's ordinary departures.
 */
public class Unemployment {

    /* ------------------------------- the dials ------------------------------- */

    /** Months a claim lasts. Jerus: "Fixed 12 months". */
    public static final int EI_MONTHS = 12;

    /** The employee premium, 2026: $1.63 per $100 of insurable earnings. The employer's 1.4x is not modelled, as CPP's employer half is not. */
    public static final double DEFAULT_PREMIUM_RATE = .0163;

    /** What EI replaces: 55% of insurable earnings. */
    public static final double DEFAULT_BENEFIT_RATE = .55;

    /** Maximum insurable earnings, 2026: $68,900 a year, over the $3,460 unskilled median the ladder is anchored on. */
    public static final double MAX_INSURABLE_MULTIPLE = 68_900.0 / 12 / 3_460;

    /**
     * The share of the evicted who leave the city rather than stay on the
     * street. A quarter: the share of discharged households that leave
     * (HouseholdBalance.LEAVE_ON_BANKRUPTCY), for the same kind of event.
     */
    public static final double LEAVE_WHEN_BROKE = .25;

    /**
     * How much faster the unhoused die: 3.7x. People experiencing homelessness
     * in Toronto died at 2.7 per 100 person-years against 0.72 for housed
     * people in the same cohort, 2021-22 (Frontiers in Public Health, 2024);
     * 2.2 after adjusting for the illness they carry, 16.8 at ages 25-44. The
     * all-ages figure, because the adult band is 18-70 in one number.
     */
    public static final double UNHOUSED_MORTALITY = 3.7;

    /** ...and how much faster they get sick. No study measures a work-absence multiplier; the mortality one stands in. Jerus: "get sick faster if unhoused". */
    public static final double UNHOUSED_SICKNESS = 3.7;

    /* ------------------------------- the stocks ------------------------------- */

    /** Claimants by the month their claim began, newest first. People. */
    private final double[] cohortPeople = new double[EI_MONTHS];

    /** The monthly wage each cohort was insured at, in today's money, before the cap. */
    private final double[] cohortWage = new double[EI_MONTHS];

    /** Out of work, housed, and past EI or never on it. */
    private double offEi;

    /** Out of work and evicted: no home, no EI. */
    private double unhoused;

    /** Last month's pool, and what the flows are struck against. */
    private double lastPool;
    private double[] lastFilled;
    private double[] lastPosts;
    private boolean started;

    /** Adults who arrived last month and joined the workforce this month. */
    private double arrivalsLooking;

    /** Evicted at the last settle, waiting to be moved. */
    private double pendingEvicted;

    /* ------------------------------- the month ------------------------------- */

    private double jobsLost, openings, localHires, arrivalHires, arrivalsUnhired;
    private double entrants, otherExits, dropped, leftWhenBroke, evictedMoved;
    private double deaths, agedOut;
    private double benefitsPaid;
    private double insuredCap;

    /* ------------------------------- reading ------------------------------- */

    /** Everyone out of work. */
    public double getPool()          { return onEi() + offEi + unhoused; }

    /** On EI this month. */
    public double onEi() {
        double sum = 0;
        for (double v : cohortPeople) sum += v;
        return sum;
    }

    public double getOffEi()         { return offEi; }
    public double getUnhoused()      { return unhoused; }

    /** Out of work with a home to keep - who the housing match is told about. */
    public double getHoused()        { return onEi() + offEi; }

    /** Claimants in a cohort, 0 the newest. */
    public double getCohort(int monthsAgo) { return cohortPeople[monthsAgo]; }

    public double getJobsLost()      { return jobsLost; }
    public double getOpenings()      { return openings; }
    public double getLocalHires()    { return localHires; }
    public double getArrivalHires()  { return arrivalHires; }
    public double getArrivalsUnhired(){ return arrivalsUnhired; }

    /** Joined the pool with no claim: a graduate with no post, somebody turning eighteen. */
    public double getEntrants()      { return entrants; }

    /** Left the pool by some door the named flows do not name. */
    public double getOtherExits()    { return otherExits; }

    /** Claims that reached their twelfth month this month. */
    public double getDroppedOffEi()  { return dropped; }

    /** The evicted who left the city this month. */
    public double getLeftWhenBroke() { return leftWhenBroke; }

    /** The evicted who stayed, moved to the unhoused this month. */
    public double getNewlyUnhoused() { return evictedMoved - leftWhenBroke; }

    public double getDeaths()        { return deaths; }
    public double getAgedOut()       { return agedOut; }

    /** What EI paid this month, in total. The treasury's bill. */
    public double getBenefitsPaid()  { return benefitsPaid; }

    /** What one claimant draws on average this month. */
    public double getBenefitPerClaimant() {
        double on = onEi();
        return on > 0 ? benefitsPaid / on : 0;
    }

    /** The insured wage this month's cap sits at. */
    public double getInsuredCap()    { return insuredCap; }

    /* ------------------------------- the month ------------------------------- */

    /** Who the last settle evicted. Moved at the next month's demographics. */
    public void noteEvicted(double households) {
        pendingEvicted = Math.max(0, households);
    }

    /**
     * Moves last month's evicted: a share leave, the rest lose their home.
     * Called before migration, so the leavers are out of the pyramid before
     * the workforce is read.
     *
     * @return adults leaving the city, for the pyramid
     */
    public double takeEvicted() {
        double evicted = Math.min(pendingEvicted, offEi);
        pendingEvicted = 0;
        evictedMoved = evicted;
        leftWhenBroke = evicted * LEAVE_WHEN_BROKE;
        offEi -= evicted;
        unhoused += evicted - leftWhenBroke;
        return leftWhenBroke;
    }

    /**
     * Out of the pool and into prison, before the month's flows are struck
     * (2026-09-11). Jerus: offenders come "from the groups that cause it", so
     * they are taken from each group in proportion to its people times its
     * weight in Crime - the evicted at 5, those past EI at 4, claimants at 2 -
     * rather than evenly. The labour market takes them off the supply the same
     * month (PopulationManager.setImprisoned()), so the pool the flows are
     * struck against is already short by them and the residual does not take
     * them a second time.
     *
     * The released come back the other way without a call: the supply grows
     * by them, and whoever the pool gains that the named flows do not explain
     * joins it with no claim - which is Jerus's "out of work, like anyone".
     *
     * @return how many were actually taken, no more than the pool holds
     */
    public double imprison(double adults) {
        lastImprisoned = 0;
        if (!(adults > 0)) return 0;
        double on = onEi();
        double wOn = on * Crime.Cause.ON_EI.weight();
        double wOff = offEi * Crime.Cause.PAST_EI.weight();
        double wOut = unhoused * Crime.Cause.NO_HOME.weight();
        double weighted = wOn + wOff + wOut;
        if (weighted <= 0) return 0;
        double take = Math.min(adults, getPool());

        // In proportion, and whatever a group could not give is asked of the
        // rest - a small pool can be emptied, but never past empty.
        double fromOn = Math.min(on, take * wOn / weighted);
        double fromOff = Math.min(offEi, take * wOff / weighted);
        double fromOut = Math.min(unhoused, take * wOut / weighted);
        double short_ = take - fromOn - fromOff - fromOut;
        if (short_ > 1e-12) {
            double more = Math.min(offEi - fromOff, short_);
            fromOff += more; short_ -= more;
            more = Math.min(unhoused - fromOut, short_);
            fromOut += more; short_ -= more;
            more = Math.min(on - fromOn, short_);
            fromOn += more;
        }
        if (on > 0) {
            double k = Math.max(0, 1 - fromOn / on);
            for (int m = 0; m < EI_MONTHS; m++) cohortPeople[m] *= k;
        }
        offEi -= fromOff;
        unhoused -= fromOut;
        lastImprisoned = fromOn + fromOff + fromOut;
        return lastImprisoned;
    }

    private double lastImprisoned;

    /** Taken from the pool into prison this month. */
    public double getImprisoned() { return lastImprisoned; }

    /** Adults who arrived this month; they look for work next month. */
    public void noteArrivals(double adults) {
        arrivalsLooking = Math.max(0, adults);
    }

    /**
     * The month's flows, the ring, and the bill.
     *
     * @param pool          the labour market's pool this month: labour force less filled posts
     * @param filledByTier  posts filled, by tier
     * @param postsByTier   posts, by tier
     * @param wageByTier    what a filled post pays a month, by tier, today
     * @param unskilledWage what an unskilled post pays a month, today - an
     *                      arrival's insured wage, and the cap's base
     * @param adultMortality the month's adult death rate, as the pyramid used it
     * @param benefitRate   the policy's replacement rate
     */
    public void advanceMonth(double pool, double[] filledByTier, double[] postsByTier,
                             double[] wageByTier, double unskilledWage,
                             double adultMortality, double benefitRate) {

        int tiers = PayTier.values().length;
        pool = Math.max(0, pool);
        double arrivals = arrivalsLooking;
        insuredCap = Math.max(0, unskilledWage) * MAX_INSURABLE_MULTIPLE;

        jobsLost = 0; openings = 0; localHires = 0; arrivalHires = 0; arrivalsUnhired = 0;
        entrants = 0; otherExits = 0; dropped = 0; deaths = 0; agedOut = 0;

        if (!started || lastFilled == null || lastFilled.length != tiers
                || filledByTier == null || filledByTier.length != tiers) {
            /*
             * THE FIRST MONTH, or a save from before this existed. Nobody's
             * history is known, so the whole pool starts a claim at the
             * unskilled wage - a year's grace for a city that never had EI.
             */
            java.util.Arrays.fill(cohortPeople, 0);
            java.util.Arrays.fill(cohortWage, 0);
            cohortPeople[0] = Math.max(0, pool - offEi - unhoused);
            cohortWage[0] = Math.max(0, unskilledWage);
            reconcile(pool);
            remember(pool, filledByTier, postsByTier);
            strikeBenefits(benefitRate);
            return;
        }

        /* ---- jobs lost: only posts that disappeared, at their tiers' wages ---- */
        double lostWage = 0;
        double filledNow = 0, filledThen = 0;
        for (int t = 0; t < tiers; t++) {
            double fellFilled = Math.max(0, lastFilled[t] - filledByTier[t]);
            double fellPosts = postsByTier == null || lastPosts == null ? fellFilled
                    : Math.max(0, lastPosts[t] - postsByTier[t]);
            double lost = Math.min(fellFilled, fellPosts);
            jobsLost += lost;
            lostWage += lost * (wageByTier != null && t < wageByTier.length ? Math.max(0, wageByTier[t]) : 0);
            filledNow += filledByTier[t];
            filledThen += lastFilled[t];
        }

        /* ---- openings, filled pro rata between the pool and the arrivals ---- */
        double existing = getPool();
        openings = Math.max(0, filledNow - filledThen);
        double lookers = existing + arrivals;
        if (lookers > 0) {
            localHires = Math.min(existing, openings * existing / lookers);
            arrivalHires = Math.min(arrivals, openings - localHires);
        }
        arrivalsUnhired = Math.max(0, arrivals - arrivalHires);

        /* ---- the exits the pyramid makes ---- */
        deaths = existing * Math.max(0, adultMortality);
        agedOut = existing * AgeBand.ADULT.monthlyOutflowRate();

        double implied = existing - localHires - deaths - agedOut + jobsLost + arrivalsUnhired;
        double residual = pool - implied;
        if (residual >= 0) entrants = residual;
        else otherExits = -residual;

        /* ---- every group shrinks by the exits, in proportion ---- */
        double exits = localHires + deaths + agedOut + otherExits;
        double keep = existing > 0 ? Math.max(0, 1 - exits / existing) : 0;
        for (int m = 0; m < EI_MONTHS; m++) cohortPeople[m] *= keep;
        offEi *= keep;
        unhoused *= keep;

        /* ---- the ring turns: the twelfth month drops off, the month's inflow starts ---- */
        dropped = cohortPeople[EI_MONTHS - 1];
        offEi += dropped;
        for (int m = EI_MONTHS - 1; m > 0; m--) {
            cohortPeople[m] = cohortPeople[m - 1];
            cohortWage[m] = cohortWage[m - 1];
        }
        double inflow = jobsLost + arrivalsUnhired;
        cohortPeople[0] = inflow;
        cohortWage[0] = inflow > 0
                ? (lostWage + arrivalsUnhired * Math.max(0, unskilledWage)) / inflow : 0;
        offEi += entrants;

        reconcile(pool);
        remember(pool, filledByTier, postsByTier);
        strikeBenefits(benefitRate);
    }

    /** The groups sum to the pool, whatever rounding or a clamped exit did. */
    private void reconcile(double pool) {
        double total = getPool();
        if (total > pool && total > 0) {
            double k = pool / total;
            for (int m = 0; m < EI_MONTHS; m++) cohortPeople[m] *= k;
            offEi *= k;
            unhoused *= k;
        } else if (total < pool) {
            offEi += pool - total;
        }
    }

    private void remember(double pool, double[] filled, double[] posts) {
        lastPool = pool;
        lastFilled = filled == null ? null : filled.clone();
        lastPosts = posts == null ? null : posts.clone();
        started = true;
    }

    private void strikeBenefits(double benefitRate) {
        double rate = Math.max(0, benefitRate);
        benefitsPaid = 0;
        for (int m = 0; m < EI_MONTHS; m++) {
            if (cohortPeople[m] <= 0) continue;
            double insured = insuredCap > 0 ? Math.min(cohortWage[m], insuredCap) : cohortWage[m];
            benefitsPaid += cohortPeople[m] * rate * insured;
        }
    }

    /* =====================================================================
       HEALTH: the unhoused, as a share of the adult band

       Blended into the pyramid's mortality factor and the city's sick rate
       by Game - the pyramid has no sub-groups, so a group's multiplier enters
       as its share of the band.
       ===================================================================== */

    /**
     * Every band's mortality factor with its unhoused and its orphans blended
     * in: the unhoused at UNHOUSED_MORTALITY times the band's factor, the
     * orphans at the factor the band has with no care at all (or the city's,
     * if that is somehow worse), the rest at the city's.
     *
     * @param factors   by band, at the city's care coverage
     * @param uncared   by band, at zero coverage - Healthcare.mortalityFactors(0, 0, 0)
     * @param inBand    people in each band
     * @param unhoused  people with no home, by band - the orphans not among them
     * @param orphans   children no family holds, by band
     * @return the blended factors, a new array
     */
    public static double[] blendMortality(double[] factors, double[] uncared, double[] inBand,
                                          double[] unhoused, double[] orphans) {
        double[] out = factors.clone();
        for (int i = 0; i < out.length; i++) {
            if (inBand == null || i >= inBand.length || !(inBand[i] > 0)) continue;
            double orphanShare = orphans == null || i >= orphans.length ? 0
                    : Math.max(0, Math.min(1, orphans[i] / inBand[i]));
            double unhousedShare = unhoused == null || i >= unhoused.length ? 0
                    : Math.max(0, Math.min(1 - orphanShare, unhoused[i] / inBand[i]));
            double f = factors[i];
            double alone = uncared == null || i >= uncared.length ? f : Math.max(f, uncared[i]);
            out[i] = (1 - orphanShare - unhousedShare) * f
                    + unhousedShare * f * UNHOUSED_MORTALITY
                    + orphanShare * alone;
        }
        return out;
    }

    /**
     * WHO AMONG THE MONTH'S DEAD WERE ORPHANS, AND WHO HAD NO HOME.
     *
     * The pyramid does not know them as people - blendMortality() folds them
     * into their band's death rate by their share of it - so their deaths are
     * attributed the same way, from the same shares: of a band's deaths from
     * its rate, each group takes its share of the band times its own factor
     * over the blended one; of its deaths from staying sick, which the ring
     * spreads over the band alike, each group takes its share of the band.
     * Jerus, 2026-09-11: a running total of who has died, by age and for the
     * orphans and the unhoused, on the graphs.
     *
     * @param factors  every band's factor at the city's care, before the blend
     * @param uncared  every band's factor with no care at all
     * @param dying    each band's deaths from its rate, sickness included
     * @param illness  of which sickness
     * @return { orphans who died, unhoused who died }, across the bands
     */
    public static double[] attributeDeaths(double[] factors, double[] uncared, double[] inBand,
                                           double[] unhoused, double[] orphans,
                                           double[] dying, double[] illness) {
        double[] blended = blendMortality(factors, uncared, inBand, unhoused, orphans);
        double orphanDead = 0, unhousedDead = 0;
        for (int i = 0; i < factors.length; i++) {
            if (inBand == null || i >= inBand.length || !(inBand[i] > 0)) continue;
            if (dying == null || i >= dying.length) continue;
            double orphanShare = orphans == null || i >= orphans.length ? 0
                    : Math.max(0, Math.min(1, orphans[i] / inBand[i]));
            double unhousedShare = unhoused == null || i >= unhoused.length ? 0
                    : Math.max(0, Math.min(1 - orphanShare, unhoused[i] / inBand[i]));
            double f = factors[i];
            double alone = uncared == null || i >= uncared.length ? f : Math.max(f, uncared[i]);
            double ill = illness == null || i >= illness.length ? 0 : Math.max(0, illness[i]);
            double fromRate = Math.max(0, dying[i] - ill);
            if (blended[i] > 0) {
                orphanDead += fromRate * orphanShare * alone / blended[i];
                unhousedDead += fromRate * unhousedShare * f * UNHOUSED_MORTALITY / blended[i];
            }
            orphanDead += ill * orphanShare;
            unhousedDead += ill * unhousedShare;
        }
        return new double[] { orphanDead, unhousedDead };
    }

    /**
     * A band's mortality factor with a share of it unhoused.
     *
     * @param factor        the band's factor at the city's care coverage
     * @param unhousedShare of the band, 0-1
     */
    public static double withUnhoused(double factor, double unhousedShare) {
        double s = Math.max(0, Math.min(1, unhousedShare));
        return factor * (1 + s * (UNHOUSED_MORTALITY - 1));
    }

    /* ------------------------------- saving ------------------------------- */

    private static final int STATE_LENGTH = EI_MONTHS * 2 + 6 + PayTier.values().length * 2 + 12;

    public double[] toSaveArray() {
        int tiers = PayTier.values().length;
        double[] out = new double[STATE_LENGTH];
        int i = 0;
        for (double v : cohortPeople) out[i++] = v;
        for (double v : cohortWage) out[i++] = v;
        out[i++] = offEi;
        out[i++] = unhoused;
        out[i++] = lastPool;
        out[i++] = started ? 1 : 0;
        out[i++] = arrivalsLooking;
        out[i++] = pendingEvicted;
        for (int t = 0; t < tiers; t++) out[i++] = lastFilled != null && t < lastFilled.length ? lastFilled[t] : 0;
        for (int t = 0; t < tiers; t++) out[i++] = lastPosts != null && t < lastPosts.length ? lastPosts[t] : 0;
        // The month, for the first frame of a reloaded screen.
        out[i++] = jobsLost;      out[i++] = openings;     out[i++] = localHires;
        out[i++] = arrivalHires;  out[i++] = arrivalsUnhired; out[i++] = entrants;
        out[i++] = otherExits;    out[i++] = dropped;      out[i++] = leftWhenBroke;
        out[i++] = evictedMoved;  out[i++] = benefitsPaid; out[i]   = insuredCap;
        return out;
    }

    /** @return false if the array is not this build's shape; nothing is changed, and the first month seeds it */
    public boolean restore(double[] saved) {
        if (saved == null || saved.length != STATE_LENGTH) return false;
        int tiers = PayTier.values().length;
        int i = 0;
        for (int m = 0; m < EI_MONTHS; m++) cohortPeople[m] = saved[i++];
        for (int m = 0; m < EI_MONTHS; m++) cohortWage[m] = saved[i++];
        offEi = saved[i++];
        unhoused = saved[i++];
        lastPool = saved[i++];
        started = saved[i++] != 0;
        arrivalsLooking = saved[i++];
        pendingEvicted = saved[i++];
        lastFilled = new double[tiers];
        lastPosts = new double[tiers];
        for (int t = 0; t < tiers; t++) lastFilled[t] = saved[i++];
        for (int t = 0; t < tiers; t++) lastPosts[t] = saved[i++];
        jobsLost = saved[i++];      openings = saved[i++];     localHires = saved[i++];
        arrivalHires = saved[i++];  arrivalsUnhired = saved[i++]; entrants = saved[i++];
        otherExits = saved[i++];    dropped = saved[i++];      leftWhenBroke = saved[i++];
        evictedMoved = saved[i++];  benefitsPaid = saved[i++]; insuredCap = saved[i];
        return true;
    }

    public void reset() {
        java.util.Arrays.fill(cohortPeople, 0);
        java.util.Arrays.fill(cohortWage, 0);
        offEi = 0; unhoused = 0; lastPool = 0; started = false;
        lastFilled = null; lastPosts = null;
        arrivalsLooking = 0; pendingEvicted = 0;
        jobsLost = 0; openings = 0; localHires = 0; arrivalHires = 0; arrivalsUnhired = 0;
        entrants = 0; otherExits = 0; dropped = 0; leftWhenBroke = 0; evictedMoved = 0;
        deaths = 0; agedOut = 0; benefitsPaid = 0; insuredCap = 0;
        lastImprisoned = 0;
    }

    /** Wages and the bill in the new unit. People do not move. */
    public void redenominate(double scale) {
        for (int m = 0; m < EI_MONTHS; m++) cohortWage[m] *= scale;
        benefitsPaid *= scale;
        insuredCap *= scale;
    }
}
