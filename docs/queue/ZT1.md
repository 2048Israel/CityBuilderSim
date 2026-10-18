# ZT1 - one palette for zones

Jerus, 2026-10-18 ("rec all" on the open calls): a zone is one colour on the
map and on Policy > Zoning. The I14 docs pass found two palettes.

## What is there

- **The map** tints a zone's districts by its *sector*:
  `LandMap.ZONE_COLOURS`, 24 bright ARGB ints in `Sectors.KEYS` order, read
  through `LandMap`'s zone-colour lookup at `ZONE_ALPHA` (22%). Its
  javadoc gives each industry its own where the map has one (Forestry's green,
  Quarrying's stone, Copper's teal).
- **Policy > Zoning** colours a zone by its *place in the list*:
  `ui/ZoningScreen.ZONE_COLOURS`, eight `Palette` names cycled by index. So
  a sector's colour there changes when a zone before it is removed, and it is
  never the map's.

## What to do

- Zoning colours each zone by its sector, read from the model's palette
  (`LandMap`'s lookup, made public if it is not). The ui may read the model;
  the model never imports the ui. The map's palette is the one kept, because
  it is per sector and therefore stable.
- Check the colours on Zoning's own ground: the land office's pink, the three
  forest greens and built ground's grey (`BUILT_COLOUR`). Where a sector's
  colour cannot be told apart from that ground, change it in
  `LandMap.ZONE_COLOURS`, so both screens move together.
- A harness: for every sector, Zoning's colour for its zone equals the map's
  (`LandMap` against `ZoningScreen`'s lookup, with no JavaFX needed: compare
  the ARGB values). Removing an earlier zone leaves a later zone's colour
  unchanged.

No save change. A screen batch, so it is checked by eye on the PC: set two
zones, see the same colour on Zoning and on the map's tint, then remove the
first.
