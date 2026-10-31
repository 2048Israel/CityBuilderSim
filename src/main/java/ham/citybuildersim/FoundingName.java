package ham.citybuildersim;

/**
 * The city's name as the Found-a-city page holds it: blank to begin with, filled by a place searched, and the player's own once they have typed one (FN1, 2026-10-30).
 *
 * WHY. The page opened on "Danzik", the model's default city, so a player who
 * wanted their own name had to select it away first, and a place searched
 * on the Earth never named the city. Jerus: "remove the default name of
 * danzik, have it just blank, or if you search for a city, its that city
 * name (unless someone already typed something, then it doesnt change the
 * city name)". The text field cannot say who put its text there, so this
 * class does: the screen tells it each change, and it keeps the text, and
 * whether the player wrote it. The default itself ({@link Founding#DEFAULT_CITY_NAME})
 * stays in the model, which old saves and the harnesses found cities by.
 *
 * THE RULE. The text is "" at first. {@link #placePicked(String)} puts a
 * place's name in only while the player has not written one, and a name it
 * put there is replaced by the next place picked. {@link #edited(String)} is
 * a change the player made: a non-blank text is theirs and stays, a cleared
 * (or blank) field is nobody's again, so the next place fills it. A change to
 * the very text this class last set is the field echoing it, not an edit.
 */
public final class FoundingName {

    private String text = "";
    private boolean typed;

    /** The name as it stands, "" while there is none. */
    public String text() { return text; }

    /** True when the player wrote the name, so a place picked leaves it alone. */
    public boolean isTyped() { return typed; }

    /** The page opening afresh: blank, and nobody's. */
    public void reset() {
        text = "";
        typed = false;
    }

    /** The field changed to {@code now}: the player's edit, unless it is the text this class set itself. */
    public void edited(String now) {
        String n = now == null ? "" : now;
        if (n.equals(text)) return;
        text = n;
        typed = !n.isBlank();
    }

    /**
     * A place picked from the search. True when the name took it (the field is to show {@link #text()}), false when
     * the player's own name stays or the place has no name that will do.
     */
    public boolean placePicked(String placeName) {
        if (typed) return false;
        String n = fromPlace(placeName);
        if (n.isEmpty()) return false;
        text = n;
        return true;
    }

    /**
     * A place's name as a city's: trimmed and cut to {@link Founding#MAX_CITY_NAME_LENGTH} (Earth's data has names
     * longer than a city may be called), or "" when the result would not pass {@link Founding#cityNameProblem(String)}.
     */
    public static String fromPlace(String placeName) {
        String n = placeName == null ? "" : placeName.trim();
        if (n.length() > Founding.MAX_CITY_NAME_LENGTH) {
            int end = Founding.MAX_CITY_NAME_LENGTH;
            if (Character.isHighSurrogate(n.charAt(end - 1))) end--;
            n = n.substring(0, end).trim();
        }
        return Founding.cityNameProblem(n) == null ? n : "";
    }
}
