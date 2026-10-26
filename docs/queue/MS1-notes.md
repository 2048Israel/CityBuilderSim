# MS1 - the expanded map: its head, a drag that selects, picks that last, and the near view anywhere (0.8.52, 2026-10-26)

The brief: [MS1.md](MS1.md), Jerus's words of 2026-10-26. Two slices, each written by a Sonnet 5.5 agent at xhigh and
reviewed by the Opus batch session: A the model's half (`LandMap.Picks`, `GlobeFrame`, MapCheck 14/14b/14c, GlobeCheck
2/2b), B the screen's (`ui/MapView`, `ui/UserInterface`, `LandMap.pressBoxes`, MapCheck 14d). The month is untouched.

## What it does, as built

1. **The window's header stays in view over the map.** The expanded map now lies under the header instead of over it
   (`UserInterface.showChartFullScreen(..., underHeader)`, the pane's top held at the header's height), so the clock and
   speed, the cash, population, GDP, inflation, out of work, the rate and the currency are the header's own, live and
   drawn on the clock: shared, not copied, nothing recomputed. Chosen over copying the figures into the map's head
   because it is what Jerus already reads, every tile of it, and the speed controls come with it at 50x. The map's head
   drops its clock line and keeps its land parts (the cash in the money the toggle pays in, the toggle, the hint, Select
   every offer, Fit, Back). A tile or the money block opens its screen and closes the map, as before; the inbox and Needs
   you, whose panels lie under the map, now close it first. City History's full screen still covers the header.
2. **A plain drag selects** (the ★, taken on its recommendation). On the expanded map a left drag that starts on an
   offer's free ground draws the box; one that starts anywhere else pans, as before; Shift and a drag boxes from
   anywhere; a right or middle drag always pans and never clicks; W, A, S and D pan. The cursor says which a press would
   do: a crosshair over an offer's free ground, an open hand elsewhere, a closed one while panning. The other ways
   weighed: a Select/Pan toggle in the head (a mode to forget, and the key to find), and the right button panning with
   every left drag boxing (it would take away the drag that pans, which Jerus uses). The press's rule is
   `LandMap.pressBoxes()`, so MapCheck 14d holds it.
3. **The picks last through the month.** The cause was not the office: it never relists a standing offer
   (`LandMarket.update()` lists only empty places; an offer leaves only when bought). `Picks.live()` itself dropped every
   pick when the month turned (spec-earth 3.2's rule, which Jerus's words now replace). A pick now holds its offer's
   rectangle: kept while its offer is listed; when its id leaves the shelf, dropped if the city now owns all its ground,
   carried onto the offers listed on what is left of it, or dropped if none is. The head says what was dropped or
   carried and why in the warning colour (`Picks.dropNote()`, e.g. "North 3 dropped: the city bought its ground") until
   the next pick. A load or another city still drops them all. The card and the box being drawn were never reset by a
   month; with the picks kept, they stay.
4. **The near view far from the city.** On an Earth world the wheel stopped at the handoff (0.48 km a pixel) once the
   view's centre was past the reach - 300 km from the city, or 50 km round its ground and offers (spec-globe 3.4, Q3;
   `GlobeFrame.clampZoom()`, `zoomAt()`). Now the wheel reaches the closest scale anywhere: closer than the handoff the
   view is the city's plane within the reach, as before, and the GROUND tier past it, drawn by the far rule through the
   globe's camera alone (`MapView.farRuleOnly()`). A generated world never stopped: `MapFrame` pans and zooms anywhere
   on its square, and MapCheck 14c now says so.
5. **The wall** was the reach's other half: zoomed in on the plane, a pan was pulled back at the reach
   (`GlobeFrame.clampPan()`), so the view stopped dead 300 km out. It is gone; crossing the reach at a near scale swaps
   the plane for the GROUND tier. No other seam turned up: at the reach's edge the plane and the far rule part on 0.15 to
   0.65% of a patch's pixels at the handoff and 0.56 to 3.32% at 0.12 km a pixel (coasts and rivers by a pixel or four;
   Halifax's coast the most), and across the antimeridian the seam's columns differ no more than neighbouring columns
   (0.8% against 12.5% on the fixture's Fiji coast, 0 against 18.8% on the full file's Chukotka).

## Checks and the trace

- **GlobeCheck** 2 rewritten (the stops are gone) and **2b** new: far from the city and across the antimeridian the
  wheel reaches `leastKmPx()`, the GROUND tier, the point under the pointer kept to 1e-9 px; drags outward at a near
  scale cross the reach with no stop, PLANE inside and GROUND past it; the roll eases; the crossing and the antimeridian
  drawn continuous (the figures above), on the fixture and on the full earth-2.bin.
- **MapCheck** 13's D-held assertion and 14's month premise rewritten; **14b** picks held over three months of relists,
  a bought one dropped and named, two counted, an unoffered one named, one carried onto its ground's new id, the cheap
  path; **14c** the generated world's far near view; **14d** the press: an offer's free ground boxes, the city's own
  ground, its centre and unoffered ground pan, Shift boxes from all.
- **KeptCheck** gained no section: `KeptBench` stands up the header but not the window's stack, so the map under the
  header could not be built there; slice B checked it with a throwaway probe on the real overlay (the pane starts at the
  header's bottom, follows its height, lets go on leave). It is the eye check's.
- **The default playtest's trace** is byte-identical to main's (0.8.51) but for its two run-time lines. **No ensemble.**
- **`./batch-check.sh --pick`**, earth-2.bin named: see the ship file; main's recorded MapCheck red the only red.

## Left for Jerus (MS1-C, the eye check on the PC)

At 50x on the expanded map: the header in view above it, figures moving; drag across offers to box them, let two
months pass, buy them; drag from the city's own ground and with the right button to pan; zoom in on the far side of the
Earth to the closest scale; from the city at the closest scale, pan out past 300 km.
