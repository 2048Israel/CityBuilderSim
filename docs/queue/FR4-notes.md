# FR4 - notes

*0.8.11, 2026-10-19 and 2026-10-20. The spec is
`docs/studies/spec-freight-and-infrastructure.md` §5, §6 and §7's FR4 row;
the mock-ups are `docs/studies/freight/`. Jerus took every freight call as
recommended ("all the freighting just do rec"). Begun on 2026-10-19 with the
PC going off at 17:10, and finished on 2026-10-20 on his word at the review
desk ("Finish it now").*

**Ready.** Every piece of §6 is built; the model's figures are unchanged and
the trace is byte-identical to main's.

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
- **A berth's card pays back** (board 4): one line from
  `BuildAdvice.berthPayback()`, the advice's own verdict (`portUncovered()`
  against `Ports.berthPaysBack()` at the dial, which `portTerminal()` asks):
  "pays back in 12 years: dues $632k a month on 2.5 Mt a year", or why it
  would not (too few tonnes, no cargo of the kind, the dues at nothing, the
  berths already taking every tonne). The rail and depot cards say Freight
  owns them (the docs pass's flag), and the Truck Depot's investors' line is
  "Freight would build one: N trucks over its depots' room".
- **FreightView** (new, pure; `FreightViewCheck`): the city's freight this
  month in legs and modes, each bar's division; Haulage's fleet, depots, the
  city leg, the quote with the rule's target; the railway's sets, track,
  tonnes, FR6's home and quay tonnes, the quote as a share of the lorry rate
  with its floor; the berths by kind; the shippers billed, largest first.
  `tonnesOf(game, sector)` is one sector's month by good, way and mode, its
  bill by division and what a siding saves or would save; `plantOf()` is the
  pellet plant at nameplate by truck and with a siding.
- **TradeBand** (new, pure; `TradeBandCheck`): a good's import-export gap in
  legs, a unit and the month's trade at today's prices - the world's margin,
  the world lorry, rail, sea, port handling and the city leg.
- **Sectors › Freight** (board 2): its Operations page opens on the division
  strip (All in legs, each bar a door to its division; Haulage; Rail; Ports),
  and its Income page's revenue note lists the shippers billed by division,
  each figure a door to that sector's input line.
- **How its tonnes move** (board 3), on every works' Operations page with
  freight: each good's legs by mode in the order the tonnes travel, the
  month's bill by division, and the siding line ("a siding would carry 75%
  of X t: about D$Y a month less haulage", or what its siding saved); Iron &
  Steel's Pelletising Plant all by truck beside with a siding.
- **Infrastructure › Freight and › Ports.** The railway and freight pages
  are one Freight page under a division strip; the Ports page has the month
  (handling, dues, tonnes, calls, the dial), berths by kind with tonnes
  against capacity, handling and dues by kind, the ships that called by
  class, and the doors "The ships on the map", its books, Berths on Build.
  `open("The railway")` still works: Freight on Rail.
- **Policy › Taxes › Port dues** (FR3's dial), with its preview
  (`PolicyPreview.portDuesAt()`, `handlingQuoteAt()`; PolicyPreviewCheck 10);
  the Government screen's "Port dues" line opens it.
- **Trade › What we trade**: the card "The band in legs", one bar for the
  month's trade and, under details, a row a good.
- **The manual** (the artifact "CityBuilderSim"), published as version 20: its header at 0.8.11 and save format 40, new pages on the legs and the
  Freight sector's three divisions, Haulage, trains and trucks on the ground,
  the freight screens and Build's two tabs, and the roads', railway's, ports'
  and vehicles' pages brought up to the freight batches; `docs/manual.html`
  and `docs/manual.md` pulled from it. Its other sections are as they were
  (its header's counts of buildings, sectors and harnesses among them).

## The model is not changed

FR4 reads and draws. `AutoBuilder.inRemit()` names the ports and the reserve
out, since they now sit in a category the city builds: they were out of the
remit by being in Industry, and they stay out (the berths are ordered on
their payback through `orderable()`, as since FR3). Every other model reader
of `cityBuilds()` reads by type or by measure, which did not move.

One saved tally: "How its tonnes move" needs a sector's month by good, way
and mode after the month, when the sectors' unit rows have been cleared and
the shares overwritten, so `Freight.haul()` tallies them where it bills,
from the same variables (`Haulage.movesOf()`, saved under `haul.move.*` with
Freight's extras, cleared in `reset()`). Nothing in the model reads it; the
save format is unchanged (a missing key reads empty). Three helpers are the
model's own sums made callable, so a screen cannot drift from them:
`Haulage.getTarget()` (closeMonth's `rawTarget()`),
`PortDivision.duesRateFor()` (duesRateOf()'s sum for any book value) and
`Ports.seaFactor()` (the ships' part of `Ports.factor()`).

The default playtest's trace (`LongPlaytest`, 270 lines) is byte-identical
to main's (rebuilt at 0c5e3af on 2026-10-20, itself identical to 4b27f49's)
but for its run-time line, so there is no re-baseline.

## Who wrote what, and what review changed

Each slice went to a Sonnet 5.5 subagent at xhigh with a brief naming its
own files, compiling into a folder of its own so several could run side by
side; Claude reviewed each diff as a stranger's and committed it.

| slice | files | what review changed |
|---|---|---|
| Policy's dues dial | `ui/PolicyScreen`, `PolicyPreview`, `PolicyPreviewCheck` | Nothing in it; Claude added the Government line's door (`policyPlace`), which the agent named and left. Checked that nothing in the model calls `PolicyPreview`. |
| FreightView | `FreightView`, `FreightViewCheck`, `AllChecks`, `Ports`, `sectors/Haulage` | `Haulage.getTarget()` repeated `closeMonth()`'s arithmetic; Claude made both read one `rawTarget()`. |
| Infrastructure › Freight, Ports | `ui/InfrastructureScreen` | Nothing; the one figure it works out (the share the trucks carried) is a ratio of two of the view's. |
| Build's tabs | `BuildAdvice`, `BuildCard`, `AutoBuilder` (`inRemit()`), `BuildingType` (comments), `ui/BuildScreen`, `BuildAdviceCheck`, `BuildCardCheck`, `BuildMenuCheck`, `RoadCheck` | Claude added "Infrastructure" to `Icons.ofCategory()` (the tab had lost its icon) and turned SectorScreen's two "Roads & transit" doors into `BuildAdvice.ROADS`, both outside its files, as it reported. Checked every model reader of `cityBuilds()` and `categoryOf()`. |
| Sectors › Freight | `ui/SectorScreen` | Nothing; its arithmetic is the bars' widths. |
| How its tonnes move, the model | `FreightView`, `FreightViewCheck`, `sectors/Haulage`, `sectors/Freight` | Nothing; checked the tally reads the bill's own variables and that FreightViewCheck 7 holds its sums to the model's counts (trucks to `Haulage.getTonnes()`, rail, sea, the home and quay tonnes), and the trace. |
| The berth's payback, the Freight cards | `BuildAdvice`, `BuildCard`, `ui/BuildScreen`, `BuildCardCheck` | It composed the dues rate from PortDivision's statics; Claude moved that sum into `PortDivision.duesRateFor()`, which `duesRateOf()` and the card both read. |
| Sectors: the shippers, How its tonnes move | `ui/SectorScreen` | The pellet card's (i) quoted 150,000 t as prose; it now points at the card's own figures. |
| Trade's band in legs | `TradeBand`, `TradeBandCheck`, `AllChecks`, `ui/TradeScreen` | It split the sea leg from `freightInBand()` by repeating `Ports.factor()`'s sea term; Claude gave Ports that term as `seaFactor()`, which both read. |
| The manual's freight pages | the artifact's page (not in the tree) | Read in samples against the code and the notes; published by a separate agent, which had to read the whole live page first (the artifact tool's rule), so it stayed out of this conversation; the pulled page is the edited page byte for byte. |

## The checks

`Batch check.bat --maps` on the finished tree, main merged in (0c5e3af):
122 harnesses, 121 passed. The one red is ScaleCheck's "...and it builds",
red on main too (Jerus queued its fix as SC1). Stale: 0 firm findings, once
README's count said 121 (FreightViewCheck and TradeBandCheck are new). The
maps are regenerated. The new harnesses and sections: FreightViewCheck (1-6,
and 7 for How its tonnes move, its tally's save round-trip among it),
TradeBandCheck (6 sections, 41 assertions), BuildCardCheck 13 (the berth's
payback against the advice's verdict, the Freight cards), PolicyPreviewCheck
10 (the dues dial). TIMING_PLACEHOLDER

## What Jerus checks by eye (FR4-C)

- **Build:** the Infrastructure tab's five groups, a berth card's payback
  line (green, amber or grey); the Freight tab's Rail and Haulage groups,
  the Truck Depot's investors' line; Industry with no ports.
- **Sectors › Freight:** the Operations strip, the legs bars' labels and key
  at a narrow window, the berth rows' widths; on Income, the shippers table
  at 560 px and its doors.
- **How its tonnes move:** on a works with bulk (Iron & Steel, Mining), the
  lines' two legs, the bill bar, the siding line; the pellet plant's two
  columns.
- **Infrastructure › Freight and › Ports:** the division strip's height
  (`DIVISION_CHROME` 38 px is a guess), "The ships on the map".
- **Policy › Taxes › Port dues** and the Government screen's door to it.
- **Trade:** the band's card and its rows a good. On TradeBandCheck's played
  city the port handling is 56% of the band (D$2.2M of D$4.0M): FR3's
  handling quote as it stands, worth a look (FR3-notes §4 on the general
  cargo dues).

## What the model does not keep

So the screens leave it out, and say so where it would be: the vans' tonnes
(the distribution leg is words), Haulage's floor (its running cost is a local
in `closeMonth()`), a shipper's railway bill split into long haul and city
tonnes, the trucks against the need over 24 months, the drivers, and the ship
calls by class (Infrastructure's Ports page counts `game.boats()` itself,
marked `// FR4: FreightView`). Trade's band is the month's trade at today's
prices, since the month's lorry and sea money are one figure
(`Freight.getPaidAbroad()`) and the bills are not kept by good.
