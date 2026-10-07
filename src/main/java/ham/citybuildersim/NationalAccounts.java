package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The city's GDP, measured properly, plus the government's own books.
 *
 * WHY THE OLD FIGURE WENT NEGATIVE
 *
 * getMonthGdp() did two wrong things at once:
 *
 *     GDP = totalWage + industrialHandler.getNetIncome() + commercialHandler.getNetIncome();
 *     return Math.round((yearGDP / 12) * 100) / 100;
 *
 * It ASSIGNED one thing and RETURNED another - the annualised figure divided by
 * twelve, which is a different number that had been computed a month earlier.
 * And what it assigned was an income-approach GDP with only two of its terms:
 * wages plus profits. Once businesses started paying interest their profits went
 * negative, swamped the wage bill, and the whole economy read as negative output
 * while its shops were plainly full and its builders busy.
 *
 * Profit is not production. A city where every firm loses money still grows food,
 * builds houses and houses people, and GDP has to say so.
 *
 * HOW IT IS MEASURED NOW
 *
 * The expenditure approach - what everything produced actually gets spent on:
 *
 *     GDP = C + I + G + NX
 *
 *   C   households buying goods from shops, and paying rent for somewhere to live
 *   I   construction work put in place, plus the change in stock held
 *   G   what the city spends running the services it owns
 *   NX  exports less imports; nothing is exported yet, so this is negative
 *
 * Only FINAL spending is counted, which is what keeps intermediate trade from
 * being counted twice: the food a store buys from a mill is not in C - only the
 * food the store then sells to a household is. Imports are subtracted for the
 * same reason, because they were produced somewhere else.
 *
 * It cannot go negative for a bad month of trading, which is the whole point.
 *
 * WHY IT WENT NEGATIVE ANYWAY, AND THE TWO ARITHMETIC ERRORS BEHIND IT
 *
 * A hand-played city of 37,730 people reported monthly GDP of -$446,424 for a
 * hundred months while its shops were full, its builders busy and its net income
 * positive. The line above claimed only net exports could be negative. It was
 * wrong twice.
 *
 * 1. IMPORTED MATERIALS WERE SUBTRACTED AND NEVER ADDED BACK.
 *
 *    Construction materials bought abroad were counted as an import the month
 *    they were bought, but the stockpile they went into was not counted as
 *    anything - inventory only ever meant food. So ordering 8,000 houses took
 *    $480,000 straight off GDP on the spot, and the houses those materials
 *    became were recognised gradually over the following years as construction
 *    work. Buy in bulk and the city's measured output collapses in the month it
 *    invests the most, which is exactly backwards.
 *
 *    Materials in the yard are inventory. They are counted as such now, so the
 *    import and the stock-build cancel in the month of purchase and the value
 *    appears as output only when the work is actually put in place.
 *
 * 2. INVENTORY WAS VALUED, NOT MEASURED.
 *
 *    investmentInventories was `units x price now` less `units x price then`, so
 *    a change in PRICE moved GDP with no change in production at all. A city
 *    holding 804,000 units of food books a quarter of its warehouse as negative
 *    output when the food price eases. That is a holding loss, not a fall in
 *    production, and real accounts strip it out with an inventory valuation
 *    adjustment for precisely this reason.
 *
 *    The change is measured in VOLUME at the current price now: what the
 *    warehouse GREW BY, priced today. A pure price move contributes nothing.
 *
 * With both fixed, every term except net exports is non-negative, and net
 * exports is bounded by trade the city actually did.
 */
public class NationalAccounts {

    /** How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). */
    private static final int HISTORY_MONTHS = 120;

    /* ---------------------------- GDP components ---------------------------- */

    private double consumptionGoods;
    private double consumptionHousing;
    private double investmentConstruction;
    private double investmentInventories;
    private double government;
    private double importsFood;
    private double importsMaterials;

    /**
     * Raw material heavy industry buys abroad, and what it ships back out.
     *
     * The city's first exports. Until now getNetExports() could only ever be
     * negative and the government screen said as much - a city that imported
     * food and building materials and sold nothing. A steel mill is the first
     * thing that earns foreign money, and it does so by importing scrap: only
     * the DIFFERENCE between the two is output the city actually produced,
     * which is exactly what net exports is for.
     */
    private double importsRawMaterial;
    private double exports;

    private double gdp;

    /** Monthly GDP, oldest first. */
    private final List<Double> history = new ArrayList<>();

    /* ------------------------- government income ---------------------------- */

    private double taxBusiness;
    private double taxIndustrial;
    private double taxSales;
    private double taxWage;
    private double utilityIncome;
    private double landSales;

    /**
     * Property tax. Its own line rather than folded into taxBusiness, because
     * it is a different kind of levy - charged on what is owned rather than on
     * what was earned - and the whole point of having both is being able to see
     * which one the city is living on.
     */
    private double propertyTax;

    private double interestExpense;
    private double capitalSpending;
    private double landPurchases;

    /**
     * Stock held at the end of last month, in UNITS, so the change can be
     * measured as a volume rather than as a value. See the class note.
     */
    private double lastFoodVolume;
    private double lastMaterialUnits;
    private double lastLuxuryUnits;

    /* =====================================================================
       EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c)

       FOURTH SIGHTING OF THE SHAPE the luxury note below names. Seed 15 of
       the ensemble's import shock read GDP of -16,056 in month 637: raw
       imports of 127,175 against GDP of about 120,000 the months either
       side. The fixJ1b notes put it down to the mills stocking up on
       imported ore; measured, the mills' ore that month was 3,786, and
       there is no ore to stock - iron is not stockable and the mills buy
       what they smelt in the month. It was the city's new railway buying
       its fleet abroad: twenty sets of rolling stock, 90,460, paid to the
       world and landed in NX, with nothing anywhere saying the railway
       still had them. The vans every sector keeps are the same shape, and
       the farms' crops are a maker's stock like a mill's food.

       So the goods in HELD are measured as the others are: each one's
       units held, every sector's stock and pantry together, the change
       from last month priced at what one costs to bring in today
       (GoodsMarket.landedPrice() - the local price while the city has some
       on offer, the import price while it has none, which is what the
       import was paid at). A fleet bought abroad nets to nothing in the
       month it lands; its wear, month by month, is its use, as a shelf's
       sale is.

       A fleet is fixed capital in real accounts, and gross fixed investment
       would count the purchase and never the wear. Here it is counted as
       stock held: Sector keeps it as a pantry worn down monthly, a local
       van's maker books its sale when the fleet takes it, and treating the
       purchase and the wear as one change in what is held is the same rule
       as the four terms before it. Construction stays the only fixed
       investment.

       NOT CARS. The car makers' showroom is the one stock left out, on
       purpose: the households' new cars are not in C (EconomyManager names
       why), so a showroom's sale at home would read as a fall in stock with
       nothing bought against it. The day C counts the households' cars,
       CARS joins HELD. GdpCheck asserts that every good a sector declares
       it holds is in one of the five terms or named here.
       ===================================================================== */

    /**
     * The goods the fifth term measures, in the order the save keeps their
     * units (slots 15 on, EconomyManager.getNationalAccountsState()): a new
     * one goes on the end. FUEL since 0.7.62 (batch K): the refiners' tanks
     * are made output not yet sold, as a mill's shed is.
     */
    public static final Good[] HELD = { Good.CROPS, Good.VANS, Good.ROLLING_STOCK, Good.FUEL };

    /** ...and how many of them a save from 0.7.58 to 0.7.61 carries: the three before FUEL, which such a city held none of. */
    public static final int HELD_BEFORE_FUEL = 3;

    /** ...and the one good a sector holds that no term measures, and why: see EVERY OTHER GOOD A SECTOR HOLDS. */
    public static final Good NOT_HELD = Good.CARS;

    private final double[] lastHeldUnits = new double[HELD.length];

    /** Whether lastHeldUnits is a real baseline: false only after loading a save from before 0.7.58, whose first month books no change in these goods. */
    private boolean heldBaselineKnown = true;

    /*
     * WORK IN HAND IS NOT AN INVENTORY TERM ANY MORE (2026-09-11).
     *
     * There was a third term: construction ordered and not yet delivered, at
     * contract value, counted as stock the month the order was placed and
     * run down as the work was recognised. It existed to cancel an import
     * that landed months before the output it paid for - the builders bought
     * an order's whole material the day it was placed - and it did that, at
     * the price of booking the whole contract as output on the order day
     * and nothing net over the years of building. The crews draw material
     * as they build now (see BuildingsStacks.materialsOwed), so the import
     * and the work it goes into land in the same month, and the honest
     * account is the plain one: the work put in place is investment, the
     * material bought abroad for it is an import, and the difference is
     * what the city's builders added. A contract on the books is a promise,
     * not production.
     */

    /* The three parts of the inventory term, kept so a diagnostic can say which
       one moved rather than leaving the reader to infer it from the total. */
    private double invFood;
    private double invMaterials;
    private double invLuxuries;
    private double invHeld;

    /**
     * Whether last month's stock is actually known.
     *
     * TRUE for a new city, and that is not a technicality: a city that has never
     * traded really does start with an empty warehouse, so its first month of
     * stock IS production and skipping it would lose real output.
     *
     * It goes false in exactly one place - restoring a save written before stock
     * was tracked in units. Such a save has no baseline to compare against, and
     * the month after it books no inventory change at all, which costs one
     * month's accuracy instead of booking an entire existing warehouse as that
     * month's production.
     */
    private boolean inventoryBaselineKnown = true;

    /**
     * Puts back the month a save was taken in.
     *
     * lastInventoryValue matters more than the GDP figure does. Investment in
     * inventories is a CHANGE - stock now less stock last month - so a loaded
     * city that thinks last month's stock was zero counts its entire warehouse
     * as this month's production. On a city holding 15,800 units of food that
     * more than doubled the next month's GDP, which then fed the interest rate.
     *
     * The rolling history is not restored here: it is not saved with the
     * month, and inventing entries for it would be worse than a short one.
     * Since 0.7.31 the load path puts it back from the graph history's GDP
     * series, which is saved - see seedHistory().
     */
    public void restore(double gdp, double lastFoodVolume,
                        double consumptionGoods, double consumptionHousing,
                        double investmentConstruction, double investmentInventories,
                        double government, double importsFood, double importsMaterials,
                        double importsRawMaterial, double exports,
                        double lastMaterialUnits, double lastLuxuryUnits,
                        boolean baselineKnown) {

        this.gdp = gdp;
        this.lastFoodVolume = lastFoodVolume;
        this.lastMaterialUnits = lastMaterialUnits;
        this.lastLuxuryUnits = lastLuxuryUnits;
        this.inventoryBaselineKnown = baselineKnown;

        // The components too, not just the total. They are what the national
        // accounts screen shows and what the GDP figure is made of; restoring
        // the sum alone gives a city whose GDP is right and whose C, I, G and
        // NX are all zero, which is a worse kind of wrong than either.
        this.consumptionGoods = consumptionGoods;
        this.consumptionHousing = consumptionHousing;
        this.investmentConstruction = investmentConstruction;
        this.investmentInventories = investmentInventories;
        this.government = government;
        this.importsFood = importsFood;
        this.importsMaterials = importsMaterials;
        this.importsRawMaterial = importsRawMaterial;
        this.exports = exports;
    }

    /**
     * Last month's food inventory, as a VOLUME.
     *
     * It counted loaves until 2026-09-15, when food became thirteen goods and
     * kilograms of grain stopped being addable to kilograms of fish. It is a
     * fixed-weight index across the thirteen now - each good's stock weighted
     * by its world import price, which is a constant of the model - so it is
     * still a quantity, still immune to a price move, and still does not
     * belong in redenominate().
     */
    public double getLastFoodVolume()    { return lastFoodVolume; }
    public double getLastMaterialUnits() { return lastMaterialUnits; }
    public double getLastLuxuryUnits()   { return lastLuxuryUnits; }
    public double getInvFood() { return invFood; }
    public double getInventoryFood()         { return invFood; }
    public double getInventoryMaterials()    { return invMaterials; }
    public double getInventoryLuxuries()     { return invLuxuries; }
    /** The fifth term: the change in every good in HELD that the sectors hold, priced at what one costs to bring in (0.7.58). */
    public double getInventoryHeld()         { return invHeld; }
    /** Last month's units of each good in HELD, in its order (0.7.58). */
    public double[] getLastHeldUnits()       { return lastHeldUnits.clone(); }
    public boolean isBaselineKnown()     { return inventoryBaselineKnown; }

    /**
     * ...and the units of HELD's goods last month (0.7.58), or null for a
     * save from before them: then the first month books no change in them,
     * rather than booking every fleet in the city as that month's output.
     */
    public void restoreHeld(double[] lastHeld) {
        java.util.Arrays.fill(lastHeldUnits, 0);
        // A save from before FUEL was held (0.7.58 to 0.7.61) carries the three
        // before it and held no fuel: its baseline is known, fuel's is zero.
        heldBaselineKnown = lastHeld != null && lastHeld.length >= HELD_BEFORE_FUEL;
        if (heldBaselineKnown) System.arraycopy(lastHeld, 0, lastHeldUnits, 0, Math.min(HELD.length, lastHeld.length));
    }

    /**
     * Measures the month.
     *
     * Every argument is a figure some handler already had; nothing here is
     * estimated. Called once a month, after the sector income statements have
     * run and before anything reads the result.
     *
     * Stock arrives as UNITS and a price, never as a pre-multiplied value - see
     * the class note. Materials are here alongside food because a yard full of
     * imported brick is inventory in exactly the way a warehouse full of food
     * is, and leaving it out is what made a bulk order read as negative output.
     */
    public void update(double retailSales, double rentPaid,
                       double constructionWorkDone,
                       double foodUnits, double foodStockWrittenOff, double foodPrice,
                       double materialUnits, double materialPrice,
                       double luxuryUnits, double luxuryPrice,
                       double governmentServices,
                       double foodImports, double materialImports,
                       double rawMaterialImports, double exportRevenue) {
        update(retailSales, rentPaid, constructionWorkDone, foodUnits, foodStockWrittenOff, foodPrice,
                materialUnits, materialPrice, luxuryUnits, luxuryPrice, governmentServices,
                foodImports, materialImports, rawMaterialImports, exportRevenue, null, null);
    }

    /**
     * ...and with every other good the sectors hold (0.7.58): heldUnits and
     * heldPrices in HELD's order - the units held, every sector's stock and
     * pantry together, and what one costs to bring in today. Null for none,
     * which is what the shorter overload passes.
     */
    public void update(double retailSales, double rentPaid,
                       double constructionWorkDone,
                       double foodUnits, double foodStockWrittenOff, double foodPrice,
                       double materialUnits, double materialPrice,
                       double luxuryUnits, double luxuryPrice,
                       double governmentServices,
                       double foodImports, double materialImports,
                       double rawMaterialImports, double exportRevenue,
                       double[] heldUnits, double[] heldPrices) {

        consumptionGoods = retailSales;
        consumptionHousing = rentPaid;

        investmentConstruction = constructionWorkDone;

        /*
         * The change in stock, as a VOLUME, priced today.
         *
         * Building up a warehouse is production that has not been sold yet;
         * running it down is consumption of something produced in an earlier
         * month. A change in PRICE is neither, and measuring the change in value
         * rather than in volume booked every price move as production.
         */
        if (inventoryBaselineKnown) {
            /*
             * The write-off is added back before the change is measured. Stock
             * destroyed with the capacity that held it left the city, but it was
             * not consumed and it was not unproduced - counting it here would
             * book a demolition as a month of negative output.
             */
            invFood = ((foodUnits + foodStockWrittenOff) - lastFoodVolume) * foodPrice;

            /*
             * THE MATERIALS PLANT'S WAREHOUSE IS THE THIRD TERM, and the city's
             * yard is not, since the sector template (2026-09-11).
             *
             * The yard is an intermediate input whose value is captured in the
             * contracts it serves: work in progress is measured at CONTRACT
             * value, and a contract already embodies the materials the job
             * will consume, so counting the yard as well subtracted the same
             * brick twice - observed as Imatl -1,340 against Iconstr +1,163 in
             * a month with no trade at all. The yard stays out.
             *
             * The plant's stock is different in kind: it is output MADE and not
             * yet sold, exactly as a mill's warehouse of food is. Building it
             * up is production; the sale to the builders moves it into a
             * contract and nets to zero. So `materialUnits` is the makers'
             * stock and it is measured like food.
             */
            invMaterials = (materialUnits - lastMaterialUnits) * materialPrice;

            /* =============================================================
               AND THE BOUTIQUE'S STOCKROOM IS THE FOURTH TERM (2026-09-17)

               THIRD SIGHTING OF ONE SHAPE. A good a sector HOLDS and has not
               sold yet needs a line here or the month it arrives is a month of
               negative output: the city pays the world for it, the payment
               lands in NX, and nothing anywhere says the city still has the
               thing it bought. Food got its line, then the materials plant's
               warehouse, and now the luxury shelf.

               IT SHOWED AS GDP OF -$8,636k IN MONTH 7 of a fresh city, which
               is one Boutique's three-month stockroom - 960 pieces at $9 -
               imported in a single month and counted on one side only. Sixty
               months in a long playtest read negative before this line; one
               did after it, and that one is the same shape in construction
               that the rounding note below already describes.

               PRICED AT WHAT THE SHOP PAID, like the materials above: this is
               one good counted in pieces, so it needs no weighting, and the
               local price of a good the city only imports IS the landed cost.
               That is what makes the arrival net to zero - NX takes the
               landed cost out and this puts the same number back - and leaves
               the shop's MARGIN as the only thing that scores, in the month
               it is actually earned, which is what a retailer adds.

               THE RULE, since it is now three: any good a sector can hold and
               does not consume within the month belongs in this block the day
               the good is written. Two of the three were found by a negative
               month rather than by remembering.
               ============================================================= */
            invLuxuries = (luxuryUnits - lastLuxuryUnits) * luxuryPrice;

            // ...and every other good held, a good at a time (0.7.58): see
            // EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM.
            invHeld = 0;
            if (heldBaselineKnown) {
                for (int i = 0; i < HELD.length; i++) {
                    invHeld += (held(heldUnits, i) - lastHeldUnits[i]) * held(heldPrices, i);
                }
            }

            investmentInventories = invFood + invMaterials + invLuxuries + invHeld;
        } else {
            investmentInventories = 0;
            invHeld = 0;
            inventoryBaselineKnown = true;
        }
        heldBaselineKnown = true;

        lastFoodVolume = foodUnits;
        lastMaterialUnits = materialUnits;
        lastLuxuryUnits = luxuryUnits;
        for (int i = 0; i < HELD.length; i++) lastHeldUnits[i] = held(heldUnits, i);

        government = governmentServices;

        importsFood = foodImports;
        importsMaterials = materialImports;
        importsRawMaterial = rawMaterialImports;
        exports = exportRevenue;

        /*
         * Rounded to the cent, which is how every other money figure in the game
         * is carried - and here it also settles the last way GDP could read
         * negative.
         *
         * Construction revenue earned and the work in progress it comes out of
         * are the same quantity reached by two different routes, so in a month
         * where they are all that happened they cancel to about -1e-13 rather
         * than to zero. Rounding turns that into -0.0, which in IEEE arithmetic
         * is NOT less than zero, so an idle month reads as the zero it is.
         *
         * This is a rounding guard, not a floor: a genuinely negative figure
         * survives it intact and will still be caught.
         */
        /*
         * ROUNDED RELATIVE TO THE MONTH, NOT TO THE CENT.
         *
         * This was `Math.round(x * 100) / 100.0`, and the reason was good: the
         * construction revenue earned and the work in progress it comes out of
         * are the same quantity reached by two routes, so in an idle month they
         * cancel to about -1e-13 rather than to zero, and -0.0 reads as
         * non-negative where -1e-13 does not. It is a rounding guard, not a
         * floor, and a genuinely negative figure still survives it.
         *
         * WHAT WAS WRONG WITH IT: a cent is a UNIT. After a hundred-to-one
         * currency reform a cent of new money is a dollar of old, so the same
         * line quantises GDP a hundred times more coarsely - and GDP is read by
         * the debt manager, the migration target and every investment decision
         * in the game. Measured: a reformed city and its unreformed twin, run
         * side by side, ended a hundred months apart at 7,247 people against
         * 5,435 and a price index of 4.25 against 3.39. Every stock in both
         * cities was correct to the cent the whole way; they simply rounded
         * different amounts off the same number and the difference compounded.
         *
         * A relative epsilon does the same job in any unit. Twelve digits below
         * the size of the terms being added is far below anything real and far
         * above the cancellation being guarded against.
         */
        double raw = getConsumption() + getInvestment() + government + getNetExports();
        double magnitude = Math.abs(getConsumption()) + Math.abs(getInvestment())
                + Math.abs(government) + Math.abs(getNetExports());
        gdp = Math.abs(raw) <= magnitude * 1e-12 ? 0 : raw;

        history.add(gdp);
        while (history.size() > HISTORY_MONTHS) {
            history.remove(0);
        }
    }

    /** One good's figure from an array in HELD's order: 0 past its end, for null, or for anything not a finite number. */
    private static double held(double[] values, int i) {
        if (values == null || i >= values.length || !Double.isFinite(values[i])) return 0;
        return values[i];
    }

    /**
     * The city's own budget for the month.
     *
     * Land deliberately appears HERE and nowhere in GDP. Selling a field to a
     * developer moves an asset that already existed from one owner to another;
     * nothing was produced, so counting it as output would let a city
     * manufacture growth by trading land with itself. It moves the treasury,
     * which is what these lines are for, and the building that goes up on it is
     * what shows up as investment.
     */
    public void updateGovernment(double business, double industrial, double sales,
                                 double wage, double utilities, double land,
                                 double property,
                                 double interest, double capital, double landBought) {
        updateGovernment(business, industrial, sales, wage, utilities, land,
                property, interest, capital, landBought, 0, 0);
    }

    /**
     * The same, with the pension flows.
     *
     * Appended rather than woven in, per the standing rule that order is the
     * format - and the shorter overload above is kept so the existing callers
     * and the harnesses that pin this signature do not all have to move at once.
     *
     * Contributions are REVENUE and pensions are SPENDING, on separate lines
     * rather than netted. A city where both are large and cancel is a very
     * different city from one where neither exists, and netting them would make
     * the two look identical on the balance line.
     */
    public void updateGovernment(double business, double industrial, double sales,
                                 double wage, double utilities, double land,
                                 double property,
                                 double interest, double capital, double landBought,
                                 double contributions, double pensions) {
        updateGovernment(business, industrial, sales, wage, utilities, land, property,
                interest, capital, landBought, contributions, pensions, 0, 0, 0, 0);
    }

    /** The same again with healthcare but no schools, for the older callers. */
    public void updateGovernment(double business, double industrial, double sales,
                                 double wage, double utilities, double land,
                                 double property,
                                 double interest, double capital, double landBought,
                                 double contributions, double pensions,
                                 double healthFees, double healthSpending) {
        updateGovernment(business, industrial, sales, wage, utilities, land, property,
                interest, capital, landBought, contributions, pensions,
                healthFees, healthSpending, 0, 0);
    }

    /**
     * The same again, with healthcare.
     *
     * Two lines, not one net figure, for exactly the reason the pension flows
     * are two lines: a city running a $6M health service that recovers $1.4M in
     * fees is a very different city from one running a $4.6M service, and
     * netting them would draw both the same. The fees are revenue like any
     * other; the bill is spending like any other.
     *
     * The bill is the GROSS cost - payroll plus upkeep - because that is what
     * leaves the treasury. See Healthcare.
     */
    public void updateGovernment(double business, double industrial, double sales,
                                 double wage, double utilities, double land,
                                 double property,
                                 double interest, double capital, double landBought,
                                 double contributions, double pensions,
                                 double healthFees, double healthSpending,
                                 double schoolFees, double schoolSpending) {
        updateGovernment(business, industrial, sales, wage, utilities, land, property,
                interest, capital, landBought, contributions, pensions,
                healthFees, healthSpending, schoolFees, schoolSpending, 0);
    }

    /**
     * ...and with what the city paid to keep a sector alive.
     *
     * SPENDING like any other, and the last line the treasury bridge was
     * missing. A subsidy leaves the treasury on the spot - Game.paySubsidyIfOwed
     * does `cash -= owed` - and appeared on no budget of any kind, so a city
     * with the dial on reported a surplus it did not have by exactly what it
     * had just given away. Held on its own line rather than folded into the
     * sector's own books deliberately: the sector's statement still shows the
     * loss, because it made one, and what changed is only who absorbed it.
     */
    public void updateGovernment(double business, double industrial, double sales,
                                 double wage, double utilities, double land,
                                 double property,
                                 double interest, double capital, double landBought,
                                 double contributions, double pensions,
                                 double healthFees, double healthSpending,
                                 double schoolFees, double schoolSpending,
                                 double subsidies) {
        this.subsidies = subsidies;
        this.healthFees = healthFees;
        this.healthSpending = healthSpending;
        this.educationFees = schoolFees;
        this.educationSpending = schoolSpending;
        taxBusiness = business;
        taxIndustrial = industrial;
        taxSales = sales;
        taxWage = wage;
        utilityIncome = utilities;
        landSales = land;
        propertyTax = property;
        interestExpense = interest;
        capitalSpending = capital;
        landPurchases = landBought;
        this.contributions = contributions;
        this.pensions = pensions;
    }

    /** Pension contributions in, pensions out. See SocialSecurity. */
    private double contributions;
    private double pensions;

    /** EI premiums in; EI benefits and student grants out. See Unemployment. Set beside updateGovernment(). */
    private double eiPremiums;
    private double eiBenefits;
    private double studentGrants;

    /** The health premium off every wage, in; a revenue line beside the EI premium (2026-09-19). See TaxPolicy. */
    private double healthPremiums;

    /** Interest the graduates paid on their student loans, in; a revenue line beside the premiums (2026-09-21). See TaxPolicy. */
    private double studentLoanInterest;

    public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants) {
        setOutsideLines(eiPremiums, eiBenefits, studentGrants, 0);
    }

    /** ...and with the health premium beside them (2026-09-19). */
    public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants,
                                double healthPremiums) {
        setOutsideLines(eiPremiums, eiBenefits, studentGrants, healthPremiums, 0);
    }

    /** ...and the student loan interest beside those (2026-09-21). */
    public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants,
                                double healthPremiums, double studentLoanInterest) {
        this.eiPremiums = eiPremiums;
        this.eiBenefits = eiBenefits;
        this.studentGrants = studentGrants;
        this.healthPremiums = healthPremiums;
        this.studentLoanInterest = studentLoanInterest;
    }

    /**
     * THE CENTRAL BANK'S TWO LINES (0.7.0): its profit remitted to the
     * treasury, in; the interest the treasury paid it on its advances, out.
     * Their own setter, for the reason setTransitLines() has one. Game moves
     * the cash for both where the central bank settles with the treasury, at
     * the top of the month, and they are here so the budget balance - and
     * the bridge it starts - carries them.
     */
    private double centralBankRemittance, centralBankInterest;

    public void setCentralBankLines(double remittance, double interest) {
        this.centralBankRemittance = remittance;
        this.centralBankInterest = interest;
    }

    /** What the central bank remitted: its profit, which is the interest the city paid itself less what reserves cost. */
    public double getCentralBankRemittance() { return centralBankRemittance; }
    /** What the treasury paid the central bank on its advances. */
    public double getCentralBankInterest()   { return centralBankInterest; }

    /**
     * THE CITY'S MORTGAGE INSURANCE (0.7.11): the premiums the landlords paid
     * on the mortgages written this month, in; what the insurance paid the
     * bank on insured mortgages the month's defaults wrote down, out. Their
     * own setter, for the reason the central bank's pair has one; Game moves
     * the cash for both where they happen - the premium as a mortgage is
     * written, the claim through TreasuryLine.MORTGAGE_INSURANCE_CLAIMS - so
     * the budget balance and the bridge carry them.
     */
    private double mortgagePremiums, mortgageClaims;

    public void setMortgageInsuranceLines(double premiums, double claims) {
        this.mortgagePremiums = Math.max(0, premiums);
        this.mortgageClaims = Math.max(0, claims);
    }

    /** The premiums the landlords paid the city's insurance this month - a revenue line. */
    public double getMortgagePremiums() { return mortgagePremiums; }

    /**
     * THE TRANSFER FROM THE CITY'S FUND (0.7.14): the withdrawal dial's share
     * of the fund's value (0.7.48; by default a twelfth of
     * TreasuryFund.TRANSFER_RATE, Norway's fiscal rule), paid from its cash
     * and over the default from what it sold - in, a revenue line beside the central bank's
     * remittance, with its own setter for the same reason. Game moves the
     * cash where the treasury settles at the top of the month. It is part of
     * the budget's balance, and so of the surplus the fund's dial takes a
     * share of: the loop is intended - a fund that grows pays the budget
     * more, and a bigger surplus puts more back into it.
     */
    private double fundTransfer;

    public void setFundTransfer(double transfer) { this.fundTransfer = Math.max(0, transfer); }

    /** What the city's fund paid the budget this month - a revenue line. */
    public double getFundTransfer() { return fundTransfer; }

    /**
     * FOOD ASSISTANCE (0.7.43): the vouchers the treasury paid toward the
     * households' groceries, a transfer like EI - a spending line, with its
     * own setter for the fund transfer's reason: Game moves the cash where it
     * is paid, after the month's sale (TreasuryLine.FOOD_ASSISTANCE).
     */
    private double foodAssistance;

    public void setFoodAssistance(double paid) { this.foodAssistance = Math.max(0, paid); }

    /** What the treasury paid toward the households' groceries this month - a spending line. */
    public double getFoodAssistance() { return foodAssistance; }
    /** What the city's insurance paid the bank this month on insured mortgages written down - a spending line. */
    public double getMortgageClaims()   { return mortgageClaims; }

    public double getEiPremiums()    { return eiPremiums; }
    /** What the health premium raised this month - a government revenue line, against the health service's cost. */
    public double getHealthPremiums(){ return healthPremiums; }
    /** What the graduates paid in interest on their student loans this month - a government revenue line; the principal is not one. */
    public double getStudentLoanInterest() { return studentLoanInterest; }
    public double getEiBenefits()    { return eiBenefits; }
    public double getStudentGrants() { return studentGrants; }

    /** The police and the prisons: payroll and upkeep. Set beside updateGovernment(), like the EI lines. See Crime. */
    private double safetySpending;
    public void setSafetySpending(double spending) { this.safetySpending = spending; }
    public double getSafetySpending() { return safetySpending; }

    /**
     * Fares in, the buses' and trams' wage bill out. See EconomyManager.
     *
     * ITS OWN SETTER RATHER THAN TWO MORE ARGUMENTS on updateGovernment(),
     * which already takes seventeen positional doubles and is exactly the
     * machine for transposing two of them silently that HistorySave's header
     * warns about. Safety went in this way for the same reason.
     *
     * IN THE TOTALS SINCE 0.7.49 (B9): the fares in getTotalRevenue() and
     * the bill in getTotalExpenses(). Until then the bill was paid by nobody
     * and the fares were journalled, outside the budget.
     */
    private double transitFares, transitSpending;

    public void setTransitLines(double spending, double fares) {
        this.transitSpending = spending;
        this.transitFares = fares;
    }

    public double getTransitSpending() { return transitSpending; }
    public double getTransitFares()    { return transitFares; }

    /** Patient and funeral fees in, the health service's bill out. See Healthcare. */
    private double healthFees;
    private double healthSpending;

    /** Tuition in, and what the schools cost. See EconomyManager.setEducation. */
    private double educationFees;
    private double educationSpending;

    /** What the city paid to hold a loss-making sector at break-even. */
    private double subsidies;
    public double getSubsidies() { return subsidies; }

    /* =====================================================================
       THE MONTH THE GOVERNMENT ACTUALLY HAD

       Twenty-seven numbers, saved and restored as one - seventeen when this
       note was written, and every one appended since has gone on the END.

       WHY THIS IS CARRIED RATHER THAN REBUILT. updateGovernment() is a plain
       setter and every one of its arguments is a FLOW struck inside the tick -
       the wage tax off a staffed wage bill that only exists while the month is
       being played, the contributions off the same, the profit taxes off sector
       statements the load path re-derives, the utility income off charges that
       have not been raised yet. The load path used to call
       refreshGovernmentAccounts() and hope, and it came back with four of the
       ten revenue lines at zero: wage tax 18.44 -> 0, utilities 8.81 -> 0,
       contributions 7.32 -> 0, profit tax 0.21 -> 0. A budget of 60.88 reloaded
       as 26.10, which is the SURPLUS line on the first screen a returning player
       opens, and the bridge underneath it stopped footing.

       A flow cannot be reconstructed from the state a month ended in. This
       codebase has now been caught by that ten times; carrying it is the answer
       every time.

       Refused WHOLE on a wrong length, following Healthcare.restore(): a save
       from before this existed has no array, keeps whatever the rebuild
       produced, and fills in properly after one month - which is exactly the
       city it was.
       ===================================================================== */
    /** The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. */
    public static final int GOVERNMENT_SLOTS_WITH_GRANTS = 20;

    double[] governmentToSave() {
        return new double[] {
            taxBusiness, taxIndustrial, taxSales, taxWage,
            utilityIncome, landSales, propertyTax,
            interestExpense, capitalSpending, landPurchases,
            contributions, pensions,
            healthFees, healthSpending,
            educationFees, educationSpending,
            subsidies,
            eiPremiums, eiBenefits, studentGrants,
            // ...and the police and the prisons, appended 2026-09-11.
            safetySpending,
            // ...and the health premium, appended 2026-09-19. An older save
            // reads zero, which is what that city collected.
            healthPremiums,
            // ...and the student loan interest, appended 2026-09-21, for the
            // premium's reason: an older save reads zero, which is what that
            // city's graduates were charged.
            studentLoanInterest,
            // ...and the central bank's two lines, appended in 0.7.0: an
            // older save reads zero for both, which is a city with no
            // central bank yet to pay or be paid by.
            centralBankRemittance, centralBankInterest,
            // ...and the mortgage insurance's two, appended in 0.7.11: an
            // older save reads zero, which is what a city that insured no
            // mortgage took in and paid out.
            mortgagePremiums, mortgageClaims,
            // ...and the transfer from the city's fund, appended in 0.7.14:
            // an older save reads zero, a city with no fund.
            fundTransfer,
            // ...and food assistance, appended in 0.7.43: an older save reads
            // zero, a city that paid none.
            foodAssistance };
    }

    void restoreGovernment(double[] saved) {
        // Twenty-nine since food assistance; twenty-eight since the city's fund; twenty-seven since the mortgage
        // insurance; twenty-five since the central bank; twenty-three since
        // the student loan interest; twenty-two since the health premium;
        // twenty-one since the police; twenty since EI and the grants;
        // seventeen from a save before them.
        if (saved == null || (saved.length != 17 && saved.length != 20
                && saved.length != 21 && saved.length != 22 && saved.length != 23
                && saved.length != 25 && saved.length != 27 && saved.length != 28
                && saved.length != 29)) return;
        fundTransfer          = saved.length >= 28 ? saved[27] : 0;
        foodAssistance        = saved.length >= 29 ? saved[28] : 0;
        centralBankRemittance = saved.length >= 25 ? saved[23] : 0;
        centralBankInterest   = saved.length >= 25 ? saved[24] : 0;
        mortgagePremiums      = saved.length >= 27 ? saved[25] : 0;
        mortgageClaims        = saved.length >= 27 ? saved[26] : 0;
        eiPremiums = saved.length >= 20 ? saved[17] : 0;
        eiBenefits = saved.length >= 20 ? saved[18] : 0;
        studentGrants = saved.length >= 20 ? saved[19] : 0;
        safetySpending = saved.length >= 21 ? saved[20] : 0;
        healthPremiums = saved.length >= 22 ? saved[21] : 0;
        studentLoanInterest = saved.length >= 23 ? saved[22] : 0;
        taxBusiness = saved[0];   taxIndustrial = saved[1];
        taxSales = saved[2];      taxWage = saved[3];
        utilityIncome = saved[4]; landSales = saved[5];
        propertyTax = saved[6];   interestExpense = saved[7];
        capitalSpending = saved[8]; landPurchases = saved[9];
        contributions = saved[10]; pensions = saved[11];
        healthFees = saved[12];   healthSpending = saved[13];
        educationFees = saved[14]; educationSpending = saved[15];
        subsidies = saved[16];
    }

    public double getContributions() { return contributions; }
    public double getPensions()      { return pensions; }
    public double getHealthFees()    { return healthFees; }
    public double getHealthSpending(){ return healthSpending; }
    public double getEducationFees()    { return educationFees; }
    public double getEducationSpending(){ return educationSpending; }

    /* ------------------------------- GDP ------------------------------------ */

    public double getConsumption()  { return consumptionGoods + consumptionHousing; }
    public double getInvestment()   { return investmentConstruction + investmentInventories; }
    public double getNetExports() {
        return exports - (importsFood + importsMaterials + importsRawMaterial);
    }

    public double getConsumptionGoods()       { return consumptionGoods; }
    public double getConsumptionHousing()     { return consumptionHousing; }
    public double getInvestmentConstruction() { return investmentConstruction; }
    public double getInvestmentInventories()  { return investmentInventories; }
    public double getGovernment()             { return government; }
    public double getImportsFood()            { return importsFood; }
    public double getImportsMaterials()       { return importsMaterials; }
    public double getImportsRawMaterial()     { return importsRawMaterial; }
    public double getExports()                { return exports; }
    public double getTotalImports() {
        return importsFood + importsMaterials + importsRawMaterial;
    }

    public double getGdp() { return gdp; }

    /** The last twelve months, or as many as there are - not gdp * 12. */
    public double getAnnualGdp() {
        double total = 0;
        int from = Math.max(0, history.size() - 12);
        for (int i = from; i < history.size(); i++) {
            total += history.get(i);
        }
        return total;
    }

    public double getGdpPerCapita(long population) {
        return (population > 0) ? getAnnualGdp() / population : 0;
    }

    /* ------------------------------ growth ---------------------------------- */

    /**
     * Month on month, as an annual rate.
     *
     * Compounded rather than multiplied by twelve, because a city growing 2% a
     * month is growing 27% a year, not 24%.
     */
    public double getMonthlyGrowthAnnualised() {
        if (history.size() < 2) return 0;

        double previous = history.get(history.size() - 2);
        if (previous <= 0) return 0;

        double monthly = gdp / previous;
        return Math.pow(monthly, 12) - 1;
    }

    /** This month against the same month a year ago. The steadier number. */
    public double getYearOnYearGrowth() {
        if (history.size() < 13) return 0;

        double yearAgo = history.get(history.size() - 13);
        if (yearAgo <= 0) return 0;

        return (gdp / yearAgo) - 1;
    }

    /** Average monthly GDP over the last twelve, to smooth a lumpy month. */
    public double getTrendGdp() {
        int from = Math.max(0, history.size() - 12);
        int count = history.size() - from;
        return (count > 0) ? getAnnualGdp() / count : 0;
    }

    public int getMonthsRecorded() { return history.size(); }

    public List<Double> getHistory() { return history; }

    /**
     * The rolling history put back after a load (0.7.31, the Government
     * spec's B1): the last HISTORY_MONTHS of the graph history's GDP series
     * (HistorySave.getGdp(), a month's GDP kept every month and saved), in
     * place of the one month the rebuild records.
     *
     * WHY. It was not restored at all, so for the twelve months after every
     * load each "of annual GDP" was one to eleven months scaled up - a
     * 2,400-month city read 36.4% where the year said 34.4% - the Government
     * tab said "There is not a year of output recorded yet", Output "Months
     * recorded 1", and the header's GDP tile "$0 / yr" and "first year". The
     * series is the same figure to ten dollars (HistorySave keeps thousands
     * to two places), so the year read after a load is the year the city had. A city with no
     * graph history (no history file) keeps what the rebuild recorded.
     *
     * Read only by screens, the time skip's report and the year's GDP
     * EconomyManager keeps for the left panel: nothing in the month reads
     * the history, so a loaded city
     * plays on exactly as before.
     */
    public void seedHistory(List<Double> monthly) {
        if (monthly == null || monthly.isEmpty()) return;
        history.clear();
        int from = Math.max(0, monthly.size() - HISTORY_MONTHS);
        for (int i = from; i < monthly.size(); i++) {
            Double v = monthly.get(i);
            history.add(v == null ? 0 : v);
        }
    }

    /* ---------------------------- government -------------------------------- */

    public double getTaxBusiness()   { return taxBusiness; }
    public double getTaxIndustrial() { return taxIndustrial; }
    public double getTaxSales()      { return taxSales; }
    public double getTaxWage()       { return taxWage; }
    public double getUtilityIncome() { return utilityIncome; }
    public double getLandSales()     { return landSales; }
    public double getPropertyTax()   { return propertyTax; }

    public double getTotalRevenue() {
        return taxBusiness + taxIndustrial + taxSales + taxWage
                + utilityIncome + landSales + propertyTax + contributions
                + eiPremiums + healthFees + educationFees + healthPremiums
                // ...and the interest on the student loans (2026-09-21); the
                // principal repaid is not revenue and is not here.
                + studentLoanInterest
                // ...and the central bank's profit (0.7.0); its advances are
                // financing, like paper raised, and are not here either.
                + centralBankRemittance
                // ...and the premiums on the mortgages it insures (0.7.11).
                + mortgagePremiums
                // ...and the transfer from the city's fund (0.7.14).
                + fundTransfer
                // ...and the transit fares (0.7.49, B9): in the cash through the
                // tax take since 2026-09-16, and on no line of the budget until now.
                + transitFares;
    }

    public double getInterestExpense() { return interestExpense; }
    public double getCapitalSpending() { return capitalSpending; }
    public double getLandPurchases()   { return landPurchases; }

    public double getTotalExpenses() {
        return interestExpense + capitalSpending + landPurchases + pensions
                + eiBenefits + studentGrants
                + healthSpending + educationSpending + safetySpending + subsidies
                // ...and the interest on the central bank's advances (0.7.0).
                + centralBankInterest
                // ...and what its mortgage insurance paid the bank (0.7.11).
                + mortgageClaims
                // ...and the food vouchers it paid (0.7.43).
                + foodAssistance
                // ...and transit's wages and upkeep, which the treasury pays since 0.7.49 (B9).
                + transitSpending;
    }

    /** Surplus or deficit - what actually moves the city's cash this month. */
    public double getBalance() {
        return getTotalRevenue() - getTotalExpenses();
    }

    /** Revenue as a share of output. The city's effective take from the economy. */
    public double getRevenueToGdp() {
        double annual = getAnnualGdp();
        return (annual > 0) ? (getTotalRevenue() * 12) / annual : 0;
    }

    public double getDebtToGdp(double debt) {
        double annual = getAnnualGdp();
        return (annual > 0) ? debt / annual : 0;
    }

    public void reset() {
        history.clear();
        lastFoodVolume = 0;
        lastMaterialUnits = 0;
        lastLuxuryUnits = 0;
        java.util.Arrays.fill(lastHeldUnits, 0);
        inventoryBaselineKnown = true;   // an empty warehouse is a real baseline
        heldBaselineKnown = true;
        gdp = 0;
    }

    /**
     * The month's national accounts, in the new unit.
     *
     * lastFoodVolume and lastMaterialUnits are QUANTITIES - a weighted index
     * of the thirteen foods, and bricks - and do not move. (Work in progress,
     * while it was a term here, was measured at CONTRACT VALUE, which is
     * money, and leaving it unscaled was the single worst bug in this whole
     * change: GDP came out at -958,009 against +18.43, and the two cities
     * never recovered. Every money field below is scaled for that reason.)
     *
     * THE FOOD INDEX NEARLY STOPPED BEING A QUANTITY ON 2026-09-15. The first
     * draft of the thirteen-good version valued each stock at its own LOCAL
     * price and handed the accounts money, which would have made this a money
     * field, needed scaling here, and - much worse - booked every move in the
     * food market as production, which is the exact bug the note on
     * update()'s inventory block records as already fixed once. Measured
     * before it shipped: 8 of 8 ensemble seeds clean became 6 of 8. Weighting
     * the thirteen at their WORLD import prices, which are constants, keeps it
     * a volume.
     */
    public void redenominate(double scale) {
        consumptionGoods *= scale;  consumptionHousing *= scale;
        investmentConstruction *= scale;  investmentInventories *= scale;
        government *= scale;
        importsFood *= scale;  importsMaterials *= scale;  importsRawMaterial *= scale;
        exports *= scale;  gdp *= scale;
        taxBusiness *= scale;  taxIndustrial *= scale;
        taxSales *= scale;  taxWage *= scale;
        utilityIncome *= scale;  landSales *= scale;
        propertyTax *= scale;  interestExpense *= scale;
        capitalSpending *= scale;  landPurchases *= scale;
        invFood *= scale;  invMaterials *= scale;  invLuxuries *= scale;  invHeld *= scale;

        /*
         * ...AND THE ROLLING HISTORY, which is ten years of GDP and is what
         * getAnnualGdp() and the growth rates are read off. Leaving it alone
         * spliced a hundred-to-one step into the middle of the series, so the
         * year to date came out as eleven months of old money plus one of new -
         * yearGDP of 69,425 where the unreformed city said 710. Everything that
         * reads a trend read a cliff.
         */
        for (int i = 0; i < history.size(); i++) history.set(i, history.get(i) * scale);
        contributions *= scale;  pensions *= scale;
        eiPremiums *= scale;  eiBenefits *= scale;  studentGrants *= scale;
        healthPremiums *= scale;  studentLoanInterest *= scale;
        centralBankRemittance *= scale;  centralBankInterest *= scale;
        mortgagePremiums *= scale;  mortgageClaims *= scale;
        fundTransfer *= scale;
        foodAssistance *= scale;
        healthFees *= scale;  healthSpending *= scale;
        educationFees *= scale;  educationSpending *= scale;
        safetySpending *= scale;
        subsidies *= scale;
    }

}
