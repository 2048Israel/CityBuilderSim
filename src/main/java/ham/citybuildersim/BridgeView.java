package ham.citybuildersim;

/**
 * What the build card says of a long bridge's crossing: the refusal of a survey in its own words, and the pieces of the
 * line a surveyed crossing gets. A pure model of what a screen draws; the money and the points are formatted by the screen.
 *
 * WHY THIS EXISTS. spec-earth2-terrain 6.3 gives the four refusals "word for word as the card shows them" (board 5), and
 * the model, the card, the refusal page and the harness have to say them the same way: every figure in a sentence is the
 * survey's, every name Landmasses.name()'s, and no screen works a length or a depth out again. The words are filled from
 * the survey (the nearest deep plot's depth and distance, the length found, the landmass the bridge was asked toward).
 */
public final class BridgeView {

    private BridgeView() { }

    /** What a landmass is called in a sentence about a bridge: "the city's own landmass" for home, "Island 2" as it is named, and "Halifax's landmass" for a name the data gives (it comes possessive). */
    public static String landmassWords(Landmasses lms, int id) {
        return landmassWords(id == lms.home() || id == 0, lms.name(id));
    }

    /** ...from the landmass's own name: home's is "the city's own landmass", a possessive name ("Halifax's") takes "landmass", any other ("Island 2") stands as it is. */
    public static String landmassWords(boolean home, String name) {
        if (home) return "the city's own landmass";
        return name.endsWith("'s") ? name + " landmass" : name;
    }

    /** A length in kilometres to a tenth: "12.4 km". */
    static String km(double metres) {
        return String.format(java.util.Locale.ROOT, "%.1f km", metres / 1000);
    }

    /** A crossing's length as a card says it: "12.4 km", and under a kilometre in metres, "420 m". */
    public static String lengthWords(double metres) {
        return metres < 1000 ? String.format(java.util.Locale.ROOT, "%.0f m", metres) : km(metres);
    }

    /** The cap as the refusals say it: "36 km". */
    public static String capWords() {
        double km = BridgeSurvey.MAX_LENGTH_M / 1000;
        return km == Math.rint(km) ? String.format(java.util.Locale.ROOT, "%.0f km", km) : String.format(java.util.Locale.ROOT, "%.1f km", km);
    }

    /** The depth the shallow sea is counted to as the refusals say it: "30 m". */
    public static String shallowWords() {
        return String.format(java.util.Locale.ROOT, "%.0f m", EarthData.SHALLOW_M);
    }

    /**
     * The survey's refusal in the card's words, or null when the crossing can be ordered:
     * "No bridgeable path: deep water (41 m) 2.1 km out from Island 2. A bridge needs water 30 m deep or less all the way across.",
     * "The far side is not dry land.",
     * "Longer than a bridge can be: 41.2 km against 36 km." (and "over 45.0 km against 36 km" where the search was cut),
     * "Both ends are on Island 2: a long bridge must link two landmasses (a short crossing is laid by the highways themselves).",
     * and, for water that ends at ground that is no landing, "No landing: ...".
     */
    public static String refusal(BridgeSurvey.Result r, Landmasses lms) {
        return refusal(r, id -> landmassWords(lms, id));
    }

    /** ...with the landmasses named by `words` (a landmass's id to how a sentence calls it): what the harness runs on a ground that has no Landmasses. */
    public static String refusal(BridgeSurvey.Result r, java.util.function.IntFunction<String> words) {
        if (r == null || r.ok()) return null;
        switch (r.why()) {
            case DEEP:
                return "No bridgeable path: deep water (" + Math.round(r.deepM()) + " m) " + lengthWords(r.deepOutM()) + " out from "
                        + words.apply(r.far()) + ". A bridge needs water " + shallowWords() + " deep or less all the way across.";
            case NOT_DRY:
                return "The far side is not dry land.";
            case TOO_LONG:
                return "Longer than a bridge can be: " + (r.over() ? "over " : "") + km(r.lengthM()) + " against " + capWords() + ".";
            case SAME_LANDMASS:
                return "Both ends are on " + words.apply(r.far())
                        + ": a long bridge must link two landmasses (a short crossing is laid by the highways themselves).";
            case NO_LANDING:
                return "No landing: the water reaches no flat or foothill ground the city owns on a landmass it has linked."
                        + " A bridge needs one at its near end.";
            default:
                return null;
        }
    }

    /**
     * The crossing of an order that can go ahead, without the money: "To Island 2: 12.4 km over water up to 24 m deep (mean 11 m)"
     * or, over fresh water, "To Island 2: 3.2 km over fresh water". A crossing under a kilometre is told in metres.
     */
    public static String crossingWords(BridgeSurvey.Result r, Landmasses lms) {
        return crossingWords(r, id -> landmassWords(lms, id));
    }

    /** ...with the landmasses named by `words`. */
    public static String crossingWords(BridgeSurvey.Result r, java.util.function.IntFunction<String> words) {
        if (r == null || !r.found()) return "";
        String length = lengthWords(r.lengthM());
        String water = r.deepestM() < 1 ? "over fresh water"
                : "over water up to " + Math.round(r.deepestM()) + " m deep (mean " + Math.round(r.meanM()) + " m)";
        return "To " + words.apply(r.far()) + ": " + length + " " + water;
    }
}
