# LC1 — data licences for a sold game ({next}, 2026-10-31)

Jerus, 2026-10-31, answering LB1-U: "note, this game will be for commercial use, so only use sources for the rankings
that allow that". The game is sold, so LC1 read the licence of every data source it ships, and of every series EP0
found for EP1. The reference is `docs/studies/licences.md`; this note is what was done and what is left.

## 1. How it was read

- Four research agents (Sonnet, xhigh) read 73 sources' terms on the providers' own sites on 2026-10-31, and the
  49 WDI series' own metadata, each writing the page,
  the licence, the sentence that decides commercial use and the credit asked; Opus judged every row and read the
  World Urbanization Prospects FAQ itself (its answer is drawn by script, so a plain download shows nothing: the
  rankings agent called it unclear, LB1 had it right).
- **The chain rule** (licences.md, The rule): a figure's licence is that of the copy taken, but where the publisher
  says a part is a third party's under the third party's terms, those are read too. The World Bank's dataset terms say
  exactly that, and every WDI series names its maker. All 49 of LB1's series carry License_Type CC BY-4.0, and the
  Bank lists no extra condition for any; but the IMF (14 series) asks permission for commercial reuse, and UNODC, the
  WHO, the WFE, UNICEF (the UN IGME's site) and UNESCO (whose two pages disagree) do not allow it or do not say.
  Taken as the brief asks: unclear is no.

## 2. The verdicts in short

- **Yes:** the World Bank's own series (national accounts, ICP, population), the UN's WPP 2024 and WUP 2025, the
  ILO, the FAO (one condition, ★2), the IEA (its browser's CC BY notice), the OECD, the BIS (★3), the EIA, the BLS;
  every world-1 source but two WDI series; every earth-2 source; the fonts (OFL 1.1, its text beside them in the jar).
- **No:** the IMF's series in the WDI (inflation, the CPI, the exchange rate, public finance, the current account,
  reserves, broad money, private credit, bad loans, bank capital), UNODC, the WFE, the WHO; unclear so no: the UN
  IGME, UNESCO's UIS.
- **EP0, for EP1:** no to the NBER Macrohistory series, the Minneapolis Fed's CPI, the Correlates of War list, the
  Bank of Japan, the Swiss National Bank, Grilli-Yang, FRASER's scans, the WDI's CPI and rate, beside the JST, the IMF
  and MeasuringWorth EP0 had refused; unclear so no: the Bank of England's millennium dataset, Historical Statistics
  of Canada, Jacks, the IRR codes, FRED as a conduit. Yes to the BIS, the Bank of Canada, the Bundesbank, the ECB, the
  RBA, Clio Infra (CC0), OWID, HYDE, Gapminder, Statistics Canada's own table, the US federal sources. What EP1 uses
  instead is the LC1 note under ★11 of `spec-1900-start.md`.

## 3. What left the shipped data

- **The Rankings' file** (`world-2025.json`, re-made by `measures.py --made 2026-10-31`): 17 measures rank the
  player's cities only, each `none:` in `measures.tsv` saying whose figure it was and why: inflation, real_rate,
  reserves_months, m2, credit_private, debt, budget_balance, tax_revenue, interest_revenue, current_account,
  bad_loans, bank_capital (the IMF's), homicides (UNODC's), stock_market (the WFE's), health_spend (the WHO's),
  child_deaths (the UN IGME's), tertiary (UNESCO's). 32 of the 70 measures are now the player's alone (15 before).
  Figures: 7,049, from 9,643. Prisoners' and police's reasons now cite Jerus's answer: LB1-U is settled and its
  Pending row can go.
- **Kept on the Bank's own series:** price_level is NY.GDP.MKTP.CD over NY.GDP.MKTP.PP.CD (market GDP over PPP GDP:
  the PPP factor over the rate the Bank converts at), fx_vs_parity its inverse, so LB2's PPP view of Your world keeps
  its price levels. The identity gdp.ppp/gdp x price_level = 1 now holds for all 188 nations (6 were off before).
  Most nations' price level moves under 1%; Eritrea loses its figure and Turkmenistan gains one; the six nations the
  Bank converts at another rate move to what their GDP implies (★5). The OECD's wages are divided by
  NY.GDP.MKTP.CN over NY.GDP.MKTP.CD, the Bank's own rate: identical but for Australia and New Zealand (+0.4% and +1%,
  their accounts run on fiscal years) and Bulgaria, whose WDI output is now in euros (`WDI_LCU`; its average wage,
  in levs, converted at the peg). `measures_fetch.py` fetches NY.GDP.MKTP.CN in place of PA.NUS.PPP.
- **The harness:** `WorldMeasuresCheck.POSITIVE` loses the five measures with no figure left (child_deaths,
  tertiary, reserves_months, m2, health_spend), so that its "every POSITIVE key has a figure somewhere" still tests
  something; no other premise moved. RankingsCheck reads whatever is sourced and needed nothing.
- **Not here:** world-1.bin's two IMF series (its paths' CPI and the annual rate where the BIS has no month) are the
  re-bake LC3, as the brief asks. world-1.bin does not ship beside the exe today; its 12-nation fixture is in the jar
  (★4).

## 4. The credits

- `Build EXE.bat` copies `tools/world/ATTRIBUTION.txt` beside the exe as `data/world-ATTRIBUTION.txt`, beside the
  Earth's: the Rankings' file and both fixtures are in the jar, and nothing gave their credits to a player.
- Both attribution files claimed "the Credits page", which does not exist; they now say where they ship. Brought to
  what each licence asks today: BACI's date of last update and its licence's live link (Etalab requires both),
  Maddison's DOI and the original sources it asks for when fewer than 12 nations are used, GEBCO's OGL line (the CEDA
  copy is under OGL v3.0), WUP's own citation and licence link, the WDI's originators with the FAO's form and the
  IEA's derived-work notice, the OECD's access date, the EIA's publication date, and a line that the figures are
  changed, as CC BY asks. `sources2.py` writes ATTRIBUTION-2.txt with LF now (it wrote CRLF on the PC).
- Where they belong: a Credits page from the title screen (CR1, queued), and the eye-check row LC1-C.

## 5. The queued batches' briefs

- **LC2, the Rankings' measures back from sources that allow it.** For each `none:` of §3, the source to try, each
  read into licences.md first: inflation from ILOSTAT's consumer prices (CC BY 4.0 if ILOSTAT's own terms page, which
  blocked the agent, agrees) or else the Bank's GDP deflator (NY.GDP.DEFL.KD.ZG, its own national accounts), the note
  saying which; child_deaths from WPP 2024's under-five mortality (CC BY 3.0 IGO); credit_private from the BIS's
  credit to the private non-financial sector; debt, budget_balance, tax_revenue, interest_revenue, current_account
  and health_spend from the OECD (members only, as the wages are); real_rate as the BIS policy rate less the
  deflator's inflation, if the study's definition allows. Reserves, broad money, bad loans, bank capital, homicides,
  market capitalisation and tertiary enrolment stay the player's unless a maker's terms change. Keep
  WorldMeasuresCheck green, put a returned measure back in POSITIVE where it cannot be 0.
- **LC3, world-1.bin baked again without the IMF's figures** (WN0's pipeline, `tools/world/`): the paths' prices from
  the same CPI LC2 chooses (ILOSTAT, else the Bank's GDP deflator, NY.GDP.DEFL.ZS), the annual rate where the BIS has
  no month from the Penn World Table 11.0's xr (to 2023) and then the Bank's implied rate (NY.GDP.MKTP.CN over
  NY.GDP.MKTP.CD); the fixture re-cut (`fixture.py`), the sha, the release asset, the ATTRIBUTION's "Not yet" line
  removed, world-2025.json re-made on it. The ensemble runs if Historical's trace moves. Until it ships, world-1.bin
  stays out of the exe's folder.
- **CR1, a Credits page.** From the title screen: both attribution files in full, the fonts' notice (IBM Plex,
  Copyright 2017 IBM Corp., SIL Open Font License 1.1), and, when EP1 brings their data, the Bank of Canada's and the
  ECB's notice that their data are free on their sites. Checked by eye.

## 6. The calls, each with a recommendation

- ★1 **The WDI's third-party series, read the safe way.** The Bank labels them CC BY 4.0 and lists no condition, but
  its own terms leave a third party's figure to that party, and the IMF asks permission. The way back: the IMF's
  permission (copyright@imf.org) for its four databases, or Jerus taking the Bank's label as enough. **Recommended:**
  as done; LC2 brings back what other sources allow, and the IMF is asked only for what he still misses.
- ★2 **The FAO's added term:** its CC BY 4.0 data may not be used to promote a commercial product. Land area (so
  density), water and hunger stay; their figures are kept off the store page and trailers. **Recommended:** keep them
  (the alternative is land area from the Factbook's CC0 mirror, a small change in LC2).
- ★3 **The BIS's condition:** its data may not add a charge for the player. **Recommended:** as is; never sell the
  Rankings or the policy rates as a paid add-on.
- ★4 **The world-1 fixture in the jar** holds 12 nations' CPI and rates from the IMF until LC3. **Recommended:** LC3
  next in the queue (two series in one pipeline); the alternative, leaving the fixtures out of the shipped jar, touches
  the build for a few days' gain.
- ★5 **Six nations' price level** (AGO BDI NPL SYR UGA ZWE) is now the one their published GDP implies, since the Bank
  converts their output at another rate than the official one (Burundi 0.34 to 0.19). **Recommended:** as done: the
  board's GDP and its price level now agree.

## 7. The checks, and one in, one out

- `./batch-check.sh WorldMeasuresCheck RankingsCheck`: exit 0 after the pipeline; the gate's `--pick` result is in
  the ship file's paragraph.
- No process file changed (scripts of the checks, the rules a session reads): `Build EXE.bat` +3 -1, the pipeline
  and the harness's list are data code. licences.md is a study, read by data briefs only.
