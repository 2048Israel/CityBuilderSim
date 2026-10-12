# Earth 2 data report (task C, and the landmass layer)

Data agent, 2026-10-12. Read-only on the game. Everything below was measured here unless marked (est.), with its reason.
Work data is in `/home/claude/e2data/` (not in the repository); scripts are in `tools/earth/` and `docs/studies/earth2/`.

## 0. In brief

- **earth-2.bin is baked**: `/home/claude/e2data/out/earth-2.bin`, 135,965,097 bytes, SHA-256 `6ccb4794b15f1c217ad368b1dd883223711a35ca54a8de2bc7757606766fa8c3` (also `tools/earth/earth-2.sha256`); earth-1's ten layers byte for byte plus slope (30", 57.2 MB), shallow sea (15", 7.8 MB), depth (1', 12.5 MB), landmass (15", 5.8 MB, the short-crossing rule) and its table; all 36 checks pass. Same container as earth-1, so `EarthData` reads it unchanged.
- **Licences:** GEBCO 2026 passes (public domain, credit); NASADEM and SRTM v3 pass (NASA: no restriction, CC0); ETOPO 2022 is CC0 on NOAA's terms but its land is built from Copernicus DEM and FABDEM, so not used; **Copernicus GLO-30/90 fail "credits only"** (an indemnity, a required liability sentence, obligations passed to players - the same features that failed HydroRIVERS); AW3D30 fails (advance notice to JAXA for commercial use); FABDEM fails (non-commercial); ArcticDEM passes (CC BY 4.0) but was not baked.
- **Slope** is measured **over 90 m** (NASADEM averaged to 3"), because at 30 m radar noise and tree tops put 15-21% "foothill" on the flat Amazon (90 m: 0.3%). NASADEM covers 61 N to 56 S; north and south of that the slope is an estimate from GEBCO's 15" slope (tested: about 3 points of error a tile).
- **Thresholds:** flat under 10% grade, foothill 10-30% (buildable, dearer), steep 30-60% (refused), very steep 60% and more (refused): FAO's slope-class edges, and Concord's "30 percent or more shall not be developed". Earth-1's ICE wins over slope. Medellín's centre (1,482 m) is 98% flat, El Alto (4,025 m) 100%, Everest's summit cell 94% very steep.
- **Depth:** shallow = 30 m or less (est.): the Halifax Narrows is a 20 m sill (BIO 1974), GEBCO 17-21 m, and comes out bridgeable; 2.0% of the world's sea and 34.3% of the sea within 20 km of land is shallow. Longest bridge (est.) 36 km (Hangzhou Bay). E4's coast-distance depth is 3-9 times too deep on broad shelves and far too shallow off Chile; the depth layer replaces it.
- **Landmasses** (ground cut off by more than a 1,020 m crossing of fresh water or shallow sea): 24,889 (10,405 of 1 km2 or more, 273 of 1,000 km2); Halifax, Dartmouth and Bedford one landmass, McNabs joined to it (463 m), Prince Edward Island, Great Britain, Sicily, Goeree-Overflakkee separate, Zealand and Funen apart (Funen joins Jutland); Wellington Island (Chile) and Vaygach join their mainlands across water the 15" data calls shallow and under 1 km - worth a look.
- **Decisions left to the study:** 30" against 15" for slope (57 MB against 204 MB; 15" keeps valleys sharper), the 30 m shallow threshold, the 36 km span, and whether to allow one deep suspension span (up to about 2 km).

## 1. Licences, read from their own texts (2026-10-12)

| source | verdict | the deciding text |
|---|---|---|
| **GEBCO 2026 Grid** | **passes** (public domain, credit) | gebco.net, "Terms of use for the GEBCO Grid and derived information products" (https://www.gebco.net/data-products/gridded-bathymetry/terms-of-use): "The GEBCO Grid is placed in the public domain and may be used free of charge ... Users are free to: Copy, publish, distribute and transmit ... Adapt ... Commercially exploit The GEBCO Grid, by, for example, ... including it in their own product or application. Users must: Acknowledge the source ... Not use The GEBCO Grid in a way that suggests any official status or that GEBCO, or the IHO or IOC, endorses any particular application ... Not mislead others". Disclaimer: "should NOT be used for navigation". The GEBCO_2026 page asks for this credit: "GEBCO Bathymetric Compilation Group 2026 (2026). The GEBCO_2026 Grid - a continuous terrain model for oceans and land at 15 arc-second intervals. NERC EDS British Oceanographic Data Centre NOC. doi:10.5285/4f68d5c7-45eb-f999-e063-7086abc036fa" (earth-1 uses a shorter form; ATTRIBUTION-2.txt uses the page's). Note: GEBCO's land is SRTM15+ v2.8; the licence covers the grid as published. |
| **NOAA ETOPO 2022** (15"/30"/60") | **passes on its own terms; not used** | ISO metadata gov.noaa.ngdc.mgg.dem:etopo_2022 (data.noaa.gov metaview): "These data were produced by NOAA and are not subject to copyright protection in the United States. NOAA waives any potential copyright and related rights in these data worldwide through the Creative Commons Zero 1.0 Universal Public Domain Dedication (CC0-1.0)." But its user guide (ETOPO 2022 User Guide, table of sources) lists its land as **Copernicus DEM 30m** and **FABDEM** (CC BY-NC-SA). NOAA can waive only its own rights; whether that clears the land heights for sale is not something I can settle, so ETOPO is not relied on for land. GEBCO covers the sea. |
| **NASADEM** (NASADEM_HGT v001, 1") | **passes** (no restriction / CC0) | NASA Earthdata "Data Use Guidance" (https://lpdaac.usgs.gov/data/data-citation-and-policies/ redirects to https://www.earthdata.nasa.gov/engage/open-data-services-software-policies/data-use-guidance): "Unless the content is marked with a use restriction or license, data provided from a NASA-led mission are licensed as Creative Commons Zero (CC0). While there are no restrictions on the use of these data, data users are very strongly urged to cite". The NASADEM_HGT page (https://lpdaac.usgs.gov/products/nasadem_hgtv001/): "This dataset is openly shared, without restriction, in accordance with the EOSDIS Data Use and Citation Guidance." Credit: "NASA JPL (2020). NASADEM Merged DEM Global 1 arc second V001 ... https://doi.org/10.5067/MEASURES/NASADEM/NASADEM_HGT.001". NASA's page says 60 N to 56 S; its catalogue has 14,520 tiles including a row at 60-61 N (256 tiles), which holds void-free heights (checked: up to 4,761 m at 60 N 140 W) from NASADEM's fill sources, so it measures to 61 N. |
| **SRTM v3** (SRTMGL1, 1") | **passes** (same NASA policy) | as NASADEM. |
| **Copernicus DEM GLO-30 and GLO-90** | **fails "credits only"** (free, commercial use not excluded, but more than credits) | "Licence for the use of the Copernicus WorldDEM-30" and "...WorldDEM-90" (in https://dataspace.copernicus.eu/sites/default/files/media/files/2025-06/copernicus_contributing_mission_data_access_v2_cop_dem_licenses.pdf, linked from the AWS registry's licence link). Rights: reproduction, distribution, communication to the public, adaptation, "free of charge", worldwide. But Article 6(c): users who distribute "must ensure that the Subsequent Users understand that neither the Licensor nor any other legal entities ... may be held liable ... The following sentence ... shall be added ... in a licence or any legal warning or notice ...: 'The organisations in charge of the Copernicus programme by law or by delegation do not incur any liability for any use of the Copernicus WorldDEM-30'"; 6(e): "he shall ensure that its Subsequent Users are bound by the above obligations"; Article 7: "The User will protect the Licensor and these legal entities and the Provider against any liability claim resulting from its use"; Article 9 (Termination) on breach. Required notice: "produced using Copernicus WorldDEM-30 © DLR e.V. 2010-2014 and © Airbus Defence and Space GmbH 2014-2018 provided under COPERNICUS by the European Union and ESA; all rights reserved". The indemnity and the pass-down obligations are the same features that failed HydroRIVERS (E6). Used here for comparison only. If Samuel wanted it, it is his call; nothing in earth-2 needs it (at 90 m it agrees with NASADEM to under a point, section 3). |
| **ALOS AW3D30** (JAXA) | **fails "credits only"** | JAXA "Terms of Use of Research Data" (https://earth.jaxa.jp/en/data/policy/): "2.3. Commercial purposes use: The user can use the Research Data for commercial purposes without a fee; however, the user needs to notify JAXA thereof in advance." Also "2.5. The terms are subject to change without notice", and a registration to download. |
| **FABDEM** (Bristol) | **fails** (non-commercial) | data.bris page (https://data.bris.ac.uk/data/dataset/s5hqmjcdj8yo2ibzi9b4ew3sn): "The FABDEM dataset is licensed under a Creative Commons 'CC BY-NC-SA 4.0' license. For commercial use queries, please contact fabdem@fathom.global". |
| **ArcticDEM** (PGC, 32 m mosaic) | **passes** (CC BY 4.0, as WorldCover) - not baked | AWS registry (https://registry.opendata.aws/pgc-arcticdem/): "License: Creative Commons Attribution 4.0 International (CC BY 4.0)"; PGC acknowledgement policy asks for "Geospatial support for this work provided by the Polar Geospatial Center under NSF-OPP awards ...". The candidate for a measured slope north of 61 N later (polar stereographic, a separate stage). |
| **ASTER GDEM v3** | passes on NASA's page ("openly shared, without restriction"); **not usable here**: LP DAAC needs an Earthdata login, and no public mirror was found. |
| CGIAR SRTM 90 m v4.1, MERIT DEM | not checked (not needed); spec-earth 2.2 already records MERIT and FABDEM as non-commercial. |

**Access, measured:** NASADEM from LP DAAC needs an Earthdata login (the file request answered "HTTP Basic: Access denied"). Two public mirrors work with no login: Microsoft's Planetary Computer (an anonymous token from https://planetarycomputer.microsoft.com/api/sas/v1/token/nasademeuwest/nasadem-cog, Cloud-Optimized GeoTIFFs; its STAC catalogue lists all 14,520 tiles; the collection's licence link is LP DAAC's policy, "Public Domain") and ESA STEP's SNAP mirror of SRTMGL1 v3 (https://step.esa.int/auxdata/dem/SRTMGL1/). The pipeline reads the first and falls back to the second.

## 2. Chosen sources

- **Slope, 61 N to 56 S:** NASADEM 1", averaged to 3" (90 m), slope by Horn's 3 x 3 method on that grid (14,459 tiles with land in earth-1).
- **Slope north of 61 N and south of 56 S (and any tile NASADEM lacks):** an estimate from GEBCO 2026's 15" slope through a table of NASADEM's mean shares at 45-60 degrees; marked per tile (`slope.est`, 11,849 tiles). Greenland and Antarctica are mostly earth-1's ICE anyway (the 2' snow-and-ice bit), which wins over slope. ArcticDEM (CC BY) is the way to measure the north later.
- **Depth and shallow sea:** GEBCO 2026 (already credited), against earth-1's sea share.
- **Landmasses:** earth-1's own sea and lake shares and GRWL widths (no new source).

## 3. Heights for slope: 30 m, 90 m or a coarse grid

**What 30 m slope gets wrong.** Slope measured pixel by pixel on a 30 m surface model counts noise and canopy as slope. On the flat Amazon floodplain near Manaus, 14.7% of the land comes out 10% grade or more from NASADEM at 30 m, and 20.8% from Copernicus GLO-30 (its tree-top edges); Rotterdam 2.0% / 4.7%; Halifax 18.1% / 25.3%. Averaged to 90 m first, the same ground is 0.3% (Amazon), 0.0% (Rotterdam), 9.0% (Halifax: its drumlins and rock ridges, which are real). So the slope is measured **over 90 m**: NASADEM 1" averaged to 3", Horn's method over 3" (each slope spans 180 m). The price: in rugged country the 90 m measure counts 2 to 6 points less "very steep" than 30 m (Medellín 4.1% against 9.4%, Wellington 1.7 against 5.2). Mean absolute difference of nas30 from nas90 over 14 sites, points by class (flat, foothill, steep, very steep): 5.2, 4.2, 2.7, 2.7.

**Public domain costs nothing in accuracy:** NASADEM at 90 m and Copernicus GLO-90 agree to 0.6, 0.8, 0.6, 0.4 points (mean), worst 2.5.

**Feasibility, measured here:** a NASADEM tile is 8.9 MB on average (80 random tiles of 14,520; projected **129 GB** in all, est. from that sample); the Planetary Computer gave 3.3 MB/s on one stream, 9.9 MB/s on 4 and 18.2 MB/s on 8; a tile's arithmetic (decode, fill voids, 3 x 3 mean, slope, histogram per 15" cell) takes 0.44 s on one core in a single test. **The whole run here:** all 14,520 tiles in 4,011 s (67 minutes), **136.1 GB streamed** at 34.0 MB/s on 12 download threads, 0.28 s a tile of arithmetic, no tile failed, nothing kept but about 3 GB of per-tile histograms. So a 90 m measure of the whole land is feasible in about an hour here; a 30 m measure would stream the same bytes (the 1" tiles are what is downloaded either way) but, by section 3's first paragraph, measure noise.

**The coarse alternative** (GEBCO 15" slope, calibrated on NASADEM 90 m at the other sites, each site left out of its own table; bins of GEBCO slope 0,1,2,3,4,5,6,8,10,12,15,18,22,26,30,35,40,50,60,80 %): mean absolute error 4.6, 4.1, 3.3, 1.8 points by class, worst 13.1, 10.3, 12.7, 6.7 (Everest's steep share, Halifax's flat). Good enough as the fallback where NASADEM does not reach; not as the main source.

**The fallback where it is used** (`estimate_error.py`): the table calibrated on NASADEM's 2,456 tiles at 45-55 degrees north and south, tested on its 1,143 measured tiles at 55-60 N (the nearest land in kind to what lies past 61 N): per tile, mean absolute error **3.0 / 2.9 / 1.5 / 0.45 points** (flat / foothill / steep / very steep), 90th percentile 6.9 / 6.3 / 4.3 / 1.4; over all those tiles, measured 77.4 / 15.4 / 5.7 / 1.5% against estimated 75.5 / 16.5 / 6.4 / 1.6%. (The bake's table uses all of 45-60.)

## 4. Thresholds (percent grade, measured over 90 m)

| class | grade | degrees | game |
|---|---|---|---|
| flat | under 10% | under 5.7 | its cover colour (grass, forest, desert); cheapest to build |
| foothill | 10-30% | 5.7-16.7 | lightest grey; buildable, dearer to prepare |
| steep | 30-60% | 16.7-31.0 | mid grey; refused for buildings |
| very steep | 60% and more | 31.0 and more | dark grey; refused |
| snow and ice | earth-1's 2' bit (snow and ice >= 50% of the cell's land) | | white; refused; wins over slope |

Evidence:
- **FAO, Guidelines for soil description, 4th ed. (2006), Table 7 "Slope gradient classes"** (https://www.fao.org/3/a0541e/a0541e.pdf): "Sloping 5-10, Strongly sloping 10-15, Moderately steep 15-30, Steep 30-60, Very steep > 60" (percent). The three thresholds are FAO's class edges.
- **Building:** Concord, California, Municipal Code 18.300 (Hillside Protection; https://www.codepublishing.com/CA/Concord/html/Concord18/Concord18300.html): hillside rules apply to "any parcel with an average slope of 15 percent or greater"; "Land with an average slope of 30 percent or more shall not be developed. Development on land with an average slope of 20 percent or more shall be limited to a single-family dwelling." Hence buildable to 30%, dearer above about 10-15%.
- **Roads and rail** (Wikipedia, "Grade (slope)", read 2026-10-12): US federal highways "up to 6% generally allowed in mountainous areas and hilly urban areas", "up to 7% grades on mountainous roads with speed limits below 60 mph"; the steepest streets 34.8% (Baldwin Street) and 41% (Bradford Street, San Francisco); the steepest adhesion railway 13.5% (Lisbon tram); rack railways to 48% (Pilatus). So a highway or railway on foothill or steep ground climbs by cuts, fills, viaducts, switchbacks and tunnels - priced by slope, as Samuel decided.
- **Sensitivity** (site shares of land, flat/foothill/steep/very steep, 90 m): Medellín at 10/30/60: 17.6/47.4/30.9/4.1; at 8/25/50: 13.1/42.5/34.5/9.9; at 15/35/70: 31.8/41.2/25.7/1.2; at 10/20/60: 17.6/27.1/51.2/4.1.
- **Medellín and the Himalaya** (spot cells, 3 x 3 15" cells, NASADEM 90 m): Medellín's centre (Parque Berrío) **1,482 m, 98% flat**; Kathmandu Durbar Square 1,306 m, 97% flat; El Alto airport **4,025 m, 100% flat**; Bogotá 2,608 m, 93% flat; Denver 1,598 m, 96% flat; Innsbruck 585 m, 87% flat; La Paz's Plaza Murillo 3,650 m: 41% flat, 49% foothill, 10% steep; St. Moritz 1,838 m: 19/35/44/2; the cell at **Everest's summit (cell mean 8,533 m): 94% very steep**. Slope, not altitude, decides.
- Caveat: the real Medellín climbs onto ground this calls steep (its hillside comunas). The refusal at 30% is a planner's rule, not a description of every city.

## 5. The sites (share of land in each class, %, flat / foothill / steep / very steep; a box about 30 km square round each)

| site | NASADEM 30 m | **NASADEM 90 m (chosen)** | Copernicus 90 m | GEBCO 15" estimate | estimate's error (points, vs 90 m) |
|---|---|---|---|---|---|
| Medellín | 13.3/43.0/34.2/9.4 | **17.6/47.4/30.9/4.1** | 17.2/46.9/31.3/4.7 | 19.7/37.1/34.4/8.9 | 2.1/10.3/3.5/4.8 |
| Bogotá | 56.9/28.9/11.8/2.4 | **61.7/28.2/8.9/1.2** | 62.0/27.6/9.2/1.3 | 59.9/22.6/14.7/2.8 | 1.8/5.6/5.8/1.6 |
| Kathmandu valley | 24.4/27.6/34.1/13.9 | **30.1/28.4/33.7/7.8** | 29.5/27.7/33.9/8.9 | 30.2/31.3/30.0/8.4 | 0.2/3.0/3.7/0.6 |
| Everest | 9.2/25.9/25.9/39.1 | **11.0/24.9/27.7/36.5** | 10.1/24.7/28.0/37.3 | 5.1/24.7/40.3/29.8 | 5.9/0.2/12.7/6.7 |
| La Paz / El Alto | 46.4/27.0/22.2/4.4 | **48.9/28.7/20.9/1.5** | 48.5/28.2/21.7/1.7 | 40.1/33.0/22.3/4.5 | 8.8/4.4/1.4/3.0 |
| Innsbruck | 10.0/19.0/40.9/30.0 | **10.6/19.6/44.5/25.3** | 10.7/19.0/44.1/26.2 | 7.8/22.8/43.8/25.6 | 2.8/3.2/0.7/0.3 |
| Upper Engadin | 5.9/20.1/41.9/32.1 | **5.8/20.8/45.0/28.4** | 5.8/20.1/44.4/29.6 | 4.7/21.3/46.8/27.2 | 1.1/0.5/1.8/1.2 |
| Halifax | 81.6/18.1/0.3/0.0 | **90.9/9.0/0.1/0.0** | 89.1/10.8/0.1/0.0 | 77.8/18.5/3.4/0.3 | 13.1/9.5/3.3/0.3 |
| Rotterdam | 98.0/2.0/0.0/0.0 | **100.0/0.0/0.0/0.0** | 99.9/0.1/0.0/0.0 | 97.1/2.7/0.3/0.0 | 2.9/2.6/0.3/0.0 |
| Rio de Janeiro | 55.2/23.8/16.1/4.8 | **63.6/19.5/14.3/2.6** | 61.7/20.4/15.0/2.8 | 65.6/20.1/11.7/2.7 | 1.9/0.6/2.6/0.1 |
| Hong Kong | 20.0/37.7/37.0/5.4 | **27.6/42.7/28.5/1.2** | 27.7/40.4/30.4/1.5 | 29.4/41.9/24.5/4.2 | 1.7/0.8/4.0/3.0 |
| Denver / Front Range | 70.2/20.1/8.8/0.9 | **78.1/15.0/6.7/0.3** | 77.9/14.7/7.1/0.3 | 65.0/24.9/8.8/1.3 | 13.1/9.9/2.1/1.1 |
| Wellington | 18.0/39.1/37.8/5.2 | **20.9/48.5/28.9/1.7** | 20.1/46.3/31.4/2.1 | 26.4/43.8/25.2/4.5 | 5.5/4.7/3.7/2.8 |
| Amazon near Manaus (flat forest) | 85.2/14.7/0.1/0.0 | **99.7/0.3/0.0/0.0** | 99.4/0.6/0.0/0.0 | 96.8/2.8/0.3/0.0 | 2.8/2.5/0.3/0.0 |

(Copernicus GLO-30 is in `work/sites_10_30_60_nas90.json` too; it is the noisiest: 79.0% flat in the Amazon.) Shares are of earth-1's land in each 15" cell (sea and lakes, flat in every DEM, are left out), land-weighted over the box.

## 6. Resolution: what a coarser slope layer loses

Land-weighted mean absolute error of a 15" cell's share at or above foothill / at or above steep when it takes its coarser cell's share instead, and the flat share kept on Medellín's valley floor (15" cells measured at least 60% flat):

| site | 30" | 1' | 2' |
|---|---|---|---|
| Medellín | 0.063 / 0.095; floor 0.76 flat | 0.103 / 0.142; floor 0.62 | 0.143 / 0.199; **floor 0.47** |
| Everest | 0.071 / 0.147 | 0.113 / 0.234 | 0.135 / 0.280 |
| Hong Kong | 0.097 / 0.121 | 0.164 / 0.180 | 0.214 / 0.219 |
| Rotterdam | 0.001 / 0.000 | 0.001 / 0.000 | 0.001 / 0.000 |

At 15" Medellín's floor cells keep 0.87 flat; at 30" 0.76; at 2' they are half foothill and the valley's shape is gone (the seeded detail then scatters flat ground over the slopes; figure `medellin-resolution.png`). **The size decides between 15" and 30"** (`slope_size.py`, projected from 2,037 random measured tiles and 600 of the 11,800 estimated ones): the slope layer would be **204 MB at 15"** and **57 MB at 30"** in the one-byte packing (285 MB and 87 MB as two 4-bit layers). 15" would make the slope four times earth-1; **30" was baked**, and it still draws Medellín's valley, Kathmandu's and the Inn's (figures). `CBS_SLOPE_CPD=240` bakes 15" if Samuel prefers the detail to the size. (All 14 sites: `work/resolution.json`.)

## 7. Depth

**Halifax.** GEBCO 2026 along the Macdonald Bridge line: deepest 21 m (cells of 6, 7 and 21 m on the water); along the MacKay: 18 m (7, 12, 14, 18). The Bedford Institute of Oceanography: "To the 70-metre deep basin the Narrows constitutes a sill 20 metres in depth and 300 to 400 metres wide" (McGonigal, Loucks and Ingraham, *Halifax Narrows: Sample Current Meter Data 1970-71*, BIO Data Series BI-D-74-5, 1974; https://waves-vagues.dfo-mpo.gc.ca/Library/49307.pdf). Bedford Basin: GEBCO's deepest cells 64 m; BIO gives "a maximum depth of 71 m" (https://www.bio.gc.ca/science/monitoring-monitorage/bbmp-pobb/description-en.php). Bridges (Halifax Harbour Bridges, https://halifaxharbourbridges.ca/about/macdonaldhistory/ and .../mackayhistory/): **Macdonald** total length 1.3 km, suspended spans 762.1 m, clearance 46.9 m; **MacKay** 1.2 km, suspended spans 739.9 m, clearance 46.9 m; main spans 441 m and 426 m (Wikipedia, the Macdonald's figure marked "citation needed"). Infobox positions: Macdonald 44.6637 N 63.5846 W, MacKay 44.6778 N 63.6119 W.

**How well 15" resolves the Narrows.** A 15" cell there is 463 m north-south by 329 m east-west; the Narrows (300-400 m) is one to one and a half cells wide. GEBCO's channel cells read 17-21 m (the sill is 20 m), the cells it shares with the shore 3-7 m. earth-1's sea share (from 10 m WorldCover) already draws the channel's shape; the shallow share is computed against it, so the Narrows comes out shallow sea along its length and the Basin's middle deep (figure `halifax.png`). A channel narrower than a cell is blended with land by GEBCO and so reads shallow - bridgeable, which is right for a narrow channel. In the baked 15" shallow layer the Narrows' sea cells at the Macdonald Bridge are 15/15 shallow and Bedford Basin's middle 0/15. The 1' depth layer reads 4 m at the Macdonald Bridge (its 1' cell is mostly shore cells) and 51 m in the Basin (a cell mean; deepest 15" cell 64 m): the depth layer is for fields and platforms, the shallow share for crossings.

**Threshold (est.): 30 m.** Real long bridges mostly stand in 5-25 m of water, and GEBCO gives 24 m for the Confederation Bridge's deepest water (12.9 km, Borden-Carleton to Cape Jourimain; 44 main piers 250 m apart, Wikipedia). Foundations in deeper water are records described as hard: the Rio-Antirrio bridge's strait "reaches 65m" and its footings were "a difficult endeavor at this depth" (Wikipedia). 30 m keeps the Narrows (20 m) with margin for GEBCO's smoothing and refuses open straits. (est.: a judgement between those cases, not a published limit.)

**Maximum bridge length (est.): 36 km**, the Hangzhou Bay Bridge (35.673 km, the longest wholly over sea); for scale the Hong Kong-Zhuhai-Macau link is 49.968 km but includes a 6.7 km tunnel, Lake Pontchartrain Causeway 38.442 km (a lake), Jiaozhou Bay 26.707 km, Chesapeake Bay Bridge-Tunnel 24.140 km, Penang Second Bridge 24.0 km, Vasco da Gama 17.185 km, Rio-Niterói 13.290 km, Confederation 12.9 km, Øresund 7.845 km, Great Belt East 6.790 km (Wikipedia, "List of longest bridges"). 36 km is 1,200 plots. An option for the study, not a recommendation of mine: one deep gap crossed by a single suspension span, up to about 2 km (est.: the record main span is 2,023 m, the 1915 Çanakkale Bridge) - it would admit Great Belt- and Akashi-type crossings that 30 m alone refuses.

**How much sea is shallow** (GEBCO 2026 against earth-1's sea, area-weighted, every 1-degree tile; "near" = within 20 km of land by a distance transform on earth-1's 15" coast; the world's sea measured as 361.1 million km2, the near sea 13.7 million km2):

| no deeper than | 5 m | 10 | 15 | 20 | 25 | **30** | 40 | 50 | 60 | 80 | 100 | 150 | 200 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| share of all sea, % | 0.3 | 0.6 | 0.9 | 1.3 | 1.6 | **2.0** | 2.6 | 3.2 | 3.7 | 4.7 | 5.4 | 6.5 | 7.2 |
| share of sea within 20 km of land, % | 8.5 | 14.9 | 20.7 | 26.1 | 30.7 | **34.3** | 39.7 | 44.1 | 47.8 | 53.3 | 57.4 | 63.4 | 66.9 |

At 30 m a third of the near-shore sea is bridgeable and 98% of the world's sea is not.

**E4's rule against GEBCO** (EarthTerrain.depthAt(): 140 m x the distance to earth-1's coast, at least 30 m, / 54,167 m):

| place | km out | E4's coast km | GEBCO m | E4 m |
|---|---|---|---|---|
| Rotterdam, west of Hoek van Holland | 20 / 40 / 80 | 9.5 / 26.7 / 58.1 | 20 / 27 / 34 | 25 / 69 / 150 |
| Halifax, Scotian Shelf SE of the harbour mouth | 10 / 50 / 100 / 150 / 200 | 10.6 / 50.3 / 100.2 / 150.2 / 200.2 | 89 / 148 / 237 / 95 / 370 | 27 / 130 / 259 / 388 / 518 |
| Galveston, Texas shelf | 20 / 40 / 80 / 120 | 21.4 / 41.2 / 81.2 / 121.2 | 18 / 16 / 23 / 39 | 55 / 107 / 210 / 313 |
| Persian Gulf off Ras Tanura | 20 / 40 / 80 | 21.1 / 41.0 / 80.8 | 15 / 32 / 66 | 55 / 106 / 209 |
| Valparaíso (narrow shelf) | 10 / 20 / 40 / 80 | 8.6 / 17.2 / 35.4 / 74.5 | 155 / 656 / 2,369 / 2,984 | 22 / 44 / 91 / 192 |

E4 makes broad shallow shelves 3-9 times too deep (the North Sea, the Texas shelf, the Gulf: no platform beyond 58 km where the real sea is 20-40 m) and narrow shelves far too shallow (off Chile it would allow a platform where the sea is 2.4 km deep); on the Scotian Shelf it is roughly right on average but misses the banks (Sable Island Bank, 95 m at 150 km, E4 388 m). The depth layer replaces it.

## 8. Layers and sizes

The container is earth-1's, unchanged (`EARTH-1\n`, layout version 1), so the game's `EarthData` reads earth-2.bin as it is; the file is named earth-2 in its meta layer and identified by its SHA-256 (`tools/earth/FORMAT-2.txt` has every byte). earth-1's ten layers are copied byte for byte. New (the slope's eight levels, not sixteen: a share from 25 or 100 samples is uncertain by about a tenth at mid shares, and one byte costs 38% less than a byte of two 4-bit shares plus a 4-bit very-steep layer, measured on the site tiles):

| layer | grid | content | size |
|---|---|---|---|
| `slope` | 30", a byte | the cell's land: bits 0-2 share at or above 10% (sevenths), bits 3-5 at or above 30% (sevenths), bits 6-7 the very steep part of the steep (thirds) | **57.17 MB** |
| `slope.est` | 8,100 bytes (925 compressed) | a bit a 1-degree tile: the shares are GEBCO's estimate | 0.001 MB |
| `shallow` | 15", a byte | share of the cell's sea no deeper than 30 m (0..15) | **7.75 MB** |
| `depth` | 1', a byte | mean depth of the cell's sea: 1-200 m by the metre, 10 m steps to 750 m, 255 deeper | **12.48 MB** |
| `landmass` | 15", palette ids | each land cell's landmass id (0 none), ids by area; ground cut off by more than a 1,020 m crossing of fresh water or shallow sea (section 9) | **5.76 MB** |
| `landmasses` | text | per id: area km2, cells, the most populous Natural Earth place on it | **0.04 MB** |
| earth-1's ten layers | as earth-1 | copied byte for byte | 52.75 MB |
| meta, header, indexes | | | 0.01 MB |
| **earth-2.bin** | | | **135.97 MB** (135,965,097 bytes) |

What other resolutions cost (measured over every 1-degree tile by `depth_study.py`, or projected where marked):
- the shallow share at 15" (4-bit): 7.79 MB at 20 m, **7.73 MB at 30 m**, 7.63 MB at 50 m (measured, all tiles);
- the depth byte with a code to 7,900 m in 100 m steps: 252.3 MB at 15", 96.7 MB at 30", 36.3 MB at 1', 13.2 MB at 2' (measured, all tiles); with the chosen code (1 m to 200 m, 10 m to 750 m, then one code) **12.2 MB at 1'**, 4.0 MB at 2' (projected from 1,500 random sea tiles of 46,002). At 15" depth would cost more than the rest of the file; the shallow share carries the 15" detail bridges need, and 1' (1.85 km) is fine for a field's platform depth.

**One file or an add-on.** Recommended: **one file, earth-2.bin**, carrying earth-1's layers and the new ones, one hash. A save names one Earth ("earth-2:<hash>"), the reader stays one file, the release is one asset, and earth-1's bytes inside it are provably the same (check2 holds every blob), so a coast never moves. The cost is that a player downloads the 53 MB of earth-1 again inside it. An add-on (earth-1.bin plus a terrain file) saves that download but makes a city depend on two hashes, two assets and two readers that must be kept in step; the save, the founding check and get-earth.bat would all have to learn pairs. Old Earth saves refusing to load is accepted, so nothing is gained by keeping earth-1 separate.

## 9. The landmass layer (the roads study's I7, with the Earth 2 study's short crossing)

**The rule (changed 2026-10-12 on the study's finding that Halifax's bridges cross about 820 m of water, not 420 m):** a landmass is ground cut off by more water than a SHORT CROSSING spans - a highway or rail bridge of up to **1,020 m (34 plots)** over **fresh water or shallow sea** (30 m or less); deep sea never joins. In `tools/earth/landmass.py`:
- land = a 15" cell whose earth-1 sea + lake levels come to 7 of 15 or less, less GRWL rivers **wider than 1,020 m** (was 420 m);
- crossable = water that is fresh (lake share at least sea share, or a river's cut) or shallow (earth-2's shallow share 8 of 15 or more);
- joined = land along edges (pass 1, as before), and across crossable water (pass 2): each crossable cell takes its nearest land's label and distance from that shore (a distance transform less the half cell to the shore); two neighbouring cells of different labels join them when shore-to-cell + cell-to-cell + cell-to-shore is 1,020 m or less. It runs in a window round every tile, wide enough for 1,020 m at its latitude, so it works across tile edges and the 180th meridian. Joins chain, so islands each within a crossing of the next reach the shore.
- Run time: 7.9 minutes (pass 2: 144,599 crossings joined, found in 12,594 tiles).

**Counts:** **24,889 landmasses** (was 113,291): 22,313 of 0.1 km2 or more, **10,405 of 1 km2 or more** (was 19,868), 3,543 of 10 km2, 1,083 of 100 km2, **273 of 1,000 km2 or more** (was 288), 71 of 10,000 km2, 5 over a million. The largest: 1 Afro-Eurasia 78.68 million km2, 2 the Americas 36.81 million, 3 Antarctica 13.76 million, 4 Australia 7.56 million, 5 Greenland 2.12 million, then New Guinea, Borneo, Madagascar, Baffin, Sumatra, Honshu (with Kyushu, 0.264 million), Great Britain (0.220 million).
**Size:** 5.76 MB (tile blobs and index) + the table 0.04 MB compressed (0.22 MB of text) = **about 5.8 MB** (was 6.7).

**The cases** (`docs/studies/earth2/landmass_gaps.py`: the shortest path through crossable water between the two pieces of land, by Dijkstra over 15" cells, and through any water for comparison):

| case | result | shortest crossable water | any water |
|---|---|---|---|
| Halifax peninsula / Dartmouth / Bedford | **one landmass** (id 2, the Americas), by land round the head of Bedford Basin | - | - |
| McNabs + Lawlor islands / the mainland | **joined** (they are one 26-cell piece; were a landmass of their own) | **463 m** (most likely Lawlor to the Eastern Passage shore; the Halifax-side channel, about 1.5 km, is not needed) | 463 m |
| Prince Edward Island / New Brunswick | **separate** (id 101, 5,611 km2) | 13,601 m | 13,601 m |
| Manhattan, Long Island / the mainland | **joined** (by land at 15": their channels are under a cell) | - | - |
| Goeree-Overflakkee / the Rotterdam mainland | **separate** (id 648, 248 km2) | **1,390 m** both on the Voorne-Putten and the Hellegatsplein sides (the real Haringvliet bridge is about 1.2 km: a near thing) | 1,390 m |
| Great Britain / France | **separate** | none (the Strait of Dover is deeper than 30 m) | 33,133 m |
| Sicily / Italy | **separate** | none (Messina is deep) | 3,158 m |
| Zealand / Funen | **separate** | 23,515 m | 18,987 m |

**What flipped:** 36 of the 288 landmasses of 1,000 km2 or more under the old rule joined a bigger one. Most are real short crossings, many of them bridged in life: **Funen** joins Jutland across the Little Belt (927 m crossable); **Cape Breton Island** the Nova Scotia mainland at Canso (787 m); **Hong Kong Island** Kowloon (446 m); Leyte and Samar (San Juanico); Skye; Manitoulin; Unimak; Fraser Island; Chongming; Vendsyssel-Thy across the Limfjord; Hinnøya; Lolland with Falster; New Guinea with Frederik Hendrik Island; **Rügen** joins by way of Dänholm (direct water 1,270 m, two crossings each under 1,020 m). Two to look at, because the strait may not be what the 15" data says: **Wellington Island** (Chile, 10,642 km2) joins the Patagonian mainland across 935 m of water GEBCO calls shallow (Chile's channels are deep fjords, poorly surveyed), and **Vaygach** joins Eurasia across 954 m (the Yugorsky Strait is usually given as about 2.5 km wide). Still separate as they should be: Sri Lanka, Sicily, Sardinia, Corsica, Isle of Wight, Penang, Bali, Tierra del Fuego, Chiloé, Bahrain, Zealand, Prince Edward Island, Great Britain, Ireland, Newfoundland, Vancouver Island.

**Limit:** channels narrower than about a cell (330-460 m) are not water at 15", so they never separate anything.

**Format** (FORMAT-2.txt, unchanged): per tile, uniform tiles as -id in the index; else zlib of a palette (uvarint count, ascending ids as uvarint deltas) and a byte per cell (a big-endian short when the palette passes 256). The table: a line per id, area km2, 15" cells, the most populous Natural Earth place on it. earthread.py's `landmass(t)` reads it.

## 10. The bake

**Baked here, whole:** `/home/claude/e2data/out/earth-2.bin`, **135,965,097 bytes**, SHA-256 **`6ccb4794b15f1c217ad368b1dd883223711a35ca54a8de2bc7757606766fa8c3`** (in `out/earth-2.sha256` and in the repository's `tools/earth/earth-2.sha256`), built 2026-10-12 with the short-crossing landmass rule, its meta's source list now ATTRIBUTION-2.txt's own (sources2.sources()); it replaces the first bake (b48adce1..., 136,924,697 bytes, 420 m rivers and every sea cutting). Time: the slope stage 67 minutes (all 14,520 NASADEM tiles, 136.1 GB streamed, none failed; its per-tile results were reused for the re-bake), the landmass pass 8 minutes, bake2 7 minutes, check2 1.5 minutes (plus the GEBCO download, 7.47 GB in about 4 minutes, and the depth study's 29 minutes, which the bake does not need). The meta layer names it earth-2 and records earth-1's hash, the thresholds, the packing, the depth code, the counts (14,459 measured tiles, 11,849 estimated; 674 million measured and 588 million estimated 15" land cells; 57.5 million 15" cells with some shallow sea) and every source with its licence and credit.

**check2, all 36 checks pass** (`out/check2-report.txt`): earth-1's ten layers byte for byte; on every tile the slope's three shares nested, no slope on all-water cells, no shallow share or depth where there is no sea, no landmass on a cell that is not land; Medellín 15 km round its centre 17.4 / 48.5 / 30.8 / 3.2% (flat/foothill/steep/very steep) with its valley floor 100% flat, the Himalaya round Everest 9.5 / 19.4 / 24.2 / 46.9%, Rotterdam 100% flat, Helsinki measured (95.6% flat), Trondheim estimated (54.8 / 37.0 / 7.7 / 0.5); the Narrows shallow (15/15) at the Macdonald Bridge, Bedford Basin 51 m (its 1' cell), the North Sea 40 km off Hoek van Holland 27 m; Paris, Beijing and Cairo on landmass 1, Denver and Buenos Aires on 2, Halifax and Dartmouth one landmass, McNabs joined to the mainland, Manhattan and Long Island on the mainland's landmass, Prince Edward Island, Great Britain, Sicily, Goeree-Overflakkee each separate, Zealand and Funen apart.

The file is not in the repository and no release was made. A re-bake gives another hash (the meta records the date), so whichever file is released defines `earth-2.sha256`; this one can be uploaded as the release asset as it is, or Samuel's PC can bake its own with the .bat below.

**On Samuel's PC instead** (or to bake again): `tools/earth/bake-earth-2.bat`, double-clicked. It finds Anaconda's Python (the usual install folders, else `python` on the PATH), pip-installs `tifffile` and `imagecodecs`, works in `data\earth2-work` (git-ignored), fetches `data\earth-1.bin` through get-earth.bat, then runs slope_tiles.py (4 processes, 12 download threads), landmass.py, bake2.py (which downloads GEBCO 2026, 7.47 GB, once, resumably) and check2.py, and copies `earth-2.bin` and `earth-2.sha256` into `data\`. Disk: about 12 GB free (GEBCO 7.5 GB kept for the bake, about 3 GB of per-tile results; the 1" tiles are deleted as they are read). Time (est., from the rates here): the download is about 140 GB in all - roughly 3 hours at 100 Mbit/s, under an hour at 500 Mbit/s - and the arithmetic about 1.2 hours of one core (0.28 s a tile measured here), shared over the 4 processes. Every stage skips work already done, so it can be double-clicked again after a stop. The release "earth-2" needs earth-2.bin, earth-2.sha256 (for get-earth.bat's twin) and `tools/earth/ATTRIBUTION-2.txt`. The .bat leaves `tools/earth/earth-2.sha256` alone: if the PC's own bake is released instead of this one, copy `data\earth-2.sha256` over it.

## 11. Load time

Measured with `docs/studies/earth2/load_cost.py` on the baked earth-2.bin, for Halifax, Medellín, Rotterdam, Kathmandu, Hong Kong and Denver:
- **A founding card (5 km round the spot) reads one or two 1-degree tiles:** 2-11 KB of earth-1's layers, plus **3-10 KB** of the new ones (Halifax 7 KB: slope, shallow, depth and landmass about 1-2 KB each; Hong Kong 10 KB; Rotterdam 3 KB). **A city's 50 km reads 4-6 tiles:** 6-40 KB of earth-1 plus **18-37 KB** new. Reading is not the cost; inflating is.
- **Inflated** (what EarthData's tile cache holds, CACHE_BYTES 10 MB), per tile that is not uniform: slope 14.4 KB at 30" (57.6 KB at 15"), shallow 57.6 KB, depth 3.6 KB, landmass 57.6 KB if kept as the palette's byte indexes (230 KB if widened to an int a cell - keep the bytes and the palette). So a tile with every layer asked costs about 250 KB inflated against earth-1's 116 KB (sea and lake): the 10 MB cache holds about 40 such tiles instead of about 86. Uniform tiles (open sea, plain land, one landmass) cost nothing, their value being in the index.
- Opening the file reads the header and every tile layer's index (a zlib of 64,800 ints each; earth-1's are 18-34 KB): the four new tile layers add about 0.1-0.15 MB read once (est. from earth-1's).
- The globe and far map can draw the slope from the 30" layer or from `far.land`'s grid; nothing new is needed for them unless the far view is to show greys (est.: the slope layer read at a coarser stride is enough).

## 12. Files written

In the repository (nothing committed by me; the orchestrator's work-in-progress commit fbd7041 holds early versions of `sites.py`, `depth_study.py`, `analyse_sites.py`, `terrain.py` and `depth.py`, and my later edits to the last three and to `earthread.py` are uncommitted, with every other file below untracked):
- `tools/earth/terrain.py` - paths (CBS_E2), thresholds, Horn's slope on a geographic grid, NASADEM/SRTMGL1 tile fetch from the public mirrors, GEBCO memmap.
- `tools/earth/slope_tiles.py` - the global slope stage (resumable; threads; CBS_PROCS for several processes).
- `tools/earth/depth.py` - the shallow share and the depth code.
- `tools/earth/landmass.py` - the landmass layer and table (the short-crossing rule).
- `tools/earth/earth-2.sha256` - the baked file's hash, for the release and get-earth.bat's twin.
- `tools/earth/bake2.py` - assembles earth-2.bin (earth-1's layers copied, the new ones, meta, sha256).
- `tools/earth/check2.py` - the checks of earth-2.bin.
- `tools/earth/sources2.py` and `tools/earth/ATTRIBUTION-2.txt` - earth-2's credits (GEBCO's credit in the page's own form; NASADEM, SRTMGL1 added; GRWL and HydroLAKES now also cut landmasses).
- `tools/earth/FORMAT-2.txt` - the new layers, byte by byte.
- `tools/earth/bake-earth-2.bat` (CRLF) - the PC's one-click bake.
- `tools/earth/earthread.py` - two readers added: `landmass(t)`, `slope_est(t)`.
- `docs/studies/earth2/`: `sites.py` (per-site histograms from NASADEM 30/90 m and Copernicus 30/90 m, GEBCO slope), `analyse_sites.py` (class shares, the GEBCO estimate left-one-site-out, the resolution test; `TRUTH=nas30|nas90`, `RES=1`), `depth_study.py` (world and near-shore shallow shares, layer sizes), `depth_sites.py` (bridge profiles, E4 against GEBCO), `slope_size.py` (the slope layer's size by packing and grid), `landmass_gaps.py` (the landmass cases: crossable and any-water gaps by Dijkstra, and islands under the old and new rule), `estimate_error.py` (the estimate tested on 55-60 N), `load_cost.py`, `figures.py` and its PNGs: `medellin.png`, `everest.png`, `halifax.png` (both bridges drawn), `innsbruck.png` - each the 90 m measure beside what earth-2's shares give a 30 m plot grid - and `medellin-resolution.png` (the slope stored at 15", 30", 1', 2').

Outside the repository (`/home/claude/e2data/`): `dl/gebco/GEBCO_2026.nc` (7.47 GB), `work/` (per-tile results, the JSON measurements named above), `out/` (earth-2.bin, its sha256, bake2-stats.json, check2-report.txt), `lic/` (the licence pages and PDFs as fetched), `log/`.

## 13. What I could not check, said plainly

- **ETOPO 2022's land** is built from Copernicus DEM and FABDEM; NOAA's CC0 covers NOAA's rights. I did not rely on it, and I cannot say whether its land heights are free of those licences.
- **GEBCO's land** is SRTM15+ v2.8; I relied on GEBCO's own terms (public domain) as published and did not trace SRTM15+'s land inputs.
- **The Planetary Computer's NASADEM copy** is Microsoft's re-encoding of NASA's tiles as COGs; I compared one tile's pixels with nothing from LP DAAC (no login), only checked that it decodes to NASADEM's 3601 x 3601 grid with sane heights. Its internal overviews use a resampling it does not document, so the pipeline never reads them.
- **NASADEM is a radar surface model** (C-band): trees and buildings raise it. Averaging to 90 m removes most of that (Amazon 0.3% foothill), but dense high-rise districts may still show as foothill (est.: Hong Kong's flat reclaimed land was not separately measured).
- **The slope north of 61 N** is an estimate; ArcticDEM (CC BY 4.0) would measure it but needs a reprojection stage I did not build.
- **NASADEM's fill**: its product page says its void filling used "GDEMs and ... AW3D30 DEM, and interpolation" (AW3D30 is JAXA's, whose own terms fail the rule). NASA distributes NASADEM "without restriction"; I relied on NASA's statement for the whole product, including its row at 60-61 N, and did not separate the filled pixels (the NUM layer that marks them is not in the Planetary Computer's copy).
- **The ice rule** is earth-1's bit (snow and ice at least half the 2' cell's land); a small glacier in a cell under half ice is not drawn. A finer ice share would need WorldCover read again (15 GB streamed).
- **The Narrows' charted depth** is from a 1974 BIO report (20 m sill); I found no current chart figure I could quote.
- **Main spans** of the two Halifax bridges (441 m, 426 m) are Wikipedia's; the operator's pages give only total length and suspended spans.
- The **shallow threshold (30 m)** and the **longest bridge (36 km)** are estimates (est.), each with its reason above; they are for the study to settle.
