# WC1 - one contract for both builders

The last step of the workflow redesign (Jerus, 2026-11-11). One short file that
Claude and Codex sessions both read, in place of the process rules that sit
today in `CLAUDE.md`, `docs/batch.md`, `docs/orchestrator.md`,
`docs/deepseek.md`, `openai-dontread.md` and about thirty rules kept only in
Claude's memory notes. It is written after GF1, GR1 and GR2 are on main and
MemWatch's state folder exists, because they change what it says. Until then
this brief is where his decisions on the workflow are kept, as he takes them.

## Settled, in his words (2026-11-11)

- **Who builds.** Codex builds whole batches, in its own app or headless, not
  slices under a Claude session ("Yes, that split"; for GR1 and GR2: "Yes,
  Codex builds"). Claude builds the batches he gives it, reviews, and takes the
  decisions with him.
- **Timing.** "Judge it nightly (Recommended)": a missed speed budget no longer
  stops a merge (GR1).
- **Main's known reds.** "A file in the repo (Recommended)" (GR2).
- **Ready.** "When it touches shared code (Recommended)": the whole suite on
  the merge with main before a batch says ready (GR2).
- **The next batch of a chain.** "After it ships (Recommended)": it is cut from
  main once its predecessor is tagged. The machinery for building on an
  unshipped branch goes: `tools/carries.sh`, the mended-predecessor case of
  `wait.sh --row`, `ship.sh`'s stop on a branch built on a batch main lacks,
  `pick.sh`'s diff against a predecessor's tip, the second half of
  `docs/batch.md` 7, the lane paragraph of `docs/orchestrator.md`, and the brief
  template in the memory note `night-line-of-sessions`.
- **Which batches run together.** "by files only, but like in a smart way, for
  example, if something is touching a file that really messes with another
  well...". So: each brief names the source files it expects to change and says
  whether it moves the playtest. Before a batch starts, whoever starts it lays
  that list beside the files the batches in flight have changed so far
  (`git diff --name-only origin/main...origin/batch/<id>`) and holds it when a
  file in common is one where the two would get in each other's way: the
  month's flow (`Game`, `EconomyManager`, `Sector`, `MoneyAudit`), a harness or
  a fixture builder both change, one screen's class. A list every batch appends
  to (`AllChecks.HARNESSES`, `docs/studies/README.md`) holds nothing, and
  neither does a large file two batches touch in sections that do not meet. Two
  batches that both move the playtest may run together on files of their own;
  the second to ship redoes its ensemble on the new main. HL1 and PW1, side by
  side on seven shared files, are the case this is for.
- **The redesign's own process batches** (GF1, GR1, GR2): "these three batches
  shouldnt need a gate no? sure a fresh session, you can create the sessions,
  and then put them on opus xhigh". No gate run: a fresh Opus session reviews
  each against its brief and merges it untagged, on the self-tests and on the
  builder's full dry run in a fresh clone.
- **What the gate runs.** Asked whether every batch that changes no game code
  should skip the whole suite at the gate, not just those three: "Yes, make it
  the rule". Scripts, docs, tools and harness-only repairs run the quick check,
  the script self-tests and the harnesses they change with those that borrow
  them; a change to the suite's runner still gets the whole suite (GR2).
- **A run already made.** Asked whether the gate should reuse a batch's own
  full run when main has not changed since: "Yes, reuse it (Recommended)". Only
  a run made by the launcher in a fresh clone counts (GR2).
- **The held three.** Asked when HL1, PW1 and LM1-D go to the gate: "Leave them
  for now". They stay on their branches, ungated, until he says. A leftover
  `batch/LM1` (its ship file on, three commits main lacks, pushed after 0.9.94
  shipped) is held with them; its widening is on `batch/LM1-D`.
- **Sessions.** Started and stopped by MemWatch, headless (`claude -p`,
  `codex exec`): "sessions decide, MemWatch dispatches". The ceiling on
  sessions at once is a setting of MemWatch's that he moves. (His MemWatch
  design note of the same day; nothing of it is built yet.)

## Still his to decide

- What the orchestrator becomes once MemWatch dispatches.
- Who keeps the gate day to day, and the gate box's part when it arrives.
- Which of `CLAUDE.md`'s standing rules stay as they are. He said in the
  MemWatch session that save slots 1-9 "are NOT important"; the rule stands
  until he drops it here.

## Done means

One file both builders read, shorter than what it replaces (one in, one out:
the count in the notes). `AGENTS.md`, `CLAUDE.md` and the agents' files point at
it. Every statement in it is true of the scripts on main that day
(`tools.Stale`). The memory notes that only repeated a rule are deleted or cut
to what the repository does not hold.
