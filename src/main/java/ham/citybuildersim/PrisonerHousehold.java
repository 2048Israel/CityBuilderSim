package ham.citybuildersim;

/**
 * Adults serving a sentence, as one ledger: the prisoners' ledger.
 *
 * Jerus, 2026-09-11: "yes that means another population category, only adults
 * can go to jail", and of a prisoner's money, held in a prisoners' ledger.
 *
 * WHAT A PRISONER HAS. No income, no rent, no groceries - the city houses and
 * feeds them, which is what the prisons' upkeep is for - and no credit. Their
 * debts are FROZEN: no interest runs, nothing is discharged, nothing is
 * borrowed. Their savings sit in the bank and do nothing else: no shares
 * bought, nothing sent abroad, no offering subscribed.
 *
 * THE MONEY FOLLOWS THEM, like everyone's. An adult sent down carries their
 * savings and debt out of the cell they were in and into this one, and a
 * prisoner released carries them back out into the out-of-work pool -
 * HouseholdBalance.followThePeople() does it on the net change, as it does
 * for every other move. Who goes to prison is mostly who is out of work, so
 * the money moves with the unskilled tier's first, as the out of work's does.
 *
 * WHO IS IN IT is Crime's: six monthly cohorts, each serving six months.
 */
public class PrisonerHousehold extends Household {

    public PrisonerHousehold() {
        super(null);
    }

    @Override public PayTier tier()      { return null; }
    @Override public int row()           { return PRISON_ROW; }
    @Override public boolean isRetired() { return false; }
    @Override public int grownUps()      { return 1; }
    @Override public int size()          { return 1; }

    @Override public String label() { return "In prison"; }
    @Override public String key()   { return "PRISONER"; }

    /** Nobody's income is split to a prisoner. */
    @Override public double earningWeight() { return 0; }

    /** Most of them were out of work: see UnemployedHousehold.stockGroup(). */
    @Override public int stockGroup() { return PayTier.UNSKILLED.ordinal(); }

    /** The city feeds them. */
    @Override protected double baskets() { return 0; }

    /** Frozen while they are inside. */
    @Override protected boolean debtFrozen() { return true; }

    /** Held in the ledger. */
    @Override public boolean canInvest() { return false; }

    /** No lender lends to somebody inside. */
    @Override double creditRoom(double disposablePer) { return 0; }

    /** ...and they plan nothing: nothing to spend, no room to borrow. */
    @Override protected double planningRoom() { return 0; }
}
