package ham.citybuildersim.ui;

/**
 * Every colour, size and spacing this game is allowed to use, in one place.
 *
 * WHY THIS EXISTS
 *
 * There are about five hundred hex literals in UserInterface.java. They are not
 * wrong - each was chosen against the thing next to it - but there is no way to
 * answer "what colour is a warning" except by finding another warning and
 * copying it, and no way to change one's mind about the answer except by
 * finding all of them. Every screen redone from here reads its colours from
 * this class, so the next forty screens agree with each other by construction
 * rather than by my memory.
 *
 * THE RULE THE WHOLE PALETTE IS BUILT ON
 *
 * Colour is news. The grounds and the greys carry structure and never say
 * anything; GOOD, WARN and BAD are the only colours that say how the city is
 * doing, and a screen that uses them for decoration has spent the one signal
 * it had. That is why there are greys and three meanings: the greys do the
 * layout, and the three are saved for when the city is actually telling you
 * something. Since 0.7.21 four more say WHERE you are - the area colours -
 * and never how it is going; the two kinds never swap jobs.
 *
 * A LABEL IS NEVER THE NEWS. In every row in this game the label on the left
 * stays grey and the figure on the right takes the colour, because the figure
 * is what changed. Colouring the label makes a row shout about its own
 * existence.
 *
 * NOT RETROFITTED IN ONE GO, on purpose. Rewriting five hundred literals in one
 * pass is a change nobody can review and a diff that hides every real edit
 * inside it. The screens adopt this as they are redone, one rail tab at a time.
 *
 * THE 0.7.21 PALETTE. Jerus asked for the interface to be "less of a
 * textbook, more colourful", saw the mockups and said "that is damn pretty,
 * go for it". The values below are the mockups' (the project's
 * playing-0-7-19-ui-notes.md, section 7; gen_common.py): a darker, bluer
 * set of grounds, three greys for text, FOUR AREA COLOURS that say which
 * part of the city a screen is about - on its rail button, its title's
 * swatch, its header tile and a one-line chart - and the three verdicts,
 * which still mean good, watch and bad and nothing else. The roles kept
 * their names, so every screen that read them took the new values without
 * an edit; the window's own literals moved here the same day.
 *
 * @author Jerus
 */
public final class Palette {

    private Palette() { }

    /* =====================================================================
       THE GROUNDS

       Four of them, and the order matters more than the values: the stage is
       darkest, the chrome around it is lighter, a block raised off the chrome
       is lighter still. Depth reads as "further forward", so the thing the eye
       should land on is the palest thing on screen.
       ===================================================================== */

    /** The middle of the window - the darkest thing, and the biggest. The mockups' window. */
    public static final String STAGE = "#0b1118";

    /** The strips around it: the two side panels (a drawer and an overlay since 0.7.24), the header, the rail and the construction tab. */
    public static final String PANEL = "#101924";

    /** A block lifted off a panel: a card, a header tile, an open section. The mockups' card. */
    public static final String RAISED = "#152130";

    /** The vitals block, and anything that should read as pinned rather than raised - and a popover. The mockups' raised. */
    public static final String PINNED = "#1a2839";

    /** Inside a control, and inside the inbox: darker than the panel it sits on - the window's own ground. */
    public static final String FIELD = STAGE;

    /** A button at rest: the mockups' raised. */
    public static final String CONTROL = "#1a2839";

    /** A button under the pointer: a step above CONTROL in its hue (the mockups draw no hover). */
    public static final String HOVER = "#22334a";

    /* =====================================================================
       THE EDGES
       ===================================================================== */

    /** Panel against stage. The one structural line in the layout. The mockups' line. */
    public static final String EDGE = "#24354a";

    /** A quieter rule: inside a panel, under a heading, around a control. The same line since 0.7.21. */
    public static final String HAIRLINE = EDGE;

    /** The border of a control that can be pressed: a step up from the line, in its hue (the mockups draw none). */
    public static final String CONTROL_EDGE = "#33475f";

    /* =====================================================================
       THE TEXT

       Weights of grey, and they are a ladder rather than a set. Going down
       the ladder is how a screen says "this matters less", and it is the
       only way it should say it - shrinking type does the same job worse,
       because a figure that has to be read cannot be 8px.

       THREE GREYS SINCE 0.7.21, the mockups': text, secondary and captions,
       and the roles share them. The figure on the right of a row is text and
       its label on the left is secondary, as the mockups draw a row; notes
       and captions are the captions' grey. Only the spent grey is not the
       mockups' - they have nothing struck through - and is a step below the
       captions in the same hue.
       ===================================================================== */

    /** Text: titles, figures, the date. */
    public static final String TEXT = "#e6edf3";

    /** Secondary: the label on the left of a row, a card's second line. */
    public static final String TEXT_2 = "#a9b8c9";

    /** Captions: a note, a unit, a lead under a title (at least 4.5:1 on the panel). */
    public static final String TEXT_3 = "#8496ab";

    /** The date. The palest thing in the window until 0.7.21; the same text white as TEXT_HEAD and TEXT_BODY since. */
    public static final String TEXT_MAX = TEXT;

    /** A screen title, a section heading, a figure that is the answer. */
    public static final String TEXT_HEAD = TEXT;

    /** Ordinary text and ordinary figures. */
    public static final String TEXT_BODY = TEXT;

    /** A caption, a unit, a subtitle, a note under a figure. */
    public static final String TEXT_MUTED = TEXT_3;

    /** The label on the left of a row. Never the news, so never the brightest. */
    public static final String TEXT_LABEL = TEXT_2;

    /** Disabled, or a heading that is not the one you are on. */
    public static final String TEXT_FAINT = TEXT_3;

    /** Struck through, settled, over with - a resolved notice, an old entry. */
    public static final String TEXT_SPENT = "#5f7189";

    /* =====================================================================
       THE THREE THAT MEAN SOMETHING

       Used on FIGURES, not on labels, and only when the figure is saying
       something a player should act on. A screen where everything is green is
       a screen where nothing is.
       ===================================================================== */

    /** It is going the right way: on target, covered, in surplus. */
    public static final String GOOD = "#3fb950";

    /** Money going the right way - the same green since 0.7.21, which has one. */
    public static final String GOOD_MONEY = GOOD;

    /** It is not wrong yet, and it will be - near a limit, or good news with a cost. */
    public static final String WARN = "#e3b341";

    /** It is costing the city something now: past a limit, overdrawn, unhoused, failing. */
    public static final String BAD = "#f85149";

    /** What sits on a verdict's fill - a play button, a rating, a confirm in the mockups' style. */
    public static final String ON_FILL = STAGE;

    /** The same, in small type where full strength reads as shouting. */
    public static final String BAD_SOFT = "#ff9e9e";

    /** Text sitting on the alert ground below. */
    public static final String BAD_TEXT = "#ffd9d4";

    /** Behind something wrong. */
    public static final String ALERT_GROUND = "#331d1d";

    /** Behind something wrong and unread. */
    public static final String ALERT_GROUND_LOUD = "#3b1f1f";

    /** The edge of either. */
    public static final String ALERT_EDGE = "#c0392b";

    /* =====================================================================
       WHAT CAN BE PRESSED

       ACCENT is "you are here" and "this is the main action"; CONFIRM is "this
       spends money or changes policy". Two, not one, because the difference
       between navigating and committing is the difference a player most needs
       to see before clicking.
       ===================================================================== */

    /** The active tab, a live link, the heading of an open section - the money blue, which the mockups' links are. */
    public static final String ACCENT = "#5aa9ff";

    /** Filled: the primary button that carries white text. */
    public static final String ACCENT_FILL = "#2f6fa8";

    /** The accent under the pointer: a link hovered, a slider's thumb (the mockups' link hover). */
    public static final String ACCENT_LIGHT = "#8cc4ff";

    /** Filled: the button that commits - pays, builds, sets the policy. */
    public static final String CONFIRM = "#2f7d52";

    /** Behind a confirmation that has already happened. */
    public static final String CONFIRM_GROUND = "#13291d";

    /* =====================================================================
       THE FOUR AREAS (0.7.21)

       Which part of the city a screen is about, said once and the same way
       everywhere it is said: the rail button of the screen when it is
       selected, the swatch before its title, its header tile's label and
       sparkline, and its chart's line where the page draws one series.
       Never a verdict - a screen about people is not good news - and never
       on a figure, which is the verdicts' place. Which screen is which is
       UserInterface.areaOf().
       ===================================================================== */

    /** People and services: People, Services, Infrastructure. Population, out of work. */
    public static final String PEOPLE = "#2ec4b6";

    /** Money and policy: Government, Finances, the bank, Policy, History. Treasury, rates, inflation. */
    public static final String MONEY = "#5aa9ff";

    /** Business and trade: Sectors, Trade & the world. GDP, profits, exports, investors' buildings. */
    public static final String BUSINESS = "#a78bfa";

    /** Building and land: Build, the land office. Sites, the queue, ground used. */
    public static final String BUILDING = "#f17cb0";

    /**
     * Categories told apart (0.7.21): the Government's two rings, whose three
     * biggest revenue slices were three steps of one blue and read as one.
     * The four area colours, then a sand that is neither a verdict nor an
     * area; the sixth slice and on are RAMP_REST. A ring's colours here say
     * WHICH, not how much - the ring's angles already say how much.
     */
    public static final String[] CATEGORIES = {
        MONEY, PEOPLE, BUILDING, BUSINESS, "#c9b68f"
    };

    /**
     * Ore (0.7.26): the land office's deposits - a plot's ORE tag, the sand
     * stripe on the ground bar, the Ore card. A resource, so neither an area
     * nor a verdict: CATEGORIES' sand, kept for exactly that. It replaces the
     * lilac the office hard-coded three times (#ce93d8), which read as the
     * business violet.
     */
    public static final String ORE = "#c9b68f";

    /* =====================================================================
       THE CHART'S LINES (0.7.23)

       A line takes its AREA's colour - the four the rail, the titles and the
       header's tiles say where you are with - so a chart of the treasury and
       the population is blue and teal before a word of its legend is read.
       RED, AMBER AND GREEN ARE NEVER A LINE. They are the verdicts, and a
       line is not good or bad news by being drawn; the eight-colour SERIES
       this replaced carried all three (0.7.21 left it for this batch).

       Several lines of one area on one chart: the first takes the area's
       colour, the next the other CATEGORIES in their order - as the mockups
       draw the rate, the price level and inflation in three of them; in
       CATEGORIES' order that is blue, teal and pink - and past those a
       lighter and a darker step of the line's own area, each at 4:1 or
       better against the card (RAISED; WCAG's ratio) - the darker violet,
       BUSINESS_DARK, the nearest at 4.02:1 (4.38:1 on the panel): it was
       #8466e8, 3.9:1, until the docs pass measured it, and was lightened
       along its own hue just far enough. LINE_COLOURS before anything
       repeats.
       ===================================================================== */

    /** The money blue, a step lighter and a step darker: the money area's second and third lines. */
    public static final String MONEY_LIGHT = "#a9d1ff", MONEY_DARK = "#2f80d9";

    /** The people teal, lighter and darker. */
    public static final String PEOPLE_LIGHT = "#93e2da", PEOPLE_DARK = "#1c968b";

    /** The business violet, lighter and darker. */
    public static final String BUSINESS_LIGHT = "#d3c5fd", BUSINESS_DARK = "#8669e8";

    /** The building pink, lighter and darker. */
    public static final String BUILDING_LIGHT = "#f9bcd7", BUILDING_DARK = "#cf5590";

    /** How many lines one chart can draw before a colour repeats: the five CATEGORIES and two steps of each of the four areas. */
    public static final int LINE_COLOURS = 13;

    /** An area's two further steps, lighter first; none for a colour that is not an area's. */
    static String[] stepsOf(String area) {
        if (MONEY.equals(area))    return new String[] {MONEY_LIGHT, MONEY_DARK};
        if (PEOPLE.equals(area))   return new String[] {PEOPLE_LIGHT, PEOPLE_DARK};
        if (BUSINESS.equals(area)) return new String[] {BUSINESS_LIGHT, BUSINESS_DARK};
        if (BUILDING.equals(area)) return new String[] {BUILDING_LIGHT, BUILDING_DARK};
        return new String[0];
    }

    /**
     * Each line's colour, given each line's area in the order they are drawn:
     * the area's own for the first of an area, then the CATEGORIES nobody on
     * the chart has, then the area's lighter and darker steps, then any of
     * the thirteen still free, and only then a repeat. A verdict never.
     */
    public static String[] lineColours(java.util.List<String> areas) {
        String[] out = new String[areas.size()];
        java.util.Set<String> used = new java.util.LinkedHashSet<>();
        // The first line of each area takes the area's colour.
        for (int i = 0; i < out.length; i++) {
            String area = areas.get(i);
            if (area != null && !used.contains(area)) { out[i] = area; used.add(area); }
        }
        java.util.List<String> all = new java.util.ArrayList<>(java.util.Arrays.asList(CATEGORIES));
        for (String a : new String[] {MONEY, PEOPLE, BUSINESS, BUILDING}) all.addAll(java.util.Arrays.asList(stepsOf(a)));
        for (int i = 0; i < out.length; i++) {
            if (out[i] != null) continue;
            String pick = null;
            for (String c : CATEGORIES) if (!used.contains(c)) { pick = c; break; }
            if (pick == null) for (String c : stepsOf(areas.get(i))) if (!used.contains(c)) { pick = c; break; }
            if (pick == null) for (String c : all) if (!used.contains(c)) { pick = c; break; }
            if (pick == null) pick = all.get(i % all.size());
            out[i] = pick;
            used.add(pick);
        }
        return out;
    }

    /* =====================================================================
       THE CHART RAMPS

       SEQUENTIAL, NOT CATEGORICAL, and that is a decision rather than a
       shortcut. A government's revenue breaks into nine sources and its
       spending into six, and nine competing hues is a palette nobody can read:
       past about four, adjacent hues stop being separable for the eight per
       cent of men with a colour deficiency, and the two that collapse first are
       red against green - which are the two colours this game has already spent
       on "good" and "bad".

       So the slices are shaded along ONE hue by size, biggest brightest. That
       encodes the magnitude twice, keeps every reserved colour free for the
       thing that actually matters on a budget screen - whether it is a surplus
       or a deficit - and cannot fail a colour-blindness check, because there is
       only one hue in it.

       The Government's two rings left these ramps in 0.7.21 for CATEGORIES
       (see there: their three biggest revenue slices read as one blue); the
       ramps still draw the bank's rings and bars (and the trade page's until
       0.7.35), the maturity ladder and GDP's layers.

       MEASURED, NOT CHOSEN. These steps were generated at fixed OKLCH hue with
       an even lightness ramp and checked against the dark surface: monotone
       lightness, at least 0.06 of L between adjacent steps, single hue, and the
       dimmest step still clearing 3:1 contrast on the panel. Five steps is what
       that budget buys; a sixth fell under the gap. So five slices and OTHER,
       which is also the most a ring can carry and still be read at a glance.
       ===================================================================== */

    /** Money coming in. Brightest is the biggest source. */
    public static final String[] REVENUE_RAMP = {
        "#afd5fe", "#8dbef1", "#6aa6e4", "#468fd6", "#1577c8"
    };

    /** Money going out. */
    public static final String[] SPENDING_RAMP = {
        "#efca9f", "#deaf78", "#cd954f", "#bc7a19", "#aa6000"
    };

    /**
     * The three instruments on the maturity ladder, short to long.
     *
     * A SEQUENTIAL RAMP, NOT A CATEGORICAL SET, and that is the right choice
     * rather than a convenience: notes, serial bonds and term loans are ordered
     * by how long the money is borrowed for, so light-to-dark down one hue says
     * something true. Three unrelated hues would say they are three unrelated
     * things, which they are not.
     *
     * The ends and the middle of REVENUE_RAMP. Checked with the validator on
     * this panel: worst adjacent pair 14.6 dE under protanopia and 15.5 to
     * normal vision, all three clearing 3:1 against the ground. The two nearer
     * pairs of the five-step ramp do NOT clear the 15 floor, which is why this
     * takes 0, 2 and 4 rather than three in a row.
     */
    public static final String[] LADDER = {
        REVENUE_RAMP[0], REVENUE_RAMP[2], REVENUE_RAMP[4]
    };

    /**
     * GDP's three stacked layers on the Reports page (0.7.6) - consumption,
     * investment, government, bottom to top - on the same three validated
     * steps as the maturity ladder, and for the same reason: they are parts
     * of one whole, and one hue says so. The GDP line over them is drawn in
     * TEXT_HEAD, which no step of this ramp is near.
     */
    public static final String[] GDP_LAYERS = {
        REVENUE_RAMP[0], REVENUE_RAMP[2], REVENUE_RAMP[4]
    };

    /** Everything too small to have its own step. Deliberately colourless. */
    public static final String RAMP_REST = "#5c6b75";

    /** How thick a donut's ring is drawn, and how wide the hole is. */
    public static final double RING = 26;

    /* =====================================================================
       TYPE

       ONE RULE: every figure is monospaced, every word is not.

       A number that changes width as it changes value moves sideways, and a
       figure that moves is one the eye has to find again every month. A
       monospaced face holds the digits in their columns. Prose in it is just
       harder to read, so prose is not in it.

       IBM PLEX SINCE 0.7.21: Plex Mono for every figure, Plex Sans for the
       words, loaded from the jar at start-up (Fonts). The words' face is set
       once on the stylesheet's root; a figure names its face through mono(),
       read when the style is built, so it is Courier New again if Plex Mono
       did not load.
       ===================================================================== */

    /** Figures. All of them, everywhere, at every size: the CSS family, "'IBM Plex Mono'" or the fallback. */
    public static String mono() { return Fonts.mono(); }

    /*
     * GLYPH AND SIZE_HEADLINE WENT IN 0.7.21. The first named a symbol face for
     * the rail, the envelope and the round buttons, which are all drawn
     * outlines now (Icons); the second was the date's and the cash's 28px on
     * the date bar, which is the header, at the mockups' sizes.
     */

    /** A screen's title. */
    public static final int SIZE_TITLE = 20;

    /** A figure that is the point of its panel. */
    public static final int SIZE_LEAD = 17;

    /** A section heading inside a screen. */
    public static final int SIZE_SECTION = 14;

    /** A panel's own heading. */
    public static final int SIZE_HEADING = 12;

    /** Body text, and the figure in a row. */
    public static final int SIZE_BODY = 11;

    /** The label in a row, and a button in a dense list. */
    public static final int SIZE_LABEL = 10;

    /** A caption under something, and a unit after something. */
    public static final int SIZE_CAPTION = 9;

    /* =====================================================================
       SPACE

       A four-point scale, because six of them is a scale nobody keeps to.
       ===================================================================== */

    public static final int GAP_TIGHT = 4;
    public static final int GAP = 8;
    public static final int GAP_LOOSE = 12;
    public static final int GAP_SECTION = 20;

    /** Corner of a block, a chip, a control. */
    public static final int RADIUS = 4;

    /** Corner of something small - a row, a badge. */
    public static final int RADIUS_TIGHT = 3;

    /* =====================================================================
       THE FIXED WIDTHS

       These are layout facts rather than taste: change one and something else
       has to move.
       ===================================================================== */

    /** The navigation rail, at the window's left edge: an icon over its name (0.7.21). */
    public static final double RAIL = 76;

    /** The header across the top: the clock, the money block, five headline tiles, the "Needs you" chip, the rating and the inbox (0.7.21; the money block and the chip 0.7.24). */
    public static final double HEADER = 84;

    /** The city panel, not counting the rail: the drawer over the stage's left since 0.7.24. */
    public static final double CITY_PANEL = 290;

    /** The construction panel down the right: over the stage while open, a tab while folded, since 0.7.24. */
    public static final double BUILD_PANEL = 280;

    /** A building row, so the price column lines up down the list. */
    public static final double BUILD_ROW = 400;

    /** The inbox, sized to the 62-character lines the notices are written at. */
    public static final double INBOX = 470;

    /* =====================================================================
       THE THREE THINGS A SCREEN IS BUILT FROM

       Sugar, so a redone screen is a list of what it says rather than a list of
       CSS. Everything below composes the constants above and nothing invents a
       value of its own.
       ===================================================================== */

    /** "-fx-text-fill: X;" */
    public static String fill(String colour) {
        return "-fx-text-fill: " + colour + ";";
    }

    /**
     * A figure: monospaced, at a size, in a colour - semibold since 0.7.21,
     * as the mockups set their figures (Courier's was bold, and Plex Mono's
     * bold is heavier than the figure needs).
     */
    public static String figure(int size, String colour) {
        return Fonts.monoSemiBold() + " -fx-font-size: " + size + "px;"
             + " -fx-text-fill: " + colour + ";";
    }

    /** A figure at the regular weight: a table's cells, where a column of semibold is a wall. */
    public static String figureRegular(int size, String colour) {
        return "-fx-font-family: " + mono() + "; -fx-font-size: " + size + "px;"
             + " -fx-font-weight: normal; -fx-text-fill: " + colour + ";";
    }

    /** Words at the semibold weight: a title, a tile's label (0.7.21). */
    public static String strong(int size, String colour) {
        return Fonts.sansSemiBold() + " -fx-font-size: " + size + "px; -fx-text-fill: " + colour + ";";
    }

    /** A word: the words' face, at a size, in a colour - Plex Sans from the stylesheet's root since 0.7.21, the platform's if it did not load. */
    public static String words(int size, String colour) {
        return "-fx-font-size: " + size + "px; -fx-text-fill: " + colour + ";";
    }

    /** A block of content raised off its ground. */
    public static String block(String ground) {
        return "-fx-background-color: " + ground + "; -fx-background-radius: " + RADIUS + ";";
    }

    /** A block with an edge, for when it has to be told from its neighbour. */
    public static String block(String ground, String edge) {
        return block(ground) + " -fx-border-color: " + edge + ";"
             + " -fx-border-radius: " + RADIUS + ";";
    }

    /* =====================================================================
       THE FACES (0.7.21)

       The two faces are loaded, not merely named, so the loading lives here
       beside the TYPE it serves, as Palette's own nested class: Palette says
       which face a figure and a word are in, and Fonts says which family
       JavaFX actually registered for each.
       ===================================================================== */

    /**
     * The game's two typefaces, IBM Plex Sans for words and IBM Plex Mono for every figure, loaded from the jar once at start-up.
     *
     * WHY (0.7.21). Jerus asked for the window to be less of a textbook and saw
     * the mockups set in IBM Plex: "that is damn pretty, go for it". The game
     * drew words in the platform's face and figures in Courier New, and the two
     * sat side by side in one panel. Plex Sans and Plex Mono are one design in
     * two widths, and the Mono holds every digit in its column, which is the one
     * job Courier was there for. The eight files are IBM's own, converted from
     * its npm packages, under the SIL Open Font License, which travels beside
     * them in src/main/resources/fonts/OFL.txt; Maven carries the folder into
     * the jar with the other resources.
     *
     * THE NAMES ARE THE ONES JAVAFX REPORTS. JavaFX names a font by the legacy
     * family and style of its TrueType name table (name IDs 1 and 2), not by the
     * typographic family (ID 16): Regular and Bold report "IBM Plex Sans", but
     * the Medium and SemiBold files report families of their own, "IBM Plex
     * Sans Medm" and "IBM Plex Sans SmBld", each with a Regular style. And CSS
     * reaches two weights of a family and no more: -fx-font-weight below 700 is
     * the family's regular face and 700 or above its bold (PrismFontLoader). So
     * a semibold word is asked for by the SmBld family at the normal weight -
     * sansSemiBold() and monoSemiBold() - and every family this class hands
     * out is the one Font.getFamily() returned for that file when it loaded.
     *
     * A FALLBACK THAT STILL LINES UP. JavaFX's CSS uses the first family named
     * and nothing after it, so "'IBM Plex Mono', 'Courier New'" would not fall
     * back: an unknown family is the platform's proportional face. So the
     * fallback is chosen here, file by file: a file that does not load leaves
     * its family at the platform's ("System" for words, Courier New for figures,
     * as before 0.7.21), and the semibold and medium faces fall back to bold and
     * regular of whatever words and figures got. The log says which.
     *
     * LOADED ONCE, BY THE WINDOW, BEFORE ANYTHING IS STYLED (load(), called
     * first in UserInterface.start()). Everything else reads the families
     * through these methods at the moment it builds a style, never into a
     * static final, so a style built after the load always gets the loaded
     * face. Nested in Palette, which owns the type. A harness
     * that builds a window without starting the toolkit (BuildMenuCheck) never
     * calls load(), and gets the fallbacks.
     */
    public static final class Fonts {

        private Fonts() { }

        /** Where the files sit on the classpath. */
        static final String FOLDER = "/fonts/";

        /** The platform's own face, for words when Plex Sans did not load. */
        static final String SYSTEM_FACE = "System";

        /** The face figures used before 0.7.21, and use again when Plex Mono did not load. */
        static final String FIGURE_FACE = "Courier New";

        private static String sans = SYSTEM_FACE, sansMedium, sansSemiBold;
        private static String mono = FIGURE_FACE, monoMedium, monoSemiBold;
        private static boolean tried;

        /**
         * Loads the eight files and records the family JavaFX reports for each.
         * Called once from UserInterface.start(), on the FX thread, before the
         * scene is styled; a second call does nothing. Never throws: a file that
         * will not load is logged and its family left at the fallback.
         */
        static synchronized void load() {
            if (tried) return;
            tried = true;
            StringBuilder said = new StringBuilder();

            String sansRegular = family("IBMPlexSans-Regular.ttf", said);
            family("IBMPlexSans-Bold.ttf", said);
            String sansMed = family("IBMPlexSans-Medium.ttf", said);
            String sansSemi = family("IBMPlexSans-SemiBold.ttf", said);
            String monoRegular = family("IBMPlexMono-Regular.ttf", said);
            family("IBMPlexMono-Bold.ttf", said);
            String monoMed = family("IBMPlexMono-Medium.ttf", said);
            String monoSemi = family("IBMPlexMono-SemiBold.ttf", said);

            if (sansRegular != null) {
                sans = sansRegular;
                sansMedium = sansMed;
                sansSemiBold = sansSemi;
            }
            if (monoRegular != null) {
                mono = monoRegular;
                monoMedium = monoMed;
                monoSemiBold = monoSemi;
            }
            System.out.println("Fonts: words in " + sans
                    + (sansSemiBold != null ? " (semibold " + sansSemiBold + ")" : "")
                    + ", figures in " + mono
                    + (monoSemiBold != null ? " (semibold " + monoSemiBold + ")" : "")
                    + (said.length() == 0 ? "" : " -" + said));
        }

        /** One file: the family JavaFX reports for it, or null - and why, in the log line. */
        private static String family(String file, StringBuilder said) {
            try (java.io.InputStream in = Fonts.class.getResourceAsStream(FOLDER + file)) {
                if (in == null) {
                    said.append(' ').append(file).append(" is not in the jar;");
                    return null;
                }
                javafx.scene.text.Font font = javafx.scene.text.Font.loadFont(in, 12);
                if (font == null) {
                    said.append(' ').append(file).append(" did not load;");
                    return null;
                }
                return font.getFamily();
            } catch (Throwable e) {
                said.append(' ').append(file).append(": ").append(e).append(';');
                return null;
            }
        }

        /** Whether Plex Sans loaded; the words are the platform's face if not. */
        static boolean loaded() { return !SYSTEM_FACE.equals(sans); }

        /** The family for words, quoted for CSS: "'IBM Plex Sans'", or the platform's. */
        static String sans() { return quoted(sans); }

        /** The family for figures, quoted for CSS: "'IBM Plex Mono'", or Courier New. */
        static String mono() { return quoted(mono); }

        /** CSS for words at the semibold weight: the SmBld family at the normal weight, or bold of the words' face. */
        static String sansSemiBold() {
            return sansSemiBold != null
                    ? "-fx-font-family: " + quoted(sansSemiBold) + "; -fx-font-weight: normal;"
                    : "-fx-font-family: " + sans() + "; -fx-font-weight: bold;";
        }

        /** CSS for words at the medium weight, or the regular face. */
        static String sansMedium() {
            return "-fx-font-family: " + (sansMedium != null ? quoted(sansMedium) : sans())
                    + "; -fx-font-weight: normal;";
        }

        /** CSS for figures at the semibold weight: the SmBld family at the normal weight, or bold of the figures' face. */
        static String monoSemiBold() {
            return monoSemiBold != null
                    ? "-fx-font-family: " + quoted(monoSemiBold) + "; -fx-font-weight: normal;"
                    : "-fx-font-family: " + mono() + "; -fx-font-weight: bold;";
        }

        /** CSS for figures at the medium weight, or the regular face. */
        static String monoMedium() {
            return "-fx-font-family: " + (monoMedium != null ? quoted(monoMedium) : mono())
                    + "; -fx-font-weight: normal;";
        }

        private static String quoted(String family) { return "'" + family + "'"; }

        /* ----- on a canvas (0.7.23): a Font, not CSS, for the charts ----- */

        /** Words at this size, for a canvas: Plex Sans or the platform's. */
        static javafx.scene.text.Font sansFont(double size) {
            return javafx.scene.text.Font.font(sans, size);
        }

        /** Words at the semibold weight, for a canvas: the SmBld family, or bold of the words' face. */
        static javafx.scene.text.Font sansStrongFont(double size) {
            return sansSemiBold != null ? javafx.scene.text.Font.font(sansSemiBold, size)
                    : javafx.scene.text.Font.font(sans, javafx.scene.text.FontWeight.BOLD, size);
        }

        /** Figures at this size, for a canvas: Plex Mono or Courier New. */
        static javafx.scene.text.Font monoFont(double size) {
            return javafx.scene.text.Font.font(mono, size);
        }
    }
}
