# CM1 - the replay's camera loads its whole move first, then runs on a click ({next}, 2026-11-02) - WIP

**Where it stands (paused 2026-11-02 ~20:35 on the orchestrator's word: resume when WN5b's ship file is on origin):**
1. `batch/CM1` is local only, from origin/main 37d65b2e; slice A committed (MoveTally in MapView, `ui/ReplayMoveProbe`); nothing pushed. On resume: rebase onto `origin/batch/WN5b` (or main if WN5b went to Pending), then `docs/batch.md` item 7.
2. The measure (step 1) is done (section 1, 2026-11-03, off screen); the recording is in the scratchpad `cm1-recording` (a fresh session makes its own: the probe records one when the folder is empty).
3. Next: slice B, the load (brief drafted in the scratchpad, `sliceB.md`): phases LOAD and READY replace the warm-up; the walk paints every plan view (240 a second of the move's clock) with the real paint, caches pinned; a month store for glides across months; the veil and "Ready - click to run".
4. Then slice C: ReplayCheck's section (the real window off screen; skipped where there is no display), then the probe again (after), the notes, `--pick`, the ship file last.
5. Open for the notes: the GROUND tier's coarse planet picture past the plane (pieces are asked only at rest), the globe at half resolution on the move, a whole load at a land-change month, boats' routes per month; CLAUDE.md's "forty-two files" is 43 now (the probe).

## 1. Measured (before)

`ReplayMoveProbe` (slice A), Andorra, a scratch recording of months 366-484 (11,008 people), shown at month 484 unless the move says; the map's canvas 1,492 x 814, the window off screen (2026-11-03 run: about 23 frames a second there, against 40-46 full screen on 11-02, so the shares compare, not the counts). Unpainted = a frame drawing black (a tile or node with no picture yet) or coarse ground (the globe's 7.4 km planet picture where the plane should be).

| move | frames | unpainted | black | coarse | worker asks | let go |
|---|---|---|---|---|---|---|
| M1 fly out, street to Earth | 290 | 158 | 4,169 (FAR 80 of 80 frames, L2 6 of 6) | 42,023 (GROUND 65 of 65) | 637 | 0 |
| M2 fly in | 305 | 113 | 2,718 (FAR 57 of 80) | 39,899 (GROUND 54 of 68) | 533 | 0 |
| M3 zoom-out glide 10 s (warm-up 5.2 s) | 235 | 112 | 1,599 (FAR 59 of 62) | 32,897 (GROUND 53 of 55) | 637 | 0 |
| M4 zoom-in glide 10 s (warm-up 4.3 s) | 229 | 97 | 2,082 (FAR 50 of 61) | 31,226 (GROUND 47 of 52) | 637 | 0 |
| M5 glide in a month, 12 km east at street x4 | 218 | 1 | 0 | 21 | 0 | 180 |
| M6 glide across months 366-484 at the handoff /8 to /16 | 295 | **295** | 11,525 (FAR every frame) | 0 | **5,256** | 0 |
| M7 = M3 again | 236 | 116 | 1,573 | 31,985 | 637 | 28 |

What it says: (1) the black is the far nodes (FAR, about 1 plot a pixel to the handoff), in nearly every frame there, warm-up or not: the warm-up's 25 views reach none of them in time, and the worker is asked about 640 things a move while it runs; (2) a glide across months is black in every frame, because each month is a new map whose nodes are all asked again (5,256 asks); (3) the globe's GROUND tier draws part of the planet from its 7.4 km picture in every moving frame, since its pieces are asked only at rest; (4) the tile levels L0-L2 are clean after the warm-up, but M5 let 180 pictures go and M7's L2 drew stale ones: the caches hold one screen.

## 2. Measured (after slice B)

The same probe, the same recording and window (2026-11-03, off screen), the moves now loaded first and run as a click runs them; the tally counts the run only (the map is veiled while it loads).

| move | load | plan views | frames | unpainted | black | coarse | worker asks | let go |
|---|---|---|---|---|---|---|---|---|
| M1 fly out | 11.4 s | 3,165 | 295 | 51 (GROUND only) | 0 | 38,641 | 0 | 0 |
| M2 fly in | 4.6 s | 3,165 | 292 | 48 (GROUND only) | 0 | 36,603 | 0 | 0 |
| M3 zoom-out glide 10 s | 10.8 s | 2,401 | 229 | 40 (GROUND only) | 0 | 30,728 | 0 | 0 |
| M4 zoom-in glide 10 s | 9.1 s | 2,401 | 236 | 41 (GROUND only) | 0 | 31,489 | 0 | 0 |
| M5 glide in a month | 4.6 s | 2,401 | 215 | 0 | 0 | 0 | 0 | 0 |
| M6 glide across months 366-484 | 14.8 s | 2,401 | 288 | **0** | 0 | 0 | **0** | 0 |
| M7 = M3 again | 12.3 s | 2,401 | 232 | 42 (GROUND only) | 0 | 32,278 | 0 | 0 |

No frame of any move draws black, asks the worker anything or lets a picture go; the glide across months, black in every frame before, is clean. Every load reached Ready with no view passed; the heap stood at 510-720 MB after them (Java's most here about 8 GB). What is left is the globe's GROUND tier: past the plane's nodes and the charts, a moving globe draws the planet from its 7.4 km picture (beyond(): pieces are asked only at rest), in 40-51 frames of each move that crosses it - coarse ground, not black (★1). M6's worst frame is 259 ms (247 ms before): the quick step to a new month's map, not the load (★3).

## ★ Calls (taken as recommended unless Jerus says otherwise)

1. **The globe's coarse ground on a move.** Between the handoff and the planet picture's tier, a moving globe draws what lies past the plane's nodes and the charts from the 7.4 km planet picture. **Recommended: ship CM1 without it and queue CM2**: in a loaded move, widen the charts asked to the whole visible disc in the GROUND tier, so the load fetches them like the rest, measured by the same tally (coarse to 0). Alternative: fold it into CM1 now (a day more; the charts' range and their memory are the work).
2. **Half resolution on the move.** The globe is drawn at half resolution while it moves (82-110 frames a move), for the frame rate. **Recommended: keep it in CM1; CM2 measures full resolution in a loaded move at 1920 x 1080** and takes it if the frame rate holds.
3. **The month step's hitch.** A glide across months has one frame of about 250 ms (before and after CM1): a month's quick step. **Recommended: a line in the todo (measure which month and why), not CM1.**
