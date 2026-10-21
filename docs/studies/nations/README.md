# nations/ - beside spec-nations.md

The boards, each one self-contained (its images inlined), drawn in the
game's palette (`ui/Palette`); open them in a browser:

- `1-the-world-map.html`: the expanded map at the far view over the North
  Atlantic: nations as dots and labels on their land, this month's trade as
  routes, the borders, the three chips (Nations, Trade routes, Borders), the
  head's POPULATION and GDP tiles, the legend that folds, Canada's card. The
  chips and the legend's head are live.
- `2-who-buys-your-wood.html`: Trade & the world > Partners: who we trade
  with, and for lumber where the next m³ goes (the export curve from
  `NationsModel`), the buyers at what we sell, the band and what moves it.
- `3-the-vault-in-eight.html`: Trade & the world > The reserves with the
  vault in eight moneys, the reserve mix and the exchange's money picker;
  the land office's card for an offer across the border at Basel.
- `4-found-the-world.html`: the founding screen's World step: Historical or
  Random, the seed, the dollar's inflation chips, and the path's chart (the
  two cards are live).

Their sources and the tools:

- `src-1-...html` to `src-4-...html`: the boards as written, with `{{BASE}}`
  where the map's ground goes and `{{BORDERS_SVG}}` where its borders go;
  `build-pages.py` inlines them and writes the boards (`python
  build-pages.py`, here).
- `world-base.png`, `world-nations.json`: the map board's ground and its
  nations' label places, drawn by `RenderWorld.java` from earth-1.bin's far
  layers in TileRaster's colours, in EarthProj's projection on Halifax. The
  label places are approximate (earth-1.bin has no borders, so a land pixel
  takes the country of its nearest place). To run it (Windows, a compiled
  worktree):

      javac -cp target/classes -d <out> docs/studies/nations/RenderWorld.java
      java -Djava.awt.headless=true -cp "<out>;target/classes" ham.citybuildersim.RenderWorld <earth-1.bin> docs/studies/nations

- `world-borders.txt`: the map board's borders, Natural Earth's admin-0
  countries at 1:10m (public domain), the edges two countries share,
  projected the same way, by `natural-earth-borders.py` (`python -I
  natural-earth-borders.py <ne_10m_admin_0_countries.geojson>
  world-borders.txt`; the GeoJSON is not kept here).
- `places-by-country.tsv`: earth-1.bin's 7,342 places by country (228
  names), each country's largest place and its people.

- `NationsModel.java`: the prototype of the world market (spec-nations §4):
  196 nations at their largest place (`places-by-country.tsv`, the Earth
  file's places by country), fifty synthetic goods with lumber shaped like
  the world's, Jerus's partner rule against the full table, the January
  solve timed, the city's monthly fill against a re-solve. Its output is
  `nations-model-output.txt`. To run it (Java 21, no game classes):

      java -Xmx2g -Drule=producers NationsModel.java places-by-country.tsv

  `-Drule=nations` takes Jerus's "10 nearest" as nations rather than makers.

Nothing in the game or the harnesses reads this folder.
