## changelog
### {next} — CM1, the replay's camera loads its whole move first, then runs on a click — 2026-11-03, ready, see docs/queue/CM1-notes.md
In replay mode a glide (3), a fly out (O) and a fly in (I) now load first: the map says "Loading the move... N s" while
every view the move will show is painted, with no time cap and the map's pictures held, then "Ready - click to run", and a
click or a key runs it (Escape cancels), so a screen recording can start first. Measured on an Andorra recording
(`ui/ReplayMoveProbe`), black frames went from up to all of a move to none: a glide across 119 months, black in all 295
frames and 5,256 asks of the map's worker before, ran with no frame unpainted and nothing asked; loads took 5-15 s. Left
for CM2: the globe near the city still draws part of the planet from its coarse 7.4 km picture while it moves (blurry, not
black), and the half resolution on the move. ReplayCheck 5d runs a move in the real window, off screen, and asserts every
frame shows a view the load painted (a run between them asked the worker on a busy PC). The normal game is untouched.
## pending
| CM1-C | Eye-check CM1 ({next}) at 1920 x 1080 on a recorded Earth city: in a replay, mark A at the street and B at the whole Earth, press 3: "Loading the move... N s", then "Ready - click to run", a click runs it with no black on the zoom out; the same with O and I, and a glide across months; Escape while it loads stops it and the view stays where the move begins | Not looked at in the running game | [CM1 notes §3](docs/queue/CM1-notes.md#3-what-was-built) |
## queued
| CM2 | The replay's loaded move on the globe (found by CM1, its ★1-3): no coarse ground past the plane in the GROUND tier (the charts asked over the whole visible disc in a loaded move), full resolution on the move if the frame rate holds at 1920 x 1080, and the 250 ms frame of a glide across months measured and taken off if the load can do its work ahead | [CM2](docs/queue/CM2.md) |
