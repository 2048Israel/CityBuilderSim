# CL5 - highways and railway that reach the whole city, on every island it builds on

The study is [spec-city-layout §5.6](../studies/spec-city-layout.md#56-highways-on-one-island-only)
(CL1, 2026-10-20). Jerus: "why do highways end up only in one island ... if there are three
islands, only the starter isalnd has highways"; and, shown why, "Queue it as CL5". After FX5 and
CL4: FX5 changes `CityRuns`' diagonals and curves, this batch its growth.

The map's alone: nothing in the model reads it, the playtest's trace stays byte-identical. Bump
`GameVersion.VERSION` (player-visible). ET5 and ET6 stay as briefed (spec-earth2-terrain §10);
this batch is what they leave out.

## The fault, measured on his cities (study §5.6)

`CityRuns.newCorridor()` opens a corridor only where its line - one district (`APART`, 256
plots) from the last - crosses the net already laid; and an arm crosses no sea. His Bergen
(1.7M, slot 1): the net is the spine alone, 603 highway plots drawn and 6,999 of the model's
never laid (92%), 3 of 35 built districts with a highway, 34 of them on the founding site's own
ground. His Danzik (6.4M, slot 2): 16,366 drawn, none short, 19 corridors, but its one built
district on another island has none.

## What to build

1. **A net never stops while it has plots to lay and the city has ground.** When no arm can grow
   and no corridor line crosses the net, the next corridor opens from the net's plot nearest its
   line, joined to it by a LINK arm laid by the arms' own rules and cost (straight, 45 degrees at
   the water, a bridge where the arms bridge), its plots the net's like any arm's (drawn,
   counted, taken back newest first). The railway likewise.
2. **A net on every piece of ground the water parts that the city builds on.** The city's owned
   dry ground in pieces (at 32-plot tiles, eight ways, water an arm cannot bridge parting them;
   worked out again when the land changes, a purchase, not every month). Each piece holding
   buildings grows its own highway net and, when it holds a works the railway serves, its own
   railway, from its own hub: its plot nearest the founding site - once ET5 has ports, its first
   port's gate; once ET6 has bridges, a long bridge's landing, where the far net simply starts.
   The model's Elevated Highway and track plots dealt between the pieces by their built ground,
   the home piece first, each piece's share grown and taken back newest first as today (H4).
3. **Nothing laid moves.** A purchase that joins two pieces leaves both nets where they are;
   they meet as any two arms meet (an interchange, or a T).
4. **The sidecar** keeps each piece's net after the home one (FORMAT bump); one with a single
   net reads as today.

## The budget

The flood at 32-plot tiles once a purchase (his Danzik: 122,630 tiles on its home piece, a
fraction of a second off the month); a link arm's search only when a net stops. Keep MapCheck's
`RECONCILE_MS`, its runs' timing and `SCREEN_MS`.

## Harnesses

A section whose fixtures cause each case, red on main first: a peninsula whose spine the sea
stops short of `APART` (all the model's plots laid after it, none before); two islands each with
buildings (each with highway plots, none laid across the sea); a purchase joining two pieces
(nothing moves). On his two saves (scratch copies) the plots never laid fall to none and every
built piece has a highway; counted, as README's *The checks* asks.

## Done when

The suite green but what was already red, the playtests byte-identical, the maps regenerated,
before/after renders of his Bergen beside the notes (`docs/studies/city-layout/RenderCL1.java` on
a scratch copy, never his slots), the changelog's block, this row out of `QUEUE.md`.
