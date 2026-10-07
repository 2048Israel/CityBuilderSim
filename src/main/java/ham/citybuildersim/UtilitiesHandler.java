/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

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
     */
    private double pricePerWaterUnit = .05;

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

    //temporary
    private double pricePerWatt = .01;

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

    public double getPricerPerWatt() {
        return pricePerWatt;
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
        return freshNameplate > freshCap ? BASE_WATER_SUPPLY + desalNameplate + freshCap : baseWaterProduction;
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
        if (fill == 0) return BASE_WATER_SUPPLY;
        if (fresh * fill > cap) return (BASE_WATER_SUPPLY + desal) * fill + cap;
        return (fresh + desal + BASE_WATER_SUPPLY) * fill;
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
        return Math.min(billedElectricityDraw * energyRatio, production) * pricePerWatt;
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
        return getElectricityRevenue() - getElectricityPayroll();
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

    //setters
    public void setWattsProduction(double watts) {
        this.baseProduction = watts + 10000;
    }

    public void setWattsConsumption(double watts) {
        this.consumption = watts;

    }

    public void setWaterProduction(double water) {
        this.baseWaterProduction = water + BASE_WATER_SUPPLY;
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
    public void updateEnergyRatio() {
        production = baseProduction * averageUtilityFill;
        if(averageUtilityFill == 0) production = 10000;
        energyRatio = Math.min(production / consumption, 1);
        
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
            waterProduction = (BASE_WATER_SUPPLY + desalNameplate) * averageUtilityFill + freshCap;
        }

        // The legacy wells keep running with nobody on shift, same as the base grid.
        if (averageUtilityFill == 0) {
            waterProduction = BASE_WATER_SUPPLY;
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
        System.out.printf("  Electricity Sales:              $%s%n", formatter.format(elecRev));
        System.out.printf("  Payroll Expense:                -$%s%n", formatter.format(elecPay));
        System.out.printf("  NET INCOME (ELECTRIC):          $%s%n", formatter.format(elecRev - elecPay));

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
        System.out.printf("Price per Unit:           $%s%n", formatter.format(pricePerWaterUnit));

        double waterRev = getWaterRevenue();
        double waterPay = getWaterPayroll();

        System.out.println("\nINCOME STATEMENT (WATER)");
        System.out.println("  (Only commercial and industrial draw is invoiced -");
        System.out.println("   households have no cash, so resident water is unbilled.)");
        System.out.printf("  Water Sales:                    $%s%n", formatter.format(waterRev));
        System.out.printf("  Payroll Expense:                -$%s%n", formatter.format(waterPay));
        System.out.printf("  NET INCOME (WATER):             $%s%n", formatter.format(waterRev - waterPay));

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
        System.out.printf("Total Revenue:                    $%s%n", formatter.format(totalRev));
        System.out.printf("Total Operating Expenses:         -$%s%n", formatter.format(totalPay));
        System.out.printf("NET INCOME (UTILITIES):           $%s%n", formatter.format(netIncome));

        /* Tax Summary */
        double taxIncome = netIncome; //* pTaxRate;

        System.out.println("\n----------------------------- TAX SUMMARY -----------------------------");
        System.out.printf("Utility Net Income:               $%s%n", formatter.format(netIncome));
        System.out.printf("Government Tax Revenue:           $%s%n", formatter.format(taxIncome));

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
        pricePerWatt      *= scale;
        pricePerWaterUnit *= scale;
        billedElectricityRevenue *= scale;
        billedWaterRevenue       *= scale;
        for (int i = 0; i < utilityWages.length; i++)     utilityWages[i]     *= scale;
        for (int i = 0; i < electricityWages.length; i++) electricityWages[i] *= scale;
        for (int i = 0; i < waterWages.length; i++)       waterWages[i]       *= scale;
    }

}
