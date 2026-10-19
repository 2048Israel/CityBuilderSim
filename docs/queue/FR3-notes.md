# FR3 - ports as a landlord port, and the Bulk Berth: notes

*VERSION_HERE, 2026-10-19. The batch is `docs/studies/spec-freight-and-infrastructure.md` §7's FR3 row: §3.4's design, §2's rule, D8-D10 (Jerus, 2026-10-12: "all the freighting just do rec"). The brief, with every number and its source, is [FR3.md](FR3.md). The slices were Sonnet 5.5 subagents' (DeepSeek's key not being set on the PC), each reviewed by Claude; slices 3 and 4 were cut off half-written by the PC's 04:09 reboot (a Windows update), committed as found (15c94e4) and finished by a second session.*

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

SLICES_TABLE_HERE

## 4. What moved in the trace

TRACE_HERE

## 5. The ensemble

ENSEMBLE_HERE

## 6. The suite

SUITE_HERE

## 7. Not built, and what is left

LEFT_HERE
