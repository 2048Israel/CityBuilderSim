# FR3 - ports as a landlord port, and the Bulk Berth

The brief for batch FR3 (2026-10-19). `QUEUE.md` links the spec; this file is
what the batch builds, with every number it uses and where each comes from.
The spec is `docs/studies/spec-freight-and-infrastructure.md`: §7's FR3 row is
the batch, §3.4 the Ports design, §2 the rule (a tonne moves in legs, each leg
paid once), §8 the decisions (D8, D9, D10), §9.5 the finding; the study's pages
are `docs/studies/freight/2-freight-sector.html` and `3-how-its-tonnes-move.html`.
Jerus answered every decision on 2026-10-12: "all the freighting just do rec".
Every recommendation stands; where the spec leaves a choice open, the call is
marked ★ below and argued in a line.

FR3 builds on FR2 (the Freight sector, 0.8.5, branch
`claude/quirky-khorana-a90e7c`, not yet on main). FR4 has the screens: FR3
builds the model and the save, and only the lines the model needs to be read.

## 1. The design, as the spec has it (§3.4, D8-D10)

- **The city builds and owns the berths** (the four terminals and the new Bulk
  Berth, `BuildingType.PORTS`). It keeps paying their repairs (their upkeep,
  `EconomyManager.CITY_MAINTAINED`); their capital stays on the city's books.
- **Freight's Ports division works them.** The crews leave the city's transit
  bill (`Game.portsBill()`, TreasuryLine.TRANSIT) for Freight's payroll.
- **It charges shippers a handling charge per tonne by cargo kind**, on every
  tonne that crosses a berth: the buyer for what it lands, the exporter for
  what it ships (§2, D2's rule). Billed through `Sector.billForService` on the
  shipper's input line "Port handling", as the railway and Haulage bill.
- **Freight pays the city port dues** per tonne crossing a berth: a new city
  revenue line, "Port dues", and a Policy dial, the city's own price as the
  fare is (D9). Its default recovers the berths' cost over their life at the
  city's borrowing rate.
- **The price rule for handling** is the division's running cost plus the
  return, as Haulage's, with the dues passed through.
- **Ports keep going first** (`seaFreightShare` below `RAIL_FLOOR` for every
  kind): nothing here changes which tonnes go by sea.
- **The Bulk Berth** (D10): Handysize and Supramax ships, about 2.5 Mt a year
  for about D$60M, so a mid-sized city ships its dry bulk by sea.
- **The advice reads a berth's payback** (§9.5): with dues a berth earns, so
  the advice can offer one and automatic building can order one.

## 2. The numbers

Money in the game's units: a price is in D$ thousands a unit (coal .075 is
D$75 a tonne), so D$4 a tonne is .004. At parity a world dollar is a D$.

### Handling: the rule sets the level; the sources are its check

The spec (§3.4) asks the brief to fix the charges from sources, and also gives
the rule: running cost plus the return, the dues passed through. ★1: **the rule
sets the quote; the sources below are the figures the rule's quote is read
against in the notes, not a tariff written into the code.** A fixed tariff
beside a cost-plus rule would be two prices for one service; the railway and
Haulage price by their rule alone.

| kind | the sources (the city end, a tonne) | the check |
|---|---|---|
| Dry bulk | DBCT's terminal infrastructure charge A$3.72 a tonne, 2025-26 [S1] (about US$2.40); Nigeria's approved dry bulk stevedoring US$4.00 a tonne imported, 2.50 exported (2019) [S2]; Georgia Ports' own stevedore use charge US$0.42 a net ton (the authority's charge, not the handling) [S3] | US$2.50-4.00 a tonne |
| Containers | terminal handling EUR 270 a box (Forto, Q3 2025) [S4], EUR 280 a dry box (Maersk, the Netherlands, from 1 October 2025) [S5]: about US$300 a box, at `Ports.TONNES_A_TEU` 9 | about US$33 a tonne |
| General cargo | stevedoring US$5-6 a tonne (Hambantota, 2025) [S6]; wharfage US$2.25 a short ton for general breakbulk (Gulfport) [S7] | US$7-9 a tonne. ★2: the spec's estimate, "general cargo the dearest per tonne", does not hold against these: a box at 9 t is dearer per tonne. The sources win. |
| Liquid bulk | none found tonight | (est.) the dry bulk figure: a manifold against a grab is cheaper per tonne, so below US$4 |

### The price rule (`sectors/PortDivision.java`, new)

| dial | value | source, and the choice |
|---|---|---|
| quote, by kind | (the kind's crews' wages + the dues it paid) / the kind's tonnes billed, x the scarcity, walked `Freight.REPRICE_SPEED` .25 a month | §3.4: "the division's running cost plus the return, as Haulage's, with the dues passed through". Per kind (§3.4: "a handling charge per tonne by cargo kind"): each kind's crews are its own terminals' posts. |
| the return | none | ★3: the return is on a rate base, and the division has none: the city owns the berths, cranes and all (the D$60M Bulk Berth is "two harbour cranes" and the quay, §3.4), and recovers them through the dues. The division earns past its costs only when its berths are scarce. |
| `DESIGN_USE` | `Ports.WORTH_A_BERTH` (.60) | ★4: the rule's tonnes are the larger of the tonnes billed and the kind's berths at .60 of their month. Without a floor a berth handling a few hundred tonnes would bill its whole crew on them (an idle General Cargo Terminal's 11 posts on 1,000 t is D$30 a tonne of coal worth D$75). Below design use the division carries the idle crew. .60 is the test player's own threshold for ordering a berth, and UNCTAD puts a busy berth at 40-70% [P10]. |
| scarcity | `Freight.MAX_SCARCITY_MULTIPLE` 1.6 when the kind's sea tonnes asked for pass its berths | Haulage's shape: the tightness is the kind's boundary tonnes over its berths, the multiple rising from 1 at a full berth to 1.6 at twice the berths' month. |
| opening quote | the rule's own target at design use, the first month a kind has a berth and a crew | Haulage's "with no quote yet, the estimate at today's prices"; no tariff written in. |

### Port dues (the city's price, D9)

| dial | value | source, and the choice |
|---|---|---|
| default, a tonne of a kind | the annuity on the kind's berths standing (their cash cost and materials at market, `BuildingManager`'s book value) at the city's borrowing rate over `PORT_LIFE_MONTHS`, over the kind's berths' month at `DESIGN_USE` | D9: "the berth's cost recovered over its life at the city's rate". Per tonne needs a throughput; the design use is ★4's. |
| the city's rate | the rate its last issue cleared at (`BondMarket.getLastIssueLoanRate()`), the bank's lending rate while it has issued none | "the city's borrowing rate" (D9) |
| `PORT_LIFE_MONTHS` | 360 (30 years) | (est.) a quay wall lasts longer; 30 years is the term a landlord port leases a terminal for [R91][R92], the life the authority prices a berth over. |
| `TaxPolicy.portDues`, the dial | 1.0 = the default; 0 to 2 | the fare's shape: the city's own price (D9). A save without the key loads 1.0. |
| what it comes to at 4% a year | Bulk Terminal about D$8 a tonne; Bulk Berth about D$2.30; Container Terminal about D$6.60 (D$59 a TEU); General Cargo Terminal about D$15; Tanker Terminal about D$2.80 | worked from the building data; DBCT's A$3.72 a tonne [S1] is the nearest real charge of this kind (a terminal's capital recovered per tonne) |

### The Bulk Berth (id 142, `BuildingType.PORTS`, the city's)

Id 142: FR2 took 139 and 140, FR7 (`origin/batch/FR7`) takes 141 for the Rail
Depot. Whoever ships second renumbers.

| field | value | source, and the choice |
|---|---|---|
| `port` | DRY_BULK, 2,500,000 t a year | §3.4 (est.): two harbour cranes at an effective 500 t an hour, 20 hours a day, half the time occupied, make 1.5-3.5 Mt a year; Paradip's mechanised 250 m berth 8 Mt [R74]; small import terminals 0.15-1 Mt [R73]. The spec's 2.5 Mt. |
| `cashCost` | 60,000 (D$60M) | §3.4: two harbour cranes and a berth US$40-80M; Paradip's 8 Mt berth about US$72M [R74]. The spec's D$60M. |
| `constructionPoints`, `constructionMaterials` | 30,000, 1,000 | the four terminals' own ratios, .5 and 1/60 of their cash |
| jobs | NO_DIPLOMA 19, DIPLOMA 7, COLLEGE_ENGINEERING 1 (27) | dry bulk 6-11 posts a Mt a year [P22]; ★5 the top, 11, since a berth worked by grabs is labour-heavier than a mechanised Capesize export terminal (the Bulk Terminal is 8.5); in the Bulk Terminal's mix (161 : 62 : 11) |
| `land` | 215,000 sq ft (2 ha) | the Bulk Terminal's ground a tonne (22 ha for 27 Mt [P14]) at 2.5 Mt |
| `electricity`, `water` | 0, 0 | as the four terminals |

### The ships (`Ports.Ship`)

| class | cargo a voyage | source |
|---|---|---|
| HANDYSIZE | 32,500 t | 30,000-35,000 t [P37], the middle |
| SUPRAMAX | 57,500 t | 55,000-60,000 t [P37], the middle |

- ★6 **Which class calls.** A Bulk Berth takes Handysize and Supramax only (a
  Panamax's 66,000 t wants the Bulk Terminal's quay and water); the Bulk
  Terminal takes Panamax and Capesize, as today. The month's bulk calls in the
  classes of the largest dry bulk berth standing: with a Bulk Terminal, as
  today; with Bulk Berths alone, a Supramax when the month's tonnes one way
  fill one, else a Handysize.
- Dry bulk's sea share stays .07 (`SEA_FREIGHT_SHARE_DRY_BULK`, the Capesize
  and Panamax rows): the research's rule has no Handysize row. Smaller ships
  are dearer a tonne at sea; noted, not moved.
- `ShipShapes` sizes a hull by the cube root of its cargo, so the new classes
  draw themselves.

## 3. The design in the code

- **The crews (slice 2).** A PORTS building is the city's but worked by
  Freight: `BuildingsTemplate.getOperator()` is Freight's key for PORTS and the
  owner (`getSector()`) for every other building. The posts a sector offers
  and pays (`BuildingManager.getJobArrayBySector()` and whatever else counts a
  sector's posts for its wages and its offered share) read the operator; land,
  value, repairs, power and tax read the owner, unchanged. `Game.portsBill()`
  keeps the upkeep and loses the payroll. Freight's cost split gains a third
  share, the ports' (`portsPayrollShare()`): Rail's and Haulage's shares are of
  what is left, so the railway's and the trucks' rules read what they read
  before FR3.
- **The month (slice 3), in `Freight.haul()`, step 1's loop, beside the
  railway's bill.** At the shares in force while the cargo moved, each owner's
  tonnes of a good across the boundary at its kind's sea share (landed and
  shipped) are billed at the kind's quote: "Port handling" on the shipper,
  "Port handling billed" on Freight. Then the dues on those tonnes at the
  kind's dues rate x the dial: Freight's expense "Port dues", the city's
  revenue "Port dues", in the pattern of the property tax a sector pays the
  city (a sector to the treasury, the audit's pools both sides). Then the
  reprice for the coming month.
- **Each leg once (§2).** The quay is a leg of its own: the ship's voyage is
  the world's, inside the band (`Ports.factor()`, untouched); the quay is
  Freight's, at home; the quay to the door is Haulage's (FR2 bills it already:
  "a sea share relieves nothing"). ★7: the band's sea share is left whole. The
  research's rule puts both ends' fixed charges inside it, so the city end is
  inside it too, but the share is .07-.20 of the lorry's: on coal, D$0.66 a
  tonne for the whole voyage door to door against handling's D$2-8 at home.
  Taking the city end out would leave nothing; the double count is at most
  the sea share's own size, and is noted.
- **The advice (slice 4).** `BuildAdvice` offers a berth on its payback: the
  dues a year at its kind's uncovered tonnes (up to its berth), against its
  cost, beside the test player's existing `WORTH_A_BERTH` order.
- **The save.** The division's quotes and last month's figures are Freight's
  extras, as Haulage's; the dial is `TaxPolicy`'s. New keys only: Gson leaves
  a missing key alone, so no `SAVE_FORMAT` bump unless a field would load
  wrongly (the dial's missing key must read 1.0, not 0).
- **The screens (FR4 has them).** Only: Freight's own lines gain the
  division's (tonnes handled by kind, the quotes, the bill, the dues paid);
  the treasury's revenue gains "Port dues"; the Policy screen's dial if time
  allows, since §7's FR3 row names it.
- **Version.** 0.8.6, the next patch after FR2's 0.8.5.

## 4. ★ calls

1. **The rule sets the handling quote; the sources check it** (§2).
2. **General cargo is not the dearest a tonne**: a box at 9 t is (§2).
3. **No return**: the division has no rate base; the city owns the berths.
4. **Design use .60** in the rule's and the dues' tonnes.
5. **The Bulk Berth's crew at 11 a Mt**, the top of [P22].
6. **The classes by the largest dry bulk berth standing.**
7. **The band's sea share left whole** (§3).

## 5. The slices

Each to a Sonnet subagent at xhigh (DeepSeek's key not being set on the PC;
Jerus, 2026-10-18), the brief in `target/deepseek/brief-FR3-<n>.md`, reviewed by
Claude as a stranger's diff and committed by Claude. The design, the month's
order, the trace and the notes are Claude's.

1. **The Bulk Berth and the ships.** Id 142 in `buildings.json` and the
   built-ins (`BuildingManager`'s PORTS block), HANDYSIZE and SUPRAMAX in
   `Ports.Ship`, the class rule (★6) in `Ports.bulkShipFor()` and
   `BoatSchedule`. PortCheck's ship section extended; BuildingDataCheck green.
2. **The crews.** The operator, the per-sector posts on it, `portsBill()` to
   upkeep only, Freight's three-way payroll split. LandlordPortCheck 3.
3. **The division.** `sectors/PortDivision.java`: the handling bill, the
   dues, the dial, the rule; Freight's wiring and extras; the city's revenue
   line. LandlordPortCheck 4-8.
4. **The advice.** The payback in `BuildAdvice`. LandlordPortCheck 9.
5. **The read path and the lines.** The load path, SaveFileCheck and
   ReadPathCheck; Freight's own lines; the Policy dial.

## 6. LandlordPortCheck - what it has to prove

Fixtures in `GameFiles.scratch()` cities, each causing its condition; every
assertion against the model's constants.

1. **The Bulk Berth.** Id 142, PORTS, DRY_BULK, 2.5 Mt a year; one standing
   adds a twelfth of that to `Ports.berths()`'s dry bulk.
2. **The ships.** With Bulk Berths alone a month under a Supramax's cargo goes
   in Handysizes and one at it in Supramaxes; with a Bulk Terminal standing,
   Panamax and Capesize as before.
3. **The crews.** A city with a terminal: the transit bill carries its upkeep
   and not its crew; Freight's wage bill carries the crew at the same fill;
   the two together are what `portsBill()` was, to the cent.
4. **Handling, each leg once.** An importer landing by sea and an exporter
   shipping: each is billed its sea tonnes x the kind's quote; Freight's
   "Port handling billed" is the shippers' lines to the cent; a tonne sold at
   home or railed pays none.
5. **The dues.** Freight pays the city the tonnes x the rate x the dial; the
   city's "Port dues" is that to the cent; the dial at 0 charges none, at 2
   twice.
6. **The dues' default.** The annuity of the kind's berths at the city's rate
   over `PORT_LIFE_MONTHS`, over their month at `DESIGN_USE`.
7. **The price.** The quote walks `REPRICE_SPEED` toward (crew + dues) / the
   larger of the tonnes and design use, x the scarcity; below design use the
   division carries the idle crew.
8. **The money.** The audit's pools hold with a port working (no dollar made
   or lost between shipper, Freight, households and the treasury).
9. **The advice.** A city whose uncovered dry bulk fills a Bulk Berth is
   advised one; one whose does not, is not.
10. **A real run.** `LongPlaytest`'s fixture city some hundreds of months:
    berths built by kind, tonnes handled, the quotes against the sources'
    checks, the dues collected; printed, and the mechanic must fire.

## 7. What moves, and what is not built

- **The trace moves** (§7): sea tonnes pay a handling bill, the transit bill
  falls by the crews, Freight's payroll rises by them, the treasury gains the
  dues, and a city may build a Bulk Berth. Re-baselined against FR2's branch;
  the ensemble run and read (FR3-notes).
- **Not built here:** the port yard (D7, with FR6); every screen of §6 (FR4);
  the island city's sea-only trade (§9.6, I1); a Handysize row in the freight
  rule.

## Sources (new in FR3)

The spec's [R..] and the oil study's [P..] (`docs/studies/oil-and-ports-research.md` §7) as cited.

- [S1] Dalrymple Bay Infrastructure, results announcement, August 2025 (TIC A$3.72 a tonne from 1 July 2025): https://announcements.asx.com.au/asxpdf/20250825/pdf/06n7wr4672c055.pdf
- [S2] NIMASA, approved stevedoring charges (2019): https://NIMASA.gov.ng/wp-content/uploads/2019/08/approved-stevedoring-charges.pdf
- [S3] Georgia Ports Authority, stevedore use charge on bulk cargo (October 2025): https://gaports.com/blog/mto/stevedore-use-charge-on-bulk-cargo/
- [S4] Forto, general tariff, the Netherlands, Q3 2025: https://explore.forto.com/hubfs/General%20tariff/2025%20Q3/Q3%20General%20Tariff%20_%20Netherlands_valid%20until%202025-09-30.pdf
- [S5] Maersk, terminal handling service to and from the Netherlands (1 October 2025): https://www.maersk.com/news/articles/2025/09/01/terminal-handling-service-origin-destination-for-world-to-from-netherlands
- [S6] Hambantota International Port Group, port tariff 2025: https://hipg.lk/images/tariff/hipg-tariff-2025-new.pdf
- [S7] Mississippi Administrative Code 28-1-04-352, wharfage charges assessed against cargo: https://www.law.cornell.edu/regulations/mississippi/28-Miss-Code-R-SS-1-04-352
