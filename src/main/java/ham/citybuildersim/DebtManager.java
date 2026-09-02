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

    /** Extra annual interest charged per 1.0 of debt-to-GDP. */
    private static final double RISK_SPREAD_PER_DEBT_TO_GDP = 0.01;

    /** Debt-to-GDP assumed when the city has no measurable output to price against. */
    private static final double UNPRICEABLE_DEBT_TO_GDP = 100;
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
     * Prices new borrowing off the city's debt-to-GDP ratio.
     *
     * The spread is unchanged in magnitude from the original formula - that
     * worked out to (baseRate - 0.02) + debtToGdp * 0.01, i.e. one extra point of
     * interest per unit of debt-to-GDP - just written so the units are visible.
     *
     * NOTE: the old version did `if (GDP == 0) GDP = allPrincipal;`, permanently
     * overwriting the GDP field with a debt figure. That corruption then fed
     * every later rate calculation. Uses a local fallback instead.
     */
    public void updateInterest() {

        double principal = getAllPrincipal();

        if (principal <= 0) {
            currentRate = baseRate - 0.02;
        } else {
            double annualGdp = GDP * 12;
            double debtToGdp = (annualGdp > 0)
                    ? (principal / annualGdp)
                    : UNPRICEABLE_DEBT_TO_GDP;

            currentRate = (baseRate - 0.02)
                    + (RISK_SPREAD_PER_DEBT_TO_GDP * debtToGdp);
        }

        currentRate = Math.max(0.005, Math.min(currentRate, 0.20));
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
