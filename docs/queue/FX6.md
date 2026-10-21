# FX6 - a full district that buys land inside itself keeps its buildings where they stand (2026-10-21)

The brief for batch FX6. `QUEUE.md` links here. It fixes a fault in CL3 (0.8.14). Jerus placed it next, on 2026-10-21, when he shipped CD1: "Ship CD1 now with that red noted, and queue a small fix".

## The fault

CL3 made buildings stay where they were put. Each district keeps a log of its arrivals, and its plan places them in that order.

On CD1's city path (0.8.15), at month 3,998, the city buys land inside district (-2,-2), which is already full (its owned plots run from 28,217 to 33,731). The plan, replayed from its log with the larger room, then moves 2,069 of the district's 2,946 buildings, 1,727 of them houses.

LongPlaytest's layout watch catches it and ships red on main because of it. The watch excuses only a district whose carry-in changed, and this one's did not. Main's playtest before CD1 never took that path. It is the "re-chaining by another door" CL3 left under CL3-C. See docs/queue/CL3-notes.md and CD1-notes §2.

## What is right

Land bought inside a district, full or not, adds room. It never re-places what already stands. The buildings already placed keep their plots, and only new arrivals go on the new ground.

## Done

- A harness section whose fixture causes it: a full district buying land inside its own bounds. It fails on 0.8.15, and passes after the fix.
- LongPlaytest green again.
- The map's alone: the trace byte-identical, and no ensemble.
- Renders before and after; FX6-notes; the row out; the version after main's; `--timing` hard and alone.
