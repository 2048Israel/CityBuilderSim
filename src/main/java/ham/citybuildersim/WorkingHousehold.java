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
    /** Which adult stage its grown-ups are in (HS2): the young, 18-34, or the established, 35-69. */
    private final AdultStage stage;

    /** An established household: every working household before the stages was one. */
    public WorkingHousehold(FamilyStructure shape, PayTier tier) {
        this(shape, tier, AdultStage.ESTABLISHED);
    }

    public WorkingHousehold(FamilyStructure shape, PayTier tier, AdultStage stage) {
        super(shape);
        if (shape.isRetired()) {
            throw new IllegalArgumentException(shape + " has no earner");
        }
        if (!stage.forms(shape)) {
            throw new IllegalArgumentException(stage + " forms no " + shape);
        }
        this.tier = tier;
        this.stage = stage;
    }

    @Override public PayTier tier()      { return tier; }
    /** The adult stage of its grown-ups (HS2). */
    public AdultStage stage()            { return stage; }

    /**
     * The young's money is a stock group of its own, one a tier after the
     * retired's (HS2): what a young household leaves behind goes first to the
     * young of its tier, so the established's savings are not spread over a
     * twenty-five-year-old's. The established keep their row's.
     */
    @Override public int stockGroup() {
        return stage == AdultStage.YOUNG ? youngGroup(tier) : row();
    }

    /** The stock group of the young at a tier: after the retired's, one a tier (Household.STOCK_GROUPS). */
    public static int youngGroup(PayTier tier) { return ROWS + PayTier.values().length + tier.ordinal(); }

    /** "COUPLE:SKILLED", and "COUPLE:SKILLED:YOUNG" for the young: the established keep the keys from before the stages. */
    @Override public String key() { return super.key() + stage.keySuffix(); }

    @Override public String label() {
        return stage == AdultStage.YOUNG ? super.label() + ", young" : super.label();
    }
    @Override public int row()           { return tier.ordinal(); }
    @Override public boolean isRetired() { return false; }
    @Override public int grownUps()      { return shape.earners(); }

    /**
     * A graduate's student loan, repaid out of the family's wages over nine
     * and a half years: a 114th of the balance a month. See
     * Household.STUDENT_LOAN_MONTHS.
     */
    @Override protected double studentRepayment() { return studentDebt / STUDENT_LOAN_MONTHS; }

    /**
     * ...and the month's interest on it, at the city's annual rate over
     * twelve, paid on top of the instalment. The only household that is
     * charged any: a student's balance accrues nothing while they study and
     * a prisoner's is frozen. See Household's THE LOAN'S RATE.
     */
    @Override public double studentInterestAt(double annualRate) {
        return studentDebt * Math.max(0, annualRate) / 12;
    }
}
