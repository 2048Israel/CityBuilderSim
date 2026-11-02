# LC3 — world-1.bin baked again without the IMF's figures (0.9.1, 2026-11-02)

The brief is LC3's row in `QUEUE.md` and `docs/queue/LC1-notes.md` §5-6: 0.9.0 went to itch.io without
`world-1.bin` (IR1, on Jerus's "Push 0.9.0 without nations (Recommended)"), because its price paths were WDI's
FP.CPI.TOTL and its annual rates PA.NUS.FCRF, both the IMF's, which allows no commercial use. Run overnight beside
FC1 on Jerus's "Run both tonight"; the pipeline is Opus's, the map fix (§5) a Sonnet slice.

## 1. The choices

- **ILOSTAT is out.** Its terms page (https://ilostat.ilo.org/about/terms-of-use/) sits behind a bot check, as it did
  for LC1, and was not read; the ILO's own page (https://www.ilo.org/rights-and-permissions) puts its datasets
  published since 2023-05-03 under CC BY 4.0. That does not decide it: ILOSTAT's data API (rplumber.ilo.org) gives every
  figure a source note, and every headline figure of its annual CPI since 2000 (CPI_XCPI_COI_RT_A, 4,090 figures, 198
  areas) carries "Repository: International Monetary Fund". By licences.md's rule they are the IMF's.
- **So the prices are the World Bank's GDP deflator (NY.GDP.DEFL.ZS)**, the Bank's own national accounts: License_Type
  CC BY-4.0, made from national statistics, the OECD's accounts and the Bank's estimates. **LC2 takes the same
  choice** for the Rankings' inflation (NY.GDP.DEFL.KD.ZG). The exception is the US, which keeps its CPI, the BLS's
  CPI-U (public domain). It is the dollar the game's own price level inflates on (`PriceLevel`, 1900-2025), and
  PriceLevelCheck 2 holds the file's US path to it within 0.003. The US deflator was 5% off it by 2024 (§4).
- **Where the deflator is not prices in the nation's money** (`econ_paths_cpi.NOT_OWN_MONEY`), the years go to the
  deflator in its money (current US$ GDP x the rate / constant US$ GDP), as WN0 filled its gaps. Zimbabwe 2000-2008
  (the Bank restates those accounts at market rates, so the deflator misses the hyperinflation by 18 orders of
  magnitude). Liberia (accounts in US dollars). Cuba 2021-2025 (the peso is held at 1 while the deflator rose
  ninefold). The 11 micro-states on another's money (`ISSUER`: AND MCO SMR LIE MHL PLW FSM TLS KIR NRU TUV) take the
  issuer's index every year: a GDP deflator there is export prices (Nauru's phosphate and Timor-Leste's oil, 13% a
  year of noise). The bake prints how far each published deflator strays from the deflator in its money. 166 of 182
  nations are within 0.1 (log10). The widest are official-against-market rates (VEN MMR LBN IRN SYR SDN), where the
  published deflator is the true price level.
- **The annual rate** (`econ_paths_moneys.annual_rates`): PWT 11.0's xr up to 2023, then the Bank's GDP in local money
  over its GDP in US$. It fills only **a year with no BIS month of the money**: a year's mean put into the last
  months of a year the BIS covers in part would jump for a falling money, so those months stay with the rules after
  (log-linear to the next annual year, else carried). `ANNUAL_SKIP` adds four moneys: Liberia's dollar (the ratio is 1,
  its accounts in US$), Iran's rial (the Bank's dollar GDP at about 453,000 rials against the BIS's official 42,000),
  and Cuba's peso and Malawi's kwacha, which keep the rates their dials give (CUP_PER_USD, the 1,751 peg).
- **The prices layer** divides by the BLS's CPI-U, not by the paths' US index. These are the same figures as WDI's US
  CPI within 0.02% (the IMF's copy was the BLS's), so the layer moves under 0.2% and keeps its meaning.
- Downloads, each in `C:/Users/Jerus/Documents/world-sources/`: `wdi/NY.GDP.DEFL.ZS.json` (WDI of 2026-10-08, as the
  rest), `bls/CUUR0000SA0-2000-2009.json` and `-2010-2014.json` (`measures_fetch.bls_file`), and `ilostat/` (the CPI and
  its two dictionaries, read and not used). NY.GDP.MKTP.CN was already there (LC1). The licence record is
  `docs/studies/licences.md` (five rows, LC3's line in §3); `tools/world/ATTRIBUTION.txt` credits the WDI's two new
  series, PWT's xr and the BLS, and its "Not yet" line is gone.

## 2. The file, and what the orchestrator runs

- **`C:/Users/Jerus/Documents/world-sources/work/out-lc3/world-1.bin`**, 8,712,947 bytes, made 2026-11-02, SHA-256
  **`fbd11a6a3b880ffeeae4e5c07e7a9d950b3d28f17f7c77656aea7a1071a6fcc1`** (`tools/world/world-1.sha256`). The stages'
  files are in `work/lc3/econ/`. WN0's file at `work/out/` and `data/world-1.bin` (`78a6e10e…`) are untouched.
- Re-run with `python -I tools/world/econ_paths.py --work <sources>/work/lc3`, then `bake.py --econ <that>/econ --out
  <sources>/work/out-lc3/world-1.bin --made 2026-11-02` (`--out` writes no sha file: it was written by hand, the same
  line), `fixture.py --full <the file> --econ <that>/econ`, `measures.py --world <the file> --made 2026-11-02`.
- Gate with `-Dcbs.world=C:/Users/Jerus/Documents/world-sources/work/out-lc3/world-1.bin`. After the ship: copy it over
  `data/world-1.bin`. Then publish the release (WN0-R, Jerus 2026-10-31: "After LC3 (Recommended)"). `--latest=false`
  keeps "Civic Ledger (0.9.0)" the repository's latest, as the earth-2 release is beside it:

      gh release create world-1 "C:/Users/Jerus/Documents/world-sources/work/out-lc3/world-1.bin" --repo 2048Israel/CityBuilderSim --latest=false --title "World data (world-1)" --notes "The nations' world (WN0), baked again by LC3 without the IMF's figures. SHA-256 fbd11a6a3b880ffeeae4e5c07e7a9d950b3d28f17f7c77656aea7a1071a6fcc1, as tools/world/world-1.sha256."

- `Build EXE.bat` now calls `get-earth.bat nopause world-1` after the Earth (it fetches the release when
  `data\world-1.bin` is missing or does not match the sha), stops without it, and copies it beside the exe with the
  credits. So **the next itch.io build carries the nations**, and needs the swap or the release first.
- The fixture (`world-1-fixture.bin`, 12 nations) was cut again and checked against the full file: all equal.
  `world-2025.json` was re-made on the new file and is the same but its made date (nothing it takes from world-1.bin
  moved).

## 3. What moved

- **Identical:** people, the three GDP series, the trade, the freight, the owners, the lines, the nations.
- **Moneys, 8 of 158**, each toward a published figure. Somalia's shilling: PWT's xr every year (2000: 9,572 a dollar,
  where WN0 carried 2009's 31,559 back). Syria's pound 2023: PWT's 7,412 (WN0 carried 2022's 2,506). Sudan's pound
  2024-25: the Bank's (2,213 at 2025-12, where 2023-02's 586 was carried). Zimbabwe Gold 2025: 31.80 (the Bank's; the
  IMF's was 26.60). Afghanistan 2022-24, Guinea 2022-25: within 15%. Mauritania, Sierra Leone: under 1%. Myanmar's
  kyat is filled otherwise and is the same (the Bank's 2,100 is the rate WN0 carried).
- **Price paths:** every nation but the US. Against the US's, the median nation's widest year moves 17%; 68 of 196
  move more than 25%. The most moved (new over old, prices against the US's):

| nation | year | new / old | why |
|---|---|---|---|
| VEN | 2021 | x0.03 | the deflator in bolivars, where WN0's fill rode the official rate of 2015 |
| ZWE | 2023 | x8.94 | the RTGS years' deflator against WN0's CPI |
| SYR | 2025 | x4.80 | the deflator (to 2022, filled in its money after) against WN0's CPI |
| SSD | 2025 | x3.43 | the deflator ends in 2015; filled in its money since |
| SOM | 2002 | x3.04 | the deflator over PWT's rate, where WN0 used Kenya's CPI |
| GHA LBY GUY | 2000-03 | x0.33-0.40 | gold and oil in the deflator |
| IRQ | 2000 | x2.74 | oil in the deflator |
| MDV TKM SDN MWI TJK BLR | 2000-25 | x0.45-2.02 | the deflator's inflation against the CPI's |

## 4. The calls

- **★1 The household consumption deflator** (NE.CON.PRVT.CN over NE.CON.PRVT.KN, the Bank's own accounts) tracks
  consumer prices much better than the GDP deflator. Over 157 nations with all three series, the median range of
  ln(index / CPI) over 2000-2025 is 0.14 against 0.22 (Iraq 0.02 against 1.13, Kuwait 0.02 against 0.58, China 0.04
  against 0.24, Libya 0.61 against 1.48). The brief named the GDP deflator, so it shipped. **Recommended:** WD1's
  re-bake, already queued after this one, takes the consumption deflator (its two series read into licences.md
  first), and LC2 the same for inflation; the alternative is to keep the GDP deflator.
- **★2 Zimbabwe Gold's 2025 rate is the Bank's 31.80**, not the official 26.6 the IMF gave. **Recommended:** as done,
  so the board's dollar GDP and its rate agree, as LC1 ★5 chose for the price level.
- **★3 HIGH's R2 in the ensemble** (§5): the high-rate policy's cost in unemployment is now below its bar, on seeds
  that spread 23 points. **Recommended:** ship, and read R2 again at WD1's re-bake, the next ensemble on new paths;
  the alternative is to hold LC3 for a study of HIGH on the new paths, which would keep the nations from players.
- The US on its CPI-U, the annual rate in whole years only, and the skip lists are not calls: §1 gives the reasons.

## 5. The checks

- `./batch-check.sh --pick` (18 harnesses), with earth-2 and the new file named, found three reds. PriceLevelCheck 2:
  the US deflator against PriceLevel's CPI-U, mended by giving the US its CPI-U. WorldDataCheck: the WDI source row gave
  two download days where the meta takes one; it now gives its last series' day, and the file was baked again, meta
  only. MapCheck 8 on the played city: "294 road plots in 3 piece(s) ... 2 of them the city's own ground joins to it".
  On the old file MapCheck 8 is green with the same code; the played city grows otherwise (month 121: 3,865 people and
  26 purchases, against 4,427 and 22).
- **The map fault** (a Sonnet slice, reviewed): in district (0, 0), two street pieces (83 and 73 plots) were walled in
  by belts of houses standing on the iron field's free plots (SITE_FIELD, built on last). The cheapest way across
  crossed 8 plots of houses, under WALL_MOST, but `DistrictPlan.wallJoinable()` refused any building on a site, a
  field's among them, so FW11's join found no way. Now it bars only a mine's or well's own site (SITE_MINED), as FW11's
  doc said; the district is planned again with 39 plots kept clear, and 330 road plots stand in 1 piece. This is
  **MF2's item 3 (EM1-M), settled here**: `docs/queue/MF2.md` says so, and PlanCheck's `groundParts` is back to the
  planner's rule, as MF2 asked once the planner changed. **PlanCheck 18** is the fixture: a cell with a band of mine
  sites and one block of field, the houses filling it, red without the fix (4 assertions) and green with it. Across
  MapCheck's and PlanCheck's cities the case counts 1 district before and 0 after. The model's months do not move
  (MapCheck 1's month-121 line and LongPlaytest's report are the same with and without the fix).
- **The full suite** with the final file (SHA fbd11a6a…c1): 169 of 169 in 1,907 s, exit 0. It holds the 31 harnesses
  `--pick` names on the final diff. Two timing lines were SOFT, not counted: SaveSlotCheck (main's, gate.txt) and
  MapCheck's 5B map work (19 ms; the slice found its paired runs with and without the fix indistinguishable under the other
  session's load). Maps regenerated; Stale 0 firm; the tree clean.
- **The trace moves** (the playtest trades on the file's paths), so the ensemble ran once (80 of 80, 12.9 min, both
  files named; `docs/ensemble/LC3.md`): 22 of 26 against 0.8.95's 23. The autopilot is better: A1 16/16 in band (14),
  A2's longest spell 16 months (20), S1 2.03% (2.09%), T1 16/16, T2 345,461 people (336,202). HIGH is worse: R2 fails
  (unemployment against the twin, median -1.22 points, where at least +0.50 is asked; 0.8.95 +1.68) and R3 has 7 seeds
  above +1 point (3). R2's seeds run from -12.8 to +10.8 points (5 of 16 above 0), so its median moves with any change
  of the path: ★3.
- One in, one out: no process file changed (`Build EXE.bat` is the build: +28 -2; `get-earth.bat` a comment, +3 -2).
