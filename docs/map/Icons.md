# Icons.java - 156 lines · 1 methods · 13 constants · interface

`ham/citybuildersim/ui/Icons.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> The rail's icons, as vector outlines.
> 
> WHY PATHS AND NOT PICTURES
> 
> There is still no image file anywhere in this project, and these did not add
> one. A PNG would have to be packaged into the jar, found at runtime, shipped
> at two or three sizes for different screens, and re-exported in a second
> colour for the active tab. An SVG path is a string: it draws crisp at any
> size, it takes its colour from whoever draws it, and it cannot fail to load.
> 
> WHAT REPLACED WHAT
> 
> The rail used geometric glyphs from the Unicode block - and the trouble with
> picking eleven marks out of one block is that they all look alike. Three of
> them were diamonds and four were filled squares, so the rail was learned by
> POSITION rather than read, which is fine for the person who built the game
> and useless for anybody in their first ten minutes of it.
> 
> These are eleven different objects: a building, a plot of land, a group of
> people, a heartbeat, a factory, a parliament, a coin, a globe, a set of
> scales, a graph, a gear. Nobody has to learn them.
> 
> SOURCE AND LICENCE
> 
> Lucide (lucide.dev), ISC licence - free to use in a commercial game, no
> attribution required in the product. Each icon is drawn on a 24x24 grid with
> a 2px stroke, round caps and round joins, and is STROKED rather than filled:
> see UserInterface.railButton(), which sets fill to null.
> 
> MULTI-PART ICONS ARE ONE STRING. Lucide draws several of these with a mix of
> <path>, <circle> and <rect> elements. JavaFX SVGPath takes a single path, so
> the parts are concatenated - every "M" starts a fresh subpath and all of them
> get stroked - and the circles and rectangles are written out as arcs, because
> SVGPath has no notion of either.

**Used by (1):** [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 46 | `Icons.BUILD` | `"M12 10h.01 M12 14h.01 M12 6h.01 M16 10h.01 M16 14h.01 M16 6h.01" + "M8 10h.0...` | A building. |
| 63 | `Icons.LAND` | `"M2 4 a2 2 0 1 0 4 0 a2 2 0 1 0 -4 0" + "M14 5 l3-3 3 3 M14 10 l3-3 3 3 M17 1...` | A tent and trees - ground, rather than a map of it. |
| 68 | `Icons.POPULATION` | `"M17 21a5 5 0 0 0-10 0" + "M22 10.5a3.5 3.5 0 0 0-5.507-2.868" + "M7.507 7.63...` | Three people, one in front. |
| 77 | `Icons.SERVICES` | `"M2 9.5a5.5 5.5 0 0 1 9.591-3.676.56.56 0 0 0.818 0A5.49 5.49 0 0 1 22 9.5" +...` | A heart with a pulse through it. |
| 83 | `Icons.SECTOR` | `"M12 16h.01 M16 16h.01 M8 16h.01" + "M3 19a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V8.5...` | A factory. |
| 90 | `Icons.GOVERNMENT` | `"M10 18v-7 M14 18v-7 M18 18v-7 M6 18v-7 M3 22h18" + "M11.119 2.205a2 2 0 0 1 ...` | A parliament, columns and all. |
| 95 | `Icons.FINANCES` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M16 8h-6a2 2 0 1 0 0 4h4a2 2 ...` | A coin with a dollar in it. |
| 111 | `Icons.BANK` | `"M3 22h18 M4 18v-7 M9 18v-7 M15 18v-7 M20 18v-7" + "M2 18h20" + "M11.5 2.4a1 ...` | A bank: a pediment on columns, with a doorway. |
| 124 | `Icons.INFRASTRUCTURE` | `"M3 19 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 0" + "M15 5 a3 3 0 1 0 6 0 a3 3 0 1 0 -6 ...` | A route: two waypoints and the road that winds between them. |
| 130 | `Icons.TRADE` | `"M2 12 a10 10 0 1 0 20 0 a10 10 0 1 0 -20 0" + "M12 2a14.5 14.5 0 0 0 0 20 14...` | A globe. |
| 136 | `Icons.POLICY` | `"M12 3v18 M7 21h10" + "M3 7h1a17 17 0 0 0 8-2 17 17 0 0 0 8 2h1" + "M19 8 l3 ...` | Scales. |
| 143 | `Icons.REPORTS` | `"M3 3v16a2 2 0 0 0 2 2h16" + "M19 9 l-5 5 -4-4 -3 3"` | A line on axes. |
| 148 | `Icons.SETTINGS` | `"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915" + "a2.34 2...` | A gear. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 41 | 116 | **type** `public final class Icons` | The rail's icons, as vector outlines. |
| 43 | 1 | `private Icons()` |  |

