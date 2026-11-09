## changelog
### {next} — TR2, the ghost cursor and Follow in replay — 2026-11-09, ready, see docs/queue/TR2-notes.md
Replay can play the player's own input back: Follow (the bar's button, or L) runs the month on view in real time of
play at 0.5x to 4x, idle stretches skipped by default, and his clicks, drags, the wheel and his keys land in the copy as
the scene would deliver them (`ui/Reenactor`), so tabs switch, pages scroll, the map pans and hover cards show; months
turn when the log says, each loaded whole, and a join mid-month starts from its marker. The window is set to the logged
size first. The ghost cursor (H) is his pointer with a ripple at each press, shown while the copy is on his screen.
Nothing on a game menu (Save, Load, New city, Quit), under a dialog, on the rail's Menu or the clock, the clock's keys,
F11, or Esc and P where they open the menu is re-enacted (`ui/FollowRules`); the viewer's press, wheel, key or a move of
8 px ends Follow. TR1's review's four follow-ups are in (a failed log write stops, a save's recording id only of the
recorder's shape, a first write after a load clears the later months first and every task queued behind one that cannot
writes nothing, a month that autosaves filled once). ReplayCheck 7-10 (10 in the real window). Recording is off by
default and Follow lives in replay: no ensemble.
## pending
| TR2-P | the district plans carried across replay's quick steps (TR2's item 5, {next}) | TR1's call 3 made it wait on a screen recording showing the map's lag, and none has; `CityMap` was lane A's file | docs/queue/TR2.md |
| TR2-G | a click in replay's copy on the drawer's pin or the construction tab changes the in-memory GamePrefs, which the next prefs save takes to the player's folder (TR1's viewer clicks too; {next}) | TR2's stranger's review, low, not this batch's | `UserInterface`, the pin's and `constructionTab`'s click handlers |
