## changelog
### {next} — RI1, rail on islands — 2026-11-09, ready, see docs/queue/RI1-notes.md
Every linked piece of the city's ground now grows its own railway, as CL5 gave each its highways (Jerus ticked "Rail
on islands", 2026-10-31; CL5-C's call 1). A piece's rail hub is the railway crossing nearest its anchor, and an island
linked by a port has its berthed terminal for its anchor, so both its nets start at the port (ET5's "a linked island's
hub at its port"); home and a bridge-linked island keep the founding site's side. Track is dealt by built weight as
the highways are; a new yard goes first to an island railway with none, on its own line nearest its hub, and that
line grows to its edges as a station's does; sidings leave from an island's line. The first yard and the freight
station stay home, and no train is routed to an island yard (ET6-C's rail bridge row stands). Unlinked ground gets
none. IslandRailCheck (new, nine sections) on synthetic ground and the Rotterdam fixture; renders in docs/queue/RI1/.
Nothing in the model reads the map: the playtest's trace is byte-identical, no ensemble, no format change.
## pending
| RI1-F | IslandRailCheck has no real-map island linked by a bridge ({next}) | the runs read the map's pieces, which do not say how a piece is linked, so the synthetic ground's islands stand for it; a fixture on BridgeMapCheck's ground would hold call 3 (a bridge-linked island's hub on the founding site's side) on a real map | [RI1](docs/queue/RI1-notes.md) |
