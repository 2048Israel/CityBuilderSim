tools/world: the pipeline that bakes world-1.bin, the nations' world (batch WN0). FORMAT.txt is the file's contract.
Offline, pure Python 3.14 standard library, nothing installed. Run every script as `python -I <script> [args]` (-I keeps
the downloads' folder off sys.path); each puts its own folder on sys.path to import common.py and xlsx.py.
Paths: --sources (default C:/Users/Jerus/Documents/world-sources, the downloads, read-only) and --work (default
<sources>/work: geo/ the geography's files, econ/ the economy's, out/ the baked file). Set PYTHONIOENCODING=utf-8.

SOURCES (under --sources): naturalearth/ (v5.1.2: countries, boundary lines, populated places, ports), cerdi/ (sea
distances, xlsx), wdi/ (countries list), and the economy's: baci/ wpp/ pwt/ maddison/ bis/. ATTRIBUTION.txt has the credits.

THE ORDER (a stage's inputs are the files named; the geography and the economy are independent until the bake)
  1. geo.py [stage ...]     stages owners nations freight raster lines meta (default all, in that order; a stage needs
                            the one before: nations needs owners, freight nations). Whole run 18 s on 20 CPUs; the raster
                            alone 8 s (19 workers) or 36 s (--procs 1). Writes work/geo/: owners.tsv nations-geo.tsv
                            freight.bin owner.index owner.blobs lines.bin meta-geo.tsv. `raster --bands 141,142` is a
                            trial of two one-degree bands (lat0 + 90) that writes nothing.
  2. econ_paths.py (E1)     writes work/econ/: nations-money.tsv moneys.bin paths.bin meta-*.tsv
  3. econ_trade.py (E2)     reads owners.tsv and freight.bin; writes work/econ/: goods.tsv trade.bin baci-samples.tsv
                            (prices.bin, the prices layer, from whichever stage makes it)
  4. bake.py                work/out/world-1.bin from the layers in FORMAT.txt's order, and tools/world/world-1.sha256.
                            --partial leaves out a missing economic layer (a trial; no sha file); --econ DIR, --out PATH,
                            --made DATE (default today; the SHA changes with it).
  5. fixture.py             src/main/resources/world/world-1-fixture.bin and world-1-fixture.expect, cut from the baked
                            file (--full PATH, --out-dir DIR, --econ DIR for baci-samples.tsv); a few seconds.
  6. measures_fetch.py (LB1) downloads what measures.tsv names into <sources>: the WDI series (wdi/) and the OECD, BIS,
                            UNODC, UN WUP, EIA and BLS files (oecd/ bis/ unodc/ wup/ eia/ bls/); --force downloads again
                            a file that exists (never WN0's six WDI files). MEASURES-SOURCES.txt there, by hand, says
                            where each file is from, under what licence, and how measures.tsv's pointers read it.
  7. measures.py (LB1)      reads measures.tsv, regions.tsv, those downloads and world-1.bin (--world PATH, via
                            worldread.py); writes src/main/resources/world/world-2025.json (--out PATH, --made DATE,
                            default today; the same inputs make the same bytes) and
                            <sources>/work/measures/coverage.tsv. FORMAT-2025.txt is the file's contract; a few seconds.
  worldread.py [path]       the reference reader (parse and encode of every layer); the script prints a file's summary.

CHOICES the contract leaves open (all in the docstrings too)
  owners   a Natural Earth feature is a territory when its TYPE is Dependency, Lease, Country, Sovereignty or Disputed and
           its sovereign is one of the 196 (Gibraltar, the Falklands and BIOT are the UK's); Indeterminate features
           (Palestine, Western Sahara, Antarctica, ...) and states beyond the 196 are areas, numbered by code from 197.
  nodes    the most populous place of a nation's own features (not its territories') within PORT_KM of a port, else the
           most populous; Nauru has no place in Natural Earth, so its node is its label point.
  areas    node_name '-', node and label both Natural Earth's label point; landlocked 1 for BRT CNM KAS KOS VAT.
  freight  a CERDI pair uses the row with iso1 < iso2; edges' a and b are the nations' numbers (1-based); the table's
           cost is recomputed as sea + LAND_PER_SEA x land from the closure's final km.
  owner    two owners in one cell: the lower number; a tile with one owner (any number of sea cells) is uniform.
  fixture  a mixed tile outside the window takes its plurality owner (0 when that is outside the twelve); make and use
           are summed in the pairs' order; the six window tiles are blobs even where the full file's are uniform.
