package ham.citybuildersim;

/**
 * The currency's unit, and the power to lop zeros off it.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * Jerus: "inflation cause numbers to be huge in the span of 300 years... so we
 * need a button to prevent that no? like still have inflation but perhaps even
 * so often the player presses a button and everything gets divided by 10, or
 * 100, or whatever, so that bread doesnt show as 300M and we start getting
 * binary rounding issues all over the place."
 *
 * Which is a currency reform, and it is what every country that has ever had a
 * long run of inflation has actually done. France lopped two zeros in 1960,
 * Germany three in 1923 and again in 1948, Brazil six times between 1967 and
 * 1994, Turkey six zeros in 2005. It is not a display trick and it is not a
 * cheat; it is a change of UNITS, and the whole content of this class is that
 * it must be nothing else.
 *
 * ==================== WHAT A REFORM IS ====================
 *
 * One new Danzik dollar is worth `unit` founding Danzik dollars. It starts at
 * one and multiplies by ten, a hundred or a thousand each time the player
 * reforms. Everything nominal - every price, wage, balance, debt, reserve and
 * exchange rate in the city - is divided by the same factor at the same moment,
 * so that:
 *
 *   - every RATIO is unchanged. The rent burden, the capital ratio, the tax
 *     take as a share of GDP, the price index: none of them move.
 *   - every REAL quantity is unchanged. The same number of people live in the
 *     same houses and eat the same amount of bread.
 *   - the city's own history is redrawn in the new unit, so the graphs do not
 *     have a cliff in them.
 *
 * DenominationCheck asserts exactly that, by running one city and a lopped copy
 * of it side by side for years and requiring that they stay the same city. That
 * assertion is the reason this is safe to ship: a reform that missed a single
 * balance would show up as two cities that drifted apart.
 *
 * ==================== THE CONSTANTS ARE STATE NOW ====================
 *
 * The hard part is not the balances. It is that this codebase has money written
 * into it as compile-time constants - a House costs $30, a shop's opening price
 * is $0.30, a bank branch gathers $60,000 of deposits, a block of land starts at
 * $70 - and a `static final` cannot be divided by anything.
 *
 * Left alone they would be a hundred times dearer in real terms the morning
 * after a hundred-to-one reform, which is not a rounding error, it is a
 * different game. So every money constant that is READ AGAINST LIVE MONEY has
 * been turned into an instance field seeded from the constant, and those fields
 * are redenominated with everything else. The constant survives as the founding
 * value and the documentation of where the number came from.
 *
 * Foreign prices are the exception and it is not an oversight: food at $0.20 and
 * building materials at $2.00 abroad are quoted in DOLLARS OF THE REST OF THE
 * WORLD, which no act of this city's parliament can change. They reach the city
 * multiplied by the exchange rate, and the exchange rate is what gets divided.
 *
 * ==================== WHEN THE BUTTON APPEARS ====================
 *
 * Jerus asked for it to unlock past a threshold and then be the player's
 * decision, which is also how it works in life: a currency reform is a
 * deliberate political act, announced, with a date. It never fires on its own.
 *
 * @author Jerus
 */
public class Denomination {

    /**
     * How many FOUNDING dollars one of today's dollars is worth.
     *
     * One at the founding. A hundred after lopping two zeros: each new dollar
     * is a hundred old ones, so every price expressed in new dollars is a
     * hundredth of what it was.
     */
    private double unit = 1;

    /** How many reforms this city has been through. Names the currency. */
    private int reforms;

    /**
     * How far prices have to have risen before the button appears.
     *
     * TEN-FOLD, measured on the city's own price index, which is the honest
     * measure - the world's inflation is not the city's business and the
     * exchange rate is a price of one thing. At ten times the founding level a
     * loaf that cost thirty cents costs three dollars and the wage bill of a
     * large city has an extra digit; that is the point where lopping one zero
     * is tidying rather than vandalism.
     *
     * A player who never presses it is never worse off than they are today.
     */
    public static final double UNLOCK_AT = 10.0;

    /** The factors the player may choose between. */
    public static final double[] FACTORS = { 10, 100, 1000 };

    /**
     * The ceiling on the unit, and it is a numeric guard rather than a policy.
     *
     * A double carries about fifteen significant figures. The unit multiplies by
     * up to a thousand per reform, and past 1e12 the smallest prices in the game
     * - a tonne of scrap at $0.0004 - would start losing digits at the bottom,
     * which is the exact failure Jerus was worried about arriving from the other
     * direction. Four reforms of a thousand is more than three centuries of any
     * inflation this game can produce.
     */
    public static final double MAX_UNIT = 1e12;

    public double getUnit()  { return unit; }
    public int getReforms()  { return reforms; }

    /**
     * A founding-dollar constant, expressed in today's money.
     *
     * The one function every redenominated constant is seeded through. Used at
     * construction and after a reform, never in a hot loop - the fields it
     * feeds are ordinary state and are scaled directly.
     */
    public double money(double foundingDollars) {
        return unit > 0 ? foundingDollars / unit : foundingDollars;
    }

    /** Whether the reform button should be offered, given the city's price level. */
    public boolean unlocked(double priceIndex) {
        return priceIndex >= UNLOCK_AT && unit < MAX_UNIT;
    }

    /** Whether a particular factor may be applied. */
    public boolean canLop(double factor) {
        return factor >= 2 && unit * factor <= MAX_UNIT;
    }

    /**
     * Records the reform. Dividing the city's money is Game.redenominate()'s
     * job; this only moves the unit, and the two are called together or the
     * city's books are denominated in a currency that does not exist.
     */
    public void lop(double factor) {
        if (!canLop(factor)) return;
        unit *= factor;
        reforms++;
    }

    /**
     * What the money is called, which changes when it is reformed.
     *
     * Real reforms rename: the nouveau franc, the second cruzado, the new
     * Turkish lira. A player who has lopped twice should be able to tell at a
     * glance that the $3.00 loaf on their screen is not the $3.00 loaf of two
     * centuries ago.
     */
    public String name() {
        if (reforms <= 0) return Currency.NAME;
        return ordinal(reforms + 1) + " " + Currency.NAME;
    }

    /** What one of today's dollars is worth in founding money, for the screen. */
    public String describeUnit() {
        if (reforms <= 0) return "founding " + Currency.NAME + "s";
        return String.format("1 = %,.0f founding %ss", unit, Currency.NAME);
    }

    private static String ordinal(int n) {
        return switch (n) {
            case 2 -> "second";
            case 3 -> "third";
            case 4 -> "fourth";
            case 5 -> "fifth";
            case 6 -> "sixth";
            case 7 -> "seventh";
            case 8 -> "eighth";
            default -> n + "th";
        };
    }

    /* -------------------------------- carrying -------------------------------- */

    public double[] toSaveArray() {
        return new double[] { unit, reforms };
    }

    public void restore(double[] saved) {
        if (saved == null || saved.length < 2) return;
        if (saved[0] > 0) unit = saved[0];
        reforms = (int) Math.round(saved[1]);
    }

    public void reset() {
        unit = 1;
        reforms = 0;
    }
}
