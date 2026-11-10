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
 * the plural reads ("crown" gives "crowns"); the symbol stays "$" unless a
 * sign is typed too (FM1, below); the qualified symbol is the typed name's
 * first letter and "$".
 *
 * THE FOREIGN MONEY IS NOT A CHOICE. It stays the US dollar, US$, USD, as a
 * static: the world holds exactly one other money and no city renames it.
 *
 * ==================== THE SIGN AND ITS PLACE (FM1) ====================
 *
 * Jerus, 2026-11-02: his Rotterdam typed "fRotterdam Guilder" for the sign,
 * because the only way to a sign other than "$" was to name the money and
 * have its first letter put in front of "$". So the sign is its own field
 * now, with its own place: typed("Krona", "KRO", "kr", true) is written
 * "2.6M kr", a "$" before is written "$2.6M" exactly as it always was, and
 * "€" after is "2.6M€". write() is the ONE writer of the sign for every
 * figure of the city's money; nothing else in the game puts a "$" on a
 * number. A space is written only where the character beside the number is a
 * letter ("CHF 2.6M", "2.6M kr", "R$2.6M"), and a minus is never moved by
 * write(): the caller keeps it outside, as "-$5" always was.
 *
 * WHERE A FOREIGN FIGURE IS IN SIGHT the qualified sign is written instead
 * (qualifiedFor()): the city's initial and the sign where the sign is one the
 * vault's moneys also use ("$", "¥", "€", "C$" - D$, T¥), the sign alone where
 * it is the city's own ("kr", "zł", "R$").
 *
 * THE ONE STATIC (writing()): ui/Money and Formats are static and cannot hold a
 * city, so they write the sign of the money the game last founded or loaded
 * (use()). It holds only how money is written, never what it is called, so the
 * leak warned of above costs a harness that sets a sign at most its sign.
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
     * Written alone, where nothing foreign is in sight: "$" for a city that
     * has not typed a sign of its own (typed(name, code, sign, after)). Every
     * screen writes local money through the one money formatter, which writes
     * the city's sign, and a player who names their money the crown has
     * renamed the money, not how a price tag is printed.
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

    /** The longest sign a player may type for their money, in characters: "kr", "zł", "CHF", "T¥" fit, and a figure beside it still reads on a narrow column. */
    public static final int MAX_SIGN_LENGTH = 3;

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

    /** Whether the sign is written after the figure ("2.6M kr") rather than before it ("$2.6M"). */
    private final boolean signAfter;

    /**
     * WHICH MONEY THE STATIC FORMATTERS WRITE. ui/Money is static and Formats a
     * singleton, so neither can hold a city; the window shows one city at a
     * time, and the game sets this (use()) when a city is founded or loaded and
     * the window binds it. It holds only how money is WRITTEN - the sign and its
     * place - and never the name, so the leak this class warns of above costs a
     * harness that sets a sign at most its sign: everything that names the money
     * is still read through the game.
     */
    private static volatile Currency writing = DANZIK;

    /** The money the static formatters are writing now: "$" before until a city says otherwise. */
    public static Currency writing() { return writing; }

    /** The city whose money the static formatters write from here (null: the Danzik dollar). */
    public static void use(Currency c) { writing = c == null ? DANZIK : c; }

    /** Runs a drawing with the static formatters writing c's money, and puts back the money they were writing, also when the drawing throws. */
    public static <T> T using(Currency c, java.util.function.Supplier<T> draw) {
        Currency was = writing;
        use(c);
        try {
            return draw.get();
        } finally {
            writing = was;
        }
    }

    /** A currency exactly as given, its sign before the figure - the load path's door, which reads back what was saved without judging it. */
    public Currency(String name, String plural, String code, String symbol, String qualifiedSymbol) {
        this(name, plural, code, symbol, qualifiedSymbol, false);
    }

    /** A currency exactly as given, its sign before the figure or after it. */
    public Currency(String name, String plural, String code, String symbol, String qualifiedSymbol, boolean signAfter) {
        this.name = name;
        this.plural = plural;
        this.code = code;
        this.symbol = symbol;
        this.qualifiedSymbol = qualifiedSymbol;
        this.signAfter = signAfter;
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
    /** Whether the sign follows the figure ("2.6M kr") rather than leads it ("$2.6M"). */
    public boolean signAfter()      { return signAfter; }

    /**
     * An already formatted number with this money's sign put around it: before,
     * sign + separator + number; after, number + separator + sign. The separator
     * is one plain space where the character of the sign that touches the number
     * is a letter, else nothing: "$2.6M", "CHF 2.6M", "R$2.6M", "2.6M kr",
     * "2.6M€", "2.6M T¥".
     *
     * A MINUS IS NEVER MOVED. write("-5") is "$-5", which is what
     * String.format("$%,.0f", -5.0) printed, and the trace is compared byte for
     * byte; a caller that wants "-$5" keeps the minus outside: "-" + write("5").
     */
    public String write(String number) { return place(symbol, number); }

    /** The same with the qualified sign, where a foreign figure is in sight. */
    public String writeQualified(String number) { return place(qualifiedSymbol, number); }

    /** Local money, on a screen with no foreign figure on it. */
    public String local(String amount) { return write(amount); }

    /** Local money, on a screen that also shows dollars. */
    public String qualified(String amount) { return writeQualified(amount); }

    private String place(String sign, String number) {
        return signAfter ? number + gap(sign) + sign : sign + gap(sign) + number;
    }

    /** The space between a sign and the figure beside it: one where the sign's letter touches the number. */
    private String gap(String sign) {
        if (sign.isEmpty()) return "";
        int touching = signAfter ? sign.codePointAt(0) : sign.codePointBefore(sign.length());
        return Character.isLetter(touching) ? " " : "";
    }

    /**
     * A figure this money wrote with its sign, rewritten with its qualified sign
     * in its place: "$5" is "D$5", "-$5" is "-D$5", "5¥" is "5 T¥", "5 kr" stays
     * "5 kr". Every figure of the text is rewritten, as the replace of the "$"
     * this stands for was; the text unchanged where the sign is not found.
     */
    public String qualify(String written) {
        if (symbol.isEmpty() || qualifiedSymbol.isEmpty()) return written;
        String was = signAfter ? gap(symbol) + symbol : symbol + gap(symbol);
        String now = signAfter ? gap(qualifiedSymbol) + qualifiedSymbol : qualifiedSymbol + gap(qualifiedSymbol);
        return was.equals(now) ? written : written.replace(was, now);
    }

    /**
     * A figure written in writing()'s money, rewritten in a foreign money's mark,
     * which always goes BEFORE the figure with no space added (the marks carry
     * their own: ForeignAccounts.MONEY_MARK has "CHF " and "XAU "). The foreign
     * money is written the same whatever the city's sign is.
     *
     * A sign before: the mark takes the sign's place, so a minus outside it stays
     * outside - "-$5" is "-US$5", "$-5" is "US$-5" - which is what ui/Money's
     * marked() always did. A sign after: the sign and its space are taken off
     * the figure and the mark goes before the figure's first digit, after any
     * "+", "-" or "−" - "-5 kr" is "-US$5", "2.6M kr a month" is "US$2.6M a
     * month". No sign found: the mark and the text.
     *
     * THE CITY'S OWN MARK STAYS IN ITS PLACE (FM1, slice C's finding): the
     * screens mark local figures with the qualified sign the same way
     * ("marked(sym(), shown)"), which with a sign after put it in front,
     * "kr2.4M". So with a sign after, the city's qualified or bare sign as
     * the mark is qualify()'s or the figure as written; with a sign before
     * nothing changes.
     */
    public static String marked(String mark, String written) {
        return writing.markedIn(mark, written);
    }

    private String markedIn(String mark, String written) {
        String bare = symbol.isEmpty() ? "" : signAfter ? gap(symbol) + symbol : symbol + gap(symbol);
        int at = bare.isEmpty() ? -1 : written.indexOf(bare);
        if (signAfter && at >= 0 && mark != null) {
            if (mark.trim().equals(qualifiedSymbol)) return qualify(written);
            if (mark.trim().equals(symbol)) return written;
        }
        if (at < 0) return mark + written;
        if (!signAfter) return written.substring(0, at) + mark + written.substring(at + bare.length());
        String figure = written.substring(0, at) + written.substring(at + bare.length());
        int start = at;   // back over the figure's own token: digits, separators, the unit letter, a leading sign
        while (start > 0 && !Character.isWhitespace(figure.charAt(start - 1))) start--;
        while (start < at && "+-−".indexOf(figure.charAt(start)) >= 0) start++;
        return figure.substring(0, start) + mark + figure.substring(start);
    }

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
                deriveCode(c), SYMBOL, qualifiedFor(c, SYMBOL));
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
     * The money as the player named it, with the "$" before the figure, or null
     * when either half does not pass (nameProblem(), codeProblem()). The code is
     * taken upper-cased, so "ard" is ARD; nothing else about it is changed.
     */
    public static Currency typed(String name, String code) {
        return typed(name, code, SYMBOL, false);
    }

    /**
     * The money as the player named it and signed it, or null when any of the
     * three does not pass (nameProblem(), codeProblem(), signProblem()). The sign
     * is taken trimmed; its qualified form is qualifiedFor(); `after` puts it
     * after the figure.
     */
    public static Currency typed(String name, String code, String sign, boolean after) {
        if (nameProblem(name) != null || codeProblem(code) != null || signProblem(sign) != null) return null;
        String n = name.trim();
        String s = sign.trim();
        String plural = n.toLowerCase(java.util.Locale.ROOT).endsWith("s") ? n : n + "s";
        return new Currency(n, plural, code.trim().toUpperCase(java.util.Locale.ROOT),
                s, qualifiedFor(n, s), after);
    }

    /**
     * Why a typed sign will not do, in the player's words, or null when it will:
     * at least one and at most MAX_SIGN_LENGTH characters once trimmed, none of
     * them a digit, a space or one of + - − . , (those are the figure's, and a
     * sign holding one would be read as part of the number), and never
     * FOREIGN_SYMBOL - the world's money is not the city's to sign.
     */
    public static String signProblem(String sign) {
        String s = sign == null ? "" : sign.trim();
        if (s.isEmpty()) return "Type the sign the money is written with, such as $, kr or €.";
        if (s.codePointCount(0, s.length()) > MAX_SIGN_LENGTH) return "A currency's sign can be at most " + MAX_SIGN_LENGTH + " characters.";
        if (s.codePoints().anyMatch(cp -> Character.isDigit(cp) || Character.isWhitespace(cp) || Character.isSpaceChar(cp) || "+-−.,".indexOf(cp) >= 0)) {
            return "A currency's sign cannot hold a digit, a space, or any of + - − . , because the figure is written beside it.";
        }
        if (s.equals(FOREIGN_SYMBOL)) return FOREIGN_SYMBOL + " is the world's money; the city's needs its own sign.";
        return null;
    }

    /**
     * Whether a sign is also one the vault's moneys are written with, so a figure
     * in it is ambiguous the moment a foreign one is in sight: "$", or a sign
     * that is, or ends, one of ForeignAccounts.MONEY_MARK trimmed ("¥" ends
     * "CN¥", "$" ends "US$" and "C$"). "kr", "zł", "Kč", "Fr", "R$" and "₹" are
     * nobody else's and stand alone.
     */
    public static boolean needsLetter(String sign) {
        if (sign == null || sign.isEmpty()) return false;
        if (sign.equals(SYMBOL)) return true;
        for (String mark : ForeignAccounts.MONEY_MARK) if (mark.trim().endsWith(sign)) return true;
        return false;
    }

    /** The sign a figure is written with where a foreign figure is in sight: the money's first letter before it where needsLetter() says so (D$, T¥), else the sign alone. */
    public static String qualifiedFor(String name, String sign) {
        return needsLetter(sign) ? initial(name) + sign : sign;
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
                && symbol.equals(c.symbol) && qualifiedSymbol.equals(c.qualifiedSymbol) && signAfter == c.signAfter;
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(name, plural, code, symbol, qualifiedSymbol, signAfter); }

    @Override
    public String toString() { return describe(); }
}
