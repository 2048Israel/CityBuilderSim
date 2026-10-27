## changelog
### {next} — ZT1, one palette for zones — 2026-10-27, ready, see docs/queue/ZT1-notes.md
Jerus's call (2026-10-18, "rec all"): a zone is one colour on the map and on Policy > Zoning. Zoning coloured a zone by its place in the list, eight colours cycled, so a sector's colour changed when an earlier zone came off and was never the map's.
Now Zoning paints a zone in its sector's colour, the map's own (`LandMap.zoneColourHex`), and the old table is gone. Checked on Zoning's own ground (built grey, the land office's pink, the forest's three greens), two sector colours were lost against it:
Luxury Retail's pink was the land office's pink and Forestry's green the open forest's; both moved in `LandMap.ZONE_COLOURS`, so the map's tint moves with them. `ZONE_GROUND_APART` (20, CIE76) is the least any sector's colour may differ from each of the five.
`ZoningViewCheck` 10 holds it: every sector's colour on the screen is the map's, a later zone keeps its colour when an earlier one is removed, the map's legend and the screen agree, and none is within the dial of the ground.
No save or model change; the default playtest cannot move.
## pending
| ZT1-C | ZT1 ({next}): look at it on the PC: set two zones on Policy > Zoning, see the same colour on the ground bar and rows and in the map's tint and legend, remove the first and see the second keep its colour; and that a zone's part reads apart from the forest's greens and built ground's grey | his check | [ZT1 notes](docs/queue/ZT1-notes.md) |
