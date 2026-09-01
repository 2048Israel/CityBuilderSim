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
    private double allPrincipal = 0;
    private double GDP;
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

        if (totalPrincipal >= 0) {
            allPrincipal = totalPrincipal;
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("TOTAL OUTSTANDING PRINCIPAL: " + formatter.format(totalPrincipal));
        System.out.println("TOTAL MONTHLY INTEREST COST: " + formatter.format(totalMonthlyInterest));
        System.out.println("============================================================\n");
    }

    //getters
    public double getRate() {
        return currentRate;
    }
    public double getAllPrincipal(){
        return allPrincipal;
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

    public void updateInterest() {
        double rate;

        if (allPrincipal != 0) {
            if (GDP == 0) {
                GDP = allPrincipal;
            }

            rate = ((allPrincipal / (GDP * 12)) / 100) - 0.02;

           
            

            currentRate = baseRate + rate;

        } else {
            currentRate = baseRate - 0.02;
        }

        // Final clamp on the total rate
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
