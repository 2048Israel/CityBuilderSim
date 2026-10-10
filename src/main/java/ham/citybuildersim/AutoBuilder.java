package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Automatic building (0.7.73): the city's own works ordered for the player
 * once a month - power, water, the roads and their lines, care, schools and
 * safety - each kept ahead of its demand with the player's spare margin past
 * it, by the build advice's own ranking and count, paid out of the cash and,
 * when the cash runs out, on the funding page's bond no further than the
 * player's debt limit - since 0.7.81 the city's debt over a year of its GDP,
 * over which it builds nothing, or with "Build from cash anyway" on, only
 * what the cash pays for.
 *
 * WHY. Jerus: "an automatic build and acquire debt button for basically
 * automatic building, with a required slack button that you add, aka
 * maintain say 15% surplus service of everything ... right in the build
 * menu, and on/off, so that one can focus on other things." His decisions
 * (2026-10-08): it builds services and infrastructure only - power, water,
 * roads, transit, schools, health, childcare and safety; not land, not
 * mines, wells or industry - and since 0.7.77 it buys the ground its own
 * orders need (THE GROUND ITS ORDERS NEED, below). A slider for the debt
 * limit - until 0.7.81 debt payments at most 15% of the city's revenue, and
 * since then the city's debt over a year of GDP (THE DEBT LIMIT, A SHARE OF
 * GDP, below) - and it borrows on the funding page's bonds only when the
 * cash runs out. A slider for the spare
 * margin, 15% by default, held for every service. On and off in the Build
 * menu, off by default. Until now only the test player (LongPlaytest) built
 * a city by itself, by rules of its own; this is the player's, in the game,
 * and it builds what the Build overview advises.
 *
 * FIXED, 0.7.101 (batch P2; Jerus's decisions A4 to A6 of 2026-10-10, "Fix
 * it"): the auto-built playtest's seeds 0 to 7 stalled or collapsed in five
 * cities of eight (runs/fixP2-notes.md). It now chooses by the long run - the
 * advice's figure is each building's order over its life for what it will
 * serve (BuildAdvice, A BUILDING OVER ITS LIFE); builds the first of a kind
 * where there is none, from the cash (kept(), pays()); is held by the debt
 * limit alone - the revenue it read the budget against is gone (THE BUDGET,
 * GONE), and the limit counts everything the city owes over the output it
 * has recorded (THE DEBT LIMIT); walks down
 * the advice's ranking past a building the money or the ground cannot pay
 * for one of (step()); buys only an order's own ground (lacks()); and its
 * limit is 240% of a year's GDP by default, the dial to 600%. The walk and
 * the limit took all eight seeds past 1.5M people; first buildings where
 * there are none and no revenue check, together, stall seven at 2k-37k -
 * staffed services a town cannot run, held only by the limit (star P2-3).
 *
 * THE MONTH (pass()), the first thing Game.nextMonth() does - between the
 * presses, where the player's own Build lands, on the city the last press
 * left and at its prices:
 *   - THE MEASURES (remit()): every one the city's five Build categories
 *     open on (BuildAdvice.measuresOf()) and the burial plots, NEEDS YOU's
 *     listed ones first in its order, the rest in the rings' order. Transit
 *     is not one of its own - it has no line, and the road's candidates are
 *     the three roads and the three lines alike (BuildAdvice, A ROAD OVER
 *     ITS LIFE), so a bus is built where it keeps the road ahead for less
 *     over its life. A school above the ladder only where NEEDS YOU lists
 *     it (kept()); a first school down the ladder, police station or prison
 *     wherever the figure is short, however far past the need one goes, and
 *     paid from the cash alone (0.7.101; it waited for half its worth until
 *     then).
 *   - THE ORDER: the advice's own card for the measure at the player's
 *     margin (BuildAdvice.suggestFor(..., slack)) - nothing while what
 *     stands and what is on site keep it ahead of the demand it will have
 *     once an order now opens, with the margin past it; otherwise the
 *     building the Build tab ranks first and the count that keeps it ahead.
 *   - CUT, IN TURN: to THE BUILDERS - no more than opens inside
 *     BusinessInvestment.MAX_ORDER_MONTHS, the businesses' own rule for an
 *     order the queue can deliver, and one when none would and nothing for
 *     the measure is on site, so a slow plant is not put off for ever; and
 *     to THE GROUND AND THE MONEY together (0.7.77) - the ground the order
 *     lacks past what is free bought as Build's land shortcut buys it
 *     (groundFor()), and the ground and the order paid for out of the cash
 *     over a month's tax (reserve()), then for the rest the funding page's
 *     bond (Game.handleLongBondForCash(), BUILD_BOND_YEARS), only as large as
 *     keeps the city's debt within the limit (canPay()) - and with the debt
 *     already over it, nothing at all, or with "Build from cash anyway" on,
 *     only what the cash pays for. Where not one is paid for, the next
 *     building in the advice's ranking (0.7.101), and held only when none is.
 *     (From 0.7.73 to 0.7.100 THE BUDGET cut a staffed building to what a
 *     year's revenue left.)
 *   - PLACED through Build's own path (Game.buildStack(); Game.paveRoads()
 *     when the advice's road is a paving), its ground first through the
 *     land office's (Game.buyLandParcel()), in its log with why, and the
 *     game's log. What held a service back - the builders, no ground to be
 *     had, the limit, or nothing the city could build - is the inbox's
 *     notice (Inbox, "autobuild"), a line a service; the ground it bought,
 *     and why, another ("autobuild-land").
 *
 * THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6; ratio()): the city's
 * debt - its bonds and bills, the left panel's Debt/GDP - over a year of
 * its output, read before every order; since 0.7.101 the only thing that
 * holds the money back. From 0.7.73 to 0.7.80 the limit was debt payments
 * as a share of the revenue the city can count on (the Finances tab's debt
 * service gauge, its leading mark).
 *
 * THE PLAYER'S SETTING AT WORK, NOT A DECISION: like the rollover's issues
 * (Game.issueForRollover()), what it orders and borrows is not in the
 * decision log; turning it on or off and moving a slider are.
 *
 * Saved under one key (State); an older save has none and loads with it
 * off. AutoBuildCheck holds it.
 */
public final class AutoBuilder {

    /* =====================================================================
       THE SETTINGS
       ===================================================================== */

    /** The spare margin a new city is given (Jerus, 2026-10-08: "maintain say 15% surplus service of everything"). */
    public static final double DEFAULT_SLACK = .15;

    /**
     * The debt limit a new city is given, the city's debt over a year of its GDP: 240% (0.7.101, Jerus's decision A6 of
     * 2026-10-10, "have the default at 240%"). From 0.7.81 to 0.7.100 60%, the Maastricht Treaty's reference value
     * (OLD_DEFAULT_DEBT_LIMIT), which stopped automatic building in most played cities - his own owed 250% of a year's
     * GDP; from 0.7.73 to 0.7.80 debt payments at most 15% of revenue.
     */
    public static final double DEFAULT_DEBT_LIMIT = 2.40;

    /**
     * The default from 0.7.81 to 0.7.100 (star N6-2): an older save that carries exactly it loads at DEFAULT_DEBT_LIMIT
     * (star P2-5) - every save since 0.7.81 writes its limit whether or not the dial was touched, and automatic building
     * was off by default, so this figure is the old default far more often than a choice.
     */
    public static final double OLD_DEFAULT_DEBT_LIMIT = .60;

    /** The spare margin's slider runs from none - just enough - to this: half again what the city uses of every service (star N4-3). */
    public static final double SLACK_MOST = .50;

    /**
     * The debt limit's slider runs from none - with any debt it builds nothing - to six years of GDP (0.7.101, star
     * P2-4): Jerus, "one should have the option for beyond 300%" - twice the old top, so a city past what any large
     * government has owed (about two and a half years: Britain's after Waterloo and after 1945, Japan's today) can still
     * be built for. Three years from 0.7.81 to 0.7.100 (star N6-2); until 0.7.81, the Finances tab's "constrained" line
     * of debt payments (CityNeeds.SERVICE_CONSTRAINED).
     */
    public static final double DEBT_LIMIT_MOST = 6.0;

    /** The spare margin's step: a whole per cent (the debt limit's until 0.7.81). */
    public static final double STEP = .01;

    /** The debt limit's step (0.7.101, star P2-4): ten per cent of a year's GDP - sixty steps to DEBT_LIMIT_MOST, as N6-4's five per cents were to three years; DEFAULT_DEBT_LIMIT and OLD_DEFAULT_DEBT_LIMIT are both on them. */
    public static final double DEBT_STEP = .10;

    /** The most of its orders the log keeps, newest kept (BuildLog's cap). */
    public static final int LOG_MOST = 40;

    private boolean on = false;
    private double slack = DEFAULT_SLACK;
    private double debtLimit = DEFAULT_DEBT_LIMIT;
    /** "Build from cash anyway" (0.7.81): over the debt limit, build what the cash alone pays for, never borrowing. Off in a new city and an older save. */
    private boolean cashAnyway = false;

    public boolean isOn() { return on; }
    public double getSlack() { return slack; }
    /** The debt limit: the city's debt over a year of its GDP (0.7.81; debt payments over revenue until then). */
    public double getDebtLimit() { return debtLimit; }
    public boolean isCashAnyway() { return cashAnyway; }

    /** A slider's value on its own steps and inside its ends. */
    static double onSteps(double v, double most) {
        return onSteps(v, most, STEP);
    }

    /** ...on steps of the caller's. */
    static double onSteps(double v, double most, double step) {
        if (!Double.isFinite(v)) return 0;
        double stepped = Math.round(v / step) * step;
        return Math.max(0, Math.min(most, Math.round(stepped * 100) / 100.0));
    }

    /** On or off; the change is a decision (DecisionLog.CONSTRUCTION). Turning it off clears what it was held back by. */
    public void setOn(boolean on, DecisionLog decisions) {
        if (this.on == on) return;
        this.on = on;
        if (!on) { held.clear(); bought.clear(); }
        if (decisions != null) decisions.record(DecisionLog.CONSTRUCTION, on
                ? "Automatic building on: " + DecisionLog.pct(slack) + " spare, debt to "
                        + DecisionLog.pct(debtLimit) + " of GDP" + (cashAnyway ? ", from cash over it" : "")
                : "Automatic building off");
    }

    /** The spare margin, on its slider's steps. */
    public void setSlack(double slack, DecisionLog decisions) {
        double v = onSteps(slack, SLACK_MOST);
        if (v == this.slack) return;
        this.slack = v;
        if (decisions != null) decisions.record(DecisionLog.CONSTRUCTION,
                "Automatic building's spare margin to " + DecisionLog.pct(v));
    }

    /** The debt limit, on its slider's steps. */
    public void setDebtLimit(double limit, DecisionLog decisions) {
        double v = onSteps(limit, DEBT_LIMIT_MOST, DEBT_STEP);
        if (v == this.debtLimit) return;
        this.debtLimit = v;
        if (decisions != null) decisions.record(DecisionLog.CONSTRUCTION,
                "Automatic building's debt limit to " + DecisionLog.pct(v) + " of GDP");
    }

    /** "Build from cash anyway", on or off; the change is a decision. */
    public void setCashAnyway(boolean anyway, DecisionLog decisions) {
        if (this.cashAnyway == anyway) return;
        this.cashAnyway = anyway;
        if (decisions != null) decisions.record(DecisionLog.CONSTRUCTION, anyway
                ? "Automatic building over its debt limit: builds from cash" : "Automatic building over its debt limit: builds nothing");
    }

    /* =====================================================================
       WHAT IT DID: THE LOG, THE TOTALS, AND WHAT HELD IT BACK
       ===================================================================== */

    /** One order it placed, and why. Money in thousands, as every figure in the model. */
    public static final class Entry {
        public int month;
        public String measure;
        public String building;
        public int count;
        /** What the advice's card asked for; more than count when the builders, the ground or the limit cut it (the budget too until 0.7.101). */
        public int wanted;
        public double cost;
        /** What the funding page's bond brought for it; 0 out of the cash. */
        public double borrowed;
        public boolean paving;
        public String why;
        /** The ground it bought for the order (0.7.77): its dry square feet, its price in local money at the day's rate, and the offers' places ("West 3, North 1"); 0 and null for none. */
        public double landSqFt;
        public double landCost;
        public String landWhere;

        public Entry() { }

        Entry(int month, String measure, String building, int count, int wanted, double cost, double borrowed,
              boolean paving, String why) {
            this.month = month;
            this.measure = measure;
            this.building = building;
            this.count = count;
            this.wanted = wanted;
            this.cost = cost;
            this.borrowed = borrowed;
            this.paving = paving;
            this.why = why;
        }

        public int month() { return month; }
        public String measure() { return measure == null ? "" : measure; }
        public String building() { return building == null ? "" : building; }
        public int count() { return count; }
        public int wanted() { return wanted; }
        public double cost() { return cost; }
        public double borrowed() { return borrowed; }
        public boolean paving() { return paving; }
        public String why() { return why == null ? "" : why; }
        public double landSqFt() { return landSqFt; }
        public double landCost() { return landCost; }
        public String landWhere() { return landWhere == null ? "" : landWhere; }
    }

    private final List<Entry> log = new ArrayList<>();

    /** What held a service back at the last pass: one line a measure, for the inbox and the card. */
    private final List<String> held = new ArrayList<>();

    /** The month of the last pass, 0 for none. */
    private int lastPass;

    private int orders;
    private long buildings;
    private double spent, borrowed;
    private int bonds;
    /** The ground it has bought for its orders (0.7.77): dry square feet, its price in local money, and how many offers. */
    private double landSqFt, landSpent;
    private int landOffers;

    /** The ground the last pass bought, a line a purchase, for the inbox (not saved: the pass that writes it runs before the month's notices are taken, loaded or not). */
    private final List<String> bought = new ArrayList<>();

    /** Its orders, oldest first. */
    public List<Entry> log() { return Collections.unmodifiableList(log); }

    /** ...newest first, at most n. */
    public List<Entry> latest(int n) {
        List<Entry> out = new ArrayList<>();
        for (int i = log.size() - 1; i >= 0 && out.size() < n; i--) out.add(log.get(i));
        return out;
    }

    /** What held it back at the last pass, a line a measure; empty when nothing did. */
    public List<String> held() { return Collections.unmodifiableList(held); }

    public int getLastPass() { return lastPass; }
    public int getOrders() { return orders; }
    public long getBuildings() { return buildings; }
    public double getSpent() { return spent; }
    public double getBorrowed() { return borrowed; }
    public int getBonds() { return bonds; }
    public double getLandSqFt() { return landSqFt; }
    public double getLandSpent() { return landSpent; }
    public int getLandOffers() { return landOffers; }

    /** The ground the last pass bought for its orders, a line a purchase saying what and why; empty when it bought none. */
    public List<String> bought() { return Collections.unmodifiableList(bought); }

    /* =====================================================================
       THE MEASURES
       ===================================================================== */

    /** A remit building: one of the city's five Build categories - utilities, roads and transit, healthcare, education, safety. */
    public static boolean inRemit(BuildingsTemplate t) {
        if (t == null) return false;
        BuildAdvice.Category c = BuildAdvice.categoryOf(t.getCategory());
        return c != null && c.cityBuilds();
    }

    /** NEEDS YOU's row for exactly this measure (the dead, not the plots, for death care), or null for none. */
    static CityNeeds.Need rowFor(List<CityNeeds.Need> all, BuildAdvice.Measure m) {
        for (CityNeeds.Need n : all) {
            if (m.equals(BuildAdvice.measureOf(n))) return n;
        }
        return null;
    }

    /**
     * Whether a measure is one the pass keeps this month: transit never (the
     * road's candidates carry it), and a school above the ladder only with a
     * NEEDS YOU row (CityNeeds.listsSchool(), Jerus's 0.7.51 rule: "way too
     * early sometimes"); every other measure always, its own figure deciding
     * whether it is short (step()'s ahead()). A FIRST BUILDING WHERE THERE IS
     * NONE (0.7.101): from 0.7.73 to 0.7.100 a first school down the ladder,
     * police station or prison waited until the need filled half the
     * smallest that serves it (FIRST_SHARE, star N4-4), so a town with
     * children and no seats - 0% served - was not given a school because one
     * would take it to 20,000%. Jerus (decision A4, 2026-10-10): build it
     * (star P2-1) - for a stage with any child or teen to teach.
     */
    static boolean kept(Game game, BuildAdvice.Measure m, List<CityNeeds.Need> all) {
        if (m.kind() == BuildAdvice.Kind.TRANSIT) return false;
        if (m.kind() != BuildAdvice.Kind.SCHOOL) return true;
        if (!m.school().isBasic()) return rowFor(all, m) != null;
        // ...a stage down the ladder with somebody to teach: with nobody its figure reads 0% (BuildAdvice.school()).
        return BuildAdvice.supplyDemand(game, m, new LinkedHashMap<>())[1] > 0;
    }

    /** Buildings that serve a measure, standing and on site. */
    static int serving(Game game, BuildAdvice.Measure m) {
        BuildingManager bm = game.getBuildingManager();
        int[] site = bm.getUnderConstructionById();
        int n = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (!m.serves(t)) continue;
            n += bm.getQuantity(t.getId());
            if (t.getId() < site.length) n += site[t.getId()];
        }
        return n;
    }

    /**
     * Whether an order for a measure is a FIRST OF A KIND WHERE THERE IS NONE
     * (0.7.101): a school down the ladder, a police station or a prison -
     * the buildings that waited for half their worth until then - with none
     * standing or on site. Paid from the cash alone (pays(), star P2-1).
     */
    public static boolean firstOfAKind(Game game, BuildAdvice.Measure m) {
        boolean waited = m.kind() == BuildAdvice.Kind.POLICE || m.kind() == BuildAdvice.Kind.CELLS
                || (m.kind() == BuildAdvice.Kind.SCHOOL && m.school().isBasic());
        return waited && serving(game, m) == 0;
    }

    /** Every measure the pass keeps this month, in its order: NEEDS YOU's listed ones first, in its order, then the rest in the rings' order. */
    public static List<BuildAdvice.Measure> remit(Game game, List<CityNeeds.Need> all) {
        List<BuildAdvice.Measure> rings = new ArrayList<>();
        for (BuildAdvice.Category c : BuildAdvice.categories()) {
            if (!c.cityBuilds()) continue;
            for (BuildAdvice.Measure m : BuildAdvice.measuresOf(c.name())) {
                rings.add(m);
                if (m.kind() == BuildAdvice.Kind.DEATH) rings.add(BuildAdvice.Measure.of(BuildAdvice.Kind.PLOTS));
            }
        }
        List<BuildAdvice.Measure> out = new ArrayList<>();
        for (CityNeeds.Need n : CityNeeds.biting(all)) {
            BuildAdvice.Measure m = n.cityBuilds() ? BuildAdvice.measureOf(n) : null;
            if (m != null && rings.contains(m) && !out.contains(m) && kept(game, m, all)) out.add(m);
        }
        for (BuildAdvice.Measure m : rings) if (!out.contains(m) && kept(game, m, all)) out.add(m);
        return out;
    }

    /** The spare margin's demand: today's, padded by the slack, no projection. */
    public BuildAdvice.Ahead target() { return new BuildAdvice.Ahead(0, 1, slack); }

    /** Whether what stands keeps a measure at its target: off NEEDS YOU's list, and a served gauge at 100%, of today's demand with the spare margin past it (BuildAdvice.ahead()). */
    public boolean atTarget(Game game, BuildAdvice.Measure m) {
        return BuildAdvice.ahead(game, m, new LinkedHashMap<>(), target());
    }

    /* =====================================================================
       THE DEBT LIMIT, A SHARE OF GDP (0.7.81, batch N6)

       Jerus: "instead of % of revenue, just make it a debt to gdp ratio that
       you choose, if below then it auto builds, if above then no, with an
       optional button of if cash available build regardless". So the limit
       is the city's debt over a year of its output (ratio()), read before
       every order, the ground's with it (canPay()):
         - AT OR UNDER IT it builds, out of the cash over the reserve and
           past it on the funding page's bond, and borrows no further than
           keeps the debt at or under it once the bond is on the books
           (ratioAfter());
         - OVER IT it builds nothing and borrows nothing - unless "Build from
           cash anyway" is on (cashAnyway), when it builds what the cash over
           the reserve pays for, never borrowing.
       THE CITY'S DEBT (debt(); 0.7.101, star P2-9) is everything it owes:
       its bonds and bills (DebtManager.getAllPrincipal()), what the treasury
       is overdrawn, and what it owes its central bank in advances - the debt
       the market prices it on (DebtManager.getPricedDebt()), read live. A
       bond adds its face and clears the overdraft its cash covers
       (debtAfter()). From 0.7.81 to 0.7.100 (star N6-3) it was the bonds and
       bills alone, the left panel's Debt/GDP, so the wages of the staffed
       services it built, paid on an overdraft the central bank advanced,
       never reached the limit until the next order's bond turned them into
       paper: the auto-built playtest's seed 6 ran overdrawn from month 84
       with its advances outside the ratio (runs/fixP2-notes.md). (Counting
       them, Jerus's live city read 274% of GDP against its Debt/GDP of 250%,
       and ten years on 470% against 134%: the advances, $25B by then, the
       difference - runs/fixN6-notes.md.) A YEAR OF GDP (annualGdp(); 0.7.101)
       is the output of the last twelve months as recorded - a city younger
       than a year is read at what it has produced, not scaled up to a year:
       scaled up, as every screen's "of annual GDP" is, the playtest's village
       of 18 at month 3 read its founding month's building as $51.4M a year,
       and 240% of that let it borrow $38M for a school.
       The default and the slider's ends: DEFAULT_DEBT_LIMIT, DEBT_LIMIT_MOST.
       ===================================================================== */

    /**
     * A year of the city's output (0.7.101): the last twelve months' as
     * recorded (NationalAccounts.getAnnualGdp()) - a city younger than a year
     * at what it has produced; 0 with none recorded. Until 0.7.101 scaled up
     * to a year from fewer, as the screens read it (Pieces.annualGdp()).
     */
    public static double annualGdp(Game game) {
        NationalAccounts na = game.getEconomyManager().getNationalAccounts();
        return na.getMonthsRecorded() <= 0 ? 0 : na.getAnnualGdp();
    }

    /** What the treasury is overdrawn, 0 when it is not. */
    static double overdraft(double cash) {
        return Math.max(0, -cash);
    }

    /** The city's debt (0.7.101, star P2-9): everything it owes - its bonds and bills, what it is overdrawn, and its central bank's advances (DebtManager.getPricedDebt()'s three, read live); its bonds and bills alone until then (star N6-3). */
    public static double debt(Game game) {
        return game.getDebtManager().getAllPrincipal() + overdraft(game.getCash())
                + game.getCentralBank().getAdvancesToTreasury();
    }

    /** ...once a bond is on the books: its face added, and the overdraft its cash clears taken off. */
    public static double debtAfter(Game game, DebtQuote q) {
        return game.getDebtManager().getAllPrincipal() + q.faceValue() + overdraft(game.getCash() + q.cashReceived())
                + game.getCentralBank().getAdvancesToTreasury();
    }

    /** The city's debt over a year of its GDP: 0 owing nothing, +∞ owing with no GDP recorded. */
    public static double ratio(Game game) {
        return over(debt(game), annualGdp(game));
    }

    /** ...once a bond is on the books. */
    public static double ratioAfter(Game game, DebtQuote q) {
        return over(debtAfter(game, q), annualGdp(game));
    }

    private static double over(double debt, double gdp) {
        if (!(debt > 0)) return 0;
        return gdp > 0 ? debt / gdp : Double.POSITIVE_INFINITY;
    }

    /** Whether the city's debt is at or under the limit: it builds, and may borrow up to it; over it, it builds only from cash with cashAnyway on. */
    public boolean within(Game game) {
        return ratio(game) <= debtLimit;
    }

    /** The funding page's bond for `gap` of cash: its quote, or null for none to be had. */
    static DebtQuote bondFor(Game game, double gap) {
        if (!(gap > 0)) return null;
        DebtQuote q = game.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        return q == null || q.isEmpty() ? null : q;
    }

    /**
     * The cash it keeps (star N4-5): a month's tax, the line under which NEEDS
     * YOU lists the treasury (CityNeeds.taxRaised()). The cash "runs out"
     * there, not at nothing: spent to nothing, the month's own bills overdraw
     * the treasury and the central bank advances the rest - new money. On
     * 0.7.73's first playtest with automatic building, spending to nothing and
     * nothing held for the budget, the treasury was overdrawn from month 200,
     * the central bank's advances $622M and the price index 14.6 at month 600
     * (the default run's 4.7) (runs/fixN4-notes.md). Kept in 0.7.101 when the
     * revenue checks went (star P2-3): it says how an order is paid - out of
     * the cash or on the bond - not whether; within the limit the bond pays
     * what the reserve leaves.
     */
    public static double reserve(Game game) {
        return Math.max(0, CityNeeds.taxRaised(game));
    }

    /** What an order may take of the cash: what is over the reserve; an overdraft counted in full, as Build's funding page counts it (Game.buildFundingGap()). */
    public static double spendable(Game game) {
        double cash = game.getCash();
        return cash < 0 ? cash : Math.max(0, cash - reserve(game));
    }

    /** What a bond must bring for an order of `total`: the part the spendable cash does not cover. */
    public static double gapFor(Game game, double total) {
        return total - spendable(game);
    }

    /**
     * Whether `total` is paid for an order (0.7.101): as canPay() says, and a
     * FIRST OF A KIND WHERE THERE IS NONE (firstOfAKind()) out of the cash over
     * the reserve alone, never on a bond (star P2-1): Jerus, "at first it doesnt
     * build schools even tho it has the money".
     */
    boolean pays(Game game, double total, boolean fromCash) {
        return canPay(game, total) && !(fromCash && gapFor(game, total) > 0);
    }

    /** Whether `total` is paid for: at or under the limit, by the cash and past it a bond that keeps the debt within it; over it, by the cash alone with cashAnyway on, and otherwise not at all (0.7.81). */
    boolean canPay(Game game, double total) {
        double gap = gapFor(game, total);
        if (!within(game)) return cashAnyway && !(gap > 0);
        if (!(gap > 0)) return true;
        if (!(debtLimit > 0)) return false;
        DebtQuote q = bondFor(game, gap);
        return q != null && ratioAfter(game, q) <= debtLimit;
    }

    /* =====================================================================
       THE PASS
       ===================================================================== */

    /** What the pass did for one measure. */
    public enum Outcome {
        /** What stands and what is on site keep it ahead of its projection with the margin. */
        AHEAD,
        /** An order placed. */
        ORDERED,
        /** Works for it are on site and the builders could open no more inside a year. */
        WAITING,
        /** The builders have no site output, or no ground is to be had for one (since 0.7.77 it buys the rest), or the limit - over it, the cash - would not pay for one of any building in the advice's ranking (0.7.101; the budget could not run one until then). */
        HELD,
        /** No building the city could staff moves it (the advice offers nothing). */
        NOTHING
    }

    /** Why a count was cut or an order held: DEBT the limit - over it, or a bond that would cross it; CASH (0.7.81) over it with "Build from cash anyway" on, to what the cash pays for. */
    public enum Cut { NONE, BUILDERS, GROUND, DEBT, CASH }

    /**
     * One measure at the last pass: what it did, and why - and since 0.7.77
     * the ground its order lacked when it was weighed (landShort, square feet
     * past what was free), the offers it bought for it and their price in
     * local money.
     */
    public record Step(BuildAdvice.Measure measure, Outcome outcome, String building, int wanted, int ordered,
                       Cut cut, double cost, double borrowed, double landShort, List<LandParcel> land, double landCost) {
        Step(BuildAdvice.Measure measure, Outcome outcome, String building, int wanted, int ordered, Cut cut, double cost,
             double borrowed) {
            this(measure, outcome, building, wanted, ordered, cut, cost, borrowed, 0, List.of(), 0);
        }

        /** The dry ground it bought, in square feet. */
        public double landSqFt() {
            double s = 0;
            for (LandParcel p : land) s += p.getSizeSqFt();
            return s;
        }
    }

    private final List<Step> steps = new ArrayList<>();

    /*
     * THE BUDGET, GONE (0.7.101, batch P2). From 0.7.73 a staffed building -
     * care, death care, a school, the police, the cells and a transit line -
     * was ordered only within what a year's revenue left after the month's
     * spending (its budget room, revenue and REVENUE_MONTHS; star N4-6), and past
     * one the budget could not run, the next in the ranking. Jerus (decision
     * A4, 2026-10-10): "for some reason some checks to check revenue, while
     * the real thing is only debt to gdp ratio". So nothing reads the revenue
     * now. What a staffed service costs to run is in its figure over its life
     * (BuildAdvice.orderLife()), so the advice prefers the one cheaper to
     * run; but nothing stops a city building one it cannot run but the debt
     * limit: its wages are paid from the treasury, the treasury overdrawn is
     * cleared by the next order's bond or advanced by the central bank, the
     * bonds are the debt the limit reads, and past the limit the pass builds
     * nothing (star P2-3).
     */

    /** The last pass, a step a measure (not saved: a harness reads it the month it is made). */
    public List<Step> steps() { return Collections.unmodifiableList(steps); }

    /**
     * The month's pass (see the header): nothing while it is off. Called by
     * Game.nextMonth() first; a harness may call it on a city between presses.
     */
    public void pass(Game game) {
        if (!on) return;
        held.clear();
        bought.clear();
        steps.clear();
        overSaid = false;
        lastPass = game.getMonth();
        // The player's setting at work, not a decision (the rollover's rule).
        game.getDecisions().hold();
        try {
            List<CityNeeds.Need> all = CityNeeds.measure(game, CityNeeds.PLAIN);
            for (BuildAdvice.Measure m : remit(game, all)) step(game, all, m);
        } finally {
            game.getDecisions().release();
        }
    }

    /** A card the money or the ground could not pay for one of (0.7.101): the suggestion, why, how many it asked for, and the inbox's line for it. */
    private record Refused(BuildAdvice.Suggestion s, Cut cut, int wanted, String line) { }

    /**
     * One measure's order, cut and placed. THE RANKING, WALKED PAST WHAT THE
     * MONEY CANNOT PAY FOR (0.7.101, star P2-6): where the ground and the money
     * would not pay for one of the advice's card - a Coal Power Plant past the
     * debt limit, a building no bare ground can be had for - the pass takes the
     * next building in the advice's ranking (its skip), as it took the next
     * past one the budget could not run from 0.7.73 to 0.7.100, and holds only
     * when none is left, in the first card's words. Until 0.7.101 it held the
     * measure on the first card, month after month, while a building the money
     * would pay for served it: on the auto-built playtest's seed 1 the advice's
     * coal plant was never paid for and power fell from 100% to 36% served
     * (runs/fixP2-notes.md). Over the limit with Build from cash anyway off
     * nothing is paid for, and it holds at once.
     */
    private void step(Game game, List<CityNeeds.Need> all, BuildAdvice.Measure m) {
        Map<BuildingsTemplate, Integer> site = BuildAdvice.onSite(game, m);
        if (BuildAdvice.ahead(game, m, site, BuildAdvice.opening(game, 0, slack))) {
            steps.add(new Step(m, Outcome.AHEAD, null, 0, 0, Cut.NONE, 0, 0));
            return;
        }
        boolean worksOnSite = BuildAdvice.units(site) > 0;
        // A first of a kind where there is none is paid from the cash alone (0.7.101, star P2-1).
        boolean fromCash = firstOfAKind(game, m);
        Set<BuildingsTemplate> skip = new HashSet<>();
        boolean noPaving = false;
        Refused first = null;
        while (true) {
            BuildAdvice.Suggestion s = BuildAdvice.suggestFor(game, rowFor(all, m), m, game.getCash(),
                    game.getLandManager().getAvailableSqFt(), slack, skip, noPaving);
            if (s == null || s.count() < 1 || !inRemit(s.template())) {
                if (first != null) {
                    hold(m, first.s(), first.cut(), first.wanted(), first.line());
                    return;
                }
                steps.add(new Step(m, Outcome.NOTHING, null, 0, 0, Cut.NONE, 0, 0));
                // ...said in the inbox while the measure is short of its margin.
                if (!atTarget(game, m)) held.add(nothingWords(game, m));
                return;
            }
            BuildingsTemplate t = s.template();
            String name = s.paving() ? ConstructionControl.PAVE_FROM + ", paved" : t.getName();
            int wanted = s.count();

            // THE BUILDERS: no more than opens inside MAX_ORDER_MONTHS.
            if (Double.isNaN(wait(game, s, 1))) {
                hold(m, s, Cut.BUILDERS, wanted, m.label() + ": " + wanted + " " + name + " wanted; the builders have no"
                        + " site output to build them.");
                return;
            }
            int n = wanted;
            Cut cut = Cut.NONE;
            if (wait(game, s, n) > BusinessInvestment.MAX_ORDER_MONTHS) {
                int lo = 0, hi = n;                      // lo opens inside, hi does not
                while (hi - lo > 1) {
                    int mid = lo + (hi - lo) / 2;
                    if (wait(game, s, mid) > BusinessInvestment.MAX_ORDER_MONTHS) hi = mid; else lo = mid;
                }
                n = lo;
                cut = Cut.BUILDERS;
                if (n == 0) {
                    if (worksOnSite) {
                        steps.add(new Step(m, Outcome.WAITING, name, wanted, 0, Cut.BUILDERS, 0, 0));
                        return;
                    }
                    n = 1;
                }
            }

            // THE GROUND AND THE MONEY (0.7.77): the ground the order lacks, bought as Build's land shortcut buys it, and
            // the two paid for out of the cash over the reserve, then the bond within the limit - fewer where all of
            // it is not; none, the next in the ranking (0.7.101).
            Ground ground = s.paving() ? Ground.NONE : groundFor(game, t, n);
            // Over the limit with "Build from cash anyway" on, what holds it is the cash (0.7.81).
            Cut money = (!within(game) && cashAnyway) || (fromCash && within(game)) ? Cut.CASH : Cut.DEBT;
            if (ground == null || !pays(game, ground.cash() + total(game, s, n), fromCash)) {
                Cut why = ground == null ? Cut.GROUND : money;
                int lo = 0, hi = n;                      // lo is paid for, its ground and all; hi is not
                while (hi - lo > 1) {
                    int mid = lo + (hi - lo) / 2;
                    Ground g = s.paving() ? Ground.NONE : groundFor(game, t, mid);
                    if (g != null && pays(game, g.cash() + total(game, s, mid), fromCash)) lo = mid; else hi = mid;
                }
                if (lo < 1) {
                    Ground one = s.paving() ? Ground.NONE : groundFor(game, t, 1);
                    Cut refused = one == null ? Cut.GROUND : money;
                    if (first == null) {
                        first = new Refused(s, refused, wanted, m.label() + ": " + wanted + " " + name + " wanted; "
                                + (one == null ? groundWords(game, t) : fromCash && within(game) ? firstWords(game, s, one)
                                : debtWords(game, s, one)));
                    }
                    // Over the limit, the toggle off, nothing is paid for: no other building in the ranking either.
                    if (refused == Cut.DEBT && !within(game)) {
                        hold(m, first.s(), first.cut(), first.wanted(), first.line());
                        return;
                    }
                    if (s.paving()) noPaving = true; else skip.add(t);
                    continue;
                }
                n = lo;
                cut = why;
                ground = s.paving() ? Ground.NONE : groundFor(game, t, n);
            }

            place(game, m, s, name, wanted, n, cut, ground, first);
            return;
        }
    }

    /**
     * Why no ground can be had for one (0.7.77; 0.7.101): no bare ground on offer to buy for it - or a city whose
     * buildings already stand on more ground than it owns, where an order on ground of its own could not be placed by
     * Build's own rule (LandManager.canAllocate()), and the pass buys only an order's own ground (star P2-7).
     */
    private static String groundWords(Game game, BuildingsTemplate t) {
        double over = overFull(game);
        if (over > 0) return "the city's buildings stand on " + LandManager.areaWords(over) + " more ground than it owns, so"
                + " no order fits on ground of its own, and it buys only an order's own. Buy that at the land office.";
        double free = game.getLandManager().getAvailableSqFt();
        return "the city has no ground for one (" + LandManager.areaWords(t.getLandSqFt()) + " each, "
                + LandManager.areaWords(Math.max(0, free)) + " free) and no bare ground on offer to buy for it. Buy land at"
                + " the land office.";
    }

    /** Places n of a suggestion, its ground bought first (0.7.77), borrowing what the spendable cash does not cover, and writes it down. */
    private void place(Game game, BuildAdvice.Measure m, BuildAdvice.Suggestion s, String name, int wanted, int n,
                       Cut cut, Ground ground, Refused first) {
        String before = reading(game, m, new LinkedHashMap<>());
        double cost = total(game, s, n);
        double landShort = s.paving() ? 0 : Math.max(0, lacks(game, s.template(), n));
        double gap = gapFor(game, cost + ground.cash());
        // Overdrawn, the bond clears the overdraft too, as Build's funding page counts it (Game.buildFundingGap()).
        double overdraft = Math.max(0, -game.getCash());
        double raised = 0;
        if (gap > 0) {
            double cashBefore = game.getCash();
            game.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
            raised = game.getCash() - cashBefore;
            bonds++;
            borrowed += raised;
        }
        // The ground first, each offer as the land office's Buy buys it - paid the way its toggle says.
        double rate = game.getForeignAccounts().getRate(), landCost = 0, landSq = 0;
        List<LandParcel> got = new ArrayList<>();
        List<String> places = new ArrayList<>();
        for (LandParcel o : ground.offers()) {
            if (!game.buyLandParcel(o.getId())) break;
            got.add(o);
            places.add(o.where());
            landCost += o.localPrice(rate);
            landSq += o.getSizeSqFt();
        }
        landOffers += got.size();
        landSqFt += landSq;
        landSpent += landCost;
        if (got.size() < ground.offers().size()) {
            if (!got.isEmpty()) bought.add(boughtWords(m, name, n, got, landCost, landShort, ground) + " Its order could not be placed.");
            hold(m, s, Cut.NONE, wanted, m.label() + ": the ground for " + n + " " + name + " could not be bought.");
            return;
        }
        boolean placed = s.paving() ? game.paveRoads(n)
                : game.buildStack(s.template(), n, false) == Game.BuildResult.SUCCESS;
        if (!placed) {
            if (!got.isEmpty()) bought.add(boughtWords(m, name, n, got, landCost, landShort, ground) + " Its order could not be placed.");
            hold(m, s, Cut.NONE, wanted, m.label() + ": " + n + " " + name + " could not be placed.");
            return;
        }
        String after = reading(game, m, BuildAdvice.onSite(game, m));
        String land = got.isEmpty() ? "" : "; " + LandManager.areaWords(landSq) + " of ground bought for it first ("
                + String.join(", ", places) + ", " + money(landCost) + ")";
        String why = m.label() + " " + before + ", " + after + " once what is on site opens"
                + cutWords(cut, wanted, n) + land + (raised > 0 && overdraft > 0 ? "; the bond also cleared the treasury's "
                + DecisionLog.money(overdraft) + " overdraft, as Build's funding page does" : "")
                + (first == null ? "" : "; the advice ranks " + first.wanted() + " " + refusedName(first.s()) + " first, "
                + (first.cut() == Cut.GROUND ? "for which no ground could be had" : "which the money would not pay for")
                + ", so the next in its ranking");
        Entry e = new Entry(game.getMonth(), m.label(), name, n, wanted, cost, raised, s.paving(), why);
        if (!got.isEmpty()) {
            e.landSqFt = landSq;
            e.landCost = landCost;
            e.landWhere = String.join(", ", places);
            bought.add(boughtWords(m, name, n, got, landCost, landShort, ground));
        }
        log.add(e);
        while (log.size() > LOG_MOST) log.remove(0);
        orders++;
        buildings += n;
        spent += cost;
        steps.add(new Step(m, Outcome.ORDERED, name, wanted, n, cut, cost, raised, landShort, List.copyOf(got), landCost));
        GameLog.note(String.format("Automatic building: %d %s for %s, $%,.0fk%s. %s", n, name,
                m.label().toLowerCase(), cost, raised > 0 ? String.format(" ($%,.0fk of it on a %d-year bond)",
                        raised, Game.BUILD_BOND_YEARS) : "", why));
    }

    /** A refused card's building as its words name it: its own name, or the gravel roads a paving paves. */
    private static String refusedName(BuildAdvice.Suggestion s) {
        return s.paving() ? ConstructionControl.PAVE_FROM + ", paved" : s.template().getName();
    }

    /** Thousands of local money as the decision log writes them ("$1.2M"). */
    private static String money(double thousands) {
        return DecisionLog.money(thousands);
    }

    /**
     * The inbox's line for ground bought (0.7.77): which offers, how much and
     * at what price, the order it was for and what that order lacked - and
     * which of the shortcut's rules picked it: one bare offer covering the
     * shortfall, or (none covering it) the best value of bare ground, offer
     * by offer.
     */
    private static String boughtWords(BuildAdvice.Measure m, String name, int n, List<LandParcel> got, double landCost,
                                      double landShort, Ground ground) {
        List<String> places = new ArrayList<>();
        double sq = 0;
        for (LandParcel o : got) { places.add(o.where()); sq += o.getSizeSqFt(); }
        return m.label() + ": bought " + String.join(", ", places) + " - " + LandManager.areaWords(sq) + " of bare ground for "
                + money(landCost) + " - for " + n + " " + name + ", which lacked " + LandManager.areaWords(landShort)
                + (ground.covers() ? " of ground: the cheapest bare offer that covers it, as Build's land shortcut buys."
                        : " of ground: no bare offer covered it, so the best value of bare ground, offer by offer, as Build's"
                        + " land shortcut buys.");
    }

    /* =====================================================================
       THE GROUND ITS ORDERS NEED (0.7.77, batch N5)

       Jerus, asked "should auto-build buy the land its orders need, instead
       of asking you in the inbox?": "no you do need to buy land" - read (the
       orchestrator's star, runs/fixN5-notes.md) as: automatic building must
       buy the ground its own orders need. Until 0.7.77 it bought none, and
       on his own city, with no ground free, every pass for ten years held
       seven services for it and built nothing (runs/fixN4-notes.md).

       An order short of ground for its count - its own ground past what is
       free, and no more (0.7.101, lacks()) - once the builders have cut it,
       buys what Build's land shortcut would buy for the
       shortfall (Game.bestOffer(LandNeed.shortfall())): the cheapest offer of
       bare ground whose dry ground covers it, the nearer on a tie; with none
       big enough, the offer with the most dry ground a dollar, not mostly
       sea, and again for what is left - every one BARE GROUND
       (LandMarket.bareGround()), so it never buys a field for its ore, where
       the shortcut's best value may hold some. Planned from the listing
       before any is bought: a listed offer's price is fixed and stays
       listed (LandMarket), so the plan is what it buys. The ground and the
       order are paid for together, out of the cash over the reserve and
       then the funding page's bond within the limit (canPay()); fewer
       buildings where all of it is not paid for, and none bought - the next
       building in the ranking, or held for the limit, or for want of bare
       ground on offer - where one is not; and none in a city whose buildings
       already stand on more ground than it owns (overFull()), where an
       order on ground of its own could not be placed (0.7.101). It
       is bought as the land office's Buy buys it (Game.buyLandParcel()),
       paid the way the office's toggle says: converting, the offers' price
       in local money; from the vault, what the vault lacks converted out of
       the cash - what the cash is weighed for. Nothing outside its remit
       reaches it: it is a step of a remit order.
       ===================================================================== */

    /** The ground an order lacks, as the offers it would buy for it in turn, the cash that takes, and whether one offer covers it all. */
    public record Ground(List<LandParcel> offers, double cash, boolean covers) {
        /** No ground to buy: the order fits what is free. */
        static final Ground NONE = new Ground(List.of(), 0, true);

        /** Their dry ground, in square feet. */
        public double sqFt() {
            double s = 0;
            for (LandParcel p : offers) s += p.getSizeSqFt();
            return s;
        }
    }

    /** The ground n of a building lack past what is free, as groundFor() plans it: NONE when it fits, null when no bare ground on offer covers it - or (0.7.101) when the city's buildings already stand on more ground than it owns (overFull()), where an order on ground of its own could not be placed. */
    public static Ground groundFor(Game game, BuildingsTemplate t, int n) {
        double lack = lacks(game, t, n);
        if (!(lack > 0)) return Ground.NONE;
        if (overFull(game) > 0) return null;
        List<LandParcel> picks = new ArrayList<>();
        Set<Integer> taken = new HashSet<>();
        double got = 0;
        while (lack - got > 0) {
            LandParcel o = shortcutOffer(game, lack - got, taken);
            if (o == null) return null;
            picks.add(o);
            taken.add(o.getId());
            got += o.getSizeSqFt();
        }
        List<Integer> ids = new ArrayList<>(taken);
        double cash = game.isLandPaidFromVault() ? game.landTopUpLocal(ids) : game.landPriceLocal(ids);
        return new Ground(List.copyOf(picks), cash, picks.size() == 1 && picks.get(0).getSizeSqFt() >= lack);
    }

    /**
     * The ground n of a building lack, in square feet: what they need past
     * what is free (0.7.101) - only the order's own ground. From 0.7.77 to
     * 0.7.100 it counted from what the city owns less what its buildings use,
     * which a city whose buildings use more than it owns has below nothing
     * (Jerus's at month 416), so its first order bought the city's whole
     * deficit: 5.66M sq ft, $432.8M of land, for a $45.8M road
     * (runs/fixN5-notes.md). Jerus (decision A5, 2026-10-10): buy only the
     * order's own ground. Such a city's order on ground of its own could not
     * be placed (Game.buildStack()'s NO_LAND, LandManager.canAllocate() reading
     * free ground from what is owned less what is used), so there it buys
     * none and says why (groundFor(), groundWords(); star P2-7).
     */
    public static double lacks(Game game, BuildingsTemplate t, int n) {
        return game.landNeededFor(t, n) - game.getLandManager().getAvailableSqFt();
    }

    /** How much more ground the city's buildings stand on than it owns, in square feet (0.7.101): 0 for a city within its ground. */
    public static double overFull(Game game) {
        LandManager land = game.getLandManager();
        return Math.max(0, land.getAllocatedSqFt() - land.getOwnedSqFt());
    }

    /**
     * The offer Build's land shortcut would buy for `lackSqFt` of dry ground,
     * the offers in `taken` passed over, bare ground only: the cheapest whose
     * dry ground covers it, the nearer on a tie (Game.bestOffer()'s shortfall
     * rule); none covering, the most dry ground a dollar not mostly sea, the
     * nearer on a tie (its best value, here whether the cash affords it or
     * not: the order may borrow); null with no bare offer left.
     */
    public static LandParcel shortcutOffer(Game game, double lackSqFt, Set<Integer> taken) {
        LandMarket market = game.getLandManager().getMarket();
        LandParcel cheapest = null, best = null;
        for (LandParcel p : market.getListing()) {
            if (taken.contains(p.getId()) || !LandMarket.bareGround(p) || !(p.getSizeSqFt() > 0)) continue;
            if (p.getSizeSqFt() >= lackSqFt && (cheapest == null || p.getPriceUsd() < cheapest.getPriceUsd()
                    || (p.getPriceUsd() == cheapest.getPriceUsd() && market.nearer(p, cheapest)))) cheapest = p;
            if (!p.isMostlySea() && (best == null || p.getDryKm2PerUsd() > best.getDryKm2PerUsd()
                    || (p.getDryKm2PerUsd() == best.getDryKm2PerUsd() && market.nearer(p, best)))) best = p;
        }
        return cheapest != null ? cheapest : best;
    }

    /** An order's wait for n: a paving's on the Paved Road site, a building's in the queue. */
    static double wait(Game game, BuildAdvice.Suggestion s, int n) {
        Game.BuildQuote q = s.paving() ? game.quotePave(n) : game.quoteBuild(s.template(), n);
        return q == null ? Double.NaN : q.months;
    }

    /** What n of the order costs: Game.quoteBuild(), or Game.quotePave() for a paving. */
    static double total(Game game, BuildAdvice.Suggestion s, int n) {
        Game.BuildQuote q = s.paving() ? game.quotePave(n) : game.quoteBuild(s.template(), n);
        return q == null ? Double.POSITIVE_INFINITY : q.total;
    }

    private void hold(BuildAdvice.Measure m, BuildAdvice.Suggestion s, Cut cut, int wanted, String line) {
        held.add(line);
        steps.add(new Step(m, Outcome.HELD, s.paving() ? ConstructionControl.PAVE_FROM + ", paved" : s.template().getName(),
                wanted, 0, cut, 0, 0));
    }

    /** Whether this pass has said why over the limit it builds nothing: the first such line says it whole, the rest in short (0.7.81). */
    private boolean overSaid;

    /** A share of GDP as the card writes it: "262%", "7%", "0%", and a place under a tenth: "0.4%", "7.2%". */
    public static String gdpShare(double share) {
        double pct = share * 100;
        boolean whole = Math.abs(pct - Math.rint(pct)) < 1e-9;
        return share < .1 && !whole ? String.format("%.1f%%", pct) : String.format("%.0f%%", pct);
    }

    /** Why the money pays for none: one more and (0.7.77) the ground it lacks - over the limit, or a bond that would cross it (0.7.81). */
    private String debtWords(Game game, BuildAdvice.Suggestion s, Ground ground) {
        String one = ground.offers().isEmpty() ? "one" : "one and the " + LandManager.areaWords(ground.sqFt())
                + " of ground it lacks (" + money(ground.cash()) + ")";
        double now = ratio(game);
        if (!(now <= debtLimit)) {
            String owes = Double.isFinite(now) ? "the city owes " + gdpShare(now) + " of a year's GDP, over your limit of "
                    + gdpShare(debtLimit) : "the city owes " + money(debt(game)) + " and has no GDP recorded to set it against";
            if (cashAnyway) return owes + ", so it builds from the cash alone, and the cash over a month's tax is short of "
                    + one + ".";
            if (overSaid) return "over your debt limit, so it builds nothing.";
            overSaid = true;
            return owes + ", so it builds nothing and borrows nothing. Pay the debt down, raise the limit, or turn on Build"
                    + " from cash anyway.";
        }
        if (!(debtLimit > 0)) return "the cash over a month's tax is short of " + one + " and your debt limit is 0%, so it"
                + " borrows nothing.";
        double gap = gapFor(game, total(game, s, 1) + ground.cash());
        DebtQuote q = bondFor(game, gap);
        if (q == null) return "the cash over a month's tax is short of " + one + " and no bond can be had.";
        double after = ratioAfter(game, q);
        if (!Double.isFinite(after)) return "the cash over a month's tax is short of " + one + " and the city has no GDP"
                + " recorded to borrow against.";
        return String.format("borrowing $%,.0fk for %s would take the city's debt to %s of a year's GDP, past your"
                + " limit of %s.", gap, one, gdpShare(after), gdpShare(debtLimit));
    }

    /** Why the cash pays for no first of a kind (0.7.101): built from the cash alone, and the cash over a month's tax short of one and its ground. */
    private String firstWords(Game game, BuildAdvice.Suggestion s, Ground ground) {
        String one = ground.offers().isEmpty() ? "one" : "one and its ground";
        return "a first one where there is none is built from the cash, never on a bond, and the cash over a month's tax is "
                + money(Math.max(0, spendable(game))) + ", short of " + one + " (" + money(total(game, s, 1) + ground.cash())
                + ").";
    }

    /** Why nothing the city could build moves a measure short of its margin: water past the fresh water it owns with no sea, or no building it could staff. */
    static String nothingWords(Game game, BuildAdvice.Measure m) {
        if (m.kind() == BuildAdvice.Kind.WATER
                && !(game.getServicesManager().getUtilitiesHandler().getFreshHeadroom() > 0)
                && game.getLandManager().getSeaKm2() <= 0) {
            return m.label() + ": " + reading(game, m, new LinkedHashMap<>()) + "; the fresh water the city owns is"
                    + " all treated and it owns no sea. Buy land with a lake, a river or coast.";
        }
        return m.label() + ": " + reading(game, m, new LinkedHashMap<>()) + "; no building the city could staff"
                + " would move it now.";
    }

    static String cutWords(Cut cut, int wanted, int n) {
        if (n >= wanted) return "";
        switch (cut) {
            case BUILDERS: return " (" + wanted + " wanted; " + n + " open inside a year)";
            case GROUND:   return " (" + wanted + " wanted; the ground to be had for " + n + ")";
            case DEBT:     return " (" + wanted + " wanted; " + n + " within the debt limit)";
            case CASH:     return " (" + wanted + " wanted; " + n + " the cash pays for, over the debt limit)";
            default:       return "";
        }
    }

    /** A measure's figure as the Build tab reads it: served for a served gauge, its own reading for the rest. */
    public static String reading(Game game, BuildAdvice.Measure m, Map<BuildingsTemplate, Integer> added) {
        if (BuildAdvice.isServed(m)) return CityNeeds.servedPct(BuildAdvice.served(game, m, added)) + " served";
        double f = BuildAdvice.figure(game, m, added);
        switch (m.kind()) {
            case DEATH:  return String.format("%,.0f dead waiting", f);
            case PLOTS:  return String.format("%,.0f months of plots", f);
            case POLICE: return String.format("crime %.2f× Canada's", f);
            case CELLS:  return String.format("%,.0f caught with no cell", f);
            default:     return String.format("%.2f", f);
        }
    }

    /* =====================================================================
       THE SAVE
       ===================================================================== */

    /** What is saved, under one key (DataSave.autoBuild). */
    public static final class State {
        public boolean on;
        public double slack;
        /** The debt limit over a year of GDP (0.7.81): a save from before has none - its "debtLimit" was a share of revenue, which nothing reads now - and loads DEFAULT_DEBT_LIMIT; one at OLD_DEFAULT_DEBT_LIMIT, the default until 0.7.101, loads it too. */
        public Double debtToGdp;
        /** "Build from cash anyway" (0.7.81): off in a save from before. */
        public boolean cashAnyway;
        public List<Entry> log;
        public List<String> held;
        public int lastPass;
        public int orders;
        public long buildings;
        public double spent;
        public double borrowed;
        public int bonds;
        /** The ground bought for its orders (0.7.77): a save from before has none. */
        public double landSqFt;
        public double landSpent;
        public int landOffers;
    }

    public State toState() {
        State s = new State();
        s.on = on;
        s.slack = slack;
        s.debtToGdp = debtLimit;
        s.cashAnyway = cashAnyway;
        s.log = new ArrayList<>(log);
        s.held = new ArrayList<>(held);
        s.lastPass = lastPass;
        s.orders = orders;
        s.buildings = buildings;
        s.spent = spent;
        s.borrowed = borrowed;
        s.bonds = bonds;
        s.landSqFt = landSqFt;
        s.landSpent = landSpent;
        s.landOffers = landOffers;
        return s;
    }

    /** Puts a saved state back; null - a save from before 0.7.73 - is off, at the defaults, with nothing done; one from 0.7.73 to 0.7.80 keeps its switch and margin and reads the default limit, "Build from cash anyway" off; one from 0.7.81 to 0.7.100 at the old default, 60%, reads the new one (0.7.101), and any other limit stands on the new steps. */
    public void restore(State s) {
        log.clear();
        held.clear();
        bought.clear();
        steps.clear();
        if (s == null) {
            on = false;
            slack = DEFAULT_SLACK;
            debtLimit = DEFAULT_DEBT_LIMIT;
            cashAnyway = false;
            lastPass = orders = bonds = landOffers = 0;
            buildings = 0;
            spent = borrowed = landSqFt = landSpent = 0;
            return;
        }
        on = s.on;
        slack = onSteps(s.slack, SLACK_MOST);
        // ...the old default, 60% (0.7.81 to 0.7.100), as the new one (star P2-5); any other limit stands, on the new steps.
        debtLimit = s.debtToGdp == null || Math.abs(s.debtToGdp - OLD_DEFAULT_DEBT_LIMIT) < 1e-9 ? DEFAULT_DEBT_LIMIT
                : onSteps(s.debtToGdp, DEBT_LIMIT_MOST, DEBT_STEP);
        cashAnyway = s.cashAnyway;
        if (s.log != null) for (Entry e : s.log) if (e != null) log.add(e);
        if (s.held != null) for (String h : s.held) if (h != null) held.add(h);
        lastPass = s.lastPass;
        orders = s.orders;
        buildings = s.buildings;
        spent = s.spent;
        borrowed = s.borrowed;
        bonds = s.bonds;
        landSqFt = s.landSqFt;
        landSpent = s.landSpent;
        landOffers = s.landOffers;
    }
}
