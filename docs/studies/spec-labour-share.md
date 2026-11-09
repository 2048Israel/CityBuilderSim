# The labour share by sector (LS1, 2026-11-09)

A study, no model change. HD1-L read Luxury Retail's payroll as 1% of its
value added and Construction's as 5%, from one month of one city, where US
compensation is about half of value added, and guessed at wages trailing
prices over a long run. This measures each sector's payroll over its value
added a year at a time, through the default playtest and an Earth city,
against BEA's compensation over value added by industry, and says where each
gap comes from. It ends in two batches (section 5).

## 1. How it was measured

- **The runs:** the default playtest itself, `LongPlaytest`, seed 0 to m3996
  and seed 1 to m2400, and the Earth city Rotterdam (`--earth`) to m2400, each
  read at the year to m600, 1,200, 2,400 and 4,000. `labour-share/run.sh`
  plays a copy of `LongPlaytest.java` with one line added after
  `g.simulateMonths(1)`, calling `LabourProbe.month(g)`; the tree is not
  touched and the city lives in a `TempDirs` folder.
- **The probe** (`labour-share/LabourProbe.java`) sums twelve months of each
  sector's `SectorBooks` month (revenue, the inputs, payroll, utilities,
  taxes, interest, net, workers, posts) before dividing: a share is a ratio of
  two flows, and one month of one sector is noise (Luxury Retail's share moves
  1.0-5.4% from year to year). Value added is revenue less inputs and
  utilities; for Retail and Luxury Retail revenue is the margin, sales less
  the goods bought, as BEA's trade gross output is.
- **BEA:** GDP by Industry, compensation of employees over value added
  (table 7) and gross output, value added and intermediate inputs (table 25),
  2023-2025, through DBnomics' copy (`labour-share/bea.py`; the two CSVs
  beside it; public domain, `licences.md`). Each sector is set against the
  industry it plays: Luxury Retail "other retail" (4A0), Copper Refining
  primary metals (331), Restaurants food services (722), and so on
  (`report.py`'s table).
- **The tables** are `labour-share/out/`: `report.txt` each run and month,
  `decompose.txt` the gap's halves, `costs.txt` each cost over revenue,
  `size.txt` each sector's share of the city, `series.txt` five sectors year by
  year, `city.txt` the whole city. The runs' CSVs were not kept; `run.sh` then
  `report.py` and `tables.py` remake them.

## 2. The city

BEA: compensation is 51.1% of GDP, 47.7% of private industries' value added.

| run | m600 | m1200 | m2400 | m3996 |
|---|---|---|---|---|
| default, seed 0 | 54.2% | 87.1% | 12.4% (28.1% without Copper Refining) | 9.9% (26.9%) |
| default, seed 1 | 26.8% | 26.8% | 47.2% | |
| Earth, Rotterdam | 26.3% | 63.5% | 39.1% | |

Payroll over value added. Household wages over GDP follow it within a few
points (seed 0, m3996: 10.6%). The young cities sit at or below BEA's half and
swing with which sector is building; seed 0's old city is a third of BEA's
because one sector, Copper Refining, is 62% of its value added at m2400 and
72% at m3996, at a share of 3% (section 4).

## 3. The sectors

The median over every run and month (n of 10), against BEA. The gap is the
share over BEA's, and splits in two: payroll over revenue against BEA's
compensation over gross output (the posts and the wage against the sales),
over value added over revenue against BEA's (the margin). va/wkr is a worker's
month of value added in the city's mean monthly wage; BEA's is what its share
makes it, one over the share, since a sector's worker earns about the mean
wage (`report.txt`'s pay/worker, 0.85-1.09).

| sector | n | share | BEA | gap | pay/rev / BEA | va/rev / BEA | va/wkr | BEA's |
|---|---|---|---|---|---|---|---|---|
| Luxury Retail | 10 | 2.2 | 41.7 | 0.05 | 0.09 | 1.69 | 47.0 | 2.4 |
| Copper Refining | 2 | 3.1 | 51.4 | 0.06 | 0.10 | 1.67 | 33.7 | 2.0 |
| Restaurants | 10 | 19.8 | 66.5 | 0.30 | 0.47 | 1.59 | 4.9 | 1.4 |
| Construction | 10 | 23.6 | 62.3 | 0.38 | 0.58 | 1.42 | 4.0 | 1.5 |
| Freight | 6 | 29.8 | 59.3 | 0.50 | 0.64 | 1.34 | 3.4 | 1.6 |
| Manufacturing | 10 | 54.4 | 65.2 | 0.83 | 0.84 | 1.03 | 1.9 | 1.4 |
| Food Processing, Cement & Concrete, Automotive | 5-9 | 43-54 | 42-52 | 1.0 | | | | |
| Retail | 10 | 65.5 | 47.2 | 1.39 | 2.20 | 1.58 | 1.5 | 2.0 |
| Business Services | 4 | 112.0 | 68.7 | 1.63 | 2.51 | 1.54 | 0.9 | 1.3 |
| Industry | 10 | 70.8 | 42.3 | 1.67 | 3.37 | 2.25 | 1.3 | 2.3 |
| Materials | 5 | 90.8 | 46.1 | 1.97 | 3.97 | 2.00 | 1.2 | 2.1 |
| Real Estate | 10 | 14.2 | 4.7 | 3.03 | 3.21 | 1.13 | 6.8 | 19.8 |

(`decompose.txt` has every sector.) Three sectors are on BEA's mark. Above
it, Business Services, Materials and Industry pay out about what they add or
more: their net over revenue is -0.46, -0.21 and 0.04 (`costs.txt`), sectors
running at a loss, not a share to correct. Real Estate's BEA figure counts
the rent owner-occupiers impute to themselves, which has no payroll; it is not
a like-for-like mark.

## 4. Where the gaps come from

**Not wages trailing prices.** Luxury Retail's share is 4.7% in the year to
m240 and 2.8% in the year to m3840 (`series.txt`): the gap is there from the
first boutique, not grown into. And the wage keeps up: the mean wage over the
price index is 0.077 at m600 on seed 0, 0.085 at m3996, 0.077-0.100 on seed 1
and 0.080-0.113 on Rotterdam, rising if anything.

**Not Luxury Retail's markup.** The shops buy their pieces at 41% of what
they sell them for (0.38-0.43, `costs.txt`), a charged margin of about 2.4
times the landed cost, inside `LuxuryRetail.MARGIN_FLOOR` (1.25) and
`MARGIN_CEILING` (4.0), and a gross margin of 59%: a jeweller's keystone, not
a monopolist's.

**Luxury Retail: the sales per post.** A piece lands at $9,000 (`Good.LUXURIES`,
9.0 thousand at 2025's prices, scaled by `PriceLevel.FACTOR` as the wage
ladder is, whose unskilled rung is 3.46) and sells at about $22,000, and a Boutique
covers 320 pieces a month with 7 posts (`buildings.json` id 69): up to $7
million of sales a month, a million a post. A worker there adds 47 mean
monthly wages of margin a month (18-105 by year) where BEA's other retail adds
2.4. The posts are a boutique's staff and the margin a boutique's margin;
what is not a boutique's is 320 pieces of a $22,000 good a month, which puts
twenty times BEA's margin per worker on each of the seven. The share is the
payroll of seven over that margin.

**Copper Refining: the margin, not the posts.** Seed 0's refineries employ
20,811 at m2400 and 128,267 at m3996 (24% of the city's workers), making 33-72
tonnes a worker a month at 1.2-1.7 mean wages a tonne, with payroll 1.2-1.8%
of sales: a capital-heavy plant's figures, which the posts per tonne do not
make wrong on their face. What stands out is the margin: it buys its input at
52% of its sales (0.47-0.57) and keeps 35% as net income, where a smelter and
refinery is paid in treatment and refining charges, a small part of the
metal's price, and BEA's primary metals buy 71% of their gross output. Value added is 43-53% of its revenue, against 29% at BEA and less at a
refinery, and that margin, multiplied by 128,267 workers' tonnes, is 72% of
the city's value added: the old city's labour share is a refinery's margin.
Only seed 0 builds this many refineries by m2400.

**Restaurants, Construction, Freight: both halves.** Their gaps of 0.30-0.50
are payroll at half to two thirds of BEA's against revenue (0.47-0.64) and a
margin 1.3-1.6 times BEA's. A worker adds 3.4-4.9 mean wages a month where
BEA's add 1.4-1.6: posts a little thin for the sales and prices a little fat
for the costs, each a factor under two. Nothing here is as far off as the two
above, and no batch is recommended for them alone.

## 5. What it recommends

- **LX1 - Luxury Retail's sales per post.** Bring the payroll over the margin
  from about 2% toward BEA's 41.7% (a post's month of margin from about 47
  mean wages toward 2.4), through the Boutique's coverage and posts together,
  the luxury piece's price held (the trade accounts and the household basket
  read it). Its check is this probe rerun, and the city's whole picture with
  it: at seed 0's m3996 Luxury Retail is already 4.1% of the workers and 15.7%
  of the value added, so twenty times the posts at today's demand would take
  most of the city; the batch has to find the coverage and posts at which the
  struck margin and the boutiques' count settle, not divide 320 by twenty.
- **CU1 - Copper Refining's margin, a study.** Why a refinery buys its input
  at half its sales, and what the cathode's price over its concentrate's
  should be (treatment and refining charges), measured on the seeds that build
  refineries; then the batch that sets it. Recommended before LX1, because at
  seed 0's m3996 the refinery's margin is the city's GDP and every other
  sector's share of it.

Neither moves a post or a markup in this batch.
