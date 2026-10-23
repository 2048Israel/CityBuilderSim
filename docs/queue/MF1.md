# MF1 - the map's faults from his "For Claude" city

Jerus, 2026-10-23, at the review desk, with 13 screenshots of his save slot
named "For Claude" on 0.8.28 (`docs/queue/MF1/1.png` ... `13.png`). He took
"one batch for all" and placed it "At the end". A map batch: nothing in the
model reads the map, except item 7 (where an overflow building stands), which
is the model's and moves the trace.

Reproduce each item on a copy of that save (never write slots 1-9; copy it to
a scratch folder), and draw a before and after into `docs/queue/MF1/` for his
eye check.

## What he found

1. **Highways drawn over buildings.** A highway runs straight across standing
   works and blocks: a Blast Furnace's block cut in two (shot 2), industry
   under the highway (8, 12), a highway ending in a field past a street (8).
   This is FX7's ★1 (`FX7-notes.md` §6): since 0.8.24 a run laid across
   standing buildings leaves them standing and both are drawn. His call
   (2026-10-23, "Buildings move off"): the buildings under a new run leave
   and are built again on free ground nearby in the district, the same
   business, and the run gets clean ground. The layout watch then flags the
   old works a run lands on, a restatement of its premise (FX7 named it); a
   building that is not under a run still never moves.
2. **Works on the rail yard** (shot 13, the land office's map): industry
   drawn over the freight yard's tracks and platform. The same call as 1:
   what stands on a yard's ground moves off it.
3. **A half roundabout** (shot 11): the tree-circle roundabout where the
   diagonal avenue meets a street is cut flat on one side (a district's or a
   tile's edge clipping it).
4. **The four-ring interchange** (shot 5): the highway crossing is drawn as
   four separate ring loops; he picked it as a fault. Draw a cleaner
   interchange (a cloverleaf's or a diamond's shape).
5. **Paved roads drawn as three roads.** His words: "continous intersection,
   like its a 3x14 road roughly, but shown as 3 roads, with intersection on
   every node". A wide street (several plots across) is drawn as parallel
   streets with a crossing at every node; draw it as one wide road.
6. **Rail with square corners, and dead ends** (shots 7, 12): FX5 (0.8.18)
   was to round the routes (`TrackCurves`), but in his city the railway
   still turns at right angles along the streets and has stubs that end
   nowhere. Find why FX5's curves do not show there; a stub with no traffic
   is not drawn or is taken up. (CL5 grows the nets; this is the drawing and
   the spurs.) With it, FX7's ★4 ("Into MF1"): the founding district's yard
   is taken up and laid again on the same spot eleven times over the
   playtest's run; trace why and stop it.
7. **The outer rim** (shot 6; the desert in 3): small buildings dotted on an
   even grid over the city's far, empty land. He: "i understand mines and
   wells, but these werent either". Likely the buildings the key counts as
   "N buildings packed without a street: the city is short of ground" (194,
   90 and 40 in his shots). Confirm what they are first. His call: keep them
   by the city - an overflow building stands at the built edge beside the
   streets it belongs to, with a street laid to it; no lone grid in the
   fields.
8. **The coast** (shots 3, 9, 10): every quay and sea terminal gets its own
   pier, so the whole shore is piers ("i dont think real life is like
   that"), each with its own ship lanes fanning out, hundreds of lines. His
   call: the berths gathered into one or two harbours at the best shore,
   ships sharing a few lanes that split near the harbour; the rest of the
   coast stays beach.

The hover cards in shots 1, 2 and 10 are right (Jerus: "No, it's right").

## Done when

Each item reproduced and fixed on the copy of his city, MapCheck (and
RailDrawCheck for item 6) holding a fixture that causes each condition, the
renders beside this note, and his eye check under Pending as MF1-C.
