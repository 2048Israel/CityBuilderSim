<!-- readiness: provisional -->
## changelog
### {next} — HL1-R, Business Services' kept share without a cliff — 2026-11-19, ready, see docs/queue/HL1-R-notes.md
HL1 kept a quarter of the city's whole upkeep in Business Services from the first seat and none before: a margin with no
capacity behind it. It now keeps the quarter (`UPKEEP_IMPORTED`'s dial) or what its seats can serve of the month, whichever is
less: `upkeepCapacity()`, every seat at what the world pays for a seat-month, the bank's bill and the services' sharing it,
and imports the rest. No seats: all imported, as before; seats enough: the same quarter, to the bit. A city with a few seats
and a large upkeep keeps less and imports more. The audit's lines and the households' identity are as HL1 left them. The
default trace is byte-identical to main's (its city has 130,200 seats), so no ensemble. `BusinessServicesCheck` section 7
causes none, one and enough seats and fails with the cliff put back. No save change.
## pending
## queued
## known-reds
