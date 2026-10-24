# ET1 - Earth 2 in the game, and drawn (0.8.40, 2026-10-24)

The brief: `docs/studies/spec-earth2-terrain.md` §10's ET1 row, with §3.1-3.4, §6.1, §7 and §9;
Jerus's decisions of 2026-10-12 ("all rec", then 15″ for D3). Five slices, each written by a
Sonnet subagent and reviewed and committed here: A the file, B1 the shallow sea and depth, B2
the relief plane, C the books and the founding card, D the drawing.

## The file

- `data/earth-2.bin` on the PC is 281,761,695 bytes, SHA-256 `c897b242…6ae33b`, the same in
  `data/earth-2.sha256` and `tools/earth/earth-2.sha256`. That is the 15″ release (§2.8, D3). The
  study's 135,965,097 bytes is the first bake at 30″, superseded the same day; §2.7's table still
  shows it.
- `EarthData.EARTH_2`, `DATA_FILE` `data/earth-2.bin`; `identity()` takes its prefix from the
  meta's name, so earth-1.bin still opens as earth-1. Readers for slope, slope.est, shallow, depth,
  landmass (a palette layer, kept as bytes) and landmasses.
- EarthCheck 11 compares earth-1's ten layers in both files: 324,360 index entries, 57,731 blobs,
  52,620,206 bytes, all identical. The new fixture is `earth-2-fixture.{bin,expect}` (533 KB),
  cut by `tools/earth/fixture.py`; the earth-1 fixture is gone.
- `get-earth.bat`, `Play CityBuilderSim.bat` and `Build EXE.bat` fetch and carry earth-2;
  `get-earth.bat nopause earth-1` still fetches earth-1 for `bake-earth-2.bat`.

## The ground

- `World.SHALLOW` (7; `CLASSES` 8) and `World.salt()`: every "is this the sea?" asks it, so ports,
  the coast, sea routes, sea fields and prices are as before. Drawn where the shallow share,
  weighted by the sea share, beats its own ranked noise.
- The relief plane: `World.FLAT`, `FOOTHILL`, `STEEP`, `VERY_STEEP`; `reliefAt`, `tileRelief`,
  `regionRelief`, and a relief array beside `farTerrain`. One ranked noise (lattices 240 m to
  1.9 km) against the land-weighted shares; ice wins, water has none, cover byte for byte as
  before. `World.buildable()` and `workable()` exist; nothing calls them until ET2.
- Depth: `EarthTerrain.depthAt()` reads the depth layer, at least 1 m on sea. `SHELF_WIDTH_M`,
  `measureShelfWidthM`, `shelfSeaSamples` and `coastDistanceM` are gone. `Oil.isShallow` is
  `canTakePlatform` (150 m unchanged). Off Rotterdam 22 / 31 m at 40 / 80 km; off Valparaíso
  640 / 750+ m at 20 / 40 km.

## The books and the card

- Each holding, offer and the centre keeps its dry ground by relief and its shallow sea
  (`CityLand.RELIEF_*`), saved as `landCentreRelief`, `landHoldingsRelief`, `landOffersRelief`.
  No row is widened; a missing key reads flat. `SAVE_FORMAT` stays 45 (the study's "40" is stale):
  no old save loads wrongly, and every earth-1 city is refused by its identity.
- The founding card: "Dry ground within 5 km: flat 48% · foothill 43% · steep 9%" (Medellín);
  "Shallow water 0.5 km away · the sea is up to 19 m deep within 5 km" (Halifax); "Its landmass:
  Charlottetown's, 5,611 km² (id 101)" (Prince Edward Island). The snap and the refusal on
  buildable ground are ET2's (D21).

## The drawing

- `TileRaster.RELIEF_GREY` foothill c8c3b8, steep 9b968c, very steep 6c6862; forest blended toward
  its grey (`FOREST_MIX` 0.5 / 0.5 / 0.6, D5); ice white; shallow 4f87b5. A generated world paints
  as before. Hover and legend name the grade and the sea's depth. The globe's texel is a code,
  relief × 8 + class.
- Renders in `docs/queue/ET1/` (`tools.ReliefRenders`): Medellín at plots and far, Halifax's
  Narrows, Everest far, the globe over the Andes and over the Himalaya.
- Budgets: a tile 0.066 ms (0.3), a far node about 6 ms (80), a picture frame 18 ms (100).

## Checks and traces

- `./batch-check.sh` with earth-2 named: 137 of 138; MapCheck 8 ("its streets one network") is
  main's known red (FW11), as in the baseline.
- The default playtest's trace: byte-identical to main's but the run-time line, so no ensemble.
- `LongPlaytest --earth rotterdam`: byte-identical too, but the data's name (earth-2, c897b242)
  and the run-time. The run never drills at sea, so real depth moves nothing, and Rotterdam is
  flat. Both runs end with main's two map findings ("a building on the map moved", "an old works
  moved"), unchanged. A hilly Earth run (`--earth medellin`) comes with ET2.

## Calls for Jerus (ET1-C, under Pending)

1. **The cache.** A tile with every layer inflated is 296 KB, so `EarthData.CACHE_BYTES` (10 MB)
   keeps 33 tiles where earth-1 kept 83, about five cities. Recommend 24 MB.
2. **Landmass names.** The data names a landmass by its most populous place: the Americas read
   "New York's", Afro-Eurasia "Mumbai's". Recommend naming the six continents by hand.
3. **"Ice" or "Snow and ice".** The legend still says "Ice"; the study's words are "Snow and ice".

## Found on the way

- The fixture's 4′ bands do not hold Medellín (adding its band moved GlobeCheck 4's whole-world
  fingerprint), so Medellín's far-rule shares are checked on the full file only.
- A deep (SALT) plot in Bedford Basin can read "about 20 m here": the depth layer's 1′ cell mean
  and the 15″ shallow share are two readings of GEBCO and need not agree at a plot (§2.5).
