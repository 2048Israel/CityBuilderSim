# AP6 notes — slot 9 at 50x

Built from `b7c7c19af` (0.9.212, format 66). Brief: `docs/queue/AP6.md`.
Jerus: “ok once again, slow down, slot 9”. No saved field or format change.

## 1. Measurements and kept evidence

Evidence is in `C:/Users/Jerus/AppData/Local/Temp/cbs-AP6-evidence/`.
AP5's kept `scratchpad/orig` was copied to its own `scratchpad/original` before any run.
The original AP2 `Perf9`, `PausedProbe`, and `JfrTop` sources were copied into `drivers/`.
`main-classes/` is the unmodified b7c7c19af compile. Every run uses `-Xmx4g`, explicit
`cbs.earth`/`cbs.world`, and `GameFiles` scratch directories. No player's slot was opened.
The first attempted headless run put the files one directory too high and read an empty
scratch city; discarded. The valid run loads dojha at m4866, 21,918,046 people.

Headless, 12 months after 2 warm: main mean **368.4**, median **346.1**, max **526.8 ms**.
Windowed: AP2's driver, rung 8 (50x), 5 s a screen, no save/load phase, 0.1 s paused,
2 redraw samples. This laptop was under other builders' load: compare the paired runs,
not AP5's earlier absolute figures. Full figures and after measurements follow below.

## 2. The calls and implementation

- ★ The posts past are read once per `getTotalJobs()` call, never held between calls.
  They depend on farms' horses and autonomous machines, independently of `POSTS_EDITS`.
  Each new call reads them live; every type retains the original integer arithmetic.
- ★ Hold the ordered sector grouping, not its totals. Outside `WorksValue`'s pass,
  `BuildingManager.getSectorStacks` holds immutable lists of live stack references on
  the reading thread, keyed on AP5's atomic `POSTS_EDITS`. A group survives a month
  boundary when its inputs have not moved. Each quantity and template getter stays live.
  `totalBySector` adds the same terms in the same order. The pass keeps AP5's own cache.
  `WorksValue.bareWhile` also bypasses this grouping, including nested bare readings.
- The bond mechanism was diagnosed by a MemWatch helper (read-only, 15-line report).
  It found one sorted holdings-map fold per settlement, and recommended a narrow
  posting scope rather than a persistent money cache. A second helper implements that
  scope and its BondCheck fixtures; the builder reviews the diff and owns the checks.

## 3. Checks, mutations, and trace

Initial `./batch-check.sh --fast`: green, compile 55 s, Stale 0 firm.
The final results, warmed-cache writer coverage and mutation table will be recorded here.
