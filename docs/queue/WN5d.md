# WN5d - the hover's quote: five findings of WN5b's review

Queued 2026-11-04 by the orchestrator, from the stranger's review at WN5b's gate (SHIP + FOLLOW-UP). WN5c, also
WN5b's, is the picked block's split on the screen's thread; this batch is the hover's own quote, which WN5b moved to
`ui/MapView`'s `QUOTES` thread (`askQuote()`, the hover's card in the block branch above it) with a copy of the office
from `LandMarket.quoter()`. Five things the review found in that path, each to be caused by a fixture before it is fixed
(`README.md`, *The checks*), the checks beside LandNationsCheck 12 or in the map's own harness:

1. **A quote that outlasts a month never shows.** The quote's key holds `g.getMonth()`, so when the clock turns the month
   while a quote is out, the hover asks again under the new key; at levels 11-12 near a foreign coast a quote takes
   seconds (WN5b notes §2) and at speed a month is shorter, so the card says "Pricing the block…" (`LandMap.QUOTING`) for
   as long as the clock runs. Show the block's last quote (the latest month's kept for that block, level and ground)
   until this month's is in, marked as last month's if the card can say so in a word.
2. **A slow quote for a block the pointer has left holds the next.** `QUOTES` is one thread, and `askQuote()` checks
   `quoteWanted` only before the quote starts: a quote of seconds runs to its end after the pointer moves on, and the
   block now under the pointer waits behind it. Stop a quote that is no longer wanted (the split checking a flag between
   sellers or rows, or the quoter's own cancel), or let a newer ask go first; measure how long the next block waits now.
3. **An `Error` in the quote leaves its key in `quotesAsked` for good.** `askQuote()` catches `RuntimeException` only;
   an `Error` thrown in `use.quote()` (out of memory at level 12, a stack overflow) ends the task before the
   `Platform.runLater` that removes the key, so that block is never asked again and reads "Pricing the block…" until
   the month turns. The hand-back to the screen's thread belongs in a `finally`.
4. **A failed quote reads as none.** A quote that threw is stored as a null, the same as a block with no ground to sell,
   so the hover returns no card and the block reads "Not the city's" (`LandMap.NOT_THE_CITYS`) all month. Keep a failure
   apart from a none: a card line that says the price could not be worked out, and asked again after a while or at the
   next month, not never.
5. **The quote's ground count runs on the common pool at normal priority.** `CityLand.unownedCounts()` splits a block of
   `PARALLEL_TILES` tiles or more over a parallel stream, which runs on `ForkJoinPool.commonPool()` at normal priority
   even when it is asked from the hover's low-priority thread, so a level-12 quote competes with the screen's thread and
   the month. Run it in a pool of the caller's priority (or its own small pool) when the caller is the quoter, and show
   by a timing assertion (`Timing.verdict`) that a frame's time with a quote out does not grow.

Done is: each of the five caused by a fixture and green after its fix, KeptCheck and MapCheck's timing within main's
(WN5b's gate stopped on them once, on a laptop leaving the library: judge them side by side with main), and the
eye-check row for the hover over a slow block at level 11-12.
