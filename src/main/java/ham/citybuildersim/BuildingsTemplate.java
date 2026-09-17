package ham.citybuildersim;


/**
 * One kind of building, and what it costs to put up.
 *
 * MONEY HERE IS IN THOUSANDS OF DOLLARS, and this is the file where that
 * matters most, because it is the file somebody opens to balance the game.
 * UserInterface.toDollars() multiplies by a thousand on the way to the screen,
 * so:
 *
 *     cashCost 30           is  $30,000
 *     cashCost 125,000      is  $125,000,000   (a Coal Power Plant)
 *     upkeep 190            is  $190,000 a month
 *
 * constructionPoints, capacity, dwellings and landSqFt are NOT money and do not
 * convert - ten points is ten points, 8,000 sq ft is 8,000 sq ft.
 *
 * The scale exists to delay floating-point error: four thousand months of
 * arithmetic in thousands stays in a range where a double has digits to spare.
 *
 * claude/reading-the-numbers.md carries every building's price in real dollars
 * next to what the real thing costs, which is the table to balance against.
 *
 * @author Jerus
 */
public class BuildingsTemplate {
    String name;

    /** What the buyer pays in cash, in THOUSANDS. 30 is $30,000. See above. */
    double cashCost;
    int constructionPoints;
    int capacity;

    /**
     * How many separate HOMES this building contains, each holding one household.
     *
     * Distinct from capacity, which is how many PEOPLE fit. A House is one home
     * for up to four; a Studio block is eighty one-person homes; a Low-Rise is a
     * hundred homes averaging two and a half. Zero for anything that is not
     * residential, and zero for a residential building in an older data file -
     * see BuildingManager.getTotalHomes(), which falls back rather than
     * pretending a building with no dwellings declared houses nobody.
     */
    int dwellings;
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

    /**
     * Trips this building puts on the road network every month.
     *
     * The same shape as electricityConsumption and waterConsumption: a demand
     * every building makes on a shared municipal capacity, whatever its
     * category. Unlike those two it is not billed to anyone - nobody pays a
     * road bill - so the only way it shows up is as congestion.
     */
    double roadLoad;
    int coverage;
    double production1;
    double production2;
    double productionModifier1;
    double productionModifier2;
    boolean nationalized = false;

    /**
     * What a healthcare building is for; NONE for everything else.
     *
     * Deliberately not derived from the name. "Nursing Home" and "Home Daycare"
     * both contain "Home", and a rule that reads the label is a rule that breaks
     * the first time somebody renames a building or translates the game.
     */
    private CareType care = CareType.NONE;

    /**
     * What a school teaches; NONE for everything else.
     *
     * The same field as `care` and for the same reason. "Medical School" and
     * "Middle School" both end in the same word and do entirely different
     * things - one licenses a profession, the other moves a twelve-year-old one
     * step along - and a rule that reads the label breaks the first time
     * somebody renames a building or translates the game.
     */
    private EducationType teaches = EducationType.NONE;

    /**
     * What a safety building is for - officers or cells - and NONE for
     * everything else. The same field as `care` and `teaches`, for the same
     * reason. See SafetyType.
     */
    private SafetyType safety = SafetyType.NONE;

    /* =======================================================================
       A BUILDING THE CITY CANNOT STAFF SHOULD NOT BE BUILDABLE (2026-09-12)

       The licence a building's practice is built on, or null for the forty-six
       that need none. An Engineering Services Office is seventy-eight licensed
       engineers; a city with no Institute of Technology and no engineers among
       its arrivals cannot open one, and should be told so rather than sinking
       ten million into a building that stands empty.

       Why a hard gate and not just the price: the licence premium already
       punishes posts over holders - up to four times the band - so an
       understaffed office IS unprofitable. But the investment planner costs a
       new building at nameplate capacity and ignores the fill rate (a standing
       open item), so it would order one anyway and the office would sit there
       paying maintenance and property tax on nothing.

       This is the second instance of the rule. The first is the mine's deposit,
       which is hard-coded on BuildingType.MINING in Game rather than declared
       here; when somebody next touches it, it belongs in this field's shape.
       ======================================================================= */
    private JobType requiresLicence = null;

    private int id;

    /* =====================================================================
       WHO OWNS IT, AND WHAT IT MAKES (2026-09-11, the sector template).

       A building belongs to one sector by the sector's saved name - "Industry",
       "Mining" - or to nobody, which is the city (roads, schools, the two
       plants) or the bank (by name). The category above stays as the
       build-menu group and the band the property tax is assessed on; it
       stopped being the owner the day there could be a thousand owners.

       What it makes and what it uses are in UNITS of a good a month, per
       building, at nameplate: a Steel Foundry makes 1,200 tonnes of STEEL and
       uses 1,320 tonnes of IRON. The price of a tonne is a fact about steel,
       not about the foundry, and lives on the Good - which is why
       production1/2 and their modifiers are gone from every sector building
       and kept only for the utilities, whose kilowatts are not a good anybody
       trades.

       `stock` is the room it has for what it holds - the plant's warehouse,
       the shop's shelf - in units.
       ===================================================================== */
    private String sector = "";
    private final java.util.Map<Good, Double> makes = new java.util.EnumMap<>(Good.class);
    private final java.util.Map<Good, Double> uses = new java.util.EnumMap<>(Good.class);
    private double stock;

    public BuildingsTemplate setSector(String sector) {
        this.sector = sector == null ? "" : sector;
        return this;
    }

    public BuildingsTemplate makes(Good good, double unitsAMonth) {
        if (good != null && unitsAMonth > 0) makes.put(good, unitsAMonth);
        return this;
    }

    public BuildingsTemplate uses(Good good, double unitsAMonth) {
        if (good != null && unitsAMonth > 0) uses.put(good, unitsAMonth);
        return this;
    }

    public BuildingsTemplate setStock(double units) {
        this.stock = Math.max(0, units);
        return this;
    }

    /** The owning sector's key, or "" for a building nobody in the private sector owns. */
    public String getSector() { return sector; }

    public boolean isOwnedBySector() { return !sector.isEmpty(); }

    /**
     * Whether a building's power and water are invoiced to anybody: the
     * business buildings a sector owns. A home is not - the tenants are not
     * charged for utilities in this model and the landlords never were - and
     * neither is anything the city or the bank owns. See
     * EconomyManager.setElectricityConsumption().
     */
    public static boolean isBilledForUtilities(BuildingsTemplate t) {
        return t != null && t.isOwnedBySector() && t.getCategory() != BuildingType.RESIDENTIAL;
    }

    /** Units of a good this building makes a month at nameplate. Zero for a good it does not make. */
    public double makes(Good good) { return makes.getOrDefault(good, 0.0); }

    /** Units of a good this building uses a month at nameplate. */
    public double uses(Good good) { return uses.getOrDefault(good, 0.0); }

    public java.util.Map<Good, Double> goodsMade() { return java.util.Collections.unmodifiableMap(makes); }
    public java.util.Map<Good, Double> goodsUsed() { return java.util.Collections.unmodifiableMap(uses); }

    /** Room for what it holds, in units. */
    public double getStock() { return stock; }

    /** The same, asked per good: a building has one warehouse and it holds whatever the building holds. */
    public double stocks(Good good) { return stock; }



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

    public BuildingsTemplate setRoadLoad(double roadLoad) {
        this.roadLoad = roadLoad;
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

    public BuildingsTemplate setCare(CareType care) {
        this.care = care == null ? CareType.NONE : care;
        return this;
    }

    public BuildingsTemplate setSafety(SafetyType safety) {
        this.safety = safety == null ? SafetyType.NONE : safety;
        return this;
    }

    public BuildingsTemplate setTeaches(EducationType teaches) {
        this.teaches = teaches == null ? EducationType.NONE : teaches;
        return this;
    }
    
    //getters
    public double getCashCost() {
        return cashCost;
    }

    public int getConstructionPoints() {
        return constructionPoints;
    }

    public int getDwellings() {
        return dwellings;
    }

    public BuildingsTemplate setDwellings(int dwellings) {
        this.dwellings = dwellings;
        return this;
    }

    /**
     * How big a household one of these units takes, in people.
     *
     * THE SIZE OF THE FLAT, not of the building. Capacity is how many the block
     * holds; this is how many fit behind one front door, and until 2026-09-07
     * nothing in the game knew the difference - homes were a pooled count, so a
     * family of six could "live" in a studio because the city had eighty spare
     * doors somewhere.
     *
     * Rounded to the nearest person. A Low-Rise averages two and a half, which
     * is a real mix of flats rather than a half-person, and three is the size
     * that mix will take: the block is a little crowded when every unit is
     * full, which is true of low-rise blocks. Falling back to four for anything
     * that declares no dwellings is the House's own ratio and the same guess
     * BuildingManager.getTotalHomes() already makes.
     */
    public int homeSize() {
        if (getCategory() != BuildingType.RESIDENTIAL || capacity <= 0) return 0;
        if (dwellings <= 0) return 4;
        return Math.max(1, (int) Math.round(capacity / (double) dwellings));
    }

    /**
     * True for a flat too small to put a child in.
     *
     * Jerus: "only single adults can occupy them, or well two people, so any
     * other structure is not allowed." Derived from the size rather than
     * flagged per building, so the rule holds for any one- or two-person unit
     * anybody adds later - a constant nobody has to remember to set.
     */
    public boolean adultsOnly() {
        int size = homeSize();
        return size > 0 && size <= 2;
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

    public double getRoadLoad() {
        return roadLoad;
    }

    /* =======================================================================
       WHAT A MODE IS GOOD AT (2026-09-16)

       Two numbers that turn three identical roads into three different modes.
       Everything standing today reads zero for both and behaves exactly as it
       always has; see InfrastructureManager for why every mode is written as a
       RELIEF on an unchanged baseline rather than as a penalty on it.
       ======================================================================= */

    private double freightGrade;
    private double transitCapacity;

    /**
     * How much of this road is built for lorries, 0 to 1.
     *
     * A gravel road is 0 and a highway is 1, and the thing it buys is not
     * capacity - that is the `capacity` field and always was - but the fact
     * that a tonne of ore on a grade-separated highway costs the network less
     * than the same tonne through streets that also have school runs on them.
     * Ore is the traffic most improved by not sharing, which is why this
     * relieves BULK hardest and GOODS a little.
     */
    public BuildingsTemplate setFreightGrade(double grade) {
        this.freightGrade = Math.max(0, Math.min(1, grade));
        return this;
    }

    public double getFreightGrade() { return freightGrade; }

    /**
     * Commuter journeys a month this carries OFF the road, if it is transit.
     *
     * Zero for everything that is not, which is everything that existed before
     * today. It is deliberately not the same field as `capacity`: road
     * capacity is a pool anything may use and this is a pool only people may
     * use, and adding them would let a metro line carry ore.
     */
    public BuildingsTemplate setTransitCapacity(double riders) {
        this.transitCapacity = Math.max(0, riders);
        return this;
    }

    public double getTransitCapacity() { return transitCapacity; }

    /** True for a building whose whole purpose is carrying people. */
    public boolean isTransit() { return transitCapacity > 0; }

    private double railCapacity;

    /**
     * Tonnes a month this can haul across the city boundary, if it is rail.
     *
     * Separate from transitCapacity for the reason that one is separate from
     * `capacity`: they are three different pools and a thing that can carry
     * one of them cannot carry the others. A metro does not move ore and a
     * freight line does not move commuters.
     */
    public BuildingsTemplate setRailCapacity(double tonnes) {
        this.railCapacity = Math.max(0, tonnes);
        return this;
    }

    public double getRailCapacity() { return railCapacity; }

    /* =======================================================================
       WHAT KIND OF TRAFFIC THIS BUILDING MAKES (2026-09-16)

       roadLoad is one number and it always meant two things. These three split
       it - and they SPLIT it, they do not replace it. loadOf(COMMUTERS) +
       loadOf(GOODS) + loadOf(BULK) is getRoadLoad() exactly, for every
       building, so the network sums to what it always summed to and this
       whole step is a decomposition rather than a rebalance. Which mode
       carries which stream is step three; this is the plumbing under it.

       WHERE THE SHARES COME FROM, AND IT IS NOT A GUESS. Least squares against
       the catalogue itself, on the three things a building obviously has:

           roadLoad  =  0.726 x jobs  +  0.878 x homes  +  0.0386 x tonnes/mo

       R-squared 0.74 over the fifty-six buildings that load a road at all. Two
       things are worth reading out of that fit.

       ONE: the implied rate is 0.0386/0.726, or ONE WORKER PER NINETEEN TONNES
       A MONTH. A truck carries about twenty. Nobody set out to encode that -
       it fell out of numbers that were tuned one building at a time over
       months - and it is the strongest evidence available that the existing
       table was already a freight model that nobody had written down. The
       constant below is twenty rather than nineteen because a truckload is a
       reason and a regression coefficient is not; the shares move by under a
       percentage point either way.

       TWO: the residuals are all explainable and all in the same direction.
       An Iron Mine is +280 over the fit, because heavy vehicles on an unpaved
       approach are worse than their tonnage; a Penitentiary is -288 and a
       Long-Term Care Complex -136, because their occupants do not commute
       however many staff they have; a University is +258 and a Medical School
       +177, because students travel and this fit only counts wages. Every one
       of those is a real fact the fit cannot see.

       AND NONE OF IT MATTERS HERE, which is the point of splitting rather than
       replacing. The fit supplies the SHARE; the stored roadLoad supplies the
       MAGNITUDE. A mine keeps its 420 and simply learns that 69% of it is ore.
       ======================================================================= */

    /** One worker's monthly travel, as tonnes of freight. A truckload. See above. */
    public static final double TONNES_PER_WORKER = 20;

    /** A home's monthly travel, as workers. Commuting, plus the shopping and the school run. */
    public static final double WORKERS_PER_HOME = 1.2;

    /**
     * This building's road load, split by what is actually moving.
     *
     * The three sum to getRoadLoad(). A building with jobs and no throughput
     * is all commuters, which is every school, hospital, prison and office in
     * the game; a building with neither - and there are none today - would be
     * called commuters rather than divided by zero.
     */
    public double loadOf(Traffic stream) {
        if (stream == null || roadLoad <= 0) return 0;

        double commuters = WORKERS_PER_HOME * makes(Good.HOUSING);
        for (JobType job : JobType.values()) commuters += getJobs(job);

        /*
         * A RAIL TERMINAL'S THROUGHPUT IS BULK ON THE ROAD, at one end of it.
         * The wagon does the long haul and a lorry does the last mile between
         * the siding and the works, so a terminal that would otherwise read as
         * a pure office - it makes nothing and uses nothing - reads as what it
         * is: a few hundred people and a very great many trucks. Zero for
         * every building that is not rail, so nothing else moved by a bit.
         */
        double goods = 0, bulk = railCapacity / TONNES_PER_WORKER;
        for (java.util.Map<Good, Double> side : java.util.List.of(makes, uses)) {
            for (java.util.Map.Entry<Good, Double> e : side.entrySet()) {
                Traffic t = e.getKey().traffic();
                if (t == null) continue;
                double tonnes = e.getValue() * e.getKey().tonnesPerUnit() / TONNES_PER_WORKER;
                if (t == Traffic.BULK) bulk += tonnes; else goods += tonnes;
            }
        }

        double total = commuters + goods + bulk;
        if (total <= 0) return stream == Traffic.COMMUTERS ? roadLoad : 0;

        /*
         * THE LAST STREAM TAKES THE REMAINDER rather than its own share, so
         * the three add to roadLoad to the bit rather than to within three
         * roundings of it. The network's total is what has to be preserved and
         * this is the cheapest way to make that arithmetic rather than a
         * tolerance.
         */
        double asCommuters = roadLoad * commuters / total;
        double asGoods = roadLoad * goods / total;
        switch (stream) {
            case COMMUTERS: return asCommuters;
            case GOODS:     return asGoods;
            default:        return roadLoad - asCommuters - asGoods;
        }
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

    public CareType getCare() {
        return care;
    }

    /** The licence this building's practice needs, or null. See the field's note. */
    public BuildingsTemplate setRequiresLicence(JobType licence) {
        this.requiresLicence = licence;
        return this;
    }

    /** The licence this building's practice needs, or null if it needs none. */
    public JobType getRequiresLicence() { return requiresLicence; }

    /** How many posts here need that licence - what the gate is measured against. */
    public int getLicensedPosts() {
        return requiresLicence == null ? 0 : getJobs(requiresLicence);
    }

    public SafetyType getSafety() {
        return safety;
    }

    public EducationType getTeaches() {
        return teaches;
    }
 
   
        
    
    //sum of all jobs
    public int getTotalJobs() {
        int sum = 0;
        for (int j: jobsByEducation) sum += j;
        return sum;
    }

    /**
     * What a building costs and what it costs to run, in the new unit.
     *
     * THIS IS THE ONE THAT WOULD HAVE BITTEN HARDEST. Building costs live in
     * buildings.json in founding dollars, so a city that lopped two zeros and
     * left them alone would find a House costing a hundred times its real price
     * the next morning - the same trap as every other money constant, except
     * this one is in a data file where nobody would think to look for it.
     *
     * Everything else on a template is physical: capacity, dwellings,
     * construction points, materials, square feet, road load, production. None
     * of it moves.
     */
    public void redenominate(double scale) {
        rememberFounding();
        cashCost *= scale;
        upkeep   *= scale;
    }


    /**
     * What this building cost when the catalogue was read, before any currency
     * reform - so a reformed city that reloads can divide it again.
     *
     * buildings.json is in FOUNDING dollars and is re-read from scratch every
     * time a game starts, so the save carries the UNIT and this carries the
     * price the unit applies to. Keeping the founding figure rather than
     * scaling in place is what makes seedConstants() idempotent: applying the
     * same unit twice is a no-op, which is what a re-seed has to be.
     */
    private double foundingCashCost = Double.NaN;
    private double foundingUpkeep   = Double.NaN;

    /** Re-seeds this template's price at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        if (Double.isNaN(foundingCashCost)) {
            foundingCashCost = cashCost;
            foundingUpkeep   = upkeep;
        }
        cashCost = foundingCashCost / unit;
        upkeep   = foundingUpkeep / unit;
    }

    /**
     * ...and a reform moves the founding figure with it, so that a LATER
     * re-seed at the new unit lands in the same place. Without this, a city
     * that reformed twice and then reloaded would divide by the second unit
     * from a base that had already been divided by the first.
     */
    private void rememberFounding() {
        if (Double.isNaN(foundingCashCost)) {
            foundingCashCost = cashCost;
            foundingUpkeep   = upkeep;
        }
    }

}
