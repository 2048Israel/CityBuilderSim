package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.FamilyModel;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Markets;
import ham.citybuildersim.PayTier;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Trade;

import java.util.List;
import java.util.Map;

/**
 * The landlords. Own every home in the city and let them by the month.
 *
 * THE LARGEST SECTOR CLASS, because rent is the most-argued price in the
 * game and none of the argument belongs in the template. What it overrides:
 *
 *   the price     two rents, per person of dwelling capacity - family doors
 *                 and studios - each walking a twelfth of the way a month
 *                 toward a target struck from what a new home costs, how
 *                 tight its segment is, and what the standing stock costs to
 *                 hold. See TWO RENTS and RENT AS A MARKET below, kept from
 *                 the old CommercialHandler because the reasoning is the
 *                 mechanic.
 *   the sale      HOUSING to the households, billed off the actual match -
 *                 FamilyModel puts households behind doors that fit them and
 *                 hands back the weight each segment bills on.
 *   planning      off jobs, not population: population is capped by housing,
 *                 so a landlord that watched population would conclude
 *                 demand had stopped exactly when it was the one causing
 *                 the shortage. And a shortage of family doors is its own
 *                 reason to build, which more studios cannot answer.
 *   shrinking     an occupied home is never scrapped out from under anyone,
 *                 and a segment with no doors to spare sheds nothing.
 *
 * Everything else - the books, the credit, the listing, the property tax on
 * the whole stock, the repair order it places with the builders - is the
 * template's.
 *
 * ======================================================================
 * RENT
 *
 * WHAT IT USED TO BE: $350 a month PER RESIDENT. Babies paid rent. Measured
 * on a city of 1,218, rent came to 106% of the entire wage bill and a large
 * family was bankrupt before it bought food. WHAT IT IS NOW: one household,
 * one rent, scaled by how big the home is - charged per person of DWELLING
 * CAPACITY, not per resident, because a flat is what a landlord charges for.
 *
 * RENT AS A MARKET. Jerus: "real estates margin, plus homes vs households
 * both ways, with lag", and rent should be free to leave affordability
 * entirely rather than being capped the way the shelf price is. A cost
 * floor, a scarcity multiple, and a lag:
 *
 *   THE FLOOR is what it costs to supply one more person of capacity - the
 *   cheapest residential building the city could put up, at today's
 *   materials and today's land, over the return a landlord wants. Expensive
 *   land and expensive concrete reach households through their rent.
 *   THE MULTIPLE is households against front doors, both ways and unbounded.
 *   Short of doors, rent rises without limit; overbuild and it falls below
 *   cost and the landlords eat it, which is what makes overbuilding a real
 *   mistake rather than a free one.
 *   THE LAG is a twelve-month lease: a twelfth of tenancies come up for
 *   renewal each month, so a twelfth of the gap closes each month.
 *
 * Unbounded is safe because dear rent empties the city - through the
 * household balance sheet, which discharges households that cannot meet
 * their fixed costs and sends a share of them away. The runaway has a brake
 * with people in it.
 *
 * TWO RENTS, BECAUSE THERE ARE TWO DECISIONS. Jerus: "theyll rent at zero
 * profit, but they wont build more if new rent is zero profit." THE FLOOR -
 * rentBreakEven() - is what the EXISTING stock costs to hold this month, per
 * person of capacity: this month's maintenance, property tax and interest
 * over the capacity they are spread across, and rent never goes below it.
 * THE HURDLE - EconomyManager.housingBuildHurdle() - is what a NEW building
 * would have to earn, and it gates the crane, not the price.
 *
 * TWO SEGMENTS. A door a child is not allowed in is a different good with
 * its own scarcity and its own price; one price for both let a city hold
 * 25,000 spare studios while ten thousand families doubled up. Each segment
 * is priced off the cheapest building THAT SEGMENT can supply, on its own
 * pressure, and the two legs are lifted together only when their blend falls
 * short of the company's carry - see targetFor() and carryLift().
 * ======================================================================
 */
public final class RealEstate extends Sector {

    /** What share of a working household's income rent should take. The affordability yardstick. */
    public static final double TARGET_RENT_BURDEN = .30;

    /** The household the yardstick is struck against: a couple both working... */
    public static final int REFERENCE_EARNERS = 2;

    /** ...in a home for four. A yardstick, not the House's spec, which grew to six. */
    public static final int REFERENCE_HOME_CAPACITY = 4;

    /**
     * What a landlord wants back each year for what the building cost. 5.80%
     * is the Global Property Guide gross rental yield for Canada; 17.3% was
     * what left the opening rent where it was on the day rent became a
     * market, and was paid off with the rebalance of 2026-09-09.
     */
    public static final double LANDLORD_YIELD = .058;

    /**
     * Repairs: one percent a year of what a building cost to put up, placed
     * as a real order with the builders in the building's own inputs. About
     * $34 a home a month; a tenth to a fifth of gross rent, which is what
     * real landlords spend.
     */
    public static final double MAINTENANCE_PER_YEAR = .01;

    /** How hard rent answers a shortage of front doors. One: households per door is the multiple. */
    public static final double SCARCITY_ELASTICITY = 1.0;

    /** The lease, in months, which is also the lag. */
    public static final int LEASE_MONTHS = 12;

    /** The margin a new building has to clear over its own costs to be worth putting up. */
    public static final double BUILD_MARGIN = .33;

    /** Below this many front doors the ratio stops meaning anything. */
    private static final int MIN_HOMES_FOR_A_MARKET = 10;

    /* ------------------------------ the prices ------------------------------ */

    /** Rent per person of dwelling capacity, family doors. A LAGGED STATE, saved. */
    private double rentPrice = rentFor(PayTier.UNSKILLED.getMonthlyWage());

    /** The studio price, per person of capacity. Opens equal and goes its own way. */
    private double studioRentPrice = rentFor(PayTier.UNSKILLED.getMonthlyWage());

    private double lastRentTarget;
    private double lastStudioTarget;

    /* ------------------------------ the doors ------------------------------ */

    private int homes;
    private double occupiedHomes;
    /** Household CAPACITY - the people the residential buildings hold. */
    private int household;
    private int population;
    private double householdCount;
    private double marginalHousingCost;

    /** What supplying one more person of capacity costs, land included - the cheapest home. */
    private double structurePerCapacity;
    private double landPerCapacity;
    /** ...and per segment, off the cheapest building that segment can supply. */
    private double studioCostPerCapacity;
    private double familyCostPerCapacity;

    /** People the residential buildings hold, sites included - the break-even's denominator. */
    private double ownedCapacity;

    private int studioHomes, familyHomes;
    private double studioSeekers, familySeekers;
    private double studioSeekerHeads, familySeekerHeads;

    /** What the households behind the doors add up to, per segment. Set by FamilyModel.house(). */
    private double studioRentWeight, familyRentWeight;
    /** The weights the month's rent was actually billed on. */
    private double rBilledStudioWeight, rBilledFamilyWeight;

    private double rRentIncome;

    public RealEstate() {
        super("Real Estate", "Real Estate", BuildingType.RESIDENTIAL);
        makes(Good.HOUSING);
        blurb("Owns every home and lets them by the month. Rent walks toward what "
                + "a new home costs times how many households are chasing each door, "
                + "and never below what the standing stock costs to hold.");
    }

    /** Rent per person of capacity at a given unskilled wage: the founding value and the yardstick. */
    public static double rentFor(double unskilledWage) {
        return TARGET_RENT_BURDEN * (REFERENCE_EARNERS * unskilledWage) / REFERENCE_HOME_CAPACITY;
    }

    /* ===================================================================
       INPUTS FROM THE CITY, set each month by EconomyManager and Game
       =================================================================== */

    public void setHomes(int homes) { this.homes = Math.max(0, homes); }

    /**
     * How many doors have somebody behind them. CLAMPED TO THE STOCK: what
     * arrives is FamilyModel.homesNeeded(), which can exceed the doors when
     * households arrive after the housing pass - a landlord cannot let a
     * flat that does not exist.
     */
    public void setOccupiedHomes(double occupied) {
        double want = Math.max(0, occupied);
        this.occupiedHomes = homes > 0 ? Math.min(want, homes) : want;
    }

    public void setHousehold(int capacity)           { this.household = Math.max(0, capacity); }
    public void setPopulation(int population)        { this.population = Math.max(0, population); }
    public void setHouseholdCount(double count)      { this.householdCount = Math.max(0, count); }
    public void setMarginalHousingCost(double perCapacity) { this.marginalHousingCost = Math.max(0, perCapacity); }

    public void setHousingCosts(double structurePerCapacity, double landPerCapacity) {
        this.structurePerCapacity = Math.max(0, structurePerCapacity);
        this.landPerCapacity = Math.max(0, landPerCapacity);
    }

    public void setSegmentHousingCosts(double studioPerCapacity, double familyPerCapacity) {
        this.studioCostPerCapacity = Math.max(0, studioPerCapacity);
        this.familyCostPerCapacity = Math.max(0, familyPerCapacity);
    }

    public void setOwnedHousingCapacity(double capacity) { this.ownedCapacity = Math.max(0, capacity); }

    public void setSegments(int studioHomes, int familyHomes,
                            double studioSeekers, double familySeekers,
                            double studioSeekerHeads, double familySeekerHeads) {
        this.studioHomes = Math.max(0, studioHomes);
        this.familyHomes = Math.max(0, familyHomes);
        this.studioSeekers = Math.max(0, studioSeekers);
        this.familySeekers = Math.max(0, familySeekers);
        this.studioSeekerHeads = Math.max(0, studioSeekerHeads);
        this.familySeekerHeads = Math.max(0, familySeekerHeads);
    }

    /** Both halves, from FamilyModel.house(). */
    public void setRentWeight(double studio, double family) {
        this.studioRentWeight = Math.max(0, studio);
        this.familyRentWeight = Math.max(0, family);
    }

    /** The old single-weight setter: everything in the family half. */
    public void setRentWeight(double weight) { setRentWeight(0, weight); }

    /* ------------------------------ readers ------------------------------ */

    public int getHomes()                    { return homes; }
    public double getOccupiedHomes()         { return occupiedHomes; }
    public int getHousehold()                { return household; }
    public double getHouseholdCount()        { return householdCount; }
    public double getMarginalHousingCost()   { return marginalHousingCost; }
    public double getStructurePerCapacity()  { return structurePerCapacity; }
    public double getLandPerCapacity()       { return landPerCapacity; }
    public double getStudioCostPerCapacity() { return studioCostPerCapacity; }
    public double getFamilyCostPerCapacity() { return familyCostPerCapacity; }
    public double getOwnedHousingCapacity()  { return ownedCapacity; }
    public int getStudioHomes()              { return studioHomes; }
    public int getFamilyHomes()              { return familyHomes; }
    public double getStudioSeekers()         { return studioSeekers; }
    public double getFamilySeekers()         { return familySeekers; }
    public double getStudioSeekerHeads()     { return studioSeekerHeads; }
    public double getFamilySeekerHeads()     { return familySeekerHeads; }
    public double getRentWeight()            { return studioRentWeight + familyRentWeight; }
    public double getStudioRentWeight()      { return studioRentWeight; }
    public double getFamilyRentWeight()      { return familyRentWeight; }
    public double getBilledRentWeight()      { return rBilledStudioWeight + rBilledFamilyWeight; }
    public double getBilledStudioWeight()    { return rBilledStudioWeight; }
    public double getBilledFamilyWeight()    { return rBilledFamilyWeight; }
    public double getRentPrice()             { return rentPrice; }
    public double getStudioRentPrice()       { return studioRentPrice; }
    public double getRentTarget()            { return lastRentTarget > 0 ? lastRentTarget : rentPrice; }
    public double getStudioRentTarget()      { return lastStudioTarget > 0 ? lastStudioTarget : studioRentPrice; }
    /** Rent billed in the month last sold - the figure the households were charged. */
    public double getRentIncomeBilled()      { return rRentIncome; }

    public void setRentPrice(double price)       { if (price > 0) rentPrice = price; }
    public void setStudioRentPrice(double price) { if (price > 0) studioRentPrice = price; }

    /** Capacity per front door, averaged over whatever the city has built. */
    public double averageHomeSize() { return homes > 0 ? household / (double) homes : 0; }

    /** Average household size in one segment. One when there is nobody to average. */
    public double averageHouseholdSize(boolean family) {
        double heads = family ? familySeekerHeads : studioSeekerHeads;
        double count = family ? familySeekers : studioSeekers;
        return count > 0 && heads > 0 ? heads / count : 1;
    }

    /* ===================================================================
       THE PRICE
       =================================================================== */

    /**
     * What the standing stock costs to hold this month, per person of
     * capacity - this month's actual maintenance, property tax and interest
     * over the capacity they are spread across. Over CAPACITY rather than
     * what is let, so an empty flat is a loss the landlord eats rather than
     * a reason to raise the rent on the full ones. Over what the COMPANY
     * OWNS, not the city's whole housing capacity, which includes the hundred
     * heads the city houses for free before any landlord exists.
     */
    public double rentBreakEven() {
        if (ownedCapacity <= 0) return 0;
        double carry = getMaintenanceExpense() + getPropertyTaxExpense() + getInterestExpense();
        return carry > 0 ? carry / ownedCapacity : 0;
    }

    /** What a landlord WANTS on a new building, before scarcity: the yield on what the cheapest home costs. */
    public double rentRequired() {
        double full = structurePerCapacity + landPerCapacity;
        if (full <= 0) return marginalHousingCost * LANDLORD_YIELD / 12;
        return full * LANDLORD_YIELD / 12;
    }

    /** The same required return, on the cheapest home THIS segment can supply. */
    public double rentRequired(boolean family) {
        double full = family ? familyCostPerCapacity : studioCostPerCapacity;
        if (full <= 0) return rentRequired();
        return full * LANDLORD_YIELD / 12;
    }

    /** The floor under rent: the measured break-even, or the required return while there is nothing to measure. */
    public double rentFloor() {
        double carry = rentBreakEven();
        return carry > 0 ? carry : rentRequired();
    }

    /** Households per front door: above one is a shortage, below one a glut. */
    public double housingPressure() {
        if (homes < MIN_HOMES_FOR_A_MARKET || householdCount <= 0) return 1;
        return householdCount / homes;
    }

    private double segmentPressure(int doors, double seekers) {
        if (doors < MIN_HOMES_FOR_A_MARKET || seekers <= 0) return housingPressure();
        return seekers / doors;
    }

    public double studioPressure() { return segmentPressure(studioHomes, studioSeekers); }
    public double familyPressure() { return segmentPressure(familyHomes, familySeekers); }

    public double rentScarcityMultiple() { return scarcityMultipleOf(housingPressure()); }

    public double scarcityMultipleOf(double pressure) {
        if (pressure <= 0) return 1;
        return Math.pow(pressure, SCARCITY_ELASTICITY);
    }

    public double rentTarget()       { return targetFor(familyPressure(), true); }
    public double studioRentTarget() { return targetFor(studioPressure(), false); }

    /**
     * One segment's target: what its own next building costs, times how
     * tight that segment is, lifted so the COMPANY still covers its carry.
     * The floor is a portfolio number and not a floor on a leg - a shortfall
     * against the carry is made up on the whole portfolio, so the two legs
     * keep the distance their costs put between them and the cheaper market
     * subsidises the dearer one, exactly as one company's rent roll does.
     */
    private double targetFor(double pressure, boolean family) {
        double required = rentRequired(family);
        double breakEven = rentBreakEven();
        if (required <= 0) return breakEven > 0 ? breakEven : rentPrice;
        return required * scarcityMultipleOf(pressure) * carryLift();
    }

    /**
     * How far short of the carry the two legs come, struck on their own
     * costs, weighted by what is actually billed. Never below one: a
     * portfolio already covering its carry is not asked to charge more.
     */
    private double carryLift() {
        double breakEven = rentBreakEven();
        if (breakEven <= 0) return 1;
        double sw = Math.max(0, studioRentWeight), fw = Math.max(0, familyRentWeight);
        double weight = sw + fw;
        if (weight <= 0) return 1;
        double blend = (sw * rentRequired(false) * scarcityMultipleOf(studioPressure())
                      + fw * rentRequired(true) * scarcityMultipleOf(familyPressure())) / weight;
        if (blend <= 0) return 1;
        return Math.max(1, breakEven / blend);
    }

    /** Moves each rent a lease-length closer to what its market says it should be. */
    public void repriceRent() {
        double target = rentTarget();
        if (target > 0) {
            lastRentTarget = target;
            rentPrice += (target - rentPrice) / LEASE_MONTHS;
            if (rentPrice < 0) rentPrice = 0;
        }
        double studioTarget = studioRentTarget();
        if (studioTarget > 0) {
            lastStudioTarget = studioTarget;
            studioRentPrice += (studioTarget - studioRentPrice) / LEASE_MONTHS;
            if (studioRentPrice < 0) studioRentPrice = 0;
        }
    }

    /** What one person of capacity actually cost on average this month - the price index's figure. */
    public double getAverageRentPaid() {
        double weight = studioRentWeight + familyRentWeight;
        if (weight <= 0) return rentPrice;
        return (studioRentWeight * studioRentPrice + familyRentWeight * rentPrice) / weight;
    }

    /** Where that average is heading - the invariant HousingCheck asserts the break-even against. */
    public double blendedRentTarget() {
        double sw = Math.max(0, studioRentWeight), fw = Math.max(0, familyRentWeight);
        double weight = sw + fw;
        if (weight <= 0) return getRentTarget();
        return (sw * studioRentTarget() + fw * rentTarget()) / weight;
    }

    /**
     * The month's rent, billed off the actual match: each segment's weight
     * at its own price. Two fallbacks, both real: a city with no homes
     * recorded, and the load path before the first housing pass.
     */
    public double getRentIncome() {
        double weight = studioRentWeight + familyRentWeight;
        if (weight > 0) return studioRentWeight * studioRentPrice + familyRentWeight * rentPrice;
        if (homes <= 0) return Math.min(household, population) * rentPrice;
        return Math.min(occupiedHomes, homes) * averageHomeSize() * rentPrice;
    }

    /* ===================================================================
       THE SALE, at the bottom of the month
       =================================================================== */

    @Override
    public void sellOwnPriced(Markets markets, Game game) {
        GoodsMarket m = markets.get(Good.HOUSING);
        rBilledStudioWeight = studioRentWeight;
        rBilledFamilyWeight = familyRentWeight;
        double income = getRentIncome();
        rRentIncome = income;
        if (income <= 0) return;
        if (studioRentWeight + familyRentWeight > 0) {
            if (studioRentWeight > 0) bookSale(m.record(key(), Trade.HOUSEHOLDS, studioRentWeight, studioRentPrice));
            if (familyRentWeight > 0) bookSale(m.record(key(), Trade.HOUSEHOLDS, familyRentWeight, rentPrice));
        } else {
            double let = income / Math.max(rentPrice, 1e-12);
            bookSale(m.record(key(), Trade.HOUSEHOLDS, let, rentPrice));
        }
    }

    /**
     * Rent walks here, and only here: moving a lagged price one step toward
     * its target IS a month passing, and the load path must not do it.
     */
    @Override
    public void endOfMonth(Game game) {
        repriceRent();
    }

    /** The landlords hold no stock: a home is not a unit in a warehouse. */
    @Override
    public double getInventoryValue() { return 0; }

    /* ===================================================================
       PLANNING - off jobs, and off the segment that is short
       =================================================================== */

    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        int totalJobs = game.getPopulationManager().getTotalJobs();
        int housingCapacity = game.getHouseholdCapacity();
        double output = game.getConstructionOutput();

        /*
         * Population is min(housing, jobs x 2.25), so housing demand IS the
         * job market - and the job market a block of flats will open into is
         * not the one standing when it is ordered. Jobs on site count: they
         * are committed, funded and visible. A population trend does not: it
         * would be reading the landlords' own echo.
         */
        int jobsComing = buildings.getJobsUnderConstruction();
        double latentDemand = (totalJobs + jobsComing) * 2.25;
        double headShortfall = latentDemand - housingCapacity * (1 + BusinessInvestment.TARGET_HEADROOM);

        // A shortage of family doors is its own reason to build, and one that
        // building more studios cannot answer.
        double familyShortfall = doorShortfall(true);

        if (headShortfall <= 0 && familyShortfall <= 0) {
            return BusinessInvestment.Decision.no(sector, String.format(
                    "housing ahead of jobs (%d now, %d coming)", totalJobs, jobsComing));
        }

        BuildingsTemplate best = null;
        double bestScore = 0, bestDoors = 0;
        String blocked = null;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.getCapacity() <= 0) continue;

            // What it would actually let, not what it holds.
            double doors = fillableDoors(t, headShortfall);
            if (doors <= 0) {
                blocked = "nobody the city has could live in one";
                continue;
            }
            double price = priceForSegment(t);

            // WHETHER to build is asked at full occupancy; WHICH on the doors
            // the city would actually fill. Asking both on fillable doors
            // stopped a city building housing entirely.
            double hurdle = game.getEconomyManager().housingBuildHurdle(t);
            if (hurdle > 0 && t.getCapacity() * price < hurdle) {
                blocked = String.format("rent does not cover a new %s", t.getName());
                continue;
            }

            double income = doors * FamilyModel.rentWeightOf(t.homeSize()) * price;
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = income / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
                bestDoors = doors;
            }
        }

        if (best == null) {
            return BusinessInvestment.Decision.no(sector, blocked != null ? blocked : "nothing worth building");
        }

        // Sized on whichever shortage is the bigger.
        double byHeads = latentDemand - housingCapacity;
        double byDoors = bestDoors * FamilyModel.rentWeightOf(best.homeSize());
        int quantity = plans.orderSize(Math.max(byHeads, byDoors), best.getCapacity(), best, output);
        if (quantity <= 0) return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));

        return new BusinessInvestment.Decision(sector, best, quantity,
                familyShortfall > 0 && headShortfall <= 0
                        ? String.format("%,.0f households need a door a child is allowed in", familyShortfall)
                        : String.format("%,.0f unhoused demand against %,d units", byHeads, housingCapacity),
                true);
    }

    /** Households in one segment with no door of their own, or fewer than none. */
    public double doorShortfall(boolean family) {
        return family ? familySeekers - familyHomes : studioSeekers - studioHomes;
    }

    /** Whether the segment this template belongs to has spare doors. */
    private boolean hasDoorsToSpare(BuildingsTemplate t) {
        boolean family = t.homeSize() > FamilyModel.STUDIO_MAX_SIZE;
        return doorShortfall(family) < 0;
    }

    /** The head shortage plan() would see if it were asked right now - for the credit check. */
    private double latentHeadShortfall() {
        int standing = 0;
        for (int n : buildings.getTotalJobs()) standing += n;
        double latent = (standing + buildings.getJobsUnderConstruction()) * 2.25;
        return latent - buildings.getTotalHouseCapacity() * (1 + BusinessInvestment.TARGET_HEADROOM);
    }

    /**
     * How many of this template's doors the city would actually put somebody
     * in: a studio only a studio-seeker; a family unit a family first and
     * then a studio-seeker the studios have no room for. Capped at what the
     * building has, plus the people who have not arrived yet, split between
     * the segments in the proportion the city's own households take.
     */
    private double fillableDoors(BuildingsTemplate t, double headShortfall) {
        int units = t.getDwellings() > 0 ? t.getDwellings() : Math.max(1, t.getCapacity() / 4);
        boolean family = t.homeSize() > FamilyModel.STUDIO_MAX_SIZE;
        double studioNeeded = doorsNeeded(false, headShortfall);
        if (!family) return Math.min(units, studioNeeded);
        return Math.min(units, doorsNeeded(true, headShortfall) + studioNeeded);
    }

    /**
     * Doors one segment is short: households here with nowhere, plus the
     * ones the job market is about to bring, at the segment's own household
     * size. The shortfall is SIGNED: arrivals move into the empty flats that
     * are already standing before they need new ones.
     */
    private double doorsNeeded(boolean family, double headShortfall) {
        double here = doorShortfall(family);
        if (headShortfall <= 0) return Math.max(0, here);
        double heads = studioSeekerHeads + familySeekerHeads;
        double share = heads > 0 ? (family ? familySeekerHeads : studioSeekerHeads) / heads : .5;
        double coming = headShortfall * share / Math.max(1, averageHouseholdSize(family));
        return Math.max(0, here + coming);
    }

    /** The price the segment this template belongs to is charging. */
    public double priceForSegment(BuildingsTemplate t) {
        return t.homeSize() <= FamilyModel.STUDIO_MAX_SIZE ? studioRentPrice : rentPrice;
    }

    /**
     * What the city's households would pay for it, not what it would collect
     * if it were full. THE SAME VALUATION plan() uses, and it has to be: this
     * decides whether the sector may BORROW for the building.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        int units = t.getDwellings() > 0 ? t.getDwellings() : Math.max(1, t.getCapacity() / 4);
        FamilyModel families = plans.families();
        if (families == null) return t.getCapacity() * rentPrice;
        double doors = Math.min(units, fillableDoors(t, latentHeadShortfall()));
        if (doors <= 0) doors = units * (families.marginalRentWeight(t.homeSize()) > 0 ? 1 : 0);
        return doors * FamilyModel.rentWeightOf(t.homeSize()) * priceForSegment(t);
    }

    /** Housing: demand is people actually living in it. Empty units can go; occupied ones never. */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        return new double[] { population, game == null ? household : game.getHouseholdCapacity() };
    }

    /** A residential holding is only sheddable if ITS OWN segment has doors to spare. */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        return hasDoorsToSpare(t);
    }

    @Override
    public String noRetirementReason(boolean distress) {
        return distress ? "overdrawn, but every door it owns is wanted"
                        : "losing money, but every door it owns is wanted";
    }

    /** People, not doors: the measure the spare-capacity rule counts in. */
    @Override
    public double unitsOf(BuildingsTemplate t) { return t.getCapacity(); }

    /* ===================================================================
       THE SCREEN
       =================================================================== */

    @Override
    public String inputLabel() { return "Repairs"; }

    @Override
    public List<Line> operations(Game game) {
        Formats f = Formats.INSTANCE;
        List<Line> lines = new java.util.ArrayList<>();
        lines.add(Line.head("The doors"));
        lines.add(Line.of("Homes owned", f.count(homes)));
        lines.add(Line.of("Let", f.count(occupiedHomes)));
        double empty = Math.max(0, homes - occupiedHomes);
        lines.add(Line.of("Standing empty", f.count(empty), empty > 0 ? Line.Tone.WARN : Line.Tone.GOOD));

        double perDoor = game == null ? 0 : game.getHouseholds().rentPerHousehold();
        double beds = rentPrice > 0 ? perDoor / rentPrice : 0;

        lines.add(Line.head("Family homes, per person of capacity"));
        lines.add(Line.of("Charged now", f.cash(rentPrice)));
        lines.add(Line.of("Heading for", f.cash(getRentTarget()),
                getRentTarget() > rentPrice ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("Households per door", String.format("%.2f", familyPressure()),
                familyPressure() > 1.2 ? Line.Tone.BAD : familyPressure() > 1 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.note("Rent moves toward its target a fraction of the gap a month rather than "
                + "jumping — leases do not all end in the same week. So a rent that is "
                + "about to rise is visible here months before it is felt."));

        lines.add(Line.head("Studios and one-beds"));
        lines.add(Line.of("Charged now", f.cash(studioRentPrice)));
        lines.add(Line.of("Heading for", f.cash(getStudioRentTarget()),
                getStudioRentTarget() > studioRentPrice ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("Households per door", String.format("%.2f", studioPressure()),
                studioPressure() > 1.2 ? Line.Tone.BAD : studioPressure() > 1 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("Doors", f.count(studioHomes) + " against " + f.count(familyHomes) + " family"));
        lines.add(Line.note("Nobody with a child may live in a studio, so the two are priced as two "
                + "markets. If the family figure is high and this one is low, the city is "
                + "not short of housing — it is short of the right shape of it, and "
                + "building more studios will not touch it."));

        lines.add(Line.of(beds > 0 ? String.format("What one let home pays, billed for %.1f", beds)
                                   : "What one let home pays",
                perDoor > 0 ? f.cash(perDoor) : "not struck yet",
                perDoor > 0 ? Line.Tone.HEAD : Line.Tone.MUTED));
        lines.add(Line.note("The price above is per head of capacity, not per front door. This line is "
                + "the same money over the other denominator, read from the households' own "
                + "ledger — it is the rent figure the Population screens print."
                + (perDoor > 0 ? "" : " It is struck when a month closes, so it reads nothing"
                        + " until you press Next Month.")));

        lines.add(Line.head("What sets the price"));
        lines.add(Line.of("The cost of the next home", f.cash(structurePerCapacity + landPerCapacity)));
        lines.add(Line.note(String.format("Per person of capacity: %s of building and %s of ground. What it would "
                + "cost to put up one more, which is what a balanced market pays for. "
                + "Cheap land and cheap materials are a rent policy.",
                f.cash(structurePerCapacity), f.cash(landPerCapacity))));
        lines.add(Line.of("They will not go below", f.cash(rentBreakEven()), Line.Tone.MUTED));
        lines.add(Line.note("Repairs, property tax and interest on what the company already owes, "
                + "over every head its buildings hold — what the standing stock costs "
                + "to hold whether anyone is in it or not. Rent can be pushed down to "
                + "this by a glut and no further: below it the landlords are paying to "
                + "house people. An empty home still costs this, which is why building "
                + "doors nobody wants is not free."));
        return lines;
    }

    /* ===================================================================
       SAVE, RESET, THE REFORM
       =================================================================== */

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        extras.put("rentPrice", rentPrice);
        extras.put("studioRentPrice", studioRentPrice);
        extras.put("lastRentTarget", lastRentTarget);
        extras.put("lastStudioTarget", lastStudioTarget);
        extras.put("studioRentWeight", studioRentWeight);
        extras.put("familyRentWeight", familyRentWeight);
        extras.put("billedStudioWeight", rBilledStudioWeight);
        extras.put("billedFamilyWeight", rBilledFamilyWeight);
        extras.put("rentIncome", rRentIncome);
        extras.put("homes", (double) homes);
        extras.put("occupiedHomes", occupiedHomes);
    }

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        rentPrice = extras.getOrDefault("rentPrice", rentPrice);
        studioRentPrice = extras.getOrDefault("studioRentPrice", studioRentPrice);
        lastRentTarget = extras.getOrDefault("lastRentTarget", 0.0);
        lastStudioTarget = extras.getOrDefault("lastStudioTarget", 0.0);
        studioRentWeight = extras.getOrDefault("studioRentWeight", 0.0);
        familyRentWeight = extras.getOrDefault("familyRentWeight", 0.0);
        rBilledStudioWeight = extras.getOrDefault("billedStudioWeight", 0.0);
        rBilledFamilyWeight = extras.getOrDefault("billedFamilyWeight", 0.0);
        rRentIncome = extras.getOrDefault("rentIncome", 0.0);
        homes = (int) Math.round(extras.getOrDefault("homes", 0.0));
        occupiedHomes = extras.getOrDefault("occupiedHomes", 0.0);
    }

    @Override
    protected void resetExtras() {
        rentPrice = rentFor(PayTier.UNSKILLED.getMonthlyWage());
        studioRentPrice = rentPrice;
        lastRentTarget = lastStudioTarget = 0;
        studioRentWeight = familyRentWeight = 0;
        rBilledStudioWeight = rBilledFamilyWeight = 0;
        rRentIncome = 0;
        homes = 0; occupiedHomes = 0; household = 0; population = 0; householdCount = 0;
        marginalHousingCost = structurePerCapacity = landPerCapacity = 0;
        studioCostPerCapacity = familyCostPerCapacity = ownedCapacity = 0;
        studioHomes = familyHomes = 0;
        studioSeekers = familySeekers = studioSeekerHeads = familySeekerHeads = 0;
    }

    /** The prices and the per-capacity costs are money; the doors, the weights and the rates are not. */
    @Override
    protected void redenominateExtras(double scale) {
        rentPrice *= scale;
        studioRentPrice *= scale;
        lastRentTarget *= scale;
        lastStudioTarget *= scale;
        rRentIncome *= scale;
        marginalHousingCost *= scale;
        structurePerCapacity *= scale;
        landPerCapacity *= scale;
        studioCostPerCapacity *= scale;
        familyCostPerCapacity *= scale;
    }
}
