# IslandChip.java - 48 lines · 2 methods · 2 constants · interface

`ham/citybuildersim/ui/IslandChip.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The chip the map draws on a landmass no port links: its lines of amber mono caption on a dark rounded plate the width of
> the longer line (MT1, BK1-L).
> 
> WHY THIS EXISTS. The chip was one line of some 70 characters ("Island 2 · 5.37 km² · not linked: needs a port here (none
> has one)") whose only ground was a 3 px dark stroke, drawn over the landmass's own amber hatch and tint, so over busy
> ground it was hard to read. It is a class of its own, public, so tools.ChipRender draws the very same plate over a
> hatch for review; MapView.islandChips() decides where and what.

**Uses:** [Palette](Palette.md) (2)

**Used by (2):** [ChipRender](ChipRender.md), [MapView](MapView.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 23 | `IslandChip.PAD_X` | `6, PAD_Y = 3, LINE_H = 12, ARC = 7` | The plate's padding either side and above and below, the height of a line, and its corner. |
| 26 | `IslandChip.PLATE` | `Color.rgb(11, 17, 24, 0.9), EDGE = Color.web(Palette.WARN, 0.45)` | The plate (the stage's dark, nearly opaque) and its hairline edge in the warning amber the text is in. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 31 | **type** `public final class IslandChip` | The chip the map draws on a landmass no port links: its lines of amber mono caption on a dark rounded plate the width of the longer line (MT1, BK1-L). |
| 20 | 1 | `private IslandChip()` |  |
| 32 | 16 | `public static void draw(GraphicsContext gc, Font font, String[] lines, double x, double y, double viewWidth, ToDoubleFunction<S...` | One chip's plate and lines centred on (x, y), kept inside a view `viewWidth` wide; `width` measures a line in `font`. |

