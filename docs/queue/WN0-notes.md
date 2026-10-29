# WN0 - the nations' world file (no tag, 2026-10-28)

The batch's record. Jerus took the nations study on 2026-10-20 ("do rec"), and this batch on 2026-10-28
("yes start wn0, no need to ask for my yes, just download"). The brief is `docs/studies/spec-nations.md`
(§3.3, §4.2, §6.1, §9's WN0 row) with XR3's monthly rates (★11). Opus wrote the format and four briefs;
four Sonnet 5.5 slices at xhigh coded them side by side (G the geography, the bake and the fixture; E1 the
moneys, rates and paths; E2 the goods and the trade; J the reader and its check), and two of them a second
round (the prices layer; the check's solve). Nothing in the game reads the file, so no player sees it: it
ships `--no-tag`.

## 1. What it is

- **`tools/world/`**, the pipeline, Python 3.14's standard library alone, run with `python -I` (README.txt
  says the order and times: geo.py 18 s, econ_paths.py 5 s, econ_trade.py 90 s, bake.py and fixture.py
  seconds). `FORMAT.txt` is the file: the earth container's layout under the magic `WORLD-1`, ten layers
  (meta, nations, moneys, paths, goods, trade, freight, owner, lines, prices). `worldread.py` is the
  reference reader. Authored tables: `moneys.tsv` (each nation's money, its history from 2000-01 and its
  regime), `goods.tsv` (the 60 traded goods: class, elasticity, units a tonne, traded share),
  `concordance.tsv` (292 rows: HS 2022 to the game's goods), `prices.tsv` (each good's Pink Sheet series).
- **`world-1.bin`**, 8,712,372 bytes, SHA-256 `78a6e10e...b68` (`tools/world/world-1.sha256`):
  - 196 nations (the 193 UN members, TWN, HKG, MAC) and 15 areas; 41 territories folded into their sovereign.
  - Paths 2000-2100: people (WPP 2024), output in 2015 US$, current US$ and 2021 PPP $, CPI 2015 = 100 and
    the year's rate, 2000-2025; the World Bank's region.
  - 158 moneys, monthly 2000-01 to 2025-12, 96.8% of money-months BIS's own.
  - The trade of 2024 by pair and good, 229,257 kept pairs, with make, use, cost and preference.
  - The freight table (19,702 edges closed), the owner raster at 15" (2,383 border tiles), 7,980 border
    lines, and each good's real price path 2000-2025.
- **`WorldData`** reads it (`open`, `of`, `locate()`: `-Dcbs.world=<path>`, else `data/world-1.bin`);
  **`WorldDataCheck`** (eight sections, 1.9 s with the full files) on the fixture
  (`src/main/resources/world/`, 252 KB, twelve nations around the earth fixture's Rotterdam window) and on
  the full file where it finds it.
- `tools/earth/get-earth.bat nopause world-1` fetches `data\world-1.bin` from the release `world-1`
  against the hash file; Play and Build EXE do not ask for it yet.

## 2. How the file reaches the PC and the release

Not in git, as earth-2.bin. The baked file is at
`C:/Users/Jerus/Documents/world-sources/work/out/world-1.bin` on the PC (outside the repository). The
release is Jerus's, as earth-2's was (WN0-R): `gh release create world-1 <that path> --title "world-1: the
nations' world" --notes "WN0; SHA-256 in tools/world/world-1.sha256"`. Then `get-earth.bat nopause world-1`
puts it at `data\world-1.bin`, where `WorldData.locate()` and the check find it. Until then the gate can name
it: `-Dcbs.world=C:/Users/Jerus/Documents/world-sources/work/out/world-1.bin` in `JDK_JAVA_OPTIONS`.
Without it the full-file sections say so in one line and the fixture carries the check.

## 3. The downloads (2026-10-28, each in its own folder of C:/Users/Jerus/Documents/world-sources/)

| folder | file(s) | source | bytes |
|---|---|---|---:|
| baci | BACI_HS22_V202601.zip | https://www.cepii.fr/DATA_DOWNLOAD/baci/data/BACI_HS22_V202601.zip | 301,386,611 |
| naturalearth | ne_10m_admin_0_countries, _boundary_lines_land, ne_10m_populated_places_simple, ne_10m_ports (.geojson), VERSION | https://raw.githubusercontent.com/nvkelso/natural-earth-vector/v5.1.2/geojson/ (first fetched from master, 5.2.0-pre with the same bytes, then from the tag) | 20,783,131 |
| wdi | NY.GDP.MKTP.KD, .CD, .PP.KD, FP.CPI.TOTL, PA.NUS.FCRF, SP.POP.TOTL (.json), countries.json | https://api.worldbank.org/v2/country/all/indicator/<id>?format=json&date=2000:2025&per_page=20000 | 9,306,978 |
| wpp | WPP2024_TotalPopulationBySex.csv.gz | https://population.un.org/wpp/assets/Excel%20Files/1_Indicator%20(Standard)/CSV_FILES/ | 16,980,593 |
| cerdi | CERDI-seadistance_V1.1.xlsx, readme.txt, used_ports.zip (not opened) | https://zenodo.org/api/records/240493/files/<name>/content | 2,174,899 |
| bis | WS_XRU_csv_col.zip | https://data.bis.org/static/bulk/WS_XRU_csv_col.zip | 4,871,004 |
| pwt | pwt110.xlsx | https://dataverse.nl/api/access/datafile/554105 | 5,839,841 |
| maddison | mpd2023_web.xlsx | https://dataverse.nl/api/access/datafile/421302 | 4,903,804 |
| pinksheet | CMO-Historical-Data-Annual.xlsx, -Monthly.xlsx (not used) | https://thedocs.worldbank.org/en/doc/74e8be41ceb20fa0da750cda2f6b9e4e-0050012026/related/ | 3,761,564 |

About 370 MB, all data files from their publishers; no account, no program. `tools/world/ATTRIBUTION.txt`
credits each with its licence. The Pink Sheet came in for WN1's note in the shared folder (each good's real
price path), the study's §3.3 source.

## 4. The calls (each ★ took the recommended way)

- **★1 The base year is 2024, BACI HS22 V202601's newest**, HS 2022 for its finest splits. WN3 can re-bake.
- **★2 What each nation makes is the study's fallback estimate everywhere (○).** FAOSTAT, USGS and EIA were
  not among WN0's downloads. Use is the good's world use (the kept trade over its traded share) by the
  nation's share of world output, its domestic flow at least 5% of its trade, and make = use - imports +
  exports, so the sums close. A maker, for D11's nearest makers, is a nation exporting at least 0.01% of the
  good's world trade. Recommended: so for now, and WN0-M brings the makers' figures before WN3.
- **★3 Land outside the 196.** Territories go to their sovereign (Natural Earth's dependencies and leases,
  and the "disputed" ones it gives a sovereign among the 196: Gibraltar, the Falklands and BIOT to the UK).
  The other 15 are areas of their own, owned by none of the 196: Antarctica, Kosovo, Northern Cyprus,
  Somaliland, Western Sahara, Palestine, the Vatican, Siachen, Bir Tawil, the Spratlys, Scarborough Reef,
  Bajo Nuevo, Serranilla, the Southern Patagonian Ice Field and Cyprus's buffer zone; WN5 sells them as the
  host's. Guantanamo follows Natural Earth's sovereign, Cuba (the US runs the lease; WN5 may say otherwise).
- **★4 Freight is CERDI's**, port to port with its own port per nation, a landlocked nation's capital-to-port
  road as land km, neighbours' roads, South Sudan by its neighbours. The reference rates (0.003 US$ a
  tonne-km by sea, 0.05 by land, US$10 a tonne handled) and the closure; WN3 recalibrates them.
- **★5 Real prices are the Pink Sheet's nominal over the US CPI** (the study's and WN1's way), not its own
  MUV-deflated sheet. 11 goods have their own series, 49 follow a proxy (fuels crude, steel iron ore, made
  goods the MUV index); the US CPI of 2025 is an estimate (WDI lacks it).
- **★6 The splits and shares are estimates.** BACI HS22 holds every refined oil in one code, so the six
  fuels share one pattern; coal is split 0.4 coking and 0.6 thermal; logs 0.6 sawlogs and 0.4 pulpwood.
  Each good's traded share, with its basis, is in `goods.tsv`.

## 5. Found on the way

1. **WN3's January solve: plain Sinkhorn is too slow with each nation's own pair.** A nation that trades a
   ten-thousandth of its use (San Marino) has a and b chase each other through its domestic flow. From cold
   to 0.01% plain sweeps take 152 to 16,205 (two goods not within 20,000), against the study's "about 61".
   Solving each nation's a and b together, exactly (a quadratic in its own flow), takes 7 to 288 passes to
   0.01% and reaches 1e-12. `econ_trade.solve_exact` and `WorldDataCheck.solve` do it; WN3's brief should
   take it.
2. BIS keeps the old ouguiya and dobra and changes Venezuela's units at each redenomination: restated to
   MRU, STN and the 2021 bolívar. Its Syrian series is stale from 2011 (WDI's used). North Korea's money is
   the official 100 a dollar, Cuba's 1.0, Ecuador's sucre 25,000: constants, marked estimates.
3. Taiwan's output is PWT's, North Korea's Maddison's by peers, Eritrea's extended 14 years by its 2007-11
   growth (probably too high). 45 nations have CPI cells filled from their GDP deflator. Every fill is listed
   in the run's `work/econ/fills-paths.txt`.
4. 867 one-degree tiles have earth-2 land but no Natural Earth polygon (395 Antarctic ice shelf, 472 islets
   10m leaves out). None is a border tile, so the check's rule holds; WN5 should give them the nearest
   nation.
5. Natural Earth gives Yei more people than Juba; South Sudan's node is pinned to Juba (`geo.NODE_OVERRIDES`).

## 6. The checks, the trace

- `./batch-check.sh --pick`: exit 0, WorldDataCheck and TimingCheck run, Stale no firm finding. Its review
  line says "threading": `WorldData`'s tile cache and identity are `synchronized`, as `EarthData`'s; nothing
  calls the class yet.
- **WorldDataCheck with the full files** (`-Dcbs.world`, `-Dcbs.earth`): all passed.
  - The reader equals worldread.py on every expect line.
  - The 196 are present and numbered; the sums close.
  - The freight closure from the edges differs from the stored table by at most 3.65e-16.
  - The base year's solve from a = b = 1: 25,803 passes in all, largest flow error 8.8e-11, 0.4 s.
  - BACI's own sums equal the fixture's 6,647 kept pairs.
  - All 124,872,431 land cells of the 2,383 border tiles are owned.
- **The trace**: the default playtest's log is byte-identical to main's (1a907f71) but for its two run-time
  lines, on the final tree. So no ensemble (EN1's rule).
