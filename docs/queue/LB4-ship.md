## changelog
### {next} — LB4, Rankings: the city in play beside its own slot, and a lighter read of the saved cities — 2026-11-09, ready, see docs/queue/LB4-notes.md
A saved slot holding the city in play (the same city name and world seed in its header) now reads "this city, as
saved · slot N" on the Rankings board and in its drawer, and is left out at Your age, where it only tied with
itself; the not-at-age line names it instead (LB3-D). The saved histories are read with Gson skipping every series
but the ~58 the board keeps, and those keep as `double[]` and `long[]` rather than boxed lists (LB3-R): ten
6,000-month cities hold 32 MB after the read, not 97. No model change; the trace is main's. RankingsCheck proves
both (19, 20, and 17's packed tally); UiProbe measures it with `-Dprobe.saved=N` in its own scratch folder.
(Jerus, 2026-10-31: "Take all (Recommended)".)
