# OS1 - The order slice's search and its countdown disagree

Queued 2026-11-01 by EP1's gate. On EP1's path at the factor, OrderSearchCheck's
long run fails "every trim and refusal in the long run is the countdown's, with
its rate and its "at prime"" (1 against 0): at month 1226, Retail 94 Convenience
Store, `Game.largestSlice()` returns a quantity of 0 where the countdown finds
10 (one order of 2,196), with identical first rates. The run that meets it is
EP1's (EP1-notes §9); at today's prices and ten price levels beside the factor
the long run does not draw that month, so the disagreement is latent on main.

## What to do

1. Reproduce it: replay EP1's factor run to month 1226 (or build the smallest
   fixture that gives `largestSlice()` the same rates, quantity bounds and
   window) and print both searches' steps.
2. Find why the search stops at 0: a bracket that excludes the countdown's 10,
   a window or step that skips it, a refusal read before the trim.
3. Make the search find what the countdown finds, or prove the countdown
   wrong there and mend that instead.

## The checks

OrderSearchCheck gains a section whose fixture causes the disagreement on
main's classes and passes after the mend; the long-run line goes green on the
factor's path with no premise moved.
