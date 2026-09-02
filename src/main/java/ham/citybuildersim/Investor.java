package ham.citybuildersim;

/**
 * Whoever is paying for a building.
 *
 * Before this existed, processBuildOrder() ended in `cash -= totalCost` - the
 * city paid for everything, and that one line was the only thing in the whole
 * build path that cared who was buying. Materials, the construction queue and
 * addStack() are all indifferent. So making businesses build their own premises
 * is a matter of naming the payer, not of duplicating the build path.
 */
public interface Investor {

    String getName();

    double getCash();

    /** Take the money. Cash may go negative; credit picks it up the same month. */
    void spend(double amount);

    /**
     * Whether this investor can raise `amount` beyond its cash.
     *
     * The city always can - it has its own bond market. A business can borrow
     * as long as the project services its own debt, which is checked separately
     * in BusinessInvestment; this is only about access to credit at all.
     */
    boolean canBorrow(double amount);

    /** Raise `amount` on credit. Called only when canBorrow() said yes. */
    void borrow(double amount, int month);
}
