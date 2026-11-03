# CM1 - the replay's camera loads its whole move first, then runs on a click

Queued 2026-11-02 from Jerus, for the trailer: "when zooming out in automatic
mode, and gliding or zooming in, allow the game to load up so that when it
zooms or glides there is no loading, basically it could just say loading 10
seconds, then let you click to run it, just cause for trailer if half the
screen is black while you zoom out well... not for the normal game, only while
in replaying mode."

## What there is

TR1 (0.8.92) gave the replay its camera (`ui/ReplayCamera`, the arithmetic in
`CameraPath`, applied by `ui/MapView`, owned by `ui/ReplayMode`): two marked
views and the glide between them, the fly out to the whole Earth and back, and
a warm-up before a glide. The warm-up is bounded, and that is where the black
comes from:

- it walks `WARM_VIEWS` (24) steps of the path, so 25 views, however many
  scales a zoom from a street to the planet passes;
- it stays on a view at most `WARM_VIEW_S` (1 s), and walks for at most
  `WARM_MOST_S` (8 s) in all, then "the glide starts with what is painted";
- it warms the start month's map only; a glide across months paints each later
  month as it comes;
- whether the fly out to the Earth and back (`Phase.FLY`) warms at all, and
  whether the tiles warmed are still held when the move reaches them (MapView's
  caches), is for the batch to measure.

## What the batch does

1. **Measure first.** In a recorded city (slot 10 or a scratch folder, never
   slots 1-9), count for each of the three moves (a zoom out, a glide, a zoom
   in; within a month and across months) the frames that show unpainted ground,
   and say which of the causes above makes them.
2. **Load the whole move before it runs.** In replay mode only, a camera move
   first loads everything it will show: every scale and tile along the path, at
   the frame rate it will run at, for every month it crosses, with no time cap.
   What it loads is kept until the move has run (pinned in MapView's caches,
   not evicted by the walk itself).
3. **Then wait for a click.** While it loads, the screen says so ("Loading the
   move... 10 s", counting down from a fair estimate, or a share done). When it
   is done it says "Ready - click to run", and the move starts on the click (or
   a key), so a screen recording can be started first. Escape cancels.
4. **The normal game is untouched.** Nothing outside replay mode loads ahead,
   waits or pins tiles; MapView's paths outside the replay are as they are.

## The checks

ReplayCheck: a section whose fixture is a long zoom out across scales and a
glide across months, asserting that after the load no frame of the move asks
MapView's worker for anything new and none draws unpainted ground; and that the
normal game's map paths take no new branch. The look of the "Loading" and
"Ready" lines is an eye check (CM1-C) for Jerus, in the replay, at 1920 x 1080.

UI and the map's tile caches: Opus with Sonnet slices. Threading (MapView's
worker, the FX thread) is involved, so a stranger's review at the gate.
