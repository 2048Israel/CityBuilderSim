<!-- gate-run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-CM1b-lA470zVh -->
## changelog
### {next} — CM1b, The replay loaded move, the review follow-ups — 2026-11-15, ready, see docs/queue/CM1b-notes.md
Replay camera moves release their stored pictures, pins and prepared months immediately when stopped while Ready, and restore their starting view if their map closes during loading. Loads remember every missing kind, stop retrying failed jobs, and refuse at a stated overall limit. Their memory budget measures growth from the starting heap, with at most one collection per load. Keys are consumed during loading, and ReplayCheck closes its replay and unregisters both map pumps before its timing section. CM2's prepared maps and ordinary game behavior are preserved.
