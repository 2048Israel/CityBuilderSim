package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The deposits the land office's map marks when it is zoomed far out: which to find for a view, which of them to draw, and the mark each is
 * drawn with - what ui/MapView draws its icons by and MapCheck holds, without the toolkit.
 *
 * WHY THIS EXISTS (LM1, 2026-11-11; Jerus, 2026-11-02: "i know its for optimization... but that makes it super hard to find resources").
 * The map shows a deposit three ways: at L0 the tiles shade its sites; at L1 and L2 a dot a field in and about the city's land and offers
 * (MapView.deposits()); and at FAR - an L2 view holding more than MapFrame.NEAR_TILES_MOST tiles, which at the expanded map's size is a
 * 2 km scale bar of 70 to 75 px or less, a plot a pixel or less - nothing at all. A player scouting round his city, the very zoom at which
 * he looks for iron or oil, saw no deposit until he zoomed in a notch. So at FAR the map draws one small icon a deposit at its centre
 * plot, in the resource's colour with its mark in it (Fe, O, St, C, Cu, U): a few dozen to a few hundred draws, never the tiles' shading.
 * Finding the deposits asks the world for every cell the view touches, so it is done for a box twice the view (Search) and again only when
 * the view leaves it, the scale halves or doubles, or the land changes - a drag or a wheel notch costs the thinning alone; and the
 * thinning keeps one icon in each ICON_PX square, the one with the most sites, so a far zoom-out never piles thousands of icons on
 * the screen. Past ICON_CELLS_MOST world cells across, the view is wider than a scouting zoom (one to four cells) and no icon is drawn.
 *
 * The squares of the thinning are ICON_PX pixels across on the screen but lie on the ground, not on the view (squareOf()): a drag
 * moves every deposit and every square with it, so the icons kept do not change while the view pans.
 */
public final class DepositMarks {

    /** An icon's side: 12 px - a disc in the resource's colour with its mark in the caption font, two letters at most, and the width of a thinning square. */
    public static final int ICON_PX = 12;

    /** The widest view that draws icons, in world cells (World.CELL plots, about 61 km): 16 - a city's scouting zoom is one to four cells, and past this the view is a continent and every deposit would be a speck. */
    public static final int ICON_CELLS_MOST = 16;

    /** The most deposits one search holds of each resource: 4,000 (MapView's FIELDS_MOST) - far more than a scouting view holds, and counted by resource so that iron, the commonest by far, never crowds oil or coal out of a wide view. */
    public static final int DEPOSITS_MOST = 4000;

    /** The box a search covers is this many times the view's size each way: 2 - a drag of half a screen finds the deposits already in hand. */
    public static final int SEARCH_TIMES = 2;

    /** A view whose scale differs from the search's by more than this factor searches again: 2 - zoomed in far enough, the list is mostly off the screen; zoomed out, the box is too small to find all. */
    public static final double RESCALE_MOST = 2;

    /** A colour whose luminance (Rec. 709, 0 to 1) is above this takes dark text, below it white: 0.5. */
    public static final double LIGHT_ABOVE = 0.5;

    private DepositMarks() { }

    /** The mark a resource's icon carries: Iron "Fe", Oil "O", Stone "St", Coal "C", Copper "Cu", Uranium "U"; empty for forest, which has no fields. */
    public static String mark(Resource r) {
        switch (r) {
            case IRON: return "Fe";
            case OIL: return "O";
            case STONE: return "St";
            case COAL: return "C";
            case COPPER: return "Cu";
            case URANIUM: return "U";
            default: return "";
        }
    }

    /** Whether the mark is written dark (the light colours: stone, uranium, copper) rather than white (iron, oil, coal): by the luminance of Resource.colour(). */
    public static boolean darkInk(Resource r) {
        int c = r.colour();
        double lum = (0.2126 * ((c >> 16) & 0xff) + 0.7152 * ((c >> 8) & 0xff) + 0.0722 * (c & 0xff)) / 255.0;
        return lum > LIGHT_ABOVE;
    }

    /** Whether a box of plots is wider than ICON_CELLS_MOST cells either way. */
    public static boolean tooWide(double x0, double y0, double x1, double y1) {
        return Math.max(x1 - x0, y1 - y0) > (double) ICON_CELLS_MOST * World.CELL;
    }

    /** ...and whether the view is: the plots it holds across and down, as a box. */
    public static boolean tooWide(MapFrame f) {
        return tooWide(f.plotX(0), f.plotY(0), f.plotX(f.width()), f.plotY(f.height()));
    }

    /**
     * Every field of every resource that lies in fields (inFields()) whose centre is in a box of plots, as the city sees them
     * (LandMap.fieldsIn()), resource by resource in Resource's order, at most DEPOSITS_MOST of each; empty when the box is wider than
     * ICON_CELLS_MOST cells.
     */
    public static List<Deposit> inBox(CityLand land, double x0, double y0, double x1, double y1) {
        if (tooWide(x0, y0, x1, y1)) return new ArrayList<>();
        return find(land, x0, y0, x1, y1);
    }

    private static List<Deposit> find(CityLand land, double x0, double y0, double x1, double y1) {
        List<Deposit> all = new ArrayList<>();
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            all.addAll(LandMap.fieldsIn(land, r, x0, y0, x1, y1, DEPOSITS_MOST));
        }
        return all;
    }

    /** The thinning's square a deposit's centre falls in on a view's scale, ICON_PX pixels across, on the ground (see the class's note): one number a square. */
    public static long squareOf(Deposit d, MapFrame f) {
        long sx = (long) Math.floor((d.x() + 0.5) * f.scale() / ICON_PX), sy = (long) Math.floor((d.y() + 0.5) * f.scale() / ICON_PX);
        return (sx << 32) ^ (sy & 0xffffffffL);
    }

    /**
     * The deposits to draw in a view, from those found: each mapped to the screen through the frame, those whose icon is wholly off the
     * screen left out, and in each square (squareOf()) the one with the most sites kept - on a tie the earlier of the list. The order is the
     * order each square was first met.
     */
    public static List<Deposit> thin(List<Deposit> found, MapFrame f) {
        Map<Long, Integer> at = new HashMap<>();
        List<Deposit> out = new ArrayList<>();
        double reach = ICON_PX / 2.0;
        for (Deposit d : found) {
            double x = f.screenX(d.x() + 0.5), y = f.screenY(d.y() + 0.5);
            if (x < -reach || y < -reach || x > f.width() + reach || y > f.height() + reach) continue;
            Integer i = at.putIfAbsent(squareOf(d, f), out.size());
            if (i == null) out.add(d);
            else if (d.sites() > out.get(i).sites()) out.set(i, d);
        }
        return out;
    }

    /**
     * The deposits found for a view, kept: a search is made for a box SEARCH_TIMES the view's size and made again only when the view
     * leaves that box, its scale is more than RESCALE_MOST times or under 1/RESCALE_MOST of the search's, or the land's key moves. The box
     * is not cut to the world: a view at the world's edge would never lie inside a box that is.
     */
    public static final class Search {
        private boolean have;
        private double x0, y0, x1, y1, scale;
        private long key;
        private List<Deposit> found = List.of();
        private int searches;

        /** The deposits in the view's box and about it, found afresh only when the view or the land has moved off the last search; empty past ICON_CELLS_MOST cells (nothing is searched). `key` changes whenever the land or its offers do. */
        public List<Deposit> forView(CityLand land, long key, MapFrame f) {
            if (tooWide(f)) return List.of();
            double vx0 = f.plotX(0), vy0 = f.plotY(0), vx1 = f.plotX(f.width()), vy1 = f.plotY(f.height());
            boolean held = have && key == this.key && vx0 >= x0 && vy0 >= y0 && vx1 <= x1 && vy1 <= y1
                    && f.scale() <= scale * RESCALE_MOST && f.scale() >= scale / RESCALE_MOST;
            if (held) return found;
            double w = (vx1 - vx0) * SEARCH_TIMES, h = (vy1 - vy0) * SEARCH_TIMES, cx = (vx0 + vx1) / 2, cy = (vy0 + vy1) / 2;
            x0 = cx - w / 2;
            y0 = cy - h / 2;
            x1 = cx + w / 2;
            y1 = cy + h / 2;
            scale = f.scale();
            this.key = key;
            found = find(land, x0, y0, x1, y1);
            have = true;
            searches++;
            return found;
        }

        /** How many searches it has made. */
        public int searches() { return searches; }

        /** Forgets the last search (a new game, a replay): the next view searches. */
        public void forget() {
            have = false;
            found = List.of();
        }
    }
}
