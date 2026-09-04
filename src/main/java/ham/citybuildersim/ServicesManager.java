package ham.citybuildersim;

import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.ToDoubleFunction;

public class ServicesManager {

    private final BuildingManager buildingManager;

    private final UtilitiesHandler utilitiesHandler;
    private final ConstructionHandler constructionHandler;
    private final InfrastructureManager infrastructureManager;

    // universal
    private final double[] fillRate = new double[11];

    // utilities labor - kept split so the utilities report can attribute
    // payroll to electricity or water rather than showing one lump
    private final double[] utilityWages = new double[11];
    private final int[] electricityJobs = new int[11];
    private final int[] waterJobs = new int[11];

    // construction labor
    private final double[] constructionWages = new double[11];
    private final int[] constructionJobs = new int[11];

    public ServicesManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
        utilitiesHandler = new UtilitiesHandler();
        constructionHandler = new ConstructionHandler();
        infrastructureManager = new InfrastructureManager();
    }

    // ===============================
    // MAIN UPDATE
    // ===============================

    public void updateServices() {

        updateProduction();
        updateLabor();
        updateHandlers();

    }

    // ===============================
    // UPDATE PHASES
    // ===============================

    private void updateProduction() {

        // electricity
        updateByCategoryHandlerDouble(
                BuildingType.ELECTRICITY,
                utilitiesHandler::setWattsProduction,
                BuildingsTemplate::getProduction1);

        updateHandlerDouble(
                utilitiesHandler::setWattsConsumption,
                BuildingsTemplate::getElectricityConsumption);

        // water
        updateByCategoryHandlerDouble(
                BuildingType.WATER,
                utilitiesHandler::setWaterProduction,
                BuildingsTemplate::getProduction1);

        // every building standing draws water, whatever its category - the
        // mirror of the electricity draw above
        updateHandlerDouble(
                utilitiesHandler::setBuildingWaterDraw,
                BuildingsTemplate::getWaterConsumption);

        // ...but only businesses are invoiced for it. Residents and the city's
        // own buildings draw water nobody is billed for.
        //
        // Heavy industry belongs here because HeavyIndustryHandler charges
        // itself for water on its own income statement. Leaving it out would
        // mean a mill paying a water bill the utility never books - money
        // leaving the economy with no one receiving it, which is the exact
        // shape of bug this file has already been fixed for once.
        /*
         * The same for power, and for the same reason. Four categories are
         * invoiced for electricity - the two above plus mining, which bills
         * itself on its own income statement. Everything else in the city draws
         * power nobody pays for, and until this existed the utility booked
         * revenue on all of it.
         */
        utilitiesHandler.setBilledElectricityDraw(
                buildingManager.getTotalByCategoryDouble(
                        BuildingType.COMMERCIAL, BuildingsTemplate::getElectricityConsumption)
                + buildingManager.getTotalByCategoryDouble(
                        BuildingType.INDUSTRIAL, BuildingsTemplate::getElectricityConsumption)
                + buildingManager.getTotalByCategoryDouble(
                        BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getElectricityConsumption)
                + buildingManager.getTotalByCategoryDouble(
                        BuildingType.MINING, BuildingsTemplate::getElectricityConsumption));

        utilitiesHandler.setBilledWaterDraw(
                buildingManager.getTotalByCategoryDouble(
                        BuildingType.COMMERCIAL, BuildingsTemplate::getWaterConsumption)
                + buildingManager.getTotalByCategoryDouble(
                        BuildingType.INDUSTRIAL, BuildingsTemplate::getWaterConsumption)
                + buildingManager.getTotalByCategoryDouble(
                        BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getWaterConsumption));

        updateInfrastructure();

        // construction
        updateByCategoryHandlerDouble(
                BuildingType.CONSTRUCTION,
                constructionHandler::setConstructionProduction,
                BuildingsTemplate::getProduction1);

        updateByCategoryHandlerDouble(
                BuildingType.CONSTRUCTION,
                constructionHandler::setConstructionMaterialsProduction,
                BuildingsTemplate::getProduction2);
    }

    /**
     * Recomputes the road network from what is standing right now.
     *
     * Capacity comes from the INFRASTRUCTURE buildings, load from every building
     * in the city - the same whole-city sweep the power draw uses, and for the
     * same reason: a house generates traffic exactly as it draws current,
     * whoever owns it.
     *
     * Both read the standing stock only, so a road still under construction
     * carries nothing and a factory still under construction sends no trucks.
     * You cannot relieve congestion by ordering a road, only by finishing one.
     *
     * PUBLIC, AND CALLED TWICE A MONTH ON PURPOSE
     *
     * This is a pure function of the building stock and holds no history, which
     * makes it the one piece of derived state that can safely be recomputed at
     * any point - and it has to be, because the economy reads it mid-month,
     * after construction has completed but before updateServices() runs.
     *
     * Leaving it to the end-of-month sweep alone cost a round trip: the live
     * game ran a month against the network as it stood before that month's
     * buildings opened, while a reloaded save recomputed it from the stock the
     * month ENDED on. Same city, two different sales-tax figures, and the save
     * check caught it as a $0.51 drift in next month's income. Energy and water
     * have the same one-month lag and it has never shown, because it only
     * matters once a ratio is actually below 1 - the reason a sector with
     * nothing in it proves nothing about that sector.
     */
    public void updateInfrastructure() {

        updateByCategoryHandlerDouble(
                BuildingType.INFRASTRUCTURE,
                infrastructureManager::setBuiltCapacity,
                BuildingsTemplate::getCapacity);

        updateHandlerDouble(
                infrastructureManager::setLoad,
                BuildingsTemplate::getRoadLoad);
    }

    private void updateLabor() {

        utilitiesHandler.updateJobFillRate(fillRate);
        constructionHandler.updateJobFillRate(fillRate);

        utilitiesHandler.updateUtilitiyWages(utilityWages, electricityJobs, waterJobs);
        constructionHandler.updateWages(constructionWages, constructionJobs);
    }

    private void updateHandlers() {

        utilitiesHandler.updateUtilitiesHandler();
        constructionHandler.updateConstructionHandler();
    }

    // ===============================
    // INPUT FROM OTHER SYSTEMS
    // ===============================

    // NOTE: this used to be named updateIndustrialWages() - a copy-paste of
    // EconomyManager's method name that had nothing to do with what this does.
    // It applies the general per-job-tier wage rate array to this manager's
    // own sectors (ELECTRICITY and CONSTRUCTION jobs), not BuildingType.INDUSTRIAL.
    public void updateServiceWages(double[] wages) {

        copyArray(wages, utilityWages);
        copyArray(wages, constructionWages);

        // ELECTRICITY and WATER are staffed out of the same utility payroll and
        // share averageUtilityFill, but are tracked separately so the report can
        // show them as two businesses. UtilitiesHandler sums them for the fill.
        copyArray(
                buildingManager.getJobArrayPerCategory(BuildingType.ELECTRICITY),
                electricityJobs
        );

        copyArray(
                buildingManager.getJobArrayPerCategory(BuildingType.WATER),
                waterJobs
        );

        copyArray(
                buildingManager.getJobArrayPerCategory(BuildingType.CONSTRUCTION),
                constructionJobs
        );
    }

    public void updateJobFillRate(double[] fillRate) {
        copyArray(fillRate, this.fillRate);
    }

    // ===============================
    // GETTERS
    // ===============================

    public ConstructionHandler getConstructionHandler() {
        return constructionHandler;
    }

    public UtilitiesHandler getUtilitiesHandler() {
        return utilitiesHandler;
    }

    public InfrastructureManager getInfrastructureManager() {
        return infrastructureManager;
    }

    public double getEnergyRatio() {
        return utilitiesHandler.getEnergyRatio();
    }

    public double getWaterRatio() {
        return utilitiesHandler.getWaterRatio();
    }

    /**
     * What congestion lets the city actually get done, 0.35 to 1.
     *
     * Sits alongside getEnergyRatio() and getWaterRatio() deliberately: three
     * municipal capacities, three throttles on the same private activity, all
     * applied at the same point in the month.
     */
    public double getRoadRatio() {
        return infrastructureManager.getThroughputRatio();
    }

    /**
     * What the CITY earns from the services it owns - now utilities only.
     *
     * Construction used to be subtracted here, so the city paid the
     * construction sector's wages every month on top of paying for the
     * buildings themselves. Construction bills its customers now and settles
     * its own payroll out of that revenue, so taking it off the city's books
     * here would be charging the same wages twice.
     */
    public double getServiceNetIncome() {
        return utilitiesHandler.getUtilityIncome();
    }

    public double getPricePerWatt() {
        return utilitiesHandler.getPricerPerWatt();
    }

    public double getPricePerWaterUnit() {
        return utilitiesHandler.getPricePerWaterUnit();
    }

    /** The people's water draw. Buildings are picked up from the templates. */
    public void setPopulation(int population) {
        utilitiesHandler.setPopulation(population);
    }
    

    // ===============================
    // PRINTERS
    // ===============================

    public void printUtilityInfo() {
        utilitiesHandler.printUtilitiesInfo();
    }

    public void printConstructionInfo() {
        constructionHandler.printConstructionInfo();
    }

    // ===============================
    // GENERIC BUILDING AGGREGATION
    // ===============================

    public void updateByCategoryHandlerDouble(
            BuildingType category,
            DoubleConsumer setter,
            ToDoubleFunction<BuildingsTemplate> getter) {

        setter.accept(
                buildingManager.getTotalByCategoryDouble(category, getter)
        );
    }

    public void updateHandlerDouble(
            DoubleConsumer setter,
            ToDoubleFunction<BuildingsTemplate> getter) {

        setter.accept(
                buildingManager.getTotalDouble(getter)
        );
    }

    // ===============================
    // GAME → HANDLER HELPERS
    // ===============================

    public void updateFromGame(DoubleConsumer setter, double value) {
        setter.accept(value);
    }

    public void updateFromGameInt(IntConsumer setter, int value) {
        setter.accept(value);
    }

    // ===============================
    // ARRAY HELPERS
    // ===============================

    private void copyArray(double[] source, double[] target) {
        System.arraycopy(source, 0, target, 0, Math.min(source.length, target.length));
    }

    private void copyArray(int[] source, int[] target) {
        System.arraycopy(source, 0, target, 0, Math.min(source.length, target.length));
    }

}