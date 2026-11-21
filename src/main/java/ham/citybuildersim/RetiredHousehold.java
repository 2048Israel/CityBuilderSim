package ham.citybuildersim;

/**
 * A household with nobody of working age in it: a senior or an elder, alone or
 * as a couple, at the pay tier its people retired from.
 *
 * No pay tier of its own, because a tier is a wage and nobody here draws one:
 * tier() is null, row() is RETIRED_ROW, and every sum and screen by row reads
 * the retired as one row. Its income is the pension bill, split across the
 * retired cells by how many pensioners each shape holds - a senior couple draws
 * two - and that is also the weight its money follows.
 *
 * WHY A FORMER TIER (HS1, 2026-11-18; the households study, 3.2). Jerus: "the
 * elderly are routinely broke." The four retired cells had no tier, so a worker
 * retiring left their savings in their pay row for whoever joined it, and the
 * retired row lived on the pension and on whatever the census's pool happened
 * to hand it: at month 4,000 of the playtest city a senior living alone held 9%
 * of a working household's net worth, and an Elite couple retired as poor as an
 * unskilled widow. Now each retired shape is six cells, one for each tier its
 * people worked at (24 in all), the month's flows carry a retiree's money into
 * the cell of the tier they left (HouseholdBalance, MONEY FOLLOWS PEOPLE), and
 * the cells of one former tier are a stock group of their own (stockGroup()),
 * so an Elite couple's money stays Elite through the census's reshuffles.
 *
 * A PENSIONER IS ANYONE PAST THE RETIREMENT AGE, not anyone in the senior band.
 * This counted members of SENIOR only, and when the band split on 2026-09-15
 * the over-85s inherited a household that reported NO pensioners in it: zero
 * weight in HouseholdBalance's row split, so zero pension, so an elder paid
 * rent out of savings until there were none. Measured on DenominationCheck's
 * founding: the landlord's revenue fell, it shed plant under the rule that a
 * firm which cannot pay must, and the city went from 7,955 homes and 18,400
 * people to 2,881 homes and 4,200 over fifteen years - while the same city on
 * the shipped build never lost a single home. Asking the BAND whether it is
 * retirement age, rather than naming one, is what makes a third band safe.
 */
public class RetiredHousehold extends Household {

    /** The tier this cell's people worked at before they retired (HS1). */
    private final PayTier former;

    public RetiredHousehold(FamilyStructure shape, PayTier former) {
        super(shape);
        if (!shape.isRetired()) {
            throw new IllegalArgumentException(shape + " has an earner");
        }
        if (former == null) throw new IllegalArgumentException(shape + " needs the tier its people retired from");
        this.former = former;
    }

    @Override public PayTier tier()      { return null; }
    @Override public int row()           { return RETIRED_ROW; }
    @Override public boolean isRetired() { return true; }
    @Override public int grownUps()      { return pensionersIn(shape); }

    /** The tier this cell's people worked at before they retired. */
    public PayTier formerTier() { return former; }

    /** The cells of one former tier, together: retirees and the census's reshuffles keep the tier (HS1). */
    @Override public int stockGroup() { return retiredGroup(former); }

    /** "SENIOR_ALONE:SKILLED": the shape and the tier its people retired from. The save's key. */
    @Override public String key() { return shape.name() + ":" + former.name(); }

    /** "Senior living alone, formerly Skilled". */
    @Override public String label() { return shape.getLabel() + ", formerly " + former.getLabel(); }

    /** The stock group of the retired from this tier: after every row's, one for each tier (Household.STOCK_GROUPS). */
    public static int retiredGroup(PayTier former) { return ROWS + former.ordinal(); }

    /** Everyone in the shape who is past the retirement age, whichever band they are in. */
    public static int pensionersIn(FamilyStructure shape) {
        int n = 0;
        for (AgeBand band : AgeBand.values()) {
            if (band.isRetirementAge()) n += shape.membersOf(band);
        }
        return n;
    }
}
