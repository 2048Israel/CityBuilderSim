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
    
    
    
    
    
    public int updatePop(int householdCapacity){

        /*
         * Workforce is taken from the population BEFORE the month moves it, and
         * that ordering is deliberate rather than accidental: the people who
         * worked this month are the ones who already lived here. Somebody who
         * moves in this month starts work next month.
         *
         * It is also why the workforce has to be SAVED rather than recomputed -
         * see recomputeWorkforce(). Leave this order alone unless you mean to
         * change how fast a growing city staffs itself, which is a balance
         * decision, not a tidy-up.
         */
        double temp = population*adultPercent;
        workforce = (int)temp;
        double cap = totalJobs*(1+adultPercent)*1.5;
        int cap1 = (int)Math.round(cap);
        return population = Math.min(householdCapacity,cap1);
        
        
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
    
    //setters
    public void setWagesPerType(){
        jobWage[0] = .800;
        jobWage[1] = 1.500;
        jobWage[2] = 3.500;
        jobWage[3] = 3.000;
        jobWage[4] = 4.000;
        jobWage[5] = 8.000;
        jobWage[6] = 7.500;
        jobWage[7] = 6.500;
        jobWage[8] = 5.500;
        jobWage[9] = 6.500;
        jobWage[10] = 6.000;
        
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
