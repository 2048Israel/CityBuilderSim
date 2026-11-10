# Copper concentrate TC/RC by year, with the copper price and the charge as a share of it

The research note behind section 7 of [spec-world-prices.md](../spec-world-prices.md) (WP1), written 2026-11-09 by a research-only session and kept as it wrote it. "I" below is that session.
Figures are stated in its own words; no source text is reproduced beyond short phrases.

**Where the study reads a source differently.** The note marks the charge figures printed in the USGS copper chapters "may ship: yes", reading the US public domain alone and leaving the call to Jerus. The study reads them by the rule in [licences.md](../licences.md) (a part a publisher credits to a third party carries the third party's terms; a compiler's estimates are the compiler's): the chapters credit CRU, so they are unclear, and unclear is no. The spec's section 7 and its call ★4 stand on that reading, and on the note's own answer to its question: there is no one continuous yearly record a sold game may use.

How to read the tables:

* TC is the treatment charge in US dollars per dry tonne of concentrate. RC is the refining charge in US cents per pound of payable copper. "Combined" is both expressed in US cents per pound of payable copper.
* Conversion rule: combined c/lb = TC x 100 / (grade x payable x 2204.62) + RC, assuming a 26.5 percent Cu concentrate with 96.5 percent payable. That is 0.1774 c/lb per dollar of TC. Example: $80/t gives 14.2 c, plus RC 8.0 c, gives 22.2 c. At 25 percent grade the factor is 0.188 and at 30 percent 0.157; Teck and CRU use about 0.157, so their printed totals run 8 to 10 percent below mine. Where a source states only a combined figure it is shown as given, and the TC and RC cells are empty.
* Copper price: World Bank Pink Sheet annual average of the LME grade A cash price, US$/t, rounded (CC BY 4.0). 2026 is the January to October mean of the Pink Sheet monthly series, computed here. Share = combined c/lb divided by (price x 0.045359 c/lb per $/t).
* Price participation (a sliding scale tied to the copper price, in contracts until the 2006 settlement) is noted where a source mentions it and is never added to a figure.
* Kinds: contract avg = a yearly average of contract charges reported by a source; benchmark = the annual settlement between a major miner and a smelter group; spot = the spot market, shown only because it is shippable evidence of the cycle and is not the benchmark.
* "read" means I opened the document and read the figure. "read (fetch)" means a web page read through the fetch tool's extraction. "snippet" means seen only in a search result or a secondary page. "pdf p." is the page of the PDF file as counted by the text extractor; check before quoting.
* "may ship": yes only where the source's own terms allow commercial use (section 2). Rows marked yes for spot are not the benchmark.

## 1. Year table

| year | TC US$/t | RC c/lb | combined c/lb | copper US$/t | charge over price % | source document, page (key, see key list) | read or snippet | may ship |
|---|---|---|---|---|---|---|---|---|
| 1989 | - | - | 29.0 | 2,848 | 22.4 | [spot] BOM89, copper chapter, smelter-terms passage (page not pinned in the OCR): combined spot charge rose 45 percent in H1 1989 to about 29 c, back to the 1988 level by year end (CRU data cited). A spot peak, not a contract term | read | yes (spot only) |
| 1990 | - | - | - | 2,661 | - | [-] GAP. No document found (the only Bureau of Mines volume I could open was 1989) | - | no source |
| 1991 | - | - | - | 2,339 | - | [-] GAP. No document found | - | no source |
| 1992 | - | - | 40.0 | 2,281 | 38.7 | [spot] CMY94, "treatment and refining charges" passage: spot combined almost 40 c at the start of 1992, under 15 c by mid-1994 (start-of-year level) | read | no (NRCan) |
| 1993 | - | - | 22.0 | 1,913 | 25.4 | [spot] U94 pdf p.4 and U95 pdf p.6: spot combined averaged about 22 c in 1993 (CRU) | read | yes (spot only) |
| 1994 | - | - | 25.0 | 2,307 | 23.9 | [contract avg] U94 pdf p.4: long-term-contract charges stated as well above spot, about 25 c (CRU) | read | yes |
| 1994 | - | - | 13.0 | 2,307 | 12.4 | [spot] U94 pdf p.4: spot averaged about 13 c | read | yes (spot only) |
| 1994 | - | - | 22.0 | 2,307 | 21.0 | [benchmark] CMY94, same passage: Japanese smelter pool benchmark about 22 c combined | read | no (NRCan) |
| 1995 | - | - | - | 2,936 | - | [spot] U95 pdf p.6: spot only, under 14 c in Q4 1994 and over 30 c in Q4 1995; no contract number for 1995 | read | no number |
| 1995 | - | - | 17.8 | 2,936 | 13.4 | [benchmark] CMY95: 1995 benchmark combined 17.8 c | read | no (NRCan) |
| 1996 | - | - | 24.1 | 2,295 | 23.2 | [benchmark] CMY95: Freeport 1996 terms, TC $90-95/t and RC 9.5 c, combined 24.14 c as printed | read | no (NRCan) |
| 1996 | 95 | 9.5 | 26.4 | 2,295 | 25.3 | [benchmark] CMY96 / CMY97 / CMY98: Japanese 1996 terms roughly $95 and 9.5 c. U96 (read) has no number, only "high" spot | read | no (NRCan) |
| 1997 | 105 | 10.5 | 29.1 | 2,277 | 28.2 | [benchmark] CMY96, CMY97, CMY98, CMY99: 1997 benchmark $105 and 10.5 c. U97 (read) has no charge figure | read | no (NRCan) |
| 1998 | - | - | 25.0 | 1,654 | 33.3 | [contract avg] U99 pdf p.6: 1998 contract charges averaged about 25 c (given as the prior year in the 1999 chapter; CRU) | read | yes |
| 1998 | - | - | 12.0 | 1,654 | 16.0 | [spot] U98 pdf p.6: combined spot fell to about 12 c in Q4 1998 | read | yes (spot only) |
| 1998 | 99 | 9.9 | 27.5 | 1,654 | 36.6 | [benchmark] CMY98, CMY99: 1998 benchmark $99 and 9.9 c | read | no (NRCan) |
| 1999 | - | - | 17.0 | 1,573 | 23.8 | [contract avg] U99 pdf p.6: contract charges averaged about 17 c per lb of recoverable copper (CRU) | read | yes |
| 1999 | 67 | 6.7 | 18.6 | 1,573 | 26.0 | [benchmark] CMY99: benchmark $67 and 6.7 c (CMY98 printed $66 and 6.7 c when it was agreed) | read | no (NRCan) |
| 2000 | - | - | 18.0 | 1,813 | 21.9 | [contract avg] U00 pdf p.6: contract charges averaged about 18 c, essentially as in 1999 (CRU) | read | yes |
| 2001 | - | - | 19.2 | 1,578 | 26.8 | [contract avg] U01 pdf p.7: annual contractual charges rose about 1 c to 19.2 c; spot fell to about 15 c by year end (CRU) | read | yes |
| 2001 | 76 | 7.6 | 21.1 | 1,578 | 29.5 | [benchmark] CMY02: mid-2001 terms $76 and 7.6 c (a mid-year settlement, not the annual) | read | no (NRCan) |
| 2002 | - | - | 17.9 | 1,559 | 25.3 | [contract avg] U02 pdf p.8: annual contractual charges fell about 2 c to 17.9 c; spot under 6 c (from 15 c at end-2001) (CRU) | read | yes |
| 2002 | 68.5 | 6.85 | 19.0 | 1,559 | 26.9 | [benchmark] CMY02: Japan benchmark by early April 2002 $68.5 and 6.85 c | read | no (NRCan) |
| 2003 | - | - | 12.0 | 1,779 | 14.9 | [contract avg] U03 pdf p.7: annual contracts about 12 c, down from an average of about 18 c in 2002; spot under 5 c in Q4 (CRU) | read | yes |
| 2003 | 46 | 4.6 (inferred) | 12.8 | 1,779 | 15.8 | [benchmark] CMY04: 2003 annual terms $46 and a 4.x c RC whose last digit is garbled in the text; 4.6 inferred from the 10-to-1 habit | read | no (NRCan) |
| 2004 | - | - | 22.0 | 2,866 | 16.9 | [contract avg] U04 pdf p.6: year-end term contracts about 22 c (a year-end level, not a full-year mean); spot ran from about 0 to about 30 c within the year (CRU) | read | yes |
| 2004 | 85 | 8.5 | 23.6 | 2,866 | 18.1 | [benchmark] CMY04: 2004 annual terms $85 and 8.5 c | read | no (NRCan) |
| 2005 | - | - | 40.0 | 3,679 | 24.0 | [spot] U05 pdf p.6 (repeated in later chapters): spot combined averaged about 40 c in 2005. SPOT only; the 2005 annual benchmark was not found (GAP) | read | yes (spot only) |
| 2006 | 95 | 9.5 | 26.4 | 6,722 | 8.6 | [benchmark] T15 slide 16 (source line: CRU): $95 and 9.5 c WITH price participation, total 24.4 c as printed by Teck (their factor is lower than the one used here). U06 gives spot only | read | no (company) |
| 2007 | 60 | 6 | 16.6 | 7,118 | 5.2 | [benchmark] T15 slide 16 (CRU): $60 and 6.0 c, no participation, Teck total 15.4 c. U07: spot under 3 c in Q3 | read | no (company) |
| 2008 | 45 | 4.5 | 12.5 | 6,956 | 4.0 | [benchmark] T15 slide 16 (CRU): $45 and 4.5 c, Teck total 11.5 c. CMY08 prints "$45 and 7.0 c": the 7.0 is the TC converted, not an RC | read | no (company) |
| 2009 | - | - | 13.0 | 5,150 | 5.6 | [contract avg] U09 pdf p.7: term contracts averaged about 13 c in Q4 2009 only (after a mid-year reset); spot about 3 c | read | yes (Q4 only) |
| 2009 | 75 | 7.5 | 20.8 | 5,150 | 8.9 | [benchmark] T15 slide 16 (CRU): $75 and 7.5 c, Teck total 19.2 c (the January settlement; reset lower mid-year) | read | no (company) |
| 2010 | - | - | 11.0 | 7,535 | 3.2 | [contract avg] U10 pdf p.7: term contracts "about 10 to 12 c" during the year (mid-point shown) | read | yes (range 10-12) |
| 2010 | 46.5 | 4.65 | 12.9 | 7,535 | 3.8 | [benchmark] T15 slide 16 (CRU): $46.50 and 4.7 c, Teck total 11.9 c | read | no (company) |
| 2011 | - | - | 18.5 | 8,828 | 4.6 | [contract avg] U11 pdf p.5: term contracts about 15 c for H1 and 22 c for H2 deliveries (mid-point shown); spot over 27 c in March, under 5 c at year end | read | yes (two half-year figures) |
| 2011 | 56 | 5.6 | 15.5 | 8,828 | 3.9 | [benchmark] T15 slide 16 (CRU): $56 and 5.6 c, Teck total 14.4 c | read | no (company) |
| 2012 | 62.5 | 6.25 | 17.3 | 7,962 | 4.8 | [benchmark] T15 slide 16 and T20 slide 8 (CRU): $62.5 and 6.25 c, Teck total 16.0 c. No USGS figure for 2012 | read | no (company) |
| 2013 | 70 | - | 12.4 (TC part only) | 7,332 | 3.7 | [benchmark] U14 pdf p.5 (Japan section): Pan Pacific Copper TC for Freeport concentrate $70/t in 2013, TC ONLY, no RC stated | read | yes (TC only) |
| 2013 | 70 | 7 (assumed 10-to-1) | 19.4 | 7,332 | 5.8 | [benchmark] T15 slide 16 (CRU): $70 and 7.0 c, Teck total 17.9 c. T20 slide 8 prints $73 and 7.3 c for the same year (see gaps) | read | no (company) |
| 2014 | 92 | - | 16.3 (TC part only) | 6,863 | 5.2 | [benchmark] U14 pdf p.5: Pan Pacific Copper TC for Freeport concentrate rose to $92/t in 2014, TC ONLY | read | yes (TC only) |
| 2014 | 92 | 9.2 (assumed 10-to-1) | 25.5 | 6,863 | 8.2 | [benchmark] T15 slide 16 and T20 slide 8 (CRU): $92 and 9.2 c, Teck total 23.6 c | read | no (company) |
| 2015 | 107 | 10.7 | 29.7 | 5,510 | 11.9 | [benchmark] T15 slide 16 and T20 slide 8 (CRU): $107 and 10.7 c, Teck total 27.4 c. No USGS figure | read | no (company) |
| 2016 | 97.35 | 9.735 | 27.0 | 4,868 | 12.2 | [benchmark] T20 slide 8 (CRU, Wood Mackenzie): $97.35 and 9.74 c, Teck total 24.96 c; a fetched summary names Antofagasta with Jiangxi | read | no (company) |
| 2017 | 92.5 | 9.25 | 25.7 | 6,170 | 9.2 | [benchmark] T20 slide 8: $92.50 and 9.25 c, Teck total 23.72 c | read | no (company) |
| 2018 | 82.25 | 8.225 | 22.8 | 6,530 | 7.7 | [benchmark] T20 slide 8: $82.25 and 8.23 c, Teck total 21.09 c | read | no (company) |
| 2019 | 80.8 | 8.08 | 22.4 | 6,010 | 8.2 | [benchmark] T20 slide 8: $80.8 and 8.08 c, Teck total 20.72 c; FM19 (fetched) agrees, agreed Nov 2018 by Antofagasta and Jiangxi | read | no (company) |
| 2020 | 62 | 6.2 | 17.2 | 6,174 | 6.1 | [benchmark] T20 slide 8: $62.0 and 6.2 c, Teck total 15.9 c. FM19 mentions an H1-2020 early deal at $64 and 6.4 c, not the annual | read | no (company) |
| 2021 | 59.5 | 5.95 (assumed 10-to-1) | 16.5 | 9,317 | 3.9 | [benchmark] CO21 pp.3-4 (Cochilco, citing Wood Mackenzie and SMM): 2021 reference TC $59.5/t, RC not printed. Guilin table (fetched) agrees | read (Guilin: fetch) | no (see licences) |
| 2022 | 65 | 6.5 | 18.0 | 8,822 | 4.5 | [benchmark] CO21 p.4 (Cochilco quoting SMM): 2022 TC $65 and RC 6.5 c. Titles of CGEP and CRU pages agree (snippets) | read | no (see licences) |
| 2023 | 88 | 8.8 | 24.4 | 8,490 | 6.3 | [benchmark] RT23 (Reuters via Zawya): the 2024 deal was 9 percent below the 2023 benchmark of $88 and 8.8 c; Guilin table (fetched) agrees | snippet (Guilin: fetch) | no (news) |
| 2024 | 80 | 8 | 22.2 | 9,142 | 5.4 | [benchmark] SMM24: 2024 benchmark $80 and 8.0 c, settled Nov 2023 by Antofagasta with Jinchuan (Reuters snippet) | read (fetch) | no (news) |
| 2025 | 21.25 | 2.125 | 5.9 | 9,947 | 1.3 | [benchmark] SMM24 and KT25: agreed 5-6 Dec 2024 by Antofagasta with Jiangxi and other Chinese smelters, $21.25 and 2.125 c | read (fetch) | no (news) |
| 2025 | 25 | 2.5 | 6.9 | 9,947 | 1.5 | [benchmark] KT25 (Reuters, 27 Nov 2025): Japanese smelters got higher 2025 terms, $25 and 2.5 c | read (fetch) | no (news) |
| 2026 | 0 | 0 | 0.0 | 13,539 (Jan-Oct mean) | 0.0 | [benchmark] KT25b (Reuters, 19 Dec 2025): Antofagasta and one Chinese smelter, $0 and 0 c. CRU26 (Jul 2026): mid-year provisional settlements with index-linked features, figures confidential. DD26: Japanese smelters said 2026 terms were above zero | read (fetch) | no (news) |

Years 1990, 1991 and 2005 (benchmark) are gaps. Nothing is filled for them. 2026's price is a ten-month mean, so its share is provisional.

### Source key

| key | document | URL |
|---|---|---|
| U94 to U05 | USGS Minerals Yearbook, copper chapter for that year (U99 = the 1999 chapter, which also states 1998) | https://d9-wret.s3.us-west-2.amazonaws.com/assets/palladium/production/mineral-pubs/copper/ then 240494.pdf (1994), 240495.pdf, 240496.pdf, 240497.pdf, 240498.pdf, 240499.pdf, 240400.pdf (2000), coppmyb01.pdf (2001), coppemyb02r.pdf (2002), coppemyb03.pdf, coppemyb04.pdf, coppemyb05.pdf |
| U06 to U15 | same series | same base, myb1-YYYY-coppe.pdf (U14 is the one with the Japanese TC for 2013 and 2014) |
| U16 to U22 | same series; no TC/RC figures found | 2016: https://d9-wret.s3.us-west-2.amazonaws.com/assets/palladium/production/atoms/files/myb1-2016-coppe.pdf ; 2017 to 2022: https://pubs.usgs.gov/myb/vol1/YYYY/myb1-YYYY-copper.pdf |
| BOM89 | Bureau of Mines Minerals Yearbook 1989, volume I, via University of Wisconsin digital collections | record https://digital.library.wisc.edu/1711.dl/PPYAWXJZXOESO8L ; file https://asset.library.wisc.edu/1711.dl/DJ3TNNJ5XIGI38K/E/file-3e055.pdf?dl |
| CMY94 to CMY04 | Canadian Minerals Yearbook, copper chapter (NRCan), archived copy | https://bac-lac.wayback.archive-it.org/web/20061103034943/http:/nrcan.gc.ca/mms/cmy/content/YYYY/25.pdf |
| CMY08 | Canadian Minerals Yearbook 2008 review, copper | https://natural-resources.canada.ca/sites/www.nrcan.gc.ca/files/mineralsmetals/pdf/mms-smm/busi-indu/cmy-amc/2008revu/pdf/cop-cui-eng.pdf |
| T15 | Teck Modelling Workshop, 4 Nov 2015 (slide 16 has the TC/RC history, source CRU) | https://www.teck.com/media/Investors-Presentations-Webcasts-20151104-modelling-workshop.pdf |
| T20 | Teck Modelling Workshop, 2 Apr 2020 (slide 8) | https://www.teck.com/media/Teck-2020-Modelling-Workshop-April-2.pdf |
| T24, T26 | Teck investor presentations, 30 Sep 2024 and 23 Jul 2026 (opened; used as cross-check only) | https://www.teck.com/media/20240930-Investor-Presentation.pdf ; https://www.teck.com/media/Investor-Presentation-July-2026.pdf |
| CO21 | Cochilco, Caracterizacion del mercado de los concentrados de cobre, DEPP 28/2021 | https://cochilco.cl/web/download/302/2021/8106/caracterizacion-del-mercado-de-los-concentrados-de-cobre.pdf |
| CO26 | Cochilco, Mercado de concentrados e industria de fundiciones, 2026 (and the 2025 edition) | https://www.cochilco.cl/web/download/1030/2026/16832/mercado-de-concentrados-e-industria-de-fundiciones-2026.pdf ; https://www.cochilco.cl/web/download/975/2025/14993/mercado-de-concentrados-e-industria-de-fundiciones-2025.pdf |
| SMM24 | SMM analysis, benchmark results for 2025 copper concentrate contracts | https://news.metal.com/newscontent/103076770-smm-analysis-benchmark-results-for-2025-copper-concentrate-contracts-finalized |
| KT25, KT25b | Reuters on Kitco, 27 Nov 2025 and 19 Dec 2025 | https://www.kitco.com/news/off-the-wire/2025-11-27/japanese-copper-smelters-seek-2026-tcrcs-different-china-set ; https://www.kitco.com/news/off-the-wire/2025-12-19/antofagasta-agrees-zero-copper-processing-charges-2026-chinese-smelter |
| DD26 | Reuters column (Andy Home) on devdiscourse | https://www.devdiscourse.com/article/international/4001093-roi-copper-concentrate-pricing-slips-into-the-shadows-andy-home |
| CRU26 | CRU, mid-year settlements and index pricing, Jul 2026 | https://www.crugroup.com/en/communities/thought-leadership/2026/mid-year-settlements-signal-a-further-shift-towards-copper-concentrates-index-pricing/ |
| FM19 | Fastmarkets comment on the benchmark and the Antofagasta breakaway | https://www.fastmarkets.com/insights/comment-what-next-for-copper-tc-rc-benchmark-as-antofagasta-begins-breakaway/ |
| RT23 | Reuters via Zawya (search snippet only) | not opened beyond the snippet |
| Guilin | Guilin Metals TC/RC table | https://www.guilinmetals.com/en/tools/tc-rc |
| WB | World Bank Commodity Markets (Pink Sheet) annual and monthly files | https://thedocs.worldbank.org/en/doc/74e8be41ceb20fa0da750cda2f6b9e4e-0050012026/related/CMO-Historical-Data-Annual.xlsx and ...-Monthly.xlsx |

## 2. Licences

I opened the terms pages for USGS, the University of Wisconsin record, the World Bank and Canada.ca (through the fetch tool). The Cochilco notice was read in the report itself. For the rest I opened nothing, and anything not listed has no licence I could find, which means no.

| source | what its terms say, in my words | terms page | commercial use of the numbers |
|---|---|---|---|
| USGS Minerals Yearbook, Mineral Commodity Summaries, Mineral Industry Surveys | Work of a US federal agency: public domain; the agency asks for a credit line and warns that some pictures and third-party items on its pages may carry their own rights. The charge figures in the copper chapters are quoted from CRU, a commercial data house; as facts reported inside a public-domain USGS text they carry no rights of their own, which I judge low risk, but it is Jerus's call. | https://www.usgs.gov/information-policies-and-instructions/copyrights-and-credits | yes |
| Bureau of Mines Minerals Yearbooks before 1995 (UW copy) | The UW record and the cover page of the volume carry the rights statement "No Copyright - United States": a US government work in the public domain. The library also says the user must judge rights for their own use. | http://rightsstatements.org/vocab/NoC-US/1.0/ and http://digital.library.wisc.edu/1711.dl/Copyright ; the Bureau of Mines archive page is https://www.usgs.gov/centers/national-minerals-information-center/bureau-mines-minerals-yearbook-1932-1993 | yes |
| World Bank Pink Sheet (copper price) | Datasets are under CC BY 4.0: reuse including commercial, attribution required; the World Bank adds a few conditions and excludes third-party items. The Pink Sheet copper series is the LME price, so a cautious reader may want to confirm the LME's own position; the World Bank publishes it under its licence. | https://data.worldbank.org/summary-terms-of-use (and https://datacatalog.worldbank.org/public-licenses#cc-by) | yes, with attribution (price only) |
| NRCan, Canadian Minerals Yearbook | Government of Canada terms: reproduction for non-commercial purposes is fine without permission; commercial reproduction or redistribution needs permission, unless a work is marked with the Open Government Licence - Canada, which I did not find on these chapters. | https://www.canada.ca/en/transparency/terms.html (older notice http://www.nrcan.gc.ca/notice_e.html) ; OGL: https://open.canada.ca/en/open-government-licence-canada | no, evidence only |
| Cochilco reports (CO21, CO25, CO26) | The 2026 report's notice allows total or partial reproduction if Cochilco is cited, "unless indicated otherwise"; it does not say whether commercial use is covered, and the TC/RC figures inside are third-party (Wood Mackenzie, SMM). The 2021 report's opening page shows only an intellectual-property registry number. | the notice is on page 1 of CO26 (no separate terms page found) | ambiguous: default no; Jerus's call, and I would not ship the third-party figures on this notice alone |
| US OTA report 1988 (copper) | US Congress agency; a federal work, so public domain. I opened it and took no yearly benchmark figures from it, so it appears nowhere in the table. | https://govinfo.library.unt.edu/ota/Ota_2/DATA/1988/8808.PDF | yes, but nothing used |
| Teck decks, CRU, Fastmarkets, SMM, Reuters (Kitco, Zawya, devdiscourse), Columbia CGEP, Guilin, AusIMM | Company or publisher copyright; I found no licence that permits commercial reuse and did not open their terms pages (bot blocks on several). Evidence only. | not opened | no |
| Not reachable | IEA (bot wall), Wiley / Hindawi / ACM / ResearchGate for the Diaz-Borrego 2019 paper (403), Australia's REQ page (connection failure), METI and Waseda PDFs (403 / 503), mining.com and S&P (403), USGS MYB 2023 and 2024 files (404) | - | unknown |

## 3. Gaps

* No year in 1990 to 1993 has a contract or benchmark number I could ship: 1989 and 1993 have a spot figure only. 1990 and 1991 have no document at all (the Bureau of Mines volumes for 1990 to 1992 were not opened; only 1989 was reachable).
* 1995 to 1997: USGS gives no contract number (1995 has spot only, 1996 and 1997 nothing). The values exist only in the Canadian yearbook (evidence, no ship).
* 2005: no annual benchmark found anywhere; USGS gives a spot mean of about 40 c.
* 2006 to 2008, 2012, 2015 to 2026: no commercially usable benchmark. USGS carries spot levels only (a few rounded numbers for 2006 to 2008), and its 2016 to 2022 chapters carry no charge figure at all. The 2023 and 2024 yearbook files were not found, and the Mineral Commodity Summaries 2023 to 2026 copper pages have no TC/RC text.
* 2009 to 2011: USGS gives a Q4 level (2009), a range (2010) and half-year levels (2011), not the annual benchmark. 2013 and 2014: TC only, no RC.
* Shippable summary: 13 years carry a benchmark-type figure from a public-domain source (1994 and 1998 to 2004 as full-year contract averages, 2009 to 2011 as partial-year, 2013 and 2014 as TC only). Counting spot-only years (1989, 1993, 2005) that makes 16 of the 38 years 1989 to 2026. 22 years have nothing shippable and 25 have no benchmark-type figure.
* Continuous shippable run: 1998 to 2004, seven years. The next longest are 2009 to 2011 (three, partial) and 2013 to 2014 (two, TC only).
* Disagreements that stay unresolved: 2013 is $70 with 7.0 c in the 2015 Teck deck and $73 with 7.3 c in the 2020 deck (I show $70). 2025 has two settlements (China-set $21.25 and Japanese $25). The 2003 RC digit is garbled in the source text. 2021 RC is inferred. Cochilco 2026 shows charts whose values I could not read (images). The 2006 settlement carried price participation whose size I did not add.
* The dated page numbers are as the extractor counted them; they were not rechecked after the last pass.

**Is there ONE continuous yearly record under a licence that allows use in a sold product?** No. The public-domain USGS and Bureau of Mines texts state a contract or benchmark charge for 13 of the 38 years (8 full years, 3 part-years, 2 TC-only), and a spot level for three more, with a continuous run of seven years (1998 to 2004). The years from 2005 onward, and the whole of 1990 to 1997, must come from reports whose terms I could not clear (Teck/CRU slides, Cochilco, news) or from the game's own rule. Assembling the series from news reports of facts is how the figures I have for 2006 to 2026 were found, but the reports themselves are copyrighted, so I list them as evidence, not as shippable.

**Fallback** (one way): ship the USGS-stated years as the anchor points (1994, 1998 to 2004, and the loose 2009 to 2011 levels), and let the game's own cycle rule fill the gaps, tuned to the evidence-only series in section 4. The anchors alone give the high-share regime of 1994 to 2002 and the 2003 to 2004 drop.

## 4. The cycle, from the evidence series

One figure per year, taken in this order of preference: USGS contract average, then the annual benchmark converted at the rule above (2005 has none, so it is left out). The basis column says which. All of it is evidence; only the USGS rows could ship.

| year | combined c/lb | copper US$/t | charge over price % | basis |
|---|---|---|---|---|
| 1994 | 25.0 | 2,307 | 23.9 | USGS contract avg |
| 1995 | 17.8 | 2,936 | 13.4 | CMY benchmark, combined |
| 1996 | 26.4 | 2,295 | 25.3 | CMY Japanese terms, converted |
| 1997 | 29.1 | 2,277 | 28.2 | CMY benchmark, converted |
| 1998 | 25.0 | 1,654 | 33.3 | USGS contract avg |
| 1999 | 17.0 | 1,573 | 23.8 | USGS contract avg |
| 2000 | 18.0 | 1,813 | 21.9 | USGS contract avg |
| 2001 | 19.2 | 1,578 | 26.8 | USGS contract avg |
| 2002 | 17.9 | 1,559 | 25.3 | USGS contract avg |
| 2003 | 12.0 | 1,779 | 14.9 | USGS contract avg |
| 2004 | 22.0 | 2,866 | 16.9 | USGS year-end term level |
| 2006 | 26.4 | 6,722 | 8.6 | Teck/CRU, converted, with participation |
| 2007 | 16.6 | 7,118 | 5.2 | Teck/CRU, converted |
| 2008 | 12.5 | 6,956 | 4.0 | Teck/CRU, converted |
| 2009 | 20.8 | 5,150 | 8.9 | Teck/CRU, converted (January) |
| 2010 | 12.9 | 7,535 | 3.8 | Teck/CRU, converted |
| 2011 | 15.5 | 8,828 | 3.9 | Teck/CRU, converted |
| 2012 | 17.3 | 7,962 | 4.8 | Teck/CRU, converted |
| 2013 | 19.4 | 7,332 | 5.8 | Teck/CRU, converted |
| 2014 | 25.5 | 6,863 | 8.2 | Teck/CRU, converted |
| 2015 | 29.7 | 5,510 | 11.9 | Teck/CRU, converted |
| 2016 | 27.0 | 4,868 | 12.2 | Teck/CRU, converted |
| 2017 | 25.7 | 6,170 | 9.2 | Teck/CRU, converted |
| 2018 | 22.8 | 6,530 | 7.7 | Teck/CRU, converted |
| 2019 | 22.4 | 6,010 | 8.2 | Teck/CRU, converted |
| 2020 | 17.2 | 6,174 | 6.1 | Teck/CRU, converted |
| 2021 | 16.5 | 9,317 | 3.9 | Cochilco/SMM, converted |
| 2022 | 18.0 | 8,822 | 4.5 | Cochilco/SMM, converted |
| 2023 | 24.4 | 8,490 | 6.3 | Reuters/SMM, converted |
| 2024 | 22.2 | 9,142 | 5.4 | SMM, converted |
| 2025 | 5.9 | 9,947 | 1.3 | SMM/Reuters, China-set, converted |
| 2026 | 0.0 | 13,539 | 0.0 | Reuters, Antofagasta deal |

Computed from that table:

* Share of price, 1994 to 2004 (copper at about $1,500 to $2,950): lowest 13.4, median 23.9, highest 33.3 percent. The highest is 1998, when copper bottomed and the charge stayed near 25 c.
* Share of price, 2006 to 2024 (copper at about $4,900 to $9,100): lowest 3.8, median 6.1, highest 12.2 percent. The lowest values come in 2008, 2010, 2011 and 2021 (4.0, 3.8, 3.9, 3.9) and the highest in 2015 and 2016 (11.9, 12.2).
* 2025 is 1.3 percent and 2026 is zero, a break from every earlier year.
* In absolute terms the combined charge stayed between 12.0 and 29.7 c/lb (median 20.1) from 1994 to 2024, almost trendless, while the copper price rose about fourfold. That is why the share fell from the 13 to 33 percent of the 1990s and early 2000s to the 4 to 12 percent of 2006 to 2024: the charge is set by smelter supply and demand for concentrate in cents, not as a fraction of the price. For the game this argues for driving the charge in cents per pound (or dollars per tonne of concentrate) and deriving the share, not the other way round.
* Peaks (combined c/lb): 1997 (29.1), a minor one in 2001 (19.2) on a run of three nearly flat years, 2006 (26.4, with price participation), 2009 (20.8, the January settlement, reset down mid-year), 2015 (29.7), 2023 (24.4). The three large peaks, 1997, 2006 and 2015, are eight to nine years apart, and a fourth followed in 2023 and 2024 before the 2025 collapse.
* Falls from a peak to the next trough: 1997 to 2003 (29.1 to 12.0), six years, with the 1998 to 2002 plateau of 17 to 25 c between; 2006 to 2008 (26.4 to 12.5), two years; 2015 to 2021 (29.7 to 16.5), six years with a pause at 17.2 in 2020; 2023 to 2026 (24.4 to 0), three years. Troughs follow peaks after two to six years. Rises take one to five years: 2003 to 2006 (12 to 26.4, three years), 2008 to 2009 (12.5 to 20.8, one year), 2010 to 2015 (12.9 to 29.7, five years).
* Number of peaks: five to six in 31 years (1997, 2001, 2006, 2009, 2015, 2023), about one every five to six years including minor ones, about one every nine years counting only the large ones.
* Drivers named in the sources I read: concentrate shortage against smelter capacity (USGS cites capacity additions in 1994 and the Grasberg force majeure of late 2003), and since 2025 a concentrate squeeze strong enough to push China-set terms to zero. I did not find a source that settles whether 2025 and 2026 are a new regime or a deep trough; CRU's mid-2026 note points to index-linked pricing replacing the fixed annual benchmark.

A simulated swing "of the same range and pace" after 2026 would therefore be a charge that wanders between roughly 12 and 30 c/lb (about $45 to $110/t of concentrate at a 10-to-1 RC), peaks every six to nine years, falls over two to six years, and gives a share of 4 to 12 percent at today's copper price, with the 2025 to 2026 collapse available as a deeper trough to start from. These are judgements from the table above, not the source's words.

## 5. Evidence-only cross-checks

* Teck decks T15 and T20 give 2006 to 2020 in one table (source line: CRU). I read both slides and checked the arithmetic: the printed totals equal TC times 0.1567 plus RC to within rounding, which is why they sit 8 to 10 percent below my conversion. They overlap for 2012 to 2015 and agree on every year except 2013 ($70 versus $73).
* My numbers reconcile with the coarse table already in docs/studies/spec-copper-margin.md, section 5: 2002 17.9 c (25 percent of $1,559), 2003 and 2004 about 12 and 22 c (15 and 17 percent), 2012 to 2020 at 15.9 to 27.4 c on Teck's own factor (4.4 to 11.3 percent there; 4.8 to 12.2 here because of my higher factor), 2024 about 22 c and about 5 percent, 2025 about 1 percent, 2026 zero.
* CMY 2007 and 2008 print a "9.4 c" and "7.0 c" that are TC converted, not RC. The Teck deck for 2016 prints $92 beside 2014 and $97.35 beside 2016; there is no 2016 figure of $92.
* Reuters and SMM agree that the 2025 benchmark of $21.25 and 2.125 c was set by Antofagasta with Chinese smelters, and that Japanese smelters took $25 and 2.5 c. KT25b gives zero for 2026 for one Chinese smelter; DD26 reports Japanese smelters expecting more than zero.
* Cochilco CO21 forecast $73 for 2023, against the $88 actually settled (RT23, Guilin): a reminder not to ship forecasts.
* Not confirmed: the 2005 benchmark, the 1990 to 1992 Bureau of Mines wording, Cochilco 2026 chart values, the IEA series, the Diaz-Borrego 2004 to 2017 table (a lead worth trying through a library), and Australia's request-for-information page.
