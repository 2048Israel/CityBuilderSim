# DM1 - standing buildings move in the district plan after a reserve (found by FC1)

Found by FC1 (2026-11-02, `docs/queue/FC1-notes.md` section 1). With FC1's builders' price on, MapCheck's cities
build otherwise, and section 2h, "none of the boxes of the month the reserve came ... has moved", finds 12 of 779
boxes moved four months on in the first scenario and 23 in the second (0 of 539 without FC1). The moved boxes are
mostly 1x1 houses and 2x2 and 2x3 mixed buildings, clustered at plots (5-14, 61-62), (67-69, 47-50) and
(34-45, 67-70). None lies on fixed ground, a run or a highway verge (one or two are beside one). DistrictPlan says
what stands never moves, so it is a planner defect that FC1's path reaches.

To do: delete MapCheck's `BuildingManager.buildersShortOn` lines (off at the top of `main()`, on after section 2h), reproduce section 2h, find why a standing
box is re-placed, and keep it. Done when MapCheck is green with FC1 on.
