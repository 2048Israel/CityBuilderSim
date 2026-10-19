# FR3 - ports as a landlord port, and the Bulk Berth: notes

*0.8.7, 2026-10-19. The batch is `docs/studies/spec-freight-and-infrastructure.md` §7's FR3 row: §3.4's design, §2's rule, D8-D10 (Jerus, 2026-10-12: "all the freighting just do rec"). The brief, with every number and its source, is [FR3.md](FR3.md). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; slices 3 and 4 were cut off half-written by the PC's 04:09 reboot (a Windows update), committed as found (15c94e4) and finished by a second session.*

## 1. What shipped

- **The Bulk Berth** (id 142, `BuildingType.PORTS`, the city's): DRY_BULK, 2.5 Mt a year, D$60M, 27 posts, 2 ha. **Handysize (32,500 t) and Supramax (57,500 t)** in `Ports.Ship`, before the Panamax; a berth's `ships` list says which classes it takes (`BuildingsTemplate.berthShips()`), and the month's bulk calls in the classes of the largest dry bulk berth standing (★6).
- **The crews on Freight's payroll** (D8): `BuildingsTemplate.getOperator()` is Freight for a PORTS building; a sector's posts follow the operator, its land, value, repairs and tax the owner. `Game.portsBill()` is the berths' upkeep alone. Freight's payroll is struck in three (`ownPayroll()`, `portsPayroll()`, `portsCrewByKind()`); the railway's and Haulage's rules read the posts Freight owns, the same figure to the bit as before.
- **The Ports division** (`sectors/PortDivision.java`), Freight's third: in `Freight.haul()` step 1, at the shares in force while the cargo moved, every shipper's sea tonnes by kind are billed at the kind's quote ("Port handling" on the shipper, "Port handling billed" on Freight: the buyer for what it lands, the exporter for what it ships), and Freight pays the city **port dues** on the same tonnes (`Sector.payTheCity()`: an input named "Port dues", in no supplier's purchases).
- **The dues reach the treasury the same month**: `EconomyManager.getPortDues()` in the tax take, `NationalAccounts.getPortDues()` a revenue line, the Government screen's "Port dues". A pool paying a pool, so the money audit needs no line of its own.
- **The dues' default** (D9): the annuity on the kind's terminals standing (cash plus materials at market, `BuildingManager.getPortBookValueByCargo()`) at the city's 30-year rate (`DebtManager.curveRate(360)`, ★8) over `PORT_LIFE_MONTHS` 360, over the kind's berths' month at `DESIGN_USE` .60; times the dial **`TaxPolicy.portDues`** (1 the default, 0 to 2; a ratio, never rescaled; a policy array without the slot loads 1).
- **The handling price** per kind: the kind's crew over the larger of its tonnes and `DESIGN_USE` of its berths' month, times the scarcity (1 to `MAX_SCARCITY_MULTIPLE` at twice the berths' month) - that crew's part walked `REPRICE_SPEED` a month, the opening quote the rule's own target - **plus the dues at the month's own rate, passed through** (the review: §3); no return (★3).
- **A berth pays back** (§9.5): `Ports.berthPaysBack()`, the dues' own break-even - the kind's uncovered tonnes reach `WORTH_A_BERTH / dial` of the berth's month - and `Ports.payingTerminal()`, the largest of a kind's terminals that pays back. The test player (`LongPlaytest.portsWhenNeeded()`), the advice (`BuildAdvice` `Kind.PORT`, one measure a cargo kind, `suggestFor()` alone, not the overview's list) and the auto-builder (`AutoBuilder.remit()`, last; held by the debt limit, the money, the ground and the coast) all choose by it.
- **The save**: the division's quotes and month in Freight's extras (`port.*`); the ledger's `paidToCity`; the dial in the policy array (`STATE_SLOTS` one longer). New keys only, no format bump.
- **The screens, as few as FR4 leaves**: Freight's page gains "Ports, the quay" (tonnes, quote, handling billed, dues paid by kind); the Government screen's "Port dues"; the build card's wording for a berth. Not the Policy screen's dial (FR4).
- **Fixed on the way**: **every id crosses a save** - `Game.sendBuildingSave()` and `loadBuildings()` ran to the template count and read it as an id, so the Bulk Berth (142, before FR7's 141) was lost across a save (§3); and a berth's sea route keeps its cell's middle where its own first leg would graze land (`SeaRoutes.route()`, §6).
- **Harnesses**: LandlordPortCheck (new, sections 3-9); BuildAdviceCheck's "a berth pays back"; PortCheck's ships; LongPlaytest's "Ports division" line.

## 2. ★ calls

The brief's (FR3.md §4), as Jerus's "rec" left them, and the review's:

1. **★1 The rule sets the handling quote; the sources check it.** A tariff beside a cost-plus rule would be two prices for one service.
2. **★2 General cargo is not the dearest a tonne**: a box at 9 t is (EUR 270-280 a box, about US$33 a tonne, against general cargo's US$7-9).
3. **★3 No return**: the division has no rate base; the city owns the berths, cranes and all, and recovers them through the dues.
4. **★4 Design use .60** (`Ports.WORTH_A_BERTH`) as the floor of the rule's tonnes and the dues' throughput.
5. **★5 The Bulk Berth's crew at 11 a Mt**, the top of [P22]'s 6-11.
6. **★6 The classes by the largest dry bulk berth standing.**
7. **★7 The band's sea share left whole**: the double count is at most the sea share itself (.07-.20 of a lorry's).
8. **★8 (review) The city's rate is its own 30-year paper** (`DebtManager.curveRate(PORT_LIFE_MONTHS)`), not the brief's `BondMarket.getLastIssueLoanRate()`: BondMarket is the businesses' market, so its last issue is a sector's bond and its rate the bank's to that sector.
9. **★9 (review) The dues passed through at the month's own rate**, only the crew's part walked `REPRICE_SPEED` a month (§3): the brief's "with the dues passed through". Walked inside the quote they lagged a growing city's rate and drained Freight.

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the Bulk Berth and the ships (9 files) | Sonnet | nothing; its classes before PANAMAX kept (only the name is saved; BoatSchedule's hashes move, the map's boats call at other minutes - drawing only). Its id, 142 before FR7's 141, reached a latent fault in the buildings' save (the last row) |
| 2, the crews (Freight, Haulage, BuildingManager, Game, LandlordPortCheck 3) | Sonnet | nothing; PortCheck's O9 line "its crews are paid with transit's" moved to D8 at the orchestrator's word |
| 3, the division (cut off by the 04:09 reboot) | Sonnet | **the dues never reached the treasury**: Freight's cash fell by them at its strike and nobody's rose - `EconomyManager.getPortDues()` in the tax take, `NationalAccounts`' line, the Government screen's. **`cityRate()` read `BondMarket.getLastIssueLoanRate()`**, a business's bond: the city's own 30-year paper now (★8). `Game.quoteDemolition()` still saved a berth's crew. OilCheck's index followed the policy array and names the duty's slot now (`STATE_BEFORE_FUEL_DUTY`, the same slot before and after: the premise did not move) |
| 3b, LandlordPortCheck 4-9 | Sonnet, a second session | nothing; the builders' line is checked as the remainder at the quote (their repairs are bought before the haul, in no row between two months) |
| 4, the advice (cut off) | Sonnet | its harness and the run's count were never written (4b); the build card's wording for a berth |
| 4b, BuildAdviceCheck 13, LongPlaytest's line | Sonnet, a second session | the quote at founding prices beside the nominal; it gave three more BuildScreen switches a PORT case and LongPlaytest `-Dplaytest.months` |
| the price, on main with FR7 | Claude | **the dues passed through at the month's own rate.** The quote walked with the dues inside it, so in a growing city it lagged the rate they were paid at and Freight paid the difference (m1550: handling 26,628 against dues 26,815). On FR2's model the railway's margin carried it; on FR7's, thinner, the default playtest's Freight was written down 17 times, lost every truck and held the city at 148,000 people from m2500 to the end (main: one write-down, 713,910). The crew's part walks now (`crewTargetOf()`, `getCrewQuote()`) and the bill adds the month's dues rate; LandlordPortCheck 7 holds both. The default run ends at 701,791 |
| the playtests' reload | Claude | **every id crosses a save.** `Game.sendBuildingSave()` and `loadBuildings()` ran to `getTemplateCount()` and read the index as an id; with 142 templates and no 141 every Bulk Berth was lost across a save (both playtests' "next-month income across a save", three Bulk Berths' crews short on Freight at m3648). Main plus slices 1-2 is clean only because its test player never orders a Bulk Berth: ordered, it fails the same way. Both loops and the Summary screen's two run to `getMaxTemplateId()` now; SaveFileCheck's new section drops a template to cause the gap whatever the catalogue, and fails on the old loops |

## 4. What moved in the trace

Against main at 74d0bc1 (0.8.6: FR2 and FR7), the model at d42c009. Both runs: "Nothing. Every month passed the audit and every reload matched." The trace is main's to the month until the first terminal opens - default row 1,146 (the test player's first order m1116, a General Cargo Terminal, as on main), Earth row 1,771 (m1770) - because nothing in FR3 moves while no berth stands.

| | main (74d0bc1) | FR3 (d42c009) |
|---|---|---|
| default, end | m4002: 713,910 people, GDP $10.9T a month at a price index of 2,546 | 701,791, $6.6T at 1,659 (real GDP -7%) |
| default, m1071 / m2022 | 10,182 / 121,396 | 10,182 / 102,450 |
| Earth (Rotterdam), end | m4005: 706,163, $7.6T at 1,763 | 769,819, $6.5T at 1,646 (real GDP -8%) |
| Earth, m1071 / m2022 | 16,976 / 132,062 | 16,976 / 134,806 |
| the berths | default: General Cargo 35; 77% of the boundary's tonnes by sea. Earth: General Cargo 38; 72% | default: Bulk Berth 33, Bulk Terminal 1, General Cargo 41; 92%. Earth: Bulk Berth 43, Bulk Terminal 1, General Cargo 52; 91% |

**The Ports division over the run** (LongPlaytest's line): default - dry bulk billed in 2,445 months, 8.46 Gt, the last quote **D$3.16 a tonne at founding prices** (the sources' check US$2.50-4.00); general cargo in 2,855 months, 1.52 Gt, **D$11.88** (the check US$7-9); $44.3T billed and $39.6T of dues paid over the run, nominal across a price index from 1 to 1,659. Earth - dry bulk 2,198 months, 10.4 Gt, D$3.58; general cargo 2,216 months, 1.89 Gt, D$14.33; $80.3T and $72.8T. **The 60% rule fires**: a Bulk Terminal on each run, and dry bulk's best month 764% of a Bulk Berth's month (default m3651), 865% (Earth m3576); the overnight agent's 27% was the spec's young city's 57k t a month, and the playtest's own first 600 months reach 5%.

**Why.** Each berth's crew leaves the transit bill for Freight's payroll and comes back on the shippers' bills; the bills are nine tenths dues, a new revenue of the treasury's; and the berths put nine tenths of the boundary's tonnes at sea (92% against 77%), whose band narrows the landed price of coal and ore. The general cargo quote is above the sources' handling because the dues recover a General Cargo Terminal (D$15 a tonne at .60 of its month, FR3.md 2) on top of the crew: a landlord port's dues and a stevedore's charge together, where the sources quote the stevedore's. Before the review's pass-through (§3) the same default run stalled at 148,000; FR3 on FR2's model (37806a8, before FR7) ended the default run at 596,492 and the Earth at 719,893 against FR2's 721,948 and 675,857. A single run swings a long way with the city's path (FR2-notes §4), so the ensemble (§5) is the measure.

## 5. The ensemble

The 16-seed x 5-policy ensemble (the workbench zip's runner through FR2's `runpc.sh`, outside the tree), scored by its own `score.py` against FR7's final (`fr7b`, f99a38d: main's model at 74d0bc1), on a class snapshot of d42c009. 80 runs, none missing or short, no LongPlaytest finding, no money-audit month, 64/64 runs their AUTO2 twin's before the switch.

| criterion | main (FR7's fr7b) | FR3 (d42c009) |
|---|---|---|
| A1 inflation m240-end | 2.25%, pass | 2.26%, pass |
| A2 longest yoy > 10% spell | 15 [2-19], FAIL | 15 [6-19], FAIL |
| A3 deflation; final/peak population | 3 [0-8], pass | 2 [0-8], pass |
| S1 0% from m1800 minus twin | 1.89%, spell 27, FAIL | 2.27%, spell 26, FAIL |
| H1 / H2 ZERO_HOT | pass / FAIL (9/16 below) | the same, to the digit |
| K1 / K2 SHOCK, R1 / R2 / R3 HIGH | pass, pass; pass, pass, FAIL (5 above +1) | the same, to the digit |
| T1 AUTO2 seeds to 40,000 | 16/16 at m1,428 | 16/16 at m1,429 |
| T2 AUTO2 final population | 214,510 [134,685-303,023] | 214,456 [171,367-299,656] |
| T2 ZERO_SLACK | 142,244 [85,921-177,175] | 143,922 [89,444-199,570] |
| T2 ZERO_HOT / SHOCK / HIGH | 3,616 / 11,804 / 3,911 | the same |
| criteria | 22/26 | 22/26 |

**What moved, and why.** Nothing in the three 720-month policies: their young cities never stand a berth, and FR3 moves nothing while none stands (§4), so H1-H2, K1-K2 and R1-R3 are FR7's to the digit. The two 2,400-month policies build berths from about m1,100 on and end where main does at the median - AUTO2 214,456 against 214,510 - with the smallest of the sixteen larger (171,367 against 134,685); S1 moves within its spread. The first draft of the price (dues walked with the crew, on FR2's model, 37806a8) ended the AUTO2 cities a tenth smaller (189,814 against 211,554) and R3 at 5 seeds above against FR2's 1: the dues lagging a growing city were a drain on Freight, and the pass-through (§3) is what takes it out.

## 6. The suite

`Batch check.bat --maps` on the final tree (d42c009, main with FR7 merged): **117 harnesses run in 1,056 s, 116 passed, 1 failed** - ScaleCheck's "...and it builds" in the ten-billion copy, main's one red (FR7-C); the ensemble ran beside it, so timing soft. Stale: no firm finding; the maps regenerated. On the way, against main with FR2 only, the suite had three reds of FR3's, fixed in 1026a94: **MapCheck 9**'s routes (its fixture stands two of every berth, so two Bulk Berths, and the one at the shore's end grazed a cell of land with its first leg: `SeaRoutes.route()` keeps the berth cell's middle where the berth's own leg would not keep to the sea; drawing only, GatewayCheck, PortCheck and ReadPathCheck green on it), **BuildCardCheck 1** (the Bulk Berth a fifth port) and **StaleCheck** (README's count). New or extended and green: LandlordPortCheck (sections 3-9), BuildAdviceCheck 13 ("a berth pays back"), SaveFileCheck ("every id crosses a save", red on the old loops), PortCheck's ships, OilCheck's slot. `--timing`, alone and hard: TIMING_HERE.

## 7. Not built, and what is left

- **Not this batch's** (the spec's later rows): every screen of spec §6 - Freight's division strip, the Policy screen's port dues dial, the berths' cards with their payback (FR4); the port yard (D7, FR6); the island city's sea-only trade (§9.6, I1); a Handysize row in the freight rule.
- **Not built, noticed**: a berth on site pays no dues until it opens, and a kind's dues rate falls a little when a second berth of it opens (its book value over more tonnes at design use); the advice's payback is a berth's own break-even, stricter for a second berth than the kind's berths together; the band's sea share is left whole (★7); the builders' handling line is checked as the remainder (their repairs are bought before the haul).
- **For Jerus (Pending, FR3-C)**:
  1. **The calls ★1-★9**, all as recommended; ★8 and ★9 the review's.
  2. **The general cargo quote**: D$12-14 a tonne at founding prices against the sources' US$7-9 - a landlord's dues and a stevedore's charge together, nine tenths of the bills dues. The dial (1, the berths' whole cost) and the design use (.60) are where it moves.
  3. **The review's pass-through** (§3): the dues billed at the month's own rate and only the crew's part walked, the brief's "with the dues passed through" read literally. Walked with the crew, the dues drained Freight in a growing city; on FR7's model the default playtest stalled at 148,000 people. With it the ensemble ends where main does (§5).
- **The ids**: the Bulk Berth is 142, FR7's Rail Depot 141. With both on main the catalogue has no gap, and since 37806a8 the save no longer needs it not to.
