package ham.citybuildersim;


/**
 *
 * @author Jerus
 */
public class BuildingsTemplate {
    String name;
    double cashCost;
    int constructionPoints;
    int capacity;
    double upkeep;
    int constructionMaterials;
    int electricityConsumption;
    // double, not int: a single House draws a fraction of a unit. Rounding that
    // to an int would either zero out residential water or overstate it 3x.
    double waterConsumption;

    /**
     * Lot footprint in square feet. The city has to own this much unallocated
     * land before the building can go up, whoever is paying for it.
     */
    double landSqFt;
    int coverage;
    double production1;
    double production2;
    double productionModifier1;
    double productionModifier2;
    boolean nationalized = false;
    private int id;
 
 
    
    //enums
    int[] jobsByEducation = new int[JobType.values().length];
    private BuildingType category;
    
    //barebones constructor
    public BuildingsTemplate(String name, BuildingType category) {
        this.name = name;
        this.category = category;
    }
   /* 
    //partial constructor
    public BuildingsTemplate(String name, double cashCost, int constructionPoints,
            String category, double upkeep, int capacity, int constructionMaterials,
            int electricityConsumption) {
        this.name = name;
        this.cashCost = cashCost;
        this.constructionPoints = constructionPoints;
        this.category = category;
        this.upkeep = upkeep;
        this.capacity = capacity;
        this.constructionMaterials = constructionMaterials;
        this.electricityConsumption = electricityConsumption;
      
        //defualt 0 jobs
        for(int i = 0; i < jobsByEducation.length; i++) jobsByEducation[i] = 0;
    }
    
    //full constructor
    public BuildingsTemplate(String name, double cashCost, int constructionPoints,
            String category, double upkeep, int capacity, int constructionMaterials,
            int electricityConsumption, int coverage, double production1, 
            double production2, double productionModifier1, double productionModifier2) {
        this.name = name;
        this.cashCost = cashCost;
        this.constructionPoints = constructionPoints;
        this.category = category;
        this.upkeep = upkeep;
        this.capacity = capacity;
        this.constructionMaterials = constructionMaterials;
        this.electricityConsumption = electricityConsumption;
        this.coverage = coverage;
        this.production1 = production1;
        this.production2 = production2;
        this.productionModifier1 = productionModifier1;
        this.productionModifier2 = productionModifier2;
      
        //defualt 0 jobs
        for(int i = 0; i < jobsByEducation.length; i++) jobsByEducation[i] = 0;
        
    }
    */
    // setters (method chaining)

    public BuildingsTemplate setJobs(JobType type, int number) {
        jobsByEducation[type.ordinal()] = number;
        return this;
    }

    public BuildingsTemplate setCashCost(double cashCost) {
        this.cashCost = cashCost;
        return this;
    }

    public BuildingsTemplate setConstructionPoints(int constructionPoints) {
        this.constructionPoints = constructionPoints;
        return this;
    }

    public BuildingsTemplate setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public BuildingsTemplate setUpkeep(double upkeep) {
        this.upkeep = upkeep;
        return this;
    }

    public BuildingsTemplate setConstructionMaterials(int constructionMaterials) {
        this.constructionMaterials = constructionMaterials;
        return this;
    }

    public BuildingsTemplate setElectricityConsumption(int electricityConsumption) {
        this.electricityConsumption = electricityConsumption;
        return this;
    }

    public BuildingsTemplate setWaterConsumption(double waterConsumption) {
        this.waterConsumption = waterConsumption;
        return this;
    }

    public BuildingsTemplate setLandSqFt(double landSqFt) {
        this.landSqFt = landSqFt;
        return this;
    }

    public BuildingsTemplate setCoverage(int coverage) {
        this.coverage = coverage;
        return this;
    }

    public BuildingsTemplate setProduction1(double production1) {
        this.production1 = production1;
        return this;
    }

    public BuildingsTemplate setProduction2(double production2) {
        this.production2 = production2;
        return this;
    }

    public BuildingsTemplate setProductionModifier1(double productionModifier1) {
        this.productionModifier1 = productionModifier1;
        return this;
    }

    public BuildingsTemplate setProductionModifier2(double productionModifier2) {
        this.productionModifier2 = productionModifier2;
        return this;
    }

    public BuildingsTemplate setNationalized(boolean nationalized) {
        this.nationalized = nationalized;
        return this;
    }

    public BuildingsTemplate setId(int id) {
        this.id = id;
        return this;
    }
    
    //getters
    public double getCashCost() {
        return cashCost;
    }

    public int getConstructionPoints() {
        return constructionPoints;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getUpkeep() {
        return upkeep;
    }

    public int getConstructionMaterials() {
        return constructionMaterials;
    }

    public int getElectricityConsumption() {
        return electricityConsumption;
    }

    public double getWaterConsumption() {
        return waterConsumption;
    }

    public double getLandSqFt() {
        return landSqFt;
    }

    public int getCoverage() {
        return coverage;
    }

    public double getProduction1() {
        return production1;
    }

    public double getProduction2() {
        return production2;
    }

    public double getProductionModifier1() {
        return productionModifier1;
    }

    public double getProductionModifier2() {
        return productionModifier2;
    }

    public boolean getNationalized() {
        return nationalized;
    }
    public int getJobs(JobType type) {
        return jobsByEducation[type.ordinal()];
    }
    
    public String getName() {
        return this.name;
    }
    
    public BuildingType getCategory() {
        return category;
    }
    
    public int getId(){
        return id;
    }
 
   
        
    
    //sum of all jobs
    public int getTotalJobs() {
        int sum = 0;
        for (int j: jobsByEducation) sum += j;
        return sum;
    }
}
