## changelog
### {next} — TR2-G, replay's copy leaves the player's prefs alone — 2026-11-18, ready, see docs/queue/TR2-G-notes.md
In a replay the window takes a copy of the player's prefs for the recorded
city's copy, and puts his back on leaving: a click in the copy on the drawer's
pin, the construction tab, a map toggle or the Reports' pins changes the copy's
values only, never the player's in memory (where the next prefs save, after
leaving, took them to his own `settings.json`; TR1's viewer clicks did the same
since 0.9.77). The copy never writes a prefs file anywhere; his own clicks
outside the copy save as before, and his F11 in a replay is not remembered.
ReplayCheck 11 (the model, and the real window) clicks the pin and the tab in
and out of the copy and fails with the mend removed. No model change: the
default trace did not move, no ensemble.
