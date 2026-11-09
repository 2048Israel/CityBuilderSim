# GW0 notes ({next}, 2026-11-09)

The staffing gate and growth, a study (`QUEUE.md`'s GW0; the brief is the wages study's 3.4 and C2). No model
change: the note is `docs/studies/spec-staffing-gate.md`, with its row in `docs/studies/README.md`, and the
prototypes (`kit.md`) and tables (`out.md`) are in `docs/studies/staffing-gate/`. Every changed path ends in `.md`.

## Session

- Started from the orchestrator (local_4e2cde26) in lane C behind LB5, then released to lane D (docs only) and
  branched from origin/main at 0e235b0f (0.9.72, R6). The PC restarted at about 15:19 and killed the wait; this
  session was compacted once, and the plan survived in its summary.
- The probe and its patches stayed in the scratchpad and are kept here only as `kit.md`'s fenced text, so no
  `.java` is committed. Every city was founded by `GateProbe` in a `TempDirs` folder. No save was read and no
  slot was written.

## Decisions

- **The kit.** The wages study's `WageProbe` without its floor, renamed `GateProbe`, with the gate's columns
  added. Main's `Sector` and `Migration` were copied with `migration-target.patch` and `gate.patch` applied
  (`-Dgate.min`, `-Dgate.draw`, `Migration.drawFor()`). Neutral checked: with no property set the patched build's
  CSV equals main's but for the two draw columns (seed 0 to month 240), and the instrumented build's equals it to
  month 480.
- **The runs.** 12 variants × 16 seeds × 2,400 months (192 cities), three at a time. The instrument covered
  seeds 0-3 to month 480 (today and draw12), and the first-plant check seeds 0-7 to month 1,200 (today, draw12,
  no share test). The instrument's eight runs used main's tree before R6 (research.json without R6's rows); the
  rest used 0.9.72's.
- **No share test (`g0aim`) was added after the first round.** It grew the most and had the fewest bad months,
  so the recommendation was tested against it. It lost on the young city's empty posts (10.3% against 5.5%) and
  on the Shared Services Centre: in all eight seeds checked it stood at month 54 in a town of 1,800 without its
  bands. Recommended: the draw (C1a), with (b) as the close second.
- **The calls** (the note's section 5), each with a recommendation:
  - C1: the gate counts the draw;
  - C2: a 12-month window;
  - C3: WG2's aim as Jerus took it;
  - C4: GW1 before WG2, or WG2 first with its later brake if it is already in hand.

  They go to Pending as GW0-C. No batch is queued, as the brief says ("queue no batch he has not taken"). GW1's
  row is in the note's section 6, ready to queue once C1-C4 are settled. Since every call has a recommendation,
  the orchestrator may settle them (BP1).
- **WG1's and WG2's files were read and not edited.** The wages study's `real-minimum.patch` no longer applies
  to main (two hunks fail). It was not needed, and it is WG1's area.
- No trace moves, so no ensemble.

## Checks

- `./batch-check.sh --fast`: see the ship file's commit. The merge changes only `.md` files.
