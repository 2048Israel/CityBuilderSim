package ham.citybuildersim;

/**
 * Why people move to this city, and the much narrower question of why they leave.
 *
 * This class is what replaced `min(housing, jobs * 2.25)`. That expression was
 * the population: derived fresh every month from nothing, with no memory, so a
 * finished tower filled instantly and a demolished one erased its residents. It
 * is now a TARGET that migration chases, and the chasing is what gives the city
 * inertia.
 *
 * ==================== JERUS'S SPEC ====================
 *
 *   "make it so its city housing space and current jobs available. If a city is
 *    full but has jobs, they'll still move in."
 *
 *   "homes pull too, but not as much as jobs. Crowding slows but jobs is still
 *    the main factor."
 *
 *   "they only leave if the respective job tier cashflow is declining for 12
 *    months straight or is zero."
 *
 * ======================================================
 *
 * THE PULL is a weighted target, jobs three parts to housing one:
 *
 *   jobs  -> every post supports RESIDENTS_PER_JOB people, worker and household
 *   homes -> the people the city's residential buildings comfortably hold
 *
 * Weighting them rather than taking the smaller of the two is the whole change
 * Jerus asked for. A minimum makes housing a GATE - build one house too few and
 * the city stops dead no matter how many jobs are going begging. A weighted sum
 * makes it a PULL: a city with jobs and no houses still attracts people, they
 * just arrive into a shortage. Which is what actually happens, and what the
 * flatshare model in FamilyModel was built to absorb.
 *
 * Housing gets a say twice, and the second is the interesting one: it also
 * DAMPS. A crowded city keeps attracting people, more slowly, until there is
 * physically nowhere left to put them - and that point is asked of FamilyModel
 * rather than typed in here, because it has to be the same line as the one where
 * the squeeze runs out of valves. See crowdingFactor().
 *
 * THE PUSH is deliberately hard to trigger. A negative gap does NOT empty a
 * city; a city with more people than jobs is a city with unemployment, not a
 * city with an exodus. People only leave when a pay tier has been shrinking for
 * a solid year or has stopped paying anything at all - which means a bust takes
 * a year to start and then years to play out, and a bad month is just a bad
 * month. That asymmetry is the point of the twelve-month gate.
 */
public class Migration {

    /* ------------------------------- the dials ------------------------------- */

    /* =====================================================================
       HOW MANY RESIDENTS A JOB SUPPORTS

       This used to be the constant 2.25, inherited from Jerus's original
       `totalJobs * (1 + adultPercent) * 1.5` with adultPercent at .5. Nobody
       ever typed 2.25; it was 1.5 x 1.5, and what it MEANT was buried.

       What it meant, reconstructed: with half the city working, two residents
       staff one job exactly. At exactly two the city would have precisely enough
       workers for precisely the jobs - no unemployment, every adult employed,
       and a labour market with no slack at all, oscillating between fully
       staffed and one person short. 2.25 is 12.5% more workers than posts, which
       is about 11% unemployment.

       So 2.25 = 1.125 / 0.5, and written that way it derives itself.

       WHY IT HAD TO STOP BEING A CONSTANT. The workforce is no longer half the
       city - it is the actual adults, around 57.5% in a settled one. The same
       2.25 then supplies 1.294 workers per post rather than 1.125, and the
       number that meant "11% unemployment" quietly came to mean 23%. It drifted
       without anyone touching it, because the thing underneath it moved.

       Now it follows the age structure. A city that ages has fewer workers per
       resident and therefore needs more residents per job, and the target moves
       with it instead of disagreeing with it in silence. At a 50% adult share
       this returns exactly 2.25 - Jerus's number, reconstructed rather than
       replaced.
       ===================================================================== */

    /** Workers per post the city aims to have, over and above one each. */
    public static final double TARGET_LABOUR_SLACK = .125;

    /**
     * Below this the arithmetic stops meaning anything - a city that is 10%
     * adults would demand ten residents per job and grow without limit. Nothing
     * plausible comes near it; it is here so nothing implausible can.
     */
    public static final double MIN_ADULT_SHARE = .25;

    public static double residentsPerJob(double adultShare) {
        double share = adultShare > 0 ? adultShare
                : PopulationCohorts.equilibriumShare(AgeBand.ADULT);
        return (1 + TARGET_LABOUR_SLACK) / Math.max(MIN_ADULT_SHARE, share);
    }

    /** Jobs are the main factor, per Jerus. Homes pull, but less. */
    public static final double JOB_WEIGHT  = .75;
    public static final double HOME_WEIGHT = .25;

    /* ===================================================================
       WHY A CITY WITH GOOD SENIOR CARE IS WORTH MOVING TO

       Jerus's, and it is the answer to a problem the model created for
       itself. Seniors are pure burden here: they draw a pension, occupy a
       home, need the most expensive care in the game and work not at all. An
       ageing pyramid was therefore something to be endured, and the only
       rational play was to hope your city stayed young - which is not a game,
       it is a wait.

       So senior care buys something. At full coverage the city is 30% more
       attractive to everybody, not just to the old: places that look after
       their parents are places people are willing to raise children. It is a
       multiplier on the TARGET rather than on the arrival rate, because the
       claim is about how big a city these conditions support, not how fast it
       fills - the same distinction JOB_WEIGHT and ARRIVAL_RATE already make.
       =================================================================== */

    /** How much more attractive full senior coverage makes the city. */
    public static final double SENIOR_CARE_PULL = .30;

    /* =====================================================================
       WHO ARRIVES, NOT JUST HOW MANY

       Until now an arrival was an undifferentiated person. The city could not
       be short of DOCTORS, only of people - which is why PopulationManager was
       able to staff two hundred doctor posts out of a pool of labourers.

       Now everybody who moves in has a high school diploma and nothing more
       comes free: every band above it starts at zero and is BOUGHT, at a
       premium over the going rate, out of a world that has few graduates to
       spare (WageBand.arrivalCeiling). A small city can import its doctor while
       a big one cannot import two hundred at any price. That is the whole
       reason schools are worth building, and it needs no rule to enforce it.

       And nobody arrives WITHOUT a diploma either. The unskilled band is fed
       only by this city's own children ageing out of the teen band while the
       high schools were full or missing - so it is a report card on the
       schools, not an import.
       ===================================================================== */

    /**
     * How far a premium has travelled from the going rate to the ceiling.
     *
     * 0 at the going rate, 1 at LabourMarket.MAX_MULTIPLE, straight line
     * between. It is what turns a wage into a REASON TO MOVE for everybody
     * above a diploma: at the going rate there is none, because the going rate
     * is what this trade costs everywhere.
     *
     * Straight rather than curved on purpose. The old model raised the premium
     * to the power 1.6, which reads as "money is worth more than
     * proportionally" - true enough - but it was multiplying a band's fixed
     * world share, so even a band paying 0.70x (nobody hiring, wage on the
     * floor) still drew at 0.56 strength forever. That was the bug Jerus kept
     * finding by playing. Anchoring at 1.00 instead of at 0 is the whole fix;
     * the shape above it is then a tuning question, and a line is the version
     * whose numbers a player can predict.
     *
     * At 2x the going rate a band draws at a third of its ceiling; at 4x, all
     * of it.
     */
    public static double reach(double premium) {
        double span = LabourMarket.MAX_MULTIPLE - 1;
        if (span <= 0) return 0;
        return Math.max(0, Math.min(1, (premium - 1) / span));
    }

    /**
     * What share of a band's unemployable surplus leaves each month, once its
     * wage has stopped falling.
     *
     * THE PUSH FACTOR THE MODEL WAS MISSING (backlog L2: "nothing pushes them
     * out again"). A wage that can still fall is a market still trying to clear;
     * a wage pinned at its floor with workers to spare is a market that has
     * given up, and the adjustment has to happen in people instead. That is
     * precisely what a binding minimum wage does in the real world.
     *
     * Small on purpose. This is a drift out of a town with no work, not an
     * evacuation - and it compounds, so 2% a month is a quarter of the surplus
     * gone within a year.
     */
    public static final double SURPLUS_DEPARTURE_RATE = .02;

    /*
     * MAX_LICENSED_ARRIVALS is gone, and it is worth saying why rather than
     * just deleting it. It capped licence holders at 55% of the graduates they
     * were CARVED OUT OF, because four professions each multiplying a fixed
     * share by premium^1.6 could between them exceed the graduates that were
     * arriving at all. Professions are no longer a subset: each one is its own
     * pull, and the graduate it brings is ADDED to the university band. The cap
     * cannot be exceeded because there is nothing left to exceed - a licence
     * holder is one of the graduates by construction, counted once. LabourCheck
     * asserts that invariant so the deletion stays honest.
     */

    /**
     * What share of a band still moves here when there is no work at its level.
     *
     * THE WAGE ALONE WAS NOT A DETERRENT, and that is what Jerus found: a city
     * with no schools and almost no graduate posts still filled up with
     * graduates. A band with nothing hiring has its wage pinned at the bottom
     * of its range, which is only 0.70x base - and 0.70^1.6 is 0.56, so an
     * utterly unwanted skill was still arriving at more than half strength
     * forever.
     *
     * The missing term is not price, it is OPPORTUNITY. Nobody moves across the
     * country for a job that does not exist, whatever it pays. So the pull is
     * now also weighted by a band's chance of finding work at its own level -
     * its posts against the people already holding them.
     *
     * Not to zero, though. People move for families and weather and a hundred
     * things that are not a job, and a band that can never receive anybody is a
     * city that can never change its mind.
     */
    public static final double OPPORTUNITY_FLOOR = .15;

    /** The multiplier on the target, given senior-care coverage. */
    public static double seniorCarePull(double seniorCoverage) {
        return 1 + SENIOR_CARE_PULL * Math.max(0, Math.min(1, seniorCoverage));
    }

    /* =====================================================================
       AND A CITY NOBODY CAN AFFORD TO LIVE IN IS A CITY PEOPLE DO NOT MOVE TO
       ---------------------------------------------------------------------
       Jerus, asked whether expensive housing should change who moves in: "Yes,
       and it pushes people out too".

       Rent became a market on 2026-09-07 - a cost floor and a scarcity
       multiple, with NO CEILING, deliberately. Unbounded is only a defensible
       specification if something stops it, and the something has to be people:
       a price nobody responds to is not a price, it is a number on a screen,
       and a rent that can rise for ever in a city that never notices is a
       runaway with no brake in it.

       There are two responses and only one of them is new.

       THE PUSH ALREADY EXISTED. Rent is a fixed cost on the household balance
       sheet - HouseholdBalance subtracts it before food - so a household that
       cannot meet it borrows, and one that cannot service the borrowing is
       discharged, and a share of the discharged leave the city. That arrives
       here through setBankruptcyDepartures() and has been audited since the
       day it was written. Dear rent therefore empties a city already, through
       the household's books rather than through a rule about rent, which is
       the better way for it to happen: the city loses the people who cannot
       pay, not a fixed percentage of everybody.

       THE PULL IS NEW, and it is this. Somebody deciding whether to move here
       is not on the balance sheet yet and cannot go bankrupt in advance; they
       look at what a flat costs against what the work pays, and they do not
       come. That is a different mechanism from the push and it fires much
       earlier - long before anyone is discharged, a city can simply stop being
       worth moving to.

       Struck against TARGET_RENT_BURDEN, which is the game's own statement of
       what rent is supposed to cost a working household, so this needs no
       number of its own for "too dear": too dear is dearer than the game
       already says it should be. At the target the multiplier is exactly 1 and
       nothing changes, which is what makes it safe to add to a model that was
       balanced without it.
       ===================================================================== */

    /**
     * How much of the affordability excess turns into people not coming.
     *
     * At 1.0 a city where rent takes twice its target share of income is half
     * as attractive as one where it takes the target share. Hyperbolic rather
     * than linear so it can never reach zero: there is always somebody who will
     * move to an expensive city for the work, which is the whole reason
     * expensive cities exist.
     */
    public static final double PRICED_OUT_WEIGHT = 1.0;

    /**
     * The multiplier dear housing puts on how big a city these conditions
     * support. 1.0 at or below the target burden; falling above it.
     *
     * @param rentBurden rent as a share of take-home, from HouseholdAccounts
     */
    public static double affordabilityPull(double rentBurden) {
        if (!(rentBurden > 0)) return 1;            // no households yet, or no rent
        double excess = (rentBurden - CommercialHandler.TARGET_RENT_BURDEN)
                / CommercialHandler.TARGET_RENT_BURDEN;
        if (excess <= 0) return 1;
        return 1 / (1 + PRICED_OUT_WEIGHT * excess);
    }

    /**
     * What rent costs a household here, as a share of take-home.
     *
     * Carried in rather than derived, like bankruptcyPush and for the same
     * reason: it is a figure about the month that has just been settled, held
     * by HouseholdAccounts, and re-deriving it here would be reading a flow off
     * the state a month ended in.
     */
    private double rentBurden;

    public void setRentBurden(double burden) { this.rentBurden = Math.max(0, burden); }

    public double getLastAffordabilityPull() { return lastAffordabilityPull; }

    private double lastAffordabilityPull = 1;

    /**
     * How much of the gap closes each month.
     *
     * Arrivals are fast and departures are slow, which is how cities actually
     * behave - a boom fills in months and a bust empties over years. At 15% a
     * month a city closes most of a gap inside half a year; at 5% a shrinking
     * one takes about a year to shed a quarter of the surplus, and that is on
     * top of the twelve months it takes to qualify at all.
     */
    public static final double ARRIVAL_RATE   = .15;
    public static final double DEPARTURE_RATE = .05;

    /** Consecutive months of falling wages before a tier's people give up. */
    public static final int DECLINE_MONTHS = 12;

    /* ------------------------------ what it carries ------------------------------ */

    private static final int TIERS = PayTier.values().length;

    /**
     * A rolling year of each tier's wage bill, oldest at index 0.
     *
     * SAVED, and it has to be. A streak is a flow, and this codebase has been
     * caught four times now by the same thing: you cannot reconstruct a flow from
     * the state a month ended in. A reloaded city that forgot its history would
     * have every tier's streak reset to zero and could not shed a single person
     * for a year, which is exactly the kind of quiet difference between a saved
     * game and a played one that this project keeps hunting down.
     */
    private final double[][] history = new double[TIERS][DECLINE_MONTHS];
    private final int[] decliningStreak = new int[TIERS];
    private int monthsRecorded;

    /* Last month's working, purely so the screen can show it. */
    /** The skills of the people who moved in this month, by band. */
    private double[] lastArrivalMix = new double[WageBand.values().length];

    /**
     * Each band's chance of work at its own level, as last computed.
     *
     * Kept so it can be asserted and shown rather than only felt. It is the
     * term that tells a graduate there is nothing here for them - the wage
     * cannot say that on its own, because a wage on the floor still looks like
     * a wage.
     */
    private final double[] lastOpportunity = new double[WageBand.values().length];

    /**
     * ...and which of them already hold a professional licence.
     *
     * ALREADY COUNTED inside the university band of lastArrivalMix: a doctor
     * who moves here is one graduate, counted once, about whom a second thing
     * is true. Their PULL is their own (see composeArrivals) - a hospital
     * shortage brings doctors whether or not anything is drawing graduates -
     * but the head they arrive on is a university head, not an extra one.
     *
     * Without this a city with no medical school could never have a doctor at
     * all - not slowly, never - because migration brings a level and the
     * licence is not a level. That would have taken the whole "import while you
     * are small, train once you are big" arc out of the game on the day the
     * professional schools arrived.
     */
    private double[] lastArrivalLicences = new double[JobType.values().length];

    /**
     * ...and of the ones who left, which is NOT the same shape.
     *
     * People pushed out by a pinned wage leave from the band that is
     * oversupplied. If departures were spread across the workforce in
     * proportion, an unskilled glut would take graduates with it and never
     * actually clear - the push factor would remove people without removing the
     * surplus, which is the mechanism failing while appearing to work.
     */
    private double[] lastDepartureMix = new double[WageBand.values().length];

    private double lastTarget;
    private double lastArrivals;
    private double lastDepartures;
    private double lastCrowding = 1;
    private double lastDecliningShare;
    private double lastSeniorPull = 1;
    private double lastResidentsPerJob = residentsPerJob(0);

    /* ------------------------------- reading ------------------------------- */

    public double getLastTarget()         { return lastTarget; }
    /** What senior care multiplied the target by. 1 when there is none. */
    public double getLastSeniorPull()     { return lastSeniorPull; }
    public double getLastArrivals()       { return lastArrivals; }
    public double[] getLastArrivalMix()   { return lastArrivalMix; }
    public double[] getLastOpportunity()  { return lastOpportunity; }
    public double[] getLastArrivalLicences() { return lastArrivalLicences; }
    public double[] getLastDepartureMix() { return lastDepartureMix; }
    public double getLastDepartures()     { return lastDepartures; }
    public double getLastCrowding()       { return lastCrowding; }
    public double getLastDecliningShare() { return lastDecliningShare; }
    public double getLastResidentsPerJob() { return lastResidentsPerJob; }
    public double getLastNet()            { return lastArrivals - lastDepartures; }

    public int getDecliningStreak(PayTier tier) {
        return decliningStreak[tier.ordinal()];
    }

    /** True once there is a full year of history to judge a streak against. */
    public boolean hasFullHistory() {
        return monthsRecorded >= DECLINE_MONTHS;
    }

    /**
     * True if this tier's people are entitled to leave.
     *
     * Two ways in, both Jerus's: a full year of consecutive decline, or a tier
     * that used to pay something and now pays nothing. The second is not
     * redundant - a plant that closes takes its tier to zero in one month, and
     * waiting a further year to notice would be absurd.
     */
    public boolean isDeclining(PayTier tier) {
        if (!hasFullHistory()) return false;
        int t = tier.ordinal();
        if (decliningStreak[t] >= DECLINE_MONTHS) return true;
        return newest(t) <= 0 && oldest(t) > 0;
    }

    private double newest(int t) { return history[t][DECLINE_MONTHS - 1]; }
    private double oldest(int t) { return history[t][0]; }

    /* ------------------------------- recording ------------------------------- */

    /**
     * Files this month's wage bill per tier and updates every streak.
     *
     * Called once a month, before the migration is worked out, so the decision
     * sees the month it is deciding about.
     */
    public void recordWages(double[] wagePerTier) {
        if (wagePerTier == null || wagePerTier.length != TIERS) {
            return;   // refused whole, per the standing rule on state arrays
        }

        for (int t = 0; t < TIERS; t++) {
            double previous = newest(t);

            System.arraycopy(history[t], 1, history[t], 0, DECLINE_MONTHS - 1);
            history[t][DECLINE_MONTHS - 1] = wagePerTier[t];

            // A streak needs something to have declined FROM, so month one of a
            // tier's existence is not a decline - it is an arrival.
            if (monthsRecorded > 0 && wagePerTier[t] < previous) {
                decliningStreak[t]++;
            } else {
                decliningStreak[t] = 0;
            }
        }
        monthsRecorded++;
    }

    /**
     * How much of the city's payroll sits in tiers whose people may leave.
     *
     * Weighted by what each tier was worth a YEAR AGO rather than today, and
     * that is the load-bearing detail. FamilyModel reassigns households to tiers
     * every month from the current job mix, so the month a tier's jobs vanish its
     * households vanish with them - weighting by today's payroll would give a
     * collapsed tier a weight of zero and let a city lose its entire steel
     * industry without losing a single resident. The people who leave are the
     * people who were there before it collapsed, so the weight is what the tier
     * was worth before it collapsed.
     */
    public double decliningShare() {
        if (!hasFullHistory()) return 0;

        double thenTotal = 0;
        for (int t = 0; t < TIERS; t++) thenTotal += oldest(t);
        if (thenTotal <= 0) return 0;

        double share = 0;
        for (PayTier tier : PayTier.values()) {
            if (isDeclining(tier)) share += oldest(tier.ordinal()) / thenTotal;
        }
        return Math.min(1, Math.max(0, share));
    }

    /**
     * How far a tier has fallen over the year, 0 to 1.
     *
     * The gate says WHETHER a trade is dying; this says HOW BADLY, and both are
     * needed. A tier that has slipped 3% across twelve months and one that has
     * shut its doors both pass the gate, and treating them the same would empty a
     * city over a rounding error.
     */
    public double severity(PayTier tier) {
        if (!isDeclining(tier)) return 0;
        int t = tier.ordinal();
        double then = oldest(t);
        if (then <= 0) return 0;
        return Math.max(0, Math.min(1, 1 - newest(t) / then));
    }

    /**
     * The share of the city's payroll that has actually been destroyed, as
     * opposed to merely sitting in a tier that qualifies.
     *
     * This is what departures are charged against: each tier's weight a year ago
     * times how much of it has gone. A city where one small trade has slipped
     * loses almost nobody; a city whose main employer has closed loses people
     * steadily.
     */
    public double decliningPressure() {
        if (!hasFullHistory()) return 0;

        double thenTotal = 0;
        for (int t = 0; t < TIERS; t++) thenTotal += oldest(t);
        if (thenTotal <= 0) return 0;

        double pressure = 0;
        for (PayTier tier : PayTier.values()) {
            pressure += oldest(tier.ordinal()) / thenTotal * severity(tier);
        }
        return Math.min(1, Math.max(0, pressure));
    }

    /* ------------------------------- deciding ------------------------------- */

    /**
     * How crowded the city is, as a multiplier on arrivals: 1 is room to spare,
     * 0 is physically full.
     *
     * The zero point is asked of FamilyModel, not chosen here. It is the fewest
     * homes the current households could crowd into once every single adult has
     * taken a flatshare and every doubling the squeeze allows has happened -
     * below that line somebody would have nowhere to go, and nobody is homeless
     * in this model. So arrivals reach zero at exactly the same point the
     * crowding valves run out, and PopulationCheck asserts they agree.
     *
     * Between one-home-each and that floor it falls off linearly. There is no
     * defence of linear beyond its being the shape that makes the endpoints mean
     * what they say; the endpoints are the part that matters.
     */
    public double crowdingFactor(int homes, FamilyModel families) {
        double comfortable = families.totalHouseholds();
        if (comfortable <= 0) return 1;               // nobody here yet

        double floor = families.minimumHomesTolerable();
        if (homes >= comfortable) return 1;
        if (homes <= floor) return 0;

        return (homes - floor) / (comfortable - floor);
    }

    /**
     * The month's net migration: positive is people arriving.
     *
     * @param population       who lives here now
     * @param totalJobs        posts the city's buildings offer
     * @param householdCapacity people its residential buildings comfortably hold
     * @param homes            front doors, which is a different number
     * @param families         the household mix, for the crowding floor
     */
    public double monthlyNet(int population, int totalJobs, int householdCapacity,
                             int homes, FamilyModel families, double adultShare) {
        return monthlyNet(population, totalJobs, householdCapacity, homes,
                families, adultShare, 0);
    }

    /**
     * The same, with the draw good senior care adds.
     *
     * @param seniorCoverage places in senior care against the seniors who need
     *                       them; 0 in a city that has built none, which is
     *                       exactly how every city behaved before this existed
     */
    public double monthlyNet(int population, int totalJobs, int householdCapacity,
                             int homes, FamilyModel families, double adultShare,
                             double seniorCoverage) {

        lastResidentsPerJob = residentsPerJob(adultShare);
        double jobTarget  = totalJobs * lastResidentsPerJob;
        double homeTarget = householdCapacity;

        lastSeniorPull = seniorCarePull(seniorCoverage);
        lastAffordabilityPull = affordabilityPull(rentBurden);
        lastTarget = (JOB_WEIGHT * jobTarget + HOME_WEIGHT * homeTarget)
                * lastSeniorPull * lastAffordabilityPull;
        lastArrivals = 0;
        lastDepartures = 0;
        lastCrowding = 1;
        lastDecliningShare = 0;

        double gap = lastTarget - population;
        lastCrowding = crowdingFactor(homes, families);

        /*
         * GROSS FLOWS, NOT NET. The two directions are computed independently
         * and netted at the end, and that is a deliberate correction rather than
         * how it was first written.
         *
         * The first version made departures the NEGATIVE BRANCH of arrivals -
         * people could only leave a city that was already oversized. Measured
         * over four thousand months: 53,528 people moved in and NOBODY EVER
         * LEFT. The decline gate opened fifteen times and never once mattered,
         * because a growing city never satisfied the other half of the
         * condition. An entire mechanic existed only in the harness.
         *
         * Both things happen at once in a real city, and they have to here: a
         * town whose steelworks has closed loses steelworkers even while its
         * shops are hiring. The cost is that a city can now bleed people while
         * its headline number still rises, which is exactly what a place hollowing
         * out looks like from the inside.
         */
        lastArrivals = gap > 0 ? gap * ARRIVAL_RATE * lastCrowding : 0;

        /*
         * And the push. A city with more people than jobs is NOT by itself a
         * reason for anybody to leave - that is unemployment, not an exodus.
         * Somebody only goes when the tier they earn in has been shrinking for a
         * full year or has stopped paying at all, and then in proportion to how
         * much of it has actually gone.
         */
        lastDecliningShare = decliningShare();
        lastDepartures = population * decliningPressure() * DEPARTURE_RATE;

        // Never evacuate. A month that would remove more people than live here
        // is arithmetic going wrong, not a city emptying.
        lastDepartures = Math.min(lastDepartures, population);

        return lastArrivals - lastDepartures;
    }

    /**
     * The same month, with a labour market behind it.
     *
     * Two things the volume-only version cannot do: give the arrivals a skill
     * mix, and let a city that cannot employ its people lose them.
     *
     * @param market the wages, for the premium that draws people
     * @param people the workforce, for the surplus that pushes them out
     */
    /**
     * Households the balance sheet discharged this month, whose people are
     * leaving because they are broke.
     *
     * Set by Game before the month's migration is struck, and consumed by it -
     * a push that has nothing to do with wages or with the labour market, so it
     * is carried in rather than derived here. Cleared on use, because it is a
     * FLOW: a month that forgot to set it must not re-spend last month's.
     */
    private double bankruptcyPush;

    public void setBankruptcyDepartures(double people) {
        this.bankruptcyPush = Math.max(0, people);
    }

    public double getLastBankruptcyDepartures() { return lastBankruptcyPush; }

    private double lastBankruptcyPush;

    public double monthlyNet(int population, int totalJobs, int householdCapacity,
                             int homes, FamilyModel families, double adultShare,
                             double seniorCoverage,
                             LabourMarket market, PopulationManager people) {

        double net = monthlyNet(population, totalJobs, householdCapacity, homes,
                families, adultShare, seniorCoverage);

        composeArrivals(market, people);

        if (market == null || people == null) return net;

        /*
         * THE PUSH. Counted band by band rather than city-wide, because "there
         * are more people than jobs" is not a reason to leave - that is
         * unemployment, and the existing decline term already covers a trade
         * actually dying. What makes somebody go is that their OWN line of work
         * is oversupplied and has stopped paying any better for it.
         */
        java.util.Arrays.fill(lastDepartureMix, 0);

        double pushed = 0;
        for (WageBand band : WageBand.values()) {
            if (!market.isPinned(band)) continue;

            /*
             * WEIGHTED BY MOBILITY, and that is the difference between a model
             * and a caricature. A graduate with no graduate work does not sit
             * in the city being unemployed at the same rate a labourer does -
             * their market is the whole country, so they go, at roughly twice
             * the rate. See WageBand.mobility() for the measurement.
             */
            double leaving = people.surplusInBand(band)
                    * SURPLUS_DEPARTURE_RATE * band.mobility();
            lastDepartureMix[band.ordinal()] += leaving;
            pushed += leaving;
        }

        /*
         * The decline-driven departures are not about any one band, so the
         * TOTAL stays exactly as it was - that number is tuned, and this is not
         * the place to change how many people leave a dying trade. What changes
         * is WHICH of them go: weighted by mobility as well as by headcount, so
         * the graduates are over-represented among the leavers exactly as they
         * are in life.
         */
        double[] share = people.getBandShare();
        double weighted = 0;
        for (WageBand band : WageBand.values()) {
            weighted += share[band.ordinal()] * band.mobility();
        }
        for (WageBand band : WageBand.values()) {
            double slice = weighted > 0
                    ? share[band.ordinal()] * band.mobility() / weighted
                    : share[band.ordinal()];
            lastDepartureMix[band.ordinal()] += lastDepartures * slice;
        }

        /*
         * AND THE ONES WHO WENT BROKE.
         *
         * Spread on the population's own band share and NOT weighted by
         * mobility, unlike every other leaver here. The mobility weighting says
         * a graduate with no graduate work goes looking elsewhere because their
         * market is national; a family that has just been discharged is not
         * choosing between markets, it is leaving because it cannot pay the
         * rent. Weighting it would have made bankruptcy a graduate phenomenon,
         * which is the opposite of what it is.
         */
        lastBankruptcyPush = Math.min(bankruptcyPush, Math.max(0, population - lastDepartures));
        bankruptcyPush = 0;
        if (lastBankruptcyPush > 0) {
            for (WageBand band : WageBand.values()) {
                lastDepartureMix[band.ordinal()] += lastBankruptcyPush * share[band.ordinal()];
            }
        }
        pushed += lastBankruptcyPush;

        double before = lastDepartures;
        lastDepartures = Math.min(lastDepartures + pushed, population);

        // Scaled back together if the evacuation guard bit, so the mix always
        // sums to the number of people who actually left.
        double asked = before + pushed;
        if (asked > lastDepartures && asked > 0) {
            double keep = lastDepartures / asked;
            for (int b = 0; b < lastDepartureMix.length; b++) lastDepartureMix[b] *= keep;
        }
        return lastArrivals - lastDepartures;
    }

    /**
     * A band's chance of work at its own level, as a multiplier on its pull.
     *
     * @param open  posts its members can actually be put into
     * @param queue everybody competing for them - the band's own workers PLUS
     *              everyone above who came down, which is the whole point
     */
    public static double opportunity(double open, double queue) {
        double chance = queue > 0 ? Math.min(1, open / queue) : 1;
        return OPPORTUNITY_FLOOR + (1 - OPPORTUNITY_FLOOR) * chance;
    }

    /**
     * Splits this month's arrivals across the skill bands.
     *
     * The world's own mix, weighted by what the city is paying over the going
     * rate. Note it is the PREMIUM and not the wage: a city paying a doctor
     * eight thousand is not thereby attractive to doctors, because eight
     * thousand is simply what doctors cost. Only paying MORE than that moves
     * anybody, which is what makes the number the player can see - the premium
     * on the People screen - the one that actually does something.
     */
    private void composeArrivals(LabourMarket market, PopulationManager people) {
        double[] weight = new double[WageBand.values().length];
        double[] licenceWeight = new double[lastArrivalLicences.length];
        double total = 0;

        // Staffable, not raw: an empty doctor post is not an opportunity for a
        // graduate who has no licence, and treating it as one made a hospital
        // shortage pull ordinary graduates.
        double[] posts = people == null ? null : people.staffablePostsByBand();
        // SUPPLY, not the band's own workers. The people you are competing
        // with for a diploma job are every diploma-holder in town PLUS every
        // graduate who could not find graduate work and came down for it -
        // which is the whole cascade, and it is the difference between a
        // market that looks open and one that is full. See below.
        double[] here  = people == null ? null : people.supplyByBand();

        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();

            /*
             * The chance of working at your own level if you come. A band with
             * three times as many people as posts is one you arrive in to
             * labour, whatever your degree says - and that is a reason not to
             * come, which the wage on its own never expressed.
             *
             * COUNTED AGAINST THE WHOLE QUEUE, cascade included (Jerus, 2026-09-07:
             * "demand isn't just those who don't have a diploma but also those
             * left over from above"). It read the band's OWN workers before,
             * which is the one number that cannot see the competition: a city
             * whose colleges have flooded the diploma jobs with graduates
             * showed an incoming diploma-holder a wide-open market, because
             * the people already taking those jobs were filed one band up.
             *
             * The wage already knew - LabourMarket prices tightness against
             * supplyByBand() - so the price said full and the opportunity said
             * empty, out of the same city, in the same month. One of them was
             * reading the wrong array.
             */
            double opportunity = opportunity(
                    posts == null ? 0 : posts[b],
                    here  == null ? 0 : here[b]);
            lastOpportunity[b] = opportunity;

            /*
             * THE DIPLOMA BAND IS THE BASE AND IS NOT COMPETED FOR.
             *
             * Its pull is 1 whatever the city pays, because a high school
             * diploma is simply what an ordinary person has - there is no
             * shortage of them anywhere to bid for. Every band above it starts
             * at ZERO and is bought: reach() is 0 at the going rate, so a city
             * paying the market rate for graduates receives none, which is the
             * entire point of the rewrite. Below it there is nothing to buy -
             * NONE's ceiling is 0, and the unskilled band is fed only by the
             * city's own children ageing out of school. See
             * WageBand.arrivalCeiling().
             */
            double pull = band == WageBand.DIPLOMA
                    ? 1
                    : reach(market == null ? 1 : market.bandPremium(band));

            weight[b] = band.arrivalCeiling() * pull * opportunity;
            total += weight[b];
        }

        /*
         * AND THE PROFESSIONS, EACH PULLING ON ITS OWN.
         *
         * Jerus's call, and it is the right one. A licensed profession used to
         * be a SUBSET carved out of whatever graduates happened to arrive - so
         * a city drowning in graduates drew no doctors however empty its
         * hospitals were, because the graduate band was in glut and nothing was
         * pulling graduates at all. Two different shortages were reading off
         * one number.
         *
         * Now a doctor is his own stream, priced on doctors
         * (LabourMarket.licencePremium: what the profession is paid over its
         * OWN band) and ADDED to the university band rather than taken out of
         * it - because a doctor is still a graduate, counted once, about whom a
         * second thing is true.
         *
         * The world's scarcity survives the change: the ceiling is the
         * university band's own, times the share of graduates anywhere who hold
         * that licence. So all four professions at maximum desperation together
         * are about a twentieth of a month's arrivals, and a hospital city
         * still has to build the school.
         */
        java.util.Arrays.fill(lastArrivalLicences, 0);
        for (EducationType type : EducationType.values()) {
            if (!type.isProfessional()) continue;
            JobType job = type.licenses();
            if (job == null) continue;

            double pull = reach(market == null ? 1 : market.licencePremium(job));
            double w = WageBand.UNIVERSITY.arrivalCeiling()
                    * type.worldLicenceShare()
                    * pull;

            licenceWeight[job.ordinal()] = w;
            total += w;
        }

        /*
         * One budget, shared. Everybody who moved in is placed exactly once, so
         * a city bidding for four professions at once is not thereby a bigger
         * city - it simply gets a different mix of the same arrivals.
         */
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            lastArrivalMix[b] = total > 0 ? lastArrivals * weight[b] / total : 0;
        }
        int uni = WageBand.UNIVERSITY.ordinal();
        for (int i = 0; i < licenceWeight.length; i++) {
            if (licenceWeight[i] <= 0) continue;
            double arriving = total > 0 ? lastArrivals * licenceWeight[i] / total : 0;
            lastArrivalLicences[i] = arriving;
            lastArrivalMix[uni] += arriving;
        }
    }

    /* ------------------------------- saving ------------------------------- */

    public double[] toSaveArray() {
        double[] out = new double[TIERS * DECLINE_MONTHS + TIERS + 1];
        int i = 0;
        for (double[] row : history) {
            for (double v : row) out[i++] = v;
        }
        for (int s : decliningStreak) out[i++] = s;
        out[i] = monthsRecorded;
        return out;
    }

    public void restore(double[] saved) {
        int expected = TIERS * DECLINE_MONTHS + TIERS + 1;
        if (saved == null || saved.length != expected) {
            return;   // refused whole rather than half-read
        }
        int i = 0;
        for (double[] row : history) {
            for (int m = 0; m < row.length; m++) row[m] = saved[i++];
        }
        for (int t = 0; t < TIERS; t++) decliningStreak[t] = (int) saved[i++];
        monthsRecorded = (int) saved[i];
    }

    public void reset() {
        for (double[] row : history) java.util.Arrays.fill(row, 0);
        java.util.Arrays.fill(decliningStreak, 0);
        monthsRecorded = 0;
        lastTarget = 0;
        lastArrivals = 0;
        lastDepartures = 0;
        lastCrowding = 1;
        lastDecliningShare = 0;
        lastSeniorPull = 1;
        lastResidentsPerJob = residentsPerJob(0);
    }
}
