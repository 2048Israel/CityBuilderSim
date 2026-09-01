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

    // utilities labor
    private final double[] utilityWages = new double[11];
    private final int[] utilityJobs = new int[11];

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

        utilitiesHandler.updateUtilitiyWages(utilityWages, utilityJobs);
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

        copyArray(
                buildingManager.getJobArrayPerCategory(BuildingType.ELECTRICITY),
                utilityJobs
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

    public double getServiceNetIncome() {
        double income = -constructionHandler.getExpenses()+utilitiesHandler.getUtilityIncome();
        return income;
    }

    public double getPricePerWatt() {
        return utilitiesHandler.getPricerPerWatt();
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