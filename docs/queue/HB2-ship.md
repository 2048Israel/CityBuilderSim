## changelog
### {next} — HB2, the harbour's detail: its stock, its rail and a crane — 2026-11-10, ready, see docs/queue/HB2-notes.md
Jerus's picks from the renders, 1a, 2a and 3a, on the map from the 500 m scale bar in. Each terminal's box is HB1's open
yard as a picture (`HarbourYard`), its stock at one of eight steps by how busy its kind's terminals were in the month
billed (`Ports.busy()`: sea tonnes both ways over the standing berths' month; nothing saved). The railway lays a quay
spur to each quay line within half a district of the main line, from behind a box (`CityRuns.QUAY`, an arm on none of
the model's track, the sidecar's format as it was); the terminals on a railed line show their quay track, joined across
the gaps, and a train of wagons in a month their kind moved cargo (`QuayRail`). At a docked boat a crane's line carries
a box, or a grab of ore, between ship and apron, two moves a call, two cranes at a long berth, a tanker two still arms
(`QuayCrane`, `BoatSchedule.atQuay()`). PortCheck 11 and the new QuayRailCheck and QuayCraneCheck hold the model; the
screen (`MapView.harbourYards`, `cranes`) was not seen in the window. A Bergen played 2,400 months gets two spurs and
five railed terminals, Sydney and Perth none (their ports lie past the reach). The default trace is main's: no ensemble.
