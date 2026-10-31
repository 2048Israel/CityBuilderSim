# HB1 - a city's ports bundled into one harbour

Queued 2026-10-31 from Jerus's Bergen city (picture 5, month ~2,418, 0.8.90:
about 15 small terminals with ships along one coast). His words, correcting
a first reading that he wanted fewer ports: "also you still want ports..
just perhaps that they bundle up? like side by side removing the buildings
and placing them somewhere else? perhaps an update to the ports sprite?"

## What it is

Keep every terminal the model builds, and draw them together.
- Place them side by side as one harbour on the coast, moving other
  buildings to make room elsewhere.
- Perhaps give them a sprite that reads as one harbour, not scattered boxes.
This is map placement and drawing: `CityMap` and the terminals' sprites.
MF1's "two harbours at the most" and SL1's capped terminal boxes are the code
there now.

## To do

1. **First find out what picture 5 is.** Load the Bergen autosave (a
   read-only fixture at
   `C:\Users\Jerus\Documents\cbs-fixtures\bergen-pictures-2026-10-31\save\`;
   copy it into a `GameFiles.scratch` folder, never a slot). Are its ~15
   terminals many small terminals in the model, or a few drawn apart?
2. **The placement**: the terminals one harbour on their coast, the
   buildings that stood there moved; MapCheck's layout watch asked whether
   moving them counts as "a building moved" (it must not read as the map
   fault MF2 fixes).
3. **The sprite**: put its look to Jerus as a call with renders (two or three
   options), not decided here.

It follows MF2, the map batch, since both change `CityMap`'s placement. AL1
(the auto-builder's links) keeps "islands left unlinked" and leaves the
harbour's look to this batch.

## His further words, 2026-10-31

"also not that all ports must be connected, just that if on same island, they
prefer to be connected, but you can still have mutliple ports disconnected,
also if the ports have inventory or whatever... perhaps showing it? and
perhaps some rails prefering to be with ports thus a sprite with port rail,
and perhaps even a small little upgrade to the rail and port sprites, showing
a little thing taking the cargo or putting it ... just a little line pixel
thing that moves say an orange box to the train or off the train or ship or
whatever. and that ships dock properly. just some ideas, if its too much no
worries."

So HB1 is narrowed and widened by one point each:
- **Bundling is a preference, not a rule.** Terminals on the same island lean
  toward one harbour; separate ports on other coasts or islands stay allowed.
  This narrows "side by side" above.
- **Ships berth alongside the quay**, not near it.
The quay's stock, the port rail and the crane are HB2's, after this batch.
