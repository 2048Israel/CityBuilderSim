package ham.citybuildersim;

/**
 * The picture of one work on the shore at the near view (HB2, 2026-11-09): its
 * open cargo yard by what it handles, the stock on it at a level, and the quay
 * tracks and wagons of a terminal the railway reaches - ARGB pixels at PX a
 * plot, with no toolkit, which the map draws over the work's box from
 * MapFrame.DETAIL_FROM in.
 *
 * WHY: until HB2 a terminal was the tile's flat box at every zoom. HB1's
 * renders settled the near look (the open yard, its render (b)) and Jerus
 * picked HB2's three details from renders (docs/queue/HB2.md: 1a the yard
 * fills, 2a tracks on the quay, 3a a pixel-line crane). The yard is a picture
 * of its own and not tile paint because the tile draws from 3.2 px a plot and
 * his rule is detail from DETAIL_FROM (the 500 m bar), because a level that
 * steps with the month would dirty tiles every month, and because the look was
 * drawn at 8 px a plot where the tile is 4 below zoom 6. The reference look is
 * docs/queue/HB2/RenderHB2.java.
 */
public final class HarbourYard {

    private HarbourYard() { }

    /** How many pixels a plot the picture is painted at: the near view's tile at its finest, and what the renders were drawn at. */
    public static final int PX = 8;

    /** How many steps of stock a yard shows above empty: level 0 is bare ground and its sheds, LEVELS a full yard. */
    public static final int LEVELS = 8;

    /** What a work on the shore handles, which is what its yard holds: boxes, heaps, tanks behind a tanker berth, crates and sheds, or a tank farm's tanks. */
    public enum Kind { CONTAINER, DRY_BULK, LIQUID, GENERAL, TANKS }

    /*
     * A picture's rail bits. LOW is the end of the box at the smaller world coordinate along the shore (west for a work whose sea side is north or
     * south, north for one whose sea side is east or west), HIGH the other end.
     */

    /** The quay track: one track along the box's whole sea-side row (and at a large berth a second, a loop off it). */
    public static final int RAIL = 1;
    /** The quay track runs on past the box's LOW end, to a neighbour on the same quay line; without it the track ends short of that end at a buffer stop. */
    public static final int OPEN_LOW = 2;
    /** ...and past its HIGH end. */
    public static final int OPEN_HIGH = 4;
    /** The lead: a track from the box's land edge up its LOW end's column to the quay track, where the railway's quay spur comes in from behind. */
    public static final int LEAD_LOW = 8;
    /** ...up its HIGH end's column. */
    public static final int LEAD_HIGH = 16;
    /** A train stands on the quay track. */
    public static final int TRAIN = 32;
}
