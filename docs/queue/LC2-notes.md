# LC2 notes ({next}, 2026-11-08): the Rankings' measures back from sources that allow sale

The row is `QUEUE.md`'s "The Rankings' measures back from sources that allow commercial use", with Pending LC3-L ★1
(inflation as the household consumption deflator) and LC1's notes §5 (the real rate from the BIS policy rate if the
definition allows; WorldMeasuresCheck green; the returned measures back in POSITIVE where they cannot be 0). Built on
main at 1751f426 (CD9, 0.9.57). Each licence read into `docs/studies/licences.md` first (2d71d679); one slice on Sonnet
5.5 at xhigh for the reader, the table and the re-bake, reviewed here as a stranger's (7955d57e).

## 1. What it built

Nine of LC1's seventeen measures rank the world's nations again, each from a copy whose terms allow a sold game to ship
its figures (`docs/studies/licences.md`, sections 2 and 3, has each licence with its link):

| measure | source (the copy taken) | nations | latest year |
|---|---|---|---|
| inflation | the WDI's household consumption deflator (CC BY 4.0) | 164 | 2025 for 126, 2024 for 31, older for 7 |
| deaths under five | WPP 2024, Demographic Indicators, medium (CC BY 3.0 IGO) | 196 | 2025 |
| private credit | the BIS's total credit, `WS_TC` | 43 | 2025 |
| current account | the OECD's Economic Outlook 119 (CC BY 4.0) | 38 | 2025 |
| budget balance | EO119 | 34 | 2025 |
| debt | EO119 | 33 | 2025 (Norway 2023) |
| tax revenue | EO119 | 32 | 2025 |
| interest over revenue | EO119 | 33 | 2025 (Israel 2021) |
| health spending | the OECD's System of Health Accounts (CC BY 4.0) | 38 | 2025 |

- **Inflation** is the household consumption deflator's change, `wdi-growth:NE.CON.PRVT.CN/NE.CON.PRVT.KN`: the World
  Bank's household consumption at current prices over it at constant prices, each year's over the year before (LC3-L
  ★1). The Bank's own national accounts, CC BY 4.0; nearer consumer prices than the GDP deflator LC3 used for the paths,
  and not ILOSTAT's consumer prices, which are the IMF's.
- **Deaths under five** are WPP 2024's Q5 (the UN's World Population Prospects, Demographic Indicators, medium variant,
  CC BY 3.0 IGO): to 2023 an estimate, mark 3; 2024 and 2025 the projection, mark 2 (★4).
- **Private credit** is the BIS's total credit to the private non-financial sector from all sectors, at market value,
  % of GDP, adjusted for breaks (`WS_TC`, Q.P.A.M.770.A), each year's last quarter with a figure; mark 3. The BIS's terms
  as for the policy rate (LB1): reuse with the source named.
- **The OECD's members only** (★2): the current account (CBGDPR), general government gross financial liabilities
  (GGFLQ), net lending (NLGQ), taxes and social contributions received (TAXQ) and gross interest paid over total
  receipts (GGINTP over YRGT) from the Economic Outlook 119; current health expenditure over GDP, all schemes, from
  the System of Health Accounts. Mark 3; EO's projected years past 2025 ignored. A new `members=OECD` key filters to
  the 38 members (`OECD_MEMBERS` in `measures.py`), so no aggregate and no partner nation (EO119 carries 12 partner
  nations, among them Brazil, China and India, and 5 aggregates) is ranked.
- **The real rate stays the player's** (★1): it is the lending rate less the GDP deflator's inflation, and the only
  licensed rate found, the BIS's policy rate, is not a lending rate. Reserves, broad money, bad loans, bank capital,
  homicides, market capitalisation and tertiary enrolment stay the player's too, each `none:` saying why.
- **The reader** (`tools/world/measures.py`): four new sources. `wdi-growth:A/B` is one year's change of A over B
  (current over constant prices), a year dropped when B's step is over `GROWTH_BREAK` (1.45) times the year before's,
  a rebased series and not growth; `oecd_csv` gains `members=OECD` (the 38) and `over=` (one EO measure over another,
  GGINTP over YRGT); `total_credit` reads the BIS's bulk file, each year's last quarter with a figure; `wpp` reads Q5,
  mark 3 to `WPP_LAST_ESTIMATE` (2023) and mark 2 after. `measures.tsv`'s nine rows name them; `world-2025.json` is
  baked again (`--made 2026-11-08`), 217 lines changed in it.
- **WorldMeasuresCheck:** child deaths, health spending, debt, tax revenue and private credit back in POSITIVE (a
  figure of 0 or under there is a misread); inflation, the current account, the balance and interest can be 0 or under
  and stay out. The javadoc names the three measures LC1 left out that are still out (tertiary enrolment, reserves,
  broad money).
- **The credits:** `tools/world/ATTRIBUTION.txt` credits WPP 2024, the BIS's total credit, the Economic Outlook 119 and
  the health accounts, each with its licence and the citation its terms ask for.
- **Outside the repository:** `C:/Users/Jerus/Documents/world-sources/MEASURES-SOURCES.txt` names each new download
  with its URL, date and licence (the WDI line gains NE.CON.PRVT.CN and .KN), as LB1's did; `measures_fetch.py`
  fetches them.

## 2. The calls (★; each takes the recommended way, BP1)

1. **The real rate stays the player's (recommended).** LC1's notes asked for it from the BIS policy rate "if the
   study's definition allows". The measure is the lending rate less the GDP deflator's inflation (the WDI's FR.INR.RINR,
   on the IMF's IFS lending rate); a policy rate is the central bank's, a lending rate the banks' to their borrowers,
   several points apart in most nations and moving apart in a crisis. Ranking the policy rate less inflation under the
   name "real interest rate" would be a different measure; the alternative is a new measure, "the real policy rate",
   which nothing in the game asks for.
2. **The OECD measures rank the 38 members only (recommended).** The other nations show a dash for those six, as for
   any measure a source lacks. The alternative was the IMF's figures for the rest, which its terms do not allow for sale.
3. **The general government, not the central government (recommended).** LC1's rows were the IMF's central government
   figures; EO publishes the general government (states, regions, towns and social security in), the figure the OECD
   compares nations on. So debt is gross financial liabilities (larger than the central government's debt in federal
   nations), tax revenue includes social security contributions, and interest is over total receipts. Each row's note
   says so, and the table's heading shows it (`RankingsScreen`'s section head).
4. **WPP's 2024 and 2025 are its projection, at mark 2 (recommended).** The Rankings show the latest year; WPP 2024's
   estimates end at 2023. Taking 2023 alone would rank deaths under five two years behind every other people measure.

## 3. The checks

- **`./batch-check.sh --pick`: exit 3**, main's recorded red only (PriceLevelCheck, `docs/notes/gate.txt`). It picked
  six harnesses for the diff: LongPlaytest, PriceLevelCheck, LandNationsCheck, RankingsCheck, TimingCheck and
  WorldMeasuresCheck; five passed, no NEW red. The maps were regenerated with nothing to commit beyond 7955d57e; Stale
  0 firm.
- **RankingsCheck and WorldMeasuresCheck** also ran by name after the re-bake: both exit 0.

- **No ensemble, no trace:** the month never reads `world-2025.json` (the Rankings screen and its harnesses do), so the
  default playtest's trace cannot move.
- **No process lines:** the batch changes no script, check machinery or rule a session reads.

## 4. For the stranger's review

- **RankingsCheck section 16** proved a measure LC1 took out shows a dash for the world and the city's own figure; it
  named inflation, which is sourced again, so it went NEW red. It names the real rate now, the one measure still out
  that the city puts (`CityMeasures`), so the section keeps its premise and is not vacuous.
- **`GROWTH_BREAK` drops three years**: Bulgaria's 2025 (household consumption at constant prices +67%, a deflator of
  -32%), Vanuatu's 2017 and the Central African Republic's 2018. Bulgaria shows 2024's 2.18%. Zimbabwe's 1104% (2024)
  is kept: hyperinflation around the ZiG, not a break. Laos (2016) and Yemen (2018) show an old year, the WDI's last.
- **The OECD's gaps**: Chile, Colombia, Costa Rica and Mexico have no EO general government figures (their current
  account is there), Turkey lacks debt, interest and the balance, Slovenia tax revenue; hence 32 to 34 of 38. Norway's
  debt is 2023's, Israel's interest 2021's.
- **SHA's 2025 figures are provisional (22) or estimated (15)** and carried at mark 3, "published", as the OECD
  publishes them. The alternative is mark 2, as WPP's projection; a one-word change in the reader.
- **The BIS's total credit covers 43 places**, Hong Kong among them; `TC_ADJUST=A` is the series adjusted for breaks,
  as its code list says.
