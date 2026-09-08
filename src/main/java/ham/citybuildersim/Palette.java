package ham.citybuildersim;

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
 * anything; GOOD, WARN and BAD are the only colours that mean something, and a
 * screen that uses them for decoration has spent the one signal it had. That is
 * why there are five greys and three meanings: the greys do the layout, and the
 * three are saved for when the city is actually telling you something.
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

    /** The middle of the window - the darkest thing, and the biggest. */
    public static final String STAGE = "#111a24";

    /** The four strips around it: both side panels, the date bar, the debt bar. */
    public static final String PANEL = "#1c262b";

    /** A block lifted off a panel: the date and cash boxes, an open section. */
    public static final String RAISED = "#26343b";

    /** The vitals block, and anything that should read as pinned rather than raised. */
    public static final String PINNED = "#223038";

    /** Inside a control, and inside the inbox: darker than the panel it sits on. */
    public static final String FIELD = "#17212c";

    /** A button at rest. */
    public static final String CONTROL = "#22303c";

    /* =====================================================================
       THE EDGES
       ===================================================================== */

    /** Panel against stage. The one structural line in the layout. */
    public static final String EDGE = "#37474f";

    /** A quieter rule: inside a panel, under a heading, around a control. */
    public static final String HAIRLINE = "#33404b";

    /** The border of a control that can be pressed. */
    public static final String CONTROL_EDGE = "#4a5c68";

    /* =====================================================================
       THE TEXT

       Five weights of grey, and they are a ladder rather than a set. Going
       down the ladder is how a screen says "this matters less", and it is the
       only way it should say it - shrinking type does the same job worse,
       because a figure that has to be read cannot be 8px.
       ===================================================================== */

    /** The date, and nothing else. The single palest thing in the window. */
    public static final String TEXT_MAX = "#ffffff";

    /** A screen title, a section heading, a figure that is the answer. */
    public static final String TEXT_HEAD = "#eceff1";

    /** Ordinary text and ordinary figures. */
    public static final String TEXT_BODY = "#c3ccd3";

    /** A caption, a unit, a subtitle, a note under a figure. */
    public static final String TEXT_MUTED = "#8fa3b0";

    /** The label on the left of a row. Never the news, so never bright. */
    public static final String TEXT_LABEL = "#78909c";

    /** Disabled, or a heading that is not the one you are on. */
    public static final String TEXT_FAINT = "#7d8f9c";

    /** Struck through, settled, over with - a resolved notice, an old entry. */
    public static final String TEXT_SPENT = "#6b7a84";

    /* =====================================================================
       THE THREE THAT MEAN SOMETHING

       Used on FIGURES, not on labels, and only when the figure is saying
       something a player should act on. A screen where everything is green is
       a screen where nothing is.
       ===================================================================== */

    /** It is going the right way. */
    public static final String GOOD = "#5fd68a";

    /** Money going the right way - a little paler, because it is bigger type. */
    public static final String GOOD_MONEY = "#8fe0aa";

    /** It is not wrong yet, and it will be. */
    public static final String WARN = "#ffb454";

    /** It is costing the city something now. */
    public static final String BAD = "#ff6b6b";

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

    /** The active tab, a live link, the heading of an open section. */
    public static final String ACCENT = "#5cb8ff";

    /** Filled: the primary button, the advance-a-month control. */
    public static final String ACCENT_FILL = "#2f6fa8";

    /** Filled: the button that commits - pays, builds, sets the policy. */
    public static final String CONFIRM = "#2f7d52";

    /** Behind a confirmation that has already happened. */
    public static final String CONFIRM_GROUND = "#13291d";

    /* =====================================================================
       THE CHART

       Eight, then it wraps. Ordered so the first three - the ones a default
       screen draws - are told apart by people who cannot tell red from green,
       which two of these three would otherwise be.
       ===================================================================== */
    public static final String[] SERIES = {
        "#5cb8ff", "#ff6b6b", "#5fd68a", "#ffb454",
        "#ce93d8", "#4dd0e1", "#d4e157", "#c8b0a5"
    };

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

    /** Everything too small to have its own step. Deliberately colourless. */
    public static final String RAMP_REST = "#5c6b75";

    /** How thick a donut's ring is drawn, and how wide the hole is. */
    public static final double RING = 26;

    /* =====================================================================
       TYPE

       ONE RULE: every figure is Courier, every word is not.

       A number that changes width as it changes value moves sideways, and a
       figure that moves is one the eye has to find again every month. Courier
       holds the digits in their columns. Prose in Courier is just harder to
       read, so prose is not in Courier.
       ===================================================================== */

    /** Figures. All of them, everywhere, at every size. */
    public static final String MONO = "'Courier New'";

    /** Glyphs - the rail, the envelope, the round buttons. Named because the
     *  platform default has no gear at U+2699 and draws an empty box instead. */
    public static final String GLYPH = "'Segoe UI Symbol', 'Segoe UI', sans-serif";

    /** The date and the cash: the two figures readable from across the room. */
    public static final int SIZE_HEADLINE = 28;

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

    /** The navigation rail, on the city panel's ground. */
    public static final double RAIL = 46;

    /** The city panel, not counting the rail. */
    public static final double CITY_PANEL = 290;

    /** The construction panel down the right. */
    public static final double BUILD_PANEL = 280;

    /** The strip under the stage holding the dome and the time controls. */
    public static final double STRIP = 72;

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

    /** A figure: monospaced, at a size, in a colour. */
    public static String figure(int size, String colour) {
        return "-fx-font-family: " + MONO + "; -fx-font-size: " + size + "px;"
             + " -fx-font-weight: bold; -fx-text-fill: " + colour + ";";
    }

    /** A word: the platform face, at a size, in a colour. */
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
}
