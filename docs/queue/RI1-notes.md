# RI1 - rail on islands ({next}, 2026-11-09)

The brief is [RI1.md](RI1.md): CL5-C's call 1 and ET5-C's "a linked island's hub at its port and the railway on
islands" (Jerus ticked "Rail on islands", 2026-10-31). Lane A, after AL1 (0.9.69).

## The design (decided before the code)

- **Slice A, the railway on each piece** (`CityRuns`, THE RAILWAY'S WAYS and the pieces). The rail net opens a
  piece on each map piece but home that has weight and a rail hub (a railway line crossing, RAIL_AT, on the piece's
  own dry ground nearest `Pieces.anchor(p)`), once the home railway has started. Its corridors come after the
  BRANCH and SIDING placeholders (2, 3), so `dropWays()` keeps them. Track is dealt by built weight as CL5 deals
  the highways (home at least 1 and the rest; nothing taken back unless the model's track falls; a net a purchase
  joined to an older one's piece frozen). An island's line is its four spine arms grown evenly from its hub; no
  corridor, no link (the railway grows none). Yards: yard 0 at home as before; a new yard goes first to an island
  rail piece with none (piece order), on its own track nearest its hub, then beside works as before. An island with
  a yard grows its line to its edges, as a station does at home (FX5). Sidings and branches leave from an island's
  line as from the home main line. The freight station is a home yard; no train is routed to an island yard
  (`wayTo()` null): what reaches the island from home stays as the link is, by sea or over ET6's bridge.
- **Slice B, the hub at the port** (`CityMap.MapPieces`): `anchor(p)` is the place of a sea terminal standing on
  piece p's shore, else its hub as before; `hub(p)` the highway crossing nearest the anchor. The home piece keeps
  the founding site.
- No sidecar format bump: the rail net's pieces are written by the code that writes the highways' (FORMAT 12); an
  old sidecar reads as a railway of one piece and the islands open afresh.
