# OB1 - Live provider budget and orchestration reserve

Jerus's 2026-11-21 recovery instruction sets a window target of projected
98-99% at reset, never intentionally 100%. It permits early consumption when
the remaining allowance covers the work, including the controller through
reset. Current MemWatch advice routes work within that reserve and ceiling.

The failed orchestrator last read live usage on 2026-11-19 at 11:38:20,
Claude 54%, then did not re-read it for its remaining 197 turns. The audit
records 33 builders (26 Opus, seven Sonnet), 20 Opus reviewers and no Codex
builders/reviewers on November 20-21; launch calls explicitly chose Claude.
Its handoff still carried an earlier Claude 38% recommendation. Claude hit
the weekly limit at 05:25 on November 21 despite Codex headroom. These are
observations, not an inference about intent. Evidence remains in
`C:/Users/Jerus/Documents/Codex/2026-11-21/om-x20/work/orchestrator-audit.json`
and `orchestrator-claude-before-recovery.txt` there.

`docs/orchestrator.md` now requires live usage and actual warnings at every
turn and immediately before launches, budget-consuming resume instructions
and substantial orchestration. It accounts for running commitments,
resumes/reviews, observation lag and uncertainty before builder allocation;
stale/missing readings stop uncertain-provider work after 15 minutes. Each
dispatch records its reading, reset, runtime/reason and reserve forecast.
Controller handoff must precede exhaustion. API-equivalent dollar estimates
give neither spending authorization nor exact subscription quota.

All new workers/reviewers use explicit Codex gpt-6.1-sol xhigh while Claude
is exhausted, with no Claude revival before reset; routing is reassessed
after reset. The screen/docs pass defaults follow that rule too. The session
text now describes portable recovery rather than relying on Claude hooks.
MemWatch currently has no hard launcher budget admission guard: this batch
changes operational instructions only. Such a guard would be separate
MemWatch source work, not an enforcement claim made here.

The historical handoff was copied to
`C:/Users/Jerus/Documents/Codex/2026-11-21/om-x20/work/orchestrator-handoff-before-codex.md`;
its NOW/Budget were replaced during recovery turn748. The old deadline and
provider paragraphs no longer govern dispatch. No game code, save, model,
playtest trace or ensemble changes; no stranger review or dry run is owed.

## One in, one out and checks

Removed redundant session, PC and publication prose already held by the
contract, and obsolete runtime-specific wake/settings instructions.
`git diff --numstat origin/main -- docs/orchestrator.md`: +55 -60.
Notes and ship file are excluded from that process count.

Validation: `batch-check.sh --fast` exit 0, compile 51 seconds, regenerated
maps unchanged, Stale 0 firm/194 soft (main's). `git diff --check` and the
UTF-8/LF/ship-file checks pass. Pick NONE; review not needed (no money, saves
or threading). No dry run owed; the docs-only gate publishes untagged.
