package ham.citybuildersim;

import ham.citybuildersim.ui.Money;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The money's sign is the city's to choose and place, and with the default it
 * writes what it always wrote (FM1). Not part of the game.
 *
 * WHY THIS EXISTS. Jerus, 2026-11-02: his Rotterdam typed "fRotterdam Guilder"
 * for the sign, because the only sign a city had was the "$" with the money's
 * first letter in front of it. The sign is its own field now, before the figure
 * or after it ("2.6M kr"), and Currency.write() is the one writer of it. Two
 * promises, and a harness for each:
 *
 *   - THE DEFAULT IS BYTE FOR BYTE WHAT IT WAS. The playtest's trace is compared
 *     against main's, and every string it holds that has a "$" in it was written
 *     by a "$" the code now routes through the writer. Section 1 holds the
 *     formatters (ui.Money's and Formats') to the strings the code gave before
 *     FM1, got by running that code and pasted in as they came.
 *   - A SIGN AFTER IS WRITTEN EVERYWHERE THE "$" WAS, and nowhere else: section
 *     4 puts a krona after the figure and reads every form back.
 *
 * What it proves, a section each, every one a fixture that causes the condition:
 *
 *   1. "$" before writes what it wrote before: the golden table.
 *   2. write() places the sign: before and after, with a space where a letter
 *      touches the figure, and a minus never moved.
 *   3. The qualified sign: the initial and the sign where the sign is one the
 *      vault's moneys also use ("$", "¥", "€", "C$"), the sign alone where it is
 *      the city's own ("kr", "zł", "R$"), and qualify() puts it in place.
 *   4. Every form with "kr" after: no "$", each ends in " kr", the foreign money
 *      is written the same whatever the city's sign is.
 *   5. signProblem() and typed().
 *   6. The save keeps the sign and its place, and a load sets what the static
 *      formatters write.
 */
public class MoneySignCheck {

    private static int fails, checks;

    static void check(String label, boolean ok) { check(label, ok, null); }

    static void check(String label, boolean ok, String detail) {
        checks++;
        System.out.println((ok ? "  ok   " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : " - " + detail));
        if (!ok) fails++;
    }

    /** Model money in thousands, the golden table's inputs: nothing, a hair under nothing, a few hundred, a loss, then each unit from k to Q and the prices under a dollar. */
    static final double[] V = {0, -0.0004, 0.4, -5, 12.3456, 999.6, 2.6e3, -2.6e6, 1e12, 1.3e15,
        1e-7, 0.0000123, 0.002, -0.002, 0.00099, 9.5, -999.97, 12.34, 1.0, 5e4};

    /** Dollars, for tightMoney(): either side of each place it changes unit. */
    static final double[] D = {0, -0.4, 0.4, 9999, 10000, 99999.5, 999499, 999500, -999600, 1e9 - 1, 999.95e6,
        1e12, 999.95e9, 1.3e15, -2.6e15, 999.96e12};

    /** The old ui.Money.marked(), as it was written before FM1: the reference the "$" before must still equal. */
    static String oldMarked(String prefix, String amount) {
        int at = amount.indexOf('$');
        return at < 0 ? prefix + amount
                : amount.substring(0, at) + prefix + amount.substring(at + 1);
    }

    /** One form of the golden table at one input. */
    static String apply(String form, double x) {
        if (form.startsWith("markedFull ")) return Money.marked(form.substring(11), Money.moneyFull(x));
        if (form.startsWith("marked ")) return Money.marked(form.substring(7), Money.money(x));
        switch (form) {
            case "money":          return Money.money(x);
            case "moneyFull":      return Money.moneyFull(x);
            case "tight":          return Money.tightMoney(x, true);
            case "tightFull":      return Money.tightMoney(x, false);
            case "cash":           return Money.cash(x);
            case "unitPrice":      return Money.unitPrice(x);
            case "signed":         return Money.signed(x, false);
            case "signedNeg":      return Money.signed(x, true);
            case "signedTight":    return Money.signedTight(x, false);
            case "signedTightNeg": return Money.signedTight(x, true);
            case "usd":            return Money.usd(x);
            case "usdFull":        return Money.usdFull(x);
            case "groundPrice":    return Money.groundPrice(x);
            case "fcash":          return Formats.INSTANCE.cash(x);
            case "famount":        return Formats.INSTANCE.amount(x);
            default: throw new IllegalArgumentException(form);
        }
    }

    /** The strings the code gave before FM1, "$" before, at V (at D for tight and tightFull): the form's name, then one string per input. */
    static final String[][] GOLDEN = {
            {"money", "$0", "$0", "$400", "-$5,000", "$12k", "$1.0M", "$2.6M", "-$2.6B", "$1.0Q", "$1,300.0Q", "$0", "$0", "$2", "-$2", "$1", "$9,500", "-$1.0M", "$12k", "$1,000", "$50.0M"},
            {"moneyFull", "$0", "$0", "$400", "-$5,000", "$12,346", "$999,600", "$2.6M", "-$2.6B", "$1.0Q", "$1,300.0Q", "$0", "$0", "$2", "-$2", "$1", "$9,500", "-$999,970", "$12,340", "$1,000", "$50.0M"},
            {"cash", "$0", "$0", "$400", "$-5,000", "$12,346", "$999,600", "$2,600,000", "$-2,600,000,000", "$1,000,000,000,000,000", "$1,300,000,000,000,000,000", "$0", "$0", "$2", "$-2", "$1", "$9,500", "$-999,970", "$12,340", "$1,000", "$50,000,000"},
            {"unitPrice", "$0", "$-0.4000", "$400.00", "-$5,000", "$12,346", "$999,600", "$2.6M", "-$2.6B", "$1.0Q", "$1,300.0Q", "$0.000100", "$0.0123", "$2.00", "$-2.00", "$0.9900", "$9,500", "-$999,970", "$12,340", "$1,000", "$50.0M"},
            {"signed", "$0", "$0", "+$400", "−$5,000", "+$12,346", "+$999,600", "+$2.6M", "−$2.6B", "+$1.0Q", "+$1,300.0Q", "$0", "$0", "+$2", "−$2", "+$1", "+$9,500", "−$999,970", "+$12,340", "+$1,000", "+$50.0M"},
            {"signedNeg", "$0", "$0", "−$400", "+$5,000", "−$12,346", "−$999,600", "−$2.6M", "+$2.6B", "−$1.0Q", "−$1,300.0Q", "$0", "$0", "−$2", "+$2", "−$1", "−$9,500", "+$999,970", "−$12,340", "−$1,000", "−$50.0M"},
            {"signedTight", "$0", "$0", "+$400", "−$5,000", "+$12k", "+$1.0M", "+$2.6M", "−$2.6B", "+$1.0Q", "+$1,300.0Q", "$0", "$0", "+$2", "−$2", "+$1", "+$9,500", "−$1.0M", "+$12k", "+$1,000", "+$50.0M"},
            {"signedTightNeg", "$0", "$0", "−$400", "+$5,000", "−$12k", "−$1.0M", "−$2.6M", "+$2.6B", "−$1.0Q", "−$1,300.0Q", "$0", "$0", "−$2", "+$2", "−$1", "−$9,500", "+$1.0M", "−$12k", "−$1,000", "−$50.0M"},
            {"usd", "US$0", "US$0", "US$400", "-US$5,000", "US$12k", "US$1.0M", "US$2.6M", "-US$2.6B", "US$1.0Q", "US$1,300.0Q", "US$0", "US$0", "US$2", "-US$2", "US$1", "US$9,500", "-US$1.0M", "US$12k", "US$1,000", "US$50.0M"},
            {"usdFull", "US$0", "US$0", "US$400", "-US$5,000", "US$12,346", "US$999,600", "US$2.6M", "-US$2.6B", "US$1.0Q", "US$1,300.0Q", "US$0", "US$0", "US$2", "-US$2", "US$1", "US$9,500", "-US$999,970", "US$12,340", "US$1,000", "US$50.0M"},
            {"groundPrice", "$0", "$-4.31", "$4,306", "-$53,820", "$132,887", "$10.8M", "$28.0M", "-$28.0B", "$10.8Q", "$13,993.1Q", "$0.0011", "$0.1324", "$21.53", "$-21.53", "$10.66", "$102,257", "-$10.8M", "$132,827", "$10,764", "$538.2M"},
            {"fcash", "$0.00", "$-0.40", "$400.00", "$-5,000.00", "$12,345.60", "$999,600.00", "$2,600,000", "$-2,600,000,000", "$1,000,000,000,000,000", "$1,300.0Q", "$0.00", "$0.01", "$2.00", "$-2.00", "$0.99", "$9,500.00", "$-999,970.00", "$12,340.00", "$1,000.00", "$50,000,000"},
            {"famount", "$0.00", "-$0.40", "$400.00", "-$5,000", "$12,346", "$999,600", "$2.6M", "-$2.6B", "$1.0Q", "$1,300.0Q", "$0.00", "$0.01", "$2.00", "-$2.00", "$0.99", "$9,500", "-$999,970", "$12,340", "$1,000", "$50.0M"},
            {"tight", "$0", "$0", "$0", "$9,999", "$10k", "$100k", "$999k", "$1.0M", "-$1.0M", "$1.0B", "$1.0B", "$1.0T", "$1.0T", "$1.3Q", "-$2.6Q", "$1.0Q"},
            {"tightFull", "$0", "$0", "$0", "$9,999", "$10,000", "$100,000", "$999,499", "$999,500", "-$999,600", "$1.0B", "$1.0B", "$1.0T", "$1.0T", "$1.3Q", "-$2.6Q", "$1.0Q"},
            {"marked US$", "US$0", "US$0", "US$400", "-US$5,000", "US$12k", "US$1.0M", "US$2.6M", "-US$2.6B", "US$1.0Q", "US$1,300.0Q", "US$0", "US$0", "US$2", "-US$2", "US$1", "US$9,500", "-US$1.0M", "US$12k", "US$1,000", "US$50.0M"},
            {"marked €", "€0", "€0", "€400", "-€5,000", "€12k", "€1.0M", "€2.6M", "-€2.6B", "€1.0Q", "€1,300.0Q", "€0", "€0", "€2", "-€2", "€1", "€9,500", "-€1.0M", "€12k", "€1,000", "€50.0M"},
            {"marked ¥", "¥0", "¥0", "¥400", "-¥5,000", "¥12k", "¥1.0M", "¥2.6M", "-¥2.6B", "¥1.0Q", "¥1,300.0Q", "¥0", "¥0", "¥2", "-¥2", "¥1", "¥9,500", "-¥1.0M", "¥12k", "¥1,000", "¥50.0M"},
            {"marked £", "£0", "£0", "£400", "-£5,000", "£12k", "£1.0M", "£2.6M", "-£2.6B", "£1.0Q", "£1,300.0Q", "£0", "£0", "£2", "-£2", "£1", "£9,500", "-£1.0M", "£12k", "£1,000", "£50.0M"},
            {"marked CN¥", "CN¥0", "CN¥0", "CN¥400", "-CN¥5,000", "CN¥12k", "CN¥1.0M", "CN¥2.6M", "-CN¥2.6B", "CN¥1.0Q", "CN¥1,300.0Q", "CN¥0", "CN¥0", "CN¥2", "-CN¥2", "CN¥1", "CN¥9,500", "-CN¥1.0M", "CN¥12k", "CN¥1,000", "CN¥50.0M"},
            {"marked C$", "C$0", "C$0", "C$400", "-C$5,000", "C$12k", "C$1.0M", "C$2.6M", "-C$2.6B", "C$1.0Q", "C$1,300.0Q", "C$0", "C$0", "C$2", "-C$2", "C$1", "C$9,500", "-C$1.0M", "C$12k", "C$1,000", "C$50.0M"},
            {"marked A$", "A$0", "A$0", "A$400", "-A$5,000", "A$12k", "A$1.0M", "A$2.6M", "-A$2.6B", "A$1.0Q", "A$1,300.0Q", "A$0", "A$0", "A$2", "-A$2", "A$1", "A$9,500", "-A$1.0M", "A$12k", "A$1,000", "A$50.0M"},
            {"marked CHF ", "CHF 0", "CHF 0", "CHF 400", "-CHF 5,000", "CHF 12k", "CHF 1.0M", "CHF 2.6M", "-CHF 2.6B", "CHF 1.0Q", "CHF 1,300.0Q", "CHF 0", "CHF 0", "CHF 2", "-CHF 2", "CHF 1", "CHF 9,500", "-CHF 1.0M", "CHF 12k", "CHF 1,000", "CHF 50.0M"},
            {"marked XAU ", "XAU 0", "XAU 0", "XAU 400", "-XAU 5,000", "XAU 12k", "XAU 1.0M", "XAU 2.6M", "-XAU 2.6B", "XAU 1.0Q", "XAU 1,300.0Q", "XAU 0", "XAU 0", "XAU 2", "-XAU 2", "XAU 1", "XAU 9,500", "-XAU 1.0M", "XAU 12k", "XAU 1,000", "XAU 50.0M"},
            {"markedFull US$", "US$0", "US$0", "US$400", "-US$5,000", "US$12,346", "US$999,600", "US$2.6M", "-US$2.6B", "US$1.0Q", "US$1,300.0Q", "US$0", "US$0", "US$2", "-US$2", "US$1", "US$9,500", "-US$999,970", "US$12,340", "US$1,000", "US$50.0M"},
    };

    static final PrintStream REAL_OUT = System.out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    /** Runs a piece of the city with the game's own narration off. */
    static <T> T quietly(java.util.function.Supplier<T> work) {
        System.setOut(QUIET);
        try { return work.get(); } finally { System.setOut(REAL_OUT); }
    }

    /** A krona after the figure, the fixture of sections 4 and 6. */
    static Currency krona() { return Currency.typed("Krona", "KRO", "kr", true); }

    /** The forms of one input that write the city's money, for the signs to be read off. */
    static List<String> forms(double x) {
        List<String> out = new ArrayList<>();
        out.add(Money.money(x));
        out.add(Money.moneyFull(x));
        out.add(Money.cash(x));
        out.add(Money.unitPrice(x));
        out.add(Money.signed(x, false));
        out.add(Money.signed(x, true));
        out.add(Money.signedTight(x, false));
        out.add(Formats.INSTANCE.cash(x));
        out.add(Formats.INSTANCE.amount(x));
        return out;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== MoneySignCheck: the money's sign, before or after the figure, one writer, and the default unchanged (FM1) ===");
        Currency.use(Currency.DANZIK);
        try {
            sections();
        } finally {
            Currency.use(Currency.DANZIK);
        }
        System.out.println("\n" + checks + " assertions, " + fails + " failed");
        System.exit(fails);
    }

    static void sections() {
        /* ---------------------------------------------------------------- 1 */
        System.out.println("\n--- 1. the \"$\" before writes what it wrote before ---");
        check("fixture: the static formatters write the Danzik dollar, \"$\" before", Currency.writing().equals(Currency.DANZIK)
                && !Currency.writing().signAfter() && Currency.writing().symbol().equals("$"));
        for (String[] row : GOLDEN) {
            String form = row[0];
            double[] in = form.equals("tight") || form.equals("tightFull") ? D : V;
            StringBuilder wrong = new StringBuilder();
            for (int i = 0; i < in.length; i++) {
                String got = apply(form, in[i]);
                if (!got.equals(row[i + 1])) wrong.append(" at ").append(in[i]).append(": ").append(got).append(" (was ").append(row[i + 1]).append(")");
            }
            check(form + " writes the " + in.length + " strings it wrote before", row.length == in.length + 1 && wrong.length() == 0, wrong.toString());
        }
        String[] odd = {"$5", "-$5", "$-5", "$2.6M a month", "+$5", "−$5", "—", "", "US$5", "$5 and $6", "about $5", "$", "5"};
        StringBuilder drift = new StringBuilder();
        for (String mark : ForeignAccounts.MONEY_MARK) {
            for (String text : odd) {
                if (!Money.marked(mark, text).equals(oldMarked(mark, text))) drift.append(" [").append(mark).append("|").append(text).append("]");
            }
        }
        check("marked() with the \"$\" before is the old code's text for every mark and every shape of text", drift.length() == 0, drift.toString());
        check("...the foreign mark placed in the sign's place, a minus staying outside it: \"-$5\" is \"-US$5\", \"$-5\" is \"US$-5\"",
                Money.marked("US$", "-$5").equals("-US$5") && Money.marked("US$", "$-5").equals("US$-5"));

        /* ---------------------------------------------------------------- 2 */
        System.out.println("\n--- 2. write() places the sign ---");
        Currency chf = new Currency("Swiss franc", "Swiss francs", "CHF", "CHF", "CHF", false);
        Currency real = new Currency("Real", "Reais", "BRL", "R$", "R$", false);
        Currency eur = new Currency("Euro", "Euros", "EUR", "€", "A€", true);
        Currency yen = Currency.typed("Tokyo", "TKY", "¥", true);
        Currency tyen = Currency.typed("Tokyo", "TKY", "T¥", true);
        check("a \"$\" before is \"$2.6M\"", Currency.DANZIK.write("2.6M").equals("$2.6M"));
        check("a letter before gets a space: \"CHF 2.6M\"", chf.write("2.6M").equals("CHF 2.6M"));
        check("a sign ending in a symbol does not: \"R$2.6M\"", real.write("2.6M").equals("R$2.6M"));
        check("a letter after gets a space: \"2.6M kr\"", krona().write("2.6M").equals("2.6M kr"));
        check("a symbol after does not: \"2.6M€\"", eur.write("2.6M").equals("2.6M€"));
        check("a sign that starts with a letter, after, does: \"2.6M T¥\"; the yen alone does not, its qualified sign does",
                tyen != null && tyen.write("2.6M").equals("2.6M T¥") && yen.write("2.6M").equals("2.6M¥") && yen.writeQualified("2.6M").equals("2.6M T¥"));
        check("write() never moves a minus: \"$\" before writes \"-5\" as \"$-5\", as String.format(\"$%,.0f\", -5.0) printed",
                Currency.DANZIK.write("-5").equals("$-5") && Currency.DANZIK.write("-5").equals(String.format("$%,.0f", -5.0)));
        check("...a sign after leaves it in front of the figure: \"-5 kr\"; a caller's \"-\" + write(\"5\") is \"-$5\"",
                krona().write("-5").equals("-5 kr") && ("-" + Currency.DANZIK.write("5")).equals("-$5"));
        check("writeQualified() is the same with the qualified sign: \"D$2.6M\", \"2.6M A€\"",
                Currency.DANZIK.writeQualified("2.6M").equals("D$2.6M") && eur.writeQualified("2.6M").equals("2.6M A€"));
        check("local() and qualified() are write() and writeQualified()",
                Currency.DANZIK.local("5").equals(Currency.DANZIK.write("5")) && eur.qualified("5").equals(eur.writeQualified("5")));

        /* ---------------------------------------------------------------- 3 */
        System.out.println("\n--- 3. the qualified sign ---");
        String[] needs = {"$", "¥", "€", "£", "C$", "A$", "S$"};
        String[] alone = {"kr", "zł", "R$", "Kč", "Fr", "₹"};
        StringBuilder bad = new StringBuilder();
        for (String s : needs) if (!Currency.needsLetter(s) || !Currency.qualifiedFor("Tokyo", s).equals("T" + s)) bad.append(" ").append(s);
        check("a sign the vault's moneys use takes the money's initial: $ ¥ € £ C$ A$ S$", bad.length() == 0, bad.toString());
        bad.setLength(0);
        for (String s : alone) if (Currency.needsLetter(s) || !Currency.qualifiedFor("Tokyo", s).equals(s)) bad.append(" ").append(s);
        check("a sign of the city's own stands alone: kr zł R$ Kč Fr ₹", bad.length() == 0, bad.toString());
        check("the Danzik dollar's is \"D$\", a derived money's the city's initial and \"$\": \"A$\" for Arden",
                Currency.DANZIK.qualifiedSymbol().equals("D$") && Currency.fromCityName("Arden").qualifiedSymbol().equals("A$"));
        check("a typed money with the old two arguments keeps the initial and \"$\", the sign before",
                Currency.typed("Krona", "KRO").qualifiedSymbol().equals("K$") && !Currency.typed("Krona", "KRO").signAfter());
        check("qualify() rewrites a figure its money wrote: \"$5\" is \"D$5\", \"-$5\" is \"-D$5\"",
                Currency.DANZIK.qualify("$5").equals("D$5") && Currency.DANZIK.qualify("-$5").equals("-D$5"));
        check("...leaves a sign that is its own qualified one alone: \"5 kr\"", krona().qualify("5 kr").equals("5 kr"));
        check("...and writes the space a letter needs: \"5¥\" is \"5 T¥\", the same as writeQualified()",
                yen != null && yen.qualify(yen.write("5")).equals("5 T¥") && yen.qualify(yen.write("5")).equals(yen.writeQualified("5")));
        check("...the text unchanged where its sign is not found", Currency.DANZIK.qualify("5").equals("5") && yen.qualify("5 US$").equals("5 US$"));

        /* ---------------------------------------------------------------- 4 */
        System.out.println("\n--- 4. every form with \"kr\" after ---");
        Currency kr = krona();
        check("fixture: a krona, \"kr\" after, which stands alone when qualified",
                kr != null && kr.signAfter() && kr.symbol().equals("kr") && kr.qualifiedSymbol().equals("kr"));
        try {
            List<String> dollarUsd = new ArrayList<>();
            for (double x : V) dollarUsd.add(Money.usd(x) + "|" + Money.usdFull(x));
            Currency.use(kr);
            check("the formatters write the krona", Currency.writing().equals(kr) && Money.money(5).equals("5,000 kr"));
            StringBuilder dollars = new StringBuilder(), notKr = new StringBuilder(), usdDiffers = new StringBuilder(), qualifies = new StringBuilder();
            for (int i = 0; i < V.length; i++) {
                double x = V[i];
                for (String s : forms(x)) {
                    if (s.indexOf('$') >= 0) dollars.append(" ").append(s);
                    String bare = s.replaceFirst("^[+\\-−]", "");
                    if (!bare.endsWith(" kr")) notKr.append(" ").append(s);
                }
                if (!(Money.usd(x) + "|" + Money.usdFull(x)).equals(dollarUsd.get(i))) usdDiffers.append(" usd(").append(x).append(")");
                for (String mark : ForeignAccounts.MONEY_MARK) {
                    String here = Money.marked(mark, Money.money(x));
                    Currency.use(Currency.DANZIK);
                    String there = Money.marked(mark, Money.money(x));
                    Currency.use(kr);
                    if (!here.equals(there)) usdDiffers.append(" marked(").append(mark).append(", ").append(x).append("): ").append(here).append(" against ").append(there);
                }
                if (!kr.qualify(Money.money(x)).equals(Money.money(x))) qualifies.append(" ").append(x);
            }
            check("no \"$\" in money, moneyFull, cash, unitPrice, signed, signedTight, Formats.cash and amount, at all " + V.length + " values", dollars.length() == 0, dollars.toString());
            check("...each ends in \" kr\" (a \"+\", \"-\" or \"−\" in front of it)", notKr.length() == 0, notKr.toString());
            check("usd() and usdFull() are what a \"$\" city writes, and marked(mark, money(x)) for each of the nine marks", usdDiffers.length() == 0, usdDiffers.toString());
            check("qualify() of money(x) is unchanged for a sign that stands alone", qualifies.length() == 0, qualifies.toString());
            check("a minus stays in front: \"-5,000 kr\", \"−5,000 kr\" and \"+5,000 kr\"",
                    Money.money(-5).equals("-5,000 kr") && Money.signed(-5, false).equals("−5,000 kr") && Money.signed(5, false).equals("+5,000 kr"));
            check("a figure that rounds to nothing is \"0 kr\", with no sign on it", Money.money(0).equals("0 kr") && Money.money(-0.0004).equals("0 kr"));
            check("the foreign mark takes the sign's place: \"-5 kr\" is \"-US$5\", \"2.6M kr a month\" is \"US$2.6M a month\"",
                    Money.marked("US$", "-5 kr").equals("-US$5") && Money.marked("US$", "2.6M kr a month").equals("US$2.6M a month"));
            check("...a mark that carries its own space keeps it: \"CHF 2.6M\"; no sign found, the mark and the text",
                    Money.marked("CHF ", "2.6M kr").equals("CHF 2.6M") && Money.marked("US$", "—").equals("US$—"));
            check("the city's own sign as the mark (the screens' marked(sym(), shown)) keeps its place: \"2.6M kr\", not \"kr2.6M\"",
                    Money.marked("kr", "2.6M kr").equals("2.6M kr") && Money.marked("kr", "-5 kr").equals("-5 kr"));
            Currency yenAfter = Currency.typed("Tokyo yen", "TOK", "¥", true);
            Currency.use(yenAfter);
            check("...and its qualified sign after: a yen after, \"2.6M¥\" marked \"T¥\" is \"2.6M T¥\"; \"US$\" still goes in front",
                    yenAfter != null && Money.marked("T¥", "2.6M¥").equals("2.6M T¥") && Money.marked("US$", "2.6M¥").equals("US$2.6M"));
            Currency.use(kr);
            check("isZero(money(0)) is true and isZero(money(5)) false", Money.isZero(Money.money(0)) && !Money.isZero(Money.money(5)));
            check("write(), ours() and sign() speak for the city: \"12 kr\", \"kr\", \"kr\"",
                    Money.write("12").equals("12 kr") && Money.ours().equals("kr") && Money.sign().equals("kr"));
            Currency.use(Currency.DANZIK);
            check("...and with the \"$\" before: isZero(money(0)) true, isZero(money(5)) false, \"$12\", \"D$\", \"$\"",
                    Money.isZero(Money.money(0)) && !Money.isZero(Money.money(5)) && Money.write("12").equals("$12")
                            && Money.ours().equals("D$") && Money.sign().equals("$"));
            Currency.use(chf);
            check("a letter before, \"CHF\": \"CHF 2.6M\" marked is \"US$2.6M\", \"-CHF 5\" is \"-US$5\"",
                    Money.marked("US$", "CHF 2.6M").equals("US$2.6M") && Money.marked("US$", "-CHF 5").equals("-US$5"));
            Currency.use(eur);
            check("a symbol after: \"2.6M€\" marked is \"US$2.6M\"", Money.marked("US$", "2.6M€").equals("US$2.6M"));
            Currency.use(tyen);
            check("a letter-first sign after: \"2.6M T¥ a month\" marked is \"US$2.6M a month\"", Money.marked("US$", "2.6M T¥ a month").equals("US$2.6M a month"));
        } finally {
            Currency.use(Currency.DANZIK);
        }
        check("the Danzik dollar is back for what comes after", Currency.writing() == Currency.DANZIK);

        /* ---------------------------------------------------------------- 5 */
        System.out.println("\n--- 5. signProblem() and typed() ---");
        String astral = "𝔸𝔹𝔺";   // three letters outside the BMP: three characters, six chars
        String[] fine = {"$", "kr", "zł", "CHF", "T¥", "€", "R$", "₹", " kr ", astral};
        StringBuilder rejected = new StringBuilder();
        for (String s : fine) if (Currency.signProblem(s) != null) rejected.append(" [").append(s).append("]");
        check("a sign of one to three characters passes ($ kr zł CHF T¥ € R$ ₹, trimmed, three letters outside the BMP)", rejected.length() == 0, rejected.toString());
        String[] refused = {null, "", "   ", "ABCD", astral + "𝔻", "1", "k1", "k r", "+", "-", "−", ".", ",", "k,r", "US$", " 1"};
        StringBuilder let = new StringBuilder();
        for (String s : refused) if (Currency.signProblem(s) == null) let.append(" [").append(s).append("]");
        check("empty, longer than " + Currency.MAX_SIGN_LENGTH + ", a digit, a space, + - − . , and the foreign US$ are refused", let.length() == 0, let.toString());
        check("MAX_SIGN_LENGTH is three", Currency.MAX_SIGN_LENGTH == 3);
        check("a refusal says why in a sentence", Currency.signProblem("").endsWith(".") && Currency.signProblem("ABCD").contains(String.valueOf(Currency.MAX_SIGN_LENGTH))
                && Currency.signProblem("US$").contains("US$"));
        Currency typedKr = Currency.typed("Krona", "kro", " kr ", true);
        check("typed(name, code, sign, after) takes the sign trimmed and its place, the code upper-cased",
                typedKr != null && typedKr.symbol().equals("kr") && typedKr.qualifiedSymbol().equals("kr") && typedKr.signAfter() && typedKr.code().equals("KRO")
                        && typedKr.name().equals("Krona") && typedKr.plural().equals("Kronas"));
        check("...with a sign that needs the letter, the initial in front of it: \"T¥\"", yen != null && yen.symbol().equals("¥") && yen.qualifiedSymbol().equals("T¥") && yen.signAfter());
        check("...null when the sign, the name or the code does not pass",
                Currency.typed("Krona", "KRO", "k1", true) == null && Currency.typed("Krona", "KRO", "", false) == null
                        && Currency.typed("", "KRO", "kr", true) == null && Currency.typed("Krona", "KR", "kr", true) == null
                        && Currency.typed("Krona", "KRO", "US$", false) == null);
        check("the old typed(name, code) is the \"$\" before", Currency.typed("Krona", "KRO").equals(Currency.typed("Krona", "KRO", "$", false)));
        check("a money's place is part of what it is: kr after is not kr before",
                !Currency.typed("Krona", "KRO", "kr", true).equals(Currency.typed("Krona", "KRO", "kr", false))
                        && Currency.typed("Krona", "KRO", "kr", true).equals(Currency.typed("Krona", "KRO", "kr", true))
                        && Currency.typed("Krona", "KRO", "kr", true).hashCode() == Currency.typed("Krona", "KRO", "kr", true).hashCode());
        check("a currency with no place given is before", !Currency.DANZIK.signAfter() && !new Currency("a", "as", "AAA", "$", "A$").signAfter());

        /* ---------------------------------------------------------------- 6 */
        System.out.println("\n--- 6. the save keeps the sign and its place ---");
        GameFiles files = GameFiles.scratch("moneysigncheck");
        Founding nordby = Founding.custom("Nordby", 62_500, 12_500, .02).withCurrency(krona());
        Game saved = quietly(() -> { Game g = new Game(files, nordby); g.run(); return g; });
        check("fixture: a city founded with the krona; the formatters write it", saved.getCurrency().equals(krona()) && Currency.writing().equals(krona()));
        check("saved", quietly(() -> saved.saveGame(10, "Nordby")).ok);
        Currency.use(Currency.DANZIK);
        Game back = quietly(() -> { Game g = new Game(files); g.loadGameSave(10); return g; });
        Currency c = back.getCurrency();
        check("the load reads the place back: kr, after, qualified \"kr\"", back.getLoadFailure() == null && c.signAfter() && c.symbol().equals("kr") && c.qualifiedSymbol().equals("kr"), String.valueOf(back.getLoadFailure()));
        check("...the whole money, as it was founded", c.equals(saved.getCurrency()));
        check("...and Currency.writing() after the load is that money", Currency.writing().equals(c) && Money.money(5).equals("5,000 kr"));
        Currency.use(Currency.DANZIK);
        Game danzik = quietly(() -> { Game g = new Game(files, Founding.defaults()); g.run(); return g; });
        check("a city on the default founding writes \"$\" before, and founding it sets the formatters to it",
                !danzik.getCurrency().signAfter() && Currency.writing().equals(danzik.getCurrency()) && danzik.getCurrency().symbol().equals("$") && Money.money(5).equals("$5,000"));
        check("saved", quietly(() -> danzik.saveGame(10, "Danzik")).ok);
        Game again = quietly(() -> { Game g = new Game(files); g.loadGameSave(10); return g; });
        check("...and loaded, the default comes back with its \"$\" before the figure",
                !again.getCurrency().signAfter() && again.getCurrency().symbol().equals("$") && Currency.writing().equals(again.getCurrency()));
        DataSave older = new com.google.gson.Gson().fromJson("{\"currencyName\":\"Krona\",\"currencyPlural\":\"Kronas\",\"currencyCode\":\"KRO\","
                + "\"currencySymbol\":\"$\",\"currencyQualified\":\"K$\"}", DataSave.class);
        check("a save from before FM1 has no place on file and reads before", !older.getFounding(.02).getCurrency().signAfter() && older.getFounding(.02).getCurrency().symbol().equals("$"));
    }
}
