package ham.citybuildersim;

/**
 * The owner families, as one ledger: one two-adult household per company that
 * has listed and not gone broke.
 *
 * Jerus, 2026-11-02, on the households study: business owners are "few in
 * nature and very rich", "one per listed company", "No job, lives of
 * dividends or sells or whatever"; "the others still have shares, they just
 * dont get the initial ownership"; "if sector goes broke he just joins the
 * regular pop pool".
 *
 * WHY A CELL OF THEIR OWN. The founders' grant used to go to every household,
 * by grown-ups, and ordinary households ended up owning 98% of what
 * households owned at month 21 (study 2.3): the people who started the
 * company held none of it more than anybody else. Now a listing company's
 * founders' shares go to this cell alone, each family an equal part of every
 * founding stake; the others buy their shares at the offerings and on the
 * exchange, by the households' rules (tradesShares()), and the stakes here
 * are sold down by the line's own rule (OWNER_SELL_A_YEAR).
 *
 * WHAT AN OWNER HAS. No wage - they live on dividends and on what they sell -
 * so the earning weight is nothing and the labour force does not count them
 * (PopulationManager.setOwners). Their savings, bonds and money abroad are
 * kept by the ordinary rules: only the shares are theirs alone.
 *
 * WHO IS IN IT is the line's: HouseholdBalance.ownerFamilies() companies, two
 * adults each, taken out of the adult pool with the outside adults
 * (FamilyModel.rebuild).
 */
public class OwnerHousehold extends Household {

    /**
     * The share of each founding stake the line offers on the exchange in a
     * year: a stake falls to about a fifth in 30 years (.95^30 = .21).
     */
    public static final double OWNER_SELL_A_YEAR = .05;

    /**
     * A twelfth of the year's sale, compounded: a month's share of each
     * holding the line offers on the exchange, so twelve months leave
     * 1 - OWNER_SELL_A_YEAR of it.
     */
    public static final double OWNER_SELL_A_MONTH = 1 - Math.pow(1 - OWNER_SELL_A_YEAR, 1.0 / 12);

    /** The line's key: the cell's name on the books, the save and the exchange's orders. */
    public static final String KEY = "OWNER";

    /**
     * Months a company's fair value sits at the exchange's floor before its
     * family gives up and joins the regular pool: the window
     * Migration.DECLINE_MONTHS gives a tier.
     */
    public static final int OWNER_BROKE_MONTHS = 12;

    /**
     * Whether a city runs the owner line: off until Jerus's call on what pays
     * the retired's care once the founders' dividends are the owners' (BO1's
     * notes, section 1: with it on, the playtest city had about a third of
     * its people at month 120). A harness switches a city's line on itself.
     */
    public static final boolean IN_CITIES = false;

    public OwnerHousehold() {
        super(null);
    }

    @Override public PayTier tier()      { return null; }
    @Override public int row()           { return OWNER_ROW; }
    @Override public boolean isRetired() { return false; }
    @Override public int grownUps()      { return 2; }
    @Override public int size()          { return 2; }

    @Override public String label() { return "Owner families"; }
    @Override public String key()   { return KEY; }

    /** No wage: "No job." Nobody's income is split to an owner family. */
    @Override public double earningWeight() { return 0; }

    /**
     * Not by the households' rules: the founding stakes are sold down by the
     * line's own (OWNER_SELL_A_YEAR), and the line takes no offering.
     */
    @Override public boolean tradesShares() { return false; }

    /** They hold bonds, money abroad and the city's paper by the ordinary rules. */
    @Override public boolean canInvest() { return true; }
}
