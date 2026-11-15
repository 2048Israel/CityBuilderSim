<!-- readiness: provisional -->

## changelog
### {next} — RT1b, RT1 money review follow-ups — 2026-11-15, ready, see docs/queue/RT1b-notes.md
The sectors' and households' paper ledgers use the paper's relative rounding
rule, so full principal payments, buybacks and central-bank purchases leave
no ulp-sized holding. HoldersCheck causes each old remainder, tests both price
levels, preserves partial holdings and cash, and measures the real firm-paper
desk against the bank's independently recorded book; a broken split fails.
