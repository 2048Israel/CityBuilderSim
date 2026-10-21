# Nations: the research notes (2026-10-20)

The research behind spec-nations.md, as gathered for it: every figure with its URL, UNVERIFIED where no primary page confirmed it, DERIVED where it is arithmetic on a cited figure or a pulled file. The pulled files (the Pink Sheet, CERDI, Natural Earth's GeoJSON, the World Bank API) were read in a scratch folder and are not kept here; WN0's pipeline fetches them again. Kept as WN0's starting point: the coverage counts, the licences and the pitfalls.

Conventions. `[url]` follows each figure. UNVERIFIED = not confirmed on a primary page (blocked, or only a search snippet or secondary press). DERIVED = my arithmetic from the cited numbers, or from a data file I pulled and computed on. Pulled files and scripts were in a scratch folder, not the repository.
Context flag: the data run to Sep/Oct 2026 and show a 2026 energy shock (Brent 71 -> 104 -> 120 $/bbl Feb-Apr 2026, 117 in Sep; the IMF WEO of 13 Oct 2026 cites "US-Iran war" energy prices). Do not calibrate "normal" prices or freight on 2026 spot. The WEO remark is from press coverage of the 13 Oct 2026 release (secondary) [https://news.cgtn.com/news/2026-10-13/IMF-raises-2026-global-growth-forecast-to-3-1--1RcEPm28WWI/share_amp.html]; the Brent figures are from the World Bank xlsx (section 4).

Licence summary (can the game ship derived numbers?):
- Open, commercial use OK with attribution: BACI (Etalab 2.0), WDI (CC BY 4.0), Pink Sheet (CC BY 4.0), PWT 11 and Maddison 2023 (CC BY 4.0), CERDI-seadistance (CC BY 4.0), Natural Earth (public domain), USGS (public domain / CC0), EIA (public domain), UN WPP (CC BY 3.0 IGO, see 3), FAOSTAT (CC BY 4.0 plus: may not be used to promote a commercial enterprise).
- Ask first or restricted: IMF WEO and COFER (commercial reuse needs permission; no bulk scraping; no LLM training), UN Comtrade (no re-dissemination without UNSD permission), BGS World Mineral Production (copyright), Energy Institute (c) terms (unverified), worldsteel (copyright).

---

## 1. Bilateral trade by product

BACI (CEPII)
- Current version 202601, released 22 Jan 2026; updated every January; latest data year 2024 (may be revised in the next version); do not mix versions. [https://www.cepii.fr/DATA_DOWNLOAD/baci/doc/baci_webpage.html]
- Coverage: "200 countries", ~5,000 products, HS 6-digit. Exact country count not stated (UNVERIFIED); "same geographic coverage as Comtrade". [same]
- Years available: HS92 1995-2024, HS96 1996-2024, HS02 2002-2024, HS07 2007-2024, HS12 2012-2024, HS17 2017-2024, HS22 2022-2024 (one CSV per year per HS revision). [same]
- Variables: t, k (HS6), i exporter, j importer, v value in thousand current USD, q quantity in metric tons; only strictly positive flows recorded. Country codes are Comtrade numeric codes, not ISO. [same]
- Method: mirror flows reconciled; CIF costs estimated and removed from import values (FOB); reporter reliability weighting. [same]
- Size: legacy 202501 zips (the 202601 sizes are not printed): HS92 2.36 GB, HS96 2.28, HS02 1.83, HS07 1.33, HS12 0.83, HS17 0.115 GB. Not openable in Excel. [same]
- Licence: Etalab Open Licence 2.0, "any use is authorized, provided the source is mentioned" (CEPII and BACI). [same]
- Taiwan has no code; "Asia nes" (code 490) is the proxy. [same]
- Reference: Gaulier & Zignago (2010), CEPII WP 2010-23. [same]
- Design note: v (kUSD) / q (t) gives a unit value per tonne by exporter-importer-product: a direct source of price per tonne and of CIF-FOB wedges.

UN Comtrade
- ~200 reporting countries or areas, annual data since 1962, HS and SITC and BEC, "more than 3 billion records" (UNSD About page via search; library guides say 140-200+ reporters: UNVERIFIED). [https://comtrade.un.org/labs/data-explorer/About.html]
- API tiers: Public (no key): preview only, 500 records, one period and one product per call. Basic Individual (free key): 500 calls/day, up to 100K records, 1 call/s. Premium Individual: 5,000 calls/day, 250K records. Institutional: unlimited. [https://uncomtrade.org/docs/subscriptions/] (rate-per-tier mapping inferred by the page reader).
- Licence: restrictive. WITS states the data are UN copyright, for internal use, no re-dissemination without written UNSD permission; the 2012 usage agreement is "a licence" accepted by use; a 2014 UNSD newsletter waives the re-dissemination fee for free or not-for-profit visualisation. Current wording UNVERIFIED. [https://wits.worldbank.org/WITS/wits/registration/TermsAndCondtions.aspx] [https://comtrade.un.org/licenseagreement.html] [https://unstats.un.org/unsd/trade/ITSB-newsletter MarJun2014v3.pdf]
- Design note: ship BACI-derived aggregates, not raw Comtrade.

HS-to-sector concordances
- WITS (World Bank) ZIP concordances: each HS vintage (H0 1988/92, H1 1996, H2 2002, H3 2007, H4 2012, H5 2017, H6 2022) to SITC1-4, BEC, GTAP, and between HS vintages; ISIC Rev.2/3 for H0-H3 (H4 ISIC Rev.3 only); CPC, SIC, MTN for the older HS; no ISIC for H5, H6. Terms: links to "Usage Conditions" (UNVERIFIED). [https://wits.worldbank.org/product_concordance.html]
- BACI's own FAQ points to these WITS tables for merging with other classifications. [https://www.cepii.fr/DATA_DOWNLOAD/baci/doc/baci_webpage.html]
- HS to ISIC Rev.4 / CPC 2.1: UNSD correlation tables (URL not checked: UNVERIFIED).

## 2. Production and use by country

FAOSTAT
- Licence: CC BY 4.0 plus additional terms: cite "FAO. [year of last update]. [database]", access date, URL, "Licence: CC-BY-4.0"; may not be used to promote a commercial enterprise or imply FAO endorsement; third-party data may differ. [https://www.fao.org/contact-us/terms/db-terms-of-use]
- Crops and livestock products: FAO releases describe 199 countries and territories (2000-2022 release), 278 products; QCL data revision 2025-12-15. The "245 countries, 1961" figures come only from a third-party portal: UNVERIFIED. [https://www.fao.org/statistics/highlights-archive/highlights-detail/agricultural-production-statistics-(2000-2022)] [https://data.fao.org/catalog/iso/d24a448b-3b62-4c09-8c1d-4a39bb599876]
- The FAOSTAT services API answers "Missing Authorization Header" without a token; use the bulk files (URL not checked). [https://faostatservices.fao.org/api/v1/en/definitions/domain/QCL/area]
- Forestry production and trade: roundwood (wood fuel, industrial), sawnwood, veneer, panels, pulp, recovered paper, paper and paperboard; "for many forest products historical data from 1961"; all countries and territories; latest year 2024; units million m3 / Mt / USD bn. [https://www.fao.org/forestry/statistics/data/en]
- 2024 world: industrial roundwood removals 1.96 bn m3 (+2%); wood-based panels 393 M m3 (+5%); wood pulp 189 Mt (+3%); wood pellets 48 Mt; wood and paper exports $486 bn; roundwood trade ~96 M m3 (-1%). Sawnwood: 445 M m3 in 2023 (-4%), 2024 about unchanged (no absolute 2024 figure found). [https://www.fao.org/newsroom/detail/global-forest-products-trade-and-production-show-signs-of-recovery-in-2024/en]

USGS Mineral Commodity Summaries 2026 (Feb 2026; >90 nonfuel commodities)
- Licence: report is a US-government work; the data release is CC0 1.0, DOI 10.5066/P1WKQ63T. [https://www.usgs.gov/data/mineral-commodity-summaries-2026-data-release] [https://pubs.usgs.gov/periodicals/mcs2026/mcs2026.pdf]
- World mine production tables list the top producers plus "Other countries" (about 10-20 named countries per commodity), not all nations.
- Read from the PDF text (column alignment imperfect): iron ore world mine production 2.6 Gt usable ore in 2024 and 2025e (1.6 Gt Fe content); copper world mine production 23.0 Mt in 2024 and 2025e (refinery ~27.6 Mt 2024, 29 Mt 2025e: columns not fully certain). [https://pubs.usgs.gov/periodicals/mcs2026/mcs2026.pdf]
- The US Minerals Yearbook is the older, slower sibling (not checked). USGS publishes "Minerals Yearbook, Area Reports: International" per country (not checked).

BGS World Mineral Production 2020-2024 (2026 edition)
- >70 commodities by country; dataset begins 1913; Excel/JSON from 1970 via BGS download tool or API. [https://www.bgs.ac.uk/mineralsuk/statistics/world-mineral-statistics/]
- Licence: NOT open. Links to "BGS Mineral Statistics terms and conditions (IPR)"; the PDF says compilations are copyright, no reproduction without the Director's permission. [https://nora.nerc.ac.uk/id/eprint/541620/1/WMP_2020%20to%202024.pdf]

World Steel Association
- 2025 world crude steel about 1.8 Gt, down 2% (worldsteel via SteelOrbis); the exact 1,803.8 Mt, China 960.8 Mt (-4.4%), India 164.9 Mt (+10.4%) come from a search snippet: UNVERIFIED. [https://www.steelorbis.com/steel-news/latest-news/world-crude-steel-output-down-two-percent-in-2025-1431540.htm] [https://worldsteel.org/media/press-releases/2026/december-2025-crude-steel-production-2025-global-crude-steel-production/]
- Monthly tables cover 70 countries, ~98% of 2025 world output. "World Steel in Figures 2026" (data finalised 3 Jun 2026). Licence: copyright, no open licence found. [https://worldsteel.org/wp-content/uploads/World-Steel-in-Figures-2026.pdf]

Energy Institute Statistical Review 2026 (75th ed., 30 Jun 2026, data to 2025)
- 2025 headlines: oil consumption 103 mb/d (+1.3%), coal consumption +0.7%, gas demand +1.6%, total energy supply >600 EJ (+1.7%), fossil 86%, energy CO2 35,806 Mt (+1.1%). [https://dieselnet.com/news/2026/07energyreview.php]
- Licence: OWID's metadata lists "(c) Energy Institute 2026" with terms at energyinst.org/terms, not an open licence. That page returned 403 to me: UNVERIFIED. The site calls the review "free to access". [https://api.ourworldindata.org/v1/indicators/1295487.metadata.json] [https://energyinst.org/exploring-energy/statistical-review]
- OWID series merge EI (from 1965) with EIA International Energy Data (public domain) and Etemad & Luciani (CC BY-SA 3.0); EIA bulk file: https://api.eia.gov/bulk/INTL.zip, reuse: https://www.eia.gov/about/copyrights_reuse.php. For an open licence prefer EIA.
- 2025 production, OWID/EI (TWh; includes crude, condensate, NGL, other liquids): oil World 54,674; USA 10,350; Saudi Arabia 6,232; Russia 6,085; China 2,513. Coal World 50,210 TWh (180.8 EJ, DERIVED); China 26,296; India 4,992; Indonesia 4,454; USA 3,067; Australia 3,024; Russia 2,545. Countries above 0.5 TWh in 2025: oil 50, coal 33 (DERIVED). [https://ourworldindata.org/grapher/oil-production-by-country.csv] [https://ourworldindata.org/grapher/coal-production-by-country.csv]
- Design note: only ~33 coal and ~50 oil producers matter; all other nations are pure importers in those goods.

## 3. GDP, population, CPI, exchange rates 2000-2025

World Bank WDI
- Licence: CC BY 4.0 with additional terms (mediation/arbitration clause); attribute "The World Bank: dataset: source"; commercial use allowed. [https://data.worldbank.org/summary-terms-of-use] [https://datacatalog.worldbank.org/public-licenses]
- 217 economies (non-aggregate entries in the API country list); data last updated 2026-10-08. [https://api.worldbank.org/v2/country?format=json&per_page=400] (DERIVED count)
- Coverage of the 196 target nations (193 UN members + Taiwan, Hong Kong, Macau), counted from the API (DERIVED): GDP current US$ 2000: 193; 2023: 189 (missing CUB, ERI, PRK, SSD, SYR, TWN, YEM); 2024: 188; 2025: 182. Population: 195 every year 2000-2025 (all but Taiwan). Official FX (PA.NUS.FCRF): 189 in 2000, 185 in 2023, 174 in 2025. CPI index (FP.CPI.TOTL): 165 in 2000, 174 in 2023, 167 in 2025. Taiwan is absent from WDI for every series. [https://api.worldbank.org/v2/country/all/indicator/NY.GDP.MKTP.CD?format=json&date=2000:2025]

IMF World Economic Outlook
- October 2026 edition (published 13 Oct 2026; world growth 3.1% 2026, 3.4% 2027; advanced 2.1/1.8; EMDE 3.7/4.4; US 2.3/2.3; China 4.5/4.0; euro area 1.1/1.2; India 7.0/7.0). [https://news.cgtn.com/news/2026-10-13/IMF-raises-2026-global-growth-forecast-to-3-1--1RcEPm28WWI/share_amp.html] (secondary)
- Database: data from 1980; projections for most series five years ahead, so the Oct 2026 edition should run to 2031 (inference: not printed on the page I could read: UNVERIFIED). Served from the IMF Data Portal (CSV, Excel, SDMX). Country count: the Oct 2017 edition said 193; current UNVERIFIED. [https://data.imf.org/datasets/WEO] [https://www.imf.org/en/publications/weo/weo-database/2017/october/index.md]
- Licence: IMF Copyright and Terms (effective 11 Oct 2024): may download, copy, adapt, publish, distribute with attribution; "for any potential commercial reuse email copyright@imf.org"; bulk automated download needs permission; no use to train LLMs without permission. [https://www.imf.org/en/About/copyright-and-terms]

UN World Population Prospects 2024
- 237 countries or areas; estimates from 1950; medium-variant projections to 2100; world population peaks about 10.3 bn in the mid-2080s. [https://www.un.org/development/desa/pd/content/world-population-prospects-2024] [https://sdg.iisd.org/news/global-population-to-peak-this-century-world-population-prospects-2024/]
- Interim update 19 Jan 2026 (Togo correction); next full revision postponed to 11 Jul 2027 (single secondary site: UNVERIFIED). [https://www.populationpyramids.org/methodology]
- Licence: CC BY 3.0 IGO is stated on WPP2019 and WPP2022 publications; the WPP2024 data portal terms were not retrievable: UNVERIFIED. [https://population.un.org/wpp/publications/files/wpp2019_highlights.pdf]

Penn World Table 11.0 (published 7 Oct 2025): 185 countries, 1950-2023, CC BY 4.0, DOI 10.34894/FABVLR. [https://www.rug.nl/ggdc/productivity/pwt/pwt-releases/pwt110] [https://www.rug.nl/ggdc/blog/penn-world-table-version-11-0-is-published]
Maddison Project Database 2023: 169 countries, data to 2022, CC BY 4.0, DOI 10.34894/INZBF2; cite Bolt & van Zanden (2024), J. Economic Surveys, doi 10.1111/joes.12618. Start year not printed on the page (long series; many countries back to 1820 or earlier: UNVERIFIED). [https://www.rug.nl/ggdc/historicaldevelopment/maddison/releases/maddison-project-database-2023]

## 4. Commodity prices: World Bank Pink Sheet

- Latest release 2 Oct 2026 (next 3 Nov 2026): monthly and annual XLSX plus PDF. Monthly sheet header: "monthly prices in nominal US dollars, 1960 to present"; I read 1960M01 to 2026M09. Monthly series are nominal USD only. [https://www.worldbank.org/en/research/commodity-markets] [https://thedocs.worldbank.org/en/doc/74e8be41ceb20fa0da750cda2f6b9e4e-0050012026/related/CMO-Historical-Data-Monthly.xlsx]
- Licence: CC BY 4.0 via World Bank Data Access and Licensing (the page links to the CC BY section). [https://datacatalog.worldbank.org/public-licenses]
- Series used: Crude oil Brent/Dubai/WTI/average ($/bbl; from 1960), Coal Australian ($/mt; from 1970), Coal South African (from 1984), Iron ore cfr spot ($/dmtu), Copper ($/mt), Aluminum, Natural gas Europe ($/mmbtu), Gold, wheat. Iron ore before about 2010 reflects annual-contract benchmark pricing, so early monthly volatility is understated (UNVERIFIED reasoning).
- Annual averages 2025 (DERIVED from the xlsx): Brent $69.0/bbl; Australian coal $108.4/t; iron ore $100.2/dmtu; copper $9,947/t. 2024: $80.7; $136.2; $109.4; $9,142.
- Latest months: 2026M09 Brent $116.8; Aus coal $147.1; iron ore $97.7; copper $14,474 (DERIVED, same file). 2026M04 Brent $120.4.

## 5. Borders and sea distances

Natural Earth admin-0 countries
- Version 5.1.1; "There are 258 countries in the world" per the NE page; I counted 258 features in ne_10m_admin_0_countries.geojson (GitHub master). TYPE field: Sovereign country 185, Dependency 33, Country 19, Indeterminate 12, Disputed 5, Sovereignty 2, Lease 2 (DERIVED). [https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-0-countries/] [https://github.com/nvkelso/natural-earth-vector]
- Scales and sizes (zip): 1:10m 4.7 MB; 1:50m 782 KB; 1:110m 210 KB. 10m GeoJSON is 13 MB. [same page, 50m: https://www.naturalearthdata.com/downloads/50m-cultural-vectors/50m-admin-0-countries-2/, 110m: https://www.naturalearthdata.com/downloads/110m-cultural-vectors/110m-admin-0-countries/]
- Default boundaries show de facto control. Point-of-view variants exist "for several dozen countries"; the repo has 33 POV files (arg, bdg, bra, chn, deu, egy, esp, fra, gbr, grc, idn, ind, iso, isr, ita, jpn, kor, mar, nep, nld, pak, pol, prt, pse, rus, sau, swe, tlc, tur, twn, ukr, usa, vnm) plus a "lakes" file. The meaning of `iso` and `tlc` is UNVERIFIED. [https://api.github.com/repos/nvkelso/natural-earth-vector/contents/geojson?ref=master]
- All 196 target nations are present: 193 UN members + Taiwan + Hong Kong + Macao match on `ISO_A3_EH`. Pitfalls: `ISO_A3` is -99 for Kosovo, N. Cyprus, Somaliland (use `ISO_A3_EH`); Hong Kong and Macao are TYPE "Country" under sovereignty China; Taiwan is TYPE "Sovereign country" but FCLASS_ISO "Admin-1 states provinces"; South Sudan is `ADM0_A3` SDS; 202 features map onto the 196 (islands and enclaves duplicate codes). (DERIVED)
- Licence: public domain; "No permission is needed ... Crediting the authors is unnecessary." [https://www.naturalearthdata.com/about/terms-of-use/]

CERDI-seadistance (Bertoli, Goujon, Santoni 2016)
- Version V1.1 (Zenodo 240493, published 12 Jan 2017, modified 14 Apr 2021); CC BY 4.0; files .dta 2.0 MB, .xlsx 2.2 MB; readme and used_ports.zip. [https://zenodo.org/record/240493] [https://zenodo.org/record/46822 (v1, March 2016)]
- Content, from the xlsx I read: 51,302 rows = 227 x 226 ordered country pairs, 227 countries and territories. Columns: iso1, iso2, seadistance (km), capitalport1, capitalport2 (road km from capital to the chosen port), roaddistance, short. (DERIVED) The 227-country figure also appears at [https://isidore.science/document/10670/1.hctdak] (abstract text via search).
- Method: a coastal country is matched to the coastal cell with the most shipping lines; a landlocked country to the foreign port with the shortest road distance to its capital; then the shortest sea route between the two ports. V1.1 picks the projection nearest the route mid-point; Armenia and Azerbaijan are tied to the Russian west port. Port can differ by partner for large countries (Russia east/west; the USA appears with different capital-port road lengths in the CHN and DEU rows). [readme: https://zenodo.org/records/240493/files/readme.txt] [https://isidore.science/document/10670/1.hctdak]
- Paper: CERDI Working Paper 2016/07 (not retrieved: HAL blocked).
- Coverage of the 196: 192 of 193 UN members present (South Sudan missing) + TWN, HKG, MAC; 35 non-UN territories also included; uses ISO codes incl. ANT (dissolved). (DERIVED)
- Sample distances (km): BRA-CHN 21,462; AUS-CHN 10,281; NLD-CHN 19,901; CAN-CHN 10,625; ZAF-CHN 13,086; CHN-USA 10,609; DEU-USA 9,988; SAU-JPN 13,003; TWN-HKG 585; MAC-HKG 52. (DERIVED)

## 6. IMF COFER and currencies

COFER shares of world allocated FX reserves (since 2025Q3 the IMF removed the "unallocated" category and imputes it, so shares sum to 100%, revised back to 2000Q1)
- 2026Q2 (IMF data brief, 30 Sep 2026): total $13.22 tn; USD 56.70% (from 57.18% in Q1); EUR 20.60% (20.04); JPY 4.96% (5.43); CNY 2.11% (1.98); GBP +0.16 pp, AUD +0.13 pp, CHF +0.03 pp, CAD -0.01 pp; other currencies -0.06 pp and still "above 6 percent". [https://data.imf.org/en/news/imf%20data%20brief%20september%2030]
- 2026Q1 (brief 1 Jul 2026, later revised): total $13.10 tn; USD 57.13% (revised 57.18); EUR 20.03%; JPY 5.44%; CNY 1.99%; other 6.18%. [https://data.imf.org/en/news/imf-data-brief-july%201]
- 2025Q4: total $13.14 tn; USD 56.77%; EUR 20.25%; CNY 1.95%; JPY+GBP+AUD+CAD+CHF combined 14.90%; other 6.13%. [https://data.imf.org/en/news/imf%20data%20brief%20march%2027]
- Individual GBP, CAD, AUD, CHF levels for 2026Q2 are not in the briefs (UNVERIFIED). New-method values for 2025Q2 from the IMF technical note: USD 57.12, EUR 20.12, JPY 5.76, GBP 4.71, CNY 1.98, CAD 2.60, AUD 2.05, CHF 0.17, other 5.50 (sums to 100). [https://www.imf.org/-/media/files/publications/tnm/2025/english/tnmea2025014.pdf.md]
- DERIVED for 2026Q2: GBP+CAD+AUD+CHF together = 100 - 56.70 - 20.60 - 4.96 - 2.11 - ~6.12 = ~9.5%; eight named currencies together = ~93.9%.
- Only CNY (from 2016Q4), AUD and CAD (from 2012Q4) are separately identified since then; all else is "other"; country data are confidential. [https://data.imf.org/en/Datasets/COFER/Frequently-Asked-Questions]
- Licence: IMF terms (see 3). DBnomics mirror is stale (indexed July 2025, old method). [https://db.nomics.world/IMF/COFER]

Currencies used by the ~196 nations (DERIVED from the ISO 4217 table, mapped to 193 UN members + TWN, HKG, MAC)
- ISO table (current entries): 155 distinct alphabetic codes touch the 196; removing 8 fund/unit codes (BOV, CLF, COU, MXV, CHE, CHW, UYI, UYW), the defunct SVC and the second Venezuelan code VED gives about 145 distinct national currencies (counting Panama's balboa PAB and Zimbabwe's ZWG as their own). 144 if PAB is folded into USD. [https://raw.githubusercontent.com/datasets/currency-codes/main/data/codes-all.csv]
- Eurozone: 21 members since 1 Jan 2026 (Bulgaria joined at 1.95583 BGN/EUR). 25 of the 193 UN members use the euro (the 21 plus Andorra, Monaco, San Marino, Montenegro). [https://www.ecb.europa.eu/press/pr/date/2026/html/ecb.pr260101~c830245e42.en.html] [ISO table above]
- CFA franc: 14 countries, two currencies at parity, pegged to the euro at 655.957 (655.96 in the source I saw): WAEMU/XOF 8 (BEN, BFA, CIV, GNB, MLI, NER, SEN, TGO); CEMAC/XAF 6 (CMR, CAF, TCD, COG, GNQ, GAB). Comoros has its own franc pegged to the euro. [https://credendo.com/cs/node/177] [https://www.diplomatie.gouv.fr/en/the-ministry-in-action/contributing-to-sustainable-balanced-globalization/combating-global-social-inequality/africa-a-priority-for-french-official-development-assistance/franc-zone]
- Eastern Caribbean dollar XCD: 6 UN members (ATG, DMA, GRD, KNA, LCA, VCT) (ISO table).
- US dollar as legal tender: ECU, SLV, MHL, FSM, PLW, TLS (plus the USA); PAN (balboa 1:1, USD notes) and HTI listed by ISO with USD; Zimbabwe de jure ZWG, de facto multi-currency (UNVERIFIED). Australian dollar: KIR, NRU, TUV (plus AUS). Swiss franc: LIE. South African rand common area: LSO, NAM, SWZ keep own currencies pegged 1:1 (ISO lists ZAR for LSO, NAM); Bhutan's ngultrum is pegged to the rupee (INR listed). (ISO table)
- Gulf USD pegs (secondary only): SAR 3.75 (since 1986), AED 3.6725 (since 1997), QAR 3.64 (since 2001), BHD 0.376; OMR 0.3845 not confirmed (UNVERIFIED). Kuwait dropped the USD peg for a basket in 2007. [https://gulfnews.com/business/markets/uae-dirham-at-one-year-low-in-forwards-market-vs-dollar-1.1419097] [https://www.aljazeera.com/news/2007/11/28/gulf-currency-peg-under-pressure]
- IMF AREAER (de facto classification, annual) is the authority for peg counts; I could not retrieve the 2023/2024 category counts: UNVERIFIED. [https://www.imf.org/en/publications/annual-report-on-exchange-arrangements-and-exchange-restrictions/issues/2024/12/19/annual-report-on-exchange-arrangements-and-exchange-restrictions-2023-541890]

## 7. Farmland prices per hectare (latest year)

Canada, FCC Farmland Values Report 2025 (released 24 Mar 2026)
- National average cultivated farmland +9.3% in 2025 (5-year average 9.5%, 10-year 8.6%). By province: BC -1.7%, AB +11.4, SK +9.4, MB +12.2, ON +2.2, QC +4.8, NB +9.1, NS +1.6, PEI +8.5. [https://www.fcc-fac.ca/en/knowledge/economics/farmland-values-report]
- Per-acre levels are not on the public page (interactive map / paid report): UNVERIFIED. From press coverage: Ontario South West $33,700/acre, Southern Ontario $23,400, Central East $13,100; northeast Saskatchewan cropland near $5,000/acre; Alberta irrigated about $20,000/acre. [https://farmtario.com/news/farmland-value-stagnates-in-ontario-in-2025/] [https://www.producer.com/news/farmland-climbs-higher-in-spite-of-headwinds/]
- DERIVED: $33,700/acre = C$83,300/ha; $5,000/acre = C$12,350/ha.

USA, USDA NASS Land Values 2026 Summary (31 Jul 2026), $/acre: farm real estate 4,500 (2025: 4,350, +3.4%); cropland 6,020 (5,830, +3.3%); pasture 2,000 (1,920, +4.2%). DERIVED: cropland $14,880/ha; farm real estate $11,120/ha. Press: Corn Belt about $8,540-8,590/acre, Illinois $9,250, New Mexico lowest $735 (cash rent $160/acre cropland: UNVERIFIED, not in the NASS text I read). [https://esmis.nal.usda.gov/sites/default/release-files/795999/land0726.txt] [https://www.dtnpf.com/agriculture/web/ag/crops/article/2026/07/31/usda-land-values-report-us-cropland]

EU, Eurostat `apri_lprc` (updated 2026-07-10), arable land, EUR/ha, reference year 2024; pulled from the API (DERIVED read): EU27 15,224; Netherlands 96,608; Portugal 76,556; Ireland 50,375; Luxembourg 48,180; Austria 37,600; Slovenia 28,348; Denmark 22,468; Poland 16,118; Czechia 14,835; Greece 14,312; Spain 12,778; Sweden 10,706; Finland 8,670; Romania 8,700; Bulgaria 8,679; Estonia 7,033; Hungary 6,041; Croatia 5,965; Slovakia 5,823; Lithuania 5,590; Latvia 4,825; France 6,400. Malta 201,263 is quoted in Eurostat press (not in my pull). Permanent grassland, 2024: Netherlands 77,609; Portugal 36,067; Romania 6,257. Portugal's value looks high: check Eurostat flags (UNVERIFIED). Germany, Italy, Belgium have no national value in this table. Rent: EU average EUR 295/ha/yr; Netherlands arable EUR 941/ha/yr (press). [https://ec.europa.eu/eurostat/api/dissemination/statistics/1.0/data/apri_lprc?format=JSON&lang=EN&time=2024] [https://www.freshplaza.com/europe/article/9806369/agricultural-land-most-expensive-in-malta-the-netherlands-and-portugal/]

Global comparison
- No open global per-hectare series found. Candidates: Savills Global Farmland Index, Knight Frank, Rabobank, national statistics (figures not retrieved).
- Australia (secondary): Ray White national median A$10,979/ha for 2025 and A$11,032/ha in Q1 2026; Rabobank median +0.4% in 2025, +~2% expected 2026 (level not retrieved). Rural Bank's own median was quoted as A$5,271/ha in an undated Kondinin piece; sources disagree by method: UNVERIFIED. [https://www.rabobank.com.au/content/dam/ranz/au/images/2026/knowledge/RaboResearch_Australian-farmland-price-outlook-2026.pdf (403 to me)] [https://www.brokernews.com.au/news/breaking-news/farmland-hits-record-highs--but-not-every-farmer-is-winning-289567.aspx]

## 8. Freight, distance elasticity, trade and Armington elasticities

Dry bulk (Baltic Exchange primary not accessed; market-monitor secondary)
- C3 Tubarao-Qingdao iron ore: $20.93/t (20 Jan 2026); $24.50/t (Sep 2025 average); $38/t (late May 2026); $33.95/t (12 Jun 2026); $41.55/t (4 Sep 2026, multi-year high). C5 West Australia-Qingdao: ~$8.5/t (Jan 2026); $16.4/t (late May); $10.95/t (12 Jun). [https://www.hellenicshippingnews.com/january-snapshot-capesize-ballasters-demand/] [https://www.thesignalgroup.com/weekly-market-monitor/weekly-dry-market-monitor-week-36-2026] [https://gmk.center/?p=125105]
- DERIVED per tonne-km: C3 at $20.93/t over CERDI's 21,462 km = 0.098 US cents per tonne-km; at $41.55 = 0.19 cents.
Containers (Drewry World Container Index, primary page, 15 Oct 2026, US$/40ft): composite 4,485 (+3% w/w); Shanghai-Los Angeles 7,884; Shanghai-New York 10,075; Shanghai-Rotterdam 3,522; Shanghai-Genoa 3,945. 15 Jan 2026: composite 2,445, Shanghai-Rotterdam 2,763 (secondary). 20 Aug 2026 composite 4,526 (secondary). 1 TEU is half a 40ft box: Shanghai-Rotterdam about $1,760/TEU (DERIVED). [https://www.drewry.co.uk/supply-chain-advisors/supply-chain-expertise/world-container-index-assessed-by-drewry] [https://www.hellenicshippingnews.com/world-container-index-20-aug/]
Cost per tonne-km by mode (weak sources; treat as order of magnitude)
- Class I rail revenue 4.05 cents per ton-mile (2013-14), barge 1.42-1.83 cents per ton-mile (1990-2004). DERIVED (1 short ton-mile = 1.459 tonne-km): rail ~2.8 US cents per tonne-km; barge ~1.0-1.25. Search snippet of the BTS archive; the page returned 403: UNVERIFIED. [https://www.bts.gov/archive/publications/national_transportation_statistics/table_03_21]
- US truck: ATRI 2025 cost $2.336 per mile (+3.4%; ex-fuel $1.854). At an assumed 20 t payload that is about 7 cents per tonne-km (DERIVED, payload assumed). [https://www.ttnews.com/articles/atri-truck-costs-2025]
- Europe (weak): truck ~EUR 0.10 per tonne-km short haul, ~EUR 0.037 beyond 800 km; rail ~EUR 0.011; intermodal ~EUR 0.013: UNVERIFIED. [https://neuron.e-gmat.com/di/questions/is-it-cheaper-for-a-business-to-move-freight-by-truck-or-rail-long-ha-312.html]
- Order of magnitude (DERIVED): sea bulk ~0.1-0.2, rail ~1-3, truck ~5-10 US cents per tonne-km.
Hummels (2007, JEP 21(3):131-154): US freight expenditure fell from ~8% of import value (1974) to ~4% (1997) then flat; elasticity of ocean freight cost to fuel price 0.232; air-freight distance elasticity 0.43 (1974) -> 0.16 (2004); ocean shipping is "99 percent of world trade by weight and a majority by value"; about 23% of world trade by value is between countries sharing a land border, nearly constant over recent decades (1-5% in Africa, Middle East, Asia; 10-20% Latin America; 25-35% Europe and North America). Ocean distance elasticity is in Table 2, not extracted: UNVERIFIED. [https://business.purdue.edu/faculty/hummelsd/papers/hummels%20jep%20rewrite%20final%20with%20tables.pdf]
Distance elasticity of trade (Disdier & Head 2008)
- 1,467 estimates from 103 papers: mean 0.91, median 0.87, inverse-variance weighted mean 0.855; 90% of estimates between 0.28 and 1.55. A 10% rise in distance cuts trade about 9%. The effect rose around mid-century and stayed high. [https://blogs.ubc.ca/khead/files/2016/07/meta.pdf] (working-paper text; published REStat 90(1):37-48.)
Trade elasticity (Head & Mayer 2014, Table 5: 744 estimates from 32 papers)
- Median -3.19 (mean -4.51) full sample; naive gravity median -1.31; structural gravity median -3.78 (mean -5.13); identified by tariffs or freight rates, structural: median -5.03 (mean -6.74, n=435), their preferred value; identified by prices, wages, exchange rates: median -1.12. [https://www.cepii.fr/PDF_PUB/wp/2013/wp2013-27.pdf]
- Under CES the trade-cost elasticity is 1 - sigma, so -5.03 implies sigma about 6 (DERIVED).
Armington elasticity
- Bajzik, Havranek, Irsova, Schwarz (2020, JIE 127:103383): 3,524 estimates; after bias correction the implied elasticity is 2.5-5.1 with median 3.8 (an AEA slide deck says this translates to a trade-cost elasticity of about 2.8). [https://doi.org/10.1016/j.jinteco.2020.103383] [https://topcat.aeaweb.org/conference/2021/preliminary/powerpoint/r3ZZRdyN]
- Feenstra, Luck, Obstfeld, Russ (2018, REStat 100(1):135-150): the "macro" elasticity (home versus foreign) is often lower than the "micro" elasticity (between foreign sources); no significant gap for up to half of goods. An early draft had a micro median of 4.4 (draft only: UNVERIFIED). [https://www.nber.org/papers/w20063]
- Simonovska & Waugh (2014, JIE 92(1):34-50): trade elasticity near 4 on 2004 data for 123 countries, about half the Eaton-Kortum estimate (8.28 in drafts). [https://www.nber.org/papers/w16796]

## 9. Volatility

GDP growth volatility
- Aguiar & Gopinath (2007, JPE 115(1):69-102), Table 1 averages over 13 emerging and 13 developed economies, quarterly data: SD of HP-filtered log GDP 2.74 (emerging) vs 1.34 (developed); SD of GDP growth 1.87 vs 0.95 (percent); correlation of net exports with GDP -0.51 vs -0.17. [https://nber.org/system/files/working_papers/w10734/w10734.pdf]
- WDI annual real GDP growth (NY.GDP.MKTP.KD.ZG), standard deviation per country, then averaged (DERIVED): 2000-2019: G7 1.76; 15 large emerging (BRA CHN IND IDN MEX TUR ZAF RUS ARG THA PHL POL EGY NGA SAU) 2.69; all countries mean 3.83 (median 3.04); by WB income group mean: high 3.50, upper-middle 4.13, lower-middle 3.39, low 5.08 (small oil and island economies inflate "high"). 2000-2024 including 2020: G7 2.50; EM15 3.22; all countries mean 4.49 (median 3.64). World growth SD 1.33 (2000-2019), 1.84 (2000-2024); world growth 2009 -1.33%, 2020 -2.89%, 2025 +2.92%. [https://api.worldbank.org/v2/country/all/indicator/NY.GDP.MKTP.KD.ZG?format=json&date=2000:2025]
Large output falls
- Barro (2006, QJE 121(3):823-866): 60 episodes of a fall of 15% or more in real per-capita GDP across 35 countries over 100 years, so 1.7% per year; sizes 15-64%; world wars and the Great Depression dominate. [https://doi.org/10.1162/qjec.121.3.823] [https://dash.harvard.edu/server/api/core/bitstreams/7312037c-67b1-6bd4-e053-0100007fdf3b/content]
- WDI 2000-2024, share of country-years with real GDP growth below -5%: 4.9% (254 of 5,168); below -10%: 1.9% (96); excluding 2020: 3.4% and 1.2%. (DERIVED, same API.)
World business-cycle share
- Kose, Otrok, Prasad (2012, IER; NBER w14292), 106 countries, 1960-2005: the global factor explains on average 11% of output-growth variance (consumption 9%, investment 6%); 27% in industrial countries, "three to four times" the emerging and other developing groups; the global share fell from 15% to 7% (full sample) between 1960-84 and 1985-2005, industrial 28% to 9%; the global factor's SD fell from 1.4% to 0.5%. [https://www.nber.org/system/files/working_papers/w14292/w14292.pdf]
- Kose, Otrok, Whiteman (2003, AER 93(4):1216-1239), 60 countries: world factor is "an important source of volatility" in most countries, regional factors minor. The headline variance share was not retrievable: UNVERIFIED. G7 version (KOW 2008): the G7 factor explains >25% of output variation on average. [https://doi.org/10.1257/000282803769206278] [https://www.imf.org/external/np/res/seminars/2003/global/pdf/kose.pdf]
Commodity price volatility (DERIVED from the Pink Sheet monthly nominal USD file, section 4): SD of the annual log change of annual-average prices, 2000-2025 / 1990-2025: Brent 0.289 / 0.270; crude average 0.284 / 0.264; Australian coal 0.403 / 0.348; South African coal 0.360 / 0.311; iron ore 0.309 / 0.266; copper 0.217 / 0.213; aluminium 0.172 / 0.171; Europe gas 0.516 / 0.443; wheat (US HRW) 0.200 / 0.193; gold 0.129 / 0.129. Annualised SD of monthly log changes, 2000-2025: Brent 0.328; Aus coal 0.296; iron ore 0.319; copper 0.213; Europe gas 0.396. [https://thedocs.worldbank.org/en/doc/74e8be41ceb20fa0da750cda2f6b9e4e-0050012026/related/CMO-Historical-Data-Monthly.xlsx]

## 10. Literature (citations, DOI or URL)

- Samuelson, P. A. (1952). Spatial price equilibrium and linear programming. American Economic Review 42(3): 283-303. No DOI found; JSTOR stable 1810381 (number from memory: UNVERIFIED). Citation confirmed via [https://journals.sagepub.com/doi/10.1068/a210099].
- Takayama, T. and Judge, G. G. (1964). Spatial equilibrium and quadratic programming. Journal of Farm Economics 46(1): 67-93. doi:10.2307/1236473 [https://doi.org/10.2307/1236473] (Crossref-confirmed).
- Takayama, T. and Judge, G. G. (1971). Spatial and Temporal Price and Allocation Models. North-Holland, Amsterdam (book; no DOI; details from memory: UNVERIFIED).
- Wilson, A. G. (1967). A statistical theory of spatial distribution models. Transportation Research 1(3): 253-269. doi:10.1016/0041-1647(67)90035-4 [https://doi.org/10.1016/0041-1647(67)90035-4] (Crossref-confirmed).
- Wilson, A. G. (1970). Entropy in Urban and Regional Modelling. Pion, London (book; no DOI; UNVERIFIED).
- Sinkhorn, R. (1964). A relationship between arbitrary positive matrices and doubly stochastic matrices. Annals of Mathematical Statistics 35(2): 876-879. doi:10.1214/aoms/1177703591 [https://doi.org/10.1214/aoms/1177703591]. Sinkhorn, R. and Knopp, P. (1967). Concerning nonnegative matrices and doubly stochastic matrices. Pacific J. Math. 21(2): 343-348. doi:10.2140/pjm.1967.21.343 [https://doi.org/10.2140/pjm.1967.21.343].
- Cuturi, M. (2013). Sinkhorn distances: lightspeed computation of optimal transport. Advances in Neural Information Processing Systems 26 (NIPS 2013), pp. 2292-2300. arXiv:1306.0895 [https://arxiv.org/abs/1306.0895].
- Fally, T. (2015). Structural gravity and fixed effects. Journal of International Economics 97(1): 76-85. doi:10.1016/j.jinteco.2015.05.005 (Crossref-confirmed). It is the tie between PPML gravity and Sinkhorn-type iterative scaling (relation from memory: UNVERIFIED). [https://doi.org/10.1016/j.jinteco.2015.05.005]
- Anderson, J. E. and van Wincoop, E. (2003). Gravity with gravitas: a solution to the border puzzle. American Economic Review 93(1): 170-192. doi:10.1257/000282803321455214 [https://doi.org/10.1257/000282803321455214]. Finds borders cut trade among industrial countries by 20-50%.
- Armington, P. S. (1969). A theory of demand for products distinguished by place of production. IMF Staff Papers 16(1): 159-178. doi:10.2307/3866403 [https://doi.org/10.2307/3866403].
- Eaton, J. and Kortum, S. (2002). Technology, geography, and trade. Econometrica 70(5): 1741-1779. doi:10.1111/1468-0262.00352 [https://doi.org/10.1111/1468-0262.00352].
- Head, K. and Mayer, T. (2014). Gravity equations: workhorse, toolkit, and cookbook. Handbook of International Economics, vol. 4, pp. 131-195. doi:10.1016/B978-0-444-54314-1.00003-3 [https://doi.org/10.1016/B978-0-444-54314-1.00003-3].
- Disdier, A.-C. and Head, K. (2008). The puzzling persistence of the distance effect on bilateral trade. Review of Economics and Statistics 90(1): 37-48. doi:10.1162/rest.90.1.37 [https://doi.org/10.1162/rest.90.1.37].
- Gopinath, G., Boz, E., Casas, C., Diez, F. J., Gourinchas, P.-O. and Plagborg-Moller, M. (2020). Dominant currency paradigm. American Economic Review 110(3): 677-719. doi:10.1257/aer.20171201 [https://doi.org/10.1257/aer.20171201]. Gopinath, G. (2015). The international price system. NBER WP 21646 [https://www.nber.org/papers/w21646]. Boz, E., Casas, C., Georgiadis, G., Gopinath, G., Le Mezo, H., Mehl, A. and Nguyen, T. (2022). Patterns of invoicing currency in global trade: new evidence. Journal of International Economics 136: 103604. doi:10.1016/j.jinteco.2022.103604 [https://doi.org/10.1016/j.jinteco.2022.103604]. Update: Boz, Bruggen, Casas, Georgiadis, Gopinath, Mehl (2025), IMF WP/25/178, 132 countries, 1990-2023: the dollar remains dominant, shares "broadly stable". [https://www.imf.org/en/Publications/WP/Issues/2025/09/12/Patterns-of-Invoicing-Currency-in-Global-Trade-in-a-Fragmenting-World-Economy-570297]
- Dollar invoicing shares. ECB (June 2025), average 1999-2023: dollar about 40% and euro more than 40% of global export invoicing; together over 80%; excluding euro-area countries, 2023 shares about 60% dollar and 25% euro; renminbi under 2% of exports. [https://www.ecb.europa.eu//press/other-publications/ire/article/html/ecb.ireart202506_02~a8e66f5ea3.en.html] Fed (July 2025), dollar share of export invoicing 1999-2019: Americas 96%, Asia-Pacific 74%, rest of world 79%; euro 66% in Europe (incl. intra-euro). [https://www.federalreserve.gov/econres/notes/feds-notes/the-international-role-of-the-u-s-dollar-2025-edition-20250718.html] Gopinath (2015): US share of its own imports 13%, Japanese imports 71% invoiced in dollars, 93% of US imports in dollars. [https://www.nber.org/papers/w21646]
- Other cited above: Aguiar and Gopinath 2007 doi:10.1086/511283; Barro 2006 doi:10.1162/qjec.121.3.823; Kose, Otrok, Whiteman 2003 doi:10.1257/000282803769206278; Hummels 2007 JEP 21(3); Feenstra et al. 2018 doi:10.1162/REST_a_00696; Bajzik et al. 2020 doi:10.1016/j.jinteco.2020.103383. All Crossref-confirmed.
