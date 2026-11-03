# FC1b - FC1's review follow-ups: a cancelled order leaves the foreign contractor's book, and overtime on the city's own crews alone ({next}, 2026-11-02)

The stranger's review at FC1's gate (SHIP + FOLLOW-UP, `docs/queue/FC1b.md`) found two leaks in the foreign
contractor's month. Both fire only in a city in the builders' trap, which no ensemble seed reached; the money audit
held in both. Lane 1, after LD2.

## 1. What was built

- **A cancelled order leaves the book.** `Construction.cancelOrder(refund, points, offTheBook)` takes the order's
  points left at the stop off the contractor's book, never below nothing (`Game.settleConstructionControl()`, C). A
  depot of the builders' served first takes none off: he builds it as theirs and never had it on his book
  (`Construction.bill(.., t)`). `BuildingManager.stopCancelled()` reads that (`goesFirst()`) while the site is still on
  site and carries it on the month's `ConstructionControl.Refund` (`servedFirst`). Before, the book kept the order:
  the contractor built other orders' points at `FOREIGN_PRICE` for points sold at the city's price, and a cancel and
  restart (`Game.restartShell()`) put the points on his book twice. `capForeignBook()`'s javadoc now says what it
  still covers: the book's points built by the city's own crews instead, once the whole queue is under the book.
- **Overtime on the city's own crews' part alone.** `BuildingManager.advanceConstruction(output, foreignPoints)` and
  `plan(output, foreignPoints)` take the contractor's points of the month (SimulationEngine passes his offer). A
  rushed site's own part of its share is the share times the month's own points over all its points
  (`ConstructionControl.Overtime.own()`, new); only that part is worked over the hours, and the premium (paid to the
  builders, paid out as wages) is struck on it. Before, the city paid its households overtime for hours his crews
  worked, and the builders earned points nobody worked. With no contractor the own part is the whole share, and the
  month is worked to the bit as it was (the arithmetic is arranged so: `(share - own) + own x factor` with `own ==
  share` is `share x factor` exactly).
- **ConstructionTrapCheck 9-11** (134 -> 234 assertions, about 7 s more):
  - 9, a cancel in the trap: twin trapped cities, the University (26,000 points) ordered while he takes orders and
    cancelled in one. His book is the kept twin's 30,200.00 less the Refund's 25,875.59 points, his month (400
    points, billed 142.82) the same to the bit. Restarted, the book is the kept twin's again, not that plus the
    order's points. Two depots the city orders in the trap, served first, cancelled: the book does not fall.
  - 10, a rush beside the contractor: the trap's sliver, 16 own points beside a 400-point offer. The Penitentiary's
    share 228.586, its own part 8.792; the premium, the work, the extra points and the treasury's payment all on that
    part; he is paid his whole offer. A city with no contractor: `own() == share()` to the bit.
  - 11, saved mid-trap: after two trap months, saved and loaded, the next month is the same to the bit (his book,
    points and bill, the builders' cash and order book, the points built, the price multiplier, whether he takes
    orders).

| slice | drafted by | what the review changed |
|---|---|---|
| the model (Construction, BuildingManager, ConstructionControl, Game, SimulationEngine) | Opus | - |
| ConstructionTrapCheck 9-11 | Sonnet 5.5, xhigh | section 10's bill checked against the offer's price at section 4's tolerance (the month restrikes its money constants at its top), not the base read after the month |

## 2. The calls (★; each takes the recommended way, Jerus asleep)

- ★1 **The book holds points, not orders.** A cancelled order's points come off it whole, whether or not that order
  was on it (a depot served first excepted). An order placed before the trap and cancelled in it therefore shortens
  his work by its points: he builds less, the city's crews build those points later, and the builders pay him less.
  The money audit holds either way. The other ways: a share in proportion (the book times the order's points over all
  owed) leaves most of a trap order on the book, the very leak the review named; tracking each site's points on his
  book by name is a saved field per site and a new book, for a case no city of the ensemble met. **Recommended: as
  built.**
- ★2 **His crews are spread over the sites as the month is.** The own part of a rushed site's share is the month's own
  points over all of them, though his points for the builders' depots go to the depots first. In the trap the own
  part is a sliver (16 of 416 points in the fixture), so a rush there buys almost nothing, as it should: the city has
  no crews of its own to rush. The Construction page's "+N% on overtime" reads work over share and shows the smaller
  figure with no change. **Recommended: as built.**

## 3. Checks

- `./batch-check.sh --pick` on main with LD2 (0.9.5) merged, the gate's files named (`JDK_JAVA_OPTIONS`): exit 0,
  28 harnesses green in 397 s (the 26 that read the diff, LongPlaytest in place of Game's and BuildingManager's 221
  readers, left to the gate), the playtest clean, Stale 0 firm. ConstructionTrapCheck 234 assertions green.
- **The trace**: byte-identical to LD2's tip (2c3db93e), all eight CSVs, the default playtest on the gate's files
  (`-Dcbs.earth=data/earth-2.bin -Dcbs.world=data/world-1.bin`); the logs differ only in the windows' run-time
  line, and both find LD2's own red word for word (`a building on the map moved 4 time(s), months 3010-3013`,
  LD2-D). No ensemble. (Without the files both find `industry stands farther out than the homes` at m4000 instead,
  again the same in both.) On FC1's tip, before LD2, it was byte-identical as well.
- Found on the way (section 11): a reloaded city plays its next month apart from the twin when the fixture holds its
  sectors (`DeathCareFirstCheck.city()`, `BusinessInvestment.holdSector()`), because a hold is a harness lever no save
  carries; the reloaded city is held again in the section. No saved field was missing.
