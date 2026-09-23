package ham.citybuildersim;

/**
 * Every kind of payment the treasury makes, and whether it is a promise.
 *
 * WHY (0.7.0). The treasury's overdraft used to be an emergency note with no
 * limit on it, so a broke city paid every bill it had by borrowing at whatever
 * the note cost - 27-36% once its bank really paid for the paper - and
 * compounded to $193 quadrillion. Now the overdraft is advances from the
 * central bank, and they stop at a ceiling in months of revenue (the player's
 * dial since 0.7.2, CentralBank.DEFAULT_ADVANCES_MONTHS until it is moved).
 * Past that something has to go unpaid, and Jerus chose what, the night the
 * rule was written: "pay promises first, cut the rest."
 *
 * A PROMISE is paid whatever the treasury holds, drawing advances past the
 * ceiling if it must: pensions, EI, health, the schools, the city's own wages,
 * and the coupons and principal on its paper (a default is a different rule).
 * Everything else is DISCRETIONARY: once the ceiling has bound, and while any
 * arrears are still owed, it is paid only from cash at or above zero
 * (Game.discretionaryRoom()). A discretionary line that is not a purchase and
 * is refused becomes ARREARS - owed, interest-free, paid down first when cash
 * returns (Game.settleTreasury()); a refused PURCHASE is simply not made.
 *
 * Every payment goes through Game.treasuryPays() (the two that do not are
 * named in TreasuryJournal's list), which reads these two flags and nothing
 * else. A constant's sentence says why it sits on its side.
 */
public enum TreasuryLine {

    /** The coupons on the city's own paper, accrued through the month: a coupon is a promise, and not paying one is a default. */
    INTEREST("Interest on the city's paper", true, false),

    /** Principal on the city's own paper, as each piece falls due - the same promise as the coupon. */
    PRINCIPAL("Principal on the city's paper", true, false),

    /** A dollar coupon abroad; not paying it is the default rule in Game.checkForeignSolvency(), not this one. */
    FOREIGN_INTEREST("Interest on the dollar debt", true, false),

    /** Dollar principal abroad, for the coupon's reason. */
    FOREIGN_PRINCIPAL("Principal on the dollar debt", true, false),

    /** What the treasury pays on its advances: a coupon to its own central bank, and remitted back to it less what reserves cost. */
    CENTRAL_BANK_INTEREST("Interest to the central bank", true, false),

    /** Pensions: a promise made to people who have stopped working. */
    PENSIONS("Pensions", true, false),

    /** EI benefits: a promise made to the people out of work. */
    EI_BENEFITS("EI benefits", true, false),

    /** The health service's bill: its staff and its upkeep. */
    HEALTHCARE("Healthcare", true, false),

    /** The schools' bill: their staff and their upkeep. */
    SCHOOLS("The schools' bill", true, false),

    /** The police and the prisons - the city's own wages again. */
    SAFETY("Police and prisons", true, false),

    /** The utilities and transit, when they cost more than they collect: the city's own wages and running costs. */
    CITY_SERVICES("The city's own services", true, false),

    /** Student loans lent: the ledger lent it at enrolment, so refusing the cash would leave a student owing money never received. */
    STUDENT_LOANS("Student loans lent", true, false),

    /** The standing policy's top-up of the builders to break-even - the construction subsidy. */
    CONSTRUCTION_SUBSIDY("Construction subsidy", false, false),

    /** The standing policy's top-up of any other protected sector to break-even. */
    BUSINESS_SUBSIDIES("Business subsidies", false, false),

    /** Grants to students: a policy the city chose and can owe. */
    STUDENT_GRANTS("Student grants", false, false),

    /** The city's share of the repair bill on its own buildings: work done, and owed to the builders if it cannot be paid. */
    CITY_REPAIRS("Repairs to the city's own buildings", false, false),

    /** Land the city buys, from the land office or back from a sector; refused, it stays unbought. */
    LAND("Land purchases", false, true),

    /** Buildings the city orders; refused, they are not ordered. */
    BUILDINGS("Building purchases", false, true),

    /** Capital put into the commercial bank. */
    BANK_CAPITAL("Capital into the bank", false, true),

    /** Foreign currency bought for the vault. */
    RESERVE_PURCHASES("Reserve purchases", false, true),

    /** A bond bought back before it is due. */
    BUYBACKS("Bonds bought back", false, true);

    /** The player's words for it, for the Government tab's arrears list and the playtest. */
    public final String label;

    /** Paid whatever the treasury holds, past the ceiling if it must be. */
    public final boolean promise;

    /** Refused, it is simply not made; nothing is owed. */
    public final boolean purchase;

    TreasuryLine(String label, boolean promise, boolean purchase) {
        this.label = label;
        this.promise = promise;
        this.purchase = purchase;
    }

    /** True for a discretionary line whose refusal is owed as arrears. */
    public boolean accruesArrears() { return !promise && !purchase; }
}
