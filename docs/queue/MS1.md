# MS1 - the expanded map: its head, a drag that selects, picks that last, and the near view anywhere

Queued 2026-10-26 by the orchestrator, first in the queue at Jerus's word.

## Jerus, 2026-10-26 (the requirement)

> on the map i thinnk there is a batch that will add ui to it, so i should be
> able to see the top part of the screen aka population, gdp, treasury, rate,
> exchange rate inflation all that i already see, i think that is queued, but
> what i need addtionally is on the map view, i should be able to not only
> click and shift click, but also drag and select, and that on monthly update
> it doesnt reset cause currently it does so its a race against time unless
> paused, and at 50x its impossible to buy land like that. additionally a map
> bug i found is that you cant zoom in on areas of the map far away from the
> city, and if zoomed in there is a wall that you can cross, probably since
> its a globe? idk.

## What MS1 does

1. **The window's header in the map's head.** The expanded map lies over the
   whole window and hides the header (`docs/studies/spec-earth.md` §6 "The
   expanded map hides the cash"; `spec-nations.md` §8 "show your population
   and gdp on the top").
   The map's head carries the header's figures: population, GDP, the
   treasury's cash, the policy rate, the exchange rate, inflation, and any
   other tile the header shows. The same figures through the same getters as
   the header (`ui/UserInterface`), refreshed on the clock; never recomputed
   in the screen. What was queued was WN2's "the head's POPULATION and GDP
   tiles": MS1 takes them over, and WN2's row says so.
2. **A drag selects.** Today a plain drag pans and Shift with a drag draws
   the box (B7, `MapView.boxed()`). Jerus wants to drag and select without
   hunting for a modifier. A ★ call, taken on its recommendation and named in
   the notes: how a plain drag both selects and pans. For example, a drag that
   starts on an offer's free ground boxes and one that starts elsewhere pans;
   or a Select/Pan toggle in the head with a key; or the right button pans.
   W, A, S and D keep panning whatever is chosen.
3. **The picks last through the month.** `MapView.picks` (`LandMap.Picks`)
   is dropped on a month because the office lists its offers again. At 50x
   that leaves no time to buy. Key a pick to the ground it covers, not to the
   offer's slot. On the new month's list, carry it onto whatever offers that
   ground now; drop only ground no longer for sale (the city bought it, or
   nothing offers it), and say in the head what was dropped and why. The box
   being drawn, and the card, survive the month too.
4. **The near view far from the city.** The expanded map cannot zoom in on
   ground far from the city. Find why (the camera's clamp, the tiles kept
   only round the city, the globe/plane switch at a scale) and give the near
   view wherever the map can show ground: on the Earth anywhere, on a
   generated world anywhere on its plane.
5. **The wall at the near view.** Zoomed in, there is an edge, "a wall",
   that the camera crosses. Perhaps the globe's seam (the antimeridian), the
   plane's edge, or a tile or cache boundary. Reproduce it headless (MapView's
   renders, the CL4/FX5 render tools in `docs/queue/`), find the cause, fix
   it.

## Scope and checks

- The screen's batch: `ui/MapView`, `LandMap`, `MapFrame`, `ui/LandScreen`
  and the header's tiles shared, not copied. The model is untouched, so
  the default playtest's trace must be byte-identical to main's but for its
  run-time line, and there is no ensemble.
- Harness sections whose fixtures cause the condition:
  - a pick held over several monthly relists and still picked; one on ground
    the city bought meanwhile dropped and named;
  - a camera far from the city reaching the near view;
  - the seam or wall drawn continuous across it;
  - the head reading the header's own getters.
- The eye check under MS1-C, for Jerus on the PC: at 50x, box a stretch of
  ground, let two months pass, buy it; zoom in on the far side of the Earth.
- Not MS1: how buying land works, the offers' shapes and their overlap with
  owned ground. That is BL1, the next row, which the same session runs after
  MS1 ships, because both live in MapView and LandMap.
