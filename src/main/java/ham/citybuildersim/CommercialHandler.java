
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class CommercialHandler {

    private double[] fillRate = new double[11];
    private double[] storeWages = new double[11];

    /**
     * The bank's share of those jobs, so its wages can be told from the shops'.
     *
     * A Commercial Bank is a COMMERCIAL building, so its two hundred and
     * seventy-eight staff have always been inside this sector's payroll - which
     * was fine while the bank had no books of its own and absurd the moment it
     * did. The shops were paying the tellers.
     */
    private final double[] bankWages = new double[11];
    private double rBankPayroll;

    /** What the bank's own staff cost this month - charged to the bank, not here. */
    public double getReportBankPayroll() { return rBankPayroll; }

    private int population;
    private int household;

    //energy stuff
    private double energyRatio = 1;
    private double pricePerWatt;
    private double electricity;

    // Water throttles output the same way power does: a store with no water
    // cannot trade. Defaults to 1 so a handler that is never told otherwise
    // behaves exactly as it did before water existed.
    private double waterRatio = 1;
    private double pricePerWaterUnit;
    private double water;

    /**
     * How much of its trade congestion lets it actually do.
     *
     * A third throttle beside energyRatio and waterRatio, and it multiplies with
     * them rather than replacing them: a gridlocked city in a brownout is worse
     * off than either alone. Defaults to 1 so a handler nobody tells about roads
     * behaves exactly as it did before they existed.
     */
    private double roadRatio = 1;

    /**
     * How much of the month's work an unwell workforce actually did.
     *
     * A FOURTH throttle beside energyRatio, waterRatio and roadRatio, and it
     * multiplies with them for the same reason they multiply with each other.
     * It is different from the three in one way that matters: it cuts OUTPUT
     * ONLY and never payroll. The staff are on the books and get paid whether
     * they came in or not - see Health for why that is the point of the whole
     * mechanic. Defaults to 1, so a handler nobody tells about sickness behaves
     * exactly as it did before it existed.
     */
    private double healthRatio = 1;

    /* ------------------------- the ratio basis -------------------------
       What the month was actually TRADED at, which is not always what the
       city looks like by the time anyone reads the report.

       Same problem as storeFillBasis and the same answer. A month's income
       statement runs at the START of the month, off the utilisation figures
       the previous month left behind; simulateMonth() then moves them. Save
       after that and the live game's report and a reloaded game's recompute
       disagree - the reload prices the month at ratios it was never traded
       at. It showed up the day roads arrived, as $0.51 of income appearing
       out of a reload, because energy and water are 1 in almost every city
       and roads are the first of the three that is routinely below it.

       Carried in the save for the same reason the property-tax charge is:
       it is a fact about a month, not a function of the state that month
       ended in.
       ------------------------------------------------------------------- */
    private double bEnergyRatio = 1;
    private double bWaterRatio = 1;
    private double bRoadRatio = 1;
    /** Sickness, carried for the same reason - see Health. */
    private double bHealthRatio = 1;

    //store variables
    private double averageStoreFill;
    private int storeCoverage;
    private int storeCapacity;
    private int storeInventory;
    /** Units the shops sold last month. Drives the restock target. */
    private int lastMonthSales;
    private int productsSold;
    private double storeInventoryCost;
    private double commercialCash;
    private double realEstateCash;



    //industrial variables
    private int foodAvailableForSale; //food available for sale from industry
    private double foodPrice;

    //printing variables
    private int pLocalImport;
    private int pGlobalImport;
    private double pTaxRate;
    private double rNetIncome;
    private double rGrossRevenue;
    private double rImportTax;

    /* -----------------------------------------------------------------------
       MONTHLY REPORT RESULTS
       -----------------------------------------------------------------------
       NOTE: all of the values below used to be computed as local variables
       inside printCommercialInfo(), which also mutated commercialCash,
       realEstateCash, productsSold, rGrossRevenue and rNetIncome as a side
       effect. That made the printer load-bearing for the economy in two bad
       ways:

         1. Opening the report re-ran the math and banked another month of net
            income - harmless in the terminal build (called once per month),
            but the JavaFX sector screen can be opened any number of times.
         2. printStartOfMonth() only calls it `if(reports)`, so switching
            reports OFF (which handleMultipleMonths() does automatically)
            silently zeroed commercial sales tax and commercial GDP.

       calculateCommercialResults() now owns the math and runs exactly once per
       month regardless of the reports setting. printCommercialInfo() and the
       JavaFX screen are both pure readers of these fields.
       ----------------------------------------------------------------------- */
    // Inputs snapshotted at calculation time. The screen used to read these
    // live off the handler while reading revenue/expenses from the snapshot,
    // so if an input changed in between, the report contradicted itself -
    // e.g. showing "Labor Fill Rate: 100%" next to a gross revenue that had
    // been computed with a fill rate of 0.
    private int rPopulation;
    private int rHousehold;
    private int rStoreCoverage;
    private int rStoreCapacity;
    private int rStoreInventory;
    private double rAverageStoreFill;
    private double rEnergyRatio;

    private int rDemand;
    private int rProductsSold;
    private double rPayroll;
    private double rInventoryCost;
    private double rElectricityCost;
    private double rWaterCost;
    private double rWaterRatio;
    private double rRoadRatio;

    /* Business credit. Two separate books, two separate loans, two rates. */
    private double retailInterestExpense;
    private double realEstateInterestExpense;

    /**
     * Property tax, charged monthly on land plus buildings.
     *
     * Real estate is the sector this lands on hardest, and correctly so: it
     * owns the entire housing stock and every lot under it, and its only
     * revenue is rent. It is also the first real cost real estate has ever had
     * besides interest - maintenance is still hardcoded to zero.
     */
    private double retailPropertyTax;
    private double realEstatePropertyTax;
    private double rRetailInterest;
    private double rRealEstateInterest;

    /* Balance sheet inputs, same shape as IndustrialHandler's. */
    private double retailLandValue;
    private double retailBuildingsValue;
    private double retailBondsPayable;
    private double realEstateLandValue;
    private double realEstateBuildingsValue;
    private double realEstateBondsPayable;
    private double rRetailPropertyTax;
    private double rRetailOperatingCost;
    private double rRetailOperatingIncome;
    private double rRetailNetIncome;

    private int rOccupiedUnits;
    private int rVacantUnits;
    private double rRentIncome;
    private double rPropertyMaintenance;
    private double rPropertyTaxExpense;
    private double rRealEstateExpenses;
    private double rRealEstateNetIncome;

    private double rTotalNetIncome;

    /* The tax each company owes, kept apart on purpose.
     *
     * The city has always taxed the two separately - a bad month in retail does
     * not shelter real estate's rent - but the report showed
     * rTotalNetIncome * rate, which nets them. So the screen could print a tax
     * bill the treasury never collected, and in a month where retail lost money
     * it printed a smaller one (or a negative one) than the city actually took.
     * Now the collected figure and the printed figure are the same number. */
    private double rRetailTax;
    private double rRealEstateTax;
    private double rTotalTax;

    /**
     * The sales tax rates on what the shop BUYS - not to be confused with
     * pTaxRate, which is the profit rate it is taxed on what it earns.
     *
     * `supplierSalesRate` is the food mill's rate, because under the
     * producer-rate design a supplier charges its own. `ownSalesRate` is
     * retail's, which is what an import is charged at since there is no local
     * supplier to have charged anything.
     *
     * Both default to zero so a handler nobody tells behaves as if it bought
     * tax-free, which is what a harness that never sets them expects.
     */
    private double supplierSalesRate;
    private double ownSalesRate;

    public void setPurchaseTaxRates(double supplierRate, double ownRate) {
        this.supplierSalesRate = Math.max(0, supplierRate);
        this.ownSalesRate = Math.max(0, ownRate);
    }

    /**
     * What the stock cost BEFORE the supplier's tax - live, then reported.
     *
     * The split matters and it caught me out. buyInventory() runs DURING the
     * month; computeMonthlyReport() runs at the START of one, off what the
     * previous month left behind - which is why rInventoryCost is assigned from
     * storeInventoryCost rather than computed. Feeding the tax ledger the LIVE
     * figure would have paired this month's purchases with last month's sales,
     * and the ledger uses report figures everywhere else for exactly that
     * reason (see the note on the mining line in EconomyManager).
     *
     * So these come in pairs, and the live half is carried in the save beside
     * storeInventoryCost, which is carried for the same reason.
     */
    private double localPurchaseValue;
    private double importPurchaseValue;
    private double rLocalPurchaseValue;
    private double rImportPurchaseValue;

    public double getLocalPurchaseValue()  { return localPurchaseValue; }
    public double getImportPurchaseValue() { return importPurchaseValue; }
    public void setPurchaseValues(double local, double imported) {
        this.localPurchaseValue = local;
        this.importPurchaseValue = imported;
    }

    public double getReportLocalPurchaseValue()  { return rLocalPurchaseValue; }
    public double getReportImportPurchaseValue() { return rImportPurchaseValue; }

    /** Months of recent sales a store tries to keep on the shelf. */
    private static final double STORE_COVER_MONTHS = 2.5;

    /** World price, supplied by FoodMarket. Imports cover whatever local supply can't. */
    private double importPrice = .20;

    //temporary variables
    /* ==================== WHAT THE SHOPS CHARGE ====================
     *
     * A CONSTANT until 2026-09-07, and the day the exchange rate started moving
     * that became untenable. The shops buy their stock at the world price
     * converted at the rate; they were selling it at $0.30 whatever they had
     * paid. So a devaluation raised what the city spent abroad and raised
     * nothing anybody was charged for it, and the national accounts came apart:
     * measured at C 30,136 against NX -31,052, which is a GDP of MINUS $727 in
     * a city of two hundred thousand people.
     *
     * This is the consumer-price half of "a devaluation is inflation", and the
     * half Jerus wanted to defer. It turns out not to be deferrable: if import
     * costs enter the economy but never reach a price anybody pays, the accounts
     * do not balance.
     *
     * COST-PLUS, WITH A LAG. The shops price at what their stock cost them plus
     * a margin, and move towards that over months rather than repricing the
     * shelf every time the currency twitches. Same shape as the wage drift in
     * LabourMarket, and for the same reason: a jump straight to the new price
     * hits households in one month with nothing smoothing it.
     */

    /** What the shops add to what their stock cost them. */
    public static final double RETAIL_MARKUP = 1.50;

    /** How fast the shelf catches up with the invoice. A quarter, roughly. */
    public static final double REPRICE_SPEED = .25;

    /** The price the game opened at, and the floor it will not go below. */
    public static final double OPENING_SELL_PRICE = .3;

    /**
     * The same floor, in TODAY's money.
     *
     * A money constant cannot survive a currency reform: after lopping two
     * zeros a thirty-cent floor would be a thirty-dollar floor in the old
     * unit, which is a hundredfold real increase nobody voted for. So the
     * constant above is the FOUNDING value and this is the live one, seeded
     * from it and divided along with every other price when the player reforms.
     * See Denomination.
     */
    private double openingSellPrice = OPENING_SELL_PRICE;

    public double getOpeningSellPrice() { return openingSellPrice; }

    private double storeSellPrice = OPENING_SELL_PRICE;

    /**
     * Moves the shelf price toward what the stock actually cost.
     *
     * Blended across what the shops actually bought: local food at the local
     * price, imports at the import price. A city buying everything at home is
     * insulated from the exchange rate, which is the whole point of having
     * substituted - and is why import substitution shows up as households being
     * protected rather than only as a better trade balance.
     */
    /**
     * How far above cost-plus a total shortage can push the shelf price.
     *
     * A shop that can meet a fifth of what its customers came for does not sell
     * at cost plus fifty per cent - it sells at whatever clears the shelf, and
     * what clears the shelf is whatever the people who can still afford it will
     * pay.
     *
     * TUNED DOWN FROM 2.5, AND THE REASON MATTERS MORE THAN THE NUMBER. At 2.5
     * five harnesses failed at once - nobody enrolled at the college, nobody
     * migrated in, the bank fixture stopped wanting a bank - because those
     * cities never build roads, so their delivery ratio sits at about a quarter
     * FOR EVER and the multiple pinned at its ceiling for three hundred years
     * while wages chased it two years behind. A permanent famine price is not
     * what a shortage does; it is what a shortage does if nothing is ever
     * fixed, which is a property of those fixtures rather than of cities.
     *
     * Sixty per cent over cost-plus at total shortage is a real ration and a
     * survivable one. If a city genuinely wants famine prices it can have them
     * by being genuinely unable to feed itself for a decade, and the sick rate
     * will say so.
     */
    public static final double MAX_SCARCITY_MULTIPLE = 1.6;

    /** Delivery share at which scarcity stops adding anything. */
    public static final double COMFORTABLE_DELIVERY = .95;

    /**
     * What the shops charge, and this is where prices learned to ration.
     *
     * COST-PLUS SETS THE FLOOR AND SCARCITY LIFTS IT. Before this the whole
     * method was the floor: `blendedCost x 1.50`, with no reference of any kind
     * to whether the shops could actually meet demand. A city whose shops could
     * hand over 46% of what households planned to buy charged exactly what a
     * city with full shelves charged, for ever.
     *
     * That is not a small omission, and its consequences were being read as
     * three other problems:
     *
     *   - 42% of the city was permanently short of food, because nothing ever
     *     rationed the shortage. A price that does not move cannot allocate.
     *   - the hunger term of the sick rate sat at its cap, and was written up
     *     as a health problem.
     *   - and there was no demand-pull inflation ANYWHERE in the model, which
     *     meant a policy interest rate would have had nothing to cool.
     *
     * WHAT IT CHANGES ABOUT HUNGER, said plainly because it is the point and it
     * is not kind. Today a shortage is shared: everybody gets less. After this,
     * a shortage is PRICED: the shelves clear and the households at the bottom
     * of the wage ladder are the ones who go without. That is what a shortage
     * does in an economy with prices in it, it is why bread riots are about
     * prices rather than about queues, and it turns hunger from a fact about
     * the city into a fact about who is poor in it - which is a thing the
     * player can act on, with the minimum wage, the sales tax, or more shops.
     *
     * @param plannedUnits demand people came with AND could pay for (rDemand)
     * @param deliveredUnits what the shops could actually hand over
     */
    public void repriceShelf(double localUnits, double localPrice,
                             double importUnits, double importPrice,
                             double plannedUnits, double deliveredUnits) {
        double units = Math.max(0, localUnits) + Math.max(0, importUnits);
        if (units <= 0) return;

        double blendedCost = (Math.max(0, localUnits) * Math.max(0, localPrice)
                + Math.max(0, importUnits) * Math.max(0, importPrice)) / units;
        if (blendedCost <= 0) return;

        double floor = Math.max(openingSellPrice, blendedCost * RETAIL_MARKUP);

        /*
         * The delivered share, and the multiple it justifies. At or above
         * COMFORTABLE_DELIVERY the shops met demand and there is nothing to
         * ration, so the price is cost-plus and this term is exactly 1.
         */
        double delivered = plannedUnits > 0
                ? Math.max(0, Math.min(1, deliveredUnits / plannedUnits))
                : 1;
        double shortage = Math.max(0, COMFORTABLE_DELIVERY - delivered)
                / COMFORTABLE_DELIVERY;
        double scarcity = 1 + shortage * (MAX_SCARCITY_MULTIPLE - 1);

        lastScarcityMultiple = scarcity;
        lastDeliveredShare = delivered;

        double target = floor * scarcity;
        storeSellPrice += (target - storeSellPrice) * REPRICE_SPEED;
    }

    private double lastScarcityMultiple = 1;
    private double lastDeliveredShare = 1;

    /** How much of the shelf price is the shortage rather than the cost. */
    public double getScarcityMultiple() { return lastScarcityMultiple; }

    /** The share of what customers came for that the shops could hand over. */
    public double getDeliveredShare() { return lastDeliveredShare; }

    /** What the shelf is heading towards, for the screen. */
    public double getShelfTarget() {
        return storeSellPrice;
    }

    public void setStoreSellPrice(double price) {
        if (price > 0) this.storeSellPrice = price;
    }

    /* =====================================================================
       RENT

       WHAT IT USED TO BE: $350 a month PER RESIDENT. Babies paid rent. So did
       pensioners, and children, and every teenager. Jerus, reading the new
       per-tier household statement: "not only is there no room for rent to
       increase... but already people are broke."

       He was right, and by more than it looked. Measured on a city of 1,218:
       rent came to 106% OF THE ENTIRE WAGE BILL. Per household, at the
       unskilled wage:

         couple, no children     2 people   $700 on $1,600   44%
         couple with a baby      3 people $1,049 on $1,600   66%
         couple, two children    4 people $1,400 on $1,600   87%
         LARGE FAMILY            6 people $2,099 on $1,600  131%
         senior living alone     1 person   $350 on nothing   -

       A childless couple was fine. Every child cost $350 a month and earned
       nothing, so a family was bankrupt before it bought food - and the model
       had no way to say so, because rent scaled with heads and nothing else.

       WHY IT WAS WRITTEN THAT WAY: there were no households. Residents were a
       single number, homes did not exist as a concept, and per-head was the only
       thing that COULD be written. `dwellings` and FamilyModel changed that.

       WHAT IT IS NOW: one household, one rent, scaled by how big the home is. A
       studio flat costs less than a house because it holds fewer people. Nobody
       is charged for their children.
       ===================================================================== */

    /** What share of a working household's income rent should take. */
    public static final double TARGET_RENT_BURDEN = .30;

    /**
     * The household and the home the price is set against.
     *
     * A couple both working, in a House - the starter residence, and the only
     * one a new city can afford. Its capacity is 4 across 1 dwelling, and
     * `HouseholdCheck` asserts that is still true, so if somebody re-costs the
     * House this derivation fails loudly instead of drifting.
     *
     * Deriving rather than typing a price is the point. The last four bugs in
     * this codebase were all constants that were correct when written and
     * silently invalidated by a change somewhere else; this one follows the wage
     * table, so raising pay raises rent with it.
     */
    public static final int REFERENCE_EARNERS = 2;

    /**
     * The home the affordability target is struck against - a family of four.
     *
     * AN AFFORDABILITY YARDSTICK, NOT THE HOUSE'S SPEC, and the two parted
     * company on 2026-09-07 when the House grew to six. Rent is charged per
     * person of dwelling capacity, so this number decides what one person of
     * capacity costs; a four-person home costs a working couple
     * TARGET_RENT_BURDEN of their wages, and a six-person House costs half as
     * much again. Moving this to six would have kept the House at the same
     * rent and made every flat in the city a third cheaper, which is the
     * opposite of what a bigger family home should mean.
     */
    public static final int REFERENCE_HOME_CAPACITY = 4;

    /**
     * Rent per person of DWELLING CAPACITY, not per resident.
     *
     * A house of capacity 4 costs 4 x this whether one person lives in it or
     * six. That is what a landlord actually charges for: the flat.
     *
     * A LAGGED STATE since 2026-09-07, not a number re-derived each month, and
     * that is the whole of what makes it a price rather than a formula. It is
     * therefore SAVED - see Game.captureSave. A flow cannot be reconstructed
     * from the state a month ended in and neither can a price that is halfway
     * to somewhere: a reloaded city would have jumped straight to its target
     * and the twelve months of lease stickiness would have vanished on every
     * load. That is the eighth time this codebase has been bitten by rebuilding
     * something instead of carrying it, and the first where the fix was written
     * before the bug.
     */
    private double rentPrice = rentFor(PayTier.UNSKILLED.getMonthlyWage());

    /* =====================================================================
       RENT AS A MARKET
       ---------------------------------------------------------------------
       Jerus, asked what scarcity should do to rent: "real estates margin, plus
       homes vs households both ways, with lag", and separately that rent should
       be free to leave affordability entirely rather than being capped the way
       the shelf price is.

       WHAT WAS WRONG WITH THE FORMULA. rentFor() is affordability and nothing
       else: 30% of two unskilled wages over four heads of capacity. It has
       never had any connection to what a home costs to build, to how many homes
       there are, or to how many households want one. Three consequences, and
       the third is the expensive one:

         - a city 60% short of housing charged exactly what a city with a spare
           room per household charged. A price that cannot rise cannot ration
           and cannot signal.
         - rent moved ONLY when wages moved, so raising the minimum wage raised
           rent by construction and the rent burden never changed. The dial the
           player had for making housing affordable could not make housing
           affordable.
         - AND IT IS WHY HOUSING NEVER GETS BUILT. BusinessInvestment values a
           residential building at what it would collect in rent. With rent
           pinned to wages, a desperate shortage did not make housing one penny
           more profitable to supply, so the sector looked at the same return in
           a crisis as in a glut and mostly declined. ShortageProbe was written
           to hunt for the bug that stopped construction; the bug was that
           nothing ever paid it to start. A hundred and ten years of a measured
           run went by with the city stuck at 191 homes and 329 households.

       WHAT IT IS NOW: a cost floor, a scarcity multiple, and a lag.

         THE FLOOR is what it costs to supply one more person of capacity - the
         cheapest residential building the city could put up, valued at today's
         materials and today's land, spread over the return a landlord wants for
         putting money in the ground. Rent therefore rises when building gets
         dear, which is a channel the game did not have: expensive land and
         expensive concrete now reach households through their rent.

         THE MULTIPLE is households against front doors, and it goes both ways.
         Short of doors, rent rises without limit. Overbuild and it falls
         without limit, below cost, and the landlords eat it - which is what a
         glut does and is what makes overbuilding a real mistake rather than a
         free one.

         THE LAG is a twelve-month lease. A twelfth of tenancies come up for
         renewal each month, so a twelfth of the gap to the new price closes
         each month. Nobody had to pick a number for this; the lease picked it.

       WHY UNBOUNDED IS SAFE HERE, which is not obvious. Unbounded rent in a
       permanently short city would run away for ever - except that dear rent
       empties the city. It goes out through the household balance sheet, which
       already discharges households that cannot meet their fixed costs and
       already sends a share of them away (HouseholdBalance.getLeavingCity, fed
       into Migration.setBankruptcyDepartures), and now also through the
       arrivals pull, which reads the rent burden. Fewer households against the
       same doors is a lower multiple. The runaway has a brake with people in
       it, which is the honest version of a cap.

       AND IT MAKES THE WAGE LOOP SAFER, which was the opposite of what I
       expected. Rent is about three quarters of the price index basket. Under
       the old formula the loop was rent -> index -> cost of living -> wage ->
       rent, closed, with a gain of about .77 at full pass-through: a hair under
       one, which is why full pass-through had to be measured so carefully. This
       cuts the last link. Wages no longer set rent; construction costs and
       housing scarcity do, and wages reach those only distantly through what
       materials cost to make. The loop that worried me most is now the loop
       this change opened up and mostly closed.
       ===================================================================== */

    /**
     * What a landlord wants back each year for what the building cost.
     *
     * READ OFF THE GAME, NOT CHOSEN. At founding the cheapest way to house
     * somebody is a House: $30 of cash and ten units of materials at $2.00,
     * which is $50 of structure for six people, or $8.3333 per person of
     * capacity. Founding rent under the old affordability formula is 30% of
     * two $0.800 wages over four heads, which is $0.120 a month. Those two
     * numbers imply 17.3% a year - so that is what this is. The game has been
     * paying its landlords 17.3% since the day it was written; nobody had ever
     * had cause to write it down.
     *
     * Which is the point. THE LEVEL OF RENT DOES NOT CHANGE ON THE DAY THIS
     * SHIPS - opening rent comes out at $0.12014 against $0.12000. What
     * changes is what MOVES it. A rebalance smuggled in under a mechanic is
     * two changes wearing one coat, and only one of them gets tested.
     *
     * It is deliberately generous - 17% gross is a fat yield - because homes in
     * this game carry no upkeep, no vacancy loss and no depreciation. When they
     * get one, this comes down.
     *
     * If anybody re-costs the House, opening rent moves with it. That is
     * correct and is the whole reason the cost is read from the template rather
     * than typed here: a dearer house is dearer to rent.
     */
    public static final double LANDLORD_YIELD = .173;

    /**
     * How hard rent answers a shortage of front doors.
     *
     * At 1.0 the multiple is the ratio: two households per door is twice the
     * rent, half a household per door is half the rent. Proportional, symmetric
     * in the only way that makes sense for a ratio - a doubling up and a
     * doubling down cost and save the same - and unbounded in both directions,
     * which is what Jerus asked for.
     *
     * Housing is famously more inelastic than that in the short run and a case
     * could be made for 1.5 or 2. One is where this starts because it is the
     * reading that needs no defending: one household bidding per door.
     */
    public static final double SCARCITY_ELASTICITY = 1.0;

    /**
     * The lease, in months, which is also the lag.
     *
     * A twelfth of the city's tenancies come up for renewal each month and only
     * those can be repriced, so rent closes a twelfth of the gap to its target
     * each month - a half-life of about eight months. Sticky enough that a bad
     * quarter does not move it and a decade does.
     */
    public static final int LEASE_MONTHS = 12;

    /**
     * Below this many front doors the ratio stops meaning anything.
     *
     * A city with two homes and three hundred households is not a housing
     * market, it is a founding month. Guarding here rather than clamping the
     * multiple keeps the curve itself unbounded, which is the specification.
     */
    private static final int MIN_HOMES_FOR_A_MARKET = 10;

    /** What supplying one person of capacity costs, at today's prices. */
    private double marginalHousingCost;

    /** Set by EconomyManager each month from the cheapest residential template. */
    public void setMarginalHousingCost(double perCapacity) {
        this.marginalHousingCost = Math.max(0, perCapacity);
    }

    public double getMarginalHousingCost() { return marginalHousingCost; }

    /** The rent that just covers building the next home and the landlord's return. */
    public double rentFloor() {
        return marginalHousingCost * LANDLORD_YIELD / 12;
    }

    /**
     * How many households the city has, which is NOT `household`.
     *
     * `household` in this class is household CAPACITY - the people the
     * residential buildings hold - and `occupiedHomes` is doors that are lived
     * in. Neither is the number of families wanting somewhere to live, and the
     * ratio this price is built on needs exactly that. It comes from
     * FamilyModel.totalHouseholds(), carried in the same way rentWeight is,
     * rather than being reconstructed here out of two numbers that nearly mean
     * it. Guessing from a near-synonym is how the last four of these went
     * wrong.
     */
    private double householdCount;

    public void setHouseholdCount(double count) {
        this.householdCount = Math.max(0, count);
    }

    public double getHouseholdCount() { return householdCount; }

    /** Households per front door: above one is a shortage, below one is a glut. */
    public double housingPressure() {
        if (homes < MIN_HOMES_FOR_A_MARKET || householdCount <= 0) return 1;
        return householdCount / homes;
    }

    /**
     * The multiple scarcity justifies over the cost floor. Unbounded both ways.
     */
    public double rentScarcityMultiple() {
        double pressure = housingPressure();
        if (pressure <= 0) return 1;
        return Math.pow(pressure, SCARCITY_ELASTICITY);
    }

    /** Where rent is heading, before the lease slows it down. */
    public double rentTarget() {
        double floor = rentFloor();
        if (floor <= 0) return rentPrice;      // no template, no market
        return floor * rentScarcityMultiple();
    }

    /**
     * Moves rent a lease-length closer to what the market says it should be.
     *
     * Called once a month from updateCommercial(), AFTER the homes and the
     * household count are set, because both are inputs. Deliberately not folded
     * into updateStoreWages(), which is where rent used to be struck: that
     * method runs off the wage array, and the entire point of this change is
     * that rent no longer comes from there.
     */
    public void repriceRent() {
        double target = rentTarget();
        if (target <= 0) return;
        lastRentTarget = target;
        rentPrice += (target - rentPrice) / LEASE_MONTHS;
        if (rentPrice < 0) rentPrice = 0;
    }

    private double lastRentTarget;

    /** What rent is walking towards, for the screen. */
    public double getRentTarget() { return lastRentTarget > 0 ? lastRentTarget : rentPrice; }

    public void setRentPrice(double price) {
        if (price > 0) this.rentPrice = price;
    }

    /**
     * Every price and balance this handler owns, in the new unit.
     *
     * @param scale what to multiply by - a hundredth for a hundred-to-one reform
     */
    public void redenominate(double scale) {
        openingSellPrice *= scale;
        storeSellPrice   *= scale;
        rSellPrice       *= scale;
        rentPrice        *= scale;
        lastRentTarget   *= scale;
        commercialCash   *= scale;
        realEstateCash   *= scale;
        foodPrice        *= scale;
        importPrice      *= scale;
        spendingCapacity *= scale;
        wantedSpend      *= scale;
        marginalHousingCost *= scale;
        pricePerWatt      *= scale;
        pricePerWaterUnit *= scale;
        realEstatePropertyTax   *= scale;
        realEstateInterestExpense *= scale;
        retailPropertyTax       *= scale;
        retailInterestExpense   *= scale;
        for (int i = 0; i < storeWages.length; i++) storeWages[i] *= scale;
        for (int i = 0; i < bankWages.length; i++)  bankWages[i]  *= scale;

        /* -------------------------------------------------------------------
           AND THE MONTH'S REPORT, which is not optional and looked as if it
           were.

           Every rSomething below is last month's income statement, and the
           instinct is to leave it alone because computeMonthlyReport() writes
           it again next month. That is true and it is too late: the figures are
           READ at the top of the next month, before they are rewritten -
           households.update() takes rGrossRevenue and rRentIncome to work out
           what the city earned and paid in rent, and the national accounts take
           them for C. So for one month a reformed city recorded its shopping
           and its rent at the OLD scale against the NEW everything else, and
           the households came out of it richer than they should have been by
           exactly the factor of the reform.

           A statement that is read before it is rewritten is a stock, not a
           flow, for as long as it takes to read it.
           ------------------------------------------------------------------- */
        rBankPayroll *= scale;  rNetIncome *= scale;  rGrossRevenue *= scale;
        rRetailSalesTax *= scale;  rRealEstateSalesTax *= scale;
        rImportTax *= scale;    rPayroll *= scale;    rInventoryCost *= scale;
        rElectricityCost *= scale;  rWaterCost *= scale;
        rRetailInterest *= scale;   rRealEstateInterest *= scale;
        rRetailPropertyTax *= scale;  rRetailOperatingCost *= scale;
        rRetailOperatingIncome *= scale;  rRetailNetIncome *= scale;
        rRentIncome *= scale;   rPropertyMaintenance *= scale;
        rPropertyTaxExpense *= scale;  rRealEstateExpenses *= scale;
        rRealEstateNetIncome *= scale;  rTotalNetIncome *= scale;
        rRetailTax *= scale;    rRealEstateTax *= scale;  rTotalTax *= scale;
        rLocalPurchaseValue *= scale;  rImportPurchaseValue *= scale;
    }

    /**
     * Rent per person of capacity at a given unskilled wage.
     *
     * Static and pure so the starting value above, the monthly re-derivation
     * in updateStoreWages() and HouseholdCheck all compute the one number.
     *
     * Since 2026-09-06 the wage handed in is the LIVE unskilled wage, not the
     * PayTier constant. Labour became a market and the minimum wage its base,
     * which left this the last price in the game still struck off the
     * compile-time table: raising the floor lifted every wage in the city and
     * rent stayed where it was, so the rent burden the household screen
     * reports drifted with a dial that was supposed to move both sides.
     * Neutral at the default minimum wage, by construction.
     */
    public static double rentFor(double unskilledWage) {
        return TARGET_RENT_BURDEN * (REFERENCE_EARNERS * unskilledWage) / REFERENCE_HOME_CAPACITY;
    }

    /** Front doors the city has, and how many of them are lived in. */
    private int homes;
    private double occupiedHomes;

    public void setHomes(int homes)              { this.homes = homes; }
    public void setOccupiedHomes(double occupied){ this.occupiedHomes = occupied; }
    public int getHomes()                        { return homes; }
    public double getOccupiedHomes()             { return occupiedHomes; }

    /** Capacity per front door, averaged over whatever the city has built. */
    public double averageHomeSize() {
        return homes > 0 ? household / (double) homes : 0;
    }

    public CommercialHandler(){

    }

    public void updateCommercialHandler(){
        sellInventory(productsSold);
        storeInventoryCost = buyInventory();


    }
    //updaters
    public void updateJobFillRate(double[]fillRate){

        System.arraycopy(fillRate, 0, this.fillRate, 0, fillRate.length);
    }

    public void updateStoreWages(double[] wages, int[] jobs) {
        updateStoreWages(wages, jobs, null);
    }

    /**
     * @param bankJobs how many of those commercial jobs are the bank's, so its
     *                 payroll can be charged to the bank rather than to the
     *                 shops. Null means no bank, which is most cities for a
     *                 while and every city that predates one.
     */
    public void updateStoreWages(double[] wages, int[] jobs, int[] bankJobs) {

        if (wages == null || jobs == null ) {
            System.out.println("null stores");
            System.out.println(wages + " " + jobs + " " + storeWages);
            return; // nothing to update print error

        }

        int length = Math.min(Math.min(wages.length, jobs.length), storeWages.length);


        for (int i = 0; i < length; i++) {
            storeWages[i] = wages[i] * jobs[i];
            // The bank's slice of the same rate. Struck HERE, off the same wage
            // and in the same loop, so the two can never be computed from
            // different months' prices.
            bankWages[i] = bankJobs != null && i < bankJobs.length
                    ? wages[i] * bankJobs[i] : 0;
        }

        /*
         * RENT NO LONGER COMES FROM HERE. It used to be re-derived off this
         * very wage array every month - `rentPrice = rentFor(unskilled)` - and
         * cutting that line is the substance of making rent a market. It is now
         * a lagged price set in repriceRent() from what housing costs to build
         * and how many households are chasing how many doors, and wages reach
         * it only the long way round, through what materials cost to make.
         *
         * rentFor() survives as the FOUNDING value and as the affordability
         * yardstick HouseholdCheck measures the burden against. It is no longer
         * the price.
         */
        double totalFilled = 0;
        int totalJobsStore = 0;



        for (int i = 0; i < jobs.length; i++) {
            totalFilled += fillRate[i] * jobs[i];// filled positions
            totalJobsStore += jobs[i];
        }

        if (totalJobsStore == 0) {
            averageStoreFill = 1;  // no jobs means fully filled by default
        }

        if(totalJobsStore!=0){
        averageStoreFill = totalFilled / totalJobsStore;

        }

    }

    //getters
    /**
     * What the city collects from the two commercial companies this month.
     *
     * THIS USED TO RE-RUN THE SECTOR (backlog item 7)
     *
     * It called getStoreIncome(), which was not a getter at all: it ASSIGNED
     * productsSold on its way through, with `Math.min(storeCoverage, population)`
     * - demand, with no inventory cap. EconomyManager.getTaxIncome() calls this
     * method, so every read of the city's income rewrote the shops' sales
     * figure, and updateCommercialHandler() then took that quantity off the
     * shelf. Two bugs in one line: the shelf could go negative, and the units
     * the shops were BILLED for stopped matching the units the income statement
     * said they SOLD.
     *
     * It is the third getter-that-was-not-a-getter this project has had, after
     * printCommercialInfo() banking cash and getIndustrialTaxIncome() rewriting
     * industry's report. Same fix as the industrial one: the month's statement
     * is the authority, so the city collects the figure the businesses were
     * charged rather than a second, differently-computed one.
     *
     * Still two separate taxes, not one on the consolidated total - a loss in
     * retail does not shelter real estate's rent, which is what the old code
     * did with its two Math.max calls and what computeMonthlyReport() now does
     * with rRetailTax and rRealEstateTax.
     */
    public double getBusinessTaxIncome(double taxRate){
        pTaxRate = taxRate;
        return rTotalTax;
    }

    /**
     * The month's rent: one cheque per occupied home, sized by the home.
     *
     * An EMPTY home earns nothing, which is why this is occupied homes rather
     * than all of them - a landlord with no tenant has no income, and a city
     * that overbuilds housing should feel it. A city with more households than
     * front doors lets every one of them; the extra households are crowded in
     * with somebody else and do not pay twice.
     *
     * Falls back to the old per-resident figure only when the city has no homes
     * recorded at all, which is a save written before dwellings existed. Better
     * a month of the old number than a month of zero rent.
     */
    public double getRentIncome(){
        /*
         * BILLED OFF THE ACTUAL MATCH since 2026-09-07.
         *
         * This was `let homes x average home size x rentPrice`, which charges
         * every home the city's average whoever is in it - so a studio billed
         * the same as a house and an empty four-bed billed as if a family were
         * in it. FamilyModel now puts households behind doors that fit them and
         * hands back the weight: half the size of the flat and half the size of
         * the household, per Jerus, so an oversized building discounts toward
         * what its tenant will actually pay rather than sitting empty at full
         * price.
         *
         * The two fallbacks below are both real paths, not defensive noise: a
         * save written before dwellings existed has no homes at all, and the
         * load path reaches here before the first housing pass has run.
         */
        if (rentWeight > 0) {
            return rentWeight * rentPrice;
        }
        // (the report snapshots what it used - see rBilledRentWeight)
        if (homes <= 0) {
            return Math.min(household, population) * rentPrice;
        }
        double let = Math.min(occupiedHomes, homes);
        return let * averageHomeSize() * rentPrice;
    }

    //getters
    public int getStoreInventory(){
        return storeInventory;
    }

    /**
     * The units updateCommercialHandler() will take off the shelf this month.
     *
     * Distinct from getReportProductsSold(), which is the figure ON the
     * statement - and the whole of backlog item 7 was that those two were
     * allowed to differ. computeMonthlyReport() sets both, from the same line;
     * this getter exists so ReadPathCheck can watch the live one and notice if
     * anything else ever starts writing to it again.
     */
    public int getProductsSold(){
        return productsSold;
    }
    public double getNetIncome(){
        return rNetIncome;
    }
    public double getGrossRevenue(){
        return rGrossRevenue;
    }

    private double rSellPrice = OPENING_SELL_PRICE;

    /** The shelf price the month's revenue was actually struck at. */
    public double getReportSellPrice() { return rSellPrice; }
    public double getImportTax(){
        return rImportTax;
    }
    public double getCommercialCash(){
        return commercialCash;
    }
    public double getRealEstateCash(){
        return realEstateCash;
    }

    /* -----------------------------------------------------------------------
       REPORT GETTERS - read-only snapshot of the last calculated month.
       Used by the console printer and by the JavaFX sector screen.
       ----------------------------------------------------------------------- */
    public int getPopulation()            { return population; }
    public int getHousehold()             { return household; }
    public int getStoreCoverage()         { return storeCoverage; }
    public int getStoreCapacity()         { return storeCapacity; }
    public double getAverageStoreFill()   { return averageStoreFill; }
    public double getEnergyRatio()        { return energyRatio; }
    public double getWaterRatio()         { return waterRatio; }
    public double getRoadRatio()          { return roadRatio; }
    public double getStoreSellPrice()     { return storeSellPrice; }
    public double getRentPrice()          { return rentPrice; }

    /* ---------------------------------------------------------------------
       WHAT THE CITY CAN AFFORD TO SPEND IN THE SHOPS THIS MONTH.

       Set from HouseholdBalance at the end of the previous month, because that
       is when a household knows what it is holding - and read here at the start
       of this one, which is when it shops. The lag is the point rather than a
       compromise: people budget from the payslip they have already had.
       --------------------------------------------------------------------- */
    private double spendingCapacity;
    private double wantedSpend;

    /** What the households behind the doors add up to. See getRentIncome(). */
    private double rentWeight;

    /**
     * The weight this month's report was actually struck with.
     *
     * NOT the same as rentWeight. The housing match runs in updateWorkforce, at
     * the END of a month, for the next one; the report runs at the START of a
     * month off the match the previous one left. So at the moment a save is
     * written, rentWeight holds a figure the books have not used yet - and
     * carrying that one made the reconstruction bill a month of rent nobody had
     * been charged. Eight cents on $482,860, found by SaveFileCheck.
     */
    private double rBilledRentWeight;

    public void setRentWeight(double weight) { this.rentWeight = weight; }
    public double getRentWeight()            { return rentWeight; }
    public double getBilledRentWeight()      { return rBilledRentWeight; }

    /** Units the city WANTED, before it counted its money. */
    private int rWantedDemand;

    public void setSpendingCapacity(double money) { this.spendingCapacity = money; }
    public void setWantedSpend(double money)      { this.wantedSpend = money; }
    public double getWantedSpend()                { return wantedSpend; }
    public double getSpendingCapacity()           { return spendingCapacity; }

    /** Demand at a headcount, which is what it would have been before budgets. */
    public int getWantedDemand()                  { return rWantedDemand; }

    /** Units the city wanted and could not pay for. */
    public int getUnaffordableDemand()            { return Math.max(0, rWantedDemand - rDemand); }

    /**
     * Share of the units people came in for that the shops actually had, 0-1.
     *
     * IN UNITS, not in money, and that distinction is load-bearing. Gross
     * revenue is scaled by the energy, water, road, health and staffing ratios,
     * so reading "how much did people actually get" off the takings makes an
     * understaffed shop look like a famine - and, since hunger makes people
     * ill and illness lowers the staffing ratio, it closes a feedback loop that
     * has nothing to do with whether there was food on the shelf.
     */
    public double getSupplyRatio() {
        return rDemand > 0 ? Math.min(1, productsSold / (double) rDemand) : 1;
    }
    public double getFoodPrice()          { return foodPrice; }

    public int getReportPopulation()      { return rPopulation; }
    public int getReportHousehold()       { return rHousehold; }
    public int getReportStoreCoverage()   { return rStoreCoverage; }
    public int getReportStoreCapacity()   { return rStoreCapacity; }
    public int getReportStoreInventory()  { return rStoreInventory; }
    public double getReportAverageStoreFill() { return rAverageStoreFill; }
    public double getReportEnergyRatio()  { return rEnergyRatio; }
    /** The sick rate the month was traded at. Not an r-field: see computeMonthlyReport. */
    public double getReportHealthRatio()   { return bHealthRatio; }
    public double getHealthRatio()         { return healthRatio; }
    public double getReportWaterRatio()   { return rWaterRatio; }
    public double getReportRoadRatio()    { return rRoadRatio; }

    public int getReportDemand()          { return rDemand; }
    public int getReportProductsSold()    { return rProductsSold; }
    public double getReportPayroll()      { return rPayroll; }
    public double getReportInventoryCost(){ return rInventoryCost; }
    public double getReportElectricityCost(){ return rElectricityCost; }
    public double getReportWaterCost()    { return rWaterCost; }
    public double getReportRetailInterest()     { return rRetailInterest; }
    public double getReportRealEstateInterest() { return rRealEstateInterest; }

    /**
     * Retail's books. Inventory is valued at the food market price, the same
     * basis industry uses, so a unit is worth the same on both balance sheets
     * and the two are directly comparable.
     */
    public BalanceSheet getRetailBalanceSheet() {
        return new BalanceSheet("Retail")
                .setCash(commercialCash)
                .setInventory(storeInventory, foodPrice)
                .setLand(retailLandValue)
                .setBuildings(retailBuildingsValue)
                .setBondsPayable(retailBondsPayable);
    }

    /** Real estate holds housing, not stock, so there is no inventory line. */
    public BalanceSheet getRealEstateBalanceSheet() {
        return new BalanceSheet("Real Estate")
                .setCash(realEstateCash)
                .setInventory(0, 0)
                .setLand(realEstateLandValue)
                .setBuildings(realEstateBuildingsValue)
                .setBondsPayable(realEstateBondsPayable);
    }
    public double getReportRetailPropertyTax()   { return rRetailPropertyTax; }
    public double getReportRetailOperatingCost() { return rRetailOperatingCost; }
    public double getReportRetailOperatingIncome() { return rRetailOperatingIncome; }
    public double getReportRetailNetIncome()     { return rRetailNetIncome; }
    public int getReportLocalImports()    { return pLocalImport; }
    public int getReportGlobalImports()   { return pGlobalImport; }

    public int getReportOccupiedUnits()   { return rOccupiedUnits; }
    public int getReportVacantUnits()     { return rVacantUnits; }
    public double getReportRentIncome()   { return rRentIncome; }
    public double getReportPropertyMaintenance() { return rPropertyMaintenance; }
    public double getReportPropertyTaxExpense()  { return rPropertyTaxExpense; }
    public double getReportRealEstateExpenses()  { return rRealEstateExpenses; }
    public double getReportRealEstateNetIncome() { return rRealEstateNetIncome; }

    public double getReportTotalNetIncome() { return rTotalNetIncome; }
    public double getReportTotalTax()       { return rTotalTax; }
    public double getReportRetailTax()      { return rRetailTax; }
    public double getReportRealEstateTax()  { return rRealEstateTax; }
    public double getReportTaxRate()        { return pTaxRate; }

    //setters
    public void setStoreCoverage(int cap){
        storeCoverage = cap;
    }
    public void setStoreCapacity(int cap){
        storeCapacity = cap;
    }
    public void setPopulation(int pop){
        population = pop;
    }
    public void setHousehold(int household){
        this.household = household;
    }
    public void setFoodAvailableForSale(int foodInventory){
        foodAvailableForSale = foodInventory;
    }
    public void setFoodPrice(double foodPrice){
        this.foodPrice = foodPrice;
    }
    public void setImportPrice(double importPrice){
        this.importPrice = importPrice;
    }
    /** Units sold last month - the demand signal the restock target is built on. */
    public int getLastMonthSales(){
        return lastMonthSales;
    }
    public double getImportPrice(){
        return importPrice;
    }
    public void setStoreInventory(int storeInventory){
        this.storeInventory = storeInventory;
    }
    public void setWaterRatio(double ratio){
        this.waterRatio = ratio;
    }
    public void setPricePerWaterUnit(double price){
        this.pricePerWaterUnit = price;
    }
    public void setWaterConsumption(double consumption){
        this.water = consumption;
    }
    public void setEnergyRatio(double ratio){
        this.energyRatio = ratio;
    }
    public void setRoadRatio(double ratio){
        this.roadRatio = ratio;
    }
    public void setHealthRatio(double ratio){
        this.healthRatio = ratio;
    }
    public void setPricePerWatt(double price){
        this.pricePerWatt = price;
    }
    public void setElectricityConsumption(int consumption){
        this.electricity = consumption;
    }
    public void setRetailPropertyTax(double value){
        this.retailPropertyTax = value;
    }

    public void setRealEstatePropertyTax(double value){
        this.realEstatePropertyTax = value;
    }

    public double getStoreInventoryCost()    { return storeInventoryCost; }
    public double getRetailInterestExpense()     { return retailInterestExpense; }
    public double getRealEstateInterestExpense() { return realEstateInterestExpense; }

    /**
     * The month's cost of goods, put back on load.
     *
     * buyInventory() sets this during the month and nothing recomputes it
     * afterwards, so a loaded city priced its retail income statement with no
     * cost of goods at all - the shops looked more profitable than they were,
     * and the city's business tax went up with them.
     */
    public void setStoreInventoryCost(double cost) { this.storeInventoryCost = cost; }

    /**
     * What the shops actually bought last month, local and global.
     *
     * The local figure is the one that matters beyond the report:
     * EconomyManager.startOfMontEconUpdate() feeds it to the mills as NEXT
     * month's demand, so the two sides trade with each other rather than each
     * guessing. A loaded city had it at zero, which told industry nobody wanted
     * anything - the mills stopped selling, their stock built up, the food
     * market repriced, and the shops started importing globally at import
     * prices. One unrestored number, and the city's whole food economy changed
     * shape two months later.
     */
    /**
     * Rebuilds the month's statement from the basis it was written against, and
     * puts back the import tax, which only buyInventory() ever sets.
     */
    public void restoreMonthReport(double storeFillBasis, double importTax,
                                   double energyBasis, double waterBasis, double roadBasis,
                                   double healthBasis) {
        computeMonthlyReport(storeFillBasis, energyBasis, waterBasis, roadBasis, healthBasis);
        rImportTax = importTax;
    }

    public void setReportImports(int local, int global) {
        this.pLocalImport = local;
        this.pGlobalImport = global;
    }

    /**
     * The rate the month is taxed at, set before the statement runs.
     *
     * The rate used to arrive as a side effect of getBusinessTaxIncome(), which
     * runs LATE in the month - so computeMonthlyReport(), which runs at the top
     * of it, always taxed the month at the rate in force during the previous
     * one. Harmless while the tax was recomputed later anyway; not harmless now
     * that the statement's figure is the one the city collects, because a rate
     * change would have taken a month to reach the treasury.
     *
     * Mirrors IndustrialHandler.setTaxRate(), and is called from the same place
     * in the month.
     */
    public void setTaxRate(double taxRate) { this.pTaxRate = taxRate; }

    public double getRetailPropertyTax()     { return retailPropertyTax; }
    public double getRealEstatePropertyTax() { return realEstatePropertyTax; }

    public void setRetailInterestExpense(double value){
        this.retailInterestExpense = value;
    }
    public void setRealEstateInterestExpense(double value){
        this.realEstateInterestExpense = value;
    }
    public void setRetailBalanceSheetInputs(double land, double buildings, double bonds){
        this.retailLandValue = land;
        this.retailBuildingsValue = buildings;
        this.retailBondsPayable = bonds;
    }
    public void setRealEstateBalanceSheetInputs(double land, double buildings, double bonds){
        this.realEstateLandValue = land;
        this.realEstateBuildingsValue = buildings;
        this.realEstateBondsPayable = bonds;
    }
    public void setCommercialCash(double cash){
        this.commercialCash = cash;
    }

    public void setRealEstateCash(double realEstateCash) {
        this.realEstateCash = realEstateCash;
    }



    //store methods
    /**
     * Units the stores want to buy this month.
     *
     * NOTE: this used to be `storeCapacity - storeInventory` - stores restocked to
     * full capacity every month regardless of how many customers they had. Ten
     * grocery stores serving 900 people held 35,000 units and paid to procure all
     * of it, which is most of why retail could never turn a profit. They now aim
     * for a few months of cover based on actual demand, so capacity is a ceiling
     * rather than a target, and a well-stocked store simply buys nothing.
     */
    public int neededInventory(){
        return Math.max(restockTarget() - storeInventory, 0);
    }

    /** Shelf stock the stores are aiming for: a few months of recent sales. */
    private int restockTarget() {

        /*
         * WHAT THEY SELL, NOT WHO WALKS PAST.
         *
         * This was max(lastMonthSales, min(coverage, population)) - a month-one
         * fallback that never stopped being applied. So the shops stocked for
         * every customer they could theoretically serve, every month, however
         * few of them could actually afford anything: measured in a city of
         * 231,000, they imported 212,000 units a month and sold 77,700.
         *
         * Harmless-looking while the import price was a small constant. Once the
         * exchange rate started moving it became the largest item in the city's
         * balance of payments by a factor of four - $55,059 a month of imported
         * food against $12,659 of everything the city sold abroad - and drove
         * GDP negative, because the units were bought in foreign currency and
         * then sat on a shelf nobody could buy them from.
         *
         * The fallback is still here. It just knows when it is month one.
         */
        int recentDemand = getLastMonthSales() > 0
                ? getLastMonthSales()
                : Math.min(storeCoverage, population);

        int target = (int) Math.ceil(recentDemand * STORE_COVER_MONTHS);
        return Math.min(target, storeCapacity);
    }

    /**
     * The demand signal handed to FoodMarket.
     *
     * NOTE: must anticipate this month's sales. The market is priced in
     * procedureUpdate(), which runs BEFORE updateCommercialHandler() takes the
     * month's sales off the shelf - so neededInventory() still sees full shelves
     * and reports roughly zero demand. Pricing off that made every scenario look
     * like a glut, including one mill supplying ten stores.
     */
    public int getExpectedPurchase() {
        int afterSales = Math.max(storeInventory - productsSold, 0);
        return Math.max(restockTarget() - afterSales, 0);
    }

    /**
     * Buys the month's restock.
     *
     * FoodMarket caps the local price at the import price, so local is never the
     * more expensive option - the store takes whatever industry has released and
     * imports only the shortfall. If it already holds enough cover it buys nothing
     * and runs the shelves down.
     *
     * NOTE: imports used to be priced at `foodPrice * 1.3` - a fixed markup on the
     * local price rather than an independent world price - so there was never an
     * actual choice between the two.
     */
    public double buyInventory(){

        int needed = neededInventory();

        int localImport = Math.min(foodAvailableForSale, needed);
        int globalImport = needed - localImport;

        storeInventory += localImport + globalImport;

        /*
         * WHAT THE SHOP ACTUALLY PAID, and at whose rate.
         *
         * Both halves used to be marked up by `pTaxRate`, which is the retail
         * PROFIT rate - a profit tax used as a purchase markup. Under the
         * producer-rate design the supplier charges its OWN sales rate, so local
         * food carries INDUSTRY's and an import carries the buyer's own, exactly
         * as SalesTaxLedger.chargeImport() describes.
         *
         * The two net values are kept as well as the gross. The gross is what
         * the shop pays out and belongs on its income statement; the NET is what
         * the input credit has to be struck against, and blending the two was
         * the whole of the bug - the credit was taken on a tax-inclusive figure
         * and over-claimed by exactly the rate. Measured at 15%, which was
         * enough to make the city's whole sales tax negative.
         */
        localPurchaseValue = localImport * foodPrice;
        importPurchaseValue = globalImport * importPrice;

        /*
         * ...and the shelf follows the invoice, slowly. Struck here, off the
         * very units and prices the shops just paid, so what they charge and
         * what they were charged cannot be computed from different months.
         */
        /*
         * Priced on the month that just traded, using the ratio this class
         * already computes for exactly this question. getSupplyRatio() is
         * productsSold over rDemand - what left the shelf against what people
         * came for AND could afford - and its own comment explains why it is
         * measured in units rather than off the takings: revenue is scaled by
         * five ratios, so reading it off the money makes an understaffed shop
         * look like a famine.
         *
         * Note what rDemand already is: demand people can PAY for. So a price
         * rise shrinks the denominator next month, which is precisely how a
         * price clears a market rather than chasing one.
         */
        repriceShelf(localImport, foodPrice, globalImport, importPrice,
                rDemand, productsSold);

        /*
         * THE PRICE, AND ONLY THE PRICE, since 2026-09-06.
         *
         * Both halves used to be marked up by a sales rate - the supplier's on
         * local food, the shop's own on imports - and the markup went to
         * nobody: the mills booked the bare price, the world took the bare
         * price, and the treasury took its VAT from the ledger without
         * debiting anyone. The rate follows the producer, so the producer
         * remits (EconomyManager.settleSalesTax) and the buyer pays what the
         * goods cost. The two rates are still held here for the report's
         * input-credit line, which is what the ledger credits the shop.
         */
        double cost = localPurchaseValue + importPurchaseValue;

        if (globalImport != 0) {
            System.out.println("Stores imported: " + formatter.format(globalImport)
                    + " food at $" + formatter.format(importPrice) + "/unit.");
        }

        pLocalImport = localImport;
        pGlobalImport = globalImport;

        return cost;
    }

    public void sellInventory(int quantity){
        storeInventory -= quantity;
        lastMonthSales = quantity;
    }

    public double getElectricityCost(){
        double cost = 0;
        // Charged for what was DELIVERED, not what was asked for - the utility
        // books the same slice. See UtilitiesHandler.getElectricityRevenue().
        cost = electricity * energyRatio * pricePerWatt;
        return cost;

    }

    /**
     * Scaled by waterRatio: during rationing you receive less water and are
     * charged for less. This is what makes the utility's water revenue and the
     * sectors' water expense the same number instead of two figures that drift
     * apart whenever supply is short.
     */
    public double getWaterCost(){
        return water * waterRatio * pricePerWaterUnit;
    }

    /**
     * Runs the commercial sector's monthly income statement and stores the
     * result. This is the ONLY place that mutates commercialCash /
     * realEstateCash / rNetIncome / rGrossRevenue, and it must be called
     * exactly once per month, before anything reads those values
     * (calculateSalesTax() and getMonthGdp() both do).
     *
     * The math here is unchanged from the old printCommercialInfo() body - only
     * its location moved, so a reports-ON game simulates identically. A
     * reports-OFF game now gets correct sales tax and GDP instead of zeroes.
     */
    public void calculateCommercialResults() {
        computeMonthlyReport();
    }

    /* =====================================================================
       THE MONTH'S SALES TAX, ON THE STATEMENT

       Two companies share this handler and only one of them supplies anything
       taxable: residential rent is exempt, so real estate's line is always
       zero. Retail's is not - on Jerus's slot 7 it was $4.79M against a
       reported profit of $16.0M, none of which appeared on the statement.

       Handed in by EconomyManager.settleSalesTax() once the ledger has settled,
       because the ledger is struck FROM the revenue this statement reports and
       cannot be known while it is being written. See
       HeavyIndustryHandler.getReportSalesTax() for the whole reasoning.

       Not in the save array - put back on load from the restored ledger, so
       there is one record of the number rather than two that can disagree.
       ===================================================================== */
    private double rRetailSalesTax;
    private double rRealEstateSalesTax;

    public double getReportRetailSalesTax()     { return rRetailSalesTax; }
    public double getReportRealEstateSalesTax() { return rRealEstateSalesTax; }

    void setSalesTaxRemitted(double retail, double realEstate) {
        this.rRetailSalesTax = retail;
        this.rRealEstateSalesTax = realEstate;
    }

    /**
     * Banks the month, net of both taxes, once the sales tax is known.
     *
     * The only accumulating state in the sector. Kept out of
     * computeMonthlyReport() so the report can be recalculated for display
     * (e.g. after a load) without banking a phantom month of income.
     *
     * NET OF THE PROFIT TAX, since 2026-09-06. Both companies used to bank
     * the pre-tax figure while the city collected the tax on it - the same
     * dollars counted twice, and the largest single leak MoneyAudit found
     * (backlog item 8, decided by Jerus: deduct it). rRetailTax and
     * rRealEstateTax are exactly what getBusinessTaxIncome() hands the
     * treasury, so the payer and the payee now agree to the cent.
     *
     * ...AND NET OF THE SALES TAX, since 2026-09-09, by the same argument one
     * layer out. The VAT used to be taken off this cash by a separate loop in
     * EconomyManager.calculateSalesTax() and to appear on no statement, so the
     * cash and the income statement described two different months. One
     * movement now, in one place, made of the figures the statement shows.
     */
    void bankMonth(double retailSalesTax, double realEstateSalesTax) {
        setSalesTaxRemitted(retailSalesTax, realEstateSalesTax);
        computeMonthlyReport();
        commercialCash += rRetailNetIncome - rRetailTax;
        realEstateCash += rRealEstateNetIncome - rRealEstateTax;
    }

    /**
     * Recomputes every report figure from the current inputs. Pure with respect
     * to the cash reserves - safe to call whenever the derived state needs
     * rebuilding, such as after loading a save.
     */
    public void computeMonthlyReport() {
        computeMonthlyReport(averageStoreFill);
    }

    /**
     * The statement-time call: the month is running now, so what the city is
     * living through IS the basis. Only the load path passes anything else.
     */
    public void computeMonthlyReport(double storeFillBasis) {
        computeMonthlyReport(storeFillBasis, energyRatio, waterRatio, roadRatio, healthRatio);
    }

    /**
     * @param storeFillBasis the staffing level the month was actually TRADED at.
     *
     * Not always the current one. A month's report is written before the job
     * fill rate is updated, so when new shops finish and dilute the labour pool
     * the report describes a month staffed at the old rate - and rebuilding it
     * later from the new rate reports revenue the shops never earned. On a city
     * that had just opened four stores that was 1.0 against 0.9508, and $3.16 of
     * revenue that appeared out of a reload.
     */
    public void computeMonthlyReport(double storeFillBasis,
                                     double energyBasis, double waterBasis, double roadBasis,
                                     double healthBasis) {

        bEnergyRatio = energyBasis;
        bWaterRatio = waterBasis;
        bRoadRatio = roadBasis;

        /*
         * DELIBERATELY NOT AN r-FIELD, unlike the other three.
         *
         * rEnergyRatio and its siblings are in the report state array that the
         * save carries, and every one of those arrays is refused whole on a
         * length mismatch. Appending a fifth ratio to four of them would make
         * every existing save recompute its statements instead of restoring
         * them - a real regression, to display a number that is the same in all
         * four sectors and already on the People screen. The basis is still
         * carried, through DataSave's ratio basis, which is what keeps the one
         * remaining recompute path honest.
         */
        bHealthRatio = healthBasis;

        // snapshot the inputs first, so every figure on the report - inputs and
        // results alike - describes the same moment
        rPopulation = population;
        rHousehold = household;
        rBilledRentWeight = rentWeight;
        rStoreCoverage = storeCoverage;
        rStoreCapacity = storeCapacity;
        rStoreInventory = storeInventory;
        rAverageStoreFill = storeFillBasis;
        rEnergyRatio = bEnergyRatio;
        rWaterRatio = bWaterRatio;
        rRoadRatio = bRoadRatio;

        /* -------------------- RETAIL / COMMERCIAL COMPANY -------------------- */
        /*
         * THE BUDGET CONSTRAINT.
         *
         * Demand was min(storeCoverage, population) - a headcount, with no
         * reference to what anybody earned, so the shops sold the same basket to
         * a household on $552 and one on $18,673 and the difference came out as
         * a deficit nothing funded. The third term is what the households can
         * actually pay for, priced at the shelf price they will pay it at, and
         * it comes from HouseholdBalance after savings and credit have been
         * drawn on. See HouseholdBalance for the waterfall.
         *
         * Zero capacity means "nobody has told us yet", not "nobody can afford
         * anything" - a fresh game and the load path both reach here before the
         * first household statement exists, and a city whose shops sold nothing
         * in month one would never start.
         */
        /*
         * The headcount is no longer the ceiling either. It was one basket a
         * person however rich they were, so a city could not spend its way to a
         * bigger retail sector - the whole top of the income distribution was
         * capped at the same basket as the bottom. The want comes from
         * HouseholdBalance now: subsistence for everybody, plus most of
         * whatever is left over. What survives of the old rule is
         * storeCoverage, which is the shops' own capacity to serve people, and
         * a city whose households shop three times as hard needs three times
         * the shops.
         */
        int wanted = Math.min(storeCoverage, population);
        int affordable = wanted;
        if (spendingCapacity > 0 && storeSellPrice > 0) {
            wanted = wantedSpend > 0
                    ? (int) Math.floor(wantedSpend / storeSellPrice)
                    : Math.min(storeCoverage, population);
            affordable = (int) Math.floor(spendingCapacity / storeSellPrice);
        }
        rWantedDemand = Math.min(storeCoverage, wanted);
        rDemand = Math.min(rWantedDemand, affordable);

        /*
         * ================= THE SHOPS SELL WHAT THEY CAN SERVE =================
         *
         * The utilisation ratios used to be applied to the REVENUE and not to
         * the UNITS:
         *
         *     productsSold = min(rDemand, storeInventory);
         *     rGrossRevenue = productsSold * price * energy * water * road
         *                     * health * fill;
         *
         * which handed over every unit and collected the money for a fraction
         * of them. Measured over 140 months in a city at 35% road throughput:
         * 419,779 units left the shelf and 117,706 were paid for. SEVENTY-TWO
         * PERCENT of everything the shops sold was given away.
         *
         * The units were not lost to a leak - they were bought, they left
         * inventory, and the shops restocked to replace them. So the city
         * imported food, gave most of it away, and imported more. Invisible
         * while imports were cheap and constant; the largest item in the balance
         * of payments once the exchange rate started moving, and the reason a
         * city of a quarter of a million could post a NEGATIVE GDP.
         *
         * A ratio of .35 means the shop can serve about a third of the people
         * who want to buy - the lorries cannot get through, or the lights are
         * off, or there is nobody on the till. It does not mean the shop serves
         * everybody and charges a third. So it throttles the QUANTITY, the
         * revenue is quantity times price with nothing else in it, and the units
         * that were not sold are still on the shelf next month, which is where
         * they were all along.
         */
        double serviceable = rDemand * bEnergyRatio * bWaterRatio * bRoadRatio
                * bHealthRatio * storeFillBasis;
        productsSold = (int) Math.floor(Math.min(serviceable, storeInventory));
        rProductsSold = productsSold;

        rGrossRevenue = productsSold * storeSellPrice;
        /*
         * THE PRICE THE MONTH ACTUALLY CHARGED, kept beside the revenue it
         * produced. The shelf price moves every month now that shortage lifts
         * it, so anybody recovering units from money by dividing by
         * getStoreSellPrice() gets the answer for a price that was struck
         * afterwards - which is exactly how ForeignCheck's "every unit that
         * leaves the shelf is paid for" started reporting a 93-unit hole in an
         * identity that had not changed.
         *
         * A flow cannot be reconstructed from the state a month ended in. This
         * is that rule applied to a price.
         */
        rSellPrice = storeSellPrice;

        double payroll = 0;
        if (storeWages != null) {
            for (double wage : storeWages) {
                payroll += wage;
            }
        }
        payroll *= storeFillBasis;

        /*
         * The bank's tellers, out of the shops' wage bill.
         *
         * Apportioned by headcount within each job type, which is exact rather
         * than approximate: every commercial building in the city is staffed at
         * the same fill basis, so a fifth of the sector's cashiers costs a fifth
         * of what the sector pays cashiers.
         */
        double bankSlice = 0;
        for (double w : bankWages) bankSlice += w;
        rBankPayroll = Math.min(payroll, bankSlice * storeFillBasis);
        rPayroll = payroll - rBankPayroll;

        /*
         * The money that actually left the account, read rather than re-derived.
         *
         * This line used to recompute the cost from the import quantities and
         * the prevailing prices - which is how it came to price globals at 1.5x
         * the local price while buyInventory() charged 1.3x, so the income
         * statement never agreed with the bank (backlog item 6). Matching the
         * two formulas fixed the symptom; reading the charge fixes the shape.
         *
         * It also unhooks the statement from pTaxRate, which matters now that
         * the rate is set at the top of the month: the imports on this line were
         * bought last month and charged sales tax at last month's rate, and
         * re-deriving them at the new one would invent a cost nobody paid.
         * storeInventoryCost is carried in the save for the same reason.
         */
        rInventoryCost = storeInventoryCost;

        // The same month's purchase, split into its two halves, so the tax
        // ledger credits the input tax on the stock this statement was written
        // against rather than on stock bought since.
        rLocalPurchaseValue = localPurchaseValue;
        rImportPurchaseValue = importPurchaseValue;
        rImportTax = importPurchaseValue * ownSalesRate;
        rElectricityCost = electricity * bEnergyRatio * pricePerWatt;
        rWaterCost = water * bWaterRatio * pricePerWaterUnit;

        rRetailPropertyTax = retailPropertyTax;
        rRetailOperatingCost = rPayroll + rInventoryCost + rElectricityCost + rWaterCost
                + rRetailPropertyTax;
        rRetailInterest = retailInterestExpense;

        // rRetailNetIncome is what gets banked to commercialCash, so interest has
        // to come out here for the sector to actually bear it.
        rRetailOperatingIncome = rGrossRevenue - rRetailOperatingCost;
        rRetailNetIncome = rRetailOperatingIncome - rRetailInterest - rRetailSalesTax;

        /* -------------------- REAL ESTATE COMPANY -------------------- */
        rOccupiedUnits = Math.min(household, population);
        rVacantUnits = Math.max(household - population, 0);

        rRentIncome = getRentIncome();

        rPropertyMaintenance = 0;
        rPropertyTaxExpense = realEstatePropertyTax;
        rRealEstateInterest = realEstateInterestExpense;

        // Maintenance is still hardcoded to zero; property tax no longer is.
        rRealEstateExpenses = rPropertyMaintenance + rPropertyTaxExpense + rRealEstateInterest;
        /*
         * Residential rent is an EXEMPT supply, so rRealEstateSalesTax is
         * always zero and this line is here for symmetry rather than for
         * arithmetic - and to be the place a future taxable supply (a car park,
         * a commercial let) would land without anybody having to notice that
         * this company had no tax line at all. See SalesTaxLedger.
         */
        rRealEstateNetIncome = rRentIncome - rRealEstateExpenses - rRealEstateSalesTax;

        /* -------------------- CONSOLIDATED -------------------- */
        rTotalNetIncome = rRetailNetIncome + rRealEstateNetIncome;

        // Per company, floored at zero, because that is what the city collects -
        // see getBusinessTaxIncome(), which now returns this figure instead of
        // computing a second one of its own.
        rRetailTax = Math.max(rRetailNetIncome * pTaxRate, 0);
        rRealEstateTax = Math.max(rRealEstateNetIncome * pTaxRate, 0);
        rTotalTax = rRetailTax + rRealEstateTax;

        rNetIncome = rTotalNetIncome;
    }

    /**
     * Pure display. Reads the values calculated by calculateCommercialResults()
     * and mutates nothing, so it is safe to call zero, one, or many times per
     * month.
     */
    public void printCommercialInfo() {

        System.out.println("\n====================== COMMERCIAL SECTOR REPORT ======================");

        /* -------------------------------------------------------------------
       COMPANY A: RETAIL / COMMERCIAL OPERATIONS
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ RETAIL OPERATIONS (COMMERCIAL COMPANY) ------------------");

        /* 1. Capacity & Market Data */
        System.out.println("\nMARKET OVERVIEW");
        System.out.printf("City Population:        %,d people%n", rPopulation);
        System.out.printf("Store Market Coverage:  %,d customers%n", rStoreCoverage);
        System.out.printf("Store Capacity:         %,d units%n", rStoreCapacity);
        System.out.printf("Current Inventory:      %,d units%n", rStoreInventory);

        /* 2. Resource Efficiency */
        System.out.println("\nRESOURCE UTILIZATION");
        System.out.printf("Labor Fill Rate:        %.1f%%%n", rAverageStoreFill * 100);
        System.out.printf("Energy Efficiency:      %.1f%%%n", rEnergyRatio * 100);
        System.out.printf("Water Efficiency:       %.1f%%%n", rWaterRatio * 100);
        System.out.printf("Road Throughput:        %.1f%%%n", rRoadRatio * 100);

        /* 3. Retail Sales Performance */
        System.out.println("\nSALES PERFORMANCE");
        System.out.printf("Market Demand:          %,d units%n", rDemand);
        System.out.printf("Units Sold:             %,d units%n", rProductsSold);
        System.out.printf("Average Sell Price:     $%s per unit%n", formatter.format(storeSellPrice));
        System.out.printf("Gross Revenue:          $%s%n", formatter.format(rGrossRevenue));

        /* 4. Income Statement (Retail Company) */
        System.out.println("\nINCOME STATEMENT (RETAIL COMPANY)");

        System.out.printf("Revenue:%n");
        System.out.printf("  Retail Sales Revenue:                $%s%n", formatter.format(rGrossRevenue));

        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Payroll Expense:                     -$%s%n", formatter.format(rPayroll));
        System.out.printf("  Inventory Procurement:               -$%s%n", formatter.format(rInventoryCost));
        System.out.printf("      Local Imports:                   %,d units%n", pLocalImport);
        System.out.printf("      Global Imports:                  %,d units%n", pGlobalImport);
        System.out.printf("  Electricity Expense:                 -$%s%n", formatter.format(rElectricityCost));
        System.out.printf("  Water Expense:                       -$%s%n", formatter.format(rWaterCost));
        System.out.printf("  Interest Expense:                    -$%s%n", formatter.format(rRetailInterest));

        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Total Operating Expenses:              -$%s%n", formatter.format(rRetailOperatingCost));
        System.out.printf("NET INCOME (RETAIL COMPANY):           $%s%n", formatter.format(rRetailNetIncome));

        /* -------------------------------------------------------------------
       COMPANY B: REAL ESTATE OPERATIONS
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ REAL ESTATE OPERATIONS COMPANY ------------------");

        System.out.println("\nPROPERTY OVERVIEW");
        System.out.printf("Total Housing Units:       %,d units%n", rHousehold);
        System.out.printf("Occupied Units:            %,d units%n", rOccupiedUnits);
        System.out.printf("Vacant Units:              %,d units%n", rVacantUnits);

        System.out.println("\nRENTAL PERFORMANCE");
        System.out.printf("Monthly Rent Revenue:      $%s%n", formatter.format(rRentIncome));

        System.out.println("\nINCOME STATEMENT (REAL ESTATE COMPANY)");

        System.out.printf("Revenue:%n");
        System.out.printf("  Rental Income:                        $%s%n", formatter.format(rRentIncome));

        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Property Maintenance:                 -$%s%n", formatter.format(rPropertyMaintenance));
        System.out.printf("  Property Tax Expense:                 -$%s%n", formatter.format(rPropertyTaxExpense));
        System.out.printf("  Interest Expense:                     -$%s%n", formatter.format(rRealEstateInterest));

        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Total Operating Expenses:               -$%s%n", formatter.format(rRealEstateExpenses));
        System.out.printf("NET INCOME (REAL ESTATE COMPANY):       $%s%n", formatter.format(rRealEstateNetIncome));


        /* -------------------------------------------------------------------
       CONSOLIDATED SUMMARY
       ------------------------------------------------------------------- */
        System.out.println("\n====================== CONSOLIDATED SUMMARY ======================");
        System.out.printf("Retail Net Income:           $%s%n", formatter.format(rRetailNetIncome));
        System.out.printf("Real Estate Net Income:      $%s%n", formatter.format(rRealEstateNetIncome));
        System.out.println("-------------------------------------------------------------------");
        System.out.printf("TOTAL NET INCOME:            $%s%n", formatter.format(rTotalNetIncome));
        System.out.printf("  Tax on Retail:             $%s%n", formatter.format(rRetailTax));
        System.out.printf("  Tax on Real Estate:        $%s%n", formatter.format(rRealEstateTax));
        System.out.printf("TOTAL TAX REVENUE:           $%s%n", formatter.format(rTotalTax));
        System.out.printf("%nRetail Cash:                 $%s%n", formatter.format(commercialCash));
        System.out.printf("Real Estate Cash:            $%s%n", formatter.format(realEstateCash));
        System.out.println("===================================================================\n");
    }
    //random

    public void resetCommercialHandler(){
        averageStoreFill = 0;
        population = 0;
        storeCoverage = 0;
        household = 0;

        // report snapshot
        rPopulation = 0;
        rHousehold = 0;
        rStoreCoverage = 0;
        rStoreCapacity = 0;
        rStoreInventory = 0;
        rAverageStoreFill = 0;
        rEnergyRatio = 0;
        rWaterRatio = 0;
        rRoadRatio = 0;
        rDemand = 0;
        rProductsSold = 0;
        rGrossRevenue = 0;
        rSellPrice = storeSellPrice;
        rPayroll = 0;
        rInventoryCost = 0;
        rElectricityCost = 0;
        rWaterCost = 0;
        rRetailInterest = 0;
        rRealEstateInterest = 0;
        rRetailOperatingIncome = 0;
        rRetailOperatingCost = 0;
        rRetailPropertyTax = 0;
        rRetailNetIncome = 0;
        rOccupiedUnits = 0;
        rVacantUnits = 0;
        rRentIncome = 0;
        rPropertyMaintenance = 0;
        rPropertyTaxExpense = 0;
        rRealEstateExpenses = 0;
        rRealEstateNetIncome = 0;
        rTotalNetIncome = 0;
        rRetailTax = 0;
        rRealEstateTax = 0;
        rTotalTax = 0;
        rNetIncome = 0;
    }
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }



    /* ======================= THE MONTH'S REPORT ========================

       Every r-field, as one array, so a save can carry the statement itself
       rather than the ingredients for rebuilding it.

       WHY THIS EXISTS

       A month's income statement runs at the START of the month, against what
       the previous month left behind. The month then moves those inputs, and a
       reloaded save used to rebuild the statement from the state the save was
       taken IN - a different city from the one the statement described.

       The project closed that one input at a time: the store fill basis, then
       the imports, then the property-tax and interest charges, then the three
       utilisation ratios. Each fix uncovered the next term underneath - the
       food price was next, with payroll and the electricity draw behind it.
       Enumerating inputs is a losing game when the report has thirty of them.

       So the report is carried instead. A reloaded city now agrees with the one
       it came from by construction rather than by list-making.

       ORDER IS THE FORMAT. These two methods must stay mirror images, and new
       fields go on the END - the array is positional, and inserting one in the
       middle would silently shift every figure after it into the wrong line of
       a player's income statement. A saved array of a different length is
       refused whole rather than padded, because a partially restored statement
       is worse than an honestly recomputed one; the caller falls back to
       recomputing, which is what saves written before this did anyway.
       ================================================================== */

    public double[] getReportState() {
        return new double[] {
            rNetIncome,
            rGrossRevenue,
            rImportTax,
            rPopulation,
            rHousehold,
            rStoreCoverage,
            rStoreCapacity,
            rStoreInventory,
            rAverageStoreFill,
            rEnergyRatio,
            rDemand,
            rProductsSold,
            rPayroll,
            rInventoryCost,
            rElectricityCost,
            rWaterCost,
            rWaterRatio,
            rRoadRatio,
            rRetailInterest,
            rRealEstateInterest,
            rRetailPropertyTax,
            rRetailOperatingCost,
            rRetailOperatingIncome,
            rRetailNetIncome,
            rOccupiedUnits,
            rVacantUnits,
            rRentIncome,
            rPropertyMaintenance,
            rPropertyTaxExpense,
            rRealEstateExpenses,
            rRealEstateNetIncome,
            rTotalNetIncome,
            rTotalTax,
            rRetailTax,
            rRealEstateTax,

            /*
             * Appended, per the standing rule that order is the format. The two
             * NET purchase values, which the sales-tax ledger needs and could
             * not previously get - it was handed the tax-inclusive cost and
             * over-claimed the input credit by exactly the rate.
             */
            rLocalPurchaseValue,
            rImportPurchaseValue
        };
    }

    /**
     * Puts a saved statement back, exactly as it was written.
     *
     * @return false if the array is not this build's shape, in which case
     *         nothing was changed and the caller should recompute instead
     */
    public boolean restoreReportState(double[] r) {

        /*
         * 37 now, 35 before the two net purchase values were appended. Both are
         * accepted: a shorter array is a save from the older build, and the
         * statement it describes is complete without them - they are inputs to
         * the tax ledger, not lines on the income statement.
         */
        if (r == null || (r.length != 37 && r.length != 35)) return false;

        int i = 0;
        rNetIncome = r[i++];
        rGrossRevenue = r[i++];
        rImportTax = r[i++];
        rPopulation = (int) r[i++];
        rHousehold = (int) r[i++];
        rStoreCoverage = (int) r[i++];
        rStoreCapacity = (int) r[i++];
        rStoreInventory = (int) r[i++];
        rAverageStoreFill = r[i++];
        rEnergyRatio = r[i++];
        rDemand = (int) r[i++];
        rProductsSold = (int) r[i++];
        rPayroll = r[i++];
        rInventoryCost = r[i++];
        rElectricityCost = r[i++];
        rWaterCost = r[i++];
        rWaterRatio = r[i++];
        rRoadRatio = r[i++];
        rRetailInterest = r[i++];
        rRealEstateInterest = r[i++];
        rRetailPropertyTax = r[i++];
        rRetailOperatingCost = r[i++];
        rRetailOperatingIncome = r[i++];
        rRetailNetIncome = r[i++];
        rOccupiedUnits = (int) r[i++];
        rVacantUnits = (int) r[i++];
        rRentIncome = r[i++];
        rPropertyMaintenance = r[i++];
        rPropertyTaxExpense = r[i++];
        rRealEstateExpenses = r[i++];
        rRealEstateNetIncome = r[i++];
        rTotalNetIncome = r[i++];
        rTotalTax = r[i++];
        rRetailTax = r[i++];
        rRealEstateTax = r[i++];

        if (r.length >= 37) {
            rLocalPurchaseValue = r[i++];
            rImportPurchaseValue = r[i++];
        } else {
            // A pre-37 save. Reconstructed from the gross cost the older build
            // did carry, which is exact whenever the two purchase rates agree
            // and close enough otherwise - and better than a zero, which would
            // hand the shop no input credit for a month.
            double rate = 1 + supplierSalesRate;
            rLocalPurchaseValue = rate > 0 ? rInventoryCost / rate : rInventoryCost;
            rImportPurchaseValue = 0;
        }

        return true;
    }

    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        openingSellPrice = OPENING_SELL_PRICE / unit;
    }

}
