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
 * (FamilyModel.rebuild) - at most OWNER_ADULT_SHARE of the city's adults
 * (Game.ownerAdultsWanted()).
 *
 * WHY THE SHARE (BO1-S, 2026-11-20). Two adults a company, whatever the town's
 * size, took a founding town's whole labour force: the supply takes the owners
 * off every band (PopulationManager.workforceByBand), so a 1900 farm town had
 * no builders at all, a held town's Home Care was unbuilt in 120 months (53
 * with the line off), and a Medellin city owned a twelfth of its ground at
 * month 2,000. BO1's "a family a company" holds now only in a city of
 * 2 / OWNER_ADULT_SHARE adults a company (200 for five): under it the cell holds
 * fewer families than companies, and under 1 / OWNER_ADULT_SHARE adults (20) it
 * is empty, so the founders' grant waits, as it always did for an empty cell,
 * and the company lists once the town has grown to its family.
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

    /** The most of a city's adults the owner families may be: two adults a company took a founding town's whole labour force (BO1-S). */
    public static final double OWNER_ADULT_SHARE = .05;

    /**
     * The owner families' adults in a city of these adults with this many
     * companies' families (reserved ones counted): two a family, at most
     * OWNER_ADULT_SHARE of the adults. Game.ownerAdultsWanted() is this, on the
     * month's figures.
     */
    public static double adultsOf(int families, double adults) {
        return Math.min(2.0 * Math.max(0, families), OWNER_ADULT_SHARE * Math.max(0, adults));
    }

    /**
     * Whether a city runs the owner line: on since BO1-S. BO1 shipped it off
     * because, on 0.9.90, the playtest city with it on had about a third of its
     * people at month 120 (BO1's notes, section 1); on 0.9.160 the same city
     * with it on is a third larger at month 121 (BO1-S's notes). A bare balance
     * never switches it and keeps the grant by grown-ups; a harness that
     * measures the line against today's rule switches a city's line itself.
     */
    public static final boolean IN_CITIES = true;

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
