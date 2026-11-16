<!-- gate-run: /tmp/cbs-gate-LF1b-6DFxKPRz -->
<!-- readiness: provisional -->
## changelog
### {next} — LF1b, the cells' share trades with the world, in the financial account — 2026-11-16, ready, see docs/queue/LF1b-notes.md
The households' cells selling shares to the world and buying them from it are declared to the money audit as the
cells' bonds have been: a sale abroad a financial inflow into their savings, a purchase a financial outflow out of
them, each a pair the cash identity cancels (`+ shares HouseholdsSoldAbroad` with its DOMESTIC half, and the
purchase the other way). The balance of payments' financial account held the cells' bonds and left out their shares;
it holds both, so the exchange rate reads them. In the default playtest the cells sold shares abroad in 968 months,
21% of the financial inflow in months 401-800, so the currency and the trace move; ShortSellingCheck causes the trade
in a real month and reads the lines.
