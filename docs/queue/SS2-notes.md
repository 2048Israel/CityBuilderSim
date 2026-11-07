# SS2 (0.9.45, 2026-11-07): SaveSlotCheck 7d's hold on the swap's own count, not a sleep

Built on PD1 (RL1); touches only `GameFiles.java` and `SaveSlotCheck.java`. No model, no save, no screen, no process change
(a seam of a few lines in one retry, and a fixture): the default playtest's trace is byte-identical to PD1's, so no ensemble.

## What went red, and why

LB3's 7d held a file open on a reader thread that let go after `HOLD_MS` (100 ms, a fifth of `GameFiles.SWAP_RETRIES` 10 x
`SWAP_RETRY_MS` 50) while the write's swap retried against it. Two clocks against each other: under load the release thread
and the swap's ten tries slipped apart, and RT1's gate (2026-11-05) saw "...after waiting for it: its swap was refused and
landed on a later try" and "...leaving the file whole as it was, and no .tmp, and it is no swap that waited and landed" red.

## What it is now

- `GameFiles.onSwapRefused` (package-private `static volatile IntConsumer`, null outside the check): called on the swap's own
  thread after each refused try that will be tried again, with the number of tries made so far, before the wait. One volatile
  read per refused swap in play.
- 7d sets it, lets go of the held reader from inside it at refused try `RELEASE_ON_REFUSAL` (2, replacing `HOLD_MS`), clears it
  in a `finally`. The release is now a step of the swap itself: the next try always lands, whatever the machine is doing.
- Assertions unchanged but for the first label (no milliseconds in it) and one added, `...having been refused exactly that many
  times first`, which only a swap tied to its own count can promise. The held-past-every-retry half is untouched (it never
  depended on a release).

## Proof

- Alone: 7d's seven assertions OK, the log line "Wrote slot-04-history.json on its try 3".
- Under load: `SaveSlotCheck` three times beside two other harness JVMs looping (`RestaurantsCheck`, `SaveFileCheck`):
  7d's seven assertions OK in each. The one red in each run is 7c's "the median due month ... holds the clock at most CLOCK_MS"
  timing figure (47-75 ms against 25 on the loaded machine): main's recorded timing red, `docs/notes/gate.txt`, not this batch's.
- Slots: 7d writes only a `TempDirs` folder; no save slot touched.

One in, one out: not a process change (no script or rule touched).

## For the gate's review hints (`--pick`: "saves: GameFiles.java; threading: +volatile")

Both come from the one seam. Saves: no byte written differs, the swap's moves, copies and retries are as LB3 left them; the hook
runs between a refused try and its wait and returns nothing, so it cannot change a result. Threading: `onSwapRefused` is a
volatile static read once per refused swap (a swap that never meets an open file never reads it) and written only by 7d, which
clears it in a `finally`; no lock, no shared state beyond that field.

`--pick` exit 3: LongPlaytest, main's recorded red, in its place for `Game.java` and `GameFiles.java`'s readers; SaveSlotCheck's one
timing assertion (7c's median due month) SOFT, as `gate.txt` records it.
