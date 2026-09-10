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
    public static final double INSOLVENCY_TRIGGER = 1.5;

    /**
     * The most a lender will advance against a borrower's assets.
     *
     * STRICTLY BELOW THE WRITE-DOWN LINE, and the distance between the two is
     * the whole of the lender's protection. For one day (2026-09-10) this was
     * defined AS INSOLVENCY_TRIGGER: the most the bank would lend and the point
     * at which it declared the borrower insolvent and took a 60% haircut were
     * the same number. Lend $1,500,000 against $1,000,000 of assets and the
     * borrower is not insolvent; let its assets fall by one dollar and it is,
     * and $900,001 is written off. Twenty-four of the thirty-two restructures
     * in a 4,000-month run fired between 1.50 and 1.74 times assets. The
     * underwriter was parking every borrower precisely on the line and the
     * first bad month pushed it over.
     *
     * The earlier measurement that "a tighter ratio makes the bank irrelevant"
     * (1.00 and 0.75 both ended with no loan book) was honest and answered a
     * different question: because the two constants were one, moving the
     * ceiling moved the insolvency line with it, so it never tested a GAP.
     * With the trigger held at 1.5 and only this moved, over 4,000 months:
     *
     *   ceiling 1.50   wrote off $25.80bn (103% of interest), failed 308x,
     *                  the city injected $3.76bn
     *   ceiling 0.90   wrote off  $0.74bn (  7% of interest), failed  14x,
     *                  the city injected $27M - and kept a book
     *
     * 0.9 is not a real-world loan-to-value (65-75% on secured commercial
     * lending); it is the first setting measured that leaves a working bank
     * AND a working city, and it is here so the next person can move it.
     * This is the SHORTFALL desk's ceiling. The investment desk asks a
     * different question of the same line - see canFundProject().
     */
    public static final double MAX_LOAN_TO_ASSETS = 0.9;

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

    /**
     * How long a sector is shut out, given how many times it has done this.
     *
     * A FLAT TWELVE MONTHS WAS THE SAME ANSWER FOR A FIRST DEFAULT AND A
     * TWENTIETH, and that is the whole of why the loop existed. A sector with
     * no viable business would default, sit out its year, come back with a
     * clean balance sheet, re-lever against the same assets, and default again.
     * Measured over four thousand months at a flat twelve: 163 restructures,
     * one somewhere every 25 months, INDUSTRY alone 72 of them.
     *
     * Real credit does not work that way. A first default is survivable and a
     * borrower is back in the market in a year or two; a serial defaulter is
     * not lent to at all. The exclusion is the borrower's record, so it grows
     * with the record: twelve months for the first, twenty-four for the second,
     * thirty-six for the third.
     *
     * Nothing new is counted for this - `restructures` was already tracked and
     * already on the credit screen. It just was not being used for anything.
     *
     * Measured with the same run: 163 restructures -> 35, one every 114 months
     * instead of every 25, INDUSTRY 72 -> 5, MINING 55 -> 9. The city's worst
     * unemployment went 27.7% -> 22.7%, hunger 3% -> 0%, the shelf 60% -> 75%
     * delivered, and the recapitalisation the city has to fund fell from
     * $62.7bn to $3.76bn - seventeen-fold - while the bank kept a $4.4bn book
     * at a 16.2% capital ratio. Strictness about WHO fixed what strictness
     * about HOW MUCH could only fix by closing the bank.
     */
    static int exclusionFor(int defaultsSoFar) {
        return BORROWING_BLOCKED_MONTHS * Math.max(1, defaultsSoFar);
    }

    /** Extra annual interest per 1.0 of debt-to-assets. */
    private static final double SPREAD_PER_DEBT_TO_ASSETS = .06;

    /**
     * Extra annual interest per prior write-down, on top of the leverage
     * spread and outside its cap, up to DEFAULT_SURCHARGE_MAX_COUNT of them.
     *
     * DEFAULTING USED TO CUT THE RATE. restructure() reprices off the
     * post-write-down leverage and the borrower's record never entered the
     * price at all, so a sector quoted 8.5% at the ceiling was quoted 5.1%
     * the month after it defaulted - and a borrower that had defeated the
     * lender eight times was quoted less than one that had never missed. The
     * record is what a lender prices; here it was only what it banned on.
     */
    private static final double DEFAULT_SURCHARGE = .01;
    private static final int DEFAULT_SURCHARGE_MAX_COUNT = 3;

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

    /**
     * Whether the lender is open for business at all.
     *
     * A failed bank has no capacity - Bank.capacity() returns zero in
     * resolution, and the class doc, the panel and the player were all told it
     * "cannot lend a penny until it has capital again". Nothing here ever
     * asked. This class holds no reference to the bank, so the only
     * consequence of the bank failing was that every borrower's premium went
     * to eighteen points - and the borrower paid it by borrowing more from the
     * same failed bank. Measured over 4,000 months: 326 months began with the
     * bank frozen, and $5.05bn of new business credit was written inside them.
     *
     * Game sets this each month from the bank's own state. Harnesses that
     * build this class on its own get an open lender, which is what they had.
     */
    private boolean lendingOpen = true;

    private final Map<String, Double> assets = new LinkedHashMap<>();
    private final Map<String, Double> rates = new LinkedHashMap<>();

    /**
     * Each sector's cash balance, refreshed with the assets. An overdraft is
     * a debt to nobody in particular - the money was paid out and never
     * borrowed - and it is what a restructure forgives; see restructure().
     */
    private final Map<String, Double> cashBalance = new LinkedHashMap<>();

    /** What this month's restructures forgave, per sector, until Game collects it. */
    private final Map<String, Double> overdraftForgiven = new LinkedHashMap<>();

    /** Principal that fell due this month, per sector, waiting to be settled. */
    private final Map<String, Double> maturedPrincipal = new LinkedHashMap<>();

    /**
     * The lender's side of the month, for MoneyAudit: what it advanced and what
     * it took back. Neither is saved - they describe the month in progress and
     * Game.nextMonth() zeroes them before anything moves.
     */
    private double lentThisMonth;
    private double repaidThisMonth;

    /*
     * THE SAME TWO FIGURES, PER SECTOR.
     *
     * Recording only - nothing below reads these back, and no decision in this
     * class or any other depends on them. They exist because a cash flow
     * statement is not a statement without them: a sector's cash moves by its
     * profit, by what it borrowed and by what it repaid, and with only the
     * city-wide totals the last two could not be attributed to the sector whose
     * cash actually moved. See SectorBooks.
     */
    private final Map<String, Double> lentBySector = new LinkedHashMap<>();
    private final Map<String, Double> repaidBySector = new LinkedHashMap<>();

    public double getLentThisMonth()   { return lentThisMonth; }
    public double getRepaidThisMonth() { return repaidThisMonth; }

    public double getLentThisMonth(String sector) {
        return lentBySector.getOrDefault(sector, 0.0);
    }

    public double getRepaidThisMonth(String sector) {
        return repaidBySector.getOrDefault(sector, 0.0);
    }

    public void startAuditMonth() {
        lentThisMonth = 0;
        repaidThisMonth = 0;
        lentBySector.clear();
        repaidBySector.clear();
    }

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

        // The record, priced. Outside the cap on purpose: the cap is what stops
        // a bad MONTH compounding; a bad HISTORY is not a month.
        spread += DEFAULT_SURCHARGE
                * Math.min(getRestructureCount(sector), DEFAULT_SURCHARGE_MAX_COUNT);

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

    public void setCash(String sector, double cash) {
        cashBalance.put(sector, cash);
    }

    public double getCash(String sector) {
        return cashBalance.getOrDefault(sector, 0.0);
    }

    private double getOverdraftForgivenPending(String sector) {
        return overdraftForgiven.getOrDefault(sector, 0.0);
    }

    /** The overdraft a restructure forgave this month, handed over once. */
    public double takeOverdraftForgiven(String sector) {
        double forgiven = overdraftForgiven.getOrDefault(sector, 0.0);
        overdraftForgiven.put(sector, 0.0);
        return forgiven;
    }

    public double getAssets(String sector) {
        return assets.getOrDefault(sector, 0.0);
    }

    /** Everything every sector owes. What the bank has lent the businesses. */
    public double getAllPrincipal() {
        double total = 0;
        for (String s : SECTORS) total += getPrincipal(s);
        return total;
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
        repaidThisMonth += due;
        repaidBySector.merge(sector, due, Double::sum);
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

        /* =====================================================================
           ...AND NOBODY LENDS PAST THE CEILING

           Until 2026-09-10 there was no test here of any kind. A negative
           balance got a loan: any size, at any leverage, every month, for ever.
           The only gate was whether the sector was already inside its
           twelve-month exclusion - which is a test of what happened LAST year,
           not of whether this loan can be repaid.

           Which left this class enforcing one half of its own rule.
           restructure() calls a sector insolvent the moment its principal
           passes assets x INSOLVENCY_TRIGGER and writes it down to
           RESTRUCTURE_TARGET; underwriting knew nothing about either number and
           would cheerfully lend straight through the line, so the cycle was:
           lend past insolvency -> write off 60% of it -> block for twelve
           months -> lend past insolvency again.

           Measured over 1,202 months: fifty restructures, one somewhere every
           two years, and INDUSTRY alone went bankrupt twenty-seven times - once
           every forty-five months, which is not a default rate, it is a firm
           being refinanced on a loop. It had lost money in 1,063 of its 1,063
           months of existence. The bank ate all of it: write-offs came to 125%
           of every dollar of interest it earned in a century, and the write-off
           was the second-largest reason it kept failing.

           The first version of this test stopped AT the insolvency line, which
           turned out to be the same mistake one notch up - see the note on
           MAX_LOAN_TO_ASSETS. The ceiling now sits below the line, and it is
           read through borrowingRoom() so the investment desk reads the same
           one.

           WHAT HAPPENS INSTEAD is what the comment above already promised: the
           cash stays negative, and that is "the pressure that makes it shed
           capacity". A sector that cannot cover its losses with debt has to
           stop making them. That path exists (Game.runRetirement); unlimited
           credit was short-circuiting it.
           ===================================================================== */
        double room = borrowingRoom(sector);

        /*
         * ...WITH AN INTEREST RESERVE ABOVE THE CEILING. The investment desk
         * funds a building against the building itself, up to the line - so a
         * sector that has just built its first plant on credit sits ABOVE the
         * shortfall ceiling, and for the year or two before the plant earns
         * its keep it cannot borrow the interest on the loan that built it.
         * Measured on the playtest: Real Estate at -$142M by month 266 with
         * every door let, and 21 households with nowhere to live for eighty
         * months, because the landlord that had borrowed to house them could
         * not borrow the interest. A construction lender carries that as an
         * interest reserve, and so does this one: up to this month's interest
         * bill, and only up to the write-down line, so the reserve can never
         * become the old spiral. Losses beyond the interest are still refused.
         */
        if (amount > room) {
            double interestDue = Math.max(0, getMonthlyInterest(sector));
            double reserve = Math.min(interestDue, roomUnder(sector, INSOLVENCY_TRIGGER));
            room = Math.max(room, Math.min(amount, reserve));
        }

        amount = Math.min(amount, room);

        if (amount <= 0) {
            return 0;
        }

        issueLoan(sector, amount, month);

        return amount;
    }

    /**
     * How much more this sector may borrow today, from either desk.
     *
     * THE ONE DEFINITION OF THE CEILING. The shortfall desk above and the
     * investment desk (Game's Investor.canBorrow) both ask this, so a sector
     * cannot be refused the money to keep the lights on and then lent the
     * money to expand. Until 2026-09-10 the investment path enforced the ban
     * and not the ceiling: measured over 4,000 months, fifteen investment
     * loans were written past it, by $563M in total, the worst at 1.84 times
     * assets against a 1.50 rule.
     *
     * Zero while the sector is barred, and zero while the lender itself is
     * shut - see lendingOpen.
     */
    public double borrowingRoom(String sector) {
        return roomUnder(sector, MAX_LOAN_TO_ASSETS);
    }

    /**
     * Whether the INVESTMENT desk will fund a project of this size.
     *
     * A different test from borrowingRoom(), and the difference is deliberate
     * and measured. A shortfall loan covers losses the borrower is already
     * making, with nothing behind it but the assets it has - so it stops well
     * short of the line. An investment loan buys a building, and the building
     * is the collateral: it lands on the balance sheet the month it is bought.
     * So the honest test is the one this class already applies, asked of the
     * balance sheet AFTER the deal - principal plus the loan, against assets
     * plus the building - and the building is taken at the loan's own value,
     * which is the conservative end of what it will book at.
     *
     * Holding the investment desk to the shortfall ceiling instead was
     * measured over 4,000 months: the city ended at 15,353 people and 21.2%
     * unemployed, worst 42.4%, against 19,562 and 15.6% before. Every early
     * sector has almost no assets, so the desk that funds every first
     * expansion had been cut to a rescue desk's limit. Capping it at the bare
     * write-down line without counting the building did the same thing one
     * notch later: 17,077 people, worst 42.2%.
     *
     * What neither desk may do is leave the borrower past the line. The
     * investment path enforced the ban and not the line until 2026-09-10:
     * fifteen loans written past it in 4,000 months, the worst at 1.84 times
     * assets. And, like the shortfall desk, it lends nothing while the sector
     * is barred or the bank is shut.
     */
    public boolean canFundProject(String sector, double amount) {
        if (amount <= 0) return false;
        if (!lendingOpen) return false;
        if (isBorrowingBlocked(sector)) return false;
        double assetsAfter = Math.max(0, getAssets(sector)) + amount;
        return getPrincipal(sector) + amount <= assetsAfter * INSOLVENCY_TRIGGER;
    }

    private double roomUnder(String sector, double multiple) {
        if (!lendingOpen) return 0;
        if (isBorrowingBlocked(sector)) return 0;
        double ceiling = Math.max(0, getAssets(sector)) * multiple;
        return Math.max(0, ceiling - getPrincipal(sector));
    }

    /** Game tells the lender each month whether the bank behind it is standing. */
    public void setLendingOpen(boolean open) {
        this.lendingOpen = open;
    }

    public boolean isLendingOpen() {
        return lendingOpen;
    }

    public BusinessLoan issueLoan(String sector, double faceValue, int month) {
        BusinessLoan loan = new BusinessLoan(
                sector, faceValue, LOAN_TERM_MONTHS, month,
                priceSector(sector, faceValue));
        loans.add(loan);
        lentThisMonth += faceValue;
        lentBySector.merge(sector, faceValue, Double::sum);

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

    /*
     * THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED.
     *
     * Until 2026-09-10 only writtenOffTotal survived a save. The loans
     * themselves were restored; the count of how many times each sector had
     * been written down, and how many months of borrowing ban it still had to
     * serve, were written nowhere and restored nowhere. The debt survived and
     * the record of why it existed did not.
     *
     * Two consequences, both measured against the playtest's own reloads: a
     * sector's DEFAULT RECORD failed to survive the save 49 times (worst, Real
     * Estate: 12 restructures -> 0) and its BORROWING BAN 6 times (worst, Heavy
     * Industry: 80 months left -> 0). A player clears an 80-month ban by saving
     * and loading, and the next default is priced as a first offence -
     * exclusionFor(1), twelve months, instead of 108. Running the whole city
     * at that flat twelve - which is what a reloading player converges on -
     * costs $238bn of write-offs against $25.8bn. The entire seventeen-fold
     * improvement the escalating exclusion bought was available to undo with
     * Ctrl-S.
     *
     * Restored as zero on a save from before this, which is what those cities
     * were already reading.
     */
    public java.util.Map<String, Integer> getRestructureCounts() {
        return new LinkedHashMap<>(restructures);
    }

    public java.util.Map<String, Integer> getBlockedMonthsAll() {
        return new LinkedHashMap<>(blockedMonths);
    }

    public void restoreCreditRecord(java.util.Map<String, Integer> counts,
                                    java.util.Map<String, Integer> blocked) {
        restructures.clear();
        blockedMonths.clear();
        if (counts != null) {
            for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    restructures.put(e.getKey(), e.getValue());
                }
            }
        }
        if (blocked != null) {
            for (java.util.Map.Entry<String, Integer> e : blocked.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    blockedMonths.put(e.getKey(), e.getValue());
                }
            }
        }
    }

    public double getTotalWrittenOff() {
        double total = 0;
        for (String sector : SECTORS) {
            total += getWrittenOffTotal(sector);
        }
        return total;
    }

    /**
     * Is this sector's debt beyond what its assets could ever cover?
     *
     * Two ways in. The loan book past the line - principal over
     * INSOLVENCY_TRIGGER times assets, or any principal at all against
     * nothing. And, since 2026-09-10, AN OVERDRAFT THAT OUTWEIGHS EVERYTHING
     * THE SECTOR OWNS: the balance sheet's assets already carry the cash, so
     * assets at or below zero with cash below zero is a firm whose unpaid
     * bills exceed its plant, whether or not it ever signed a loan. Before
     * this a sector with no loans could run -$13bn for ever and never be
     * called anything.
     */
    public boolean isInsolvent(String sector) {
        double principal = getPrincipal(sector);
        double totalAssets = getAssets(sector);
        if (totalAssets <= 0) {
            return principal > 0 || getCash(sector) < 0;
        }
        return principal > 0 && principal > totalAssets * INSOLVENCY_TRIGGER;
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

        /*
         * ONE DEFAULT IS ONE EPISODE. A sector inside its ban is already in
         * resolution: it cannot borrow, and the distress rule is selling its
         * plant month by month. Judging it insolvent again every month while
         * that happens - which it is, its assets are falling - counted each
         * month as a fresh default, and the escalating exclusion did what it
         * says: 46 write-downs on Retail in one run, a ban of 525 months, and
         * a city that could not build a shop for forty years. The record
         * grows when the ban ENDS and the sector is still under water, which
         * is what "defaulted again" means.
         */
        if (isBorrowingBlocked(sector)) {
            return 0;
        }

        double principal = getPrincipal(sector);
        double target = Math.max(getAssets(sector) * RESTRUCTURE_TARGET, 0);
        double writeOff = Math.max(0, principal - target);

        /*
         * ...AND THE OVERDRAFT GOES WITH THE LOAN. The cash a sector paid out
         * and never had is a debt to nobody in particular, and a restructure
         * that wrote the loan down and left the overdraft standing left the
         * sector as insolvent as it found it - assets still at or below zero,
         * so insolvent again the month the ban lifted, for ever. Jerus's call
         * (2026-09-10): the hole is written off, declared, and the record
         * shows it. Game collects the figure through takeOverdraftForgiven()
         * and puts the money back, from outside the city's pools, the same
         * way the bank's creditors absorb a failed bank.
         */
        double overdraft = Math.max(0, -getCash(sector));
        if (writeOff <= 0 && overdraft <= 0) {
            return 0;
        }

        if (writeOff > 0) {
            double scale = target / principal;
            for (BusinessDebt loan : loans) {
                if (loan.getSector().equals(sector)) {
                    loan.writeDown(scale);
                }
            }
        }

        overdraftForgiven.put(sector, getOverdraftForgivenPending(sector) + overdraft);
        cashBalance.put(sector, getCash(sector) + overdraft);

        writtenOffThisMonth.put(sector, getWrittenOffThisMonth(sector) + writeOff);
        writtenOffTotal.put(sector, getWrittenOffTotal(sector) + writeOff);
        restructures.put(sector, getRestructureCount(sector) + 1);
        blockedMonths.put(sector, exclusionFor(getRestructureCount(sector)));

        // Less debt against the same assets is better credit on the leverage
        // leg - and a fresh default is worse credit on the record leg, which
        // priceSector() now charges for. Net, the month after a write-down
        // the borrower is quoted MORE than it was, not less.
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
            cashBalance.put(sector, 0.0);
            overdraftForgiven.put(sector, 0.0);
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

    /** Every business loan and this month's lending, in the new unit. */
    public void redenominate(double scale) {
        lentThisMonth   *= scale;
        repaidThisMonth *= scale;
        lentBySector.replaceAll((k, v) -> v * scale);
        repaidBySector.replaceAll((k, v) -> v * scale);
        for (BusinessDebt loan : loans) {
            if (loan != null) loan.redenominate(scale);
        }
        maturedPrincipal.replaceAll((sector, due) -> due * scale);
        writtenOffThisMonth.replaceAll((sector, off) -> off * scale);
        writtenOffTotal.replaceAll((sector, off) -> off * scale);
        assets.replaceAll((sector, value) -> value * scale);
    }

}
