<!-- readiness: provisional -->
## changelog
### {next} — TQ1, the staffing gate's tie rule — 2026-11-17, ready, see docs/queue/TQ1-notes.md
A planner whose buildings the staffing gate refused hands one hold to the wage (LD1), the refusal nearest to
passing, and in a city with no spare the refused templates tie exactly (GW1's snapped share). Each of thirteen
planners broke the tie by the order its buildings are listed in: in main's default playtest a tie decided 638 of
3,502 holds, mostly in the founding, and a Builders' Yard's 20 posts were priced, never the Construction Depot's 50.
The thirteen keep their hold in one place now (`Sector.Staffing.held()`), and a tie is shared: each tied building
asks for its part of its posts, so list order decides nothing (StaffingTieCheck, red on main's rule). No save field,
no format bump. The path moves from month 4. Its two findings, households with no home in the ensemble's seed 0 (once,
month 182) and in the default playtest (months 692-745), pass by name with FB11: Real Estate held for land while
nothing buys ground. In same-state probes main's rule stalls alike (the playtest's from 560 on; seed 0's, from
month 2, is the same city just under the watch's line); the escape is a works order buying ground it does not use.
## queued
| TW1 | TreasuryCheck's dollar-note fixture causes its own open window: on dc8f4da6 (TQ1 before main's 0.9.130) the window was shut when the note fell due at month 65 ("65% of exports already goes to foreign creditors") and five assertions failed, on the merged tree it is open, so the fixture asserts what the path gives ({next}) | [TQ1's notes, 5](docs/queue/TQ1-notes.md) |
