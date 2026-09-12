package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every month the city has ever lived, one number at a time.
 *
 * WHY THE SERIES ARE NAMED FIELDS AND NOT A MAP
 *
 * A Map<String, List<Double>> would be shorter and would let a series be added
 * without touching anything. It is not used, and the reason is on disk: this
 * class IS the file format. Gson matches JSON to fields BY NAME, so a history
 * written before a series existed still loads into this build - the missing
 * list simply stays empty - and a history written by this build still loads
 * into an older one, which ignores what it does not recognise. Moving to a map
 * would change the shape of every history file ever written and break both
 * directions at once, to save a few lines.
 *
 * WHAT A SHORT SERIES MEANS
 *
 * It means the city was ALIVE before that number was being kept, so the series
 * lines up with the END of the month axis and not the start. A save from before
 * sickness was recorded has 400 months and no sick rate; play ten more and it
 * has 410 months and ten sick rates, describing months 401-410. aligned() is
 * the only correct way to read one, and the graph goes through it - drawing a
 * short series from the left would silently place the last decade's data in the
 * founding years.
 *
 * WHAT IS NOT HERE
 *
 * Anything derivable. Unemployment is workforce and jobs, GDP per capita is GDP
 * and population, the average wage is the wage bill and the workforce. Storing
 * a derived series would double the file to hold numbers that can disagree with
 * the ones they came from, and the first time they did, nothing would say which
 * was right. Derived() computes them on the way to the screen.
 */
public class HistorySave {

    /* ------------------------------------------------------------------
       THE AXIS

       Every other list is measured against this one, and its length is the
       length of the city's life. A series shorter than this started later;
       a series LONGER than this is a bug and HistoryCheck says so.
       ------------------------------------------------------------------ */
    private List<Integer> month = new ArrayList<>();

    /* --------------------------- money --------------------------- */
    private List<Double> cash = new ArrayList<>();
    private List<Double> gdp = new ArrayList<>();
    private List<Double> debt = new ArrayList<>();
    private List<Double> interestRate = new ArrayList<>();

    /** Government revenue and what it kept, both monthly. */
    private List<Double> revenue = new ArrayList<>();
    private List<Double> surplus = new ArrayList<>();

    /* --------------------------- people --------------------------- */
    private List<Integer> jobs = new ArrayList<>();
    private List<Integer> workforce = new ArrayList<>();
    private List<Integer> population = new ArrayList<>();

    /** The four flows that move the population, and only these four move it. */
    private List<Integer> births = new ArrayList<>();
    private List<Integer> deaths = new ArrayList<>();
    private List<Integer> arrivals = new ArrayList<>();
    private List<Integer> departures = new ArrayList<>();

    /** The wage BILL, not the average - the average needs the workforce too. */
    private List<Double> totalWage = new ArrayList<>();

    /** The dial the player sets, and what the labour market did with it. */
    private List<Double> minimumWage = new ArrayList<>();

    /**
     * What an unskilled hour costs relative to its base, and how much of the
     * workforce has any training at all.
     *
     * The second is the one to watch: with no schools in the game it can only
     * rise by attracting people, so it is a direct read on whether the city is
     * pulling its weight in the wider labour market.
     */
    private List<Double> unskilledPremium = new ArrayList<>();
    private List<Double> skilledShare = new ArrayList<>();

    /** The schools: how much of the basic ladder is covered, and what it costs. */
    private List<Double> schoolCoverage = new ArrayList<>();
    private List<Double> schoolBill = new ArrayList<>();

    /* ------------------------- what throttles ------------------------- */
    private List<Double> energyRatio = new ArrayList<>();
    private List<Double> waterRatio = new ArrayList<>();
    private List<Double> roadRatio = new ArrayList<>();
    private List<Double> sickRate = new ArrayList<>();
    private List<Double> careCoverage = new ArrayList<>();

    /* --------------------------- prices --------------------------- */
    private List<Double> landPrice = new ArrayList<>();
    private List<Double> foodPrice = new ArrayList<>();
    private List<Double> materialsPrice = new ArrayList<>();
    private List<Double> orePrice = new ArrayList<>();

    /* --------------------------- the edge ---------------------------

       None of this existed when the first twenty-eight series were written,
       which is the whole reason this block is here: a city can now run a
       deficit abroad, hold somebody else's money and owe it, and none of that
       left a mark on the graph.

       THE UNITS ARE NOT ALL THE SAME, and the redenominate() at the bottom is
       where that is stated. The rate and the current account are quoted in the
       city's own money and move with a reform; the reserves and the foreign
       debt are quoted in USD and cannot, because no domestic reform reaches
       into somebody else's currency.
       ------------------------------------------------------------------ */
    private List<Double> fxRate = new ArrayList<>();
    private List<Double> reservesUsd = new ArrayList<>();
    private List<Double> foreignDebtUsd = new ArrayList<>();
    private List<Double> currentAccount = new ArrayList<>();
    private List<Double> exportsAbroad = new ArrayList<>();
    private List<Double> importsAbroad = new ArrayList<>();

    /* ------------------------ money and credit ------------------------

       The price level is the one to watch here, and it is deliberately NOT
       scaled by a reform: an index is a ratio of today's basket to the base
       year's, and dividing both by a hundred leaves it exactly where it was.
       ------------------------------------------------------------------ */
    private List<Double> priceIndex = new ArrayList<>();
    private List<Double> businessDebt = new ArrayList<>();
    private List<Double> bankPremium = new ArrayList<>();
    private List<Double> bankDeposits = new ArrayList<>();
    private List<Double> bankLent = new ArrayList<>();
    private List<Double> bankEquity = new ArrayList<>();
    private List<Double> bankWriteOffs = new ArrayList<>();

    /* ------------------- what the bank could carry, and how hard -------------------

       Added for the Bank tab. The five above say what the bank IS; these four
       say whether it is in trouble, which is a different question and the one
       every rate in the city turns on.

       STRAIN IS A RATIO and BRANCHES ARE A COUNT, so neither is scaled by a
       currency reform - see redenominate(). Capacity and profit are money and
       are.

       These start empty on a save written before they existed, which is exactly
       what aligned() pads with NaN: "we were not counting" rather than "it was
       zero". The chart draws nothing for those months, which is the truth.
       ------------------------------------------------------------------------ */
    private List<Double> bankCapacity = new ArrayList<>();
    private List<Double> bankStrain = new ArrayList<>();
    private List<Double> bankProfit = new ArrayList<>();
    private List<Double> bankBranches = new ArrayList<>();
    private List<Double> householdSavings = new ArrayList<>();

    /* --------------------------- the budget ---------------------------

       Revenue is already kept as one figure. These are its parts, kept
       separately because the interesting question about a tax take is never
       how big it is - it is which tax it came from, and a single total cannot
       answer that.
       ------------------------------------------------------------------ */
    private List<Double> taxWage = new ArrayList<>();
    private List<Double> taxProperty = new ArrayList<>();
    private List<Double> taxSales = new ArrayList<>();
    private List<Double> taxBusiness = new ArrayList<>();
    private List<Double> taxIndustrial = new ArrayList<>();
    private List<Double> contributions = new ArrayList<>();
    private List<Double> pensionBill = new ArrayList<>();
    private List<Double> healthBill = new ArrayList<>();

    /* ---------------------------- housing ----------------------------

       The price, and the two stocks it sits between. Vacancy is not kept - it
       is homes and households, and a stored third copy could disagree with
       both.
       ------------------------------------------------------------------ */
    private List<Double> rentPrice = new ArrayList<>();
    private List<Integer> homes = new ArrayList<>();
    private List<Double> households = new ArrayList<>();

    /* ------------------------- school and care ------------------------- */
    private List<Double> students = new ArrayList<>();
    private List<Double> graduates = new ArrayList<>();
    private List<Double> licences = new ArrayList<>();
    private List<Double> unburied = new ArrayList<>();

    /* ---------------------- outside the families ----------------------

       2026-09-11. The out of work on EI and past it, everybody with no home,
       the orphans, the month's evictions, and what EI, the premium, the
       grants and the student loans came to. Jerus: "the more graphs the
       juicier".
       ------------------------------------------------------------------ */
    private List<Double> outOfWorkOnEi = new ArrayList<>();
    private List<Double> outOfWorkOffEi = new ArrayList<>();
    private List<Double> unhoused = new ArrayList<>();
    private List<Double> orphans = new ArrayList<>();
    private List<Double> evicted = new ArrayList<>();
    private List<Double> eiPaid = new ArrayList<>();
    private List<Double> eiPremiums = new ArrayList<>();
    private List<Double> studentGrants = new ArrayList<>();
    private List<Double> studentLoansOwed = new ArrayList<>();

    /* ------------------------------------------------------------------
       THE LONG SICK, 2026-09-11. How many have been sick more than two
       months, how many died of it, and what share of the sick got better.
       ------------------------------------------------------------------ */
    private List<Double> sickPastTwoMonths = new ArrayList<>();
    private List<Double> diedOfIllness = new ArrayList<>();
    private List<Double> sickRecovery = new ArrayList<>();

    /* ------------------------------------------------------------------
       THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many
       households of each type the city has, now that the builder keeps what
       still fits from month to month. One series per shape, keyed by the
       shape's name so a shape added later starts its own series rather than
       reading another's - the same reason the market's series are a map.
       Counted after the housing valves: what the landlords see.
       ------------------------------------------------------------------ */
    private Map<String, List<Double>> householdsByShape = new LinkedHashMap<>();

    /* ------------------------------------------------------------------
       WHO DIED, 2026-09-11. The month's dead by age band, and how many of
       them were orphans or had no home (Unemployment.attributeDeaths()).
       Monthly, like every flow here: the running totals Jerus asked for are
       derived on the way to the screen, so they cannot drift from these.
       ------------------------------------------------------------------ */
    private List<Double> deathsBabies = new ArrayList<>();
    private List<Double> deathsChildren = new ArrayList<>();
    private List<Double> deathsTeens = new ArrayList<>();
    private List<Double> deathsAdults = new ArrayList<>();
    private List<Double> deathsSeniors = new ArrayList<>();
    private List<Double> deathsOrphans = new ArrayList<>();
    private List<Double> deathsUnhoused = new ArrayList<>();

    /* ------------------------------------------------------------------
       CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a
       year per 100,000, the police coverage, who is inside and who was
       caught with nowhere to put them, what was stolen, who was killed -
       killed also counted with the dead - the bill, and each reason's share
       of the crime, by the reason's saved name.
       ------------------------------------------------------------------ */
    private List<Double> crimeRate = new ArrayList<>();
    private List<Double> policeCoverage = new ArrayList<>();
    private List<Double> prisoners = new ArrayList<>();
    private List<Double> caughtNotHeld = new ArrayList<>();
    private List<Double> stolen = new ArrayList<>();
    private List<Double> deathsKilled = new ArrayList<>();
    private List<Double> safetyBill = new ArrayList<>();
    private Map<String, List<Double>> crimeByCause = new LinkedHashMap<>();

    /** The series name the screens ask for, per reason for crime. */
    public static String crimeKey(Crime.Cause cause) { return "crime:" + cause.name(); }

    /** The series name the screens ask for, per household shape. */
    public static String householdKey(FamilyStructure shape) { return "households:" + shape.name(); }

    /* ------------------------- what runs out ------------------------- */
    private List<Integer> constructionCapacity = new ArrayList<>();
    private List<Double> landUse = new ArrayList<>();

    /* --------------------------- the market ---------------------------

       Jerus, 2026-09-11: "add a history of the stock price for each company,
       both in each industry, and in the reports rail." One series per
       company, by the register's name, from the month it lists - so a
       company listed in month 40 has a series 40 shorter than the axis, and
       aligned() pads the front with "we were not counting", which is the
       truth. Two of them: what the desk quoted, and what the register said
       a share was worth, because the distance between the two is the thing
       a shareholder watches.

       PER FOUNDING SHARE, not per share. A share splits a hundred for one
       when it gets dear, and a chart of the raw quote would fall a
       hundredfold in the month nothing happened to anybody's wealth. See
       Exchange.midPerFoundingShare(). Money, so a reform scales it.
       ------------------------------------------------------------------ */
    private Map<String, List<Double>> sharePrice = new LinkedHashMap<>();
    private Map<String, List<Double>> shareValue = new LinkedHashMap<>();

    /** The series name the screens ask for, per company. */
    public static String priceKey(String company) { return "sharePrice:" + company; }
    public static String valueKey(String company) { return "shareValue:" + company; }

    /* ==================================================================
       RECORDING
       ================================================================== */

    /**
     * One month, read off the city itself.
     *
     * TAKES THE GAME rather than twenty-one arguments. The old signature was
     * eight positional numbers, which was survivable; twenty-one would be a
     * machine for transposing two of them silently, and nothing in a list of
     * doubles complains when the debt goes in the interest-rate slot.
     *
     * Every read here is a getter the screens already use, so the graph cannot
     * drift from what the rest of the game says it is showing.
     */
    public void recordMonth(Game game) {

        EconomyManager economy = game.getEconomyManager();
        NationalAccounts accounts = economy.getNationalAccounts();
        PopulationManager people = game.getPopulationManager();

        month.add(game.getMonth());

        cash.add(round2(game.getCash()));
        gdp.add(round2(economy.getMonthGdp()));
        debt.add(round2(game.getDebtManager().getAllPrincipal()));
        // Four decimals: a rate is a fraction and rounding it to cents would
        // record every rate under 0.5% as zero.
        interestRate.add(Math.round(game.getDebtManager().getRate() * 10000.0) / 10000.0);
        revenue.add(round2(accounts.getTotalRevenue()));
        surplus.add(round2(accounts.getBalance()));

        jobs.add(people.getTotalJobs());
        workforce.add(people.getWorkforce());
        population.add(people.getPopulation());

        PopulationCohorts pyramid = game.getCohorts();
        Migration flows = game.getMigration();
        births.add((int) Math.round(pyramid.getLastBirths()));
        deaths.add((int) Math.round(pyramid.getLastDeaths()));
        arrivals.add((int) Math.round(flows.getLastArrivals()));
        departures.add((int) Math.round(flows.getLastDepartures()));
        totalWage.add(round2(people.getTotalWage()));

        LabourMarket labour = game.getLabourMarket();
        minimumWage.add(round4(labour.getMinimumWage()));
        unskilledPremium.add(round4(labour.premium(JobType.NO_DIPLOMA)));

        double[] mix = people.getBandShare();
        skilledShare.add(round4(1 - mix[WageBand.NONE.ordinal()]));

        Education schools = game.getEducation();
        schoolCoverage.add(round4(schools.basicCoverage()));
        schoolBill.add(round2(schools.getNetCost()));

        energyRatio.add(round4(game.getEnergyRatio()));
        waterRatio.add(round4(game.getWaterRatio()));
        roadRatio.add(round4(game.getRoadRatio()));
        sickRate.add(round4(game.getHealth().getSickRate()));
        careCoverage.add(round4(game.getHealth().getCoverage()));

        landPrice.add(Math.round(game.getLandManager().getAcquisitionCostPerSqFt() * 1e6) / 1e6);
        foodPrice.add(round4(game.getMarkets().get(Good.FOOD).getLocalPrice()));
        materialsPrice.add(round4(game.getBuildingManager().getConstructionMaterialPrice()));
        orePrice.add(round4(game.getMarkets().get(Good.IRON).exportPrice()));

        /* ------------------------- the edge ------------------------- */
        ForeignAccounts abroad = game.getForeignAccounts();
        fxRate.add(round4(abroad.getRate()));
        reservesUsd.add(round2(abroad.getReservesUsd()));
        foreignDebtUsd.add(round2(abroad.getForeignDebtUsd()));
        currentAccount.add(round2(abroad.currentAccount()));
        exportsAbroad.add(round2(abroad.getExports()));
        importsAbroad.add(round2(abroad.tradeImports()));

        /* --------------------- money and credit --------------------- */
        priceIndex.add(round4(game.getPriceIndex().getIndex()));
        businessDebt.add(round2(economy.getBusinessDebtManager().getTotalPrincipal()));

        Bank lender = game.getBank();
        bankPremium.add(round4(lender.ratePremium()));
        bankDeposits.add(round2(lender.depositsGathered()));
        bankLent.add(round2(lender.getBook()));
        bankEquity.add(round2(lender.equity()));
        bankWriteOffs.add(round2(lender.getWriteOffs()));
        bankCapacity.add(round2(lender.capacity()));
        // CLAMPED AT TEN. strain() is Double.MAX_VALUE in a city with no bank,
        // which is correct and unplottable - it would flatten every other point
        // on the chart into the axis. Ten times capacity is already off any
        // scale a player cares about.
        bankStrain.add(round4(Math.min(10, lender.strain())));
        bankProfit.add(round2(lender.getNetIncome()));
        bankBranches.add(round2(lender.getBranches()));
        householdSavings.add(round2(game.getHouseholds().getCumulativeSaving()));

        /* ------------------------ the budget ------------------------ */
        taxWage.add(round2(accounts.getTaxWage()));
        taxProperty.add(round2(accounts.getPropertyTax()));
        taxSales.add(round2(accounts.getTaxSales()));
        taxBusiness.add(round2(accounts.getTaxBusiness()));
        taxIndustrial.add(round2(accounts.getTaxIndustrial()));
        contributions.add(round2(accounts.getContributions()));
        pensionBill.add(round2(accounts.getPensions()));
        healthBill.add(round2(game.getHealthcare().getNetCost()));

        /* ------------------------- housing ------------------------- */
        rentPrice.add(round2(game.getSectors().realEstate().getRentPrice()));
        homes.add(game.getBuildingManager().getTotalHomes());
        households.add(round2(game.getFamilies().totalHouseholds()));

        /* --------------------- school and care --------------------- */
        students.add(round2(sum(schools.getStudying())));
        graduates.add(round2(sum(schools.getGraduates())));
        licences.add(round2(sum(schools.getLicences())));
        unburied.add(round2(game.getHealthcare().getUnburied()));

        /* ------------------- outside the families ------------------- */
        Unemployment pool = game.getUnemployment();
        FamilyModel families = game.getFamilies();
        outOfWorkOnEi.add(round2(pool.onEi()));
        outOfWorkOffEi.add(round2(pool.getOffEi()));
        double noDoor = pool.getUnhoused();
        for (double v : families.unhousedPeopleByBand()) noDoor += v;
        unhoused.add(round2(noDoor));
        orphans.add(round2(families.getOrphansTotal()));
        evicted.add(round2(game.getHouseholdBalance().getEvicted()));
        eiPaid.add(round2(accounts.getEiBenefits()));
        eiPremiums.add(round2(accounts.getEiPremiums()));
        studentGrants.add(round2(accounts.getStudentGrants()));
        studentLoansOwed.add(round2(game.getHouseholdBalance().totalStudentDebt()));
        Sickness sickness = game.getSickness();
        sickPastTwoMonths.add(round2(sickness.peoplePastTwoMonths(pyramid)));
        diedOfIllness.add(round2(sickness.getLastDeaths()));
        sickRecovery.add(round4(sickness.getLastRecovery()));
        for (FamilyStructure shape : FamilyStructure.values()) {
            householdsByShape.computeIfAbsent(shape.name(), k -> new ArrayList<>())
                    .add(round2(families.totalOf(shape)));
        }
        deathsBabies.add(round2(pyramid.getDeaths(AgeBand.BABY)));
        deathsChildren.add(round2(pyramid.getDeaths(AgeBand.CHILD)));
        deathsTeens.add(round2(pyramid.getDeaths(AgeBand.TEEN)));
        deathsAdults.add(round2(pyramid.getDeaths(AgeBand.ADULT)));
        deathsSeniors.add(round2(pyramid.getDeaths(AgeBand.SENIOR)));
        deathsOrphans.add(round2(game.getLastOrphanDeaths()));
        deathsUnhoused.add(round2(game.getLastUnhousedDeaths()));
        Crime crime = game.getCrime();
        crimeRate.add(round2(crime.getRatePer100k()));
        policeCoverage.add(round4(crime.getCoverage()));
        prisoners.add(round2(crime.prisoners()));
        caughtNotHeld.add(round2(crime.getNotHeld()));
        stolen.add(round2(crime.getStolen()));
        deathsKilled.add(round2(pyramid.getKilled(AgeBand.ADULT)));
        safetyBill.add(round2(crime.getGrossCost()));
        for (Crime.Cause cause : Crime.Cause.values()) {
            crimeByCause.computeIfAbsent(cause.name(), k -> new ArrayList<>())
                    .add(round2(crime.getCrimes(cause)));
        }

        /* ----------------------- what runs out ----------------------- */
        constructionCapacity.add(game.getBuildingManager().getTotalConstructionCapacity());
        landUse.add(round4(game.getLandManager().getUtilisation()));

        /* ------------------------- the market ------------------------- */
        Equity register = game.getEquity();
        Exchange exchange = game.getExchange();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (register.getShares(c) <= 0) continue;   // not listed: not counting
            String company = Equity.COMPANIES[c];
            sharePrice.computeIfAbsent(company, k -> new ArrayList<>())
                    .add(round4(exchange.midPerFoundingShare(c)));
            shareValue.computeIfAbsent(company, k -> new ArrayList<>())
                    .add(round4(exchange.fairPerFoundingShare(c)));
        }
    }

    /**
     * A whole array in one figure.
     *
     * The education and licence series are per-type arrays, and the graph wants
     * the city's total - one line saying "this many people are studying", not
     * six saying which rung each is on. The screens that care about the split
     * already have it.
     */
    private static double sum(double[] values) {
        if (values == null) return 0;
        double total = 0;
        for (double v : values) total += v;
        return total;
    }

    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
    private static double round4(double v) { return Math.round(v * 10000.0) / 10000.0; }

    /**
     * Takes over another history wholesale - the load path.
     *
     * ONE PLACE. The load used to copy eight lists by hand in Game, so every
     * series added had to be remembered in two files, and a series forgotten
     * here would simply vanish on reload while looking perfectly fine in a live
     * game. Reflection is not used: an explicit list that lives next to the
     * fields it copies is checkable by eye, and HistoryCheck saves and reloads a
     * played city and compares every series, so a line missing from here fails
     * out loud.
     */
    public void restoreFrom(HistorySave loaded) {
        if (loaded == null) return;

        month = copy(loaded.month);

        cash = copy(loaded.cash);
        gdp = copy(loaded.gdp);
        debt = copy(loaded.debt);
        interestRate = copy(loaded.interestRate);
        revenue = copy(loaded.revenue);
        surplus = copy(loaded.surplus);

        jobs = copy(loaded.jobs);
        workforce = copy(loaded.workforce);
        population = copy(loaded.population);
        births = copy(loaded.births);
        deaths = copy(loaded.deaths);
        arrivals = copy(loaded.arrivals);
        departures = copy(loaded.departures);
        totalWage = copy(loaded.totalWage);
        minimumWage = copy(loaded.minimumWage);
        unskilledPremium = copy(loaded.unskilledPremium);
        skilledShare = copy(loaded.skilledShare);
        schoolCoverage = copy(loaded.schoolCoverage);
        schoolBill = copy(loaded.schoolBill);

        energyRatio = copy(loaded.energyRatio);
        waterRatio = copy(loaded.waterRatio);
        roadRatio = copy(loaded.roadRatio);
        sickRate = copy(loaded.sickRate);
        careCoverage = copy(loaded.careCoverage);

        landPrice = copy(loaded.landPrice);
        foodPrice = copy(loaded.foodPrice);
        materialsPrice = copy(loaded.materialsPrice);
        orePrice = copy(loaded.orePrice);

        fxRate = copy(loaded.fxRate);
        reservesUsd = copy(loaded.reservesUsd);
        foreignDebtUsd = copy(loaded.foreignDebtUsd);
        currentAccount = copy(loaded.currentAccount);
        exportsAbroad = copy(loaded.exportsAbroad);
        importsAbroad = copy(loaded.importsAbroad);

        priceIndex = copy(loaded.priceIndex);
        businessDebt = copy(loaded.businessDebt);
        bankPremium = copy(loaded.bankPremium);
        bankDeposits = copy(loaded.bankDeposits);
        bankLent = copy(loaded.bankLent);
        bankEquity = copy(loaded.bankEquity);
        bankWriteOffs = copy(loaded.bankWriteOffs);
        bankCapacity = copy(loaded.bankCapacity);
        bankStrain = copy(loaded.bankStrain);
        bankProfit = copy(loaded.bankProfit);
        bankBranches = copy(loaded.bankBranches);
        householdSavings = copy(loaded.householdSavings);

        taxWage = copy(loaded.taxWage);
        taxProperty = copy(loaded.taxProperty);
        taxSales = copy(loaded.taxSales);
        taxBusiness = copy(loaded.taxBusiness);
        taxIndustrial = copy(loaded.taxIndustrial);
        contributions = copy(loaded.contributions);
        pensionBill = copy(loaded.pensionBill);
        healthBill = copy(loaded.healthBill);

        rentPrice = copy(loaded.rentPrice);
        homes = copy(loaded.homes);
        households = copy(loaded.households);

        students = copy(loaded.students);
        graduates = copy(loaded.graduates);
        licences = copy(loaded.licences);
        unburied = copy(loaded.unburied);

        outOfWorkOnEi = copy(loaded.outOfWorkOnEi);
        outOfWorkOffEi = copy(loaded.outOfWorkOffEi);
        unhoused = copy(loaded.unhoused);
        orphans = copy(loaded.orphans);
        evicted = copy(loaded.evicted);
        eiPaid = copy(loaded.eiPaid);
        eiPremiums = copy(loaded.eiPremiums);
        studentGrants = copy(loaded.studentGrants);
        studentLoansOwed = copy(loaded.studentLoansOwed);
        sickPastTwoMonths = copy(loaded.sickPastTwoMonths);
        diedOfIllness = copy(loaded.diedOfIllness);
        sickRecovery = copy(loaded.sickRecovery);
        householdsByShape = copyMap(loaded.householdsByShape);
        deathsBabies = copy(loaded.deathsBabies);
        deathsChildren = copy(loaded.deathsChildren);
        deathsTeens = copy(loaded.deathsTeens);
        deathsAdults = copy(loaded.deathsAdults);
        deathsSeniors = copy(loaded.deathsSeniors);
        deathsOrphans = copy(loaded.deathsOrphans);
        deathsUnhoused = copy(loaded.deathsUnhoused);
        crimeRate = copy(loaded.crimeRate);
        policeCoverage = copy(loaded.policeCoverage);
        prisoners = copy(loaded.prisoners);
        caughtNotHeld = copy(loaded.caughtNotHeld);
        stolen = copy(loaded.stolen);
        deathsKilled = copy(loaded.deathsKilled);
        safetyBill = copy(loaded.safetyBill);
        crimeByCause = copyMap(loaded.crimeByCause);

        constructionCapacity = copy(loaded.constructionCapacity);
        landUse = copy(loaded.landUse);

        sharePrice = copyMap(loaded.sharePrice);
        shareValue = copyMap(loaded.shareValue);
    }

    /** A map of series, copied list by list, and never null - see copy(). */
    private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from) {
        Map<String, List<Double>> out = new LinkedHashMap<>();
        if (from == null) return out;
        for (Map.Entry<String, List<Double>> e : from.entrySet()) out.put(e.getKey(), copy(e.getValue()));
        return out;
    }

    /**
     * A copy, and never null.
     *
     * Gson leaves a field alone when the JSON has no key for it, so a history
     * written before a series existed arrives with that field still holding the
     * empty list from the field initialiser. It arrives as null only if the
     * file explicitly says null. Both mean the same thing here - nothing was
     * recorded - and both have to come out as a list the screen can ask the
     * size of.
     */
    private static <T> List<T> copy(List<T> from) {
        return from == null ? new ArrayList<>() : new ArrayList<>(from);
    }

    /**
     * The graph history. Written the same guarded way as the save itself: this
     * file is every month the city has ever lived, so losing it to a half-write
     * costs more than the save does.
     */
    public GameFiles.Result saveHistory(GameFiles files, int slot) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return files.write(files.historyFile(slot), gson.toJson(this));
    }

    /* ==================================================================
       READING
       ================================================================== */

    /** How many months the city has lived. Every series is measured against it. */
    public int months() { return month.size(); }

    public List<Integer> getMonth() { return month; }

    /**
     * A series as doubles, padded at the FRONT to the full month axis.
     *
     * The padding is the whole point. A series shorter than the axis was not
     * being recorded yet, which puts its data at the END of the city's life -
     * so it is pushed right and the months before it are NaN. NaN rather than
     * zero, because zero is a value: a sick rate of 0% is a healthy city and
     * "we were not counting" is not the same claim.
     *
     * A series LONGER than the axis cannot be padded into anything meaningful
     * and is returned trimmed to the last months(), which is the only reading
     * of it that is not a guess. HistoryCheck asserts this never happens.
     */
    public double[] aligned(String name) {
        List<? extends Number> raw = seriesByName().get(name);
        double[] out = new double[month.size()];
        if (raw == null) {
            java.util.Arrays.fill(out, Double.NaN);
            return out;
        }
        int offset = month.size() - raw.size();
        for (int i = 0; i < out.length; i++) {
            int j = i - offset;
            out[i] = (j >= 0 && j < raw.size()) ? raw.get(j).doubleValue() : Double.NaN;
        }
        return out;
    }

    /**
     * A monthly series summed from its first recorded month, for the running
     * totals of the dead. The months before a series was recorded stay NaN -
     * not zero, for aligned()'s reason - and a NaN gap after it began carries
     * the total forward rather than breaking the line.
     */
    public static double[] runningTotal(double[] monthly) {
        double[] out = new double[monthly.length];
        double sum = 0;
        boolean started = false;
        for (int i = 0; i < monthly.length; i++) {
            if (Double.isNaN(monthly[i])) {
                out[i] = started ? sum : Double.NaN;
                continue;
            }
            started = true;
            sum += monthly[i];
            out[i] = sum;
        }
        return out;
    }

    /** Every stored series, by the name the screen asks for. */
    public Map<String, List<? extends Number>> seriesByName() {
        Map<String, List<? extends Number>> map = new LinkedHashMap<>();
        map.put("cash", cash);
        map.put("gdp", gdp);
        map.put("debt", debt);
        map.put("interestRate", interestRate);
        map.put("revenue", revenue);
        map.put("surplus", surplus);
        map.put("jobs", jobs);
        map.put("workforce", workforce);
        map.put("population", population);
        map.put("births", births);
        map.put("deaths", deaths);
        map.put("arrivals", arrivals);
        map.put("departures", departures);
        map.put("totalWage", totalWage);
        map.put("minimumWage", minimumWage);
        map.put("unskilledPremium", unskilledPremium);
        map.put("skilledShare", skilledShare);
        map.put("schoolCoverage", schoolCoverage);
        map.put("schoolBill", schoolBill);
        map.put("energyRatio", energyRatio);
        map.put("waterRatio", waterRatio);
        map.put("roadRatio", roadRatio);
        map.put("sickRate", sickRate);
        map.put("careCoverage", careCoverage);
        map.put("landPrice", landPrice);
        map.put("foodPrice", foodPrice);
        map.put("materialsPrice", materialsPrice);
        map.put("orePrice", orePrice);

        map.put("fxRate", fxRate);
        map.put("reservesUsd", reservesUsd);
        map.put("foreignDebtUsd", foreignDebtUsd);
        map.put("currentAccount", currentAccount);
        map.put("exportsAbroad", exportsAbroad);
        map.put("importsAbroad", importsAbroad);

        map.put("priceIndex", priceIndex);
        map.put("businessDebt", businessDebt);
        map.put("bankPremium", bankPremium);
        map.put("bankDeposits", bankDeposits);
        map.put("bankLent", bankLent);
        map.put("bankEquity", bankEquity);
        map.put("bankWriteOffs", bankWriteOffs);
        map.put("bankCapacity", bankCapacity);
        map.put("bankStrain", bankStrain);
        map.put("bankProfit", bankProfit);
        map.put("bankBranches", bankBranches);
        map.put("householdSavings", householdSavings);

        map.put("taxWage", taxWage);
        map.put("taxProperty", taxProperty);
        map.put("taxSales", taxSales);
        map.put("taxBusiness", taxBusiness);
        map.put("taxIndustrial", taxIndustrial);
        map.put("contributions", contributions);
        map.put("pensionBill", pensionBill);
        map.put("healthBill", healthBill);

        map.put("rentPrice", rentPrice);
        map.put("homes", homes);
        map.put("households", households);

        map.put("students", students);
        map.put("graduates", graduates);
        map.put("licences", licences);
        map.put("unburied", unburied);

        map.put("outOfWorkOnEi", outOfWorkOnEi);
        map.put("outOfWorkOffEi", outOfWorkOffEi);
        map.put("unhoused", unhoused);
        map.put("orphans", orphans);
        map.put("evicted", evicted);
        map.put("eiPaid", eiPaid);
        map.put("eiPremiums", eiPremiums);
        map.put("studentGrants", studentGrants);
        map.put("studentLoansOwed", studentLoansOwed);
        map.put("sickPastTwoMonths", sickPastTwoMonths);
        map.put("diedOfIllness", diedOfIllness);
        map.put("sickRecovery", sickRecovery);
        map.put("deathsBabies", deathsBabies);
        map.put("deathsChildren", deathsChildren);
        map.put("deathsTeens", deathsTeens);
        map.put("deathsAdults", deathsAdults);
        map.put("deathsSeniors", deathsSeniors);
        map.put("deathsOrphans", deathsOrphans);
        map.put("deathsUnhoused", deathsUnhoused);
        map.put("crimeRate", crimeRate);
        map.put("policeCoverage", policeCoverage);
        map.put("prisoners", prisoners);
        map.put("caughtNotHeld", caughtNotHeld);
        map.put("stolen", stolen);
        map.put("deathsKilled", deathsKilled);
        map.put("safetyBill", safetyBill);
        if (crimeByCause != null) {
            for (Map.Entry<String, List<Double>> e : crimeByCause.entrySet()) {
                map.put("crime:" + e.getKey(), e.getValue());
            }
        }
        if (householdsByShape != null) {
            for (Map.Entry<String, List<Double>> e : householdsByShape.entrySet()) {
                map.put("households:" + e.getKey(), e.getValue());
            }
        }

        map.put("constructionCapacity", constructionCapacity);
        map.put("landUse", landUse);

        // Every company that has ever been listed, by the register's name.
        if (sharePrice != null) {
            for (Map.Entry<String, List<Double>> e : sharePrice.entrySet()) map.put(priceKey(e.getKey()), e.getValue());
        }
        if (shareValue != null) {
            for (Map.Entry<String, List<Double>> e : shareValue.entrySet()) map.put(valueKey(e.getKey()), e.getValue());
        }
        return map;
    }

    /* The originals, still here because other code and the harnesses read them. */
    public List<Double> getCash()          { return cash; }
    public List<Double> getGdp()           { return gdp; }
    public List<Double> getDebt()          { return debt; }
    public List<Double> getInterestRate()  { return interestRate; }
    public List<Integer> getJobs()         { return jobs; }
    public List<Integer> getWorkforce()    { return workforce; }
    public List<Integer> getPopulation()   { return population; }

    /**
     * Redraws the city's whole history in the new unit.
     *
     * WITHOUT THIS THE GRAPHS GET A CLIFF. A reform is a change of units, not
     * an event in the economy, so a chart of three centuries of GDP must not
     * have a hundredfold step in it on the month the player pressed the button.
     * Every real country's long-run price series is spliced exactly this way.
     *
     * Rates and ratios are left alone - an interest rate, a fill ratio and a
     * sick rate are the same numbers in any currency - and so are headcounts.
     */
    public void redenominate(double scale) {
        scaleAll(scale, cash, gdp, debt, revenue, surplus,
                totalWage, minimumWage, schoolBill,
                landPrice, foodPrice, materialsPrice, orePrice);

        /*
         * The same reform, applied to everything added since - and the list of
         * what is NOT here is the interesting half.
         *
         * reservesUsd and foreignDebtUsd are owed and held in somebody else's
         * money, which a domestic reform cannot reach. priceIndex is a ratio of
         * two baskets and a reform divides both. bankPremium is a rate. homes,
         * households, students, graduates, licences, unburied and
         * constructionCapacity are counts of things, and landUse is a share -
         * none of them is money at all.
         */
        scaleAll(scale, fxRate, currentAccount, exportsAbroad, importsAbroad,
                businessDebt, bankDeposits, bankLent, bankEquity, bankWriteOffs,
                bankCapacity, bankProfit,
                householdSavings,
                taxWage, taxProperty, taxSales, taxBusiness, taxIndustrial,
                contributions, pensionBill, healthBill,
                rentPrice,
                eiPaid, eiPremiums, studentGrants, studentLoansOwed,
                stolen, safetyBill);

        // A share's price is money; how many shares there are is not.
        if (sharePrice != null) for (List<Double> s : sharePrice.values()) scaleAll(scale, s);
        if (shareValue != null) for (List<Double> s : shareValue.values()) scaleAll(scale, s);
    }

    @SafeVarargs
    private static void scaleAll(double scale, List<Double>... series) {
        for (List<Double> s : series) {
            if (s == null) continue;
            for (int i = 0; i < s.size(); i++) {
                Double v = s.get(i);
                if (v != null) s.set(i, v * scale);
            }
        }
    }

}
