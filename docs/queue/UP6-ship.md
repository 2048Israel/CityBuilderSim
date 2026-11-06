## changelog
### {next} — UP6, The autosave: a skip's end, the window's close, the player's Save off the clock — 2026-11-06, ready, see docs/queue/UP6-notes.md
A multi-month skip ends with an autosave of its own last month, unfloored, asked for and not waited for (about 5 ms on
the clock), so a skip inside the 30 s floor no longer leaves the save from before it on disk; none after a skip that
stopped part-way. Closing the window autosaves like Quit ("Autosave - on close", the real city during a replay), on disk
before the game exits. The player's own Save is written on the save writer from a snapshot: the page says "Saving..."
until the write lands, then the result as before; a second Save waits for the first, the Save menu waits as Load's does,
and the writer writes a numbered slot only when the player saves there. A history that could not be written is told:
"Saved, but its history could not be written: <reason>" on the Save page, an inbox notice for the autosave (and for an
autosave that fails whole), the .bak kept. SaveSlotCheck 7e holds each; 7b and 7c.5 read the skip's end. No model
change: no ensemble.
