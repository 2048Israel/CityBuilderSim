<!-- gate-run: /home/jerus/cbs/runs/cbs-gate-GB3-yTd4otxs -->
<!-- readiness: provisional -->
## changelog
### GB3 — ensembles run on the gate box first, and BW2 — 2026-11-20, ready, see docs/queue/GB3-notes.md
A builder's 80-run ensemble (`tools/ensemble/ensemble.sh <label>`) runs on `gatebox` first, with the laptop as the overflow
(Jerus, 2026-11-20, "Gatebox first"). It hands its runs to `tools/gate-box.sh --ensemble`, GB1's road: the box plays the
branch's pushed commit eight runs at once (BO1-S's 80 in 17.5 min, against 116 on the laptop), and the runs come home to
`target/ensemble/<label>/` to be scored here as before. Gates and the nightly come first: an ensemble waits for them and
holds `~/cbs/box.lock` whole while it plays; when one comes, the runs started finish (277 s at the longest), the rest
give way, and it resumes after. It runs on the laptop and says why when two heavy jobs already run or wait on the box,
when the box is down (`jerus` mailed), or when it names its own data files. Measured first: the box's scores equal the
laptop's in 47 of 50 rows, the other three one ZERO_SLACK seed's minimums, within the ensemble's noise. With it, BW2: a
gate says "published" only when origin's main holds the batch's tip, and never on a dry run (HS2's dry run beside
FB13's ship was told "HS2 shipped"). Scripts and docs only.
