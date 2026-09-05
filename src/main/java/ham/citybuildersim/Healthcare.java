package ham.citybuildersim;

/**
 * The city's healthcare service: what it costs, what it collects, and what it
 * does with the dead.
 *
 * TWO CLASSES, AND THE DIFFERENCE MATTERS. {@link Health} is the EFFECT - how
 * much of the workforce is off sick this month, which is a multiplier on output.
 * This is the SERVICE - an employer with a payroll, a landlord with upkeep, a
 * till that takes fees, and a cemetery with a finite number of plots. Health
 * reads coverage; Healthcare pays for it.
 *
 * WHY THIS EXISTS AT ALL
 *
 * Because it did not, and the hole was the largest conservation break in the
 * game. Every category of building with jobs is paid by some handler - shops by
 * CommercialHandler, mills by IndustrialHandler, the grid and the water and the
 * crews by ServicesManager. HEALTHCARE was paid by nobody, and it carries 2,128
 * jobs across its fourteen templates, more than any other category. Its wages
 * were counted in the city's wage bill, the city collected wage tax on them, the
 * households received and spent them, and nothing debited the treasury. A
 * General Hospital created $1.15M a month out of nothing; a Regional Medical
 * Centre $3.25M.
 *
 * `upkeep` had a matching hole of its own, and a wider one: getUpkeep() was read
 * by a debug println and by BuildingDataCheck, and by nothing else, for ANY
 * building in the game. It has always been a wish. Healthcare is the first thing
 * that actually charges it, because healthcare is the first building category
 * whose running cost is the whole point of it.
 *
 * A NET DEFICIT BUSINESS, per Jerus - "patients still pay, just not much". The
 * fees below recover roughly a fifth to a quarter of what the service costs, and
 * they are charged on people SERVED rather than on capacity built, so an empty
 * ward is paid for and collects nothing. Overbuilding is meant to hurt.
 */
public class Healthcare {

    /* ===================================================================
       WHAT THE CITY WAS FOUNDED WITH

       A doctor, a nursery, an almshouse and a churchyard - enough for about
       twelve hundred people, and not a building any of them. The city starts
       with 100 units of housing and 400 of road capacity for exactly the same
       reason: a game that opens with a crisis is not opening, it is already
       failing, and a player who has to build a clinic before they build a shop
       has learned nothing about why the clinic matters.

       SIZED OFF THE PYRAMID'S OWN SHARES rather than typed in. Childcare's
       founding capacity is whatever share of twelve hundred people are babies
       and children at equilibrium, and senior care's is the senior share, so
       these stay right if AgeBand's spans ever move. A constant is a cached
       answer to a question nobody re-asks; this asks it.

       THE CHURCHYARD RUNS OUT, and that is the point of it being plots rather
       than a rate. Two and a half thousand graves is four centuries for a city
       of twelve hundred, twenty-five years for one of twenty thousand, and six
       for one of eighty thousand - so a city that grows finds the ground
       filling up while it still has other things to worry about, which is when
       the "filling up" warning earns its keep.
       =================================================================== */

    /** How many residents the founding endowment was meant to serve. */
    public static final int FOUNDING_CITY = 1200;

    /** Graves in the old churchyard. */
    public static final int FOUNDING_PLOTS = 2500;

    /**
     * Capacity the city has before it builds anything.
     *
     * Deliberately NOT discounted by staffing, unlike everything the player
     * builds - there is nobody on the payroll for it, because it is not a
     * building. It is what was already here.
     */
    public static double foundingCapacity(CareType care) {
        if (care == null) return 0;
        switch (care) {
            case GENERAL:   return FOUNDING_CITY;
            case CHILDCARE: return FOUNDING_CITY
                    * (PopulationCohorts.equilibriumShare(AgeBand.BABY)
                     + PopulationCohorts.equilibriumShare(AgeBand.CHILD));
            case SENIOR:    return FOUNDING_CITY
                    * PopulationCohorts.equilibriumShare(AgeBand.SENIOR);
            case BURIAL:    return FOUNDING_PLOTS;
            default:        return 0;   // nobody founds a city with a crematorium
        }
    }

    /* ===================================================================
       WHAT PATIENTS PAY

       Per person served, per month, in thousands. Set against what each kind
       of care actually costs per head, so the recovery rate is roughly even
       across the three rather than an accident of which buildings exist:

         general    costs $33-46/person   fee $10    ~25%
         childcare  costs $600-680/child  fee $150   ~23%
         senior     costs $200-2,573/head fee $300   ~12-100%

       The senior spread is the interesting one and it is left alone
       deliberately. A Home Care Service costs $200 a head and a Nursing Home
       $2,573, so a flat fee makes community care very nearly self-funding and
       institutional care heavily subsidised - which is both true to life and a
       real decision for the player.
       =================================================================== */

    public static final double GENERAL_FEE   = .010;
    public static final double CHILDCARE_FEE = .150;
    public static final double SENIOR_FEE    = .300;

    /* ===================================================================
       THE TWO WAYS TO BURY SOMEBODY

       A cemetery turns a profit and consumes land permanently; a crematorium
       runs at breakeven on a great deal of electricity. Jerus's design, and
       the fees are what make it true rather than a comment.

       A Municipal Cemetery costs $117k a month to run and holds 60,000 plots.
       A Crematorium costs $99k a month and handles 120 a month - so $825 a
       body is its breakeven AT FULL THROUGHPUT, and an underused one loses
       money. That is not a flaw in the number, it is the trade: the cemetery
       is a large fixed asset that pays for itself over decades, and the
       crematorium is a machine you have to keep busy.
       =================================================================== */

    public static final double BURIAL_FEE    = 3.000;
    public static final double CREMATION_FEE = .900;

    /**
     * How long a household is assumed to be putting money aside for a funeral.
     *
     * Jerus: "if there is excess savings then people prefer cemetery, otherwise
     * crematorium". A plot is a purchase people save up for rather than pay out
     * of one month's income, so the test is whether a household's monthly
     * surplus would cover a plot over ten years - which is a low bar, and
     * deliberately so. Nearly any household that is not running a deficit
     * chooses burial; the ones that cannot are the ones the city's prices have
     * already squeezed dry.
     */
    public static final int BURIAL_SAVING_MONTHS = 120;

    /**
     * How far behind a city can get before it starts improvising.
     *
     * Two years of funerals, and the cap exists for two reasons that have
     * nothing to do with the health penalty - that is capped separately, in
     * Health, and it saturates at about nine months' worth, so this never
     * softens it.
     *
     * The first reason is that an uncapped stock reads as nonsense: a city that
     * ignored death care for thirty years would report seven hundred people
     * lying about, which is not a backlog, it is a different genre. The second
     * is a windfall - build one cemetery after thirty years of neglect and the
     * city would bury the whole accumulation in a single month and collect
     * thirty years of burial fees at once. Past two years the city is dealing
     * with its dead somehow, badly, and has already paid the full price in the
     * sick rate.
     */
    public static final int MAX_BACKLOG_MONTHS = 24;

    /* ===================================================================
       WHAT CARE DOES TO MORTALITY

       A multiplier on the band's ANNUAL death rate, interpolated GEOMETRICALLY
       on coverage:

           factor(c) = swing ^ (1 - 2c)

       so an unserved city is multiplied by the swing, a fully served one is
       divided by it, and a half-served one gets exactly today's rate. That
       last property is Jerus's earlier call - "today sits in the middle" - and
       the geometric form is what lets it survive a swing of forty. The first
       version interpolated linearly with the two ends summing to two, which
       caps the uncovered end at 2x by construction: ask for 40x and the
       covered end goes to -38.

       Log space is the right space for a multiplier anyway. Halfway between
       "40 times worse" and "40 times better" is "the same", which is what a
       reader means by halfway; arithmetically it would be twenty times worse.

       AgeBand.monthlyFromAnnual() caps the result at MAX_MONTHLY_MORTALITY, so
       even a forty-fold swing cannot empty a band in a month.
       =================================================================== */

    /**
     * Children, and it is enormous - per Jerus, "really really really".
     *
     * A forty-fold swing takes AgeBand's 0.10%/yr infant mortality to 4%/yr
     * untreated, which over the six years of the baby band is about a fifth of
     * every child born, and down to 0.0025%/yr where there is care for
     * everybody. That is roughly the real gap between a city with clinics and
     * one without, and it is the single number that moves most with basic
     * medicine.
     *
     * The previous nine-fold version was measured at 0.2% of the baby band over
     * a year - a dramatic ratio and an invisible effect, because nine times
     * almost nothing is still almost nothing. This is the fix for that.
     */
    public static final double CHILDCARE_SWING = 40;

    /**
     * Teenagers and adults, on general care.
     *
     * New, and Jerus's call. General care already earns its keep through the
     * sick rate, so this is a second effect from one lever - but a city where
     * hospitals do not affect whether adults live is a strange city, and three
     * is deliberately modest beside childcare's forty: it takes the adult band
     * from 0.45%/yr to 1.35% untreated and 0.15% served. Medicine saves far
     * more infants than it saves thirty-year-olds.
     */
    public static final double GENERAL_SWING = 3.0;

    /**
     * Seniors, and it stays gentle.
     *
     * Nothing stops a ninety-year-old eventually, so senior care buys comfort
     * and a little time rather than a different outcome: 4.5%/yr becomes 6.1%
     * untreated and 3.3% served. What senior care is really FOR is the draw it
     * gives the city - see Migration.SENIOR_CARE_PULL.
     */
    public static final double SENIOR_SWING = 1.35;

    /**
     * How much more a city with childcare gives birth.
     *
     * One-directional, unlike the mortality swings, because Jerus asked for an
     * increase rather than a swing: a city with nowhere to put its children has
     * today's birth rate, and one that has solved the problem has twice it.
     * Fifteen per thousand per year to thirty, which is the gap between a
     * developed country and a young one.
     */
    public static final double CHILDCARE_BIRTH_BONUS = 1.0;

    /**
     * What this band's death rate is multiplied by, given the city's coverage.
     *
     * ONE CARE TYPE PER BAND, which is why general care's coverage is not also
     * applied to babies. Childcare and general care both plausibly help an
     * infant, and letting both apply would multiply two effects that mean the
     * same thing and make the childcare lever unreadable. Each band answers to
     * exactly one building type, and the buildings are named for it.
     */
    public static double mortalityFactor(AgeBand band, double childcareCoverage,
                                         double generalCoverage, double seniorCoverage) {
        if (band == null) return 1;

        if (CareType.CHILDCARE.servedBy(band)) return swing(CHILDCARE_SWING, childcareCoverage);
        if (band == AgeBand.SENIOR)            return swing(SENIOR_SWING, seniorCoverage);
        return swing(GENERAL_SWING, generalCoverage);
    }

    /** The two-argument form, for callers that only care about the extremes. */
    public static double mortalityFactor(AgeBand band,
                                         double childcareCoverage, double seniorCoverage) {
        return mortalityFactor(band, childcareCoverage, .5, seniorCoverage);
    }

    /** swing at no coverage, 1 at half, 1/swing at full. */
    private static double swing(double swing, double coverage) {
        if (swing <= 0) return 1;
        return Math.pow(swing, 1 - 2 * clamp(coverage));
    }

    /** The whole array, in band order, for PopulationCohorts.advanceMonth(). */
    public static double[] mortalityFactors(double childcareCoverage,
                                            double generalCoverage,
                                            double seniorCoverage) {
        AgeBand[] bands = AgeBand.values();
        double[] factor = new double[bands.length];
        for (int i = 0; i < bands.length; i++) {
            factor[i] = mortalityFactor(bands[i], childcareCoverage,
                    generalCoverage, seniorCoverage);
        }
        return factor;
    }

    /**
     * What the birth rate is multiplied by.
     *
     * Somewhere to leave a child is the difference between one and two, and the
     * model has no other lever that touches fertility at all - births were a
     * flat 15 per thousand a year regardless of anything the player did.
     */
    public static double birthFactor(double childcareCoverage) {
        return 1 + CHILDCARE_BIRTH_BONUS * clamp(childcareCoverage);
    }

    private static double clamp(double v) {
        return Math.max(0, Math.min(1, v));
    }

    /* ------------------------------ the month ------------------------------ */

    private double payroll;
    private double upkeep;
    private double fees;
    private double treatmentFees;
    private double funeralFees;

    private double deaths;
    private double burials;
    private double cremations;

    /* What the city had to work with, kept so the screen can say how tight it
       was. Inputs rather than results, but they are facts about the month and
       the report is read long after the month has moved on. */
    private double plotsBuilt;
    private double cremationCapacity;

    /* --------------------------- carried forward --------------------------- */

    /**
     * Plots used, ever.
     *
     * The one number in this class that is a STOCK rather than a flow, and the
     * reason a cemetery is a different kind of decision from a crematorium: a
     * plot is consumed permanently and the land never comes back. A city that
     * fills its cemeteries has to buy more land, not run the machine harder.
     */
    private double plotsUsed;

    /**
     * The dead nobody could deal with, accumulated.
     *
     * Drains as soon as capacity exists - each month handles the backlog and
     * this month's deaths together, oldest first in effect - and while it
     * stands it makes the living ill. See Health.
     */
    private double unburied;

    /**
     * Settles the month.
     *
     * @param staffedPayroll   healthcare wages actually being paid, fill applied
     * @param upkeep           running cost of the healthcare buildings standing
     * @param served           people treated, indexed by CareType.ordinal()
     * @param deaths           who died this month
     * @param burialShare      the share who would choose a plot if one exists
     * @param plotsBuilt       cemetery capacity ever built, in plots
     * @param cremationCapacity what the crematoria can handle in a month
     */
    public void advanceMonth(double staffedPayroll, double upkeep,
                             double[] served,
                             double deaths, double burialShare,
                             double plotsBuilt, double cremationCapacity) {

        this.payroll = Math.max(0, staffedPayroll);
        this.upkeep = Math.max(0, upkeep);
        this.deaths = Math.max(0, deaths);
        this.plotsBuilt = Math.max(0, plotsBuilt);
        this.cremationCapacity = Math.max(0, cremationCapacity);

        treatmentFees = feeOn(served, CareType.GENERAL, GENERAL_FEE)
                + feeOn(served, CareType.CHILDCARE, CHILDCARE_FEE)
                + feeOn(served, CareType.SENIOR, SENIOR_FEE);

        settleDeaths(burialShare, plotsBuilt, cremationCapacity);

        funeralFees = burials * BURIAL_FEE + cremations * CREMATION_FEE;
        fees = treatmentFees + funeralFees;
    }

    private static double feeOn(double[] served, CareType care, double fee) {
        if (served == null || care.ordinal() >= served.length) return 0;
        return Math.max(0, served[care.ordinal()]) * fee;
    }

    /**
     * Who gets buried, who gets burned, and who gets neither.
     *
     * Preference first, then whatever is actually available - Jerus's rule is
     * "people prefer cemetery, otherwise crematorium, or just whichever option
     * is available", so the overflow runs BOTH ways. A city with a full
     * cemetery cremates; a city whose crematorium is at capacity buries; a city
     * with neither leaves people where they fell, and pays for it in the sick
     * rate.
     */
    private void settleDeaths(double burialShare, double plotsBuilt, double cremationCapacity) {

        double toHandle = deaths + unburied;
        double share = clamp(burialShare);

        double wantBurial = toHandle * share;
        double wantCremation = toHandle - wantBurial;

        double plotsFree = Math.max(0, plotsBuilt - plotsUsed);
        double capacity = Math.max(0, cremationCapacity);

        burials = Math.min(wantBurial, plotsFree);
        double turnedAwayFromTheCemetery = wantBurial - burials;

        cremations = Math.min(wantCremation + turnedAwayFromTheCemetery, capacity);
        double stillWaiting = (wantCremation + turnedAwayFromTheCemetery) - cremations;

        // ...and back the other way, for whoever the ovens could not take.
        double extraBurials = Math.min(stillWaiting, plotsFree - burials);
        burials += extraBurials;
        stillWaiting -= extraBurials;

        plotsUsed += burials;
        unburied = Math.min(Math.max(0, stillWaiting), deaths * MAX_BACKLOG_MONTHS);
    }

    /* ------------------------------ reading it ------------------------------ */

    public double getPayroll()       { return payroll; }
    public double getUpkeep()        { return upkeep; }

    /** Everything the service costs the city before a penny comes back. */
    public double getGrossCost()     { return payroll + upkeep; }

    public double getFees()          { return fees; }
    public double getTreatmentFees() { return treatmentFees; }
    public double getFuneralFees()   { return funeralFees; }

    /**
     * The line that belongs on the city's expenditure list.
     *
     * Never floored at zero. If the city ever built nothing but cemeteries and
     * filled them, healthcare would genuinely turn a profit, and a floor would
     * hide that rather than prevent it.
     */
    public double getNetCost()       { return getGrossCost() - fees; }

    /** Share of the gross cost the fees cover. */
    public double getCostRecovery() {
        double gross = getGrossCost();
        return gross > 0 ? fees / gross : 0;
    }

    public double getDeaths()      { return deaths; }
    public double getBurials()     { return burials; }
    public double getCremations()  { return cremations; }
    public double getPlotsUsed()   { return plotsUsed; }

    /** The dead the city has nowhere to put. */
    public double getUnburied()    { return unburied; }

    public static double plotsRemaining(double plotsBuilt, double plotsUsed) {
        return Math.max(0, plotsBuilt - plotsUsed);
    }

    /* ===================================================================
       DEATH CARE AS A UTILITY

       Deliberately the same shape as the grid and the water supply, per
       Jerus - "same as energy and electricity". Those two report a
       satisfaction ratio, a STABLE/BROWNOUT status and a critical block
       naming the shortfall, and the player already knows how to read all
       three. Death care is a municipal capacity with a demand against it,
       so it gets the same instruments rather than a bespoke one.

       WHERE IT DIFFERS, and the difference is the whole design: the grid
       is a rate and a cemetery is a STOCK. A power station short of demand
       is short every month until somebody builds another; a cemetery is
       fine right up until the month it is full, and then it is never fine
       again. So there are two warnings, not one - "you are running out"
       and "you have run out" - and only the first is any use.
       =================================================================== */

    /** How much of this month's demand the city actually dealt with. */
    public double getDeathCareRatio() {
        double demand = burials + cremations + unburied;
        if (demand <= 0) return 1;
        return (burials + cremations) / demand;
    }

    /** Share of the crematoria's monthly throughput that was used. */
    public double getCremationUtilisation() {
        return cremationCapacity > 0 ? cremations / cremationCapacity : 0;
    }

    /** Share of every plot ever built that is now occupied. */
    public double getPlotUtilisation() {
        return plotsBuilt > 0 ? plotsUsed / plotsBuilt : 0;
    }

    public double getPlotsBuilt()        { return plotsBuilt; }
    public double getPlotsLeft()         { return plotsRemaining(plotsBuilt, plotsUsed); }
    public double getCremationCapacity() { return cremationCapacity; }

    /** True when the city dealt with fewer people than died. */
    public boolean isOverwhelmed() { return unburied > 0; }

    /**
     * True while there is still room and not much of it.
     *
     * The grid's isStrained() has one number to watch; this has two, and
     * either one going is enough. The plots are the sharper of the two -
     * running out of ground is permanent until somebody buys more.
     */
    public boolean isStrained() {
        if (isOverwhelmed()) return false;
        if (getCremationUtilisation() >= STRAINED) return true;
        return plotsBuilt > 0 && monthsOfPlotsLeft(plotsBuilt) <= PLOT_WARNING_MONTHS;
    }

    /** Past this share of the ovens' throughput, say so. Matches the roads'. */
    public static final double STRAINED = .90;

    /** Warn once the ground will not last this long at the current rate. */
    public static final int PLOT_WARNING_MONTHS = 60;

    /** One line for the panel, in the grid's own vocabulary. */
    public String getStatus() {
        if (deaths <= 0 && unburied <= 0) return "Clear";
        if (isOverwhelmed())              return "OVERWHELMED";
        if (isStrained())                 return "Filling up";
        return "Adequate";
    }

    /**
     * Months of burials the city's remaining plots will take at this rate.
     *
     * Returns MAX_VALUE when nobody is being buried, which is the honest answer
     * to "how long until you run out" for a city that is not using the ground.
     */
    public double monthsOfPlotsLeft(double plotsBuilt) {
        double left = plotsRemaining(plotsBuilt, plotsUsed);
        return burials > 0 ? left / burials : Double.MAX_VALUE;
    }

    /* ------------------------------- saving ------------------------------- */

    /**
     * The state, in order. New fields go on the END.
     *
     * plotsUsed and unburied are the two that MUST be here - both are stocks
     * that no amount of looking at the city could reconstruct. A reloaded city
     * that forgot its plots would resurrect a century of graves, and one that
     * forgot its backlog would walk out of an epidemic of its own making. The
     * month's flows are carried for the usual reason: an income statement covers
     * a period and cannot be rebuilt from the instant it ended.
     */
    public double[] getState() {
        return new double[] {
            plotsUsed, unburied,
            payroll, upkeep, treatmentFees, funeralFees, fees,
            deaths, burials, cremations,
            plotsBuilt, cremationCapacity
        };
    }

    /** Refused whole on a length mismatch, per the standing rule. */
    public boolean restore(double[] state) {
        if (state == null || state.length != getState().length) return false;

        int i = 0;
        plotsUsed     = state[i++];
        unburied      = state[i++];
        payroll       = state[i++];
        upkeep        = state[i++];
        treatmentFees = state[i++];
        funeralFees   = state[i++];
        fees          = state[i++];
        deaths        = state[i++];
        burials       = state[i++];
        cremations    = state[i++];
        plotsBuilt        = state[i++];
        cremationCapacity = state[i++];
        return true;
    }
}
