# CM1 - the replay's camera loads its whole move first, then runs on a click ({next}, 2026-11-02) - WIP

**Where it stands (paused 2026-11-02 ~20:35 on the orchestrator's word: resume when WN5b's ship file is on origin):**
1. `batch/CM1` is local only, from origin/main 37d65b2e; slice A committed (MoveTally in MapView, `ui/ReplayMoveProbe`); nothing pushed. On resume: rebase onto `origin/batch/WN5b` (or main if WN5b went to Pending), then `docs/batch.md` item 7.
2. The measure (step 1) ran M1-M4 and part of M5 before the stop (below); rerun it whole on resume: the recording is kept at the session's scratchpad `cm1-recording` (119 months, 366-484, Andorra, 11,008 people).
3. Next: slice B, the load (brief drafted in the scratchpad, `sliceB.md`): phases LOAD and READY replace the warm-up; the walk paints every plan view (240 a second of the move's clock) with the real paint, caches pinned; a month store for glides across months; the veil and "Ready - click to run".
4. Then slice C: ReplayCheck's section (the real window off screen; skipped where there is no display), then the probe again (after), the notes, `--pick`, the ship file last.
5. Open for the notes: the GROUND tier's coarse planet picture past the plane (pieces are asked only at rest), the globe at half resolution on the move, a whole load at a land-change month, boats' routes per month; CLAUDE.md's "forty-two files" is 43 now (the probe).

## 1. Measured (before), partial

Andorra, month 484 of a scratch recording, full screen (the canvas 1,492 x 814 at 165 Hz), `ReplayMoveProbe`:

| move | frames | unpainted | black (FAR nodes) | coarse (GROUND) | worker asks | fps |
|---|---|---|---|---|---|---|
| M1 fly out, street to Earth | 583 | 348 | 9,990 (FAR 211 of 211 frames) | 71,988 (GROUND 120 of 120) | 638 | 40 |
| M2 fly in | 567 | 300 | 8,858 (FAR 202 of 219) | 53,388 (GROUND 96 of 113) | 556 | 41 |
| M3 zoom-out glide, 10 s (warm-up 7.2 s) | 465 | 258 | 6,682 (FAR 167 of 167) | 42,940 (GROUND 91 of 91) | 637 | 46 |
| M4 zoom-in glide, 10 s | | | 7,191 (FAR 153 of 166) | 45,727 (GROUND 85 of 90) | 637 | 45 |
| M5 glide in a month, 12 km east | | | 0 | 58 | 0 (180 pictures let go) | |

The black is the far nodes (FAR, from about 1 plot a pixel to the handoff): every frame there has nodes the worker has not grounded yet, about 40 a frame; the warm-up's 25 views do not reach them. The tile levels (L0-L2) are clean after the warm-up. On the globe's GROUND tier every frame draws part of the planet from its 7.4 km picture (pieces come only at rest).
