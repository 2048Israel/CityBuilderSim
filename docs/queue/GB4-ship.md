## changelog
### GB4 — the gate box's measurements on record — 2026-11-20, ready, see docs/queue/GB4-notes.md
`docs/gatebox.md` holds what the gate box is and what its first night measured (Jerus, 2026-11-20: "put it on github
so that if other sessions ask its there"). It covers the machine, its 7-hour soak (every test passed, 83.8 °C at
most), one job alone (3% from best core to worst, +30% beside its own core's other thread) and many at once. 16 jobs
at once run about 40% slower each, yet finish about 30% sooner than two rounds of 8. A gate someone waits on runs at
most 8 at once, one per core; a pile of independent runs fills all 16 threads. The contract's gate box bullet points
to it. Docs only.
