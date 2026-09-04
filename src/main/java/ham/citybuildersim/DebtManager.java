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

    private double baseRate = .03;
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
     * Two measures, ten points each, so the spread runs 0 to 20 points over the
     * floor and the rate runs 1% to 21%. Neither measure can price the city on
     * its own: a city with no economy but plenty of revenue, or the reverse, is
     * capped at half the punishment.
     */
    private static final double MAX_SPREAD_PER_MEASURE = 0.10;

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
     * Fifty years of revenue is genuinely all-in, so that is where a measure
     * reaches its ten points, and it ramps linearly to get there.
     *
     * MEASURED on a city of 1,256 - annual GDP $17.5M, annual tax $3.7M, so GDP
     * runs 4.7x revenue - with the loan itself priced in:
     *
     *     1x revenue   1.2%      20x    5.9%      100x   15.3%
     *     2x revenue   1.5%      30x    8.3%      200x   19.5%
     *     5x revenue   2.2%      50x   13.1%      500x   the ceiling
     *    10x revenue   3.4%
     *
     * Ordinary municipal leverage - one to five years of revenue - is now low
     * single digits, which is about what a real city pays. The kink at 50x is
     * the revenue measure topping out; past it only the GDP measure is still
     * climbing, which is why the curve visibly flattens there.
     */
    private static final double FULL_STRESS_MULTIPLE = 50;

    /** The cheapest money the market will ever offer, whatever the books say. */
    private static final double MIN_RATE = 0.005;

    /** How many times to walk the face-value/rate fixed point. See quoteRate(). */
    private static final int QUOTE_ITERATIONS = 6;

    private List<Debt> debts;

    public DebtManager() {
        debts = new ArrayList<>();

    }

    public void addShortTermTBill(double faceValue, int months, int monthStarted) {
        ShortTermTBill shortTermTBill = new ShortTermTBill(faceValue, months, monthStarted);
        debts.add(shortTermTBill);
    }

    public void addMediumTermBond(double faceValue, int months, int monthStarted, double rate) {
        MediumTermBond mediumTermBond = new MediumTermBond(faceValue, months, monthStarted, rate);
        debts.add(mediumTermBond);
    }

    public void addLongTermBond(double faceValue, int months, int monthStarted, double rate) {
        LongTermBond longTermBond = new LongTermBond(faceValue, months, monthStarted, rate);
        debts.add(longTermBond);
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
    public double getPricedDebt() {
        return getAllPrincipal() + Math.max(0, overdraft);
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
    private double priceAt(double debt) {
        double rate = floorRate()
                + spreadFor(debt, GDP * 12)
                + spreadFor(debt, monthlyTaxRevenue * 12);
        return Math.max(MIN_RATE, Math.min(rate, ceilingRate()));
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
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
}
