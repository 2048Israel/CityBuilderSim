# UP6 - the autosave: a skip's end, the window's close, the player's Save off the clock

Jerus, 2026-10-23, at the review desk, on UP1's calls (`UP1-notes.md` §6 and
the orchestrator's review in the UP1-C row). UP1-C's eye check is done ("Fine;
checked, looks right": the stall gone at 50x, Load's autosave row the latest),
and ★3, the floor spanning the session and a load, stays as built ("Fine").
No model change; the trace does not move.

1. **An autosave at a multi-month skip's end** (★4, "Autosave at the skip's
   end"). The "before skipping" save resets the 30 s floor (Game.java about
   10389 and 6015), so a skip that runs inside 30 s ends with the save from
   before it on disk, and closing the window then loses the skip. Write an
   unfloored autosave when a multi-month skip ends (about 5 ms on the clock);
   the running clock keeps its floor. Correct Game.java's comment near 6087
   ("finds the files as they were before the writer left the clock"), untrue
   for the player either way.
2. **The window's close button autosaves like Quit** (★1, "Yes, autosave on
   close"), through the writer, waiting for it before the game exits, as a
   write in flight already does.
3. **The player's own Save goes to the writer** (★2, "Background +
   'Saving...'"): no 119 ms freeze at month 4,000; a "Saving..." line shows
   until the write lands, then the result as today. The writer never writes
   slots 1-9 except when the player saves there; a second Save while one is in
   flight waits for it. Load's menu keeps its wait on the writer.

Done when SaveFileCheck (or UP1's section 7) holds a fixture for each: a short
skip followed by a close leaves the skip's last month on disk; a close
autosaves; a player's Save returns at once and its file is complete when the
line clears. Harness cities through `GameFiles.scratch`, never a real slot.
