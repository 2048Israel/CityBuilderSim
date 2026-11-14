# WN5d — The hover's quote

## Fixtures before the fixes

Initial batch-check --fast passed on e8a468c2, target/wn5d-initial-fast.txt. No known-reds entry names WN5d.

The old MapView lifecycle was extracted into LandMap.Quotes with injected worker and owner dispatchers so the real state transitions can be held across a month, pointer change and fault. The worker measurement uses the same readGround/priceGround split as WN5c. target/wn5d-before.txt shows nine failures covering an answer held through two played months, missing previous-month fallback, RuntimeException and Error cleanup/retry, and a newer hover waiting 419.61 ms behind a running 400 ms quote fixture. These assertions run through the same controller now installed in MapView.

target/wn5d-priority-before.txt causes a level-12 count after starting the common pool from a normal-priority caller. Live stack samples see rowClasses on the common pool's normal-priority workers, and the low-priority assertion fails. The first probe had started the common pool from its low-priority quote, causing those freshly created threads to inherit low priority. Warming the already-normal pool reproduces the game's reported condition. The fixture retains the real parallel threshold and checks the complete plot count.

★ A late measurement will be repriced on FX from immutable geometry at acceptance, following WN5c. That allows a quote slower than the clock to become visible with the current month's price. Cached previous prices will remain labelled while the next measurement runs. Hover pricing omits the unused landmass linkage walk, as WN5b's copied office already did. No month phase, save format or HW1 file is changed.

## Mends and ownership

★ Keep one request for the wanted block across month changes. The worker measures the quoter's copied land and private office. FX alone checks the Game, office, CityLand, grid, owned count, level and sellers identities before pricing the returned immutable geometry with the live office's current month. Prior cached prices are keyed by block within that ground scope and say Last month or Earlier while a fresh measurement runs. The hover never uses a measurement as a purchase price.

★ A FutureTask owns each quote. A pointer change, exit, press, collapse or detached map cancels it. CityLand polls interruption between tiles and LandSellers between tiles and plot rows. The worker's finally always dispatches completion, including RuntimeException and Error. Owner dispatch checks request identity, so a superseded completion cannot clear or overwrite its successor. Successful none and failed work have separate records. Failure shows Price could not be worked out, retains an older price if available, and retries after RETRY_MS or the next month. Only the wanted failed block keeps the frame clock awake for a retry.

★ Low-priority parallel ground counts use a dedicated two-worker ForkJoinPool at MIN_PRIORITY. Normal-priority callers retain their existing parallel path and ordered integer reduction. An interrupted quote stops waiting immediately and signals its count's rows through an AtomicBoolean. Every row checks that flag between tiles, so leftover count work also ends. The pool touches only the copied CityLand grid and world reads plus the existing synchronized tile-count cache. LandSellers shares only its established thread-safe world/seller caches and atomic observation counter. The request cancellation bit and count stop bit are the only new cross-thread mutable state. Controller maps, request ownership, prices, Picks, live office and scene nodes are written only on FX through Platform.runLater.

Focused fixture result target/wn5d-final-probe.txt, exit 0. Queued and completed cancellation, a quote spanning two played months, current-price equality, last-month fallback, a failed refresh retaining the old price, retry at RETRY_MS and the next month, Error cleanup, successful none, changed ownership, a new city/office and a real level-raising purchase all pass. Owner-thread map geometry median was 0.253 ms idle and 0.084 ms with the level-12 count out, judged through Timing.verdict. Stack sampling found only the two low-priority count workers and the counted unowned plots equal the grid's count.

The real seller-split cancellation fixture also failed on the extracted old controller with main's CityLand and LandSellers compiled into isolated target/wn5d-before-classes. It waited 260.37 ms after the next ask, against 11.24 ms after the fix, with the old level-12 river quote demonstrably still walking seller plots at the switch. Logs target/wn5d-real-before.txt and target/wn5d-real-after.txt. The artificial 400 ms held-work fixture separately fell from 419.61 ms to 0.56 ms. Budgets are expressed against QUOTER_MS.

Named LandNationsCheck passed through batch-check, exit 0, target/wn5d-land.txt. Compile and generated maps passed, Stale has 0 firm findings. Further fixtures added afterward are included in the final pick.
