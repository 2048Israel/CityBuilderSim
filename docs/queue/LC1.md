# LC1 - every data source the game ships allows commercial use

Queued 2026-10-31 on Jerus's word, answering LB1-U (UNODC's figures, whose
portal now says "personal, non-commercial use"): "note, this game will be for
commercial use, so only use sources for the rankings that allow that". The
same holds for every figure the game ships: it is sold.

## What it audits

1. **The rankings' file**: `src/main/resources/world/world-2025.json` and its
   sources (`tools/world/measures.tsv`, `ATTRIBUTION.txt`, `measures.py`'s
   source keys): the WDI series, the OECD, the BIS, the UN's WUP 2025, the
   EIA, the BLS and any other.
2. **The world file** (`world-1.bin`, WN0's pipeline in `tools/world/`) and
   **the Earth file** (`earth-2.bin`, `tools/earth/`, its
   `ATTRIBUTION-2.txt`): the sources their attribution names.
3. **EP0's series for EP1** (`docs/studies/1900-start/*.csv`, sources in
   `docs/studies/spec-1900-start.md`): the Macrohistory Database, the
   Maddison Project, the national statistics and the parities. EP1 will bake
   these into the game.

For each source, read its licence or terms page as it stands today. Record
the licence's name and URL, whether commercial use is allowed, and the
attribution it asks for.

## What it changes

- A source that does not allow commercial use leaves the shipped data. For
  the rankings, its measure ranks the player's own cities only (as LB1 did
  for UNODC). For EP0's series, name a commercial-use source that gives the
  figure instead, or the gap rule EP1 applies. For the world and Earth files,
  name the replacement and leave the re-bake to its own batch if one is
  needed. Say which in the notes.
- `docs/studies/licences.md`: one table, source by source, the reference
  every later data brief reads first.
- The game's credits: if the attributions the licences ask for are not all
  shown somewhere a player can see, say where they belong (an eye check for
  Jerus), and add the missing ones to the attribution files.
