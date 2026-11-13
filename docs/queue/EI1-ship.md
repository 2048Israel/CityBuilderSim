## changelog

### EI1 — The ensemble's outcome criteria as information — 2026-11-13, ready, no tag (process), see docs/queue/EI1-notes.md

The ensemble gives every criterion a hard or info class. M1 (money audit) and F1 (findings) alone determine
its batch verdict. Every other criterion keeps its measurement, threshold and verdict as information,
beside the baseline's value and verdict, with changed verdicts called out. Old baselines remain readable.
The runner retains scored artifacts and returns 4 on a hard failure. Self-tests cover information failures
passing and audit/findings failures stopping in every policy. The batch and gate rules use the same split.
