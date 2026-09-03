
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

        // NOTE: fill-rate discount added here to match the wage-expense fix in
        // ConstructionHandler.calculateExpenses() - an understaffed construction
        // sector should build slower, not just cost less. This uses whatever
        // fill rate ConstructionHandler last computed (i.e. from last month's
        // updateServices() call, since this month's hasn't run yet at this point
        // in the cycle) rather than reordering the whole simulation - moving this
        // call after updateServices() would change *when* a building that
        // finishes construction starts counting toward population/economy for
        // the month, which is a bigger behavioral change than what was asked for.
        double constructionFillRate = servicesManager.getConstructionHandler().getAverageFill();
        int discountedCapacity = (int) Math.round(
                buildingManager.getTotalConstructionCapacity() * constructionFillRate
        );
        buildingManager.advanceConstruction(discountedCapacity);

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
        economyManager.setTotalWage(populationManager.getTotalWage());
        economyManager.setEnergyRatio(servicesManager.getEnergyRatio());
        economyManager.setWaterRatio(servicesManager.getWaterRatio());
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
