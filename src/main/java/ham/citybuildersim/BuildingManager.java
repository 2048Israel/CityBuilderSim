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
        house.setCapacity(6);
        house.setDwellings(1);
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
        studioApartments.setCapacity(160);
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
        lowRiseApartments.setCapacity(252);
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
        convienceStore.setCoverage(480);
        convienceStore.setCapacity(1400);
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
        smallGroceryStore.setCoverage(1600);
        smallGroceryStore.setCapacity(7000);
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
        BuildingsTemplate texttileMill = new BuildingsTemplate("Textile Mill", BuildingType.INDUSTRIAL);
        texttileMill.setCapacity(15000);
        texttileMill.setCashCost(18717);
        texttileMill.setConstructionPoints(1800);
        texttileMill.setConstructionMaterials(347);
        texttileMill.setProduction1(5500);
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
        BuildingsTemplate foodProcessingPlant = new BuildingsTemplate("Food Processing Plant", BuildingType.INDUSTRIAL);
        foodProcessingPlant.setCapacity(18000);
        foodProcessingPlant.setCashCost(8994);
        foodProcessingPlant.setConstructionPoints(700);
        foodProcessingPlant.setConstructionMaterials(166);
        foodProcessingPlant.setProduction1(6000);
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
        constructionMaterialsPlant.setCapacity(5);
        constructionMaterialsPlant.setCashCost(44904);
        constructionMaterialsPlant.setConstructionPoints(9000);
        constructionMaterialsPlant.setConstructionMaterials(831);
        constructionMaterialsPlant.setProduction2(160);
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
        constructionDepot.setCapacity(5);
        constructionDepot.setCashCost(1482);
        constructionDepot.setConstructionPoints(400);
        constructionDepot.setConstructionMaterials(55);
        constructionDepot.setProduction1(400);
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
                .setProduction1(1200)               // tonnes of steel a month
                .setProduction2(1320)               // tonnes of scrap that takes
                .setProductionModifier1(0.847)         // export price per tonne
                .setProductionModifier2(0.41)         // scrap price per tonne
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
                .setProduction1(6000)
                .setProduction2(6600)
                .setProductionModifier1(0.847)
                .setProductionModifier2(0.41)
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
                .setId(30);

        templates.add(elevatedHighway);

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
                .setProduction1(2500)           // tonnes of ore a month
                .setProductionModifier1(0.14)    // export price per tonne - THE FLOOR
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
        //add more buildings; next Building ID is 42
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

        if (getUnderConstruction() != 0) {
            double outputPerStack = (double) constructionOutput / getUnderConstruction();
            for (BuildingsStacks stack : stacks) {
                stack.advanceConstruction(outputPerStack);
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

    public int getTotalStoreCapacity() {
        return getTotalByCategoryInteger(BuildingType.COMMERCIAL, BuildingsTemplate::getCapacity);
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
                + getTotalByCategoryInteger(BuildingType.CONSTRUCTION, t -> (int) t.getProduction1());
    }

    /**
     * calculates Construction Materials production from all buildings
     *
     * @return Construction Materials production
     */
    public int getConstructionMaterialsProduction() {
        return BASE_MATERIALS
                + getTotalByCategoryInteger(BuildingType.CONSTRUCTION, t -> (int) t.getProduction2());
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

    /**
     * Puts a template's in-progress work back onto its stack.
     *
     * Deliberately does not create the stack: the load path decides what exists,
     * and a restore that quietly conjures a stack for an id the save no longer
     * recognises would hide a real problem. Returns false in that case instead.
     */
    public boolean restoreConstruction(int templateId, int underConstruction,
                                       double progress) {
        for (BuildingsStacks stack : stacks) {
            if (stack.getBuilding().getId() == templateId) {
                stack.setUnderConstruction(underConstruction);
                stack.setConstructionProgress(progress);
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

        if (constructionMaterials >= required) {
            constructionMaterials -= required;
            return;
        }

        int shortage = required - constructionMaterials;
        constructionMaterials = 0;

        System.out.println(
                "Construction Materials Imported: "
                + formatter.format(shortage)
                + " Cost: $" + formatter.format(shortage * materialsCost)
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
        materialsCost *= scale;
    }


    /** Re-seeds every template's price at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        for (BuildingsTemplate t : templates) {
            if (t != null) t.seedConstants(unit);
        }
    }

}
