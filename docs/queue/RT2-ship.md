<!-- readiness: provisional -->

## changelog
### {next} — RT2, The other buildings counted in their sector retirement measure — 2026-11-15, ready, see docs/queue/RT2-notes.md
FoodIndustry counts each mill's two baked outputs in heads served; worked-out
CoalMining measures raw mine nameplate, and Freight measures standing track
against offered tonnes. Each retirement measure now uses its buildings' own
unit. Freight also limits the whole sale to retain operating headroom after
removing a depot's or spur's handling. Sector fixtures fail before each
repair and pass after it. The bakers' power-and-consumables fixture moves
from month 36 to month 8, where both flows occur, preserving its city and
assertions.
