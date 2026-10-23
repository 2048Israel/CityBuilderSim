# UP7 - the speed reading as the reached speed alone; the drawer's bar and the construction tab kept

Jerus, 2026-10-23, at the review desk, on UP2's calls (`UP2-notes.md` §5 and
the orchestrator's review in the UP2-C row). ★2 (REDRAW_SPACING 3, "Keep,
3×"), ★3 (owed time dropped, "Keep, drop owed time") and ★4 (the inbox's
kept scroll, "Fine") stay as built; his eye check at 50x is done ("checked,
looks right"). No model change; the trace does not move.

1. **The speed reading** (★1, "Reached speed alone, amber"). When the city
   falls a tenth short, the label between the arrows shows the reached speed
   alone, in amber, in place of the rung asked for ("39×", not "50× · 39×");
   at pace it shows the rung as today. The label's width is fixed (the widest
   reading reserved), so the tiles to its right never move.
2. **The stall in the reading.** `ClockPace.windowAdd` counts a stalled
   frame's raw time, not its clamped 0.25 s, so after a half-second stall even
   1x reads short for about 2 s; count the clamped time. And the rounding just
   past the tenth can print the rung's own figure as the reached one ("2x ·
   2x"): show short only when the rounded figures differ.
3. **What UP2 left rebuilt**: the drawer's pinned bar and the construction tab
   (`drawerBar`, `refreshConstructionTab`), kept through `ui/Kept` as the rest
   of the chrome, with KeptCheck sections against them built new.

Done when ClockPace's harness holds the clamped stall and the equal pair, and
KeptCheck the two pieces; the renders of the window the same pixels at pace.
