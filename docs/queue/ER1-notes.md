# ER1 — What holds Rotterdam's Earth city at twelve thousand for a century

## Contract and reproduction

Diagnosis only, from [AN2's finding and ★1](AN2-notes.md). No model code,
save slot or balance file is changed. The branch starts at main 8083aaf33
(0.9.117). `docs/notes/known-reds.txt` has no entry naming ER1 at that commit.
The first `./batch-check.sh --fast` passed: compile, maps, zero firm stale
findings and no tree changes.

★ Player comparison (orchestrator's instruction, 2026-11-15): run today's
standalone defaults first, since that is the game a fix would change. AW1
changed the standalone player's default to automatic works with cash building
past the debt limit. AN2's then-default player was the scored advisor. If the
current default no longer stalls, repeat with `-Dplaytest.autobuild=false`,
and name that player when describing the result. Each Rotterdam city has a
generated-world default beside it using the same player. This avoids calling
a player-policy difference a demonstrated model repair.

The standard commands use `LongPlaytest --earth rotterdam` and `LongPlaytest`,
`-Dplaytest.months=4000`, the standard founding, world path and seed, with each
of the eight standard trace files enabled. Both data paths are explicit:
`C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin`
and the neighbouring `world-1.bin`. The cities run sequentially in a detached
Git Bash process (launcher PID 3516). They use LongPlaytest's own TempDirs;
none opens Jerus's saved Rotterdam. Raw logs and CSVs are in `target/ER1/`.

The report's `fields` figure is crop self-sufficiency, capped at 100%, from
`Agriculture.getSelfSufficiency(Game)`, not the fraction of land occupied by
farms. Its `land` figure is `LandManager.getUtilisation()`: the clamped used
share of linked, buildable ground. Keeping the two denominators distinct is
necessary before treating a third of the crops as a third of the ground.
