# FD1 - Freight sells its last station and buys it back

Queued 2026-10-29 by MF1 (`MF1-notes.md` §1 item 6 and ★2), from its trace of
FX7's ★4, "the founding district's yard is taken up and laid again on the same
spot eleven times" (Jerus put it into MF1: "Into MF1").

## What MF1 found

The yard's relays were the model's, not the map's. In the default playtest
the Freight sector's only Rail Depot is sold once its last Rail Spur is gone -
`Freight.mayRetire()` (`sectors/Freight.java`, about line 1751) keeps the last
station only while a spur or a line stands, so `Game.runRetirement()` sells it
by the spare-capacity or distress rule - and another is ordered 3 to 93 months
later: 16 times in 3,140 months, 4 of them sold and ordered again in the same
month (m2,606, 3,505, 3,534, 3,859). The probe that found it is
`MF1/MF1YardProbe.java`; its log was in MF1's scratch.

MF1 stopped what the map showed (a yard whose building left stays laid, idle,
`CityRuns.YARD_IDLE_MONTHS`), but the model still pays for the churn: a
depot's price and its sale, and the months without one.

## What to do

1. Measure the churn: how often the last station (and any building) is sold
   and ordered again within N months, in the default playtest and the
   ensemble's seeds, and what it costs the city.
2. Stop it at its cause: a building is not sold by the retirement rules while
   the orders would buy it back within a horizon (or the last station of a
   kind is kept while the city has rail freight to carry, or the order waits
   on a demand that stays). Pick the rule the figures support.
3. A harness section whose fixture causes it (the last spur sold, the depot
   then sold and bought again), red before, green after.

The model moves, so the trace moves and the ensemble runs (EN1).
