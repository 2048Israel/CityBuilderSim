# FR3 - ports as a landlord port, and the Bulk Berth: notes

*0.8.7, 2026-10-19. The batch is `docs/studies/spec-freight-and-infrastructure.md` §7's FR3 row: §3.4's design, §2's rule, D8-D10 (Jerus, 2026-10-12: "all the freighting just do rec"). The brief, with every number and its source, is [FR3.md](FR3.md). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; slices 3 and 4 were cut off half-written by the PC's 04:09 reboot (a Windows update), committed as found (15c94e4) and finished by a second session.*

## 1. What shipped

- **The Bulk Berth** (id 142, `BuildingType.PORTS`, the city's): DRY_BULK, 2.5 Mt a year, D$60M, 27 posts, 2 ha. **Handysize (32,500 t) and Supramax (57,500 t)** in `Ports.Ship`, before the Panamax; a berth's `ships` list says which classes it takes (`BuildingsTemplate.berthShips()`), and the month's bulk calls in the classes of the largest dry bulk berth standing (★6).
- **The crews on Freight's payroll** (D8): `BuildingsTemplate.getOperator()` is Freight for a PORTS building; a sector's posts follow the operator, its land, value, repairs and tax the owner. `Game.portsBill()` is the berths' upkeep alone. Freight's payroll is struck in three (`ownPayroll()`, `portsPayroll()`, `portsCrewByKind()`); the railway's and Haulage's rules read the posts Freight owns, the same figure to the bit as before.
- **The Ports division** (`sectors/PortDivision.java`), Freight's third: in `Freight.haul()` step 1, at the shares in force while the cargo moved, every shipper's sea tonnes by kind are billed at the kind's quote ("Port handling" on the shipper, "Port handling billed" on Freight: the buyer for what it lands, the exporter for what it ships), and Freight pays the city **port dues** on the same tonnes (`Sector.payTheCity()`: an input named "Port dues", in no supplier's purchases).
- **The dues reach the treasury the same month**: `EconomyManager.getPortDues()` in the tax take, `NationalAccounts.getPortDues()` a revenue line, the Government screen's "Port dues". A pool paying a pool, so the money audit needs no line of its own.
- **The dues' default** (D9): the annuity on the kind's terminals standing (cash plus materials at market, `BuildingManager.getPortBookValueByCargo()`) at the city's 30-year rate (`DebtManager.curveRate(360)`, ★8) over `PORT_LIFE_MONTHS` 360, over the kind's berths' month at `DESIGN_USE` .60; times the dial **`TaxPolicy.portDues`** (1 the default, 0 to 2; a ratio, never rescaled; a policy array without the slot loads 1).
- **The handling price** per kind: the kind's crew over the larger of its tonnes and `DESIGN_USE` of its berths' month, times the scarcity (1 to `MAX_SCARCITY_MULTIPLE` at twice the berths' month), plus the due; walked `REPRICE_SPEED` a month; the opening quote the rule's own target; no return (★3).
- **A berth pays back** (§9.5): `Ports.berthPaysBack()`, the dues' own break-even - the kind's uncovered tonnes reach `WORTH_A_BERTH / dial` of the berth's month - and `Ports.payingTerminal()`, the largest of a kind's terminals that pays back. The test player (`LongPlaytest.portsWhenNeeded()`), the advice (`BuildAdvice` `Kind.PORT`, one measure a cargo kind, `suggestFor()` alone, not the overview's list) and the auto-builder (`AutoBuilder.remit()`, last; held by the debt limit, the money, the ground and the coast) all choose by it.
- **The save**: the division's quotes and month in Freight's extras (`port.*`); the ledger's `paidToCity`; the dial in the policy array (`STATE_SLOTS` one longer). New keys only, no format bump.
- **The screens, as few as FR4 leaves**: Freight's page gains "Ports, the quay" (tonnes, quote, handling billed, dues paid by kind); the Government screen's "Port dues"; the build card's wording for a berth. Not the Policy screen's dial (FR4).
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

## 3. The slices, and what the review changed

| slice | by | what the review changed |
|---|---|---|
| 1, the Bulk Berth and the ships (9 files) | Sonnet | nothing; its classes before PANAMAX kept (only the name is saved; BoatSchedule's hashes move, the map's boats call at other minutes - drawing only). Its id, 142 before FR7's 141, reached a latent fault in the buildings' save (the last row) |
| 2, the crews (Freight, Haulage, BuildingManager, Game, LandlordPortCheck 3) | Sonnet | nothing; PortCheck's O9 line "its crews are paid with transit's" moved to D8 at the orchestrator's word |
| 3, the division (cut off by the 04:09 reboot) | Sonnet | **the dues never reached the treasury**: Freight's cash fell by them at its strike and nobody's rose - `EconomyManager.getPortDues()` in the tax take, `NationalAccounts`' line, the Government screen's. **`cityRate()` read `BondMarket.getLastIssueLoanRate()`**, a business's bond: the city's own 30-year paper now (★8). `Game.quoteDemolition()` still saved a berth's crew. OilCheck's index followed the policy array and names the duty's slot now (`STATE_BEFORE_FUEL_DUTY`, the same slot before and after: the premise did not move) |
| 3b, LandlordPortCheck 4-9 | Sonnet, a second session | nothing; the builders' line is checked as the remainder at the quote (their repairs are bought before the haul, in no row between two months) |
| 4, the advice (cut off) | Sonnet | its harness and the run's count were never written (4b); the build card's wording for a berth |
| 4b, BuildAdviceCheck 13, LongPlaytest's line | Sonnet, a second session | the quote at founding prices beside the nominal; it gave three more BuildScreen switches a PORT case and LongPlaytest `-Dplaytest.months` |
| the playtests' reload | Claude | **every id crosses a save.** `Game.sendBuildingSave()` and `loadBuildings()` ran to `getTemplateCount()` and read the index as an id; with 142 templates and no 141 every Bulk Berth was lost across a save (both playtests' "next-month income across a save", three Bulk Berths' crews short on Freight at m3648). Main plus slices 1-2 is clean only because its test player never orders a Bulk Berth: ordered, it fails the same way. Both loops and the Summary screen's two run to `getMaxTemplateId()` now; SaveFileCheck's new section drops a template to cause the gap whatever the catalogue, and fails on the old loops |

## 4. What moved in the trace

Against main at efff4b9 (0.8.5, FR2's model), the model at 37806a8. Both runs: "Nothing. Every month passed the audit and every reload matched." The trace is main's to the month until the first terminal opens - default row 1,447 (the test player's first order m1438, a General Cargo Terminal, as on main), Earth row 2,031 (m2017) - because nothing in FR3 moves while no berth stands.

| | main | FR3 (37806a8) |
|---|---|---|
| default, end | m4000: 721,948 people, GDP $15.4T a month at a price index of 3,506 | 596,492, $22.5T at 5,559 (real GDP -8%) |
| default, m1071 / m2022 | 7,987 / 86,720 | 7,987 / 73,634 |
| Earth (Rotterdam), end | m4005: 675,857, $4.5T at 983 | 719,893, $6.7T at 1,273 (real GDP +15%) |
| Earth, m1071 / m2022 | 18,259 / 122,291 | 18,259 / 122,291 |
| the berths | default: General Cargo 46; 73% of the boundary's tonnes by sea. Earth: General Cargo 42; 66% | default: Bulk Berth 7, General Cargo 36; 85%. Earth: Bulk Berth 19, General Cargo 44; 84% |

**The Ports division over the run** (LongPlaytest's line): default - dry bulk billed in 704 months, 469 Mt, the last quote **D$2.76 a tonne at founding prices** (the sources' check US$2.50-4.00); general cargo in 2,552 months, 1.25 Gt, **D$15.67** (the check US$7-9); $43.5T billed and $39.7T of dues paid over the run, nominal across a price index from 1 to 5,559. Earth - dry bulk 1,755 months, 2.14 Gt, D$2.19; general cargo 1,973 months, 1.34 Gt, D$12.29; $15.2T and $13.6T. **The 60% rule fires**: dry bulk's best month is 161% of a Bulk Berth's month on the default run (m3705), 362% on the Earth (m3576); the overnight agent's 27% was the spec's young city's 57k t a month, and the playtest's own 600 months reach 5%.

**Why.** Each berth's crew leaves the transit bill for Freight's payroll and comes back on the shippers' bills; the bills are nine tenths dues, a new revenue of the treasury's; and the Bulk Berths put dry bulk at sea (85% of the boundary's tonnes against 73%), whose band narrows the landed price of coal and ore. The general cargo quote is about twice the sources' handling because the dues recover a General Cargo Terminal (D$15 a tonne at .60 of its month, FR3.md 2) on top of the crew: a landlord port's dues and a stevedore's charge together, where the sources quote the stevedore's. A single run swings a long way with the city's path (FR2-notes §4), so the ensemble (§5) is the measure.

## 5. The ensemble

ENSEMBLE_HERE

## 6. The suite

SUITE_HERE

## 7. Not built, and what is left

LEFT_HERE
