# FX7

**The map's "an old works moved" on a grown city.** Jerus, 2026-10-22, on
shipping AN1 (0.8.21) with LongPlaytest red: "Ship, queue a map fix".

## What is wrong

CL3's layout watch in LongPlaytest ("buildings that stay where they were
put", `docs/queue/CL3-notes.md`) fires on AN1's default playtest: of 15
industry boxes standing at m1001, only 4 stand where they were at m4000.
District (0,0) held 4 bottling plants at m1001 and 63 at m4000
(`docs/queue/AN1-notes.md` §5 ★3). AN1 changed no map code. Its city grows to
625,815 people, against XR1's 10,980, and no playtest has grown a city on
this path since XR1. 0.8.19's classes with AN1's lag read the same finding,
so it is the map's. FX6 (0.8.16) fixed the watch's last finding, a full
district that buys land inside itself.

## What done looks like

1. **Find the cause first**: which boxes move, in which months, and on what
   path through `CityMap` / `DistrictPlan` (CL3's order of arrival, FX6's
   dated ground, CL4's avenues). Write it up in `docs/queue/FX7-notes.md`.
2. **Fix it in the map**, so the watch reads clean on the default playtest
   with its premise unchanged. If the move turns out to be right (a works
   that should move), stop and report: restating the watch is Jerus's call,
   not the batch's.
3. A MapCheck section whose fixture causes the move, failing on 0.8.21 and
   passing on the fix.
4. The map draws nothing the model reads, so the trace should not move. If
   it does, say why.
