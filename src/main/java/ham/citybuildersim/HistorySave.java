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
        foodPrice.add(round4(economy.getIndustrialHandler().getFoodPrice()));
        materialsPrice.add(round4(game.getBuildingManager().getConstructionMaterialPrice()));
        orePrice.add(round4(game.getIronMarket().getExportPrice()));
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
}
