package ham.citybuildersim;

/**
 * Crime, the police who deter and catch it, and the prisons that hold who
 * they catch.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * Jerus, 2026-09-11: "crime is a function of unemployment, and tight or under
 * households, we need police, and also prison, and yes that means another
 * population category, only adults can go to jail, only adults commit crime,
 * if there is crime, alot of police drastically reduces it but never
 * eliminates it, but does reduce it alot. just if there is a reason for crime
 * there is no way to actually remove it without changing the underlying
 * reason."
 *
 * That last sentence is the design. The police are a multiplier on crime,
 * never a subtraction from it: full coverage takes 90% off whatever the city's
 * reasons make, and the reasons are the out of work, the unhoused, the crowded
 * and the households short of money - the things the rest of the game already
 * measures. A city that wants less crime and has built all the police it can
 * has to build homes and jobs.
 *
 * ==================== THE MONTH ====================
 *
 * 1. WHO IS AT RISK. Adults at liberty, sorted without double counting (Game
 *    sorts them - see Causes): no home (5), out of work past EI (4), short of
 *    money (3), on EI (2), crowded (2), everybody else (0.1). Where a group
 *    overlaps another, the higher weight counts.
 *
 * 2. HOW MANY CRIMES.
 *
 *        pressure = sum of adults x weight + 2 x (1 - coverage) x adults
 *        crimes   = K x pressure x (1 - 0.9 x coverage^1.5)
 *        coverage = staffed officers / (360 per 100,000 people), at most 1
 *
 *    The second term is Jerus's "low police will increase crime incentive
 *    regardless of other reasons": every adult at liberty is two points more
 *    tempted with no police at all, fading to nothing at full coverage. K is
 *    struck so a Canada-like city - 3% of adults on EI, 1.5% past it, 0.15%
 *    with no home, 5% short of money, 8% crowded - at Canada's half coverage
 *    makes Canada's 5,585 crimes per 100,000 people a year (StatCan, 2025).
 *
 * 3. WHAT IT DOES. A quarter is violent: half a month off work for each,
 *    and 0.115% of them kill (Canada's 1.61 homicides per 100,000 against its
 *    violent crime). The rest is theft, a quarter of a month's unskilled wage
 *    each, half from the households and half from the businesses, handed to
 *    the offenders' households. And a city with more crime than Canada's draws
 *    fewer people and loses some - see Migration.
 *
 * 4. WHO IS CAUGHT, AND WHO IS HELD. 9% of crimes times coverage end in a
 *    six-month sentence - Canada's half coverage sends 4.5% of its crimes to
 *    prison, 251 admissions a year per 100,000 people, which at six months
 *    each is Canada's 127 inside on an average day. With no police nobody is
 *    caught. The caught go in if there is a staffed cell free; the rest are
 *    caught but not held. Six monthly cohorts; the seventh month, out, into
 *    the pool of the out of work.
 *
 * WHAT IS NOT HERE. Who a crime happens to, apart from the dead (adults - the
 * pyramid has no other way to say who). Sentences by offence. Parole,
 * reoffending, a record that follows a released prisoner into the labour
 * market. Each would be a finer version of something this does coarsely;
 * none is needed for the thing Jerus asked for, which is that crime has
 * reasons and police cannot remove them.
 */
public class Crime {

    /* ------------------------------- the dials ------------------------------- */

    /** Police officers per 100,000 people, Canada, 2025 (StatCan: 75,107 officers). */
    public static final double CANADA_OFFICERS_PER_100K = 180;

    /** Full coverage: twice Canada's level. Jerus: "twice Canada's". */
    public static final double FULL_OFFICERS_PER_100K = 2 * CANADA_OFFICERS_PER_100K;

    /** Police-reported crime per 100,000 people a year, Canada, 2025. */
    public static final double CANADA_CRIMES_PER_100K = 5_585;

    /** Homicides per 100,000 people a year, Canada, 2025. */
    public static final double CANADA_HOMICIDES_PER_100K = 1.61;

    /** What full coverage takes off. Jerus: "drastically reduces it but never eliminates it". */
    public static final double MAX_DETERRENCE = .90;

    /**
     * The curve between none and full. At 1.5, Canada's half coverage takes a
     * third off and full takes 90%. The research slope is far gentler -
     * Chalfin and McCrary (2018) find crime falls 0.34% (violent) and 0.17%
     * (property) per 1% more police - and Jerus's "drastically" wins.
     */
    public static final double DETERRENCE_POWER = 1.5;

    /** Every adult at liberty, tempted this much more with no police at all. Jerus: "like everyone being on EI". */
    public static final double NO_POLICE_WEIGHT = 2;

    /** A quarter of crime is violent. */
    public static final double VIOLENT_SHARE = .25;

    /** Months off work per violent crime, on the sick rate. */
    public static final double MONTHS_OFF_PER_VIOLENT = .5;

    /** Of violent crimes, the share that kill: Canada's homicides over its violent crime, 0.115%. */
    public static final double KILLED_PER_VIOLENT =
            CANADA_HOMICIDES_PER_100K / (CANADA_CRIMES_PER_100K * VIOLENT_SHARE);

    /** What one property crime takes, as a share of a month's unskilled wage. */
    public static final double THEFT_WAGE_SHARE = .25;

    /** ...of which this much from households, the rest from the businesses. */
    public static final double FROM_HOUSEHOLDS = .5;

    /** The share of crimes that end in a sentence at full coverage; straight down to none with no police. */
    public static final double CAUGHT_AT_FULL = .09;

    /** Months a sentence lasts. Jerus: "six months each". */
    public static final int SENTENCE_MONTHS = 6;

    /* ------------------------------- the causes ------------------------------- */

    /**
     * Why an adult at liberty offends, and how much. Jerus's grades.
     *
     * FEW_POLICE is not a group of people - it is every adult at liberty, at
     * NO_POLICE_WEIGHT times what the police do not cover - and it is kept in
     * the same list so the screens can show it as one reason among the others.
     */
    public enum Cause {
        NO_HOME("No home", 5),
        PAST_EI("Out of work, EI run out", 4),
        SHORT_OF_MONEY("Short of money", 3),
        ON_EI("Out of work, on EI", 2),
        CROWDED("Crowded", 2),
        NO_CAUSE("No reason", .1),
        FEW_POLICE("Too few police", NO_POLICE_WEIGHT);

        private final String label;
        private final double weight;

        Cause(String label, double weight) {
            this.label = label;
            this.weight = weight;
        }

        public String label()  { return label; }

        /** Per adult; FEW_POLICE's is before the coverage it is missing. */
        public double weight() { return weight; }

        /** The six that are groups of people. */
        public boolean isGroup() { return this != FEW_POLICE; }
    }

    private static final int CAUSES = Cause.values().length;

    /**
     * The adults at liberty, sorted into the groups above - each adult once.
     * Built by Game, which is the only thing that can see the families, the
     * pool and the household books at once.
     */
    public static final class Causes {
        private final double[] adults = new double[CAUSES];

        /** Of the weighted pressure, the part that is the out of work's. */
        private double poolWeighted;

        public Causes add(Cause cause, double people) {
            if (cause != null && cause.isGroup() && people > 0) adults[cause.ordinal()] += people;
            return this;
        }

        /** Another set of adults, added group by group - a cell's, into the city's. */
        public Causes add(Causes other) {
            if (other == null) return this;
            for (int i = 0; i < CAUSES; i++) adults[i] += other.adults[i];
            poolWeighted += other.poolWeighted;
            return this;
        }

        public Causes addPool(double weighted) {
            if (weighted > 0) poolWeighted += weighted;
            return this;
        }

        public double adults(Cause cause) { return adults[cause.ordinal()]; }

        /** Every adult at liberty. */
        public double adults() {
            double sum = 0;
            for (Cause c : Cause.values()) if (c.isGroup()) sum += adults[c.ordinal()];
            return sum;
        }

        /** Adults times weight, the six groups. */
        public double weighted() {
            double sum = 0;
            for (Cause c : Cause.values()) if (c.isGroup()) sum += adults[c.ordinal()] * c.weight();
            return sum;
        }

        public double poolWeighted() { return poolWeighted; }

        /**
         * A city of this many adults at liberty sorted the way Canada's are:
         * 3% on EI, 1.5% past it, 0.15% with no home, 5% short of money, 8%
         * crowded. What K is struck against.
         */
        public static Causes canadaLike(double adults) {
            Causes k = new Causes();
            k.add(Cause.ON_EI, adults * .03);
            k.add(Cause.PAST_EI, adults * .015);
            k.add(Cause.NO_HOME, adults * .0015);
            k.add(Cause.SHORT_OF_MONEY, adults * .05);
            k.add(Cause.CROWDED, adults * .08);
            k.add(Cause.NO_CAUSE, adults * (1 - .03 - .015 - .0015 - .05 - .08));
            return k;
        }
    }

    /**
     * Crimes a month per point of pressure. Struck from Canada: a Canada-like
     * city at half coverage, with the game's own share of adults at
     * equilibrium, makes 5,585 crimes per 100,000 people a year. About 0.0075.
     */
    public static final double K = strikeK();

    private static double strikeK() {
        double adultShare = PopulationCohorts.equilibriumShare(AgeBand.ADULT);
        double perPersonMonth = CANADA_CRIMES_PER_100K / 100_000.0 / 12;
        Causes canada = Causes.canadaLike(1);
        double half = .5;
        double pressurePerAdult = canada.weighted() + NO_POLICE_WEIGHT * (1 - half);
        return perPersonMonth / adultShare / (pressurePerAdult * deterrence(half));
    }

    /** What the police leave of the crime the reasons make: 1 with none, 0.1 at full. */
    public static double deterrence(double coverage) {
        double c = Math.max(0, Math.min(1, coverage));
        return 1 - MAX_DETERRENCE * Math.pow(c, DETERRENCE_POWER);
    }

    /** Staffed officers against full coverage for this many people, 0-1. An empty city is covered. */
    public static double coverageOf(double officers, double population) {
        if (!(population > 0)) return 1;
        double full = population * FULL_OFFICERS_PER_100K / 100_000.0;
        return Math.max(0, Math.min(1, Math.max(0, officers) / full));
    }

    /** The share of crimes that end in a sentence at this coverage. */
    public static double caughtShare(double coverage) {
        return CAUGHT_AT_FULL * Math.max(0, Math.min(1, coverage));
    }

    /* ------------------------------- the stock ------------------------------- */

    /** Prisoners by months served, 0 the month they went in. People. */
    private final double[] ring = new double[SENTENCE_MONTHS];

    /* ------------------------------- the month ------------------------------- */

    private double population, officers, cells, coverage = 1, adultsAtLiberty;
    private final double[] pressureBy = new double[CAUSES];
    private double pressure, crimes, violent, killed, injuredShare, theftWanted;
    private double caught, admitted, notHeld, released, releasedEarly;
    private double stolenFromHouseholds, stolenFromBusinesses;
    private double payroll, upkeep;

    /* ------------------------------- lifetime ------------------------------- */

    private double everCrimes, everKilled, everStolen, everAdmitted, everNotHeld;

    /* ------------------------------- the month ------------------------------- */

    /**
     * Strikes the month's crime and turns the prison ring.
     *
     * @param causes        the adults at liberty, sorted
     * @param population    everyone in the city, for the rates and coverage
     * @param staffedOfficers police capacity with the staff the city could fill
     * @param staffedCells  prison cells the same way
     * @param adultExitRate the month's adult deaths and ageing out, which thin
     *                      the prisoners as they thin everyone
     * @param unskilledWage what an unskilled post pays a month, today
     */
    public void advanceMonth(Causes causes, double population, double staffedOfficers,
                             double staffedCells, double adultExitRate, double unskilledWage) {

        if (causes == null) causes = new Causes();
        this.population = Math.max(0, population);
        officers = Math.max(0, staffedOfficers);
        cells = Math.max(0, staffedCells);
        coverage = coverageOf(officers, this.population);
        adultsAtLiberty = causes.adults();

        /* ---- 1 and 2: the pressure, and what the police leave of it ---- */
        pressure = 0;
        for (Cause c : Cause.values()) {
            pressureBy[c.ordinal()] = c.isGroup()
                    ? causes.adults(c) * c.weight()
                    : c.weight() * (1 - coverage) * adultsAtLiberty;
            pressure += pressureBy[c.ordinal()];
        }
        crimes = K * pressure * deterrence(coverage);

        /* ---- 3: what it does ---- */
        violent = crimes * VIOLENT_SHARE;
        killed = violent * KILLED_PER_VIOLENT;
        injuredShare = this.population > 0
                ? Math.min(1, violent * MONTHS_OFF_PER_VIOLENT / this.population) : 0;
        theftWanted = (crimes - violent) * THEFT_WAGE_SHARE * Math.max(0, unskilledWage);
        stolenFromHouseholds = 0;
        stolenFromBusinesses = 0;

        /* ---- 4: the prisons ---- */
        double keep = Math.max(0, 1 - Math.max(0, adultExitRate));
        for (int m = 0; m < SENTENCE_MONTHS; m++) ring[m] *= keep;

        released = ring[SENTENCE_MONTHS - 1];
        for (int m = SENTENCE_MONTHS - 1; m > 0; m--) ring[m] = ring[m - 1];
        ring[0] = 0;

        /*
         * A PRISON THAT LOST ITS GUARDS CANNOT HOLD WHO IT HELD. When the
         * staffed cells fall below the people in them - a jail closed, or its
         * officers went elsewhere - the overflow goes out, the nearest to
         * finishing first.
         */
        releasedEarly = 0;
        double over = prisoners() - cells;
        for (int m = SENTENCE_MONTHS - 1; m >= 0 && over > 1e-12; m--) {
            double out = Math.min(ring[m], over);
            ring[m] -= out;
            over -= out;
            releasedEarly += out;
        }

        caught = Math.min(crimes * caughtShare(coverage), adultsAtLiberty);
        double free = Math.max(0, cells - prisoners());
        admitted = Math.min(caught, free);
        notHeld = caught - admitted;
        ring[0] = admitted;

        everCrimes += crimes;
        everKilled += killed;
        everAdmitted += admitted;
        everNotHeld += notHeld;
    }

    /**
     * What the thieves actually got - which is less than getTheftWanted()
     * when the households or the businesses had less than that to take.
     */
    public void recordStolen(double fromHouseholds, double fromBusinesses) {
        stolenFromHouseholds = Math.max(0, fromHouseholds);
        stolenFromBusinesses = Math.max(0, fromBusinesses);
        everStolen += stolenFromHouseholds + stolenFromBusinesses;
    }

    /** What the police and the prisons cost this month. Set by Game off the buildings. */
    public void setCosts(double payroll, double upkeep) {
        this.payroll = Math.max(0, payroll);
        this.upkeep = Math.max(0, upkeep);
    }

    /* ------------------------------- reading ------------------------------- */

    /** Everyone serving a sentence. */
    public double prisoners() {
        double sum = 0;
        for (double v : ring) sum += v;
        return sum;
    }

    /** Prisoners in their nth month, 0 the newest. */
    public double cohort(int monthsServed) { return ring[monthsServed]; }

    public double getPopulation()      { return population; }
    public double getOfficers()        { return officers; }
    public double getCells()           { return cells; }
    public double getCoverage()        { return coverage; }
    public double getAdultsAtLiberty() { return adultsAtLiberty; }

    public double getPressure()           { return pressure; }
    public double getPressure(Cause c)    { return pressureBy[c.ordinal()]; }

    /** This month's crimes. */
    public double getCrimes()          { return crimes; }

    /** ...of which this many from one cause, by its share of the pressure. */
    public double getCrimes(Cause c) {
        return pressure > 0 ? crimes * pressureBy[c.ordinal()] / pressure : 0;
    }

    /**
     * What this month's reasons would have made at another coverage: the
     * groups the same, the part the police are missing moved, and what the
     * police leave of it. For a screen asking what one more station buys.
     */
    public double crimesAt(double otherCoverage) {
        double c = Math.max(0, Math.min(1, otherCoverage));
        double groups = 0;
        for (Cause k : Cause.values()) if (k.isGroup()) groups += pressureBy[k.ordinal()];
        return K * (groups + NO_POLICE_WEIGHT * (1 - c) * adultsAtLiberty) * deterrence(c);
    }

    /** ...and with no police at all, for how much the police are taking off. */
    public double crimesWithoutPolice() { return crimesAt(0); }

    public double getViolent()         { return violent; }
    public double getProperty()        { return crimes - violent; }
    public double getKilled()          { return killed; }

    /** The share of the city off work this month with an injury. On the sick rate. */
    public double getInjuredShare()    { return injuredShare; }

    /** What the month's thefts would take if there were money to take. */
    public double getTheftWanted()     { return theftWanted; }
    public double getStolenFromHouseholds() { return stolenFromHouseholds; }
    public double getStolenFromBusinesses() { return stolenFromBusinesses; }
    public double getStolen()          { return stolenFromHouseholds + stolenFromBusinesses; }

    public double getCaught()          { return caught; }
    public double getAdmitted()        { return admitted; }
    public double getNotHeld()         { return notHeld; }
    public double getReleased()        { return released; }
    public double getReleasedEarly()   { return releasedEarly; }

    /** Crimes a year per 100,000 people, at this month's pace. */
    public double getRatePer100k() {
        return population > 0 ? crimes * 12 * 100_000.0 / population : 0;
    }

    /** The rate against Canada's: 1 is Canada. */
    public double getRateVsCanada() {
        return getRatePer100k() / CANADA_CRIMES_PER_100K;
    }

    /** Officers per 100,000 people. */
    public double getOfficersPer100k() {
        return population > 0 ? officers * 100_000.0 / population : 0;
    }

    /** Prisoners per 100,000 people. */
    public double getPrisonersPer100k() {
        return population > 0 ? prisoners() * 100_000.0 / population : 0;
    }

    public double getPayroll()   { return payroll; }
    public double getUpkeep()    { return upkeep; }
    public double getGrossCost() { return payroll + upkeep; }

    public double getEverCrimes()   { return everCrimes; }
    public double getEverKilled()   { return everKilled; }
    public double getEverStolen()   { return everStolen; }
    public double getEverAdmitted() { return everAdmitted; }
    public double getEverNotHeld()  { return everNotHeld; }

    /* ------------------------------- saving ------------------------------- */

    private static final int STATE_LENGTH = SENTENCE_MONTHS + 5 + CAUSES + 13 + 2 + 5;

    /**
     * The ring, and the month as it was struck - the migration pull, the
     * killings and the injuries next month read are this month's figures, and
     * a flow cannot be rebuilt from the state a month ended in.
     */
    public double[] getState() {
        double[] out = new double[STATE_LENGTH];
        int i = 0;
        for (double v : ring) out[i++] = v;
        out[i++] = population; out[i++] = officers; out[i++] = cells;
        out[i++] = coverage;   out[i++] = adultsAtLiberty;
        for (double v : pressureBy) out[i++] = v;
        out[i++] = pressure;   out[i++] = crimes;   out[i++] = violent;
        out[i++] = killed;     out[i++] = injuredShare; out[i++] = theftWanted;
        out[i++] = caught;     out[i++] = admitted; out[i++] = notHeld;
        out[i++] = released;   out[i++] = releasedEarly;
        out[i++] = stolenFromHouseholds; out[i++] = stolenFromBusinesses;
        out[i++] = payroll;    out[i++] = upkeep;
        out[i++] = everCrimes; out[i++] = everKilled; out[i++] = everStolen;
        out[i++] = everAdmitted; out[i] = everNotHeld;
        return out;
    }

    /** @return false if the array is not this build's shape; nothing is changed */
    public boolean restore(double[] saved) {
        if (saved == null || saved.length != STATE_LENGTH) return false;
        int i = 0;
        for (int m = 0; m < SENTENCE_MONTHS; m++) ring[m] = Math.max(0, saved[i++]);
        population = saved[i++]; officers = saved[i++]; cells = saved[i++];
        coverage = saved[i++];   adultsAtLiberty = saved[i++];
        for (int c = 0; c < CAUSES; c++) pressureBy[c] = saved[i++];
        pressure = saved[i++];   crimes = saved[i++];   violent = saved[i++];
        killed = saved[i++];     injuredShare = saved[i++]; theftWanted = saved[i++];
        caught = saved[i++];     admitted = saved[i++]; notHeld = saved[i++];
        released = saved[i++];   releasedEarly = saved[i++];
        stolenFromHouseholds = saved[i++]; stolenFromBusinesses = saved[i++];
        payroll = saved[i++];    upkeep = saved[i++];
        everCrimes = saved[i++]; everKilled = saved[i++]; everStolen = saved[i++];
        everAdmitted = saved[i++]; everNotHeld = saved[i];
        return true;
    }

    public void reset() {
        java.util.Arrays.fill(ring, 0);
        java.util.Arrays.fill(pressureBy, 0);
        population = 0; officers = 0; cells = 0; coverage = 1; adultsAtLiberty = 0;
        pressure = 0; crimes = 0; violent = 0; killed = 0; injuredShare = 0; theftWanted = 0;
        caught = 0; admitted = 0; notHeld = 0; released = 0; releasedEarly = 0;
        stolenFromHouseholds = 0; stolenFromBusinesses = 0; payroll = 0; upkeep = 0;
        everCrimes = 0; everKilled = 0; everStolen = 0; everAdmitted = 0; everNotHeld = 0;
    }

    /** Money in the new unit. People and rates do not move. */
    public void redenominate(double scale) {
        theftWanted *= scale;
        stolenFromHouseholds *= scale;
        stolenFromBusinesses *= scale;
        payroll *= scale;
        upkeep *= scale;
        everStolen *= scale;
    }
}
