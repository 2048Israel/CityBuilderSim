<!-- gate-run: /c/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-PO1/target/po1-gate-main-ei1 -->

## changelog
### PO1 — The path lines as nightly observations — 2026-11-13, ready, no tag (process), see docs/queue/PO1-notes.md
Played-path premises in 29 harnesses use 170 observation call sites. A missed
premise prints OBSERVED and names its skipped dependent laws. Reached laws keep
their correctness verdicts. Nightly retains each line's last hold and reports
every miss with the intervening merge range. Exactly 15 superseded known-red
entries are retired. Full picks, the nightly self-test and the fresh-clone
no-tag dry run are green, with the existing DeathRecordCheck and PlanCheck reds.
