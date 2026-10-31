# Data licences: what a sold game may ship

*2026-10-31, batch LC1. Jerus, answering LB1-U the same day: "note, this game will be for commercial use, so only use sources for the rankings that allow that". The game is sold, so the rule holds for every figure it ships. This note is the reference every later data brief reads first: before a source is used, its row here says whether it may ship; a source with no row is read and added here first.*

*Every page below was read on 2026-10-31, on the provider's own site, by four research agents and checked by Opus (three pages, the IMF's, the OECD's and the BLS's, refuse plain downloads and were read through a fetch tool or a browser). The terms are given in our words, not quoted; the URL is the page read. A study may still cite any source as evidence; only its figures may not reach the game.*

## The rule

- **Yes** means the provider's own terms allow use in a product that is sold. **No** means they forbid it, or ask for permission first. **Unclear** means no licence is stated, or the provider's pages disagree: it is treated as no until the provider says otherwise.
- **A figure's licence is the licence of the copy taken.** Where that copy's publisher says some of its parts are a third party's and carry the third party's terms, the third party's own terms are read too. The World Bank says so in its dataset terms, and names each WDI series' source, so a WDI series made by the IMF is the IMF's figure. Penn World Table, Maddison and the like licence their whole database as their own work, so a figure in them is theirs.
- **A compiler's own estimates are the compiler's:** the World Bank's national accounts and its ICP parities, the Penn World Table, the Maddison Project, Our World in Data's processing.
- **Facts are nobody's:** a date, who ruled a land, a gold par fixed in law. A page's wording is its author's: the game may state what Wikipedia says happened, never copy how it says it.
- **Credit as asked.** Each licence's attribution goes in the attribution file of the data it is in (`tools/world/ATTRIBUTION.txt`, `tools/earth/ATTRIBUTION-2.txt`), which ships beside the exe (`Build EXE.bat`).

## 1. What the game ships today

### The Rankings: `src/main/resources/world/world-2025.json` (in the jar)

| source | licence | read at | commercial | attribution it asks |
|---|---|---|---|---|
| World Bank, WDI: the Bank's own series (national accounts, ICP parities, population) | CC BY 4.0; each series' metadata says so | https://datacatalog.worldbank.org/public-licenses , https://www.worldbank.org/ext/en/legal/terms-conditions/datasets | yes | The World Bank: dataset name: data source; say what was changed |
| WDI series made by the IMF (IFS, GFS, BOP, FSI: 14 of LB1's 49, the exchange rate PA.NUS.FCRF among them) | the IMF's own terms for its data | https://www.imf.org/en/About/copyright-and-terms | **no**: commercial reuse needs the IMF's permission (copyright@imf.org) | - |
| The Bank's GDP at PPP (three series), which lists the IMF's WEO among its inputs | the Bank's own estimate (ICP parities carried forward) | as above | yes, as the Bank's; the WEO itself is never a source (spec-nations 3.3) | as above |
| WDI series made by UNODC (homicides); UNODC's own portal (prisoners, police) | UN site terms | https://www.unodc.org/unodc/legal.html | **no**: personal, non-commercial use only | - |
| WDI series made by the World Federation of Exchanges (market capitalisation) | all rights reserved | https://www.world-exchanges.org/our-work/statistics | **no** | - |
| WDI series made by the WHO (health spending, GHED) | WHO terms for its data compilations | https://www.who.int/about/policies/publishing/data-policy/terms-and-conditions | **no**: public health purposes only, no sale | - |
| WDI series made by the UN IGME (deaths under five) | none stated; the site's owner, UNICEF, reserves every right | https://www.unicef.org/legal | **unclear: no** | - |
| WDI series made by UNESCO's UIS (tertiary enrolment) | its pages disagree: CC BY-SA 4.0 on the UIS site, CC BY-NC-SA 4.0 in the World Bank's own catalogue | https://databrowser.uis.unesco.org/terms-and-conditions | **unclear: no** | - |
| WDI series made by the ILO (unemployment, participation) | CC BY 4.0 for ILO datasets published since 3 May 2023 | https://www.ilo.org/rights-and-permissions | yes | the ILO as the source |
| WDI series made by the FAO (land area, so density; water; hunger) | CC BY 4.0 with the FAO's own terms added | https://www.fao.org/contact-us/terms/db-terms-of-use/en/ | yes, on one condition: an FAO figure is never used to promote the game (the store page, a trailer) | FAO. year. dataset. accessed date. URL. Licence: CC-BY-4.0 |
| WDI series made by the IEA (renewable share) | CC BY 4.0 by the data browser's own notice, which the IEA's terms say governs | https://www.iea.org/terms | yes | IEA, licence CC BY 4.0, and that the game's figure is derived from IEA material, the game alone answers for it, and the IEA does not endorse it |
| WDI series from the UN's WPP (births, deaths, life, age, migration) | CC BY 3.0 IGO | https://population.un.org/wpp/downloads | yes | UN DESA, Population Division (2024), World Population Prospects 2024 |
| Eurostat, upstream of 9 WDI series | the Commission's reuse policy | https://ec.europa.eu/eurostat/en/help/copyright-notice | yes for the EU's, EFTA's and candidates' data, which is what the WDI takes from it | the source, and that it was changed |
| National statistical offices, upstream of the Bank's own series | about 200 bodies, not read | - | taken as the World Bank's compilation (the residual risk of this audit) | - |
| OECD data (wages, yields, pensions, households, homes, homelessness, functional urban areas) | the OECD's own terms for data | https://www.oecd.org/en/about/terms-conditions.html | yes: any purpose, commercial too; check a dataset's own source note | OECD (year), dataset, URL, accessed date |
| BIS, central bank policy rates (WS_CBPOL) | the BIS's terms for its statistics | https://data.bis.org/help/legal | yes, if the BIS is cited and the data adds no charge for the player | the BIS as the source |
| UN, World Urbanization Prospects 2025 (cities) | CC BY 3.0 IGO, "even commercially" (its FAQ, Data Use; the page draws by script, so read it in a browser) | https://population.un.org/wup/faqs | yes | UN DESA, Population Division (2025), World Urbanization Prospects 2025, Online Data, URL; a link to the licence; say what was changed |
| US EIA, International Energy Statistics (electricity, oil) | public domain | https://www.eia.gov/about/copyrights_reuse.php | yes | Source: U.S. Energy Information Administration, with the publication date |
| US BLS, CPI-U (the 2015-dollar pivot) | public domain | https://www.bls.gov/opub/copyright-information.htm | yes | the BLS as the source (asked, not required) |

### The world file: `world-1.bin` (not yet in the exe's folder) and its fixture `world-1-fixture.bin` (in the jar)

| source | licence | read at | commercial | attribution it asks |
|---|---|---|---|---|
| Natural Earth 5.1.2 | public domain | https://www.naturalearthdata.com/about/terms-of-use/ | yes | none needed; "Made with Natural Earth" suggested |
| CERDI-seadistance 1.1 | CC BY 4.0 | https://zenodo.org/records/240493 | yes | Bertoli, Goujon and Santoni (2016), CERDI Working Paper 2016/7 |
| CEPII BACI HS22 V202601 | Etalab Open Licence 2.0 | https://www.cepii.fr/DATA_DOWNLOAD/baci/doc/baci_webpage.html | yes (BACI is built from UN Comtrade, whose terms are narrower; CEPII licenses its reconciled flows as its own, and world-1 keeps only aggregates) | the producer and the date of last update; Gaulier and Zignago (2010) |
| WDI's NY.GDP.MKTP.KD, .CD, .PP.KD | CC BY 4.0, the Bank's own | as above | yes | as above |
| WDI's FP.CPI.TOTL and PA.NUS.FCRF (the paths' prices; the annual rate where the BIS has no month) | the IMF's (IFS) | as above | **no** | - |
| UN, World Population Prospects 2024 | CC BY 3.0 IGO | https://population.un.org/wpp/downloads | yes | as above |
| BIS, US dollar exchange rates (WS_XRU) | the BIS's terms | https://data.bis.org/help/legal | yes | the BIS as the source |
| Penn World Table 11.0 (and 10.01) | CC BY 4.0 | https://www.rug.nl/ggdc/productivity/pwt/ | yes | Feenstra, Inklaar and Timmer (2015), AER 105(10) |
| Maddison Project Database 2023 | CC BY 4.0 | https://www.rug.nl/ggdc/historicaldevelopment/maddison/releases/maddison-project-database-2023 | yes | Bolt and van Zanden (2024), doi 10.1111/joes.12618; for fewer than 12 nations, also the original sources its Sources sheet names |
| World Bank, the Pink Sheet (commodity prices) | CC BY 4.0 (catalogue entry 0038238); the Bank's own averages of quotes it buys | https://datacatalog.worldbank.org/public-licenses | yes | The World Bank: Commodity Price Data (The Pink Sheet) |

### The Earth file: `earth-2.bin` (beside the exe, with its credits) and `earth-2-fixture.bin` (in the jar)

| source | licence | read at | commercial | attribution it asks |
|---|---|---|---|---|
| GEBCO 2026 Grid and TID grid | public domain (gebco.net); the copy read from CEDA says Open Government Licence v3.0 | https://www.gebco.net/data-products/gridded-bathymetry/terms-of-use | yes; not for navigation; no endorsement implied | the grid's DOI citation; the OGL statement for the CEDA copy |
| NASADEM_HGT v001 | no restriction (NASA EOSDIS: CC0 1.0) | https://lpdaac.usgs.gov/products/nasadem_hgtv001/ | yes | NASA JPL (2020), DOI |
| SRTMGL1 v003 | no restriction (CC0 1.0) | https://lpdaac.usgs.gov/products/srtmgl1v003/ | yes | NASA JPL (2013), DOI |
| ESA WorldCover 10 m 2021 v200 | CC BY 4.0 | https://esa-worldcover.org/en/data-access | yes | © ESA WorldCover project 2021 / Contains modified Copernicus Sentinel data (2021) processed by ESA WorldCover consortium |
| HydroLAKES v1.0 | CC BY 4.0; the authors ask that the original not be redistributed whole | https://www.hydrosheds.org/products/hydrolakes | yes (the file holds derived shares and points) | Messager et al. (2016), Nature Communications 13603 |
| GRWL V01.01 | CC BY 4.0 | https://zenodo.org/records/1297434 | yes | Allen and Pavelsky (2018), Science 361 |
| USGS, Geologic Provinces of the World (WEP_PRVG) | public domain; use constraints none | https://www.sciencebase.gov/catalog/item/60ad2fd7d34e4043c850edb3 | yes | the USGS data release citation |

### Not data, but shipped

| what | licence | commercial | what it asks |
|---|---|---|---|
| IBM Plex Sans and Mono (`src/main/resources/fonts/`) | SIL Open Font License 1.1 | yes; may be bundled and sold, not sold alone | the copyright notice and the licence travel with the fonts: `OFL.txt` is beside them in the jar |

## 2. EP0's series, for EP1

EP1 will bake history from 1900 (`docs/studies/spec-1900-start.md`). The verdicts, by what each series gives; what EP1 uses instead is the study's LC1 note under ★11.

| source | licence | read at | commercial | note for EP1 |
|---|---|---|---|---|
| Our World in Data, population | CC BY for its own processing; inputs keep their licences | https://ourworldindata.org/faqs | yes | credit OWID and HYDE, Gapminder, UN WPP |
| HYDE 3.3 | CC BY 4.0 | https://api.datacite.org/dois/10.24416/uu01-aezzit | yes | before 1800 only |
| Gapminder population | CC BY 4.0; may be sold | https://www.gapminder.org/free-material/ | yes | the credit must say "free", CC-BY and gapminder.org |
| Maddison Project 2023; Penn World Table 11.0 | CC BY 4.0 | as above | yes | PWT 11.0, not 10.01 (FRED now serves 11.0) |
| BIS long consumer prices (WS_LONG_CPI); BIS policy rates | the BIS's terms | https://data.bis.org/help/legal | yes | the BIS cited; no extra charge |
| Clio Infra, inflation | CC0 1.0 (its Dataverse record) | https://dataverse.nl/dataset.xhtml?persistentId=doi:10.34894/MMUGQL | yes | cite de Zwart, Reinhart and Rogoff as a courtesy |
| Jordà-Schularick-Taylor Macrohistory Database | CC BY-NC-SA 4.0 | https://www.macrohistory.net/database/ | **no** | evidence only |
| IMF data (CPI, exchange rates, IFS discount rates, also through FRED) | the IMF's terms | https://www.imf.org/en/About/copyright-and-terms | **no** | evidence only |
| WDI's FP.CPI.TOTL, PA.NUS.FCRF from 1960 | the IMF's | as above | **no** | the Bank's own series (population, GDP) are yes |
| Statistics Canada, table 18-10-0005-01 | Statistics Canada Open Licence; may be sold | https://www.statcan.gc.ca/en/terms-conditions/open-licence | yes | its own source line |
| Historical Statistics of Canada (11-516-X) | none stated; a joint work, partly reprinted | https://www150.statcan.gc.ca/n1/pub/11-516-x/11-516-x1983001-eng.htm | **unclear: no** | Canada 1900-1913 by the gap rule |
| BLS CPI-U, PPI; BEA deflator; EIA; USGS DS140; Federal Reserve Board (H.10, Banking and Monetary Statistics) | US public domain | https://www.bls.gov/opub/copyright-information.htm , https://www.bea.gov/help/faq/145 , https://www.federalreserve.gov/disclaimer.htm | yes | cite each as the source |
| Federal Reserve Bank of Minneapolis, CPI 1800- | its site: research and information use only | https://www.minneapolisfed.org/site-information/disclaimer | **no** | the US 1900-1912 from a federal publication instead |
| MeasuringWorth | all rights reserved; non-profit education only | https://www.measuringworth.com/ | **no** | evidence only |
| NBER Macrohistory Database (also through FRED) | the NBER's site terms: personal, non-commercial | https://www.nber.org/terms-and-conditions | **no** | evidence only |
| FRED, as a conduit | its terms do not cover selling another owner's data | https://fred.stlouisfed.org/legal/ | **unclear: no** | take each series from its owner, under the owner's terms |
| FRASER (scans of historical publications) | its terms: no commercial use without consent | https://fraser.stlouisfed.org/terms-of-use | **no** | a federal publication is public domain; read it from a copy whose terms allow it |
| Bank of England, A millennium of macroeconomic data | none stated; the Bank grants non-commercial re-use of such material | https://www.bankofengland.co.uk/legal | **unclear: no** | Bank Rate from the Bank's statistical database, which is under the Open Government Licence (its exchange rates excepted) |
| Bank of Japan, Time-Series Data Search | commercial copying needs the Bank's permission first | https://www.stat-search.boj.or.jp/info/notice_en.html | **no** | the BIS from 1946; before it, the gap rule (or the Bank's permission) |
| Bank of Canada (Valet) | the Bank's terms: free use with credit | https://www.bankofcanada.ca/terms/ | yes | a sold product must say the data came from the Bank's website, free there |
| Swiss National Bank | non-commercial use only | https://www.snb.ch/en/srv/disclaimer_copyright | **no** | the BIS for the years it has; the gap rule before |
| Deutsche Bundesbank; ECB | the ESCB's common reuse policy: free, with the source quoted | https://www.bundesbank.de/en/homepage/user-information/terms-of-use-642972?index=6 , https://www.ecb.europa.eu/stats/ecb_statistics/governance_and_quality_framework/html/usage_policy.en.html | yes | quote the source; mark what was changed; the ECB's sold-product notice as the Bank of Canada's |
| Reserve Bank of Australia | CC BY 4.0, and its financial data may be used commercially | https://www.rba.gov.au/copyright/ | yes | Source: RBA; not its third-party tables |
| World Bank, Pink Sheet and MUV index | CC BY 4.0 | as above | yes | as above |
| Grilli-Yang index (the 1900-2016 update on Mendeley Data) | CC BY-NC 3.0 | https://data.mendeley.com/datasets/j24mjpcrrz | **no** | evidence only |
| Jacks (2019) commodity prices, openICPSR 198401 | could not be read (the page blocks automated readers); the study says it mixes in a paid database | https://www.openicpsr.org/openicpsr/project/198401/view | **unclear: no** | evidence only |
| Ilzetzki-Reinhart-Rogoff regimes | none stated | https://www.ilzetzki.com/irr-data | **unclear: no** | a regime's dates are facts; its codes are theirs |
| Correlates of War, State System Membership | its terms forbid commercial use and redistribution | https://correlatesofwar.org/data-sets/ | **no** | the 1900 statuses from the Factbook, Wikidata and the facts in Wikipedia |
| CIA World Factbook (the GitHub mirror) | US public domain; the mirror CC0 1.0 | https://github.com/factbook/factbook.json | yes | the CIA left it on 2026-02-04 |
| Wikipedia | CC BY-SA 4.0 | https://foundation.wikimedia.org/wiki/Policy:Terms_of_Use | yes, but ShareAlike binds the text | facts only, never its wording |
| Wikidata | CC0 1.0 | https://www.wikidata.org/wiki/Wikidata:Licensing | yes | none needed |

## 3. What LC1 changed

- **The Rankings:** the 17 measures whose figures are the IMF's, UNODC's, the WFE's, the WHO's, the UN IGME's or UNESCO's rank the player's cities only (`measures.tsv` says why in each `none:`): inflation, the real rate, reserves, broad money, private credit, debt, the budget balance, tax revenue, interest over revenue, the current account, bad loans, bank capital, homicides, market capitalisation, health spending, deaths under five, tertiary enrolment. The price level and the money against parity are now the World Bank's own GDP at market rates over its GDP at PPP (the same figure for nearly every nation; the six the Bank converts at another rate now agree with their GDP); the OECD's wages are divided by the Bank's GDP in local money over its GDP in US$. 7,049 figures where there were 9,643.
- **The credits:** the world's attribution file now ships beside the exe with the Earth's, and both files' credits were brought to what each licence asks today (section 4).
- **Queued:** the Rankings' measures back from sources that allow it (LC2), world-1.bin baked again without the IMF's figures (LC3), and a Credits page in the game (CR1).

## 4. Where the credits are, and where they belong

- `tools/earth/ATTRIBUTION-2.txt` goes beside the exe as `data/earth-2-ATTRIBUTION.txt` (ET1), and since LC1 `tools/world/ATTRIBUTION.txt` as `data/world-ATTRIBUTION.txt`: the Rankings' file and the two fixtures are in the jar, so their credits travel with it.
- In the game, the Rankings' (i) names each source with its licence. No screen shows the full credits: both attribution files said "the Credits page", which does not exist. A Credits page, from the title screen, showing both files and the fonts' notice, is CR1; until then the files beside the exe are the credits, which CC BY allows ("any reasonable manner").
