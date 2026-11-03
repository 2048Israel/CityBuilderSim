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
