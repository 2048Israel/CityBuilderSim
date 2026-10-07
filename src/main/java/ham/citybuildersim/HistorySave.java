package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigDecimal;
import java.math.MathContext;
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
 *
 * PAST FIVE HUNDRED YEARS, A YEAR TO A POINT (0.7.55)
 *
 * Jerus: "at the 500y mark, past data starts converting to yearly figures,
 * but always keep 500 recent years in monthly". The newest MONTHLY_KEPT
 * months stay a month to an entry; a calendar year all of whose months are
 * older than those folds into one entry (foldOldYears(), at the end of every
 * recordMonth()), each series by the rule the year book reads it by
 * (YearBook.kindOf()): a flow's months ADDED, a level's LAST month, a rate's
 * months AVERAGED. The fold's entry sits on the axis at its year's last
 * month, and foldedMonths says how many months each folded entry holds - the
 * first yearlyPoints() entries are years, every one after is a month. A
 * history from before 0.7.55 has no foldedMonths and is all months, as it
 * always was.
 *
 * READ A MONTH AT A TIME. aligned() hands a folded year's flow back as its
 * monthly average - the year's sum over its months - so a chart's line, a
 * per-head figure and a rolling year all read one unit across the boundary;
 * a level is its year's end and a rate its year's average, as stored. What
 * needs the sum itself (total(), runningTotal(), recentTotal(), worstYear())
 * reads the stored figures, and monthsIn() says what each entry weighs.
 */
public class HistorySave {

    /* ------------------------------------------------------------------
       THE AXIS

       Every other list is measured against this one, and its length is the
       length of the city's life. A series shorter than this started later;
       a series LONGER than this is a bug and HistoryCheck says so.
       ------------------------------------------------------------------ */
    private List<Integer> month = new ArrayList<>();

    /**
     * How many months each folded year holds, oldest first (0.7.55): the
     * first foldedMonths.size() entries of the axis, and of every series
     * lined up with it, are years - see PAST FIVE HUNDRED YEARS above. Twelve
     * for a whole year; fewer for a founding year the city did not live all
     * of. Empty in a history that has never folded, which is every history
     * written before 0.7.55: Gson leaves the field's empty list alone, so an
     * older file reads as all months, which it is.
     */
    private List<Integer> foldedMonths = new ArrayList<>();

    /** Months kept a month to an entry, the newest: five hundred years (0.7.55). Older years fold, a year to an entry. */
    public static final int MONTHLY_KEPT = 6_000;

    /* --------------------------- money --------------------------- */
    private List<Double> cash = new ArrayList<>();
    private List<Double> gdp = new ArrayList<>();
    private List<Double> debt = new ArrayList<>();
    private List<Double> interestRate = new ArrayList<>();

    /** Government revenue and what it kept, both monthly. */
    private List<Double> revenue = new ArrayList<>();
    private List<Double> surplus = new ArrayList<>();

    /* --------------------------- people --------------------------- */
    private List<Long> jobs = new ArrayList<>();
    private List<Long> workforce = new ArrayList<>();
    /**
     * The people out of work - the labour force less the posts FILLED, the
     * People screen's figure (PopulationManager.getUnemployed()). Recorded
     * since 2026-09-15 because nothing else here could reproduce it: the
     * workforce above still counts the students and the prisoners, and the
     * jobs are posts offered rather than posts filled, so "workforce less
     * jobs" read 14% on a city whose pool was empty.
     */
    private List<Long> outOfWork = new ArrayList<>();
    private List<Long> population = new ArrayList<>();

    /** The four flows that move the population, and only these four move it. */
    private List<Long> births = new ArrayList<>();
    private List<Long> deaths = new ArrayList<>();
    private List<Long> arrivals = new ArrayList<>();
    private List<Long> departures = new ArrayList<>();

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

    /**
     * The outbreak running, as extra absence on top of what the city's care
     * explains (Health.getOutbreakSeverity()); 0 between outbreaks. What the
     * year book names an epidemic on (A8, 0.7.46) - the sick rate itself sits
     * near Health.UNTREATED_RATE for the whole life of a city short of care.
     * A share, so a reform leaves it alone; from 0.7.46 on, so an older
     * history names no epidemic before it.
     */
    private List<Double> outbreak = new ArrayList<>();

    /* --------------------------- prices --------------------------- */
    /**
     * What the world asks the city for a square foot of ground, in thousands
     * of US DOLLARS since 0.7.6 (LandManager.getGroundUsdPerSqFt()); the
     * months an older save recorded are local money, and stay as recorded.
     */
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
    /**
     * Parity beside the rate (0.7.35): where a basket costs the same here
     * and abroad, in the rate's own units (ForeignAccounts.getParity()), so
     * the Trade tab can draw the two on one chart. A history file older than
     * 0.7.35 has none, and its months read as not recorded.
     */
    private List<Double> fxParity = new ArrayList<>();
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
    /*
     * THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction
     * a year, and how far it believed the bank (Expectations) - both ratios,
     * so a reform leaves them alone. An older save has none until it plays a
     * month, which aligned() pads as "not counting".
     */
    private List<Double> expectedInflation = new ArrayList<>();
    private List<Double> credibility = new ArrayList<>();
    private List<Double> businessDebt = new ArrayList<>();
    /*
     * bankPremium WAS HERE until 0.7.7: the points the strained bank added to
     * every rate in the city. The premium is gone, so the series is: a save's
     * history that still carries the key loads as it always did - Gson skips
     * a key no field is named for - and the series is not written again.
     */
    private List<Double> bankDeposits = new ArrayList<>();
    private List<Double> bankLent = new ArrayList<>();
    private List<Double> bankEquity = new ArrayList<>();
    private List<Double> bankWriteOffs = new ArrayList<>();

    /* ------------------- what the bank could carry, and how hard -------------------

       Added for the Bank tab. The four above (five, with bankPremium, until
       0.7.7) say what the bank IS; these four say whether it is in trouble,
       which is a different question - and until 0.7.7 the one every rate in
       the city turned on.

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

    /* ------------------------ the price of money (0.7.7) ------------------------

       The dial and the bank's three prices, a month each. policyRate is the
       one that was missing longest: a player whose autopilot moved the dial
       could see every rate it moved and never the dial itself. bankPrime is
       what a sound business pays (Bank.prime()), bankDepositRate what savers
       were paid, and bankFees the month's fee income, which is money and moves
       with a reform; the other three are rates and do not. An older history
       starts them empty, padded with NaN like every late series.
       ------------------------------------------------------------------------------ */
    private List<Double> policyRate = new ArrayList<>();
    private List<Double> bankPrime = new ArrayList<>();
    private List<Double> bankDepositRate = new ArrayList<>();
    private List<Double> bankFees = new ArrayList<>();

    /* ------------------------ the bank's capital (0.7.8) ------------------------

       What it holds and what it does with it, a month each: its capital
       ratio (equity over the weighted book, clamped at ten like the strain,
       and ten with nothing lent), the target it chose, the allowance it has
       set aside against its loans, the month's provision, the dividend it
       paid, and its return on the equity it opened the month with, a year.
       The allowance, the provision and the dividend are money and move with
       a reform; the three rates do not. An older history starts them empty.
       ------------------------------------------------------------------------------ */
    private List<Double> bankCapitalRatio = new ArrayList<>();
    private List<Double> bankCapitalTarget = new ArrayList<>();
    private List<Double> bankAllowance = new ArrayList<>();
    private List<Double> bankProvisions = new ArrayList<>();
    private List<Double> bankDividends = new ArrayList<>();
    private List<Double> bankReturnOnEquity = new ArrayList<>();

    /*
     * ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity
     * over everything on its sheet (Bank.leverageRatio()) and the target it
     * holds on that measure (leverageTarget()). The capital ratio above is
     * clamped at ten and a city's bank is often held by this one instead
     * (leverageBinds()), which was never recorded. Rates, clamped at ten like
     * the capital ratio - nothing on the sheet reads as the ceiling - and
     * from 0.7.46 on: an older history starts them empty.
     */
    private List<Double> bankLeverageRatio = new ArrayList<>();
    private List<Double> bankLeverageTarget = new ArrayList<>();

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
    private List<Long> homes = new ArrayList<>();
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
    /** The health premium collected, a month at a time - the EI premium's shape (2026-09-19). */
    private List<Double> healthPremiums = new ArrayList<>();
    private List<Double> studentGrants = new ArrayList<>();
    /*
     * FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households'
     * groceries a month, and the baskets that bought at the price charged.
     * An older save has none until it plays a month, which aligned() pads as
     * "not counting".
     */
    private List<Double> foodAssistance = new ArrayList<>();
    private List<Double> fedByAssistance = new ArrayList<>();
    private List<Double> studentLoansOwed = new ArrayList<>();
    /** Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). */
    private List<Double> studentLoanInterest = new ArrayList<>();

    /* ------------------------------------------------------------------
       THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's:
       M0 (what the central bank owes - every dollar it has made and not
       taken back), M2 (what the public holds - the bank's deposits plus
       currency), what the treasury owes it in advances, its reserves
       liability (M0 less currency, which is none yet, so the two lines lie
       on each other until somebody holds cash), and the month's remittance.
       ------------------------------------------------------------------ */
    private List<Double> m0 = new ArrayList<>();
    private List<Double> m2 = new ArrayList<>();
    private List<Double> advancesToTreasury = new ArrayList<>();
    private List<Double> reserves = new ArrayList<>();
    private List<Double> remittance = new ArrayList<>();

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
    /** The over-85s, since the band was split on 2026-09-15. */
    private List<Double> deathsElders = new ArrayList<>();
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
    private List<Long> constructionCapacity = new ArrayList<>();
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
       Exchange.pricePerFoundingShare() - the last trade since 0.7.12 round 2,
       the dealer's quote before. Money, so a reform scales it.
       ------------------------------------------------------------------ */
    private Map<String, List<Double>> sharePrice = new LinkedHashMap<>();
    private Map<String, List<Double>> shareValue = new LinkedHashMap<>();

    /** The series name the screens ask for, per company. */
    public static String priceKey(String company) { return "sharePrice:" + company; }
    public static String valueKey(String company) { return "shareValue:" + company; }

    /* ------------------------- the sectors (0.7.4) -------------------------

       Jerus, on the sector list: "i think it would be pretty for beside each
       sector to show some quick info, not only net income and change but
       also a little graph of its net income, perhaps how much workers it
       employs total". SectorBooks keeps two months and a sparkline wants
       two years, so the list gets two series per sector the way the share
       registers have theirs: keyed by the sector's name (Sector.key(), which
       is its label and the register's company name), from the month it is
       first recorded - so an older save has none until it plays a month, and
       aligned() pads the front with "we were not counting".

       The month's net income after tax off the sector's own statement - the
       figure the list's card shows, SectorBooks' netIncome() - a FLOW, money,
       so a reform scales it; and its posts filled, Sector.getWorkers(), a
       LEVEL and a headcount.
       ------------------------------------------------------------------ */
    private Map<String, List<Double>> sectorNetIncome = new LinkedHashMap<>();
    private Map<String, List<Double>> sectorWorkers = new LinkedHashMap<>();

    /** The series name the screens ask for, per sector: the month's net income after tax. */
    public static String netIncomeKey(String sector) { return "netIncome:" + sector; }
    /** ...and its posts filled. */
    public static String workersKey(String sector)   { return "workers:" + sector; }

    /* ------------------------ GDP in layers (0.7.6) ------------------------

       Jerus: "have it so the gdp graph can be a toggle, and if toggled it
       switches from line to mountain graph ... showing how much is made up
       of investments, net exports, government spending, aka breaking it
       down." gdp is C+I+G+NX and only the sum was kept, so the four parts
       are kept beside it, off NationalAccounts' own getters - consumption,
       investment, government and net exports - each a FLOW and money, so a
       reform scales them. They add up to gdp for the month to the cent
       (each is rounded on its own), and an older save has none of them
       until it plays a month, which aligned() pads as "not counting".
       ------------------------------------------------------------------ */
    private List<Double> consumption = new ArrayList<>();
    private List<Double> investment = new ArrayList<>();
    private List<Double> government = new ArrayList<>();
    private List<Double> netExports = new ArrayList<>();

    /* --------------------- the city's fund (0.7.39) ---------------------

       Jerus, 2026-10-02: "the city fund should show pnl and acb and all
       that". The fund's worth each month (Game.fundValue(), what its transfer
       is struck on), and what the city has put into it and taken out of it,
       CUMULATIVE (TreasuryFund.getPutIn(), getTakenOut()) - so a month's flow
       is a difference and nothing new is kept on the fund. The fund's
       Portfolio chart and its return over the chart's window (FundView.rangeReturn(),
       Modified Dietz) read the three. Money, so a reform scales them; an
       older save has none until it plays a month, which aligned() pads as
       "not counting".
       ------------------------------------------------------------------ */
    private List<Double> fundValue = new ArrayList<>();
    private List<Double> fundPutIn = new ArrayList<>();
    private List<Double> fundTakenOut = new ArrayList<>();

    /* ------------------ the new price model, read (0.7.45) ------------------

       The UI pass over 0.7.42-0.7.44's prices: what City History draws of
       the anchor, the basket, the shelf and who goes without. The expected
       price level every Pe-struck constant is struck at; each component's
       own level, chained across every link (PriceIndex.getComponentLevel()),
       and its weight in the basket in force, with the month that basket was
       linked - a change in it is a link, which the chart marks; the shelf
       price and its floor (money, so a reform scales them); the baskets
       asked for at the price and handed over; the hunger and its priced-out
       half, and the households on food assistance; and the margin the
       kitchens and the counters charge against the one they aim at. Recorded
       from 0.7.45 with no back-fill - a flow cannot be reconstructed - so an
       older city's lines start at its first month on this build, which
       aligned() pads as "not counting".
       ------------------------------------------------------------------------ */
    private List<Double> expectedLevel = new ArrayList<>();
    private List<Double> indexGroceries = new ArrayList<>();
    private List<Double> indexRent = new ArrayList<>();
    private List<Double> indexMeals = new ArrayList<>();
    private List<Double> indexLuxury = new ArrayList<>();
    private List<Double> indexServices = new ArrayList<>();
    private List<Double> weightGroceries = new ArrayList<>();
    private List<Double> weightRent = new ArrayList<>();
    private List<Double> weightMeals = new ArrayList<>();
    private List<Double> weightLuxury = new ArrayList<>();
    private List<Double> weightServices = new ArrayList<>();
    private List<Double> basketLinkedAt = new ArrayList<>();
    private List<Double> shelfPrice = new ArrayList<>();
    private List<Double> shelfFloor = new ArrayList<>();
    private List<Double> basketsAsked = new ArrayList<>();
    private List<Double> basketsHanded = new ArrayList<>();
    private List<Double> hunger = new ArrayList<>();
    private List<Double> hungerPricedOut = new ArrayList<>();
    private List<Double> householdsAssisted = new ArrayList<>();
    private List<Double> mealMargin = new ArrayList<>();
    private List<Double> mealTargetMargin = new ArrayList<>();
    private List<Double> luxuryMargin = new ArrayList<>();
    private List<Double> luxuryTargetMargin = new ArrayList<>();

    /** The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. */
    public static String indexKey(int component) { return "index" + capitalised(PriceIndex.COMPONENT_NAMES[component]); }

    /** ...and its weight in the basket in force: "weightGroceries" and so on. */
    public static String weightKey(int component) { return "weight" + capitalised(PriceIndex.COMPONENT_NAMES[component]); }

    private static String capitalised(String s) { return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1); }

    /** The component lists in COMPONENTS order, for the recording and the reform. */
    private List<List<Double>> indexLists()  { return java.util.List.of(indexGroceries, indexRent, indexMeals, indexLuxury, indexServices); }
    private List<List<Double>> weightLists() { return java.util.List.of(weightGroceries, weightRent, weightMeals, weightLuxury, weightServices); }

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
        // ...and the four parts it is the sum of (0.7.6), off the same accounts.
        consumption.add(round2(accounts.getConsumption()));
        investment.add(round2(accounts.getInvestment()));
        government.add(round2(accounts.getGovernment()));
        netExports.add(round2(accounts.getNetExports()));
        debt.add(round2(game.getDebtManager().getAllPrincipal()));
        // Four decimals: a rate is a fraction and rounding it to cents would
        // record every rate under 0.5% as zero.
        interestRate.add(Math.round(game.getDebtManager().getRate() * 10000.0) / 10000.0);
        revenue.add(round2(accounts.getTotalRevenue()));
        surplus.add(round2(accounts.getBalance()));

        jobs.add(people.getTotalJobs());
        workforce.add(people.getWorkforce());
        outOfWork.add(people.getUnemployed());
        population.add(people.getPopulation());

        PopulationCohorts pyramid = game.getCohorts();
        Migration flows = game.getMigration();
        births.add(Math.round(pyramid.getLastBirths()));
        deaths.add(Math.round(pyramid.getLastDeaths()));
        arrivals.add(Math.round(flows.getLastArrivals()));
        departures.add(Math.round(flows.getLastDepartures()));
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
        outbreak.add(round4(game.getHealth().getOutbreakSeverity()));

        landPrice.add(Math.round(game.getLandManager().getGroundUsdPerSqFt() * 1e6) / 1e6);
        foodPrice.add(round4(game.getSectors().retail().getFoodPrice()));
        materialsPrice.add(round4(game.getBuildingManager().getConstructionMaterialPrice()));
        orePrice.add(round4(game.getMarkets().get(Good.IRON).exportPrice()));

        /* ------------------------- the edge ------------------------- */
        ForeignAccounts abroad = game.getForeignAccounts();
        fxRate.add(round4(abroad.getRate()));
        fxParity.add(round4(abroad.getParity()));
        reservesUsd.add(round2(abroad.getReservesUsd()));
        foreignDebtUsd.add(round2(abroad.getForeignDebtUsd()));
        currentAccount.add(round2(abroad.currentAccount()));
        exportsAbroad.add(round2(abroad.getExports()));
        importsAbroad.add(round2(abroad.tradeImports()));

        /* --------------------- money and credit --------------------- */
        priceIndex.add(round4(game.getPriceIndex().getIndex()));
        expectedInflation.add(round4(game.getExpectations().getExpectedInflation()));
        credibility.add(round4(game.getExpectations().getCredibility()));
        businessDebt.add(round2(economy.getBusinessDebtManager().getTotalPrincipal()));

        Bank lender = game.getBank();
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

        double policy = game.getDebtManager().getPolicyRate();
        policyRate.add(round4(policy));
        bankPrime.add(round4(lender.prime(policy)));
        bankDepositRate.add(round4(lender.depositRate()));
        bankFees.add(round2(lender.feeIncome()));
        bankCapitalRatio.add(round4(lender.getWeightedBook() > 0 ? Math.min(10, lender.capitalRatio()) : 10));
        bankCapitalTarget.add(round4(lender.capitalTarget()));
        bankAllowance.add(round2(lender.getAllowance()));
        bankProvisions.add(round2(lender.provisions()));
        bankDividends.add(round2(lender.getDividendsPaid()));
        // Clamped at ten times, like the ratio: a month's income over a
        // sliver of equity is not a return anybody earns.
        bankReturnOnEquity.add(round4(Math.max(-10, Math.min(10, lender.returnOnEquity()))));
        // ...and the leverage ratio and its target (A7, 0.7.46), the ratio at the same ceiling.
        bankLeverageRatio.add(round4(Math.min(10, lender.leverageRatio())));
        bankLeverageTarget.add(round4(lender.leverageTarget()));

        CentralBank cb = game.getCentralBank();
        m0.add(round2(cb.m0()));
        m2.add(round2(game.getM2()));
        advancesToTreasury.add(round2(cb.getAdvancesToTreasury()));
        reserves.add(round2(cb.getReserves()));
        remittance.add(round2(cb.getRemitted()));

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
        // The month's gains, not the movement, which nets to nothing (A6, 0.7.46).
        graduates.add(round2(schools.gainedThisMonth()));
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
        healthPremiums.add(round2(accounts.getHealthPremiums()));
        studentGrants.add(round2(accounts.getStudentGrants()));
        foodAssistance.add(round2(game.getEconomyManager().getFoodAssistance()));
        fedByAssistance.add(round2(game.getHouseholdBalance().getFedByAssistance()));
        studentLoansOwed.add(round2(game.getHouseholdBalance().totalStudentDebt()));
        studentLoanInterest.add(round2(accounts.getStudentLoanInterest()));
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
        deathsElders.add(round2(pyramid.getDeaths(AgeBand.ELDER)));
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
            // ...to six significant figures (C5, 0.7.48): four places rounded a consolidated company to nothing.
            sharePrice.computeIfAbsent(company, k -> new ArrayList<>())
                    .add(roundSig(exchange.pricePerFoundingShare(c)));
            shareValue.computeIfAbsent(company, k -> new ArrayList<>())
                    .add(roundSig(exchange.fairPerFoundingShare(c)));
        }

        /* ------------------------- the sectors ------------------------- */
        for (Sector sector : game.getSectors().all()) {
            sectorNetIncome.computeIfAbsent(sector.key(), k -> new ArrayList<>())
                    .add(round2(sector.statement().netIncome));
            sectorWorkers.computeIfAbsent(sector.key(), k -> new ArrayList<>())
                    .add(round2(sector.getWorkers()));
        }

        /* ------------------ the new price model, read (0.7.45) ------------------ */
        PriceIndex basket = game.getPriceIndex();
        expectedLevel.add(round4(finite(game.getExpectations().getExpectedLevel())));
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) {
            indexLists().get(k).add(round4(finite(basket.getComponentLevel(k))));
            weightLists().get(k).add(round4(finite(basket.getWeight(k))));
        }
        basketLinkedAt.add((double) basket.getLinkedMonth());
        ham.citybuildersim.sectors.Retail shops = game.getSectors().retail();
        shelfPrice.add(round6(finite(shops.getStoreSellPrice())));
        shelfFloor.add(round6(finite(shops.getFloorPrice())));
        basketsAsked.add(round2(finite(shops.getDemandAtPrice())));
        basketsHanded.add((double) shops.getProductsSold());
        HouseholdBalance fed = game.getHouseholdBalance();
        hunger.add(round4(finite(fed.getHungerRate())));
        hungerPricedOut.add(round4(finite(fed.getHungerPricedOut())));
        householdsAssisted.add(round2(finite(fed.getHouseholdsAssisted())));
        mealMargin.add(round4(finite(game.getSectors().restaurants().getMargin())));
        mealTargetMargin.add(round4(finite(game.getSectors().restaurants().getTargetMargin())));
        luxuryMargin.add(round4(finite(game.getSectors().luxuryRetail().getMargin())));
        luxuryTargetMargin.add(round4(finite(game.getSectors().luxuryRetail().getTargetMargin())));

        /* --------------------- the city's fund (0.7.39) ---------------------
           Last, after the share prices: the warrants' value in fundValue()
           reads the bank's price history (Game.bankVolatility()), which has
           this month in it only from here. */
        fundValue.add(round2(game.fundValue()));
        fundPutIn.add(round2(game.getFund().getPutIn()));
        fundTakenOut.add(round2(game.getFund().getTakenOut()));

        // ...and a year past the newest MONTHLY_KEPT months folds (0.7.55).
        foldOldYears();
    }

    /* ==================================================================
       THE FOLD (0.7.55)

       A year folds once every month of it is older than the newest
       MONTHLY_KEPT, so the months kept never fall under MONTHLY_KEPT: the
       year a month of 6,001 starts to leave folds the month its last month
       leaves, and from then on one year a year. Incremental - recordMonth()
       asks after every month - and so a history is never folded twice and
       never refolded: an entry once a year stays that year.
       ================================================================== */

    /**
     * Folds every calendar year whose months are all older than the newest
     * MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT
     * months or fewer. Called at the end of recordMonth(); public for a
     * harness that builds a history by hand.
     */
    public void foldOldYears() {
        while (true) {
            int first = yearlyPoints();
            int monthly = month.size() - first;
            if (monthly <= MONTHLY_KEPT) return;
            int year = CityCalendar.yearOf(month.get(first));
            int end = first;
            while (end < month.size() && CityCalendar.yearOf(month.get(end)) == year) end++;
            if (monthly - (end - first) < MONTHLY_KEPT) return;
            foldYear(first, end);
        }
    }

    /** Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. */
    private void foldYear(int from, int to) {
        int axis = month.size();
        for (Map.Entry<String, List<? extends Number>> e : seriesByName().entrySet()) {
            foldSeries(e.getKey(), e.getValue(), axis, from, to);
        }
        int last = month.get(to - 1);
        month.subList(from, to).clear();
        month.add(from, last);
        foldedMonths.add(to - from);
    }

    /**
     * One series' months for axis entries [from, to), folded in place by its
     * rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a
     * series nobody has given a rule, as the year book folds it - the last.
     * A series that began inside the year folds the months it has; one that
     * began after it is not touched, and still lines up with the axis's end.
     * A count stays a whole number: a Long series' months are added as longs.
     */
    @SuppressWarnings("unchecked")
    private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to) {
        if (raw == null) return;
        int offset = axis - raw.size();
        int s = Math.max(0, from - offset), e = Math.min(raw.size(), to - offset);
        if (e <= s) return;
        YearBook.Kind kind = YearBook.kindOf(name);
        boolean whole = raw.get(s) instanceof Long;
        Number folded;
        if (kind == YearBook.Kind.FLOW) {
            if (whole) {
                long sum = 0;
                for (int i = s; i < e; i++) sum += raw.get(i).longValue();
                folded = sum;
            } else {
                double sum = 0;
                for (int i = s; i < e; i++) sum += raw.get(i).doubleValue();
                folded = sum;
            }
        } else if (kind == YearBook.Kind.RATE) {
            double sum = 0;
            int seen = 0;
            for (int i = s; i < e; i++) {
                double v = raw.get(i).doubleValue();
                if (Double.isNaN(v)) continue;
                sum += v;
                seen++;
            }
            double mean = seen > 0 ? sum / seen : raw.get(e - 1).doubleValue();
            folded = whole ? (Number) Math.round(mean) : (Number) mean;
        } else {
            folded = raw.get(e - 1);
        }
        List<Number> list = (List<Number>) raw;
        list.subList(s, e).clear();
        list.add(s, folded);
    }

    /**
     * Harnesses only (0.7.55): one more month on every series this history
     * has, each value asked of `value` by the series' name, then the fold
     * recordMonth() ends with - so a history can be grown past MONTHLY_KEPT
     * without a city behind it. A Long series takes the value rounded.
     */
    @SuppressWarnings("unchecked")
    void appendMonth(int m, java.util.function.ToDoubleFunction<String> value) {
        month.add(m);
        for (Map.Entry<String, List<? extends Number>> e : seriesByName().entrySet()) {
            List<Number> list = (List<Number>) e.getValue();
            double v = value.applyAsDouble(e.getKey());
            boolean whole = !list.isEmpty() ? list.get(0) instanceof Long : LONG_SERIES.contains(e.getKey());
            list.add(whole ? (Number) Math.round(v) : (Number) v);
        }
        foldOldYears();
    }

    /** The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. */
    static final java.util.Set<String> LONG_SERIES = java.util.Set.of("jobs", "workforce", "outOfWork", "population",
            "births", "deaths", "arrivals", "departures", "homes", "constructionCapacity");

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

    /** Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). */
    static final int SHARE_PRICE_DIGITS = 6;

    /** A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). */
    static double roundSig(double v) {
        if (v == 0 || !Double.isFinite(v)) return finite(v);
        return new BigDecimal(v).round(new MathContext(SHARE_PRICE_DIGITS)).doubleValue();
    }
    /** Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). */
    private static double round6(double v) { return Math.round(v * 1_000_000.0) / 1_000_000.0; }
    /** A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). */
    private static double finite(double v) { return Double.isFinite(v) ? v : 0; }

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
        // ...and how many months each folded year holds (0.7.55); none in an
        // older file, and never more than the axis has entries.
        foldedMonths = copy(loaded.foldedMonths);
        while (foldedMonths.size() > month.size()) foldedMonths.remove(foldedMonths.size() - 1);

        cash = copy(loaded.cash);
        gdp = copy(loaded.gdp);
        consumption = copy(loaded.consumption);
        investment = copy(loaded.investment);
        government = copy(loaded.government);
        netExports = copy(loaded.netExports);
        fundValue = copy(loaded.fundValue);
        fundPutIn = copy(loaded.fundPutIn);
        fundTakenOut = copy(loaded.fundTakenOut);
        expectedLevel = copy(loaded.expectedLevel);
        indexGroceries = copy(loaded.indexGroceries);
        indexRent = copy(loaded.indexRent);
        indexMeals = copy(loaded.indexMeals);
        indexLuxury = copy(loaded.indexLuxury);
        indexServices = copy(loaded.indexServices);
        weightGroceries = copy(loaded.weightGroceries);
        weightRent = copy(loaded.weightRent);
        weightMeals = copy(loaded.weightMeals);
        weightLuxury = copy(loaded.weightLuxury);
        weightServices = copy(loaded.weightServices);
        basketLinkedAt = copy(loaded.basketLinkedAt);
        shelfPrice = copy(loaded.shelfPrice);
        shelfFloor = copy(loaded.shelfFloor);
        basketsAsked = copy(loaded.basketsAsked);
        basketsHanded = copy(loaded.basketsHanded);
        hunger = copy(loaded.hunger);
        hungerPricedOut = copy(loaded.hungerPricedOut);
        householdsAssisted = copy(loaded.householdsAssisted);
        mealMargin = copy(loaded.mealMargin);
        mealTargetMargin = copy(loaded.mealTargetMargin);
        luxuryMargin = copy(loaded.luxuryMargin);
        luxuryTargetMargin = copy(loaded.luxuryTargetMargin);
        debt = copy(loaded.debt);
        interestRate = copy(loaded.interestRate);
        revenue = copy(loaded.revenue);
        surplus = copy(loaded.surplus);

        jobs = copy(loaded.jobs);
        workforce = copy(loaded.workforce);
        outOfWork = copy(loaded.outOfWork);
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
        outbreak = copy(loaded.outbreak);
        careCoverage = copy(loaded.careCoverage);

        landPrice = copy(loaded.landPrice);
        foodPrice = copy(loaded.foodPrice);
        materialsPrice = copy(loaded.materialsPrice);
        orePrice = copy(loaded.orePrice);

        fxRate = copy(loaded.fxRate);
        fxParity = copy(loaded.fxParity);
        reservesUsd = copy(loaded.reservesUsd);
        foreignDebtUsd = copy(loaded.foreignDebtUsd);
        currentAccount = copy(loaded.currentAccount);
        exportsAbroad = copy(loaded.exportsAbroad);
        importsAbroad = copy(loaded.importsAbroad);

        priceIndex = copy(loaded.priceIndex);
        expectedInflation = copy(loaded.expectedInflation);
        credibility = copy(loaded.credibility);
        businessDebt = copy(loaded.businessDebt);
        bankDeposits = copy(loaded.bankDeposits);
        bankLent = copy(loaded.bankLent);
        bankEquity = copy(loaded.bankEquity);
        bankWriteOffs = copy(loaded.bankWriteOffs);
        bankCapacity = copy(loaded.bankCapacity);
        bankStrain = copy(loaded.bankStrain);
        bankProfit = copy(loaded.bankProfit);
        bankBranches = copy(loaded.bankBranches);
        householdSavings = copy(loaded.householdSavings);
        policyRate = copy(loaded.policyRate);
        bankPrime = copy(loaded.bankPrime);
        bankDepositRate = copy(loaded.bankDepositRate);
        bankFees = copy(loaded.bankFees);
        bankCapitalRatio = copy(loaded.bankCapitalRatio);
        bankCapitalTarget = copy(loaded.bankCapitalTarget);
        bankAllowance = copy(loaded.bankAllowance);
        bankProvisions = copy(loaded.bankProvisions);
        bankDividends = copy(loaded.bankDividends);
        bankReturnOnEquity = copy(loaded.bankReturnOnEquity);
        bankLeverageRatio = copy(loaded.bankLeverageRatio);
        bankLeverageTarget = copy(loaded.bankLeverageTarget);

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
        healthPremiums = copy(loaded.healthPremiums);
        studentGrants = copy(loaded.studentGrants);
        foodAssistance = copy(loaded.foodAssistance);
        fedByAssistance = copy(loaded.fedByAssistance);
        studentLoansOwed = copy(loaded.studentLoansOwed);
        studentLoanInterest = copy(loaded.studentLoanInterest);
        m0 = copy(loaded.m0);
        m2 = copy(loaded.m2);
        advancesToTreasury = copy(loaded.advancesToTreasury);
        reserves = copy(loaded.reserves);
        remittance = copy(loaded.remittance);
        sickPastTwoMonths = copy(loaded.sickPastTwoMonths);
        diedOfIllness = copy(loaded.diedOfIllness);
        sickRecovery = copy(loaded.sickRecovery);
        householdsByShape = copyMap(loaded.householdsByShape);
        deathsBabies = copy(loaded.deathsBabies);
        deathsChildren = copy(loaded.deathsChildren);
        deathsTeens = copy(loaded.deathsTeens);
        deathsAdults = copy(loaded.deathsAdults);
        deathsSeniors = copy(loaded.deathsSeniors);
        deathsElders  = copy(loaded.deathsElders);
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

        sectorNetIncome = copyMap(loaded.sectorNetIncome);
        sectorWorkers = copyMap(loaded.sectorWorkers);
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

    /**
     * How many entries the axis has - a month each, and since 0.7.55 a year
     * each for the oldest yearlyPoints() of a history past MONTHLY_KEPT.
     * Every series is measured against it; monthsLived() is the months.
     */
    public int months() { return month.size(); }

    /** The axis: each entry's month, and a folded year's last month (0.7.55). */
    public List<Integer> getMonth() { return month; }

    /** How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. */
    public int yearlyPoints() { return foldedMonths == null ? 0 : foldedMonths.size(); }

    /** The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). */
    public int monthsIn(int i) {
        return i >= 0 && i < yearlyPoints() ? foldedMonths.get(i) : 1;
    }

    /** The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). */
    public int firstMonthOf(int i) {
        return month.get(i) - monthsIn(i) + 1;
    }

    /** How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. */
    public int monthsLived() {
        int n = month.size() - yearlyPoints();
        for (int i = 0; i < yearlyPoints(); i++) n += foldedMonths.get(i);
        return n;
    }

    /**
     * The entry `months` months before entry i (0.7.55): the month that far
     * back, or the folded year that holds it - so "a year before" a folded
     * year is the year before it, and before a month in the first year after
     * the fold, the folded year that month falls in. -1 when the history
     * does not reach that far. For a history that has never folded, exactly
     * i - months.
     */
    public int back(int i, int months) {
        if (i < 0 || i >= month.size() || months < 0) return -1;
        int want = month.get(i) - months;
        for (int j = i; j >= 0; j--) {
            if (firstMonthOf(j) <= want && want <= month.get(j)) return j;
            if (month.get(j) < want) return -1;
        }
        return -1;
    }

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
     *
     * A MONTH AT A TIME (0.7.55): a folded year's FLOW comes back as its
     * monthly average - the year's sum over monthsIn() - so every reader
     * that takes an entry for a month (a chart's line, a per-head figure, a
     * rolling year weighted by monthsIn()) reads one unit across the fold.
     * A level and a rate come back as stored. raw() is the stored figures.
     */
    public double[] aligned(String name) {
        double[] out = raw(name);
        int years = Math.min(yearlyPoints(), out.length);
        if (years > 0 && YearBook.kindOf(name) == YearBook.Kind.FLOW) {
            for (int i = 0; i < years; i++) out[i] /= monthsIn(i);
        }
        return out;
    }

    /** A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). */
    public double[] raw(String name) {
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

    /* ------------------- a series' record, counted (0.7.9) -------------------
     *
     * What the Bank tab's history page states under its charts - the months
     * the bank's capital stood under its target and under the minimum, and
     * its worst year of provisions - asked of the record rather than worked
     * out on the screen. Months a series was not recorded in count for
     * nothing, for aligned()'s reason.
     * --------------------------------------------------------------------- */

    /**
     * Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget).
     * A folded year counts its months when its figures stood under (0.7.55).
     */
    public int monthsUnder(String series, String line) {
        double[] a = aligned(series), b = aligned(line);
        int n = 0;
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i]) && a[i] < b[i]) n += monthsIn(i);
        }
        return n;
    }

    /** ...and under a fixed level: its months under the city's minimum. */
    public int monthsUnder(String series, double level) {
        double[] a = aligned(series);
        int n = 0;
        for (int i = 0; i < a.length; i++) if (!Double.isNaN(a[i]) && a[i] < level) n += monthsIn(i);
        return n;
    }

    /** Months a series was recorded in - a folded year's months each (0.7.55). */
    public int monthsRecorded(String series) {
        double[] a = aligned(series);
        int n = 0;
        for (int i = 0; i < a.length; i++) if (!Double.isNaN(a[i])) n += monthsIn(i);
        return n;
    }

    /** A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). */
    public double total(String series) {
        double sum = 0;
        for (double v : raw(series)) if (!Double.isNaN(v)) sum += v;
        return sum;
    }

    /**
     * How far a series has moved over the last `months` months, as a share of
     * where it stood then: the last recorded value over the one `months`
     * entries before it, less one (0.7.35: the Trade tab's rate this month and
     * over a year). NaN when either was not recorded or the old one is not
     * above nothing.
     */
    public double changeOver(String series, int months) {
        double[] a = aligned(series);
        int last = a.length - 1, then = back(last, months);
        if (months <= 0 || then < 0) return Double.NaN;
        if (Double.isNaN(a[last]) || Double.isNaN(a[then]) || !(a[then] > 0)) return Double.NaN;
        return a[last] / a[then] - 1;
    }

    /**
     * A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports).
     * A folded year inside the window adds its sum; one the window cuts, the
     * months of it inside, at its monthly average (0.7.55).
     */
    public double recentTotal(String series, int months) {
        double[] a = aligned(series);
        int from = a.length, covered = 0;
        while (from > 0 && covered < months) covered += monthsIn(--from);
        double sum = 0;
        for (int i = from; i < a.length; i++) {
            if (Double.isNaN(a[i])) continue;
            int in = i == from ? monthsIn(i) - Math.max(0, covered - months) : monthsIn(i);
            sum += in == 1 ? a[i] : a[i] * in;
        }
        return sum;
    }

    /**
     * A flow's worst year: the largest sum of any twelve months in a row since
     * it was first recorded - over fewer than twelve, what there is. Nothing
     * with nothing recorded.
     */
    public double worstYear(String series) {
        double[] a = raw(series);
        int from = yearlyPoints();
        // A folded year is a year already (0.7.55): the worst of them, then
        // the worst twelve months in a row among the months.
        if (from == 0) return worstYear(a);
        double[] tail = java.util.Arrays.copyOfRange(a, Math.min(from, a.length), a.length);
        boolean years = false, months = false;
        double folded = -Double.MAX_VALUE;
        for (int i = 0; i < Math.min(from, a.length); i++) {
            if (!Double.isNaN(a[i])) { folded = Math.max(folded, a[i]); years = true; }
        }
        for (double v : tail) if (!Double.isNaN(v)) { months = true; break; }
        if (!years) return worstYear(tail);
        return months ? Math.max(folded, worstYear(tail)) : folded;
    }

    /** worstYear()'s twelve months in a row, over a run of months. */
    private static double worstYear(double[] a) {
        int from = 0;
        while (from < a.length && Double.isNaN(a[from])) from++;
        if (from >= a.length) return 0;
        int window = 12;
        double sum = 0, worst = -Double.MAX_VALUE;
        for (int i = from; i < a.length; i++) {
            sum += Double.isNaN(a[i]) ? 0 : a[i];
            if (i - window >= from) sum -= Double.isNaN(a[i - window]) ? 0 : a[i - window];
            if (i - from + 1 >= window || i == a.length - 1) worst = Math.max(worst, sum);
        }
        return worst;
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

    /**
     * ...and a series of this history summed from its first recorded entry
     * (0.7.55): off the stored figures, so a folded year adds its sum - what
     * City History's running totals read.
     */
    public double[] runningTotal(String series) {
        return runningTotal(raw(series));
    }

    /** Every stored series, by the name the screen asks for. */
    public Map<String, List<? extends Number>> seriesByName() {
        Map<String, List<? extends Number>> map = new LinkedHashMap<>();
        map.put("cash", cash);
        map.put("gdp", gdp);
        map.put("consumption", consumption);
        map.put("investment", investment);
        map.put("government", government);
        map.put("netExports", netExports);
        map.put("fundValue", fundValue);
        map.put("fundPutIn", fundPutIn);
        map.put("fundTakenOut", fundTakenOut);
        map.put("debt", debt);
        map.put("interestRate", interestRate);
        map.put("revenue", revenue);
        map.put("surplus", surplus);
        map.put("jobs", jobs);
        map.put("workforce", workforce);
        map.put("outOfWork", outOfWork);
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
        map.put("outbreak", outbreak);
        map.put("landPrice", landPrice);
        map.put("foodPrice", foodPrice);
        map.put("materialsPrice", materialsPrice);
        map.put("orePrice", orePrice);

        map.put("fxRate", fxRate);
        map.put("fxParity", fxParity);
        map.put("reservesUsd", reservesUsd);
        map.put("foreignDebtUsd", foreignDebtUsd);
        map.put("currentAccount", currentAccount);
        map.put("exportsAbroad", exportsAbroad);
        map.put("importsAbroad", importsAbroad);

        map.put("priceIndex", priceIndex);
        map.put("expectedInflation", expectedInflation);
        map.put("credibility", credibility);
        // The new price model, read (0.7.45).
        map.put("expectedLevel", expectedLevel);
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) map.put(indexKey(k), indexLists().get(k));
        for (int k = 0; k < PriceIndex.COMPONENTS; k++) map.put(weightKey(k), weightLists().get(k));
        map.put("basketLinkedAt", basketLinkedAt);
        map.put("shelfPrice", shelfPrice);
        map.put("shelfFloor", shelfFloor);
        map.put("basketsAsked", basketsAsked);
        map.put("basketsHanded", basketsHanded);
        map.put("hunger", hunger);
        map.put("hungerPricedOut", hungerPricedOut);
        map.put("householdsAssisted", householdsAssisted);
        map.put("mealMargin", mealMargin);
        map.put("mealTargetMargin", mealTargetMargin);
        map.put("luxuryMargin", luxuryMargin);
        map.put("luxuryTargetMargin", luxuryTargetMargin);
        map.put("businessDebt", businessDebt);
        map.put("bankDeposits", bankDeposits);
        map.put("bankLent", bankLent);
        map.put("bankEquity", bankEquity);
        map.put("bankWriteOffs", bankWriteOffs);
        map.put("bankCapacity", bankCapacity);
        map.put("bankStrain", bankStrain);
        map.put("bankProfit", bankProfit);
        map.put("bankBranches", bankBranches);
        map.put("householdSavings", householdSavings);
        map.put("policyRate", policyRate);
        map.put("bankPrime", bankPrime);
        map.put("bankDepositRate", bankDepositRate);
        map.put("bankFees", bankFees);
        map.put("bankCapitalRatio", bankCapitalRatio);
        map.put("bankCapitalTarget", bankCapitalTarget);
        map.put("bankAllowance", bankAllowance);
        map.put("bankProvisions", bankProvisions);
        map.put("bankDividends", bankDividends);
        map.put("bankReturnOnEquity", bankReturnOnEquity);
        map.put("bankLeverageRatio", bankLeverageRatio);
        map.put("bankLeverageTarget", bankLeverageTarget);

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
        map.put("healthPremiums", healthPremiums);
        map.put("studentGrants", studentGrants);
        map.put("foodAssistance", foodAssistance);
        map.put("fedByAssistance", fedByAssistance);
        map.put("studentLoansOwed", studentLoansOwed);
        map.put("studentLoanInterest", studentLoanInterest);
        map.put("m0", m0);
        map.put("m2", m2);
        map.put("advancesToTreasury", advancesToTreasury);
        map.put("reserves", reserves);
        map.put("remittance", remittance);
        map.put("sickPastTwoMonths", sickPastTwoMonths);
        map.put("diedOfIllness", diedOfIllness);
        map.put("sickRecovery", sickRecovery);
        map.put("deathsBabies", deathsBabies);
        map.put("deathsChildren", deathsChildren);
        map.put("deathsTeens", deathsTeens);
        map.put("deathsAdults", deathsAdults);
        map.put("deathsSeniors", deathsSeniors);
        map.put("deathsElders", deathsElders);
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
        // ...and every sector, by its name (0.7.4).
        if (sectorNetIncome != null) {
            for (Map.Entry<String, List<Double>> e : sectorNetIncome.entrySet()) map.put(netIncomeKey(e.getKey()), e.getValue());
        }
        if (sectorWorkers != null) {
            for (Map.Entry<String, List<Double>> e : sectorWorkers.entrySet()) map.put(workersKey(e.getKey()), e.getValue());
        }
        return map;
    }

    /* The originals, still here because other code and the harnesses read them. */
    public List<Double> getCash()          { return cash; }
    public List<Double> getGdp()           { return gdp; }
    public List<Double> getDebt()          { return debt; }
    public List<Double> getInterestRate()  { return interestRate; }
    public List<Long> getJobs()        { return jobs; }
    public List<Long> getWorkforce()   { return workforce; }
    public List<Long> getOutOfWork()   { return outOfWork; }
    public List<Long> getPopulation()  { return population; }

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
                foodPrice, materialsPrice, orePrice);
        // NOT landPrice, since 0.7.6: it is the world's dollar price of ground,
        // which no reform reaches (LandMarket.redenominate()). The months an
        // older save recorded in local money stay as recorded, in the unit
        // they were recorded in - the year book's note says so.
        // GDP's four parts (0.7.6), money like the sum they make.
        scaleAll(scale, consumption, investment, government, netExports);
        // The city's fund (0.7.39): its worth and what went in and out, money.
        scaleAll(scale, fundValue, fundPutIn, fundTakenOut);
        // The shelf and its floor (0.7.45), money; the levels, weights, margins, shares and counts beside them are not.
        scaleAll(scale, shelfPrice, shelfFloor);

        /*
         * The same reform, applied to everything added since - and the list of
         * what is NOT here is the interesting half.
         *
         * reservesUsd and foreignDebtUsd are owed and held in somebody else's
         * money, which a domestic reform cannot reach. priceIndex is a ratio of
         * two baskets and a reform divides both. policyRate, bankPrime and
         * bankDepositRate are rates (bankPremium was, until 0.7.7), and so
         * are the bank's capital ratio, its target and its return on equity
         * (0.7.8), and its leverage ratio and that target (0.7.46); its
         * allowance, provisions and dividends are money. homes,
         * households, students, graduates, licences, unburied,
         * constructionCapacity and fedByAssistance (baskets, 0.7.43) are counts
         * of things, and landUse is a share -
         * none of them is money at all.
         */
        scaleAll(scale, fxRate, fxParity, currentAccount, exportsAbroad, importsAbroad,
                businessDebt, bankDeposits, bankLent, bankEquity, bankWriteOffs,
                bankCapacity, bankProfit,
                householdSavings, bankFees,
                bankAllowance, bankProvisions, bankDividends,
                taxWage, taxProperty, taxSales, taxBusiness, taxIndustrial,
                contributions, pensionBill, healthBill,
                rentPrice,
                eiPaid, eiPremiums, studentGrants, studentLoansOwed, foodAssistance,
                stolen, safetyBill, healthPremiums, studentLoanInterest,
                m0, m2, advancesToTreasury, reserves, remittance);

        // A share's price is money; how many shares there are is not.
        if (sharePrice != null) for (List<Double> s : sharePrice.values()) scaleAll(scale, s);
        if (shareValue != null) for (List<Double> s : shareValue.values()) scaleAll(scale, s);
        // A sector's net income is money; its workers are people (0.7.4).
        if (sectorNetIncome != null) for (List<Double> s : sectorNetIncome.values()) scaleAll(scale, s);
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
