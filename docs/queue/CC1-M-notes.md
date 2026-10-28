# CC1-M - the city map across a save (0.8.63, 2026-10-28)

The batch's record. Its brief was the row alone (`runs/fixCC1-notes.md` item 7 was
never committed): "The city map did not survive a save once: LongPlaytest
-Dplaytest.childcare at m1,184 (slot 3)", queued by CC1 at 0.7.139 on 2026-10-12, its
own batch by Jerus on 2026-10-18; and FW6-C's ★1, whether the first month after a
load moves the drawn railway. Opus wrote the probes and the brief, Sonnet 5.5 (xhigh)
the slice, Opus reviewed and committed.

## 1. What did not survive (brief item 1)

**The failure of 2026-10-12 is mended, by FR6, and was never linked to this row.**
CC1's tree (4dde3ff3, 0.7.139) rebuilt in a scratch folder and its childcare playtest
run again: "the city map across a save 1 time(s), months 1184-1184", as CC1 saw it.
With a diagnostic in `roundTrip()`: the districts and the shore read back the same,
the runs did not - the railway net, not started, had its hub at (365320, 373800) in
the live map and (0, 0) in the one read back. `Net.reset()` (the last Rail Depot gone)
kept the hub; a net with no arms writes no runs (`runs() == 0`), so the load laid
fresh ones. FR6 met the same edge in its own playtest at m1186 and mended it on
2026-10-19 (64ab8773, `hubX = hubY = 0` in `reset()`). That line on 0.7.139's tree:
9 of 9 reloads the same. On main the childcare playtest reads 10 of 10 back the
same, the default 9 of 9.

**The month after a load (FW6-C's ★1): the drawn railway does move, on a coast.**
Each map below was saved, read back, and the two played on side by side; compared
were `same()` and the sidecar each writes, byte for byte (scratch probes, not
committed):

| city | read back at | result on main |
|---|---|---|
| ReloadCheck's probe city (Danzik) | m35, 123, 363, 1205 | the same, at the load and 3 months on |
| the same on the Earth (Rotterdam) | m65, 123, 245, 485 | the same, 3 months on |
| the default playtest's city kept at m4001 (25 districts, 2,358 highway plots, 36 arms) | 3 loads, 40 months apart | the same, 3 months on each |
| a part-grown island link (IslandNetCheck 1b's neck ground) | 30 points | its way planned again, the same net grown on |
| GatewayCheck 16's coast city, laid month by month | every 5th month | **12 of 23 differ**, every load from m60 on |

The parts, as the brief names them: the **stamp** follows the bytes; the **plans**
are drawn from the districts and the runs, never saved, and are the same where those
are; the districts, their logs and journals and the shore came back the same
everywhere; the **runs** differed only in the **railway**, and only in the month
after the load; the **sidecar's format** (12) lacked the one field that month reads.

**The cause.** `CityRuns.Arm.stoppedBy` - what last stopped an arm: OPEN, EDGE or the
SEA - was transient. `cleanSpine()`, at the start of every `layRail()` that runs,
reads it: the main line is the gate arm and the spine arms after it for as long as
each waits at the sea; the first that does not ends the line, and the arms past it
are taken back to nothing. A map just read has every arm OPEN and `railStampSet`
false, so its first month re-lays the railway in full, takes the main line back past
its first sea turn, and lays it again in one month, on the city as it stands, where
the live map grew it over many - on the coast city 4 plots shorter (293 against 297,
the last arm 10 steps against 14), and it stays shorter. A player sees it as: a coastal
city's railway, after a load, stops a little short of where it ran before the save.

## 2. The fix (item 2)

The sidecar carries `stoppedBy`: **FORMAT 13**, a byte after each arm's crossings,
both nets (`CityRuns.write()`/`read()`; a byte other than OPEN, EDGE or SEA makes the
sidecar unread). A sidecar of FORMAT 12 or older reads OPEN, as before. The coast
sweep with it: 0 of 23 loads differ, byte for byte to month 130. `same()`, the rail
stamp and `layRail()` are untouched: the first month after a load still re-lays the
railway (it has no stamp to compare), and now lays what was there.

No save-format change: the sidecar is the map's alone, `SAVE_FORMAT` stays.

**ReloadCheck's `mapStamp` exemption is removed** - a harness premise moved, and this
is why: it was exempt because the first month after a load re-lays the railway
(FW6, ★1), so the sidecar's bytes "can differ". The re-lay now lays the same map, and
on ReloadCheck's own cities the bytes were the same a month and two on even on main
(the table's first two rows). A map that moves after a load is a red line there now.

## 3. The harness (item 3)

GatewayCheck **21, the coast across a save**: section 16's coast city laid month by
month to `COAST_SAVED_AT` (100), where its gate arm waits at the sea and the main
line runs on past it (the fixture line); the sidecar read back the same, every arm's
`stoppedBy` with it, the file byte for byte; both maps played on with the same counts
to `MapCheck.MONTHS` and `COAST_HELD` months past it, the same map and the same file
every month; and the condition is the fixture's: the same map written at FORMAT 12
and read back lays a shorter railway the month after.

On the final tree: at month 100 the railway has 297 plots, its gate arm waits at the
sea, 3 arms after it laid; 30 months compared after the load (101 to 130), 0 differ,
297 plots on both at the end; the FORMAT 12 copy 293 the month after, against 297.
Before the fix the same sweep had every load from m60 on differ from its first month.

## 4. Checks and the trace

- `./batch-check.sh --pick`, earth-2.bin named: 19 harnesses (the map's family,
  ReloadCheck, SaveFileCheck, ReadPathCheck, LongPlaytest, TimingCheck), 18 passed,
  **exit 3**: MapCheck's recorded red alone (`gate.txt`, section 8's one network).
  GatewayCheck green with section 21; IslandNetCheck (which writes and reads the runs
  at `CityMap.FORMAT`) green; ReloadCheck green with `mapStamp` compared - the probe
  city at 35, 123, 365 and 1205 and Rotterdam at 123, a month and two on. Maps
  regenerated, Stale 0 firm.
- **The trace**: the default playtest on main's classes (104eb492) and the branch's,
  each built apart: identical but for its run time and the layout watch's seconds.
  The model is untouched, so **no ensemble** (EN1).

## 5. Calls

- ★1 **FW6-C's ★1 is answered**: the drawn railway did move after a load, on a coast
  whose main line turns at the sea, and no longer does. Recommended: the
  orchestrator drops the ★1 clause ("The map after a load: ReloadCheck exempts
  `mapStamp`...") from FW6-C's Pending row; the row's other items stand.
- ★2 **A coastal city saved before 0.8.63** has a FORMAT 12 sidecar, and loses those
  few plots of its main line once, in the month after its first load in 0.8.63.
  Recommended: accept - no conversion code (CLAUDE.md, the old saves rule), the map's
  alone, and a save written after that month carries FORMAT 13.
- ★3 **LongPlaytest's round trip compares the map at the load only**, never a month
  on, and its flag does not say which part differed. Recommended: leave it - the
  trace must not move for a map check, GatewayCheck 21 holds the month after, and the
  probe that names the parts took a diagnostic of a dozen lines when it was needed.

## 6. Left, and found on the way

- Still transient, and looked at: each arm's `waitAt`, `knownAt` and `known`, each
  net's `stuckAt` and `pieceStuck` (versions of the ground, which a load renumbers),
  and a link arm's plan. Each only spares work that is redone the same; none showed a
  difference on the cities above.
- The probes are in the session's scratch folder, not the tree; GatewayCheck 21 is
  their fixture for the case that mattered.
