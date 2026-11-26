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
- ★ Reuse AP5's raw integer post sums and ordered sector grouping across the month.
  `BuildingManager` owns one transient, volatile `HeldPosts` outside `WorksValue`'s
  pass, keyed on AP5's atomic `POSTS_EDITS`; the pass keeps its own cache and counters.
  `getSectorStacks` exposes immutable lists of live stack references. A group survives
  a month boundary when its inputs have not moved. Each quantity and template getter
  in `totalBySector` stays live: the same terms, added in the same order. Offered
  shares, posts past and instances also remain live; no new floating sum is retained.
  `WorksValue.bareWhile` bypasses every held raw-post/group reading, including nested
  bare readings. The harness warms both reading threads before a writer hands off.
- ★ Publish the completed, unmodified raw-post/group value through `volatile`,
  owned by the building manager. A per-manager `ThreadLocal` could retain a
  discarded city through its stack callbacks and the thread's value map. Owning
  the value in the manager avoids that retention; no raw getter depends on a
  reading thread's NewUnit/LicenceFees context (audited below). This is a threading
  change for the orchestrator's stranger review, alongside the bond money change.
- The bond mechanism was diagnosed by a MemWatch helper (read-only, 15-line report).
  It found one sorted holdings-map fold per settlement, and recommended a narrow
  posting scope rather than a persistent money cache. A second helper implements that
  scope and its BondCheck fixtures; the builder reviewed both files and kept the
  settlement expressions and their order. Only its two household face writers
  use `putBondFace` during posting, collecting touched identities. A `finally`
  recounts each with the original sorted-id fold; its nested `finally` clears the
  scope even after failure. Every other settlement remains eager. No map delta
  arithmetic and no money figure held after the posting call. Counters and an
  eager-mode twin switch are transient, per market, and used by BondCheck.
  The helper found no aggregate reader between the precomputed weights/targets
  and this scope's end: capacity uses live face, cash and posting budgets.
- ★ Keep bond aggregates eager outside that narrow call. Primary issues, maturity,
  defaults, census/migration/estates/folding, redenomination, clear/reset and load
  retain their existing writers and recounts. There is no new retained money
  cache for any of those paths to invalidate. The two changed settlement writers
  are exercised on warmed native monthly twins, with partial fills on three ids,
  manual and waterfall trades, and an exception after the seller has changed.
- The private-investment trace was delegated, with one offline reader of the kept
  baseline JFR. It has 475/4,177 FX samples (**11.37%**). `getPostsWithheld` has 80
  inclusive (**1.92% FX**, 16.84% of that subtree; 79 first-app), `totalBySector`
  57 (**1.36% FX**, 12.00%; 55 self). Both are the same raw-post/sector walks:
  the monthly `HeldPosts` reuse fixes them. `EquipmentFit.choose` is only 13 samples
  (0.31% FX), `rank` 6 (0.14%), and `refreshLand` zero: no profile case to expand
  those paths. Dynamic new-unit pricing revisits fees and recipes, not a fixed
  figure that can be lifted over investment orders. The full offline report/source
  is kept under `codex-AP6-bonds/target/AP6-investment/` and copied into the evidence.
- A read-only helper audited the longer cache lifetime against every writer and
  getter context; no missing invalidator was found. It caught the new load fixture
  reading an old manager: `loadGameSave` replaces it. The warmed same-manager case
  now uses initialized `loadGame`; separate `loadGameSave` and `newGame` cases read
  the replacement manager's held figures against its own bare ones. This causes
  both real load paths instead of assuming they share a manager.

## 3. Checks, mutations, and trace

Initial `./batch-check.sh --fast`: green, compile 55 s, Stale 0 firm.
The final results, warmed-cache writer coverage and mutation table will be recorded here.
