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
    private JobType[] jobTypes = JobType.values();
    private int constructionMaterials;
    /**
     * What a unit of construction material costs, in the city's money.
     *
     * Imported - see ConstructionHandler, which only charges for the shortfall
     * local plants cannot cover - so it is a world price and moves with the
     * exchange rate. A devaluation makes building dearer, which is one of the
     * first things a real currency crisis does to a city.
     *
     * $18,000 A UNIT since 2026-09-10, from $2,000. The unit is defined by the
     * House: it takes ten of them, and a real house's materials are about
     * $180,000, so a unit is a tenth of a house's worth of structure. At
     * $2,000 the whole of a House's materials were 4% of what it cost, a
     * Coal Power Plant's were 6%, and eight small buildings wanted more
     * material than the real building costs in total (a Home Daycare asked
     * for $80,000 of it for a $147,000 conversion). See
     * claude/the-rebalance-stage-two.md, which found it and left it for its
     * own pass.
     *
     * EVERY TEMPLATE'S SPLIT WAS RE-DERIVED WITH IT and every template's
     * TOTAL held to the dollar: units = round(total x share / 18), cash =
     * total - 18 x units, where share is the material share of a real
     * building's capital cost - 40% for a building (residential, commercial,
     * institutional, the water plant), 25% for a plant (process equipment is
     * the cost, structure the rest), 60% for a road, which is its materials.
     * A House is 10 units still; a Studio block is 320 where it was 680; the
     * Coal Power Plant is 19,848 where it was 40,000. The fields are money
     * against materials again, and both halves mean what they say.
     *
     * Only the fields are in thousands: this constant is $18,000 written as
     * the game writes money.
     */
    public static final double MATERIALS_WORLD_PRICE = 18;
    private double materialsCost = MATERIALS_WORLD_PRICE;

    public void setExchangeRate(double rate) {
        this.materialsCost = MATERIALS_WORLD_PRICE * (rate > 0 ? rate : 1);
    }

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

    /**
     * Loads every building the game knows about.
     *
     * Tries buildings.json first - the editable data file, so a balance pass is
     * an edit and a restart rather than an edit and a rebuild. Falls back to the
     * definitions written out below if the file is missing or unusable, which
     * means a typo in the data file costs you the data file and not the game.
     *
     * The built-ins are also the reference: BuildingDataCheck asserts the JSON
     * produces templates identical to them, so the two cannot silently drift.
     */
    public void initializeTemplates() {

        List<BuildingsTemplate> loaded = new BuildingCatalog().load();

        if (loaded != null && !loaded.isEmpty()) {
            templates.addAll(loaded);
            return;
        }

        System.out.println("Using built-in building definitions.");
        initializeBuiltInTemplates();
    }

    /**
     * The definitions as code. Kept as the fallback rather than deleted, so
     * there is always a working set of buildings even with no data file at all.
     */
    public void initializeBuiltInTemplates() {
        
        //Residential Buildings
        /*
         * HOUSING IS CHEAP TO BUILD, AND THAT IS NOT A BALANCE TWEAK.
         *
         * Rent stopped being charged per head and became one cheque per home,
         * which cut what a landlord collects by about two thirds. Measured
         * straight afterwards: the advisor stopped building apartments
         * ENTIRELY - 62 low-rise blocks over four thousand months became zero -
         * and put the money into steel instead.
         *
         * So the three residential templates carry a third of the construction
         * points and materials they used to. Same cash price, per Jerus: the
         * capital outlay is unchanged, what fell is the EFFORT. Which is the
         * right lever, because effort was the binding one - a low-rise block
         * needed 7,000 points against a mid-game city's 35 a month, or two
         * hundred months of every builder in the city on one building, and
         * `orderSize` refuses anything over twelve.
         *
         * A third, because that is what rent fell by. Not a number chosen to
         * make the playtest come out well.
         */
        /*
         * SIX, not four, since 2026-09-07 (Jerus: "houses should be able to
         * house 6... houses should also be higher rent cost"). A House is the
         * only home in the game big enough for a large family or a
         * five-adult flatshare - both exist as household shapes and neither
         * fitted anything - and because rent is charged per person of dwelling
         * capacity, a bigger house is a dearer one by construction. It is the
         * family building now, and it costs like one.
         */
        BuildingsTemplate house = new BuildingsTemplate("House", BuildingType.RESIDENTIAL);
        house.setSector("Real Estate");
        house.setCapacity(6);
        house.setDwellings(1);
        house.makes(Good.HOUSING, 1);
        house.setCashCost(266);
        house.setConstructionPoints(10);
        house.setConstructionMaterials(10);
        house.setElectricityConsumption(1);
        house.setWaterConsumption(.2);
        house.setLandSqFt(8000);
        house.setRoadLoad(2);
        house.setId(0);
        templates.add(house);

        BuildingsTemplate studioApartments = new BuildingsTemplate("Studio Apartments", BuildingType.RESIDENTIAL);
        /*
         * TWO TO A UNIT since 2026-09-07, and it is the reason the building
         * stopped being pointless. Eighty one-person flats cost $25,000 a
         * resident against a House's $7,500, so nothing could make them the
         * right answer at any land price - backlog I1, "strictly dominated". A
         * studio that takes a couple is both what a studio actually is and
         * $12,500 a resident on a sixth of a House's land.
         *
         * WHAT THESE THREE COST IN REAL MONEY, at founding prices, because the
         * fields below are in thousands and nobody can balance in thousands.
         *
         * BEFORE (and why it was wrong):
         *
         *     House       $53,640 for one 6-person home    =  $8,940 a head
         *     Studio    $3,371,375 for 80 2-person homes   = $21,071 a head
         *     Low-Rise $11,127,300 for 63 4-person homes   = $44,156 a head
         *
         * A FIVE-FOLD SPREAD IN COST PER HEAD, with the studio near the cheap
         * end. In reality the spread is almost flat and runs the OTHER WAY -
         * about $75,000 a head for a detached house, $90,000 for a studio unit,
         * $67,500 for a low-rise flat - because a studio is nearly all kitchen
         * and bathroom, which is where the money in a dwelling is, and a house
         * amortises one of each over six people.
         *
         * That inversion was the whole reason nothing ever built a Low-Rise: a
         * low-rise door cost 4.2x a studio door for a unit holding twice as
         * many people, where a real two-bedroom is 1.6-1.8x a studio. The
         * previous note here said the fix was to raise the studio and the house
         * rather than cut the low-rise - "cutting the one honest number to
         * match two wrong ones" - and left it as a rebalance wanting
         * measurement.
         *
         * AFTER (2026-09-09), measured with RealismCheck against North
         * American build costs:
         *
         *     House      $449,640 for one 6-person home    = $74,940 a head
         *     Studio   $14,401,375 for 80 2-person homes   = $90,009 a head
         *     Low-Rise $17,017,300 for 63 4-person homes   = $67,529 a head
         *
         * All three within 0.1% of the real figure, the spread flat, and the
         * studio correctly the DEAREST per head.
         *
         * ONLY cashCost moved that night. constructionPoints and
         * constructionMaterials are PHYSICAL QUANTITIES - the yard's labour
         * and the units of material it consumes - and multiplying those by
         * eight would have cut the city's building rate eightfold and
         * octupled the import bill, which is a pacing change and a trade
         * shock, not a price. So for a day the split inside a building was
         * money against quantity rather than labour against materials.
         *
         * THE MATERIALS HALF CAUGHT UP ON 2026-09-10. The unit of material is
         * $18,000 now (see MATERIALS_WORLD_PRICE) and every template's units
         * were re-derived at the real material share of its cost, with the
         * TOTAL held to the dollar - so the three figures above are still the
         * three figures, and a House is $266k of work and ten units of
         * material at $18k, which is $180k of structure, which is what a
         * house is made of. The points did not move; they are the build rate.
         *
         * See claude/reading-the-numbers.md.
         */
        studioApartments.setSector("Real Estate");
        studioApartments.setCapacity(160);
        studioApartments.makes(Good.HOUSING, 80);
        studioApartments.setDwellings(80);
        studioApartments.setCashCost(8628);
        studioApartments.setConstructionPoints(680);
        studioApartments.setConstructionMaterials(320);
        studioApartments.setElectricityConsumption(8);
        studioApartments.setWaterConsumption(2);
        studioApartments.setLandSqFt(25000);
        studioApartments.setRoadLoad(35);
        studioApartments.setId(1);
        templates.add(studioApartments);

        BuildingsTemplate lowRiseApartments = new BuildingsTemplate("Low-Rise Apartments", BuildingType.RESIDENTIAL);
        /*
         * FOUR TO A FLAT since 2026-09-07 (Jerus: "low rise should be four").
         * Sixty-three flats rather than a hundred, for the same 252 people -
         * the block did not get bigger, its units did, which is what makes it
         * the dense answer for a FAMILY. A studio cannot take one at all and a
         * House needs five and a half times the land per resident.
         *
         * Deliberately not a hundred four-person flats. That would be 400
         * people on 60,000 sq ft - 150 a head, undercutting the studio's 156 -
         * and a studio has to be the cheapest ground in the game or the whole
         * point of a tiny flat is gone.
         */
        lowRiseApartments.setSector("Real Estate");
        lowRiseApartments.setCapacity(252);
        lowRiseApartments.makes(Good.HOUSING, 63);
        lowRiseApartments.setDwellings(63);
        lowRiseApartments.setCashCost(10196);
        lowRiseApartments.setConstructionPoints(2400);
        lowRiseApartments.setConstructionMaterials(377);
        lowRiseApartments.setElectricityConsumption(25);
        lowRiseApartments.setWaterConsumption(6);
        lowRiseApartments.setLandSqFt(60000);
        lowRiseApartments.setRoadLoad(100);
        lowRiseApartments.setId(6);
        templates.add(lowRiseApartments);

        /* =====================================================================
           HEALTHCARE

           Four functions, fourteen buildings, small to very large. The city
           builds all of them and none of them pays for itself.

           CAPACITY IS THE AGE BAND IT SERVES, which is the whole reason these
           are interesting: childcare counts babies and children, senior care
           counts seniors, and general care counts everyone. An ageing city
           needs beds it did not need twenty years ago, so the pyramid finally
           decides what has to be built rather than just being looked at.

           WHERE THE TOP OF THE WAGE LADDER FINALLY WORKS. COLLEGE_HEALTH and
           UNIV_DOCTOR have sat in JobType unused since the beginning - six of
           the eleven job types were never employed by anything. A doctor costs
           $8,000 a month against an unskilled $800, so a hospital's payroll is
           unlike anything else in the game, and that is correct: healthcare is
           expensive because of who works there.

           THE TWO WAYS TO BURY SOMEBODY are a genuine trade, per Jerus:
           cemeteries turn a profit and consume land permanently, crematoria run
           at breakeven on almost no land but burn a great deal of electricity.
           A Municipal Cemetery is 2,000,000 sq ft - the largest footprint in
           the game - and costs less to build than a nursing home.

           AND NOW THEY ARE WIRED IN. General care sets the workforce's sick
           rate; childcare and senior care move the death rate in their bands
           (drastically for infants, gently for seniors); senior coverage makes
           the whole city more attractive to migrants; the cemeteries and the
           crematoria decide what happens to the dead, and a city that has
           neither makes the living ill. The whole service is paid for out of
           the treasury, minus what patients and funerals bring back - see
           Healthcare, and claude/healthcare-funded.md.

           The one thing UPKEEP is charged on. getUpkeep() had two callers in
           the entire codebase before this - a debug println and
           BuildingDataCheck - so every building's upkeep in this file was a
           wish. Healthcare pays its own.
           ===================================================================== */

        BuildingsTemplate homeDaycare = new BuildingsTemplate("Home Daycare", BuildingType.HEALTHCARE)
                .setCapacity(8)
                .setCashCost(93)
                .setConstructionPoints(40)
                .setConstructionMaterials(3)
                .setUpkeep(3)
                .setElectricityConsumption(2)
                .setWaterConsumption(1)
                .setLandSqFt(6000)
                .setRoadLoad(3)
                .setJobs(JobType.NO_DIPLOMA, 2)
                .setCare(CareType.CHILDCARE)
                .setId(15);
        templates.add(homeDaycare);

        BuildingsTemplate neighbourhoodDaycare = new BuildingsTemplate("Neighbourhood Daycare", BuildingType.HEALTHCARE)
                .setCapacity(60)
                .setCashCost(1004)
                .setConstructionPoints(600)
                .setConstructionMaterials(38)
                .setUpkeep(22)
                .setElectricityConsumption(12)
                .setWaterConsumption(4)
                .setLandSqFt(25000)
                .setRoadLoad(14)
                .setJobs(JobType.NO_DIPLOMA, 10)
                .setJobs(JobType.DIPLOMA, 5)
                .setJobs(JobType.COLLEGE_HEALTH, 1)
                .setCare(CareType.CHILDCARE)
                .setId(16);
        templates.add(neighbourhoodDaycare);

        BuildingsTemplate childcareCentre = new BuildingsTemplate("Childcare Centre", BuildingType.HEALTHCARE)
                .setCapacity(220)
                .setCashCost(3272)
                .setConstructionPoints(2200)
                .setConstructionMaterials(122)
                .setUpkeep(70)
                .setElectricityConsumption(40)
                .setWaterConsumption(12)
                .setLandSqFt(70000)
                .setRoadLoad(40)
                .setJobs(JobType.NO_DIPLOMA, 34)
                .setJobs(JobType.DIPLOMA, 16)
                .setJobs(JobType.COLLEGE_HEALTH, 3)
                .setCare(CareType.CHILDCARE)
                .setId(17);
        templates.add(childcareCentre);

        BuildingsTemplate walkInClinic = new BuildingsTemplate("Walk-in Clinic", BuildingType.HEALTHCARE)
                .setCapacity(2500)
                .setCashCost(1501)
                .setConstructionPoints(1200)
                .setConstructionMaterials(55)
                .setUpkeep(45)
                .setElectricityConsumption(30)
                .setWaterConsumption(5)
                .setLandSqFt(20000)
                .setRoadLoad(30)
                .setJobs(JobType.DIPLOMA, 4)
                .setJobs(JobType.COLLEGE_HEALTH, 5)
                .setJobs(JobType.UNIV_DOCTOR, 2)
                .setCare(CareType.GENERAL)
                .setId(18);
        templates.add(walkInClinic);

        BuildingsTemplate communityHealthCentre = new BuildingsTemplate("Community Health Centre", BuildingType.HEALTHCARE)
                .setCapacity(12000)
                .setCashCost(9569)
                .setConstructionPoints(8000)
                .setConstructionMaterials(355)
                .setUpkeep(190)
                .setElectricityConsumption(160)
                .setWaterConsumption(30)
                .setLandSqFt(90000)
                .setRoadLoad(90)
                .setJobs(JobType.NO_DIPLOMA, 12)
                .setJobs(JobType.DIPLOMA, 20)
                .setJobs(JobType.COLLEGE_HEALTH, 26)
                .setJobs(JobType.COLLEGE_BUSINESS, 3)
                .setJobs(JobType.UNIV_DOCTOR, 8)
                .setCare(CareType.GENERAL)
                .setId(19);
        templates.add(communityHealthCentre);

        BuildingsTemplate generalHospital = new BuildingsTemplate("General Hospital", BuildingType.HEALTHCARE)
                .setCapacity(40000)
                .setCashCost(149870)
                .setConstructionPoints(52000)
                .setConstructionMaterials(5550)
                .setUpkeep(700)
                .setElectricityConsumption(900)
                .setWaterConsumption(180)
                .setLandSqFt(500000)
                .setRoadLoad(300)
                .setJobs(JobType.NO_DIPLOMA, 60)
                .setJobs(JobType.DIPLOMA, 90)
                .setJobs(JobType.COLLEGE_HEALTH, 150)
                .setJobs(JobType.COLLEGE_BUSINESS, 12)
                .setJobs(JobType.UNIV_SCIENCE, 6)
                .setJobs(JobType.UNIV_DOCTOR, 45)
                .setCare(CareType.GENERAL)
                .setId(20);
        templates.add(generalHospital);

        BuildingsTemplate regionalMedicalCentre = new BuildingsTemplate("Regional Medical Centre", BuildingType.HEALTHCARE)
                .setCapacity(120000)
                .setCashCost(719620)
                .setConstructionPoints(140000)
                .setConstructionMaterials(26652)
                .setUpkeep(2100)
                .setElectricityConsumption(2600)
                .setWaterConsumption(520)
                .setLandSqFt(1400000)
                .setRoadLoad(700)
                .setJobs(JobType.NO_DIPLOMA, 150)
                .setJobs(JobType.DIPLOMA, 240)
                .setJobs(JobType.COLLEGE_HEALTH, 420)
                .setJobs(JobType.COLLEGE_BUSINESS, 30)
                .setJobs(JobType.UNIV_SCIENCE, 20)
                .setJobs(JobType.UNIV_HIGHTECH_ENG, 6)
                .setJobs(JobType.UNIV_DOCTOR, 130)
                .setCare(CareType.GENERAL)
                .setId(21);
        templates.add(regionalMedicalCentre);

        BuildingsTemplate homeCareService = new BuildingsTemplate("Home Care Service", BuildingType.HEALTHCARE)
                .setCapacity(400)
                .setCashCost(533)
                .setConstructionPoints(500)
                .setConstructionMaterials(20)
                .setUpkeep(30)
                .setElectricityConsumption(15)
                .setWaterConsumption(3)
                .setLandSqFt(15000)
                .setRoadLoad(45)
                .setJobs(JobType.NO_DIPLOMA, 22)
                .setJobs(JobType.DIPLOMA, 12)
                .setJobs(JobType.COLLEGE_HEALTH, 4)
                .setCare(CareType.SENIOR)
                .setId(22);
        templates.add(homeCareService);

        BuildingsTemplate assistedLivingResidence = new BuildingsTemplate("Assisted Living Residence", BuildingType.HEALTHCARE)
                .setCapacity(90)
                .setCashCost(13783)
                .setConstructionPoints(5000)
                .setConstructionMaterials(510)
                .setUpkeep(150)
                .setElectricityConsumption(110)
                .setWaterConsumption(40)
                .setLandSqFt(80000)
                .setRoadLoad(25)
                .setJobs(JobType.NO_DIPLOMA, 24)
                .setJobs(JobType.DIPLOMA, 10)
                .setJobs(JobType.COLLEGE_HEALTH, 4)
                .setCare(CareType.SENIOR)
                .setId(23);
        templates.add(assistedLivingResidence);

        BuildingsTemplate nursingHome = new BuildingsTemplate("Nursing Home", BuildingType.HEALTHCARE)
                .setCapacity(220)
                .setCashCost(36554)
                .setConstructionPoints(14000)
                .setConstructionMaterials(1354)
                .setUpkeep(380)
                .setElectricityConsumption(280)
                .setWaterConsumption(95)
                .setLandSqFt(160000)
                .setRoadLoad(55)
                .setJobs(JobType.NO_DIPLOMA, 70)
                .setJobs(JobType.DIPLOMA, 34)
                .setJobs(JobType.COLLEGE_HEALTH, 18)
                .setJobs(JobType.UNIV_DOCTOR, 2)
                .setCare(CareType.SENIOR)
                .setId(24);
        templates.add(nursingHome);

        BuildingsTemplate longTermCareComplex = new BuildingsTemplate("Long-Term Care Complex", BuildingType.HEALTHCARE)
                .setCapacity(650)
                .setCashCost(101893)
                .setConstructionPoints(38000)
                .setConstructionMaterials(3774)
                .setUpkeep(1000)
                .setElectricityConsumption(750)
                .setWaterConsumption(260)
                .setLandSqFt(380000)
                .setRoadLoad(130)
                .setJobs(JobType.NO_DIPLOMA, 200)
                .setJobs(JobType.DIPLOMA, 100)
                .setJobs(JobType.COLLEGE_HEALTH, 55)
                .setJobs(JobType.COLLEGE_BUSINESS, 5)
                .setJobs(JobType.UNIV_DOCTOR, 6)
                .setCare(CareType.SENIOR)
                .setId(25);
        templates.add(longTermCareComplex);

        BuildingsTemplate crematorium = new BuildingsTemplate("Crematorium", BuildingType.HEALTHCARE)
                .setCapacity(120)
                .setCashCost(2988)
                .setConstructionPoints(2500)
                .setConstructionMaterials(111)
                .setUpkeep(90)
                .setElectricityConsumption(220)
                .setWaterConsumption(8)
                .setLandSqFt(30000)
                .setRoadLoad(20)
                .setJobs(JobType.NO_DIPLOMA, 5)
                .setJobs(JobType.DIPLOMA, 3)
                .setCare(CareType.CREMATION)
                .setId(26);
        templates.add(crematorium);

        BuildingsTemplate memorialCemetery = new BuildingsTemplate("Memorial Cemetery", BuildingType.HEALTHCARE)
                .setCapacity(12000)
                .setCashCost(10084)
                .setConstructionPoints(900)
                .setConstructionMaterials(374)
                .setUpkeep(25)
                .setElectricityConsumption(6)
                .setWaterConsumption(20)
                .setLandSqFt(400000)
                .setRoadLoad(15)
                .setJobs(JobType.NO_DIPLOMA, 8)
                .setJobs(JobType.DIPLOMA, 2)
                .setCare(CareType.BURIAL)
                .setId(27);
        templates.add(memorialCemetery);

        BuildingsTemplate municipalCemetery = new BuildingsTemplate("Municipal Cemetery", BuildingType.HEALTHCARE)
                .setCapacity(60000)
                .setCashCost(49852)
                .setConstructionPoints(3200)
                .setConstructionMaterials(1846)
                .setUpkeep(80)
                .setElectricityConsumption(15)
                .setWaterConsumption(90)
                .setLandSqFt(2000000)
                .setRoadLoad(30)
                .setJobs(JobType.NO_DIPLOMA, 26)
                .setJobs(JobType.DIPLOMA, 6)
                .setJobs(JobType.COLLEGE_BUSINESS, 2)
                .setCare(CareType.BURIAL)
                .setId(28);
        templates.add(municipalCemetery);

        // Commercial Buildings
        BuildingsTemplate convienceStore = new BuildingsTemplate("Convenience Store", BuildingType.COMMERCIAL);
        convienceStore.setSector("Retail");
        convienceStore.setCoverage(480);
        /*
         * THE SHELF IS MEASURED IN KILOGRAMS NOW, AND THAT IS WHY THE STOCK
         * FIGURE MOVED BY FIFTY.
         *
         * It used to hold 1,400 UNITS of FOOD, where a unit was a person fed
         * for a month. The thirteen goods are kilograms, and one person-month
         * is exactly 50kg of them (consumption.json's reference quantities sum
         * to that), so the same shelf is 70,000kg. Left at 1,400 the shop
         * could stock a tenth of the drinks its customers wanted and delivered
         * 20%% of demand for ever - which is what InfrastructureCheck caught,
         * on a city with roads and a city without scoring exactly alike
         * because both were starved by the shelf rather than by the road.
         */
        convienceStore.setStock(70000);
        convienceStore.makes(Good.GROCERIES, 480);
        convienceStore.uses(Good.GRAINS, 1440);
        convienceStore.uses(Good.BREAD, 1680);
        convienceStore.uses(Good.DAIRY_EGGS, 3840);
        convienceStore.uses(Good.VEGETABLES, 3360);
        convienceStore.uses(Good.FRUIT, 2400);
        convienceStore.uses(Good.MEAT, 2160);
        convienceStore.uses(Good.FISH, 576);
        convienceStore.uses(Good.FATS, 384);
        convienceStore.uses(Good.PROCESSED_MEAT, 480);
        convienceStore.uses(Good.READY_MEALS, 720);
        convienceStore.uses(Good.BAKERY, 720);
        convienceStore.uses(Good.SNACKS, 480);
        convienceStore.uses(Good.DRINKS, 5760);
        convienceStore.setCashCost(442);
        convienceStore.setConstructionPoints(120);
        convienceStore.setConstructionMaterials(17);
        convienceStore.setElectricityConsumption(6);
        convienceStore.setWaterConsumption(1);
        convienceStore.setLandSqFt(5000);
        convienceStore.setRoadLoad(10);
        convienceStore.setJobs(JobType.NO_DIPLOMA, 2);
        convienceStore.setJobs(JobType.DIPLOMA, 3);
        convienceStore.setId(2);
        templates.add(convienceStore);

        BuildingsTemplate smallGroceryStore = new BuildingsTemplate("Small Grocery Store", BuildingType.COMMERCIAL);
        smallGroceryStore.setSector("Retail");
        smallGroceryStore.setCoverage(1600);
        smallGroceryStore.setStock(350000);
        smallGroceryStore.makes(Good.GROCERIES, 1600);
        smallGroceryStore.uses(Good.GRAINS, 4800);
        smallGroceryStore.uses(Good.BREAD, 5600);
        smallGroceryStore.uses(Good.DAIRY_EGGS, 12800);
        smallGroceryStore.uses(Good.VEGETABLES, 11200);
        smallGroceryStore.uses(Good.FRUIT, 8000);
        smallGroceryStore.uses(Good.MEAT, 7200);
        smallGroceryStore.uses(Good.FISH, 1920);
        smallGroceryStore.uses(Good.FATS, 1280);
        smallGroceryStore.uses(Good.PROCESSED_MEAT, 1600);
        smallGroceryStore.uses(Good.READY_MEALS, 2400);
        smallGroceryStore.uses(Good.BAKERY, 2400);
        smallGroceryStore.uses(Good.SNACKS, 1600);
        smallGroceryStore.uses(Good.DRINKS, 19200);
        smallGroceryStore.setCashCost(3478);
        smallGroceryStore.setConstructionPoints(800);
        smallGroceryStore.setConstructionMaterials(128);
        smallGroceryStore.setElectricityConsumption(35);
        smallGroceryStore.setWaterConsumption(6);
        smallGroceryStore.setLandSqFt(40000);
        smallGroceryStore.setRoadLoad(55);
        smallGroceryStore.setJobs(JobType.NO_DIPLOMA, 15);
        smallGroceryStore.setJobs(JobType.DIPLOMA, 10);
        smallGroceryStore.setJobs(JobType.COLLEGE_BUSINESS, 2);
        smallGroceryStore.setId(5);
        templates.add(smallGroceryStore);

        //Industrial Buildings
        /* =====================================================================
           WHAT A LIGHT-INDUSTRY WORKER MAKES IN A MONTH

           Jerus, 2026-09-10: "min wage i had to lower it like in game cause all
           the industries were bankrupt via wages while the entire populaition
           was becoming rich." He was right that they were bankrupt and right to
           lower it; the wages were not the fault.

           Measured on his month-1124 save, the sector's income statement:

             revenue      1,965,209        payroll   1,467,035   = 74.7%
             operating income  +328,446    interest   -932,223

           Operating income was POSITIVE. What killed INDUSTRY was debt service,
           not pay - see the note in DebtManager. But 74.7% of revenue going out
           as wages is why it had no room to carry any debt at all, and why
           doubling the wage back to its default turned +328,446 into -1,070,000
           and made the sector unbuildable. Three anchors, taken separately, all
           say the same thing about output per worker:

             PAYROLL SHARE. US food manufacturing pays about $85bn of wages on
             about $1,000bn of shipments - 8.5%, or 11% counting all
             compensation. Retail here runs 22.5%, heavy industry 11.9%, mining
             29.4%: every other sector in this game is already in its real band.
             Only this one is not, and closing 74.7% to ~15% needs 4.2x.

             EMPLOYMENT SHARE. Food manufacturing employs 0.5% of the US
             population, consumer manufacturing as a whole about 1.4%. His city
             ran 810 industry jobs against 34,649 people - 2.3%, or 4.3x the
             food figure.

             TONNES PER WORKER. A unit here feeds one person for about two
             months (16,620 units a month fed 34,649 people), so at ~70kg of
             food a person it is about 0.146 tonnes. A plant at 6,000 units was
             therefore doing 10,500 tonnes a year with 270 staff - 39 tonnes a
             head, against roughly 235 in US food manufacturing. 6x.

           4.2, 4.3 and 6. Taking the middle at FIVE, and applying it to output
           rather than to the wage bill, because the wage table is the one thing
           in this game that is already on real published figures (see PayTier)
           and the output was never anchored to anything at all.

           At 5x, one plant's payroll is 8% of its revenue at the import price
           and 14% at the price the local market has actually been clearing at -
           both inside the real band - and it stays under 30% at DOUBLE the
           current wage, which is what makes the minimum-wage dial usable again.

           The jobs are UNCHANGED. A plant of this size and staffing was not
           over-manned; it was producing about a fifth of what a plant that size
           produces. A city needs fewer of them now, which is the correct
           consequence and not a side effect to be tuned away.

           THE WAREHOUSE MOVES WITH IT, and has to. `capacity` on an industrial
           template is storage, and IndustrialHandler.trimInventory() WRITES OFF
           whatever will not fit - so a plant making five times as much into the
           same shed destroys most of it every month. Raising output alone left
           the fixture city importing 4,186k where it used to import 1,344k and
           exporting 566k where it used to export 5,545k: it had turned a food
           exporter into a food importer by giving it more food. ForeignCheck
           caught it on the trade balance, which is what that assertion is for.
           Both sheds keep the months of stock they held before - three for the
           plant, two and three quarters for the mill.
           ===================================================================== */
        /*
         * THE INDUSTRIAL BAKERY, WHICH USED TO BE A TEXTILE MILL THAT MADE FOOD.
         *
         * It was a copy-paste: a textile mill with a food line on it, and
         * nobody noticed for as long as "food" was one good. When the shelf
         * was itemised on 2026-09-15 the line had to name a real product, and
         * a mill that turns crops into cloth does not exist. Jerus's call: it
         * is a bakery, and the plant below is the small one.
         *
         * THE ID IS KEPT. Deleting building 3 would have orphaned every save
         * that contains one - saves are keyed by id and the catalog says so.
         * A rename costs nothing and a deletion costs somebody's city.
         *
         * NOTE FOR A BALANCE PASS, NOT TOUCHED HERE: building 7 below is 48%
         * of this one's cash cost, half its land and 83% of its jobs, and it
         * out-produces it. That was true before this change and is not part of
         * it.
         */
        BuildingsTemplate texttileMill = new BuildingsTemplate("Industrial Bakery", BuildingType.INDUSTRIAL);
        texttileMill.setSector("Industry");
        texttileMill.setStock(829000);
        texttileMill.setCashCost(18717);
        texttileMill.setConstructionPoints(1800);
        texttileMill.setConstructionMaterials(347);
        /*
         * BREAD AND THE THINGS NEXT TO IT ON THE COUNTER, SPLIT SEVEN TO THREE
         * - roughly the 3.5kg and 1.5kg a person eats of each in a month.
         *
         * THESE OVENS MADE NOTHING FOR ONE BATCH, and the reason was the
         * engine rather than the trade. getMarginalCostPerUnit() divided the
         * whole line's electricity, water and input bill by ONE good's output,
         * so a plant with two outputs was charged its entire cost twice over
         * and neither cleared its own marginal cost: 858,000kg of nameplate
         * capacity and zero produced, while the city imported bread beside it.
         * Every BUILDING in this game makes one good, which is what made this
         * look like a new path - but the cost methods are the SECTOR's, and
         * Business Services and Manufacturing have been quietly charging each
         * of their goods the whole line's bill since they shipped.
         * Sector.costShareOf() splits it now, for all three.
         *
         * 52.5% of the crop's mass. THE YIELD IS STRUCK FROM THE MARGIN, NOT
         * THE MILLING: a real wheat-to-bread yield of about 74% gave both
         * ovens a 60% operating margin and made this the most profitable
         * building in the game, which Agriculture's own header records as a
         * bug it held once already. This leaves the crop bill at 29% of
         * revenue - the band every other plant occupies, and where these two
         * sat before they stopped making FOOD.
         */
        texttileMill.makes(Good.BREAD, 213000);
        texttileMill.makes(Good.BAKERY, 91000);
        // 579 tonnes of crops. The mills bought nothing at all until 2026-09-13.
        texttileMill.uses(Good.CROPS, 579);
        texttileMill.setElectricityConsumption(40);
        texttileMill.setWaterConsumption(60);
        texttileMill.setLandSqFt(80000);
        texttileMill.setRoadLoad(40);
        texttileMill.setJobs(JobType.NO_DIPLOMA, 45);
        texttileMill.setJobs(JobType.DIPLOMA, 20);
        texttileMill.setId(3);
        templates.add(texttileMill);

        /* =====================================================================
           A FIFTH OF THE SIZE, EVERYTHING IN PROPORTION. The morning of
           2026-09-10 found the plant under-producing five-fold and raised its
           output to 30,000 with the jobs unchanged - the right productivity,
           at a size the city cannot use: 30,000 units is more than a city of
           twenty thousand eats, so the one plant it owned sat with its shed
           permanently full, set its own price by the surplus, and could never
           be shed because floor(spare / 30,000) is zero when you own one.

           Same output per worker, same cost per unit, same shed-months of
           stock - every figure divided by five, so a city holds three to five
           of these and can shed one. Costed linearly: a real small plant is
           somewhat dearer per unit than a large one, and that is an economy of
           scale for later, not a reason to keep a plant nobody can build.
           ===================================================================== */
        /** The small bakery. Same trade as building 3, on a smaller footprint. */
        BuildingsTemplate foodProcessingPlant = new BuildingsTemplate("Bakery", BuildingType.INDUSTRIAL);
        foodProcessingPlant.setSector("Industry");
        foodProcessingPlant.setStock(996000);
        foodProcessingPlant.setCashCost(8994);
        foodProcessingPlant.setConstructionPoints(700);
        foodProcessingPlant.setConstructionMaterials(166);
        foodProcessingPlant.makes(Good.BREAD, 232000);
        foodProcessingPlant.makes(Good.BAKERY, 100000);
        foodProcessingPlant.uses(Good.CROPS, 632);   // the same 74% yield
        foodProcessingPlant.setElectricityConsumption(24);
        foodProcessingPlant.setWaterConsumption(30);
        foodProcessingPlant.setLandSqFt(40000);
        foodProcessingPlant.setRoadLoad(40);
        foodProcessingPlant.setJobs(JobType.NO_DIPLOMA, 28);
        foodProcessingPlant.setJobs(JobType.DIPLOMA, 24);
        foodProcessingPlant.setJobs(JobType.COLLEGE_ENGINEERING, 2);
        foodProcessingPlant.setId(7);
        templates.add(foodProcessingPlant);

        /* =====================================================================
           THE MATERIALS PLANT MAKES 160 UNITS A MONTH, from 400 (2026-09-10).

           The unit went from $2,000 to $18,000 and the plant's output had to be
           looked at again, because at 400 units it was absurd in both
           directions. At $2,000 it made $800k of material a month with 250
           people on the payroll - $969k a month of wages at the real ladder -
           so it lost money before it paid for power, and nothing ever built
           one. At $18,000, 400 units is $7.2M a month of free material for a
           $60M building: a ten-month payback on something the player buys
           once and keeps for ever.

           WHAT 160 IS. A real building-materials producer turns over about
           1.4x its capital a year, and this one would - $86M on $60M - but it
           does not BUY anything in this model: no cement, no logs, no
           aggregate, no steel, which in a real plant is 55-65% of what it
           sells. So the plant cannot be credited with what it sells; it can be
           credited with what it ADDS, which is the remaining 35-40%. That is
           $30M a year, $2.9M a month, 160 units. Net of wages, power and the
           property tax it returns about 34% a year, which is inside the band
           this game's other businesses run in (a mill 28-55%, a mine ~37%).

           If the plant ever buys its inputs abroad - a real import line on the
           balance of payments - the 400 comes back with them.
           ===================================================================== */
        BuildingsTemplate constructionMaterialsPlant = new BuildingsTemplate("Construction Materials Plant", BuildingType.CONSTRUCTION);
        constructionMaterialsPlant.setSector("Materials");
        constructionMaterialsPlant.setStock(480);
        constructionMaterialsPlant.setCashCost(44904);
        constructionMaterialsPlant.setConstructionPoints(9000);
        constructionMaterialsPlant.setConstructionMaterials(831);
        constructionMaterialsPlant.makes(Good.MATERIALS, 160);
        constructionMaterialsPlant.setElectricityConsumption(200);
        constructionMaterialsPlant.setWaterConsumption(80);
        constructionMaterialsPlant.setLandSqFt(300000);
        constructionMaterialsPlant.setRoadLoad(350);
        constructionMaterialsPlant.setJobs(JobType.NO_DIPLOMA, 160);
        constructionMaterialsPlant.setJobs(JobType.DIPLOMA, 80);
        constructionMaterialsPlant.setJobs(JobType.COLLEGE_ENGINEERING, 10);
        constructionMaterialsPlant.setId(8);
        templates.add(constructionMaterialsPlant);

        BuildingsTemplate constructionDepot = new BuildingsTemplate("Construction Depot", BuildingType.CONSTRUCTION);
        constructionDepot.setSector("Construction");
        constructionDepot.setCashCost(1482);
        constructionDepot.setConstructionPoints(400);
        constructionDepot.setConstructionMaterials(55);
        constructionDepot.makes(Good.BUILDING_WORK, 400);
        constructionDepot.setElectricityConsumption(25);
        constructionDepot.setWaterConsumption(3);
        constructionDepot.setLandSqFt(60000);
        constructionDepot.setRoadLoad(90);
        constructionDepot.setJobs(JobType.NO_DIPLOMA, 35);
        constructionDepot.setJobs(JobType.DIPLOMA, 15);
        constructionDepot.setId(4);
        templates.add(constructionDepot);

        // ELECTRICTY buildings
        BuildingsTemplate coalPowerplant = new BuildingsTemplate("Coal Power Plant", BuildingType.ELECTRICITY)
                .setCashCost(1071816)
                .setConstructionPoints(120000)
                .setConstructionMaterials(19848)
                .setProduction1(280000) // electricity output
                .setElectricityConsumption(15)
                .setWaterConsumption(400) // cooling - the biggest single draw in the game
                .setLandSqFt(2000000)
                .setRoadLoad(120)
                .setJobs(JobType.NO_DIPLOMA, 40)
                .setJobs(JobType.DIPLOMA, 20)
                .setJobs(JobType.COLLEGE_ENGINEERING, 6)
                .setJobs(JobType.UNIV_SCIENCE, 2)
                .setId(9);

        templates.add(coalPowerplant);

        /* ---------------------------- WIND ----------------------------
           Jerus, 2026-09-09: "a windmill or windmill farm, or you think better
           no? Cause cost wise its trash right."

           It is not trash - it is the cheap one, and that was the whole problem
           with adding it. EIA AEO2025 puts onshore wind at $1,489/kW against
           ultra-supercritical coal at $4,103/kW, both in 2023 dollars. Derate
           for capacity factor (wind ~40%, coal ~85%) and wind is still about a
           quarter cheaper per unit of energy actually DELIVERED, before coal
           buys a single tonne of fuel.

           WHICH IS EXACTLY WHY IT COULD NOT SIMPLY BE CHEAPER POWER. A building
           that wins on every axis makes the other one dead, and this project
           has spent two days digging out of that: Low-Rise Apartments were
           never built because the cost ladder was inverted, and Studio
           Apartments sat in the backlog as "strictly dominated" for months.

           So it loses on the two axes the model already has, and needs no new
           mechanic for either:

             LAND. NREL's direct-impact figure for wind is about 1.85 acres per
             MW - the pads, the access roads and the substation, which is what a
             city actually has to buy; the rest of a wind lease stays farmland.
             At 2 acres/MW this farm sits on 40 acres for 8,100 units of power,
             where the Coal Power Plant sits on 46 acres for 280,000. That is
             roughly THIRTY TIMES the ground per unit, and this game has a land
             market whose price climbs with the city ($0.46/sq ft at founding,
             about $46 in a big one). The crossover lands near $8.50/sq ft: wind
             wins on cheap ground and loses on dear ground, which is a band,
             exactly like the three roads.

             JOBS. A coal plant here employs 68. Real wind O&M is about 0.2-0.3
             permanent jobs per MW, so a 20 MW farm is a handful of turbine
             technicians - seven, generously. This city wants employers, and
             this is not one.

           AND IT IS THE SMALL PLANT THE GAME HAS BEEN MISSING. The 4,000-month
           playtest sat at 2-3% power utilisation for its entire life, because a
           325 MW coal plant is the only generator in the game and a city of
           77,000 needs a fraction of one. This farm is 20 MW, $31.8M and 1,800
           construction points - a founding city can buy it in money AND in
           time, where the coal plant is 300 months of a founding yard.

           NO INTERMITTENCY SYSTEM, deliberately. The capacity factor is baked
           into production1 the same way the coal plant's 85% already is:
           20 MW x 40% x 730 hours = 5.84 GWh a month, and one electricity unit
           is 720 kWh a month (derived from the Steel Foundry's documented
           450 kWh a tonne - see RealismCheck). Weather and storage are a
           different game.
           ------------------------------------------------------------------ */
        BuildingsTemplate windFarm = new BuildingsTemplate("Wind Farm", BuildingType.ELECTRICITY)
                .setCashCost(23240)                 // $31.8M all-in at $1,590/kW
                .setConstructionPoints(1800)        // months, not years
                .setConstructionMaterials(431)
                .setProduction1(8100)               // 20 MW at a 40% capacity factor
                .setElectricityConsumption(1)       // its own parasitic load
                .setWaterConsumption(0)             // none, which is half the point
                .setLandSqFt(1742400)               // 40 acres - 2 per MW
                .setRoadLoad(5)
                .setJobs(JobType.NO_DIPLOMA, 2)
                .setJobs(JobType.DIPLOMA, 4)        // turbine technicians, a real trade
                .setJobs(JobType.COLLEGE_ENGINEERING, 1)
                .setId(41);

        templates.add(windFarm);

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
                .setCashCost(65784)
                .setConstructionPoints(44000)
                .setConstructionMaterials(2436)
                .setProduction1(60000) // water output
                // Water and wastewater are typically 2-4% of a city's electrical
                // load. 900 against ~25,000 houses' worth of draw sits in that band.
                .setElectricityConsumption(900)
                .setWaterConsumption(20) // filter backwash and process losses
                .setLandSqFt(800000)
                .setRoadLoad(15)
                .setJobs(JobType.NO_DIPLOMA, 8)
                .setJobs(JobType.DIPLOMA, 14)          // certified operators, the bulk of the crew
                .setJobs(JobType.COLLEGE_ENGINEERING, 4)
                .setJobs(JobType.UNIV_SCIENCE, 3)      // water quality lab
                .setId(10);

        templates.add(waterTreatmentPlant);

        /* ------------------------------ STEEL ------------------------------
           Real electric-arc ratios, on a deliberately small plant: 1.1 tonnes
           of scrap per tonne of steel, about 450 kWh a tonne, a couple of cubic
           metres of make-up water a tonne.

           Scrap in at $400 a tonne, steel out at $500. That $100 conversion
           margin is roughly half what a real mill clears, because a small
           distant producer is a price taker at both ends - and out of it come
           the wages, the power and the water. What is left is a few percent.

           They are here to employ people. The city's return is the payroll,
           the wage tax on it, and everything those wages buy.
           ------------------------------------------------------------------ */
        BuildingsTemplate steelFoundry = new BuildingsTemplate("Steel Foundry", BuildingType.HEAVY_INDUSTRY)
                .setCashCost(22471)
                .setConstructionPoints(1400)
                .setConstructionMaterials(416)
                .setSector("Heavy Industry")
                .makes(Good.STEEL, 1200)            // tonnes of steel a month
                .uses(Good.IRON, 1320)              // tonnes of ore or scrap that takes
                .setElectricityConsumption(750)
                .setWaterConsumption(65)
                .setLandSqFt(90000)
                .setRoadLoad(110)
                .setJobs(JobType.NO_DIPLOMA, 22)
                .setJobs(JobType.DIPLOMA, 12)
                .setJobs(JobType.COLLEGE_ENGINEERING, 4)
                .setId(11);

        templates.add(steelFoundry);

        BuildingsTemplate steelMiniMill = new BuildingsTemplate("Steel Mini-Mill", BuildingType.HEAVY_INDUSTRY)
                .setCashCost(89864)
                .setConstructionPoints(9000)
                .setConstructionMaterials(1664)
                .setSector("Heavy Industry")
                .makes(Good.STEEL, 6000)
                .uses(Good.IRON, 6600)
                // An arc furnace is the largest single electrical load a city
                // this size can build. That is the point of it as a mechanic.
                .setElectricityConsumption(3700)
                .setWaterConsumption(320)
                .setLandSqFt(400000)
                .setRoadLoad(500)
                .setJobs(JobType.NO_DIPLOMA, 70)
                .setJobs(JobType.DIPLOMA, 45)
                .setJobs(JobType.COLLEGE_ENGINEERING, 12)
                .setJobs(JobType.UNIV_SCIENCE, 3)
                .setId(12);

        templates.add(steelMiniMill);

        /* --------------------------- INFRASTRUCTURE ---------------------------
           Roads, as a building, because that is what makes them cost anything.

           A road order goes through the construction sector like everything
           else: it consumes materials, occupies the build queue, and pays the
           builders. That is the whole reason for modelling public works this
           way - it makes them a demand-side lever on a private industry rather
           than a number the player raises for free.

           No jobs, on any of them. Nobody staffs a road; the construction crews
           that build it are already paid, and pretending a highway has an
           operating payroll would put wages in the economy that no employer is
           paying.

           =====================================================================
           THREE ROADS, AND WHY THERE ARE THREE

           Jerus: "one that is construction points cheap to build, but more land
           intensive, one that is land cheap but costly and construction
           intensive." Which is the real engineering trade: you can spend ground,
           or you can spend money and labour to avoid spending ground, and a city
           changes its mind about which it would rather spend as it fills up.

           Before this there was one road, so congestion had exactly one answer
           and the answer got worse as the city grew - the 4,000-month playtest
           ends at 42-71% road throughput with population still climbing, and the
           reason is that the only road on offer eats a quarter of a million
           square feet a time in a city that has run out of room.

           WHAT MAKES THIS THREE CHOICES AND NOT ONE CHOICE WITH DECORATION

           The trap is Studio Apartments: a building that loses to something else
           at every price is not an option, it is a mistake the player can make.
           So these are costed so each one is the cheapest per trip carried
           across a band of land prices, and InfrastructureCheck asserts it -
           at today's materials price the crossovers are about $16.50 and $28.50
           a square foot, against a ground price that starts at $0.70 and climbs
           with every block owned and every thousand residents.

               under $16.50/sq ft ......... Gravel Road
               $16.50 to $28.50/sq ft ..... Paved Road
               over $28.50/sq ft .......... Elevated Highway

           Per 1,000 trips of capacity, which is the only way to compare them:

                              cash   points  materials       land   power
               Gravel        2,000    1,556      2,222    500,000     5.6
               Paved         2,917    3,333      4,167    208,333    33.3
               Elevated      5,333    6,667      5,333     42,000    90.0

           Read across: the gravel road is the cheapest thing in the game to
           BUILD and the most expensive to FIND ROOM FOR; the elevated highway is
           the reverse, twelve times more land-efficient and four times slower to
           put up. The paved road is neither and wins the middle.

           The power column is the third cost and it is deliberate. An elevated
           highway is lit end to end and pumped and signalled; a gravel road is
           lit by headlights. So digging your way out of a land shortage lands
           the bill on the power station instead - which is the point. There is
           no free direction.
           ---------------------------------------------------------------- */

        /*
           The cheap one. Grade a strip, lay aggregate, and let it be wide -
           which is exactly why it needs half a million square feet for 900
           trips. Two thirds less construction work than a paved road per trip
           carried, and two and a half times the ground.
        */
        BuildingsTemplate gravelRoad = new BuildingsTemplate("Gravel Road", BuildingType.INFRASTRUCTURE)
                .setCapacity(900)
                .setCashCost(2326)
                .setConstructionPoints(1400)
                .setConstructionMaterials(193)
                .setElectricityConsumption(5)   // barely lit
                .setLandSqFt(450000)
                .setRoadLoad(0)                 // a road does not drive on itself
                .setId(29);

        templates.add(gravelRoad);

        /*
           The one that was here first. Its TOTAL is unchanged - $13.5M, about
           1.7x a real figure, held there on purpose so the three bands below
           stay where three-roads.md put them.

           Deliberately materials-heavy and cash-light next to the power plant:
           a road is mostly aggregate and labour, not equipment. Sixty percent
           of it is material - 450 units at $18,000 is $8.1M against $5.4M of
           cash - which means the first road a city needs is also the thing
           that makes the materials plant worth building.

           Renamed from "Road Network" when it stopped being the only road. The
           id is untouched, so every existing save loads it into the same slot -
           saves key on id, never on the name.
        */
        BuildingsTemplate pavedRoad = new BuildingsTemplate("Paved Road", BuildingType.INFRASTRUCTURE)
                .setCapacity(1200)              // road capacity provided
                .setCashCost(5400)
                .setConstructionPoints(4000)
                .setConstructionMaterials(450)
                .setElectricityConsumption(40)  // street lighting and signals
                .setLandSqFt(250000)
                .setRoadLoad(0)                 // a road does not drive on itself
                .setFreightGrade(.35)           // kerbed and signalled; a lorry still stops at junctions
                .setId(13);

        templates.add(pavedRoad);

        /*
           The expensive one, and the only road a built-out city can still put
           up. 63,000 square feet is a column every so often instead of a
           right-of-way - a quarter of a paved road's footprint for a quarter
           more capacity.

           10,000 construction points is several months of the entire city's
           output, and that is meant: being out of room is a late-game problem,
           and a late-game city has builders idle and nothing urgent to point
           them at. It stays inside the existing 12-month order cap.
        */
        BuildingsTemplate elevatedHighway = new BuildingsTemplate("Elevated Highway", BuildingType.INFRASTRUCTURE)
                .setCapacity(1500)
                .setCashCost(9600)
                .setConstructionPoints(10000)
                .setConstructionMaterials(800)
                .setElectricityConsumption(135) // lit, pumped and signalled end to end
                .setLandSqFt(63000)
                .setRoadLoad(0)                 // a road does not drive on itself
                .setFreightGrade(1.0)           // grade-separated: no junction shares it
                .setId(30);

        templates.add(elevatedHighway);

        /* ===================================================================
           THE THINGS THAT CARRY PEOPLE (2026-09-16)

           The land ladder these sit on was already in this file and nobody had
           pointed at it. Per unit of capacity:

             Gravel Road        500 sq ft      $2.58
             Paved Road         208            $4.50
             Elevated Highway    42            $6.40

           Twelve times the capacity per acre for two and a half times the
           price. Transit is the same trade taken two rungs further, which is
           what makes it the answer to the thing that actually stops a mature
           city: land at eighty-eight percent with nothing built for a decade.

           THEY ARE PUBLIC. Jerus: "transit is public, so thats the
           government." The city builds them, the city pays for them, and the
           fare is the player's to set - see TaxPolicy. Roads work exactly this
           way already and have since they were buildings.

           AND THEY CARRY NOTHING BUT PEOPLE, which is not a limitation to be
           designed around but the point: a city cannot solve an ore problem
           with a tram. See InfrastructureManager and Traffic.
           =================================================================== */

        /*
         * BUS NETWORK. Depots, shelters and the buses themselves - and it runs
         * on the road, which is why it is the one transit mode with a road load
         * of its own. The cheapest way to move people and the one that helps
         * least when the streets are already full, which is exactly what a bus
         * is.
         */
        BuildingsTemplate busNetwork = new BuildingsTemplate("Bus Network", BuildingType.INFRASTRUCTURE)
                .setTransitCapacity(2500)
                .setCashCost(12000)
                .setConstructionPoints(2200)
                .setConstructionMaterials(280)
                .setElectricityConsumption(60)
                .setWaterConsumption(12)
                .setLandSqFt(20000)             // a depot and a few hundred shelters
                .setRoadLoad(120)               // the buses are on the road too
                .setJobs(JobType.NO_DIPLOMA, 210)
                .setJobs(JobType.DIPLOMA, 40)
                .setId(59);

        templates.add(busNetwork);

        /*
         * LIGHT RAIL. Its own right of way at street level: eight times a bus
         * network's capacity on twice the land and five times the money, and
         * it is not in traffic. The middle rung, and the first one that is
         * cheaper per rider than it is per acre.
         */
        BuildingsTemplate lightRail = new BuildingsTemplate("Light Rail Line", BuildingType.INFRASTRUCTURE)
                .setTransitCapacity(8000)
                .setCashCost(64000)
                .setConstructionPoints(14000)
                .setConstructionMaterials(1900)
                .setElectricityConsumption(420)
                .setWaterConsumption(20)
                .setLandSqFt(45000)
                .setRoadLoad(30)                // stations and the vans that service them
                .setJobs(JobType.NO_DIPLOMA, 180)
                .setJobs(JobType.DIPLOMA, 90)
                .setJobs(JobType.COLLEGE_ENGINEERING, 14)
                .setId(60);

        templates.add(lightRail);

        /*
         * METRO LINE. Thirty thousand journeys a month on thirty thousand
         * square feet, which is FIVE HUNDRED TIMES a gravel road's capacity per
         * acre for four times its price per rider. The whole ladder in one
         * building: a city that has run out of ground and not out of money
         * builds this, and a city with ground to spare never should.
         */
        BuildingsTemplate metro = new BuildingsTemplate("Metro Line", BuildingType.INFRASTRUCTURE)
                .setTransitCapacity(30000)
                .setCashCost(320000)
                .setConstructionPoints(62000)
                .setConstructionMaterials(9400)
                .setElectricityConsumption(1850)
                .setWaterConsumption(90)
                .setLandSqFt(30000)             // entrances and vents; the rest is underneath
                .setRoadLoad(60)
                .setJobs(JobType.NO_DIPLOMA, 520)
                .setJobs(JobType.DIPLOMA, 260)
                .setJobs(JobType.COLLEGE_ENGINEERING, 48)
                .setId(61);

        templates.add(metro);

        /* ------------------------------- RAIL -------------------------------
           Freight rail, and the first thing the city does NOT build.

           Jerus: "you build roads obviously but rail is its own sector... it
           wants and will do everything possible to stay profitable and maximize
           profits", and "no at first, rail doesnt even build, everything is
           exported by truck, which is obviously more expensive and road
           demanding." So these are financed by the investor against a business
           case like a steel mill, they are owned by sectors.Rail, and a city
           that never builds one trades exactly as it always did.

           WHAT ONE IS WORTH, AT THE PLAYTEST'S OWN NUMBERS. The 4,000-month
           city ships 1,166,821 tonnes a month across its boundary - 99.7% of it
           bulk, and 97% of that steel in and fabricated steel out - and pays
           $372m of freight on it against a GDP of $592m. A lorry charges about
           $319 a tonne. At 60% of that a Freight Line's 100,000 tonnes is
           $19.1m a month of haulage, which is why these cost what they cost:
           the capital is the only thing standing between a player and the
           whole of that bill.

           THE LADDER IS PER TONNE, like every other ladder in this catalogue.
           $5,600 of capital per tonne a month on a spur, $5,200 on a line,
           $4,960 on a terminal - bigger is cheaper, and bigger is also 1,482
           posts, which is the largest single hiring commitment in the game.

           AND THEY ARE ENORMOUS ON THE GROUND. Seven and a half million square
           feet for a terminal, against two million for a Coal Power Plant, the
           largest thing here before today. That is the trade Jerus asked for:
           rail takes bulk freight off the road and hands back a lot of the land
           it saved, so a city buys track with ground as well as money.
           ------------------------------------------------------------------ */

        /*
         * RAIL SPUR. A siding, a small yard and enough track to reach it - what
         * a city lays when its first mill starts shipping and the lorries are
         * charging the world's price to move it.
         */
        BuildingsTemplate railSpur = new BuildingsTemplate("Rail Spur", BuildingType.RAIL)
                .setSector("Rail")
                .setRailCapacity(50000)         // tonnes across the boundary a month
                .setCashCost(140000)
                .setConstructionPoints(21000)
                .setConstructionMaterials(4500)
                .setElectricityConsumption(600)
                .setWaterConsumption(30)
                .setLandSqFt(700000)
                // Mostly the drayage between the siding and the works, which is
                // still a lorry - see Rail's header and BuildingsTemplate.loadOf.
                .setRoadLoad(500)
                .setJobs(JobType.NO_DIPLOMA, 120)
                .setJobs(JobType.DIPLOMA, 45)
                .setJobs(JobType.COLLEGE_ENGINEERING, 8)
                .setId(62);

        templates.add(railSpur);

        /*
         * FREIGHT LINE. Real main line: four times the spur's tonnage for less
         * than four times its money, and the rung a trading city actually lives
         * on.
         */
        BuildingsTemplate freightLine = new BuildingsTemplate("Freight Line", BuildingType.RAIL)
                .setSector("Rail")
                .setRailCapacity(200000)
                .setCashCost(520000)
                .setConstructionPoints(78000)
                .setConstructionMaterials(16500)
                .setElectricityConsumption(2300)
                .setWaterConsumption(110)
                .setLandSqFt(2400000)
                .setRoadLoad(2000)
                .setJobs(JobType.NO_DIPLOMA, 430)
                .setJobs(JobType.DIPLOMA, 165)
                .setJobs(JobType.COLLEGE_ENGINEERING, 30)
                .setId(63);

        templates.add(freightLine);

        /*
         * RAIL TERMINAL. A quarter of a million tonnes a month, 186,000
         * construction points - a year and a half of everything the playtest
         * city can build - and seven and a half million square feet. The
         * biggest single decision in the game.
         */
        BuildingsTemplate railTerminal = new BuildingsTemplate("Rail Terminal", BuildingType.RAIL)
                .setSector("Rail")
                .setRailCapacity(500000)
                .setCashCost(1240000)
                .setConstructionPoints(186000)
                .setConstructionMaterials(39000)
                .setElectricityConsumption(5600)
                .setWaterConsumption(260)
                .setLandSqFt(5500000)
                .setRoadLoad(4900)
                .setJobs(JobType.NO_DIPLOMA, 1020)
                .setJobs(JobType.DIPLOMA, 390)
                .setJobs(JobType.COLLEGE_ENGINEERING, 72)
                .setId(64);

        templates.add(railTerminal);

        /* --------------------------- AUTOMOTIVE ---------------------------
           The top of the chain, and the biggest payroll on it.

           THE RECIPE IS THE MECHANIC. A car is 3.5 tonnes of fabricated steel
           and 400kg of machinery; a van is 5 and 600; a wagon set is 120 and
           20. Neither input can be bought from the world at any price - see
           Good's header - so every one of these plants is a bet that the city
           already has fabrication shops and a machine works behind it. That is
           the longest dependency in the game: a mine, a mill, a fabricator, a
           machine works, and then this.

           WHAT THE NUMBERS ARE ANCHORED ON. Revenue per worker, against the
           bakeries, exactly as Food Processing was: about $17k a head a month,
           which is where every maker in this catalogue sits. At a mid-band car
           price of $40k an Assembly Plant's five hundred cars is $20m a month
           against 1,180 posts - $16.9k a head - and the parts are $17k a car
           at mid-band prices, about 42% of revenue, falling to 28% in a city
           whose fabricators are competing. That gap IS the cluster reward.

           AND THEY ARE ENORMOUS EMPLOYERS. An Assembly Plant is 1,180 posts,
           the second largest single hiring commitment in the game after a rail
           terminal, and it needs 80% of them staffable before it is ordered.
           A city builds this when it has people, not when it has money.
           ------------------------------------------------------------------ */

        /*
         * VEHICLE WORKS. The small rung: a coachbuilder rather than a
         * production line, and the one a city can put up before it has twelve
         * hundred spare workers.
         */
        BuildingsTemplate vehicleWorks = new BuildingsTemplate("Vehicle Works", BuildingType.AUTOMOTIVE)
                .setSector("Automotive")
                .makes(Good.CARS, 120)                  // cars a month
                .uses(Good.FABRICATED_STEEL, 420)       // 3.5 t a car
                .uses(Good.MACHINERY, 48)               // 400 kg a car
                .setCashCost(66000)
                .setConstructionPoints(7260)
                .setConstructionMaterials(1580)
                .setElectricityConsumption(600)
                .setWaterConsumption(45)
                .setLandSqFt(500000)
                .setRoadLoad(150)
                .setStock(360)                          // three months on the lot
                .setJobs(JobType.NO_DIPLOMA, 100)
                .setJobs(JobType.DIPLOMA, 55)
                .setJobs(JobType.COLLEGE_ENGINEERING, 13)
                .setJobs(JobType.UNIV_SCIENCE, 2)
                .setId(65);

        templates.add(vehicleWorks);

        /* ===================================================================
           THE LUXURY SHOPS (2026-09-17)

           They make nothing and that is the point. A boutique buys a watch
           from the world and sells it over a counter, and the only thing it
           adds - the only thing this city can be SHORT of - is the counter.
           See Good.LUXURY_TRADE and LuxuryRetail.

           COVERAGE PER DOLLAR IS DELIBERATELY WORSE THAN A GROCER'S. A
           Convenience Store covers 480 people for $442k; a Boutique covers 320
           for $1,180k, which is eight times the money a head. Selling somebody
           a watch takes more floor, more staff and more of both per customer
           than selling them bread, and a city that wants to spend its money
           has to build for it rather than have it happen.
           =================================================================== */
        BuildingsTemplate boutique = new BuildingsTemplate("Boutique", BuildingType.LUXURY)
                .setSector("Luxury Retail")
                .makes(Good.LUXURY_TRADE, 320)          // customers served a month
                .uses(Good.LUXURIES, 320)               // one piece each, bought in
                .setCashCost(1180)
                .setConstructionPoints(190)
                .setConstructionMaterials(26)
                .setElectricityConsumption(9)
                .setWaterConsumption(1)
                .setLandSqFt(6000)
                .setRoadLoad(8)
                .setStock(960)                          // three months on the shelf
                .setJobs(JobType.NO_DIPLOMA, 2)
                .setJobs(JobType.DIPLOMA, 4)
                .setJobs(JobType.COLLEGE_BUSINESS, 1)
                .setId(69);
        boutique.setCoverage(320);
        templates.add(boutique);

        /*
         * ...AND THE BIG ONE, which is where a large city actually spends.
         * Thirteen times the customers on eighteen times the money, so it is
         * the worse buy per head until the city is big enough to fill it -
         * which is the same shape every other big building in this game has.
         */
        BuildingsTemplate departmentStore = new BuildingsTemplate("Department Store", BuildingType.LUXURY)
                .setSector("Luxury Retail")
                .makes(Good.LUXURY_TRADE, 4200)
                .uses(Good.LUXURIES, 4200)
                .setCashCost(21600)
                .setConstructionPoints(2900)
                .setConstructionMaterials(560)
                .setElectricityConsumption(130)
                .setWaterConsumption(18)
                .setLandSqFt(90000)
                .setRoadLoad(70)
                .setStock(12600)
                .setJobs(JobType.NO_DIPLOMA, 34)
                .setJobs(JobType.DIPLOMA, 46)
                .setJobs(JobType.COLLEGE_BUSINESS, 9)
                .setJobs(JobType.UNIV_FINANCE, 2)
                .setId(70);
        departmentStore.setCoverage(4200);
        templates.add(departmentStore);


        /* ===================================================================
           THE KITCHENS (2026-09-18)

           COVERAGE HERE IS MEALS A MONTH, NOT PEOPLE A MONTH, and every
           arithmetic in this sector turns on the difference. A person eats
           NINETY meals in a month and buys ONE basket, so a Diner's 13,500
           covers a hundred and fifty person-months of food and serves four
           hundred and fifty covers a day. Read as customers it would look
           ninety times the business it is.

           WHAT THEY BUY IS THE REFERENCE BASKET, to the kilogram, scaled to
           the person-months their meals come to: the Convenience Store's
           thirteen lines divided by its 480 and multiplied back up. That is
           not decoration. A meal out REPLACES groceries - Jerus's own rule -
           and it can only replace them honestly if it is made of the same
           food in the same proportions. A restaurant that bought a cheaper
           basket would be feeding people a different meal and the substitution
           underneath the whole sector would be a fiction.

           A KITCHEN IS NOT A WAREHOUSE. Stock is about a month and a half of
           throughput where a shop carries three; food goes off, and a
           restaurant that held a quarter's dinners would be a restaurant
           throwing most of them away.

           AND THE SIZE IS SET BY THE PAYROLL, WHICH IS WHAT CAUGHT THE FIRST
           TRY. The trade's own ratio is food about a third of the ticket and
           labour about another third - so a kitchen's FOOD BILL should be
           about its WAGE BILL, and that is what fixes how many meals a
           building with eight staff has to serve. Measured at the founding
           city's prices, a person-month of food costs $197 and a meal $2.19,
           so eight staff at $30.8k a month need a hundred and fifty
           person-months through the kitchen - 13,500 meals.

           THE FIRST PASS HAD A DINER AT 2,700 MEALS, sized off nothing but a
           guess at what a small restaurant looks like, and it bought $5.9k of
           food a month against $30.8k of wages. It could not have paid them in
           any city at any margin the band allows, and nothing outside
           RestaurantsCheck would have said so. Same lesson as the eleventh
           sector's four calibration passes: revenue per worker is the ratio
           that decides whether a maker survives a city growing up.

           EVEN SIZED PROPERLY THEY ARE THE LOWEST REVENUE-PER-WORKER BUSINESS
           IN THE CATALOGUE - about $12k a head against a Steel Foundry's $34k
           - and that is what a restaurant is. What a city buys here is
           somewhere to eat, and somewhere to eat is mostly people.
           =================================================================== */
        BuildingsTemplate diner = new BuildingsTemplate("Diner", BuildingType.HOSPITALITY)
                .setSector("Restaurants")
                .makes(Good.MEALS, 13500)            // meals served a month
                .uses(Good.GRAINS, 450)
                .uses(Good.BREAD, 525)
                .uses(Good.DAIRY_EGGS, 1200)
                .uses(Good.VEGETABLES, 1050)
                .uses(Good.FRUIT, 750)
                .uses(Good.MEAT, 675)
                .uses(Good.FISH, 180)
                .uses(Good.FATS, 120)
                .uses(Good.PROCESSED_MEAT, 150)
                .uses(Good.READY_MEALS, 225)
                .uses(Good.BAKERY, 225)
                .uses(Good.SNACKS, 150)
                .uses(Good.DRINKS, 1800)
                .setCashCost(1100)
                .setConstructionPoints(280)
                .setConstructionMaterials(40)
                .setElectricityConsumption(22)
                .setWaterConsumption(8)
                .setLandSqFt(5000)
                .setRoadLoad(14)
                .setStock(12000)
                .setJobs(JobType.NO_DIPLOMA, 3)
                .setJobs(JobType.DIPLOMA, 1)
                .setId(71);
        diner.setCoverage(13500);
        templates.add(diner);

        /*
         * ...AND THE ONE A CITY EATS AT. Three times the meals on three and a
         * third times the money, so it is the worse buy per cover until the
         * city is large enough to fill it - the same shape the Department
         * Store takes against the Boutique, and for the same reason.
         */
        BuildingsTemplate restaurant = new BuildingsTemplate("Restaurant", BuildingType.HOSPITALITY)
                .setSector("Restaurants")
                .makes(Good.MEALS, 40500)            // meals served a month
                .uses(Good.GRAINS, 1350)
                .uses(Good.BREAD, 1575)
                .uses(Good.DAIRY_EGGS, 3600)
                .uses(Good.VEGETABLES, 3150)
                .uses(Good.FRUIT, 2250)
                .uses(Good.MEAT, 2025)
                .uses(Good.FISH, 540)
                .uses(Good.FATS, 360)
                .uses(Good.PROCESSED_MEAT, 450)
                .uses(Good.READY_MEALS, 675)
                .uses(Good.BAKERY, 675)
                .uses(Good.SNACKS, 450)
                .uses(Good.DRINKS, 5400)
                .setCashCost(3600)
                .setConstructionPoints(900)
                .setConstructionMaterials(130)
                .setElectricityConsumption(68)
                .setWaterConsumption(24)
                .setLandSqFt(14000)
                .setRoadLoad(42)
                .setStock(36000)
                .setJobs(JobType.NO_DIPLOMA, 8)
                .setJobs(JobType.DIPLOMA, 3)
                .setJobs(JobType.COLLEGE_BUSINESS, 1)
                .setId(72);
        restaurant.setCoverage(40500);
        templates.add(restaurant);

        /*
         * ASSEMBLY PLANT. The real thing: four times the works on four times
         * the money, and the single largest industrial payroll a city of this
         * size can carry.
         */
        BuildingsTemplate assemblyPlant = new BuildingsTemplate("Assembly Plant", BuildingType.AUTOMOTIVE)
                .setSector("Automotive")
                .makes(Good.CARS, 500)
                .uses(Good.FABRICATED_STEEL, 1750)
                .uses(Good.MACHINERY, 200)
                .setCashCost(221000)
                .setConstructionPoints(24300)
                .setConstructionMaterials(5300)
                .setElectricityConsumption(2600)
                .setWaterConsumption(190)
                .setLandSqFt(1800000)
                .setRoadLoad(610)
                .setStock(1500)
                .setJobs(JobType.NO_DIPLOMA, 415)
                .setJobs(JobType.DIPLOMA, 225)
                .setJobs(JobType.COLLEGE_ENGINEERING, 52)
                .setJobs(JobType.UNIV_SCIENCE, 8)
                .setId(66);

        templates.add(assemblyPlant);

        /*
         * COMMERCIAL VEHICLE PLANT. Vans and trucks - heavier, dearer, and
         * bought by a business rather than a household.
         */
        BuildingsTemplate vanPlant = new BuildingsTemplate("Commercial Vehicle Plant", BuildingType.AUTOMOTIVE)
                .setSector("Automotive")
                .makes(Good.VANS, 150)
                .uses(Good.FABRICATED_STEEL, 750)       // 5 t a van
                .uses(Good.MACHINERY, 90)               // 600 kg a van
                .setCashCost(132000)
                .setConstructionPoints(14500)
                .setConstructionMaterials(3170)
                .setElectricityConsumption(1200)
                .setWaterConsumption(90)
                .setLandSqFt(950000)
                .setRoadLoad(290)
                .setStock(450)
                .setJobs(JobType.NO_DIPLOMA, 195)
                .setJobs(JobType.DIPLOMA, 106)
                .setJobs(JobType.COLLEGE_ENGINEERING, 25)
                .setJobs(JobType.UNIV_SCIENCE, 4)
                .setId(67);

        templates.add(vanPlant);

        /*
         * LOCOMOTIVE WORKS. Four wagon sets a month - a locomotive and what it
         * pulls - and the most expensive single thing anybody in this game
         * sells. The railway's supplier, once the railway needs one.
         */
        BuildingsTemplate locoWorks = new BuildingsTemplate("Locomotive Works", BuildingType.AUTOMOTIVE)
                .setSector("Automotive")
                .makes(Good.ROLLING_STOCK, 4)
                .uses(Good.FABRICATED_STEEL, 480)       // 120 t a set
                .uses(Good.MACHINERY, 80)               // 20 t a set
                .setCashCost(130000)
                .setConstructionPoints(14300)
                .setConstructionMaterials(3120)
                .setElectricityConsumption(1100)
                .setWaterConsumption(80)
                .setLandSqFt(800000)
                .setRoadLoad(295)
                .setStock(12)
                .setJobs(JobType.NO_DIPLOMA, 180)
                .setJobs(JobType.DIPLOMA, 100)
                .setJobs(JobType.COLLEGE_ENGINEERING, 26)
                .setJobs(JobType.UNIV_SCIENCE, 4)
                .setId(68);

        templates.add(locoWorks);

        /* ------------------------------ MINING ------------------------------
           An iron mine, and the biggest employer in the game.

           The profit is the smaller half of why it exists. 396 jobs against a
           Food Processing Plant's 270 - and population here is capped at jobs
           times 2.25, so one mine is worth about nine hundred residents to a
           city that can house them. The 4,000-month playtest froze every city
           at 297 jobs; a single mine is larger than that whole equilibrium.

           The economics, at the mid-band ore price of $360 a tonne:

               revenue   1,330 t x $360        =  $478.8k
               payroll   300 x $800 + 70 x $1,500 + 6 x $4,000 = $369k
               power     1,200 kW              =   $12k
               water     90 units              =    $4.5k
               ---------------------------------------------
               about $63k a month on an asset costing about $2.2M

           Just under 3% a month, which is a real industrial return. Exporting
           everything instead - a mine in a city with no mills - clears about
           $10k, which is thin but positive: worth building first, much better
           once the mills follow.
           ------------------------------------------------------------------ */
        /*
         * THE MINE IS A VOLUME BUSINESS, AND IT WAS NOT PRICED AS ONE
         *
         * It used to lift 1,330 tonnes a month with 376 people on shift - three
         * and a half tonnes per worker per month, which is not a mine, it is a
         * quarry with a shovel. That single number was what made the whole ore
         * economy marginal: at $290 of cost in every tonne, ore could never be
         * cheap enough to make steel worth building AND dear enough to make the
         * mine worth building. The band had nowhere to sit.
         *
         * 2,500 tonnes for the same crew puts the cost at $159 a tonne, which
         * leaves room underneath it for a floor at $200. That is what lets both
         * halves work at once:
         *
         *   selling every tonne abroad at the floor    NET  $103.5k/month
         *   with one mill next door, ore at $269       NET  $276.0k/month
         *
         * The first of those is the important one. A mine is now profitable with
         * nobody to sell to locally, so an investor will sink one on the strength
         * of the export market alone and the mills follow the ore rather than the
         * other way round.
         *
         * Power and water rise with the tonnage; the workforce, the footprint and
         * the site do not. Same hole in the ground, worked properly.
         */
        BuildingsTemplate ironMine = new BuildingsTemplate("Iron Mine", BuildingType.MINING)
                .setCashCost(7286)
                .setConstructionPoints(1600)
                .setConstructionMaterials(135)
                .setSector("Mining")
                .makes(Good.IRON, 2500)         // tonnes of ore a month
                .setElectricityConsumption(2000)
                .setWaterConsumption(150)
                .setLandSqFt(400000)
                .setRoadLoad(420)               // every tonne leaves by truck
                .setJobs(JobType.NO_DIPLOMA, 45)
                .setJobs(JobType.DIPLOMA, 12)
                .setJobs(JobType.COLLEGE_ENGINEERING, 3)
                .setId(14);

        templates.add(ironMine);

        /* ---------------------------- EDUCATION ----------------------------
           The other half of the labour market.

           Until these existed every skilled worker in the game had arrived from
           somewhere else. The city could attract a doctor and could never make
           one, so the eleven job types were a demand curve with no domestic
           supply behind it - and a measured city of 9,016 staffed 220 doctor
           posts out of a pool of labourers because nothing said it could not.

           HOW A SCHOOL WORKS. Capacity is PLACES, and throughput is places
           divided by the length of the course (EducationType.months()) - so a
           university with 2,000 seats and a four-year degree graduates about
           forty a month, not two thousand. The lag is not a special rule, it is
           that arithmetic, and the lag is the whole character of the mechanic.

           THE BASIC LADDER IS A PIPELINE. Elementary and middle school serve
           CHILD, high school serves TEEN, and the pipeline is only as wide as
           its narrowest stage: a city with elementary places for every child
           and one high school does not produce half-educated adults, it
           produces as many diplomas as the high school can seat.

           THE FOUR PROFESSIONAL SCHOOLS RAISE NOBODY'S LEVEL. They license one
           job type each. A city without a medical school can have all the
           graduates it likes and not one of them can be a doctor - the posts
           sit empty and the only doctors it will ever have are the ones who
           moved there.

           AND NOTHING REFUSES TO BE BUILT. Jerus: "basically there would be one
           student in the whole grad school, so cost ineffective basically." A
           Medical School is $78M to build and $1,150k a month to run before its
           280 staff are paid, against a throughput of six doctors a month at
           full enrolment. In a town of forty thousand it enrols nobody and
           costs the same. The eight-hundred-thousand-population figure everyone
           quotes is not a rule anywhere in this file; it is where the
           arithmetic stops being stupid.
           ------------------------------------------------------------------ */

        BuildingsTemplate elementarySchool = new BuildingsTemplate("Elementary School", BuildingType.EDUCATION)
                .setTeaches(EducationType.ELEMENTARY)
                .setCapacity(600)                // places
                .setCashCost(17984)
                .setConstructionPoints(1800)
                .setConstructionMaterials(666)
                .setUpkeep(38)
                .setElectricityConsumption(22)
                .setWaterConsumption(6)
                .setJobs(JobType.NO_DIPLOMA, 8)
                .setJobs(JobType.DIPLOMA, 14)
                .setJobs(JobType.COLLEGE_BUSINESS, 26)
                .setLandSqFt(60000)
                .setRoadLoad(45)
                .setId(31);

        templates.add(elementarySchool);

        BuildingsTemplate middleSchool = new BuildingsTemplate("Middle School", BuildingType.EDUCATION)
                .setTeaches(EducationType.MIDDLE)
                .setCapacity(500)                // places
                .setCashCost(19188)
                .setConstructionPoints(2100)
                .setConstructionMaterials(710)
                .setUpkeep(44)
                .setElectricityConsumption(26)
                .setWaterConsumption(7)
                .setJobs(JobType.NO_DIPLOMA, 8)
                .setJobs(JobType.DIPLOMA, 12)
                .setJobs(JobType.COLLEGE_BUSINESS, 28)
                .setLandSqFt(70000)
                .setRoadLoad(50)
                .setId(32);

        templates.add(middleSchool);

        BuildingsTemplate highSchool = new BuildingsTemplate("High School", BuildingType.EDUCATION)
                .setTeaches(EducationType.HIGH)
                .setCapacity(900)                // places
                .setCashCost(44966)
                .setConstructionPoints(4200)
                .setConstructionMaterials(1665)
                .setUpkeep(96)
                .setElectricityConsumption(60)
                .setWaterConsumption(15)
                .setJobs(JobType.NO_DIPLOMA, 16)
                .setJobs(JobType.DIPLOMA, 24)
                .setJobs(JobType.COLLEGE_BUSINESS, 52)
                .setJobs(JobType.COLLEGE_ENGINEERING, 8)
                .setLandSqFt(140000)
                .setRoadLoad(110)
                .setId(33);

        templates.add(highSchool);

        BuildingsTemplate communityCollege = new BuildingsTemplate("Community College", BuildingType.EDUCATION)
                .setTeaches(EducationType.COLLEGE)
                .setCapacity(700)                // places
                .setCashCost(32957)
                .setConstructionPoints(6000)
                .setConstructionMaterials(1220)
                .setUpkeep(145)
                .setElectricityConsumption(90)
                .setWaterConsumption(20)
                .setJobs(JobType.NO_DIPLOMA, 18)
                .setJobs(JobType.DIPLOMA, 30)
                .setJobs(JobType.COLLEGE_BUSINESS, 40)
                .setJobs(JobType.COLLEGE_ENGINEERING, 10)
                .setJobs(JobType.UNIV_SCIENCE, 6)
                .setLandSqFt(180000)
                .setRoadLoad(150)
                .setId(34);

        templates.add(communityCollege);

        BuildingsTemplate university = new BuildingsTemplate("University", BuildingType.EDUCATION)
                .setTeaches(EducationType.UNIVERSITY)
                .setCapacity(2000)                // places
                .setCashCost(209837)
                .setConstructionPoints(26000)
                .setConstructionMaterials(7771)
                .setUpkeep(620)
                .setElectricityConsumption(380)
                .setWaterConsumption(90)
                .setJobs(JobType.NO_DIPLOMA, 70)
                .setJobs(JobType.DIPLOMA, 95)
                .setJobs(JobType.COLLEGE_BUSINESS, 110)
                .setJobs(JobType.COLLEGE_ENGINEERING, 30)
                .setJobs(JobType.UNIV_SCIENCE, 44)
                .setJobs(JobType.UNIV_POLICY, 12)
                .setLandSqFt(620000)
                .setRoadLoad(520)
                .setId(35);

        templates.add(university);

        BuildingsTemplate medicalSchool = new BuildingsTemplate("Medical School", BuildingType.EDUCATION)
                .setTeaches(EducationType.MEDICAL)
                .setCapacity(420)                // places
                .setCashCost(167873)
                .setConstructionPoints(44000)
                .setConstructionMaterials(6217)
                .setUpkeep(1150)
                .setElectricityConsumption(520)
                .setWaterConsumption(190)
                .setJobs(JobType.NO_DIPLOMA, 40)
                .setJobs(JobType.DIPLOMA, 70)
                .setJobs(JobType.COLLEGE_HEALTH, 120)
                .setJobs(JobType.UNIV_DOCTOR, 34)
                .setJobs(JobType.UNIV_SCIENCE, 16)
                .setLandSqFt(480000)
                .setRoadLoad(380)
                .setId(36);

        templates.add(medicalSchool);

        BuildingsTemplate lawSchool = new BuildingsTemplate("Law School", BuildingType.EDUCATION)
                .setTeaches(EducationType.LAW)
                .setCapacity(360)                // places
                .setCashCost(41941)
                .setConstructionPoints(20000)
                .setConstructionMaterials(1554)
                .setUpkeep(520)
                .setElectricityConsumption(190)
                .setWaterConsumption(42)
                .setJobs(JobType.NO_DIPLOMA, 14)
                .setJobs(JobType.DIPLOMA, 34)
                .setJobs(JobType.COLLEGE_BUSINESS, 40)
                .setJobs(JobType.UNIV_LAW, 26)
                .setJobs(JobType.UNIV_POLICY, 8)
                .setLandSqFt(190000)
                .setRoadLoad(170)
                .setId(37);

        templates.add(lawSchool);

        BuildingsTemplate businessSchool = new BuildingsTemplate("Business School", BuildingType.EDUCATION)
                .setTeaches(EducationType.BUSINESS)
                .setCapacity(480)                // places
                .setCashCost(59960)
                .setConstructionPoints(18000)
                .setConstructionMaterials(2220)
                .setUpkeep(460)
                .setElectricityConsumption(175)
                .setWaterConsumption(38)
                .setJobs(JobType.NO_DIPLOMA, 13)
                .setJobs(JobType.DIPLOMA, 30)
                .setJobs(JobType.COLLEGE_BUSINESS, 46)
                .setJobs(JobType.UNIV_FINANCE, 24)
                .setJobs(JobType.UNIV_POLICY, 6)
                .setLandSqFt(175000)
                .setRoadLoad(165)
                .setId(38);

        templates.add(businessSchool);

        BuildingsTemplate instituteOfTechnology = new BuildingsTemplate("Institute of Technology", BuildingType.EDUCATION)
                .setTeaches(EducationType.ENGINEERING)
                .setCapacity(520)                // places
                .setCashCost(41926)
                .setConstructionPoints(33000)
                .setConstructionMaterials(1552)
                .setUpkeep(780)
                .setElectricityConsumption(640)
                .setWaterConsumption(70)
                .setJobs(JobType.NO_DIPLOMA, 22)
                .setJobs(JobType.DIPLOMA, 44)
                .setJobs(JobType.COLLEGE_ENGINEERING, 64)
                .setJobs(JobType.UNIV_HIGHTECH_ENG, 28)
                .setJobs(JobType.UNIV_SCIENCE, 18)
                .setLandSqFt(300000)
                .setRoadLoad(240)
                .setId(39);

        templates.add(instituteOfTechnology);

        /* ------------------------------ SAFETY ------------------------------
           Police and prisons (2026-09-11). Jerus: "crime is a function of
           unemployment, and tight or under households, we need police, and
           also prison." See Crime for what they do; this is what they cost.

           CAPACITY IS OFFICERS OR CELLS. Coverage is staffed officers against
           Crime.FULL_OFFICERS_PER_100K a hundred thousand people - twice
           Canada's 180 (StatCan, 2025) - so a Police Station keeps a city of
           33,000 at full coverage and 67,000 at Canada's level. A cell holds
           one prisoner for a six-month sentence.

           OFFICERS ARE COLLEGE JOBS. The Police Foundations diploma is a
           college programme and the game has no protective-services type, so
           the nearest post is COLLEGE_BUSINESS; correctional officers are
           DIPLOMA. Civilians are a third of police staff in Canada (75,107
           officers beside about 37,000 civilians).

           WHAT THEY COST. Every template totals cashCost + 18 x materials, at
           the 60/40 split of work against material the other city buildings
           carry:
             Police Headquarters  $180M, Halifax's proposed HQ (2026)
             Police Station       $45M, a divisional station a quarter its size
             Jail                 $360M, 300 beds at Ontario's $1.2M a bed
                                  (2,500 beds for $3bn, 2026)
             Penitentiary         $600M, 600 beds at $1.0M, for its scale
           and to run, payroll plus upkeep: about $74,000 a prisoner a year in
           the jail and $85,000 in the penitentiary at full, against $67,000
           provincial (2011-12) and $115,000 federal (2016). The upkeep is
           what feeds them - a prisoner buys nothing in the shops.
           ------------------------------------------------------------------ */

        BuildingsTemplate policeStation = new BuildingsTemplate("Police Station", BuildingType.SAFETY)
                .setSafety(SafetyType.POLICE)
                .setCapacity(120)                // officers
                .setCashCost(27000)
                .setConstructionPoints(5000)
                .setConstructionMaterials(1000)
                .setUpkeep(260)
                .setElectricityConsumption(60)
                .setWaterConsumption(6)
                .setJobs(JobType.NO_DIPLOMA, 12)
                .setJobs(JobType.DIPLOMA, 40)
                .setJobs(JobType.COLLEGE_BUSINESS, 120)
                .setLandSqFt(60000)
                .setRoadLoad(150)
                .setId(42);

        templates.add(policeStation);

        BuildingsTemplate policeHeadquarters = new BuildingsTemplate("Police Headquarters", BuildingType.SAFETY)
                .setSafety(SafetyType.POLICE)
                .setCapacity(500)                // officers
                .setCashCost(108000)
                .setConstructionPoints(22000)
                .setConstructionMaterials(4000)
                .setUpkeep(1200)
                .setElectricityConsumption(300)
                .setWaterConsumption(30)
                .setJobs(JobType.NO_DIPLOMA, 40)
                .setJobs(JobType.DIPLOMA, 170)
                .setJobs(JobType.COLLEGE_BUSINESS, 500)
                .setJobs(JobType.UNIV_SCIENCE, 20)
                .setJobs(JobType.UNIV_LAW, 8)
                .setJobs(JobType.UNIV_POLICY, 12)
                .setLandSqFt(180000)
                .setRoadLoad(520)
                .setId(43);

        templates.add(policeHeadquarters);

        BuildingsTemplate jail = new BuildingsTemplate("Jail", BuildingType.SAFETY)
                .setSafety(SafetyType.PRISON)
                .setCapacity(300)                // cells
                .setCashCost(216000)
                .setConstructionPoints(38000)
                .setConstructionMaterials(8000)
                .setUpkeep(650)
                .setElectricityConsumption(420)
                .setWaterConsumption(95)
                .setJobs(JobType.NO_DIPLOMA, 40)
                .setJobs(JobType.DIPLOMA, 190)
                .setJobs(JobType.COLLEGE_HEALTH, 14)
                .setJobs(JobType.COLLEGE_BUSINESS, 16)
                .setJobs(JobType.UNIV_POLICY, 4)
                .setLandSqFt(450000)
                .setRoadLoad(70)
                .setId(44);

        templates.add(jail);

        BuildingsTemplate penitentiary = new BuildingsTemplate("Penitentiary", BuildingType.SAFETY)
                .setSafety(SafetyType.PRISON)
                .setCapacity(600)                // cells
                .setCashCost(360000)
                .setConstructionPoints(62000)
                .setConstructionMaterials(13333)
                .setUpkeep(1500)
                .setElectricityConsumption(900)
                .setWaterConsumption(200)
                .setJobs(JobType.NO_DIPLOMA, 90)
                .setJobs(JobType.DIPLOMA, 400)
                .setJobs(JobType.COLLEGE_HEALTH, 36)
                .setJobs(JobType.COLLEGE_BUSINESS, 44)
                .setJobs(JobType.UNIV_DOCTOR, 4)
                .setJobs(JobType.UNIV_POLICY, 16)
                .setLandSqFt(1300000)
                .setRoadLoad(140)
                .setId(45);

        templates.add(penitentiary);

        /* =====================================================================
           THE BANK

           One building, and every loan in the city goes through it - the
           sectors', the treasury's bonds and the households' credit alike. What
           it buys is not a service the way a clinic or a school is: it is the
           price of borrowing. A city with no branch pays eighteen points over
           the odds on everything it owes, because a bank with no capacity is
           infinitely strained and Bank.ratePremium() falls straight out of
           that - no special case for "there is no bank".

           It is also the first real employer of UNIV_FINANCE. There were
           sixty-four such posts in the whole building set before this and
           twenty-four of them were in a research institute, which made a
           finance degree a qualification for almost nothing.
           ===================================================================== */
        BuildingsTemplate bank = new BuildingsTemplate("Commercial Bank", BuildingType.COMMERCIAL);
        bank.setCapacity(0);
        bank.setCashCost(5681);
        bank.setConstructionPoints(4200);
        bank.setConstructionMaterials(211);
        bank.setUpkeep(190);
        bank.setElectricityConsumption(60);
        bank.setWaterConsumption(12);
        bank.setLandSqFt(45000);
        bank.setRoadLoad(60);
        /* =====================================================================
           TWENTY-NINE STAFF - 278, then 69, now the figure the world has

           The 278 was a building nobody had costed. The 69 was the first honest
           attempt at it, made the day the bank got an income statement, and it
           was measured against ONE MONTH of one branch. Over 1,202 months the
           answer is much harsher and it is the largest single number in the
           bank's life:

             interest earned      18,055,807
             payroll             -32,461,669     180% of every dollar it earned
             write-offs          -18,449,110     102%
             deposit interest     -2,945,257      16%
             wholesale funding      -239,812       1%
             = lifetime profit   -36,040,041

           A real bank's ENTIRE non-interest expense is 55-65% of revenue, of
           which payroll is a bit over half. This bank's wage bill alone was
           nearly twice its revenue, and 926 of the 944 months in which no loan
           was written off at all still ran a loss - 98%. The bankruptcies are
           the visible shocks; the machine underneath never worked at any book
           size it was allowed to reach.

           THREE ANCHORS, and they only reconcile if BOTH constants move:

             STAFF PER BRANCH. US commercial banking employs about 2.05m people
             across roughly 71,000 branches - 29 each, and that figure already
             carries every head-office, IT and back-office job spread over the
             counters, which is the right comparison because this game has one
             banking building and not three. 69 -> 29 is 2.4x.

             DEPOSITS PER BRANCH. About $18tn across the same 71,000 branches is
             $253M each. Bank.DEPOSITS_PER_BRANCH said $60M. That is 4.2x, and
             it moved with this.

             ASSETS PER EMPLOYEE. US banking holds about $24tn against those
             2.05m people - $11.7M each. This city ran $12.28bn of deposits
             across 150 branches of 69 - $1.19M each. That is 9.8x, and
             2.4 x 4.2 = 10.1. The staffing anchor and the deposit anchor
             multiply out to the productivity one, which is what says the two of
             them together are the whole of the gap rather than either alone.

           PAID_IN_PER_BRANCH was checked at the same time and left where it
           was: $2.2tn of US bank equity over 71,000 branches is $31M, and it
           says $32M. It was already right - see the note there.

           Still a substantial white-collar employer, and still an employer whose
           business case needs a loan book worth banking - which is the decision
           this building is meant to be.
           ===================================================================== */
        bank.setJobs(JobType.NO_DIPLOMA, 2);
        bank.setJobs(JobType.DIPLOMA, 9);
        bank.setJobs(JobType.COLLEGE_BUSINESS, 13);
        bank.setJobs(JobType.UNIV_FINANCE, 4);
        bank.setJobs(JobType.UNIV_LAW, 1);
        bank.setId(40);
        templates.add(bank);

        /* =====================================================================
           BUSINESS SERVICES - THE THREE THE WORLD PAYS FOR

           The first private buildings in the game whose customer is not in the
           city. Everything else a business builds here sells to the people who
           live here, or sells a physical good dug out of the ground under them;
           these sell a month of somebody's work to a client somewhere else,
           which is the only kind of job creation that does not need the city to
           be bigger first.

           Every figure is sourced except one, and the exception is named. Per
           seat: 165 sq ft of floor (JLL's 2025 benchmark), 250 sq ft of LOT by
           analogy with the Police Headquarters at 240 per job - the same kind
           of building, multi-storey and mostly parking - 0.80 power units
           (EIA CBECS: 6,900 kWh a year per office worker, so 0.79 kW
           continuous) and 0.80 of road load, commuters with no freight. A
           three-hundred-seat centre draws a THIRD of the Steel Foundry's 750
           for eight times the jobs, which is this sector in one line.

           Cost is $550 a square foot all-in: fit-out is $307 sourced (Cushman
           & Wakefield 2026 Toronto, including the 22% furniture line; JLL's
           guide agrees at $295 for medium quality) and THE SHELL IS AN ESTIMATE
           at about $245. Every fit-out guide in existence prices the tenant
           interior only, on a building that already stands, and no sourced
           Canadian base-building figure could be found. That is the one number
           here to revisit. Split 40% material at $18,000 a unit, the standing
           convention for a building rather than a plant, and points at $8.4k of
           all-in cost each, the median across the buildings that already carry
           a building's 40% share.

           Three hundred seats is Nova Scotia's own average: 15,693 call-centre
           workers across 53 centres in 2007 is 296 apiece, and 3,700 across 13
           in 1999 is 285. A hundred and twenty for the engineering office is
           the middle of what Stantec's 32,000 staff across hundreds of offices
           implies - an inference, not a benchmark.

           No upkeep: this sector pays the same 1%-a-year maintenance every
           other building pays, and upkeep is the city's own line.
           ===================================================================== */
        BuildingsTemplate contactCentre = new BuildingsTemplate("Contact Centre", BuildingType.BUSINESS_SERVICES)
                .setCapacity(300)                   // seats
                .setCashCost(16335)
                .setConstructionPoints(3240)
                .setConstructionMaterials(605)
                .setUpkeep(0)
                .setSector("Business Services")
                .makes(Good.SUPPORT_WORK, 300)      // seat-months a month
                .setElectricityConsumption(240)
                .setWaterConsumption(6)
                .setLandSqFt(75000)
                .setRoadLoad(240)
                .setJobs(JobType.NO_DIPLOMA, 255)   // agents
                .setJobs(JobType.DIPLOMA, 36)       // team leads
                .setJobs(JobType.COLLEGE_BUSINESS, 9)
                .setId(46);

        templates.add(contactCentre);

        BuildingsTemplate sharedServices = new BuildingsTemplate("Shared Services Centre", BuildingType.BUSINESS_SERVICES)
                .setCapacity(300)                   // seats
                .setCashCost(16335)
                .setConstructionPoints(3240)
                .setConstructionMaterials(605)
                .setUpkeep(0)
                .setSector("Business Services")
                .makes(Good.BACK_OFFICE_WORK, 300)
                .setElectricityConsumption(240)
                .setWaterConsumption(6)
                .setLandSqFt(75000)
                .setRoadLoad(240)
                .setJobs(JobType.DIPLOMA, 90)
                .setJobs(JobType.COLLEGE_BUSINESS, 180)
                /* UNIV_POLICY, not UNIV_FINANCE, and the first draft had finance.
                   Finance is GATED on a Business School, so thirty posts of it
                   in an ungated building is a trap twice over: the seats can
                   never be staffed, which drags the whole centre's fill rate,
                   AND the licence premium takes that wage to four times the
                   band because posts outrun holders. Measured over 333 years:
                   payroll went to 95% of revenue and the sector went bust
                   nineteen times. Administration, which needs no licence, is
                   also the truer description of a shared-services back office. */
                .setJobs(JobType.UNIV_POLICY, 30)
                .setId(47);

        templates.add(sharedServices);

        /* ---------------------------------------------------------------------
           And the one with a gate on it. Seventy-eight licensed engineers, at
           senior professional pay, so this is also the most expensive payroll
           in the sector - and the licence premium (posts over holders, up to
           four times the band) bites first and hardest on a city that has no
           Institute of Technology.

           The premium alone is not enough to stop it being built, though,
           because the investment planner costs a building at nameplate and
           ignores the fill rate. So the licence is a hard refusal: see
           BuildingsTemplate.requiresLicence and Game.LICENCE_COVER_TO_OPEN.
           --------------------------------------------------------------------- */
        BuildingsTemplate engineeringOffice = new BuildingsTemplate("Engineering Services Office", BuildingType.BUSINESS_SERVICES)
                .setCapacity(120)                   // seats
                .setCashCost(6534)
                .setConstructionPoints(1300)
                .setConstructionMaterials(242)
                .setUpkeep(0)
                .setSector("Business Services")
                .makes(Good.ENGINEERING_WORK, 120)
                .setElectricityConsumption(96)
                .setWaterConsumption(2)
                .setLandSqFt(30000)
                .setRoadLoad(96)
                .setJobs(JobType.DIPLOMA, 12)
                .setJobs(JobType.COLLEGE_ENGINEERING, 30)
                .setJobs(JobType.UNIV_HIGHTECH_ENG, 78)
                .setRequiresLicence(JobType.UNIV_HIGHTECH_ENG)
                .setId(48);

        templates.add(engineeringOffice);

        /* =====================================================================
           MANUFACTURING - WHAT THE CITY MAKES OUT OF ITS OWN STEEL

           The ninth sector's three plants, and the first buildings in the game
           that BUY a traded good another sector makes. Everything here follows
           from two world prices and one wage schedule; nothing is a round
           number chosen to make a plant work.

           HOW TO READ THE ARITHMETIC, per plant, at founding (exchange rate 1)
           and with steel at the 1.284 import ceiling, which is what a city with
           no mills of its own pays:

                                 sells     steel      wages    left over
             Fabrication Shop    $2,652k    61.0%      20.4%      18.5%
             Fabrication Works   $6,630k    61.0%      16.7%      22.2%
             Machine Works       $1,620k    20.7%      49.1%      30.2%

           Every one of those is before power, water, repairs, property tax and
           interest, which together come to two or three points. A Steel Foundry
           on imported scrap leaves 31% by the same measure and MiningCheck
           calls that an electric-arc mill's margin, so the two fabrication
           plants are DELIBERATELY thinner than an ordinary plant on imported
           steel - and both go to 29% and 33% on local steel in the middle of
           the band, and to 39% and 43% on steel at its export floor. That
           spread is the mine-and-mill story one link up the chain and it is
           what the sector is for.

           THE TWO ARE BOUNDED BY DIFFERENT THINGS, which is the design and not
           a detail - see sectors.Manufacturing. Fabrication lives and dies on
           the steel price; the machine works lives and dies on the wage bill
           and the currency, like a contact centre but at a machinist's wage.

           WHERE THE FIGURES COME FROM

           Output per head. US Census NAICS 332, fabricated metal product
           manufacturing: $442.6bn of shipments over 1.44m employees is $307k a
           head a year. NAICS 333, machinery: $364k. The ratio between them,
           1.19, is the ratio between the Fabrication Shop's $19.6k a head a
           month and what a Machine Works would bill at the same productivity -
           and the Machine Works is set deliberately BELOW that, at $9.4k,
           because a plant in this game buys steel and nothing else. A real
           machine builder buys castings, motors, bearings and electronics from
           suppliers who are somewhere else; this one has no suppliers, so it
           employs the people who would have been at them. That is why its wage
           bill is half of revenue where a fabricator's is a fifth, and it is
           the honest consequence of the model rather than a thumb on it.

           Payroll share. NAICS 332 payroll is 19-22% of shipments and NAICS 333
           is about 18.5% - the Fabrication Shop's 20.4% and the Works' 16.7%
           are those numbers. The Machine Works' 49% is not, for the reason
           above, and it is the number to watch in a playtest.

           Capital, and this is the number that was wrong first. A structural
           fabrication shop - a clear-span bay with overhead cranes, a CNC beam
           line, welding and blast-and-paint - is quoted at $15-25m for a
           hundred-odd staff, and $16.0m was written down here on that basis. At
           $16.0m a Fabrication Shop clears 3.1% of its build cost a MONTH even
           on imported steel, which is a 37% annual return on the most expensive
           thing an investor can buy, and the first fixture that ran with it put
           up eleven thousand tonnes of capacity in thirty-six months in a town
           of seven hundred houses. It was the most profitable building in the
           game by a factor of three.

           Capital at ONE TIMES ANNUAL REVENUE, and the brake is the GROUND
           rather than the price. Jerus's call, 2026-09-13, taken against four
           measured calibrations over sixteen seeds of 333 years each:

                                median pop    smallest    price index   currency
             no ninth sector        16,834      13,764        0.799       0.603
             1x revenue            190,228     123,489        0.967       0.780
             2x revenue             89,700      65,938        0.888       0.675
             3x revenue             65,730      57,035        0.835       0.606

           WHAT THAT TABLE ACTUALLY SAYS, and it is not what it was run to
           find out: the price does not decide whether a city makes it, only
           how big it gets. Every calibration takes off in 16 of 16. Making the
           plants dearer does not restore the possibility of failure, it
           COMPRESSES the outcome - at 3x every one of sixteen cities lands
           between 57,035 and 80,003. Once a city can sell labour-embodied
           goods to somebody who is not here, the plateau has no bite, and that
           is the finding rather than the dial.

           So the dial was set where the ECONOMY reads best: at 1x the price
           index settles at 0.967 and the currency at 0.780, which is the
           closest this project has come to price stability and to a currency
           that stops appreciating - both of them standing problems with their
           own write-ups (claude/why-there-is-no-inflation.md). A sector that
           fixes two of those and makes the city five times bigger is worth
           having at its strong setting.

           AND THE LAND IS WHAT LIMITS IT. Jerus again: gate it on ground, so
           the city has to choose between a factory and a neighbourhood. A
           fabricator is the honest place for that - a shop is a clear-span bay
           with a LAYDOWN YARD around it, twenty tonnes of beam waiting to be
           cut and twenty more waiting for a truck, and real structural
           fabricators sit on ten to thirty acres. At 400 sq ft a tonne a month
           the two sheds become the most ground-hungry buildings in the game
           per post - 3,556 and 4,167 sq ft against a Steel Foundry's 2,368 and
           a Contact Centre's 250. The Machine Works is the opposite and is
           meant to be: machining happens indoors, so it is DENSER than the
           foundry at 867, and pays for that in power instead at 4.7 kW a post.

           MEASURED, and the gate is a price rather than a wall: land use ends
           at 88-91% either way, but the ground under the marginal plant costs
           what a mature city charges for it. Median population 190,228 falls
           to 127,651, the cost of ground per person of housing goes from 10.59
           to 11.97 - so the tenants feel it, which is the choice being asked
           for - and the price index holds at 0.975. Doubling the land again
           takes the median to 86,580 and the index back down to 0.931, and
           puts housing findings in eleven seeds instead of three: that is the
           gate overshooting into the thing it was meant to price.

                                build cost      land   imported steel   mid band
             Fabrication Shop      $31.8m     480,000       1.55%/mo     2.41%/mo
             Fabrication Works     $79.6m   1,150,000       1.86%/mo     2.72%/mo
             Machine Works         $19.4m     150,000       2.51%/mo     2.81%/mo

           Fabrication is still the one that wants a mill next door - the gap
           from imported steel to local is most of a point, where the machine
           works, whose steel is only a fifth of what it spends, barely moves.
           That is the mine-and-mill story one link further up and the first
           reason in this game to put three things near each other.

           Split 30% material for the two sheds and 25% for the machine plant,
           which is the plant convention rather than the building one (the Steel
           Foundry is 25%). Points at $13.0k for the sheds and $17.0k for the
           machine plant, between the Contact Centre's $8.4k and the Foundry's
           $21.4k, which is where a building that is half shed and half
           equipment belongs. The LAND is not in any of that: a lot is bought
           at the going rate on the day the plant is ordered, and taxed every
           month after, which is exactly why it works as the brake.

           Steel in. 1.05 tonnes a tonne fabricated - real shop yield is 92-97%
           - and 1.45 for machinery, where the swarf goes.

           FILED UNDER HEAVY_INDUSTRY, not under a category of its own, and
           that is a change from how Business Services was added. Business
           Services got its own row because the thing a player has to
           understand about it is the one thing it does not share with a mill.
           A fabrication shop shares everything with a mill except the input:
           it is heavy, it is dirty, it is on the same kind of lot, it sells to
           the same foreign buyers, and it is the building a player is looking
           for when they go and look at the mills. BuildingType is only the
           build-menu group and the tax band - the property tax is struck per
           SECTOR, not per type (TaxPolicy.effectiveMonthlyPropertyRate) - so
           sharing the category costs nothing and puts the three plants on the
           shelf next to the two mills that feed them.
           ===================================================================== */
        BuildingsTemplate fabricationShop = new BuildingsTemplate("Fabrication Shop", BuildingType.HEAVY_INDUSTRY)
                .setCashCost(22260).setConstructionPoints(2445).setConstructionMaterials(530)
                .setSector("Manufacturing")
                .makes(Good.FABRICATED_STEEL, 1200).uses(Good.STEEL, 1260)
                .setElectricityConsumption(190).setWaterConsumption(18)
                .setLandSqFt(480000).setRoadLoad(200)
                .setJobs(JobType.NO_DIPLOMA, 68)
                .setJobs(JobType.DIPLOMA, 62)
                .setJobs(JobType.COLLEGE_ENGINEERING, 5).setId(49);

        templates.add(fabricationShop);

        /* ---------------------------------------------------------------------
           The machine plant, and the one that does NOT want a yard. Eight
           hundred and twenty kilowatts against the shop's hundred and ninety -
           4.7 a post, where a contact centre draws 0.8 and a steel foundry
           19.7 - because machine tools, heat treatment and compressed air run
           all shift, and a quarter of the shop's ground per POST, because all
           of it happens under a roof. It is the rung a city with no room left
           can still build.

           Thirty NO_DIPLOMA against a hundred and five
           DIPLOMA and thirty-four COLLEGE_ENGINEERING, which is the whole
           difference from the shop next door: this one wants machinists and
           technicians, so it is the rung a city with a college can build and
           the fabrication shop is the one it can build first.

           UNIV_SCIENCE and not UNIV_HIGHTECH_ENG for the four design posts,
           because high-tech engineering is GATED on an Institute of Technology
           and gated posts in an ungated building is the trap the Shared
           Services Centre paid for: the seats can never be staffed, which drags
           the whole plant's fill rate, and the licence premium takes that wage
           to four times the band because posts outrun holders. Measured over
           333 years it put payroll at 95% of revenue and sent that sector bust
           nineteen times. Applied science needs no licence, and a production
           engineer in a machine shop is not a P.Eng. signing drawings.
           --------------------------------------------------------------------- */
        BuildingsTemplate machineWorks = new BuildingsTemplate("Machine Works", BuildingType.HEAVY_INDUSTRY)
                .setCashCost(14580).setConstructionPoints(1143).setConstructionMaterials(270)
                .setSector("Manufacturing")
                .makes(Good.MACHINERY, 180).uses(Good.STEEL, 261)
                .setElectricityConsumption(820).setWaterConsumption(24)
                .setLandSqFt(150000).setRoadLoad(190)
                .setJobs(JobType.NO_DIPLOMA, 30)
                .setJobs(JobType.DIPLOMA, 105)
                .setJobs(JobType.COLLEGE_ENGINEERING, 34)
                .setJobs(JobType.UNIV_SCIENCE, 4).setId(50);

        templates.add(machineWorks);

        /* ---------------------------------------------------------------------
           And the same trade at scale. Two and a half times the shop's output
           on two times its people, which is what a beam line and a bigger
           crane bay buy you; the payroll share falls from 20.4% to 16.7% and
           that is the whole of the economy of scale.

           TWO HUNDRED AND SEVENTY-SIX POSTS on twenty-six acres, which makes
           this a late-game object by arithmetic rather than by a gate: the
           building is $79.6m and the ground under it is whatever a city that
           has already grown charges for eleven and a half city blocks. The
           staffing floor still does more work here than anywhere in the game
           bar the Contact Centre - see Sector.MIN_STAFFABLE_TO_ORDER for what
           happened the last time a three-hundred-post building could be
           ordered into a village.
           --------------------------------------------------------------------- */
        BuildingsTemplate fabricationWorks = new BuildingsTemplate("Fabrication Works", BuildingType.HEAVY_INDUSTRY)
                .setCashCost(55690).setConstructionPoints(6120).setConstructionMaterials(1326)
                .setSector("Manufacturing")
                .makes(Good.FABRICATED_STEEL, 3000).uses(Good.STEEL, 3150)
                .setElectricityConsumption(440).setWaterConsumption(40)
                .setLandSqFt(1150000).setRoadLoad(480)
                .setJobs(JobType.NO_DIPLOMA, 142)
                .setJobs(JobType.DIPLOMA, 122)
                .setJobs(JobType.COLLEGE_ENGINEERING, 11)
                .setJobs(JobType.UNIV_SCIENCE, 1).setId(51);

        templates.add(fabricationWorks);

        /* =====================================================================
           AGRICULTURE - THE GROUND UNDER THE LOAF

           The tenth sector's three, and the first buildings in this game whose
           cost is not the building. A Mixed Farm's shed and machinery come to
           $1.19m; the twenty-four city blocks under it come to $3.1m in a young
           city and $144m in a grown one. Everything interesting about this
           sector is that second number.

           HOW TO READ IT, per building, with crops in the middle of their band:

                                 grows      posts    power    ground    a month
             Mixed Farm            500 t        5     25 kW    55 ac    $144k
             Grain Farm          1,200 t        8     60 kW   165 ac    $346k
             Greenhouse Complex  2,400 t       45  2,500 kW     9 ac    $691k

           FIVE PEOPLE ON FIFTY-FIVE ACRES, AND FORTY-FIVE ON NINE. That
           contrast is the sector in one line and both halves of it are real: a
           mixed field farm of this size in Nova Scotia is one or two full-time
           people and some seasonal help, and a hectare of Dutch glass is about
           one person per quarter acre, picking by hand. Five and eight are
           already generous against the field figures; forty-five is what the
           glass actually takes.

           The first cut had eighteen and twenty-six, which was six times a real
           farm's crew, and it is what killed the sector in its first measured
           run - a field cannot carry an industrial payroll on a commodity
           price. Labour is now about a seventh of what a field sells and power
           a five-hundredth, which is Jerus's brief exactly ("labour and energy
           cheap") and is also what farming is: roughly 9% of a Canadian farm's
           cash costs are wages and 4% fuel and electricity. What a farm spends
           is ground and machinery, and the game charges it for the ground
           twice: once at the going rate when the lot is bought, and again every
           month as property tax on what that lot would fetch.

           WHICH MAKES THE SECTOR A CLOCK. At a young city's $1.30 a square foot
           all three clear about two and a half percent of what they cost a
           month, which is the best return on the board. At a grown city's $60:

                                 early     late      and why
             Mixed Farm          2.49%    0.074%     twenty-four blocks of ground
             Grain Farm          2.35%    0.075%     seventy-two blocks
             Greenhouse Complex  2.57%    0.96%      four blocks

           Thirteen times, and it is the whole late game of this sector. A field
           is the cheap way to grow food in a town and an impossible way to grow
           it in a city; glass is the reverse. That is the Netherlands, and
           Leamington, and every acre under plastic anybody ever put next to a
           city instead of away from one.

           WHAT A CITY CAN ACTUALLY FEED ITSELF. A city of 130,000 eats 130,000
           units of food a month, which is 20,000 tonnes of crops at six and a
           half units to the tonne. In fields that is forty Mixed Farms on 96
           million square feet, against the 150 million a city that size owns -
           so a city CANNOT grow its own dinner without giving up most of itself,
           and the crop market has an import ceiling for exactly that reason. In
           glass the same 20,000 tonnes is thirteen complexes on five million
           square feet, or three percent of the city. The whole sector is that
           comparison.

           THE YIELDS ARE COMPRESSED AND THERE IS NO HONEST WAY ROUND IT. A
           Mixed Farm grows 106 tonnes an acre a year where a real mixed farm
           grows two to four. Every plant in this game is compressed - a Steel
           Foundry does 1,200 tonnes a month on two acres where a real mini-mill
           needs fifty - but heavy industry is compressed about twenty-five times
           and this is compressed about fifty. It has to be, because agriculture
           is a REGIONAL land use and this game's map is a city: at real
           intensity one farm would be four hundred blocks and feed two hundred
           people. Compressed, the decision survives - fields or houses, and how
           much of your dinner you are willing to buy from strangers - and the
           decision is the point.

           Water is small on purpose and it is the one figure here that is not
           what it looks like. Irrigation is most of the fresh water anybody uses
           anywhere, but a farm takes it from a river or a well; it does not buy
           it from the city at $5 a thousand gallons. What these three draw is
           what they take off the mains - the yard, the dairy, the packing shed,
           and for the greenhouse a recirculating system that really is on
           treated water.
           ===================================================================== */
        BuildingsTemplate mixedFarm = new BuildingsTemplate("Mixed Farm", BuildingType.AGRICULTURE)
                .setCashCost(240).setConstructionPoints(25).setConstructionMaterials(4)
                .setSector("Agriculture")
                /*
                 * A MIXED FARM IS CALLED MIXED BECAUSE IT KEEPS ANIMALS.
                 * It made crops and nothing else until 2026-09-16, which made
                 * it a small Grain Farm rather than a different trade. Its crop
                 * line falls from 125 to 69 because most of what it grows now
                 * goes into its own animals instead of to market - the revenue
                 * is the same, the goods are not.
                 */
                .makes(Good.CROPS, 69)
                .makes(Good.DAIRY_EGGS, 5000)
                .makes(Good.MEAT, 1400).setStock(15000)
                .setElectricityConsumption(8).setWaterConsumption(30)
                .setLandSqFt(600000).setRoadLoad(8)
                .setJobs(JobType.NO_DIPLOMA, 2)
                .setJobs(JobType.DIPLOMA, 1).setId(52);

        templates.add(mixedFarm);

        /* ---------------------------------------------------------------------
           And the same thing at three times the size on two and a half times as
           few people an acre, which is what a combine is for. Seventy-two city
           blocks - by a wide margin the largest footprint in the game, past the
           coal station's twenty - for twenty-six posts and sixty kilowatts. A
           grain farm is the purest statement this sector makes: it is ground,
           and almost nothing else.
           --------------------------------------------------------------------- */
        BuildingsTemplate grainFarm = new BuildingsTemplate("Grain Farm", BuildingType.AGRICULTURE)
                .setCashCost(1200).setConstructionPoints(100).setConstructionMaterials(17)
                .setSector("Agriculture")
                .makes(Good.CROPS, 500).setStock(1500)
                .setElectricityConsumption(25).setWaterConsumption(70)
                .setLandSqFt(2400000).setRoadLoad(18)
                .setJobs(JobType.NO_DIPLOMA, 3)
                .setJobs(JobType.DIPLOMA, 1).setId(53);

        templates.add(grainFarm);

        /* ---------------------------------------------------------------------
           And the one that escapes the clock. Twenty times a field's yield off
           an acre, paid for in glass, people and electricity: $13.5m of
           structure where a Mixed Farm is $1.2m, forty-five posts against
           eighteen, and two and a half megawatts against twenty-five kilowatts -
           a hundred times the power for three times the crop, which is heating
           and lighting and is the actual trade a greenhouse makes.

           Real Dutch and Ontario glass runs about $1.5m an acre to build and
           uses roughly 400 kWh a square metre a year between heat and light.
           Nine acres at those figures is this building.

           It is never the cheap way to grow anything. It is simply the only way
           left once the ground is worth more than the harvest, and a city that
           has built its last field will build these instead - which is the
           sector having a late game at all.
           --------------------------------------------------------------------- */
        BuildingsTemplate greenhouse = new BuildingsTemplate("Greenhouse Complex", BuildingType.AGRICULTURE)
                .setCashCost(5600).setConstructionPoints(410).setConstructionMaterials(104)
                .setSector("Agriculture")
                /*
                 * GLASS EXISTS TO GROW SALAD, and a Greenhouse Complex making
                 * "crops" was the same abstraction as a Textile Mill making
                 * "food". Vegetables and fruit, seven to three by value, at the
                 * revenue it always had.
                 */
                .makes(Good.VEGETABLES, 154000)
                .makes(Good.FRUIT, 47500).setStock(300000)
                .setElectricityConsumption(940).setWaterConsumption(150)
                .setLandSqFt(150000).setRoadLoad(34)
                .setJobs(JobType.NO_DIPLOMA, 12)
                .setJobs(JobType.DIPLOMA, 4)
                .setJobs(JobType.COLLEGE_ENGINEERING, 1).setId(54);

        templates.add(greenhouse);

        /* =====================================================================
           THE LIVESTOCK FARM, AND WHY IT IS THE DEAREST THING IN THE CATALOGUE
           PER ACRE.

           Jerus's rule for the whole of agriculture: "just purely $ math, if its
           more cost effective then it feeds itself... just that it usually
           isnt". So nothing here is aimed at a self-sufficiency figure. The
           yields are struck off real revenue per acre relative to grain, and
           the capital is struck off what a real dairy operation costs per acre -
           roughly FIVE TIMES a grain farm's, because a milking parlour, the
           herd and its quota are most of the money and the ground is not.

           What falls out: revenue of about 840 a month against a build cost of
           13,000, which is 6.5% - against the Grain Farm's 18% and about the
           Greenhouse's 7%. Livestock is the worst business per dollar on the
           menu, which is the answer the arithmetic gives rather than one that
           was chosen, and it is why a city usually buys its meat from the world.

           PASTURE-FED, so it takes no feed line. That is honest for the
           Maritimes, and it is also what keeps Agriculture from being a sector
           that makes CROPS and uses CROPS at the same time - which the market
           would clear, but which would hand the greenhouse's vegetables a share
           of the cows' feed bill through the joint-cost split. The land IS the
           feed bill here, and there is a lot of it.
           ===================================================================== */
        BuildingsTemplate livestockFarm = new BuildingsTemplate("Livestock Farm", BuildingType.AGRICULTURE)
                .setCashCost(13000).setConstructionPoints(1080).setConstructionMaterials(184)
                .setSector("Agriculture")
                .makes(Good.DAIRY_EGGS, 210000)
                .makes(Good.MEAT, 30000).setStock(400000)
                .setElectricityConsumption(120).setWaterConsumption(600)
                .setLandSqFt(5227200).setRoadLoad(40)
                .setJobs(JobType.NO_DIPLOMA, 9)
                .setJobs(JobType.DIPLOMA, 3).setId(55);

        templates.add(livestockFarm);

        /* =================================================================
           THE ELEVENTH SECTOR'S THREE PLANTS (2026-09-16)

           Jerus: "a new sector which makes the ready meals, processed meals,
           snacks, drinks, and cooking fats". Those five are 16.3kg of the
           reference basket's 50 and $51.40 of its $161.75 - a third of the
           shelf - and every gram of them was imported until today.

           THE YIELDS ARE STRUCK OFF REAL PROCESS FIGURES, then the crew is
           struck so the margin lands in the band every other plant occupies.
           That order matters, and the bakeries' own note says why: a real
           wheat-to-bread yield gave both ovens a 60% margin and made them the
           most profitable buildings in the game. Yield first, margin second.

           AND THE CAPITAL IS ONE YEAR OF REVENUE, which is Manufacturing's
           measured calibration rather than the ovens'. Priced off the
           Industrial Bakery instead - twenty-eight months of revenue - every
           one of these was refused by servicesItsOwnDebt() for four thousand
           months running: "not even one would cover its interest". A real food
           manufacturer carries five to eight months of revenue in assets, so
           twelve is generous and twenty-eight was the outlier. The ovens are
           the thing out of line here, not these.

           EACH PLANT SERVES ABOUT TWO AND A HALF THOUSAND PEOPLE, deliberately
           small. An
           Industrial Bakery feeds sixty thousand, which is most of a playtest
           city, and a plant a young city cannot fill is a plant a young city
           never builds - the Grain Farm's problem, one sector over. Three
           small rungs let a city of two thousand buy its first one and a city
           of a hundred thousand buy forty - the way a Convenience Store covers
           four hundred and eighty people rather than the whole town.

           AND THIS SECTOR CANNOT EXPORT ITS WAY OUT, which is the fact that
           forced the size. The world sells raw meat at $7.00 and buys processed
           meat at $5.60: a plant that imports its input at the ceiling and
           ships its output at the floor earns 27 CENTS A TONNE before wages.
           It is the mirror of Business Services, whose customer is only ever
           foreign - this one's customer is only ever here. A plant bigger than
           the city it stands in has nowhere to put the surplus, which is why
           the first version, sized at forty thousand people, ran a loss every
           month from the month it opened.

           EACH PLANT HAS ITS OWN COST STORY, which is the whole point of three
           rather than one - the same "two brakes" argument Manufacturing made:

             Meat Works      46% of revenue is MEAT. The meat price decides it.
             Snack & Oils    19% crops, the rest labour and the fryers.
             Bottling Plant   4% crops, and the first real water bill in the
                              game - a drink is mostly water.

           SIZED BY REVENUE PER WORKER, and the first three passes were not.

           The measure that matters for a maker in this economy is what one
           plant sells a month divided by how many people run it, because the
           wage is the one cost that follows the city up. Here is the game's
           own catalogue, at mid-band prices, on the day these were written:

             Bakery                $17.8k a head    pay 22% of revenue
             Industrial Bakery     $13.5k           pay 28%
             Steel Foundry         $33.6k           pay 12%
             Fabrication Shop      $19.6k           pay 20%
             Iron Mine             $11.5k           pay 33%

           The first calibration of these three came in at $10.7k, $6.3k and
           $6.0k a head - pay at 35%, 59% and 61% of revenue. A Bottling Plant
           was six people producing thirty tonnes a month, which is a tonne a
           day: a craft operation, not a plant. It cleared $12k a month at a
           founding city's wages and lost $20k at a grown one's, and the run
           showed exactly that - built at month 271, under water by month 288,
           the sector bankrupt with the plant still standing.

           These three are at $17.1k, $16.8k and $18.0k a head, pay 21-22% at
           mid-band and 28% at the export floor. The nameplates are two to
           four times the first pass against the same order of staff, which is
           also what a real line looks like.
           ================================================================= */

        /*
         * MEAT WORKS. 1.2kg of finished product per kilo of meat, which is the
         * rusk, the brine and the water a real sausage carries; a ready meal is
         * a quarter meat, two fifths vegetables and a tenth dry grains by
         * purchased weight. 12.5 tonnes out of 9.75 in, and the basket says
         * that feeds about five thousand people.
         *
         * THE DEAREST INPUT BILL OF ANY BUILDING IN THE GAME as a share of
         * what it sells: 46% at mid-band, and it stays 46% at the floor
         * because meat and sausage move together. That is the whole business.
         * On a city's own herd it clears about a quarter of revenue; on meat
         * landed at the world's ceiling the input bill alone is 73% and there
         * is nothing left for the wages. See sectors.FoodProcessing.
         */
        BuildingsTemplate meatWorks = new BuildingsTemplate("Meat Works", BuildingType.INDUSTRIAL)
                .setCashCost(1023).setConstructionPoints(98).setConstructionMaterials(19)
                .setSector("Food Processing")
                .makes(Good.PROCESSED_MEAT, 5000)
                .makes(Good.READY_MEALS, 7500).setStock(25000)
                .uses(Good.MEAT, 6000)
                .uses(Good.VEGETABLES, 3000)
                .uses(Good.GRAINS, 750)
                .setElectricityConsumption(8).setWaterConsumption(6)
                .setLandSqFt(6000).setRoadLoad(4)
                .setJobs(JobType.NO_DIPLOMA, 4)
                .setJobs(JobType.DIPLOMA, 1).setId(56);
        templates.add(meatWorks);

        /*
         * SNACK & OILS PLANT. Four kilos of potatoes make a kilo of crisps and
         * a tonne of oilseed presses to 420kg of oil; blended over a mixed line
         * that is three tonnes of crops a tonne of output. The crop bill is
         * small - 19% - and the fryers and the packing hall are not, which is
         * what makes this the labour-and-power rung. Eighteen tonnes a month
         * feeds about ten thousand people of both.
         *
         * THE WIDEST MARK-UP IN THE CATALOGUE, and it is not an accident of
         * the price table: crisps really are $10 a kilo made out of $0.44
         * potatoes. What stops it being a money printer is the band - a city
         * whose plants cover its own appetite pushes snacks down towards the
         * $6.20 floor, and the sales tax falls on value added, which is nearly
         * all of what this plant is.
         */
        BuildingsTemplate snackPlant = new BuildingsTemplate("Snack & Oils Plant", BuildingType.INDUSTRIAL)
                .setCashCost(1207).setConstructionPoints(116).setConstructionMaterials(22)
                .setSector("Food Processing")
                .makes(Good.SNACKS, 10000)
                .makes(Good.FATS, 8000).setStock(48000)
                .uses(Good.CROPS, 54)
                .setElectricityConsumption(15).setWaterConsumption(5)
                .setLandSqFt(7000).setRoadLoad(5)
                .setJobs(JobType.NO_DIPLOMA, 5)
                .setJobs(JobType.DIPLOMA, 1).setId(57);
        templates.add(snackPlant);

        /*
         * BOTTLING PLANT. A drink is about an eighth sugar and concentrate and
         * the rest water, so the crop line is a rounding error - 4% - and the
         * water line is real: forty units a month on $72k of revenue, eight
         * times a bakery's intensity per dollar and the first building in the
         * game whose utility bill is worth reading. Twelve kilos a head a
         * month is what the basket says a city drinks, so sixty tonnes serves
         * about five thousand people.
         *
         * WHICH MAKES IT THE WAGE PLANT. With almost no input bill to credit
         * against the sales tax, nearly everything this plant sells is value
         * added, and the two things that can eat it are the payroll and the
         * tax rate. A player who wants to know what a sales tax does to a
         * business can watch it here first.
         */
        BuildingsTemplate bottling = new BuildingsTemplate("Bottling Plant", BuildingType.INDUSTRIAL)
                .setCashCost(864).setConstructionPoints(83).setConstructionMaterials(16)
                .setSector("Food Processing")
                .makes(Good.DRINKS, 60000).setStock(162000)
                .uses(Good.CROPS, 8)
                .setElectricityConsumption(4).setWaterConsumption(40)
                .setLandSqFt(5000).setRoadLoad(8)
                .setJobs(JobType.NO_DIPLOMA, 3)
                .setJobs(JobType.DIPLOMA, 1).setId(58);
        templates.add(bottling);
        //add more buildings; next Building ID is 62
    }

    public void finalUpdateBuildings() {
        System.out.println("Construction Materials Produced:            " + getConstructionMaterialsProduction());
        constructionMaterials += getConstructionMaterialsProduction();
        System.out.println("Total Available:                            " + constructionMaterials);

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

        // The materials are the caller's business since the sector template:
        // Game.drawMaterials() takes them from the yard, the plant and the
        // world before the order reaches here. See Markets.draw().

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

    /** Every template, in load order. */
    public List<BuildingsTemplate> getTemplates() {
        return templates;
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

    /**
     * Runs the month's construction and reports what actually opened.
     *
     * Returns rather than logging here, because BuildingManager has no clock -
     * the month a completion happened in is Game's to know, and handing this
     * class the month just so it could stamp a log entry would be the wrong
     * dependency in the wrong direction.
     *
     * Instances are advanced but not reported. They are latent (nothing calls
     * addInstance() yet) and the aggregation helpers already skip them - see
     * backlog item 4 - so reporting them would be the only place in the codebase
     * pretending that path is live.
     */
    /**
     * What each site gets of the month's output. The engine splits evenly
     * per stack (backlog item 2 - per stack, not per work remaining), and the
     * construction panel used to re-derive this split beside it. One place.
     */
    public double outputPerSite(int constructionOutput) {
        int sites = getUnderConstruction();
        return sites > 0 ? (double) constructionOutput / sites : constructionOutput;
    }

    /**
     * Months until a site's last building finishes at a given per-site
     * output: the points still owed over the pace. NaN when nothing is
     * moving, so the screen can say why rather than print 2147483647.
     */
    public double monthsLeft(BuildingsStacks site, double perSiteOutput) {
        if (perSiteOutput <= 0 || site.getUnderConstruction() <= 0) return Double.NaN;
        double owed = site.getUnderConstruction() * (double) site.getBuilding().getConstructionPoints()
                - site.getConstructionProgress();
        return Math.ceil(Math.max(0, owed) / perSiteOutput);
    }

    public java.util.List<Completion> advanceConstruction(int constructionOutput) {

        java.util.List<Completion> finished = new java.util.ArrayList<>();
        materialsDue = 0;
        revenueDue = 0;

        if (getUnderConstruction() != 0) {
            double outputPerStack = (double) constructionOutput / getUnderConstruction();
            for (BuildingsStacks stack : stacks) {
                stack.advanceConstruction(outputPerStack);
                materialsDue += stack.getMaterialsDue();
                revenueDue += stack.getRevenueDue();
                if (stack.getLastFinished() > 0) {
                    finished.add(new Completion(
                            stack.getBuilding().getName(), stack.getLastFinished()));
                }
            }
            for (BuildingInstance inst : instances) {
                inst.advanceConstruction();
            }
        }

        return finished;
    }

    /**
     * Units of material this month's building work drew on, summed over the
     * sites by advanceConstruction() - the builders' purchase for the month,
     * which Game.drawSiteMaterials() takes from the yard, the plant and the
     * world. Read once; the next month's advance sets it again.
     */
    private double materialsDue;

    public double takeMaterialsDue() {
        double d = materialsDue;
        materialsDue = 0;
        return d;
    }

    /** ...and what the same work earned of the builders' contracts. See BuildingsStacks.contractValue. */
    private double revenueDue;

    public double takeRevenueDue() {
        double d = revenueDue;
        revenueDue = 0;
        return d;
    }

    /** One building type and how many of it opened this month. */
    public static final class Completion {

        public final String building;
        public final int quantity;

        Completion(String building, int quantity) {
            this.building = building;
            this.quantity = quantity;
        }
    }

    public void displayAllBuildings() {
        System.out.println("--- Aggregated Buildings ---");
        for (BuildingsStacks stack : stacks) {
            System.out.println(stack.getName() + " x" + stack.getQuantity()
                    + " (Under Construction: " + stack.getUnderConstruction() + ")");
        }
    }
    //getters

    /** How many finished buildings of this name the city has. */
    public int countByName(String name) {
        int total = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getName().equals(name)) total += stack.getQuantity();
        }
        return total;
    }

    /**
     * How many of one named building are on site right now.
     *
     * Per NAME rather than per category, because the advisor's "already
     * building" guard is a question about the thing being ordered. Asked by
     * category, it told the bank planner that a city with a grocery store going
     * up was too busy to open a branch - which is how a city with $5.8B of loans
     * and no bank at all came to sit at the punitive rate for four thousand
     * months without ever ordering the building that would fix it.
     */
    public int underConstructionByName(String name) {
        int total = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getName().equals(name)) total += stack.getUnderConstruction();
        }
        return total;
    }

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

    /**
     * Production capacity of one category that is ON SITE but not finished.
     *
     * SUPPLY THAT IS COMING, and leaving it out is what makes a sector build the
     * same shortage three times. The planner reads what the city PRODUCES,
     * decides it is short, and orders - and then does the same thing again next
     * month, because the plants it ordered are still going up and have not
     * started producing. Measured: food plants went 1 -> 3 -> 4 against a
     * shortage that one and a half would have covered, and the resulting glut
     * halved the local price, put the sector below cost, and retired the lot.
     *
     * A hog cycle, in the textbook sense, and the textbook cause: acting on a
     * price signal without counting the capacity your last decision already put
     * in the ground.
     */
    public double productionUnderConstruction(BuildingType category) {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getCategory() != category) continue;
            total += stack.getUnderConstruction() * stack.getBuilding().getProduction1();
        }
        return total;
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

    /**
     * Materials already bought for buildings that are not finished yet.
     *
     * WHY GDP NEEDS THIS. Materials for a whole order are imported and paid for
     * the moment the player confirms it, but the buildings they become are
     * recognised as investment only as the work is put in place - months later
     * for a large order. Measured without this, the import lands as a straight
     * hit to output in the month the city invests the MOST, which is how a city
     * ordering eight thousand houses reported negative production.
     *
     * They are work in progress: bought, embedded in something unfinished, and
     * not yet output. Counting them as inventory holds the import and the
     * building it becomes in the same accounts until the building is done.
     */
    public double getMaterialsInProgress() {
        double units = 0;
        for (BuildingsStacks stack : getStacksUnderConstruction()) {
            units += (double) stack.getUnderConstruction()
                    * stack.getBuilding().getConstructionMaterials();
        }
        return units;
    }

    /**
     * Jobs that will exist once everything on site is finished.
     *
     * Real estate needs this. Population is capped at jobs x 2.25, so housing
     * demand is a function of the JOB market - and by the time an apartment
     * block is up, the mill that was being built alongside it is staffed. An
     * investor looking only at jobs that exist today is reading a number that is
     * already out of date by exactly the lead time of its own project, and it
     * under-builds every time the city is growing, which is every time it
     * matters.
     */
    public int getJobsUnderConstruction() {
        int jobs = 0;
        for (BuildingsStacks stack : getStacksUnderConstruction()) {
            jobs += stack.getUnderConstruction() * stack.getBuilding().getTotalJobs();
        }
        return jobs;
    }

    /** Homes that will exist once everything on site is finished. */
    public int getHouseCapacityUnderConstruction() {
        int capacity = 0;
        for (BuildingsStacks stack : getStacksUnderConstruction()) {
            if (stack.getBuilding().getCategory() == BuildingType.RESIDENTIAL) {
                capacity += stack.getUnderConstruction() * stack.getBuilding().getCapacity();
            }
        }
        return capacity;
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

    /**
     * How many separate HOMES the city has, each holding one household.
     *
     * The other way of counting housing, and the one the demographics model
     * uses: capacity says how many people fit, this says how many front doors
     * there are. A city can have room for ten thousand people and only two
     * thousand homes, and which of those binds is a different question with a
     * different answer.
     *
     * FALLS BACK RATHER THAN RETURNING ZERO for residential buildings whose data
     * predates the dwellings field. A building that houses people but declares
     * no homes would otherwise read as uninhabitable, which is worse than an
     * estimate - four people to a home is the House's own ratio and the least
     * surprising guess available.
     */
    public int getTotalHomes() {
        int homes = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCategory() != BuildingType.RESIDENTIAL) continue;

            int per = t.getDwellings() > 0
                    ? t.getDwellings()
                    : Math.max(1, t.getCapacity() / 4);
            homes += stack.getQuantity() * per;
        }
        return homes;
    }

    /**
     * The city's finished homes, counted by how big a household each one takes.
     *
     * Index is the unit size in people; index 0 is unused. What FamilyModel
     * needs to put households behind doors that actually fit them, rather than
     * against one pooled count that let a family of six into a studio.
     */
    public int[] homesBySize() {
        int widest = 1;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCategory() != BuildingType.RESIDENTIAL) continue;
            widest = Math.max(widest, t.homeSize());
        }

        int[] out = new int[widest + 1];
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCategory() != BuildingType.RESIDENTIAL) continue;

            int per = t.getDwellings() > 0
                    ? t.getDwellings()
                    : Math.max(1, t.getCapacity() / 4);
            int size = Math.max(1, t.homeSize());
            out[size] += stack.getQuantity() * per;
        }
        return out;
    }

    /** Homes that will exist once everything on site is finished. */
    public int getHomesUnderConstruction() {
        int homes = 0;
        for (BuildingsStacks stack : getStacksUnderConstruction()) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCategory() != BuildingType.RESIDENTIAL) continue;

            int per = t.getDwellings() > 0
                    ? t.getDwellings()
                    : Math.max(1, t.getCapacity() / 4);
            homes += stack.getUnderConstruction() * per;
        }
        return homes;
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

    /** Shelf room across the shops, in units. The stores' `stock` field since the sector template. */
    public int getTotalStoreCapacity() {
        return (int) totalBySector("Retail", BuildingsTemplate::getStock);
    }

    /**
     * The city's own crews, plus whatever the depots add.
     *
     * BASE_CONSTRUCTION is the municipal works department: a city with no
     * Construction Depot at all still puts up this many points a month, which
     * is what makes the opening city able to build its first depot.
     *
     * RAISED FROM 100 TO 400 on 2026-09-09, Jerus's call, with the founding
     * endowment and the founding bank. A hundred points against a Construction
     * Depot that costs 3,000 is thirty months of the whole city's output to buy
     * the thing that makes it build faster - so the opening decade was spent
     * waiting rather than deciding, and every measurement of the early game was
     * really a measurement of that queue.
     *
     * It is also the number housing maintenance is charged against. At 100 a
     * depot-less city with a thousand flats was spending a third of its works
     * department on repairs; at 400 that is under a tenth, which is the share
     * the maintenance rate was calibrated for.
     */
    public static final int BASE_CONSTRUCTION = 400;

    /**
     * Same idea for materials: a yard that produces this many a month on its own.
     *
     * MOVED WITH BASE_CONSTRUCTION, and by the same factor, deliberately, on
     * 2026-09-09: points and materials were two halves of one build rate, and
     * raising one without the other would have quadrupled how fast the city
     * could build and left it importing four times as much material to do it.
     *
     * 320 -> 36 on 2026-09-10, WITH THE UNIT, NOT AGAINST IT. The unit of
     * material went from $2,000 to $18,000 (see MATERIALS_WORLD_PRICE), so the
     * yard's free output is held at what it was WORTH - 320 x $2,000 = $640k a
     * month, 36 x $18,000 = $648k - and not at what it counted. Holding the
     * count would have handed a founding city $5.8M of free material a month
     * against a founding GDP of $5.5M, which is not a works yard, it is a
     * second endowment nobody decided on.
     *
     * What that means physically: the yard used to cover the materials of 32
     * Houses a month and now covers 3.6, because a House's materials used to
     * be 4% of its cost and are now the real 40%. The materials are not
     * scarcer; the old figure was a tenth of what a house is made of.
     */
    public static final int BASE_MATERIALS = 36;

    public int getTotalConstructionCapacity() {
        // NOTE: getProduction1() is a double; the original loop truncated it via
        // implicit int += double narrowing. Casting explicitly here to keep that
        // same truncating behavior rather than silently changing it to round.
        return BASE_CONSTRUCTION
                + (int) totalBySector("Construction", t -> t.makes(Good.BUILDING_WORK));
    }

    /**
     * calculates Construction Materials production from all buildings
     *
     * @return Construction Materials production
     */
    /**
     * THE YARD'S OWN OUTPUT, and only that, since the sector template. The
     * Construction Materials Plant sells what it makes on the materials
     * market now (see sectors.Materials); the public works yard still turns
     * out its BASE_MATERIALS a month for free, drawn first by every order.
     */
    public int getConstructionMaterialsProduction() {
        return BASE_MATERIALS;
    }

    /*
    ---------------------------------------------------------------------------
     */

 /*Industrial methods
    
    
    ---------------------------------------------------------------------------
    
    
     */
    /** Kilograms of bakery goods the city's own ovens turn out a month. */
    public int getFoodProduction() {
        return (int) totalBySector("Industry", t -> t.makes(Good.BREAD) + t.makes(Good.BAKERY));
    }

    public int getFoodCapacity() {
        return (int) totalBySector("Industry", BuildingsTemplate::getStock);
    }

    /* =====================================================================
       BY SECTOR (2026-09-11, the sector template)

       The same sums as the category ones below, keyed by the owning sector's
       name rather than the menu group. A Sector reads everything about its
       own buildings through these - capacity, posts, land, book value, sites
       - and nothing else in the game has to know which buildings are whose.
       ===================================================================== */

    /** Finished buildings only: quantity times the getter, over the sector's stacks. */
    public double totalBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter) {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (!t.getSector().equals(sector)) continue;
            total += stack.getQuantity() * getter.applyAsDouble(t);
        }
        return total;
    }

    /** Buildings on site only - what is coming. */
    public double underConstructionBySector(String sector, ToDoubleFunction<BuildingsTemplate> getter) {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (!t.getSector().equals(sector)) continue;
            total += stack.getUnderConstruction() * getter.applyAsDouble(t);
        }
        return total;
    }

    /** Orders on site for a sector, in buildings. */
    public int getUnderConstructionBySector(String sector) {
        int total = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getSector().equals(sector)) total += stack.getUnderConstruction();
        }
        return total;
    }

    /** The posts a sector's finished buildings offer, per tier. */
    public int[] getJobArrayBySector(String sector) {
        int[] out = new int[JobType.values().length];
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (!t.getSector().equals(sector)) continue;
            for (JobType j : JobType.values()) out[j.ordinal()] += stack.getQuantity() * t.getJobs(j);
        }
        return out;
    }

    /** Square feet a sector holds, standing and on site - the plot is occupied the day it is bought. */
    public double getLandSqFtBySector(String sector) {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (!t.getSector().equals(sector)) continue;
            total += t.getLandSqFt() * (stack.getQuantity() + stack.getUnderConstruction());
        }
        return total;
    }

    /** Finished and unfinished together, at cash plus materials at market - what the sector's buildings are worth. */
    public double getBuildingsValueBySector(String sector) {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (!t.getSector().equals(sector)) continue;
            total += (stack.getQuantity() + stack.getUnderConstruction())
                    * (t.getCashCost() + t.getConstructionMaterials() * materialsCost);
        }
        return total;
    }

    /** People a sector's buildings hold, sites included. See getCapacityInPortfolio. */
    public int getCapacityInPortfolioBySector(String sector) {
        int total = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (!t.getSector().equals(sector)) continue;
            total += (stack.getQuantity() + stack.getUnderConstruction()) * t.getCapacity();
        }
        return total;
    }

    /** Every template a sector may build. */
    public List<BuildingsTemplate> getTemplatesBySector(String sector) {
        List<BuildingsTemplate> out = new ArrayList<>();
        for (BuildingsTemplate t : templates) if (t.getSector().equals(sector)) out.add(t);
        return out;
    }

    /** Every sector name any template answers to, for the catalogue check. */
    public java.util.Set<String> sectorsNamed() {
        java.util.Set<String> out = new java.util.LinkedHashSet<>();
        for (BuildingsTemplate t : templates) if (t.isOwnedBySector()) out.add(t.getSector());
        return out;
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
    /**
     * Square feet of lot held by one category, standing and under construction.
     *
     * Under-construction land counts because the plot was bought and allocated
     * the day the order was placed - a half-built plant is occupying and owing
     * tax on its site exactly like a finished one.
     */
    /**
     * People a category's buildings hold, plus those its SITES will hold.
     *
     * MATCHES getLandSqFtByCategory below, deliberately. The property tax bill
     * is assessed on every plot the category occupies, finished or not, so any
     * per-capacity figure struck against that bill has to count the same
     * buildings - otherwise a large order on site puts its whole ground rent
     * on the handful of finished units.
     *
     * Measured before this existed, on CreditCheck's fixture: 2,500 houses on
     * site and 26 standing priced the rent floor at $2.40 a head against a
     * going rate of $0.08, and the city fell from 22 residents to 5.
     */
    public int getCapacityInPortfolio(BuildingType category) {
        int total = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getCategory() != category) continue;
            total += (stack.getQuantity() + stack.getUnderConstruction())
                    * stack.getBuilding().getCapacity();
        }
        return total;
    }

    public double getLandSqFtByCategory(BuildingType category) {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getCategory() == category) {
                total += stack.getBuilding().getLandSqFt()
                        * (stack.getQuantity() + stack.getUnderConstruction());
            }
        }
        return total;
    }

    /**
     * How many people the city's finished buildings of one care type have room
     * for.
     *
     * FINISHED ONLY, unlike getLandSqFtByCategory. A half-built hospital treats
     * nobody, and the whole point of the number is that the city has to have
     * actually paid for and completed the capacity before it counts. Land is the
     * opposite case - a site is occupied the day it is bought - which is why the
     * two methods differ and why this comment exists.
     *
     * Keyed on the care type rather than the category, so a future non-HEALTHCARE
     * building that happens to provide care (a company clinic, say) counts
     * without this method changing.
     */
    public double getCareCapacity(CareType care) {
        if (care == null || care == CareType.NONE) return 0;

        // The doctor, the nursery, the almshouse and the churchyard the city was
        // founded with, exactly as getTotalHouseCapacity() starts at 100 and the
        // road network at 400. See Healthcare.foundingCapacity().
        double total = Healthcare.foundingCapacity(care);
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getCare() == care) {
                total += stack.getQuantity() * (double) stack.getBuilding().getCapacity();
            }
        }
        return total;
    }

    /**
     * The same capacity, discounted by how much of it is actually staffed.
     *
     * A General Hospital with no doctors was treating forty thousand people.
     * Every other sector's output is cut by its fill rate - the mills, the
     * shops, the mines, the crews - and care was the one thing in the game you
     * could get for free by pouring concrete.
     *
     * Staffing is worked out PER BUILDING from its own job mix rather than from
     * one city-wide average, because a healthcare building's mix is unlike
     * anything else: a Walk-in Clinic is two doctors and five nurses, and if the
     * city has no doctors that clinic is shut whatever the unskilled fill rate
     * says. A building with no jobs at all counts as fully staffed, which is the
     * right answer for a cemetery that needs nobody.
     *
     * @param jobFillRate the per-JobType fill, from PopulationManager
     */
    public double getStaffedCareCapacity(CareType care, double[] jobFillRate) {
        if (care == null || care == CareType.NONE) return 0;
        if (jobFillRate == null) return getCareCapacity(care);

        // Undiscounted, because nobody is on the payroll for it - the founding
        // endowment is not a building and has no fill rate to be short of.
        double total = Healthcare.foundingCapacity(care);
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCare() != care) continue;

            double posts = 0, staffed = 0;
            for (JobType job : JobType.values()) {
                int n = t.getJobs(job);
                if (n == 0) continue;
                posts += n;
                staffed += n * (job.ordinal() < jobFillRate.length
                        ? jobFillRate[job.ordinal()] : 1);
            }

            double staffing = posts > 0 ? staffed / posts : 1;
            total += stack.getQuantity() * (double) t.getCapacity() * staffing;
        }
        return total;
    }

    /**
     * School places, discounted by how much of the teaching staff turned up.
     *
     * The same method as getStaffedCareCapacity() and for the same reason: a
     * university with no professors teaches nobody, and pouring concrete should
     * not produce graduates any more than it produces treatments. Staffing is
     * worked out per building from its own job mix, because a school's mix is
     * particular - a Medical School is 34 doctors in 280 posts, and a city with
     * no doctors cannot run one whatever its unskilled fill rate says.
     *
     * FINISHED ONLY, like care capacity. A half-built school teaches nobody.
     */
    public double[] getStaffedEducationPlaces(double[] jobFillRate) {

        double[] places = new double[EducationType.values().length];

        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            EducationType teaches = t.getTeaches();
            if (teaches == EducationType.NONE) continue;

            double posts = 0, staffed = 0;
            for (JobType job : JobType.values()) {
                int n = t.getJobs(job);
                if (n == 0) continue;
                posts += n;
                staffed += n * (jobFillRate != null && job.ordinal() < jobFillRate.length
                        ? jobFillRate[job.ordinal()] : 1);
            }
            double staffing = posts > 0 ? staffed / posts : 1;

            places[teaches.ordinal()] +=
                    stack.getQuantity() * (double) t.getCapacity() * staffing;
        }
        return places;
    }

    /**
     * Officers or cells, discounted by how much of the staff turned up.
     *
     * The same method as getStaffedCareCapacity() and for the same reason: a
     * police station with nobody on shift patrols nothing, and a jail with no
     * guards holds nobody. Per building, from its own job mix. The founding
     * constabulary is not a building and is not discounted - see
     * SafetyType.foundingCapacity().
     *
     * FINISHED ONLY. A half-built jail holds nobody.
     */
    public double getStaffedSafetyCapacity(SafetyType safety, double[] jobFillRate) {
        if (safety == null || safety == SafetyType.NONE) return 0;
        double total = safety.foundingCapacity();
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getSafety() != safety) continue;

            double posts = 0, staffed = 0;
            for (JobType job : JobType.values()) {
                int n = t.getJobs(job);
                if (n == 0) continue;
                posts += n;
                staffed += n * (jobFillRate != null && job.ordinal() < jobFillRate.length
                        ? jobFillRate[job.ordinal()] : 1);
            }
            double staffing = posts > 0 ? staffed / posts : 1;
            total += stack.getQuantity() * (double) t.getCapacity() * staffing;
        }
        return total;
    }

    /** ...and without the discount: what the buildings would hold, the founding constabulary included. */
    public double getSafetyCapacity(SafetyType safety) {
        if (safety == null || safety == SafetyType.NONE) return 0;
        double total = safety.foundingCapacity();
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getSafety() == safety) {
                total += stack.getQuantity() * (double) stack.getBuilding().getCapacity();
            }
        }
        return total;
    }

    /** The wage bill of just the police, or just the prisons. */
    public double getSafetyPayroll(SafetyType safety, double[] wagePerType, double[] jobFillRate) {
        if (safety == null || safety == SafetyType.NONE || wagePerType == null) return 0;
        double payroll = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getSafety() != safety) continue;
            payroll += stack.getQuantity() * jobBill(t, wagePerType, jobFillRate);
        }
        return payroll;
    }

    /** ...and their upkeep. */
    public double getSafetyUpkeep(SafetyType safety) {
        if (safety == null || safety == SafetyType.NONE) return 0;
        double upkeep = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getSafety() != safety) continue;
            upkeep += stack.getBuilding().getUpkeep() * stack.getQuantity();
        }
        return upkeep;
    }

    /** Places without the staffing discount - what the buildings would seat. */
    public double[] getBuiltEducationPlaces() {
        double[] places = new double[EducationType.values().length];
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getTeaches() == EducationType.NONE) continue;
            places[t.getTeaches().ordinal()] +=
                    stack.getQuantity() * (double) t.getCapacity();
        }
        return places;
    }

    /**
     * The healthcare service's wage bill, with the fill rate applied.
     *
     * Charged to the city, which is the whole point - see Healthcare. Computed
     * here rather than in EconomyManager because this is where the job arrays
     * live, and computing it anywhere else would be a second copy of a sum that
     * has to agree with getJobArrayPerCategory().
     */
    public double getCategoryPayroll(BuildingType category,
                                     double[] wagePerType, double[] jobFillRate) {
        if (wagePerType == null) return 0;

        int[] jobs = getJobArrayPerCategory(category);
        double payroll = 0;
        for (int i = 0; i < jobs.length && i < wagePerType.length; i++) {
            double fill = (jobFillRate != null && i < jobFillRate.length) ? jobFillRate[i] : 1;
            payroll += jobs[i] * wagePerType[i] * fill;
        }
        return payroll;
    }

    /* ===================================================================
       THE SAME TWO SUMS, NARROWED TO ONE SERVICE.

       READ-ONLY, and nothing below changes a thing. Payroll and upkeep are
       charged to the city by CATEGORY - all of healthcare together, all of
       education together - which is the right unit to bill and the wrong unit
       to explain. "Health costs $20.5M a month" is not something a player can
       act on; "childcare is $3.1M of it and covers half the children" is.

       Keyed on the care type and the course exactly as getStaffedCareCapacity
       and getStaffedEducationPlaces are, so a building that provides care
       counts here whatever category it was filed under.
       =================================================================== */

    /** The wage bill of just the buildings providing one kind of care. */
    public double getCarePayroll(CareType care, double[] wagePerType, double[] jobFillRate) {
        if (care == null || care == CareType.NONE || wagePerType == null) return 0;
        double payroll = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCare() != care) continue;
            payroll += stack.getQuantity() * jobBill(t, wagePerType, jobFillRate);
        }
        return payroll;
    }

    /** ...and what those same buildings cost to keep standing. */
    public double getCareUpkeep(CareType care) {
        if (care == null || care == CareType.NONE) return 0;
        double upkeep = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getCare() != care) continue;
            upkeep += stack.getQuantity() * stack.getBuilding().getUpkeep();
        }
        return upkeep;
    }

    /** The wage bill of just the schools teaching one course. */
    public double getSchoolPayroll(EducationType teaches,
                                   double[] wagePerType, double[] jobFillRate) {
        if (teaches == null || teaches == EducationType.NONE || wagePerType == null) return 0;
        double payroll = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getTeaches() != teaches) continue;
            payroll += stack.getQuantity() * jobBill(t, wagePerType, jobFillRate);
        }
        return payroll;
    }

    /** ...and their upkeep. */
    public double getSchoolUpkeep(EducationType teaches) {
        if (teaches == null || teaches == EducationType.NONE) return 0;
        double upkeep = 0;
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getTeaches() != teaches) continue;
            upkeep += stack.getBuilding().getUpkeep() * stack.getQuantity();
        }
        return upkeep;
    }

    /** One building's monthly wage bill, with the fill rate applied per post. */
    private static double jobBill(BuildingsTemplate t,
                                  double[] wagePerType, double[] jobFillRate) {
        double bill = 0;
        for (JobType job : JobType.values()) {
            int n = t.getJobs(job);
            if (n == 0) continue;
            int i = job.ordinal();
            double fill = (jobFillRate != null && i < jobFillRate.length) ? jobFillRate[i] : 1;
            bill += n * (i < wagePerType.length ? wagePerType[i] : 0) * fill;
        }
        return bill;
    }

    /** What the standing buildings of one category cost to run each month. */
    public double getUpkeepByCategory(BuildingType category) {
        return getTotalByCategoryDouble(category, BuildingsTemplate::getUpkeep);
    }

    public double getBookValueByCategory(BuildingType category) {
        return getTotalByCategoryDouble(
                category,
                t -> t.getCashCost() + t.getConstructionMaterials() * materialsCost);
    }

    /** The same, for the buildings one sector owns - finished ones only. */
    public double getBookValueBySector(String sector) {
        return totalBySector(sector,
                t -> t.getCashCost() + t.getConstructionMaterials() * materialsCost);
    }

    /**
     * What a category has PAID FOR and not yet got: buildings on site, at the
     * same valuation the finished ones carry.
     *
     * CONSTRUCTION IN PROGRESS IS AN ASSET, and it was on nobody's balance
     * sheet. A business pays for a building in full the month it orders it and
     * the book value above counts only what has finished - so for the whole
     * build the money was gone and the asset was not there, and a sector that
     * had borrowed to build was insolvent by the ratio the next month. Traced
     * on the playtest's own founding: Industry ordered its first plant on
     * $10.8M of credit at month 3, was written down to nothing at month 4
     * (principal $10.8M against assets of -$61), and the bank ate the whole
     * loan before the plant had opened at month 6. That is the "firm being
     * refinanced on a loop" the credit write-ups kept finding: every plant a
     * sector ever built on credit defaulted a month later, for the length of
     * the project.
     *
     * Real books carry it as construction in progress. So do these now.
     */
    public double getWorkInProgressByCategory(BuildingType category) {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            BuildingsTemplate t = stack.getBuilding();
            if (t.getCategory() != category || stack.getUnderConstruction() <= 0) continue;
            total += stack.getUnderConstruction()
                    * (t.getCashCost() + t.getConstructionMaterials() * materialsCost);
        }
        return total;
    }

    /** Finished and unfinished together - what the sector's buildings are worth. */
    public double getBuildingsValueByCategory(BuildingType category) {
        return getBookValueByCategory(category) + getWorkInProgressByCategory(category);
    }

    /**
     * The same array, for one named building rather than a whole category.
     *
     * Exists so the bank can be charged its own tellers. Its jobs are part of
     * the commercial category's payroll, and telling the two apart is the whole
     * of what a bank's income statement needs that the category total cannot
     * give it.
     */
    public int[] getJobArrayByName(String name) {
        int[] jobs = new int[JobType.values().length];
        for (BuildingsStacks stack : stacks) {
            if (!stack.getBuilding().getName().equals(name)) continue;
            for (int j = 0; j < jobs.length; j++) {
                jobs[j] += stack.getTotalJobs(jobTypes[j]);
            }
        }
        return jobs;
    }

    public int[] getJobArrayPerCategory(BuildingType category) {
        int[] jobs = new int[JobType.values().length];

        for (int i = 0; i < stacks.size(); i++) {

            if (stacks.get(i).getBuilding().getCategory() == category) {

                for (int j = 0; j < jobs.length; j++) {
                    jobs[j] += stacks.get(i).getTotalJobs(jobTypes[j]);
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

    /** How many of template id {@code i} are finished and standing. */
    public int getQuantity(int i) {
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getId() == i) {
                return stack.getQuantity();
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

    /**
     * Every square foot the city has committed to buildings - standing and on
     * site both, since a half-built plant is occupying its plot.
     *
     * This is what a load recomputes the land ledger from, rather than trusting
     * a saved figure: the buildings ARE the allocation, so deriving it means a
     * save written before land existed still loads a correct city, and any
     * drift between the two heals itself the next time the game is loaded.
     */
    public double getTotalLandFootprint() {
        double total = 0;
        for (BuildingsStacks stack : stacks) {
            total += stack.getBuilding().getLandSqFt()
                    * (stack.getQuantity() + stack.getUnderConstruction());
        }
        return total;
    }

    /**
     * Scraps finished buildings. The counterpart to addStack(), and the first
     * thing in the game that makes the city smaller.
     *
     * Only COMPLETED buildings go: a half-built one has a construction contract
     * behind it that has been partly billed and partly delivered, and unwinding
     * that is a different problem from a firm deciding it owns too much.
     *
     * The stack is left in place even when it empties, because the save format
     * indexes construction arrays by stack position and removing one would
     * shift every index after it. That is backlog item 5 biting from a new
     * direction; an empty stack is harmless.
     *
     * @return how many were actually scrapped, which may be fewer than asked
     */
    public int retire(BuildingsTemplate template, int quantity) {

        if (template == null || quantity <= 0) {
            return 0;
        }

        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getId() == template.getId()) {

                int scrapped = Math.min(quantity, stack.getQuantity());
                if (scrapped > 0) {
                    stack.removeQuantity(scrapped);
                }
                return scrapped;
            }
        }
        return 0;
    }

    /** How many stacks exist, for callers checking a save's arrays line up. */
    public int getStackCount() {
        return stacks.size();
    }

    public int[] getUnderConstructionArray() {
        int[] progress = new int[stacks.size()];

        for (int i = 0; i < stacks.size(); i++) {
            progress[i] = stacks.get(i).getUnderConstruction();
        }

        return progress;
    }

    /* ----------------------------------------------------------------------
     * Construction state for the save, keyed by template id.
     *
     * The pair below this (getConstructionProgress / getUnderConstructionArray)
     * returns one entry per STACK, in whatever order the player happened to
     * build things. The load path recreates stacks in template-id order and
     * only for templates with a completed quantity - so a building that was
     * purely under construction left no stack at all, every later position
     * shifted, and the arrays were refused wholesale. That is why in-progress
     * construction never survived a save.
     *
     * Keyed by id, the way buildings[] already is, position stops mattering.
     * The old methods are kept because saves written before this change are
     * still in the old shape and the load path still has to read them.
     * -------------------------------------------------------------------- */

    /**
     * The highest id in the catalogue, not the number of templates.
     *
     * These are not the same thing and assuming they are is a live hazard: ids
     * come from buildings.json, so deleting one building leaves twelve
     * templates whose ids still run to 12, and an array sized by the count is
     * one short of the id it has to hold.
     */
    public int getMaxTemplateId() {
        int max = -1;
        for (BuildingsTemplate template : templates) {
            max = Math.max(max, template.getId());
        }
        return max;
    }

    public double[] getConstructionProgressById() {
        double[] out = new double[getMaxTemplateId() + 1];
        for (BuildingsStacks stack : stacks) {
            int id = stack.getBuilding().getId();
            if (id >= 0 && id < out.length) {
                out[id] = stack.getConstructionProgress();
            }
        }
        return out;
    }

    public int[] getUnderConstructionById() {
        int[] out = new int[getMaxTemplateId() + 1];
        for (BuildingsStacks stack : stacks) {
            int id = stack.getBuilding().getId();
            if (id >= 0 && id < out.length) {
                out[id] = stack.getUnderConstruction();
            }
        }
        return out;
    }

    /** Material the sites still have to draw, by template id. See BuildingsStacks.materialsOwed. */
    public double[] getMaterialsOwedById() {
        double[] out = new double[getMaxTemplateId() + 1];
        for (BuildingsStacks stack : stacks) {
            int id = stack.getBuilding().getId();
            if (id >= 0 && id < out.length) {
                out[id] = stack.getMaterialsOwed();
            }
        }
        return out;
    }

    /** Material delivered to a template's sites from the yard: off what they owe. See Game.deliverYardToSites. */
    public void deliverToSites(BuildingsTemplate template, double units) {
        BuildingsStacks stack = getStack(template);
        if (stack != null) stack.deliverMaterials(units);
    }

    /** The builders' price for an order, on the stack it was placed on. See BuildingsStacks.contractValue. */
    public void bookContract(BuildingsTemplate template, double amount) {
        BuildingsStacks stack = getStack(template);
        if (stack != null) stack.bookContract(amount);
    }

    /** One order book, spread over the sites by the points they still owe. For a save from before the book was kept per stack. */
    public void spreadContracts(double unearned) {
        if (unearned <= 0) return;
        double owed = getRemainingConstructionPoints();
        if (owed <= 0) return;
        for (BuildingsStacks stack : stacks) {
            double points = stack.getUnderConstruction() * (double) stack.getBuilding().getConstructionPoints()
                    - stack.getConstructionProgress();
            if (points > 0) stack.setContractValue(unearned * points / owed);
        }
    }

    /** The builders' contracts still on site, by template id. */
    public double[] getContractValueById() {
        double[] out = new double[getMaxTemplateId() + 1];
        for (BuildingsStacks stack : stacks) {
            int id = stack.getBuilding().getId();
            if (id >= 0 && id < out.length) {
                out[id] = stack.getContractValue();
            }
        }
        return out;
    }

    /** ...and in total, for the screens. */
    public double getMaterialsOwed() {
        double total = 0;
        for (BuildingsStacks stack : stacks) total += stack.getMaterialsOwed();
        return total;
    }

    /**
     * Puts a template's in-progress work back onto its stack.
     *
     * Deliberately does not create the stack: the load path decides what exists,
     * and a restore that quietly conjures a stack for an id the save no longer
     * recognises would hide a real problem. Returns false in that case instead.
     */
    public boolean restoreConstruction(int templateId, int underConstruction,
                                       double progress, double materialsOwed, double contractValue) {
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getId() == templateId) {
                stack.setUnderConstruction(underConstruction);
                stack.setConstructionProgress(progress);
                stack.setMaterialsOwed(materialsOwed);
                stack.setContractValue(contractValue);
                return true;
            }
        }
        return false;
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

    /**
     * Takes the order's materials out of the yard, importing whatever is short.
     *
     * MOVES STOCK, DOES NOT BILL (backlog item 1)
     *
     * This used to also write the import bill into a `cost` field, drained by a
     * getSetCost() that every caller had to remember to call. Three things were
     * wrong with that at once:
     *
     *   - it was `cost =`, not `cost +=`, so two orders short of materials in
     *     one month lost the first bill entirely
     *   - the drain lived inside calculateTotalCost(), which is the QUOTE the
     *     build screen shows. Asking the price changed the price: after a
     *     private investor built six stores, the player's quote for their own
     *     twenty houses read $2,600, and reading it a second time read $1,800.
     *     The $800 difference was the investor's materials, about to be charged
     *     to the city because buildFor() never drained the field
     *   - and it was a second, redundant copy of a number every caller already
     *     computes for itself: processBuildOrder(), buildFor() and
     *     calculateTotalCost() each work out the shortage from the same yard
     *     stock and the same order, and each bills its own payer for it
     *
     * So the field is gone rather than patched. The yard is stock, the bill is
     * the caller's, and a quote is a quote.
     */
    public void handleConstructionMaterials(int required) {
        takeFromYard(required);
    }

    /**
     * Takes what the yard has, up to what was asked. The rest is the
     * caller's to find - from the materials plant or the world, through
     * Markets.draw() - and the caller bills whoever is building.
     *
     * @return units actually taken from the yard
     */
    public int takeFromYard(int required) {
        int taken = Math.max(0, Math.min(required, constructionMaterials));
        constructionMaterials -= taken;
        return taken;
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
        // The yard's founding stock: one month of its own output, which is what
        // Game.newGame() seeds too. This read 80 - the base yard before it was
        // quadrupled, in units that were then repriced - and had no callers.
        constructionMaterials = BASE_MATERIALS;
    }


    /** Every template's price, and the materials the city is holding, in the new unit. */
    public void redenominate(double scale) {
        for (BuildingsTemplate t : templates) {
            if (t != null) t.redenominate(scale);
        }
        for (BuildingsStacks s : stacks) s.redenominate(scale);
        materialsCost *= scale;
    }


    /** Re-seeds every template's price at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        for (BuildingsTemplate t : templates) {
            if (t != null) t.seedConstants(unit);
        }
    }

}
