## changelog
### {next} — FW10, the full-screen chart's foot stays on the screen — 2026-10-25, ready, see docs/queue/FW10-notes.md
City History's chart in full screen, with many lines picked, ran past the
window's foot: `TimeChart.heightBesidePlot()` counted the toolbar as one row of
34 px, where a row is 27 and each row the legend wraps to adds about 29, so from
three rows the overview strip and the caption were cut (at 1280x720, 16 lines
ended 43 px below the window). The chart now measures what stands beside its
plot at the width it is drawn at, and `fitTo()` gives the plot what is left.
ChartFitCheck lays it out at four window sizes with 2 to 16 lines: red on
main's sizing (23 FAIL), green on the fix. Renders in `docs/queue/FW10/`. The
model is untouched; no ensemble.
