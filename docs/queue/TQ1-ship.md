<!-- readiness: provisional -->
## changelog
### {next} — TQ1, the staffing gate's tie rule — 2026-11-16, ready, see docs/queue/TQ1-notes.md
A planner whose buildings the staffing gate refused hands one hold to the wage (LD1), the refusal nearest to
passing, and in a city with no spare the refused templates tie exactly (GW1's snapped share). Each of thirteen
planners broke the tie by the order its buildings are listed in, and in main's default playtest a tie decided 638 of
3,502 holds, mostly in the founding, the first listed being the smallest in 636: a Builders' Yard's 20 posts were
priced, never the Construction Depot's 50. The thirteen keep their hold in one place now (`Sector.Staffing.held()`),
and a tie is shared: each tied building is noted as it would be alone, asking for its part of its posts, so the
order a sector lists its buildings in decides nothing (StaffingTieCheck, 22 of 53 red on main's rule). No save field,
no format bump. The default playtest moves from month 2.
