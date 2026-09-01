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
    JobType[] jobNoUse = JobType.values();
    //
    private int[] jobs = new int[jobNoUse.length];
    private double[] jobWage = new double[jobs.length];
    private double[] totalWagePerType = new double[jobWage.length];
    private int totalJobs;
    //temporary
    private double adultPercent = .5;
    private int workforce;
    
    
    
    
    
    public int updatePop(int householdCapacity){
        //change later
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

                String jobName = jobNoUse[i].name();

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
