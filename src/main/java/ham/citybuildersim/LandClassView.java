package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * What the land office and the sector statements say about the CLASSES of ground (ET3, spec-earth2-terrain 5.3), in
 * words, worked out without a node: the office's prices for flat, foothill and steep ground, an offer's ground by
 * class, and note 11's rows (a sector's land by the ground it stands on) written as the sentences the screens show.
 *
 * WHY. Slices A and B gave every class of ground a price and the books a note by class, and left the screens saying
 * nothing of them: a player on hills saw one price for ground that was sold at three, an offer that was mostly
 * mountain looked like any other, and the balance sheet's land was a single line. The words were going to be
 * written twice, in LandScreen and in SectorScreen, and read a third time by the harness and by the renders that
 * go in front of Jerus (tools.LandClassRenders), so they are written once, here, in the model's package, from the
 * model's own getters (LandManager, LandMarket, SectorStatements.landNote()) and none of the toolkit. A screen lays
 * these lines out; it recomputes nothing. In a city with no foothill and no steep ground, and an offer that is all
 * flat, every method here answers nothing - the screens draw exactly what they drew before ET3.
 *
 * THE MONEY WORDS are the screens' (ui.Money): a ground price is a square metre's, to the cent from a dollar up and
 * to the ten-thousandth under one (unitPrice()), a sum compact (money()), the city's mark ahead of the figure and
 * the world's "US$". They are copied here, since the model never imports the interface, and LandClassCheck 7 holds
 * the copy against ui.Money on a spread of figures so the two cannot drift.
 */
public final class LandClassView {

    private LandClassView() { }

    /* ===== THE LAND OFFICE'S PRICES BY CLASS ===== */

    /**
     * The land office's prices by class, a line for each class of dry ground the city owns or the shelf offers, flat
     * first: what a business pays the city a square metre (LandManager's getPricePerSqFt(), getFoothillPricePerSqFt(),
     * getSteepPricePerSqFt()) and what the office asks of the world (LandMarket's getFlatUsdPerSqFt() and the other
     * two); the foothill's line says how it comes to its price, flat's less its preparation or the base price or the
     * flat price itself. Empty for a city whose ground and shelf have no foothill and no steep ground.
     */
    public static List<String> officeLines(Game g) {
        LandManager lm = g.getLandManager();
        LandMarket m = lm.getMarket();
        boolean foothill = lm.getFoothillSqFt() > 0, steep = lm.getSlopeSqFt() > 0;
        for (LandParcel p : g.landShelf()) {
            if (p == null) continue;
            foothill |= p.getReliefKm2(CityLand.RELIEF_FOOTHILL) > 0;
            steep |= p.getReliefKm2(CityLand.RELIEF_STEEP) > 0 || p.getReliefKm2(CityLand.RELIEF_VERY_STEEP) > 0;
        }
        List<String> lines = new ArrayList<>();
        if (!foothill && !steep) return lines;
        String mark = g.getCurrency().qualifiedSymbol();
        double going = lm.getPricePerSqFt(), prep = lm.getPreparationPerSqFt(), base = m.getBasePricePerSqFt();
        lines.add("Flat: " + local(mark, going) + " a m² to a business · the office asks " + usd(m.getFlatUsdPerSqFt()) + " a m²");
        if (foothill) {
            double price = lm.getFoothillPricePerSqFt();
            String how = price >= going ? "the flat price: flat ground is worth no more than the base price"
                    : going - prep > base ? "flat less its preparation, " + local(mark, prep) + " a m²"
                    : "the base price: flat less its preparation, " + local(mark, prep) + " a m², would be under it";
            lines.add("Foothill: " + local(mark, price) + " a m² (" + how + ") · " + usd(m.getFoothillUsdPerSqFt()) + " a m²");
        }
        if (steep) {
            lines.add("Steep (only mines, quarries and wells): " + local(mark, lm.getSteepPricePerSqFt()) + " a m² · "
                    + usd(m.getSteepUsdPerSqFt()) + " a m²");
        }
        return lines;
    }

    /* ===== AN OFFER'S GROUND BY CLASS ===== */

    /**
     * An offer's dry ground by class, in the unit of its dry ground whole, and what a buyer who builds on all of it
     * pays: "Flat 0.62 km² · foothill 0.31 · steep 0.08 · buildable 0.93 km², US$1.2M with its foothill prepared"
     * (the flat and the foothill buildings may stand on, LandMarket.buildableSqFt(); the price is
     * LandMarket.priceWithPrepUsd(), the foothill's preparation added, and only said where there is foothill). The
     * steep and the very steep ground are one "steep", which only works use. Empty for an offer all flat, or none.
     */
    public static String offerLine(LandParcel p, LandMarket m) {
        if (p == null) return "";
        double flat = p.getReliefKm2(CityLand.RELIEF_FLAT), foothill = p.getReliefKm2(CityLand.RELIEF_FOOTHILL),
                steep = p.getReliefKm2(CityLand.RELIEF_STEEP) + p.getReliefKm2(CityLand.RELIEF_VERY_STEEP);
        if (!(foothill > 0 || steep > 0)) return "";
        double whole = LandManager.sqFt(p.getDryKm2());
        String unit = LandManager.areaWords(whole).endsWith(" km²") ? "km²" : "m²";
        String[] names = { "flat", "foothill", "steep" };
        double[] parts = { flat, foothill, steep };
        StringBuilder w = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < parts.length; i++) {
            if (!(parts[i] > 0)) continue;
            if (!first) w.append(" · ");
            // ...the first class opens the line, capitalised and with the unit; the rest are figures in it.
            w.append(first ? Character.toUpperCase(names[i].charAt(0)) + names[i].substring(1) : names[i]).append(' ')
                    .append(LandManager.partFigure(LandManager.sqFt(parts[i]), whole));
            if (first) w.append(' ').append(unit);
            first = false;
        }
        double buildable = LandMarket.buildableSqFt(p);
        w.append(" · ").append(buildable > 0 ? "buildable " + LandManager.partFigure(buildable, whole) + " " + unit : "nothing a building can stand on");
        if (foothill > 0 && m != null) w.append(", ").append(usd0(m.priceWithPrepUsd(p))).append(" with its foothill prepared");
        return w.toString();
    }

    /* ===== NOTE 11, A SECTOR'S LAND BY CLASS ===== */

    /**
     * Note 11's rows as words, one for each class that holds any of the sector's land (SectorStatements.landNote()):
     * "Flat 1.24 km² at D$41.20 a m² · D$51.1M". `mark` is the city's money's ("D$"). Empty where the month did not
     * read its land by class.
     */
    public static List<String> landNoteLines(SectorBooks.SectorMonth now, String mark) {
        List<String> out = new ArrayList<>();
        for (SectorStatements.Row r : SectorStatements.landNote(now)) out.add(landNoteLabel(r, mark) + " · " + marked(mark, money(r.now())));
        return out;
    }

    /** ...for a game's city (its money's mark). */
    public static List<String> landNoteLines(Game g, SectorBooks.SectorMonth now) {
        return landNoteLines(now, g.getCurrency().qualifiedSymbol());
    }

    /** One of note 11's rows without its value, which the statement's own column writes: "Flat 1.24 km² at D$41.20 a m²". */
    public static String landNoteLabel(SectorStatements.Row r, String mark) {
        return r.label() + " " + LandManager.areaWords(LandManager.sqFt(r.part(SectorStatements.LAND_AREA))) + " at "
                + marked(mark, unitPrice(r.part(SectorStatements.LAND_PRICE))) + " a m²";
    }

    /* ===== THE MONEY WORDS (ui.Money's, copied; LandClassCheck 7 holds them to it) ===== */

    /** A ground price a square metre in the city's money from the thousands a square foot the model keeps: "D$41.20" (Money.groundPrice() marked). */
    public static String local(String mark, double thousandsPerSqFt) { return marked(mark, groundPrice(thousandsPerSqFt)); }

    /** ...in the world's: "US$3.10". */
    public static String usd(double thousandsPerSqFt) { return marked(Currency.FOREIGN_SYMBOL, groundPrice(thousandsPerSqFt)); }

    /** A sum in thousands of US dollars: "US$1.2M" (Money.usd()). */
    public static String usd0(double thousands) { return marked(Currency.FOREIGN_SYMBOL, money(thousands)); }

    /** Money.groundPrice(): a square metre of what the model keeps a square foot, as unitPrice() writes it, no mark. */
    static String groundPrice(double thousandsPerSqFt) { return unitPrice(LandManager.perM2(thousandsPerSqFt)); }

    /** Money.marked(): the mark replaces the "$" of the amount. */
    public static String marked(String prefix, String amount) {
        int at = amount.indexOf('$');
        return at < 0 ? prefix + amount : amount.substring(0, at) + prefix + amount.substring(at + 1);
    }

    /** Money.money(): thousands as the screens abbreviate city money. */
    public static String money(double thousands) { return tight(thousands * 1000, true); }

    /** Money.unitPrice(): a price small enough that the cents matter - "$20,882", "$41.20", "$0.7500". */
    static String unitPrice(double thousands) {
        double d = thousands * 1000;
        double a = Math.abs(d);
        if (a >= 1_000) return tight(d, false);
        if (a >= 1) return String.format("$%,.2f", d);
        if (a > 0) return String.format("$%.4f", unsigned0(d, 4));
        return "$0";
    }

    private static final NumberFormat GROUPED = NumberFormat.getNumberInstance(Locale.CANADA);
    static {
        GROUPED.setMaximumFractionDigits(2);
        GROUPED.setMinimumFractionDigits(0);
    }

    /** Money.tightMoney(): `compact` abbreviates from ten thousand dollars (k), else only from a million (M). */
    private static String tight(double value, boolean compact) {
        double a = Math.abs(value);
        String sign = value < 0 ? "-" : "";
        if (a >= 1e15 || tenths(a / 1e12) >= 1000) return String.format("%s$%,.1fQ", sign, a / 1e15);
        if (a >= 1e12 || tenths(a / 1e9) >= 1000) return String.format("%s$%.1fT", sign, a / 1e12);
        if (a >= 1_000_000_000 || tenths(a / 1e6) >= 1000) return String.format("%s$%.1fB", sign, a / 1_000_000_000);
        if (a >= 1_000_000 || (compact && Math.round(a / 1e3) >= 1000)) return String.format("%s$%.1fM", sign, a / 1_000_000);
        if (compact && a >= 10_000) return String.format("%s$%.0fk", sign, a / 1_000);
        long whole = Math.round(a);
        return (whole == 0 ? "" : sign) + "$" + GROUPED.format(whole);
    }

    private static double tenths(double v) { return Math.round(v * 10) / 10.0; }

    private static double unsigned0(double value, int decimals) {
        if (value == 0) return 0.0;
        if (value < 0 && -value * Math.pow(10, decimals) < .5) return 0.0;
        return value;
    }
}
