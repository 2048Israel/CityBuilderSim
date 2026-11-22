package ham.citybuildersim;

/**
 * Closed forms for the amounts among a cell's holders (HC1).
 * WHY: a cell average cannot say who has a thin buffer or a large balance.
 * The mean is conditional on holding; callers apply their holding share once.
 */
public final class CellSpread {
    private CellSpread() { }

    /** SCF 2022 card mean/median: sqrt(2 log(6.1/2.7)). */
    public static final double CARD_SIGMA = Math.sqrt(2 * Math.log(6.1 / 2.7));
    /** SCF 2025 total debt mean/median: sqrt(2 log(179.1/88.1)). */
    public static final double TOTAL_SIGMA = Math.sqrt(2 * Math.log(179.1 / 88.1));
    /** Estimated dispersion of liquid savings pending the wealth study. */
    public static final double SAVINGS_SIGMA = 1.5;
    /** SCF home-secured debt mean/median. */
    public static final double HOME_SIGMA = Math.sqrt(2 * Math.log(239.7 / 183.6));

    /** Probability an amount is at or below x, including the point mass at zero. */
    public static double below(double x, double mean, double sigma) {
        if (x < 0) return 0;
        if (!(mean > 0)) return 1;
        if (!(x > 0)) return 0;
        if (!(sigma > 0)) return x >= mean ? 1 : 0;
        return BusinessDebtManager.normalCdf((Math.log(x / mean) + sigma * sigma / 2) / sigma);
    }

    /** E[X; X > x], a part of the unconditional mean, never a conditional mean. */
    public static double aboveMean(double x, double mean, double sigma) {
        if (!(mean > 0)) return 0;
        if (!(x > 0)) return mean;
        if (!(sigma > 0)) return mean > x ? mean : 0;
        return mean * BusinessDebtManager.normalCdf((Math.log(mean / x) + sigma * sigma / 2) / sigma);
    }

    /** E[(gap - savings)+], the credit needed by a spread of liquid buffers. */
    public static double shortfall(double gap, double mean, double sigma) {
        if (!(gap > 0)) return 0;
        return Math.max(0, gap * below(gap, mean, sigma) - belowMean(gap, mean, sigma));
    }

    /** E[X; X <= x], evaluated directly to retain thin tails beside large buffers. */
    public static double belowMean(double x, double mean, double sigma) {
        if (!(mean > 0) || !(x > 0)) return 0;
        if (!(sigma > 0)) return x >= mean ? mean : 0;
        return mean * BusinessDebtManager.normalCdf((Math.log(x / mean) - sigma * sigma / 2) / sigma);
    }

    /** E[min(X, payment)], used for repayments without inventing negative debt. */
    public static double cappedMean(double payment, double mean, double sigma) {
        if (!(payment > 0)) return 0;
        return Math.max(0, belowMean(payment, mean, sigma)
                + payment * (1 - below(payment, mean, sigma)));
    }
}
