package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class DebtManager {

    /* =======================================================================
       THE POLICY RATE
       =======================================================================

       This was a constant, and it was the most load-bearing constant in the
       game without anybody having decided anything about it. Every price of
       money in the city is built on it: floorRate() and ceilingRate() are it
       plus and minus a spread, the city's own borrowing rate sits between them,
       and Bank.fundToCover() is handed the result and strikes the DEPOSIT rate
       and the wholesale FUNDING rate off it. Since phase 4 the carry trade
       reads the gap between those and the world's rate.

       So a central bank does not need new plumbing here. It needs to own this
       number. Everything downstream already listens.

       WHAT MOVING IT DOES, in the order the ECB lists the channels:
         - the bank's deposit and lending rates, immediately
         - what the city itself pays to borrow
         - how much the bank will lend at all, through the capital and funding
           limits, which is the channel that actually bites
         - the carry spread, so foreign money arrives or leaves
         - and, since this phase, the exchange rate directly

       Bounded at both ends because a policy rate is a decision, not a wish: no
       central bank sets a negative nominal rate by typing one, and past about a
       fifth the instrument stops transmitting and starts destroying.
    */
    public static final double MIN_POLICY_RATE = .0;
    public static final double MAX_POLICY_RATE = .25;

    /** What the city's central bank charges. The player's dial. */
    private double baseRate = .03;

    public double getPolicyRate() { return baseRate; }

    public void setPolicyRate(double rate) {
        baseRate = Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, rate));
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
     * What a rule would set, given this month's inflation.
     *
     * INFLATION ONLY, WITH NO OUTPUT GAP, and that is a judgement about this
     * model rather than about monetary policy. A Taylor rule normally carries a
     * second term in the output gap; this city runs 34% unemployment and has
     * for its whole history, so any gap measure built on it would read as
     * permanent enormous slack and advise the floor for ever. The game has a
     * trustworthy price index and does not have a trustworthy output gap, so
     * the rule uses the one it has.
     *
     * Advisory. It is shown beside the dial with its reasoning; it never moves
     * anything on its own.
     */
    public double advisedPolicyRate(double inflation) {
        double advised = NEUTRAL_RATE + TAYLOR_WEIGHT * (inflation - INFLATION_TARGET);
        return Math.max(MIN_POLICY_RATE, Math.min(MAX_POLICY_RATE, advised));
    }

    /** ...and why, in words, for the screen. */
    public String adviceReason(double inflation) {
        double advised = advisedPolicyRate(inflation);
        if (Math.abs(inflation - INFLATION_TARGET) < .002) {
            return String.format("Inflation is %.1f%%, on its %.0f%% target. Hold at %.2f%%.",
                    inflation * 100, INFLATION_TARGET * 100, advised * 100);
        }
        return String.format(
                "Inflation is %.1f%% against a %.0f%% target, so the rule says %.2f%% "
                + "- %s a point of inflation by %.1f points of rate, which is what it "
                + "takes to make money genuinely dearer rather than only nominally.",
                inflation * 100, INFLATION_TARGET * 100, advised * 100,
                inflation > INFLATION_TARGET ? "meeting" : "giving back",
                TAYLOR_WEIGHT);
    }
    private double currentRate = baseRate - .02;
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
     * floor and the rate runs 1% to 11%. Neither measure can price the city on
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
     * loan itself priced in:
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

    public void addShortTermTBill(double faceValue, int months, int monthStarted) {
        addShortTermTBill(faceValue, months, monthStarted, false);
    }

    public void addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign) {
        book(new ShortTermTBill(faceValue, months, monthStarted, foreign));
    }

    public void addMediumTermBond(double faceValue, int months, int monthStarted, double rate) {
        addMediumTermBond(faceValue, months, monthStarted, rate, false);
    }

    public void addMediumTermBond(double faceValue, int months, int monthStarted,
                                  double rate, boolean foreign) {
        book(new MediumTermBond(faceValue, months, monthStarted, rate, foreign));
    }

    public void addLongTermBond(double faceValue, int months, int monthStarted, double rate) {
        addLongTermBond(faceValue, months, monthStarted, rate, false);
    }

    public void addLongTermBond(double faceValue, int months, int monthStarted,
                                double rate, boolean foreign) {
        book(new LongTermBond(faceValue, months, monthStarted, rate, foreign));
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
    private void book(Debt paper) {
        paper.setExchangeRate(exchangeRate);
        debts.add(paper);
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
     * returns 0 for any city whose cumulative foreign position is negative,
     * which is most of them and says nothing about creditworthiness, and it
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

    /** The all-in annual rate on a new USD bond. */
    public double foreignRate() {
        return WORLD_BASE_RATE + countryPremium();
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

    //getters
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

    public List<Debt> getDebt() {
        return debts;
    }

    public void processAllDebts(Game game) {

        Iterator<Debt> iterator = debts.iterator();

        while (iterator.hasNext()) {
            Debt debt = iterator.next();

            debt.processMonth(game);

            if (debt.isMatured()) {
                iterator.remove();
            }

        }
        updateInterest();
    }

    /**
     * Everything the city owes, including what it is overdrawn.
     *
     * NOT the same as getAllPrincipal(), which is bonds and bills only. This is
     * what the market is actually looking at when it decides what to charge.
     */
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

    public double getPricedDebt() {
        return getAllPrincipal() + Math.max(0, overdraft);
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

    /** What every outstanding bond would cost to buy back at today's rate. */
    public double getTotalMarketValue() {
        double total = 0;
        for (Debt debt : debts) {
            total += debt.getMarketValue(currentRate);
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
     */
    /**
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
        double rate = floorRate()
                + spreadFor(debt, GDP * 12)
                + spreadFor(debt, monthlyTaxRevenue * 12);
        return Math.max(MIN_RATE, Math.min(rate, ceilingRate())) + bankPremium;
    }

    /** What a spotless city pays. */
    public double floorRate() {
        return Math.max(MIN_RATE, baseRate - 0.02);
    }

    /** What a hopeless one pays - both measures maxed out. */
    public double ceilingRate() {
        return (baseRate - 0.02) + 2 * MAX_SPREAD_PER_MEASURE;
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
         * lent out past its deposits is funding itself abroad, and a city with
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

        double existing = debtAfterProceedsOf(requested);
        double rate = priceAt(existing + Math.max(0, requested));

        for (int i = 0; i < QUOTE_ITERATIONS; i++) {
            double face = faceOf.applyAsDouble(rate);
            if (!Double.isFinite(face) || face < 0) break;
            rate = priceAt(existing + face);
        }
        return rate;
    }

    /** Straight-line version for instruments whose face value IS the request. */
    public double quoteRate(double requested) {
        return priceAt(debtAfterProceedsOf(requested) + Math.max(0, requested));
    }

    /**
     * The debt the loan lands ON TOP OF - which is not simply what is owed now.
     *
     * The cash a loan hands over pays the overdraft down, so an overdrawn city
     * borrowing its way out is not left owing both. Counting both would price
     * the hole twice and quote a rate for a balance sheet that will not exist a
     * moment after the money arrives.
     *
     * The emergency T-Bill is exactly this case: it is issued precisely to
     * cover the gap, so what it should be priced against is the bonds plus the
     * bill itself, not the bonds plus the bill plus the gap it is closing.
     */
    private double debtAfterProceedsOf(double received) {
        double clearsOverdraft = Math.min(Math.max(0, overdraft), Math.max(0, received));
        return getAllPrincipal() + Math.max(0, overdraft) - clearsOverdraft;
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
