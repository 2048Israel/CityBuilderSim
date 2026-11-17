# LX1 - Luxury Retail's sales per post (2026-11-17)

The brief is LX1's row in `QUEUE.md` and [LS1's study](../studies/spec-labour-share.md), section 5; CU1's ★6 (LX1
waits for CU2, whose prices set the old city's demand). Cut from main ec481b4a (0.9.132, after CU2 and TQ1). A model
batch, built by an Opus session (Codex out of plan to 2026-11-17 19:35).

## 1. Measured first, on main's path (ec481b4a), before the balance moved

**How.** LS1's probe (`docs/studies/labour-share/LabourProbe.java`, unchanged) on the default playtest, played as
LS1's `run.sh` plays it (a copy of `LongPlaytest.java` with the probe's line after `g.simulateMonths(1)`, the city in
a `TempDirs` folder), with a second read-only probe beside it for the shops themselves (`LuxuryProbe`, kept in
`target/lx1/probe-src/`, never committed: the templates standing, coverage, the queue and the pieces served over the
year, the margin, the posts). Seed 0 to m3996, seed 1 to m2400, Rotterdam (`--earth Rotterdam`) to m2400, as LS1 ran
them; LS1's `report.py` over the CSVs. Main's own default playtest (the trace's reference) exits 1 on TQ1's finding,
"households with no home", 54 times in months 692-745; the Rotterdam run exits 1 on "industry stands farther out than
the homes" at m2400. Neither is this batch's.

**Payroll over the margin** (LS1's share: payroll over value added, the margin less power, water and upkeep, a year
summed), and a post's month of margin in mean monthly wages ("mrg/wkr"; BEA's other retail, 4A0: 41.7% and 2.4):

| run | m240 | m600 | m1200 | m2400 | m3996 |
|---|---|---|---|---|---|
| seed 0: share | 6.8% | 0.6% (m720) | 1.4% | 0.8% | 0.8% |
| seed 0: mrg/wkr | 15.6 | 136.5 (m720) | 68.1 | 112.7 | 117.9 |
| seed 1: share | 13.1% | 1.7% | 1.1% | 0.8% | |
| seed 1: mrg/wkr | 7.9 | 54.2 | 84.2 | 112.4 | |
| Rotterdam: share | 3.4% | 0.7% | 1.3% | 0.6% | |
| Rotterdam: mrg/wkr | 29.4 | 123.1 | 63.9 | 154.6 | |

After the founding the share is 0.5-1.7% (seed 0's years m480-m3996: 0.5-1.4%, median 0.7%), lower than LS1's 2.2%
before CU1 and CU2, and a post's month of margin is 54-185 mean wages, 20-75 times BEA's.

**Sales per post.** Pieces served a post a month: 17-43 (seed 0: 40.7 at m600, 17.0 at m1200, 37.3 at m2400, 30.9 at
m3996; seed 1 18.0-29.9; Rotterdam 19.8-42.7), against the Boutique's 320 over 7 posts, 45.7, at nameplate. The
shops serve 37-93% of their coverage (u) at a struck margin of 2.10-2.81. A piece lands at 1.6-3.4 mean monthly wages
(seed 0, every 240 months; 2.4 at m3996), so a post's month of margin at the margin struck is 4 mean wages a piece.
No Department Store stands on any of the three runs at any month read: the Boutique is the luxury shop.

**The size, and what BEA's share would make it at today's demand.** Luxury Retail's margin is 10-30% of the
business sectors' value added on seed 0 (11.1% at m2400, 25.0% at m3996; seed 1 8-11%; Rotterdam 9-19%), with
0.2-0.8% of their workers. At BEA's 41.7% of that margin, paid at the shops' own pay a worker, the sector would employ
12% of the business sectors' workers at seed 0's m2400 and 30-35% from m3120 on (seed 1 9-10%, Rotterdam 7-19%):
LS1's warning ("twenty times the posts at today's demand would take most of the city") holds after CU2, and the
factor is about fifty, not twenty.

**Why the demand does not fall with the price.** The households' luxury demand is a budget
(`HouseholdBalance.luxuriesWanted()`: the budget over the price, `luxuryBudgetOf()`: the wealth term and a share of
the surplus), so the spending is the same at any margin the shops can serve, and the margin's value added is the
budget times (1 - 1/margin). The share is therefore (posts / coverage) x (pay a post / landed) / (u x (margin - 1)),
and the number of workers it makes is fixed by the budget and the share, whatever the Boutique's coverage and posts:
only the margin moves it, and only between the planner's settling point and `MARGIN_CEILING`.

## 2. The change, and where

- **`buildings.json`, ids 69 and 70** (and `BuildingManager`'s built-ins, the same figures, `BuildingDataCheck`): the
  Boutique covers 7 pieces a month where it covered 320, with 8 posts where it had 7 (diploma 4 to 5; no diploma 2,
  college business 1); makes and uses 7, its stockroom 21 (three months, as before). The Department Store covers 80
  where it covered 4,200, makes and uses 80, stockroom 240; its 91 posts held. Cost, floor, power, road held; no id
  moved; only the luxury templates touched (R6b's 153-154 are elsewhere in the file).
- **`sectors/LuxuryRetail.java`**, banner "WHAT A POST SELLS (LX1, 2026-11-17)": why the coverage is what eight staff
  sell, and two dials: `LABOUR_SHARE` = .417 (BEA 4A0, 2023-2025, LS1's `bea-compensation-share.csv`) and
  `LABOUR_SHARE_FACTOR` = 2 (the band either way of it: a piece's landed cost over the mean wage moves 1.6-3.4 through
  one run). No mechanic reads them; they are the mark the catalogue is struck to and the harness asserts against.
- **`LuxuryRetailCheck`** (new, in `AllChecks` after `RestaurantsCheck`), section 5 below.
- Prose that named the old figures: `GdpCheck` (two comments: a Boutique's stockroom), `NationalAccounts` (one).
- No saved field: coverage and posts are read from the template each month, so an old save loads the new shops (its
  standing Boutiques sell 7 a month from the first month, their stock over three months' worth until sold). No format
  bump.
- **`BuildingsTemplate.loadOf`** (a3e714c69): the last of a building's three road-load streams is now its road load
  less the two shares' sum, `roadLoad - (asCommuters + asGoods)`, where it was `roadLoad - asCommuters - asGoods`.
  Taken one at a time the remainder was rounded twice, and `TradeCostCheck`'s bitwise "three loads add to the one"
  read the new Boutique's 8 as 7.999999999999999. In the order the three are added back the sum is the road load to
  the bit. Every building's bulk stream moves by at most a rounding; the default playtest's eight trace files are
  the same to the byte before and after it.
- **`KeptCheck.fundLists`** (a3e714c69): the fold choices take at most the two older years the record has. In the
  first run of the harness on this balance the played city's fund record had one older year: "the played city's
  record causes multiple older year folds" failed, and the next line's `subList(0, 2)` threw on the FX thread,
  leaving the JVM up until the suite's 1,740 s timeout. The check stands; only the throw is gone (section 5).

## 3. The calls (Jerus's, taken the recommended way)

- **Where in the band.** ★ About BEA's 41.7% in the old city: 7 pieces on 8 posts (0.88 pieces a post), struck at the
  ladder's wages and the planner's margin to 33% in the catalogue, read 36-41% (median, m240 on) on the three runs.
  Other ways: 8 on 8 (1.0 a post; probed as C1, 19-55%, 36% at m2400 and m3996 on seed 0); half-way, about 21% (the
  band's floor; the city less changed, a quarter of the posts again). Why: the batch is to move the share to BEA's,
  and the old city is where it reads steady.
- **What the Boutique gives up.** ★ Its coverage (320 to 7), its posts 7 to 8, the building held: its 750 sq ft and
  $148k a post stand beside the Convenience Store's 1,000 and $88k, and its power (1.12 a worker) and road (1.0) are a
  shop's. Other ways: posts up and coverage held (about 365 posts on 320 pieces: a Boutique would be a works); a
  geometric split (about 50 posts on 44 pieces, fewer bigger shops, the building grown sevenfold); 7 posts on 7
  pieces (the posts held, about 29% in the catalogue, nearer the band's floor). Why: what was wrong was the sales a
  post, not the building.
- **The Department Store moves with it.** ★ 80 pieces on its 91 posts. The planner scores templates by coverage x
  (sell - landed) / cost and does not read payroll, so with the Boutique alone cut it would build Department Stores,
  each covering 4,200 on 91 posts, and the share would not move. Other way: the Boutique only (no change in the city).
- **The band's width.** ★ A factor of two either way of 41.7% (20.8-83.4%). Why: the landed cost over the mean wage
  alone moves by two in one run. Other ways: 30-55% (from m240, 130 of seed 0's 314 years fall outside it, 65 of
  seed 1's 181, 50 of Rotterdam's 181); none (a point value no run holds).
- **The fixture's fortune.** ★ `LuxuryRetailCheck` credits its 200-house town a wealth sized from
  `WEALTH_SPENT_A_MONTH` to ask for two Boutiques' coverage. Why: a 24-month town has no luxury queue of its own (the
  first fixture's 60 houses bought nothing at the planner's margin), and the share is a ratio a queue of any size reads.
- **The Department Store is never chosen.** ★ Left as it is, for LQ1 or Jerus (REV-LX1-1's finding). The planner
  scores a template by coverage x margin over cost and reads no city size (`LuxuryRetail.plan()`), so at 80 pieces on
  $21,600 against the Boutique's 7 on $1,180 (0.0037 a dollar against 0.0059) it never builds one while a Boutique
  can be staffed. It was so on main too (4,200 and 320: 0.19 against 0.27), and no Department Store stood on any run
  read (section 1); `BuildingManager`'s comment said it became the better buy as the city grew, and now says this.
  Other ways: price it to the Boutique's coverage a dollar (about $13,500 where it is $21,600), so the planner weighs
  the two alike; give the planner a scale term (the big shop where the queue is large), a mechanic of its own; take
  it off the planner's list, its id kept. Why left: LX1 is what a post sells; which shop a large city builds is part
  of LQ1's question, the luxury queue's size.

## 4. LS1's probe, before and after

The same runs as section 1 (seed 0 to m3996, seed 1 and Rotterdam to m2400), main ec481b4a then this balance. Share:
payroll over value added, a year; mrg/wkr: a post's month of margin in mean monthly wages (BEA: 2.4); LR workers:
Luxury Retail's staffed posts.

| run, month | share | mrg/wkr | LR workers | people |
|---|---|---|---|---|
| seed 0, m600 | 0.7%; 29.8% | 127.75; 3.04 | 7; 256 | 14,441; 8,493 |
| seed 0, m1200 | 1.4%; 54.6% | 68.17; 1.70 | 28; 1,016 | 33,302; 25,685 |
| seed 0, m2400 | 0.8%; 45.5% | 112.74; 2.10 | 504; 17,361 | 633,124; 550,804 |
| seed 0, m3996 | 0.8%; 38.8% | 117.89; 2.40 | 4,894; 420,800 | 1,871,609; 5,523,722 |
| seed 1, m600 | 1.7%; 39.8% | 54.26; 2.28 | 6; 231 | 4,962; 7,349 |
| seed 1, m1200 | 1.1%; 60.3% | 84.26; 1.61 | 21; 1,088 | 26,655; 30,866 |
| seed 1, m2400 | 0.8%; 32.7% | 112.40; 2.92 | 132; 74,176 | 198,726; 1,592,536 |
| Rotterdam, m600 | 0.7%; 37.6% | 123.13; 2.55 | 7; 307 | 13,236; 8,970 |
| Rotterdam, m1200 | 1.3%; 44.3% | 63.92; 2.05 | 28; 884 | 35,682; 24,211 |
| Rotterdam, m2400 | 0.6%; 104.3% | 154.58; 0.97 | 42; 2,624 | 42,409; 40,954 |

Household wages over GDP (LS1's other measure), main then LX1: seed 0 46.1/58.7/45.9/31.2% and 46.2/51.6/47.7/39.8%
(m600, m1200, m2400, m3996); seed 1 46.7/58.9/53.9% and 52.9/57.5/38.8%; Rotterdam 49.2/70.8/43.1% and
57.6/64.1/83.1%.

**How often the share is in the band, counted over every year of the runs** (the house rule: count it in a real run):

| run | years | in BEA's band, main | in BEA's band, LX1 | LX1 from m240: median, low-high, above the band |
|---|---|---|---|---|
| seed 0 | 331 | 0 | 313 | 36.1%, 18.3-192.6%, 11 years |
| seed 1 | 198 | 0 | 194 | 40.1%, 23.9-93.4%, 4 |
| Rotterdam | 198 | 0 | 194 | 40.6%, 24.4-104.3%, 4 |

Main's share is 0.4-13.1% in every year of the three runs, never in the band. The years above it are slumps, when the
shops' margin falls with the city's spending and their posts do not: seed 0 m924-996 (the city's staffed workers
14,831 to 6,534, Business Services 2,836 to 0 and Manufacturing 5,145 to 854, both already at a loss; the bank's
leverage at its run low, 0.07% at m940), and Rotterdam's m2400, where 328 Boutiques serve 27-48% of their coverage
(main's 6 served 93%). Rotterdam flags "industry stands farther out than the homes" at m2400 as main does.

**What it costs the city.** At BEA's share the luxury budget buys posts: Luxury Retail takes 23-27% of the business
sectors' workers in seed 0's old city (0.8% on main), and the cities are smaller through m1200 and differently sized
after (seed 0 551k against 633k at m2400 and 5.5M against 1.9M at m3996; seed 1 1.59M against 199k at m2400). The
budget's size is LQ1's question (the ship file's queued row): BEA's other retail is a few per cent of GDP, the luxury
margin here 10-30% of the business sectors' value added.

## 5. The checks

- **`LuxuryRetailCheck`** (new). Section 1, the catalogue: each luxury template's payroll at the ladder's wages
  (`PayTier`) over its coverage's month of margin at the planner's settling margin (2.6585x, `TARGET_HEADROOM`'s
  queue, struck from the model's own `MARGIN_FLOOR`/`MARGIN_CEILING`) is in BEA's band, `LABOUR_SHARE` divided and
  multiplied by `LABOUR_SHARE_FACTOR` (20.8-83.4%). Section 2, the fixture: a 200-house town of 24 months, Luxury
  Retail held; the households credited a wealth that asks for `BOUTIQUES_ASKED` (2) Boutiques' coverage; those two
  built standing; 24 months played; a year of its `SectorBooks` read as LS1 reads them, each month checked sold and
  staffed. Section 3: the households given three Boutiques' worth more and a month, so the queue past the counters is
  a whole Boutique's coverage; the investor's estimate of one more Boutique is then a profit. First form (96dae306b,
  and on 3ea6cf35b): all passed, the town's year 54.7% on three Boutiques. REV-LX1-1 found three faults, mended:
  section 3 never read a queue (three Boutiques over a queue of about 15, so `estimatedMonthlyProfit()` fell back to
  half the coverage); the count was the ceiling of the town's own queue over a Boutique's, so the town's demand set
  it rather than `BOUTIQUES_ASKED`; "sold every month" checked the year's sums. Now (on a21b0cc32's tree, main
  69a29638 merged): all passed, Boutique 33.4%, Department Store 32.9%; the queue 16 against 14 pieces of counters;
  12 of 12 months sold; the town's year 57.6% ($22.885k of payroll on $40.698k of margin less $0.964k; 14.9 of 16
  posts filled, 6 pieces served of 25 wanted a month at 2.943x); 44.0 pieces wanted past 14 of counters; one more
  Boutique +$2.69k a month.
- **Red on main, by hand**: main's two templates put back in `buildings.json` and `BuildingManager` for one run, then
  restored (log `target/lx1/check-main.txt`): exit 1, "LuxuryRetailCheck: 3 FAILED":
  - "Boutique: its payroll at the ladder over a full month's margin is in BEA's band FAIL 0.6%: 7 posts, $0.787k of
    wages, on 320 pieces at $0.3864k of margin each"
  - "Department Store: its payroll at the ladder over a full month's margin is in BEA's band FAIL 0.6%: 91 posts,
    $10.181k of wages, on 4,200 pieces at $0.3864k of margin each"
  - "a year of the town's Boutiques: payroll over value added (LS1's share) is in BEA's band FAIL 1.3%: $30.124k of
    payroll on $2,339.858k of margin less $1.450k of power, water and upkeep"
  Section 3 passes on main too ($62.96k a month): it guards the change, not main. The red run was of the first
  form; section 1's two lines are unchanged since, and on main's balance the mended section 2 cannot stand its
  fixture (two Boutiques are 640 pieces of counters on a queue of about 16). Not rerun on main after the mends.
- **The fixture's first two forms failed, and why** (kept so the fixture's size is not taken as arbitrary): a 60-house
  town had no buyer at the planner's margin after 24 months, so the fortune; a town too small for its posts filled
  6.1 of 24 at twice the ladder and read 107% with a loss, so 200 houses (439 people, its workers outnumbering the
  posts).
- **`BuildingDataCheck`**: the built-ins match `buildings.json` exactly, before and after the merge.
- **The harnesses that name the luxury shops** (`BankCheck`, `BuildCardCheck`, `ForeignCheck`, `GdpCheck`,
  `InvestCheck`, `KeptCheck`, `LuxuryRetailCheck`, `MonetaryCheck`, `NewGameCheck`, `RestaurantsCheck`,
  `SectorStatementCheck`, `TradeCostCheck`, `TreasuryCheck`, `VanCheck`, `BuildingDataCheck`, with `CentralBankCheck`
  and `YoungBankCheck` the suite added), on 96dae306b: 13 passed; `InvestCheck` and `YoungBankCheck` red on main's
  recorded lines; two NEW. `TradeCostCheck`: "Boutique: three loads add to the one FAIL 7.999999999999999 != 8.0",
  mended in `loadOf` (section 2), passing since. `KeptCheck`: the fund's fold fixture, below.
- **`KeptCheck`, section 63, the city's fund.** Its fixtures read the played city (`LongPlaytest.probeCity`, 600
  months), which on this balance is the smaller city of section 6 (8,493 people at m600 against main's 14,441). In
  the first run "the played city's record causes multiple older year folds" failed (one older year in the fund's
  last `ACTIVITY_ROWS` rows) and the harness threw and hung (section 2). In the second, on a3e714c69, that line
  passed and "the played city's market causes at least three Search pages" and "Search Show more reaches page 3: the
  redraw and the next equal a page built new" failed (61 securities in the fund's reach); its other three reds are
  main's recorded FB6 lines. The two runs played the same model; what the section's city holds by then moves with
  the harness's run, and on LX1's path it stands at the fixtures' edge. They are fixtures that do not cause their
  condition: the class of KS1-rest ("KeptCheck on another city's path"). Recorded in the ship file's `## known-reds`
  against FB6 (queued; its KF63 is section 63's fixtures), not mended here.
- **The fast checks** on the merged tree (7bf9a4276): compile, maps, Stale 0 firm, tree clean but the generated docs.

## 6. The trace

`-Dplaytest.trace` on the default playtest (2,400 months, exit 0 in 243 s, "All checks passed.", findings: "Nothing.")
of the balance (96dae306b; a3e714c69's, after the remainder fix, is the same to the byte) against main's reference
made for this batch (`C:/Users/Jerus/AppData/Local/Temp/cbs-trace-main-ec481b4a/`; main's own run exits 1 on TQ1's
"households with no home", months 692-745, not this batch's). All eight files move: `bank`, `build`, `house`,
`labour`, `month` and `pop` from month 11, `bonds` from 12, `borrow` from its first paper (main's month 59, LX1's
63; 44 papers in the run on main, 37 on LX1). One city's path, so the ensemble judges; the size, main then LX1:

| month | people | out of work | jobs | inflation, a year | unskilled premium | diploma premium |
|---|---|---|---|---|---|---|
| 60 | 977; 815 | 0; 0 | 596; 531 | 15.5%; 15.2% | 1.079; 1.282 | 0.825; 0.993 |
| 120 | 1,471; 1,064 | 0; 11.7% | 767; 500 | 10.6%; 2.2% | 0.963; 0.886 | 0.665; 0.689 |
| 240 | 1,833; 1,249 | 9.4%; 19.4% | 819; 508 | -4.2%; -4.2% | 0.939; 0.850 | 0.713; 0.783 |
| 600 | 14,441; 8,493 | 0; 0.0% | 7,244; 4,120 | 15.1%; 7.8% | 1.037; 1.009 | 0.791; 0.738 |
| 1,200 | 33,302; 25,685 | 9.4%; 9.0% | 13,852; 10,849 | 7.4%; 11.3% | 0.850; 0.850 | 0.700; 0.677 |
| 1,800 | 40,436; 40,681 | 9.1%; 9.5% | 16,856; 16,607 | -3.8%; -2.0% | 0.868; 0.867 | 0.700; 0.676 |
| 2,399 | 630,320; 547,879 | 5.9%; 3.6% | 258,580; 228,753 | -7.8%; -5.3% | 0.971; 0.991 | 0.723; 0.726 |

The founding is smaller (the luxury shops now take posts the founding's other works wanted; 11.7-19.4% out of work at
m120-m240 where main had 0-9.4%), level with main by m1800 and 13% smaller at m2399. Prices rise less: the price index
at m2399 is 5,752 against main's 35,033. In real terms the shops' monthly sales at m2399 are 13.4% of GDP against
main's 17.3%, and their net 20% of their sales against 44%.

**The longer run.** Seed 0's 4,000-month probe run on this balance flags "the city map across a save" and "the played
city sidecar was refused or lost history", 2 times each, at months 3589 and 3952 (slot 9 of its `TempDirs` folder; 8
of 10 reloads read the map back the same), in a city of 5.5M; main's same run, 1.9M at its end, flags nothing. The
gate's 2,400 months do not reach it; the nightly's 4,000 may. It is the map's sidecar refused whole on a reload, what
FB8's DM2-B row studies ("no normal-play route to that mismatch" was DM2's finding): this is one, in a city larger
than main's runs reach, and is left to FB8 (not this batch's code: `CityMap`, `MapSiteHistory`). The nightly prints
both as findings, so the ship file records them as known reds against FB8.

**Against main 69a29638 (0.9.134, after OX1), the merged tree.** OX1 moved main's own trace (its city 820k at m2399
where ec481b4a's was 630k), so main's reference was made again (`C:/Users/Jerus/AppData/Local/Temp/
cbs-trace-main-69a29638/`, one JVM from a `git archive` of 69a29638: exit 0, findings "Nothing.", run to m2405), and
the merged tree's (a21b0cc32's model; the harness and comment mends after it move nothing the playtest reads) set
beside it: exit 0 in 279 s, "All checks passed.", findings "Nothing.", run to m2402. All eight files move: `bank`,
`build`, `house`, `labour`, `month` and `pop` from month 11, `bonds` from 12, `borrow` from its first paper (m63 on
both, its figures apart; 44 papers on main, 37 here). The size, main 69a29638 then LX1 merged:

| month | people | out of work | jobs | inflation, a year | unskilled premium | diploma premium |
|---|---|---|---|---|---|---|
| 60 | 888; 814 | 0; 0 | 559; 531 | 10.7%; 15.0% | 1.060; 1.285 | 0.876; 0.996 |
| 120 | 1,205; 1,356 | 1.1%; 6.3% | 623; 665 | 14.0%; 3.2% | 0.960; 0.930 | 0.661; 0.682 |
| 240 | 1,295; 1,468 | 6.0%; 3.6% | 603; 705 | -4.8%; -3.6% | 0.976; 0.981 | 0.710; 0.711 |
| 600 | 3,496; 8,894 | 12.8%; 0 | 1,509; 4,171 | 2.9%; 2.5% | 0.907; 1.021 | 0.689; 0.783 |
| 1,200 | 36,343; 40,420 | 7.4%; 10.4% | 15,261; 16,418 | 8.2%; 9.8% | 0.879; 0.850 | 0.670; 0.676 |
| 1,800 | 49,857; 175,374 | 3.5%; 0.8% | 22,082; 74,405 | -2.3%; -2.5% | 0.915; 0.971 | 0.704; 0.735 |
| 2,399 | 819,808; 1,306,447 | 4.3%; 7.6% | 331,441; 528,581 | -3.9%; -3.6% | 0.987; 0.936 | 0.721; 0.716 |

On OX1's main the branch's city is the larger from m120 on (1.31M against 820k at m2399), where on ec481b4a it was
the smaller: one path each, the ensemble's to judge. The price index at m2399 is 8,237 against 25,268; the shops'
sales are 25.4% of GDP against 19.4%, their net 36% of their sales against 45%.

## 7. The ensemble

- **On 2faef9e6's model (main 48eb9b42 merged), against `docs/ensemble/0.9.132.md`**, run as information while OX1
  shipped (target/ensemble/LX1/score.md): 80 of 80 runs in 49.4 min, exit 4, criteria 21/26, hard 7/10. M1 (the
  money audit): 0 in all five scenarios. F1 (the findings): FAIL in AUTO2 2/16, ZERO_SLACK 2/16 and ZERO_HOT 1/16,
  PASS in SHOCK 0/16 and HIGH 0/16 (HIGH FAIL to PASS, ZERO_HOT PASS to FAIL; ZERO_HOT H2 VACUOUS to PASS, info).
  Every F1 city is one cause class, "households with no home", one month each: AUTO2-4 and ZERO_SLACK-4 at m728 (one
  city), AUTO2-10 and ZERO_SLACK-10 at m1088 (one city), ZERO_HOT-2 at m353. TQ1's class, raised by main's
  ec481b4a default run too (months 692-745); main 69a29638's default run and the merged branch's raise none. Not
  committed as `docs/ensemble/LX1.md`: the run on the merged model is the one that counts.
