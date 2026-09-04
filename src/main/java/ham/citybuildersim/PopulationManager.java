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
    
    public int[] getJobVacancy() {

    int workforceLeft = workforce;
    int[] vacancy = new int[totalWagePerType.length];

    for (int i = totalWagePerType.length - 1; i >= 0; i--) {

        if (workforceLeft >= jobs[i]) {
            // Enough workers to fill all jobs
            workforceLeft -= jobs[i];
            vacancy[i] = 0;
        } else {
            // Not enough workers
            vacancy[i] = jobs[i] - workforceLeft;
            workforceLeft = 0;
        }
    }

    return vacancy;
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
