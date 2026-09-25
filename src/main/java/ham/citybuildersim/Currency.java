package ham.citybuildersim;

import java.text.Normalizer;

/**
 * What the city's money is called, and how it is written.
 *
 * ONE PLACE THAT DECIDES, which is the whole reason this exists rather than a
 * string literal per screen. Twenty screens each writing "$" is twenty screens
 * that will disagree the first time one of them has to say WHICH dollar.
 *
 * THE CONVENTION IS JERUS'S, from when the exchange rate went in and every
 * screen started having to say which currency it meant:
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
 * ==================== EACH CITY NAMES ITS OWN (0.7.10) ====================
 *
 * Until 0.7.10 this class was five static finals: every city's money was the
 * Danzik dollar. Jerus, on the new-game screen: "a small thing at the start
 * of the game where you choose the name of the city and the currency", named
 * from the city with "a tick to manually input the currency name and a 3
 * letter code if wanted". So a Currency is a VALUE now, one per city, held on
 * the city's founding record (Founding) and read through the game -
 * Game.getCurrency() - by every screen, the year book and the playtest.
 *
 * READ THROUGH THE GAME, NOT STATIC STATE SET ON FOUNDING, because one process
 * holds several cities at once: NewGameCheck keeps a pristine city beside a
 * played one, the playtest reloads into a second Game to compare, and the
 * screens outlive every city they draw. A static would name whichever city
 * was founded or loaded LAST, so the first city's year book could print the
 * second city's money. Carried by the city, a name cannot leak from one city
 * into the next by construction - buildWorld()'s argument again.
 *
 * WHAT IS DERIVED FROM A CITY'S NAME (fromCityName()):
 *
 *   - the name is "<City> dollar" and the plural "<City> dollars";
 *   - the code is the first three letters A-Z of the name, upper-cased
 *     (deriveCode() says what happens to accents, other scripts, short names
 *     and the foreign code);
 *   - the symbol stays "$" (see SYMBOL);
 *   - the qualified symbol is the name's first letter, upper-cased, and "$":
 *     Arden gives the A$.
 *
 * WHAT IS TYPED BY HAND (typed()): a name and a three-letter code. The code
 * must be exactly three letters A-Z and never the foreign one (codeProblem()).
 * The plural is the name with an "s" unless it already ends in one - English
 * has irregular plurals this cannot know, and the player can type the name so
 * the plural reads ("crown" gives "crowns"); the symbol stays "$"; the
 * qualified symbol is the typed name's first letter and "$".
 *
 * THE FOREIGN MONEY IS NOT A CHOICE. It stays the US dollar, US$, USD, as a
 * static: the world holds exactly one other money and no city renames it.
 *
 * @author Jerus
 */
public final class Currency {

    /* ------------------------------------------------------------ the world's */

    /** The world's money, which the game holds exactly one of. */
    public static final String FOREIGN_NAME   = "US dollar";
    public static final String FOREIGN_CODE   = "USD";
    public static final String FOREIGN_SYMBOL = "US$";

    /** ...and its hundredth, for what one local dollar buys once it is worth less than one of them. */
    public static final String FOREIGN_CENT_SYMBOL = "US\u00a2";

    /** Foreign money, always marked, because it is never the default here. */
    public static String foreign(String amount) { return FOREIGN_SYMBOL + amount; }

    /* ------------------------------------------------------------ the rules */

    /**
     * Written alone, where nothing foreign is in sight: "$" for every city,
     * derived or typed. Every screen writes local money through the one money
     * formatter with this sign in it, and a player who names their money the
     * crown has renamed the money, not how a price tag is printed.
     */
    public static final String SYMBOL = "$";

    /** What a derived currency is: "<City> dollar". */
    public static final String DERIVED_NOUN = "dollar";

    /** Exactly this many letters A-Z in a code, as ISO 4217 has. */
    public static final int CODE_LENGTH = 3;

    /** What a derived code is padded with when the name has fewer than three letters A-Z: ISO 4217's own letter for money that belongs to no country (XAU, XDR). */
    public static final char CODE_PAD = 'X';

    /** The longest name a player may type for their money: a line on the trade tab's rate, not a policy. */
    public static final int MAX_NAME_LENGTH = 32;

    /* ------------------------------------------------------------ the one a city had before 0.7.10 */

    /**
     * THE DANZIK DOLLAR: every city's money until 0.7.10, and so the money of
     * every save written before then, which has no currency of its own on
     * file (Founding.legacy()). DZD rather than the DAN a city named Danzik
     * derives today: it is the code the game always gave it, and a save keeps
     * what it was founded with.
     */
    public static final Currency DANZIK =
            new Currency("Danzik dollar", "Danzik dollars", "DZD", SYMBOL, "D$");

    /* ------------------------------------------------------------ the value */

    private final String name, plural, code, symbol, qualifiedSymbol;

    /** A currency exactly as given - the load path's door, which reads back what was saved without judging it. */
    public Currency(String name, String plural, String code, String symbol, String qualifiedSymbol) {
        this.name = name;
        this.plural = plural;
        this.code = code;
        this.symbol = symbol;
        this.qualifiedSymbol = qualifiedSymbol;
    }

    /** "Arden dollar". */
    public String name()            { return name; }
    /** "Arden dollars". */
    public String plural()          { return plural; }
    /** "ARD". */
    public String code()            { return code; }
    /** "$" - written alone. */
    public String symbol()          { return symbol; }
    /** "A$" - written where a foreign figure is on the same line. */
    public String qualifiedSymbol() { return qualifiedSymbol; }

    /** Local money, on a screen with no foreign figure on it. */
    public String local(String amount) { return symbol + amount; }

    /** Local money, on a screen that also shows dollars. */
    public String qualified(String amount) { return qualifiedSymbol + amount; }

    /** "Arden dollars per USD", as the exchange rate's unit. */
    public String rateUnit() { return plural + " per " + FOREIGN_CODE; }

    /** "the Arden dollar, A$, ARD" - the founding screen's line. */
    public String describe() { return "the " + name + ", " + qualifiedSymbol + ", " + code; }

    /* ------------------------------------------------------------ derived from the city */

    /**
     * The city's money named after the city: "Arden" gives the Arden dollar,
     * A$, ARD. The name is used as given, trimmed; see deriveCode() for the
     * code and initial() for the symbol's letter.
     */
    public static Currency fromCityName(String city) {
        String c = city == null ? "" : city.trim();
        return new Currency(c + " " + DERIVED_NOUN, c + " " + DERIVED_NOUN + "s",
                deriveCode(c), SYMBOL, initial(c) + SYMBOL);
    }

    /**
     * The code a city's name gives its money: its first three letters A-Z,
     * upper-cased.
     *
     * ACCENTS ARE FOLDED to the letter under them, so Zurich and Zürich are
     * both ZUR; a letter with no A-Z under it (the Danish Ø, anything in
     * another script) is skipped like a space or a digit, so Ørsted gives RST.
     *
     * FEWER THAN THREE LETTERS are padded with CODE_PAD: "Ur" gives URX, and a
     * name with no A-Z letter at all gives XXX - ISO's own code for "no
     * currency", which the player can replace by typing one.
     *
     * NEVER THE FOREIGN CODE, which is the world's money: a name that starts
     * U-S-D takes the next letter of the name as its third instead (Usdane
     * gives USA), and CODE_PAD if there is none (Usd gives USX).
     */
    public static String deriveCode(String city) {
        String letters = asciiLetters(city);
        StringBuilder code = new StringBuilder(letters.substring(0, Math.min(CODE_LENGTH, letters.length())));
        int next = code.length();
        while (code.length() < CODE_LENGTH) code.append(CODE_PAD);
        if (code.toString().equals(FOREIGN_CODE)) {
            code.setCharAt(CODE_LENGTH - 1, next < letters.length() ? letters.charAt(next) : CODE_PAD);
        }
        return code.toString();
    }

    /** The first letter of a name, upper-cased - any script - or the pad when it has none. */
    static String initial(String name) {
        if (name != null) {
            for (int i = 0; i < name.length(); i++) {
                int cp = name.codePointAt(i);
                if (Character.isLetter(cp)) return new String(Character.toChars(Character.toUpperCase(cp)));
                if (Character.isSupplementaryCodePoint(cp)) i++;
            }
        }
        return String.valueOf(CODE_PAD);
    }

    /** The name's letters A-Z, accents folded and upper-cased, everything else dropped. */
    private static String asciiLetters(String s) {
        if (s == null) return "";
        String folded = Normalizer.normalize(s, Normalizer.Form.NFD);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < folded.length(); i++) {
            char ch = Character.toUpperCase(folded.charAt(i));
            if (ch >= 'A' && ch <= 'Z') out.append(ch);
        }
        return out.toString();
    }

    /* ------------------------------------------------------------ typed by hand */

    /**
     * The money as the player named it, or null when either half does not
     * pass (nameProblem(), codeProblem()). The code is taken upper-cased, so
     * "ard" is ARD; nothing else about it is changed.
     */
    public static Currency typed(String name, String code) {
        if (nameProblem(name) != null || codeProblem(code) != null) return null;
        String n = name.trim();
        String plural = n.toLowerCase(java.util.Locale.ROOT).endsWith("s") ? n : n + "s";
        return new Currency(n, plural, code.trim().toUpperCase(java.util.Locale.ROOT),
                SYMBOL, initial(n) + SYMBOL);
    }

    /** Why a typed name will not do, in the player's words, or null when it will. */
    public static String nameProblem(String name) {
        String n = name == null ? "" : name.trim();
        if (n.isEmpty()) return "Name the currency, or untick the box to name it after the city.";
        if (n.length() > MAX_NAME_LENGTH) return "A currency's name can be at most " + MAX_NAME_LENGTH + " characters.";
        if (n.codePoints().noneMatch(Character::isLetter)) return "A currency's name needs at least one letter.";
        return null;
    }

    /**
     * Why a typed code will not do, in the player's words, or null when it
     * will: exactly CODE_LENGTH letters A-Z (upper-cased first), and never
     * FOREIGN_CODE - the world's money is not the city's to issue.
     */
    public static String codeProblem(String code) {
        String c = code == null ? "" : code.trim().toUpperCase(java.util.Locale.ROOT);
        if (!c.matches("[A-Z]{" + CODE_LENGTH + "}")) return "A currency code is exactly three letters, A to Z.";
        if (c.equals(FOREIGN_CODE)) return FOREIGN_CODE + " is the world's money; the city's needs its own code.";
        return null;
    }

    /* ------------------------------------------------------------ as a value */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Currency c)) return false;
        return name.equals(c.name) && plural.equals(c.plural) && code.equals(c.code)
                && symbol.equals(c.symbol) && qualifiedSymbol.equals(c.qualifiedSymbol);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(name, plural, code, symbol, qualifiedSymbol); }

    @Override
    public String toString() { return describe(); }
}
