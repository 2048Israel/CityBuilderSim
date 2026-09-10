package ham.citybuildersim;

/**
 * A household with nobody of working age in it: a senior alone, or a senior
 * couple.
 *
 * No pay tier, because a tier is a wage and nobody here draws one. Its income
 * is the pension bill, split across the retired cells by how many pensioners
 * each shape holds - a senior couple draws two - and that is also the weight
 * its money follows: a worker retiring into SENIOR_ALONE carries their wallet
 * across the row boundary, and one of a senior couple dying leaves half the
 * couple's position to the survivor and takes the other half out of the city.
 *
 * FamilyModel keeps the retired at tier index 0 by convention; this class is
 * where that convention stops - a retired cell has no tier at all, and sums
 * into RETIRED_ROW.
 */
public class RetiredHousehold extends Household {

    public RetiredHousehold(FamilyStructure shape) {
        super(shape);
        if (!shape.isRetired()) {
            throw new IllegalArgumentException(shape + " has an earner");
        }
    }

    @Override public PayTier tier()      { return null; }
    @Override public int row()           { return RETIRED_ROW; }
    @Override public boolean isRetired() { return true; }
    @Override public int grownUps()      { return shape.membersOf(AgeBand.SENIOR); }
}
