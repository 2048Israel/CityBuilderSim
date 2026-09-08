package ham.citybuildersim;

/**
 * What the city's money is called, and how it is written.
 *
 * ONE PLACE THAT DECIDES, which is the whole reason this exists rather than a
 * string literal per screen. Twenty screens each writing "$" is twenty screens
 * that will disagree the first time one of them has to say WHICH dollar.
 *
 * THE CITY'S OWN MONEY IS THE DANZIK DOLLAR - Jerus, after the exchange rate
 * went in and every screen started having to say which currency it meant. The
 * convention is his:
 *
 *     USD 10,000  ->  D$14,560        when both are on the screen
 *                 ->  $14,560         when it is the only money in sight
 *
 * Which is how anybody writes about money: nobody says "CAD" in Toronto, and
 * everybody does the moment a US price is beside it. The player spends their
 * whole game in one currency and should not be made to read a currency code to
 * buy a house; they should be made to read one the instant a foreign price is
 * on the same line, because that is the instant it matters.
 *
 * @author Jerus
 */
public final class Currency {

    private Currency() { }

    /** The city's own money. */
    public static final String NAME   = "Danzik dollar";
    public static final String PLURAL = "Danzik dollars";
    public static final String CODE   = "DZD";

    /** Written alone, where nothing foreign is in sight. */
    public static final String SYMBOL = "$";

    /** ...and written where a foreign figure is on the same screen. */
    public static final String QUALIFIED = "D$";

    /** The world's money, which the game holds exactly one of. */
    public static final String FOREIGN_NAME   = "US dollar";
    public static final String FOREIGN_CODE   = "USD";
    public static final String FOREIGN_SYMBOL = "US$";

    /** Local money, on a screen with no foreign figure on it. */
    public static String local(String amount) { return SYMBOL + amount; }

    /** Local money, on a screen that also shows dollars. */
    public static String qualified(String amount) { return QUALIFIED + amount; }

    /** Foreign money, always marked, because it is never the default here. */
    public static String foreign(String amount) { return FOREIGN_SYMBOL + amount; }

    /** "Danzik dollars per US dollar", as the exchange rate's unit. */
    public static String rateUnit() {
        return PLURAL + " per " + FOREIGN_CODE;
    }
}
