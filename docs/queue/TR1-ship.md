## changelog
### {next} — TR1, replay a recorded city (the trailer's camera tools) — 2026-10-31, shipped, see docs/queue/TR1-notes.md
"Record this city" (the Menu, kept in the save) keeps every month - the save and the map, gzipped - in recordings\<id>\, written on
SaveWriter's thread; the month pays only the save's fill (3-5 ms on a 78,000-person city), about 0.2-0.5 MB a month on disk, and a
rewind rewrites from the month played on. The player's input is logged beside it for TR2's ghost. "Replay a recording" opens any
month on a throwaway copy in its own scratch folder, so nothing sticks and no save or slot is written: a time bar plays both ways
at 1-16 months a second (the map swapped while moving, the month loaded whole at rest, 0.2-0.8 s). Cinematic mode (C) hides the
chrome and the cursor, and a button sizes the window to 1920 x 1080. On the map: the wheel and W A S D ease, a drift, a glide
from view A to B through time (1, 2, 3), a fly out to the whole Earth and back (O, I), and faint GDP and population lines behind
the city (G). Recording is off by default and changes no month (ReplayCheck 6): no ensemble.
## pending
| TR1-C | Eye-check TR1 for the trailer ({next}): Menu › Record this city on, play a year, Menu › Replay a recording. (1) Drag the bar and play it both ways at 4 and 16 months a second: the map follows, the screens show the month when it rests. (2) C: nothing but the page and the map, no cursor; C or Esc brings it all back; 1920 x 1080. (3) M, then 1 and 2 at two zooms and months, 3: the warm-up walk, then a smooth glide with the city growing; O and I: the fly to the whole Earth and back (blocky while it moves?). (4) G while it plays: the faint lines rise behind the city (the look is ours: say if it should change). (5) The Policy screen in cinematic mode, a chip clicked: gone at the next month | Built without launching the game (it would have run on his real save folder); only the model is checked by a harness | [TR1-notes §1, §4](docs/queue/TR1-notes.md) |
| TR1-M | The manual's section on recording and replay, its text written: put it in the published manual at the docs pass | The manual is the published page other lanes publish too, and docs/manual.* are generated from it | [TR1-notes §6](docs/queue/TR1-notes.md) |
## queued
| TR2 | The ghost cursor and Follow in replay: the input log TR1 writes played back in real time, the player's clicks re-enacted on the copy, months turning where the log turned them; and the district plans carried across quick steps if TR1's recordings show the map lagging | [TR2](docs/queue/TR2.md) |
