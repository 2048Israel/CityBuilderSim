## changelog
### {next} — FC1b, the foreign contractor's book and overtime after FC1 — 2026-11-03, ready, see docs/queue/FC1b-notes.md
In the builders' trap a cancelled order now comes off the foreign contractor's book by its points left (a depot of
the builders' served first, never on his book, excepted). Before, he kept building a cancelled order's points at his
price, and a cancel and restart put them on his book twice. A rushed site's overtime is now worked and paid on the
city's own crews' part of its share alone. Before, the city paid its households overtime for hours his crews worked,
and the builders earned points nobody worked. With no contractor the month is the same to the bit. ConstructionTrapCheck
9-11 cause each case: the cancel and restart, the rush beside him, and a city saved mid-trap whose next month matches
its twin's. The playtest's trace is byte-identical (no ensemble). Both fire only in a city in the trap.
