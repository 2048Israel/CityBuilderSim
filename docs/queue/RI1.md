# RI1 - rail on islands

Queued 2026-10-31: Jerus ticked "Rail on islands", CL5-C's call 1 ("the
railway on other islands: its own track and yards from each piece's hub now,
or wait for ET5's ports") with ET5-C's "a linked island's hub at its port and
the railway on islands". ET5's ports (0.8.51) and ET6's bridges (0.8.57) now
link a landmass, so the wait is over.

## What it does

- **A linked island's hub at its port** (ET5-notes, Not done): the hub a
  piece's nets grow from is its port's place, not a guess on its ground.
- **Its own railway from that hub**: track and yards on each linked piece,
  as CL5 gave each piece its own highway net (CL5 notes, Calls 1), dealt and
  dated as the home railway is (FX7's runs), with the island's works served
  by sidings as at home.
- What reaches it from home stays as the link is: by sea through the port,
  or over a Rail Bridge where ET6's stands. ET6-C's "trains and sidings do
  not use a rail bridge" stays in its row unless this work takes it whole;
  the notes say which.

## Checks

A fixture of its own: a city with an island linked by a port, and one by a
bridge, each growing its own track and yard from the hub; an unlinked island
none (ET5's rule); MapCheck's month lines against main. If rail freight's
model reads the island's track (`HomeRailCheck`'s home share), the trace may
move on an Earth run: the ensemble then. Renders in `docs/queue/RI1/`.
