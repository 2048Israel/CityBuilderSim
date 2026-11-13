# PO1 — The path lines as nightly observations

Built from HA1 on main, 0f4fb634, on 2026-11-13. Harness and process changes use
no tag. Work in progress until the ship file is pushed.

## Decisions

- ★ A path premise returns its actual truth. Its callers guard only the laws
  that require it. A path share with no dependent law is reported alone.
- ★ The nightly records reached readings too. Each observation retains its own
  last-held commit, and a miss in any sample wins over holds that night.
  A first-ever miss states that no earlier hold is available.
- ★ Constructed-book identities, save/load equality, per-pass audits and
  deliberately caused conditions retain their correctness verdicts.
- The game's model, save format and default playtest have no changes. The
  ensemble is unnecessary because this batch cannot move the model's trace.

## Checks

Implementation and per-harness audit are in progress. Fast checks compile,
regenerate the maps and run Stale and tree validation after each group.
