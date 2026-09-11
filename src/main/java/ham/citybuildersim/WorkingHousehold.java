package ham.citybuildersim;

/**
 * A household with an earner in it, at one pay tier.
 *
 * Its income is the tier's wage bill, split across the tier's cells by how
 * many earners each shape fields - a couple takes home twice what a single
 * adult does, five adults sharing five times. Its money follows its earners
 * when the household changes shape, for the same reason.
 *
 * There is no SharedHousehold. Five adults sharing differ from a couple in
 * nothing this ledger does - they pay one rent, they draw on one credit line
 * per household, they discharge together - and a subclass that overrode
 * nothing would be a costume. The flatshare's difference is upstream, in how
 * FamilyModel forms it and what the rent per door does to five wages at once.
 */
public class WorkingHousehold extends Household {

    private final PayTier tier;

    public WorkingHousehold(FamilyStructure shape, PayTier tier) {
        super(shape);
        if (shape.isRetired()) {
            throw new IllegalArgumentException(shape + " has no earner");
        }
        this.tier = tier;
    }

    @Override public PayTier tier()      { return tier; }
    @Override public int row()           { return tier.ordinal(); }
    @Override public boolean isRetired() { return false; }
    @Override public int grownUps()      { return shape.earners(); }

    /**
     * A graduate's student loan, repaid out of the family's wages over nine
     * and a half years. Interest free. See Household.STUDENT_LOAN_MONTHS.
     */
    @Override protected double studentRepayment() { return studentDebt / STUDENT_LOAN_MONTHS; }
}
