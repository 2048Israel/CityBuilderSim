## changelog
### {next} — UP7b, the speed reading shows a stall the clock's own redraw keeps making — 2026-11-07, ready, see docs/queue/UP7b-notes.md
UP7's gate review, on the clock. UP7 counted every stalled frame at its clamped share, so a redraw costing more than a quarter of a second
(due every three times its cost) ran the city at about 83% of the rung while the header's reading stayed white. The frame after the
clock's own redraw now counts the redraw's measured cost as well (at most half a second), so that stall reaches the window and the reading
turns amber once the city is under nine tenths of the rung; a stall nobody made (a dragged or minimised window) is still clamped, and a
redraw begun while the clock stood still claims nothing. The post-layout listener that takes a redraw's cost reads the pace field when it
fires (`() -> pace.redrawEnded()`), no longer the pace the clock started with. ClockCheck 7 causes the recurring stall at 1x and 50x and
reads the city at 82% of the rung; it fails with the cost left out. No model change, no saved field, no ensemble.
