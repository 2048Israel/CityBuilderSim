# SX2: the cells never buy shares from the world on LX1's path

Found at FB4's dry run (2026-11-17, `/tmp/cbs-gate-FB4-1zp4n63E`).

**The red.** ShortSellingCheck section 7 (`howOften()`) plays `RUN_MONTHS` (1,000) months of the playtest's city
(`play(true, ...)`). It asserts that the cells both sold shares to the world and bought from it: its long, its short
or the leavers.

| main | months sold to the world | months bought from it | verdict |
|---|---:|---:|---|
| f18b10537 (0.9.137, before LX1) | 289, $23,238.99 | 63, $17,939.36 | green |
| 886bd92dc (LX1's model, 0.9.138) | 269, $36,057.90 | 0 | red |
| c471e961f (0.9.139) | 269, $36,057.90 | 0 | red |

FB4's branch (31b60952a) has main's figures to the cent.

No gate has run ShortSellingCheck since LX1's model change: it has no log in LX1's kept run
(`/tmp/cbs-gate-LX1-MV0wqgUr`) or in FB7's (`/tmp/cbs-gate-FB7-Gss1vBX6`), as the orchestrator checked. FB4
passes it by name (its ship file's `## known-reds`).

**Done** means:
- find why the cells' purchases from the world stop on LX1's path. LX1 changed luxury retail; which part of it stops
  the purchases is not known;
- then either a model fix, or a section-7 fixture that causes a purchase from the world.

The assertion's premise stays: a count of how often the trade fires in a real run.

Also find why the pick left ShortSellingCheck out of LX1's and FB7's suites, though LX1 moved the model: a pick gap,
like the MoneyAudit one.
