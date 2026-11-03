# LM1 - the land office map: the starting ground, the offers' numbers, the deposits zoomed out

Queued 2026-11-02 from Jerus's play: his screenshots of a new city 7.8 km
west of Stockholm, 1 January 1900, month 1, nothing pressed yet.

## What he saw

1. **The city starts on a river strip.** The new city owns a strip one or
   two squares wide that winds about 5 km along a river from one lake to
   another, plus one block of about half a km. A city should start on one
   compact piece of ground. Find where the founding ground is chosen and why
   it follows the river (the river and the lakes are the likely pull). Make
   it one compact piece round the founding spot, taking in water only where
   it borders that piece.
2. **The offers' numbers repeat.** Each clump of offers along the strip is
   numbered 1-5 (one 1-6) on its own, so the map shows "1" and "3" several
   times and no number names one offer (`MapView`, and `LandScreen`'s "its
   offers hatched and numbered"). Give one number per offer across the whole
   map, the same number the land office's list shows.
3. **Deposits vanish below a zoom level.** Two shots of the same map: with
   the 2 km scale bar about 75 px long, no deposit is drawn at all; at about
   87 px they are. The cut-off sits right at the zoom a player scouts at.
   Jerus: "i know its for optimization... but that makes it super hard to
   find resources". Keep the saving. Past the cut-off, draw one small icon
   per deposit (its resource's mark, at its centre): a few dozen draws, not
   thousands of shaded squares. Zoomed in, the shading stays as now. A
   Resources toggle beside Nations, Trade routes and Borders, on by default,
   hides the icons.

## Checks

- On a fixture founded on a river between two lakes (the shape of his
  Stockholm city), and on his Stockholm spot itself, the starting ground is
  one compact piece. The measure is the batch's to choose, asserted against
  its own constant.
- No offer number appears twice on the map, and each matches the list.
- At the zoom where the shading is off, every deposit in view has its icon.
  Time one frame at that zoom before and after the change.
- The founding change moves the trace (a new city's ground differs); the
  rest is screens only.
