# WN3 - the world market ({next}, 2026-10-29)

The batch's record. The study's WN3 row (`docs/studies/spec-nations.md` §9, line 408 ★) and §4: the world outside the
city stops being a price list. A night-line batch (2026-10-28, Jerus: "you are to continue all night until 7am"),
built on WN2 (RL1). Opus wrote the briefs, reviewed and committed; Sonnet 5.5 slices at xhigh wrote the code (7).

## 1. What it is

- **`WorldMarket`** (new, model): the 196 nations of WN0's `world-1.bin` trade each of its 60 goods on a smooth spatial
  equilibrium, exact hat algebra on the base year's (2024) flows and WN0's CES kernel. **Every January** each good is
  solved: each maker's mill price and one world supply scale, so the good's price follows its real path (WN1's
  `Nations.goodPrice`), from each nation's growth. Anderson-accelerated, warm: 12 sweeps a good cold (worst 50), a
  January 19-31 ms for all 60 goods, a month's bands 5-8 µs. `Base` is the file's, once a process (pairs, the
  freight table in US$ a tonne from WN0's dials, the calibration); a static cache shares a year's solve between cities.
- **The city's band** (§4.4-4.5): from its site's legs to the nations (its host by HOME_KM, six entry nodes by land
  within 2,500 km and by sea once it has a berth, the table onward), each buyer's price is its cheapest landed seller's
  (`B_j`), and the band from **last month's** exports and imports (D16): the sale fill over the **binding suppliers**
  (★3), the purchase fill from the cheapest landed sellers, each capped at what a buyer uses or a seller makes, and
  the world's slope on top (D4). **The floor is never above the ceiling by construction** (the triangle through the
  city-view closure f'), checked in 141,000 combinations. Past every seller's capacity the ceiling stops at
  SCARCITY_MULTIPLE (4) of the cheapest landed cost.
- **The literals stay the prices** (D12): per good a price scale and a freight scale, so the reference city - Halifax,
  no berth, Historical 2025, trading nothing - sees `Good.worldExportPrice()` and `worldImportPrice()` to 1e-9
  (55 two-sided goods; the 5 one-sided take their class's median freight scale).
- **Wired** (`Game`, THE WORLD MARKET): built the first month (or the load) where a world file is found; the January
  solve and the band at the top of `startOfMonthUpdate()`, after the rate's strike, before anything clears; the band
  into each traded good's `GoodsMarket.setWorldPrices()`. `Game.nextMonth()` is the only caller of both
  `startOfMonthUpdate()` and `SimulationEngine.simulateMonth()`, so every month runs one order. **No world file: the
  game plays on the literals** - as main's to the byte until THE ORDER (5 item 7), which mends every works' orders
  on the literals too (4: the default playtest is main's to month 1608).
- **`PartnerTrade`** (new): the month's trade by nation and the year's by (good, nation), from the month's own fills
  and draws (the pre-clearing draws included: the fleets' fuel and the builders' materials, which the clearing's
  tally wipes); a snapshot for the screens; the band reads its units (★9).
- **Saved** (D28): the solved year, mill prices and scales (restored, never re-solved: a reload is exact), the trade
  by partner, the markets' world prices (`Markets.State`), and **the History series by partner and good** (a yearly
  record; 1.2 MB over 333 years). **`SAVE_FORMAT` 48 and `OLDEST_LOADABLE_FORMAT` 48**: an older save has no solved
  world and no trade by partner (the row's and D28's call). A pre-landmass Earth save is now the floor's to refuse.
- **The dollar's draws on the world's seed** (§11 item 1): `WorldEconomy` mixes in the path seed's difference from
  `Nations.DEFAULT_SEED`, so two seeds meet two dollars and **the default seed meets the dollar every harness was
  tuned on**, to the bit.
- **The map** (WN2 left it, its ★1-2): the **Trade routes** chip beside Nations and Borders; this month's trade as
  lanes from the city, violet sold and sand bought, width by money, none under D$0.3M a month (board 1). **They share
  and branch** (`TradeLanes`: a partner joins the nearest vertex of an existing lane within 15% of its great circle):
  ten European partners from Halifax leave the city as one lane with 5 branch points; all 196 make 248 edges, 2
  leaving the city. Built once a month (0.4-0.6 ms), culled to the view, a frame 0.008-0.11 ms (budget 0.5), at
  most 16 strokes however many partners. The nation's card gains "Buys from us this year", "Sells us this year" and
  "The haul, this year"; a generated world's partners are arrows at the map's edge (§6.4).
- **`WorldMarketCheck`** (new, 11 sections, fixture and full file, 11 s): the solve clears, the calibration, the floor
  under the ceiling, a city moves its price as big as it is, the month's step against a re-solve, seeds, the save,
  the stand-in, the pieces, timings, the wood case. GlobeCheck 13-14 (the lanes), SaveFileCheck and ReadPathCheck
  sections for the market and the trade.

## 2. The calls (★; each took the study's recommendation where it had one, BP1)

1. ★ **The January solve is the nations' alone; the city moves its own prices by the month's step** at its current
   volume (the study put the city's year into the solve). The solve is then a pure function of (file, path, seed,
   year), shared by every city of one world, and a reload needs only it; the step reproduces a re-solve with the
   city's tonnes in (2). Lost: a city that floods a market and stops finds it recovered at once, not next January.
   **Recommended: keep.** A later batch can add the year's memory.
2. ★ **The step's reach.** Against a re-solve with the city's tonnes in, in % of the price (full file): every good
   within 3% at 0.1% of world use (worst 0.30%); at 1% the median good 0.70% and lumber, crude, iron and grains each
   within 3% (41 of 56 goods); at 5% and 10% of world use, a giant's, median 4.1% and 5.0%, worst 13.7% and 20.7%:
   **printed, not asserted**. Lumber, the study's case: +0.02%, +0.10%, −4.5%, −5.0%. The re-solve compared is
   max(0, realised): the floor is clamped at 0. **Recommended: ship; queue WN3-S** (2 below).
3. ★ **The sale fill acts on the binding suppliers** (each buyer's cheapest landed seller, at the CES seller's own
   slope over its share of that buyer's purchases, capped at the buyer's use; the purchase fill alike), not on each
   buyer's own slope as the study's prototype did. WN0's file gives every maker of a good one base-year unit value, so
   every buyer's price is set by one supplier; with the buyer's slope the step missed a re-solve by up to 34%.
4. ★ **A Halifax city's first buyer is its own host**, Canada, not the United States (the study's wood case), because
   of that one unit value. The US joins the lumber fill at 2.0% of world use, Mexico 9.2%, Portugal 10.2% (study:
   Canada 4.8%, Portugal 5.6%, Mexico 10.3%). **Recommended:** each seller's own unit value from BACI's tonnes at
   the next re-bake (pending, WN3-D).
5. ★ **A city moves its price as big as it is.** At 0.1% of world use a Halifax city moves its netback by up to 5.2%
   of the price (Canada's market is small for most goods; the re-solve agrees to 0.03%); the check asserts an
   ordinary city, 0.01% of world use, under 1% (worst 0.52%), and a tenth at least 5% (least 19%).
6. ★ **D23's stand-in: a coastal nation drawn by base-year output, from the ground's seed** (not uniformly; not the
   path's). A uniform draw lands on a remote island one time in five; the ground's seed keeps a city's place when its
   path is redrawn (WN1's ★1). The default founding (seed 4127) stands at **New York**. **Recommended: yes.**
7. ★ **The lorries' freight in a world-priced band shrinks with the band** (`GoodsMarket.bandFreight()`): freight's
   moves took more than a narrow world band held, and WOOD_PELLETS' floor stood over its ceiling (the playtest's
   month 1399). Shrunk in proportion, worldMargin() + freightInBand() is still the band; the railway's own invoice
   stays on the lorry rate.
8. ★ **A good draws its series' price shock** (`Nations.shockOf`, a fix to WN1): each good drew its own after 2025
   (and on Random from 2000), so a chain on one Pink Sheet series drifted apart; anode went dearer than its cathode in
   2035, a refinery imported anode at a loss, and the playtest's GDP went below zero 14 months (422-637).
9. ★ **The band reads the trade's own record**, not the clearing's tally (pre-clearing draws were wiped before it).
10. ★ **The freight scale carries the literal margin**: 0.7-10 for bulk, 10-90 for processed, 115-217 for made goods
    (the literal bands are much wider than real freight). Noted; WN4's moneys do not touch it.
11. **Not done here:** the foreign accounts' own market seed (`marketSeedFor`) still uses the ground's seed; LandMarket's
    ground resources still at the literal export price (WN5's); the basket's weights and other reference figures on
    the literals (Consumption, ShadowBasket, OilView, Haulage, LuxuryRetail, Founding, EconomyManager's constant).

## 3. The founding balance moves (§7.1, D13): Jerus chose Random (WN3-B)

**His word** (2026-10-29 morning, shown the three ways measured overnight, below): "(2a) Random by default", the
option worded "A new world defaults to the Random path instead of Historical". So `Nations.DEFAULT_PATH` is Random,
on the Earth and on a generated world alike: most new players found on the Earth (the founding screen opens there
when its data is here), and the change is to keep them out of the trap below. Historical stays the founding
screen's first chip on both pages. SaveFileCheck's two off-default fixtures found on whichever path is not the
default (`OTHER_PATH`); nothing else in the tree named the default.

**What happens on Historical.** The world starts in January 2000 at that year's real prices and replays 2000-2025
(D13, on the study's recommendation; §7.1 warned that the founding balance moves). The 2000s commodity boom comes
into a young city through what it imports - food, fuel, metals - several-fold by 2011:

| the default playtest | m72 (2006) | m96 (2008) | m120 (2010) | m144 (2012) |
|---|---:|---:|---:|---:|
| price index, WN3 / WN2 (0.8.69) | 1.41 / 1.06 | 2.01 / 1.01 | 3.10 / 1.01 | 4.38 / 1.17 |
| D$ a US$, WN3 / WN2 | 1.04 / 0.99 | 1.58 / 1.03 | 2.75 / 0.93 | 3.10 / 0.96 |
| the treasury, WN3 | in funds | **runs dry** | short | short, borrowing at 28.7% |

The default city then stood at about **5,000 people from m540 to m1260 (60 years)**, where WN2's grew to 24,000.

**The three ways, measured overnight** (the default playtest; the ensemble's AUTO2 policy over its 16 seeds, final
population at m2400):

| | the default playtest | 16 seeds: median, lowest | reach 40,000 |
|---|---|---:|---:|
| main (0.8.68 / 0.8.69) | 544,112, no plateau | 255,250, 208,401 | 16/16 |
| (1) Historical, as built | 583,738; ~5,000 people m540-m1260 | 304,036, **92,624** (seed 0) | 16/16 (seed 0 at m1886) |
| **(2a) Random by default: taken** | 655,152; ~3,700 m266-m601 | 247,608, **4,015** (seed 13) | 15/16 |
| (2b) the world's band eased in over the first 25 years | 741,303, no plateau | 301,005, 251,596 | 16/16 |

(2a) as built, on 0.8.71: first 247,608 and seed 13 at 4,015, as measured overnight; with THE ORDER (5 item 7) the
AUTO2 median is 257,991 and seed 13 ends 4,020, and the default playtest 719,951 with its plateau m266-m601 (4).

**Seed 13: the trap is the central bank's window, on either path.** A young treasury runs dry about m94 (2007-2008)
in most seeds, whichever path, and its bank goes to the central bank's window for its funding. A city stalls while its
bank lives on the window and grows once it leaves (the ensemble's AUTO2 runs; "on the window" = advances over 1,000):

| | treasury dry | on the window | people at m600 / m1200 / m2400 | prices at m600 (D$ a US$) |
|---|---|---|---:|---:|
| seed 13, Random | m93 | **m94 to the end, never leaving** (2,307 months) | 3,219 / 3,845 / **4,015** | 20.2 (14.9) |
| seed 13, Historical | m97 | m98-m474, four spells (193 months) | 10,282 / 24,180 / 232,320 | 5.1 (3.4) |
| seed 0, Random | m124 | m147-m1317 | 3,653 / 3,704 / 118,469 | (3.9) |
| seed 0, Historical | m94 | m95-m1381 | 3,767 / 3,932 / 92,624 | (6.3) |
| seed 2, Random | m94 | m95-m1154 | 4,098 / 7,517 / 135,760 | (6.1) |
| seed 10, Random | never | never | 9,078 / 42,387 / 110,605 | (2.1) |

On seed 13's Random run the bank's advances grow from 6,122 at m96 to 444,694 at m192 (the city has 2,757 people),
the money base is the advances, the D$ falls from 1.34 to 21.6 a US$ by m576 and prices rise 6-10% a year for forty
years (the index 402.7 at the end); hunger reaches 0.53 at m96, unemployment 12% from m288. Its Historical twin
borrowed 774,309 at m144, repaid the window to nothing, and grew. On the literals no seed is trapped (main's lowest is
208,401): the world's dearer imports put the young treasury there, and Random's own swings (its 2000 prices reverting
to 2025's) do it as Historical's boom does. So (2a) moved the trap from seed 0 to seed 13; it did not remove it.

A second strand: **the calibration loads each literal band's whole margin onto freight** (★10), so a made good's
freight scale is 115-217x real freight and where a city stands moves its band too much (at New York in 2000
fabricated steel exports at 1.94x its literal while steel is 0.43x, and wood pellets 1.95x).

**Recommendation for WN3-B** (pending, Jerus's): (2b)'s easing on both paths - each month's band the literal plus
w x (the world's - the literal), w = months since founding / 300 - measured first on Random's 16 seeds, since on
Historical it kept every seed off the trap (lowest 251,596); the patch of the experiment is in the orchestrator's
notes folder. If seed 13 still stalls, the window itself for the monetary lane: why a bank that went to the window in
its first decade never leaves it. The freight scale is studied before WN4.

## 3b. The policy rate on the band: Jerus's call (R3, MonetaryCheck's dial)

**Jerus chose (c), 2026-10-29: "WN3 yes ship, ill look into it later".** WN3 was held for this call (the orchestrator:
a premise red is Jerus's call, never recorded away; HD1 met the same question today, its coupon inverting
MonetaryCheck's inflation-with-rate relation). It ships with R3 red, the gate passing the suite's two model reds on his
words (4), and the study queued as WN3-R, one study with HD1-C's coupon.

**What the band does to the rate.** A higher policy rate does two things to the city's prices. It strengthens the D$,
so what the city imports costs less: that pulls inflation down. And it raises what a landlord pays to finance a home,
and rent (0.62 of the basket) follows that cost up: that pushes inflation up. On the literals the pull wins. On the
world's band the city imports more of what it eats and builds, its prices swing with the world's, and in some cities
the push wins: a dial set higher gives *more* inflation, not less.

**R3** (the ensemble's HIGH policy, a 15% target from m600; its inflation m660-m720 against its AUTO2 twin at 2%; the
premise: at most 2 seeds of 16 more than 1 point above the twin). On the final model: median **-2.72 points**, but
**4 seeds above** - in every one of them rent ended above the twin's though the D$ was the stronger:

| seed | HIGH minus twin | HIGH's inflation / the twin's | at m720, HIGH over the twin: rent, D$ a US$ |
|---:|---:|---:|---:|
| 4 | **+4.04** | 1.99% / -2.05% | rent +35%, D$ -20% |
| 7 | **+1.07** | 2.46% / 1.39% | rent +12%, D$ -27% |
| 9 | **+3.58** | 3.21% / -0.37% | rent +24%, D$ -20% |
| 11 | **+3.75** | 3.53% / -0.22% | rent +66%, D$ -18% |
| the other 12 | -0.12 to -6.70 | | rent -9% to -36% but seed 6 (+20%), D$ -30% to -62% |

The count: 2 on main (0.8.68, the literals), 6 on Historical (last night), 2 on Random before THE ORDER, 4 after it
(THE ORDER moved seeds 4, 9 and 11, three of its six, from m425). The twins of the four sit near or below zero in the
window, so the gap is the twin's calm as much as HIGH's heat; but the sign is the rent's.

**MonetaryCheck's dial** (the same premise in a harness): its held runs at inflation dials of 3%, 10%, 20% and 40%
give 10.43 / 9.68 / 8.99 / 8.82% on the literals (the dial holds inflation down, as asserted) and 4.23 / 6.55 / 5.96 /
5.75% on the band (rent 1.19 to 1.32 of its start from 3% to 10%). Last night MonetaryCheck, PassThroughCheck and
CurveCheck were put on the literals (`playOnLiterals()`) to pass; that keeps them green and leaves the band untested
by them - the same premise, recorded the other way.

**The choices:**
- **(a) Ship, and record R3 as the band's monetary effect**: R3 and the three harnesses on the literals named as
  known (the gate's `--red-ok` with his words for R3). Fastest; the premise stays broken on the band.
- **(b) A study of monetary policy on the band first, WN3 held**: what rent should do when the rate rises (the
  landlords' financing cost passed on, against the demand a dear mortgage takes away), how strong the D$'s pull is on
  an import-heavy city; then the fix, and the three harnesses back on the band. Slowest: the market, (2a) and THE
  ORDER wait for it.
- **(c) Ship on his word with the premise reds named, and queue the study as the next batch of the monetary lane**
  (WN3-R): the market and its fixes reach main now, and the premise is restored by a mechanism the study chooses,
  the three harnesses going back on the band in that batch - the red is a debt with a row, not a record.

**Recommendation: (c).** The channel predates WN3 (main shows it in 2 seeds); the band makes it visible, it holds in
the median, and WN3's other work (the market, the default path, THE ORDER's fix of every works' orders) should not
wait on it. If Jerus would rather no premise shipped red, (b).

## 4. The checks

**The final tip** (fb639039: main 0.8.74 merged - BK1, HD1, EM1 - and the M7 mends), both files named: **the full
suite exits 4, two NEW reds, both WN3-F** (5 item 10, shipped on Jerus's word, the fix queued as FL1): SlopeCheck's Rod Mill, and
LandmassCheck's Rotterdam run, which meets the same fault m962-m968 on the merged path (after the January re-solve
cathode's import price 79.8 to 120.1, rod's export price 97.5 to 70.7; Copper Refining imports 10.1M of cathode and
exports 5.3M of rod a month until it cuts back; GDP -997k at m962). SectorStatementCheck is green (5 item 9).
LongPlaytest finds nothing. 3 timing lines soft, Stale no firm finding. **M7**: the merge turned nine harnesses red
that were green on both sides; eight fixtures were mended to cause their conditions again (CreditCheck's Note sized to
half a year of tax; CurrencyRegimeCheck 10's candidates given the world's appetite for the city's paper, EM1's buybacks
abroad emptying the peg's vault a month early; SaveFileCheck waiting for hot money to arrive; EducationCheck's hand-sum
carrying every line of the revenue total, EM1's fund transfer among them; HealthCheck's drain keeping the bank shut for
the window, a fixture that had never caused its condition, 52.33 against 52.61 before; CarCheck holding Freight, a
railway now carrying the town's petrol; MapCheck relisting the pick it holds and warming its played city past its first
view change; LandmassCheck filling the island in the month it links, EM1's city having no free ground left at m504).
The ninth was a model fault, fixed at its cause: `DebtManager.debtAfterProceedsOf()` left out the indexation a linker
had accrued, which `getPricedDebt()` counts (CD5), so a linker sale was priced rich and FloatLinkCheck 7's round trip
came out 20.48 ahead (latent on main, where the fee hid it; -12.72 now). No assertion moved. **The ensemble rerun
on the merged tip** (d039d33f, scored against EM1's 0.8.74; `docs/ensemble/WN3.md`): **23 of 26** (22 before the
merge; EM1 alone 23 of 25). AUTO2 median 292,982 (EM1's 261,977), T1 16 of 16 (m874-m1,154), F1 clean; A2 and S1 as
on main; A3 passes now; **R3 red in 3 seeds** (median -1.12): seeds 3, 4 and 10 at +2.16, +1.40 and +1.05 points, rent
+42% and +111% over the twin in 3 and 4 (the D$ 17% the stronger and level), seed 10's rent 10% under it (the D$ 42%
the stronger) - two of the three are 3b's channel. Seed 13 grows (237,445 at m2400) and never draws on the central
bank's window; 8 AUTO2 seeds still draw on it for a while (seed 1 for 297 months, m242-m638), none trapped (WN3-B).

Before them:

On the branch's tip (018f7761: main 0.8.71 merged, (2a), THE ORDER, the mends), earth-2.bin and world-1.bin named:

- **The full suite** (`./batch-check.sh`): **exit 4, two NEW reds, both model faults** left for Jerus's word (5 items
  9 and 10): SlopeCheck (a works making a flow good runs at a loss: negative GDP m834-m1837 on its city) and
  SectorStatementCheck (the bank's equity after a deep reopening, 2,444 at m284). Every other harness green.
  **Rechecked after BK1 (main 0.8.72) was merged:** SectorStatementCheck is still red on the same two lines, but its
  miss is far smaller: BK1's re-strike clears the reopening's 2,444; 223 of 479 bank months are still off by more than
  a cent, the worst 5.40 at m263 (5 item 9).
  **LongPlaytest finds nothing** (on Random before THE ORDER it had one layout finding, m2010; Historical's two are
  that path's, 5 item 1). 4 timing lines soft (SaveSlotCheck, MapCheck 2, KeptCheck), Stale no firm finding.
- **The mends.** Last night 32 harnesses were mended to cause their conditions on Historical's band (M1-M4); on
  Random today 14 more (M5: Credit, PolicyPreview, Call, FloatLink, Money, Bank, Bond; M6: Land, Zoning, Labour,
  Education, FoodProcessing, Refinery; and SaveFileCheck's two fixtures). No assertion moved. Where the premise is a
  price or a margin the harness names, its goods are set to their literal band or its city plays on the literals
  (LandCheck's iron, ZoningCheck's farm goods, FoodProcessingCheck's Works, RefineryCheck's section 17); where a
  town's planner would build what the harness is not about, the sector is held (5 item 8).
- **The premises** (3b, Jerus's): the ensemble's R3, and MonetaryCheck, PassThroughCheck and CurveCheck, which play
  on the literals since last night.
- **No world file**: the default playtest is main's (0.8.71, built apart) to the byte to month 1608; from m1609 THE
  ORDER moves it (a works' order held to what it makes, on the literals too): it ends at 622,722 against 599,734, no
  finding.
- **With the file** the default playtest stands at about 3,700-3,840 people from m266 to m601, then grows: 35,037 at
  m1302, 148,606 at m2006, **719,951 at m4005** (main 599,734 on the literals; Historical as built 583,738).
- **The ensemble** (`docs/ensemble/WN3.md`, 80/80 on 48c92296; the harness mends after it read no model figure):
  **22 of 26** against 0.8.68. F1 passes (no LongPlaytest finding in any run; 6 of 16 seeds read negative GDP before
  THE ORDER). AUTO2 median 257,991 (main 255,250); T1 15/16 at m1,139 (seed 13 never reaches 40,000, 3); seeds 4 and
  5 end 258,301 and 292,003 (CC1-S's stall seeds; 298,736 and 325,523 before THE ORDER); A2 and S1 fail as on main; A3 fails as it did before THE ORDER (one seed's
  13-month deflation spell); **R3 newly fails** (4 seeds over +1 point; 3b).

## 5. Found on the way

1. **Three map faults the market's city exposes** (for MF1, the map lane; LongPlaytest's two layout findings, red
   with the world file named and not without: the literal city has one depot and one old works and never meets them):
   (1) the city's one rail yard is dealt to the first district in map order holding a Rail Depot (`CityMap.yardsOf`,
   L5761), so a depot arriving earlier in the order moves the yard and its old district's depot becomes a drawn box at
   its dated log place (m3995, district (-2,-3): 10 boxes moved); (2) ground bought inside a built district moves old
   works (m2078, district (0,0): 112 plots bought, a Rod Mill, a Meat Works and a Bottling Plant each lost a standing
   box; again m2323); (3) the undated interchange reserve (`FIXED_RESERVE`, 335 plots) laid across standing works
   (m2782, district (-1,0): 41 of 65 Construction Depots moved). The watcher is not weakened. Found on
   Historical and still there after MF1 (0.8.70); on the default path (Random) after THE ORDER LongPlaytest meets none
   of them, so they wait latent for a map batch.
2. **A dormant load-order fault in old-save code**: at a load `inferContracts` runs before the wage rates are struck,
   so a House's labour is read unindexed (26.600 against 26.673). InvestCheck's older-save cities now play on the
   literals; the fault is left.
3. **The calibration's freight scale** (3, ★10): wood pellets at 1.95x their literal in 2000 at New York and fabricated
   steel at 1.94x while steel is 0.43x; every default-played town raises a Wood Pellet Mill by m90.
4. `GoodsMarket.bandFreight()` is public now (`TradeBand.of` reads it: the ships' piece of the band's own freight).
5. **The monetary premise on the world's band** (for Jerus, with WN3-B): MonetaryCheck's held runs at inflation dials of
   3% to 40% give 10.43 / 9.68 / 8.99 / 8.82% on the literals (the dial holds inflation down, as asserted) and
   4.23 / 6.55 / 5.96 / 5.75% on the band: the founding is calmer, but rent (weight 0.62 of the basket) rises with the
   cost of financing, 1.19 to 1.32 from 3% to 10%, outweighing the stronger D$. MonetaryCheck, PassThroughCheck (the
   twins' D$ gap 34% at m120 on the band) and CurveCheck (a cement works' loan sends the young bank to the window)
   play on the literals; whether that cost-push is the game Jerus wants is his.
6. ExchangeRateCheck's playtest treasury ends m360 at cash −18,397 on 201,258 of debt on the band: the young city's trap
   again (3); its fixture hands the treasury the cash the hand deal needs.

7. **A works bought inputs for output it would not make: THE ORDER (Sector, fixed here).** On (2a)'s first ensemble 6
   of 16 seeds (4, 5, 7, 9, 11, 15) read "negative monthly GDP" in months 425-459, under four policies (F1). Probed on
   seeds 7, 4 and 15: the city had built an Electrolytic Refinery on imported anode (Copper Refining's own header: -1.9%
   of its cost a month even on the literals); by 2035 cathode sold under what a unit cost (20.57 against an export price
   of 18.73), so `offer()` withheld the whole shed (23,280 t at the dump line), `produceStock()` made nothing - and
   `Sector.bid()` still ordered the anode at the operating rate, a flow good consumed on the spot: 114,000-242,000 a
   month of imports with nothing booked against them, GDP -30,000 to -155,000 against a C of about 20,000. A smelter's
   anode, captive to an idle refinery's need, went the same way. **The fix** (a Sonnet slice, reviewed): before the
   market `planOrders()` fixes each stockable good's order share from the rules `offer()`, `getPlannedOutput()` and
   `getExportBoundOutput()` apply at the stock the shed should hold after its sales; `bid()`, the captive need and
   `produceFlow()`'s captive order for each building's use times its share; `produceStock()` makes what was ordered
   for, a forecast that missed high held in the shed (written off past its room, as ever). A works' inputs are costed
   at the landed price, not a price struck in a month nobody bid (otherwise a works that stopped ordering found its
   input cheap and ran every other month). `Refining` is exempt: its crude is the run's (THE RUN). The fixture is
   `CopperRefiningCheck` section 8 (the found case, the shared pool, the normal month unchanged, the month's GDP): six
   lines red on the unfixed model, green after. Left: an idle works still buys its consumables at its rate (about 540
   a month in the fixture) and a scrap refinery its old copper. **The call is the orchestrator's** (2026-10-29, 07:55),
   against my recommendation to ship (2a) and queue the fix as its own batch, because F1 was a new ensemble red, in
   the new default.

8. **The young town orders works it cannot build or run** (M5, on Random's 2000 prices): CreditCheck's, BankCheck's
   and PolicyPreviewCheck's towns borrow for a Lime Kiln the crews take 45 months to open ($53M by m13, $343k a month
   of interest, defaults m48-m56) or a Panel Mill and Pellet Mill whose sheds sit full while the sector loses
   $450-850k a month (defaults m104-m107, $139M written off against $35M of the bank's equity); the planner's estimate
   does not see the build time or the band moving. Those harnesses hold the sector (`holdSector`) to keep their own
   subject; the bank surviving a big sector's default is now tested nowhere - a harness for the young-town batch.
9. **The bank's equity does not add up after a deep reopening** (SectorStatementCheck, red; a model fault, left):
   `Debt.unaccretedOnPaid()` (Debt.java L346) spreads a line's whole unaccreted discount over the bank's face pro rata,
   so when a line is reopened at a far deeper discount than its first sale the bank's unearned discount is overstated,
   and `Game.refreshBank()` puts it straight into equity. At m138 the 20-year (sold m126 at 2.1%) was reopened for
   59,100 of face at 24%; the bank took 40% of the new part against 78.8% of the old; its unearned discount read 8,952
   against about 6,550, and paid-in + retained + preferred misses equity by 2,444 at m284. It needs the discount kept
   per tranche or per holder: a finance batch. Not new in kind; the band's deeper discounts make it show.
   **BK1 (0.8.72) found the same cause and booked the re-blend to retained earnings as a named cause**
   (`Game.restrikeBankDiscount()`, at the settle and the central bank's add-on). Merged here, it clears the 2,444, but
   the harness stays red on its two lines: 223 of 479 bank months off by more than a cent, the worst 5.40 at m263
   (234 months and -2,444.78 at m284 before). What moves the rest is not traced: perhaps an event that grows a line's
   pool besides BK1's two. SectorStatementCheck is green on main, so it is reached only on the band.
   **Green after main 0.8.73-0.8.74 (HD1, EM1) was merged**: 0 of 479 bank months off; what cleared the 5.40 is not
   traced. No row left.
10. **A works making a flow good runs at a loss** (SlopeCheck, red; a model fault, left): THE ORDER holds a works'
   order for what it makes of a *stockable* good; a Rod Mill makes rod, a flow good, made at the rate whatever it
   fetches. On SlopeCheck's city from m834 Copper Refining imports 163,302 t of cathode at 347.18 and exports 160,728 t
   of rod at 314.59 - a works whose product sells under its input: GDP -1,219,708 at m834, 49 negative months to
   m1837. The fix is THE ORDER's for flow goods (a flow good's line idles when its net export price is under its
   marginal cost, as `getExportBoundOutput()` already rules for a stockable good's spare) - a model change, for
   Jerus's word with 3b. After main 0.8.74 was merged LandmassCheck's Rotterdam run meets it too (m962-m968, 4 above):
   both reds shipped on his word (2026-10-30, asked whether WN3 ships with them known: "Ship, fix next
   (Recommended)"), the fix queued as FL1 beside HD2, built on WN3 (`docs/queue/FL1.md`).

## 6. For the next batches

- WN4: the band is in world (US) dollars a game unit today; the moneys put it in each nation's money.
- WN6: `PartnerTrade`'s snapshot and the History by partner and good are the Partners page's figures;
  `WorldMarket.millPrice/buyerPrice` the export curve's.
- `Game.playOnLiterals()`: a harness whose premise is a fixed world price builds its city on the literals.

## 7. One in, one out

Not a process batch: no script, check machinery or session rule changed.

## 8. How it was built

Slices on Sonnet 5.5 at xhigh, each in a worktree of its own where they ran side by side: A the model and its check
(and three rounds on the step's slope), C the lanes, B the wiring and the save, M1-M3 the harnesses' fixtures. Opus
reviewed each diff and wrote the bounded freight, the bounded ceiling, the shared price shocks, the default seed's
dollar and `playOnLiterals()`.
