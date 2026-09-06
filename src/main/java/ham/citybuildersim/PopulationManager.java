package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class PopulationManager {
    private int population;
    //dont use this one for anything other than create different jobs
    JobType[] jobTypes = JobType.values();
    //
    private int[] jobs = new int[jobTypes.length];
    private double[] jobWage = new double[jobs.length];
    private double[] totalWagePerType = new double[jobWage.length];
    private int totalJobs;
    //temporary
    private double adultPercent = .5;
    private int workforce;

    /* =====================================================================
       WHO THE WORKFORCE ACTUALLY IS

       The workforce used to be one integer, and getJobVacancy() handed it to
       the most-skilled posts first. A measured city of 9,016 people staffed 220
       doctor posts at 100% with no school, college or university anywhere in
       the game, while a third of its workers sat idle. Every JobType
       distinction was real on the demand side and imaginary on the supply side.

       A SHARE, NOT A COUNT, and that is deliberate. The pyramid still owns how
       many adults there are - it knows who was born, who aged in and who died,
       and none of that is this class's business. What is tracked here is only
       the MIX, which is the thing migration moves and which nothing else knows.
       Four doubles that sum to one, against a headcount that stays exactly
       where it was.

       Everyone starts unskilled because, until education buildings exist, the
       only source of a skill is somebody who already had one moving in.
       ===================================================================== */
    /**
     * HEADS, and only the skilled ones. Index 0 is unused.
     *
     * The first version of this kept four shares that summed to one, and it was
     * subtly wrong in a way that took a measurement to see: multiplying an
     * existing share by a GROWN workforce hands the new workers the city's
     * current skill mix, so a city that had attracted graduates went on
     * producing them out of its own births. Over 240 months it drifted to 8.5%
     * university-educated in a game with no universities.
     *
     * Storing the skilled heads instead makes the rule true by construction
     * rather than by arithmetic: skilled counts move ONLY when somebody skilled
     * arrives or leaves, and the unskilled band is whatever is left of the
     * workforce. A child growing into work is unskilled because they are not in
     * this array, not because a formula remembered to dilute them.
     */
    private final double[] skilledHeads = new double[WageBand.values().length];

    /**
     * People licensed to hold a GATED job, by job type.
     *
     * The four professional schools do not raise anybody's band - a medical
     * student is already a university graduate - so this is a second, narrower
     * fact about the same people: a subset of skilledHeads[UNIVERSITY] who may
     * additionally practise medicine. Every entry for an ungated job stays zero
     * and is never read.
     *
     * WITHOUT THIS, within-band fungibility means a university graduate can be
     * a doctor, which is the simplification the professional schools exist to
     * remove. See EducationType.
     */
    private final double[] licensed = new double[JobType.values().length];
    
    
    
    
    
    /**
     * Takes the population the demographics arrived at, and works out who can
     * work this month.
     *
     * THIS USED TO DECIDE THE POPULATION. It computed
     * `min(householdCapacity, totalJobs * 2.25)` and returned it, which meant
     * the city had no memory at all - the number was rebuilt from scratch every
     * month, so a finished tower filled instantly and a demolished one erased
     * its residents. That expression still exists, in `Migration`, but it is now
     * a TARGET the city migrates toward rather than the answer.
     *
     * THE WORKFORCE IS NOW THE CITY'S ACTUAL ADULTS. It used to be
     * `population * 0.5` - a flat share, typed in, that took no notice of who
     * actually lived here. The pyramid has known the real figure for three
     * batches and nothing read it, which made a city of pensioners staff its
     * factories exactly as well as a city of thirty-year-olds.
     *
     * A settled pyramid runs about 57% adults rather than 50%, so this is worth
     * roughly a seventh more workers at the same population - but the point is
     * not the level, it is that the number now MOVES. A city that ages loses
     * workers without losing residents, and the dependency ratio on the People
     * screen stops being decoration.
     *
     * Taken from the adults who ALREADY LIVED HERE, before this month's
     * arrivals, and that ordering is deliberate rather than accidental: the
     * people who worked this month are the ones who were here. Somebody who
     * moves in this month starts work next month. That property is the reason
     * this method still exists rather than the caller just assigning the field.
     *
     * It is also why the workforce has to be SAVED rather than recomputed - see
     * recomputeWorkforce(). Leave the order alone unless you mean to change how
     * fast a growing city staffs itself, which is a balance decision, not a
     * tidy-up.
     *
     * @param newPopulation      what the demographics arrived at
     * @param adultsAlreadyHere  the adult band before this month's migration
     */
    public int applyPopulation(int newPopulation, double adultsAlreadyHere){
        workforce = (int) Math.max(0, adultsAlreadyHere);
        return population = Math.max(0, newPopulation);
    }
    
    public void updateJobs(int[] newJobs){
        //change later
        totalJobs = 0;
        for(int i = 0; i < jobs.length; i++){
            jobs[i] = newJobs[i];
            totalJobs += jobs[i];
        }
       
    }
    
    
    
    //getters
    public int getTotalJobs(){
        return totalJobs;
    }
    
    public int getPopulation(){
        return population;
    }
    
    public double[] getTotalWagePerType(){
        return totalWagePerType;
    }
    
    /**
     * Positions actually staffed, across every tier.
     *
     * getTotalJobs() counts posts that exist; this counts the ones with someone
     * in them, which is what the wage bill is actually paid on and therefore
     * the right denominator for an average wage.
     */
    public int getJobsFilled(){
        double[] fillRate = getJobFillRate();
        double filled = 0;
        for(int i = 0; i < jobs.length; i++) {
            filled += jobs[i] * fillRate[i];
        }
        return (int) Math.round(filled);
    }

    public double getTotalWage(){
        double totalWage = 0;
        double[] fillRate = getJobFillRate().clone();
        for(int i = 0; i < totalWagePerType.length; i++) {
            totalWage += totalWagePerType[i]*fillRate[i];
        }
        return totalWage;
    }
    
    public double[] getWagesPerType(){
        return jobWage;
    }

    /**
     * The wage bill per job type with the fill rate already applied.
     *
     * The banded wage tax needs the split behind getTotalWage(), and it must be
     * the SAME split: this sums to getTotalWage() by construction, because it is
     * that method's loop body with the accumulation taken out.
     *
     * Handing out the unstaffed array and a separately-sampled fill rate looked
     * equivalent and was not - the fill rate is recomputed live on every call,
     * so the two were sampled at different moments and a reloaded city taxed a
     * different wage bill from the live one it was supposed to match.
     */
    public double[] getStaffedWagePerType(){
        double[] fillRate = getJobFillRate();
        double[] staffed = new double[totalWagePerType.length];
        for (int i = 0; i < totalWagePerType.length; i++) {
            staffed[i] = totalWagePerType[i] * fillRate[i];
        }
        return staffed;
    }
    
    /**
     * The same staffed wage bill, collapsed onto the six pay tiers.
     *
     * Migration watches a tier's cashflow for twelve months of decline, and a
     * tier is a group of job types - so somebody has to do this mapping. Doing
     * it HERE rather than at the call site is the whole point: this method is one
     * line of aggregation over getStaffedWagePerType(), so it cannot disagree
     * with the wage bill the tax is charged on. A second copy of the fill-rate
     * arithmetic somewhere else is exactly the bug this codebase keeps finding -
     * the copy is right the day it is written and wrong the first time the
     * original changes.
     */
    public double[] getStaffedWagePerTier(){
        double[] byType = getStaffedWagePerType();
        double[] byTier = new double[PayTier.values().length];
        for (JobType type : JobType.values()) {
            byTier[PayTier.of(type).ordinal()] += byType[type.ordinal()];
        }
        return byTier;
    }

    //setters
    /**
     * Wages, read from PayTier rather than typed out here.
     *
     * This used to be eleven hand-written figures with TEN distinct values -
     * UNIV_FINANCE and UNIV_HIGHTECH_ENG were both 6.5 - which made "group
     * families by what they earn" a ten-way split. PayTier collapses that to
     * six by role, and owns the numbers, so there is one wage table rather than
     * one table and a copy of it that agrees until somebody edits the wrong one.
     *
     * Measured cost of the collapse on a city of 8,792: the whole wage bill
     * moved -1.08%.
     */
    public void setWagesPerType(){
        for (JobType type : JobType.values()) {
            jobWage[type.ordinal()] = PayTier.wageOf(type);
        }
    }

    /**
     * Takes this month's wages from the market instead of the constants.
     *
     * setWagesPerType() above is kept as the starting position and as the
     * fallback: PayTier is still the one definition of the LADDER, and
     * LabourMarket anchors to it, so at the default minimum wage these two
     * produce the same eleven numbers. What changes is that after this call
     * they can move.
     */
    public void takeWagesFrom(LabourMarket market){
        if (market == null) return;
        double[] priced = market.getWages();
        System.arraycopy(priced, 0, jobWage, 0, Math.min(priced.length, jobWage.length));
    }
    public void setPopulation(int population){
        this.population = population;
    }

    /**
     * Recomputes workforce from the current population, without touching
     * population itself.
     *
     * NOTE: workforce was previously assigned in exactly one place - updatePop()
     * - which only the normal monthly path reaches. Game.rebuildSimulationState()
     * never called it, so after loading a save workforce stayed 0, getJobVacancy()
     * marked every position vacant, and getJobFillRate() returned 0 for every job
     * tier. That zeroed retail revenue and payroll, industrial output, utility and
     * construction expense, wage tax, and the construction-speed discount - which
     * is why a loaded game took two or three months to settle back to its real
     * numbers.
     *
     * updatePop() can't just be called here instead: it also recomputes population
     * from totalJobs, which hasn't been rebuilt yet at that point in
     * rebuildSimulationState(), and would clamp the restored city to 0.
     */
    public void recomputeWorkforce(){
        workforce = (int)(population * adultPercent);
    }

    /**
     * Puts back the workforce the month was actually worked by.
     *
     * recomputeWorkforce() above was the first fix for this and is no longer
     * the right one. It derives the workforce from the population the save was
     * taken WITH, but updatePop() derives it from the population the month
     * STARTED with - so in any city that is still growing, a reloaded save came
     * back with a month's extra workers. Measured at 418 against the 386 that
     * actually worked, on a city of 836: a 5% overstatement of the wage bill,
     * the wage tax, and every fill rate downstream of it.
     *
     * The third value in this codebase to be re-derived on load from state that
     * had since moved, after the property-tax charge and the month's income
     * statements. Same answer as both: it is a fact about a month, so carry it.
     *
     * recomputeWorkforce() stays as the fallback for saves written before this,
     * where it is still much better than the zero it originally replaced.
     */
    public void restoreWorkforce(int workforce){
        this.workforce = Math.max(0, workforce);
    }

    public int getWorkforceForSave(){
        return workforce;
    }
    
    public void UpdateTotalWagePerType(){
        for(int i = 0; i < totalWagePerType.length; i++){
            totalWagePerType[i] = jobs[i]*jobWage[i];
        }
    }
    
    /**
     * Positions nobody qualified is available to fill.
     *
     * THIS METHOD USED TO INVENT DOCTORS. It walked the job array from the top
     * index down - `for (int i = length - 1; i >= 0; i--)` - handing an
     * undifferentiated pool of adults to the most-skilled posts first and
     * passing the remainder downward. So a city with no education of any kind
     * staffed 220 doctor posts at 100% while its factories went empty, and the
     * eleven job types were a demand-side fiction.
     *
     * It now allocates by BAND, top down, cascading each band's surplus into
     * the bands below it. Downward substitution only: a graduate can labour, a
     * labourer cannot doctor. Within a band the workers are fungible, so a
     * short band's posts are filled proportionally rather than in enum order -
     * which matters, because enum order is arbitrary and would otherwise decide
     * that the city staffs its lawyers before its doctors.
     */
    public int[] getJobVacancy() {

        int[] vacancy = new int[totalWagePerType.length];
        double[] supply = workforceByBand();

        // Highest band first: a graduate takes graduate work if there is any,
        // and only labours with what is left over.
        double carried = 0;
        for (int b = WageBand.values().length - 1; b >= 0; b--) {

            double available = carried + supply[b];

            /*
             * GATED JOBS FIRST, AND ONLY FROM PEOPLE WHO HOLD THE LICENCE.
             *
             * A medical post can be filled by a doctor and by nobody else. It
             * is not enough to be a university graduate - that is precisely the
             * within-a-band fungibility the professional schools exist to
             * remove, and without this loop a city with no medical school would
             * go on staffing its hospitals out of the general graduate pool,
             * which is the 220-doctors bug wearing a different hat.
             *
             * Taken off `available` because a licence holder IS one of the
             * band's workers - the licence is a second fact about the same
             * person, not a second person. A surplus of licence holders stays
             * in `available` and competes for the ungated posts like anybody
             * else, which is right: a doctor with no hospital can do research.
             */
            double gatedPosts = 0;
            for (JobType job : JobType.values()) {
                if (WageBand.of(job).ordinal() != b) continue;
                if (!isGated(job)) continue;

                int i = job.ordinal();
                double staffed = Math.min(jobs[i], Math.min(licensed[i], available));
                vacancy[i] = Math.max(0, jobs[i] - (int) Math.round(staffed));
                available -= staffed;
                gatedPosts += jobs[i];
            }

            double posts = 0;
            for (JobType job : JobType.values()) {
                if (WageBand.of(job).ordinal() == b) posts += jobs[job.ordinal()];
            }
            double openPosts = posts - gatedPosts;

            double filled = Math.min(Math.max(available, 0), openPosts);
            double share = openPosts > 0 ? filled / openPosts : 1;

            for (JobType job : JobType.values()) {
                if (WageBand.of(job).ordinal() != b) continue;
                if (isGated(job)) continue;
                int i = job.ordinal();
                vacancy[i] = Math.max(0, jobs[i] - (int) Math.round(jobs[i] * share));
            }

            carried = Math.max(0, available - filled);
        }

        return vacancy;
    }

    /**
     * True for a job no amount of general education qualifies anybody for.
     *
     * Asked of EducationType rather than listed here, so adding a fifth
     * professional school gates its profession automatically and there is no
     * second list to forget.
     */
    public static boolean isGated(JobType job) {
        for (EducationType type : EducationType.values()) {
            if (type.licenses() == job) return true;
        }
        return false;
    }

    /**
     * The workforce split by skill.
     *
     * The skilled bands are carried counts; the unskilled band is the
     * remainder. If the city has shrunk below the number of skilled workers it
     * was carrying - a plague, an exodus - the skilled counts are scaled back
     * to fit rather than allowed to exceed the workforce, because a city cannot
     * have more doctors than adults.
     */
    public double[] workforceByBand() {
        double[] out = new double[skilledHeads.length];

        double skilled = 0;
        for (int b = 1; b < skilledHeads.length; b++) skilled += Math.max(0, skilledHeads[b]);

        double scale = (skilled > workforce && skilled > 0) ? workforce / skilled : 1;
        double used = 0;
        for (int b = 1; b < out.length; b++) {
            out[b] = Math.max(0, skilledHeads[b]) * scale;
            used += out[b];
        }
        out[WageBand.NONE.ordinal()] = Math.max(0, workforce - used);
        return out;
    }

    /** Posts that exist at each skill level. The demand side of the same axis. */
    public double[] postsByBand() {
        double[] out = new double[WageBand.values().length];
        for (JobType job : JobType.values()) {
            out[WageBand.of(job).ordinal()] += jobs[job.ordinal()];
        }
        return out;
    }

    /**
     * Workers actually available to each band, cascade included.
     *
     * What the labour market prices against. NOT the same as workforceByBand():
     * a band's supply is its own workers PLUS everyone above it who could not
     * find work at their own level, which is what makes an oversupply of
     * graduates depress the diploma wage rather than sitting in a separate
     * pool being unemployed on its own.
     */
    public double[] supplyByBand() {
        double[] own = workforceByBand();
        double[] posts = postsByBand();
        double[] out = new double[own.length];

        double carried = 0;
        for (int b = own.length - 1; b >= 0; b--) {
            out[b] = carried + own[b];
            carried = Math.max(0, out[b] - posts[b]);
        }
        return out;
    }

    /**
     * How many more workers of this band the city has than posts for them.
     *
     * Migration's push factor reads this. Counted against the band's OWN posts
     * rather than against the cascade, because a graduate labouring is employed,
     * not surplus - they are simply employed below their training, which is a
     * different complaint and not one that makes people leave a city.
     */
    public double surplusInBand(WageBand band) {
        return Math.max(0, workforceByBand()[band.ordinal()] - postsByBand()[band.ordinal()]);
    }

    /** The mix as shares, for anything that wants proportions. Derived. */
    public double[] getBandShare() {
        double[] heads = workforceByBand();
        double[] share = new double[heads.length];
        double total = 0;
        for (double h : heads) total += h;
        if (total <= 0) { share[WageBand.NONE.ordinal()] = 1; return share; }
        for (int b = 0; b < share.length; b++) share[b] = heads[b] / total;
        return share;
    }

    /** The carried skilled counts, for the save. */
    public double[] getSkilledHeads() { return skilledHeads; }

    public double getLicensed(JobType job) {
        return job == null ? 0 : licensed[job.ordinal()];
    }

    public double[] getLicensedHeads() { return licensed; }

    public void restoreLicensed(double[] saved) {
        if (saved == null || saved.length != licensed.length) return;
        System.arraycopy(saved, 0, licensed, 0, licensed.length);
    }

    /**
     * New graduates of the professional schools.
     *
     * Only ever grows here. A licence is not lost by working somewhere else -
     * a doctor driving a bus is still a doctor - so the only things that reduce
     * it are death and departure, which arrive through trim() below.
     */
    public void addLicences(double[] newly) {
        if (newly == null) return;
        for (int i = 0; i < licensed.length && i < newly.length; i++) {
            licensed[i] = Math.max(0, licensed[i] + newly[i]);
        }
    }

    /**
     * Death and retirement, which take the skilled along with everybody else.
     *
     * A RATCHET, AND IT WAS REAL. Skilled counts only ever moved by migration,
     * while the workforce also shrank by mortality and by adults ageing into
     * SENIOR - and the unskilled band is the REMAINDER, so every one of those
     * losses came out of the unskilled band alone. A doctor was, in effect,
     * immortal and never retired, and the city drifted more educated every
     * month for no reason anybody chose.
     *
     * The correction is the obvious one: an adult who dies or turns seventy is
     * a random adult, and a random adult is skilled in proportion to the mix.
     * Rate taken from AgeBand's own numbers rather than typed in here, so the
     * day somebody retunes adult mortality this follows it.
     *
     * Small - about two tenths of a per cent a month - and it compounds over
     * four hundred, which is exactly the kind of slow wrong that never looks
     * like a bug on any single screen.
     */
    public void retireSkilled(double leaveRate) {
        if (leaveRate <= 0) return;
        double survive = Math.max(0, 1 - leaveRate);
        for (int b = 1; b < skilledHeads.length; b++) skilledHeads[b] *= survive;
        for (int i = 0; i < licensed.length; i++) licensed[i] *= survive;
    }

    /**
     * Keeps the licensed counts inside the band that contains them.
     *
     * A licence holder is a university graduate first. When the graduate count
     * falls - people die, people leave - the licences have to fall with it or
     * the city ends up with more doctors than it has graduates, and the
     * allocator will happily staff posts out of the difference. Scaled rather
     * than subtracted, because there is no way to know WHICH of them went.
     */
    public void trimLicencesToBand() {
        double[] heads = workforceByBand();
        for (WageBand band : WageBand.values()) {
            double held = 0;
            for (JobType job : JobType.values()) {
                if (WageBand.of(job) == band) held += licensed[job.ordinal()];
            }
            if (held <= heads[band.ordinal()] || held <= 0) continue;

            double keep = heads[band.ordinal()] / held;
            for (JobType job : JobType.values()) {
                if (WageBand.of(job) == band) licensed[job.ordinal()] *= keep;
            }
        }
    }

    public void restoreSkilledHeads(double[] saved) {
        if (saved == null || saved.length != skilledHeads.length) return;
        System.arraycopy(saved, 0, skilledHeads, 0, skilledHeads.length);
        skilledHeads[WageBand.NONE.ordinal()] = 0;   // derived, never carried
    }

    /**
     * Who moved in and who moved out, by skill.
     *
     * The ONLY thing that changes the skilled counts, which is the whole rule:
     * until schools exist, a skill in this city arrived here in somebody's
     * head. The unskilled band is not touched because it is not stored - it is
     * whatever is left of the workforce, so births and deaths move it for free.
     */
    /**
     * This month's schooling, as a net movement between bands.
     *
     * Education hands over +1 at the destination and -1 at the source, so a
     * degree is a person MOVING rather than a person appearing - a city that
     * sends a thousand diploma-holders to university has a thousand fewer
     * diploma-holders, which is the difference between a labour market and a
     * wish. Index 0 is deliberately ignored: the unskilled band is not stored,
     * it is whatever is left of the workforce, so it shrinks on its own.
     */
    public void applyBandFlow(double[] flow) {
        if (flow == null) return;
        for (int b = 1; b < skilledHeads.length && b < flow.length; b++) {
            skilledHeads[b] = Math.max(0, skilledHeads[b] + flow[b]);
        }
    }

    public void applySkilledFlows(double[] arrivals, double[] departures) {
        for (int b = 1; b < skilledHeads.length; b++) {
            double in  = arrivals   == null || b >= arrivals.length   ? 0 : arrivals[b];
            double out = departures == null || b >= departures.length ? 0 : departures[b];
            skilledHeads[b] = Math.max(0, skilledHeads[b] + in - out);
        }
    }

    /**
     * Reads a plausible skill mix off the jobs the city is currently staffing.
     *
     * FOR SAVES WRITTEN BEFORE SKILL EXISTED, which have a workforce and no
     * record of what any of them can do. Resetting them all to unskilled would
     * close every hospital in the city on load; assuming everyone is a graduate
     * would be a gift. Reading it off the posts they are demonstrably filling
     * is the only inference that leaves the city exactly as the player left it.
     */
    public void inferBandShareFromJobs() {
        java.util.Arrays.fill(skilledHeads, 0);

        double[] posts = postsByBand();
        double assigned = 0;
        // Everyone holding a skilled post must have that skill; whoever is left
        // over is unskilled, which is the safe direction to be wrong in.
        for (int b = skilledHeads.length - 1; b >= 1; b--) {
            skilledHeads[b] = Math.min(posts[b], Math.max(0, workforce - assigned));
            assigned += skilledHeads[b];
        }
    }
    
    public double[] getJobFillRate(){
        double[] fillRate = new double[jobs.length];
        int[] vacancy = getJobVacancy().clone();
        for(int i = 0; i < jobs.length; i++){
            if(jobs[i]>0){
                fillRate[i] = (jobs[i] - vacancy[i]) / (double) jobs[i];
            }else{
            fillRate[i] = 1;
            }
        }
        
        return fillRate;
    }
    
    public int getWorkforce(){
        return workforce;
    }

    /**
     * Adults who want work and have none.
     *
     * Defined against getJobsFilled() rather than against the raw job count,
     * because a city can have more posts than workers AND unemployment at the
     * same time - a mine with nobody willing to take the shift does not employ
     * the shop assistant it could not hire either. The fill rate already
     * allocates the workforce across the posts; this is whatever it could not
     * place.
     *
     * Lives here rather than at the two screens that show it. The wage bill was
     * once computed in two places from a separately-sampled fill rate and the
     * copies disagreed, which is how a reloaded city came to tax a different
     * payroll from the live one; one definition, and it cannot happen again.
     */
    public int getUnemployed(){
        return Math.max(0, workforce - getJobsFilled());
    }

    /** Unemployed as a share of everyone who could work, 0-1. */
    public double getUnemploymentRate(){
        return workforce > 0 ? getUnemployed() / (double) workforce : 0;
    }
    
    public int[] getJobs(){
        return jobs.clone();
    }
    
    public double getAdultPercent(){
        return adultPercent;
    }
    
    public void printPopulationInfo() {

        System.out.println("\n====================== DEMOGRAPHIC & LABOR REPORT ======================");

        /* -------------------------------------------------------------------
       POPULATION OVERVIEW
       ------------------------------------------------------------------- */
        System.out.println("\nPOPULATION OVERVIEW");
        System.out.printf("Total Population:        %,d citizens%n", population);
        System.out.printf("Workforce Share:         %.1f%%%n", adultPercent * 100);
        System.out.printf("Total Workforce:         %,d workers%n", workforce);

        /* -------------------------------------------------------------------
       LABOR MARKET SUMMARY
       ------------------------------------------------------------------- */
        int[] vacancies = getJobVacancy();
        double[] fillRates = getJobFillRate();

        int totalVacancies = 0;
        for (int v : vacancies) {
            totalVacancies += v;
        }

        double utilization
                = (totalJobs > 0)
                        ? ((double) (workforce - Math.max(0, workforce - totalJobs)) / totalJobs) * 100
                        : 0;

        System.out.println("\nLABOR MARKET SUMMARY");
        System.out.printf("Total Jobs Available:    %,d positions%n", totalJobs);
        System.out.printf("Total Vacancies:         %,d positions%n", totalVacancies);
        System.out.printf("Workforce Utilization:   %.2f%%%n", utilization);

        /* -------------------------------------------------------------------
       JOB DISTRIBUTION TABLE
       ------------------------------------------------------------------- */
        System.out.println("\nLABOR SUPPLY BY EDUCATION / JOB TIER");

        System.out.printf("%-20s | %-14s | %-9s | %-10s | %-14s%n",
                "Job Type (Enum)", "Jobs Available", "Vacancies", "Fill Rate", "Monthly Payroll");

        System.out.println("-------------------------------------------------------------------------------");

        for (int i = 0; i < jobs.length; i++) {

            if (jobs[i] > 0) {

                String jobName = jobTypes[i].name();

                double payroll = jobWage[i] * jobs[i];

                System.out.printf("%-20s | %,14d | %,9d | %8.1f%% | $%13s%n",
                        jobName,
                        jobs[i],
                        vacancies[i],
                        fillRates[i] * 100,
                        formatter.format(payroll)
                );
            }
        }

        /* -------------------------------------------------------------------
       LABOR MARKET STATUS
       ------------------------------------------------------------------- */
        if (workforce > totalJobs) {

            System.out.println("\nSTATUS: LABOR SURPLUS");
            System.out.printf("%,d citizens are currently seeking employment.%n",
                    (workforce - totalJobs));

        } else if (totalVacancies > 0) {

            System.out.println("\nWARNING: LABOR SHORTAGE");
            System.out.printf("%,d positions across the city remain unfilled.%n",
                    totalVacancies);
        }

        System.out.println("==========================================================================\n");
    }

    public void resetPopulationManager() {
        population = 0;
        totalJobs = 0;
        workforce = 0;
    }
    
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
    
    
}
