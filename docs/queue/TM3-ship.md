## changelog
### TM3 — the gate's timing judged side by side, main against the batch — 2026-10-27, ready, no tag (tooling), see docs/queue/TM3-notes.md
A timing red at the gate was judged by one run of the merge's and one of main's minutes later, so a PC busy with Jerus's game stopped HY1, MS2 and CC1-S on map lines main itself misses.
Now every timing assertion writes its figure and budget to `target/checks/timing.tsv` (`Timing.verdict(ok, label)`), and when the hard run has a red, `ship.sh` runs the red harnesses
again with main's classes and the merge's in turn, three rounds, no quiet minute between them, and passes the red unless the batch's median is more than 15% over main's
(`tools/timing-pair.awk` holds the arithmetic and the dial); the Gate-timing line and `gate.txt`'s `#` lines carry both medians. `--red-ok` reaches that step and names the lines it passes.
MapCheck's "the view adds nothing a tile", a comparison of two measured times that stayed hard in the soft suite, is a `Timing` assertion, and TimingCheck fails any other.
`ship.sh` runs from a copy of itself, so a batch that changes it cannot rewrite it under the shell. `ship-selftest.sh` 94 of 94 with stub figures; one end-to-end rehearsal on real JVMs.
