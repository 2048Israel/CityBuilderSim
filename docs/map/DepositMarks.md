# DepositMarks.java - 164 lines · 12 methods · 6 constants · model

`ham/citybuildersim/DepositMarks.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The deposits the land office's map marks when it is zoomed far out: which to find for a view, which of them to draw, and the mark each is
> drawn with - what ui/MapView draws its icons by and MapCheck holds, without the toolkit.
> 
> WHY THIS EXISTS (LM1, 2026-11-11; Jerus, 2026-11-02: "i know its for optimization... but that makes it super hard to find resources").
> The map shows a deposit three ways: at L0 the tiles shade its sites; at L1 and L2 a dot a field in and about the city's land and offers
> (MapView.deposits()); and at FAR - an L2 view holding more than MapFrame.NEAR_TILES_MOST tiles, which at the expanded map's size is a
> 2 km scale bar of 70 to 75 px or less, a plot a pixel or less - nothing at all. A player scouting round his city, the very zoom at which
> he looks for iron or oil, saw no deposit until he zoomed in a notch. So at FAR the map draws one small icon a deposit at its centre
> plot, in the resource's colour with its mark in it (Fe, O, St, C, Cu, U, Au): a few dozen to a few hundred draws, never the tiles' shading.
> Finding the deposits asks the world for every cell the view touches, so it is done for a box twice the view (Search) and again only when
> the view leaves it, the scale halves or doubles, or the land changes - a drag or a wheel notch costs the thinning alone; and the
> thinning keeps one icon in each ICON_PX square, the one with the most sites, so a far zoom-out never piles thousands of icons on
> the screen. Past ICON_CELLS_MOST world cells across, the view is wider than a scouting zoom (one to four cells) and no icon is drawn.
> 
> The squares of the thinning are ICON_PX pixels across on the screen but lie on the ground, not on the view (squareOf()): a drag
> moves every deposit and every square with it, so the icons kept do not change while the view pans.

**Uses:** [Deposit](Deposit.md) (10), [Resource](Resource.md) (4), [MapFrame](MapFrame.md) (4), [CityLand](CityLand.md) (3), [World](World.md) (1), [LandMap](LandMap.md) (1)

**Used by (2):** [MapCheck](MapCheck.md), [MapView](MapView.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 29 | `DepositMarks.ICON_PX` | `12` | An icon's side: 12 px - a disc in the resource's colour with its mark in the caption font, two letters at most, and the width of a thinning square. |
| 32 | `DepositMarks.ICON_CELLS_MOST` | `16` | The widest view that draws icons, in world cells (World.CELL plots, about 61 km): 16 - a city's scouting zoom is one to four cells, and past this the view is a continent and every deposit would be a speck. |
| 35 | `DepositMarks.DEPOSITS_MOST` | `4000` | The most deposits one search holds of each resource: 4,000 (MapView's FIELDS_MOST) - far more than a scouting view holds, and counted by resource so that iron, the commonest by far, never crowds oil or coal out of a w... |
| 38 | `DepositMarks.SEARCH_TIMES` | `2` | The box a search covers is this many times the view's size each way: 2 - a drag of half a screen finds the deposits already in hand. |
| 41 | `DepositMarks.RESCALE_MOST` | `2` | A view whose scale differs from the search's by more than this factor searches again: 2 - zoomed in far enough, the list is mostly off the screen; zoomed out, the box is too small to find all. |
| 44 | `DepositMarks.LIGHT_ABOVE` | `0.5` | A colour whose luminance (Rec. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 129 | `private boolean have` |  |
| 130 | `private double x0, y0, x1, y1, scale` |  |
| 131 | `private long key` |  |
| 132 | `private List<Deposit> found` |  |
| 133 | `private int searches` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 139 | **type** `public final class DepositMarks` | The deposits the land office's map marks when it is zoomed far out: which to find for a view, which of them to draw, and the mark each is drawn with - what ui/MapView draws its icons by and MapCheck holds, without the... |
| 46 | 1 | `private DepositMarks()` |  |
| 49 | 12 | `public static String mark(Resource r)` | The mark a resource's icon carries: Iron "Fe", Oil "O", Stone "St", Coal "C", Copper "Cu", Uranium "U", Gold "Au"; empty for forest, which has no fields. |
| 63 | 5 | `public static boolean darkInk(Resource r)` | Whether the mark is written dark (the light colours: stone, uranium, copper) rather than white (iron, oil, coal): by the luminance of Resource.colour(). |
| 70 | 3 | `public static boolean tooWide(double x0, double y0, double x1, double y1)` | Whether a box of plots is wider than ICON_CELLS_MOST cells either way. |
| 75 | 3 | `public static boolean tooWide(MapFrame f)` | ...and whether the view is: the plots it holds across and down, as a box. |
| 84 | 4 | `public static List<Deposit> inBox(CityLand land, double x0, double y0, double x1, double y1)` | Every field of every resource that lies in fields (inFields()) whose centre is in a box of plots, as the city sees them (LandMap.fieldsIn()), resource by resource in Resource's order, at most DEPOSITS_MOST of each; em... |
| 89 | 8 | `private static List<Deposit> find(CityLand land, double x0, double y0, double x1, double y1)` |  |
| 99 | 4 | `public static long squareOf(Deposit d, MapFrame f)` | The thinning's square a deposit's centre falls in on a view's scale, ICON_PX pixels across, on the ground (see the class's note): one number a square. |
| 109 | 13 | `public static List<Deposit> thin(List<Deposit> found, MapFrame f)` | The deposits to draw in a view, from those found: each mapped to the screen through the frame, those whose icon is wholly off the screen left out, and in each square (squareOf()) the one with the most sites kept - on ... |
| 128 | 36 | **type** `public static final class Search` | The deposits found for a view, kept: a search is made for a box SEARCH_TIMES the view's size and made again only when the view leaves that box, its scale is more than RESCALE_MOST times or under 1/RESCALE_MOST of the ... |
| 136 | 18 | `public List<Deposit> forView(CityLand land, long key, MapFrame f)` _(in DepositMarks.Search)_ | The deposits in the view's box and about it, found afresh only when the view or the land has moved off the last search; empty past ICON_CELLS_MOST cells (nothing is searched). |
| 156 | 1 | `public int searches()` _(in DepositMarks.Search)_ | How many searches it has made. |
| 159 | 4 | `public void forget()` _(in DepositMarks.Search)_ | Forgets the last search (a new game, a replay): the next view searches. |

