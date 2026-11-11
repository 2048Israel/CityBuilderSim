/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.ToDoubleFunction;

/**
 *
 * @author Jerus
 */
public class UtilitiesHandler {

    public double production;
    public double baseProduction;
    public double consumption;
    public double energyRatio;

    /* =====================================================================
       WATER SUPPLY

       Deliberately production-side only for now. Nothing draws water yet, so
       waterRatio sits at 1 and throttles nothing - consumption is the next
       piece of work, and sewage after that.

       Structured to mirror electricity exactly (base + labour fill -> output
       -> ratio against demand) so that wiring consumption in later is a
       matter of feeding setWaterConsumption(), not reworking this.
       ===================================================================== */

    /**
     * What the city can draw before it builds anything: the legacy wells and
     * the old municipal intake. Same role as the 10,000 kW the grid starts
     * with - enough to get going, nowhere near enough to grow into.
     */
    private static final double BASE_WATER_SUPPLY = 8000;

    /** The kilowatts the grid starts with, before a power plant is built (setWattsProduction()). */
    private static final double BASE_GRID_KW = 10000;

    /**
     * How many founding endowments the city holds (FW9, 2026-10-25;
     * BuildingManager.getFoundingEndowments()): 1 for a founded city. The
     * grid's 10,000 kW and the wells' 8,000 units are one founded city's, as
     * its hundred household places are, and a harness's copy K times over holds
     * K of them: the playtest's town at month 812 lives on the grid and the
     * wells almost alone, and its copy with one of each had 0.2% of its power
     * and 0.3% of its water in its first month. Set by ServicesManager each
     * time the services update.
     */
    private long foundingEndowments = 1;

    public void setFoundingEndowments(long endowments) { this.foundingEndowments = Math.max(1, endowments); }

    private double baseGrid()  { return BASE_GRID_KW * foundingEndowments; }
    private double baseWater() { return BASE_WATER_SUPPLY * foundingEndowments; }

    /**
     * Per-resident draw, in units of 10,000 gallons/month. 0.3 units = 3,000
     * gallons = about 100 gallons/day, the standard US residential figure.
     *
     * This is the people; each building adds its own draw on top (landscaping,
     * cooling, process water). Between them a House and its four residents come
     * to ~1.1 units = ~92 gal/person/day all in.
     */
    private static final double WATER_PER_PERSON = .3;

    /**
     * $50 per unit, i.e. $5 per 1,000 gallons. Real US municipal water runs
     * $4-6 per 1,000 gallons, so this is close to life and - deliberately -
     * affordable next to the wage table: a household of four pays about $55 a
     * month against the lowest full-time wage of $800.
     *
     * Today's figure, priced at the founding's level (times PriceLevel.FACTOR, EP1).
     */
    private double pricePerWaterUnit = .05 * PriceLevel.FACTOR;

    /* =====================================================================
       THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7)

       A Water Treatment Plant treats fresh water, and since the city's land
       is on the world (0.7.57) the city owns a measured area of lake and
       river. The plants together can treat no more than that area yields a
       month - the fresh cap - and the rest of their nameplate idles:

           fresh cap  = FRESH_UNITS_PER_KM2 x owned fresh km2 + the rights
           production = (the wells + desalination) x fill
                        + min(the fresh plants x fill, the fresh cap)

       The wells' BASE_WATER_SUPPLY is groundwater, outside the cap, so at
       zero fill production is still the wells', as before. A desalination
       plant draws the sea and the cap does not reach it. The rights are what
       an older city already pumped beyond its lakes (Game.getFreshRights()),
       so loading one idles nothing it had. Nothing is stored: the cap is the
       land's and the rights', read again each month (setFreshCap()).

       WITH THE CAP NOT BINDING THIS IS THE OLD FORMULA TO THE BIT - the
       nameplate total plus the wells times the fill, as it always was -
       and a handler nobody has told about any land (a bare fixture, as
       WaterCheck's first sections build) has an infinite cap.
       ===================================================================== */

    /**
     * Units of fresh water a month one square kilometre of owned lake or
     * river yields: the world's renewable river runoff (about 42,700 km3 a
     * year, Shiklomanov) over the world's river and stream area (773,000 km2,
     * Allen & Pavelsky 2018) is 5.524e7 m3 a km2 a year, 4.603e6 m3 a month,
     * or 121,600 units of 37.854 m3 (10,000 gallons). One Water Treatment
     * Plant's 60,000 needs 0.49 km2 of it.
     */
    public static final double FRESH_UNITS_PER_KM2 = 121_600;

    public double waterProduction;
    public double baseWaterProduction;

    /** The fresh plants' nameplate and the desalination plants', standing - the two parts of baseWaterProduction past the wells (0.7.59). */
    private double freshNameplate, desalNameplate;

    /** What the city's fresh water yields a month, the rights included; infinite until the city's land says otherwise (setFreshCap()). */
    private double freshCap = Double.POSITIVE_INFINITY;

    /** This month's fresh water treated - the fresh plants at their staffing, held to the cap - and the sea desalinated. */
    private double freshDrawn, desalOutput;
    public double buildingWaterDraw;
    public double residentWaterDraw;

    /**
     * The slice of demand that is actually invoiced: commercial and industrial
     * buildings. Residents draw the majority of the city's water but have no
     * cash to pay with, so billing them would be revenue from nowhere.
     */
    public double billedWaterDraw;
    public double waterConsumption;
    public double waterRatio = 1;

    //jobs
    private double[] utilityWages = new double[11];
    private long[] utilityJobs = new long[11];

    // Same payroll, split by which utility the job belongs to, so the report
    // can show two businesses rather than one lump.
    private double[] electricityWages = new double[11];
    private double[] waterWages = new double[11];
    private double[] fillRate = new double[11];

    private double averageUtilityFill = 1;

    public UtilitiesHandler() {

    }

    //updaters
    public void updateUtilitiesHandler() {
        updateEnergyRatio();
        updateWaterRatio();
    }

    //getters
    public double getEnergyRatio() {
        return energyRatio;
    }

    /** What commerce and light industry pay a kW a month: the struck book's commerce price (THE MERIT ORDER, PW1); the founding tariff until a month is struck. */
    public double getPricerPerWatt() {
        return struck.commercePrice;
    }

    /* -----------------------------------------------------------------------
       READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is
       already a pure printer - it only reads these same fields - so the screen
       reads them live too and the two always agree.
       ----------------------------------------------------------------------- */
    public double getProduction()          { return production; }
    public double getBaseProduction()      { return baseProduction; }
    public double getConsumption()         { return consumption; }
    public double getAverageUtilityFill()  { return averageUtilityFill; }

    public double getWaterProduction()     { return waterProduction; }
    public double getBaseWaterProduction() { return baseWaterProduction; }
    public double getWaterConsumption()    { return waterConsumption; }
    public double getBuildingWaterDraw()   { return buildingWaterDraw; }
    public double getResidentWaterDraw()   { return residentWaterDraw; }
    public double getBilledWaterDraw()     { return billedWaterDraw; }
    public double getUnbilledWaterDraw()   { return waterConsumption - billedWaterDraw; }
    public double getWaterRatio()          { return waterRatio; }

    /** The fresh water limit this month: FRESH_UNITS_PER_KM2 x the owned lakes and river, plus the rights (0.7.59); infinite when no land was given. */
    public double getFreshCap()            { return freshCap; }
    /** The Water Treatment Plants' nameplate, standing. */
    public double getFreshNameplate()      { return freshNameplate; }
    /** The fresh water treated this month: the fresh plants at their staffing, held to the cap. */
    public double getFreshDrawn()          { return freshDrawn; }
    /** The Desalination Plants' nameplate, standing. */
    public double getDesalNameplate()      { return desalNameplate; }
    /** The sea desalinated this month: their nameplate at the staffing. */
    public double getDesalOutput()         { return desalOutput; }

    /** Whether the fresh water limit holds the plants back this month: what they would treat at their staffing is more than the cap. */
    public boolean isFreshCapped() {
        return freshNameplate * averageUtilityFill > freshCap;
    }

    /** The share of what the fresh plants would treat at their staffing that the limit idles, 0 to 1: the Services page's "62% of the plants' nameplate idle". */
    public double getFreshIdleShare() {
        double could = freshNameplate * averageUtilityFill;
        return could > 0 ? Math.max(0, 1 - freshDrawn / could) : 0;
    }

    /** The fresh water a plant more would add at the staffing: what is left under the cap, never below nothing. */
    public double getFreshHeadroom() {
        return Math.max(0, freshCap - freshNameplate * averageUtilityFill);
    }

    /** What the works would produce fully staffed, held to the fresh water limit: the Services page's "at full staff" (0.7.59; baseWaterProduction until then). */
    public double getWaterAtFullStaff() {
        return freshNameplate > freshCap ? baseWater() + desalNameplate + freshCap : baseWaterProduction;
    }

    /**
     * The water the works produce at a staffing, by the rule above, from
     * its parts: the nameplate and the wells times the fill, unless the
     * fresh plants' share would pass the cap, when the wells and the sea are
     * times the fill and the fresh water is the cap; at no staffing at all,
     * the wells. updateWaterRatio()'s rule, written once for BuildAdvice to
     * reckon an order with and WaterCheck to assert against (the handler
     * keeps its own uncapped line, which sums the nameplate as it always
     * did).
     */
    public static double waterOutput(double fresh, double desal, double fill, double cap) {
        return waterOutput(fresh, desal, fill, cap, 1);
    }

    /** ...for a city holding that many founding endowments (FW9): the wells are one endowment's, times them. */
    public static double waterOutput(double fresh, double desal, double fill, double cap, long endowments) {
        double wells = BASE_WATER_SUPPLY * endowments;
        if (fill == 0) return wells;
        if (fresh * fill > cap) return (wells + desal) * fill + cap;
        return (fresh + desal + wells) * fill;
    }
    public double getPricePerWaterUnit()   { return pricePerWaterUnit; }

    /**
     * SERVED (0.7.41): what the grid generates over what the city asks of
     * it, unclamped - energyRatio's own fraction without its cap at 1, so
     * past 100% it keeps counting the headroom - CityNeeds.servedShare(): +∞
     * with nothing asked, 0 with an ask and nothing generating. What every
     * power gauge reads.
     */
    public double getPowerServed()         { return CityNeeds.servedShare(production, consumption); }
    /** ...and what the water works treat over what the city asks of them, waterRatio's fraction unclamped. */
    public double getWaterServed()         { return CityNeeds.servedShare(waterProduction, waterConsumption); }

    /* -----------------------------------------------------------------------
       Split books. The two utilities share one workforce and one fill rate, but
       the report shows them as separate businesses that then total, so revenue
       and payroll have to be attributable to one side or the other.
       ----------------------------------------------------------------------- */

    /**
     * The month's electricity bill - only the draw somebody is actually invoiced
     * for, and only the fraction delivered.
     *
     * THIS USED TO READ `min(consumption, production) * pricePerWatt`, where
     * `consumption` is EVERY STANDING BUILDING. Only four categories are ever
     * charged - commercial, industrial, heavy industry and mining - so houses,
     * the construction depot, the materials plant, the coal plant, the 900W
     * water treatment plant and the road network all drew power that the utility
     * booked as revenue and nobody paid. Measured on a small city: 73% of the
     * draw was unbilled, and the revenue went to the treasury through
     * getServiceNetIncome(). Money from nowhere, scaling with the housing stock.
     *
     * It is the same bug water was already fixed for, and the fix is a copy of
     * that one: bill the billed draw, and apply the ratio on BOTH sides so a
     * brownout charges customers for what they received rather than what they
     * asked for. The comment in EconomyManager.setElectricityConsumption() has
     * said "water is billed to the sectors that draw it, exactly as power is"
     * for months. It was not.
     */
    public double getElectricityRevenue() {
        if (billedByStatement) return billedElectricityRevenue;
        return Math.min(billedElectricityDraw * energyRatio, production) * getPricePerWatt();
    }

    /*
     * WHAT THE CUSTOMERS WERE ACTUALLY CHARGED, since 2026-09-06.
     *
     * The two formulas here and in getWaterRevenue() recompute the bill from
     * the draw, the ratio and the price - and they were struck at the END of
     * the month, after updateServices() had moved the ratio, while the four
     * sectors were charged at the START of the month against last month's
     * ratio. Whenever a brownout began or ended, the utility booked a
     * different number from the one its customers paid. ConservationCheck
     * did not see it because its fixture's ratios never move; MoneyAudit saw
     * it on the first run. The customers' statements are the fact, so Game
     * hands their total in and the utility books that.
     */
    private boolean billedByStatement;
    private double billedElectricityRevenue;
    private double billedWaterRevenue;

    public void setBilledRevenue(double electricity, double water) {
        this.billedByStatement = true;
        this.billedElectricityRevenue = electricity;
        this.billedWaterRevenue = water;
    }

    /** ...and what the works sold it of their own power, as their statements booked it (0.7.112; THE POWER THE WORKS MAKE, below). */
    public void setBilledRevenue(double electricity, double water, double powerBought) {
        setBilledRevenue(electricity, water);
        this.billedPowerBought = Double.isFinite(powerBought) ? Math.max(0, powerBought) : 0;
    }

    /* =====================================================================
       THE POWER THE WORKS MAKE (0.7.112, batch I2;
       docs/studies/spec-materials.md 2.8, the study's 1.6)

       A works may make its own power (BuildingsTemplate.getGen()): it draws
       from the grid only what it needs past that, so the grid's consumption
       is every building's net draw (ServicesManager), and what it makes past
       its own draw it sells the grid (setWorksPower()). That surplus is added
       to what the grid generates - unscaled by the utility's staffing, since
       the works' own crews made it - and the utility pays the works for it at
       the tariff, out of its takings: the electricity income is the bills
       less the payroll less the power bought (getPowerBought()), which the
       works' statements booked (Sector.Statement.powerSold), handed in with
       the bills (setBilledRevenue()). No building made power until 0.7.123
       (batch I4: the Coke Ovens, the Blast Furnace and the Oxygen Steel
       Shop); while none of them stands, the grid and the utility's books are
       what they were, to the bit.
       ===================================================================== */

    /** kW the works make past their own draws, standing: added to the grid's generation. Set by ServicesManager. */
    private double worksPower;

    /** What the works' statements booked as power sold to the grid this month: what the utility pays them. */
    private double billedPowerBought;

    /** The surplus the works make past their own draws, kW (ServicesManager, from the buildings standing). */
    public void setWorksPower(double kw) {
        this.worksPower = Double.isFinite(kw) ? Math.max(0, kw) : 0;
    }

    /** kW the works sell the grid: added to what it generates. */
    public double getWorksPower() { return worksPower; }

    /** What the utility pays the works for their power this month: the statements' figure, or with none handed in the surplus at the tariff. */
    public double getPowerBought() {
        if (billedByStatement) return billedPowerBought;
        return worksPower > 0 ? worksPower * getWorksPricePerWatt() : 0;
    }

    /** What the four charged categories draw. Set by ServicesManager. */
    public double billedElectricityDraw;

    public void setBilledElectricityDraw(double draw) {
        this.billedElectricityDraw = Math.max(0, draw);
    }
    public double getBilledElectricityDraw()   { return billedElectricityDraw; }
    public double getUnbilledElectricityDraw() {
        return Math.max(0, consumption - billedElectricityDraw);
    }

    /**
     * What the homes draw, power and water (0.7.28): the residential
     * buildings' own draw, set beside the billed draws by ServicesManager,
     * so the Services screen can say who the unbilled draw is - the homes,
     * or the city's own buildings (in the 2,400-month playtest city the
     * water plants alone draw more power than every home). A read; nothing
     * is billed by it, and it is not saved - the month recomputes it from
     * the standing stock, as it does the billed draws.
     */
    private double homesElectricityDraw, homesWaterDraw;

    public void setHomesDraw(double electricity, double water) {
        this.homesElectricityDraw = Math.max(0, electricity);
        this.homesWaterDraw = Math.max(0, water);
    }
    public double getHomesElectricityDraw() { return homesElectricityDraw; }
    public double getHomesWaterDraw()       { return homesWaterDraw; }

    /**
     * Only the billed slice, and only the fraction actually delivered - during
     * rationing customers receive waterRatio of what they asked for and are
     * charged for that, which is also exactly what the commercial and industrial
     * handlers book as their water expense. The two sides tie out.
     */
    public double getWaterRevenue() {
        if (billedByStatement) return billedWaterRevenue;
        return billedWaterDraw * waterRatio * pricePerWaterUnit;
    }

    /*
     * PER JOB TYPE since 0.7.17, as every employer's payroll is: each type's
     * wage for the posts of that type filled, which is what the households in
     * them are paid. It was the wage bill times the utilities' AVERAGE fill -
     * see Sector.getPayroll() for what that charged and who it paid. The
     * average stays where it belongs, on what the plants produce
     * (updateUtilitiesHandler()).
     */
    public double getElectricityPayroll() {
        return staffed(electricityWages);
    }

    public double getWaterPayroll() {
        return staffed(waterWages);
    }

    /** A wage bill by job type, at each type's own fill. */
    private double staffed(double[] bill) {
        double total = 0;
        if (bill == null) return 0;
        for (int i = 0; i < bill.length && i < fillRate.length; i++) total += bill[i] * fillRate[i];
        return total;
    }

    public double getElectricityIncome() {
        double income = getElectricityRevenue() - getElectricityPayroll();
        // ...less the power it bought from the works (0.7.112): taken only when there is some, so a month without is struck as it was.
        double bought = getPowerBought();
        if (bought != 0) income -= bought;
        // ...and the fuel and the upkeep its plants burned abroad and at home (PW1), taken only when there is some, so a month without is struck as it was.
        double fuelBill = getFuelBill();
        return fuelBill != 0 ? income - fuelBill : income;
    }

    public double getWaterIncome() {
        return getWaterRevenue() - getWaterPayroll();
    }

    private static double sum(double[] a) {
        double total = 0;
        if (a != null) for (double v : a) total += v;
        return total;
    }

    public double getUtilityPayroll() {
        return getElectricityPayroll() + getWaterPayroll();
    }

    public double getUtilityRevenue() {
        return getElectricityRevenue() + getWaterRevenue();
    }

    /* =====================================================================
       THE MERIT ORDER (PW1, 2026-11-11; spec-electricity 2.2, 2.6; Jerus's Q1: "Market price")

       The month's power is dispatched (PowerDispatch) rather than summed: the works' surplus
       first, the wind at its weather, the hydro stations levelling the load with their water,
       the fuelled plants by marginal cost, and each half-block of the month clears at the last
       plant it ran. What the month cost to make is the price the customers pay.

       THE STRUCK BOOK is that price, kept. updateEnergyRatio() dispatches twice a month and its
       price moves with the fleet and the fuel; the sectors are billed at the START of the month,
       against a price that must not move under them, and the utility pays the works at the END at
       the price their statements were booked at. So the month's clearing is struck once
       (strikePower()) into a Book, and the book is what every bill, the advice and the screens
       read until the next strike: the commerce price (the energy and all the wires), the industry
       price (the energy and the long wires alone: Heavy Industry and Mining take power at high
       voltage, spec 2.6), the price of a kW of the works' surplus, and the month's kWh by kind
       and fuel by good, for the fuel to be drawn from the market (setFuelPaid(), a later slice).
       Until a month is struck the book holds the founding tariff, "still at founding: no month
       has cleared".

       Nothing calls strikePower(), seedConstants() or setFuelPaid() yet; the money constants
       are re-struck by Game.restrikeMoneyConstants() in the slice that wires them.
       ===================================================================== */

    /** The founding tariff a kW a month, D$10 at the founding's level, held by the struck book until a month has cleared (the tariff the game had before PW1). */
    public static final double FOUNDING_TARIFF = .01 * PriceLevel.FACTOR;

    private static final String WORKS_ROW = "works", GRID_ROW = "grid";

    /** What a short half-block clears at, and what the long and the street wires charge a kWh delivered: city money, struck by seedConstants(). */
    private double voll = PowerDispatch.VOLL_A_KWH, transmission = PowerDispatch.TRANSMISSION_A_KWH, distribution = PowerDispatch.DISTRIBUTION_A_KWH;

    /** The power plants standing and how many of each (ServicesManager); null until handed in. */
    private Map<BuildingsTemplate, Integer> plants;

    /** Where the month's fuel price, the exchange rate and the calendar month come from; null for a bare fixture (the world's price, at par, no season). */
    private ToDoubleFunction<Good> fuelPriceSource;
    private DoubleSupplier exchangeRateSource;
    private IntSupplier calendarMonth;

    /** The month last dispatched, and the rows it ran. */
    private PowerDispatch.Month latest = new PowerDispatch.Month();
    private List<PowerDispatch.Row> latestRows = new ArrayList<>();

    /** The month last struck: the founding tariff until one is. */
    private Book struck = Book.founding();

    /** The fuel the plants burned and the upkeep they paid, in city money: at home, abroad, and the upkeep (set by setFuelPaid()). */
    private double fuelLocal, fuelImported, upkeepPaid;

    /** The power plants standing and how many of each. */
    public void setPlants(Map<BuildingsTemplate, Integer> standing) {
        this.plants = standing == null ? null : new LinkedHashMap<>(standing);
    }

    /** The city's price of a unit of a fuel; null: the world's price (which already carries PriceLevel.FACTOR). */
    public void setFuelPrice(ToDoubleFunction<Good> cityPrice) { this.fuelPriceSource = cityPrice; }

    /** City money per world thousand of the founding's dollars (Markets.getExchangeRate(): city money a dollar times the world's price level, the D$ the goods are struck at); null: 1, at par. */
    public void setExchangeRate(DoubleSupplier rate) { this.exchangeRateSource = rate; }

    /** The calendar month, 1 to 12, for the season; null: no season. */
    public void setCalendarMonth(IntSupplier month) { this.calendarMonth = month; }

    private double fuelPrice(Good g) {
        return fuelPriceSource != null ? fuelPriceSource.applyAsDouble(g) : g.worldImportPrice();
    }

    private double exchangeRate() {
        return exchangeRateSource != null ? exchangeRateSource.getAsDouble() : 1;
    }

    /** Re-seeds the money constants at a given unit: the dial over the unit, as Bank.seedConstants() does (the later slice calls it from Game.restrikeMoneyConstants()). */
    public void seedConstants(double unit) {
        if (!(unit > 0) || !Double.isFinite(unit)) return;
        voll         = PowerDispatch.VOLL_A_KWH / unit;
        transmission = PowerDispatch.TRANSMISSION_A_KWH / unit;
        distribution = PowerDispatch.DISTRIBUTION_A_KWH / unit;
    }

    public PowerDispatch.Month getLatestMonth() { return latest; }

    public List<PowerDispatch.Row> getLatestRows() { return latestRows; }

    /** A month's price and what it was made of: what the bills and the screens read. Public fields, for the save. */
    public static final class Book {
        /** The month's ratio served, and its energy price a kWh delivered. */
        public double ratio, energyPrice;
        /** City money a kW a month: commerce and light industry (the energy and all the wires), Heavy Industry and Mining (the energy and transmission), and the works' surplus. */
        public double commercePrice, industryPrice, worksPrice;
        /** Per half-block: the price a kWh, the load, and what was left unserved, kW. */
        public double[] price = new double[PowerDispatch.HALVES], load = new double[PowerDispatch.HALVES], unserved = new double[PowerDispatch.HALVES];
        /** The month's kWh asked, served and curtailed, and made by the fuelled plants, the wind, the hydro, the works and the founding grid. */
        public double askedKwh, servedKwh, curtailedKwh, fuelledKwh, windKwh, hydroKwh, worksKwh, gridKwh;
        /** The variable upkeep the plants ran up, thousands of the WORLD's money. */
        public double upkeepWorld;
        /** Fuel burned: good name to units. */
        public LinkedHashMap<String, Double> fuel = new LinkedHashMap<>();
        /** True once a month has been struck into this book; false in the founding book, for the screens to say "still at founding". */
        public boolean cleared;

        /** The book before any month has cleared: the founding tariff, ratio 1, nothing made. */
        public static Book founding() {
            Book b = new Book();
            b.ratio = 1;
            b.commercePrice = b.industryPrice = b.worksPrice = FOUNDING_TARIFF;
            return b;
        }

        Book copy() {
            Book b = new Book();
            b.ratio = ratio; b.energyPrice = energyPrice;
            b.commercePrice = commercePrice; b.industryPrice = industryPrice; b.worksPrice = worksPrice;
            b.price = price.clone(); b.load = load.clone(); b.unserved = unserved.clone();
            b.askedKwh = askedKwh; b.servedKwh = servedKwh; b.curtailedKwh = curtailedKwh;
            b.fuelledKwh = fuelledKwh; b.windKwh = windKwh; b.hydroKwh = hydroKwh; b.worksKwh = worksKwh; b.gridKwh = gridKwh;
            b.upkeepWorld = upkeepWorld;
            b.fuel = new LinkedHashMap<>(fuel);
            b.cleared = cleared;
            return b;
        }
    }

    private static void scale(Book b, double scale) {
        b.energyPrice *= scale; b.commercePrice *= scale; b.industryPrice *= scale; b.worksPrice *= scale;
        for (int s = 0; s < b.price.length; s++) b.price[s] *= scale;
    }

    /** Strikes the month last dispatched into the book: a new object each time, sharing nothing with the dispatch. */
    public void strikePower() {
        PowerDispatch.Month m = latest;
        Book b = new Book();
        b.ratio = m.ratio;
        b.energyPrice = m.energyPrice;
        // Commerce and light industry pay the energy and all the wires; Heavy Industry and Mining take power at high voltage and pay the energy and the long wires alone (spec 2.6).
        b.commercePrice = (m.energyPrice + transmission + distribution) * PowerDispatch.MONTH_HOURS;
        b.industryPrice = (m.energyPrice + transmission) * PowerDispatch.MONTH_HOURS;
        // The works are paid what their kWh cleared at; with none standing, the generation price (the energy less the losses it carries).
        PowerDispatch.Row works = null;
        for (PowerDispatch.Row r : latestRows) if (r.kind == PowerDispatch.Kind.MUST_RUN && WORKS_ROW.equals(r.name)) works = r;
        b.worksPrice = works != null && works.kw > 0 ? works.earned / works.kw
                : m.energyPrice * (1 - PowerDispatch.GRID_LOSSES) * PowerDispatch.MONTH_HOURS;
        b.price = m.price.clone(); b.load = m.load.clone(); b.unserved = m.unserved.clone();
        b.askedKwh = m.askedKwh; b.servedKwh = m.servedKwh; b.curtailedKwh = m.curtailedKwh;
        for (PowerDispatch.Row r : latestRows) {
            switch (r.kind) {
                case MUST_RUN -> b.worksKwh += r.kwh;
                case WIND -> b.windKwh += r.kwh;
                case HYDRO -> b.hydroKwh += r.kwh;
                case FUELLED -> { if (GRID_ROW.equals(r.name)) b.gridKwh += r.kwh; else b.fuelledKwh += r.kwh; }
            }
            if (r.fuel != null && r.fuelUnits > 0) b.fuel.merge(r.fuel.name(), r.fuelUnits, Double::sum);
            b.upkeepWorld += r.kwh * r.upkeepPerKwh;
        }
        b.cleared = true;
        struck = b;
    }

    public Book getStruckBook() { return struck; }

    /** True while no month has been struck: the prices are the founding tariff (the book's own flag, so a load keeps it). */
    public boolean isFoundingBook() { return !struck.cleared; }

    /** What the month's energy cleared at, city money (thousands) a MWh delivered. */
    public double getEnergyPricePerMwh() { return struck.energyPrice * 1000; }

    /** Units of a good the plants burned in the month last struck (tonnes, for coal). */
    public double getFuelBurned(Good g) { return struck.fuel.getOrDefault(g.name(), 0.0); }

    /** kWh of the wind and the works' surplus turned away in the month last struck, for want of a load. */
    public double getCurtailedKwh() { return struck.curtailedKwh; }

    /** kWh of the load left unserved in the month last struck, summed over its six half-blocks. */
    public double getUnservedKwh() {
        double kwh = 0;
        for (int s = 0; s < struck.unserved.length; s++) kwh += struck.unserved[s] * PowerDispatch.halfHours(s);
        return kwh;
    }

    /** How many of the month's six half-blocks left some load unserved. */
    public int getHalfBlocksShort() {
        int n = 0;
        for (double u : struck.unserved) if (u > 0) n++;
        return n;
    }

    /** What commerce and light industry pay a kW a month (the struck commerce price). */
    public double getPricePerWatt() { return struck.commercePrice; }

    /** What Heavy Industry and Mining pay a kW a month (the energy and the long wires). */
    public double getIndustryPricePerWatt() { return struck.industryPrice; }

    /** What the utility pays the works a kW of surplus a month. */
    public double getWorksPricePerWatt() { return struck.worksPrice; }

    /**
     * The fuel the plants burned this month and the upkeep they ran up, in city money: at home, abroad, and the upkeep (a later
     * slice draws them from the market at the top of the month). The utility's income is taken by their sum.
     */
    public void setFuelPaid(double local, double imported, double upkeep) {
        this.fuelLocal = Double.isFinite(local) ? local : 0;
        this.fuelImported = Double.isFinite(imported) ? imported : 0;
        this.upkeepPaid = Double.isFinite(upkeep) ? upkeep : 0;
    }

    public double getFuelLocal() { return fuelLocal; }
    public double getFuelImported() { return fuelImported; }
    public double getUpkeepPaid() { return upkeepPaid; }
    public double getFuelBill() { return fuelLocal + fuelImported + upkeepPaid; }

    /** What the save keeps of the merit order: the struck book and the fuel paid (a later slice wires it to DataSave). */
    public static final class PowerState {
        public Book struck;
        public double fuelLocal, fuelImported, upkeepPaid;
    }

    public PowerState toPowerState() {
        PowerState s = new PowerState();
        s.struck = struck.copy();
        s.fuelLocal = fuelLocal; s.fuelImported = fuelImported; s.upkeepPaid = upkeepPaid;
        return s;
    }

    /** Restores a save's book and fuel paid; a save with none (null, or no book) leaves the founding book. */
    public void restorePowerState(PowerState s) {
        struck = s == null || s.struck == null ? Book.founding() : s.struck.copy();
        if (s == null) { fuelLocal = fuelImported = upkeepPaid = 0; return; }
        setFuelPaid(s.fuelLocal, s.fuelImported, s.upkeepPaid);
    }

    //setters
    public void setWattsProduction(double watts) {
        this.baseProduction = watts + baseGrid();
    }

    public void setWattsConsumption(double watts) {
        this.consumption = watts;

    }

    public void setWaterProduction(double water) {
        this.baseWaterProduction = water + baseWater();
    }

    /** The two parts of that nameplate (0.7.59): the fresh plants' and the desalination plants'. Set beside it by ServicesManager. */
    public void setWaterSources(double fresh, double desal) {
        this.freshNameplate = Math.max(0, fresh);
        this.desalNameplate = Math.max(0, desal);
    }

    /** The fresh water limit, from the city's land and rights (Game.getFreshCap()); infinite for none. */
    public void setFreshCap(double cap) {
        this.freshCap = Double.isNaN(cap) ? Double.POSITIVE_INFINITY : Math.max(0, cap);
    }

    /** Summed draw of every building standing, from the templates. */
    public void setBuildingWaterDraw(double water) {
        this.buildingWaterDraw = water;
    }

    /** The commercial + industrial slice, i.e. the part with a paying customer. */
    public void setBilledWaterDraw(double water) {
        this.billedWaterDraw = water;
    }

    /**
     * The people. Kept separate from the building draw so the report can show
     * which of the two is actually eating the supply - that is the difference
     * between "stop building housing" and "stop building food plants".
     */
    public void setPopulation(long population) {
        this.residentWaterDraw = population * WATER_PER_PERSON;
    }

    public void setPricePerWaterUnit(double price) {
        this.pricePerWaterUnit = price;
    }

    //passers
    //calculators
    /**
     * The month dispatched (PW1, THE MERIT ORDER): the rows the city's plants make, run against the grid's draw by PowerDispatch;
     * energyRatio is the energy served over the energy asked, and production the power offered, as the screens have read it.
     */
    public void updateEnergyRatio() {
        double fill = averageUtilityFill;
        production = baseProduction * fill;
        if(fill == 0) production = baseGrid();
        // ...and what the works sell the grid, whatever the utility's staffing (0.7.112, THE POWER THE WORKS MAKE).
        if (worksPower > 0) production += worksPower;

        List<PowerDispatch.Row> rows = new ArrayList<>();
        if (worksPower > 0) rows.add(PowerDispatch.Row.mustRun(WORKS_ROW, worksPower));
        // ...the grid the city was founded with: free, as it has always been, and at the founding's size whatever the staffing.
        rows.add(PowerDispatch.Row.fuelled(GRID_ROW, baseGrid() * (fill > 0 ? fill : 1), 0, null, 0, 0));
        if (plants == null) {
            // A handler nobody has told of its plants (a bare fixture): what setWattsProduction() was given, free to run, as it was.
            double watts = (baseProduction - baseGrid()) * fill;
            if (watts > 0) rows.add(PowerDispatch.Row.fuelled("plants", watts, 0, null, 0, 0));
        } else {
            for (Map.Entry<BuildingsTemplate, Integer> e : plants.entrySet()) {
                BuildingsTemplate t = e.getKey();
                int n = e.getValue() == null ? 0 : e.getValue();
                if (n <= 0) continue;
                // The upkeep is US$ a MWh at today's level (buildings.json): over 1e6 thousands a kWh, times FACTOR the founding's dollars, as
                // a good's world price is struck; the exchange rate (the world's price level in it) then makes it the month's city money, as it
                // does the fuel's. PW1: read as today's dollars it was 38.6 times too dear, nine-tenths of a coal plant's marginal.
                double kw = t.getProduction1() * n * fill, upkeep = t.getVariableUpkeep() / 1e6 * PriceLevel.FACTOR, perKwh = t.fuelPerKwh();
                if (t.isHydro()) {
                    // A station's month is its water (its production1 over the month), levelled under its nameplate and its staffing.
                    double average = t.getProduction1() * n * (fill > 0 ? 1 : 0);
                    rows.add(PowerDispatch.Row.hydro(t.getName(), t.getNameplate() * n * fill, average * PowerDispatch.MONTH_HOURS, upkeep));
                    production += average - kw;
                } else if (t.isWind()) {
                    rows.add(PowerDispatch.Row.wind(t.getName(), kw));
                } else if (perKwh > 0) {
                    rows.add(PowerDispatch.Row.fuelled(t.getName(), kw, fuelPrice(t.getFuel()) * perKwh + upkeep * exchangeRate(),
                            t.getFuel(), perKwh, upkeep));
                } else {
                    rows.add(PowerDispatch.Row.fuelled(t.getName(), kw, 0, null, 0, 0));
                }
            }
        }
        latest = PowerDispatch.dispatch(rows, consumption, PowerDispatch.season(calendarMonth == null ? 0 : calendarMonth.getAsInt()), voll);
        latestRows = rows;
        energyRatio = latest.ratio;
    }

    public void updateWaterRatio() {

        waterConsumption = buildingWaterDraw + residentWaterDraw;

        waterProduction = baseWaterProduction * averageUtilityFill;
        freshDrawn = freshNameplate * averageUtilityFill;
        desalOutput = desalNameplate * averageUtilityFill;

        // ...unless the fresh plants would treat more than the city's fresh
        // water yields: then they treat the cap and the rest idles (0.7.59,
        // THE FRESH WATER LIMIT). Not binding, the line above is the old
        // formula to the bit.
        if (freshDrawn > freshCap) {
            freshDrawn = freshCap;
            waterProduction = (baseWater() + desalNameplate) * averageUtilityFill + freshCap;
        }

        // The legacy wells keep running with nobody on shift, same as the base grid.
        if (averageUtilityFill == 0) {
            waterProduction = baseWater();
            freshDrawn = 0;
            desalOutput = 0;
        }

        // Nothing consumes water yet, so this would be 0/0 -> NaN, and a NaN
        // ratio silently poisons everything downstream that multiplies by it
        // the moment consumption is wired up. Guard on demand instead.
        waterRatio = (waterConsumption > 0)
                ? Math.min(waterProduction / waterConsumption, 1)
                : 1;
    }

    /**
     * Both utilities consolidated. Water now contributes revenue and payroll
     * here, so this is no longer electricity alone.
     */
    public double getUtilityIncome() {
        return getElectricityIncome() + getWaterIncome();
    }

    /**
     * NOTE: this now takes the electricity and water job arrays separately
     * rather than one combined array. The fill rate and the total payroll are
     * unchanged - they are computed off the sum - but the report needs to
     * attribute payroll to one utility or the other, and that is impossible to
     * recover once the arrays have been added together.
     */
    public void updateUtilitiyWages(double[] wages, long[] electricityJobs, long[] waterJobs) {

        if (wages == null || electricityJobs == null || waterJobs == null || utilityWages == null) {
            System.out.println("null stores");
            System.out.println(wages + " " + electricityJobs + " " + waterJobs + " " + utilityWages);
            return; // nothing to update print error

        }

        int length = Math.min(
                Math.min(wages.length, utilityWages.length),
                Math.min(electricityJobs.length, waterJobs.length));

        long[] jobs = new long[utilityJobs.length];
        for (int i = 0; i < length; i++) {
            jobs[i] = electricityJobs[i] + waterJobs[i];
        }
        utilityJobs = jobs;

        for (int i = 0; i < length; i++) {
            utilityWages[i] = 0;
            electricityWages[i] = 0;
            waterWages[i] = 0;
        }
        for (int i = 0; i < length; i++) {
            electricityWages[i] += wages[i] * electricityJobs[i];
            waterWages[i]       += wages[i] * waterJobs[i];
            utilityWages[i]     += wages[i] * jobs[i];

        }
        double totalFilled = 0;
        long totalJobsUtility = 0;

        if (fillRate != null) {

            for (int i = 0; i < jobs.length; i++) {
                totalFilled += fillRate[i] * jobs[i];// filled positions
                totalJobsUtility += jobs[i];
            }

            if (totalJobsUtility == 0) {
                averageUtilityFill = 1;  // no jobs means fully filled by default
            }

            if (totalJobsUtility != 0) {
                averageUtilityFill = totalFilled / totalJobsUtility;

            }
        } else {
            System.out.println("fillRate is null");
        }
    }

    public void updateJobFillRate(double[] fillRate) {

        System.arraycopy(fillRate, 0, this.fillRate, 0, fillRate.length);
    }

    //printers
    public void printUtilitiesInfo() {

        System.out.println("\n====================== MUNICIPAL UTILITIES REPORT ======================");

        /* -------------------------------------------------------------------
       ELECTRIC POWER
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ ELECTRIC POWER GENERATION AUTHORITY ------------------");

        /* 1. Grid Status */
        System.out.println("\nGRID STATUS");
        System.out.printf("Grid Satisfaction:        %.1f%%%n", energyRatio * 100);
        System.out.printf("System Stability:         %s%n", (energyRatio >= 1.0 ? "STABLE" : "BROWNOUT"));

        /* 2. Energy Load Analysis */
        System.out.println("\nENERGY LOAD ANALYSIS");
        System.out.printf("Total Grid Consumption:   %s kW%n", formatter.format(consumption));
        System.out.printf("Maximum Generation:       %s kW%n", formatter.format(baseProduction));
        System.out.printf("Current Power Output:     %s kW%n", formatter.format(production));

        double elecRev = getElectricityRevenue();
        double elecPay = getElectricityPayroll();

        System.out.println("\nINCOME STATEMENT (ELECTRIC POWER)");
        System.out.printf("  Electricity Sales:              %s%n", Currency.writing().write(formatter.format(elecRev)));
        System.out.printf("  Payroll Expense:                -%s%n", Currency.writing().write(formatter.format(elecPay)));
        System.out.printf("  NET INCOME (ELECTRIC):          %s%n", Currency.writing().write(formatter.format(elecRev - elecPay)));

        if (energyRatio < 1.0) {
            System.out.println("\n[CRITICAL] ELECTRICAL GRID SHORTAGE");
            System.out.printf("Additional Capacity Needed:       %s kW%n",
                    formatter.format(consumption - production));
            System.out.println("Industrial and Commercial efficiency may be reduced.");
        }

        /* -------------------------------------------------------------------
       WATER
       ------------------------------------------------------------------- */
        System.out.println("\n----------------------- MUNICIPAL WATER AUTHORITY -----------------------");

        System.out.println("\nSUPPLY STATUS");
        System.out.printf("Supply Satisfaction:      %.1f%%%n", waterRatio * 100);
        System.out.printf("System Status:            %s%n", (waterRatio >= 1.0 ? "ADEQUATE" : "RATIONING"));

        System.out.println("\nWATER LOAD ANALYSIS");
        System.out.printf("Resident Draw:            %s units%n", formatter.format(residentWaterDraw));
        System.out.printf("Building Draw:            %s units%n", formatter.format(buildingWaterDraw));
        System.out.printf("Total Draw:               %s units%n", formatter.format(waterConsumption));
        System.out.printf("  of which billed:        %s units%n", formatter.format(billedWaterDraw));
        System.out.printf("  of which unbilled:      %s units%n", formatter.format(getUnbilledWaterDraw()));
        System.out.printf("Maximum Capacity:         %s units%n", formatter.format(baseWaterProduction));
        if (isFreshCapped()) {
            System.out.printf("Fresh Water Limit:        %s units (%.0f%% of the plants' nameplate idle)%n",
                    formatter.format(freshCap), getFreshIdleShare() * 100);
        }
        System.out.printf("Current Output:           %s units%n", formatter.format(waterProduction));
        System.out.printf("Price per Unit:           %s%n", Currency.writing().write(formatter.format(pricePerWaterUnit)));

        double waterRev = getWaterRevenue();
        double waterPay = getWaterPayroll();

        System.out.println("\nINCOME STATEMENT (WATER)");
        System.out.println("  (Only commercial and industrial draw is invoiced -");
        System.out.println("   households have no cash, so resident water is unbilled.)");
        System.out.printf("  Water Sales:                    %s%n", Currency.writing().write(formatter.format(waterRev)));
        System.out.printf("  Payroll Expense:                -%s%n", Currency.writing().write(formatter.format(waterPay)));
        System.out.printf("  NET INCOME (WATER):             %s%n", Currency.writing().write(formatter.format(waterRev - waterPay)));

        if (waterRatio < 1.0) {
            System.out.println("\n[CRITICAL] WATER SUPPLY SHORTAGE");
            System.out.printf("Additional Capacity Needed:       %s units%n",
                    formatter.format(waterConsumption - waterProduction));
            System.out.println("Industrial and Commercial output is reduced.");
        }

        /* -------------------------------------------------------------------
       CONSOLIDATED
       ------------------------------------------------------------------- */
        System.out.println("\n-------------------------- RESOURCE UTILIZATION -------------------------");
        System.out.printf("Labor Fill Rate:          %.1f%%%n", averageUtilityFill * 100);
        System.out.println("(One workforce runs both utilities, so this fill rate gates them together.)");

        double totalRev = elecRev + waterRev;
        double totalPay = elecPay + waterPay;
        double netIncome = totalRev - totalPay;

        System.out.println("\n------------------ CONSOLIDATED (ALL UTILITIES) ------------------------");
        System.out.printf("Total Revenue:                    %s%n", Currency.writing().write(formatter.format(totalRev)));
        System.out.printf("Total Operating Expenses:         -%s%n", Currency.writing().write(formatter.format(totalPay)));
        System.out.printf("NET INCOME (UTILITIES):           %s%n", Currency.writing().write(formatter.format(netIncome)));

        /* Tax Summary */
        double taxIncome = netIncome; //* pTaxRate;

        System.out.println("\n----------------------------- TAX SUMMARY -----------------------------");
        System.out.printf("Utility Net Income:               %s%n", Currency.writing().write(formatter.format(netIncome)));
        System.out.printf("Government Tax Revenue:           %s%n", Currency.writing().write(formatter.format(taxIncome)));

        System.out.println("=======================================================================\n");
    }

    //miscelanous
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }


    /** The utilities' prices and this month's bills, in the new unit. */
    public void redenominate(double scale) {
        voll         *= scale;
        transmission *= scale;
        distribution *= scale;
        scale(struck, scale);
        // ...and the month last dispatched, which strikePower() would read in the old unit.
        latest.energyPrice *= scale;
        for (int s = 0; s < latest.price.length; s++) latest.price[s] *= scale;
        for (PowerDispatch.Row r : latestRows) r.earned *= scale;
        fuelLocal    *= scale;
        fuelImported *= scale;
        upkeepPaid   *= scale;
        pricePerWaterUnit *= scale;
        billedElectricityRevenue *= scale;
        billedWaterRevenue       *= scale;
        billedPowerBought        *= scale;
        for (int i = 0; i < utilityWages.length; i++)     utilityWages[i]     *= scale;
        for (int i = 0; i < electricityWages.length; i++) electricityWages[i] *= scale;
        for (int i = 0; i < waterWages.length; i++)       waterWages[i]       *= scale;
    }

}
