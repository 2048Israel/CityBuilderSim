# UX6 - the icon set: an icon and a colour per building

From UX1 (`docs/queue/UX1-notes.md`; mockups 9 and 10 in `docs/studies/ux1/mockups/`). UX1's brief: "the
build screen: ... an icon and colour per building"; the desk chat's idea: "one per building in today's
outline style, a base glyph per group plus a mark per building, coloured by area".

## What it builds

1. **An icon per building kind** (173 in `buildings.json` today), in `ui/Icons.java`'s style: Lucide-style
   outlines on the 24x24 grid, a 2 px stroke, stroked never filled. A base glyph per group (Food mills, Iron
   & Steel, Refining, Schools, Homes...) and, where a group has several kinds, a small mark per kind (a size
   step, a dot, a bar) so the kinds tell apart at 20 px. Today's 72 icons are kept and reused where they fit
   (INDUSTRY, MILL, FOOD, CAN, INGOT, ORE, GEAR, TANK, WELL, PLATFORM, TANKER, FLAME, TREES, PAPER, CRANE,
   LORRY...). Any new outline is drawn here or taken from Lucide (ISC, as the file's header says), never
   from another game.
2. **A colour per building**: a hue per family (UX7's five Industry families; each of the city's
   categories its area's colour) and a shade per kind within it, from `ui/Palette.java`'s families, all
   legible on PANEL and RAISED grounds.
3. **One place to ask**: `BuildingLook` (or a method on Icons and Palette) gives a building id's icon and
   colour; Build, the map card (UX5) and Construction read it. A building added later without one gets its
   group's base glyph and the group's colour.

## Files

`ui/Icons.java`, `ui/Palette.java`, a new `ui/BuildingLook.java`, `BuildingDataCheck.java` (or a new
IconsCheck). `buildings.json` is not changed (ids are permanent), unless a field for the group is needed.

## Size

Medium: one session, about 700 lines, most of it path data. The drawing suits one Opus session with a
picture of every icon at 20 and 40 px (a sheet rendered as UX1's mockups were, by Edge headless:
`docs/studies/ux1/mockups/lib/icons2js.py` reads Icons.java into the mockups' `lib/icons.js`).

## The guard

- **BuildingDataCheck** (or IconsCheck): every building in `buildings.json` has an icon and a colour; no two
  kinds in one group share both; every path parses as SVG path data (JavaFX's SVGPath), on the 24x24 grid.
- **UiSteadyCheck** and **KeptCheck** green; the sheet of every icon in the notes, at 20 and 40 px.

## Order and fit

After UX2 (it needs nothing of it, but its first reader is the frame's build screen); beside UX3, UX4, UX5.
Before UX7. BMD1 changes Build's lab descriptions, not icons.

## Not this batch's

The build screen's layout (UX7), the map card (UX5), the manual.
