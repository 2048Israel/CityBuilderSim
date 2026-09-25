package ham.citybuildersim;

/**
 * An insured mortgage on a new residential building: a level payment every
 * month over a forty-year amortization, at a rate fixed for a ten-year term
 * and renewed at the day's rate when the term ends, insured by the city.
 *
 * WHY (0.7.11). The landlords built on BusinessLoan, the same instrument as
 * a mill: interest only for LOAN_TERM_MONTHS, three years, then the whole
 * principal back at once. That is a bridge loan, and nobody finances
 * housing on one. A trace of 0.7.10 on autopilot (seed 0, months 2,280 to
 * 2,490) found what it did: the landlord's maturing loans were not rolled,
 * so its principal fell from $3.4B to $0.5B while its till went from
 * -$3.9B to -$6.8B; every new building then had to borrow its cost plus
 * that hole, so even one unit failed the interest test at a 2.9% prime;
 * and nothing was built for about two hundred months while the city was
 * 37% short of homes and nobody was out of work. Held at a 10% dial the
 * same test lent against 100% of the cost at 11.3-11.7%, a new home earned
 * 8-9% gross at market rent, and nothing was built for 333 years. A high
 * rate cut the supply of homes much harder than it cut the demand for them
 * (the project's the-rate-that-stops-the-cranes.md).
 *
 * Jerus chose the instrument a landlord actually borrows on, and whose
 * terms: "Mortgages", "CMHC (Canada)", "Keep it" (the rent floor), and
 * "Insured by the city".
 *
 * THE PRODUCT. CMHC's Mortgage Loan Insurance for Standard Rental Housing
 * (five units or more): a loan of up to 85% of the lending value, amortized
 * over up to 40 years - which "must not exceed the remaining economic life
 * of the property" - with a debt coverage ratio of at least 1.20 on a term
 * of ten years or more (1.30 on a shorter one). CMHC, Standard Rental
 * Housing, https://www.cmhc-schl.gc.ca/professionals/project-funding-and-
 * mortgage-financing/mortgage-loan-insurance/multi-unit-insurance/standard-
 * rental-housing, and its reference guide, https://assets.cmhc-schl.gc.ca/
 * sf/project/cmhc/pdfs/content/en/reference-guide.pdf. The constants below
 * are those terms; the lender's two tests (MORTGAGE_MAX_LOAN_TO_COST,
 * MORTGAGE_DEBT_COVERAGE) are asked in Game.consider() and
 * BusinessDebtManager.canFundMortgage().
 *
 * WHAT IT DOES EACH MONTH. The payment is the level annuity on what is left
 * over what is left of the amortization, at the fixed rate:
 *
 *     A = B r / (1 - (1 + r)^-n),   r = rate / 12
 *
 * The interest, B r, is an expense on the landlord's income statement, as a
 * loan's interest always was (getMonthlyInterestExpense(); the rent floor
 * reads it through the sector's interest line). The rest, A - B r, is
 * principal: a cash movement that lowers the balance and is not an expense -
 * the manager parks it with the principal that fell due this month and the
 * month's settle takes it from the landlord's till, exactly as it takes a
 * bullet's at maturity (BusinessDebtManager.processMonth()). After
 * MORTGAGE_AMORTIZATION_MONTHS payments nothing is owed.
 *
 * WHY A BusinessDebt AND NOT A BusinessLoan. The hierarchy is where business
 * credit lives: its interest is the borrower's expense and never the city's
 * cash, its principal is settled by the manager against the borrower's
 * books, the bank's book reads getOutstandingPrincipal(), and a default
 * writes every instrument of the borrower down pro rata through writeDown().
 * A mortgage needs all of that. What it does not share with BusinessLoan is
 * the schedule: a loan charges interest on its face and hands its principal
 * back at maturity; this pays both down every month, and renews rather than
 * matures. A sibling, not a child.
 *
 * INSURED. The city insures it, as the Government of Canada backs CMHC: the
 * landlord pays the premium to the treasury, added to the loan (premiumRate()),
 * and when the landlord's debt is written down the treasury pays the bank
 * what came off this balance (Game.runPrivateInvestment()). So the bank's
 * expected loss on it is nothing and its price carries none
 * (Bank.insuredMortgageRate()); it weighs Bank.RISK_INSURED_MORTGAGE,
 * nothing, and carries no allowance. What it still ties up, since round 2
 * of 0.7.11, is the capital the bank's leverage ratio asks of every dollar
 * lent, and its price carries that (Bank.capitalPerDollar()); while the
 * leverage requirement is the larger, the bank's capital rule rations it
 * with every other loan. The default is still the landlord's: its record, its
 * surcharge and its ban follow the ordinary rules. Every mortgage this build
 * writes is insured; the flag is kept so the rule stays a rule.
 */
public class Mortgage extends BusinessDebt {

    /** The term the rate is fixed for, in months: ten years, the term CMHC's 1.20 debt coverage is asked on (1.30 under ten), and the point of the bank's curve it is priced at (Bank.insuredMortgageRate()). */
    public static final int MORTGAGE_TERM_MONTHS = 120;

    /** The amortization, in months: forty years, the longest CMHC insures standard rental housing over - and no longer than the economic life of the building, which in this game does not wear out. */
    public static final int MORTGAGE_AMORTIZATION_MONTHS = 480;

    /** The most a mortgage lends against the building's cost: 85%, CMHC's highest loan-to-value for standard rental housing - so the landlord puts at least 15% from its own funds. */
    public static final double MORTGAGE_MAX_LOAN_TO_COST = .85;

    /** The lender's test: the building's net operating income must cover the mortgage's monthly payment this many times - CMHC's minimum debt coverage ratio, 1.20, on a term of ten years or more. */
    public static final double MORTGAGE_DEBT_COVERAGE = 1.20;

    /**
     * CMHC's premium on a standard rental loan at an LTV of 85% or less, as
     * a share of the loan: 5.00%. CMHC, multi-unit fees and premiums,
     * https://eppdscrmssa01.blob.core.windows.net/cmhcprodcontainer/sf/
     * project/cmhc/pdfs/content/en/fees-and-premiums.pdf. The schedule shows
     * two rates for this band, 5.35% and 5.00%, by whether the building's
     * effective gross income has been met; 5.00% is the base rate for a
     * building that has not yet shown its income, and a new one has not.
     */
    public static final double PREMIUM_RATE = .05;

    /** The premium's surcharge for each PREMIUM_SURCHARGE_STEP_YEARS of amortization past PREMIUM_BASE_YEARS: 0.25% of the loan (the same schedule), so 0.75% at forty years. */
    public static final double PREMIUM_SURCHARGE = .0025;

    /** The amortization the base premium is struck for, in years: 25; a longer one pays PREMIUM_SURCHARGE a step. */
    public static final int PREMIUM_BASE_YEARS = 25;

    /** How many years of amortization past PREMIUM_BASE_YEARS each step of the surcharge covers: five, up to forty. */
    public static final int PREMIUM_SURCHARGE_STEP_YEARS = 5;

    /** What was advanced toward the building, before the premium was added to it. */
    private double loan;

    /**
     * The premium, added to the principal and paid to the treasury.
     *
     * CMHC holds a premium as unearned revenue and earns it over the policy;
     * booking it as the treasury's revenue the month it is paid is this
     * model's simplification, and the claims it may one day meet are booked
     * the month they are met.
     */
    private double premium;

    /** The whole amortization it was written over, and the months of it still to pay. */
    private int amortizationMonths;
    private int amortizationLeft;

    /** How long the term that is running was written for; remainingMonths is what is left of it. */
    private int termMonths;

    /** How many times it has renewed. */
    private int renewals;

    /** Insured by the city: every mortgage this build writes. */
    private boolean insured;

    /** The principal this month's payment took off the balance, for the manager to settle. Not saved: it is read in the month it is struck. */
    private transient double principalPaid;

    /** For Gson. */
    Mortgage() { }

    /**
     * Writes a mortgage: the loan, the premium added to it, a term of
     * MORTGAGE_TERM_MONTHS at this rate, and MORTGAGE_AMORTIZATION_MONTHS to
     * pay it off.
     *
     * @param loan       what is advanced toward the building, before the premium
     * @param annualRate the insured rate this month (Bank.insuredMortgageRate())
     */
    public Mortgage(String sector, double loan, int monthStarted, double annualRate, boolean insured) {
        this.sector = sector;
        this.loan = Math.max(0, loan);
        this.premium = insured ? this.loan * premiumRate(MORTGAGE_AMORTIZATION_MONTHS) : 0;
        this.faceValue = this.loan + this.premium;
        this.outstandingPrincipal = this.faceValue;
        this.amortizationMonths = MORTGAGE_AMORTIZATION_MONTHS;
        this.amortizationLeft = MORTGAGE_AMORTIZATION_MONTHS;
        this.termMonths = Math.min(MORTGAGE_TERM_MONTHS, MORTGAGE_AMORTIZATION_MONTHS);
        this.duration = this.termMonths;
        this.remainingMonths = this.termMonths;
        this.monthStarted = monthStarted;
        this.annualRate = annualRate;
        this.insured = insured;
        this.type = "MORTGAGE";
    }

    /* ----------------------------- the arithmetic ----------------------------- */

    /** The premium as a share of the loan, for an amortization this long: PREMIUM_RATE, plus PREMIUM_SURCHARGE for each PREMIUM_SURCHARGE_STEP_YEARS (or part of one) past PREMIUM_BASE_YEARS. */
    public static double premiumRate(int amortizationMonths) {
        double years = amortizationMonths / 12.0;
        double past = Math.max(0, years - PREMIUM_BASE_YEARS);
        return PREMIUM_RATE + PREMIUM_SURCHARGE * Math.ceil(past / PREMIUM_SURCHARGE_STEP_YEARS - 1e-9);
    }

    /** The premium as a share of the loan on a mortgage this build writes, over MORTGAGE_AMORTIZATION_MONTHS: 5.75%. */
    public static double premiumRate() {
        return premiumRate(MORTGAGE_AMORTIZATION_MONTHS);
    }

    /**
     * The level monthly payment that pays this balance off over this many
     * months at this annual rate: B r / (1 - (1 + r)^-n), r a twelfth of the
     * rate - and B / n at no rate at all.
     */
    public static double payment(double balance, double annualRate, int months) {
        if (!(balance > 0) || months <= 0) return 0;
        double r = annualRate / 12;
        if (Math.abs(r) < 1e-15) return balance / months;
        return balance * r / -Math.expm1(-months * Math.log1p(r));
    }

    /**
     * What is left after k of the payments above, the closed form: B (1 + r)^k
     * less the payments compounded, A ((1 + r)^k - 1) / r. For the harness,
     * which asserts the month-by-month balance against it.
     */
    public static double balanceAfter(double balance, double annualRate, int months, int k) {
        if (k <= 0) return balance;
        if (k >= months) return 0;
        double r = annualRate / 12;
        double a = payment(balance, annualRate, months);
        if (Math.abs(r) < 1e-15) return balance - a * k;
        double grown = Math.exp(k * Math.log1p(r));
        return balance * grown - a * (grown - 1) / r;
    }

    /**
     * The loan that covers a shortfall once its fee is paid: the landlord is
     * handed the loan less Bank.LOAN_FEE of the principal the premium is
     * added to, so the loan is the shortfall over 1 - LOAN_FEE x (1 +
     * premiumRate()). Equivalently, the landlord pays the fee out of its own
     * funds along with its down payment, and the loan is what the building
     * costs less what it put in.
     */
    public static double loanFor(double shortfall) {
        if (!(shortfall > 0)) return 0;
        return shortfall / (1 - Bank.LOAN_FEE * (1 + premiumRate()));
    }

    /**
     * The least the landlord must hold to buy a building of this cost on a
     * mortgage: its down payment, 1 - MORTGAGE_MAX_LOAN_TO_COST of the cost,
     * and the fee on the largest mortgage the rest could be - what makes
     * loanFor(cost - funds) exactly MORTGAGE_MAX_LOAN_TO_COST of the cost.
     */
    public static double ownFundsFor(double cost) {
        if (!(cost > 0)) return 0;
        return cost - MORTGAGE_MAX_LOAN_TO_COST * cost * (1 - Bank.LOAN_FEE * (1 + premiumRate()));
    }

    /**
     * THE LENDER'S TEST, as a ratio: this net operating income over the
     * monthly payment on the mortgage that covers this shortfall - its
     * principal, the premium added, at this rate over
     * MORTGAGE_AMORTIZATION_MONTHS. The lender asks MORTGAGE_DEBT_COVERAGE of
     * it. Unbounded with nothing to borrow.
     */
    public static double coverage(double netOperatingIncome, double shortfall, double annualRate) {
        double principal = loanFor(shortfall) * (1 + premiumRate());
        double a = payment(principal, annualRate, MORTGAGE_AMORTIZATION_MONTHS);
        return a > 0 ? netOperatingIncome / a : Double.POSITIVE_INFINITY;
    }

    /* --------------------------- the landlord's order --------------------------- */

    /**
     * WHAT THE LANDLORD CAN BUY ON A MORTGAGE, and why not more: the largest
     * number of these, scanning down from what was asked for, that its own
     * funds buy outright, or that its own funds can put the down payment on
     * (ownFundsFor()) and whose net operating income covers the payment
     * MORTGAGE_DEBT_COVERAGE times (coverage()). Nothing when not even one
     * passes - short of the down payment on one (a till in overdraft covers
     * nothing), or failing the lender's test on one. Game.consider() asks
     * it, and the advisor's words are refusal() and trimmed().
     *
     * @param asked      what the planner ordered
     * @param costOf     what n of them cost, land included (BusinessInvestment.getCostOf())
     * @param cash       the landlord's till, after its owners and its money abroad
     * @param noiPerUnit one's rent less what it costs to hold (EconomyManager.housingCarry())
     * @param annualRate the insured rate a mortgage is written at this month
     */
    public static Decision decide(int asked, java.util.function.IntToDoubleFunction costOf,
                                  double cash, double noiPerUnit, double annualRate) {
        String trimmedBy = null;
        double ownForOne = Double.NaN, coverageOfOne = Double.NaN;
        boolean shortForOne = false;
        for (int n = asked; n >= 1; n--) {
            double cost = costOf.applyAsDouble(n);
            // Its own funds buy it outright: no lender to ask.
            if (cash >= cost) return new Decision(n, trimmedBy, false, ownForOne, coverageOfOne);
            double own = ownFundsFor(cost);
            if (n == 1) ownForOne = own;
            if (!(cash > 0) || cash < own) {
                if (n == 1) shortForOne = true;
                if (trimmedBy == null) trimmedBy = Decision.DOWN_PAYMENT;
                continue;
            }
            double coverage = coverage(noiPerUnit * n, cost - cash, annualRate);
            if (n == 1) coverageOfOne = coverage;
            if (coverage >= MORTGAGE_DEBT_COVERAGE) return new Decision(n, trimmedBy, false, ownForOne, coverageOfOne);
            if (trimmedBy == null) trimmedBy = Decision.LENDERS_TEST;
        }
        return new Decision(0, trimmedBy, shortForOne, ownForOne, coverageOfOne);
    }

    /**
     * The order, decided: how many; what trimmed it from what was asked, if
     * anything did (DOWN_PAYMENT or LENDERS_TEST, the first that failed on
     * the way down); and, when not even one passed, whether one was short of
     * its down payment, the funds one needs, and the coverage one gives.
     */
    public record Decision(int quantity, String trimmedBy, boolean shortOfDown,
                           double ownFundsForOne, double coverageOfOne) {

        /** Trimmed, or refused, because its own funds could not put the down payment on it. */
        public static final String DOWN_PAYMENT = "down payment";
        /** ...because its rent would not cover the payment MORTGAGE_DEBT_COVERAGE times. */
        public static final String LENDERS_TEST = "lender's test";

        /** The advisor's line when not even one passes: the down payment it needs, or the lender's figure against its own. */
        public String refusal(String building) {
            if (shortOfDown) {
                return String.format("Holding: needs %s of its own for the %.0f%% down payment on a %s",
                        Formats.INSTANCE.cash(ownFundsForOne), (1 - MORTGAGE_MAX_LOAN_TO_COST) * 100, building);
            }
            return String.format("Declined %s - its rent would cover the mortgage %.2f×; the lender asks %.2f×",
                    building, coverageOfOne, MORTGAGE_DEBT_COVERAGE);
        }

        /** ...and the words for an order cut down from what was asked, or nothing when it was not. */
        public String trimmed(int asked) {
            if (quantity <= 0 || quantity >= asked) return "";
            return DOWN_PAYMENT.equals(trimmedBy)
                    ? String.format(" (trimmed from %,d - its own funds put the %.0f%% down on %,d)",
                            asked, (1 - MORTGAGE_MAX_LOAN_TO_COST) * 100, quantity)
                    : String.format(" (trimmed from %,d - the lender's test passes %,d)", asked, quantity);
        }
    }

    /* ------------------------------- the month ------------------------------- */

    /**
     * One payment: the interest on what is owed at the fixed rate, which is
     * the income statement's; and the principal, the rest of the level
     * payment, taken off the balance and left for the manager to settle
     * (takePrincipalPaid()). No cash moves here - see BusinessDebt. The last
     * payment takes whatever is left, so the balance ends at nothing, not at
     * a rounding error. The term's clock runs down with it; the manager
     * renews it when it has run (renew()).
     */
    @Override
    public void processMonth() {
        principalPaid = 0;
        if (amortizationLeft <= 0 || !(outstandingPrincipal > 0)) {
            principalPaid = Math.max(0, outstandingPrincipal);
            outstandingPrincipal = 0;
            amortizationLeft = 0;
            remainingMonths = 0;
            return;
        }
        double interest = outstandingPrincipal * annualRate / 12;
        double pay = payment(outstandingPrincipal, annualRate, amortizationLeft);
        double principal = amortizationLeft == 1 ? outstandingPrincipal
                : Math.max(0, Math.min(outstandingPrincipal, pay - interest));
        outstandingPrincipal -= principal;
        principalPaid = principal;
        amortizationLeft--;
        remainingMonths--;
        if (amortizationLeft <= 0) outstandingPrincipal = 0;
    }

    /** The principal this month's payment took off the balance, handed over once. */
    public double takePrincipalPaid() {
        double paid = principalPaid;
        principalPaid = 0;
        return paid;
    }

    /** True once the term that is running has run, with something still to pay. */
    public boolean isTermEnded() {
        return remainingMonths <= 0 && amortizationLeft > 0 && outstandingPrincipal > 0;
    }

    /** True once the amortization is done and nothing is owed. */
    public boolean isPaidOff() {
        return amortizationLeft <= 0 || !(outstandingPrincipal > 0);
    }

    /**
     * RENEWS for another term at the day's rate: MORTGAGE_TERM_MONTHS, or
     * what is left of the amortization if that is shorter. The payment is
     * recomputed from the next month on what is left, over what is left.
     * ROUTINE, because the loan is insured: the lender is not asked to take
     * a risk again, only to fund what it already funds at today's price.
     */
    public void renew(double annualRate) {
        this.annualRate = annualRate;
        this.termMonths = Math.min(MORTGAGE_TERM_MONTHS, amortizationLeft);
        this.duration = this.termMonths;
        this.remainingMonths = this.termMonths;
        this.renewals++;
    }

    /** Closes it: the whole balance falls due, as a loan's does at maturity (a renewal the bank cannot write). */
    public double close() {
        double owed = Math.max(0, outstandingPrincipal);
        outstandingPrincipal = 0;
        amortizationLeft = 0;
        remainingMonths = 0;
        return owed;
    }

    /* ------------------------------- readings ------------------------------- */

    /** Interest on what is owed: the balance, not the face - it is paid down. */
    @Override
    public double getMonthlyInterestExpense() {
        return Math.max(0, outstandingPrincipal) * annualRate / 12;
    }

    @Override
    public double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    /** The month the term that is running ends - when it renews, not when it is paid off (getPaidOffMonth()). */
    @Override
    public int getMaturityMonth() {
        return getNextRenewalMonth();
    }

    /** Never matures as a loan does: it renews, or it is paid off, and the manager handles both. */
    @Override
    public boolean isMatured() {
        return false;
    }

    @Override
    public String getType() {
        return type;
    }

    /** The level payment due next month, on what is owed over what is left at the fixed rate. */
    public double getMonthlyPayment() {
        return payment(outstandingPrincipal, annualRate, amortizationLeft);
    }

    /** The month the term that is running ends. */
    public int getNextRenewalMonth() {
        return monthStarted + (amortizationMonths - amortizationLeft) + Math.max(0, remainingMonths);
    }

    /** The month the last payment falls. */
    public int getPaidOffMonth() {
        return monthStarted + amortizationMonths;
    }

    public double getLoan()            { return loan; }
    public double getPremium()         { return premium; }
    public int getAmortizationMonths() { return amortizationMonths; }
    public int getAmortizationLeft()   { return amortizationLeft; }
    public int getTermMonths()         { return termMonths; }
    public int getRenewals()           { return renewals; }
    public boolean isInsured()         { return insured; }
    public int getMonthStarted()       { return monthStarted; }

    /** The mortgage in the new unit: its balance, its face, the loan and the premium. */
    @Override
    public void redenominate(double scale) {
        super.redenominate(scale);
        loan *= scale;
        premium *= scale;
        principalPaid *= scale;
    }
}
