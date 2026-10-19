# FR3 - ports as a landlord port, and the Bulk Berth: notes

*Work in progress, 2026-10-19 (the night run, until 05:45). The batch is
`docs/studies/spec-freight-and-infrastructure.md` §7's FR3 row: §3.4's design,
§2's rule, D8-D10 (Jerus, 2026-10-12: "all the freighting just do rec"). The
brief, with every number and its source, is [FR3.md](FR3.md). It builds on FR2
(0.8.5, branch `claude/quirky-khorana-a90e7c`, not yet on main): the branch
`batch/FR3` carries FR2's commits and must not be pushed as ready until FR2
ships. The slices were Sonnet subagents' (DeepSeek's key not being set on the
PC), each reviewed by Claude.*

## 1. What is built

- **The Bulk Berth** (id 142, after FR7's Rail Depot at 141; whoever ships
  second renumbers): the city's, PORTS, DRY_BULK, 2.5 Mt a year, D$60M, 27
  posts, 215,000 sq ft. A berth's optional `ships` list (`BuildingsTemplate
  .berthShips()`): the Bulk Berth takes Handysize and Supramax, the Bulk
  Terminal Panamax and Capesize.
- **Handysize (32,500 t) and Supramax (57,500 t)** in `Ports.Ship`, before the
  Panamax. `Ports.bulkShipsStanding()`: the month's bulk calls in the classes
  of the largest dry bulk berth standing (★6); `bulkShipFor(tonnes, classes)`
  is today's rule exactly with the Terminal's two.
- **The crews on Freight's payroll** (D8): `BuildingsTemplate.getOperator()`
  is Freight for a PORTS building; a sector's posts follow the operator, its
  land, value, repairs and tax the owner. `Game.portsBill()` is the berths'
  upkeep alone (none today: the terminals have no upkeep line). Freight's
  payroll is struck in three (`ownPayroll()`, `portsPayroll()`,
  `portsCrewByKind()`): the railway's and Haulage's rules read the posts
  Freight owns, the same figure to the bit as before.

(Slices 3 and 4 below as they land.)

## 2. ★ calls

From the brief (FR3.md §4):

1. **The rule sets the handling quote; the sources check it.** A tariff
   beside a cost-plus rule would be two prices for one service.
2. **General cargo is not the dearest a tonne**: a box at 9 t is (EUR 270-280
   a box, about US$33 a tonne, against general cargo's US$7-9).
3. **No return**: the division has no rate base; the city owns the berths,
   cranes and all, and recovers them through the dues.
4. **Design use .60** (`Ports.WORTH_A_BERTH`) as the floor of the rule's
   tonnes and the dues' throughput.
5. **The Bulk Berth's crew at 11 a Mt**, the top of [P22]'s 6-11.
6. **The classes by the largest dry bulk berth standing.**
7. **The band's sea share left whole**: the double count is at most the sea
   share itself (.07-.20 of a lorry's).

## 3. The numbers to look at

- **The Bulk Berth**: 2.5 Mt a year, D$60M, 27 posts, 2 ha (FR3.md §2).
- **The dues' default** at 4% a year over 30 years (est.), at .60 use: Bulk
  Terminal about D$8 a tonne, Bulk Berth D$2.30, Container Terminal D$6.60
  (D$59 a TEU), General Cargo Terminal D$15, Tanker Terminal D$2.80. DBCT's
  A$3.72 a tonne is the nearest real charge of the kind.
- **The handling sources** (the check): bulk US$2.50-4 a tonne (DBCT,
  NIMASA); boxes about US$33 a tonne; general cargo US$7-9.

## 4. The slices, and what the review changed

| slice | by | state | what the review changed |
|---|---|---|---|
| 1, the Bulk Berth and the ships (9 files) | Sonnet | committed f0f796a | nothing. Its choice kept: the classes before PANAMAX (only the name is saved); this moves BoatSchedule's hash seeds, so the map's boats call at other minutes - drawing only |
| 2, the crews (Freight, Haulage, BuildingManager, Game, LandlordPortCheck 3) | Sonnet | committed f0f796a | nothing; at the orchestrator's word it also rewrote PortCheck's O9 assertion "its crews are paid with transit's" to the landlord design (D8 replaces O9's), and passed the classes in `Game.boats()` |

## 5. Found on the way

- **The 60% rule may never build a Bulk Berth.** The spec's playtest dry bulk
  (about 57k t a month) fills 27% of a Bulk Berth's 208k t month; the test
  player orders a berth at 60%, and with the dues at the default a berth pays
  back exactly there. The real run's count says whether it fires.
- **The terminals have no upkeep**, so the city's part of a port is now
  nothing a month; their repairs were never charged as upkeep
  (`EconomyManager.CITY_MAINTAINED` bills repairs on PORTS separately).

## 6. What is left

- The trace re-baselined against FR2's (FR2-notes §4: default m4001 795,408
  people; Earth m4005 762,356), and the ensemble against FR2's
  (`ens/out/fr2tip` in the session scratchpad, `runpc.sh` beside it, about 35
  minutes for 80 runs).
- `GameVersion.VERSION` 0.8.6; `--timing` alone and hard; pushed as ready only
  after FR2 ships.
- The Policy screen's dues dial and every screen of spec §6 (FR4).
