package ham.citybuildersim;

/**
 * Adults who are out of work, as one ledger per situation.
 *
 * Jerus, 2026-09-11: "we are to add a new household structure called
 * unemployed, these are different, these will just sum up by age the
 * unemployed... and they will have their own cashflow and stuff."
 *
 * Until this class every adult was put in a family at the tier mix of the
 * filled jobs, so the out of work shared a tier's wages - an unskilled single
 * adult took home $1,262 in a month of 40% unemployment. The families are now
 * built from the adults who work (FamilyModel.rebuild), and whoever is left
 * is here: one adult per household, no tier, in one of three situations.
 *
 *   ON_EI     lost a job, or came for one and did not get it, in the last
 *             twelve months; draws Employment Insurance (see Unemployment)
 *   OFF_EI    past the twelfth month, or never insured; lives on savings and
 *             the credit line, and is evicted when neither covers the rent
 *   UNHOUSED  evicted: no rent, no income, going without, sick faster
 *
 * THE MONEY FOLLOWS THEM. A worker who loses a job carries their savings and
 * debt from the family cells into this row; a hire carries them back; a
 * claimant whose twelfth month ends moves theirs from ON_EI to OFF_EI. Weighed
 * by grown-ups, like every other move - see HouseholdBalance.followThePeople().
 *
 * WHERE THEY LIVE is FamilyModel's: a studio of their own, five to a home
 * with their own kind when priced out or short of doors, and no door when
 * both valves fail. The rent share that decides is handed in by Game.
 */
public class UnemployedHousehold extends Household {

    /** Where one of them stands. */
    public enum Status {
        ON_EI("On EI"),
        OFF_EI("EI run out"),
        UNHOUSED("Lost their home");

        private final String label;
        Status(String label) { this.label = label; }
        public String label() { return label; }
    }

    private final Status status;

    public UnemployedHousehold(Status status) {
        super(null);
        this.status = status;
    }

    public Status status() { return status; }

    @Override public PayTier tier()      { return null; }
    @Override public int row()           { return UNEMPLOYED_ROW; }
    @Override public boolean isRetired() { return false; }
    @Override public int grownUps()      { return 1; }
    @Override public int size()          { return 1; }

    /** The EI bill is split among those still drawing it. */
    @Override public double earningWeight() { return status == Status.ON_EI ? 1 : 0; }

    /** Only once EI has ended - Jerus: "EI ends, savings gone". */
    @Override public boolean canBeEvicted() { return status == Status.OFF_EI; }

    /**
     * THE OUT OF WORK CARRY THE UNSKILLED TIER'S MONEY, NOT THE CITY'S.
     *
     * Measured the first night: an adult whose EI had run out held $5.6M in a
     * city where a working unskilled single held nothing, because the money
     * followed them out of the city-wide pool - every household that moved
     * that month, the rich ones included - and nobody was ever evicted. Who is
     * out of work is the labour market's surplus, and a graduate who finds no
     * post at their own level takes one below it, so the surplus sits at the
     * bottom of the ladder: the unskilled. Their money moves with that tier's
     * first, and only past it with the city's.
     */
    @Override public int stockGroup() { return PayTier.UNSKILLED.ordinal(); }

    @Override public String label() { return "Out of work, " + status.label().toLowerCase(); }

    /** "UNEMPLOYED:ON_EI". The save's key. */
    @Override public String key()   { return "UNEMPLOYED:" + status.name(); }
}
