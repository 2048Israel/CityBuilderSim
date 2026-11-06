package ham.citybuildersim;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * A piece of equipment a building template carries: what it is called, when it is known, which 1900 form it ends (if any), what fitting it buys and
 * how long it lasts. Immutable; read from buildings.json's "equipment" (BuildingCatalog.readEquipment()) and held by the template
 * (BuildingsTemplate.getEquipment()).
 *
 * WHY (R1, 2026-11-05; docs/studies/research-design.md 2.1-2.2, research-1900.md 3). It replaced nothing: before R1 a building had one way to run,
 * and R0c's 1900 forms (Sector.setSteamShare(), Agriculture.setHorseShare(), Haulage.setHorseShare()) were shares of a sector that nobody set.
 * A piece says which form it ends, so that the count of a stack's buildings still without it (BuildingsStacks.getUnfitted()) is what sets the
 * form's share (EquipmentForms) and "the forms read one number" (R0c's call 3); and it says what fitting it costs, in goods and in
 * BUILDING_WORK, and how long it lasts, which is what R1b's payback rule weighs against what it saves. Data and not code, so that the next piece
 * is a line in the file and not a class.
 *
 * A PIECE MAY END NO FORM (R1c, 2026-11-05; research-design 2.1): "ends" is then null, and the piece exists to enable a recipe (Recipe's
 * needsEquipment): the by-product ovens that replace the beehive ovens, which no 1900 form is the share of. The catalogue keeps such a piece
 * only where a recipe of its template needs it. A piece that ends a form may be the one a recipe needs too (the tractor will be).
 *
 * "since" is a year, as text ("1900"), or a catalogue code ("PE14"). A year is known from its January; a code from its entry's month in
 * research.json (R2: ResearchCatalog.knownFrom(); before R2 a code was never known, R1-notes call 7), so the tractor is known from PE14's month,
 * June 1917, and not before; a code that is not in the catalogue is never known. knownAt() asks by the game month, knownIn() by the year.
 */
public final class Equipment {

    /** The 1900 form a piece ends: the share of a sector (or of the carts) that still runs the old way, EquipmentForms sets it from the count of buildings unfitted. */
    public enum Form {
        /** A factory turning its own steam engine in place of drawing power from the grid: Sector.setSteamShare(). */
        STEAM,
        /** Farms on horses: Agriculture.setHorseShare(). */
        FARM_HORSES,
        /** Freight's road leg on horse and wagon: Haulage.setHorseShare(). */
        CART_HORSES
    }

    private final String id;
    private final String name;
    private final String since;
    private final boolean year;
    private final int knownFrom;
    private final Form ends;
    private final Map<Good, Double> buys;
    private final double work;
    private final int lifeMonths;

    /**
     * @param id         the piece's permanent key on its template ("electric-drive"); never empty
     * @param name       what the screens call it; the id when blank
     * @param since      a year as text ("1900") or a catalogue code ("PE14"); never empty
     * @param ends       the form it ends, or null for a piece that ends none (it exists to enable a recipe)
     * @param buys       the goods fitting one building buys, units at nameplate: finite and not negative (copied)
     * @param work       the BUILDING_WORK points to fit it: finite and not negative
     * @param lifeMonths how long it lasts, in months: more than 0
     * @throws IllegalArgumentException for any of the above that is not so; the catalogue reports it and skips the piece
     */
    public Equipment(String id, String name, String since, Form ends, Map<Good, Double> buys, double work, int lifeMonths) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("a piece of equipment has no id");
        if (since == null || since.isBlank()) throw new IllegalArgumentException("\"" + id + "\" has no since");
        if (lifeMonths <= 0) throw new IllegalArgumentException("\"" + id + "\" has a life of " + lifeMonths + " months");
        if (!Double.isFinite(work) || work < 0) throw new IllegalArgumentException("\"" + id + "\" has work " + work);
        EnumMap<Good, Double> bought = new EnumMap<>(Good.class);
        if (buys != null) {
            for (Map.Entry<Good, Double> e : buys.entrySet()) {
                double v = e.getValue() == null ? Double.NaN : e.getValue();
                if (e.getKey() == null || !Double.isFinite(v) || v < 0) {
                    throw new IllegalArgumentException("\"" + id + "\" buys " + e.getValue() + " of " + e.getKey());
                }
                bought.put(e.getKey(), v);
            }
        }
        this.id = id.trim();
        this.name = name == null || name.isBlank() ? this.id : name.trim();
        this.since = since.trim();
        this.year = yearOf(this.since) != null;
        this.knownFrom = ResearchCatalog.knownFrom(this.since);
        this.ends = ends;
        this.buys = Collections.unmodifiableMap(bought);
        this.work = work;
        this.lifeMonths = lifeMonths;
    }

    /** A "since" read as a year: the year when it parses as a whole number, null for a catalogue code. The one reading of "since" a piece and a Recipe share. */
    static Integer yearOf(String since) {
        try {
            return Integer.parseInt(since.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** The piece's permanent key on its template. */
    public String getId() { return id; }

    /** What the screens call it. */
    public String getName() { return name; }

    /** The year it is known from, as text, or the catalogue code that says (research.json). */
    public String getSince() { return since; }

    /** The 1900 form it ends, or null for a piece that ends none (it exists to enable a recipe): everything that reads it takes null. */
    public Form getEnds() { return ends; }

    /** The goods fitting one building buys, units at nameplate; unmodifiable, and empty for a piece that buys none. */
    public Map<Good, Double> getBuys() { return buys; }

    /** The BUILDING_WORK points it takes to fit one building. */
    public double getWork() { return work; }

    /** How long it lasts, in months. */
    public int getLifeMonths() { return lifeMonths; }

    /** Whether "since" is a year (it parses as a whole number) and not a catalogue code. */
    public boolean isYear() { return year; }

    /** The game month it is known from: a year's January, a code's entry's month (ResearchCatalog.knownFrom()); Integer.MAX_VALUE for a code not in the catalogue. */
    public int knownFrom() { return knownFrom; }

    /** Whether it is known at a game month (month 1 = January 1900): from the month it is known from on. */
    public boolean knownAt(int gameMonth) {
        return gameMonth >= knownFrom;
    }

    /**
     * Whether it is known at a game month in a city whose research may have found it first (R3): from history's month, or from the month the
     * city found its code, whichever is first (Research.foundMonth()). No research reads history's (knownAt(int)); a year "since" is never found,
     * so it reads history's too.
     */
    public boolean knownAt(int gameMonth, Research found) {
        return found == null ? knownAt(gameMonth) : gameMonth >= Math.min(knownFrom, found.foundMonth(since));
    }

    /** Whether it is known by a calendar year's January: knownAt() of that month (januaryOf()), so that a screen that asks by the year agrees with the model that asks by the month. */
    public boolean knownIn(int calendarYear) {
        return knownAt(januaryOf(calendarYear));
    }

    /**
     * The game month of a calendar year's January: PolicyDates.monthOf(year, 1), the game's one formula, but not clamped at month 1, so that the
     * year before the start (1899) is before it and a piece dated 1900 is not known in it, as it never was. Recipe.knownIn() reads it too.
     */
    static int januaryOf(int calendarYear) {
        return 1 + 12 * (calendarYear - CityCalendar.EPOCH_YEAR);
    }

    @Override
    public String toString() {
        return id + " (" + since + (ends == null ? ", ends no form)" : ", ends " + ends + ")");
    }
}
