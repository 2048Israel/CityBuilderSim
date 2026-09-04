
package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public class SimulationEngine {

    private EconomyManager economyManager;
    private PopulationManager populationManager;
    private ServicesManager servicesManager;
    private BuildingManager buildingManager;
    private DebtManager debtManager;

    public SimulationEngine(
            EconomyManager economyManager,
            PopulationManager populationManager,
            ServicesManager servicesManager,
            BuildingManager buildingManager,
            DebtManager debtManager) {

        this.economyManager = economyManager;
        this.populationManager = populationManager;
        this.servicesManager = servicesManager;
        this.buildingManager = buildingManager;
        this.debtManager = debtManager;
    }

    public void simulateMonth(Game game) {

        /*
         * The month's build rate: capacity, discounted for staffing and for
         * congestion.
         *
         * Asked of Game rather than worked out here, because the same figure is
         * also what the screen shows and what construction is PAID for in
         * startOfMonthUpdate(). This used to be a second copy of the arithmetic
         * and the two drifted the moment roads were added - the sites crawled
         * while the sector still billed for a full month. One definition now.
         *
         * Both discounts are a month stale, deliberately. The fill rate is
         * whatever ConstructionHandler last computed, since this month's
         * updateServices() has not run yet, and the road ratio is the network as
         * it stood while the crews were working. Reordering to make either
         * current would change *when* a newly finished building starts counting
         * toward population and the economy, which is a much larger behavioural
         * change than the discount itself.
         *
         * Congestion slowing the site is the pointed part: a jammed city builds
         * the roads that would unjam it more slowly. MIN_THROUGHPUT is what
         * keeps that from being a trap - it always digs itself out eventually,
         * slowly, at a real cost in months.
         */
        game.recordCompletions(
                buildingManager.advanceConstruction(game.getConstructionOutput()));

        // Roads, before anything reads them. Capacity and load are both pure
        // functions of what is standing, and what is standing just changed:
        // the shops that opened this morning trade this month, are counted for
        // population and jobs this month, and put their trucks on the road this
        // month too. Recomputing here rather than waiting for updateServices()
        // is also what lets a reloaded save agree with the game it was saved
        // from - see ServicesManager.updateInfrastructure().
        servicesManager.updateInfrastructure();

        // updatePopulation(), NOT refreshPopulationInputs() - a month is exactly
        // when the city's population is supposed to move. Routing this through
        // refreshDerivedState() once seemed tidy and quietly froze every city at
        // zero residents, which is the difference between the two calls.
        updatePopulation(game);
        updateEconomy(game);
        updateServices(game);
    }

    public void updatePopulation(Game game) {
        game.updatePopulation();
        populationManager.updateJobs(game.getJobs());
        populationManager.UpdateTotalWagePerType();
    }
    
    public void updateEconomy(Game game) {
        //TBE
        
        economyManager.setTotalJobs(populationManager.getTotalJobs());
        economyManager.setPopulation(populationManager.getPopulation());
        economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());
        economyManager.updateStoreWages(populationManager.getWagesPerType(),buildingManager.getJobArrayPerCategory(BuildingType.COMMERCIAL));
        economyManager.updateIndustrialWages(populationManager.getWagesPerType());
        economyManager.updateJobFillRate(populationManager.getJobFillRate());
        // After updateJobFillRate: the mills' payroll is discounted by the fill,
        // so the fill has to be current before their wages are set.
        economyManager.updateHeavyIndustryWages(populationManager.getWagesPerType());
        economyManager.updateMiningWages(populationManager.getWagesPerType());
        economyManager.setTotalWage(populationManager.getTotalWage());

        // The split behind that total. The wage tax is banded, so a single
        // figure cannot be charged at four different rates - and this line has
        // to sit beside setTotalWage() in BOTH places that sync wages, the
        // monthly path here and rebuildSimulationState() on load, or the two
        // disagree and a reloaded city taxes a different wage bill.
        economyManager.setWageDetail(populationManager.getStaffedWagePerType());
        economyManager.setEnergyRatio(servicesManager.getEnergyRatio());
        economyManager.setWaterRatio(servicesManager.getWaterRatio());
        economyManager.setRoadRatio(servicesManager.getRoadRatio());
        economyManager.updateEcon();
        
         
    }
    
    public void updateServices(Game game){
        servicesManager.updateServiceWages(populationManager.getWagesPerType());
        servicesManager.updateJobFillRate(populationManager.getJobFillRate());
        // must precede updateServices(): the residents' draw is part of the
        // water demand the ratio is computed against
        servicesManager.setPopulation(populationManager.getPopulation());
        servicesManager.updateServices();
        servicesManager.updateFromGame(servicesManager.getConstructionHandler()::setMaterialsInventory, buildingManager.getConstructionMaterials());//must finish
        servicesManager.updateFromGame(servicesManager.getConstructionHandler()::setMaterialsPrice, buildingManager.getConstructionMaterialPrice());
        servicesManager.updateFromGameInt(servicesManager.getConstructionHandler()::setMaterialsConsumed,game.materialsConsumed);
       
    
    }

}
