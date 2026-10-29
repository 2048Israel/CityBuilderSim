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
  game plays on the literals exactly** (the default playtest's eight trace files byte-identical to WN2's).
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

## 3. The founding balance moves (§7.1, D13): Jerus's call (WN3-B)

**WN3 is held for this** (the orchestrator, 2026-10-29 02:45: the default playtest is what a new player on itch.io
would see). The ship file goes on after Jerus's word; the branch is ready for (1) as it stands.

**What happens.** On the Historical path the world starts in January 2000 at that year's real prices and replays
2000-2025 (D13, taken on the study's recommendation; §7.1 warned that the founding balance moves). The 2000s
commodity boom then comes into a young city through what it imports - food, fuel, metals - several-fold by 2011:

| the default playtest | m72 (2006) | m96 (2008) | m120 (2010) | m144 (2012) |
|---|---:|---:|---:|---:|
| price index, WN3 / WN2 (0.8.69) | 1.41 / 1.06 | 2.01 / 1.01 | 3.10 / 1.01 | 4.38 / 1.17 |
| D$ a US$, WN3 / WN2 | 1.04 / 0.99 | 1.58 / 1.03 | 2.75 / 0.93 | 3.10 / 0.96 |
| the treasury, WN3 | in funds | **runs dry** | short | short, borrowing at 28.7% |

The treasury never recovers from it while the city is small: its debt grows from 57k to 16M and the city stands at
about **5,000 people from m540 to m1260 (60 years)**, where WN2's grows to 24,000. It then grows past WN2's and ends
at 583,738 (WN2 544,112). Ensemble seed 0 falls into the same trap (index 2.34 at m120 against 1.05 without the
world file; 40,000 people only at m1886; ends 92,624); the same classes without the world file give it 274,007.

A second strand, for the same batch: **the calibration loads each literal band's whole margin onto freight** (★10),
so a made good's freight scale is 115-217x real freight and where a city stands moves its band too much: at New York
(the default ground's stand-in) in 2000, fabricated steel exports at 1.94x its literal while steel is 0.43x, and wood
pellets 1.95x - a fabrication works and a pellet mill pay far better than the game was balanced for.

**The choice**, each measured tonight (the default playtest; the ensemble's AUTO2 policy over its 16 seeds, final
population at m2400):

| | the default playtest | 16 seeds: median, lowest | reach 40,000 |
|---|---|---:|---:|
| main (0.8.68 / 0.8.69) | 544,112, no plateau | 255,250, 208,401 | 16/16 |
| **(1) ship as is**, WN3-B pending | 583,738; ~5,000 people m540-m1260 | 304,036, **92,624** (seed 0) | 16/16 (seed 0 at m1886) |
| **(2a)** a generated world's default path Random | 655,152; ~3,700 m266-m601; one layout finding | 247,608, **4,015** (seed 13) | 15/16 |
| **(2b)** the world's band eased in over the first 25 years | **741,303**, no plateau, no finding | **301,005, 251,596** | 16/16 |

- **(1)** is D13 as written. Its cost is the young city's trap: one seed in 16, and the default city's 60 years.
- **(2a)** removes the replayed boom but Random draws its own swings, from 2000's prices reverting to 2025's: it is
  worse (one seed never grows, two others end near 110,000).
- **(2b)** keeps the Historical world and lets a young city meet it gradually: each month's band is the literal plus
  w x (the world's - the literal), w = months since founding / 300 (25 years: the city reaches the world fully in
  2025, where the calibration makes the world and the literals meet at Halifax). The boom reaches it at about 40% at
  its 2008 peak. Measured on an experimental build (a property, not committed): ~15 lines in `Markets.setWorldBand`
  and `Game`, a dial, a WorldMarketCheck section, the ensemble again.

The harnesses saw the same trap: LongPlaytest's test player with the AutoBuilder on (AutoBuildCheck's town) sits at
about 2,000 people from m84 and passes its 240% debt limit by m204 (on the literals: 21,729 people at m600).

**Recommendation: (2b)**, with the margin strand (the freight scale) studied before WN4: it keeps D13's world, keeps
today's founding balance for a new city, and was the best of the three on every figure here. It is a change beyond
the study, so it is Jerus's.

## 4. The checks

- **The full suite** (`./batch-check.sh`, earth-2.bin and world-1.bin named): **exit 4: one NEW red, LongPlaytest's two layout findings** (5 item 1: map faults the market's larger
  city exposes, still there on main 0.8.70 after MF1: "a building on the map moved" m3989-m3995, "an old works moved"
  m4000), every other harness green; 3 timing lines soft (SaveSlotCheck, MapCheck, KeptCheck), Stale no firm finding. Before the harnesses were
  mended it was 36 harnesses and 169 red lines; after the model's fixes (3, ★7-8) 32 and 149. Each was mended to cause
  its condition on the world's band, no assertion moved (four slices, M1-M4); where the premise is a fixed world price
  the harness's city plays on the literals (`Game.playOnLiterals()`): RefineryCheck, OilCheck, ManufacturingCheck,
  ScaleCheck (a big city moves its own prices on purpose), LabourCheck, InvestCheck's older saves, AutoBuildCheck,
  CurveCheck, MonetaryCheck and PassThroughCheck (5 item 5). Two gates were corrected, not loosened: SectorBooksCheck's
  imports identity now counts the named imported services (Consumables, the shuttle tankers: imports with no good,
  MaterialsCheck's own identity), and PlanCheck counts homes cells by place, not by sample (its 1-in-59,049 premise is
  of distinct cells). FloatLinkCheck reads the businesses' coupons at the settle (they agree to the bit a month on).
- **With no world file the game is main's**: the default playtest traced from the final classes, its eight CSVs
  byte-identical to main's (27dc925f, built apart), its log but for the run-time line.
- **The trace with the file** moves: §3's table (the default city's plateau m540-m1260, then 583,738 against 544,112).
- **The ensemble** (`docs/ensemble/WN3.md`, 80/80 on 8aba2e2d; the merged harness mends since read no model figure):
  23 of 26 against 0.8.68. Final population up under every policy (AUTO2 304,036 against 255,250); 16/16 reach 40,000
  (m1,122 against m1,222); no playtest finding; CC1-S's seeds 4 and 5 end 298,907 and 309,166. A2 FAIL 10 [2 to 12]
  (0.8.68 FAIL [3 to 31]); S1 FAIL, a 64-month spell (0.8.68 80); **R3 newly FAIL**, 6 HIGH seeds over +1 point (0.8.68
  2); **seed 0 trapped** (§3). For (2b) the ensemble must be run again on its build.

## 5. Found on the way

1. **Three map faults the market's city exposes** (for MF1, the map lane; LongPlaytest's two layout findings, red
   with the world file named and not without: the literal city has one depot and one old works and never meets them):
   (1) the city's one rail yard is dealt to the first district in map order holding a Rail Depot (`CityMap.yardsOf`,
   L5761), so a depot arriving earlier in the order moves the yard and its old district's depot becomes a drawn box at
   its dated log place (m3995, district (-2,-3): 10 boxes moved); (2) ground bought inside a built district moves old
   works (m2078, district (0,0): 112 plots bought, a Rod Mill, a Meat Works and a Bottling Plant each lost a standing
   box; again m2323); (3) the undated interchange reserve (`FIXED_RESERVE`, 335 plots) laid across standing works
   (m2782, district (-1,0): 41 of 65 Construction Depots moved). The watcher is not weakened. They are LongPlaytest's
   only red, and still there after MF1 (0.8.70): a gate passes them only on Jerus's word, or after a map batch mends them.
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
