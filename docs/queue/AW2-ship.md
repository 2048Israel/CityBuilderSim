<!-- gate-run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-AW2-22S9uAGu -->

## changelog
### {next} — AW2, allocated land kept through a normal load — 2026-11-13, ready, see docs/queue/AW2-notes.md
Saves carry the live allocated-land counter and its building footprint, so an
ordinary load keeps the same free ground and construction behaviour while
allowing for later catalogue plot changes. Saves without the counter retain
the previous footprint reconstruction; explicit zero remains zero. SaveFileCheck
causes the WG2 fixture's land gap, changed plots and continued private/automatic
building with land purchases; LongPlaytest counts and traces ledger gaps.
