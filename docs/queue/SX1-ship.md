<!-- readiness: provisional -->
<!-- gate-run: /tmp/cbs-gate-SX1-IHgxFMHv -->

## changelog
### {next} — SX1, The exchange's prices readable — 2026-11-14, ready, see docs/queue/SX1-notes.md
Shares now split above a quarter of the average monthly wage and consolidate
below a four-hundredth, with every holding, order and fund cost moving together
and charts kept per founding share. One shared formatter replaces percentages
above 1,000% with compound yearly growth over an observed span or a compact
multiple. Ordinary percentages keep their precision. Exact split, conservation,
wage-band, formatter and save/reload checks cover the change.
The 80-run ensemble passes money audit and findings; the full merged gate
has only the existing PlanCheck, KeptCheck and DeathRecordCheck reds.
Split-occurrence checks reject a no-split run and verify the audit in forty
actual split months of the played fixture.
