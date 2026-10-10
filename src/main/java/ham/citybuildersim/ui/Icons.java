package ham.citybuildersim.ui;

/**
 * The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one
 * per category, and the few the frame draws (the money block, the "Needs you"
 * chip, the construction tab).
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
 * scales, a graph, a gear. Nobody has to learn them - and since 0.7.21 each
 * has its name under it on the rail, the gear is three lines (Menu), and the
 * header's envelope and the (i) beside a short line are drawn the same way.
 *
 * SOURCE AND LICENCE
 *
 * Lucide (lucide.dev), ISC licence - free to use in a commercial game, no
 * attribution required in the product. Each icon is drawn on a 24x24 grid with
 * a 2px stroke, round caps and round joins, and is STROKED rather than filled:
 * see UserInterface.railButton(), which sets fill to null.
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

    /**
     * A route: two waypoints and the road that winds between them.
     *
     * Lucide's `route`. Picked over a bus, a lorry or a train because the tab
     * is not any one of those - it is the roads, the trams, the railway and
     * the freight on all three, and a picture of a bus would have promised a
     * screen about buses. The circles are written as arcs; see the note above.
     */
    public static final String INFRASTRUCTURE =
            "M3 19 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0"
          + "M15 5 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0"
          + "M9 19h8.5a3.5 3.5 0 0 0 0-7h-11a3.5 3.5 0 0 1 0-7H15";

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

    /** Three lines: the game menu, at the foot of the rail since 0.7.21 (it was the gear). */
    public static final String MENU = "M4 7h16 M4 12h16 M4 17h16";

    /** An envelope: the inbox, in the header since 0.7.21. The mockups' outline. */
    public static final String MAIL = "M4 6h16v12H4z M4 7l8 6 8-6";

    /** A circled i: the (i) that opens a line's full text (0.7.21; Pieces.infoButton()). */
    public static final String INFO =
            "M3 12 a9 9 0 1 0 18 0 a9 9 0 1 0 -18 0"
          + "M12 11v5 M12 8h.01";

    /* =====================================================================
       THE BUILD TAB'S AND THE FRAME'S (0.7.24)

       One per Build category and the few the overview, the header's money
       block, the "Needs you" chip and the construction tab draw: the round-2
       mockups' paths (gen_build2.py's I table, on Lucide's 24-unit grid and
       its 2px round stroke), the circles written as arcs.
       ===================================================================== */

    /** Four squares: the Build overview. */
    public static final String OVERVIEW = "M4 4h7v7H4z M13 4h7v7h-7z M4 13h7v7H4z M13 13h7v7h-7z";

    /** A bolt: Utilities. */
    public static final String UTILITIES = "M13 3L5 13h6l-1 8 8-10h-6l1-8z";

    /** Two kerbs and a dashed line: Roads & transit. */
    public static final String ROADS = "M8 3L5 21 M16 3l3 18 M12 4v3 M12 10.5v3 M12 17v3";

    /** A cross: Healthcare. */
    public static final String HEALTH = "M9 3h6v6h6v6h-6v6H9v-6H3V9h6z";

    /** A mortarboard: Education. */
    public static final String EDUCATION = "M2 9l10-5 10 5-10 5z M6 11v5c0 1.5 2.7 3 6 3s6-1.5 6-3v-5 M22 9v6";

    /** A shield with a tick: Safety. */
    public static final String SAFETY = "M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z M9 12l2 2 4-4";

    /** A house: Homes. */
    public static final String HOMES = "M4 20V10l8-6 8 6v10z M10 20v-5h4v5";

    /** An awning over a shopfront: Shops. */
    public static final String SHOPS = "M4 9l1.5-5h13L20 9"
          + "M4 9h16v1.5a2.7 2.7 0 0 1-5.3 0 2.7 2.7 0 0 1-5.4 0A2.7 2.7 0 0 1 4 10.5z"
          + "M5 13v7h14v-7 M10 20v-4h4v4";

    /** Saw-tooth roofs and a chimney: Industry. */
    public static final String INDUSTRY = "M3 20h18 M4 20V10l5 3V10l5 3V7h3l.6-3h1.2l.6 3v13";

    /** An office block: Offices. */
    public static final String OFFICES = "M5 20V4h10v16 M15 9h4v11 M3 20h18"
          + "M8 7h1 M11 7h1 M8 10h1 M11 10h1 M8 13h1 M11 13h1 M8 16h1 M11 16h1";

    /** An ear of wheat: Farms. */
    public static final String FARMS = "M12 21V9"
          + "M12 9c-2.4-.8-3.6-2.8-3.6-5.4 2.4.3 3.6 2.4 3.6 5.4z"
          + "M12 9c2.4-.8 3.6-2.8 3.6-5.4-2.4.3-3.6 2.4-3.6 5.4z"
          + "M12 14c-2.4-.6-4-2.2-4.5-4.4 2.3.1 4 1.8 4.5 4.4z"
          + "M12 14c2.4-.6 4-2.2 4.5-4.4-2.3.1-4 1.8-4.5 4.4z"
          + "M12 19c-2.4-.6-4-2.2-4.5-4.4 2.3.1 4 1.8 4.5 4.4z"
          + "M12 19c2.4-.6 4-2.2 4.5-4.4-2.3.1-4 1.8-4.5 4.4z";

    /** A train's face: Rail. */
    public static final String RAIL = "M7 3h10a2 2 0 0 1 2 2v9a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V5a2 2 0 0 1 2-2z"
          + "M5 10h14 M9 21l1.5-4 M15 21l-1.5-4 M8.5 13.5h.01 M15.5 13.5h.01";

    /** A car: Vehicles. The wheels are arcs; see LAND. */
    public static final String VEHICLES = "M5 16v-4l2-5h10l2 5v4z M3 12h18"
          + "M6 17 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0"
          + "M14 17 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0";

    /** A cut stone: Luxury shops. */
    public static final String LUXURY = "M6 4h12l3 5-9 11L3 9z M3 9h18 M9 4l3 16 3-16";

    /** A fork and a knife: Restaurants. */
    public static final String FOOD = "M7 3v7a2 2 0 0 0 2 2v9 M5 3v5 M9 3v5 M17 21V3c-2 1-3.2 4-3.2 8H17";

    /** A tower crane: what is on site - the overview's tiles and the construction tab. */
    public static final String CRANE = "M6 21V5 M3 5h17 M6 5l4-3 M18 5v5 M17 10h2v2h-2z M3 21h7";

    /** A coin: the header's money block. The circle is arcs; see LAND. */
    public static final String COIN = "M4 12 a8 8 0 1 0 16 0 a8 8 0 1 0 -16 0"
          + "M14.5 9.2c-.6-.8-1.5-1.2-2.5-1.2-1.4 0-2.5.8-2.5 2s1.1 1.6 2.5 2 2.5.9 2.5 2-1.1 2-2.5 2"
          + "c-1.1 0-2.1-.5-2.6-1.3 M12 6.5v11";

    /** A warning triangle: the header's "Needs you" chip. */
    public static final String ALERT = "M12 4l9 16H3z M12 10v4 M12 17h.01";

    /** A person: a Build card's staff bar, the posts the city can't fill. The head is arcs; see LAND. */
    public static final String STAFF = "M8.5 7 a3.5 3.5 0 1 0 7 0 a3.5 3.5 0 1 0 -7 0"
          + "M5 21c0-4 3-6.5 7-6.5s7 2.5 7 6.5 M12 15v3 M10.5 16.5h3";

    /** A pin: the drawer that stays open (Lucide's pin). */
    public static final String PIN = "M12 17v5"
          + "M9 10.76a2 2 0 0 1-1.11 1.79l-1.78.9A2 2 0 0 0 5 15.24V16a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-.76"
          + "a2 2 0 0 0-1.11-1.79l-1.78-.9A2 2 0 0 1 15 10.76V7a1 1 0 0 1 1-1 2 2 0 0 0 0-4H8a2 2 0 0 0 0 4"
          + "a1 1 0 0 1 1 1z";

    /** A cross: close. */
    public static final String CLOSE = "M6 6l12 12 M18 6L6 18";

    /**
     * A pick: ore - the land office's Ore card (0.7.26). Drawn here on the
     * same grid rather than taken from Lucide: a handle, and the head as a
     * thin lens bowed away from it, its two points towards the handle.
     */
    public static final String ORE = "M4 20L16 8 M9.5 4.5Q19.5 4.5 19.5 14.5Q17 7 9.5 4.5z";

    /*
     * THE PEOPLE PAGE'S FIVE (0.7.27), on the same grid and stroke, drawn by
     * hand for the redrawn People page: a birth, a death, somebody arriving
     * and somebody leaving (the month's waterfall), and a child (the
     * orphans' tile).
     */

    /** A head and shoulders with a plus: born. */
    public static final String BIRTH = "M9 7a3 3 0 1 0 6 0a3 3 0 1 0-6 0 M6 20c0-4 3-6 6-6s6 2 6 6 M19 3v4 M17 5h4";

    /** A headstone on the ground: died. */
    public static final String DEATH = "M7 21V9a5 5 0 0 1 10 0v12 M4 21h16 M12 10v5 M10 12h4";

    /** An arrow into a door: moved in. */
    public static final String ARRIVE = "M3 12h11 M10 8l4 4-4 4 M15 4h5v16h-5";

    /** An arrow out of a door: moved out. */
    public static final String LEAVE = "M21 12H10 M17 8l4 4-4 4 M9 4H4v16h5";

    /** A small figure: a child, the orphans' tile. */
    public static final String CHILD = "M10 6a2 2 0 1 0 4 0a2 2 0 1 0-4 0 M8 20v-6l4-3 4 3v6";

    /** A tick: nothing needed (the "Needs you" chip, a ring's middle). */
    public static final String TICK = "M5 12.5l4.5 4.5L19 7.5";

    /*
     * THE SERVICES SCREEN'S THREE (0.7.28), on the same grid and stroke,
     * drawn by hand: water, senior care and the prisons. Childcare takes
     * CHILD and death care DEATH, the People page's.
     */

    /** A drop: water. */
    public static final String DROP = "M12 3c-3.5 5-6 8-6 11a6 6 0 0 0 12 0c0-3-2.5-6-6-11z";

    /** A walking cane, its crook at the top: senior care. */
    public static final String CANE = "M9 8a3 3 0 0 1 6 0v13";

    /** A door of bars: the prisons. */
    public static final String CELL = "M5 4h14v16H5z M9 4v16 M12 4v16 M15 4v16";

    /** A bus, from the side (0.7.29, Lucide's bus): the Infrastructure tab's Transit page. */
    public static final String BUS = "M8 6v6 M15 6v6 M2 12h19.6"
            + " M18 18h3s0.5 -1.7 0.8 -2.8c0.1 -0.4 0.2 -0.8 0.2 -1.2 0 -0.4 -0.1 -0.8 -0.2 -1.2"
            + "l-1.4 -5C20.1 6.8 19.1 6 18 6H4a2 2 0 0 0 -2 2v10h3"
            + " M5 18 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0 M9 18h5 M14 18 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0";

    /** A lorry (0.7.29, Lucide's truck): the Infrastructure tab's Freight page, and the fleets on it. */
    public static final String LORRY = "M14 18V6a2 2 0 0 0 -2 -2H4a2 2 0 0 0 -2 2v11a1 1 0 0 0 1 1h2 M15 18H9"
            + " M19 18h2a1 1 0 0 0 1 -1v-3.65a1 1 0 0 0 -0.22 -0.624l-3.48 -4.35A1 1 0 0 0 17.52 8H14"
            + " M15 18 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0 M5 18 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0";

    /*
     * THE SECTORS SCREEN'S FIVE (0.7.30), on the same grid and stroke, drawn
     * by hand: a sector each where the Build categories have none of their
     * own - the bakeries' windmill, the food plants' tin, the mills' ingot,
     * the fabricators' gear (the old settings gear, drawn again) and the
     * mine's pick (the land office's ore).
     */

    /** A windmill, its sails crossed over the tower: Industry, the food mills. */
    public static final String MILL = "M9.5 21l1-8h3l1 8z M11 21v-2.5h2V21"
            + " M12 10L6.5 4.5 M12 10l5.5-5.5 M12 10l-4.5 4.5 M12 10l4.5 4.5";

    /** A tin, its lid an ellipse: Food Processing. The ellipses are arcs; see LAND. */
    public static final String CAN = "M6 6.5 a6 2.5 0 1 0 12 0 a6 2.5 0 1 0 -12 0"
            + " M6 6.5v11 a6 2.5 0 0 0 12 0v-11 M6 12 a6 2.5 0 0 0 12 0";

    /** An ingot, its top face over its side: Heavy Industry, the steel. */
    public static final String INGOT = "M2 18h20l-3-6H5z M5 12l2.5-4h9l2.5 4";

    /** A gear: Manufacturing (the settings gear's outline, drawn since 0.7.30). */
    public static final String GEAR =
            "M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915"
          + "a2.34 2.34 0 0 1 2.33 4.033 2.34 2.34 0 0 0 0 3.831"
          + "a2.34 2.34 0 0 1-2.33 4.033 2.34 2.34 0 0 0-3.319 1.915"
          + "a2.34 2.34 0 0 1-4.659 0 2.34 2.34 0 0 0-3.32-1.915"
          + "a2.34 2.34 0 0 1-2.33-4.033 2.34 2.34 0 0 0 0-3.831"
          + "A2.34 2.34 0 0 1 6.35 6.051a2.34 2.34 0 0 0 3.319-1.915"
          + "M9 12 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0";

    /** A pick: Mining, and an investors' word about ore (the land office's ORE). */
    public static final String PICK = ORE;

    /*
     * THE FINANCES SCREEN'S THREE (0.7.32), on the same grid and stroke: the
     * city's paper (Lucide's file-text), its money (Lucide's banknote, the
     * rectangle and the circle written as arcs - see LAND) and its fund (a
     * safe, drawn by hand: a rounded box, a dial, a handle and two feet).
     * Jerus checks them by eye, as he did ORE.
     */

    /** A sheet of paper with its lines: the city's paper, The book. */
    public static final String PAPER = "M15 2H6a2 2 0 0 0 -2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2 -2V7z"
            + " M14 2v4a2 2 0 0 0 2 2h4 M10 9H8 M16 13H8 M16 17H8";

    /** A banknote: the money itself, M0 and M2. */
    public static final String BANKNOTE = "M4 6h16a2 2 0 0 1 2 2v8a2 2 0 0 1 -2 2H4a2 2 0 0 1 -2 -2V8a2 2 0 0 1 2 -2z"
            + " M10 12a2 2 0 1 0 4 0a2 2 0 1 0 -4 0 M6 12h.01 M18 12h.01";

    /** A safe on two feet, its dial and its handle: the city's fund. */
    public static final String SAFE = "M5 3h14a2 2 0 0 1 2 2v12a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V5a2 2 0 0 1 2 -2z"
            + " M7.5 11a3 3 0 1 0 6 0a3 3 0 1 0 -6 0 M10.5 11h.01 M17 9v4 M6 19v2 M18 19v2";

    /*
     * THE REFINERY'S PICTOGRAM'S SIX (0.7.95, batch O11), on the same grid and
     * stroke: the research's mockup 1 drew the first four (claude/oil-and-
     * ports-research.md 6) - a nodding-donkey well, a platform on the waves, a
     * tanker, a unit's vessel - and the tank and the flame are drawn for it.
     */

    /** A land well, its beam nodding over the pad: the pictogram's land wells. */
    public static final String WELL = "M3 21h18 M8 21l3-9 3 9 M2 9l14-4 3 3-14 4z M16 5v3 M5 12v4";

    /** A platform on its legs over the waves: the pictogram's platform wells. */
    public static final String PLATFORM = "M2 20c2 0 2-1.5 4-1.5S8 20 10 20s2-1.5 4-1.5 2 1.5 4 1.5 2-1.5 4-1.5"
            + " M6 18V10h12v8 M5 10h14 M8 10V6h5v4 M15 10V3";

    /** A tanker, low in the water: imported crude. */
    public static final String TANKER = "M2 15l2 4h16l2-4z M5 15v-3h9v3 M16 15V8h3v7";

    /** A process vessel, a column with its trays: a conversion unit's box. */
    public static final String VESSEL = "M8 7a4 4 0 0 1 8 0v10a4 4 0 0 1 -8 0z M8 9h8 M8 15h8";

    /** A storage tank, its roof a shallow cone: what goes into the refiners' tanks, and a Tank Farm's crude. */
    public static final String TANK = "M4 9l8-4 8 4 M4 9v11h16V9 M4 14h16";

    /** A flame: the furnaces, where residue no diesel can cut is burned. */
    public static final String FLAME = "M12 3c3 4 5 6.5 5 10a5 5 0 0 1 -10 0c0-2 1-3.5 2.5-5 .5 2 1.5 3 2.5 3 -1-3 -.5-5.5 0-8z";

    /** Two arrows passing, one each way (Lucide's arrow-left-right): money changed from one currency to the other - the Trade tab's exchange (0.7.35). */
    public static final String EXCHANGE = "M8 3L4 7l4 4 M4 7h16 M16 21l4-4-4-4 M20 17H4";

    /**
     * The icon of a good (0.7.35, the Trade tab's rows): its family's -
     * FOOD for the fourteen foods and the restaurants' meals, ORE for iron
     * ore, INDUSTRY for steel, fabricated steel and machinery, VEHICLES for
     * cars and vans, RAIL for rolling stock, CRANE for building materials
     * and building work,
     * LUXURY for luxury goods, OFFICES for the three kinds of office work,
     * HOMES for housing and SHOPS for what the shops sell on.
     */
    public static String ofGood(ham.citybuildersim.Good good) {
        if (good == null) return TRADE;
        switch (good) {
            case IRON:                        return ORE;
            // ...the oil and what it is refined into (0.7.62), a drop of each -
            // the liquids since 0.7.76; bitumen is the road it binds and coke a lump.
            case CRUDE: case LPG: case NAPHTHA: case PETROL: case JET: case DIESEL:
            case LUBRICANTS: case FUEL_OIL:   return DROP;
            case BITUMEN:                     return ROADS;
            case COKE:                        return ORE;
            case STEEL: case FABRICATED_STEEL: case MACHINERY: return INDUSTRY;
            case CARS: case VANS:             return VEHICLES;
            case ROLLING_STOCK:               return RAIL;
            case MATERIALS: case BUILDING_WORK: return CRANE;
            case LUXURIES:                    return LUXURY;
            case LUXURY_TRADE: case GROCERIES: return SHOPS;
            case HOUSING:                     return HOMES;
            case SUPPORT_WORK: case BACK_OFFICE_WORK: case ENGINEERING_WORK: return OFFICES;
            default:                          return FOOD;
        }
    }

    /** The icon of a sector (0.7.30): its Build category's, or one of the five above where the category is shared. */
    public static String ofSector(ham.citybuildersim.Sector sector) {
        if (sector == null) return SECTOR;
        switch (sector.key()) {
            case ham.citybuildersim.Sectors.RETAIL:            return SHOPS;
            case ham.citybuildersim.Sectors.REAL_ESTATE:       return HOMES;
            case ham.citybuildersim.Sectors.INDUSTRY:          return MILL;
            case ham.citybuildersim.Sectors.CONSTRUCTION:      return CRANE;
            case ham.citybuildersim.Sectors.HEAVY_INDUSTRY:    return INGOT;
            case ham.citybuildersim.Sectors.MINING:            return PICK;
            case ham.citybuildersim.Sectors.MATERIALS:         return BUILD;
            case ham.citybuildersim.Sectors.BUSINESS_SERVICES: return OFFICES;
            case ham.citybuildersim.Sectors.MANUFACTURING:     return GEAR;
            case ham.citybuildersim.Sectors.AGRICULTURE:       return FARMS;
            case ham.citybuildersim.Sectors.FOOD_PROCESSING:   return CAN;
            case ham.citybuildersim.Sectors.RAIL:              return RAIL;
            case ham.citybuildersim.Sectors.AUTOMOTIVE:        return VEHICLES;
            case ham.citybuildersim.Sectors.LUXURY_RETAIL:     return LUXURY;
            case ham.citybuildersim.Sectors.RESTAURANTS:       return FOOD;
            // ...the wells, a drop of oil, and the refinery, a plant (0.7.62).
            case ham.citybuildersim.Sectors.OIL:               return DROP;
            case ham.citybuildersim.Sectors.REFINING:          return INDUSTRY;
            default:                                           return SECTOR;
        }
    }

    /** The icon of a Build category, by its name (BuildAdvice's). */
    public static String ofCategory(String name) {
        switch (name) {
            case "Utilities":       return UTILITIES;
            case "Roads & transit": return ROADS;
            case "Healthcare":      return HEALTH;
            case "Education":       return EDUCATION;
            case "Safety":          return SAFETY;
            case "Homes":           return HOMES;
            case "Shops":           return SHOPS;
            case "Industry":        return INDUSTRY;
            case "Offices":         return OFFICES;
            case "Farms":           return FARMS;
            case "Rail":            return RAIL;
            case "Vehicles":        return VEHICLES;
            case "Luxury shops":    return LUXURY;
            case "Restaurants":     return FOOD;
            default:                return OVERVIEW;
        }
    }

    /**
     * The icon of a named episode's kind (0.7.37, City History's hard
     * times): a recession or a depression the History chart's own line,
     * a slump the out of work, an epidemic the cross, a financial crisis the
     * bank, the money's crises - the currency, inflation, deflation - the
     * coin, a treasury crisis the treasury's.
     */
    public static String ofEpisode(String kind) {
        switch (kind == null ? "" : kind) {
            case "recession": case "depression":              return REPORTS;
            case "slump":                                     return STAFF;
            case "epidemic":                                  return HEALTH;
            case "financial":                                 return BANK;
            case "currency": case "inflation": case "deflation": return COIN;
            case "treasury":                                  return FINANCES;
            default:                                          return ALERT;
        }
    }

    /**
     * The icon of a decision's kind (0.7.37, City History's decisions), as
     * its flag is coloured by the area it is about: a tax the government's,
     * a promise the policy scales, the central bank and the bank the bank's,
     * the money the coin, the paper and the fund the treasury's, the queue
     * the crane.
     */
    public static String ofDecision(String kind) {
        switch (kind == null ? "" : kind) {
            case ham.citybuildersim.DecisionLog.TAX:          return GOVERNMENT;
            case ham.citybuildersim.DecisionLog.PROMISE:      return POLICY;
            case ham.citybuildersim.DecisionLog.CENTRAL_BANK:
            case ham.citybuildersim.DecisionLog.BANK:         return BANK;
            case ham.citybuildersim.DecisionLog.CURRENCY:     return COIN;
            case ham.citybuildersim.DecisionLog.BORROWING:
            case ham.citybuildersim.DecisionLog.FUND:         return FINANCES;
            case ham.citybuildersim.DecisionLog.CONSTRUCTION: return CRANE;
            case ham.citybuildersim.DecisionLog.RESERVE:      return DROP;
            default:                                          return PIN;
        }
    }

    /** A die showing five (Lucide "dice-5"): the founding screen's roll of a new world (0.7.56). The square is written as arcs; the pips are dots. */
    public static final String DICE = "M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V5a2 2 0 0 1 2 -2z"
            + " M8 8h.01 M16 8h.01 M12 12h.01 M8 16h.01 M16 16h.01";

    /** A folded map (Lucide "map", its earlier three-panel form): the land office's map, expanded over the window, and Build's shortcut, "Buy the best: North 3 · ..." (0.7.61; "Buy the best land" until 0.7.69). */
    public static final String MAP = "M3 6l6-3 6 3 6-3v15l-6 3-6-3-6 3z M9 3v15 M15 6v15";

    /** Two arrows out to the corners (Lucide "maximize-2"): the land office's Expand (0.7.61). */
    public static final String EXPAND = "M15 3h6v6 M9 21H3v-6 M21 3l-7 7 M3 21l7-7";

    /** A gear. Nothing draws it since 0.7.21: the rail's foot is Menu. */
    public static final String SETTINGS =
            "M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915"
          + "a2.34 2.34 0 0 1 2.33 4.033 2.34 2.34 0 0 0 0 3.831"
          + "a2.34 2.34 0 0 1-2.33 4.033 2.34 2.34 0 0 0-3.319 1.915"
          + "a2.34 2.34 0 0 1-4.659 0 2.34 2.34 0 0 0-3.32-1.915"
          + "a2.34 2.34 0 0 1-2.33-4.033 2.34 2.34 0 0 0 0-3.831"
          + "A2.34 2.34 0 0 1 6.35 6.051a2.34 2.34 0 0 0 3.319-1.915"
          + "M9 12 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0";
}
