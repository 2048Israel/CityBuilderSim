# R0d - research: the 1901 wage ladder and the arrivals' skill mix by year ({next}, 2026-11-06)

## WHERE IT STANDS

**Building.** Branch `batch/R0d` from `batch/R3b`'s ship file (RL1, 853c29bd). Slice 1 (the ladder) is 742590a6; slice 2
(the arrivals' mix) is with Sonnet. Only a city founded the 1900 way (`Founding.isUnfitted()`, R1c ★1, R2 ★3) reads either;
R1d switches that founding on and judges it.

## 1. What R0d is

R0's ★1 split research-1900 §5.4's items 2 and 6 out of R0: founding wages at 1901's level with the 1901 census's ratios
(§5.2), and the arrivals' diplomas from the year's skill mix. R0's ★2: the ladder waits for 1900's tech, so it is switched
on with R1's 1900 recipes, in a city founded the 1900 way, and nowhere else.

| slice | what | state |
|---|---|---|
| 1 | `WageLadder` (today's, PayTier's to the bit; 1901's, the census's, converted to founding money once), the labour market, the allocator's band bases, the pension's wage base and the grant, the founding rents and the depot's founding quote on the city's ladder (`Game.layTheLadder()`, at founding and on load; nothing saved); `WageLadderCheck` | 742590a6 |
| 2 | `ArrivalMix` (knots 1901, 1913, 1953 and today's model in 2021, the shares interpolated by month), `Migration.composeArrivals()`'s 1900 branch, Game's push each month; `ArrivalMixCheck` | with Sonnet |

## 2. The 1901 ladder

Men's earnings at regular work for a year, 1901, each the comparator nearest the rung's own today (`PayTier`'s comments);
in founding money a census dollar is a founding dollar (founding money is 1900's price level, `PriceLevel.FACTOR`; the US
CPI is 8.333 in both years; the Canadian dollar at par on gold), so a rung is its year over 12,000.

| rung | 1901 comparator | a year | a month (founding k) | over the labourer | today's ratio | source |
|---|---|---|---|---|---|---|
| Unskilled | labourers, men | $256.29 | .02136 | 1.00 | 1.00 | B1 |
| Skilled | electricians | $518.12 | .04318 | 2.02 | 1.30 | CYB07b |
| College | draughtsmen and designers | $723.72 | .06031 | 2.82 | 1.59 | B1 |
| Professional | civil engineers | $1,316.31 | .10969 | 5.14 | 2.37 | B1 |
| Senior professional | bank managers | $1,784.37 | .14870 | 6.96 | 2.77 | B1 |
| Elite | **est.**: civil engineers x today's 15.6/8.2 | $2,504.20 | .20868 | 9.77 | 4.51 | est. |

The unskilled rung is .2385 of today's at the founding's prices (.08956): the study's "about a quarter" (§5.2, est. .23)
from the census's own dollars. Skill paid more in 1901, as §5.2 says: an engineer 5.1 labourers against 2.4 today.

The research (2026-11-06, a subagent's pass over the StatCan pages; its notes in the session folder) corrected the study
twice: its "labourers and domestic men $272" is the whole domestic class (labourers alone, $256.29), and its "engineers
$1,306" divides CYB07b's salaries by 1,143 of B1's 1,243 engineers (a misprint; civil engineers, $1,316.31, are used).

## 3. The arrivals' mix

Shares of a year's working arrivals (NONE's part bought by a premium apart), at January of the year, interpolated by month:

| knot | no diploma | a trade (diploma) | college | university | from |
|---|---|---|---|---|---|
| 1901 | .7307 | .2318 | .0300 | .0075 | 1913's (est.: the nearest year read) |
| 1913 | .7307 | .2318 | .0300 est. | .0075 est. | FY1912-13's 238,779 adult men by occupation (CYB1913 Table 47); college and university 1901's adults |
| 1953 | .4745 | .4286 | .0606 | .0363 | 91,133 working arrivals by intended occupation (HSC A351-368); the professions split as the 1951 census's adults |
| 2021 | today's model | | | | `WageBand.arrivalCeiling()`: .12 bought, 1.00, .35, .12 |

In the model a 1900 city's month reads three figures relative to the diploma band's 1: the no-diploma arrivals who come at
the going rate (3.15 per diploma arrival in 1913, 1.11 in 1953, 0 today), and the college and university ceilings (.129
and .032 in 1913; .141 and .085 in 1953; .35 and .12 today). The study's check: 1.24 university students per 1,000 in 1901
and 4.67 in 1950 (HSC W340-438, 3.8x); the university share of arrivals goes .0075 to .0363 (4.8x).

## 4. The calls (★)

1. **★ The ladder's comparators.** Each rung the 1901 occupation nearest its own today's comparator (§2's table), the
   anchor the labourers ($256.29) rather than the study's whole domestic class ($272) or the farm hands ($207.55, often
   with board). Taken.
2. **★ The elite rung is an estimate.** Doctors and lawyers were self-employed and the 1901 census counted wage-earners (31
   salaried physicians averaged $1,097.74, 8 lawyers about $750). Recommended and taken: civil engineers x today's elite
   over professional (15.6/8.2), $2,504, which keeps the ladder rising. The alternative, the 1941 census's wage-earning
   physicians at 1.17 civil engineers ($1,540), puts doctors under bank managers; self-employed incomes ran higher (the
   1939-40 tax statistics: professionals 1.9 times employees).
3. **★ A 1901 tradesman is a diploma-holder.** The no-diploma and diploma split is by the arrivals' occupations (labourers,
   farm hands and miners against mechanics and clerks), not by schooling (about 9% of 1901's adults had 11-12 years).
   Recommended and taken: `WageBand` is the skill each job needs, and a mechanic fills a trades post. The alternative, by
   schooling, gives 1913 about 9% diploma-holders where the tables give 23%, and a 1900 city's trades posts empty.
4. **★ In a 1900 city the ordinary arrival may hold no diploma.** No-diploma arrivals come at the going rate in the year's
   proportion, today's bought part on top; the diploma band stays the base; graduates are bought exactly as today. Taken:
   at the going rate today's model brings diploma-holders only, which a 1901 city's arrivals were not.
5. **★ The professions' licence shares stay today's in every year.** The year enters through the university ceiling.
   Recommended: the census counts make 1901's physicians .30 of the degree-holders and 1951's .057, against the game's .14
   (a design figure), so year shares would fall and rise again across the join. The alternative is year shares from the
   counts.
6. **★ 1953 to 2021 is a straight line to today's model** (no 1961-2011 knots; est.). Recommended: keep until a batch sources
   them from the censuses' attainment tables.

## 5. Checks

- **The trace.** The default playtest (2,405 months) traced on R3b's tip (853c29bd) and slice 1 (742590a6), each built
  apart and run back to back: all eight CSVs byte-identical, the log but for "in 52s"/"in 51s" and the layout windows'
  "1.8 s"/"1.7 s". (Slice 2 and the final tree: below.)
- **FittingCheck 4** went red on slice 1: its 1900 village, on 1901's wages, grows more slowly (1,242 people at month 14
  against 1,780), so its builders finish the second pair of Bakeries after the 36 months the fixture gave them. The window
  is now `BAKERY_MONTHS`, 60; the premise (what a finished Bakery comes with) is unmoved, and a default village stops as
  soon as its pair is open.

## 6. Sources and licences

Every figure shipped is from a Statistics Canada publication (Statistics Canada Open Licence, commercial use allowed) or a
census volume out of Crown copyright (Copyright Act s.12, fifty years after publication):

- [B1] Census and Statistics Office, Bulletin I, *Wage-earners by occupations* (1901 census, 1907), Table II,
  https://publications.gc.ca/collections/collection_2017/statcan/CS98-1901P-1.pdf
- [CYB07a], [CYB07b] Canada Year Book 1907, earnings by class of occupation and the professional class's salaries, 1901,
  https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907015901-eng.htm and ..._1907015903-eng.htm
- [CYB1913] Canada Year Book 1913, Table 47, https://publications.gc.ca/collections/collection_2017/statcan/11-202/CS11-202-1913-eng.pdf
- [HSC] Historical Statistics of Canada, 11-516-X, series A351-368 and W340-438, https://www150.statcan.gc.ca/n1/pub/11-516-x/
- [V1911] Fifth Census 1911, Vol. VI (occupations, 1901 re-classified), https://publications.gc.ca/collections/collection_2016/statcan/CS98-1911-6.pdf
- [V1951] Ninth Census 1951, Vol. I, Table 27 (years of schooling), https://publications.gc.ca/collections/collection_2017/statcan/CS98-1951-2.pdf

A copyrighted article on lawyers' incomes (the Canadian Bar Review, 1951) was read as a pointer and is the source of no
figure.
