# ZT1 - one palette for zones (0.8.59, 2026-10-27)

The batch's record beside its brief, `ZT1.md`. A screen batch: no save change, no
money, nothing the month reads. Run whole on Sonnet 5.5 at xhigh, Opus at the gate
only (BP1 item 7).

## 1. What was there

The map tinted a zone's districts by its sector (`LandMap.ZONE_COLOURS`, 24 colours
in `Sectors.KEYS` order); Policy > Zoning coloured a zone by its place in the list
(`ZoningScreen.ZONE_COLOURS`, eight `Palette` names cycled by index). Two palettes,
and on Zoning a sector's colour changed when an earlier zone came off.

## 2. What changed

- **Zoning reads the map's palette.** `ZoningScreen.ZONE_COLOURS` is gone;
  `zoneColour(sector)` returns `LandMap.zoneColourHex(sector)`, new beside the map's
  own `zoneColour`. The map's palette is the one kept, as the brief says: it is per
  sector, so a zone's colour no longer depends on which zones stand.
- **Two colours moved**, because the brief's check on Zoning's own ground found them
  lost against it. The ground is five colours: built ground's grey, the land office's
  pink and the forest's three greens (`ZoningScreen.BUILT_COLOUR`, `FREE_COLOUR`,
  `FOREST_*`). In CIE76 distance (`LandMap.colourGap`):
  - Luxury Retail's pink `f48fb1` was 11.0 from the land office's `f17cb0`; now
    `e91e63`, 36 from it.
  - Forestry's green `81c784` was 11.7 from the open forest's `9cc48a` and 17.7
    from the working forest's; now `4caf50`, 26 from the nearest.
  Every other sector was already at least 21.9 away (Coal Mining's blue-grey against
  built ground, the least). The map's tint moves with them, at 22% alpha.
- **The dial**: `LandMap.ZONE_GROUND_APART = 20` (CIE76 units, about nine
  just-noticeable steps), the least a zone's colour may differ from each of the five.
  Some pairs of the 24 sit as close as 5.5 apart (Retail and Food Processing's
  yellows); that is the map's old table and the brief asks only for the ground, so it
  is left.

## 3. The harness

`ZoningViewCheck` section 10, `--- one palette for zones ---`, on the interface bound
to the city with no window (`hubFlags`' binding), reaching the screen's own
`zoneColour` and constants by reflection: for every one of the 24 sectors Zoning's
colour is the map's, ARGB for ARGB; with Mining's zone before Forestry's, taking
Mining's off leaves Forestry's colour unchanged (the fixture asserts the list order
did change, so the old index colour would have moved); the map's legend entry for a
zone and the screen's colour for it are the same; no two sectors share a colour; and
every sector is at least `ZONE_GROUND_APART` from each of the five ground colours
(the least read: 21.9). The old tables fail the first and the last.

## 4. Checks

`--pick` (13 harnesses of 149) exits 3: MapCheck's recorded red only
(`docs/notes/gate.txt`), its one timing assertion SOFT beside another JVM. Earth
harnesses ran on `data/earth-2.bin`. No trace was compared: the change is two colour
tables and a lookup the month never reads, so the playtest is the same by
construction, and no ensemble.

## 5. Left for Jerus's eye (the PC)

Set two zones on Policy > Zoning and see the same colour on the ground bar and zone
rows and in the map's tint and legend, then remove the first zone and see the second
keep its colour. Watch the bar: a zone's part beside the forest's greens and built
ground's grey should read apart. Two sectors' tints are slightly brighter (Luxury
Retail) or deeper (Forestry) than before.
