package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * NEEDS YOU, measured: everything with a lever, each against its own line, in
 * the order a city is built - the list the left panel's Summary prints, the
 * header's "Needs you" chip counts, and the Build tab's overview and its
 * suggestions read (0.7.24).
 *
 * WHY. The list was measured inside the interface (SummaryScreen.watchAll(),
 * since 2026-09-14), and 0.7.24's Build overview needed the same verdicts -
 * which need in a category is worst, and how bad - for its rings and for
 * the orders it suggests (BuildAdvice). A second copy of the thresholds in
 * the Build tab would have been a second scoring, and the two would have
 * disagreed the first time either was tuned; a scoring the interface alone
 * can run is one no harness can hold. So the measuring moved here, verbatim
 * - every line, every threshold, every reading and the order they are read
 * in - and the panel maps each need to the screen that answers it (its Go).
 * The interface's words for a figure (people, money, a wait) are passed in
 * as Words, so the readings on the panel are the ones it printed before;
 * PLAIN is the same shapes without the toolkit, for a harness.
 *
 * One row is new: FALLS DUE, the bottom strip's red maturity, which left the
 * frame in 0.7.24 (see fallsDue()).
 *
 * Reads the city; changes nothing. Every figure is a getter the screens
 * already read.
 */
public final class CityNeeds {

    private CityNeeds() { }

    /** What a need is about - the measure the Build tab's advice and rings read. */
    public enum Kind {
        POWER, WATER, ROADS, CARE, DEAD, PLOTS, BASIC_SCHOOLS, HIGHER_SCHOOL, CRIME, CELLS,
        HOMES, GROUND, BUILDERS, TREASURY, BANK, BORROWING, FALLS_DUE, PENSIONS, WAGES, BUDGET
    }

    /** Where a need's fix is: the screen its row opens. */
    public enum Go {
        UTILITIES, ROADS, HEALTHCARE, EDUCATION, SAFETY, HOMES,
        LAND, BUILDERS, FINANCES, BANK, PENSIONS, WAGES, TAXES
    }

    /**
     * One thing being watched.
     *
     * @param level   0 fine, 1 near the line, 2 past it
     * @param near    how close to the yellow line, 0 to 1, and only meaningful
     *                at level 0 - it is what picks the "next to watch" line on
     *                a city with nothing wrong
     * @param value   the figure the level was read from
     * @param yellow  the line past which it is listed; red the line past
     *                which it is red; higherWorse which way "past" is. A flag
     *                carries 1 and 1, and its level is the flag's own; its
     *                value is the figure behind it where there is one (the
     *                unburied, the cash, the bank's strain), else 0.
     * @param care    the care type a CARE need is about, else NONE
     * @param school  the school a school need is about (the basic ladder's
     *                bottleneck for BASIC_SCHOOLS), else NONE
     * @param onSite  units on site of the buildings that answer it, for
     *                anybody's order; onSiteMonths the soonest of their waits
     * @param a       the reading's second figure where it has one: a higher
     *                school's seats, a network's supply; b its third, who
     *                would come, the demand
     */
    public record Need(String label, String reading, int level, double near,
                       Kind kind, Go go, CareType care, EducationType school,
                       double value, double yellow, double red, boolean higherWorse,
                       int onSite, double onSiteMonths, double a, double b) {

        /** A need the city answers with a building of its own (BuildAdvice): roads, care, schools, police, cells, power and water. */
        public boolean cityBuilds() {
            switch (kind) {
                case POWER: case WATER: case ROADS: case CARE: case DEAD: case PLOTS:
                case BASIC_SCHOOLS: case HIGHER_SCHOOL: case CRIME: case CELLS:
                    return true;
                default:
                    return false;
            }
        }

        Need withOnSite(String moreReading, int units, double months, int newLevel) {
            return new Need(label, reading + moreReading, newLevel, near, kind, go, care, school,
                    value, yellow, red, higherWorse, units, months, a, b);
        }
    }

    /**
     * How the interface writes a figure, passed in so the readings are its
     * own: a headcount, an amount of money in the model's thousands, a short
     * number for a narrow cell, and a build time.
     */
    public interface Words {
        String people(double count);
        String money(double thousands);
        String shortNumber(double value);
        String monthsWait(double months);
    }

    /** The same four shapes without the toolkit (Formats), for a harness and a log. */
    public static final Words PLAIN = new Words() {
        @Override public String people(double count)       { return Formats.INSTANCE.count(count); }
        @Override public String money(double thousands)    { return Formats.INSTANCE.amount(thousands); }
        @Override public String shortNumber(double value) {
            if (value >= 1_000_000) return String.format("%.1fM", value / 1_000_000);
            if (value >= 10_000)    return String.format("%.0fk", value / 1_000);
            if (value >= 1_000)     return String.format("%.1fk", value / 1_000);
            return String.format("%.0f", value);
        }
        @Override public String monthsWait(double months) {
            if (Double.isNaN(months)) return "stalled";
            return months < 1 ? "under a month" : "~" + Formats.INSTANCE.count(months) + " mo";
        }
    };

    /* =====================================================================
       THE LINES. Per condition, not one rule, because the same percentage
       means different things: a city at 90% of its burial capacity is fine
       and a city at 90% of its water is not. As they stood in watchAll().
       ===================================================================== */

    /** A network is listed from three-quarters of its capacity, red once it is over. */
    public static final double NETWORK_YELLOW = .75, NETWORK_RED = 1;

    /** General care is watched hardest: it moves the sick rate. */
    public static final double GENERAL_YELLOW = .80, GENERAL_RED = .50;

    /** Childcare and senior care kill at the ends of life; a young city legitimately has neither for a while. */
    public static final double OTHER_CARE_YELLOW = .70, OTHER_CARE_RED = .40;

    /** Burial plots are watched once fewer than this many months are left. */
    public static final double PLOTS_WATCHED = 120;
    /** ...listed under two years of plots, red under six months. */
    public static final double PLOTS_YELLOW = 24, PLOTS_RED = 6;

    /** The basic ladder's bottleneck, taught. */
    public static final double SCHOOLS_YELLOW = .90, SCHOOLS_RED = .60;

    /** A school above the ladder: who would come over its seats. */
    public static final double SEATS_YELLOW = 1.05, SEATS_RED = 2;

    /** A class's worth: fewer would-be students than this and a school is not a row (measured: 3 would-be law students in a city of 1,650). */
    public static final double SEATS_FLOOR = 25;

    /** Crime against Canada's rate. */
    public static final double CRIME_YELLOW = 1.2, CRIME_RED = 1.5;

    /** The caught, not held. */
    public static final double CELLS_YELLOW = 1, CELLS_RED = 25;

    /**
     * The sick rate, the share of the workforce off sick (0.7.28): the left
     * panel's OFF SICK lines (SummaryScreen's literals until then), here so
     * the Services screen colours the same figure by the same lines. Not a
     * NEEDS YOU row - OFF SICK is a symptom, with no lever of its own.
     */
    public static final double SICK_YELLOW = .06, SICK_RED = .12;

    /**
     * FALLS DUE (0.7.24): paper due within this many months is red, as the
     * bottom strip drew its maturity chip until it left the frame
     * (its maturity chip, FinancesScreen's until 0.7.32 and its urgency() since: gap <= 3
     * red, <= FALLS_DUE_SOON_MONTHS amber). The strip's
     * red rule only, as the brief asked.
     */
    public static final int FALLS_DUE_MONTHS = 3;

    /**
     * ...and paper due within this many months is amber: the strip's other
     * rule (gap <= 12), named in 0.7.32 so the Finances tab's NEXT DUE, its
     * book and its strip colour a maturity by one line. Not a NEEDS YOU row:
     * a year off is something to see coming, not something to do.
     */
    public static final int FALLS_DUE_SOON_MONTHS = 12;

    /**
     * DEBT SERVICE, A SHARE OF THE TAKE (0.7.32): what the city pays its
     * lenders against what it collects is comfortable under SERVICE_FELT,
     * felt from there, and constrained past SERVICE_CONSTRAINED - the bands
     * the Finances tab drew as literals (its SERVICE cell, its gauge and its
     * sentence) until 0.7.32, named so the three read one line. Not a NEEDS
     * YOU row: the market prices the city on its debt, and BORROWING is that.
     */
    public static final double SERVICE_FELT = .12, SERVICE_CONSTRAINED = .25;

    /**
     * A WALL ON THE LADDER (0.7.32): a calendar year whose payments - coupons
     * and principal - pass this share of a year of revenue is one a city
     * meets by refinancing before it arrives. The Finances tab's ladder page
     * raised its red alert at it (a literal until then); its hub's heaviest
     * year is tagged red past it. Not a NEEDS YOU row: FALLS DUE is the
     * maturity a player has to act on.
     */
    public static final double YEAR_WALL = .5;

    /** A debt-service share's band (0.7.32): 0 comfortable, 1 felt, 2 constrained; a share that is not a number is comfortable. */
    public static int serviceLevel(double share) {
        if (!(share > SERVICE_FELT)) return 0;
        return share > SERVICE_CONSTRAINED ? 2 : 1;
    }

    /* =====================================================================
       THE LIST
       ===================================================================== */

    /**
     * Everything with a lever, measured against its own line, in the order
     * it is read - the fine ones too, because "next to watch" on a healthy
     * city is the nearest of them. NEEDS YOU is biting(measure(...)).
     */
    public static List<Need> measure(Game game, Words w) {

        List<Need> out = new ArrayList<>();

        EconomyManager economy = game.getEconomyManager();
        UtilitiesHandler utilities = game.getServicesManager().getUtilitiesHandler();
        InfrastructureManager roads = game.getInfrastructureManager();
        Healthcare care = game.getHealthcare();
        Education schools = game.getEducation();
        Crime crime = game.getCrime();
        FamilyModel families = game.getFamilies();
        Bank bank = game.getBank();
        PopulationCohorts cohorts = game.getCohorts();
        double[] staffing = game.getPopulationManager().getJobFillRate();
        int population = game.getPopulationManager().getPopulation();

        /* ---------------------------- the networks ---------------------------- */
        int at = out.size();
        network(out, "POWER", Kind.POWER, utilities.getConsumption(), utilities.getProduction(),
                utilities.getEnergyRatio());
        onTheWay(game, w, out, at, t -> t.getCategory() == BuildingType.ELECTRICITY);
        at = out.size();
        network(out, "WATER", Kind.WATER, utilities.getWaterConsumption(),
                utilities.getWaterProduction(), utilities.getWaterRatio());
        onTheWay(game, w, out, at, t -> t.getCategory() == BuildingType.WATER);

        // Its own constants: STRAINED is .85 and free flow ends at .90. Read as
        // the pair every screen writes the road in (0.7.29): how full, and the
        // flow that leaves - "162% full · 56% flow".
        double traffic = roads.getUtilisation();
        over(out, "ROADS", String.format("%.0f%% full · %.0f%% flow", traffic * 100, roads.getThroughputRatio() * 100),
                traffic, InfrastructureManager.STRAINED, InfrastructureManager.FREE_FLOW,
                Kind.ROADS, Go.ROADS, CareType.NONE, EducationType.NONE, 0, 0);
        onTheWay(game, w, out, t -> t.getCategory() == BuildingType.INFRASTRUCTURE);

        /* ------------------------------- the care ------------------------------- */
        double general = careCover(game, CareType.GENERAL, cohorts, staffing);
        under(out, "GENERAL CARE", String.format("%.0f%% covered", general * 100),
                general, GENERAL_YELLOW, GENERAL_RED,
                Kind.CARE, Go.HEALTHCARE, CareType.GENERAL, EducationType.NONE, 0, 0);
        onTheWay(game, w, out, t -> t.getCare() == CareType.GENERAL);

        double childcare = careCover(game, CareType.CHILDCARE, cohorts, staffing);
        under(out, "CHILDCARE", String.format("%.0f%% covered", childcare * 100),
                childcare, OTHER_CARE_YELLOW, OTHER_CARE_RED,
                Kind.CARE, Go.HEALTHCARE, CareType.CHILDCARE, EducationType.NONE, 0, 0);
        onTheWay(game, w, out, t -> t.getCare() == CareType.CHILDCARE);

        double senior = careCover(game, CareType.SENIOR, cohorts, staffing);
        under(out, "SENIOR CARE", String.format("%.0f%% covered", senior * 100),
                senior, OTHER_CARE_YELLOW, OTHER_CARE_RED,
                Kind.CARE, Go.HEALTHCARE, CareType.SENIOR, EducationType.NONE, 0, 0);
        onTheWay(game, w, out, t -> t.getCare() == CareType.SENIOR);

        // The dead are a STOCK: a backlog does not clear itself and the plots do
        // not come back, so this one is red the moment anybody is waiting.
        double unburied = care.getUnburied();
        flag(out, "THE DEAD", unburied > 0 ? w.people(unburied) + " unburied" : "all dealt with",
                unburied > 0, unburied > 0, Kind.DEAD, Go.HEALTHCARE, unburied);
        onTheWay(game, w, out, t -> t.getCare() == CareType.BURIAL || t.getCare() == CareType.CREMATION);

        /*
         * MEASURED: with nobody dying this returns Double.MAX_VALUE - not
         * infinity, so isFinite() lets it through - and a founding city holds
         * 2,500 plots, which reads as "6500 months left" for three centuries.
         * A decade of headroom is not news; the row appears when it stops being
         * true.
         */
        double plots = game.getBuildingManager().getCareCapacity(CareType.BURIAL);
        double monthsLeft = care.monthsOfPlotsLeft(plots);
        if (monthsLeft < PLOTS_WATCHED) {
            under(out, "BURIAL PLOTS", String.format("%.0f months left", monthsLeft),
                    monthsLeft, PLOTS_YELLOW, PLOTS_RED,
                    Kind.PLOTS, Go.HEALTHCARE, CareType.BURIAL, EducationType.NONE, 0, 0);
            onTheWay(game, w, out, t -> t.getCare() == CareType.BURIAL);
        }

        /* ------------------------------ the schools ------------------------------ */
        /*
         * basicCoverage() IS the bottleneck's coverage - it returns
         * coverage[basicBottleneck()], the minimum of the three rungs - and the
         * row says which one (Jerus: "just the schools one, it doesnt tell me
         * which").
         */
        double basic = schools.basicCoverage();
        if (population > 0) {
            EducationType bottleneck = schools.basicBottleneck();
            String thin = bottleneck.getLabel().toLowerCase();
            under(out, "SCHOOLS", String.format("%s %.0f%% taught", thin, basic * 100),
                    basic, SCHOOLS_YELLOW, SCHOOLS_RED,
                    Kind.BASIC_SCHOOLS, Go.EDUCATION, CareType.NONE, bottleneck, 0, 0);
            onTheWay(game, w, out, t -> t.getTeaches() == bottleneck);
        }

        /* ------------------------ and the schools above them ------------------------ */
        seatsWanted(game, w, out);

        /* ------------------------------- the police ------------------------------- */
        double vsCanada = crime.getRateVsCanada();
        over(out, "CRIME", String.format("%.1fx Canada's", vsCanada),
                vsCanada, CRIME_YELLOW, CRIME_RED,
                Kind.CRIME, Go.SAFETY, CareType.NONE, EducationType.NONE, 0, 0);
        onTheWay(game, w, out, t -> t.getSafety() == SafetyType.POLICE);

        double unheld = crime.getNotHeld();
        over(out, "CELLS", unheld >= 1 ? w.people(unheld) + " caught, not held" : "enough for the caught",
                unheld, CELLS_YELLOW, CELLS_RED,
                Kind.CELLS, Go.SAFETY, CareType.NONE, EducationType.NONE, 0, 0);
        onTheWay(game, w, out, t -> t.getSafety() == SafetyType.PRISON);

        /* ------------------------------- the housing ------------------------------- */
        double unplaced = families.getStillUnplaced();
        over(out, "HOMES", unplaced >= .5 ? w.people(unplaced) + " with nowhere to live" : "everybody housed",
                unplaced, .5, 25, Kind.HOMES, Go.HOMES, CareType.NONE, EducationType.NONE, 0, 0);
        onTheWay(game, w, out, t -> t.getCategory() == BuildingType.RESIDENTIAL);

        /* -------------------------------- the ground -------------------------------- */
        out.add(ground(game, w));

        flag(out, "BUILDERS", game.isConstructionShedding() ? "being laid off" : "in work",
                game.isConstructionShedding(), false, Kind.BUILDERS, Go.BUILDERS, 0);

        /* -------------------------------- the money -------------------------------- */
        double cash = game.getCash();
        double spending = Math.max(1, taxRaised(game));
        /*
         * A ROW THAT IS LISTED SAYS WHY (0.7.24, after the PC check): it read
         * "in hand" in amber, which looks like "fine". The line is a month's
         * tax (taxRaised(), the Policy tab's TAX A MONTH - what this method
         * scales the budget's rows by and calls spending), so the amber words
         * name it and both figures.
         */
        flag(out, "TREASURY", cash < 0 ? "overdrawn"
                        : cash < spending ? w.money(cash) + ", under a month's tax (" + w.money(spending) + ")"
                        : "in hand",
                cash < spending, cash < 0, Kind.TREASURY, Go.FINANCES, cash);

        // Until 0.7.7 the warning was the strain premium on every rate; with the
        // premium gone, a bank past its capacity is the thing worth a glance.
        flag(out, "THE BANK", bank.isInsolvent() ? "failed"
                        : bank.getBranches() <= 0 ? "there is none"
                        : bank.strain() > 1
                                ? String.format("%.0f%% lent", bank.strain() * 100)
                                : "lending",
                bank.isInsolvent() || bank.getBranches() <= 0 || bank.strain() > 1,
                bank.isInsolvent() || bank.getBranches() <= 0, Kind.BANK, Go.BANK, bank.strain());
        // The model counts its branches by this name (Game: bank.openBranches(...countByName(...))).
        onTheWay(game, w, out, t -> "Commercial Bank".equals(t.getName()));

        flag(out, "BORROWING", game.getDebtManager().atCeiling()
                        ? "priced out of the market" : "the market is open",
                game.getDebtManager().atCeiling(), true, Kind.BORROWING, Go.FINANCES, 0);

        fallsDue(game, w, out);

        /* -------------------------------- the promises -------------------------------- */
        /*
         * MEASURED: the gap grows with the pensioner count in every city; an
         * unfunded promise the city is paying without noticing is not this
         * list's business, one it cannot pay is - so it is gated on the budget.
         */
        double gap = economy.getPensionShortfall();
        double balanceNow = economy.getNationalAccounts().getBalance();
        over(out, "PENSIONS", gap > 0 ? w.money(gap) + " short a month" : "funded",
                balanceNow < 0 ? gap / spending : 0, .05, .20,
                Kind.PENSIONS, Go.PENSIONS, CareType.NONE, EducationType.NONE, gap, 0);

        // MEASURED: the unskilled band sits on the floor in month one of every
        // city and again whenever the city stalls. One band pinned is the
        // minimum wage doing its job; three of four is the ladder collapsing.
        int pinned = population > 0 ? pinnedBands(game) : 0;
        over(out, "WAGES", pinned > 0
                        ? pinned + (pinned == 1 ? " band" : " bands") + " pinned to the floor"
                        : "no band is pinned",
                pinned, 2, 4, Kind.WAGES, Go.WAGES, CareType.NONE, EducationType.NONE, 0, 0);

        double balance = balanceNow;
        over(out, "THE BUDGET", balance < 0 ? w.money(-balance) + " short a month" : "in surplus",
                balance < 0 ? -balance / spending : 0, .05, .20,
                Kind.BUDGET, Go.TAXES, CareType.NONE, EducationType.NONE, balance, 0);

        return out;
    }

    /** Free ground under which NEEDS YOU lists the GROUND row: a block, 100,000 sq ft. */
    public static final double GROUND_YELLOW = LandManager.BLOCK_SQ_FT;

    /**
     * The GROUND row on its own (0.7.26), as measure() lists it: what the
     * land office's GROUND FREE, Build's LAND FREE and the left panel's land
     * colour themselves by, so the three and NEEDS YOU agree - none of them
     * reads the ground used, which sits at 95-100% for centuries with
     * nothing wrong.
     *
     * GROUND THE CITY OWNS AND HAS NOT BUILT ON - not
     * isPrivateInvestmentLandLocked(), which probed true at month 12 with
     * 1,974,000 sq ft free and never cleared. Listed at a block free or less,
     * red at none.
     */
    public static Need ground(Game game, Words w) {
        double free = game.getLandManager().getAvailableSqFt();
        List<Need> one = new ArrayList<>();
        under(one, "GROUND TO BUILD ON",
                free > 0 ? w.shortNumber(free) + " sq ft free" : "none - nobody can break ground",
                free, GROUND_YELLOW, 0, Kind.GROUND, Go.LAND, CareType.NONE, EducationType.NONE, 0, 0);
        return one.get(0);
    }

    /**
     * NEEDS YOU: what is near a line or past one, red above yellow - and
     * inside a tier the order they were measured in, which is the order a
     * city is built. A list that re-sorts itself every month is a list nobody
     * can learn the shape of.
     */
    public static List<Need> biting(List<Need> all) {
        List<Need> biting = new ArrayList<>();
        for (Need n : all) if (n.level() > 0) biting.add(n);
        biting.sort((x, y) -> Integer.compare(y.level(), x.level()));
        return biting;
    }

    /** NEEDS YOU for this city, in the panel's order. */
    public static List<Need> needsYou(Game game) {
        return biting(measure(game, PLAIN));
    }

    /**
     * The worst need of those a screen answers: the highest level, and of
     * equals the first in NEEDS YOU's order - the row the panel would list
     * first. Null when none of them is near its line.
     */
    public static Need worst(List<Need> all, Go go) {
        for (Need n : biting(all)) if (n.go() == go) return n;
        return null;
    }

    /* =====================================================================
       THE PIECES
       ===================================================================== */

    /** How much of the people who need a kind of care the staffed beds could take: the panel's coverage, Game.careCoverage()'s. */
    public static double careCover(Game game, CareType care, PopulationCohorts cohorts, double[] staffing) {
        return Health.coverageOf(
                game.getBuildingManager().getStaffedCareCapacity(care, staffing),
                care.populationServed(cohorts));
    }

    /** What the city raised in tax last month: profit, sales, wages and property (the Policy tab's TAX A MONTH). */
    public static double taxRaised(Game game) {
        NationalAccounts na = game.getEconomyManager().getNationalAccounts();
        return na.getTaxBusiness() + na.getTaxIndustrial() + na.getTaxSales()
                + na.getTaxWage() + na.getPropertyTax();
    }

    /** Wage bands pinned to the minimum wage with people spare in them. */
    public static int pinnedBands(Game game) {
        LabourMarket labour = game.getLabourMarket();
        PopulationManager people = game.getPopulationManager();
        int pinned = 0;
        for (WageBand band : WageBand.values()) {
            if (labour.isPinned(band) && people.surplusInBand(band) > 0) pinned++;
        }
        return pinned;
    }

    /**
     * A figure's level against two lines, as a row is struck - over() when
     * higher is worse, under() when lower is (0.7.28): for a screen that
     * colours a figure no row lists (the Services screen's OFF SICK) by the
     * same rule, so the two cannot drift.
     */
    public static int level(double value, double yellow, double red, boolean higherWorse) {
        return higherWorse ? (value >= red ? 2 : value >= yellow ? 1 : 0)
                           : (value <= red ? 2 : value <= yellow ? 1 : 0);
    }

    /** Higher is worse. */
    static void over(List<Need> out, String label, String reading, double value, double yellow, double red,
                     Kind kind, Go go, CareType care, EducationType school, double a, double b) {
        int level = value >= red ? 2 : value >= yellow ? 1 : 0;
        out.add(new Need(label, reading, level,
                yellow <= 0 ? 1 : Math.max(0, Math.min(1, value / yellow)),
                kind, go, care, school, value, yellow, red, true, 0, Double.NaN, a, b));
    }

    /** Lower is worse. */
    static void under(List<Need> out, String label, String reading, double value, double yellow, double red,
                      Kind kind, Go go, CareType care, EducationType school, double a, double b) {
        int level = value <= red ? 2 : value <= yellow ? 1 : 0;
        out.add(new Need(label, reading, level,
                value <= 0 ? 1 : Math.max(0, Math.min(1, yellow / value)),
                kind, go, care, school, value, yellow, red, false, 0, Double.NaN, a, b));
    }

    /** A thing that is simply true or not. */
    static void flag(List<Need> out, String label, String reading, boolean bad, boolean severe,
                     Kind kind, Go go, double value) {
        out.add(new Need(label, reading, bad ? (severe ? 2 : 1) : 0, bad ? 1 : 0,
                kind, go, CareType.NONE, EducationType.NONE, value, 1, 1, true, 0, Double.NaN, 0, 0));
    }

    /**
     * ...AND WHAT IS ALREADY ON THE WAY (0.7.20). The need at `at`, if there
     * is one, says what of the buildings that answer it is on site, for
     * anybody's order - "1 on the way, ~8 mo", the soonest of them by the
     * wait the quote reads (Game.onSiteMonths()) - and a red one falls to
     * amber: the city has done something about it, and it is waiting on the
     * builders.
     */
    static void onTheWay(Game game, Words w, List<Need> out, int at,
                         java.util.function.Predicate<BuildingsTemplate> serves) {
        if (at < 0 || at >= out.size()) return;
        int units = 0;
        double soonest = Double.NaN;
        for (BuildingsStacks site : game.getBuildingManager().getStacksUnderConstruction()) {
            if (!serves.test(site.getBuilding())) continue;
            units += site.getUnderConstruction();
            double months = game.onSiteMonths(site.getBuilding());
            if (!Double.isNaN(months) && !(months >= soonest)) soonest = months;
        }
        if (units <= 0) return;
        Need n = out.get(at);
        out.set(at, n.withOnSite(" · " + Formats.INSTANCE.count(units) + " on the way, " + w.monthsWait(soonest),
                units, soonest, Math.min(n.level(), 1)));
    }

    /** The same, for the need just measured. */
    static void onTheWay(Game game, Words w, List<Need> out,
                         java.util.function.Predicate<BuildingsTemplate> serves) {
        onTheWay(game, w, out, out.size() - 1, serves);
    }

    /**
     * One network: how much of its capacity is spoken for, and whether it is
     * still meeting demand. HOW FULL, NOT HOW SHORT: the ratio sits at 1.00
     * until the city is already throttled, so the reading is the LOAD, on the
     * .75 the dashboard's RESOURCES line uses, red once the ratio breaks.
     *
     * @param ratio min(supply/demand, 1) - under one the city is being
     *              throttled, and the load figure has stopped being the news.
     */
    static void network(List<Need> out, String label, Kind kind,
                        double demand, double supply, double ratio) {
        if (supply <= 0) {
            // Nothing built: a city drawing nothing is not short of anything;
            // one drawing something with no plant is very short indeed.
            if (demand > 0) {
                out.add(new Need(label, "nothing supplying it", 2, 1, kind, Go.UTILITIES,
                        CareType.NONE, EducationType.NONE, Double.POSITIVE_INFINITY,
                        NETWORK_YELLOW, NETWORK_RED, true, 0, Double.NaN, supply, demand));
            }
            return;
        }
        double load = demand / supply;
        over(out, label, ratio < .99
                        ? String.format("only %.0f%% supplied", ratio * 100)
                        : String.format("%.0f%% of capacity", load * 100),
                load, NETWORK_YELLOW, NETWORK_RED, kind, Go.UTILITIES, CareType.NONE, EducationType.NONE,
                supply, demand);
    }

    /**
     * SEATS AGAINST WHO WOULD COME, for the schools above the basic ladder:
     * what the schools hold, and the student body this city would sustain if
     * seats were free - intake demand times the course - so a bigger second
     * number means another building fills. One row per school, and none below
     * a class's worth (SEATS_FLOOR).
     */
    static void seatsWanted(Game game, Words w, List<Need> out) {
        Education schools = game.getEducation();
        PopulationManager pm = game.getPopulationManager();
        LabourMarket market = game.getLabourMarket();
        double[] seatsBy = game.getBuildingManager().getStaffedEducationPlaces(pm.getJobFillRate());

        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE || type.isBasic()) continue;
            double couldHold = wouldCome(game, type);
            if (couldHold < SEATS_FLOOR) continue;
            double seats = seatsBy[type.ordinal()];
            over(out, type.getLabel().toUpperCase(),
                    String.format("%s seats, %s would come", w.people(seats), w.people(couldHold)),
                    couldHold / Math.max(seats, 1), SEATS_YELLOW, SEATS_RED,
                    Kind.HIGHER_SCHOOL, Go.EDUCATION, CareType.NONE, type, seats, couldHold);
            onTheWay(game, w, out, t -> t.getTeaches() == type);
        }
    }

    /** The student body a school above the ladder would hold if seats were free: eligible x willing x the enrolment rate, times the course. */
    public static double wouldCome(Game game, EducationType type) {
        if (type == EducationType.NONE || type.isBasic()) return 0;
        Education schools = game.getEducation();
        double wanted = schools.eligibleFor(type, game.getPopulationManager())
                * schools.willingShare(type, game.getLabourMarket()) * Education.ENROLMENT_RATE;
        return wanted * type.months();
    }

    /**
     * FALLS DUE (0.7.24). The bottom strip coloured its maturities red at
     * three months or less (the strip's maturity chip, until the strip
     * left the frame for the Finances hub); that rule is this row, at the
     * same threshold, pointing to Finances. The soonest of the city's paper,
     * and what of it falls due inside the window. Nothing listed while none
     * does.
     */
    static void fallsDue(Game game, Words w, List<Need> out) {
        DebtManager ledger = game.getDebtManager();
        int month = game.getMonth();
        int soonest = Integer.MAX_VALUE;
        double dueSoon = 0;
        for (Debt debt : ledger.getDebt()) {
            int gap = debt.getMaturityMonth() - month;
            soonest = Math.min(soonest, gap);
            if (gap <= FALLS_DUE_MONTHS) dueSoon += debt.getOustandingPrincipal();
        }
        if (soonest == Integer.MAX_VALUE) return;
        boolean due = soonest <= FALLS_DUE_MONTHS;
        String when = soonest <= 0 ? "this month" : soonest == 1 ? "in 1 mo" : "in " + soonest + " mo";
        out.add(new Need("FALLS DUE", due ? w.money(dueSoon) + " " + when
                        + (game.getRollover().getMode() == Rollover.Mode.MANUAL ? ", by hand" : ", the rollover's")
                        : "nothing inside " + FALLS_DUE_MONTHS + " months",
                due ? 2 : 0, due ? 1 : 0, Kind.FALLS_DUE, Go.FINANCES, CareType.NONE, EducationType.NONE,
                soonest, FALLS_DUE_MONTHS, FALLS_DUE_MONTHS, false, 0, Double.NaN, dueSoon, 0));
    }
}
