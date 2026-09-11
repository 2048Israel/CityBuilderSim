package ham.citybuildersim;

/**
 * Full-time students, as one ledger.
 *
 * Jerus, 2026-09-11, asked where the students go now the out of work have
 * books of their own: "new category". They were counted as unemployed -
 * PopulationManager.getUnemployed() never took them off the workforce - and
 * lived in family households whose tier's wages they shared.
 *
 * WHAT A STUDENT LIVES ON, per Jerus: their own savings, a grant, and a
 * student loan. The grant is the Canada Student Grant ($525 a month of study,
 * 2026-27) as a share of the unskilled wage, paid by the treasury. The loan is
 * the treasury's too and, for now, it never runs out - "can't run out, for
 * now" - so where a family would go to the bank's credit line and then go
 * without, a student draws a loan for whatever is still short. They pay their
 * own tuition. The loan is carried into a family when they graduate, and
 * repaid there out of wages; see WorkingHousehold.studentRepayment().
 *
 * They live in a home of their own, like the unemployed - a studio, or five
 * to a home with other students.
 */
public class StudentHousehold extends Household {

    /** A loan that covers anything is the plan's to count on; large, not infinite, so the arithmetic stays finite. */
    private static final double UNLIMITED = 1e15;

    public StudentHousehold() {
        super(null);
    }

    @Override public PayTier tier()      { return null; }
    @Override public int row()           { return STUDENT_ROW; }
    @Override public boolean isRetired() { return false; }
    @Override public int grownUps()      { return 1; }
    @Override public int size()          { return 1; }

    @Override public String label() { return "Full-time students"; }
    @Override public String key()   { return "STUDENT"; }

    /** The student loan: whatever is still short, from the treasury, at no interest. */
    @Override
    protected double fundShortfall(double still, double disposablePer) {
        if (!(still > 0)) return 0;
        studentBorrowed += still;
        studentDebt += still;
        return still;
    }

    @Override protected double planningRoom() { return UNLIMITED; }
}
