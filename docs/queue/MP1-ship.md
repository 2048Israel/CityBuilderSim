## changelog
### {next} — MP1, The map fast again — 2026-10-27, ready, see docs/queue/MP1-notes.md
On a quiet PC main missed one map timing line, the month's map work at 5B (58 ms): 66.4, 56.9, 59.1 ms in three
hard rounds; every other line in MP1's table passed there, so what the gates saw on 2026-10-26 was mostly the PC's
load. Profiled, the 5B month was three wastes, all there since UP3 set the line (no later batch slowed it): the
interchanges' reserve guard walked all 14,661 districts' mined sites on every call (FR7), now once a change of the
map; the world line read each plot four times, now once; and the district index's keys all collided (a Long's hash
of dx << 32 | dy is dx ^ dy), now spread. The far node over Medellin parsed every tile's rivers and sorted their
widths on each call (E5); each tile's drawable runs are now kept. Quiet, in turn with main: the 5B month 21.3 ms,
Medellin's far node 54.5 -> 31.4, every line passing three rounds in a row. Same pixels and trace; at 50x a
five-billion city's map now follows the clock month by month, and at his city's size it never slowed the game.
## pending
| MP1-C | MP1 ({next}): x 80's chain, 25 ms a month on the screen's thread - each moved district's plan input (its frame's runs and reserves) gathered as its job is handed out; the reserves could stay kept across months if the guard's version moved only with the mines and shore works it reads; recommended as a small batch with a harness counting how often the reserves are found again. Also `EarthData.CACHE_BYTES` (10 MB): raising it would speed Rotterdam's far node (54 of 80 ms) for memory, not needed for any line | his call on both: no budgeted line waits on them | [MP1 notes](docs/queue/MP1-notes.md) |
