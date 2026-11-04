# CM2 - the replay's loaded move on the globe: no coarse ground, full resolution, no month hitch

Found by CM1 (2026-11-03, its ★1-3, taken as recommended). Jerus films his trailer from a replay (2026-11-02: "if half the
screen is black while you zoom out well... not for the normal game, only while in replaying mode"). CM1 loads a camera move
whole before it runs, and after it no frame of any move draws black, asks the map's worker anything or lets a picture go
(`docs/queue/CM1-notes.md` §2). Three things are left, all in replay mode only:

1. **Coarse ground on the globe.** In the GROUND tier (from the handoff, `GlobeFrame.HANDOFF_KM`, to the planet picture's
   tier), a moving globe draws what lies past the plane's nodes and the charts from the planet's 7.4 km picture
   (`MapView.beyond()`: the pieces are asked only at rest, from the full-resolution compose). CM1's probe counts it in 40-51
   frames of each move that crosses the tier, about 700 sampled points a frame (`MoveTally`'s GROUND coarse). Wanted: in a
   loaded move, the charts asked cover the whole visible disc in the GROUND tier, so CM1's load fetches them like the rest
   (`chartNodes(globe)`'s range, widened only while `move != null`), with the heap guard (`LOAD_HEAP_SHARE`) left to refuse a
   move too wide; coarse to 0 in the probe's M1-M4 and M7.
2. **Half resolution on the move.** The globe is drawn at half resolution while it moves (`globeHalf`, 82-110 frames a move).
   Measure a loaded move at full resolution at 1920 x 1080 (the probe, `-Dcbs.probe.onscreen=true` on a 1080p screen, or the
   window sized so); take it if the frames hold the rate the half-resolution ones do.
3. **The month step's hitch.** A glide across months (the probe's M6, 119 months in 10 s) has one frame of about 250 ms,
   before CM1 and after it: a month's quick step (`ReplayCity.stepQuick`, `Game.replayMap`), not the load. Find which month
   and what takes the time; if it is work the load could do ahead (a land-change month's whole load, the boats' routes), do
   it there.

The measure is CM1's: `ui/ReplayMoveProbe` (its class comment says how to run it; it records its own city in an empty
folder), before and after, and ReplayCheck 5d keeps asserting no asks, nothing unpainted and nothing let go. The normal
game stays untouched (everything behind `move != null` or the replay's camera).
