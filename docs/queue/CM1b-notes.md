# CM1b - the replay loaded move, the review follow-ups

Built from CM2 main `2853630b` on `batch/CM1b`. Read CM2's notes and `git log -p 8a67da4d..2853630b -- src` before editing. No known-reds row names CM1b at this starting main; ReplayCheck's historical negative-ask entry belongs to GR2-R. LD3's named files, including Game.java, are untouched. No save slot was written; all cities and windows use scratch folders.

## Decisions and implementation

★ Keep CM2's synchronous `MapView.forget()` release. It already fixes leaving replay while Ready; CM1b adds synchronous cancellation through the expanded map while its reference still exists, and before collapse detaches it. Esc and X while Ready release the move, copies, pins and prepared contexts immediately. A load cancelled or losing its map restores its starting month and view; an in-flight move stops where it stands. Completion still releases after the final frame is tallied, retaining CM2's zero-eviction law.

★ Cap the whole load at `MapView.LOAD_MAX_S = 120`, including busy time. CM2 measured successful loads above the old eight-second warm-up limit (up to 34.2 seconds at 1920 x 1080); 120 seconds leaves room for those loads while bounding failure. The refusal names the dial on the replay card and restores the starting view. Keep the separate two-second idle give-up for a view. Remember district pictures and all missing visible cell types, including cells never asked for. Failed worker jobs are recorded on FX and suppressed for this move.

★ Preserve `LOAD_HEAP_SHARE = 0.8` as a share of the heap available when the load starts, rather than the whole maximum heap. Check against that fixed per-load budget, and collect at most once across its views. A deterministic sampler fixture covers a city already over the old threshold, growth at and above the budget, and repeated excessive samples without allocating most of the test JVM's heap.

All keys except Escape are consumed while loading, before text/modifier and ordinary replay handling. Ready keeps its click/any-key trigger, with X taking its documented stop action. ReplayMoveProbe.check deliberately re-enters replay at the end so its finally must turn replay off and explicitly stop both registered pumps before section 6's timing begins.

## Verification in progress

The first fast check passed, exit 0 (compile, maps, Stale 0 firm). Two initial focused runs exposed fixture setup mistakes (reflecting inherited Map.clear as a declared method, and scanning after a synthetic month change without first adopting its map). Fixed the fixtures; these were not model findings. Those exception exits left JavaFX alive, so only the verified owned ReplayCheck process trees (native parent PIDs 43840 and 36048) were ended by PID. Other sessions and MemWatch were untouched.

Final named checks, regression demonstrations, default trace comparison and serialized gate evidence follow below.
