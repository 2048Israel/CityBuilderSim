# R0e - research: the household's basket by year ({next}, 2026-11-07)

## WHERE IT STANDS

**In progress.** Branch `batch/R0e` from `batch/R0d`'s ship file (RL1, 6a16cabd).

## 1. What R0e is

research-1900 §5.4 item 5: "The household's basket follows the year: 1900's has more raw staples, lamp oil and coal, and
no car (§4)". R0c built the homes' lamp oil and coal as two shares of the homes, off, and left R0e to size them by year
(R0c ★1, ★2, ★11). R0e puts the basket by year in `consumption.json` (the basket's file: "every number a household's grocery
basket is built from lives here") and reads it in three places, all **only in a city founded the 1900 way**
(`Game.foundedUnfitted()`, R1b; R1d switches that founding on for players):

| what | where it is read | in 1900 | grows into today's |
|---|---|---|---|
| bread baked at home | `CityBasket.perHead()` → `Consumption.basket(x, pressure, year)`, the shops' and the kitchens' basket (Game ~4335, ~4343) | 51% of the bread bought; the rest baked from GRAINS at the same calories | all bought by 1971 |
| homes lit by lamp oil | `Game.basketHearsTheYear()` sets `HomeFuel.setLampShare()` each month, at founding and on load | 96% of the homes | none by 1961 |
| homes heated by a coal stove | the same, `setCoalShare()` | 50% | none by 1971 |
| cars | `Motoring.month()`: the year's cars per household caps the ownership ceiling | none | no cap from 1971 |

`Game.basketYear()` is `foundedUnfitted() ? getConsumption().yearAt(month) : null`; a default city has no year, so its
basket, its homes' shares (0) and its car ceiling are today's to the bit (§5). The homes' held sale, struck at 6d and
charged next month, now crosses a save (`DataSave.homeFuelSale`, written only for a month that struck one), since R0e is
what turns it on (R0c ★2 left that to R0e).

## 2. The figures

Knots at January of the census years; each figure linear by month between them; today's from 1971's January.

| year | homes on lamp oil | on a coal stove | cars a household (cap) | bread bought |
|---|---|---|---|---|
| 1900 | .96 est. | .50 est. | 0 | .51 est. |
| 1911 | .81 est. | .54 interp. | .0145 | .51 |
| 1921 | .39 est. | .57 interp. | .221 est. | .51 |
| 1931 | .17 est. | .61 interp. | .456 | .51 |
| 1941 | .034 | .644 | .497 | .51 est. |
| 1951 | .007 | .496 | .618 | .51 est. |
| 1961 | 0 est. | .106 (all dwellings) | .950 | .755 interp. |
| 1971 | 0 | 0 | 1 (no cap) | 1 |

- **Lamp oil** (`lampShare`): the urban share of dwellings without electric light. The censuses give 1941 (96.55% of urban
  dwellings lit, S14) and 1951 (99.31%, S15). 1921 and 1931: residential and farm customers over households (43.8%, 59.3%;
  S10, S11; the method is within a point of both censuses nationally) times 1941's urban-to-all ratio, 1.397. Before 1920
  Canada has no figure: the US dwellings with electric service (S13: 8.0% in 1907, 15.9% in 1912) at their growth, times
  1920's urban-and-non-farm over all (47.4 / 34.7 = 1.366): 4.2% lit in 1900, 18.9% in 1911.
- **Coal** (`coalShare`): the urban dwellings heated principally by coal or coke, 64.4% in 1941 (S14) and 49.6% in 1951
  (S15); 1961's 10.6% is all dwellings (S17; no urban split found); 1971's 1.1% (S17) is taken as today's 0. In 1900 the
  Department's budget buys an eighth of a short ton of coal and an eighth of a cord of wood a week (S1), a coal home and a
  wood home weighted alike: .50.
- **Cars** (`carsPerHousehold`): passenger cars registered (S12) over households (S11) at each census. Before 1923 the
  prairie provinces and British Columbia report totals only: 1911 is all registrations less motorcycles, 1921 the reporting
  provinces' passenger share of the rest. None registered in 1900 (Ontario's first 178 in 1903). 1971 has 115 a hundred
  households, so the cap is off.
- **Bread** (`bought.BREAD`): the budget buys 15 lb of bread and 10 lb of flour a week for five (S1); at 0.70 lb of flour a
  pound of bread (est.) that is 14.3 lb baked at home, so 51% bought. No source gives a bought share by year; the bakeries
  and the homes each took about as much flour from 1929 to 1950 (bakery bread 33-45% of all flour, S8 with S9; bakers and
  family trade 40-45% each about 1939, S6), so it is held to 1951 and drawn to today's by 1971.
- **What a home burns** (`HomeFuel`'s two dials, R0c's estimates replaced): a lamp-lit home burns the budget's gallon of
  coal oil a week, the imperial gallon, 4.54609 L x 52 / 12 = **19.70 L a month** (was 25 L est.); a coal-heated home the
  budget's eighth of a short ton a week (1/16 anthracite and 1/16 bituminous), 0.125 x 52 / 12 x 0.90718 = **0.491 t a
  month** (was 0.7 t est.). The prices are the market's, not dials; for the record the budget's December 1900 prices are
  coal oil 24.0 c a gallon, anthracite $6.32 and bituminous $4.98 a ton (R0c's $6.50 guess was 3% high).

The research (2026-11-07, a subagent's pass over the Year Books, the censuses and Statistics Canada's historical series)
is in the session folder as `R0e-research.md`; it corrected the printed 1900 fuel-and-light total ($1.947 is a
transposition of $1.497: the five lines sum to 149.7 c, and only that closes the $9.373 grand total).

## 3. The calls (★)

## 4. PD1

PD1 (the drivers' petrol perhaps counted twice in the households' spending) follows R0e in this lane. R0e touches
`Motoring.month()`'s ceiling only, not `Retail.sellFuel()` nor `Game.syncHouseholdAccounts()`'s `retailSales` lines that
PD1 is about; R0c's deduction of the homes' bill from the shopping (Game ~4163) is unchanged and is what PD1 mirrors.

## 5. Checks

## 6. Sources and licences

Every figure shipped comes from a source that allows commercial use.

- **S1** Annuaire du Canada 1918, p. 485, the Department of Labour's weekly budget of a family of five, 60 cities, December
  1900, https://www66.statcan.gc.ca/fra/1918/191805150485_p.%20485.pdf - Crown copyright expired (1918).
- **S3** Board of Inquiry into the Cost of Living in Canada, Report, vol. II (1915), pp. 71-76,
  https://publications.gc.ca/collections/collection_2014/bcp-pco/CP32-98-1915-1-eng.pdf - Crown copyright expired.
- **S6** USDA Bureau of Agricultural Economics, Marketing and Transportation Situation, 28 May 1947,
  https://esmis.nal.usda.gov/sites/default/release-files/sq87bt605/1831cp30s/p8418r41z/MTS-05-28-1947.pdf - US federal work,
  public domain.
- **S8** DBS, Bakeries 1964 (32-203), Table 14, https://publications.gc.ca/collections/collection_2018/statcan/32-203/CS32-203-1964-eng.pdf
  - Crown copyright expired; "may be used freely".
- **S9, S10, S11, S12** Historical Statistics of Canada 11-516-X, series M428-445, Q97-101, A248-253, T147-194,
  https://www150.statcan.gc.ca/n1/pub/11-516-x/ (sectionm, sectionq, sectiona, sectiont CSVs) - Statistics Canada Open
  Licence; the DBS annual reports behind them are Crown works over 50 years old.
- **S13** Historical Statistics of the United States, Colonial Times to 1970 (Census Bureau, 1975), series S108-119,
  https://www2.census.gov/library/publications/1975/compendia/hist_stats_colonial-1970/hist_stats_colonial-1970p2-chS.pdf -
  US federal work, public domain (not the copyrighted Millennial Edition).
- **S14** Census of Canada 1941, vol. IX, Tables 11 and 13, https://publications.gc.ca/collections/collection_2017/statcan/CS98-1941-9.pdf
  - Crown copyright expired.
- **S15** Census of Canada 1951, vol. III, Tables 24 and 36, https://publications.gc.ca/collections/collection_2017/statcan/CS98-1951-3.pdf
  - Crown copyright expired.
- **S17** Canada Year Book 1975, 14.3.5, https://www66.statcan.gc.ca/eng/1975/197505940582_p.%20582.pdf - Statistics Canada
  Open Licence (Crown copyright also expired by 2026).
