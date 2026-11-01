# GT1 - Faster gates: no re-measuring main's own timing reds, a shorter playtest at the gate, and checks that share its work

Queued 2026-11-01 from Jerus, asking why a gate takes 45-60 minutes ("do we
need 4k months?"; "whenever running the long playtest of say 2400 months, why
not use its output to simultanously check all of the checks or the ones
doable? since its already has the numbers?"). A process batch: the one-in,
one-out rule binds it (its notes give the count).

## Where a gate's time goes now (EP1's gate, 2026-11-01)

- The suite: 166 harnesses, three at a time, 2,387 s (40 min). The long
  playtest (4,000 months, every month audited, reloads checked) is the longest
  single harness; measure its own share first.
- The timing re-measure: every gate this week paid 3 paired rounds against
  main (TR1 21 min, IC1 29 min, EP1 19 min) because two timing lines fail on
  main itself: SaveSlotCheck's median due month (~29 ms, budget CLOCK_MS 25)
  and MapCheck's month at 10 billion (~4.4-4.8 ms, budget 5), so every merge
  is "red" and judged within 15% of main.

## What to build

1. **Main green on timing.** Make SaveSlotCheck's due month and MapCheck's
   10-billion month fit their budgets again (the code, not the budget, unless
   the budget's own sentence is shown wrong); then a gate meets the paired
   rounds only when a batch is really slower.
2. **The playtest's length at the gate.** The gate plays 2,400 months (1900 to
   2100; everything found on 2026-11-01 showed up between months 1,100 and
   1,800). The full 4,000 runs once a night on main (the hourly check-in or a
   scheduled run), its result in the day's record, so a fault past month 2,400
   is met the next morning with only that day's ships as suspects. Mind the
   playtest's constants derived from TARGET_MONTHS (LAYOUT_STARTS) and its
   fixed months (MARKET_MONTHS 600/1200/2400).
3. **Invariants ride on the playtest.** A claim that must hold in every city
   every month (the money audit and the reload already do; the registers
   adding up to their holders, prices and stocks never negative, the books
   closing) is asserted on the playtest's months rather than in a city of a
   harness's own, where that harness founds and plays a city only to read it.
4. **Shared grown cities.** List the harnesses that found a city and play it
   hundreds of months before asserting (ScaleCheck's town, CentralBankCheck's
   probe city, PriceLevelCheck's played city, MapCheck's, ...). Where two play
   the same founding and rhythm, one saved city at that month, made once per
   suite run (GameFiles.scratch, never a player's slot), is loaded by both.
   Do NOT move a harness onto a different city to share one: a fixture must
   still cause its condition, and a played city's path moves with every batch
   (2026-11-01: ScaleCheck and CentralBankCheck went red only because the path
   moved), so a harness whose condition is a behaviour keeps its own fixture.

5. **README's harness count set at the gate.** Two batches that each add a
   harness both write the count up by one, and the second to ship stops on
   StaleCheck ("every stated count is the count"): CD7 after LB2, TR1 beside
   LD1, WN5 after WN7 (2026-11-01, a 45-minute regate). `ship.sh` writes the
   count itself in the merge (the *Check.java files on the merged tree), or
   README states no exact count; either way a batch no longer edits it.

## The checks

The gate's time before and after, the suite's and each step's, on the same
commit. Every harness keeps every assertion (the count by harness, before and
after). TimingCheck and KnownRedsCheck green; the nightly 4,000-month run
reported in the next day's record.
