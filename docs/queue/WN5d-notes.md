# WN5d — The hover's quote

## Fixtures before the fixes

Initial batch-check --fast passed on e8a468c2, target/wn5d-initial-fast.txt. No known-reds entry names WN5d.

The old MapView lifecycle was extracted into LandMap.Quotes with injected worker and owner dispatchers so the real state transitions can be held across a month, pointer change and fault. The worker measurement uses the same readGround/priceGround split as WN5c. target/wn5d-before.txt shows nine failures covering an answer held through two played months, missing previous-month fallback, RuntimeException and Error cleanup/retry, and a newer hover waiting 419.61 ms behind a running 400 ms quote fixture. These assertions run through the same controller now installed in MapView.

target/wn5d-priority-before.txt causes a level-12 count after starting the common pool from a normal-priority caller. Live stack samples see rowClasses on the common pool's normal-priority workers, and the low-priority assertion fails. The first probe had started the common pool from its low-priority quote, causing those freshly created threads to inherit low priority. Warming the already-normal pool reproduces the game's reported condition. The fixture retains the real parallel threshold and checks the complete plot count.

★ A late measurement will be repriced on FX from immutable geometry at acceptance, following WN5c. That allows a quote slower than the clock to become visible with the current month's price. Cached previous prices will remain labelled while the next measurement runs. Hover pricing omits the unused landmass linkage walk, as WN5b's copied office already did. No month phase, save format or HW1 file is changed.
