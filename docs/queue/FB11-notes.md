# FB11 — notes: held towns, and the advisor's land funding

Builder: Claude (Opus), headless, branch `batch/FB11` cut from main 7b30fe1f8 (0.9.206, save
format 66). Rows: RE3-J, ER1-F and FB11H ([FB11](FB11.md), [FB11H](FB11H.md)).

## 1. Re-measured on 0.9.206 first: which findings still stand

Every finding in the briefs was measured on today's city before R1g made the 1900 founding the
default. Each was measured again on 0.9.206 (7b30fe1f8), the 1900 default and today's city
(`-Dplaytest.founded=today`).

**The ensemble's seeds** (RE3-J's 3, 11 and 13; FB11H's 14): `tools/ensemble/ensemble.sh` on the
gate box, AUTO2, `SEEDS="3 11 13 14"`, with `-Dplaytest.groundTrace=380-470`, labels
`FB11-m1900` and `FB11-mtoday` (runs in `target/ensemble/`, not committed). The ensemble names
the data files since EN2 (0.9.206's baseline does), so "unnamed", as RE3 ran it, is no longer
how the ensemble runs a seed; these are named. Population at m600 / m1000 / m1500 / m2000 /
m2400, and the longest stay in RE3-J's 25,000-33,000 band:

| seed | 1900 default | months in 25-33k | today's city | months in 25-33k |
|---|---|---:|---|---:|
| 3 | 188,860 / 948,292 / 2,895,776 / 5,517,399 / 7,383,168 | 17 | 584,115 / 2,020,668 / 4,595,156 / 7,213,032 / 9,764,806 | 25 |
| 11 | 91,099 / 686,982 / 2,225,330 / 3,661,701 / 6,059,890 | 33 | 482,820 / 1,804,306 / 4,029,552 / 6,205,105 / 8,274,124 | 23 |
| 13 | 91,787 / 1,169,002 / 3,044,977 / 4,750,463 / 7,307,737 | 21 | 655,741 / 2,198,640 / 4,243,292 / 7,194,688 / 9,370,682 | 19 |
| 14 | 246,235 / 1,435,950 / 3,544,055 / 5,290,035 / 7,935,948 | 20 | 662,858 / 1,960,465 / 4,285,620 / 7,408,277 / 9,386,979 | 28 |

No run has a finding (F1), and none has more than 1 household unplaced in m380-470. The ground
trace for m380-470: on the 1900 default the cash share held 9 of 20 looks in seed 14 and none in
the others, and the landlords never waited on ground; on today's city it held 17-20 of 19-20
looks in every seed, the landlords waiting on ground 6-18 months of 91 (seed 11, m381: cash
20,910, offer 15,142, the share 2,091).

**Rotterdam** (ER1-F; `LongPlaytest --earth rotterdam`, on the laptop, both data files named,
the eight trace files under `target/fb11/`, not committed):

- 1900 default, run to m3013 and stopped there (the city past 2 million): 30,044 people at m1000,
  36,192 at m1200, 65,809 at m1400, 581,802 at m2400. Its jobs stood still for 112 months from
  m1079. Traced m900-1300 (a second run): the ground full and the landlords waiting on it
  283 months of 401; homes 13,278 from m1100 to m1180; 68 of 76 looks held by the share, with
  up to 393,826 in the treasury.
- Today's city, traced from m600 and stopped at m1457: 32,918 people at m600, 35,451 at m800,
  39,875 at m1000, 51,794 at m1200, 75,432 at m1400; jobs still for 141 months from m676. From
  m600 to m1400 the share held 155 of 156 looks and bought nothing (m790: cash 260,176, the best
  offer 703,740, the share 26,018), and the landlords waited on ground 532 months of 801.

**Which findings stand:**

- **RE3-J: gone. It leaves the bundle.** No seed holds: 3, 11 and 13 pass through 25,000-33,000
  in 17 to 33 months on either founding and finish at 6 to 10 million. There is no held town to
  study, so no study note was written; this section is the evidence. The orchestrator files the
  row.
- **FB11H: its finding is gone. It leaves the bundle.** Seed 14 has no finding at m456 or
  anywhere on either founding, and no household was unplaced in m380-470. Its class, the share
  holding demanded ground, still stands on today's city and is ER1-F's, repaired below; its proof
  requirements (the funded purchase and its money, a debt-service-refused control, nothing
  clipped or given) are ER1Check's sections. HT1's .183 snapshot was not replayed: its source is
  not 0.9.206. The orchestrator files the row.
- **ER1-F: stands, on both foundings.** Rotterdam is held for land with the cash share stopping
  every look: today's city from about m600 to m1000 at 33,000-40,000 (ER1's 33,000), the 1900
  city for about a hundred months at 30,000-36,000. By the brief's test this is a defect, not a
  real town's stall: the land is for sale, a sector is waiting on it, and the city could pay for
  it on the land office's own bond; only the player's cash-only rule refuses.

## 2. What was built (ER1-F)

`LongPlaytest` only; `LandMarket` and `Game` read, not changed.

- ★ **`fundGround()`, the funded route.** When a look's best offer is past what is left of the
  cash share (or past the cash), and a sector waits on ground (`groundDemanded()`), the player
  buys the whole offer anyway: at most the share out of the cash, the rest raised first on the
  funding page's `Game.BUILD_BOND_YEARS` bond (`handleLongBondForCash(gap, BUILD_BOND_YEARS,
  fundingLot("Term"))`, the bond `buyWhole()` and the land office's funding page use), when the
  player's own test for borrowing, `canService()` (`DEBT_SERVICE_LIMIT`), carries the gap. An
  overdrawn treasury's overdraft is in the gap, as `buyWhole()`'s `Game.landCashGap()` counts it
  (REV-FB11's P1, section 8). Not
  when the land is paid from the vault (`buyWhole()`'s rule), and nothing when no sector waits on
  ground. If the bond raised less than the gap (by more than `FUNDED_SLACK` of the price), or the
  office then refuses the offer, the money raised stays in the treasury, as `buyWhole()` leaves
  it, counted in `groundBondsUnspent` and `groundBondsUnspentRaised` (the run's summary prints
  them). Recommended way, ER1's: the alternative, cash-only buying with the funding left to the
  player's page, keeps the hold this batch is for.
- ★ **"Demanded" is the model's own record**: `Game.getLandBlockedSectors()`, the sectors whose
  plan or the landlords' order the month refused for want of ground, which the inbox's "nowhere
  to build" notice reads; at a look it holds the last month's. Not "past the line" alone: that
  would borrow for ground nobody has asked for.
- **`keepGroundAhead()`** calls it where the share stopped the look; a funded offer spends the
  share by the part the cash paid, a cash offer by its price as before. Counted:
  `groundAheadFunded`, `groundAheadBorrowed`; the run's summary prints them.
- **The room-to-grow move** (`advise()`): when the cash cannot buy its offer, the same route with
  a tenth of the cash (`roomMovesFunded`). When the cash covers it, it buys from the cash as
  before.
- **`-Dplaytest.groundFunding=off`** plays the player as before FB11 (the pattern of LP-G1's
  `groundWatch=off`), for a trace to set beside. GroundSkipCheck run with it gives the same
  figures as main's (below).
- The banner GROUND FUNDED WHEN A SECTOR WAITS ON IT states the rule and its WHY.

## 3. The paired measure (Rotterdam, the same player with and without the funding)

Of commit 1fedd82d4. The later commit changed only how a cash purchase spends the share (by its
price, as before FB11, where 1fedd82d4 read the cash's fall); the two are equal for a converted
purchase, so the figures stand for the final model.

| | 1900, main | 1900, FB11 | today, main | today, FB11 |
|---|---:|---:|---:|---:|
| people m800 / m1000 / m1200 | 16,679 / 30,044 / 36,192 | 44,276 / 136,649 / 379,904 | 35,451 / 39,875 / 51,794 | 77,323 / 109,798 / 158,865 |
| people at the end (m1302; m1400) | 42,503 | 443,756 | 75,432 | 241,689 |
| longest stretch of still jobs | 112 months from m1079 | 75 from m1207 | 141 from m676 | 69 from m842 |
| landlords waiting on ground, traced window | 283 of 401 months | 4 of 401 | 532 of 801 | 542 of 801 |
| looks held by the share / of them a sector waiting | 68 of 76 / - | 67 of 82 / 14 | 155 of 156 / - | 154 of 155 / 142 |
| offers funded, raised, over the run | - | 73, 13,507,150 | - | 43, 6,569,540 |
| debt, years of GDP (m800, m1000, m1200) | 0.7, 0.1, 1.4 | 1.0, 1.2, 1.5 | 0.2, 0.1, 2.4 | 2.1, 2.4, 2.4 |
| interest, share of tax (same months) | 9%, 2%, 28% | 14%, 10%, 21% | 4%, 2%, 25% | 37%, 32%, 35% |

The 1900 city's hold is gone, its debt no larger a share of its output. Today's city grows three
times as fast, and from about m600 its looks are held again, now by the player's own test: it
carries the debt main's city reaches by m1200 (2.4 years of GDP, interest about a third of the
tax) four hundred months earlier, and the test refuses more. That is the bound the brief asked
to keep, not a new hold; the landlords' months waiting stay about as many as main's, in a city
three times the size. Main's 1900 run and the FB11 one both end their 1300 months with one
LongPlaytest line red ("the households' own line + households UnsettledFees ran past its bound",
m1302); main's also "industry stands farther out than the homes" (m1300). Today's FB11 run ends
with "industry stands farther out than the homes" at m1400. These are the short runs' last months
(the gate's run is 2,400), the same lines on main, not FB11's.

## 4. The checks

- `./batch-check.sh --fast`: compile OK, maps regenerated, stale 0 firm.
- **ER1Check** (restated, 34 assertions, 4.3 s, with REV-FB11's section 6 below), green. Its old first section, "a full city holds
  jobs while its land buyer has no cash", was the defect this batch repairs; it is now a control
  and a repair: (1) with nothing waiting on ground, the share alone decides, though the player's
  own test would lend: counted short, nothing bought or borrowed; (2) a full city that borrowed on
  the funding page until its test refused even no more, then as much again, holds its jobs every
  month of the planning horizon, a sector waiting on ground at every look (6 of 6); (3) restoring
  the cash still buys through the share, borrowing nothing, and releases the shop's order;
  (4) paying from the vault, the look borrows nothing; (5) a city its test will lend to buys the
  whole offer on the bond: 6 offers, all funded, its cash paying at most the share, the debt up by
  no less than the bonds raised, raised plus cash equal to the offers' price, the sellers owed the
  offers' dollars, the shop's plot permitted and its order placed, and the next month's money audit
  closed (residual and drift, the ensemble's rule), as the month before it did; (6) an overdrawn
  treasury (REV-FB11, section 8) buys the offer on a bond that covers the overdraft with it, and
  sells no bond that bought nothing.
  ★ The control city plays its first `PLANNING_HORIZON` months with its ground free before the
  ground is consumed: at month 2 its revenue was 0.095 a month, and the player's test refused for
  want of revenue, not of the demand gate (diagnosed). Its premise "every whole offer exceeds the
  treasury cash" became "the offer a look finds is past the share": with six months' revenue in
  hand some small offer is inside the cash, and the share is the rule the look applies.
- **GroundSkipCheck**: exit 3, only main's known red (section 2, LP-G2's row). Section 3, "an
  offer the share cannot pay still waits", was red under FB11: its town's landlords wait on ground
  and the town could service the bond, so the look funded the offer, which is the new rule.
  ★ Restated to "in a city its own test will not lend to", the town borrowing on the funding page
  until the test refuses, then as much again; all its other lines as they were, green. Run with
  `-Dplaytest.groundFunding=off` it gives main's figures to the digit (section 2's 29 offers and
  6 months of 60 held), so section 2's red is main's.
- **LandCheck, LandmassCheck**: green.
- **The pick** (`sh tools/pick.sh origin/main HEAD`): 90 of 256 harnesses, not ALL; review line
  "not needed - no money, saves or threading". The brief asks for a money review all the same: the
  player now borrows and buys land with it. `./batch-check.sh --pick` (7560256a3): exit 4, 13 harnesses red,
  12 of them main's known reds (InvestCheck, HousingDoorCheck, OrderDoorCheck, RoadCheck, AutoBuildCheck,
  ForeignDebtCheck, DenominationCheck, MapCheck, PlanCheck, KeptCheck, CarCheck, GroundSkipCheck), and one
  line NEW: CopperRefiningCheck 11, traced in section 7 and passed by name (main's latent cycle, FS8's).

## 5. Calls taken

- ★ RE3-J and FB11H leave the bundle, their conditions gone on 0.9.206 (section 1); no study note,
  since there is no held town to study.
- ★ The funding is gated on a sector waiting on ground, and bounded by the player's own
  `canService()`; the share still bounds what the cash pays. Prices, whole offers, the sellers'
  payment and the vault untouched (`LandMarket` not changed).
- ★ The room move funds only when the cash cannot buy its offer, so a city with the cash plays as
  before.
- ★ An off switch, `-Dplaytest.groundFunding=off`, for the paired traces and the comparison with
  main; on by default.
- Found in passing, not a defect: the trace's `sizeSqFt=0` offers (11 of 187 in today's main
  Rotterdam) are offers with no dry ground but foothill, which `Game.bestOffer()` ranks by
  buildable ground; `LandParcel.getSizeSqFt()` counts the dry class alone.

## 6. Left for the orchestrator

- File RE3-J and FB11H (section 1): both conditions are gone on 0.9.206, on both foundings.
- The money review (the brief's), on "FB11 mends at".
- The ensemble: the player's land buying moves the default run; it runs on the final model
  against docs/ensemble/0.9.206.md after the review.

## 7. CopperRefiningCheck 11, the pick's one new red

"seed 1's 2,400 months stand at most ROD_MILLS_STANDING (1) Rod Mill: FAIL 23 Rod Mills". The harness plays
LongPlaytest's seed 1 for 2,400 months in a child and reads the copper works at the end: on FB11 "Copper Smelter 24,
Electrolytic Refinery 24, Scrap Copper Refinery 25, Rod Mill 23, Wire & Cable Works 0". Traced by a helper (two
2,400-month runs of seed 1 one at a time, with a temporary print, since removed; `target/fb11/cu-off.log` and
`cu-on.log`, not committed):

- Main's player (`-Dplaytest.groundFunding=off`) ends "Copper Smelter 15, Electrolytic Refinery 15, Scrap Copper
  Refinery 16, Rod Mill 0". But it builds and sheds Rod Mills in waves: 17 standing at m1353, 16 at m1693 and 9 at
  m2028 (the first at m889; 0, 5, 0 and 0 at m600/1200/1800/2400). FB11's city builds its first at m1385 and stands
  8 at m1800 and 23 at m2400, ten of them ordered in m2324-2357. The bound reads one month of a cycle main's own run
  has (checked in the log: `CU m1353 rod 17`).
- Ground held neither. Copper Refining was never in `getLandBlockedSectors()` in either run (every 60th month and
  every month the count moved). Main's holds were staff, the interest test and, at the end, a broke sector. Nothing
  in `fundGround()` orders a works: the mills are `ProcessingSector.plan()`'s (SpreadPlanner `best()`), through
  `CopperRefining.feedRefusal()`'s feed gate (the mills run at 95% of nameplate).
- They pay, thinly. No rod is sold locally: no wire works, rod demand 0, all of it exported (174,902 t at m2400).
  The mills take the city's own cathode, which the planner values at its net export price, the city exporting
  141,545 t. Rod over cathode at the export side was 1.109 at main's m897, 1.023 at m1351, 1.0175 at FB11's m2324,
  1.0136 at FB11's m2400 and 1.0117 at main's m2400. The study's 1.03 (spec-world-prices.md 5) prices the feed at
  import, which a city exporting its cathode does not pay. Losing mills are shed: main went from 17 to 0 in
  m1353-1435 under `SpreadPlanner.mayShed()`, and from 9 to 0 in m2050-2113 in a sector-wide distress
  (`ProcessingSector.mayRetire()`).
- Reality: an integrated refiner rolls part of its cathode into rod on a premium of about 1-3%, which roughly covers
  the conversion, so this is real in kind. 23 mills exporting all their rod with no wire works is large in scale; that
  is FS8's question, not this batch's.

★ Passed by name, not mended here: a `## known-reds` row naming FS8 (docs/studies/flaky-suite.md 6.7: "CopperRefining
11's premise is caused the same way... its 28-minute child of LongPlaytest's seed 1 stays nightly as the playtest's
audit"). The red is not FB11's code. It is main's own Rod Mill cycle, which FB11's moved city reaches at m2400. The
law to restate (no mill on rod priced apart from its cathode, losing mills shed, not a count at one month) is the
sweep's that owns the harness.

## 8. The review (REV-FB11) and its mends

The orchestrator's stranger's money review on "mends at" 3685baf8e: MEND, one P1 and two P2s. The
rest stands: the bond route is the land screen's and `buyWhole()`'s, the audit closes, the game
never calls `fundGround()`, GroundSkipCheck 3 is fair, the 443k Rotterdam is real (Amsterdam's
erfpacht, Stockholm's land bank of 1904), and CopperRefiningCheck 11 is main's class under FS8.

- **P1, a bond that buys no land, look after look.** `fundGround()`'s gap was the price less
  `max(0, min(share, cash))`, so it ignored an overdraft, which `buyWhole()`'s `landCashGap()`
  counts. On an overdrawn treasury the bond raised the price, the overdraft took the proceeds,
  `canAffordParcel()` failed, and the debt rose with nothing bought, at every later look while the
  overdraft lasted. Mended: the gap is `max(local - fromCash, g.landCashGap(List.of(p.getId())))`.
  A bond that buys nothing (a short auction, or the office refusing) is now counted
  (`groundBondsUnspent`, `groundBondsUnspentRaised`, in the summary). ★ A funded offer spends
  the share by the fall in the cash the look began with, `max(0, cashBefore) - cash`. A bond's
  surplus over its gap comes back to the share before the next offer borrows again, as before,
  but an overdraft it cleared does not. A first try, `max(0, cashBefore - cash)`, kept the surplus
  out: section 5 then bought 7 offers and left 4.48 in the treasury, borrowed. The final form
  gives section 5 the reviewed figures to the digit (6 offers, cash 2.18 to 2.70, 31.26 raised).
  ER1Check's new section 6 is the evidence:
  - The treasury is overdrawn by as much as it held (-2.18), with the office's gap 7.30 (an offer
    of 5.12 plus the overdraft). The look bought 6 offers, all funded, and no bond bought nothing.
    It ended at +0.62. The bonds raised 33.55 against a debt up 37.53, raised less the overdraft
    cleared equal to the offers' price. The sellers are owed the offers' dollars, and the second
    month's audit closed.
  - Run against the old gap (a temporary revert, restored): 4 FAILs, 0 bought, 1 bond that bought
    nothing, the debt 0 to 6.47 and the cash -2.18 to 3.92 with no ground.
- **Real, not only a fixture's.** The treasury is overdrawn at some looks in the traced Rotterdam
  runs: main's 1900 at 7 of 76, main's today at 5 of 156. FB11's funded runs had one between them
  (today's, m880: cash -209,715, offer 640,835, held). Its bond book shows no issue at m880-881:
  the test refused and no bond was sold, so section 3's paired figures stand.
- **P2, the ship file's words.** "debt service at most a quarter of the tax" was not
  `canService()`. Main's rule (`LongPlaytest.java:6030`) is the interest alone on the whole debt,
  the new bond in it, at the marginal quote, with no principal; today's funded city pays 32-37% of
  its tax in interest at m800-1200 (section 3). The words now say so. Queued in the ship file,
  ARL1: a servicing test with principal and the book's own coupons, as Ontario's Annual Repayment
  Limit (principal and interest at most 25% of own-source revenue).
- **P2, ER1Check's premise.** "its private buyer can pay its whole quote" asserted only
  `buyer.getCash() > 0`. It now asserts the whole quote, ground included (`canPayWhole()`), as the
  funded section's premise does.

## 9. The ensemble

Label FB11, on the gate box, classes of ee746bd77 (the REV-FB11 mends with main ca030ab08 merged),
80 of 80 runs in 28.7 minutes, scored against `docs/ensemble/0.9.206.md` (R1g's a3fc9ab0c); its
baseline is `docs/ensemble/FB11.md`. **Verdict PASS**: hard criteria 10 of 10, M1 (the money
audit) and F1 (findings) passed under all five policies; 24 of 26 criteria (1 retired). The two not
passed are AUTO2's A1 and A2 (info), which failed on 0.9.206 too: inflation from m240 2.88%
[2.45% to 3.48%], 9 of 16 seeds in band (0.9.206: 2.96%); the longest spell of yoy above 10%,
14 months [9 to 32] (0.9.206: 13).

Final population, median [range], FB11 against 0.9.206. T2 (info) went from FAIL to PASS under
all five policies:

| policy | FB11 | 0.9.206 |
|---|---:|---:|
| AUTO2 | 5,815,558 [2,367,023 to 9,020,635] | 6,262,849 |
| ZERO_SLACK | 4,326,150 [1,267,092 to 7,059,834] | 4,317,140 |
| ZERO_HOT | 19,202 [8,770 to 33,893] | 13,344 |
| SHOCK | 606,768 [29,537 to 1,070,531] | 367,962 |
| HIGH | 184,152 [13,254 to 349,428] | 104,940 |

The held cities grow most where they were held: ZERO_HOT, SHOCK and HIGH by 44-75% in their
medians. AUTO2's median is 7% lower, its seeds' range wider at the top (9.02 million against 8.22).
