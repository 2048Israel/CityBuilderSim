package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

/**
 * The city's borrowing: every bond, note and dollar bond the treasury owes,
 * the market that prices the next one, and the policy rate every price of
 * money in the city is built on - the player's dial, or the rule's with the
 * autopilot on (0.7.0).
 *
 * @author Jerus
 */
public class DebtManager {

    /* =======================================================================
       THE POLICY RATE
       =======================================================================

       This was a constant, and it was the most load-bearing constant in the
       game without anybody having decided anything about it. Every price of
       money in the city is built on it: floorRate() IS it (never less than the
       bank's own cost of funds), ceilingRate() is it plus both spreads, and
       the city's own borrowing rate sits between them. Since 0.7.0 it is the
       rate the CENTRAL BANK pays on the commercial bank's reserves, so
       Bank.fundToCover() is handed it directly: the bank's spare cash earns
       it, the window charges it plus CentralBank.WINDOW_PENALTY, and the
       treasury's advances are charged it. Since phase 4 the carry trade reads
       the gap between the bank's lending rate and the world's.

       So the central bank did not need new plumbing here. It needed to own
       this number, and since 0.7.0 it does (CentralBank; autopilot below).

       WHAT MOVING IT DOES, in the order the ECB lists the channels:
         - the bank's deposit and lending rates, immediately
         - what the city itself pays to borrow
         - how much the bank will lend at all, through the capital and funding
           limits, which is the channel that actually bites
         - the carry spread, so foreign money arrives or leaves
         - and, since this phase, the exchange rate directly

       Bounded at both ends because a policy rate is a decision, not a wish: no
       central bank sets a negative nominal rate by typing one. The top was 25%
       until 0.7.2, "past about a fifth the instrument stops transmitting and
       starts destroying" - which in this model was true: the currency's
       support from a high rate saturated 13 points over the world and the
       carry appetite at 6, so a higher dial bought nothing but dearer credit.
       0.7.2 gave the rate its channel (ForeignAccounts, THE REAL RATE, NOT THE
       NOMINAL; CapitalFlows.MAX_SPREAD) and the dial lost its stop with it.
       Jerus: "i think we need to uncap the rate... but if we do... what
       happens to everyone?" - the answer is the-central-bank.md's batch D
       measurement.
    */
    /** The floor of the dial: no central bank sets a negative nominal rate by typing one. */
    public static final double MIN_POLICY_RATE = .0;
    /** The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. */
    public static final double MAX_POLICY_RATE = 1.00;

    /** What the city's central bank charges. The player's dial - or the rule's, with autopilot on. */
    private double baseRate = .03;

    public double getPolicyRate() { return baseRate; }

    public void setPolicyRate(double rate) {
        baseRate = Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, rate));
    }

    /* -----------------------------------------------------------------------
       THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1).

       Until 0.7.0 the city's own paper was quoted CITY_DISCOUNT - two points -
       UNDER the policy rate, "the one number in this file that does not
       describe anything real": the Policy screen read 3.00% and the Finances
       screen 1.00% on the same morning, and the city borrowed cheaper than
       its own central bank, which no borrower anywhere does. Jerus found it
       on the screens, 2026-09-12, and decided: "leave as is for now, much
       later when we will redesign it realistically aka central bank stepping
       in to keep it at that rate and longer durations deviating."

       The central bank steps in now. The commercial bank's reserves earn the
       policy rate, so nobody lends the city less than that, and a note the
       city sells settles at the policy rate plus the credit spread its own
       debt measures produce - the T-bill rate, by arbitrage. The discount is
       deleted and the floor is the dial. The rest of Jerus's sentence,
       "longer durations deviating", is THE CURVE below (0.7.1): a term premium
       by maturity on top of this floor, and the central bank's holdings
       bending the long end down.
       ----------------------------------------------------------------------- */

    /**
     * THE AUTOPILOT (0.7.0): whether the rule holds the dial rather than the
     * player. Jerus's toggle, decided with the floor: when on, the top of
     * every month sets the dial to advisedPolicyRate() off the price index
     * before anything is priced (Game.nextMonth()), and the player's hand on
     * the dial - takeTheDial() - turns it off. Saved beside the rate
     * (DataSave.policyAutopilot); an old save reads off, which is the hand
     * that was on the dial when it was saved.
     */
    private boolean autopilot;

    public boolean isAutopilot()              { return autopilot; }
    public void setAutopilot(boolean on)      { this.autopilot = on; }

    /**
     * The player moves the dial by hand, which takes it back from the rule.
     * What the screens call; setPolicyRate() is what the rule and a fixture
     * call, and leaves the toggle alone.
     */
    public void takeTheDial(double rate) {
        autopilot = false;
        setPolicyRate(rate);
    }

    /** Where the rate sits when nobody is leaning on it either way. */
    public static final double NEUTRAL_RATE = .03;

    /** What the city is trying to hold inflation at. */
    public static final double INFLATION_TARGET = .02;

    /**
     * How hard the advised rate reacts to inflation missing its target.
     *
     * The Taylor principle: a coefficient greater than one, so a point of extra
     * inflation is met with MORE than a point of extra rate and the REAL rate
     * rises. Below one, raising rates in response to inflation still leaves
     * money cheaper than it was and feeds the thing it was meant to stop.
     */
    public static final double TAYLOR_WEIGHT = 1.5;

    /**
     * What a rule would set, given this month's inflation, held inside the
     * dial's bounds - ruleRate() is the rule before them.
     *
     * INFLATION ONLY, WITH NO OUTPUT GAP, and that is a judgement about this
     * model rather than about monetary policy. A Taylor rule normally carries a
     * second term in the output gap; this city runs 34% unemployment and has
     * for its whole history, so any gap measure built on it would read as
     * permanent enormous slack and advise the floor for ever. The game has a
     * trustworthy price index and does not have a trustworthy output gap, so
     * the rule uses the one it has.
     *
     * Advisory unless the rule has the dial. It is shown beside the dial with
     * its reasoning, and moves nothing on its own - except with the autopilot
     * on (THE AUTOPILOT, above), when the top of every month sets the dial to
     * it.
     */
    public double advisedPolicyRate(double inflation) {
        return Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, ruleRate(inflation)));
    }

    /**
     * What the rule itself says, before the dial's bounds (2026-09-21).
     *
     * advisedPolicyRate() is clamped to the dial, which is right for anything
     * that SETS the dial and wrong for a sentence about it: at 45% inflation
     * the monetary page said "the rule says 25.00%" when the rule says 67.5%
     * and it is the dial that stops at 25%. A player reading that could not
     * tell which of the two was the limit. The screens print both when they
     * differ. The cap was Jerus's until the money supply gave the rate a
     * channel to work through, and lifted to 100% with it in 0.7.2 - so the
     * two now differ at the floor, when inflation runs under zero,
     * and at the top only past 66.7% inflation.
     */
    public double ruleRate(double inflation) {
        return NEUTRAL_RATE + TAYLOR_WEIGHT * (inflation - INFLATION_TARGET);
    }

    /**
     * ...and why, in words, for the screen. Past either stop it names both
     * figures, the rule's and the dial's, because they are then not the same
     * number and only one of them is the limit.
     */
    public String adviceReason(double inflation) {
        double advised = advisedPolicyRate(inflation);
        if (Math.abs(inflation - INFLATION_TARGET) < .002) {
            return String.format("Inflation is %.1f%%, on its %.0f%% target. Hold at %.2f%%.",
                    inflation * 100, INFLATION_TARGET * 100, advised * 100);
        }
        double rule = ruleRate(inflation);
        String says = rule > MAX_POLICY_RATE
                ? String.format("the rule would set %.1f%%; the dial stops at %.0f%%, a guard "
                        + "against a typo rather than a policy", rule * 100, MAX_POLICY_RATE * 100)
                : rule < MIN_POLICY_RATE
                ? String.format("the rule would set %.1f%%; the dial stops at %.0f%%, because no "
                        + "central bank sets a negative rate by typing one",
                        rule * 100, MIN_POLICY_RATE * 100)
                : String.format("the rule says %.2f%%", advised * 100);
        return String.format(
                "Inflation is %.1f%% against a %.0f%% target, so %s "
                + "- %s a point of inflation by %.1f points of rate, which is what it "
                + "takes to make money genuinely dearer rather than only nominally.",
                inflation * 100, INFLATION_TARGET * 100, says,
                inflation > INFLATION_TARGET ? "meeting" : "giving back",
                TAYLOR_WEIGHT);
    }
    private double currentRate = baseRate;
    private double GDP;

    /**
     * The month's tax take. One of the two measures the rate is priced on.
     *
     * GDP alone is the wrong denominator for a city: the lender is not repaid
     * out of the economy's output, it is repaid out of what the government can
     * actually collect from it. Real municipal credit is rated on debt against
     * revenue for exactly that reason. Using both, evenly, means a city that
     * grows its economy AND one that raises its rate both become better credits,
     * which is the right pair of levers to reward.
     */
    private double monthlyTaxRevenue;

    /**
     * How far the city is overdrawn. Counted as borrowing, because it is.
     *
     * Without this the city could sit $1.1M in the red with no bonds outstanding
     * and be quoted 1%, because getAllPrincipal() was the only input. That
     * happened in the hand-played run the day this was written: the 30-year
     * balloon matured, principal went to zero, cash went to -$1,143,330, and the
     * market immediately offered the floor rate. An overdraft is the most
     * desperate borrowing there is and it now prices like it.
     */
    private double overdraft;

    /**
     * The most either measure alone can add to the rate.
     *
     * Two measures, five points each, so the spread runs 0 to 10 points over the
     * floor and the rate from the dial to ten points above it - 3% to 13% at
     * the default dial, where it ran 1% to 11% while CITY_DISCOUNT stood (until
     * 0.7.0). Neither measure can price the city on
     * its own: a city with no economy but plenty of revenue, or the reverse, is
     * capped at half the punishment.
     *
     * GENTLER AGAIN. This was ten points each, and Jerus asked for "a lot more
     * gentler" after playing a city by hand: a young city with a small tax base
     * was quoted 18% for one ordinary apartment block, because a sensible build
     * is simply large next to the revenue of a town of two thousand people. The
     * curve was punishing the size of the city, not the recklessness of the
     * borrowing.
     */
    private static final double MAX_SPREAD_PER_MEASURE = 0.05;


    /**
     * Debt, as a multiple of a year of the thing, at which a measure maxes out.
     *
     * GENTLER ON PURPOSE. The first version blended the two measures into one
     * index and multiplied by a flat spread, with no per-measure ceiling, which
     * put the rate against its cap at about 20x annual revenue. Jerus played a
     * run under it: "the financing actually should be gentler... debt was really
     * bad." He is right, and 20x was never defensible - real cities carry
     * multiples of revenue for decades without being priced as distressed.
     *
     * A hundred and fifty years of revenue is where a measure reaches its five
     * points, and it ramps linearly to get there.
     *
     * That is deliberately far out. Fifty was the first attempt and it read as
     * harsh in play, because the ratio a growing city runs at is dominated by
     * how small its tax base is rather than by how rash it is being - a city
     * borrowing to build the thing that will produce its revenue is at its worst
     * ratio precisely when the borrowing is most sensible.
     *
     * MEASURED on a city of 1,256 with $3.6M of annual tax revenue, with the
     * loan itself priced in - before 0.7.0, on a floor two points under the
     * dial, so every figure below is two points higher today:
     *
     *      1x revenue  1.05%      30x   2.44%      200x   8.96%
     *      2x revenue  1.10%      50x   3.41%      500x   the 11% ceiling
     *      5x revenue  1.24%     100x   5.81%
     *     10x revenue  1.48%
     *
     * For comparison, the ask that made Jerus raise this - $900k on a town of
     * 2,000, about 42x its annual revenue - was quoted 18.23% under the old
     * curve and prices at about 3% under this one.
     */
    private static final double FULL_STRESS_MULTIPLE = 150;

    /** The cheapest money the market will ever offer, whatever the books say. */
    private static final double MIN_RATE = 0.005;

    /** How many times to walk the face-value/rate fixed point. See quoteRate(). */
    private static final int QUOTE_ITERATIONS = 6;

    private List<Debt> debts;

    public DebtManager() {
        debts = new ArrayList<>();

    }

    public Debt addShortTermTBill(double faceValue, int months, int monthStarted) {
        return addShortTermTBill(faceValue, months, monthStarted, false);
    }

    public Debt addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign) {
        return book(new ShortTermTBill(faceValue, months, monthStarted, foreign));
    }

    public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate) {
        return addMediumTermBond(faceValue, months, monthStarted, rate, false);
    }

    public Debt addMediumTermBond(double faceValue, int months, int monthStarted,
                                  double rate, boolean foreign) {
        return book(new MediumTermBond(faceValue, months, monthStarted, rate, foreign));
    }

    public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate) {
        return addLongTermBond(faceValue, months, monthStarted, rate, false);
    }

    public Debt addLongTermBond(double faceValue, int months, int monthStarted,
                                double rate, boolean foreign) {
        return book(new LongTermBond(faceValue, months, monthStarted, rate, foreign));
    }

    /**
     * The one place paper joins the list, so the one place it can be told the
     * rate.
     *
     * Three add methods each doing their own `debts.add()` is three chances to
     * forget, and a foreign bond that never learned the exchange rate values
     * itself at 1.00 for ever - it would sit in the city's debt looking cheap
     * and never respond to the currency at all. Silent, and the kind of thing
     * that only shows up when somebody wonders why devaluation did nothing.
     */
    private Debt book(Debt paper) {
        paper.setExchangeRate(exchangeRate);
        debts.add(paper);
        return paper;
    }

    /* =======================================================================
       THE RATE THE FOREIGN PAPER IS VALUED AT
       ======================================================================= */

    private double exchangeRate = 1.0;

    /**
     * Pushed down to every instrument, from the month tick AND from the load
     * path.
     *
     * Both, because they are two different ways of arriving at the same state
     * and only one of them was ever going to be remembered. A city loaded with
     * USD debt and a currency at 1.40 would otherwise value that debt at 1.00
     * until the first month ticked, which is long enough for the debt screen,
     * the credit rating and the player's decision to all be wrong.
     */
    public void setExchangeRate(double rate) {
        if (rate <= 0) return;
        this.exchangeRate = rate;
        for (Debt d : debts) d.setExchangeRate(rate);
    }

    public double getExchangeRate() { return exchangeRate; }

    /** What the city owes abroad, in local money at today's rate. */
    public double getForeignPrincipal() {
        double total = 0;
        for (Debt d : debts) if (d.isForeign()) total += d.getOustandingPrincipal();
        return total;
    }

    /** ...and in the dollars it is actually owed in, which do not move. */
    public double getForeignPrincipalUsd() {
        double total = 0;
        for (Debt d : debts) if (d.isForeign()) total += d.principalInCurrency();
        return total;
    }

    /** What it owes at home. */
    public double getDomesticPrincipal() {
        double total = 0;
        for (Debt d : debts) if (!d.isForeign()) total += d.getOustandingPrincipal();
        return total;
    }

    /** Next month's USD coupon bill, in dollars. */
    public double getForeignCouponUsd() {
        double total = 0;
        for (Debt d : debts) if (d.isForeign()) total += d.couponInCurrency();
        return total;
    }

    public boolean hasForeignDebt() {
        for (Debt d : debts) if (d.isForeign()) return true;
        return false;
    }

    /* =======================================================================
       WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING
       =======================================================================

       CHEAPER UP FRONT, AND THAT IS THE TRAP.

       The world's base rate sits below the city's own floor, so the first USD
       bond a city issues really is cheaper than the domestic one beside it -
       which is precisely why governments take them, and precisely why the bill
       arrives later in a currency they do not print. The screen quotes both
       together so the choice is informed rather than hidden; it does not make
       the choice safe.

       PRICED ON EXPORTS, not on GDP or tax revenue. A foreign lender is not
       repaid out of the local economy and cannot be repaid in local money: it
       is repaid in dollars, and the only dollars the city has ever earned came
       from selling something abroad. Debt-to-exports is the measure sovereign
       analysts actually use for exactly this reason.

       AND THE MEASURE IS TAKEN IN LOCAL MONEY, which is what closes the loop.
       getForeignPrincipal() rises when the currency falls, so a devaluation
       worsens the ratio without a cent being borrowed, which raises the
       premium, which makes the next bond dearer. Devalue, the burden rises,
       solvency looks worse, the currency falls further. Mexico 1994, Asia 1997,
       Argentina 2001, Turkey repeatedly. It is not modelled as a special case;
       it falls out of measuring the right two things against each other.
       ======================================================================= */

    /** The world's price of money. Deliberately below the city's own floor. */
    public static final double WORLD_BASE_RATE = .02;

    /** What the world adds on top of that, at the city's very worst. */
    public static final double MAX_COUNTRY_PREMIUM = .16;

    /** USD debt at this many years of exports, and the solvency term maxes out. */
    public static final double FULL_STRESS_EXPORT_YEARS = 8;

    /**
     * A year's USD bill at this share of a year's exports, and the service term
     * maxes out. A quarter of export earnings going to foreign creditors is
     * roughly where real sovereigns start being priced as distressed.
     */
    public static final double FULL_STRESS_SERVICE_SHARE = .25;

    /** How the two halves of country risk are weighted. Can it pay, and can it pay NOW. */
    public static final double SOLVENCY_WEIGHT = .60, SERVICE_WEIGHT = .40;

    /** Above this many years of exports the window shuts outright. */
    public static final double WINDOW_SHUT_EXPORT_YEARS = 14;

    /** ...and above this share of exports going out in service, likewise. */
    public static final double WINDOW_SHUT_SERVICE_SHARE = .45;

    /** What a default abroad adds to the premium the day it happens. */
    public static final double DEFAULT_SCAR = .10;

    /** ...and how much of the scar is left after each month. Half-life ~5 years. */
    public static final double SCAR_DECAY = .9885;

    private double monthlyExports;
    private double importCover = Double.MAX_VALUE;
    private double defaultScar;
    private int monthsSinceForeignDefault = -1;

    /**
     * The two figures the world prices the city on, handed down each month.
     *
     * @param monthlyExportsLocal a trailing month of exports, in local money
     * @param cover               months of import cover
     */
    public void setTrade(double monthlyExportsLocal, double cover) {
        this.monthlyExports = Math.max(0, monthlyExportsLocal);
        this.importCover = cover;
    }

    public double getMonthlyExports()    { return monthlyExports; }
    public double getMonthlyTaxRevenue() { return monthlyTaxRevenue; }

    /**
     * Tears every piece of foreign paper off the books.
     *
     * @return what was written off, in local money at today's rate
     */
    public double repudiateForeignDebt() {
        double written = 0;
        Iterator<Debt> it = debts.iterator();
        while (it.hasNext()) {
            Debt d = it.next();
            if (d.isForeign()) {
                written += d.getOustandingPrincipal();
                it.remove();
            }
        }
        return written;
    }
    public double getImportCover()    { return importCover; }

    /** Can it pay at all: USD debt against a year of exports. */
    public double solvencyStress() {
        return solvencyStressAt(getForeignPrincipal());
    }

    /**
     * ...priced with a proposed bond already on the books.
     *
     * A quote that ignores the loan being quoted is the same bug the domestic
     * curve had and quoteRate() exists to avoid: the city is told a rate that
     * stops being true the instant it accepts.
     *
     * @param owed foreign principal in LOCAL money, the new paper included
     */
    public double solvencyStressAt(double owed) {
        if (owed <= 0) return 0;
        double annual = monthlyExports * 12;
        if (annual <= 0) return 1;                       // owes dollars, earns none
        return Math.min(1, (owed / annual) / FULL_STRESS_EXPORT_YEARS);
    }

    /**
     * Can it pay NOW: next year's USD bill against next year's export earnings.
     *
     * THE DEBT SERVICE RATIO, which is the other half of every sovereign credit
     * assessment and the one that actually times a crisis. A country can carry
     * a large stock of foreign debt indefinitely if the schedule is long; it
     * fails when too much of it comes due at once against what it earns.
     *
     * THIS REPLACED A RESERVE-COVER TERM, and the reason is worth recording.
     * Cover was the obvious measure and it was wrong twice over: importCover()
     * returned 0 for any city whose cumulative foreign position was negative
     * (the vault and the cumulative balance were one number then), which was
     * most of them and said nothing about creditworthiness, and it
     * meant a city with NO foreign debt at all was quoted a 5.6% risk premium
     * for the privilege of not owing anybody anything. Measured, on a fixture
     * that had never borrowed a dollar.
     *
     * Reserves still matter - they are what defends the currency, which is
     * phase 2's job and shown on the trade screen. They are not what a lender
     * is looking at.
     */
    public double serviceStress() {
        return serviceStressAt(nextYearService());
    }

    public double serviceStressAt(double service) {
        if (service <= 0) return 0;
        double annual = monthlyExports * 12;
        if (annual <= 0) return 1;
        return Math.min(1, (service / annual) / FULL_STRESS_SERVICE_SHARE);
    }

    /** Everything foreign paper demands over the next twelve months, in local money. */
    public double nextYearService() {
        double total = 0;
        for (Debt d : debts) {
            if (!d.isForeign()) continue;
            double[] flows = d.remainingCashFlows();
            for (int i = 0; i < Math.min(12, flows.length); i++) total += flows[i];
        }
        return total;
    }

    /** What the world adds to its base rate for lending to THIS city. */
    public double countryPremium() {
        return countryPremiumAt(getForeignPrincipal());
    }

    public double countryPremiumAt(double owed) {
        double scale = getForeignPrincipal() > 0 ? owed / getForeignPrincipal() : 1;
        double risk = SOLVENCY_WEIGHT * solvencyStressAt(owed)
                + SERVICE_WEIGHT * serviceStressAt(nextYearService() * Math.max(1, scale));
        return Math.min(MAX_COUNTRY_PREMIUM, MAX_COUNTRY_PREMIUM * risk + defaultScar);
    }

    /** The all-in annual rate on a new USD bond: the short end of the world's curve (foreignCurveRate()). */
    public double foreignRate() {
        return WORLD_BASE_RATE + countryPremium();
    }

    /**
     * THE WORLD'S CURVE (0.7.2): what the world charges this city for dollar
     * paper of this many months - the foreign rate, plus the SAME term
     * premium table the city's own curve carries (termPremium()). One curve
     * shape for both currencies: a lender tying money up for thirty years
     * wants paying for the thirty years whoever's money it is, and the
     * world's term premium is not a separate dial. What the city's central
     * bank holds does not compress it - it holds none of the world's paper.
     *
     * WHY (0.7.1's found-on-the-way): a dollar bond was VALUED at the city's
     * short rate (marketValue() read currentRate) and a dollar term loan
     * PRICED flat at the world's rate at every maturity. So the player's dial
     * moved what a dollar bond was worth, which nothing in the world would
     * do, and a dollar buyback priced off the city's curve rather than the
     * one it was sold on. Issue and buyback both read this now, which is what
     * keeps a dollar round trip neutral by construction, as the domestic one
     * is (RestructureCheck).
     */
    public double foreignCurveRate(int months) {
        return foreignRate() + termPremium(months);
    }

    /**
     * ...quoted with the proposed bond priced in.
     *
     * @param extraUsd face of the bond being contemplated, in dollars
     */
    public double quoteForeignRate(double extraUsd) {
        return WORLD_BASE_RATE
                + countryPremiumAt(getForeignPrincipal() + Math.max(0, extraUsd) * exchangeRate);
    }

    /** ...and at a maturity, on the world's curve (0.7.2): the quote plus the term premium for those months. */
    public double quoteForeignRate(double extraUsd, int months) {
        return quoteForeignRate(extraUsd) + termPremium(months);
    }

    /**
     * ...and priced with the proposed paper ON THE BOOKS, its own next year
     * of service included (0.7.2): the rate the world's curve will read for
     * this paper, at this maturity, the moment it is booked.
     *
     * WHY. quoteForeignRate(extraUsd) prices the proposed principal in, and
     * scales the service the city ALREADY owes by it - so a city with no
     * dollar paper was quoted a service term of nothing, and the paper, once
     * booked, was valued with its own coupons in that term. The buyback read
     * a dearer rate than the issue had. At 0.7.1, with a dollar bond valued at
     * the city's short rate, a dollar term round trip netted the city 10-22%
     * of the money raised; on the world's curve alone it still netted
     * 0.4-1%. Priced this way the issue and the buyback read the same premium
     * - this is countryPremium() as it will stand, computed before the paper
     * is booked - so the round trip is neutral by construction, the way the
     * domestic one is. Game.quoteForeign() walks the fixed point (the coupon
     * is struck on the rate, and the service on the coupon).
     *
     * @param proposed the paper as it would be booked, in dollars, not yet on the books
     */
    public double quoteForeignRate(Debt proposed, int months) {
        if (proposed == null) return foreignCurveRate(months);
        proposed.setExchangeRate(exchangeRate);
        double owed = getForeignPrincipal() + proposed.getOustandingPrincipal();
        double service = nextYearService();
        double[] flows = proposed.remainingCashFlows();
        for (int i = 0; i < Math.min(12, flows.length); i++) service += flows[i];
        double risk = SOLVENCY_WEIGHT * solvencyStressAt(owed) + SERVICE_WEIGHT * serviceStressAt(service);
        return WORLD_BASE_RATE + Math.min(MAX_COUNTRY_PREMIUM, MAX_COUNTRY_PREMIUM * risk + defaultScar)
                + termPremium(months);
    }

    /**
     * Whether anybody abroad is still willing to lend.
     *
     * A SUDDEN STOP, on conditions the player can read off the screen before it
     * happens rather than discover. Existing paper still has to be repaid on
     * schedule - that is what makes it dangerous - but no new dollars arrive.
     */
    public boolean foreignWindowOpen() {
        return foreignWindowReason() == null;
    }

    /** Why it is shut, in words, or null if it is open. */
    public String foreignWindowReason() {
        if (monthsSinceForeignDefault >= 0 && monthsSinceForeignDefault < 60) {
            return String.format("the city defaulted abroad %d months ago",
                    monthsSinceForeignDefault);
        }
        double annual = monthlyExports * 12;
        double owed = getForeignPrincipal();
        if (owed > 0 && annual <= 0) {
            return "the city owes dollars and sells nothing abroad";
        }
        if (annual > 0 && owed / annual > WINDOW_SHUT_EXPORT_YEARS) {
            return String.format("USD debt is %.0f years of exports",
                    owed / annual);
        }
        double service = nextYearService();
        if (annual > 0 && service / annual > WINDOW_SHUT_SERVICE_SHARE) {
            return String.format("%.0f%% of exports already goes to foreign creditors",
                    service / annual * 100);
        }
        return null;
    }

    /** Called the month the city fails to pay abroad. */
    public void markForeignDefault() {
        defaultScar = Math.min(MAX_COUNTRY_PREMIUM, defaultScar + DEFAULT_SCAR);
        monthsSinceForeignDefault = 0;
    }

    /** The scar fades, slowly, and the window reopens before the price does. */
    public void ageForeignStanding() {
        if (monthsSinceForeignDefault >= 0) monthsSinceForeignDefault++;
        defaultScar *= SCAR_DECAY;
        if (defaultScar < 1e-6) defaultScar = 0;
    }

    public double getDefaultScar()            { return defaultScar; }
    public int getMonthsSinceForeignDefault() { return monthsSinceForeignDefault; }

    /** Carried, because a scar that heals on reload is not a scar. */
    public double[] foreignStandingToSave() {
        return new double[] { defaultScar, monthsSinceForeignDefault };
    }

    public void restoreForeignStanding(double[] saved) {
        if (saved == null || saved.length < 2) return;
        defaultScar = saved[0];
        monthsSinceForeignDefault = (int) Math.round(saved[1]);
    }

    public void printDebtInfo(int currentMonth) {
        double totalPrincipal = 0;
        double totalMonthlyInterest = 0;

        System.out.println("\n================ DEBT PORTFOLIO ================");

        // 1. Sort by Maturity
        List<Debt> sortedDebts = new ArrayList<>(debts);
        sortedDebts.sort(Comparator.comparingInt(Debt::getMaturityMonth));

        // 2. Header with Column Spacing
        System.out.printf("%-10s | %-15s | %-12s | %-15s%n", "Maturity", "Type", "Principal", "Mo. Interest");
        System.out.println("------------------------------------------------------------");

        for (Debt debt : sortedDebts) {
            double principal = debt.getOustandingPrincipal();
            double interest = debt.getMonthlyInterestExpense(); // Assuming this getter exists

            totalPrincipal += principal;
            totalMonthlyInterest += interest;

            // Highlight if due this month
            String status = (debt.getMaturityMonth() == currentMonth) ? " [DUE NOW]" : "";

            System.out.printf("Month %-4d | %-15s | %-12s | %-15s%s%n",
                    debt.getMaturityMonth(),
                    debt.getType(),
                    formatter.format(principal),
                    formatter.format(interest),
                    status);
        }

        // NOTE: this used to assign the manager's allPrincipal field here, which
        // made the entire debt->interest feedback loop a side effect of printing.
        // getAllPrincipal() computes it on demand now; this printer is pure.
        System.out.println("------------------------------------------------------------");
        System.out.println("TOTAL OUTSTANDING PRINCIPAL: " + formatter.format(totalPrincipal));
        System.out.println("TOTAL MONTHLY INTEREST COST: " + formatter.format(totalMonthlyInterest));
        System.out.println("============================================================\n");
    }

    /**
     * THE CITY'S RATE: the SHORT END of the curve - the note's rate, no term
     * premium - which is what every screen means by "the city's rate", what
     * the bank's strain and the advisor read, and what the households' lender
     * prices off. A term bond is quoted at curveRate() for its maturity
     * (0.7.1); this one number stays the T-bill rate.
     */
    public double getRate() {
        return currentRate;
    }

    /**
     * What the bank's strain is adding to every rate in the city.
     *
     * Set once a month from Bank.ratePremium(). Held here rather than reached
     * for, because the debt market prices in several places and a rate that
     * changed under a quote half way through would be a quote nobody was
     * offered.
     */
    private double bankPremium;
    public void setBankPremium(double premium) { this.bankPremium = Math.max(0, premium); }
    public double getBankPremium()             { return bankPremium; }

    /**
     * The city's rate without the bank's strain in it (0.7.2): what the hot
     * money compares with the world's (CapitalFlows, MAX_SPREAD). A strained
     * bank's premium is what the bank charges for its own trouble, not a
     * return anybody abroad is offered; read as one, a strained bank drew
     * money in because it was strained. Suspected of 0.7.2's extra bank
     * failures and measured when it went in: on the eight default seeds it
     * moved none of them (GameVersion, 0.7.2).
     */
    public double getRateBeforeStrain()        { return currentRate - bankPremium; }

    /**
     * What the bank pays for the money it lends the city.
     *
     * Set beside the premium, from Bank.marginalCostOfFunds(), and for the
     * opposite job. The premium is what a STRAINED bank adds; this is what any
     * bank's money costs at all, and the city's rate may not go under it - see
     * floorRate().
     */
    private double costOfFunds;
    public void setCostOfFunds(double rate) { this.costOfFunds = Math.max(0, rate); }
    public double getCostOfFunds()          { return costOfFunds; }
    /**
     * Total outstanding principal across every live debt.
     *
     * NOTE: this used to return a cached field assigned in exactly one place -
     * inside printDebtInfo(). printStartOfMonth() only calls that printer
     * `if(reports)`, so the debt->interest feedback loop silently stopped working
     * whenever reports were off (which simulateMonths does by design). Observed:
     * a $700,000 bond sat outstanding for 100 months with the rate pinned at the
     * 1% floor, then jumped straight to the 20% ceiling the first time a report
     * printed. It went stale the other way too - a matured, removed bond kept
     * being priced in until the next printed report.
     *
     * The new JavaFX debt screen sums the list itself and never touched that
     * cache, so the side effect was disappearing entirely as the console reports
     * get retired. Computed on demand now; the list is small.
     */
    public double getAllPrincipal(){
        double total = 0;
        for (Debt debt : debts) {
            total += debt.getOustandingPrincipal();
        }
        return total;
    }

    //setters
    public void setGDP(double GDP) {
        this.GDP = GDP;
    }

    /** The month's tax take - the other half of what the market prices against. */
    public void setTaxRevenue(double monthlyTaxRevenue) {
        this.monthlyTaxRevenue = monthlyTaxRevenue;
    }

    /**
     * How far the city is overdrawn, pushed in from Game each month.
     *
     * Takes the CASH balance and keeps the negative part; a positive balance is
     * not credit and does not improve the rate.
     */
    public void setCashPosition(double cash) {
        this.overdraft = Math.max(0, -cash);
    }

    public double getOverdraft()  { return overdraft; }

    /**
     * What the treasury owes its central bank in advances (0.7.0), pushed in
     * with the overdraft. Priced like it, because it is the same borrowing a
     * month later: the overdraft at a month's end is advanced at the top of
     * the next, and before 0.7.0 it became an emergency note, which was
     * principal and priced. The market looks at everything the city owes.
     */
    private double advances;

    public void setAdvances(double owed) { this.advances = Math.max(0, owed); }
    public double getAdvances()          { return advances; }

    public List<Debt> getDebt() {
        return debts;
    }

    public void processAllDebts(Game game) {

        Iterator<Debt> iterator = debts.iterator();
        accretedForBank = 0;

        while (iterator.hasNext()) {
            Debt debt = iterator.next();

            /*
             * THE DISCOUNT ACCRETES (0.7.1), a month of it, before the month's
             * payments move the holders' shares: the bank's part is its
             * interest at the settle, the households' is theirs at maturity
             * (their paper is carried at market), and the central bank's was
             * taken when it bought, at face. See Debt.accrete().
             */
            if (!debt.isForeign()) {
                double out = debt.getOustandingPrincipal();
                double bankShare = out > 0 ? debt.bankPrincipal() / out : 0;
                double step = debt.accrete();
                if (step > 0) accretedForBank += step * bankShare;
            }

            debt.processMonth(game);

            if (debt.isMatured()) {
                iterator.remove();
            }

        }
        updateInterest();
    }

    /**
     * Face value of the discount notes outstanding.
     *
     * The screens need it because a note is the one instrument whose interest
     * is not a monthly expense - it was taken out of the proceeds at issue - so
     * a city financed entirely on notes shows a Debt Interest line of zero
     * while plainly owing money. That reads as a bug, and the honest answer is
     * a sentence rather than a number. See ShortTermTBill.
     */
    public double getNotePrincipal() {
        double total = 0;
        for (Debt d : debts) {
            if (d instanceof ShortTermTBill) total += d.getOustandingPrincipal();
        }
        return total;
    }

    /**
     * Everything the city owes, including what it is overdrawn and what it
     * owes the central bank in advances.
     *
     * NOT the same as getAllPrincipal(), which is bonds and bills only. This is
     * what the market is actually looking at when it decides what to charge.
     */
    public double getPricedDebt() {
        return getAllPrincipal() + Math.max(0, overdraft) + advances;
    }

    /**
     * Takes one bond off the books. The caller has already paid for it.
     *
     * Deliberately dumb: no cash, no pricing, no policy. Game.repurchaseDebt()
     * owns the decision and the money, and this owns the list - which keeps the
     * one irreversible step (a debt disappearing) in the class that is allowed
     * to change the list, and keeps DebtManager from needing to know what the
     * city can afford.
     *
     * @return true if that bond was on the books and is not any more
     */
    public boolean retire(Debt debt) {
        return debts.remove(debt);
    }

    /** What every outstanding bond would cost to buy back today: each at the curve's rate for the months it has left (0.7.1). */
    public double getTotalMarketValue() {
        double total = 0;
        for (Debt debt : debts) {
            total += marketValue(debt);
        }
        return total;
    }

    /**
     * What one measure adds to the rate: a linear ramp, then flat.
     *
     * PIECEWISE, AND THAT IS THE POINT. Below FULL_STRESS_MULTIPLE years of
     * whatever is being measured against, each extra dollar of debt costs the
     * same small amount of rate. Above it, the measure has said everything it
     * has to say and stops. A single unbounded term is what let one bad
     * denominator - a city whose GDP had not caught up yet, say - drag the whole
     * quote to the ceiling on its own.
     *
     * @param annualCapacity a year of GDP, or a year of tax revenue
     */
    private double spreadFor(double debt, double annualCapacity) {

        if (debt <= 0) return 0;

        // Nothing to measure against is the worst case, not a free pass: a
        // borrower whose capacity to repay cannot be established pays the full
        // ten points for that measure.
        if (annualCapacity <= 0) return MAX_SPREAD_PER_MEASURE;

        double yearsOfIt = debt / annualCapacity;
        return MAX_SPREAD_PER_MEASURE * Math.min(1.0, yearsOfIt / FULL_STRESS_MULTIPLE);
    }

    /**
     * The curve itself: a floor, plus up to ten points from each measure.
     *
     * Either one alone is a bad measure. GDP flatters a city that cannot tax
     * what it produces, and revenue alone would let a city with a tiny economy
     * borrow freely by taxing it to death. Between them they say "how much of
     * this can you carry", and because each is capped separately, being poor on
     * one measure and sound on the other is priced as exactly that - half of the
     * worst case, not the whole of it.
     *
     * The market always lends. There is a price at which it will do anything.
     *
     * @return the city's own credit spread, PLUS what the bank charges for
     *         funds - and the second half has to be here rather than at the one
     *         call site that sets the standing rate.
     *
     *         It was, for about ten minutes. quoteRate() prices a NEW loan by
     *         iterating priceAt() against the face it would create, so a
     *         premium added afterwards was in the repurchase price and not in
     *         the issue price - and a bond bought back at a higher rate than it
     *         was sold at is worth less than the city received for it. Free
     *         money, at $567,131 over eight round trips, caught by
     *         RestructureCheck's "can the city print money with this?" section,
     *         which exists for exactly this failure and has now caught it twice.
     *
     *         Added outside the clamp on purpose: the ceiling is what a
     *         hopeless city pays on its OWN merits, and the funding premium is
     *         a fact about the money rather than about the borrower.
     */
    private double priceAt(double debt) {
        return priceAt(debt, 0);
    }

    /**
     * ...at a maturity (0.7.1): the same credit judgement, clamped the same
     * way, plus the term premium for that many months less what the central
     * bank's holdings compress of it, and the bank's premium outside it all.
     * The shape is added OUTSIDE the clamp, so a city at the ceiling still has
     * a curve - the ceiling is what a hopeless borrower pays on its merits,
     * and the premium is the price of time, which it pays on top. Zero months
     * is the short end: no premium, no compression, and exactly the figure
     * the city was quoted before there was a curve.
     */
    private double priceAt(double debt, int months) {
        double rate = floorRate()
                + spreadFor(debt, GDP * 12)
                + spreadFor(debt, monthlyTaxRevenue * 12);
        double credit = Math.max(MIN_RATE, Math.min(rate, ceilingRate()));
        return credit + termShape(months) + bankPremium;
    }

    /* =======================================================================
       THE CURVE (0.7.1)
       =======================================================================

       Jerus: "i think that the short term rates, aka the one you choose,
       those should be basically the tbill rate, the others change just as in
       real life."

       Until 0.7.1 a thirty-year bond and a six-month note were priced at the
       same rate - one number for every maturity, which no market has ever
       done. A lender who ties money up for thirty years wants paying for the
       thirty years: inflation might return, rates might rise, the city might
       change. So:

           rate(months) = policy + termPremium(months) - compression(months)
                          + credit spread (+ the bank's premium, as ever)

       THE SHORT END IS THE DIAL. A note (3-12 months) carries no premium: it
       is the T-bill rate, Jerus's "the one you choose", and getRate() - the
       standing "city's rate" the screens and the bank's strain read - stays
       exactly that. The premium is a table of five points at the five term
       maturities, linear between them, linear from nothing at a year to the
       ten-year point (so a serial bond's final maturity prices on the same
       curve), and flat past fifty years.

       THE CREDIT SPREAD IS THE SAME AT EVERY MATURITY, so a hike moves the
       whole curve up in parallel and a city that over-borrows pays more at
       every point of it. THE COMPRESSION is what the central bank's holdings
       buy (CentralBank, THE HOLDINGS DIAL): the premium times the share of
       the city's term paper it holds, over the most it may hold. It never
       touches the short end, which has no premium to compress, and at the
       maximum holding the premium is gone.

       EVERY PRICE OF CITY PAPER GOES THROUGH curveRate() or quoteRate() at a
       maturity: the three quotes hand their duration in, a buyback and the
       market value of a bond price at the months it has LEFT, the central
       bank buys at it, the households sell back at it. Issue and buyback off
       one function is what keeps RestructureCheck's round trip neutral by
       construction.
       ======================================================================= */

    /** The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. */
    public static final double TERM_PREMIUM_10Y = .0050;

    /** ...on twenty-year money. */
    public static final double TERM_PREMIUM_20Y = .0090;

    /** ...on thirty-year money. */
    public static final double TERM_PREMIUM_30Y = .0115;

    /** ...on forty-year money. */
    public static final double TERM_PREMIUM_40Y = .0135;

    /** ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. */
    public static final double TERM_PREMIUM_50Y = .0150;

    /** The table, at 10, 20, 30, 40 and 50 years - LongTermBond.MATURITIES. */
    private static final double[] TERM_PREMIUM = {
            TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TERM_PREMIUM_50Y };

    /**
     * What a lender adds for tying money up this many months, before the
     * central bank compresses any of it.
     *
     * Nothing for a year or less - the note, the T-bill. Linear from nothing
     * at twelve months to TERM_PREMIUM_10Y at ten years, linear between the
     * table's points, and TERM_PREMIUM_50Y from fifty years on.
     */
    public static double termPremium(int months) {
        if (months <= 12) return 0;
        if (months <= 120) return TERM_PREMIUM_10Y * (months - 12) / (120.0 - 12);
        if (months >= 600) return TERM_PREMIUM_50Y;
        int below = months / 120 - 1;                   // 0 at 10y .. 3 at 40y
        double at = (months - (below + 1) * 120) / 120.0;
        return TERM_PREMIUM[below] + (TERM_PREMIUM[below + 1] - TERM_PREMIUM[below]) * at;
    }

    /**
     * What the central bank's holdings take off the premium at this many
     * months: the premium, times the share of the city's term paper it
     * actually holds (not its target) over CentralBank.MAX_QE_SHARE, times
     * CentralBank.QE_COMPRESSION. Nothing at the short end, which has no
     * premium; the whole premium at the maximum holding.
     */
    public double compression(int months) {
        double premium = termPremium(months);
        if (premium <= 0) return 0;
        double held = centralBankShareOfTerm();
        if (held <= 0) return 0;
        return premium * Math.min(1, held / CentralBank.MAX_QE_SHARE) * CentralBank.QE_COMPRESSION;
    }

    /** The premium less the compression: the curve's shape over the short end. */
    private double termShape(int months) {
        if (months <= 12) return 0;
        return termPremium(months) - compression(months);
    }

    /**
     * THE CURVE: what the city's paper of this many months is worth to a
     * lender today - the standing rate at that maturity, on what the city owes
     * now. At twelve months or less it is getRate().
     */
    public double curveRate(int months) {
        return priceAt(getPricedDebt(), months);
    }

    /**
     * What this paper would fetch today: the present value of what it still
     * owes at the curve's rate for the months it has left - the city's curve
     * for its own paper, the world's (foreignCurveRate()) for a dollar bond
     * since 0.7.2, which was valued at the city's short rate until then, so
     * the dial moved it.
     */
    public double marketValue(Debt paper) {
        if (paper == null) return 0;
        if (paper.isForeign()) return paper.getMarketValue(foreignCurveRate(paper.getRemainingMonths()));
        return paper.getMarketValue(curveRate(paper.getRemainingMonths()));
    }

    /** True for the city's own term paper - serial and term, not the notes: what the central bank's dial holds. */
    public static boolean isTermPaper(Debt paper) {
        return paper != null && !paper.isForeign() && !(paper instanceof ShortTermTBill);
    }

    /** The city's own term paper outstanding, at face: the base of the holdings dial. */
    public double termPrincipal() {
        double total = 0;
        for (Debt d : debts) if (isTermPaper(d)) total += d.getOustandingPrincipal();
        return total;
    }

    /** The share of it the central bank holds - the compression's measure. Zero with none outstanding. */
    public double centralBankShareOfTerm() {
        double term = 0, held = 0;
        for (Debt d : debts) {
            if (!isTermPaper(d)) continue;
            term += d.getOustandingPrincipal();
            held += d.centralBankPrincipal();
        }
        return term > 0 ? held / term : 0;
    }

    /* ---------------------- who holds it (0.7.1) ---------------------- */

    /** What the city's households hold of its own paper, at face. The sum of every cell's paper; HoldersCheck asserts it. */
    public double householdPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.householdPrincipal();
        return total;
    }

    /** ...the central bank, at face. CentralBank.getPaperHeld() is the same figure from the other side. */
    public double centralBankPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.centralBankPrincipal();
        return total;
    }

    /** ...and the commercial bank: the domestic principal less the other two. What its book carries. */
    public double bankPrincipal() {
        double total = 0;
        for (Debt d : debts) total += d.bankPrincipal();
        return total;
    }

    /**
     * ...of which the paper the bank has PAID for: what its book carries. Paper
     * sold between two presses is on nobody's book until the settle - the
     * treasury has the cash, and what the buyers owe is carried against the
     * bank's pool in MoneyAudit - so a city reloaded between the issue and
     * the settle carries the same book as the one that was saved.
     */
    public double bankBook() {
        double total = 0;
        for (Debt d : debts) if (d.getSettleDue() <= 0) total += d.bankPrincipal();
        return total;
    }

    /** Each holder's book at the curve: 0 the households, 1 the central bank, 2 the bank. */
    public double[] bookValues() {
        double[] out = new double[3];
        for (Debt d : debts) {
            if (d.isForeign()) continue;
            double face = d.getOustandingPrincipal();
            if (face <= 0) continue;
            double perFace = marketValue(d) / face;
            out[0] += d.householdPrincipal() * perFace;
            out[1] += d.centralBankPrincipal() * perFace;
            out[2] += d.bankPrincipal() * perFace;
        }
        return out;
    }

    /**
     * The households' book at market over its face: ONE RATIO A MONTH, which
     * is what their paper counts for in their net worth and what the desk
     * pays them for it. At face - 1 - when they hold none.
     */
    public double householdBookRatio() {
        double face = householdPrincipal();
        return face > 0 ? bookValues()[0] / face : 1;
    }

    /** What the households' paper yields at today's curve, weighted by what they hold of each piece; the short rate when they hold none. */
    public double householdBookYield() {
        double face = 0, weighted = 0;
        for (Debt d : debts) {
            double held = d.householdPrincipal();
            if (held <= 0) continue;
            face += held;
            weighted += held * curveRate(d.getRemainingMonths());
        }
        return face > 0 ? weighted / face : getRate();
    }

    /**
     * The discount the bank has not yet earned on what it holds: every piece's
     * unaccreted remainder, in the share of its principal the bank holds. The
     * bank carries it against its book (Bank.setUnearnedDiscount()), so its
     * equity does not jump at the settle and the discount reaches its income
     * only as it accretes.
     */
    public double bankUnearnedDiscount() {
        double total = 0;
        for (Debt d : debts) {
            if (d.isForeign() || d.getSettleDue() > 0) continue;   // not on its book yet: see bankBook()
            total += d.unaccretedOn(d.bankPrincipal());
        }
        return total;
    }

    /** This month's accretion on the bank's share, struck in processAllDebts() before the month's payments; handed to the bank at the settle. */
    private double accretedForBank;

    public double getAccretedForBank() { return accretedForBank; }

    /* ===================================================================
       THE RATE, TAKEN APART - for the Finances screen and nothing else.

       priceAt() is four terms added together, and until these existed the game
       could tell a player their rate was 1.23% and not one thing about WHY, or
       which of their decisions would move it. A quoted price with no visible
       components is a price a player can only respond to by borrowing less.

       Pure reads of the same functions priceAt() uses. Nothing here is a second
       definition: change the pricing and these change with it.
       =================================================================== */

    /**
     * What the city would be quoted if the policy rate were this instead.
     *
     * priceAt() with the base rate swapped and nothing else touched, so the
     * Policies screen can show a player what moving the dial does to their own
     * borrowing before they move it. It lives here rather than in the screen
     * for the obvious reason: the day the pricing changes, this changes with
     * it, and a copy in the UI would not.
     *
     * PURE. Reads the same spreads priceAt() reads and sets nothing.
     */
    public double rateAtPolicy(double policy) {
        double floor   = Math.max(MIN_RATE,
                Math.max(policy, costOfFunds + Bank.MIN_MARGIN));
        double ceiling = policy + 2 * MAX_SPREAD_PER_MEASURE;
        double rate = floor + gdpSpread() + revenueSpread();
        return Math.max(MIN_RATE, Math.min(rate, ceiling)) + bankPremium;
    }

    /** The floor everybody pays: the policy rate, or the bank's cost of funds if that is higher. */
    public double baseComponent() { return floorRate(); }

    /** What the debt costs against the size of the economy. */
    public double gdpSpread() { return spreadFor(getPricedDebt(), GDP * 12); }

    /** ...and against what the city can actually collect. */
    public double revenueSpread() { return spreadFor(getPricedDebt(), monthlyTaxRevenue * 12); }

    /** How much of the worst case each measure has used up, 0 to 1. */
    public double gdpStress() {
        return MAX_SPREAD_PER_MEASURE > 0 ? gdpSpread() / MAX_SPREAD_PER_MEASURE : 0;
    }

    public double revenueStress() {
        return MAX_SPREAD_PER_MEASURE > 0 ? revenueSpread() / MAX_SPREAD_PER_MEASURE : 0;
    }

    /** The most either measure can add on its own. */
    public static double maxSpreadPerMeasure() { return MAX_SPREAD_PER_MEASURE; }

    /** Years of GDP, or of revenue, at which a measure has said all it can. */
    public static double fullStressMultiple() { return FULL_STRESS_MULTIPLE; }

    /** A year of output, as the market is pricing it. */
    public double annualCapacityGdp() { return GDP * 12; }

    /** ...and a year of tax, likewise. */
    public double annualCapacityRevenue() { return monthlyTaxRevenue * 12; }

    /** True when the quoted rate is pinned at the top of the curve. */
    public boolean atCeiling() {
        return getRate() >= ceilingRate() + bankPremium - 1e-9;
    }

    /**
     * What a spotless city pays: the policy rate - since 0.7.0, see THE FLOOR
     * IS REAL NOW - but never less than the money costs the bank that lends it.
     *
     * THE DISCOUNT SURVIVED THIS, AND STOPPED BEING A SUBSIDY THE LENDER PAID
     * (2026-09-13), until 0.7.0 deleted it outright; the note is kept as the
     * history. CITY_DISCOUNT was two points UNDER the policy rate, and the
     * bank then funded itself at two points OVER it. The city therefore
     * borrowed at a guaranteed four-point loss to its own bank, by
     * construction, on every dollar the bank's deposits did not cover - and
     * the bank had no say in how much of it to hold, because
     * Game.refreshBank() set cityBook to the whole of the principal.
     *
     * Measured on Jerus's own save, 2026-09-13: $18.2bn of thirty-year paper
     * issued in month 144 at 0.625% a year, 91.6% of a book yielding 1.307%
     * against wholesale funding at 5.593%. The bank had failed eighteen times
     * by month 354 and was three months from the nineteenth. See
     * claude/the-bond-that-broke-the-bank.md.
     *
     * NOTE WHAT THIS IS NOT. It is not a re-tightening of the stress curve.
     * FULL_STRESS_MULTIPLE stays at 150 years and MAX_SPREAD_PER_MEASURE at
     * five points, exactly as gentle as Jerus asked for them - a city can
     * still borrow a large multiple of its economy without being priced as
     * distressed. What it can no longer do is borrow below cost.
     */
    public double floorRate() {
        return Math.max(MIN_RATE,
                Math.max(baseRate, costOfFunds + Bank.MIN_MARGIN));
    }

    /** What a hopeless one pays - both measures maxed out. */
    public double ceilingRate() {
        return baseRate + 2 * MAX_SPREAD_PER_MEASURE;
    }

    /**
     * Re-prices the standing rate off what the city owes right now.
     *
     * NOTE: the old version did `if (GDP == 0) GDP = allPrincipal;`, permanently
     * overwriting the GDP field with a debt figure. That corruption then fed
     * every later rate calculation. Uses a local fallback instead.
     */
    public void updateInterest() {
        /*
         * ...PLUS WHAT THE BANK IS CHARGING FOR BEING STRAINED.
         *
         * The city's own paper is priced on its debt against its tax base, and
         * that is still the whole of the credit judgement. What the bank adds
         * is the price of the MONEY, not of the borrower: a city whose bank is
         * lent out past its deposits is funding itself at the central bank's
         * window (abroad, until 0.7.0), and a city with
         * no bank at all is borrowing from strangers who have never heard of
         * it. Eighteen points, at the worst, which is what a founding city with
         * no branch now pays until it builds one. See Bank.ratePremium().
         */
        currentRate = priceAt(getPricedDebt());
    }

    /**
     * What a NEW loan of this size would cost - priced with itself included.
     *
     * WHY THIS IS NOT JUST priceAt(debt + amount)
     *
     * The face value of a loan depends on the rate (a T-Bill discounts by it, a
     * bond takes a premium off it), and the rate now depends on the face value.
     * That is a fixed point, not a formula. It needs no calculus though: the
     * spread is small and the curve is clamped at both ends, so iterating the
     * two definitions against each other contracts onto the answer in three or
     * four passes. Six, for margin.
     *
     * The caller supplies faceOf(), because each instrument grosses a request up
     * differently - and getting that wrong is the whole point of the exercise.
     * Pricing off the balance sheet BEFORE the loan is what let a debt-free city
     * borrow ten million at one percent, which is not a thing that happens.
     *
     * @param requested what the city wants to receive
     * @param faceOf    given a rate, what the city would end up owing
     */
    public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf) {
        return quoteRate(requested, faceOf, 0);
    }

    /**
     * ...at a maturity (0.7.1): the same fixed point on the curve's rate for
     * that many months. The three instruments hand their duration in, so the
     * issue and the buyback price off the same curve - see THE CURVE.
     */
    public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf, int months) {

        double existing = debtAfterProceedsOf(requested);
        double rate = priceAt(existing + Math.max(0, requested), months);

        for (int i = 0; i < QUOTE_ITERATIONS; i++) {
            double face = faceOf.applyAsDouble(rate);
            if (!Double.isFinite(face) || face < 0) break;
            rate = priceAt(existing + face, months);
        }
        return rate;
    }

    /** Straight-line version for instruments whose face value IS the request. */
    public double quoteRate(double requested) {
        return quoteRate(requested, 0);
    }

    /** ...at a maturity. */
    public double quoteRate(double requested, int months) {
        return priceAt(debtAfterProceedsOf(requested) + Math.max(0, requested), months);
    }

    /**
     * The debt the loan lands ON TOP OF - which is not simply what is owed now.
     *
     * The cash a loan hands over pays the overdraft down, so an overdrawn city
     * borrowing its way out is not left owing both. Counting both would price
     * the hole twice and quote a rate for a balance sheet that will not exist a
     * moment after the money arrives.
     *
     * The emergency T-Bill was exactly this case until 0.7.0: it was issued
     * precisely to cover the gap, so what it should be priced against is the
     * bonds plus the bill itself, not the bonds plus the bill plus the gap it
     * is closing. The central bank's advances are the same case now: cash
     * above zero repays them first thing, so a note's proceeds clear them
     * after the overdraft.
     */
    private double debtAfterProceedsOf(double received) {
        double owedShort = Math.max(0, overdraft) + advances;
        double clears = Math.min(owedShort, Math.max(0, received));
        return getAllPrincipal() + owedShort - clears;
    }

    public void clearDebts() {
        debts.clear();
    }

    public void setDebt(List<Debt> debts) {
        this.debts = debts;
        setExchangeRate(exchangeRate);   // the load path's half of the rule above
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    /** The city's debt book, in the new unit. Rates and premiums do not move. */
    public void redenominate(double scale) {
        overdraft *= scale;
        GDP *= scale;
        monthlyTaxRevenue *= scale;
        monthlyExports *= scale;
        exchangeRate *= scale;
        for (Debt paper : getDebt()) {
            if (paper != null) paper.redenominate(scale);
        }
    }

}
