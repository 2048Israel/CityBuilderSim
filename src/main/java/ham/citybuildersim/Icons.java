package ham.citybuildersim;

/**
 * The rail's icons, as vector outlines.
 *
 * WHY PATHS AND NOT PICTURES
 *
 * There is still no image file anywhere in this project, and these did not add
 * one. A PNG would have to be packaged into the jar, found at runtime, shipped
 * at two or three sizes for different screens, and re-exported in a second
 * colour for the active tab. An SVG path is a string: it draws crisp at any
 * size, it takes its colour from whoever draws it, and it cannot fail to load.
 *
 * WHAT REPLACED WHAT
 *
 * The rail used geometric glyphs from the Unicode block - and the trouble with
 * picking eleven marks out of one block is that they all look alike. Three of
 * them were diamonds and four were filled squares, so the rail was learned by
 * POSITION rather than read, which is fine for the person who built the game
 * and useless for anybody in their first ten minutes of it.
 *
 * These are eleven different objects: a building, a plot of land, a group of
 * people, a heartbeat, a factory, a parliament, a coin, a globe, a set of
 * scales, a graph, a gear. Nobody has to learn them.
 *
 * SOURCE AND LICENCE
 *
 * Lucide (lucide.dev), ISC licence - free to use in a commercial game, no
 * attribution required in the product. Each icon is drawn on a 24x24 grid with
 * a 2px stroke, round caps and round joins, and is STROKED rather than filled:
 * see UserInterface.icon(), which sets fill to null.
 *
 * MULTI-PART ICONS ARE ONE STRING. Lucide draws several of these with a mix of
 * <path>, <circle> and <rect> elements. JavaFX SVGPath takes a single path, so
 * the parts are concatenated - every "M" starts a fresh subpath and all of them
 * get stroked - and the circles and rectangles are written out as arcs, because
 * SVGPath has no notion of either.
 *
 * @author Jerus
 */
public final class Icons {

    private Icons() { }

    /** A building. The rect is written as arcs; the dots are its windows. */
    public static final String BUILD =
            "M12 10h.01 M12 14h.01 M12 6h.01 M16 10h.01 M16 14h.01 M16 6h.01"
          + "M8 10h.01 M8 14h.01 M8 6h.01"
          + "M9 22v-3a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v3"
          + "M6 2 H18 A2 2 0 0 1 20 4 V20 A2 2 0 0 1 18 22 H6 A2 2 0 0 1 4 20 V4 A2 2 0 0 1 6 2 Z";

    /**
     * A tent and trees - ground, rather than a map of it.
     *
     * NOTE THE ABSOLUTE MOVES. Lucide writes each of these subpaths as its own
     * &lt;path&gt; element starting with a relative "m", which is relative to the
     * origin when the element stands alone - and relative to wherever the LAST
     * subpath ended once they are concatenated into one string. Left as written,
     * the two trees and the guy rope were drawn tens of units off the grid,
     * which stretched the icon's bounds and shoved the whole rail out of
     * alignment. Every subpath here starts with an absolute M.
     */
    public static final String LAND =
            "M2 4 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0"
          + "M14 5 l3-3 3 3 M14 10 l3-3 3 3 M17 14V2 M17 14H7l-5 8h20Z M8 14v8 M9 14 l5 8";

    /** Three people, one in front. */
    public static final String POPULATION =
            "M17 21a5 5 0 0 0-10 0"
          + "M22 10.5a3.5 3.5 0 0 0-5.507-2.868"
          + "M7.507 7.632A3.5 3.5 0 0 0 2 10.5"
          + "M9 13 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0"
          + "M16 4.5 a2.5 2.5 0 1 0 5 0 a2.5 2.5 0 1 0 -5 0"
          + "M3 4.5 a2.5 2.5 0 1 0 5 0 a2.5 2.5 0 1 0 -5 0";

    /** A heart with a pulse through it. */
    public static final String SERVICES =
            "M2 9.5a5.5 5.5 0 0 1 9.591-3.676.56.56 0 0 0 .818 0A5.49 5.49 0 0 1 22 9.5"
          + "c0 2.29-1.5 4-3 5.5l-5.492 5.313a2 2 0 0 1-3 .019L5 15c-1.5-1.5-3-3.2-3-5.5"
          + "M3.22 13H9.5l.5-1 2 4.5 2-7 1.5 3.5h5.27";

    /** A factory. */
    public static final String SECTOR =
            "M12 16h.01 M16 16h.01 M8 16h.01"
          + "M3 19a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V8.5a.5.5 0 0 0-.769-.422"
          + "l-4.462 2.844A.5.5 0 0 1 15 10.5v-2a.5.5 0 0 0-.769-.422"
          + "L9.77 10.922A.5.5 0 0 1 9 10.5V5a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2z";

    /** A parliament, columns and all. */
    public static final String GOVERNMENT =
            "M10 18v-7 M14 18v-7 M18 18v-7 M6 18v-7 M3 22h18"
          + "M11.119 2.205a2 2 0 0 1 1.762 0l7.84 3.846A.5.5 0 0 1 20.5 7h-17a.5.5 0 0 1-.22-.949z";

    /** A coin with a dollar in it. */
    public static final String FINANCES =
            "M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0"
          + "M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"
          + "M12 18V6";

    /**
     * A bank: a pediment on columns, with a doorway.
     *
     * Deliberately not the parliament above it - both are classical buildings
     * and the rail already learned that lesson once. The government's is a wide
     * roof over four even columns; this one is narrower, has a stepped base and
     * a door, and carries the landmark shape a player associates with a bank
     * rather than a legislature.
     *
     * Lucide "landmark" with the base drawn as a plinth.
     */
    public static final String BANK =
            "M3 22h18 M4 18v-7 M9 18v-7 M15 18v-7 M20 18v-7"
          + "M2 18h20"
          + "M11.5 2.4a1 1 0 0 1 1 0l8 4.6A.5.5 0 0 1 20.3 8H3.7a.5.5 0 0 1-.2-1z";

    /** A globe. */
    public static final String TRADE =
            "M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0"
          + "M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 0 0 0-20"
          + "M2 12h20";

    /** Scales. The two pans are absolute moves; see LAND. */
    public static final String POLICY =
            "M12 3v18 M7 21h10"
          + "M3 7h1a17 17 0 0 0 8-2 17 17 0 0 0 8 2h1"
          + "M19 8 l3 8 a5 5 0 0 1-6 0z"
          + "M5 8 l3 8 a5 5 0 0 1-6 0z";

    /** A line on axes. The line is an absolute move; see LAND. */
    public static final String REPORTS =
            "M3 3v16a2 2 0 0 0 2 2h16"
          + "M19 9 l-5 5 -4-4 -3 3";

    /** A gear. */
    public static final String SETTINGS =
            "M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915"
          + "a2.34 2.34 0 0 1 2.33 4.033 2.34 2.34 0 0 0 0 3.831"
          + "a2.34 2.34 0 0 1-2.33 4.033 2.34 2.34 0 0 0-3.319 1.915"
          + "a2.34 2.34 0 0 1-4.659 0 2.34 2.34 0 0 0-3.32-1.915"
          + "a2.34 2.34 0 0 1-2.33-4.033 2.34 2.34 0 0 0 0-3.831"
          + "A2.34 2.34 0 0 1 6.35 6.051a2.34 2.34 0 0 0 3.319-1.915"
          + "M9 12 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0";
}
