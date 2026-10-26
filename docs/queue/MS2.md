# MS2 - the near view drawn the same past the reach

Queued 2026-10-26 by the orchestrator, after BL1, for the same session (the
same files: `ui/MapView`, `GlobeFrame`).

## Jerus, 2026-10-26, on 0.8.52 (the requirement)

> now i can zoom anywhere, but look at these two images, both around the
> edge of last time except i can continue but look a the wierd resolution
> by just panning a few more kms

His two screenshots, both at the 2 km scale, a few km apart, 37 km north of
Baddeck (Cape Breton):
- `MS2-near.webp`, inside the reach: the city's plane, with the ground
  classes as smooth patches, lakes in their real shapes, and the full legend.
- `MS2-far.webp`, just past it: speckled, dithered ground, every lake a
  perfect circle, and the globe's short legend.

## The cause, from MS1's notes (§4)

Closer than the handoff (0.48 km a pixel), the view is the city's plane only
within the reach: 300 km from the city, or 50 km round its ground and offers
(spec-globe 3.4, Q3). Past the reach, MS1 lets the camera go on, but draws the
GROUND tier by the far rule through the globe's camera alone
(`MapView.farRuleOnly()`). That rule was made for far scales, and at a near
scale it shows as speckle and circles.

## What MS2 does

At every near scale, anywhere on the Earth, the ground is drawn as the plane
draws it inside the reach: the classes at their own resolution from
earth-2's tiles, lakes and rivers in their real shapes, the coast as the
plane draws it, and the same legend. There is no visible change at the
reach's edge as the camera crosses it. The ways to get there are the
session's to weigh, with a recommendation in MS2-notes §1. For example, the
plane's ground raster for any tile the camera sees (the city's own layers
only within the reach), or the far rule given the plane's resolution and
water at near scales. Watch the EarthData LRU (ET1-C: 33 tiles of earth-2 in
the 10 MB cache) and the frame time while panning.

## Scope and checks

- The screen's batch. The model is untouched, so the trace must be
  byte-identical, and there is no ensemble.
- A harness fixture renders a near-scale view straddling the reach (the
  generated world's equivalent too, if it has one). It finds the ground
  classes and water continuous across the edge, not dithered, and no lake
  drawn as a circle where earth-2 has its outline. The timing harness that
  covers the map's frame stays inside its budget.
- The eye check under MS2-C, for Jerus: pan from the city out past 300 km at
  the 2 km scale. The ground should look the same all the way.
