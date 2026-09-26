package ham.citybuildersim;

/**
 * Interim financing: the bank's loan to a sector in the month it defaulted,
 * for what the month's bills left unpaid after its debt was written down,
 * ranked ahead of everything else the sector owes.
 *
 * WHY (0.7.12, round 5). Round 4 made a till that nobody would lend to
 * default that month - Canada's Bankruptcy and Insolvency Act, s. 2, the
 * cash-flow test - and closed the rest of its overdraft the way the backstop
 * closes one: forgiven, money created to pay wages and suppliers the sector
 * never had. Jerus: "Interim financing. After the debt is written down, the
 * bank lends the unpaid part as a new loan that ranks ahead of all other
 * debt, so no money is created. This is what real restructurings do
 * (Canada's CCAA s. 11.2; US Chapter 11, 11 U.S.C. s. 364). If nobody will
 * lend even then, the whole industry goes to the existing full write-off."
 *
 * A BUSINESS LOAN IN EVERY OTHER RESPECT: a bullet on the shortfall desk's
 * term (BusinessDebtManager.LOAN_TERM_MONTHS), its fee kept back as every
 * loan's is, priced by the bank's own rule applied to its rank, and repaid
 * or rolled when it falls due like any other. What differs is its place in
 * a later default: BusinessDebtManager writes it down only after the
 * sector's other debt (INTERIM FINANCING). Saved under its own type, so a
 * reload knows its rank.
 */
public class InterimLoan extends BusinessLoan {

    /** The type a save carries it under; Game's load reads it back as this class. */
    public static final String TYPE = "INTERIM-LOAN";

    public InterimLoan(String sector, double faceValue, int months, int monthStarted, double annualRate) {
        super(sector, faceValue, months, monthStarted, annualRate);
        this.type = TYPE;
    }
}
