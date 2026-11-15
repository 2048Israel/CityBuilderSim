<!-- readiness: provisional -->

## changelog
### {next} — RT2, The other buildings counted in their sector retirement measure — 2026-11-15, ready, see docs/queue/RT2-notes.md
FoodIndustry counts each mill's two baked outputs in heads served; worked-out
CoalMining measures raw mine nameplate, and Freight measures standing track
against offered tonnes. Each retirement measure now uses its buildings' own
unit, so washing coal or a shortage of wagon sets or stations cannot conceal
spare plant. Three sector fixtures fail on main's code and pass the repairs.
The bakers' power-and-consumables fixture moves from month 36 to month 8,
where both flows occur, preserving its city and assertions.
