## changelog
### OB1 — live provider budget and orchestration reserve — 2026-11-21, ready, see docs/queue/OB1-notes.md
The orchestrator reads live usage and warnings every turn and before dispatch, reserves its cost through reset plus running and pending commitments,
and targets projected subscription use of 98-99% at reset without intentionally exhausting a plan. Stale readings forbid uncertain-provider launches;
current MemWatch advice routes within the ceiling, with explicit Codex workers while Claude is exhausted and reassessment after reset. Dispatches
record readings and reserve forecasts, and controller handoff precedes exhaustion. This is operational policy, not a hard MemWatch launcher guard.
