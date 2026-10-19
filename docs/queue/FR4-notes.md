# FR4 - notes

*0.8.11, 2026-10-19. The spec is `docs/studies/spec-freight-and-infrastructure.md`
§5, §6 and §7's FR4 row; the mock-ups are `docs/studies/freight/`. Jerus took
every freight call as recommended ("all the freighting just do rec"). Started
at 15:58 with the PC going off at 17:10.*

**Work in progress.** Everything under "What it does" is built and checked
(the suite, the trace, the timing harnesses), and can ship as it is; "Left"
is the part of §6 not built.

## What it does

- **Build › Infrastructure and Build › Freight** (§5's table). "Roads &
  transit" is Infrastructure again, with five groups: Roads, Highways,
  Transit, Ports (the four sea terminals and the Bulk Berth, out of
  Industry) and Reserve (the Strategic Reserve, out of Industry's oil
  storage). "Rail" is Freight: Rail (the rungs and the Rail Depot) and
  Haulage (the Truck Depot). Industry keeps only what investors build, so its
  dot is honest again; the Tank Farm stays there (Refining's); the Heavy
  Truck Plant was already on Vehicles. The identifiers `BuildAdvice.ROADS`
  and `RAIL` keep their names; their strings changed, and each category's
  `was` holds the old label, so a door still holding it lands right.
- **FreightView** (new, pure; `FreightViewCheck`): the city's freight this
  month in legs and modes (long haul: world lorry, rail, sea; city leg:
  truck, siding), each bar's division; Haulage's fleet, depots, the city
  leg, the quote with the rule's target; the railway's sets, track, tonnes,
  FR6's home and quay tonnes, the quote as a share of the lorry rate with
  its floor; the berths by kind (count, capacity, tonnes, use, calls,
  handling, dues, the quote with the due as its floor and the rule's
  target); the shippers billed, largest first. Two read-only getters:
  `Ports.berthCounts()` and `Haulage.getTarget()` (closeMonth's own
  `rawTarget()`, so they cannot drift).
- **Infrastructure › Freight and › Ports.** The railway and freight pages
  are one Freight page under a division strip (All, Haulage, Rail, Ports).
  All is the old Freight page, Rail the old railway page, Haulage new (the
  quote and target, the city leg, the trucks, the depots, the business),
  and the Ports chip is the new Ports page: the month (handling, dues,
  tonnes, calls, the dial), berths by kind with tonnes against capacity,
  handling and dues by kind, the ships that called by class, and the doors
  "The ships on the map" (the map opened on the berths), its books, Berths
  on Build. `open("The railway")` still works: Freight on Rail.
- **Policy › Taxes › Port dues** (FR3's dial): the dial from none to
  `TaxPolicy.MAX_PORT_DUES`, the default marked; the preview's rows are the
  month's dues, the handling a tonne by kind, and the budget
  (`PolicyPreview.portDuesAt()`, `handlingQuoteAt()`; PolicyPreviewCheck
  10). The Government screen's "Port dues" line opens it.
- **Sectors › Freight** (board 2): its Operations page opens on the division
  strip, above the flow card. All is the month's freight in legs (a long
  haul bar of world lorry, rail and sea, a city leg bar of truck and siding,
  on one scale, the paid-abroad parts hatched, each bar a door to its
  division) and a summary card a division; Haulage the trucks against the
  need, the fleet, the month, the price card (quote and target) and the
  shippers billed; Rail the wagon sets against the track's need, the
  tonnes with the siding and the port yard, the price as a share of the
  lorry rate over its floor; Ports a row a kind of berth with its fill, the
  handling quote with its due and target, the dues and the calls.

## The model is not changed

FR4 reads and draws. `AutoBuilder.inRemit()` names the ports and the reserve
out, since they now sit in a category the city builds: they were out of the
remit by being in Industry, and they stay out (the berths are ordered on
their payback through `orderable()`, as since FR3). Every other model reader
of `cityBuilds()` reads by type or by measure, which did not move.
The default playtest's trace (`LongPlaytest`, 270 lines) is byte-identical
to main's at 4b27f49 (0.8.10) but for its run-time line (96 s against 94 s),
so there is no re-baseline.

## Who wrote what, and what review changed

Each slice went to a Sonnet subagent (xhigh; no DeepSeek key on this PC) with
a brief naming its own files, compiling into a folder of its own so four
could run side by side; Claude reviewed each diff as a stranger's and
committed it.

| slice | files | what review changed |
|---|---|---|
| Policy's dues dial | `ui/PolicyScreen`, `PolicyPreview`, `PolicyPreviewCheck` | Nothing in it; Claude added the Government line's door (`policyPlace`), which the agent named and left. Checked that nothing in the model calls `PolicyPreview`. |
| FreightView | `FreightView`, `FreightViewCheck`, `AllChecks`, `Ports`, `sectors/Haulage` | `Haulage.getTarget()` repeated `closeMonth()`'s arithmetic; Claude made both read one `rawTarget()`. |
| Infrastructure › Freight, Ports | `ui/InfrastructureScreen` | Nothing; the one figure it works out (the share the trucks carried) is a ratio of two of the view's. |
| Build's tabs | BuildAdvice, BuildCard, AutoBuilder (inRemit()), BuildingType (comments), ui/BuildScreen, BuildAdviceCheck, BuildCardCheck, BuildMenuCheck, RoadCheck | Its own checks were green (8 harnesses). Claude added "Infrastructure" to Icons.ofCategory() (the tab had lost its icon) and turned SectorScreen's two "Roads & transit" doors into BuildAdvice.ROADS, both outside its files, as it reported. Checked every model reader of cityBuilds() and categoryOf(): by type or by measure, unmoved; the trace agrees. |
| Sectors › Freight | ui/SectorScreen | Nothing; its arithmetic is the bars' widths. |

## The checks

`Batch check.bat --maps` on the finished tree (the docs pass running
beside it, so timing soft): 121 harnesses, 120 passed. The one red is
ScaleCheck's "...and it builds", red on main too (FR7-C). Stale: 0 firm
findings, once README's count of harnesses said 120 (FreightViewCheck is
new). The maps are regenerated. FreightViewCheck runs in about 2 s on
three cities (none, a full one with rail, the port yard, three kinds of
berth, a depot and dues, and one railed with no berth); PolicyPreviewCheck
gained its section 10 (two played berth cities). Last and alone,
`Batch check.bat --timing`: 8 timing harnesses hard, 7 passed; the eighth is
ScaleCheck's "...and it builds" again, main's red, as FX4 found it.

## What Jerus checks by eye (FR4-C)

- **Build:** the strip's Infrastructure tab with its five groups and a berth's
  card with its payback line; the Freight tab's Rail and Haulage groups and
  the Truck Depot's investors' line; Industry with no ports and no "only the
  city builds these".
- **Infrastructure › Freight:** the division strip's height (DIVISION_CHROME
  38 px is a guess), each division's page, a door from the Roads page's
  freight stream landing on Freight.
- **Infrastructure › Ports:** the berths' bars (one colour a kind), the
  calls by ship class, "The ships on the map" opening the map on the quays.
- **Policy › Taxes › Port dues:** the dial, its default mark, the preview
  rows, and the Government screen's door to it.
- **Sectors › Freight › Operations:** the strip, the legs bars' labels and
  key at a narrow window, the berth rows' widths, the three summary cards in
  a row; the mode colours are Palette's, not the study's exact hues.

## Left

Not built, for a later batch (FR4's rest):

- **"How its tonnes move"** (board 3): the strip on every works' Operations
  page, the siding's saving and the pellet plant before and after. The model
  keeps a shipper's bills, not its tonnes by leg and mode.
- **The Income page's division column** (board 2's third bullet).
- **The berth card's payback line** on Build (board 4): nothing in the tree
  draws one yet; BuildAdvice's berth payback is there to read.
- **Trade's band in legs** (§6's last bullet).
- **The manual's** freight pages.
- **What the model does not keep**, so the screens leave out: the vans'
  tonnes (the distribution leg is words), Haulage's floor (its running cost
  is a local in closeMonth()), a shipper's tonnes, the trucks against the
  need over 24 months, the drivers, and the ship calls by class (the Ports
  page counts game.boats() itself, marked // FR4: FreightView).
