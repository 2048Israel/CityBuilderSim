# CU1 notes ({next}, 2026-11-09)

Copper Refining's margin, a study (CU1's row in `QUEUE.md`; LS1's study,
section 4). No model change: every changed path ends in `.md`.

## What it found

- The note is `docs/studies/spec-copper-margin.md`, its row at the top of
  `docs/studies/README.md`; the probes' sources are text in
  `docs/studies/copper-margin/probes.md` and what they printed is
  `copper-margin/tables.md` (the merge is documents only, so no `.java`,
  `.py` or `.sh` is committed).
- The sector is Rod Mills on imported cathode, exporting rod (LS1's city: 276
  by m2400, 1,679 by m3996; no mine or smelter in any run). Its books are the
  world's rod over its cathode: 1.03 x 1.09 / 1.93 = 0.58 against the books'
  57.3% at m2400.
- The cause is in `WorldMarket`: each good is solved alone, the mean mill
  price is held to the path, and the city buys from the cheapest maker (DR
  Congo's cathode at a seventh of the median maker's by 2100) and sells to the
  best buyer. Third appearance of one fault, after WN3's `shockOf` and FL1.
- It is on every seed whose sector is alive (363-446 mills at m2400 on seeds
  1-3 of main) and on the Historical path (251); main's seed 0 builds none
  because its sector is dead (DS1), so the gate's default playtest never sees
  the mills.
- Real figures (USGS, the Economic Census, OTA; Teck and others as evidence
  only): materials 67-86% of sales, a charge between a quarter of the price
  (2002) and nothing (2026). The literals are right; the played world leaves
  them.

## Decisions

- Measured on LS1's commit (0.9.60, 092130f4, in a temporary detached
  worktree, removed) and on main at 0.9.69 (cce06c9c). The branch is cut from
  0.9.70 (bf5b0686); `WorldMarket.java`, `Nations.java`, `Good.java` and
  `buildings.json` are the same files in both.
- The recommended way is to re-anchor a series' followers on its own good at
  the city's site, wire and cable as rod plus fabrication: CU2, briefed in the
  study's section 7. It was prototyped (`anchor.py`), not built. The prototype
  ran `LongPlaytest` only; no harness was run on the patched class.
- Queued (`## queued` in the ship file): CU2, WP1 (a study), DS1 (a look).
  The ids were free (`git grep -w` over the `.md` files).
- CU1's row covers "the study, then the batch that sets it": the study is
  this, the batch is CU2. LX1's row says "after CU1"; it should wait for CU2
  (★6), which is the orchestrator's edit in `QUEUE.md`.
- `licences.md` gets no row: no figure from the research reaches the game
  here or in CU2. A batch that takes one adds its row first.
- No trace moves (no model change): no ensemble. Not a process change (one
  in, one out does not apply).

## The calls for Jerus (the study's section 7)

| call | recommended |
|---|---|
| ★1 the way: re-anchor at the site, a corridor, a chain-linked solve, or stop the planner | re-anchor at the site |
| ★2 its scope: copper's four followers, or every series' | every series, one rule |
| ★3 wire and cable as rod plus fabrication, in CU2 | yes (without it the Wire & Cable Works take the mills' place, 81 to 276) |
| ★4 the charge's level: keep the 2024 literals, move them, or a cycle | keep them, no cycle |
| ★5 the path anchor for every good | not in CU2; WP1 |
| ★6 LX1 waits for CU2 | yes (CU2 halves the old city's income a head on three seeds of four) |
| ★7 seed 0's dead Copper Refining | a look, DS1 |

## What to look at

- The study's section 6 table: what the recommended way does to a played
  city (people kept, GDP a head 3.4-4.6 to 1.7-2.1, Manufacturing and
  Automotive carrying it).
- What the prototype does not show: the suite under the anchor, anything
  past m2400, an Earth city, a city with a mine.

## Follow-ups, not fixed here

- A `SessionStart:compact` hook printed the orchestrator's hand-off note into
  this worktree session and into its research subagent after each compaction;
  the note says its hooks print nothing in a worktree.
- Three of the sixteen prototype runs print "a building on the map moved"
  once; one is main's own seed 2 at m2397 (the known class, FD1-D and BA1-D).

## Checks

- `./batch-check.sh --fast` exit 0: compile OK, the maps regenerated with no
  generated document changed, Stale 0 firm findings. `git ls-files --eol`
  shows the five files LF.
