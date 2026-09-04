package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Private-sector credit. The counterpart to DebtManager, which handles the
 * city's own borrowing.
 *
 * One manager holds every business loan in the city, tagged by sector, rather
 * than one manager per sector. That way there is a single list to save, a single
 * place to see total private credit, and adding a fourth sector is a constant in
 * this file instead of another object to wire up. Each sector still gets its own
 * rate - the rate is per sector, the bookkeeping is shared.
 *
 * PRICING
 *
 *     rate = government rate + credit spread
 *
 * The government rate is the risk-free floor: no private borrower is safer than
 * the city that can tax them. The spread is driven by leverage - debt to total
 * assets - which mirrors how DebtManager prices the city off debt-to-GDP. Both
 * ask the same question of the same shape: how much is owed against how much
 * there is to pay with.
 *
 *     spread = MIN_SPREAD + SPREAD_PER_DEBT_TO_ASSETS * (debt / assets)
 *
 * clamped to [MIN_SPREAD, MAX_SPREAD]. A debt-free business still pays
 * MIN_SPREAD over the city, because it is still not the city. A business whose
 * debts exceed its assets pays the ceiling and no more - the cap is what stops
 * a bad month from compounding into an unrecoverable one.
 *
 * Capping the SPREAD rather than the total rate keeps the two systems coupled:
 * if city borrowing drives the risk-free rate up, business credit follows it up
 * rather than compressing to nothing against a fixed ceiling.
 *
 * ORIGINATION
 *
 * Loans are underwritten automatically when a sector cannot cover its month.
 * Buildings are paid for out of city cash, so a business never borrows to
 * expand; the only thing it needs credit for is a shortfall. Before this
 * existed, a sector's cash simply went negative with no lender, no interest and
 * no liability on its balance sheet - the food industry was $48,011.82 overdrawn
 * at month 170 and paying nothing for the privilege.
 */
public class BusinessDebtManager {

    public static final String RETAIL = "Retail";
    public static final String REAL_ESTATE = "Real Estate";
    public static final String INDUSTRY = "Industry";
    public static final String CONSTRUCTION = "Construction";
    public static final String HEAVY_INDUSTRY = "Heavy Industry";
    public static final String MINING = "Mining";

    /** Every set of books that can borrow. Add a sector here and it just works. */
    public static final String[] SECTORS =
            { RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING };

    /** Floor over the government rate. Nobody borrows at sovereign. */
    private static final double MIN_SPREAD = .01;

    /** Ceiling over the government rate - the "even worst case, not too bad" cap. */
    private static final double MAX_SPREAD = .08;

    /* =======================================================================
       INSOLVENCY

       Before this existed a sector could borrow without limit forever. That is
       not generosity, it is a missing mechanic: construction at equilibrium had
       no orders, no revenue, a standing payroll and interest on its debt, so it
       borrowed to pay the interest, which raised the interest. Its debt went
       from $91,753 to $506,045 across two hundred months in which nothing was
       built. Nothing in the model could ever stop it, because a business here
       could not shrink, default, or be refused.

       A lender in that position stops lending and takes a haircut, so that is
       what happens now.
       ======================================================================= */

    /** Debt above this multiple of assets is not getting repaid, and both sides know it. */
    private static final double INSOLVENCY_TRIGGER = 1.5;

    /** What a restructured borrower is left owing, as a multiple of its assets. */
    private static final double RESTRUCTURE_TARGET = .6;

    /**
     * Months a sector cannot borrow after being restructured.
     *
     * Without this the write-down is a gift: the sector is handed a clean
     * balance sheet and immediately re-levers to pay the same bills it could not
     * pay before, and the spiral restarts one month later with the lender
     * funding it. Being cut off is what forces the real adjustment - selling
     * capacity it is not using - and a firm in default genuinely cannot raise
     * money.
     */
    private static final int BORROWING_BLOCKED_MONTHS = 12;

    /** Extra annual interest per 1.0 of debt-to-assets. */
    private static final double SPREAD_PER_DEBT_TO_ASSETS = .06;

    private static final int LOAN_TERM_MONTHS = 36;

    /**
     * Borrow enough to cover the hole plus this many months of the current loss.
     *
     * Without the buffer a chronically loss-making sector writes a fresh loan
     * every single month - the food industry would be carrying about 170 of them
     * by now, and the debt schedule would be unreadable. Overshooting slightly
     * keeps it to a handful of larger loans.
     */
    private static final double BUFFER_MONTHS = 3;

    private List<BusinessDebt> loans = new ArrayList<>();

    private double riskFreeRate;

    /** Written off this month, and over the whole game, per sector. */
    private final Map<String, Double> writtenOffThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> writtenOffTotal = new LinkedHashMap<>();
    private final Map<String, Integer> restructures = new LinkedHashMap<>();

    /** Months of borrowing ban remaining, per sector. */
    private final Map<String, Integer> blockedMonths = new LinkedHashMap<>();

    private final Map<String, Double> assets = new LinkedHashMap<>();
    private final Map<String, Double> rates = new LinkedHashMap<>();

    /** Principal that fell due this month, per sector, waiting to be settled. */
    private final Map<String, Double> maturedPrincipal = new LinkedHashMap<>();

    public BusinessDebtManager() {
        for (String sector : SECTORS) {
            assets.put(sector, 0.0);
            rates.put(sector, MIN_SPREAD);
            maturedPrincipal.put(sector, 0.0);
        }
    }

    //setters
    public void setRiskFreeRate(double rate) {
        this.riskFreeRate = rate;
    }

    /** Total assets from that sector's balance sheet - the denominator of leverage. */
    public void setAssets(String sector, double totalAssets) {
        assets.put(sector, totalAssets);
    }

    //pricing
    public void updateRates() {
        for (String sector : SECTORS) {
            rates.put(sector, priceSector(sector));
        }
    }

    private double priceSector(String sector) {
        return priceSector(sector, 0);
    }

    /**
     * @param extraPrincipal borrowing about to be taken on, included in the
     *                       ratio. A lender prices the loan it is writing, not
     *                       the balance sheet from before it existed - without
     *                       this a sector's FIRST loan always priced as though
     *                       it had no debt, i.e. at the cheapest rate available,
     *                       however much it was borrowing.
     */
    private double priceSector(String sector, double extraPrincipal) {

        double principal = getPrincipal(sector) + extraPrincipal;
        double totalAssets = assets.getOrDefault(sector, 0.0);

        double spread;

        if (totalAssets <= 0) {
            // Nothing to lend against, or liabilities already exceed what there
            // is - which is where the food industry sits. Worst case, and this
            // has to be checked BEFORE the debt-free case: a business with no
            // debt and no assets is not a good credit, it is an empty one. The
            // first version returned the minimum spread here, so the insolvent
            // food industry borrowed $49,611 at 2%.
            spread = MAX_SPREAD;
        } else {
            // Zero debt against real assets falls out of this as MIN_SPREAD, so
            // it needs no special case of its own.
            double debtToAssets = principal / totalAssets;
            spread = MIN_SPREAD + SPREAD_PER_DEBT_TO_ASSETS * debtToAssets;
        }

        spread = Math.max(MIN_SPREAD, Math.min(spread, MAX_SPREAD));

        return riskFreeRate + spread;
    }

    //getters
    /** What NEW borrowing costs this sector today. Existing loans keep their own rate. */
    public double getRate(String sector) {
        return rates.getOrDefault(sector, riskFreeRate + MIN_SPREAD);
    }

    public double getSpread(String sector) {
        return getRate(sector) - riskFreeRate;
    }

    public double getRiskFreeRate() {
        return riskFreeRate;
    }

    public double getLeverage(String sector) {
        double totalAssets = assets.getOrDefault(sector, 0.0);
        return (totalAssets > 0) ? getPrincipal(sector) / totalAssets : 0;
    }

    public double getAssets(String sector) {
        return assets.getOrDefault(sector, 0.0);
    }

    public double getPrincipal(String sector) {
        double total = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                total += loan.getOutstandingPrincipal();
            }
        }
        return total;
    }

    public double getTotalPrincipal() {
        double total = 0;
        for (BusinessDebt loan : loans) {
            total += loan.getOutstandingPrincipal();
        }
        return total;
    }

    /** This month's interest cost for a sector - the income statement's expense line. */
    public double getMonthlyInterest(String sector) {
        double total = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                total += loan.getMonthlyInterestExpense();
            }
        }
        return total;
    }

    public double getTotalMonthlyInterest() {
        double total = 0;
        for (BusinessDebt loan : loans) {
            total += loan.getMonthlyInterestExpense();
        }
        return total;
    }

    /**
     * Blended annual rate actually being paid on existing debt, as opposed to
     * getRate() which is what the next loan would cost. The two diverge when a
     * sector borrowed cheaply and then deteriorated.
     */
    public double getEffectiveRate(String sector) {
        double principal = getPrincipal(sector);
        return (principal > 0) ? (getMonthlyInterest(sector) * 12) / principal : 0;
    }

    public int getLoanCount(String sector) {
        int count = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                count++;
            }
        }
        return count;
    }

    public List<BusinessDebt> getLoans() {
        return loans;
    }

    public List<BusinessDebt> getLoans(String sector) {
        List<BusinessDebt> result = new ArrayList<>();
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                result.add(loan);
            }
        }
        return result;
    }

    //monthly cycle
    /**
     * Advances every loan and retires the ones that mature.
     *
     * Matured principal is parked per sector rather than paid here, because this
     * class has no access to anyone's cash. EconomyManager collects it with
     * takeMaturedPrincipal() and settles it against the right books.
     */
    public void processMonth() {

        Iterator<BusinessDebt> iterator = loans.iterator();

        while (iterator.hasNext()) {
            BusinessDebt loan = iterator.next();
            loan.processMonth();

            if (loan.isMatured()) {
                String sector = loan.getSector();
                maturedPrincipal.put(sector,
                        maturedPrincipal.getOrDefault(sector, 0.0) + loan.getOutstandingPrincipal());
                iterator.remove();
            }
        }
    }

    /** Reads and clears the principal that fell due this month for one sector. */
    public double takeMaturedPrincipal(String sector) {
        double due = maturedPrincipal.getOrDefault(sector, 0.0);
        maturedPrincipal.put(sector, 0.0);
        return due;
    }

    /**
     * Underwrites a loan if the sector is short, and returns the proceeds.
     *
     * @param cash        the sector's cash after the month has settled
     * @param monthlyLoss this month's loss, if any - used to size the buffer
     * @param month       the current month, for the maturity schedule
     * @return the amount lent, which the caller must add to that sector's cash
     */
    public double coverShortfall(String sector, double cash, double monthlyLoss, int month) {

        if (cash >= 0) {
            return 0;
        }

        // A sector in default cannot raise money. Its cash stays negative, which
        // is the honest picture and the pressure that makes it shed capacity.
        if (isBorrowingBlocked(sector)) {
            return 0;
        }

        double buffer = Math.max(monthlyLoss, 0) * BUFFER_MONTHS;
        double amount = -cash + buffer;

        issueLoan(sector, amount, month);

        return amount;
    }

    public BusinessLoan issueLoan(String sector, double faceValue, int month) {
        BusinessLoan loan = new BusinessLoan(
                sector, faceValue, LOAN_TERM_MONTHS, month,
                priceSector(sector, faceValue));
        loans.add(loan);

        // A new loan changes the sector's leverage, so the next one prices off
        // the new position rather than the one before this loan existed.
        rates.put(sector, priceSector(sector));

        return loan;
    }

    /* ----------------------------- insolvency ----------------------------- */

    public boolean isBorrowingBlocked(String sector) {
        return blockedMonths.getOrDefault(sector, 0) > 0;
    }

    public int getBlockedMonths(String sector) {
        return blockedMonths.getOrDefault(sector, 0);
    }

    public double getWrittenOffThisMonth(String sector) {
        return writtenOffThisMonth.getOrDefault(sector, 0.0);
    }

    public double getWrittenOffTotal(String sector) {
        return writtenOffTotal.getOrDefault(sector, 0.0);
    }

    public int getRestructureCount(String sector) {
        return restructures.getOrDefault(sector, 0);
    }

    /**
     * Puts the write-off history back on load.
     *
     * Per sector, because that is how it is kept and how the credit screen
     * reads it. This is a record of money that was destroyed - a city that
     * forgets it on every load reads as having a cleaner credit history than it
     * has, which is the one direction this figure must never move by accident.
     */
    public void restoreWriteOffs(java.util.Map<String, Double> totals) {
        writtenOffTotal.clear();
        if (totals == null) return;
        for (java.util.Map.Entry<String, Double> e : totals.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) {
                writtenOffTotal.put(e.getKey(), e.getValue());
            }
        }
    }

    public java.util.Map<String, Double> getWriteOffTotals() {
        return new LinkedHashMap<>(writtenOffTotal);
    }

    public double getTotalWrittenOff() {
        double total = 0;
        for (String sector : SECTORS) {
            total += getWrittenOffTotal(sector);
        }
        return total;
    }

    /** Is this sector's debt beyond what its assets could ever cover? */
    public boolean isInsolvent(String sector) {
        double principal = getPrincipal(sector);
        if (principal <= 0) {
            return false;
        }
        double totalAssets = getAssets(sector);
        return totalAssets <= 0 || principal > totalAssets * INSOLVENCY_TRIGGER;
    }

    /**
     * Writes a sector's debt down to what its assets can support.
     *
     * Call once a month, AFTER the balance sheets have been refreshed - the
     * whole judgement is principal against assets, so acting on last month's
     * assets would restructure the wrong sector.
     *
     * @return the amount written off, or 0 if the sector was solvent enough
     */
    public double restructure(String sector) {

        if (!isInsolvent(sector)) {
            return 0;
        }

        double principal = getPrincipal(sector);
        double target = Math.max(getAssets(sector) * RESTRUCTURE_TARGET, 0);
        double writeOff = principal - target;

        if (writeOff <= 0) {
            return 0;
        }

        double scale = (principal > 0) ? target / principal : 0;

        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                loan.writeDown(scale);
            }
        }

        writtenOffThisMonth.put(sector, getWrittenOffThisMonth(sector) + writeOff);
        writtenOffTotal.put(sector, getWrittenOffTotal(sector) + writeOff);
        restructures.put(sector, getRestructureCount(sector) + 1);
        blockedMonths.put(sector, BORROWING_BLOCKED_MONTHS);

        // Less debt against the same assets is genuinely better credit, even
        // though the improvement was taken out of the lender's hide.
        rates.put(sector, priceSector(sector));

        return writeOff;
    }

    /** Restructures whoever needs it. @return total written off this month. */
    public double restructureInsolventSectors() {

        double total = 0;
        for (String sector : SECTORS) {
            writtenOffThisMonth.put(sector, 0.0);
        }

        for (String sector : SECTORS) {
            total += restructure(sector);
        }
        return total;
    }

    /** Counts down the borrowing bans. Call once a month. */
    public void advanceBlocks() {
        for (String sector : SECTORS) {
            int left = blockedMonths.getOrDefault(sector, 0);
            if (left > 0) {
                blockedMonths.put(sector, left - 1);
            }
        }
    }

    //save / load
    public void setLoans(List<BusinessDebt> loans) {
        this.loans = loans;
    }

    public void clearLoans() {
        loans.clear();
        for (String sector : SECTORS) {
            maturedPrincipal.put(sector, 0.0);
            writtenOffThisMonth.put(sector, 0.0);
            writtenOffTotal.put(sector, 0.0);
            restructures.put(sector, 0);
            blockedMonths.put(sector, 0);
        }
    }

    //printers
    public void printBusinessDebtInfo(int currentMonth) {

        System.out.println("\n=============== PRIVATE SECTOR CREDIT ===============");
        System.out.printf("Government (risk-free) rate: %.2f%%%n", riskFreeRate * 100);

        for (String sector : SECTORS) {
            System.out.printf("%n%s%n", sector.toUpperCase());
            System.out.printf("  Outstanding Principal:  $%s%n", formatter.format(getPrincipal(sector)));
            System.out.printf("  Monthly Interest:       $%s%n", formatter.format(getMonthlyInterest(sector)));
            System.out.printf("  Leverage (debt/assets): %.2f%n", getLeverage(sector));
            System.out.printf("  New Borrowing Rate:     %.2f%%  (govt %.2f%% + %.2f%% spread)%n",
                    getRate(sector) * 100, riskFreeRate * 100, getSpread(sector) * 100);
            System.out.printf("  Rate on Existing Debt:  %.2f%%%n", getEffectiveRate(sector) * 100);
            System.out.printf("  Loans Outstanding:      %d%n", getLoanCount(sector));

            for (BusinessDebt loan : getLoans(sector)) {
                System.out.printf("    Month %-4d | $%-12s @ %.2f%%%n",
                        loan.getMaturityMonth(),
                        formatter.format(loan.getOutstandingPrincipal()),
                        loan.getAnnualRate() * 100);
            }
        }

        System.out.println("-----------------------------------------------------");
        System.out.printf("TOTAL PRIVATE CREDIT:     $%s%n", formatter.format(getTotalPrincipal()));
        System.out.printf("TOTAL MONTHLY INTEREST:   $%s%n", formatter.format(getTotalMonthlyInterest()));
        System.out.println("=====================================================\n");
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
}
