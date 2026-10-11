#!/bin/sh
# Builds earth-1.bin from nothing (each stage skips work already done; logs in e0/log/).
# 2 CPUs shared with the game's builds: every stage runs at nice 10, WorldCover on 3 workers.
set -e
cd "$(dirname "$0")"
P="nice -n 10 python3 -I"
$P fetch.py                       # whole-file sources: dl/, or the study's copies reused
$P sources.py                     # out/sources.json, out/ATTRIBUTION.txt
$P gebco_antarctic.py             # GEBCO elevation >= 0 south of 60 S, by range (1.24 GB streamed)
$P geology.py                     # USGS provinces at 0.25 degree
$P lakes.py &                     # HydroLAKES: shares of the big lakes, points of the small ones
$P rivers.py 30 &                 # GRWL by range (1.36 GB streamed), Douglas-Peucker at 30 m
$P wc_tiles.py 0 3 & $P wc_tiles.py 1 3 & $P wc_tiles.py 2 3 &   # WorldCover by range (15 GB streamed)
wait
$P bake.py                        # out/earth-1.bin, earth-1.sha256, bake-stats.json
$P check.py                       # out/check-report.txt
