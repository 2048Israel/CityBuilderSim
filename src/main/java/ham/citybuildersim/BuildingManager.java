package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 *
 * @author Jerus
 */
public class BuildingManager {

    private List<BuildingsTemplate> templates;
    private List<BuildingsStacks> stacks;
    private List<BuildingInstance> instances;
    private JobType[] jobNoUse = JobType.values();
    private int totalConstructionMaterialCost;
    private int constructionMaterials;
    private double materialsCost = 2;
    private double cost; //the total cost of materials that need to be purchased this month

    public BuildingManager() {
        templates = new ArrayList<>();
        stacks = new ArrayList<>();
        instances = new ArrayList<>();
    }

    /* -------------------------------------------------------------------------
       WATER DRAW, per building, in units of 10,000 gallons/month.

       This is the building's OWN use - landscaping, cooling, cleaning, process
       water - on top of the per-resident draw the population contributes
       separately (see UtilitiesHandler.WATER_PER_PERSON). Residential numbers
       are therefore small and fall per capita as density rises: a House with a
       yard is .05/person, an apartment block .024/person.

       Industry is where water actually bites, and that is true to life:
       textile dyeing and food processing are two of the most water-intensive
       industries there are, and thermoelectric cooling is the single largest
       category of freshwater withdrawal in the US - hence the coal plant's 400.
       ------------------------------------------------------------------------- */

    //Initialize all templates
    public void initializeTemplates() {
        
        //Residential Buildings
        BuildingsTemplate house = new BuildingsTemplate("House", BuildingType.RESIDENTIAL);
        house.setCapacity(4);
        house.setCashCost(30);
        house.setConstructionPoints(30);
        house.setConstructionMaterials(30);
        house.setElectricityConsumption(1);
        house.setWaterConsumption(.2);
        house.setId(0);
        templates.add(house);

        BuildingsTemplate studioApartments = new BuildingsTemplate("Studio Apartments", BuildingType.RESIDENTIAL);
        studioApartments.setCapacity(80);
        studioApartments.setCashCost(2000);
        studioApartments.setConstructionPoints(2000);
        studioApartments.setConstructionMaterials(2000);
        studioApartments.setElectricityConsumption(8);
        studioApartments.setWaterConsumption(2);
        studioApartments.setId(1);
        templates.add(studioApartments);

        BuildingsTemplate lowRiseApartments = new BuildingsTemplate("Low-Rise Apartments", BuildingType.RESIDENTIAL);
        lowRiseApartments.setCapacity(250);
        lowRiseApartments.setCashCost(7000);
        lowRiseApartments.setConstructionPoints(7000);
        lowRiseApartments.setConstructionMaterials(6000);
        lowRiseApartments.setElectricityConsumption(25);
        lowRiseApartments.setWaterConsumption(6);
        lowRiseApartments.setId(6);
        templates.add(lowRiseApartments);

        // Commercial Buildings
        BuildingsTemplate convienceStore = new BuildingsTemplate("Convience Store", BuildingType.COMMERCIAL);
        convienceStore.setCoverage(120);
        convienceStore.setCapacity(350);
        convienceStore.setCashCost(120);
        convienceStore.setConstructionPoints(120);
        convienceStore.setConstructionMaterials(80);
        convienceStore.setElectricityConsumption(6);
        convienceStore.setWaterConsumption(1);
        convienceStore.setJobs(JobType.NO_DIPLOMA, 2);
        convienceStore.setJobs(JobType.DIPLOMA, 3);
        convienceStore.setId(2);
        templates.add(convienceStore);

        BuildingsTemplate smallGroceryStore = new BuildingsTemplate("Small Grocery Store", BuildingType.COMMERCIAL);
        smallGroceryStore.setCoverage(800);
        smallGroceryStore.setCapacity(3500);
        smallGroceryStore.setCashCost(900);
        smallGroceryStore.setConstructionPoints(800);
        smallGroceryStore.setConstructionMaterials(700);
        smallGroceryStore.setElectricityConsumption(35);
        smallGroceryStore.setWaterConsumption(6);
        smallGroceryStore.setJobs(JobType.NO_DIPLOMA, 15);
        smallGroceryStore.setJobs(JobType.DIPLOMA, 10);
        smallGroceryStore.setJobs(JobType.COLLEGE_BUSINESS, 2);
        smallGroceryStore.setId(5);
        templates.add(smallGroceryStore);

        //Industrial Buildings
        BuildingsTemplate texttileMill = new BuildingsTemplate("Texttile Mill", BuildingType.INDUSTRIAL);
        texttileMill.setCapacity(3000);
        texttileMill.setCashCost(1200);
        texttileMill.setConstructionPoints(1800);
        texttileMill.setConstructionMaterials(1600);
        texttileMill.setProduction1(1100);
        texttileMill.setElectricityConsumption(40);
        texttileMill.setWaterConsumption(60);
        texttileMill.setJobs(JobType.NO_DIPLOMA, 45);
        texttileMill.setJobs(JobType.DIPLOMA, 20);
        texttileMill.setId(3);
        templates.add(texttileMill);

        BuildingsTemplate foodProcessingPlant = new BuildingsTemplate("Food Processing Plant", BuildingType.INDUSTRIAL);
        foodProcessingPlant.setCapacity(18000);
        foodProcessingPlant.setCashCost(3500);
        foodProcessingPlant.setConstructionPoints(3500);
        foodProcessingPlant.setConstructionMaterials(3000);
        foodProcessingPlant.setProduction1(6000);
        foodProcessingPlant.setElectricityConsumption(120);
        foodProcessingPlant.setWaterConsumption(150);
        foodProcessingPlant.setJobs(JobType.NO_DIPLOMA, 140);
        foodProcessingPlant.setJobs(JobType.DIPLOMA, 120);
        foodProcessingPlant.setJobs(JobType.COLLEGE_ENGINEERING, 10);
        foodProcessingPlant.setId(7);
        templates.add(foodProcessingPlant);

        BuildingsTemplate constructionMaterialsPlant = new BuildingsTemplate("Construction Materials Plant", BuildingType.CONSTRUCTION);
        constructionMaterialsPlant.setCapacity(5);
        constructionMaterialsPlant.setCashCost(20000);
        constructionMaterialsPlant.setConstructionPoints(9000);
        constructionMaterialsPlant.setConstructionMaterials(7000);
        constructionMaterialsPlant.setProduction2(400);
        constructionMaterialsPlant.setElectricityConsumption(200);
        constructionMaterialsPlant.setWaterConsumption(80);
        constructionMaterialsPlant.setJobs(JobType.NO_DIPLOMA, 160);
        constructionMaterialsPlant.setJobs(JobType.DIPLOMA, 80);
        constructionMaterialsPlant.setJobs(JobType.COLLEGE_ENGINEERING, 10);
        constructionMaterialsPlant.setId(8);
        templates.add(constructionMaterialsPlant);

        BuildingsTemplate constructionDepot = new BuildingsTemplate("Construction Depot", BuildingType.CONSTRUCTION);
        constructionDepot.setCapacity(5);
        constructionDepot.setCashCost(9000);
        constructionDepot.setConstructionPoints(400);
        constructionDepot.setConstructionMaterials(1000);
        constructionDepot.setProduction1(400);
        constructionDepot.setElectricityConsumption(25);
        constructionDepot.setWaterConsumption(3);
        constructionDepot.setJobs(JobType.NO_DIPLOMA, 35);
        constructionDepot.setJobs(JobType.DIPLOMA, 15);
        constructionDepot.setId(4);
        templates.add(constructionDepot);

        // ELECTRICTY buildings
        BuildingsTemplate coalPowerplant = new BuildingsTemplate("Coal Power Plant", BuildingType.ELECTRICITY)
                .setCashCost(125000)
                .setConstructionPoints(120000)
                .setConstructionMaterials(40000)
                .setProduction1(280000) // electricity output
                .setElectricityConsumption(15)
                .setWaterConsumption(400) // cooling - the biggest single draw in the game
                .setJobs(JobType.NO_DIPLOMA, 40)
                .setJobs(JobType.DIPLOMA, 20)
                .setJobs(JobType.COLLEGE_ENGINEERING, 6)
                .setJobs(JobType.UNIV_SCIENCE, 2)
                .setId(9);

        templates.add(coalPowerplant);

        // WATER buildings
        //
        // Costed off a real ~20 MGD conventional treatment plant serving about
        // 100,000 people, which runs ~$95M all-in. As with House, cashCost is
        // only part of that: the materials are bought at market price
        // (22,000 x $2k = $44M) and the labour is paid by the construction
        // sector, so cash covers the equipment, land and engineering.
        //
        // constructionPoints is also the build-time knob. At 44,000 it is
        // roughly a third of the coal plant's 120,000 - a long project, but
        // the city hits the water wall well before it can afford a power
        // plant, so it needs to be reachable sooner.
        BuildingsTemplate waterTreatmentPlant = new BuildingsTemplate("Water Treatment Plant", BuildingType.WATER)
                .setCashCost(45000)
                .setConstructionPoints(44000)
                .setConstructionMaterials(22000)
                .setProduction1(60000) // water output
                // Water and wastewater are typically 2-4% of a city's electrical
                // load. 900 against ~25,000 houses' worth of draw sits in that band.
                .setElectricityConsumption(900)
                .setWaterConsumption(20) // filter backwash and process losses
                .setJobs(JobType.NO_DIPLOMA, 8)
                .setJobs(JobType.DIPLOMA, 14)          // certified operators, the bulk of the crew
                .setJobs(JobType.COLLEGE_ENGINEERING, 4)
                .setJobs(JobType.UNIV_SCIENCE, 3)      // water quality lab
                .setId(10);

        templates.add(waterTreatmentPlant);

        //add more buildings; next Building ID is 11
    }

    public void finalUpdateBuildings() {
        System.out.println("Construction Materials Produced:            " + getConstructionMaterialsProduction());
        constructionMaterials += getConstructionMaterialsProduction();
        System.out.println("Total Available:                            " + constructionMaterials);

    }

    public double getSetCost() {
        double cost1 = cost;
        cost = 0;
        return cost1;
    }

    //getters
    public double getConstructionMaterialPrice(){
        return materialsCost;
    }
    
    
    public void addStack(BuildingsTemplate template, int quantity, boolean noConstruction) {

        if (template.getName() == null) {
            System.out.println("error");
            return;
        }

        int materialCost = 0;
        if(!noConstruction) materialCost = template.getConstructionMaterials() * quantity;
        

        // Always handle materials immediately
        handleConstructionMaterials(materialCost);

        for (BuildingsStacks stack : stacks) {
            if (stack.getName().equals(template.getName())) {

                if (noConstruction) {
                    stack.addQuantity(quantity);
                } else {
                    stack.startConstruction(quantity);
                }
                return;
            }
        }

        // Stack does not exist
        BuildingsStacks newStack = new BuildingsStacks(template, 0);
        stacks.add(newStack);

        if (noConstruction) {
            newStack.addQuantity(quantity);
        } else {
            newStack.startConstruction(quantity);
        }
    }

    public List<BuildingsTemplate> getTemplatesByCategory(EnumSet<BuildingType> categories) {
        List<BuildingsTemplate> result = new ArrayList<>();

        for (BuildingsTemplate t : templates) {
            if (categories.contains(t.getCategory())) {
                result.add(t);
            }
        }

        return result;
    }

    public void addInstance(BuildingsTemplate template) {
        instances.add(new BuildingInstance(template));
    }

    public void advanceConstruction(int constructionOutput) {
        if (getUnderConstruction() != 0) {
            double outputPerStack = (double) constructionOutput / getUnderConstruction();
            for (BuildingsStacks stack : stacks) {
                stack.advanceConstruction(outputPerStack);
            }
            for (BuildingInstance inst : instances) {
                inst.advanceConstruction();
            }
        }

        totalConstructionMaterialCost = 0;
    }

    public void displayAllBuildings() {
        System.out.println("--- Aggregated Buildings ---");
        for (BuildingsStacks stack : stacks) {
            System.out.println(stack.getName() + " x" + stack.getQuantity()
                    + " (Under Construction: " + stack.getUnderConstruction() + ")");
        }
    }
    //getters

    public BuildingsTemplate getTemplateByName(String name) {
        for (BuildingsTemplate t : templates) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    public int[] getTotalJobs() {
        // NOTE: this used to loop over `stacks` directly and never counted
        // `instances`, while getTotalJobs(JobType) below does include instances.
        // That meant this array and a per-type lookup could silently disagree.
        // Delegating to getTotalJobs(JobType) fixes the inconsistency and
        // removes the duplicate loop.
        JobType[] jobTypes = JobType.values();
        int[] total = new int[jobTypes.length];
        for (int i = 0; i < jobTypes.length; i++) {
            total[i] = getTotalJobs(jobTypes[i]);
        }
        return total;
    }

    /**
     * The stacks that currently have at least one building in progress, for the
     * construction panel in the UI. Returns a copy of the list, so callers can't
     * mutate the manager's stacks; the BuildingsStacks objects themselves are
     * live and read-only via their getters.
     */
    public List<BuildingsStacks> getStacksUnderConstruction() {
        List<BuildingsStacks> result = new ArrayList<>();
        for (BuildingsStacks stack : stacks) {
            if (stack.getUnderConstruction() > 0) {
                result.add(stack);
            }
        }
        return result;
    }

    /**
     * Stacks of one category with work still on site.
     *
     * Used to stop a sector queueing a second building while the first is still
     * going up - without it a business would re-read the same unmet demand every
     * month and order against it again, because the capacity it already paid for
     * does not show up until it opens.
     */
    /**
     * Construction points still owed on everything on site.
     *
     * Divided by the city's monthly output this is the queue length in months,
     * which is what tells the construction sector whether it is the bottleneck.
     */
    public double getRemainingConstructionPoints() {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            int building = stack.getUnderConstruction();
            if (building <= 0) continue;

            double perUnit = stack.getBuilding().getConstructionPoints();
            // progress is against the current unit only; the rest are untouched.
            total += perUnit * building - stack.getConstructionProgress();
        }
        return Math.max(total, 0);
    }

    public int getUnderConstructionByCategory(BuildingType category) {
        int count = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getCategory() == category
                    && stack.getUnderConstruction() > 0) {
                count++;
            }
        }
        return count;
    }

    public int getUnderConstruction() {
        int sum = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getIfUnderConstruction()) {
                sum += 1;
            }
        }

        return sum;
    }
    public String getName(BuildingsTemplate selected){
        return selected.getName();
    }

    public int getTotalJobs(JobType type) {
        int total = 0;
        for (BuildingsStacks stack : stacks) {
            total += stack.getTotalJobs(type);
        }
        for (BuildingInstance inst : instances) {
            total += inst.getJobs(type);
        }
        return total;
    }

    public int getTotalHouseCapacity() {
        // was a hand-rolled loop over RESIDENTIAL stacks summing quantity*capacity;
        // that's exactly what getTotalByCategoryInteger already does.
        return 100 + getTotalByCategoryInteger(BuildingType.RESIDENTIAL, BuildingsTemplate::getCapacity);
    }

    /*Commercial Methods
    
    
    ---------------------------------------------------------------------------
    
    
     */
    public int getTotalStoreCoverage() {
        return getTotalByCategoryInteger(BuildingType.COMMERCIAL, BuildingsTemplate::getCoverage);
    }

    public int getTotalStoreCapacity() {
        return getTotalByCategoryInteger(BuildingType.COMMERCIAL, BuildingsTemplate::getCapacity);
    }

    public int getTotalConstructionCapacity() {
        // NOTE: getProduction1() is a double; the original loop truncated it via
        // implicit int += double narrowing. Casting explicitly here to keep that
        // same truncating behavior rather than silently changing it to round.
        return 100 + getTotalByCategoryInteger(BuildingType.CONSTRUCTION, t -> (int) t.getProduction1());
    }

    /**
     * calculates Construction Materials production from all buildings
     *
     * @return Construction Materials production
     */
    public int getConstructionMaterialsProduction() {
        return 80 + getTotalByCategoryInteger(BuildingType.CONSTRUCTION, t -> (int) t.getProduction2());
    }

    /*
    ---------------------------------------------------------------------------
     */

 /*Industrial methods
    
    
    ---------------------------------------------------------------------------
    
    
     */
    public int getFoodProduction() {
        // NOTE: "food" here really just means production1 across all INDUSTRIAL
        // buildings, not a food-specific category — same truncating cast as above.
        return getTotalByCategoryInteger(BuildingType.INDUSTRIAL, t -> (int) t.getProduction1());
    }

    public int getFoodCapacity() {
        return getTotalByCategoryInteger(BuildingType.INDUSTRIAL, BuildingsTemplate::getCapacity);
    }

    /*
    ---------------------------------------------------------------------------
     */

 /*Universal methods
    
    
    ---------------------------------------------------------------------------
    
    
     */
    public double getTotalByCategoryDouble(BuildingType category, ToDoubleFunction<BuildingsTemplate> getter) {
        double total = 0;

        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate building = stack.getBuilding();

            if (building.getCategory() == category) {
                total += stack.getQuantity() * getter.applyAsDouble(building);
            }
        }

        return total;
    }
    
    public int getTotalByCategoryInteger(BuildingType category, ToIntFunction<BuildingsTemplate> getter) {
        int total = 0;

        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate building = stack.getBuilding();

            if (building.getCategory() == category) {
                total += stack.getQuantity() * getter.applyAsInt(building);
            }
        }

        return total;
    }

    public double getTotalDouble(ToDoubleFunction<BuildingsTemplate> getter) {
        double total = 0;

        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate building = stack.getBuilding();

            total += stack.getQuantity() * getter.applyAsDouble(building);

        }

        return total;
    }

    /**
     * Gross book value of everything standing in a category: cash paid plus the
     * materials it consumed, valued at market.
     *
     * cashCost alone is NOT the cost of a building - it is only the part paid in
     * cash, with materials bought separately and construction labour billed to
     * the construction sector. Using cashCost as book value would have put the
     * Water Treatment Plant on the books at $45M when it cost about $95M.
     *
     * Construction labour is still excluded, so this understates true cost. There
     * is no depreciation either, so it is gross rather than net book value.
     */
    public double getBookValueByCategory(BuildingType category) {
        return getTotalByCategoryDouble(
                category,
                t -> t.getCashCost() + t.getConstructionMaterials() * materialsCost);
    }

    public int[] getJobArrayPerCategory(BuildingType category) {
        int[] jobs = new int[JobType.values().length];

        for (int i = 0; i < stacks.size(); i++) {

            if (stacks.get(i).getBuilding().getCategory() == category) {

                for (int j = 0; j < jobs.length; j++) {
                    jobs[j] += stacks.get(i).getTotalJobs(jobNoUse[j]);
                }

            }
        }

        return jobs;
    }

    /*
    ---------------------------------------------------------------------------
     */
    public int getConstructionMaterials() {
        return constructionMaterials;
    }

    public int getStackIndex(BuildingsTemplate template) {
        return template.getId();

    }

    public BuildingsTemplate getTemplate(int i) {
        for (BuildingsTemplate template : templates) {
            if (template.getId() == i) {
                return template;
            }
        }
        return null;
    }

    public int getQuantity(int i) {
        int quantity = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getId() == i) {
                return quantity = stack.getQuantity();
            }
        }
        return 0;
    }

    public int getTemplateCount() {
        return templates.size();
    }

    public double[] getConstructionProgress() {
        double[] progress = new double[stacks.size()];

        for (int i = 0; i < stacks.size(); i++) {
            progress[i] = stacks.get(i).getConstructionProgress();
        }

        return progress;
    }

    public int[] getUnderConstructionArray() {
        int[] progress = new int[stacks.size()];

        for (int i = 0; i < stacks.size(); i++) {
            progress[i] = stacks.get(i).getUnderConstruction();
        }

        return progress;
    }

    public void setConstructionProgress(double[] progress) {
        if (progress.length != stacks.size()) {
            throw new IllegalArgumentException("Array length must match number of stacks.");
        }

        for (int i = 0; i < stacks.size(); i++) {
            stacks.get(i).setConstructionProgress(progress[i]);
        }
    }

    public void setUnderConstructionArray(int[] progress) {
        if (progress.length != stacks.size()) {
            throw new IllegalArgumentException("Array length must match number of stacks.");
        }

        for (int i = 0; i < stacks.size(); i++) {
            stacks.get(i).setUnderConstruction(progress[i]);
        }
    }

    public void setConstructionMaterials(int constructionMaterials) {
        this.constructionMaterials = constructionMaterials;
    }

    public void clearStacks() {
        stacks.clear();
    }

    public void handleConstructionMaterials(int required) {

        if (constructionMaterials >= required) {
            constructionMaterials -= required;
            return;
        }

        int shortage = required - constructionMaterials;
        constructionMaterials = 0;

        cost = shortage * materialsCost;

        System.out.println(
                "Construction Materials Imported: "
                + formatter.format(shortage) + " Cost: $" + formatter.format(cost)
        );
    }

    public BuildingsStacks getStack(BuildingsTemplate template) {
        for (BuildingsStacks stack : stacks) {
            if (template.equals(stack.getBuilding())) {
                return stack;
            }
        }

        return null;
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    public void resetBuildingManager() {
        clearStacks();
        constructionMaterials = 80;
        totalConstructionMaterialCost = 0;
        cost = 0;
    }

}
