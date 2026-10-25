# FR9 - one truck, one whole trip

Jerus, 2026-10-20, at the review desk: "the trucks on screen only flow for a
bit then disappear into the void... but still inside the city", then "Queue
it" on one truck followed the whole way. The map's alone: nothing in the
model reads it, and the playtest's trace stays byte-identical. A new
version (player-visible): `{next}` in its ship file, and `ship.sh` sets
`GameVersion.VERSION` (BP1).

## What is there (FR5, 0.8.8; FR5-notes §1, ★5, ★6)

`TruckSchedule` draws two kinds of truck on two separate schedules:

- **on the highways** (`CityMap.truckRoutes()`): from the world line, in to
  its arm and along the highways to each arm's end, a ramp, where the truck
  stands `DWELL_SECONDS` and runs back out. Imports and exports only. The
  arm's end lies inside the city, so to the player the truck vanishes there.
- **on the access roads** (`accessRoutesIn()`): from the road's end on the
  street grid to its work's gate; every load; each drawn district's roads
  an equal share of the city's loads, not their works' tonnes.

FR5-notes §5 lists it as not built: "one truck followed from the world line
along the highway, off at a ramp and down an access road to its gate".

## What to build

- **A trip is one schedule entry**: an import from the world line along the
  highways, off at the ramp nearest its district, along the streets to the
  access road and down it to the gate; an export the other way; a home load
  from one gate to another along access roads and streets. Still a pure
  function of (load, month, truck number) and time, as the trains' and
  boats' are; nothing simulated or saved.
- **A street leg**: the ramp to the access road's end, on the drawn street
  grid (arterials first), found as `CityRuns.wayOn()` finds a net's way; a
  district not yet drawn takes no trip (★8 holds).
- **A district's trips by its works' tonnes** (FR5 ★5's open half), from the
  model's own figures through a public getter, never recomputed on the map.
- **The pace** stays the trains' clock: the whole trip in a time that grows
  with its length, so a long trip is not a blur; the legend's "one per N
  loads" unchanged.
- **Harness**: TruckRouteCheck sections whose fixture is a city with a
  highway, a ramp and a works' access road: every trip's points on road,
  every import's last point a gate, no point where no road is drawn, a
  district's trips in its tonnes' share; and the frame within the trains'
  bound at 10 billion (MapCheck 9's 0.5 ms).

## Not this batch's

Trucks drawn at the middle view; a truck's hover; the depots as where trucks
start the day (FR5-notes §5).

## Done when

The suite green but what was already red, the playtests byte-identical, the
maps regenerated, the changelog's block in its ship file,
`docs/queue/FR9-ship.md` (`ship.sh` puts it in `docs/notes/changelog.md` and
takes this row out of `QUEUE.md`, BP1), and the renders of a played city beside the notes for
Jerus's eye.
