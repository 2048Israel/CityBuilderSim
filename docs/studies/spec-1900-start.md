# The game from 1900: the price level, the history and the calendar, a study for EP1

*2026-10-30. Batch EP0, a read-only study: no game code, dial or harness was changed. Jerus, that night: "might as well run another subsession that does the research ep1 will need for its batch? that way ep1 has less work?". EP1 moves the start to January 1900 with today's tech, and with today's real economy priced at 1900's price level (Jerus, 2026-10-30: "1900 price level"; `docs/queue/EP1.md`). This study finds the factor, the anchors it reaches, the history's sources back to 1900, a rule for the nations that did not exist, and what the calendar moves; §7 is the calls, each with a recommendation.*

*What was read: the code at bc1e81f7 (main, 0.8.78), before tonight's WN4, CD7, XR4, CD8, LB1-LB3 and KP1 land, through `Where`, grep and the maps; the studies `research-1900.md` (§5, §6.3, §11), `spec-nations.md` (§3, §5, §7, §9) and `spec-exchange-rates.md` (§3.7-3.9, §5); `tools/world/FORMAT.txt`, `prices.tsv` and `moneys.tsv`. Figures were read on 2026-10-30 from the sources at the end; downloads were CSV or JSON only, and only the extracted series are kept, in `1900-start/` (§8). "est." marks an estimate and says how it was made; "(rec.)" marks a recommendation, not a fact; [KEY] are the sources at the end.*

---

## 0. In one page

**The price level (§1).** The factor is the US CPI's 1900 over its 2025: 8.333 (est.) over 321.943, **0.02588, or 1/38.6** [BLS-CPI], [MINNFED]. Canada's is 4.458 (est.) over 164.2, **0.02715, or 1/36.8** [SC-CPI], [HSC-K33]. Neither country had an official CPI before 1913-14: the US years are Rees's cost-of-living index joined at 1913, Canada's the wholesale index joined at 1913 (est.). The two factors differ by 5%, inside the estimates' error. The US one is recommended: the world file is in US$, the game opens at D$1 = US$1, and gold held both dollars at par in 1900.

**The anchors (§2).** No one place reaches every dollar today. The factor must reach 68 goods with three prices each, 13 foods priced again in `consumption.json`, 6 wage rungs, 148 building costs (31 upkeeps, 41 consumables), about 12 founding amounts and about 75 nominal dials, and must not reach the ratios and rates beside them. Scaling one side of a ratio between files (food over wage, tuition over wage) breaks the real side silently. Recommended: one constant through four funnels (`PayTier`, the `Good` constructor, `BuildingCatalog`, the seeding strike) and about 35 named literals, checked by twins against the currency reform the game already has.

**Historical from 1900 (§3, §4).** People reach 1900 for 195 of the 196 nations (OWID's long-run series). Real output reaches 1900 for 47 (Maddison 2023), who held 78.5% of the people. Prices reach it for 7 nations from an open source (the BIS), or 18 from the Macrohistory Database, whose licence is non-commercial. The moneys reach 1900 through the gold pars, the pound and the mark. The policy rates reach 1900 for the US$ (a market rate to 1914), the pound, the yen and the euro's ancestor (the Reichsbank); the Swiss franc starts in 1907, the C$ in 1935, the A$ in 1969 and the yuan in 1990. Ten of the 16 commodity series reach 1900 as US prices; bananas have no open series. The rule for every gap: chain the file's level back by growth rates, and before a series starts, follow the region or the anchor money (est.).

**The world was small (§7, ★5).** Its real output in 1900 was about 1/36 of 2022's and 1/17 of 2000's (est., Maddison and OWID). A Historical world from 1900 is a small market that grows 36-fold.

**The nations (§5).** On 1 January 1900, 55 of the 196 were sovereign (42 on the Correlates of War list). The other 141 were colonies, protectorates or parts of another state: Britain held 55, France 20, the Russian Empire 15, Germany 13, the Ottomans 10. 113 became states after 1945. Recommended: all 196 stay as places on today's borders from 1900, with their own people and output, and a ruler by date sells their land and lends them its money until they become states. This is the world file's own rule for territories (Greenland's land is Denmark's), dated.

**The calendar (§6).** Three places read the epoch, and one breaks: `Nations.FIRST_YEAR` follows it to 1900 while the world file starts in 2000, so the world will not open, and the game catches the error and plays on without nations. Four saved fields hold a calendar year. With the price level, an old save would load wrongly, so `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` rise together. CalendarCheck (about 17 assertions) and NationsCheck (about 12) are re-premised on the constants, and the fixture is re-cut.

**The calls (§7):** thirteen, each with a recommendation; the large ones are ★2 (how the factor reaches the anchors), ★4 and ★5 (what of the real world replays before 2000), ★6 (the moneys before the euro) and ★7 (the nations).

---

## 1. The price level

EP1 prices today's real economy at 1900's price level (Jerus, 2026-10-30: "1900 price level"). Every nominal anchor is multiplied by one factor: the consumer price index of 1900 divided by that of 2025. The series behind the figures are `docs/studies/1900-start/price-level-us.csv` and `price-level-ca.csv`, one row a year from 1900 to 2025, each row marked `pub` (published) or `est` (estimated).

### 1.1 The two factors

| money | index and base | 1900 | 2025 | 2025 ÷ 1900 | factor (1900 ÷ 2025) | sources |
|---|---|---|---|---|---|---|
| US$ | CPI-U, 1982-84 = 100 | 8.333 (est.) | 321.943 | ×38.63 | **0.02588** (1/38.6) | [BLS-CPI], [BLS-FAQ], [MINNFED] |
| Canada's $ | CPI, 2002 = 100 | 4.458 (est.) | 164.2 | ×36.83 | **0.02715** (1/36.8) | [SC-CPI], [HSC-K8], [HSC-K33] |

- The US 2025 figure is the BLS annual average. October 2025 was not collected in the shutdown, and the BLS forms the 2025 average from the 11 published months [BLS-FAQ]. The mean of those 11 months is 321.943, the figure the BLS publishes.
- Canada's figures agree with the first study: 4.45 in 1900 and ×36.9 (`research-1900.md` §5.1). That figure was Urquhart's GNP deflator joined to the CPI at 1914. This one comes from a different index (wholesale prices, joined at 1913). Both give the same answer.
- Every year is in the CSVs. If an anchor sits at another year's prices, divide by that year's index instead. The calendar's epoch is 2000 today: 2000 ÷ 1900 is ×20.7 for the US$ and ×21.4 for Canada's $.

### 1.2 Before an official CPI

**The US$, 1900-1912 (est.).** The BLS CPI starts in 1913. Before it, the Minneapolis Fed's table gives the CPI on 1967 = 100. For 1890-1912 that table uses Albert Rees's cost-of-living index as compiled by the BLS [MINNFED]. It prints whole numbers: 1900 is 25, 1913 is 29.7. The join is at 1913, where the BLS CPI-U is 9.9. So every earlier year is divided by 29.7 ÷ 9.9 = 3, and 1900 = 25 ÷ 3 = 8.333. The table's own ratio on its 1967 base is 967.5 ÷ 25 = ×38.70. Because the table rounds to whole numbers, 1900 could be anywhere from 24.5 to 25.5. That puts the factor between ×37.9 and ×39.4.

**Canada's $, 1900-1913 (est.).** Statistics Canada's CPI table starts in 1914, at 6.0 [SC-CPI]. Historical Statistics of Canada has no consumer or cost-of-living index before 1913 [HSC-K]. So the chain is:

- 1913 = 6.0 × (K8 1913 28.7 ÷ K8 1914 28.9) = 5.958. K8 is the CPI for 1913-1975 on 1971 = 100 [HSC-K8].
- 1900-1912 = 5.958 × K33 for that year ÷ K33 for 1913 (83.4). K33 is the general wholesale price index, excluding gold, 1867-1975 [HSC-K33]. 1900's value is 62.4, so 1900 = 4.458.
- Wholesale prices stand in for consumer prices here, and the two can drift apart. How far they drifted in Canada before 1913 is not measured here. From 1900 to 1913 Canada's wholesale index rose 34%, against 19% for the US cost of living (25 → 29.7). Those were boom years in Canada, with rents climbing fast: the first study found +71% in the family budget's rent, 1900-10.
- The wholesale index falls from 1912 to 1913 (85.2 → 83.4), so the spliced CPI does too (6.087 → 5.958).

### 1.3 The other measures and their spread

| measure | 1900 | 2025 ÷ 1900 | note |
|---|---|---|---|
| US CPI, this splice | 8.333 (est.) | ×38.63 | recommended |
| US CPI, Minneapolis Fed's own 1967 base | 25 | ×38.70 | [MINNFED] |
| US CPI, 1900 = 24.5 or 25.5 (the table's rounding) | 8.17-8.50 | ×37.9-39.4 | |
| US CPI, Officer and Williamson | 8.14 | ×39.55 | [MW-CPI]; their 1913-28 differs from the BLS (9.60 against 9.9 in 1913) |
| US GDP deflator, 2017 = 100 | 4.07 (est.) | ×31.65 | [BEA-DEF] from 1929, [MW-DEF] before, joined at 1929 (8.778 against 8.75) |
| Canada CPI, this splice | 4.458 (est.) | ×36.83 | recommended for Canada's $ |
| Canada, K33 joined at 1914 instead of 1913 | 4.384 (est.) | ×37.45 | wholesale rose 2.4% in 1914, the CPI 0.7% |
| Canada, K8 rebased at its 1971 overlap (1913 = 5.998) | 4.488 (est.) | ×36.59 | 1914 = 6.0 is rounded to one decimal |
| Canada, Urquhart's GNP deflator joined at 1914 | 4.45 (est.) | ×36.9 | the first study [CM] |

- The CPI factors span about ×37 to ×39.5. The GDP deflator gives ×31.7, about a fifth less. It covers investment and government as well as consumption. The anchors are mostly wages and the prices people pay, so the CPI is the measure to use.
- The Jordà-Schularick-Taylor database (R6) is offered only as xlsx and dta, and its documentation PDF could not be read here. It is not checked. Its series end in 2020, so the comparison is 2020 ÷ 1900: ×31.06 for the US$ and ×30.73 for Canada's $ in these CSVs.

### 1.4 Consistency check

In 1900 both dollars were gold at the same par: US$1 = C$1 [POWELL]. In 2025 the Bank of Canada's annual average was C$1.3978 per US$, so C$1 = US$0.7154 [BOC-FX]. If prices had kept their 1900 relation across the border, Canada's factor would be ×38.63 × 1.3978 = ×54.0. It is ×36.83. In one line:

**36.83 = 38.63 × 1.3978 × 0.682**

The last term is the real exchange rate. In one currency, Canada's consumer prices rose about a third less than the US's. From 1914, where both are official CPIs, the term is 0.608 (×27.37 against ×32.19, taking 1914's rate as par, est.). So the splice before 1914 does not make the gap. The two factors differ by only 4.9% (38.63 ÷ 36.83 = 1.049). That is the C$'s fall to US$0.7154, almost cancelled by Canada's slower inflation.

### 1.5 Recommendation

Use the US CPI factor, **0.02588 (1/38.6)**, for every nominal anchor: the world's US$ prices and the city's founding D$. The world file is priced in US$. The game opens at D$1.00 per US$ with a basket costing the same at home and abroad (`ForeignAccounts.OPENING_RATE` = `OPENING_PARITY` = 1.00). In 1900 Canada's dollar was at gold par with the US$. So one US factor keeps both at 1.00, as history had them. Its link before 1913 is a cost-of-living index; Canada's is a wholesale one.

**The alternative** is Canada's factor, **0.02715 (1/36.8)**. It prices 1900 about 5% higher. The gap is inside the uncertainty of the two estimates for 1900, so the choice changes little. The GDP deflator (1/31.7) is not recommended.

### 1.6 Missing

- **The JST R6 cross-check** (`cpi` for USA and CAN, and its GDP deflator). The data is xlsx or dta only. EP1 downloads `JSTdatasetR6.xlsx` and compares its 2020 ÷ 1900 with ×31.06 (USA) and ×30.73 (CAN). Rule until then: the CSVs stand.
- **Canada's family budget, 1900-1913.** This is the Department of Labour's weekly cost of staple foods, fuel, light and rent in 60 cities, for 1900, 1905 and 1910-17 (Canada Year Book 1918, p. 485, [CYB18]). Historical Statistics of Canada's 1965 edition has an index of it for 1900-39 (Section J, cited by [HSC-K]). Both exist only as scanned PDFs, and neither was read. It is a narrow basket that leans on rent, so it may raise Canada's factor. Rule: keep the wholesale splice. Note the budget beside it if EP1 keys it in.
- **McInnis's dataset [CM].** The first study's links (datalib.usask.ca) return 404 as of 2026-10-30, so its 4.45 was not re-read. The wholesale splice above checks it independently.
- **Canada's GDP deflator back to 1900.** Not built. It would join three pieces: Urquhart to 1926, HSC K172 to 1961, and Statistics Canada after. The US deflator already shows the spread.

## 2. The nominal anchors the factor must reach

Surveyed on main at 0.8.78 (bc1e81f7), before WN4 lands. No file was changed and nothing was run.

- **There is no single place today.** Three multipliers exist, and each reaches only part of the money (§2.7). Every other anchor is a literal read straight.
- **Counts.** About 75 true nominal amounts in `static final` dials (30 seeded through the money machinery, about 45 that bypass it), plus six wage rungs, 68 goods with three prices each (59 importable, 59 exportable), 13 foods priced a second time in `consumption.json`, 148 buildings with `cashCost` (31 with `upkeep`, 41 with `consumables`), and about 12 founding amounts. Harnesses: 8 hold 107 study-table literals, 3 hold the wage 3.46, and about 95 fixture-cash call sites.
- **The traps are the ratios that cross files.** Food price over wage, tuition over wage, materials over cash cost, run-works dollars over cash per point, the two land bases. Scale one side and the real side breaks silently, 35 times over.
- **Recommendation.** One compile-time constant, computed from the Historical US path so that it times the path's 2025 level is 1. Apply it at four funnels and about 35 named literals, not at 120 sites, and test with the twin-city method of `DenominationCheck`.

Conventions. Money fields are in thousands (`BuildingsTemplate.java:5-16`): .20 reads $200. "D$" is the city's founding dollar. `ForeignAccounts.OPENING_RATE` is 1.00 (`ForeignAccounts.java:108`), so at founding 1 D$ = 1 US$. The world's literals are 2025 US$ (`WorldMarket.REFERENCE_YEAR` 2025, `WorldMarket.java:88`; the spec's D13).

### 2.1 The world's prices

| file:line | what | value | money and level | scale? |
|---|---|---|---|---|
| `Good.java:114-1117`, constructor `:1150` | 68 goods, each: import ceiling, export floor, base freight | 59 with an import price, 59 with an export price; import from `.0008` (grains, kg, `:221`) to `2302.2` (rolling stock, `:647`) | thousands of world US$ a unit, 2025 | yes. Choke point: the constructor (`:1150-1163`) |
| `Good.java:340` | MATERIALS | 18 import, 7.2 export, 4.05 freight | as above | yes. Copied at `BuildingManager.java:70` (`MATERIALS_WORLD_PRICE` 18; `:242` makes `materialsCost` from it) |
| `Good.java:320, 753, 1000, 1115` | steel .650/.470; crude .60/.50; copper 10.5/10.0; trucks 215 | | as above | yes |
| `Good.java:1196` | `baseFreight`: three quarters of the wedge on each good | 68 values | world US$ | yes, with the goods |
| `consumption.json` (13 foods), read at `Consumption.java:272-273` | the same food prices again (grains .0008/.0005 ... snacks .010/.0062) | 26 values | world thousands a kg | yes. Held equal to `Good` to 1e-12 by `ConsumptionCheck.java:299-304` |
| `LandMarket.java:111` | ground a ft2 before premiums | .0007 (US$0.70, about US$75,000 a ha) | thousands of US$; used raw at `:424` and as `BASE/unit` at `:1420` | yes, both bases together |
| `LandManager.java:240` | opening sale price a ft2 | .001 | founding thousands (seeded) | yes, with the land base: .001/.0007 is the 43% margin |
| `Retail.java:67` | opening shelf price and floor | .3 | founding thousands (seeded) | yes |
| `UtilitiesHandler.java:77, 150` | water a unit; power a kW (marked "temporary") | .05; .01 | founding thousands. Plain fields, redenominated on a reform only, never struck | yes |
| `Motoring.java:97` | fuel a journey (fallback for a load, `Game.java:17366`) | .002 | world thousands | yes |
| `Freight.java:275` | `WORLD_FUEL_PER_TONNE` | .03 | history: "nothing in the model reads it" | no (dead) |
| `SitePreparation.java:44, 51`; `RunWorks.java:48, 66`; `BridgeWorks.java:44-62` | earth US$20 a m3; wall US$430 a m2; deck US$250 a ft2; tunnel bore US$35e6 a km; Confederation C$1.3e9 x CPI 1.79 x .72 | 7 literals | real dollars (not thousands), 2025 US$ | yes. Turned into points over the template's founding cash a point (`RunWorks.java:127`, `BridgeWorks.java:122`) |
| `WorldData` (world-1.bin), `WorldMarket` freight table | output is constant 2015 US$; good prices are ratios to 2025; CPI is 2015 = 100 | file | real or ratio | no. `sigma`/`phi` fit the model to the literals (`WorldMarket.java:620-624`), so scaled literals scale the band by themselves |
| `Nations.java:144-150` | income class limits | 890 / 3,510 / 10,890 | US$ of 2015 against the file's real output | no |

No world wage exists in the model. Freight and electricity have no world tariff: haulage rates come out of wages, diesel and trucks. Land by nation (WN5, not built) is a ratio to Canada's hectare, so it does not scale.

### 2.2 Founding wages and per-head amounts

| file:line | what | value | money and level | scale? |
|---|---|---|---|---|
| `PayTier.java:68-84` | the six rungs: unskilled, skilled, college, professional, senior, elite | 3.460, 4.500, 5.500, 8.200, 9.600, 15.600 a month | Nova Scotia 2026 medians, thousands | yes. Choke point for the whole ladder (reads: `PopulationManager.java:263`, `LabourMarket.java:77,268`, `Education.java:744`, `Game.java:6874,13631`, `TaxPolicy.java:263,1093`) |
| `LabourMarket.java:77, 112-115` | default minimum wage; dial bounds | = unskilled; x.25 and x5 | derived from `PayTier` | follows. Do not scale again |
| `TaxPolicy.java:1093`, `:263` | pension wage base; default fixed grant | unskilled / unit; share x unskilled | derived | follows |
| `SocialSecurity.java:86`; `Unemployment.java:76-82`; `Crime.java:110` | pension .45; EI .55 and cap `68_900/12/3_460`; theft .25 | ratios of the unskilled wage | | no. The 3_460 inside `Unemployment.java:82` and `TaxPolicy.java:248` (`525/3_460`) is a denominator: leave it |
| `WageBand.java:141-144` | wage-tax band multipliers | .80 / 1.00 / 1.90 / 2.35 | ratios | no |
| `TaxPolicy.java:866, 869, 1034` | transit fare, its cap, fuel duty cap | .0025, .05, .001 | founding thousands, read as `x expectedLevel` (`:958, 1042`) | yes |
| `Healthcare.java:141-143, 180-181` | fees: general, childcare, senior, burial, cremation | .010, .150, .300, 3.000, .900 | founding thousands (seeded) | yes |
| `Education.java:221-234` | tuition table, 9 kinds | .10, .18, .55, 1.20, 2.60 | founding thousands (seeded) | yes. Read against the wage in `Education.java:739-748` |
| `Bank.java:3154` | account fee a household a month | .012 | founding thousands (seeded) | yes |
| `EconomyManager.java:1065-1069` | new-rental rebate cap and band | 6.3; full below 350; none from 450 | founding thousands, x `foundingToToday` (`Game.java:383-388`) | yes |
| `HouseholdBalance.java:360` | opening buffer | 1.5 months of disposable income | derived from wages | follows |

### 2.3 Building costs and other data files

| file:line | what | value | money and level | scale? |
|---|---|---|---|---|
| `buildings.json`, parsed at `BuildingCatalog.java:191` | `cashCost`, 148 buildings | 146 to 4,200,000 | thousands, founding. A House is 266 + 10 materials x 18 | yes. One loader |
| same, `:194` | `upkeep`, 31 buildings | 0 to 2,100 a month | thousands | yes |
| same, `:221` | `consumables`, 41 buildings | .566 to 17,726 a month | world thousands | yes |
| same | `constructionMaterials` (148), `constructionPoints` (148), `jobs` (137), `land`, `capacity`, `electricity`, `water`, `stock` | physical | priced by `Good.MATERIALS`, the builders' wages, `PayTier` | no, but they carry the other anchors |
| `since` (41), `production1`, `pump`, `wash`, `gen` | years and quantities | | | no |
| `BuildingsTemplate.java:1225, 782` | `foundingCashCost` is captured from the loader's value | | | follows the loader |
| `consumption.json` | prices (§2.1); `referenceIncomeMultiple` 27 | | ratio | prices yes; the multiple no |
| `conversion-saves.json` | five old saves' land, with `foundingCash` | fixture | not live | no |

### 2.4 The opening money

Founding is `Game.buildWorld` (`Game.java:280-683`), reached by the constructor and `newGame()`. A city opens with no bank balance, no central-bank reserves, no firm cash, no loans.

| file:line | what | value | money and level | scale? |
|---|---|---|---|---|
| `Game.java:613`, `FOUNDING_CASH` `:1130` | treasury (Standard preset) | 100,000 = D$100M | founding thousands | yes |
| `Game.java:629`, `FOUNDING_RESERVE_USD` `:1133` | vault, bought at the opening rate | 25,000 = US$25M | thousands of US$, 2025 | yes |
| `Founding.java:130-139` | Lean 25,000 / 10,000; Wealthy 2,500,000 / 1,000,000 | 4 | as above | yes |
| `Founding.java:213-222` | custom bounds: cash 6,000 to 10,000,000; vault 0 to 4,000,000 | 4 | as above | yes (0 stays 0) |
| `Founding.java:113`, `Game.foundTheLandBond` | Insane preset: land owed abroad | `.0007` x the centre's ft2, 3% for 20 years | derived from the US$ land base | follows. Do not scale twice |
| `Bank.java:5510` (`reset`) | bank cash, deposits, branches | 0 | | |
| `Bank.java:187`, `:2018` | paid-in capital a branch; capital scale | 32,000; 400,000 | founding thousands (seeded) | yes |
| `CentralBank.java:908` (`reset`) | opening reserves | none; the vault is read from `ForeignAccounts` | | follows the vault |
| `HouseholdBalance.java:964` | households' savings | 1.5 x disposable | | follows wages |
| `Sector.java:1015` | firm cash | 0 | | |
| `Equity.java:129` | a founding share | 1.0 = D$1,000 | seeded | yes |
| `LandManager.java:176`; `BuildingManager.java:6926` | opening land 3,000,000 ft2; builders' yard 36 materials | physical | | no |

### 2.5 Dollar literals in dials

Counted from `docs/dials.md` by reading names and sentences: 4,760 constants, 3,464 outside harnesses, about 75 true nominal amounts. The rest are rates, shares, counts, physical units, pixels and seeds.

**5a. Seeded through `seedMoneyConstants` (`Game.java:19243`), 15 classes.** Each is `founding / unit`. Eight of them (`Retail`, `LandManager`, `BuildingManager`, `Healthcare`, `Education`, `TaxPolicy`, `Equity`, `Bank`) are also struck every month at `unit / level` (`Game.java:13901-13923`); the other seven are seeded at the unit only.

| class | literals | scale? |
|---|---|---|
| `BuildingManager` / `BuildingsTemplate` | 148 x cashCost, upkeep | yes |
| `Bank` | 3 (above) | yes |
| `Healthcare` | 5 | yes |
| `Education` | 5 values | yes |
| `LandManager`, `LandMarket`, `Retail`, `TaxPolicy` | 1 each | yes (`TaxPolicy` derived) |
| `Equity` | 1 (`FOUNDING_PRICE`) | yes |
| `Exchange` | `MIN_DEALER_EQUITY` 1.0 (`:373`) | yes |
| `ForeignAccounts` | `MIN_TRADE` 50 (`:321`) | yes. `parityBase` (`OPENING_RATE/unit`, `:199`) and `MIN_RATE`/`MAX_RATE` (`:170-172`) are rates: no |
| `LabourMarket` | 2 (derived) | follows |
| `CapitalFlows` | `MIN_STOCK` 1 (`:91`) | yes |
| numeric floors: `OrderBook.DUST` 1e-9 (`:63`), `Exchange.MIN_FAIR` 1e-9 (`:357`), `HouseholdBalance.MIN_MOVE` 1e-12 (`:2987`), `OutwardInvestment.MIN_MOVE` 1e-9 (`:119`) | 4 | no. Rounding guards, they follow the unit and not the price level |

**5b. Bypass the machinery** (a literal read straight, or a plain field).

| class | literals |
|---|---|
| `PayTier`, `Good`, `consumption.json`, `UtilitiesHandler`, `Founding`/`Game` founding amounts | §2.1, §2.2 and §2.4 |
| `Game` | `FIXED_ISSUE_COST` 12 (`:12359`, read as x level / unit), `BUILD_NOTE_GRANULE` 1000 (`:10542`), `BUILD_BOND_GRANULE` 100 (`:12344`) |
| `EconomyManager` | `NRRP_CAP/FULL_BELOW/NONE_FROM` (3) |
| `TaxPolicy` | transit fare, cap, fuel duty (3) |
| `BuildingManager` | `MATERIALS_WORLD_PRICE` (1) |
| `Motoring`, `SitePreparation`, `RunWorks`, `BridgeWorks`, `LandMarket` (raw use) | 1, 2, 2, 3 (incl. CPI and CAD), 1 |

**5c. Look like money, do not scale.** Display and drawing thresholds: `SectorStatements.THOUSANDS_UNTIL` 10_000_000 (`:185`) and `NOTICE` .001, `FundView.FOLD_UNDER` 1, `TradeLanes.MIN_LANE` 300 (`:37`). `Denomination.MAX_UNIT` 1e12 and `StudentHousehold.UNLIMITED` 1e15 are guards. Debt limits, rates, `CAR_DEPOSIT`, loan-to-cost, `MAX_INSURABLE_MULTIPLE`, `RENT_CAP` are ratios.

### 2.6 Dollar literals in harnesses

| harness (file:line) | what | count | scale? |
|---|---|---|---|
| `CementConcreteCheck:122,128`, `CoalMiningCheck:208,217`, `CopperMiningCheck:117,196,219`, `CopperRefiningCheck:207,230`, `ForestryCheck:136,215,221`, `MaterialsCheck:730,868,871,1253,1256`, `QuarryingCheck:298,304`, `WoodProductsCheck:121,221,239` | study tables of cost, running cost, import and export price, "D$ thousands" | 8 harnesses, 107 literals | yes: re-premise to the factor times the study figure |
| `CoalMiningCheck:231`, `MaterialsCheck:733` (diesel .0006), `OilCheck:668` (duty), `RefineryCheck:576` (78,000), `RailDepotCheck:61` (lorry .319), `PortCheck:126` (US$2.0, .080), `LandCheck:1730` (.0004), `GlobeCheck:2466` (D$1.16 a US$) | one-off prices | 8 | yes, except the GlobeCheck rate |
| `BuildingDataCheck` (4 `getCashCost() == literal`, 2 "18 x materials"), `PortCheck`, `BusinessServicesCheck`, `ManufacturingCheck` | pinned building costs | 4 harnesses | yes |
| `CrimeCheck`, `EducationCheck`, `OutsideCheck` | the unskilled wage 3.46 | 3 | yes |
| 35 harnesses name `PayTier` | through the constant | 35 | follow |
| `CarCheck`, `CoalMiningCheck`, `MiningCheck`, `OilCheck`, `QuarryingCheck`, `RailCheck`, `VanCheck` | a Good price compared with a decimal literal | about 11 lines | yes |
| fixtures: `LandClassCheck:103`, `SlopeCheck:87` (1e8), `RunWorksCheck:114` (1e10), `AuctionCheck:144` (3e8), `setCash`/`addCash`/`Founding.custom` | treasuries "more than any order costs" | about 95 call sites (`CreditCheck` 34, `NewGameCheck` 18) | no; they stay big enough |
| `CENT` .01/1000, 1e-5 and `TOLERANCE` 1e-6 in 9 harnesses | absolute money tolerances in thousands | | no |
| text | 64 harnesses name "D$" (227 amounts), 120 "US$" amounts | labels | edit if shown |

127 of 154 harnesses hold underscore-grouped literals (`BankCheck` 250, `ExchangeCheck` 129, `CreditCheck` 102), mostly quantities. `tools/ensemble/score.py` holds no money literal (rates, shares, months, `TAKEOFF_POP` 40000).

### 2.7 The key design question

**What exists.** Three multipliers, none complete.

| mechanism | file:line | opens at | reaches | misses |
|---|---|---|---|---|
| `Denomination.unit`, `money()`, `seedMoneyConstants`, `reformCurrency` | `Denomination.java:79,123`; `Game.java:19243, 19295` | 1 (a founding is always unit 1: `Game.java:301-304`) | the 15 classes above, every live balance, every cache; guarded by `DenominationCheck` | `PayTier`, `Good`, JSON, tariffs, founding amounts. Also divides the rate (`ForeignAccounts.java:199`) |
| `Expectations.level` / `struck` | `Expectations.java:106, 112`; `Game.java:13901` | 1.0 (`:174, :219, :252`) | the eight monthly re-struck classes; five classes keep a copy (`BuildingManager.java:196`, `Retail.java:147`, `TaxPolicy.java:949`, `InfrastructureManager.java:721`) | the same. It is a ratio to founding |
| `WorldEconomy.priceLevel` x rate | `WorldEconomy.java:213`; `EconomyManager.java:309-312`; `Game.java:12033` | 1.0 | every foreign price, materials, food, and land's US$ side (`Game.java:427`, `LandMarket.java:424`) | wages, tariffs, consumption's multiple. It is a ratio |

**What would double-scale or mis-scale.**
1. **Putting the factor in an index.** `ForeignAccounts.setParity` is `parityBase x local / world` (`:271`), and both indices open at 1.00. A world level that opened at f against a city index of 1 gives parity 1/f, and the D$ is pulled to 35 a US$.
2. **Scaling the file's real paths.** `WorldData` prices are ratios to 2025 (real: Pink Sheet over CPI). The 1900-2000 good prices EP1 gathers must be deflated the same way, or the 1900 level is counted twice. The dollar's own path (WN4) is a ratio to founding, so f is absolute and the path multiplies it: consistent only if f x P(2025) = 1.
3. **Pairs written twice.** `Good.MATERIALS` and `BuildingManager.MATERIALS_WORLD_PRICE`; `consumption.json` and `Good`; the two land bases (equal "at the founding rate and unit", `LandMarket.java:419-424`); `.001` over `.0007`.
4. **Ratios between files.** `CityBasket.java:77` passes `foreign.getRate()` only, so `incomeMultiple` (`Consumption.java:350`) is wage over json price at the rate: if wages carry f and the json does not, a household reads 35 times poorer. Same shape: tuition over wage (`Education.java:739-748`); US$ works over founding cash a point (`RunWorks.java:127`); materials over cash cost (`Founding.orderCost`, `:545`).
5. **Derived constants scaled again.** The `PayTier` family in §2.2.
6. **The unit and the factor both applied.** Founding by a reform opens the rate at f (`ForeignAccounts.java:199`) and divides D$ by 35. If the US$ literals also carry f, foreign prices come out at f squared.

**Recommendation for EP1: a list fed by one constant.**
- One constant, `PRICE_LEVEL_1900` (name for EP1), compile-time like `CityCalendar.EPOCH_YEAR` (`CityCalendar.java:28`). Every city starts in 1900, so no saved field. If the start year later becomes a choice (`research-1900.md:648`), it moves to `Founding` and `DataSave`. Compute it from the same CPI series the Historical path lays, so that f x the path's 2025 level is 1. Assert that in a harness.
- Four funnels cover most values: `PayTier` (6, `:68-84`); the `Good` constructor (68 x 3, `:1150`) with `consumption.json`'s reader (`Consumption.java:272`) and the materials copy removed; `BuildingCatalog` (`:191, 194, 221`: 148, 31, 41); and the seeding strike (`Game.java:13905`, `19243`: pass `unit / f`, except the three rate guards and the four numeric floors).
- About 35 literals by name: §2.4 and §2.5 (5b), plus the three transit and duty dials and the two tariffs. Each gets a ratio assertion.
- Keep the rate at 1.00, so a founding D$ stays a founding US$. Do not put f in `WorldEconomy`, `PriceIndex` or `Expectations`.
- Test by twins, as `DenominationCheck` does. The oracle exists: `Game.reformCurrencyForTest` (`Game.java:19509`). A city founded at f must match a city founded at 1 and reformed by 1/f, in D$ and in every real quantity. The rate differs (1.00 against f), so compare in D$. Add a `PriceLevelCheck` for items 3 to 5 above.
- Cheaper alternative, not tried: found at 1 and reform by 1/f at month 0. It reuses the tested machinery and touches no literal. But the rate opens at f, the US$ side stays 2025's (not "the world's prices at 1900's level"), and `reforms` becomes 1, which renames the money ("second ..."). Worth a decision in the study.
- Outside the anchors, f is read by the rankings: the leaderboard's D1 bridge and WN6's real US$ (`spec-exchange-rates.md` 3.9) say P_US,2015 / P_US,founding is 1. With a 1900 founding it is CPI 2015 over CPI 1900. That belongs to LB1 to LB3 and WN6.
- The old saves rule applies: wages and templates would load 35 times off, so `SAVE_FORMAT` (48) and `OLDEST_LOADABLE_FORMAT` (48) rise together.
- Display: prices fall to a 35th, so `Money.unitPrice` (`ui/Money.java:283`) needs a look for sub-cent prices (LPG .0002345 becomes about $0.007 a litre).

## 3. Historical from 1900: people, output, prices and moneys

The world file runs 2000-2100. EP1 starts the game in 1900, so each series needs a century behind it. This section says which sources reach 1900, which start later, and how to fill the years before a series starts. Counts are of today's 196 nations (the ISO3 list in `tools/world/moneys.tsv`), made on 2026-10-30 from CSV and JSON files. Rules marked **(rec.)** are recommendations, not facts. "est." marks an estimate and says how it was made.

Three files go with it, in `docs/studies/1900-start/`:

- `series-coverage.csv`: each source, its first year, its count of nations in 1900, 1913, 1950 and 1960, its licence and URL.
- `moneys-eight-regimes.csv`: 86 rows, one per regime period of each of the eight moneys, 1900-2000.
- `moneys-eight-annual.csv`: the eight moneys' units per US$, one row a year 1900-2000, each cell marked pub, par, der or est, or left empty.

### 3.1 What reaches 1900

| Series | Reaches 1900 | Starts later | Count 1900 / 1913 / 1950 / 1960 |
|---|---|---|---|
| People | OWID long-run population (HYDE, Gapminder, UN WPP) | UN WPP 1950, WDI 1960 | 195 / 194 / 196 / 196 |
| Real output | Maddison Project 2023, GDP per head | PWT 1950, WDI 1960 | 47 / 71 / 140 / 146 (1929: 67) |
| Prices | JST R6 (18 economies), BIS long CPI | IMF 1950s, WDI 1960 | BIS 7 / 8 / 26 / 34; JST 18 throughout; WDI 65 in 1960 |
| Moneys | gold pars, BoE (pound), NBER (mark, franc), JST R6 | IMF 1950, PWT 1950, OECD 1957, WDI 1960, H.10 1971 | IMF 142 in 1950, 153 in 1960; WDI 154 in 1960 |

People reach 1900 for nearly every nation. Real output reaches 1900 for 47 nations, which held 78.5% of the 196 nations' people in 1900. Prices reach 1900 for under 20 nations from an open source. Moneys reach 1900 through the gold pars, and through published market rates for only the pound and the mark.

### 3.2 People

**The source.** Our World in Data's long-run population joins HYDE (to 1800), Gapminder (1800-1949) and UN WPP 2024 (1950 on) [OWID-POP]. It has 195 of the 196 nations in 1900. Ireland is missing before 1950, as the whole island is a separate entity to 1920; Macao is missing 1913-1929. The world file's own source, UN WPP, starts in 1950 with all 196 [WPP]. WDI starts in 1960 with 195 (no Taiwan) [WDI].

**Borders.** OWID and Gapminder count each nation within today's borders. Gapminder's values before 1950 are interpolations between benchmark counts for many nations, so they are est.

**Gap rule (rec.).** Keep WPP from 1950. For 1900-1949 take OWID's values, scaled by one ratio per nation so that 1950 joins WPP's 1950. Mark 1900-1949 est. For Ireland 1900-1949, take the whole island's series times the Republic's share of the island in 1951 (est.).

### 3.3 Real output

**The source.** The Maddison Project Database 2023 gives GDP per head in 2011 international dollars and population, for 169 countries [MPD]. It is CC BY 4.0. The xlsx is the file to download (https://dataverse.nl/api/access/datafile/421302); OWID republishes GDP per head as CSV [OWID-MPD].

| Year | Nations with a value |
|---|---|
| 1900 | 47 |
| 1913 | 71 |
| 1929 | 67 |
| 1950 | 140 |
| 1960 | 146 |

72 nations have a value in or before 1900. 27 of them have values only at benchmark years either side of 1900, so 1900 needs interpolation. 33 nations never have a value: small states, plus Sudan, Somalia, Papua New Guinea, Eritrea, Timor-Leste and Guyana among others. The USSR (to 1972), Yugoslavia (to 1951) and Czechoslovakia (to 1969) are single entities; their successors start in 1973, 1952, 1970 and 1985. Maddison also gives eight regions and the world; the OWID file has them at decades only, the xlsx's regional sheet annually.

**Borders.** Maddison's own series were adjusted to present frontiers where he could. The Project keeps current borders where possible, but its 2013 working paper says many historical estimates refer to other territories, and its former states (USSR, Yugoslavia, Czechoslovakia) are kept whole. The 2023 release page does not say. Treat the series as present borders, and check the xlsx's notes per nation before relying on it.

**WDI and PWT.** WDI's constant-dollar GDP starts in 1960 with 105 nations [WDI]. Penn World Table 10.01 has real GDP from 1950 for 183 countries [PWT]; it is the bridge where WDI starts late.

**Gap rule (rec.).** Keep the world file's level (2015 US$ in 2000). Chain it back with growth rates only, never Maddison's dollar levels:

1. WDI's real growth back to its first year.
2. Then PWT's real growth back to 1950.
3. Then Maddison's growth of GDP (GDP per head times population), interpolating log-linearly between benchmark years.
4. Before a nation's first Maddison value: its Maddison region's growth of GDP per head, times its own population growth (est.).
5. A successor state before its first year: the former state's growth (USSR, Yugoslavia, Czechoslovakia) (est.).
6. The 33 never in Maddison: region growth from their first WDI or PWT year back (est.).

Every chained year is est. where step 4, 5 or 6 made it.

### 3.4 Prices

**The sources.** WDI's CPI starts in 1960 with 65 nations [WDI]. The IMF's 2025 CPI dataflow is thin before 1970: 3 nations in 1950, 24 in 1960 [IMF-CPI]; the old IFS history of 1948-1957 is not in it. The BIS long CPI reaches 1900 for 7 nations (Belgium, Denmark, Britain, Iceland, the Netherlands, Norway, Sweden), 8 in 1913 with the United States, 16 in 1929, 26 in 1950, 34 in 1960 [BIS-LCPI]. JST R6 has 18 advanced economies from 1870, but it is CC BY-NC-SA 4.0: use it to check, not to ship [JST]. Clio Infra's inflation series (from Reinhart and Rogoff and others) is the broadest, but it is xlsx only and its licence is not stated on its page; EP1 counts it and checks the licence [CLIO].

**Gap rule (rec.).** Chain the world file's CPI (2015 = 100) back with inflation rates: WDI, then BIS, then Clio Infra if its licence allows. Before a nation's first CPI value, its prices follow its anchor (est.):

- A money on gold, or pegged, follows its anchor's inflation (the United States for the dollar area, Britain for the sterling area, France for the franc area).
- A floating money follows its anchor's inflation plus the change in its rate to the anchor (relative PPP).
- A nation that used another's money takes that money's country's CPI.

### 3.5 The eight moneys

**The euro's ancestor (rec.): the German mark line, divided by 1.95583.** The DM was the anchor of the European Monetary System. OECD and PWT already publish Germany's rate as DM / 1.95583. The line joins the euro without a step: 0.900 in 1998, 0.939 in 1999. The franc line has more published years (NBER monthly 1876-1914 and 1918-1939), but it was devalued many times and redenominated 100 to 1 in 1960. An ECU basket exists only from 1979. The mark line has three gaps: the paper mark 1914-1923 (hyperinflation; 1 Reichsmark = 10^12 paper marks), 1933-1934, and 1939-1949. The Reichsmark is counted 1 to 1 with the DM, as the 1948 reform converted wages, prices and rents 1 to 1.

**The regimes, by date** (sources in `moneys-eight-regimes.csv`):

| Date | Event |
|---|---|
| 1900 | US$ on gold at $20.67 an ounce (Gold Standard Act, 14 Mar 1900). The pound (US$4.8665), the mark (4.1979 per US$), the Swiss franc (5.1826), the yen (2.0062, gold since 1897) and the Canadian dollar (at par) all at gold pars. Australia uses British coin. China on silver: the Haikwan tael floats against gold. |
| 1910 | Australian notes, at par with sterling. |
| Aug 1914 | War: the pound, the mark, the Swiss franc and the C$ leave gold in practice. |
| 1916-1919 | The pound held near US$4.77 (BoE monthly). |
| Sep 1917 | Japan embargoes gold exports (month to check). |
| Mar 1919 | The pound floats; US$3.38 in Feb 1920. |
| 1922-1923 | German hyperinflation; stabilised Nov 1923 at 4.2 x 10^12 paper marks per US$; the Reichsmark at the old gold par from Aug 1924. |
| Apr 1925 | The pound back on gold at US$4.8665. The Swiss franc on gold at its old par from 1925. |
| Jul 1926 | The C$ back on gold; off again in effect from 1929. |
| Jan 1930 | Japan back on gold at the old par. |
| Jul 1931 | German banking crisis; exchange control; the Reichsmark keeps its official par. |
| 21 Sep 1931 | The pound leaves gold. The C$ follows (gold exports banned 31 Oct 1931). |
| Dec 1931 | Japan leaves gold (13 Dec); the yen falls 60% to Nov 1932. Australia pegs at 125 Australian pounds to 100 sterling. |
| Apr 1933 | The US$ leaves gold. The yen in effect pegged to sterling. |
| 31 Jan 1934 | The US$ at $35 an ounce. Gold-bloc moneys keep their gold: the Swiss franc becomes 3.061 per US$, the Reichsmark officially 2.479. |
| Nov 1935 | China leaves silver: the fabi, a managed note. |
| 27 Sep 1936 | The Swiss franc devalued about 30%. |
| Sep 1939 | War: the pound at US$4.03; the C$ at 1.10 per US$ (15 Sep); the yen to the US$ (Oct). |
| Jul 1946 | The C$ back at par with the US$. |
| Dec 1946 | IMF par values: the pound US$4.03, the C$ at par. |
| Jun 1948 | The Deutsche Mark. Aug 1948: China's gold yuan, which soon collapses; Dec 1948: the People's Bank of China and the renminbi. |
| Apr 1949 | The yen at 360 per US$. |
| Sep 1949 | The pound devalued 30.5% to US$2.80; the Australian pound to US$2.24; the C$ to 1.10; the DM to 4.20. |
| Sep 1950 | The C$ floats. |
| Mar 1955 | The new renminbi (10,000 old to 1) at 2.4618 per US$. |
| Mar 1961 | The DM revalued 5% to 4.00. |
| May 1962 | The C$ pegged at 92.5 US cents (1.0811). |
| Feb 1966 | The decimal A$: 2 per Australian pound (day to check). |
| Nov 1967 | The pound devalued 14.3% to US$2.40; the A$ keeps its US$ rate. |
| Oct 1969 | The DM revalued to 3.66. |
| May 1970 | The C$ floats again (from June). |
| May 1971 | The DM floats; the Swiss franc revalued 7% to 4.084. |
| 15 Aug 1971 | The US gold window closes; the yen, pound and Swiss franc float for four months. |
| 18 Dec 1971 | Smithsonian: gold $38; central rates yen 308, DM 3.2225, Swiss franc 3.84, pound US$2.6057; the C$ keeps floating. The A$ moves from sterling to the US$. |
| 23 Jun 1972 | The pound floats. |
| 23 Jan 1973 | The Swiss franc floats. |
| 12 Feb 1973 | The US$ devalued 10%: gold $42.22. The yen floats (14 Feb). |
| Mar 1973 | General float; the DM in the European joint float. |
| Sep 1974 | The A$ devalued 12% and fixed to a trade-weighted basket; Nov 1976 devalued 17.5%, then a crawling peg. |
| Mar 1979 | The European Monetary System. |
| 1981-1984 | China's dual rates. |
| 12 Dec 1983 | The A$ floats. |
| Oct 1990-Sep 1992 | The pound in the ERM. |
| 1 Jan 1994 | China unifies its rates (H.10 1994 average 8.64); near 8.28 from 1997. |
| 1 Jan 1999 | The euro, at 1.95583 DM. |

**One line each.**

- **US$:** gold at $20.67 to 1933, $35 from 1934, the anchor of Bretton Woods to 1971, $38 then $42.22, floating from March 1973.
- **Euro (mark line):** gold mark to 1914, the paper mark's collapse to 1923, the Reichsmark at its gold par under exchange control, the DM from 1948 at 4.20, revalued 1961, 1969 and 1971, floating from 1973, the euro from 1999.
- **Yen:** gold at 2.006 to 1917 and again in 1930-31, floating and sterling-linked in the 1930s, 360 from 1949 to 1971, 308 at the Smithsonian, floating from February 1973.
- **Pound:** gold at US$4.8665 to 1914 and 1925-31, US$4.03 from 1939, 2.80 from 1949, 2.40 from 1967, floating from June 1972.
- **Yuan:** silver to 1935, the fabi and the gold yuan, the renminbi from 1948, 2.4618 from 1955, a managed basket in the 1970s, dual rates in the 1980s, unified at about 8.6 in 1994.
- **C$:** gold at par to 1914 and 1926-29, 1.10 under exchange control 1939-46, par 1946-49, 1.10 1949-50, floating 1950-62, 1.0811 1962-70, floating from 1970.
- **A$:** sterling to 1910, the Australian pound at par with sterling to 1929, 125 to 100 sterling from 1931, the A$ from 1966, the US$ from 1971, a basket from 1974, a crawl from 1976, floating from 1983.
- **Swiss franc:** the Latin Union franc on gold at 5.18 to 1914 and from 1925, 3.06 after the dollar's devaluation, devalued 1936, about 4.37 to 1971, 4.084 then 3.84 in 1971, floating from January 1973.

**The annual table.** `moneys-eight-annual.csv` gives the year's average where a source publishes it, and the par where a par held all year. Filled years of 101: GBP 101, AUD 99, EUR proxy 78, CAD 77, CHF 73, JPY 69, CNY 49. Sources by period:

| Years | Source | Mark |
|---|---|---|
| 1900-1956 | BoE millennium dataset, US$ per pound [BOE-M] | pub |
| 1900-1913 | NBER m14002, marks per 100 US$ [NBER-M14] | pub |
| pre-1950 | gold pars and official rates held all year | par |
| 1932-1949 | AUD from the pound and the 125/100 peg | der |
| 1910-1929; 1946, 1949 | AUD at par with sterling; CAD time-weighted | est |
| 1950-1956 | Penn World Table xr via FRED [PWT] | pub |
| 1957-1970 | OECD Main Economic Indicators via FRED [OECD-MEI] (EUR to 1998, CNY to 1980) | pub |
| 1971-2000 | Federal Reserve H.10 annual via FRED [H10-A] | pub |

One OECD figure is out of line: Australia 1957 at 0.599, against the US$2.24 par and against PWT and IMF (0.893). PWT is used for that year.

### 3.6 The other moneys

| From | Source | Nations |
|---|---|---|
| 1950 | IMF exchange rates, period average [IMF-ER] | 142 in 1950, 153 in 1960; 193 at some date |
| 1950 | Penn World Table xr [PWT] | 183 countries over 1950-2019 (count by year not made) |
| 1957 | OECD Main Economic Indicators [OECD-MEI] | OECD members and partners |
| 1960 | WDI official rate [WDI] | 154 in 1960 |
| 1914-1941 | Fed Banking and Monetary Statistics, Table 173 [FRB-BMS1] | about 40 moneys, monthly; PDF only |
| 1870 | JST R6 xrusd [JST] | 18; non-commercial |
| 1876, 1887 | NBER franc and mark [NBER-M14] | 2 |

Nothing open reaches 1900 for the other moneys. Most of today's nations had no money of their own then: they were colonies or used another's money. The IMF's ER has colonies from 1950 at their issuer's money. The IMF's terms allow free use with attribution, but commercial reuse needs its permission; PWT (CC BY 4.0) has the same years and is the one to ship.

**Gap rule (rec.).** Before a money's first published rate:

1. A nation that used another's money (uses:CODE) takes that money's rate.
2. A colonial or pegged money takes its anchor's rate times the par in force. EP1 takes each par from the IMF's par value tables or [FRB-BMS1], never from memory.
3. Between a peg's dates the par holds; a break moves to the new par on the date.
4. A floating money with no source: the anchor's rate times the ratio of the two CPIs, from the last sourced year (relative PPP, est.).
5. Before 1950, the regime and anchor come from [IRR]'s chronologies (from 1940/1946) or the colonial ruler.

### 3.7 What is missing

- The eight's market rates in the floating years before 1950: CAD 1914-1926 and 1929-1939; JPY 1917-1929 and 1931-1949; CHF 1914-1925, 1933-1934 and 1936-1949; the mark 1914-1923, 1933-1934 and 1939-1949; AUD 1930-1931. All are in [FRB-BMS1] Table 173 (public domain, PDF, Part I p. 662). FRASER's full-text view stops at p. 194, so EP1 keys the figures from the PDF and checks them against JST R6 xrusd.
- China 1900-1951: the Haikwan tael's US$ value (US Treasury proclamations; Chinese Maritime Customs returns, compiled by [HSIAO], a copyright book), Shanghai rates 1914-1941 in [FRB-BMS1]. No open file.
- Dates still to check: the Japanese gold embargo (Sep 1917), the yen's 1933 sterling rate and 1939 US$ rate, the yen's single rate (25 Apr 1949), the DM's 1948 rate and its 1949 date, the Swiss return to gold in 1925, the A$'s 1966 day, China's 1981-84 internal rate (2.8).
- Counts not made: Maddison population, PWT by year, Clio Infra, IRR. Each is xlsx or dta only.

## 4. Historical from 1900: the policy rates and the goods' prices

Two parts: (A) the eight vault moneys' policy rates; (B) the 16 Pink Sheet series the world
file's goods follow. The data are in `docs/studies/1900-start/`: `policy-rates-eight.csv`, `policy-rates-banks.csv`,
`commodity-sources.csv` and `commodity-prices-nominal.csv`. Every file names its sources in its `#` lines.

### 4.1 The eight central banks' rates

XR4 reads the BIS's policy rates [BIS-CBPOL] for 2000-2025. The BIS series start between 1946 and 1999. Before them,
each money's own bank is used where it existed, a market rate stands in where it did not, and the cell is empty where
nothing was found. The table is the annual average of monthly values. Year-end values were tried and dropped: an
overnight rate sampled on the last day of December is noisy (the US in December 1974: 3.87% on the last day, 8.53% over
the month).

| Money | Bank and rate | BIS from | Before the BIS, in the table | Empty |
|---|---|---|---|---|
| US$ | none to Nov 1914 (stand-in: New York commercial paper); New York Fed discount rate from 16 Nov 1914; effective fed funds rate from Jul 1954; fed funds target from 19 Dec 1985 | 1954-07 | 1900-1914 S [NBER-CP]; 1915-1954 discount rate [NBER-NYFED]; 1955-1985 [FEDFUNDS] | none |
| euro (proxy: Germany) | Reichsbank discount rate 1876-1945; Bank deutscher Länder and Bundesbank 1948-1998 (discount rate to 1978, Lombard 1979-84, repo 1985-98); ECB from 5 Jan 1999 | DE 1948-07; XM 1999-01 | 1900-1938 [NBER-REICH] | 1939-1947 |
| yen | Bank of Japan official discount rate from Oct 1882; call rate guideline from Sep 1998 | 1946-01 | 1900-1945 [BOJ-IR01]; 1999-2000 call rate [BOJ-FM02] | none |
| pound | Bank of England Bank Rate from 1694 (MLR 1972, band 1 rate 1981, repo 1997, Bank Rate 2006) | 1946-01 | 1900-1945 [BOE-3C] | none |
| yuan | no central bank rate found before 1990; People's Bank of China from Dec 1948 (unverified); one-year lending rate 1996-2019, loan prime rate from 2019 | 1996-01 | 1990-1995 IMF "discount rate" [IMF-IFS] | 1900-1989 |
| Canadian $ | none before 1914; the Finance Act advance rate 1914-1935 (no yearly series); Bank of Canada Bank Rate from 11 Mar 1935; overnight target from Jun 1994 | 1960-07 | 1935-1960 [BOC-RATE] | 1900-1934 |
| Australian $ | Commonwealth Bank from 1911, no rate series; Reserve Bank from 1960; overnight cash rate 1976; cash rate target from Jan 1990 | 1976-04 | 1969-1976 S: IMF "discount rate", not defined [IMF-IFS] | 1900-1968 |
| Swiss franc | none before the SNB; SNB discount rate from 1907; Libor target range 2000; SNB policy rate from Jun 2019 | 1946-01 | 1907-1945 year-end [SNB-HTS4] | 1900-1906 |

**What reaches 1900:** the US$ (a stand-in to 1914), the pound, the yen and the euro's proxy. The franc starts in 1907,
the Canadian $ in 1935, the Australian $ in 1969 (a stand-in to 1976), the yuan in 1990. The checks: the BIS's yen
equals the Bank of Japan's discount rate in every month to Aug 1998; the BIS's pound and franc equal the older series in
1946.

**The euro's ancestor: Germany.** The ECB was built on the Bundesbank, the mark anchored the European Monetary System,
and Germany was the largest euro economy. Its series runs 1876-1938 and 1948-1998 on one bank's rate. Two warnings:
1923 averages 38.7% (90% in the hyperinflation's last months), so a real rate for 1922-1923 means nothing and EP1
should cap it; and 1939-1947 is empty. France is the fallback (BIS from 1945, unbroken;
not gathered tonight). The Bundesbank's own discount rate 1948-1998 is in `policy-rates-banks.csv` as an alternative
[BBK-DIS].

**The gap rule (recommended).** Each fill is marked "est." in EP1's data.
1. Fetch the named primary files first: SNB table 1.1a for Swiss discount rates 1838-1906 [SNB-HTS4] (xls); RBA
   Occasional Paper 8 tables 3.21-3.22 for Australian rates from 1949-50 [RBA-OP8] (xls); the Reichsbank's rate
   1939-1945 is in print only.
2. Otherwise a money takes its anchor's rate: the Canadian $ the US$'s (Canada's dollar was at par with the US$
   [POWELL]); the Australian $ the pound's (pegged to sterling to 1971, unverified); the franc 1900-1906 the euro proxy's; the euro
   proxy 1939-1947 holds 1938's rate; the yuan before 1990 the US$'s, or the yuan stays out of the vault before 1949.
3. The JST Macrohistory Database [JST-R6] covers seven of the eight countries (not China) with a short-term rate
   (`stir`) from 1870 (each country's first year not checked), but it is xlsx/dta only and CC BY-NC-SA 4.0. Non-commercial and share-alike: check against Civic Ledger's
   terms on itch.io before using it.

Monthly values exist for every cell but the franc 1907-1945 (the SNB's monthly table is xls), if EP1 wants months.

### 4.2 World commodity prices before the Pink Sheet

The world file follows 16 Pink Sheet series [WB-PINK], 2000-2025. In the local annual file, 13 start in 1960, Australian
coal in 1970, fish meal and plywood in 1979. Before that, two kinds of series exist. World series built by economists
(Grilli and Yang [GY], from 1900; Jacks [JACKS], from 1850) are xlsx or login downloads and were not fetched. US
series (NBER Macrohistory [NBER-PRICES] and BLS producer prices [BLS-PPI] on FRED, EIA's crude price [EIA-CRUDE]) are
CSV, and their yearly nominal values are in `commodity-prices-nominal.csv`. EP1 deflates them by the US CPI and
splices each to its Pink Sheet series by the ratio in the first common year.

| Pink series | Pink from | US CSV chain (years) | Long world series to key | Recommended before the Pink Sheet |
|---|---|---|---|---|
| Maize | 1960 | Chicago corn 1900-1951; BLS grains 1926- | GY maize | the CSV chain (the US set the price) |
| Wheat, US HRW | 1960 | Chicago wheat 1900-1951; BLS wheat 1947- | GY | the CSV chain |
| Chicken | 1960 | NY fowl 1908-1940; BLS slaughter poultry 1946-, chickens 1947- | none | the chain; 1900-1907 and 1941-1945 follow the farm products index (est.) |
| Banana, US | 1960 | none | GY bananas 1900- | GY; else missing (hold 1960's real price, est.) |
| Beef | 1960 | Chicago cattle 1900-1940; BLS slaughter cattle 1947- | Jacks (GY 1900-1959) | Jacks; else the chain, 1941-1946 by the farm index (est.) |
| Fish meal | 1979 | BLS fish 1947-; farm products 1900-1966 | none | 1960-1978 Pink "Shrimps, Mexican"; 1947-1959 BLS fish; before, the farm index (est.) |
| Palm oil | 1960 | US crude cottonseed oil 1910-1940 | Jacks (GY 1900-1959) | Jacks; the US oil is a weak stand-in |
| Sugar, world | 1960 | NY granulated 1900-1955; BLS sugar 1926- | Jacks (GY 1900-1959) | Jacks; US sugar sat behind tariffs and quotas |
| Iron ore | 1960 | Mesabi ore 1900-1940; BLS iron ore 1947- | USGS DS140 unit value 1900- | DS140 (public domain); else the chain, 1941-1946 held (est.) |
| MUV Index | 1960 | BLS wholesale prices other than farm and food 1900-1970; BLS industrial commodities 1913- | GY's MUV 1900- | the CSV chain (one series, overlaps the MUV 1960-1970) |
| Crude oil | 1960 | EIA first purchase price 1900-2000 | (EIA from 1859) | EIA |
| Coal, Australian | 1970 | NBER coal at mines 1906-1930; BLS bituminous coal 1926-; fuel and lighting 1900-1951 | Jacks (US coal) | BLS coal to 1969; 1900-1905 the fuel index (est.) |
| Copper | 1960 | NY Lake 1900-1912, electrolytic 1911-1954; BLS copper mill shapes 1954- | USGS DS140; GY | DS140; else the chain |
| Logs, Malaysian | 1960 | NY white oak 1900-1923; lumber index 1913-1951; BLS lumber 1926- | GY timber | the chain (hardwood, then lumber) |
| Sawnwood, Malaysian | 1960 | as Logs | GY timber | as Logs |
| Plywood | 1979 | BLS plywood 1947- | none | 1960-1978 Pink "Sawnwood, Malaysian"; 1947-1959 BLS plywood; before, lumber |

**What reaches 1900 in the CSV:** maize, wheat, beef (to 1940), sugar, iron ore (to 1940), the MUV's proxy, crude oil,
copper, logs and sawnwood (oak, then lumber), coal (by the fuel index to 1905). **Later:** chicken 1908, palm oil's
stand-in 1910, fish 1947, plywood 1947. **None:** bananas (GY only).

**Read with care.** These are US prices. Sugar, coal and iron ore were domestic or protected markets and differ from
world prices. Prices were fixed in the war years (1942-1946), so some series stop and the PPI's poultry has months
missing. Jacks's real series mix in Global Financial Data, a paid database, for corn and petroleum, among others
[JACKS-WP]: check his deposit's licence before shipping any of it. The NBER states no licence for its Macrohistory
series (`research-1900.md` §6.4 says the same); the BLS, EIA and USGS series are US public domain. USDA's farm prices
(Quick Stats) need an API key, and Statistics Canada's Historical Statistics of Canada (section K) is HTML and PDF;
neither was needed.

## 5. The nations that did not exist in 1900

The world file holds 2025's 196 nations. In 1900 most of them were not states. This section gives the rule first and the evidence after it (5.2 onward, from `1900-start/nations-1900.csv`).

### 5.1 The rule (rec.)

Three rules were weighed.

| rule | what a 1900 world holds | for | against |
|---|---|---|---|
| **(a) today's places under their 1900 ruler** | the 196 places on today's borders, each with its own people, output, making and using; a ruler by date sells its land and lends its money until it becomes a state | the data are on today's borders; it is the file's own territory rule, dated | the map's borders are not 1900's |
| (b) each nation held at its first year | the 196, each frozen at the year it became a state until that year | nothing to add to the file | 113 nations became states after 1945, so most of the world would sit frozen for 50 to 110 years, at the wrong size: Nigeria had 18.7 million people in 1900 and 45.1 million in 1960, India 280.7 million and 359.3 million in 1947 [OWID-POP] |
| (c) 1900's own states | about 55 states and their empires, on 1900's borders, merging and splitting by date | the true map | new borders for each decade (CShapes-style) and a list of nodes that changes; no open series gives a colony's output as part of its empire |

**The evidence for (a).**
- The series are on today's borders. OWID's people cover 195 of the 196 in 1900, each within today's borders; Maddison's output is on present borders where its authors could make it so (§3.3); the Factbook's dates are for today's 196.
- The world file already gives a territory's land and money to its sovereign (`spec-nations.md` §3.1: Greenland's ground is sold by Denmark, Puerto Rico's by the United States), and its `money_history` column already lists a nation's moneys by month (`tools/world/FORMAT.txt`). Rule (a) is that rule with a date on it.
- The market solve needs places that make and use goods, not states. India made and used goods in 1900 under British rule.

**The rule, in full (rec.):**
1. All 196 places exist from 1900 on today's borders, with their own paths (§3).
2. A new column, `sovereign_history` (`ISO3:yyyy-mm`, as `money_history`), gives each place's ruler by date, from `ruler_1900` and the Factbook's dates in `nations-1900.csv`. Until its own date a place's land is sold by its ruler, in its ruler's money.
3. A ruler that is not one of the 196 stands as its continuing nation: the Ottoman Empire as Turkey (the Correlates of War list counts them as one state from 1816), the Russian Empire as Russia, Qing China as China, Sweden-Norway as Sweden, the Korean Empire as South Korea for both Koreas until Japan's protectorate of 17 November 1905. Austria-Hungary has no continuing nation: Austria stands for it (rec., est.; Vienna was its capital).
4. Before its own money, a place uses its ruler's money (`uses:CODE`), or its own colonial money at its par to the ruler's where a source gives one (the Congo Free State franc at the Belgian franc [WP:Congolese_franc]; EP1 takes each par from [FRB-BMS1] or the IMF, never from memory). Which money the euro's members use before 1999 is ★6.
5. On the map and in the rankings a place that is not yet a state is named with its ruler ("India, British") and counted as its own place, as Hong Kong and Macau are today.

The data is `docs/studies/1900-start/nations-1900.csv`: one row for each of the 196 nations in `tools/world/moneys.tsv`, with `status_1900`, `ruler_1900`, `independent`, `money_1900` and `source`. The rest of this section is the evidence: how the rows were made, the counts and the hard cases. Every row has source keys. A field that no source gave is empty and counted below.

### 5.2 How the rows were made

- Sovereignty comes from two lists. The Correlates of War list [COW] says which states were members of the system on 1 January 1900. The Wikipedia list of sovereign states in the 1900s [WP1900] says which rulers and half-sovereign polities existed and who held the colonies.
- Dates of independence and the colonial power at independence come from the CIA Factbook [FB]. The CIA took the Factbook down in February 2026. The pages on cia.gov now show a sunset notice. The text was read from the public-domain mirror `factbook/factbook.json` (251 country files).
- COW has a size rule. It leaves out micro-states and some recognised small states. Costa Rica and Liberia appear only from 1920, Luxembourg from 1920, Nepal from 1920, Oman and Bhutan from 1971, Montenegro from 2006, Andorra, Monaco, Liechtenstein and San Marino from 1990-93. So the COW count is a floor. The second count below adds the states [WP1900] and [FB] show as sovereign.
- `ruler_1900` is the power that held sovereignty and foreign relations on 1 January 1900. A protectorate that kept its own ruler inside (Afghanistan, Kuwait, the Trucial States) names the protector. A state that was sovereign names itself. Where two or three powers held the land, the row lists them with + in order of their share of today's land. That order is my estimate. No source states it.
- The rows say "Russian Empire", "Ottoman Empire" and so on in the ruler field only where today's nation is the continuation (RUS (Russian Empire), CHN (Qing China), SWE (Sweden-Norway)). Austria-Hungary, the Ottoman Empire and the South African Republic have no nation of today as their continuation in the world file, so the row names the polity.
- The Correlates of War colonial history data set (ICOW) would give a colonizer for each state and year. It is a zip file. The project's rule allows only CSV and JSON downloads, so it was not used.

### 5.3 Counts

Status on 1 January 1900, by my reading of the sources:

| status_1900 | nations |
|---|---|
| sovereign | 55 |
| colony | 65 |
| part of another state | 37 |
| protectorate | 28 |
| disputed or no state | 7 (BIH CUB MRT SAU TCD VUT WSM) |
| self-governing colony | 3 (Australia, New Zealand, South Africa) |
| dominion | 1 (Canada) |
| total | 196 |

Sovereign states:

| count | what it is |
|---|---|
| 42 | in the COW list on 1 January 1900: 18 in the Americas, 15 in Europe, 7 in Asia (with Turkey), 2 in Africa. South Korea and North Korea are two nations but one COW state (Korea, 1887-1905). Serbia is COW's "Yugoslavia" code from 1878. |
| 13 | sovereign by [WP1900] and [FB] but not in COW: Andorra, Bulgaria (de facto), Bhutan, Costa Rica, Liberia, Liechtenstein, Luxembourg, Monaco, Montenegro, Nepal, Oman, San Marino, Tonga |
| 55 | total sovereign in 1900 |
| 141 | not sovereign in 1900 (154 of the 196 are not on the COW list) |

Three of the 13 are not clear-cut. [WP1900] calls Nepal a protectorate and Oman "de jure independent, de facto a British protectorate". It calls Bulgaria "de facto independent until 5 October 1908". [FB] says Nepal "retained its independence" after 1816. The rows keep them sovereign and say so in the status text.

Who ruled the 141 nations that were not sovereign (by the first named ruler; the second column also counts the nations where a power held part of the land):

| ruler on 1 January 1900 | nations (first ruler) | nations (any share) | which |
|---|---|---|---|
| Britain | 55 | 58 | AFG ARE ATG AUS BGD BHR BHS BLZ BRB BRN BWA CAN CYP DMA EGY FJI GHA GMB GRD GUY HKG IND IRL JAM KEN KIR KNA KWT LCA LKA LSO MDV MLT MMR MUS MWI MYS NGA NZL PAK SDN SGP SLB SLE SSD SYC TTO TUV UGA VCT VUT YEM ZAF ZMB ZWE; plus Somalia, Tanzania (Zanzibar) and Papua New Guinea in part |
| France | 20 | 21 | BEN BFA CAF CIV COG COM DJI DZA GAB GIN KHM LAO MDG MLI MRT NER SEN TCD TUN VNM; plus Vanuatu in part |
| Russia (Russian Empire) | 15 | 16 | ARM AZE BLR EST FIN GEO KAZ KGZ LTU LVA MDA TJK TKM UKR UZB; plus Poland in part |
| Germany | 13 | 13 | BDI CMR FSM MHL NAM NRU PLW PNG POL RWA TGO TZA WSM |
| Ottoman Empire | 10 | 11 | ALB IRQ ISR JOR LBN LBY MKD QAT SAU SYR; plus Yemen in part |
| Portugal | 7 | 7 | AGO CPV GNB MAC MOZ STP TLS |
| Austria-Hungary | 7 | 9 | AUT BIH CZE HRV HUN SVK SVN; plus Poland and western Ukraine in part |
| United States | 2 | 2 | CUB PHL |
| Italy | 2 | 2 | ERI SOM |
| Netherlands | 2 | 2 | IDN SUR |
| Belgium (Leopold II personally) | 1 | 1 | COD |
| Spain | 1 | 1 | GNQ |
| Japan | 1 | 1 | TWN |
| Qing China | 1 | 1 | MNG |
| Sweden-Norway | 1 | 1 | NOR |
| Denmark | 1 | 1 | ISL |
| Colombia | 1 | 1 | PAN |
| South African Republic (Transvaal) | 1 | 2 | SWZ; plus South Africa in part |
| total | 141 | | |

Five rows have two or three rulers: Papua New Guinea (Germany, Britain), Poland (Germany, Russia, Austria-Hungary), Somalia (Italy, Britain), Vanuatu (Britain, France; a joint naval commission) and Yemen (Britain, Ottoman Empire).

Nations that were parts of a state that later split or ended:

| state | nations of the 196 | count |
|---|---|---|
| Austria-Hungary (ended 1918) | AUT BIH CZE HRV HUN SVK SVN; Poland and western Ukraine in part | 7 |
| Russian Empire (fell 1917, USSR 1922-91) | ARM AZE BLR EST FIN GEO KAZ KGZ LTU LVA MDA TJK TKM UKR UZB; Poland in part | 15 |
| Ottoman Empire (ended 1922) | ALB IRQ ISR JOR LBN LBY MKD QAT SAU SYR; Yemen in part | 10 |
| Sweden-Norway (ended 1905) | NOR | 1 |
| Korean Empire (sovereign in 1900) | KOR PRK, one state that became two nations | 2 |
| total | | 35 |

Russia, Turkey and Germany continue as states, and their rows name themselves. Poland and Yemen are not in the total, because their first-named ruler (Germany, Britain) is a continuing state. Ukraine is counted in the Russian row.

When a nation became a state, by the year [FB] gives. The 55 nations that were sovereign in 1900 are left out of this table:

| era | nations | which |
|---|---|---|
| before 1900 | 1 | CAN (1867; a dominion, not sovereign) |
| 1900-1918 | 16 | AUS CUB PAN NOR NZL ZAF MNG ALB FIN AUT EST HUN ISL LTU LVA POL |
| 1919-1945 | 8 | AFG IRL EGY IRQ SAU LBN IDN VNM |
| 1946-1970 | 60 | JOR PHL SYR IND PAK ISR LKA MMR LAO LBY KHM SDN TUN GHA MYS GIN and 44 more, up to FJI (1970) |
| after 1970 | 53 | ARE BGD BHR QAT BHS GNB GRD AGO COM CPV MOZ PNG STP SUR TLS SYC DJI DMA SLB TUV KIR LCA VCT VUT ZWE ATG BLZ KNA BRN FSM MHL NAM YEM ARM AZE BLR GEO HRV KAZ KGZ MDA MKD SVN TJK TKM UKR UZB BIH CZE ERI SVK PLW SSD |
| no independence | 3 | HKG MAC TWN |
| total | 141 | |

So 113 of the 196 became states after 1945 (60 in 1946-1970 and 53 after 1970). Two of the 1919-1945 rows are declarations that were recognised later: Indonesia declared 1945-08-17 and was recognised 1949-12-27, Vietnam declared 1945-09-02 and COW lists it from 1954. Counted by recognition, the figure is 115. Another 25 states (the 8, the 16 and Canada) came before 1946. Korea is in the 55: it was sovereign in 1900, lost its state in 1905-1910, and returned in 1945-48.

### 5.4 What the two rules would give

- The rule "later territory under its 1900 ruler" would put the land of 55 nations under Britain, 20 under France, 15 under the Russian Empire, 13 under Germany and 10 under the Ottoman Empire. Seven would go to Austria-Hungary, which has no one nation of today to hold it. The Korean Empire's land is one state in 1900 and two nations today.
- The ruler's name and the nation of the world file differ for the Russian Empire (Russia), the Ottoman Empire (Turkey), Qing China (China), Sweden-Norway (Sweden), the Congo Free State (Leopold II, whose state Belgium took over in 1908) and the Transvaal (South Africa). In each the continuing nation is clear. It is not clear for Austria-Hungary.
- The rule "the nation held at its first year" has a first year for 138 of the 141 nations that were not sovereign in 1900. The years run from 1867 to 2011, and 113 of them fall after 1945. Hong Kong, Macau and Taiwan have no year in [FB].

### 5.5 Hard cases

- **Hong Kong.** A British Crown colony: seized 1841, ceded 1842, the New Territories leased for 99 years in 1898 [FB]. Ruler GBR. Money: the silver dollar (Spanish and Mexican dollars, British trade dollars from 1895, bank-issued dollar notes) [WP:Hong_Kong_dollar]. The Factbook gives no independence: it became a special administrative region of China on 1 July 1997.
- **Macau.** A Portuguese colony [WP1900]; the Portuguese settled in the 1550s and were long under Chinese control [FB]. Ruler PRT. No money found. No independence: a special administrative region of China.
- **Taiwan.** Ceded by the Qing to Japan in 1895 and governed by Japan for 50 years [FB]. [WP1900] lists it as a Japanese dependency. Ruler JPN. Money: the yen, issued by the Bank of Taiwan [WP:Old_Taiwan_dollar]. The Factbook's independence field is empty. COW has "Taiwan" as a state from 1949-12-08.
- **Congo Free State.** "A state in personal union with the Kingdom of Belgium" until Belgium annexed it on 1908-11-15 [WP1900]. It was Leopold II's own possession, so the row says BEL (Leopold II personally). Money: the Congo Free State franc, introduced 1887 and equal to the Belgian franc [WP:Congolese_franc].
- **Korea.** The Korean Empire was sovereign in 1900 and is on the COW list (1887 to 1905-11-17). It became a Japanese protectorate on 1905-11-17 [WP1900]. It is one state in 1900 and two nations in the world file. Both rows say "Korean Empire (KOR and PRK)". Money: the yang (1892-1902) [WP:Korean_yang].
- **The Boer republics.** The Transvaal and the Orange Free State were "widely recognized" independent states in 1900 and were at war with Britain from 1899-10-11. Britain annexed both on 1902-05-31 [WP1900]. The Cape Colony and Natal were British colonies. South Africa's row says "self-governing colony" with GBR as ruler, and names the republics in the status text. I did not sum how much of today's land each held.
- **Ethiopia.** Sovereign and on the COW list from 1898. Eritrea was Italian (row ERI). Money: the Maria Theresa thaler; Menelik's own coins circulated only in Addis Ababa [WP:Ethiopian_birr].
- **Afghanistan.** [WP1900] calls it a protectorate under the Treaty of Gandamak; COW lists it only from 1919-08-08, and [FB] gives 1919-08-19 "from UK control over Afghan foreign affairs". The Emir ruled inside. The row names GBR. Money: the Afghan rupee, 1891-1923 [WP:Afghan_rupee].
- **Nepal.** COW lists it from 1920 and [WP1900] says protectorate. [FB] says it kept its independence after 1814-16. The row says sovereign (de facto). Money: the Nepalese rupee, or mohar [WP:Nepalese_rupee].
- **Bhutan.** [WP1900]: a widely recognised state. [FB]: a subsidy under the 1865 Treaty of Sinchulu and a unified hereditary kingdom in 1907. COW lists it only from 1971. The row says sovereign (de facto). No money found.
- **The Gulf.** Kuwait gave Britain foreign affairs in 1899 [FB]. Bahrain and the Trucial States (the UAE) had 19th-century treaties with Britain [FB]. Oman was "de jure independent, de facto a British protectorate" [WP1900]. Qatar is listed under Ottoman autonomous territories [WP1900] and became a British protectorate later [FB]. The rows name GBR for Kuwait, Bahrain and the UAE, the Ottoman Empire for Qatar, and Oman itself. Only Oman has a money: the Indian rupee on the coast and the Maria Theresa thaler inland [WP:Omani_rial].
- **The Ottoman lands.** Ten nations were Ottoman provinces: Albania (Janina, Scutari, Monastir), North Macedonia (Monastir, Kosovo), Iraq (Baghdad, Basra, Mosul), Syria, Lebanon (the autonomous Mount Lebanon), Jordan, Israel (the Jerusalem sanjak), Libya (Tripoli, Benghazi), Saudi Arabia (the Hejaz, and Najd under the Rashidi emirate of Ha'il, which worked with the Ottomans; the Al Saud returned to Riyadh in 1902) and Qatar [WP:Vilayets_of_the_Ottoman_Empire]. Yemen was half Ottoman (the north) and half British (Aden and its protectorates). Four more had an Ottoman suzerain on paper: Egypt (a "de jure autonomous vassal under de facto British occupation" [WP1900], row GBR), Bulgaria (an autonomous principality until 1908), Bosnia (occupied and administered by Austria-Hungary from 1878, annexed 1908) and Cyprus (British administration since 1878). The Ottoman gold lira was on the gold standard from 1881 [WP:Ottoman_lira].
- **Austria-Hungary's successors.** Austria, Hungary, Czechia, Slovakia, Slovenia, Croatia and Bosnia were its lands. Hungary had its own government inside the Dual Monarchy (Transleithania). Poland and western Ukraine (Galicia) had parts. The krone was the only legal note from 1900 [WP:Austro-Hungarian_krone]. Romania, Serbia and Italy gained A-H land later; their rows name themselves, because they were sovereign in 1900.
- **Norway and Sweden.** Norway was in personal union with Sweden until 1905-06-07 [WP1900, FB], with its own constitution and parliament. COW lists Sweden from 1816 and Norway from 1905. The row says SWE (Sweden-Norway). Both used the krone of the Scandinavian Monetary Union (1873, Norway 1875) [WP:Scandinavian_Monetary_Union].
- **Finland and the Baltics.** Finland was an autonomous grand duchy of Russia [WP1900, FB] with its own money, the markka [WD]. Estonia, Latvia and Lithuania were under Russian rule [FB]. All four rows say RUS (Russian Empire).
- **Poland.** Divided among Russia, Prussia and Austria between 1772 and 1795 [FB]; the three partitions still stood in 1900. Its land in 1900 was German, Russian and Austro-Hungarian, with three moneys. The row lists DEU first because the 1945 border brought in German land; that is my estimate and no source gives the shares.
- **The Pacific.** Fiji was a British colony with sterling [WP:Fijian_pound]. Tonga was sovereign until its protection treaty of 1900-05-18 [WP1900]. The Gilbert and Ellice Islands (Kiribati, Tuvalu) were a British protectorate [WP1900]. The Solomons were British, with the north German until 1899. Vanuatu had a joint British-French naval commission, and the Condominium began in 1906 [WP1900]. German Samoa began 1900-03-01 [WP1900], after a partition agreed in December 1899. Micronesia and Palau (the Caroline Islands) passed from Spain to Germany in 1899, and the Marshalls and Nauru were German. Papua New Guinea was German in the north and British in the south-east. Only Fiji has a money found.
- **Other rows that need a call.** Cuba was a US territory until 1902-05-20 [WP1900]; Spain gave up sovereignty on 1898-12-10 [FB]. Panama was part of Colombia until 1903-11-03 [FB]. Iceland was a Danish dependency [WP1900]. Ireland was part of the United Kingdom. Mauritania was Moorish emirates (Trarza, Brakna and Tagant are listed as states in [WP1900]) under a French claim; the French protectorate began in 1903 [WP1900]. Chad had the independent Wadai [WP1900], Rabih's state and a French protectorate over Baguirmi; the French colony began 1900-09-05 [WP1900]. Swaziland: [WP1900] calls it an independent state until 1902 and a protectorate from 1903; the row follows the 1894 convention with the Transvaal. Somalia: British north, Italian south [WP1900].

### 5.6 Empty fields

The `status_1900`, `ruler_1900`, `independent` and `source` fields are filled for all 196. The `money_1900` field is empty for 41 nations: AND ARE ATG BHR BLZ BRN BTN CMR CUB FSM GNQ ISL KIR KWT LAO MAC MDV MHL MNE MNG MRT MWI NAM NRU PLW PNG QAT SAU SLB SOM SWZ TCD TGO TLS TON TUV UGA VUT WSM YEM ZMB. For these the fetched articles gave no 1900 figure. The Gulf is the largest gap (the Indian rupee and the Maria Theresa thaler were in use, but no fetched page says so for 1900).

A money entry is the article's statement for the time, not always for the year. Where an article gave only a span (the Portuguese real until 1911, the Ottoman lira from 1881), 1900 falls inside it. For the French colonies the entry is the French franc, from the article on the Latin Monetary Union, which says the French colonies came under it in 1865. That is a general statement, not a check of each colony.

Details in the status text that were taken from standard histories and not read in any fetched page: the treaty years of the Gulf states (1820, 1861, 1892); Kuwait's 1899-01-23 and Sudan's 1899-01-19 agreement dates; British protection of Brunei (1888), the Maldives (1887), Kiribati and Tuvalu (1892), the Solomons (1893), Gambia (1894), Sierra Leone (1896), Bechuanaland (1885) and Basutoland (1884); French rule in Cambodia (1863), Laos (1893), Tunisia (1881) and Madagascar (1896); the Congo Free State's date of founding; Luxembourg's 1890 end of its union with the Netherlands; the Portuguese title to Macau (1887); Korea's annexation (1910); Qatar's 1916 treaty; German rule in Rwanda and Burundi; Iceland's 1874 constitution; the Caroline Islands sale and German rule in the Marshalls and Nauru; Samoa's 1889 Berlin Act and December 1899 partition; British New Guinea; the northern Solomons; Vanuatu's joint commission of 1887; Rabih's state, Baguirmi and Adrar; the 1894 convention over Swaziland; Serbian and Italian land that came from Austria-Hungary; Serbia's 1882 kingdom; and the Philippine-American war years. These are well known, and each can be checked in the sources named for the row before EP1 relies on it.

## 6. The calendar

**Summary.** Outside `CityCalendar`, three places read `EPOCH_YEAR` (ChartModel:420, Nations:69, and CityCalendar's own `yearOf`). About 145 mentions of `CityCalendar.` in 21 files read the date, and all of them MOVE: they print or bin a year and follow the constant. The one real BREAK is the world file. `Nations.FIRST_YEAR` follows the epoch (1900) while the file's paths, prices and monthly rates begin in 2000, so `new Nations(...)` throws at Nations:262 and `Game.getNations()` swallows it (Game:1168-1169): a 1900 city would play silently on the goods' literals, with no world market and no map dots. Four saved fields hold a calendar year, not a month index. No model mechanic is gated on a calendar year; the only year gates are the world file's (history to 2025, UN people to 2100). The save needs `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` raised together (48 to 49); §6.3 says why.

Two of the files a first grep lists do not read the calendar's year. `GovernmentScreen.yearOf` is its own method (a month figure times 12), not the calendar's. `MapView` reads `Nations.yearOf`, which is `CityCalendar.yearOf`. Both still MOVE.

### 6.1 Readers of the epoch and the year constants

| file:line | reads | what a 1900 epoch does | class |
|---|---|---|---|
| CityCalendar.java:28, 100 | `EPOCH_YEAR`, in `yearOf` | month 1 is January 1900; month 121 is January 1910 | MOVES |
| ChartModel.java:420 | `1 + 12 * (y - EPOCH_YEAR)`, month of a year tick | follows; years before the epoch give m < 1 and are skipped (:421) | MOVES |
| Nations.java:68-69 | `FIRST_YEAR = EPOCH_YEAR` | the path begins 1900; the file's begins 2000 | BREAKS |
| Nations.java:262-263 | guard: `pathFirstYear() != FIRST_YEAR` or `pathYears() != 2100 - FIRST_YEAR + 1` | throws on the 2000 file (101 years, not 201) | BREAKS |
| Nations.java:292-294 | `file.moneyAt(i + 1, t * 12 + 11)`, t counted from `Nations.FIRST_YEAR`, the month counted from `WorldData.FIRST_YEAR` | the two zeros must be the same year | BREAKS |
| Nations.java:299, 301, 500-501 | `file.pathValue(series, nation, year)` for `FIRST_YEAR + i` | throws "no paths for year" below the file's first (WorldData:823) | BREAKS |
| Nations.java:342, 354-355 | `priceFirstYear() != FIRST_YEAR` guard; `file.price(g, FIRST_YEAR + k)` | throws below the price layer's first year (WorldData:1064) | BREAKS |
| Nations.java:265, 383, 576-577, 680 | `HISTORY_LAST_YEAR - FIRST_YEAR`, `ensure(PEOPLE_LAST_YEAR)`, `step(t)`, `moneyOf` | follow; the constructor builds every year to 2100 at once: 201 years, not 101 (twice the work, on the WORKER thread, MapView:3926) | MOVES |
| Nations.java:442 | `yearOf(gameMonth)` = `CityCalendar.yearOf` | the nation's year is the city's calendar year | MOVES |
| WorldData.java:110 | `FIRST_YEAR = 2000`, "the year month 0 is in" | separate constant; stays 2000 until the file is rebuilt | BREAKS |
| WorldData.java:558-566, 670 | `monthNumber`, `monthLabel`, money-history months parsed from `yyyy-mm` | all counted from 2000-01; a 1900 file needs 1512 months, not 312 | BREAKS |
| WorldData.java:823-824, 1064-1065 | `pathIndex`, `priceIndex` throw outside the file's years | the throw behind every row above | BREAKS |
| WorldMarket.java:1494 | `restore`: `year < Nations.FIRST_YEAR` | follows | MOVES |
| WorldMarket.java:88, 573-584 | `REFERENCE_YEAR = 2025`, the calibration city's year | a file year, not a calendar one; unchanged | MOVES |
| Game.java:1313, 1376 | `Nations.yearOf(month)` for `solveYear` | solves 1900 first; `growth()`/`level()` (WorldMarket:701-728) read Nations 1900 and the base year | MOVES, if the file has 1900 |
| PartnerTrade.java:229 | `Nations.yearOf(m)`, turns the trade-by-partner year | follows | MOVES |

`yearOf` readers outside `Nations`, all MOVES: DebtManager:1596-1640 (the ladder bins by calendar year), FundView:186, 379, 519, Game:16490 (`payTransfer(…, yearOf(month))`), HistorySave:892-894 (`foldOldYears`; the fold depends only on `monthOfYear`, so it folds the same), OilView:732, 782, YearBook:1244, 1538, ui/FinancesScreen:3020, 3905, 3938, ui/TimeChart:1349, ui/MapView:4238, 4351, 4403 (via `Nations.yearOf`). `monthOfYear` readers (Game:4047, 4090, ChartModel:413, SectorScreen:575, UserInterface:2815, FinancesScreen:1176) read the month only: any epoch in January gives the same result. `format`, `formatShort`, `formatDay`, `until` and the month names are 121 call sites in 16 files (FinancesScreen alone has 40 mentions), all printing.

### 6.2 Year literals in model and screen code

| file:line | literal | what a 1900 epoch does | class |
|---|---|---|---|
| Nations.java:72, 75 | `HISTORY_LAST_YEAR = 2025`, `PEOPLE_LAST_YEAR = 2100` | file years; stay. Real history ends month 1512 (Dec 2025), the UN people month 2412 (Dec 2100) | LITERAL (file's) |
| ui/FoundingScreen.java:885-892 | "The same January 2000", "from January 2000", "draws its future from 2026", "year by year to 2025" | wrong the day the epoch moves; build it from `Nations.FIRST_YEAR`, `HISTORY_LAST_YEAR` | LITERAL, BREAKS |
| NationsMap.java:824 | `"GDP, US$ of 2015"` | the file's price base, not the calendar | LITERAL, stays |
| BuildingManager.java (41 `setSince`), buildings.json (41 `"since"`: 30 at 1900, 1902, 1906, 1952, 3 at 1955, 1968, 1969, 1970, 1973, 1980) | the year a building first existed | read by nothing but BuildingDataCheck:192, 714-766 (BuildingsTemplate:549-567 says so). 1900 means "stood before the start" and now equals the epoch. Not wired; R0's | LITERAL, data |
| FinancesScreen.java:6188 | "1987-2024" (a sourced default rate) | prose; untouched | LITERAL |
| PolicyScreen.java:3521-3522, 3850, 3868, 4089; ConstructionScreen.java:272, 897-906; TaxPolicy.java:235, PolicyScreen.java:4378 ("the real 2026 figures") | sourced history and 2026 defaults in text | anachronism in a 1900 city; no mechanic. The 2026 defaults belong to the price-level study | LITERAL |
| ui/ChartFitBench.java:91 | `"March 2031 · running at 20x"` | a bench label, not shipped | LITERAL |
| FoundingScreen path words, Nations.java:42, 131-137 | "past 2100", "2091-2100" in comments and the `EASE_YEARS` text | file years; stay | LITERAL |

**Mechanics gated on a calendar year: none in the model.** No lever, notice, sector, tax or build list asks the year. A grep of 19xx/20xx/21xx in non-comment, non-harness code finds only the constants above, `setSince` data and sourced prose. The "2015 bridge dollar" of spec-leaderboards D1 is design only: nothing reads it, it is fixed at founding and not through the calendar (spec-leaderboards 69), and `world-2025.json` does not exist yet (LB1). What is year-gated is the world file's content: Historical replays the file to 2025 and then follows the game's rule (Nations:595, 579), people are the UN's to 2100 and held (Nations:592), and Random's draws are keyed by calendar year (`scramble(seed, nation, year, what)`, Nations:560-565), so a Random world meets different draws at the same month once the epoch moves: every trace moves.

### 6.3 The save's dates

The city's month is saved as an index: `DataSave.month` (DataSave.java:90, month 1 is January of the epoch). So are debt and bond maturities, issue months, notices, the history axis (`HistorySave`), build and demolition logs. An old save re-reads as the same position one century earlier: month 300 is December 2024 today and December 1924 under 1900. That part is cosmetic and consistent.

Four fields hold a calendar year instead:

| field (file:line) | what it holds | an epoch-2000 save under 1900 | class |
|---|---|---|---|
| DataSave.worldYear (:215, 1141-1149) | the year the world market last solved (2041, say) | `WorldMarket.restore` accepts it (:1494); next month `solvedYear() != Nations.yearOf(month)` (Game:1313) so it solves 1941 again from the 2041 state; the restore is wasted | BREAKS (self-heals) |
| PartnerTrade.State.year (:58, 118, 316-340) | the year to date's calendar year | next tally sees 1941 ≠ 2041, `finishYear()` (:260) sends a part-year table dated 2041 to the history, then starts 1941 | BREAKS |
| PartnerTrade.Table.year in HistorySave.tradeByPartner (HistorySave:516, 1204-1215) | finished years, "oldest first" | tables 2000...2040 then 1941...; order assumption broken; any year lookup by number reads wrong | BREAKS |
| TreasuryFund.transferYear (:335, 515-519, 978, 1024) | the year the transfer counters belong to | a mismatch resets "transfers this year" once, mid-year | BREAKS (small) |

**Does it need the bump? Yes.** `GameVersion.SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` are both 48 (GameVersion:8214, 8256); raise both to 49 and say so in the notes. Reasons: (1) four saved years above would read a century ahead of the clock; (2) a Historical city would stand in another real year's world: its saved prices, rates and market state belong to 2041 and now meet 1941; (3) the same batch rescales every nominal anchor to 1900's price level, so a saved city's dollars are 30 to 40 times too high for its world. (3) alone is enough under the house rule. `research-1900.md` row 633 says the same. Nothing short of a saved epoch would keep old cities honest, and `CityCalendar` is static and stateless by design, so no conversion code: refuse them.

### 6.4 Axes and labels that print or span a year

| file:line | what | 1900 epoch | class |
|---|---|---|---|
| ChartModel.java:396, 413-434 | time ticks: January labelled with the year, `YEAR_STEPS` up to 1000 | ticks step from multiples of the step, 1900 divides every step to 100; pre-epoch ticks skipped | MOVES |
| ui/TimeChart.java:788-789, 1349-1426 | span line, "the year N, kept whole", marks, bands | follows | MOVES |
| ui/HistoryScreen.java:1097-1132, 2152-2153, 2723-2729, 2869-2880, 2951-2960 | "since", bands, episodes, "five years either side" | follows | MOVES |
| ui/FinancesScreen.java (40 mentions), DebtManager:1596-1640 | maturities, ladder rungs by calendar year | follows; `LADDER_YEARS` is relative | MOVES |
| ui/UserInterface.java:2815, 2873, 6474, 6635; ui/SectorScreen.java:575 | sparkline January marks, the clock's date | follows | MOVES |
| YearBook.java:902-903, 1244, 1538 | episode names "Depression of 1930" | follows; names read oddly against sourced prose | MOVES |
| CityCalendar.java:66-74 | `isLeapYear`, `daysIn` | 1900 is not a leap year (by 100, not 400): February of the first year has 28 days, not 29 | MOVES |

Caps and spans: nothing caps a year. The 4,002-month playtest ends June 2233 (June 2333 from 2000). Nations extends lazily to any year (Nations:481-483); the file's history ends 2025, people are held after 2100; `MONTHLY_KEPT = 6,000` months (HistorySave:87) folds only past 500 years, so a 4,000-month run never folds. From 1900 the data-driven phase is longer: Historical's real history runs 1,512 months, the UN's people 2,412, so 38% of a 4,000-month run is replayed history and 60% has the UN's people. A 4-digit year is 4 digits in both epochs; no width changes.

### 6.5 Harnesses that assert a calendar year or a year literal

| harness:line | asserts | 1900 epoch |
|---|---|---|
| CalendarCheck:43-47, 67-70, 78-80, 86-87, 108, 111, 123, 132, 139-140 | "month 1 is January 2000", "month 121 is January 2010", "4002 is June 2333", Feb 2000 has 29 days, "February 2100 has 28" (month 1201+1), the clock prints "16 March 2000", month 0 floors at January 2000 | BREAKS, about 17 assertions; re-derive from `EPOCH_YEAR` (Feb 1900 has 28; month 1201+1 is Feb 2000, 29) |
| NationsCheck:297, 308, 461, 482-484, 585-586, 594-599, 622-624, 638 | loops from "2001", "its 2000 stand-in", "Random's 2000", China's 2000-2002 gap fill, Canada's and Belgium's 2000 rate stand-ins | BREAKS, about 12; the literal stands for `FIRST_YEAR`. The other ~58 (2025, 2030, 2100, 2150, 2300, "2001-2008") are real file years: they hold if the file spans them |
| NationsCheck, GlobeCheck, WorldMarketCheck, WorldDataCheck on the fixture | `new Nations(fixture)` | BREAKS until `tools/world/fixture.py` cuts a 1900 fixture (src/main/resources/world/world-1-fixture.bin and .expect); `PATH_YEARS = [2000, 2015, 2025, 2100]`, `RATE_MONTHS = [0, 100, 311]` |
| WorldMarketCheck:272-278, 405-417, 594-623, 957 | solves 2000-2040; "the founding solve (2000, every good, from nothing) takes under SOLVE_COLD_MS" (a timing check) | LITERAL; mechanically holds, but the premise is the epoch's founding. Re-premise to `FIRST_YEAR`: the 1900 cold solve is the real one and may be slower |
| WorldDataCheck:172, 175, 997-1030 | `monthNumber(2025, 12)`, "history starts at 2000-01", `pathFirstYear() == WorldData.FIRST_YEAR` | MOVES with the file; labels LITERAL |
| GlobeCheck:2463, 2508, 2536, 2844, 2862, 2886 | `YEAR = 2020`, cards at 2025 and 2030, the first year's growth is "-" | MOVES (constants); 2020/2025/2030 are file years |
| SaveFileCheck:3598-3599, 3616; ReadPathCheck:2128 | `solvedYear() == yearOf(month)`, first trade table's year == `Nations.FIRST_YEAR`; they build a city on a world file, so the file and the fixture must be 1900 too | MOVES |
| ChartCheck:440, 544; YearBookCheck:586-633; ForeignDebtCheck:802-865; OilViewCheck:631; FundCheck:756; AuctionCheck:1264 | read `CityCalendar.*` or `yearOf(n)` | MOVES, 6 harnesses, ~25 lines |
| FundCheck:956, 959 | `payTransfer(1_000_000, 2000)`, a year as an argument | LITERAL, harmless |
| BuildingDataCheck:192, 714-766; CementConcreteCheck:176; CoalMining/CopperMining/CopperRefining/ForestryCheck; MaterialsCheck:903 | `getSince()` equals 1900, 1955... | LITERAL data; 1900 is now the start year as the comments say |
| LongPlaytest, tools/ensemble | no calendar read; `score.py:218` `EXPECTED_END` is in months | MOVES, but the Random path and the world market move: every trace is re-baselined |

The other `*Check` files with 19xx/20xx numbers (MapCheck, TradeCostCheck, CreditCheck, TruckCheck...) are counts and sizes, not years.

### 6.6 Anything else dated by calendar year

| where | what | class |
|---|---|---|
| world file, `data/world-1.bin` (WorldData:70, tools/world/FORMAT.txt:22, 43) | paths 2000-2100, prices 2000-2025, rates 2000-01 to 2025-12; the asset of release `world-1`; `tools/world/common.py:45` `FIRST_YEAR = 2000`, `MONTHS` | BREAKS: rebuild from 1900, a new file name and release (`DATA_FILE`, `MAGIC`, the get-earth fetch) |
| research and build list (`since`) | dated rows, unread | LITERAL; R0 wires it |
| policy levers, inbox notices (UserInterface:6105-6107) | notices print `format(month)`; no lever asks a year | MOVES |
| leaderboard (spec-leaderboards, LB1-LB3) | "Real world 2025" fixed; "projected to the city's year" reads `Nations.yearOf`; "same year" is read as age in months | not built; MOVES if built on `Nations.yearOf` |
| WN4 (moneys), XR4 (policy rates), LB1-LB3, KP1 | queued ahead of EP1; each may add readers of `Nations.yearOf` or `FIRST_YEAR` | rescan before EP1 |
| ensemble, long playtest | month counts, not years | MOVES |
| docs/manual.md:2061 | "Month 1 is January 2000" (generated from the manual page) | change the page, publish, pull |

### 6.7 For EP1

1. Change `CityCalendar.EPOCH_YEAR` to 1900 and rewrite its javadoc ("Month 1 is January 2000", "1999").
2. Rebuild the world file from 1900: paths, prices and monthly rates (`tools/world/common.py:45`, `econ_paths_*.py`), `WorldData.FIRST_YEAR` to 1900 in the same batch, a new file name and release asset.
3. Define `Nations.FIRST_YEAR = WorldData.FIRST_YEAR` and let `Nations` take the file's first year, with a check that it equals `EPOCH_YEAR`, so a mismatch fails in one place and not as a silent fall to the literals (Game:1168-1169 should say so loudly).
4. Re-cut the fixture (`fixture.py`, `PATH_YEARS`, `RATE_MONTHS`) and its `.expect`; WorldDataCheck, NationsCheck, GlobeCheck, WorldMarketCheck depend on it.
5. Build `FoundingScreen` path words (885-892) from the constants.
6. Raise `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` to 49 together; four saved years (worldYear, PartnerTrade year and tables, transferYear) are the calendar's share of the reason.
7. Re-premise CalendarCheck (about 17 assertions) and NationsCheck's twelve FIRST_YEAR literals to the constants; WorldMarketCheck's founding solve to `FIRST_YEAR`; none of their premises moved.
8. Time the 1900 founding solve and the doubled `Nations` build (201 years at construction); the monthly rates layer is 4.8 times as long (1,512 months). The PC is short of memory.
9. Rescan for new readers after WN4, XR4, LB1-LB3 and KP1 land: `grep -rn "EPOCH_YEAR\|FIRST_YEAR\|Nations.yearOf\|yearOf("`.
10. Re-baseline every trace: Random's draws are keyed by calendar year, so the same month meets other draws.

## 7. The calls for EP1

Each call has a recommendation; EP1 takes the recommended way unless Jerus says otherwise.

1. **★1 The factor: the US CPI's, 0.02588 (1/38.6)** (§1). For every nominal anchor, the world's US$ prices and the city's D$ alike. The opening rate stays D$1.00 = US$1.00 (`ForeignAccounts.OPENING_RATE`), as gold held the two dollars at par in 1900. The alternatives are Canada's CPI, 0.02715 (1/36.8, 5% dearer), and the US GDP deflator (1/31.7). **Recommended:** the US CPI's, computed in code from the series the Historical US path lays (`1900-start/price-level-us.csv`), so that the factor times the path's 2025 level is exactly 1, and a harness asserts it.

2. **★2 How the factor reaches the anchors** (§2.7). (a) One compile-time constant through four funnels: `PayTier`, the `Good` constructor (with `consumption.json`'s food prices read from `Good` and the `MATERIALS_WORLD_PRICE` copy removed), `BuildingCatalog` and the seeding strike; plus about 35 named literals, each with a ratio assertion. A `PriceLevelCheck` compares twins: a city founded at the factor against one founded at 1 and reformed by its inverse (`Game.reformCurrencyForTest`). (b) Found at 1 and reform the money by the inverse at month 0: no literal is touched, but the rate opens at the factor, the world's US$ prices stay at 2025's, and the money is renamed as a second currency. **Recommended: (a).** (b) leaves the world's prices at today's level, which is not the call Jerus took.

3. **★3 The world's dollar in Historical: the US CPI from 1900.** WN4 makes the in-game US$ inflate on the US nation's path (D29). From 1900 that path is `price-level-us.csv`: prices doubled from 1913 to 1920, fell 11% in 1921 and 25% from 1929 to 1933 [BLS-CPI], then the 1940s and the 1970s. It is what brings the world's prices from 1900's level to the literals' 2025 level in 2025. Random draws its own from 1900. **Recommended:** yes, one series for the path and for the factor (★1).

4. **★4 The goods' real prices before 2000.** (a) Each good held at its real ratio of 2000 from 1900 to 1999, which joins the file's 2000-2025 path with no step; the 1900-2000 series gathered here (§4.2) wait for R0. (b) Replay them from 1900: §4.2's chains, deflated by the US CPI and spliced to the Pink Sheet in their first common year. **Recommended: (a).** The settled call is today's real economy at 1900's price level. 1900's real prices of food and materials go with 1900's tech and wages, which R0 brings ("the real side ... is R0's", EP1's brief). Under (b) today's farms and mines would sell at 1900's real prices with today's yields.

5. **★5 The world's people and output before 2000: replay them.** §3's chains give every nation a path from 1900. The world's real output in 1900 was about 1/36 of 2022's (est.: Maddison's world GDP a head, 2,265 against 16,677 in 2011 international dollars, times OWID's world people, 1,627 million against 8,021 million [OWID-MPD], [OWID-POP]) and about 1/17 of 2000's. Every nation's making and using scale with its output, so a 1900 world is a small market that grows 36-fold. The alternative is to hold quantities at 2000's until R0. **Recommended:** replay. The world's size is history, not tech, and it is what Jerus asked Historical to run ("i feel like that will let the historical run right?"). EP1's ensemble measures how soon a city of today's productivity becomes the market (`spec-nations.md` §4.8); with ★4 (a), prices and quantities part company before 2000 by design, until R0.

6. **★6 The moneys before 1999.** The world file's moneys are 2025's ISO codes. The euro did not exist before 1999, and most of today's moneys did not exist in 1900. (a) A place uses its ruler's money until it has its own (§5.1). The euro's members use their own legacy moneys (the franc, the lira, the guilder, the Belgian franc and the rest) at published rates, each converting at its fixed euro rate in 1999 [EU-2866/98]. The vault's euro before 1999 is the mark line divided by 1.95583 (§3.5). (b) Every euro member on the mark line from 1900. **Recommended: (a).** Under (b) alone, France would live through Germany's 1923. The data job: 1900-1913 is the gold pars; from 1950, PWT (CC BY 4.0) has every money; 1914-1949 is keyed from [FRB-BMS1] Table 173 for the eight and the euro's large members, and the rest take their anchor's rate times the par in force (est.).

7. **★7 The nations that did not exist: today's places under their 1900 ruler** (§5.1, rule (a)). A `sovereign_history` column; Turkey, Russia, China, Sweden and South Korea stand for the Ottoman, Russian and Qing empires, Sweden-Norway and the Korean Empire, and Austria for Austria-Hungary. Before EP1 bakes the column, it checks each `ruler_1900` against the Factbook and the Correlates of War list. Rows whose detail came from standard histories rather than a fetched page are listed in §5.6. **Recommended:** (a).

8. **★8 The policy rates before their banks** (§4.1). `policy-rates-eight.csv` as gathered; its empty cells filled by the anchor's rate, marked est.: the C$ takes the US$'s to 1934; the A$ the pound's to 1968; the Swiss franc the euro proxy's to 1906; the euro proxy holds 1938's through 1947; the yuan stays out of the vault before 1949, when it had no rate. Germany's 1922-1923 rates (38.7% on average in 1923) are hyperinflation's: EP1 caps the real rate it derives from them. **Recommended:** as listed. The city's own levers on history's dates (the gold years, the 1914 Advance Rate, the Bank of Canada in 1935) are research's (`research-1900.md` §7 and Q1), not EP1's: the D$ starts under XR2's default regime.

9. **★9 The world file: rebuilt from 1900 as a new file.** Its paths run 1900-2100 (201 years), its prices 1900-2025, its monthly rates 1900-01 to 2025-12 (1,512 months, about 1.7 MB for 145 moneys). It gets a new name and release (as earth-2 had), and the fixture is re-cut. `Nations.FIRST_YEAR` is read from the file and checked against `EPOCH_YEAR`, so a mismatch fails in one place, and `Game.getNations` says so loudly rather than playing on without nations (§6.1). The release is Jerus's. **Recommended:** yes; time the 1900 founding solve and the doubled `Nations` build on the PC, whose memory is short.

10. **★10 Old saves are refused.** Raise `SAVE_FORMAT` and `OLDEST_LOADABLE_FORMAT` together (48 on main today; one above whatever main has when EP1 runs). Each of three things alone would make an old save load wrongly: four saved calendar years (§6.3), a Historical city's world state from another century, and every dollar 38 times its new world's. **Recommended:** yes, by the house rule (old saves need not load until the game is published).

11. **★11 What may ship.** Ship, with credit: BLS, BEA, EIA, USGS and the Federal Reserve (US public domain), Statistics Canada (Open Licence), Maddison, OWID, PWT and OECD (CC BY 4.0), the Factbook (public domain), the Correlates of War list. Check the terms first: the BIS, the Bank of England, the Bank of Japan, the Bank of Canada, and the NBER Macrohistory series (no licence stated; `research-1900.md` Q17 already prefers USGS DS140 to NBER prices). Do not ship: the Macrohistory Database (CC BY-NC-SA 4.0, non-commercial), the IMF's data (commercial reuse by its permission; PWT has the same years), MeasuringWorth (all rights reserved) and Jacks's series (they mix in Global Financial Data, a paid database). **Recommended:** as listed; the three refused are used only to check.

12. **★12 Readers to rescan before EP1 starts.** Tonight's batches add readers: LB1-LB3's rankings and WN6's real US$ hold the leaderboard's bridge (P_US,2015 / P_US,founding = 1 at a 2000 founding; from 1900 it is 237.017 / 8.333 = 28.4 [BLS-CPI], [MINNFED]); WN4, XR4 and CD8 may read `Nations.yearOf`, the US path or a year. **Recommended:** EP1's first step greps `EPOCH_YEAR`, `FIRST_YEAR`, `Nations.yearOf` and `yearOf(` again (§6.7, item 9), and maps the bridge to the factor's series.

13. **★13 The screens at a 38th of today's prices.** LPG falls from US$0.23 to about US$0.006 a litre, and other unit prices below a cent; `Money.unitPrice` (`ui/Money.java:283`) needs a look. `FoundingScreen`'s dated words (885-892) are built from the constants. The manual's "Month 1 is January 2000" is changed on the page, published and pulled. **Recommended:** yes, checked by eye on Jerus's screens, the screen-check session's slots.

## 8. The files in `1900-start/`

Each CSV opens with `#` lines naming its sources with their URLs, its units and its rules, then one header row. Only extracted series are kept; no raw download is in the repository.

| file | rows | what |
|---|---|---|
| `price-level-us.csv` | 1900-2025 | the US CPI-U, 1982-84 = 100, marked pub or est (§1) |
| `price-level-ca.csv` | 1900-2025 | Canada's CPI, 2002 = 100, marked pub or est (§1) |
| `series-coverage.csv` | 23 sources | each source of people, output, prices and moneys: first year, nations covered in 1900, 1913, 1950 and 1960, licence, URL (§3) |
| `moneys-eight-regimes.csv` | 86 periods | each of the eight moneys' regimes 1900-2000: from, to, gold, float, peg or par, units a US$, the event, the source (§3.5) |
| `moneys-eight-annual.csv` | 1900-2000 | the eight's units a US$ a year, each cell marked pub, par, der or est, empty where nothing is sourced (§3.5) |
| `policy-rates-eight.csv` | 1900-2000 | the eight's policy rates, annual averages of months, % a year, each cell marked policy or stand-in, empty where none (§4.1) |
| `policy-rates-banks.csv` | 37 rows | who set each money's rate, and when (§4.1) |
| `commodity-sources.csv` | 58 rows | for the 16 Pink Sheet series, every long series found: id, years, units, licence, the proxy before it (§4.2) |
| `commodity-prices-nominal.csv` | 1900-2000 | 33 US nominal price series from FRED and the EIA, in their own units; EP1 deflates by `price-level-us.csv` (§4.2) |
| `nations-1900.csv` | 196 | each nation's status, ruler, year of independence and money on 1 January 1900 (§5) |

## Sources

Keys marked * were named, not read, in this study; EP1 checks them.

- Not used: the ICOW Colonial History Data Set v1.1 (Hensel and Mitchell), http://www.paulhensel.org/Data/colhist.zip . It is a zip, and Hensel asks that it is not redistributed.

- [BLS-CPI]: US Bureau of Labor Statistics, Consumer Price Index for All Urban Consumers (CPI-U), all items, U.S. city average, NSA, series CUUR0000SA0, monthly 1913-2025 via the public API v1, downloaded 2026-10-30. https://data.bls.gov/timeseries/CUUR0000SA0
- [BLS-FAQ]: US Bureau of Labor Statistics, "2025 Federal Government Shutdown Impact on the CPI: FAQ": the 2025 annual average is "the average of the 11 index values published". https://www.bls.gov/cpi/additional-resources/2025-federal-government-shutdown-impact-cpi-faq.htm
- [MINNFED]: Federal Reserve Bank of Minneapolis, "Consumer Price Index, 1800-", 1967 = 100; 1890-1912 from Albert Rees's cost-of-living index as compiled by the BLS for the Handbook of Labor Statistics; read 2026-10-30. https://www.minneapolisfed.org/about-us/monetary-policy/inflation-calculator/consumer-price-index-1800-
- [BEA-DEF]: US Bureau of Economic Analysis, GDP implicit price deflator, annual, 2017 = 100, via FRED series A191RD3A086NBEA (updated 2026-09-30): 1929 = 8.778, 2025 = 128.893; read from the table page. https://fred.stlouisfed.org/data/A191RD3A086NBEA
- [MW-DEF]: Johnston, L. and Williamson, S.H., "What Was the U.S. GDP Then?", MeasuringWorth, 2026, GDP deflator 2017 = 100 (1900 = 4.06, 1929 = 8.75). Not on the study's list of primary sources; used only for the range. https://www.measuringworth.com/datasets/usgdp/
- [MW-CPI]: Officer, L.H. and Williamson, S.H., "The Annual Consumer Price Index for the United States, 1774-Present", MeasuringWorth, 2026 (1900 = 8.14, 1982-84 = 100). Range only, as [MW-DEF]. https://www.measuringworth.com/datasets/uscpi/
- [SC-CPI]: Statistics Canada, table 18-10-0005-01, Consumer Price Index, annual average, Canada, all-items, 2002 = 100, vector v41693271, 1914-2025 via the Web Data Service, downloaded 2026-10-30. https://www150.statcan.gc.ca/t1/tbl1/en/tv.action?pid=1810000501
- [HSC-K]: Statistics Canada, Historical Statistics of Canada (11-516-X), Section K, Price indexes, text. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionk/4057753-eng.htm
- [HSC-K8]: Statistics Canada, Historical Statistics of Canada (11-516-X), series K8, Consumer Price Index for Canada, 1913-1975, 1971 = 100. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionk/K8_18-eng.csv
- [HSC-K33]: Statistics Canada, Historical Statistics of Canada (11-516-X), series K33, General Wholesale Price Index excluding gold, 1867-1975, 1935-39 = 100. https://www150.statcan.gc.ca/n1/pub/11-516-x/sectionk/K33_43-eng.csv
- [BOC-FX]: Bank of Canada, Valet series FXAUSDCAD, annual average exchange rate of the US dollar in Canadian dollars, 2025 = 1.3978. https://www.bankofcanada.ca/valet/observations/FXAUSDCAD/json
- [POWELL]: Powell, J. (2005), A History of the Canadian Dollar, Bank of Canada (gold par with the US dollar to August 1914). https://www.bankofcanada.ca/wp-content/uploads/2010/07/dollar_book.pdf
- [CM]: McInnis, R.M. (2001), Historical Canadian Macroeconomic Dataset 1871-1994, University of Saskatchewan Data Library (as cited in `research-1900.md`; the link returned 404 on 2026-10-30). https://datalib.usask.ca/dli/business/histCanMacroData/
- [CYB18]: Statistics Canada, Canada Year Book 1918, p. 485, weekly family budget in 60 cities, 1900, 1905, 1910-1917 (scanned PDF, not read). https://www66.statcan.gc.ca/fra/1918/191805150485_p.%20485.pdf
- [JST]: Jordà, Schularick and Taylor, Macrohistory Database, release 6 (xlsx and dta only; not read). https://www.macrohistory.net/database/
- [OWID-POP]: Our World in Data, Population (HYDE 2023, Gapminder 2022, UN WPP 2024), grapher CSV, read 2026-10-30. https://ourworldindata.org/grapher/population.csv (CC BY 4.0)
- [OWID-MPD]: Our World in Data, GDP per capita, Maddison Project Database 2023, grapher CSV, read 2026-10-30. https://ourworldindata.org/grapher/gdp-per-capita-maddison-project-database.csv (CC BY 4.0)
- [MPD]: Bolt, J. and van Zanden, J. L., Maddison Project Database 2023, GGDC, University of Groningen; J. Economic Surveys (2024), doi 10.1111/joes.12618. https://www.rug.nl/ggdc/historicaldevelopment/maddison/releases/maddison-project-database-2023 (CC BY 4.0)
- [WPP]: United Nations, World Population Prospects 2024. https://population.un.org/wpp/ (CC BY 3.0 IGO)
- [WDI]: World Bank, World Development Indicators, API JSON for SP.POP.TOTL, NY.GDP.MKTP.KD, FP.CPI.TOTL, PA.NUS.FCRF, 1960-61, read 2026-10-30. https://api.worldbank.org/v2/ (CC BY 4.0, World Bank terms; check)
- [PWT]: Feenstra, Inklaar and Timmer, Penn World Table 10.01, GGDC; xr via FRED XRNCUS<CC>A618NRUG. https://www.rug.nl/ggdc/productivity/pwt/ (CC BY 4.0)
- [BIS-LCPI]: Bank for International Settlements, long consumer prices, SDMX CSV, read 2026-10-30. https://stats.bis.org/api/v1/data/WS_LONG_CPI/A../all?format=csv (BIS terms; check)
- [CLIO]: Clio Infra, Inflation (de Zwart, Reinhart, Rogoff). https://clio-infra.eu/Indicators/Inflation.html (licence not stated)
- [IMF-ER], [IMF-CPI]: International Monetary Fund, data portal SDMX 3.0, ER and CPI dataflows, read 2026-10-30. https://api.imf.org/external/sdmx/3.0/ (IMF terms: attribution; commercial reuse by permission)
- [IMF-AR]*: IMF Annual Reports, par value changes 1946-1973. https://www.imf.org/en/Publications/AREB
- [OECD-MEI]: OECD Main Economic Indicators, currency conversions, via FRED CCUSMA02<CC>A618N. https://fred.stlouisfed.org/series/CCUSMA02CAA618N (CC BY 4.0)
- [H10-A], [H10-M]: Federal Reserve Board, H.10 foreign exchange rates, annual and monthly via FRED (AEXCAUS, EXCAUS and others). https://fred.stlouisfed.org/series/AEXCAUS (public domain)
- [BOE-M]: Bank of England, A millennium of macroeconomic data, US$ per pound, via FRED USUKFXUKM and USUKFXUKA. https://fred.stlouisfed.org/series/USUKFXUKM (BoE terms; check)
- [NBER-M14]: NBER Macrohistory Database, chapter 14, m14002 (Berlin on New York) and m14004a/b (New York on Paris), via FRED. https://fred.stlouisfed.org/series/M14002DEM368NNBR (NBER; check)
- [FRB-BMS1]*: Board of Governors of the Federal Reserve System (1943), Banking and Monetary Statistics 1914-1941, Table 173. https://fraser.stlouisfed.org/title/38 (public domain)
- [ERP1972]: Council of Economic Advisers, Economic Report of the President 1972, ch. 5, Table 31 (Smithsonian changes). https://fraser.stlouisfed.org/docs/publications/ERP/1972/ERP1972_Chapter5.pdf (public domain)
- [FRH-GRA]: Richardson, Komai and Gou, "Gold Reserve Act of 1934", Federal Reserve History. https://www.federalreservehistory.org/essays/gold-reserve-act
- [FRH-NIXON]: Ghizoni, "Nixon Ends Convertibility of U.S. Dollars to Gold", Federal Reserve History. https://www.federalreservehistory.org/essays/gold-convertibility-ends
- [FRUS-1973]*: US Department of State, Foreign Relations of the United States 1969-76, vol. XXXI, doc. 12 (12 Feb 1973 devaluation). https://history.state.gov/historicaldocuments/frus1969-76v31/d12
- [RBA-UNR]: Reserve Bank of Australia, Unreserved, research guide "Float of the Australian Dollar". https://unreserved.rba.gov.au/nodes/view/88861
- [RBA-AR1973]*: Reserve Bank of Australia, Annual Report 1973, The International Environment. https://www.rba.gov.au/publications/annual-reports/rba/1973/int-environment.html
- [SHIZUME]: Shizume, M. (2009), "The Japanese Economy during the Interwar Period", Bank of Japan Review 2009-E-2. https://www.boj.or.jp/en/research/wps_rev/rev_2009/rev09e02.htm
- [SNB-H]*: Swiss National Bank, history. https://snb.ch/en/the-snb/organisation/history
- [HLS]*: Historisches Lexikon der Schweiz, "Franken". https://hls-dhs-dss.ch/de/articles/013748/
- [GOLD-1936]*: World Gold Council, After the gold standard, 27 Sep 1936. https://www.gold.org/sites/default/files/documents/after-the-gold-standard/1936sept27.pdf
- [FRB-G5-1971]*: Federal Reserve Board, G.5 release, 1 Jun 1971 (Swiss franc par 24.485 US cents from 10 May 1971). https://fraser.stlouisfed.org/files/docs/releases/g5/g5_19710601.pdf (public domain)
- [BBK-AR1969]*: Deutsche Bundesbank, Annual Report 1969. https://www.bundesbank.de/resource/blob/702882/24b74d66c91ba100be60837ff917e7ce/mL/1969-annual-report-data.pdf
- [WIFO-1969]*: WIFO Monatsberichte 11/1969 (DM parity 3.66, 24 Oct 1969). https://wifo.ac.at/wp-content/uploads/upload-6385/MB_1969_11_04.pdf
- [BBK-H]*: Deutsche Bundesbank, history of the mark (1923-24 stabilisation, 1948 reform). https://www.bundesbank.de
- [EU-2866/98]*: Council Regulation (EC) No 2866/98 (euro conversion rates). https://eur-lex.europa.eu/eli/reg/1998/2866/oj
- [MINT-PARS]*: the gold contents in law: US Gold Standard Act 1900, UK Coinage Act 1870, German Coinage Act 1873, Latin Monetary Union 1865, Japan Coinage Act 1897; the pars are printed in [FRB-BMS1].
- [IRR]*: Ilzetzki, Reinhart and Rogoff, exchange rate arrangements and anchor currencies, with country chronologies. https://www.ilzetzki.com/irr-data
- [HSIAO]*: Hsiao, L. (1974), China's Foreign Trade Statistics 1864-1949, Harvard University Press (copyright).
- [MW]: Officer, L. H., Exchange Rates Between the US Dollar and Forty-one Currencies, MeasuringWorth. https://www.measuringworth.com/datasets/exchangeglobal/ (all rights reserved; non-profit educational use only)
- [BIS-CBPOL]: Bank for International Settlements, Central bank policy rates (WS_CBPOL), monthly, end of period; downloaded 2026-10-30. https://data.bis.org/topics/CBPOL (BIS terms: attribution; not checked tonight)
- [NBER-CP]: NBER Macrohistory Database, commercial paper rates, New York, monthly 1857-1971 (FRED M13002US35620M156NNBR). https://fred.stlouisfed.org/series/M13002US35620M156NNBR (no licence stated)
- [NBER-NYFED]: NBER Macrohistory Database, discount rates, Federal Reserve Bank of New York, monthly 1914-1969 (FRED M13009USM156NNBR). https://fred.stlouisfed.org/series/M13009USM156NNBR (no licence stated)
- [FEDFUNDS]: Board of Governors of the Federal Reserve System, effective federal funds rate, monthly (FRED FEDFUNDS). https://fred.stlouisfed.org/series/FEDFUNDS (US public domain)
- [NBER-REICH]: NBER Macrohistory Database, official bank discount rate, Germany, monthly 1876-1938 (FRED M13015DEM156NNBR). https://fred.stlouisfed.org/series/M13015DEM156NNBR (no licence stated)
- [BOE-3C]: Bank of England, A millennium of macroeconomic data (Three Centuries), Bank Rate, monthly from 1694 (FRED BOERUKM). https://fred.stlouisfed.org/series/BOERUKM (reproduction permitted with attribution, per the series note)
- [BOJ-IR01]: Bank of Japan, Time-Series Data Search, IR01 MADR1M, basic discount rate and basic loan rate, monthly from Oct 1882. https://www.stat-search.boj.or.jp/api/v1/getDataCode?format=json&lang=en&db=IR01&code=MADR1M (terms not checked)
- [BOJ-FM02]: Bank of Japan, Time-Series Data Search, FM02 STRACLUCON, uncollateralized overnight call rate, monthly average. https://www.stat-search.boj.or.jp/api/v1/getDataCode?format=json&lang=en&db=FM02&code=STRACLUCON
- [BOC-RATE]: Bank of Canada, Valet, V122530, Bank rate, monthly from 1935. https://www.bankofcanada.ca/valet/observations/V122530/csv (Bank of Canada terms: https://www.bankofcanada.ca/terms/)
- [SNB-HTS4]: Swiss National Bank, Historical time series 4: Interest rates and yields (2007), table 1.1 (official rates 1907-2006, year end) and table 1.1a (Switzerland 1838-1906, web only, xls). https://www.snb.ch/en/the-snb/mandates-goals/statistics/statistics-pub/publication-history/historical-time-series
- [BBK-DIS]: Deutsche Bundesbank, discount rate, end of month, 1948-1998 (BBIN1.M.DE.BBK.BBKDIS.DEM.ME). https://api.statistiken.bundesbank.de/rest/data/BBIN1/M.DE.BBK.BBKDIS.DEM.ME?format=csv&lang=en
- [IMF-IFS]: International Monetary Fund, International Financial Statistics, discount rates for China (from 1990-03) and Australia (from 1969-07) (FRED INTDSRCNM193N, INTDSRAUM193N). https://fred.stlouisfed.org/series/INTDSRCNM193N (copyright IMF, reprinted with permission; IMF terms)
- [JST-R6]: Jordà, Schularick and Taylor, Macrohistory Database, release 6 (xlsx, dta). https://www.macrohistory.net/database/ (CC BY-NC-SA 4.0)
- [RBA-OP8]: Reserve Bank of Australia, Occasional Paper 8, Australian Economic Statistics 1949-50 to 1996-97, tables 3.21a-3.22b (xls). https://www.rba.gov.au/statistics/frequency/occ-paper-8.html
- [WB-PINK]: World Bank, Commodity Price Data (The Pink Sheet), annual nominal prices and the MUV Index (CMO-Historical-Data-Annual.xlsx, updated 2026-09-02). https://www.worldbank.org/en/research/commodity-markets (CC BY 4.0)
- [NBER-PRICES]: NBER Macrohistory Database, chapter 4, wholesale prices (FRED ids in commodity-sources.csv). https://fred.stlouisfed.org/ (no licence stated)
- [BLS-PPI]: US Bureau of Labor Statistics, Producer Price Index by commodity (FRED WPU..., PPIIDC, PPIACO). https://fred.stlouisfed.org/ (US public domain)
- [EIA-CRUDE]: US Energy Information Administration, U.S. crude oil first purchase price, annual from 1859 (F000000__3), read from the page. https://www.eia.gov/dnav/pet/hist/LeafHandler.ashx?n=PET&s=F000000__3&f=A (US public domain)
- [USGS-DS140]: Kelly, T.D. and Matos, G.R., Historical Statistics for Mineral and Material Commodities in the United States, USGS Data Series 140 (copper 2020, iron ore 2021; xlsx). https://www.usgs.gov/centers/national-minerals-information-center/historical-statistics-mineral-and-material-commodities (US public domain)
- [GY]: Grilli, E.R. and Yang, M.C. (1988), Primary commodity prices, manufactured goods prices, and the terms of trade of developing countries: what the long run shows, World Bank Economic Review 2(1): 1-47; per-commodity indexes and the MUV from 1900. https://doi.org/10.1093/wber/2.1.1 (data file not located; licence not stated)
- [JACKS]: Jacks, D.S. (2019), From boom to bust: a typology of real commodity prices in the long run, Cliometrica 13(2): 201-220; data 1850-2023 at openICPSR 198401. https://www.openicpsr.org/openicpsr/project/198401/view (freely provided with citation; the deposit's licence not checked; login to download)
- [JACKS-WP]: Jacks, D.S. (2013), NBER Working Paper 18874, appendix of sources per commodity. https://www.nber.org/papers/w18874
- [COW]: Correlates of War project, State System Membership List, v2016 (`states2016.csv`, 243 rows, read as CSV). https://correlatesofwar.org/data-sets/state-system-membership/ (free for research use)
- [FB]: CIA, The World Factbook, fields Government/Independence and Introduction/Background, read from the mirror `factbook/factbook.json` (251 country files; repository last pushed 2026-09-11). https://github.com/factbook/factbook.json . Original: https://www.cia.gov/the-world-factbook/ (removed February 2026; public domain)
- [WP1900]: Wikipedia, "List of sovereign states in the 1900s", read through WebFetch. https://en.wikipedia.org/wiki/List_of_sovereign_states_in_the_1900s (CC BY-SA)
- [WD]: Wikimedia, Wikidata SPARQL query for currencies with inception before 1901 (Eritrean tallero, Netherlands Indies guilder, Finnish markka, Bulgarian lev, Polish rubel). https://query.wikidata.org/ (CC0)
- [WP:Vilayets_of_the_Ottoman_Empire]: Wikipedia, "Vilayets of the Ottoman Empire", the 1905 table. https://en.wikipedia.org/wiki/Vilayets_of_the_Ottoman_Empire (CC BY-SA)
- [WP:Rashidi_dynasty]: Wikipedia, "Rashidi dynasty", with a web search on Najd and the 1902 return to Riyadh. https://en.wikipedia.org/wiki/Rashidi_dynasty (CC BY-SA)
- [WP:Afghan_rupee], [WP:Argentine_peso_moneda_nacional], [WP:Austro-Hungarian_krone], [WP:Bolivian_boliviano], [WP:Brazilian_real_(old)], [WP:British_West_African_pound], [WP:British_West_Indies_dollar], [WP:Chilean_peso], [WP:Colombian_peso], [WP:Congolese_franc], [WP:Costa_Rican_colón], [WP:Cypriot_pound], [WP:Dominican_peso], [WP:Ecuadorian_sucre], [WP:Egyptian_pound], [WP:Ethiopian_birr], [WP:Fijian_pound], [WP:French_Indochinese_piastre], [WP:German_East_African_rupie], [WP:Gold_standard], [WP:Guatemalan_quetzal], [WP:Haitian_gourde], [WP:Honduran_lempira], [WP:Hong_Kong_dollar], [WP:Indian_rupee], [WP:Iranian_rial], [WP:Japanese_yen], [WP:Korean_yang], [WP:Latin_Monetary_Union], [WP:Liang_(currency)], [WP:Liberian_dollar], [WP:Luxembourgish_franc], [WP:Maltese_lira], [WP:Mauritian_rupee], [WP:Mexican_peso], [WP:Monegasque_franc], [WP:Moroccan_rial], [WP:Nepalese_rupee], [WP:Nicaraguan_córdoba], [WP:Old_Taiwan_dollar], [WP:Omani_rial], [WP:Ottoman_lira], [WP:Paraguayan_peso], [WP:Peruvian_sol], [WP:Philippine_peso], [WP:Portuguese_real], [WP:Russian_ruble], [WP:Salvadoran_colón], [WP:Sammarinese_lira], [WP:Scandinavian_Monetary_Union], [WP:Serbian_dinar], [WP:South_African_pound], [WP:Sri_Lankan_rupee], [WP:Straits_dollar], [WP:Surinamese_guilder], [WP:Thai_baht], [WP:Tunisian_franc], [WP:Uruguayan_peso], [WP:Venezuelan_bolívar]: Wikipedia, the article with that name, read through WebFetch on 2026-10-30; each backs the `money_1900` entry of the rows that cite it. URL pattern https://en.wikipedia.org/wiki/<Article> (CC BY-SA)
