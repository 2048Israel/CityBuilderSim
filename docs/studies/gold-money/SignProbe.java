import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * What the Java runtime the game already ships knows about a nation's money: its sign at home, its sign in English, and whether
 * the nation's own language writes the sign before or after the number (the gold-money study, 2026-11-02).
 *
 * WHY. Jerus, 2026-11-02: a city founded in Tokyo should default to "the yen sign", and a money of the player's own may be "$ or
 * something else". Java 21's locale data is the Unicode CLDR's (JEP 252), under the Unicode License, which allows commercial use,
 * and it is inside the runtime already: the game would ship no table of its own for today's moneys. The place of the sign is the
 * language's, not the money's: the euro is "2.600.000,00 €" in Berlin and "€2,600,000.00" in Dublin. So the founding default is
 * read for the nation's own language, one locale a nation.
 *
 * Run with any Java 21 (no game classes): java -Dstdout.encoding=UTF-8 docs/studies/gold-money/SignProbe.java
 * It prints thirty-six nations' main locales, then the count over every country locale the runtime has.
 */
public final class SignProbe {

    /** A nation's main locale, one each (est.: the language most of its people write; the batch takes CLDR's territory data). */
    static final List<String> MAIN = List.of("ja-JP", "zh-CN", "ko-KR", "en-US", "en-GB", "en-CA", "fr-CA", "en-AU", "en-IN", "hi-IN",
            "de-DE", "fr-FR", "it-IT", "es-ES", "nl-NL", "en-IE", "sv-SE", "nb-NO", "da-DK", "fi-FI", "pl-PL", "cs-CZ", "hu-HU", "ru-RU",
            "uk-UA", "tr-TR", "he-IL", "ar-EG", "th-TH", "vi-VN", "id-ID", "pt-BR", "es-MX", "es-AR", "de-CH", "en-ZA");

    public static void main(String[] args) {
        System.out.println("locale  code  home   english  place   2,600,000 at home");
        for (String tag : MAIN) {
            Locale l = Locale.forLanguageTag(tag);
            String[] v = read(l);
            System.out.printf("%-7s %-5s %-6s %-8s %-7s %s%n", tag, v[0], v[1], v[2], v[3], v[4]);
        }
        int before = 0, after = 0, all = 0;
        for (Locale l : Locale.getAvailableLocales()) {
            if (l.getCountry().isEmpty() || !l.getVariant().isEmpty() || !l.getScript().isEmpty()) continue;
            String[] v = read(l);
            if (v == null) continue;
            all++;
            if (v[3].equals("before")) before++; else if (v[3].equals("after")) after++;
        }
        System.out.printf("%nevery country locale with a money (%d): the sign before the number in %d, after it in %d%n", all, before, after);
    }

    /** code, home sign, English sign, before or after, and 2,600,000 written at home; null where the locale has no money. */
    static String[] read(Locale l) {
        Currency c;
        try { c = Currency.getInstance(l); } catch (IllegalArgumentException e) { return null; }
        if (c == null) return null;
        NumberFormat nf = NumberFormat.getCurrencyInstance(l);
        String positive = nf instanceof DecimalFormat d ? d.toPattern().split(";")[0] : "";
        int sign = positive.indexOf('¤'), digit = -1;
        for (int i = 0; i < positive.length() && digit < 0; i++) if (positive.charAt(i) == '#' || positive.charAt(i) == '0') digit = i;
        String place = sign < 0 || digit < 0 ? "?" : sign < digit ? "before" : "after";
        return new String[] {c.getCurrencyCode(), c.getSymbol(l), c.getSymbol(Locale.ENGLISH), place, nf.format(2_600_000)};
    }
}
