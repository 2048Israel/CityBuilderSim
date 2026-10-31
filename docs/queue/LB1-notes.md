# LB1 - Rankings: the measures file (0.8.80, 2026-10-30)

The batch's record. The study's LB1 row (`docs/studies/spec-leaderboards.md` §11) with §6 (the 70 measures), §8 (the
file) and D7 (the peers): the real world's latest figures, baked into the resources for the Rankings' **Real world
2025** board. Lane B's first batch of the night line (2026-10-30). Opus wrote the contract, the tables and the briefs,
reviewed and committed; three Sonnet 5.5 slices at xhigh found the sources (A), wrote the builder (B) and the reader
and harness (C). Nothing in the game reads the file yet: the default playtest's trace cannot move, so no ensemble (EN1).

## 1. What it is

- **`src/main/resources/world/world-2025.json`** (386 KB): 196 nations and 62 city regions, each with its kind, size
  band (digits of its people less one) and up to 70 measures; GDP and GDP per person in three moneys (current US$,
  constant 2015 US$, PPP); for a region its nation, its boundary (`def`) and its shares of the nation's people and
  output. A figure is `[value, year, mark, source]`, plus a rule id when the mark is ○ (1). A missing figure is absent,
  never 0. 9,643 figures, 178 of them the game's estimates. The meta: the sources with their licences, the rules' text
  (the drawer's "how a ○ was made"), the two city boundaries, the 70 measures as the study lists them (group, unit,
  arrow, money, scope N/NC/Y, W), the variants, the counts, and the **CPI-U's annual averages 2015-2025** with
  2015 over 2025 (0.73621...).
- **`tools/world/`**, beside WN0's pipeline and without touching what it bakes: `FORMAT-2025.txt` (the contract),
  `measures.tsv` (the 70 and where each comes from), `regions.tsv` (the 62, with their WUP and OECD codes),
  `measures_fetch.py` (the downloads, reproducible, never WN0's six WDI files), `measures.py` (the builder; the same
  inputs make the same bytes). README's stages 6 and 7; ATTRIBUTION.txt's world-2025 credits.
- **`WorldMeasures`** (model): the reader. **`WorldMeasuresCheck`**: the harness (§4).

## 2. Coverage (nations of 196 with a figure dated 2015 or later)

| | measures |
|---|---|
| 180-196 | GDP (each money), GDP per person, real growth, 10-year growth, current account, unemployment, participation, inflation, currency against parity, price level, people, growth, births, deaths, child deaths, life expectancy, aged 65+, old-age dependency, net migration, area, density, health spending, electricity, renewable share, oil |
| 140-179 | household spending, investment, government spending, exports, imports, trade balance, tertiary, reserves, M2, budget balance, tax, interest, bank capital, bad loans, credit, homicides, going hungry, water |
| 33-133 | real interest rate 133, stock market 85, public debt 65, policy rate 58 (BIS; the euro area's rate for its 20), yield 45, pensions 42, average wage 41, homes 42, minimum wage 36, homeless 36, household debt 34, Kaitz 33 (the OECD's) |
| none | the 4 "yours" (D10), and 11 with no source a game may ship: debt in foreign money, city fund, credit rating, people per household, rent, households sharing, home price to income, cars, transit, prisoners and police (UNODC, ★7) |

So 55 measures carry real figures (the study counted 66); the other 15 rank only the player's cities, D10's rule.
Taiwan, which the WDI does not cover, has its GDP and people from world-1.bin (rule `world-1`) and little else.
City regions: GDP (three moneys), people, growth, area and density for all 62; GDP per person and real growth for the
16 on the OECD's boundary.

## 3. Sources and their licences, each read before a figure went in

- **The World Bank's WDI** (49 series): CC BY 4.0, and each series' own metadata (`License_Type`) says CC BY-4.0,
  homicides (UNODC's figures, republished) among them. PA.NUS.PPPC.RF (price level) is archived; the same ratio is
  made from PA.NUS.PPP over PA.NUS.FCRF.
- **The OECD** (earnings, yields, social expenditure, households, functional urban areas, the Affordable Housing
  Database): its terms' Data section, "for any purpose, even for commercial use", with credit.
- **The BIS** (policy rates): unrestricted with the BIS cited and no charge for the data itself.
- **The UN's World Urbanization Prospects 2025** (cities of 50,000 or more): CC BY 3.0 IGO.
- **The US EIA** (electricity, oil) and **the BLS** (the CPI-U): public domain.
- **world-1.bin**'s 2025 figures for the nations the WDI misses: its own sources (WDI, WPP 2024, PWT, Maddison).
- **Not used:** the IMF (WEO, FSI), UNODC directly (★7), World Prison Brief, rating agencies, Numbeo, OICA.
- Quirks handled: the WDI gives Bulgaria's PPP factor in euros and its rate in levs (and the OECD its minimum wage in
  euros): converted at the peg. The WDI converts six nations' GDP at another rate (AGO BDI NPL SYR UGA ZWE) and gives
  Cyprus its own per-person population, so a handful of published figures miss the identities; they are kept as
  published, and the harness holds 95% (§4, 7).

## 4. The harness, `WorldMeasuresCheck`

Seven sections, 90 checks (92 with world-1.bin named, as the gate names it). `problems(WorldMeasures)` is the file's
rules in one method: the real file has none, and **36 copies of it, each changed in one place** (a GDP of 0, a figure
for a `none:` measure, a source "imf", an estimate without its rule, a blank licence, a share of 1.2, the ratio's last
digit, a falling CPI year, ...), each have exactly that one problem, so every rule is shown to catch what it names.
1. The file: name, year, window, under 1 MB; 70 measures in the study's ten groups (11, 6, 8, 8, 5, 11, 5, 4, 4, 8),
   13 NC, 4 Y, and D15's ten on Your world by key.
2. Every nation of world-1.bin present once, with its name (the fixture's twelve always; all 196 when the file is
   named); no region id is a nation's.
3. A missing figure is absent, never 0: Taiwan has no WDI figure and its GDP and people from world-1.bin; no `none:`
   or Y measure anywhere; a region only NC measures; every value finite, the positive measures over 0 (oil may be a
   published 0: EIA's, 89 nations).
4. Every mark, source and rule resolves; every source names its licence and url; none is the WEO; every source and
   rule is used; the counts equal a recount.
5. A region's shares of its nation are over 0 and under 1, its nation a nation, its def a definition, its people
   under the nation's.
6. The CPI-U: 2015 and 2025 are the BLS's 237.017 and 321.943 exactly, the ratio their division to the last bit
   (0.73620796227903695), every year of the window present and rising.
7. The moneys agree: the US's price level and currency against parity are 1; gdp.ppp / gdp is 1 / price level within
   1% for 166 of 172 nations (96.5%; the six the WDI converts at another rate), gdp_pp is gdp / people within 1% for
   177 of 182 (97.3%); the floor is 95% over at least 100 nations; bands from people; a region's GDP under its
   nation's; the UN-boundary regions without GDP per person, the OECD ones with all three.

## 5. What LB2 reads

- `WorldMeasures.load()` (cached) and `parse(json)`; `measures()` in the study's order, `measure(key)` with
  `arrow()` (UP, DOWN, NONE: lower-is-better ranks ascending), `money()`, `scope()` / `forRegions()` / `yoursOnly()`,
  `yourWorld()` (D15's ten); `places()`, `nations()`, `regions()`, `place(id)`; `Place.figure(key)` (null when
  absent: the row is left off, never 0) and `value(key)` (NaN); `Figure.year()` (the "as of"), `mark()` (3 ●, 2 ◐,
  1 ○), `source()`, `rule()`; `sources()`, `rules()`, `definitions()`, `marks()` for the (i) and the drawer; the counts.
- **The pivot (D1, D29):** a figure in current US$ is in its own year's dollars: times `cpiU(2015) / cpiU(year)`
  it is in real 2015 dollars. `gdp.k` / `gdp_pp.k` are already constant 2015 US$ (Convert at: constant 2015, D14);
  `gdp.ppp` / `gdp_pp.ppp` are current international $ (Convert at: PPP), a ○ `ppp-market` copy of the market figure
  where none is published (D13). The other money measures (household spending, wages) come in current US$ only:
  at PPP they convert by the nation's gdp.ppp / gdp of the same year, at constant 2015 by gdp.k / gdp.
- **City regions on Your world:** `sharePeople()` and `shareOutput()` times the nation's path (§4 of the study);
  the 46 `wup` regions' output share is their people share (○), and they carry no GDP per person.
- **Kinds:** a nation's id is its ISO3 (as `Nations` and `WorldData`); a region's is regions.tsv's (Mexico City is
  CMX, so no region shares a nation's code).

## 6. ★ Calls taken, on the recommendation

1. **The 62 cities** (D7, "about 60"): the 25 largest by continent, 27 capitals over 2M (the largest, spread over the
   continents; every capital over 2M would be some 70 alone) and 10 small cities near the game's sizes (Halifax,
   Reykjavik, Hobart, Galway, Bergen, Salzburg, Dunedin, Tartu, Uppsala, Boulder). Why: D7's count is Jerus's answer.
2. **The window 2015-2025:** a figure older than 2015 is left out, not carried. Why: an electricity figure of 2014 on a
   2025 board says nothing true about 2025.
3. **Each city on one boundary:** the OECD's functional urban area where it publishes the city's share of its nation's
   GDP (16), the UN's urban centre (WUP 2025) for the rest (46). Why: a share of output and a count of people of two
   boundaries would make a GDP per person neither has.
4. **The 46 UN-boundary cities' output share is their people share** (○, rule `share-people`), and they get no GDP
   per person. Why: no open source publishes their output; the per-person figure would only repeat the nation's.
5. **Credit to firms and households from the WDI** (176 nations) rather than the BIS (44). Why: one definition across
   the board; the study named the BIS when the WEO was still in.
6. **WUP 2025 for city people**, the UN Population Division's cities companion to the WPP the study names. Why: the
   WPP has no cities, and WUP's terms are the WPP's.
7. **Prisoners and police left out** (UNODC): the portal's current terms allow "personal, non-commercial use" only;
   its older terms let anyone copy the data with credit. A Pending row asks Jerus; the two pointers are written in
   the sources folder's MEASURES-SOURCES.txt, ready.
8. **The OECD's city GDP is at constant prices:** its metadata does not say, but the US areas' implied national total
   falls 2.2% in 2020 and rises 5.8% in 2021, as real GDP did (-2.1, 6.2), not nominal (about -0.9, +10.7). So the
   16 get real growth (Bergen's published -10% kept).

## 7. How it was done

- Opus wrote `measures.tsv` (each measure's WDI series chosen against the study's definition), `regions.tsv` (D7's
  rules) and `FORMAT-2025.txt` first, so the builder and the reader were written to one contract.
- **Slice A** (Sonnet): found and read each source's terms, wrote `measures_fetch.py`, downloaded about 130 MB into
  `C:/Users/Jerus/Documents/world-sources/` (wdi oecd bis unodc wup eia, and bls since), filled the 13 pointers and
  the cities' codes, and wrote `MEASURES-SOURCES.txt` there. Opus checked the WDI's 49 series' licences one by one.
- **Slice B** (Sonnet): `measures.py`. Review sent back three changes: no GDP per person for the people-share cities,
  the OECD's city GDP read as constant prices on the evidence (★8), the README's stages. Opus added the CPI-U's
  every year (a 2023 dollar needs 2023's prices) and the credits.
- **Slice C** (Sonnet): `WorldMeasures`, `WorldMeasuresCheck`, AllChecks, the tree. Review fixed one sentence (an
  arrow-less measure ranks largest first, it is not left unranked) and the tree's claim that the board reads it now.
- To rebuild: `python -I tools/world/measures_fetch.py` (skips what is there), then `python -I tools/world/measures.py`
  (`--made yyyy-mm-dd` to keep the bytes), then `WorldMeasuresCheck`. **One in, one out** does not bind: the batch
  changes no script, check machinery or rule a session reads (`tools/world/` is the data pipeline).

## 8. Checks

`./batch-check.sh`, the full suite with both data files named as the gate names them (2026-10-31): **exit 3**, nothing
red but main's recorded reds (PassThroughCheck, ScaleCheck: `docs/notes/gate.txt`); compile OK; two timing figures
SOFT and not counted (MapCheck, side by side with lane A); maps regenerated; Stale 0 firm. WorldMeasuresCheck green:
90 checks plain, 92 with world-1.bin. The default playtest's trace is main's by construction (no model class reads
the file or the reader), so the ensemble is skipped (EN1).
