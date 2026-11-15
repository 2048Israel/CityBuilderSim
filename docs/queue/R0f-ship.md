<!-- gate-run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-R0f-sYBv7jmW -->
<!-- readiness: provisional -->
<!-- verification: normal-route ALL dry run exit 0; suite exit 3, known reds only; eight default traces byte-identical; see notes -->
## changelog
### {next} — R0f, Money on Canada's dates — 2026-11-15, ready, see docs/queue/R0f-notes.md
Cities founded the 1900 way follow Canada's dated money: reserves hold the dollar at par until August 1914 and again from July 1926 to September 1931; exhaustion ends a gold episode early and survives a save. Interest follows the world on gold, the government's Advance Rate opens in August 1914, and the Bank of Canada opens the rest of Money in March 1935. Dates cannot be discovered early. Default founding retains its existing Money mechanics. Save format and oldest readable format rise to 53, because older unfitted cities had a different monetary contract. Founded-city, save and read checks cover the dates and the remembered exit.

## queued
| R0f-S | Keep the month-2 early city's Retail.wantedSpend, rent and whole foreign-account twin identical after loading; unchanged main reproduces the first Retail.wantedSpend difference | docs/queue/R0f-S.md |

## known-reds
MoneyDatesCheck | the exhausted-gold city's whole foreign-account twin matches after the next month | R0f-S | 2026-11-15 | Unchanged main c0120d77 reproduces Retail.wantedSpend 0 becoming 0.02587509525508486 on load, then rent/cash/foreign-account divergence in the identical zero-vault/endowment fixture. Gold state and inherited interest round-trip correctly; keep exact whole-twin equality. See R0f-notes.md and R0f-S.md.
