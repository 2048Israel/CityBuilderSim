package ham.citybuildersim;

import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.ToDoubleFunction;

public class ServicesManager {

    private final BuildingManager buildingManager;

    private final UtilitiesHandler utilitiesHandler;
    private final ConstructionHandler constructionHandler;

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

        // ...but only commercial and industrial are invoiced for it. Residents
        // and the city's own buildings draw water nobody is billed for.
        utilitiesHandler.setBilledWaterDraw(
                buildingManager.getTotalByCategoryDouble(
                        BuildingType.COMMERCIAL, BuildingsTemplate::getWaterConsumption)
                + buildingManager.getTotalByCategoryDouble(
                        BuildingType.INDUSTRIAL, BuildingsTemplate::getWaterConsumption));

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

    public double getEnergyRatio() {
        return utilitiesHandler.getEnergyRatio();
    }

    public double getWaterRatio() {
        return utilitiesHandler.getWaterRatio();
    }

    public double getServiceNetIncome() {
        double income = -constructionHandler.getExpenses()+utilitiesHandler.getUtilityIncome();
        return income;
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