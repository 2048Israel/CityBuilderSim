## changelog
### {next} — FW8, WHAT IS BITING flags a zone short of what it wants — 2026-10-25, ready, see docs/queue/FW8-notes.md
The Policy hub's WHAT IS BITING had no zoning flag. Now a zone short of what
it wants raises one: the zones together want more than the free ground, so
each holds the same share of its want and nothing is free for anyone else.
"Agriculture's zone holds less than it wants" ("It holds 78.5 km² of the 90
km² it wants"), or "2 zones hold less than they want", with a door to the
zone's page or to Zoning's The ground. The figures and words are the model's
(`ZoningView.shortfall()`); the hub is not drawn kept. ZoningViewCheck 9
makes one zone and then two short and asserts the flag, on the hub's own list
too, and none for a zone with enough or for no zone. The map's zone tint was
I14's (0.8.0), so nothing is left undone. The month is untouched: no trace
moved, no ensemble.
